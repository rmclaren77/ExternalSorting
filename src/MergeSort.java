
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * 
 * @author rmclaren swooty97
 * @version 4.2.19
 *          This class handles the Mergesort part of the sorting. It takes in 8
 *          runs at a time and sorts them into one longer sorted run. If the
 *          original amount of runs is greater than 8, it will continue this
 *          process until there is only one run
 *
 */
public class MergeSort {

    /**
     * This is the general mergeSort method, which will pass different arguments
     * depending on if there are less than or more than 8 runs
     * 
     * @param file
     *            is the run file
     * @param heapArray
     *            is the array allocated to hold the 8 runs
     * @param in
     *            is the input buffer
     * @param out
     *            is the output buffer
     * @param offsetList
     *            is the list of runs and their offsets in the run file
     * @param outputFile
     *            is the file that the sorted run will be printed out to
     * @param blocks
     *            is the total number of blocks
     * @throws IOException
     */
    public static void mergeSort(
        RandomAccessFile file,
        Record[] heapArray,
        byte[] in,
        byte[] out,
        LinkedList<Run> offsetList,
        RandomAccessFile outputFile,
        int blocks)
        throws IOException {
        /*
         * RandomAccessFile outputFile = new RandomAccessFile(fileName +
         * "sorted.bin", "rw");
         */

        // Calls the mergeSortSimpleMethod, passing in the actual outputFile
        // where the run will be printed to
        if (offsetList.size() <= 8) {

            mergeSortSimple(file, heapArray, in, out, offsetList, outputFile,
                false, blocks);
/*
 * long length = outputFile.length();
 * int place = 0;
 * for (int x = 0; x < length; x += 8192 * 5) {
 * for (int y = 0; y < 5; y++) {
 * place = x + 8192 * y;
 * if (place >= length) {
 * return;
 * }
 * outputFile.seek(place);
 * System.out.print(outputFile.readLong() + " " + outputFile
 * .readDouble() + " ");
 * }
 * System.out.print("\n");
 * }
 */

            return;
        }
        // If there are greater than 8 runs, it will sort the first 8 runs and
        // add this to the end of the run file. Then, it will shift this run to
        // the end and call mergeSort again
        else {
            long beg = file.length();
            mergeSortSimple(file, heapArray, in, out, offsetList, file, true,
                blocks);
            for (int x = 0; x < 8; x++) {
                // Grabs the last Run, changes the offsets of the run(where it
                // starts and ends in the runFile), and adds it to the end of
                // the linkedlist of runs
                if (x == 7) {
                    offsetList.getFirst().setBeg(beg);
                    offsetList.getFirst().setCurr(beg);
                    offsetList.getFirst().setEnd(file.length());

                    offsetList.addLast(offsetList.removeFirst());

                }
                else {
                    offsetList.removeFirst();
                }

            }
            // Calls itself, needs to continue until there is only one run
            mergeSort(file, heapArray, in, out, offsetList, outputFile, blocks);
        }

    }


