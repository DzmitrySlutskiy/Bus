/*
 * Bus schedule for Grodno
 */

package by.slutskiy.busschedule.data;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import by.slutskiy.busschedule.utils.StringUtils;
import jxl.Range;
import jxl.Workbook;
import jxl.Sheet;
import jxl.Cell;
import jxl.read.biff.BiffException;

/*
 * Work with XLS (Excel 97-2003 format) file uses library JExcelApi (jxl)
 * JExcelApi can find here http://jexcelapi.sourceforge.net/
 *
 * Version 1.0
 * 2014
 * Created by Dzmitry Slutskiy
 * e-mail: dsslutskiy@gmail.com
 */

public class XLSHelper {

    private static final String LOG_TAG = XLSHelper.class.getSimpleName();

    public static final String TAG_A = "A";
    public static final int STOP_COLUMN_INDEX = 4;
    public static final int TAG_A_COLUMN_INDEX_DEFAULT = 0;
    public static final int ROUTE_NAME_COLUMN_OFFSET = 1;
    public static final int ROUTE_NAME_ROW_OFFSET = 2;
    public static final int TYPE_DAY_ROW_OFFSET = 4;
    public static final int STOP_ROW_OFFSET = 2;        //offset from "A" tag to hour line

    private static final String[] SCHEDULE_TYPE = {"по", "по раб.", "по вых.", "вых.", "раб.",
            "субб", "воскр", "пн", "вт", "ср", "чт", "пт", "сб", "вс", "ежедневно"};

    public static final String DEFAULT_SCHEDULE_TYPE = SCHEDULE_TYPE[4];

    private static final String TAG_TERM = "(КОНЕЧНАЯ)";
    private static final String TAG_STREET = "УЛИЦА";

    public static final String[] DELETING_SUBSTRING = {TAG_TERM, TAG_STREET};

    public static final String TAG_STOP_DIVIDER = " - ";

    private static final int NEWS_SEARCH_ROW = 1;
    private static final int NEWS_SEARCH_COLUMN = 0;
    private static final int UPD_DATE_SHEET = 0;

    private static LinkedList<RangeIndex> sRangeList;
    private static int sSheetRows;
    private static int sSheetColumns;

    private Workbook mWorkbook = null;

    public enum MergeSearchType {Row, Column}

    /**
     * Public constructor for XLSHelper
     *
     * @param xlsFilePath file path
     * @throws IOException
     * @throws BiffException
     */
    public XLSHelper(String xlsFilePath) throws IOException, BiffException {
        mWorkbook = Workbook.getWorkbook(new File(xlsFilePath));
    }

    /**
     * close the workbook and free up memory
     */
    public void closeWorkbook() {
        mWorkbook.close();
        mWorkbook = null;
    }

    /**
     * read news from {@code UPD_DATE_SHEET} sheet
     *
     * @return list with news
     */
    public List<String> getNewsList() {
        ArrayList<String> newsList = new ArrayList<String>();
        if (mWorkbook != null) {                                    //check workbook
            Sheet sheet = mWorkbook.getSheet(UPD_DATE_SHEET);        //get sheet with news

            /**
             * in first sheet news start at "A" column with 1+ RowIndex, we need to find
             * first news - in the cycle check rows from index 1 to row count for data in
             * sheet cell[A;i]
             */
            boolean firstNewsFound = false;
            for (int i = NEWS_SEARCH_ROW; i < sheet.getRows(); i++) {
                Cell cell = sheet.getCell(NEWS_SEARCH_COLUMN, i);
                String cellContent = cell.getContents().trim();
                String news = StringUtils.EMPTY_STRING;
                if (! cellContent.equals(StringUtils.EMPTY_STRING)) {     //if string not empty
                    firstNewsFound = true;                              //we found news
                    news = cellContent;
                }
                if (firstNewsFound) {
                    /*
                    * sometime news take 2 rows, second row data news start from "B" column
                    * check right cell to text data */
                    Cell rCell = sheet.getCell(NEWS_SEARCH_COLUMN + 1, i);
                    String rCellContent = rCell.getContents().trim();
                    if (! rCellContent.equals(StringUtils.EMPTY_STRING)) {
                        news += rCellContent;
                    }
                }
                if (! news.equals(StringUtils.EMPTY_STRING)) {
                    newsList.add(news);
                }
            }
        }
        return newsList;
    }

    /**
     * Get sheet with index sheetIndex from workbook
     *
     * @param sheetIndex sheet index in workbook
     * @return Sheet with index sheetIndex
     */
    public Sheet getSheet(int sheetIndex) {
        return mWorkbook.getSheet(sheetIndex);
    }

