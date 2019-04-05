/**
 * 
 * @author rmclaren
 * @version 4.2.19
 *          This class stores the values pertaining to a given run
 *
 */
public class Run {
    private long beg;
    private long end;
    private long curr;


    public Run(long beg, long end) {
        this.beg = beg;
        this.end = end;
        this.curr = beg;
    }


    public long getBeg() {
        return beg;
    }


    public long getEnd() {
        return end;
    }


    public long getCurr() {
        return curr;
    }


    public void setCurr(long newCurr) {
        curr = newCurr;
    }


    public void setEnd(long newEnd) {
        end = newEnd;
    }


    public void setBeg(long newBeg) {
        beg = newBeg;
    }


    public boolean atEnd() {
        return curr == end;
    }

}
