package com.cannontech.web.exceptions;

import com.cannontech.database.TransactionException;

public class InvalidPointOffsetException extends TransactionException {

    public InvalidPointOffsetException() {
        super();
        // TODO Auto-generated constructor stub
    }

    public InvalidPointOffsetException(String s) {
        super(s);
        // TODO Auto-generated constructor stub
    }

    public InvalidPointOffsetException(String s, Throwable t) {
        super(s, t);
        // TODO Auto-generated constructor stub
    }

}
