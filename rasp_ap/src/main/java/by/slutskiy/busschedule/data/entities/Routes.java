package by.slutskiy.busschedule.data.entities;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * Routes
 * Version 1.0
 * 28.08.2014
 * Created by Dzmitry Slutskiy.
 */
@DatabaseTable(tableName = "Routes")
public class Routes {

    public static final String ID = "_id";
    public static final String BUS_ID = "BusId";
    public static final String BEGIN_STOP_ID = "BeginStopId";
    public static final String END_STOP_ID = "EndStopId";

    /*  private fields  */

    @DatabaseField(generatedId = true, columnName = ID)
    private int mId;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = BUS_ID)
    private BusList mBus;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = BEGIN_STOP_ID)
    private StopList mBeginStop;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = END_STOP_ID)
    private StopList mEndStop;


    /*  public constructors */

    public Routes() {/*   code    */}

    /*  public methods  */

    public int getmId() {
        return mId;
    }

    public BusList getmBus() {
        return mBus;
    }

    public void setmBus(BusList mBus) {
        this.mBus = mBus;
    }

    public StopList getmBeginStop() {
        return mBeginStop;
    }

    public void setmBeginStop(StopList mBeginStop) {
        this.mBeginStop = mBeginStop;
    }

    public StopList getmEndStop() {
        return mEndStop;
    }

    public void setmEndStop(StopList mEndStop) {
        this.mEndStop = mEndStop;
    }

    @Override
    public String toString() {
        return "" + mBus + "   " + mBeginStop + " - " + mEndStop;
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
        if (! (o instanceof Routes)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        Routes lhs = (Routes) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.

        //сравниваем только с полем mBusNumber, поле mId игнорим
        return ((mBus == null ? lhs.mBus == null
                : mBus.equals(lhs.mBus)) &&
                (mBeginStop == null ? lhs.mBeginStop == null
                        : mBeginStop.equals(lhs.mBeginStop)) &&
                (mEndStop == null ? lhs.mEndStop == null
                        : mEndStop.equals(lhs.mEndStop)));
    }

    @Override
    public int hashCode() {
        int result = 17;

        //при расчете игнорим поле mId

        result = 31 * result + (mBus == null ? 0 : mBus.hashCode());
        result = 31 * result + (mBeginStop == null ? 0 : mBeginStop.hashCode());
        result = 31 * result + (mEndStop == null ? 0 : mEndStop.hashCode());

        return result;
    }
}
