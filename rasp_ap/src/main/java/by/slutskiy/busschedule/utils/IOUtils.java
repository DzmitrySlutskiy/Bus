package by.slutskiy.busschedule.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.zip.ZipInputStream;

/**
 * IOUtils
 * Version 1.0
 * 30.09.2014
 * Created by Dzmitry Slutskiy.
 */
public class IOUtils {

    private static final String LOG_TAG = "IOUtils";

    private static final int NOTIFY_PERCENT = 1;
    private static final int MAX_BUFFER = 1024;
    private static final int EOF = - 1;

    /*  default buffer size  */
    private static final int BUFFER_SIZE = 8192;

    private IOUtils() {/*   code    */}


    /**
     * delete file in {@code filePath}
     *
     * @param filePath file path
     */
    public static void delFile(String filePath) {
        delFile(new File(filePath));
    }

    /**
     * delete file {@code file}
     *
     * @param file reference file
     */
    public static void delFile(File file) {
        Log.i(LOG_TAG, "Try delete file:" + file);
        if (file.exists()) {
            Log.i(LOG_TAG, (file.delete() ? "File deleted:" : "Can't delete file:") + file);
        }
    }

    /**
     * get last modification date resource by url as {@code string}
     *
     * @param url url address as {@code string}
     * @return Date last modified or {@code null} if any error occurs
     */
    public static Date getLastModifiedDate(String url) {
        return getLastModifiedDate(getUrl(url));
    }

    /**
     * get last modification date resource by url as {@code URL}
     *
     * @param url url address as {@code URL}
     * @return Date last modified or {@code null} if any error occurs
     */
    public static Date getLastModifiedDate(URL url) {
        if (url == null) {
            return null;
        }

        Date lastModifiedDate = null;

        HttpURLConnection uCon = null;
        InputStream stream = null;

        /*   try open internet connection to remote host  */
        try {
            uCon = (HttpURLConnection) url.openConnection();
            stream = uCon.getInputStream();               //check internet IOException throws

            lastModifiedDate = new Date(uCon.getLastModified());

            Log.i(LOG_TAG, "getLastModifiedDate: " + lastModifiedDate);
        } catch (IOException e) {
            lastModifiedDate = null;
        } finally {

            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error while closing stream: " + e.getMessage());
                }
            }

            if (uCon != null) {
                uCon.disconnect();
            }
        }

        return lastModifiedDate;
    }

    /**
     * generate URL for fileURL string
     *
     * @param fileURL string for URL
     * @return instance URL
     */
    private static URL getUrl(String fileURL) {
        try {
            return new URL(fileURL);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Bad URL string: " + fileURL);
        }
        return null;
    }

    public static boolean saveUrlToFile(String url, String filePath,
                                        LoadProgressListener listener) {
        HttpURLConnection uCon = null;
        InputStream stream = null;
        OutputStream outStream = null;
        int fileSize = 0;

        URL fUrl = getUrl(url);
        /*   try open internet connection to remote host  */
        try {
            uCon = (HttpURLConnection) fUrl.openConnection();
            stream = uCon.getInputStream();               //check internet IOException throws
            fileSize = uCon.getContentLength();

            if (listener != null) {
                listener.onStartLoad(fileSize);
            }

            File file = new File(filePath);
            stream = new BufferedInputStream(stream);
            outStream = new BufferedOutputStream(new FileOutputStream(file));

            /*  cycle count for NOTIFY_PERCENT - if read maxCount time - we read more than
            * NOTIFY_PERCENT % and can notify user*/
            int maxCount = fileSize / MAX_BUFFER / 100 * NOTIFY_PERCENT;
            int currentCount = 0;       //current cycle count

            byte[] buffer = new byte[MAX_BUFFER];
            int readBytes;
            int readBytesSum = 0;

            while ((readBytes = stream.read(buffer)) != EOF) {
                outStream.write(buffer, 0, readBytes);
                readBytesSum += readBytes;

                /*   send read bytes count to update dialog   */
                if (fileSize > 0 &&
                        (currentCount++ > maxCount)) {          //if true next NOTIFY_PERCENT % read
                    currentCount = 0;                           //reset counter

                    if (listener != null) {
                        listener.onProgressLoad(readBytesSum, fileSize);
                    }
                }
            }

            if (listener != null) {                         //show 100% read
                listener.onProgressLoad(readBytesSum, fileSize);
            }
        } catch (IOException e) {
            return false;
        } finally {
            closeStreamSilent(stream);

            if (uCon != null) {
                uCon.disconnect();
            }

            closeStreamSilent(outStream);

            if (listener != null) {
                listener.onFinishLoad();
            }
        }

        Log.i(LOG_TAG, "Download complete file size: " + fileSize);

        return true;
    }

    /**
     * Close stream with hide exception in close method
     *
     * @param needToClose closeable interface
     */
    public static void closeStreamSilent(Closeable needToClose) {
        try {
            if (needToClose != null) {
                needToClose.close();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Silent close exception: " + e);
        }
    }

    public static void extractFile(Context context, int resourceId, String dest) throws IOException {

        Log.i(LOG_TAG, "try extract db from zip file");

        File fileDst = new File(dest);

        /*   streams for unzip file and save to fileDst   */
        InputStream inputStream = context.getResources().openRawResource(resourceId);

        ZipInputStream zipInputStream = null;
        OutputStream outputStream = null;
        try {
            zipInputStream = new ZipInputStream(new BufferedInputStream(inputStream));
            outputStream = new BufferedOutputStream(new FileOutputStream(fileDst));

            byte[] buffer = new byte[BUFFER_SIZE];
            int count;
            while (zipInputStream.getNextEntry() != null) {
                while ((count = zipInputStream.read(buffer)) != - 1) {
                    outputStream.write(buffer, 0, count);
                }
            }

            Log.i(LOG_TAG, "extract db from zip file complete");
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "unzip error: " + ioe.getMessage());
            throw new IOException(ioe);
        } finally {

            /*  close all streams   */
            IOUtils.closeStreamSilent(inputStream);

            IOUtils.closeStreamSilent(outputStream);

            IOUtils.closeStreamSilent(zipInputStream);
        }
    }

    public static void mkDir(String path) {
        File dirCreator = new File(path);
        if (! dirCreator.mkdirs()) {
            Log.i(LOG_TAG, "make dir return false! path:" + dirCreator.getPath());
        }
    }

    /**
     * Call back interface
     */
    public interface LoadProgressListener {

        /**
         * Calls when load starts
         *
         * @param fileSize file size for downloading
         */
        public void onStartLoad(int fileSize);

        /**
         * call when {@code NOTIFY_PERCENT} of downloading done
         *
         * @param current current bytes downloaded
         * @param total   total bytes in file
         */
        public void onProgressLoad(int current, int total);

        /**
         * Calls when load finish
         */
        public void onFinishLoad();
    }
}
