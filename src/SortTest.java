import java.io.IOException;
import student.TestCase;

/**
 * 
 * @author rmclaren swooty97
 * @version 4.5.19
 *          Tests to make sure the runs are sorted
 *
 * 
 * @author rmclaren, swooty97
 * @version Apr 7, 2019
 */
public class SortTest extends TestCase {
    /**
     * Gets a random bin file and sorts it
     * Look over printout to make sure the values are ascending in order
     * 
     * @throws IOException
     */
    public void testSort() throws IOException {

        int m = 2400;
        m = m - m % 8;
        Genfile_proj3.main(new String[] { "testFile" + m + ".bin", "" + m });
        Externalsort.main(new String[] { "testFile" + m + ".bin" });
        assertTrue(m % 8 == 0);

    }

}
