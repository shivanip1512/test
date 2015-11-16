package com.cannontech.web.search.lucene;

import java.io.IOException;

import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

public interface TopDocsCallbackHandler<E> {
    E processHits(TopDocs topDocs, IndexSearcher indexSearcher) throws IOException;
}
