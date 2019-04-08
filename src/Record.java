/**
 * Record class
 * @author rmclaren, swooty
 * @version 4.1.19
 */
public class Record implements Comparable<Record> {

    private double doubleValue;
    private long longValue;

    /**
     * Record constructor
     * 
     * @param dub
     *            double value (key value)
     * @param lon
     *            long value (record ID)
     */
    public Record(long lon, double dub) {
        longValue = lon;
        doubleValue = dub;

    }

    /**
     * Getter method for double
     * 
     * @return the record's double value
     */
    public double getDouble() {
        return doubleValue;
    }

    /**
     * Getter method for long
     * 
     * @return the record's long value
     */
    public long getLong() {
        return longValue;
    }

    /**
     * Setter method for double field
     * 
     * @param d
     *            is the new double value
     */
    public void setDouble(Double d) {
        doubleValue = d;
    }

    /**
     * Setter method for long field
     * 
     * @param l
     *            is the new double value
     */
    public void setLong(Long l) {
        longValue = l;
    }

    /**
     * Print out values of long and double in string
     * 
     * @return the string representation of the record
     */
    public String toString() {
        return longValue + " " + doubleValue;
    }

    /**
     * Compare key values.
     * 
     * @return positive value if greater than, negative if less than, or 0 if
     *         equal to the record being compared
     */
    public int compareTo(Record r) {
        if (doubleValue < r.getDouble()) {
            return -1;
        }
        else if (doubleValue == r.getDouble()) {
            return 0;
        }
        else {
            return 1;
        }
    }

}
