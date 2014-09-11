package by.slutskiy.busschedule.data.entities;

import android.support.annotation.NonNull;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

/**
 * StopList
 * Version 1.0
 * 28.08.2014
 * Created by Dzmitry Slutskiy.
 */
@DatabaseTable(tableName = "StopList")
public class StopList  extends BaseDaoEnabled implements Comparable {

    public static final String ID = "_id";
    public static final String STOP_NAME = "StopName";

    /*  private fields  */
    @DatabaseField(generatedId = true, columnName = ID, index = true)
    private int mId;

    @DatabaseField(columnName = STOP_NAME, index = true)
    private String mStopName;

    /*  public constructors */

    public StopList() {
        this(- 1, "");
    }

    public StopList(int id, String stopName) {
        mId = id;
        mStopName = stopName;
    }

    public StopList(String stopName) {
        this(- 1, stopName);
    }

    /*  public methods  */

    public int getmId() {
        return mId;
    }

    public String getmStopName() {
        return mStopName;
    }

    @Override
    public String toString() {
        return mStopName;
    }

    /**
     * Compares this instance with the specified object and indicates if they
     * are equal. In order to be equal, {@code o} must represent the same object
     * as this instance using a class-specific comparison. The general contract
     * is that this comparison should be reflexive, symmetric, and transitive.
     * Also, no object reference other than null is equal to null.
     * <p/>
     * <p>The default implementation returns {@code true} only if {@code this ==
     * o}. See <a href="{@docRoot}reference/java/lang/Object.html#writing_equals">Writing a correct
     * {@code equals} method</a>
     * if you intend implementing your own {@code equals} method.
     * <p/>
     * <p>The general contract for the {@code equals} and {@link
     * #hashCode()} methods is that if {@code equals} returns {@code true} for
     * any two objects, then {@code hashCode()} must return the same value for
     * these objects. This means that subclasses of {@code Object} usually
     * override either both methods or neither of them.
     *
     * @param o the object to compare this instance with.
     * @return {@code true} if the specified object is equal to this {@code
     * Object}; {@code false} otherwise.
     * @see #hashCode
     */
    @Override
    public boolean equals(Object o) {
        // Return true if the objects are identical.
        // (This is just an optimization, not required for correctness.)
        if (this == o) {
            return true;
        }

        // Return false if the other object has the wrong type.
        // This type may be an interface depending on the interface's specification.
        if (! (o instanceof StopList)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        StopList lhs = (StopList) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.

        //сравниваем только с полем mStopName, поле mId игнорим
        return ((mStopName == null ? lhs.mStopName == null
                : mStopName.equals(lhs.mStopName)));
    }

    @Override
    public int hashCode() {

        //при расчете игнорим поле mId

        return (mStopName == null ? 0
                : mStopName.hashCode());
    }

    /*   implements Comparable for sorting   */
    @Override
    public int compareTo(@NonNull Object another) {
        return ((another instanceof StopList))
                ? mStopName.compareTo(((StopList) another).getmStopName())
                : 0;
    }
}
