import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.LinkedList;

/**
 * 
 * @author rmclaren, swooty
 * @version 3.31.19
 * 
 *          This class is used for sorting 8 blocks of memory, creating the
 *          longest run
 */
public class ReplacementSort {
    private static MinHeap heap;


    /**
     * @throws IOException
     *             Reading file exception
     * @return the name of the runFile which will be used by the mergeSort
     */

    public static LinkedList<Run> replacementSort(
        RandomAccessFile file,
        Record[] heapArray,
        byte[] input,
        byte[] output)
        throws IOException {

        LinkedList<Run> runOffsets = new LinkedList<Run>();
        Run xRun = new Run(0, 0);
        String runFileName = "runFile.bin";

        DataOutputStream file2 = new DataOutputStream(new BufferedOutputStream(
            new FileOutputStream(runFileName)));
        long numBlocks = file.length() / 8152;

        int outputIndex = 0;
        ByteBuffer inputBuffer = ByteBuffer.wrap(input);

        ByteBuffer outputBuffer = ByteBuffer.wrap(output);
        int heapIndex = 0;

        // Reads input file to add records to heapArray
        for (int x = 0; x < 8; x++) {
            file.read(input);
            inputBuffer = ByteBuffer.wrap(input);
            for (int y = 0; y < 512; y++) {

                heapArray[heapIndex].setLong(inputBuffer.getLong());
                heapArray[heapIndex].setDouble(inputBuffer.getDouble());
                heapIndex++;
            }
        }

        Record temp = new Record(0, 0);

        heap = new MinHeap(heapArray, heapArray.length, 4096);
        boolean once = true;
        boolean finishHeap = true;
        long filePointer = file.getFilePointer();
        long fileLength = file.length();
        while (finishHeap && xRun.getEnd() < fileLength) {
            xRun = new Run(file2.size(), 0);
            if (!once) {
                if (heap.maxValueCount() > 0) {
                    moveValues(heapArray);
                    finishHeap = false;
                }
                int heapLength = 4096 - heap.maxValueCount();
                heap = new MinHeap(heapArray, heapLength, heapLength);

            }
            outputIndex = 0;

            if (!inputBuffer.hasRemaining() && filePointer < fileLength) {
                file.read(input);
                filePointer += 8192;
                inputBuffer = ByteBuffer.wrap(input);
            }
            while (heap.heapsize() > 0) {

                if (!outputBuffer.hasRemaining()) {
                    outputBuffer.flip();

                    file2.write(output);

                    outputBuffer.rewind();

                    outputIndex = 0;

                    outputBuffer.clear();
                }

                if (!inputBuffer.hasRemaining() && filePointer < fileLength) {
                    file.read(input);
                    filePointer += 8192;
                    inputBuffer = ByteBuffer.wrap(input);
                }

                if (inputBuffer.hasRemaining()) {

                    temp.setLong(inputBuffer.getLong());
                    temp.setDouble(inputBuffer.getDouble());

                    temp = heap.removemin(temp);
                }
                else {

                    temp = heap.removemin();
                    if (temp.getDouble() == Double.MAX_VALUE && temp
                        .getLong() == 0) {
                        break;
                    }

                }
                outputBuffer.putLong(temp.getLong());

                outputBuffer.putDouble(temp.getDouble());
                outputIndex++;
            }

            file2.write(output, 0, outputIndex * 16);
            xRun.setEnd(file2.size());
            runOffsets.add(xRun);
            outputBuffer.rewind();

            once = false;

            outputBuffer.clear();
        }
        file2.flush();
        file2.close();
        return runOffsets;
    }


    /**
     * 
     * @param array
     *            array being evaluated
     */
    private static void moveValues(Record[] array) {
        int begIndex = 0;
        int endIndex = array.length - 1;
        boolean move1 = false;
        boolean move2 = false;
        while (begIndex < endIndex) {
            if (array[begIndex].getLong() != 0) {
                begIndex++;
                move1 = true;
            }
            if (array[endIndex].getLong() == 0) {
                endIndex--;
                move2 = true;
            }
            if (!move1 && !move2) {
                Record temp = array[endIndex];
                array[endIndex] = array[begIndex];
                array[begIndex] = temp;
                begIndex++;
                endIndex--;

            }
            move1 = false;
            move2 = false;

        }
    }

}
