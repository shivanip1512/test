package com.cannontech.web.exceptions;

import com.cannontech.database.TransactionException;

public class PortDoesntExistException extends TransactionException {

    final private static String defaultMsg = "ERROR: Please specify a valid communication chanel.";  
    
    public PortDoesntExistException(String message) {
        super(message);
       
    }

    public PortDoesntExistException() {
        
         super(defaultMsg);
    
    }
    
    

    

}
