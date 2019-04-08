/**
 * 
 * @author rmclaren, swooty97
 * @version 4.2.19 This class stores the values pertaining to a given run
 *
 */
public class Run {
    private long beg;
    private long end;
    private long curr;

    /**
     * Constructor
     * 
     * @param beg
     *            beginning number
     * @param end
     *            end number
     */
    public Run(long beg, long end) {
        this.beg = beg;
        this.end = end;
        this.curr = beg;
    }

    /**
     * Getter method for beg
     * 
     * @return beg field
     */
    public long getBeg() {
        return beg;
    }

    /**
     * Getter method for end
     * 
     * @return end field
     */
    public long getEnd() {
        return end;
    }

    /**
     * Getter method for curr
     * 
     * @return curr field
     */
    public long getCurr() {
        return curr;
    }

    /**
     * Setter method for Curr
     * 
     * @param newCurr
     *            new curr field
     */
    public void setCurr(long newCurr) {
        curr = newCurr;
    }

    /**
     * Setter method for end
     * 
     * @param newEnd
     *            new end field
     */
    public void setEnd(long newEnd) {
        end = newEnd;
    }

    /**
     * Setter method for beg
     * 
     * @param newBeg
     *            new beg field
     */
    public void setBeg(long newBeg) {
        beg = newBeg;
    }

    /**
     * Checks if at the end
     * 
     * @return true if curr == end, false otherwise
     */
    public boolean atEnd() {
        return curr == end;
    }

}
