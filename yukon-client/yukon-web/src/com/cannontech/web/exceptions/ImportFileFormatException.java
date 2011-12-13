package com.cannontech.web.exceptions;

public class ImportFileFormatException extends Exception {
    String headerName;
    String interfaceName;
    
    public ImportFileFormatException() {
    }

    public ImportFileFormatException(String message) {
        super(message);
    }

    public ImportFileFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public ImportFileFormatException(Throwable cause) {
        super(cause);
    }
    
    public void setHeaderName(String name) {
        headerName = name;
    }
    
    public String getHeaderName() {
        return headerName;
    }
    
    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }
    
    public String getInterfaceName() {
        return interfaceName;
    }
}
