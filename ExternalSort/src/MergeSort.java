import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.LinkedList;

public class MergeSort {

    public static void mergeSort(
        RandomAccessFile file,
        Record[] heapArray,
        byte[] in,
        byte[] out,
        LinkedList<Run> offsetList)
        throws IOException {
        RandomAccessFile outputFile = new RandomAccessFile("output.bin", "rw");
        if (offsetList.size() <= 8) {

            mergeSortSimple(file, heapArray, in, out, offsetList, outputFile);

            long length = outputFile.length();
            System.out.print(outputFile.length());
            return;
        }
        else {
            return;
        }

    }


    public static long mergeSortSimple(
        RandomAccessFile file,
        Record[] array,
        byte[] in,
        byte[] out,
        LinkedList<Run> offsetList,
        RandomAccessFile outputFile)
        throws IOException {

        Run tempRun = null;
        int[] arrStart = new int[8];
        int[] arrEnd = new int[8];
        int amtRuns = offsetList.size();
        ByteBuffer inputBuffer = ByteBuffer.wrap(in);
        for (int x = 0; x < amtRuns; x++) {
            tempRun = offsetList.get(x);
            readIn(file, array, in, out, tempRun, arrStart, arrEnd, x,
                inputBuffer);

        }
        ByteBuffer outputBuffer = ByteBuffer.wrap(out);

        while (amtRuns > 0) {

            int minIndex = arrStart[0];
            Record minRec = array[minIndex];
            int minX = 0;
            for (int x = 1; x < amtRuns; x++) {
                Record uh = array[arrStart[x]];
                if (uh.compareTo(minRec) < 0) {
                    minIndex = arrStart[x];
                    minX = x;
                    minRec = uh;
                }
            }
            outputBuffer.putLong(array[minIndex].getLong());
            outputBuffer.putDouble(array[minIndex].getDouble());
            System.out.println(array[minIndex].getLong() + " " + array[minIndex]
                .getDouble());
            arrStart[minX] += 1;

            if (arrStart[minX] >= arrEnd[minX]) {

                tempRun = offsetList.get(minX);
                if (tempRun.atEnd()) {
                    array[arrStart[minX]].setDouble(Double.MAX_VALUE);
                    amtRuns--;
                }
                else {
                    readIn(file, array, in, out, tempRun, arrStart, arrEnd,
                        minX, inputBuffer);
                }
            }

            if (!outputBuffer.hasRemaining()) {
                outputBuffer.flip();
                outputFile.write(outputBuffer.array());
                outputBuffer.clear();
            }

        }

        return 0;
    }


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
