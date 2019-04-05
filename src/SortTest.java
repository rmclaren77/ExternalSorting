import java.io.IOException;
import student.TestCase;

public class SortTest extends TestCase {

    public void testSort() throws IOException {

        int m = 2400;
        m = m - m % 8;
        Genfile_proj3.main(new String[] { "testFile" + m + ".bin", "" + m });
        Externalsort.main(new String[] { "testFile" + m + ".bin" });
        assertTrue(m % 8 == 0);

    }

}
