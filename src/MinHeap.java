
/**
 * Implementation of MinHeap
 * 
 * @author rmclaren, swooty87
 * @version Apr 7, 2019
 */
class MinHeap {
    private Record[] heap; // Pointer to the heap array
    private int size; // Maximum size of the heap
    private int n; // Number of things now in heap
    private boolean hasValue; // Tells whether or not there are still values
                              // from the file in the heap
    private int maxCount; // Determines how many MAX_VALUES have been inserted


    /**
     * Constructor supporting preloading of heap contents
     * 
     * @param h
     *            heap
     * @param num
     *            number of things now in heap
     * @param max
     *            is the max number of things to go in the heap
     */
    public MinHeap(Record[] h, int num, int max) {
        heap = h;
        n = num;
        size = max;
        buildheap();

    }


    /**
     * Set size of heap
     * 
     * @param num
     *            number being set for n
     */
    void setSize(int num) {
        n = num;
    }


    /**
     * Getter method for max value count
     * 
     * @return maxcount field
     */
    int maxValueCount() {
        return maxCount;
    }


    /**
     * Return current size of the heap
     * 
     * @return n field
     */
    int heapsize() {
        return n;
    }


    /**
     * Checks if hepa is empty
     * 
     * @return true if heap has values in heap, false if heap is empty
     */
    boolean hasValue() {
        return hasValue;
    }


    /**
     * Checks if position is leaf
     * 
     * @param pos
     *            position being checked
     * @return true if pos a leaf position, false otherwise
     */
    boolean isLeaf(int pos) {
        return (pos >= n / 2) && (pos < n);
    }


    /**
     * Finds left child's position
     * 
     * @param pos
     *            position
     * @return position for left child of pos
     */
    int leftchild(int pos) {
        if (pos >= n / 2) {
            return -1;
        }
        return 2 * pos + 1;
    }


    /**
     * Finds right child's position
     * 
     * @param pos
     *            position
     * @return position for right child of pos
     */
    int rightchild(int pos) {
        if (pos >= (n - 1) / 2) {
            return -1;
        }
        return 2 * pos + 2;
    }


    /**
     * Finds parent's position
     * 
     * @param pos
     *            position
     * @return position for parent
     */
    int parent(int pos) {
        if (pos <= 0) {
            return -1;
        }
        return (pos - 1) / 2;
    }


    /**
     * Insert value to heap
     * 
     * @param key
     *            recording being added
     */
    void insert(Record key) {
        if (n >= size) {
            return;
        }
        int curr = n++;
        heap[curr] = key; // Start at end of heap
        // Now shift up until curr's parent's key < curr's key
        while ((curr != 0) && (heap[curr].compareTo(heap[parent(curr)]) < 0)) {
            swap(curr, parent(curr));
            curr = parent(curr);
        }
    }


    /**
     * heapify contents of heap
     */
    void buildheap() {
        hasValue = true;
        for (int i = n / 2; i >= 0; i--) {
            siftdown(i);
        }
    }


    /**
     * Put elements in its correct place
     * 
     * @param pos
     *            position
     */
    void siftdown(int pos) {
        if ((pos < 0) || (pos >= n))
            return; // Illegal position
        while (!isLeaf(pos)) {
            int j = leftchild(pos);

            if ((j < (n - 1)) && (heap[j].compareTo(heap[j + 1]) > 0)) {
                j++; // j is now index of child with smaller value
            }

            if (heap[pos].compareTo(heap[j]) < 0) {
                return;
            }
            swap(pos, j);
            pos = j; // Move down
        }
    }


    /**
     * Remove minimum value
     * 
     * @param m
     *            record
     * @return minimum value
     */
    Record removemin(Record m) {
        if (n == 0) {
            return null; // Removing from empty heap
        }
        Record min = heap[0];

        heap[0] = m;
        if (m.compareTo(min) < 0) {
            hasValue = true;
            swap(0, n - 1);
            n--;
        }
        siftdown(0);
        return min;
    }


    /**
     * Remove min
     * 
     * @return min value
     */
    Record removemin() {
        if (n == 0) {
            return null; // Removing from empty heap
        }
        Record minReturn = new Record(heap[0].getLong(), heap[0].getDouble());

        heap[0].setDouble(Double.MAX_VALUE);
        heap[0].setLong((long)0);
        swap(0, n - 1);
        n--;
        siftdown(0);
        maxCount++;

        return minReturn;

    }


    /**
     * Modify the value at the given position
     * 
     * @param pos
     *            is the position to change
     * @param newVal
     *            is the new value
     */
    void modify(int pos, Record newVal) {
        if ((pos < 0) || (pos >= n)) {
            return; // Illegal heap position
        }
        heap[pos] = newVal;
        update(pos);
    }


    /**
     * The value at pos has been changed, restore the heap property
     * 
     * @param pos
     *            is the position to update
     */
    void update(int pos) {
        // If it is a big value, push it up
        while ((pos > 0) && (heap[pos].compareTo(heap[parent(pos)]) < 0)) {
            swap(pos, parent(pos));
            pos = parent(pos);
        }
        if (n != 0) {
            siftdown(pos); // If it is little, push down
        }
    }


    /**
     * Swaps two positions
     * 
     * @param pos
     *            original position
     * @param newPos
     *            new position
     */

    void swap(int pos, int newPos) {
        Record temp = heap[pos];
        heap[pos] = heap[newPos];
        heap[newPos] = temp;
    }


    /**
     * Prints out the heap
     */
    void printString() {
        for (int x = 0; x < heap.length / 2; x++) {
            System.out.println(" PARENT : " + heap[x].toString()
                + " LEFT CHILD : " + heap[2 * x].toString() + " RIGHT CHILD :"
                + heap[2 * x + 1].toString());
        }
    }
}
