package by.slutskiy.busschedule.data.entities;


import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;

/**
 * BusList
 * Version 1.0
 * 28.08.2014
 * Created by Dzmitry Slutskiy.
 */
@DatabaseTable(tableName = "BusList")
public class BusList extends BaseDaoEnabled {

    public static final String ID = "_id";
    public static final String BUS_NUMBER = "BusNumber";


    /*  private fields  */
    @DatabaseField(generatedId = true, columnName = ID)
    private int mId;

    @DatabaseField(columnName = BUS_NUMBER)
    private String mBusNumber;

    /*  public constructors */

    public BusList() {
        this(- 1, "");
    }

    public BusList(int id, String busNumber) {
        mId = id;
        mBusNumber = busNumber;
    }

    public BusList(String busNumber) {
        this(- 1, busNumber);
    }

    /*  public methods  */

    public int getmId() {
        return mId;
    }

    public String getmBusNumber() {
        return mBusNumber;
    }

    @Override
    public String toString() {
        return mBusNumber;
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
        if (! (o instanceof BusList)) {
            return false;
        }

        // Cast to the appropriate type.
        // This will succeed because of the instanceof, and lets us access private fields.
        BusList lhs = (BusList) o;

        // Check each field. Primitive fields, reference fields, and nullable reference
        // fields are all treated differently.

        //сравниваем только с полем mBusNumber, поле mId игнорим
        return ((mBusNumber == null ? lhs.mBusNumber == null
                : mBusNumber.equals(lhs.mBusNumber)));
    }

    @Override
    public int hashCode() {

        //при расчете игнорим поле mId

        return (mBusNumber == null ? 0
                : mBusNumber.hashCode());
    }
}
