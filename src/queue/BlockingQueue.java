package queue;

import java.util.LinkedList;
import java.util.Queue;

public class BlockingQueue<T> {

    private final Queue<T> queue = new LinkedList<>();
    private final int size;

    public BlockingQueue(int size) {
        this.size = size;
    }

    // Inserts the specified element into this queue if it is possible to do so
    // Returns true upon success and throwing an IllegalStateException if no space is currently available.
    public synchronized void add(T element) throws IllegalStateException {
        if (isFull()) {
            throw new IllegalStateException("Queue is full");
        }
        queue.add(element);
        notifyAll();
    }

    // Inserts the specified element into this queue, waiting if necessary for space to become available.
    public synchronized void put(T element) throws InterruptedException {
        while (isFull()) {
            wait();
        }
        queue.add(element);
        notifyAll();
    }

    // Inserts the specified element into this queue if it is possible to do so immediately without violating capacity restrictions,
    // returning true upon success and false if no space is currently available.
    public synchronized boolean offer(T element) {
        if (isFull()) {
            return false;
        }
        boolean result = queue.offer(element);
        notifyAll();
        return result;
    }

    // Inserts the specified element into this queue,
    // waiting up to the specified wait time if necessary for space to become available.
    public synchronized boolean offer(T element, long timeoutMillis) throws InterruptedException {
        if (isFull()) {
            wait(timeoutMillis);
            if (isFull()) {
                return false;
            }
        }
        boolean result = queue.offer(element);
        notifyAll();
        return result;
    }

    // Retrieves and removes the head of this queue,
    // waiting if necessary until an element becomes available.
    public synchronized T take() throws InterruptedException {
        while (isEmpty()) {
            wait();
        }
        T result = queue.poll();
        notifyAll();
        return result;
    }

    // Retrieves and removes the head of this queue,
    // waiting up to the specified wait time if necessary for an element to become available.
    public synchronized T poll(long timeoutMillis) throws InterruptedException {
        if (isEmpty()) {
            wait(timeoutMillis);
            if (isEmpty()) {
                return null;
            }
        }
        T result = queue.poll();
        notifyAll();
        return result;
    }

    // Returns true if this queue is full.
    public synchronized boolean isFull() {
        return queue.size() >= size;
    }

    // Returns true if this queue is empty.
    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }

    // Removes all the elements from this queue.
    public synchronized void clear() {
        queue.clear();
        notifyAll();
    }

}
