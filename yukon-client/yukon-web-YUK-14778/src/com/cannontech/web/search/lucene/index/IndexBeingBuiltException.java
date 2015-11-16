package com.cannontech.web.search.lucene.index;

public class IndexBeingBuiltException extends RuntimeException {
    public IndexBeingBuiltException(String msg) {
        super(msg);
    }
}