    /**
     * Get sheet count in workbook
     *
     * @return count sheets
     */
    public int getSheetCount() {
        return mWorkbook.getNumberOfSheets();
    }

    /**
     * Get merged cell size with coordinate column,row from sheet. if type = ROW method return
     * merged cell size on row, if type = column - return merged cell size on column
     *
     * @param sheet  sheet
     * @param column column index
     * @param row    row index
     * @param type   ROW/COLUMN size
     * @return merged cell size in row or column by MergeSearchType type, if cell not merged
     * return 1
     */

    public static int getMergedCellSize(Sheet sheet, int column, int row, MergeSearchType type) {
        CoordinateRange range;
        range = getMergedCellIndex(sheet.getCell(column, row), type);

        if ((range != null) && (range.bottomIndex >= range.topIndex)) {
            return range.bottomIndex - range.topIndex + 1;
        }
        return 1;
    }

    /**
     * Get cell content as string with delete this cell from merged range list sRangeList
     *
     * @param sheet  current sheet
     * @param column cell column index
     * @param row    cell row index
     * @return string value for cell (if cell not have content return empty string)
     */
    public static String getCellContent(Sheet sheet, int column, int row) {
        return getCellContent(sheet, column, row, true);
    }

    /**
     * Get cell content as string with delete this cell from merged range list sRangeList if
     * deleteRangeItem = true
     *
     * @param sheet           current sheet
     * @param column          cell column index
     * @param row             cell row index
     * @param deleteRangeItem if true will delete this cell merged info from sRangeList
     * @return return cell's content as string value (if cell not have content return empty string)
     */
    public static String getCellContent(Sheet sheet, int column, int row, boolean deleteRangeItem) {
        if ((sSheetRows > row) && (sSheetColumns > column)) {
            Cell cell = sheet.getCell(column, row);

            /* if cell merged, we can't get data from this cells if using not top/left coordinate
            * check cell content, if has empty string - we need check Range array for this cell*/
            if (cell.getContents().trim().equals(StringUtils.EMPTY_STRING)) {
                RangeIndex rangeIndex = checkRange(cell, deleteRangeItem);
                if (rangeIndex != null) {
                    return sheet.getCell(rangeIndex.topColumn,
                            rangeIndex.topRow).getContents().trim();
                }
            }
            return cell.getContents().trim();
        } else {
            Log.e(LOG_TAG, "sheet.rows: " + sSheetRows + " row:"
                    + row + " sheet.columns: " + sSheetColumns + " column: " + column);
            return "";
        }
    }

    /**
     * Check cell in range array
     *
     * @param cell            cell for check
     * @param deleteRangeItem if true will delete this cell merged info from sRangeList
     * @return RangeIndex for merged cell implements top left and right bottom coordinate
     */
    private static RangeIndex checkRange(Cell cell, boolean deleteRangeItem) {

        /*In sRangeList data was sort, and we don't need to check full array
         * If we find row index more than cell's row index, this cell not merged*/
        for (int i = 0; i < sRangeList.size(); i++) {
            RangeIndex rng = sRangeList.get(i);
            if ((cell.getRow() >= rng.topRow)
                    && (cell.getRow() <= rng.bottomRow)
                    && (cell.getColumn() >= rng.topColumn)
                    && (cell.getColumn() <= rng.bottomColumn)) {
                if (deleteRangeItem) {
                    sRangeList.remove(i);
                }
                return rng;
            } else if ((cell.getRow() < rng.topRow)) {
                break;
            }
        }
        return null;
    }

    /**
     * check schedule day type (по вых/раб/ежедневно)
     *
     * @param typeStr checked string value
     * @return true if bad type
     */
    public static boolean isBadScheduleType(String typeStr) {
        typeStr = typeStr.trim();
        return (typeStr.equals(SCHEDULE_TYPE[0]) ||
                typeStr.equals(StringUtils.EMPTY_STRING) ||
                (Integer.getInteger(typeStr, Integer.MAX_VALUE) != Integer.MAX_VALUE));
    }

    /**
     * processing minutes string
     *
     * @param minutes minutes as string
     * @return minutes
     */
    public static String processingMinutes(String minutes) {
        //иногда в файле сдвигается описание типа расписания с 22 колонки на другие
        //необходимо проверить нет ли слов субб воскр и др из SCHEDULE_TYPE в текущей строке
        for (String type : SCHEDULE_TYPE) {
            if (minutes.contains(type)) {
                return "";
            }
        }
        return minutes.trim();
    }

