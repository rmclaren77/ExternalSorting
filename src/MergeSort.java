import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.LinkedList;

/**
 * 
 * @author rmclaren, swooty97
 * @version Apr 7, 2019
 */
public class MergeSort {

    /**
     * 
     * @param file
     *            file reading from
     * @param heapArray
     *            heap array holding records
     * @param in
     * @param out
     * @param offsetList
     * @param outputFile
     * @param blocks
     * @throws IOException
     *             read file exception
     */
    public static void mergeSort(RandomAccessFile file, Record[] heapArray,
            byte[] in, byte[] out, LinkedList<Run> offsetList,
            RandomAccessFile outputFile, int blocks) throws IOException {
        // RandomAccessFile outputFile = new RandomAccessFile(fileName +
        // "sorted.bin", "rw");
        if (offsetList.size() <= 8) {

            mergeSortSimple(file, heapArray, in, out, offsetList, outputFile,
                    false, blocks);
            /*
             * long length = outputFile.length(); int place = 0; for (int x = 0;
             * x < length; x += 8192 * 5) { for (int y = 0; y < 5; y++) { place
             * = x + 8192 * y; if (place >= length) { return; }
             * outputFile.seek(place); System.out.print(outputFile.readLong() +
             * " " + outputFile .readDouble() + " "); } System.out.print("\n");
             * }
             */

            return;
        }
        else {
            long beg = file.length();
            mergeSortSimple(file, heapArray, in, out, offsetList, file, true,
                    blocks);
            for (int x = 0; x < 8; x++) {

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
            mergeSort(file, heapArray, in, out, offsetList, outputFile, blocks);
        }

    }

    /**
     * 
     * @param file
     * @param array
     * @param in
     * @param out
     * @param offsetList
     * @param outputFile
     * @param overWrite
     * @param blocks
     * @return
     * @throws IOException
     */
    public static long mergeSortSimple(RandomAccessFile file, Record[] array,
            byte[] in, byte[] out, LinkedList<Run> offsetList,
            RandomAccessFile outputFile, boolean overWrite, int blocks)
            throws IOException {

        int printCount = 1;

        long writeIndex;
        if (!overWrite) {
            writeIndex = 0;
        }
        else {
            writeIndex = outputFile.length();
        }

        Run tempRun = null;
        int[] arrStart = new int[8];
        int[] arrEnd = new int[8];
        boolean[] checkRun = new boolean[8];
        int amtRuns;
        if (offsetList.size() <= 8) {
            amtRuns = offsetList.size();
        }
        else {
            amtRuns = 8;
        }
        int runCount = amtRuns;
        ByteBuffer inputBuffer = ByteBuffer.wrap(in);
        for (int x = 0; x < amtRuns; x++) {
            checkRun[x] = true;
            tempRun = offsetList.get(x);
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
                Record uh = array[arrStart[x]];
                if (uh.compareTo(minRec) < 0) {
                    minIndex = arrStart[x];
                    minX = x;
                    minRec = uh;
                }
            }
            if (minIndex >= 0) {
                outputBuffer.putLong(array[minIndex].getLong());
                outputBuffer.putDouble(array[minIndex].getDouble());

                arrStart[minX] += 1;

                if (arrStart[minX] >= arrEnd[minX]) {

                    tempRun = offsetList.get(minX);
                    if (tempRun.atEnd()) {
                        checkRun[minX] = false;
                        runCount--;
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
                    System.out.print(outputBuffer.getLong() + " "
                            + outputBuffer.getDouble() + " ");
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
                outputFile.write(outputBuffer.array());
                writeIndex += outputBuffer.array().length;
                outputBuffer.clear();
            }

        }
        if (overWrite && outputBuffer.hasRemaining()) {
            outputBuffer.flip();
            outputFile.seek(writeIndex);
            outputFile.write(outputBuffer.array());
            outputBuffer.clear();

        }

        return 0;
    }

    /**
     * 
     * @param file
     *            file being read from
     * @param array
     *            array holding records
     * @param in
     *            input
     * @param out
     *            output
     * @param tempRun
     *            temporary run
     * @param arrStart
     *            array start
     * @param arrEnd
     *            array end
     * @param index
     *            index of
     * @param inputBuffer
     * @throws IOException
     *             reading file exception
     */
    private static void readIn(RandomAccessFile file, Record[] array, byte[] in,
            byte[] out, Run tempRun, int[] arrStart, int[] arrEnd, int index,
            ByteBuffer inputBuffer) throws IOException {

        int tempBeg = (int) tempRun.getCurr();
        file.seek((long) tempBeg);
        int tempEnd = (int) tempRun.getEnd();
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
        Record tempRec = null;
        for (int y = arrStart[index]; y < arrEnd[index]; y++) {

            long l = inputBuffer.getLong();
            double d = inputBuffer.getDouble();
            array[y].setLong(l);
            array[y].setDouble(d);

        }

        inputBuffer.clear();

    }

}
