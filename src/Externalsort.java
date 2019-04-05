
// On my honor:
//
// - I have not used source code obtained from another student,
// or any other unauthorized source, either modified or
// unmodified.
//
// - All source code and documentation used in my program is
// either my original work, or was derived by me from the
// source code published in the textbook for this course.
//
// - I have not discussed coding details about this project with
// anyone other than my partner (in the case of a joint
// submission), instructor, ACM/UPE tutors or the TAs assigned
// to this course. I understand that I may discuss the concepts
// of this program with other students, and that another student
// may help me debug my program so long as neither of us writes
// anything during the discussion or modifies any computer file
// during the discussion. I have violated neither the spirit nor
// letter of this restriction

import java.io.IOException;
import java.io.RandomAccessFile;
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
        RandomAccessFile file = new RandomAccessFile(fileName, "rw");
        in = new byte[8192];
        for (int x = 0; x < 4096; x++) {
            heapArray[x] = new Record(0, Double.MAX_VALUE);
        }
        LinkedList<Run> offsetList = ReplacementSort.replacementSort(file,
            heapArray, in, out);
        RandomAccessFile file2 = new RandomAccessFile("runFile.bin", "rw");

        /*
         * for (int x = 0; x < file.length() / 16 && x < 512; x++) {
         * System.out.println(file.readLong() + " " + file.readDouble());
         * System.out.println(file.getFilePointer());
         * }
         */
        // fileName = fileName.substring(0, fileName.indexOf("."));
        MergeSort.mergeSort(file2, heapArray, in, out, offsetList, file,
            (int)file2.length());
        file2.close();
        file.close();

    }

}