    /**
     * processing day type and delete sub strings ("по", "." etc)
     *
     * @param type type string
     * @return updated string value
     */
    public static String processingDayType(String type) {
        String[] clear = {SCHEDULE_TYPE[0], "."};
        for (String clearItem : clear) {
            if (type.contains(clearItem)) {
                type = StringUtils.deleteSubString(type, clearItem);
            }
        }
        return type.trim();
    }

    /**
     * Check cell in merged range array sRangeList and return CoordinateRange for Row dimension
     * if type = MergeSearchType.Row, or column dimension otherwise
     *
     * @param cell cell for checking
     * @param type row index needed or column
     * @return CoordinateRange instance if cell merged with any cells (exists in sRangeList)
     * null otherwise
     */
    private static CoordinateRange getMergedCellIndex(Cell cell, MergeSearchType type) {
        RangeIndex rangeIndex = checkRange(cell, true);
        if (rangeIndex != null) {
            CoordinateRange cRange = new CoordinateRange();
            if (type == MergeSearchType.Row) {
                cRange.topIndex = rangeIndex.topRow;
                cRange.bottomIndex = rangeIndex.bottomRow;
            } else {
                cRange.topIndex = rangeIndex.topColumn;
                cRange.bottomIndex = rangeIndex.bottomColumn;
            }
            return cRange;
        }
        return null;
    }

    /**
     * Update merged cells array for used sheet
     * this method must be called for every new parsing sheets once at the begin sheet parsing
     *
     * @param sheet sheet for update
     */
    public static void updateMergedCell(Sheet sheet) {
        Range[] sRange = sheet.getMergedCells();

        sRangeList = new LinkedList<RangeIndex>();
        for (Range range : sRange) {
            Cell topLeftCell = range.getTopLeft();
            Cell bottomRightCell = range.getBottomRight();

            if ((topLeftCell.getColumn() == 0) && (bottomRightCell.getColumn() == 1)) {
                continue;                   //exclude tag A from range
            }
            RangeIndex rangeIndex = new RangeIndex();

            rangeIndex.topColumn = topLeftCell.getColumn();
            rangeIndex.topRow = topLeftCell.getRow();

            rangeIndex.bottomColumn = bottomRightCell.getColumn();
            rangeIndex.bottomRow = bottomRightCell.getRow();

            sRangeList.add(rangeIndex);
        }
        Collections.sort(sRangeList);               //sort rangeList by rows
    }

    /**
     * Update sheet size for uses sheet
     * this method must be called for every new parsing sheets once at the begin sheet parsing
     * call getRows or getColumns need more time but for every one sheet return always one value
     * for optimizing used private vars and call this method at once for every sheets
     *
     * @param sheet sheet for update
     */
    public static void updateSheetSize(Sheet sheet) {
        sSheetRows = sheet.getRows();
        sSheetColumns = sheet.getColumns();
    }

    /**
     * Used for coordinate range definition (row or column range)
     */
    private static class CoordinateRange {
        public int topIndex;
        public int bottomIndex;
    }

    /**
     * RangeIndex implements merged cell coordinate. topRow and topColumn get a topLeft cell
     * coordinate for merged cells, bottomRow and bottomColumn bottomRight cell.
     * <p/>
     * class implements interface Comparable for sorting range by topRow indexes
     */
    private static class RangeIndex implements Comparable {
        public int topRow;
        public int bottomRow;
        public int topColumn;
        public int bottomColumn;

        @Override
        public String toString() {
            return "topRow " + topRow + " bottomRow " + bottomRow +
                    " topColumn " + topColumn + " bottomColumn " + bottomColumn;
        }

        /**
         * Compares this object to the specified object to determine their relative
         * order.
         *
         * @param another the object to compare to this instance.
         * @return a negative integer if this instance is less than {@code another};
         * a positive integer if this instance is greater than
         * {@code another}; 0 if this instance has the same order as
         * {@code another}.
         * @throws ClassCastException if {@code another} cannot be converted into something
         *                            comparable to {@code this} instance.
         */
        @Override
        public int compareTo(@SuppressWarnings("NullableProblems") Object another) {
            //compare was added at api level 19
            //return Integer.compare(this.topRow, ((RangeIndex) another).topRow);
            return (this.topRow < ((RangeIndex) another).topRow)
                    ? - 1
                    : (this.topRow == ((RangeIndex) another).topRow ? 0 : 1);
        }
    }
}
