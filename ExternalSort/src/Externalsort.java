
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * 
 * @author rmclaren
 * @version 3.31.19
 *          This class contains the main method, which takes in the bytes from
 *          the file and uses the ReplacementSort and MergeSort to sort the file
 *
 */
public class Externalsort {
    private static byte[] in;
    private static byte[] out;
    private static Record[] heapArray;


    public static void main(String[] input) throws IOException {

        String fileName = input[0];
        heapArray = new Record[4096];
        out = new byte[8192];

        in = new byte[8192];
        for (int x = 0; x < 4096; x++) {
            heapArray[x] = new Record(0, Double.MAX_VALUE);
        }
        LinkedList<Run> offsetList = ReplacementSort.replacementSort(fileName,
            heapArray, in, out);
        RandomAccessFile file = new RandomAccessFile("runFile.bin", "r");
        /*
         * for (int x = 0; x < file.length() / 16 && x < 512; x++) {
         * System.out.println(file.readLong() + " " + file.readDouble());
         * System.out.println(file.getFilePointer());
         * }
         */
        MergeSort.mergeSort(file, heapArray, in, out, offsetList);

    }

}
