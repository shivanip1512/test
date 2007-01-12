package com.amdswireless.messages.twoway;

public class RetryMessage implements java.io.Serializable {
    
    private static final long serialVersionUID = 2L;
    private String xml;
    private int appSequence;
    private SentMessage sm;

    public RetryMessage(String x, int as) {
        this.xml=x;
        this.appSequence=as;
    }
    
    public void setSentMessage(SentMessage s ) {
    	this.sm =s;
    }
    
    public SentMessage getSentMessage() {
    	return this.sm;
    }

    public String getXML() {
        return this.xml;
    }

    public int getAppSequence() {
        return this.appSequence;
    }
}
