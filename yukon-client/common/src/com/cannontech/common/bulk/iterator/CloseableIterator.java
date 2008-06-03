package com.cannontech.common.bulk.iterator;

import java.io.Closeable;
import java.util.Iterator;

/**
 * An iterator whose return method should be called after it has
 * been used. Implementations of this interface may use a no-op
 * for the close method, but the caller must always invoke the 
 * close method.
 */
public interface CloseableIterator<T> extends Iterator<T>, Closeable {

}
