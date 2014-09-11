package by.slutskiy.busschedule.data.entities;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * RouteList
 * Version information
 * 28.08.2014
 * Created by Dzmitry Slutskiy.
 */
@DatabaseTable(tableName = "RouteList")
public class RouteList {

    public static final String ID = "_id";
    public static final String ROUTE_ID = "RouteId";
    public static final String STOP_ID = "StopId";
    public static final String STOP_INDEX = "StopIndex";

    /*  private fields  */

    @DatabaseField(generatedId = true, columnName = ID)
    private int mId;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = ROUTE_ID, index = true)
    private Routes mRoutes;

    @DatabaseField(foreign = true, foreignAutoRefresh = true, columnName = STOP_ID)
    private StopList mStop;

    @DatabaseField(columnName = STOP_INDEX)
    private int mStopIndex;

    @ForeignCollectionField(eager = true)
    ForeignCollection<TimeList> mTimeList;


    /*  public constructors */

    public RouteList() {/*   code    */}

    /*  public methods  */

    public int getmId() {
        return mId;
    }


    public Routes getmRoutes() {
        return mRoutes;
    }

    public void setmRoutes(Routes mRoutes) {
        this.mRoutes = mRoutes;
    }

    public void setmStop(StopList mStop) {
        this.mStop = mStop;
    }


    public void setmStopIndex(int mStopIndex) {
        this.mStopIndex = mStopIndex;
    }

    public ForeignCollection<TimeList> getmTimeList() {
        return mTimeList;
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
        if (! (o instanceof RouteList)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        RouteList lhs = (RouteList) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.

        //сравниваем только с полем mBusNumber, поле mId игнорим
        return ((mRoutes == null ? lhs.mRoutes == null
                : mRoutes.equals(lhs.mRoutes)) &&
                (mStop == null ? lhs.mStop == null
                        : mStop.equals(lhs.mStop)) &&
                (mStopIndex == lhs.mStopIndex));
    }

    @Override
    public int hashCode() {
        int result = 17;

        //при расчете игнорим поле mId
        result = 31 * result + mStopIndex;

        result = 31 * result + (mRoutes == null ? 0 : mRoutes.hashCode());
        result = 31 * result + (mStop == null ? 0 : mStop.hashCode());

        return result;
    }
}
