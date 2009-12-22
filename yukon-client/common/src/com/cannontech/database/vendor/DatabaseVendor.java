package com.cannontech.database.vendor;


public enum DatabaseVendor {
    MS2000    ("Microsoft SQL Server","08."),
    MS2005    ("Microsoft SQL Server","09."),
    MS2008    ("Microsoft SQL Server","10."),
    ORACLE9I  ("Oracle","Oracle9i Enterprise Edition Release"),
    ORACLE10G ("Oracle","Oracle Database 10g Release"),
    ORACLE11G ("Oracle","Oracle Database 11g Release"), 
    UNKNOWN("", "") {
        @Override
        public String getDescription() {
            return "Unkown";
        }
    }
    ;
    
    private final String venderText;
    private final String productVersionPrefix;
    
    DatabaseVendor(String venderText, String productVersionPrefix) {
        this.venderText = venderText;
        this.productVersionPrefix = productVersionPrefix;
    }
    public String getVenderText() { return this.venderText; }
    public String getProductVersionPrefix() { return this.productVersionPrefix; }

    public String getDescription() {
        return getVenderText() + ", " + getProductVersionPrefix();
    }

}