    /**
     * This sorts the first eight runs using mergeSort
     * 
     * @param file
     *            is the run file
     * @param array
     *            is the array allocated to hold the 8 runs
     * @param in
     *            is the input buffer
     * @param out
     *            is the output buffer
     * @param offsetList
     *            is the list of runs and their offsets in the run file
     * @param outputFile
     *            is the file that the sorted run will be printed out to
     * @param overWrite
     *            tells whether or not you are writing the new run to the run
     *            File
     * @param blocks
     *            is the total number of blocks
     */
    public static void mergeSortSimple(
        RandomAccessFile file,
        Record[] array,
        byte[] in,
        byte[] out,
        LinkedList<Run> offsetList,
        RandomAccessFile outputFile,
        boolean overWrite,
        int blocks)
        throws IOException {
        int printCount = 1;
        int outputIndex = 0;
        long writeIndex; // index of where the file will be writing to
        if (!overWrite) {
            writeIndex = 0;
        }
        else {
            writeIndex = outputFile.length();
        }

        Run tempRun = null;
        // Where in the record array the runs begin
        int[] arrStart = new int[8];
        // Where in the record array the runs end
        int[] arrEnd = new int[8];
        // Whether there is anything left in the run
        boolean[] checkRun = new boolean[8];
        // number of runs for this merge
        int amtRuns;

        if (offsetList.size() <= 8) {
            amtRuns = offsetList.size();
        }
        else {
            amtRuns = 8;
        }
        // This method will run until runCount == 0
        int runCount = amtRuns;
        ByteBuffer inputBuffer = ByteBuffer.wrap(in);
        for (int x = 0; x < amtRuns; x++) {
            checkRun[x] = true;
            tempRun = offsetList.get(x);
            // Reads values into the array from the runs
            readIn(file, array, in, out, tempRun, arrStart, arrEnd, x,
                inputBuffer);

        }
        ByteBuffer outputBuffer = ByteBuffer.wrap(out);

        Record upperLimit = new Record(0, Double.MAX_VALUE);
        while (runCount > 0) {

            int minIndex = -1;
            Record minRec = upperLimit;
            int minX = -1;
            if (checkRun[0]) {
                minIndex = arrStart[0];
                minRec = array[minIndex];
                minX = 0;
            }

            for (int x = 1; x < amtRuns; x++) {
                if (!checkRun[x]) {
                    continue;
                }
                Record tempRec = array[arrStart[x]];
                if (tempRec.compareTo(minRec) < 0) {
                    minIndex = arrStart[x];
                    minX = x;
                    minRec = tempRec;
                }
            }
            if (minIndex >= 0) {
                outputBuffer.putLong(array[minIndex].getLong());
                outputBuffer.putDouble(array[minIndex].getDouble());
                outputIndex += 16;
                arrStart[minX] += 1;

                if (arrStart[minX] >= arrEnd[minX]) {

                    tempRun = offsetList.get(minX);
                    if (tempRun.atEnd()) {
                        checkRun[minX] = false;
                        runCount--;

                        if (runCount == 1) {
                            int finalRunIndex = -1;
                            Run finalRun = null;
                            for (int x = 0; x < amtRuns; x++) {
                                if (checkRun[x]) {
                                    finalRunIndex = x;
                                    finalRun = offsetList.get(x);
                                }

                            }
                            while (arrStart[finalRunIndex] 
                                < arrEnd[finalRunIndex]) {
                                if (outputBuffer.hasRemaining()) {
                                    outputBuffer.putLong(array[minIndex]
                                        .getLong());
                                    outputBuffer.putDouble(array[minIndex]
                                        .getDouble());
                                    outputIndex += 16;
                                    arrStart[finalRunIndex] += 1;
                                }
                                if (!outputBuffer.hasRemaining()) {
                                    outputBuffer.flip();
                                    if (!overWrite) {
                                        System.out.print(outputBuffer.getLong()
                                            + " " + outputBuffer.getDouble()
                                            + " ");
                                        if (printCount == 5) {
                                            printCount = 1;
                                            System.out.print("\n");
                                        }
                                        else {
                                            printCount++;
                                        }
                                    }
                                    outputBuffer.rewind();
                                    outputFile.seek(writeIndex);
                                    outputFile.write(out);
                                    writeIndex += out.length;
                                    outputBuffer.clear();
                                    outputIndex = 0;

                                }

                            }
                            while (!finalRun.atEnd()) {
                                readIn(file, array, in, out, tempRun, arrStart,
                                    arrEnd, minX, inputBuffer);
                                out = in;
                                if (!overWrite) {
                                    System.out.print(outputBuffer.getLong()
                                        + " " + outputBuffer.getDouble() + " ");
                                    if (printCount == 5) {
                                        printCount = 1;
                                        System.out.print("\n");
                                    }
                                    else {
                                        printCount++;
                                    }
                                }
                                outputBuffer.rewind();
                                outputFile.seek(writeIndex);
                                outputFile.write(out);
                                writeIndex += out.length;
                                outputBuffer.clear();
                                outputIndex = 0;

                            }
                        }

                    }
                    else {
                        readIn(file, array, in, out, tempRun, arrStart, arrEnd,
                            minX, inputBuffer);
                    }
                }
            }

            if (!outputBuffer.hasRemaining()) {
                outputBuffer.flip();
                if (!overWrite) {
                    System.out.print(outputBuffer.getLong() + " " + outputBuffer
                        .getDouble() + " ");
                    if (printCount == 5) {
                        printCount = 1;
                        System.out.print("\n");
                    }
                    else {
                        printCount++;
                    }
                }
                outputBuffer.rewind();
                outputFile.seek(writeIndex);
                outputFile.write(out);
                writeIndex += out.length;
                outputBuffer.clear();
                outputIndex = 0;

            }

        }
        if (overWrite && outputBuffer.hasRemaining()) {
            outputBuffer.flip();
            outputFile.seek(writeIndex);
            outputFile.write(out, 0, outputIndex);
            outputBuffer.clear();

        }
    }


    /**
     * Reads in the next block of bytes from the specified run
     * 
     * @param file
     *            is the file being read from
     * @param array
     *            is the array where the records will be stored
     * @param in
     *            is the input array
     * @param out
     *            is the output array
     * @param tempRun
     *            is the run being read in
     * @param arrStart
     *            is the array of run starts in the record array
     * @param arrEnd
     *            is the array of run Ends in the record array
     * @param index
     *            tells which run it is
     * @param inputBuffer
     *            is the input Buffer
     * @throws IOException
     */
    private static void readIn(
        RandomAccessFile file,
        Record[] array,
        byte[] in,
        byte[] out,
        Run tempRun,
        int[] arrStart,
        int[] arrEnd,
        int index,
        ByteBuffer inputBuffer)
        throws IOException {

        int tempBeg = (int)tempRun.getCurr();
        file.seek((long)tempBeg);
        int tempEnd = (int)tempRun.getEnd();
        int tempDiff;
        if (index == 0) {
            arrStart[index] = 0;
        }
        else {
            arrStart[index] = arrEnd[index - 1];
        }
        if (tempEnd - tempBeg < 8192) {
            file.read(in, 0, (tempEnd - tempBeg));
            tempDiff = tempEnd - tempBeg;
            tempRun.setCurr(tempEnd);
            arrEnd[index] = arrStart[index] + tempDiff / 16;

        }
        else {
            file.read(in, 0, 8192);
            tempDiff = 8192;
            tempRun.setCurr(tempBeg + 8192);
            arrEnd[index] = arrStart[index] + 512;
        }
        inputBuffer.rewind();
        for (int y = arrStart[index]; y < arrEnd[index]; y++) {

            long l = inputBuffer.getLong();
            double d = inputBuffer.getDouble();
            array[y].setLong(l);
            array[y].setDouble(d);

        }

        inputBuffer.clear();

    }

}
