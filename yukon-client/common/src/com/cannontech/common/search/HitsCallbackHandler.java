package com.cannontech.common.search;

import java.io.IOException;

import org.apache.lucene.search.Hits;

public interface HitsCallbackHandler<E> {
    E processHits(Hits hits) throws IOException;
}
