package com.cannontech.cbc.exceptions;

import com.cannontech.database.TransactionException;

public class PortDoesntExistException extends TransactionException {

    final private static String defaultMsg = "ERROR: Please specify a valid communication channel.";  
    
    public PortDoesntExistException(String message) {
        super(message);
       
    }

    public PortDoesntExistException() {
        
         super(defaultMsg);
    
    }
    
    

    

}
