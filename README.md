# Java LRU cache exercise

`O(1)` LRU cache exercise implemented in Java.

## Expected interface

The constructor of the cache object should receive a capacity and a function to compute
a value to a given input.

The cache should provide a `get(K input)` public function to obtain values.
If the value for the given input is not in the cache, it should call the given compute function and
store the value in the cache, and serve it from the cache for this input upon later calls.

If the number of stored items reaches the given `capacity` of the cache, and a new item needs to be
cached (because of a `get` call with an unseen input), then the _least recently used_ (LRU) item
should be dropped from the cache.

The algorithm of the `get` function should have `O(1)` (i.e. constant time) complexity.
