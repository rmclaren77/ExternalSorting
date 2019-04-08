// WARNING: This program uses the Assertion class. When it is run,
// assertions must be turned on. For example, under Linux, use:
// java -ea Genfile

/**
 * Generate a data file. The size is a multiple of 8192 bytes.
 * Each record is one long and one double.
 * 
 * @author Young Cao
 * @version 4.7.19
 * 
 */

import java.io.*;
import java.util.*;

/**
 * Generates a random file with longs and doubles
 * 
 * @author Young Cao
 * @version 4.7.19
 */
public class GenfileProj3 {
    /**
     * How many records each block holds
     */
    static final int NUMRECS = 512; // Because they are short ints

    /** Initialize the random variable */
    static private Random value = new Random(); // Hold the Random class object


    /**
     * generates random long
     * 
     * @return the long value
     */
    static long randLong() {
        return value.nextLong();
    }


    /**
     * Generates random double
     * 
     * @return the double value
     */
    static double randDouble() {
        return value.nextDouble();
    }


    /**
     * Creates a file filled with random bytes
     * 
     * @param args
     *            are the file name and size
     * @throws IOException
     *             thrown if input output is incorrect
     */
    public static void main(String[] args) throws IOException {
        long val;
        double val2;
        assert (args.length == 2) : "\nUsage: Genfile <filename> <size>"
            + "\nOptions \nSize is measured in blocks of 8192 bytes";

        int filesize = Integer.parseInt(args[1]); // Size of file in blocks
        DataOutputStream file = new DataOutputStream(new BufferedOutputStream(
            new FileOutputStream(args[0])));

        for (int i = 0; i < filesize; i++) {
            for (int j = 0; j < NUMRECS; j++) {
                val = (long)(randLong());
                file.writeLong(val);
                val2 = (double)(randDouble());
                file.writeDouble(val2);
            }
        }

        file.flush();
        file.close();
    }

}
