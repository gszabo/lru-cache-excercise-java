package test.java.hello;

import main.java.hello.LruCache;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;


public class LruCacheTest {

    @Test
    void zeroCapacity_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new LruCache<>(0, x -> x));
    }

    @Test
    void negativeCapacity_exceptionThrown() {
        assertThrows(IllegalArgumentException.class, () -> new LruCache<>(-1, x -> x));
        assertThrows(IllegalArgumentException.class, () -> new LruCache<>(-5, x -> x));
    }

    @Test
    void dataAccessedFirstTime_computesTheResult() {
        Function<Integer, Integer> computeValue = x -> x + 10;
        int capacity = 1;
        LruCache<Integer, Integer> cache = new LruCache<>(capacity, computeValue);
        assertEquals(11, (int) cache.get(1));
    }

    @Test
    void dataAccessedSecondTime_resultComesFromCache() {
        SpyFunction<Integer, Integer> computeValue = new SpyFunction<>(x -> x + 10);
        int capacity = 1;

        LruCache<Integer, Integer> cache = new LruCache<>(capacity, computeValue);

        assertEquals(11, (int) cache.get(1));
        assertEquals(11, (int) cache.get(1));

        assertEquals(asList(1), computeValue.calls);
    }

    @Test
    void cachingTestWithMoreCapacity() {
        SpyFunction<Integer, Integer> computeValue = new SpyFunction<>(x -> x + 10);
        int capacity = 3;

        LruCache<Integer, Integer> cache = new LruCache<>(capacity, computeValue);

        assertEquals(11, (int) cache.get(1));
        assertEquals(11, (int) cache.get(1));
        assertEquals(11, (int) cache.get(1));
        assertEquals(13, (int) cache.get(3));
        assertEquals(13, (int) cache.get(3));
        assertEquals(13, (int) cache.get(3));
        assertEquals(12, (int) cache.get(2));
        assertEquals(12, (int) cache.get(2));
        assertEquals(12, (int) cache.get(2));

        assertEquals(asList(1, 3, 2), computeValue.calls);
    }

    @Test
    void cacheIsFullWithOneItem_secondDataReplacesFirst() {
        SpyFunction<Integer, Integer> computeValue = new SpyFunction<>(x -> x + 10);
        int capacity = 1;

        LruCache<Integer, Integer> cache = new LruCache<>(capacity, computeValue);

        assertEquals(11, (int) cache.get(1));
        assertEquals(11, (int) cache.get(1));
        assertEquals(12, (int) cache.get(2));
        assertEquals(12, (int) cache.get(2));
        assertEquals(11, (int) cache.get(1));

        assertEquals(asList(1, 2, 1), computeValue.calls);
    }

    @Test
    void cacheIsFullWithTwoItem_thirdDataReplacesFirst() {
        SpyFunction<Integer, Integer> computeValue = new SpyFunction<>(x -> x + 10);
        int capacity = 2;

        LruCache<Integer, Integer> cache = new LruCache<>(capacity, computeValue);

        assertEquals(11, (int) cache.get(1));
        assertEquals(12, (int) cache.get(2));
        assertEquals(13, (int) cache.get(3));
        assertEquals(12, (int) cache.get(2));

        assertEquals(asList(1, 2, 3), computeValue.calls);
    }

    @Test
    void leastRecentlyUsedItemIsRemoved() {
        SpyFunction<Integer, Integer> computeValue = new SpyFunction<>(x -> x + 10);
        int capacity = 2;

        LruCache<Integer, Integer> cache = new LruCache<>(capacity, computeValue);

        assertEquals(11, (int) cache.get(1));
        assertEquals(12, (int) cache.get(2));
        assertEquals(11, (int) cache.get(1));
        assertEquals(13, (int) cache.get(3));
        assertEquals(12, (int) cache.get(2));

        assertEquals(asList(1, 2, 3, 2), computeValue.calls);
    }

}

class SpyFunction<K, V> implements Function<K, V> {

    public List<K> calls = new ArrayList<>();

    private final Function<K, V> func;

    public SpyFunction(Function<K, V> func) {
        this.func = func;
    }

    @Override
    public V apply(K input) {
        calls.add(input);
        return func.apply(input);
    }
}