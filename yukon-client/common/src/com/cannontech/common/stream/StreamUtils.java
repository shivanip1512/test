package com.cannontech.common.stream;

import java.util.Map;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 * Utility class providing functions and objects helpful for working with streams.
 */
public final class StreamUtils {
    private StreamUtils() {} //Utility class, not instantiable
    
    /**
     * Similar to {@link Collectors.toMap}, but returns a Collector that collects a stream of objects into a Guava 
     * Multimap.
     * <p>
     * For example, collecting a stream of Objects into a Multimap where the Object's String representation is the key,
     * and the Object is the value:
     * 
     * <pre>{@code 
     * List<Object> objectList = Lists.newArrayList(new Object(), new Object(), new Object());
     * 
     * Multimap<String, Object> multimap = objectList.stream()
     *                                               .collect(toMultimap(Object::toString,
     *                                                                   Function.identity()));
     * }</pre>
     * 
     * @param <O> The type of the objects in the input stream.
     * @param <K> The type of the Multimap keys.
     * @param <V> The type of the Multimap values.
     * @param keyMapper A function for mapping the objects from the stream to keys in the map.
     * @param valueMapper A function for mapping the objects from the stream to values in the map.
     * @return A Collector that collects a stream of objects into a Guava Multimap.
     */
    public static final <O,K,V> Collector<O,?,Multimap<K,V>> toMultimap(Function<O,K> keyMapper, 
                                                                        Function<O,V> valueMapper) {
        return Collector.of(
            HashMultimap::create,
            (map, object) -> map.put(keyMapper.apply(object), valueMapper.apply(object)),
            (map1, map2) -> { map1.putAll(map2); return map1; }
        );
    }
    
    /**
     * Identical to {@link toMultimap}, but to be used when the {@code valueMapper} returns an Iterable of values
     * instead of a single value.
     * 
     * @param keyMapper A function for mapping the objects from the stream to keys in the map.
     * @param valueMapper A function for mapping the objects from the stream to values in the map.
     * @return
     */
    public static final <O,K,V> Collector<O,?,HashMultimap<K,V>> mappedValueToMultimap(Function<O,K> keyMapper, 
                                                                        Function<O,Iterable<V>> valueMapper) {
        
        return Collector.of(
            HashMultimap::create,
            (map, object) -> map.putAll(keyMapper.apply(object), valueMapper.apply(object)),
            (map1, map2) -> { map1.putAll(map2); return map1; }
        );
    }
    
    /**
     * Similar to {@link Collectors.groupingBy}, but produces a Guava Multimap instead of a Map of Lists.
     * <p>
     * Returns a Collector implementing a "group by" operation on input elements of type O, grouping elements according 
     * to a classification function, and returning the results in a Guava Multimap. The classification function maps 
     * elements to some key type K. 
     * <p>
     * The collector produces a {@code Multimap<K, O>} whose keys are the values resulting 
     * from applying the classification function to the input elements, and whose corresponding values are the input 
     * elements which map to the associated key under the classification function.
     * <p>
     * For example, creating a Multimap of WidgetTypes, grouped by their WidgetCategory (one of their properties):
     * 
     * <pre>{@code 
     * Multimap<WidgetCategory, WidgetType> multimap = WidgetType.stream()
     *                                                           .collect(groupingBy(WidgetType::getCategory));
     * 
     * }</pre>
     * 
     * @param <O> The type of the objects in the input stream, and the values in the Multimap.
     * @param <K> The type of the keys in the Multimap.
     * @param classifier A function that generates classifications of objects
     * @return A Collector that collects a stream of objects into a Guava Multimap, with keys dictated by the classifier
     * function.
     */
    public static final <O,K> Collector<O,?,HashMultimap<K,O>> groupingBy(Function<O,K> classifier) {
        return Collector.of(
             HashMultimap::create, 
             (map, object) -> map.put(classifier.apply(object), object), 
             (map1, map2) -> { map1.putAll(map2); return map1; }
        );
    }
    
    /**
     * Returns a Collector that collects a stream of objects into a Map. The values are the unmodified objects from the
     * stream. The keys are dictated by the <code>keyMapper<code> Function.
     * <p>
     * This is equivalent to {@code Collectors.toMap(keyMapper, Function.identity())}.
     * 
     * @param keyMapper A Function to map the input to a key.
     * @return A Collector that collects a stream of objects into a Map.
     */
    public static final <O,K> Collector<O,?,Map<K,O>> mapToSelf(Function<O, K> keyMapper) {
        return Collectors.toMap(keyMapper, Function.identity());
    }
    
    /**
     * Returns a Collector that collects a stream of objects into a Map. The keys are the unmodified objects from the
     * stream. The values are dictated by the <code>valueMapper<code> Function.
     * <p>
     * This is equivalent to {@code Collectors.toMap(Function.identity(), valueMapper)}.
     * 
     * @param valueMapper
     * @return A Collector that collects a stream of objects into a Map.
     */
    public static final <K,V> Collector<K,?,Map<K,V>> mapSelfTo(Function<K,V> valueMapper) {
        return Collectors.toMap(Function.identity(), valueMapper);
    }
    
    /**
     * Generates a Stream via an indexed getter function and a length. This makes it cleaner to get a stream out of an 
     * indexed object that isn't a Java Collection.
     * 
     * <pre>{@code 
     * List<String> list = new ArrayList<>();
     * Stream<String> listValuesStream = stream(list::get, list.size());
     * }</pre>
     * 
     * @param indexedGetterFunction A function that accepts an int parameter and provides an object for the stream.
     * @param length The number of times to call the function.
     * @return A Stream consisting of the objects returned by the specified function.
     */
    public static final <T> Stream<T> stream(IntFunction<T> indexedGetterFunction, int length) {
        return IntStream.range(0, length).mapToObj(indexedGetterFunction); 
    }
    
    /**
     * Helper function to invoke the typical spliterator+StreamSupport calls to create a non-parallel stream of an Iterable. 
     * @param iterable the Iterable<T> to stream
     * @return the resulting Stream<T>
     */
    public static final <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(iterable.spliterator(), false);
    }

    /**
     * Returns a Predicate that is the logical negation of the supplied Predicate.  Provides cleaner method reference negation.
     * 
     * <pre>{@code 
     * List<String> list = new ArrayList<>();
     * Stream<String> notEmpty = list.stream().filter(not(String::empty));
     * }</pre>
     * 
     * @param pred The predicate to negate.
     * @return The negated predicate.
     */
    public static final <T> Predicate<T> not(Predicate<T> pred) {
        return pred.negate();
    }
}
