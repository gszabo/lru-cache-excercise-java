package main.java.hello;

import java.util.function.Function;
import java.util.*;

public class LruCache<K, V> {
    private final int capacity;
    private final Function<K, V> computeValue;

    private Map<K, Node<K, V>> store;
    private Node<K, V> head, tail;

    public LruCache(int capacity, Function<K, V> computeValue) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("capacity");
        }

        this.capacity = capacity;
        this.computeValue = computeValue;

        this.store = new HashMap<>();
        this.head = null;
        this.tail = null;
    }

    public V get(K input) {
        if (store.containsKey(input)) {
            moveItemToMostRecentPlace(input);
            return store.get(input).value;
        } else {
            if (isFull()) {
                deleteLeastRecentItem();
            }
            V result = this.computeValue.apply(input);
            addNewMostRecentItem(input, result);
            return result;
        }
    }

    private void moveItemToMostRecentPlace(K input) {
        if (size() > 1) {
            Node<K, V> nodeToMove = store.get(input);
            if (nodeToMove == tail) {
                tail = nodeToMove.prev;
            }
            nodeToMove.detach();
            makeHead(nodeToMove);
        }
    }

    private boolean isFull() {
        return size() == capacity;
    }

    private void deleteLeastRecentItem() {
        K lruItem = tail.key;
        removeTail();
        store.remove(lruItem);
    }

    private void addNewMostRecentItem(K input, V result) {
        Node<K, V> n = new Node<>(input, result);

        if (size() == 0) {
            head = tail = n;
        } else {
            makeHead(n);
        }

        store.put(input, n);
    }

    private void makeHead(Node<K, V> node) {
        node.next = head;
        head.prev = node;
        head = node;
    }

    private void removeTail() {
        if (size() == 1) {
            head = tail = null;
        } else {
            tail = tail.detach();
        }
    }

    private int size() {
        return store.size();
    }
}

class Node<K, V> {
    final K key;
    final V value;

    Node<K, V> prev;
    Node<K, V> next;

    Node(K key, V value) {
        this.key = key;
        this.value = value;
    }

    Node<K, V> detach() {
        Node<K, V> result = prev;
        jumpOut();
        prev = next = null;
        return result;
    }

    private void jumpOut() {
        if (prev != null) {
            prev.next = next;
        }
    }
}
