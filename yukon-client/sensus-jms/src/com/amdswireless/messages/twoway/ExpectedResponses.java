package com.amdswireless.messages.twoway;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

import com.amdswireless.messages.twoway.OneResponse;

public class ExpectedResponses implements Serializable {
    private static final long serialVersionUID = 1L;
    Hashtable<String,OneResponse> expectedResponses;
    transient int appSeq = 0;
    transient StringBuffer returnMessage;

    public ExpectedResponses() {
        expectedResponses = new Hashtable<String,OneResponse>();
        returnMessage = new StringBuffer();
    }

    public void add(String s,OneResponse o) {
        expectedResponses.put(s, o);
    }

    public Hashtable<String,OneResponse> getHash() {
        return expectedResponses;
    }

    public int get(String s) {
        if ( !expectedResponses.containsKey(s)) {
            return 0;
        } else {
            OneResponse o = (OneResponse)expectedResponses.get(s);
            return o.getMsgCount();
        }
    }

    public void set(String s, OneResponse o) {
        if ( expectedResponses.containsKey(s) ) {
            expectedResponses.put(s, o);
        }
    }

    public boolean isComplete() {
        boolean complete=true;
        Enumeration e = expectedResponses.keys();
        while (e.hasMoreElements() ) {
            OneResponse o = (OneResponse)expectedResponses.get(e.nextElement());
            int val = o.getMsgCount();
            if ( val != 0 ) {
                complete=false;
            }
        }
        return complete;
    }

    public int getAppSeq() {
        return this.appSeq;
    }

    public void setAppSeq(int a) {
        this.appSeq=a;
    }

    public void messageReceived(String s) {
        if ( expectedResponses.containsKey(s) ) {
            OneResponse o = (OneResponse)expectedResponses.get(s);
            int val = o.getMsgCount();
            val--;
            if ( val<0) { val=0; }
            o.setMsgCount(val);
            expectedResponses.put(s,o);
        }
    }

    public boolean containsType( String s ) {
        if ( expectedResponses.containsKey(s) ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean typeComplete( String s ) {
        if ( expectedResponses.containsKey(s) ) {
            OneResponse o = (OneResponse)expectedResponses.get(s);
            if ( o.getMsgCount() > 0 ) {
                return false;
            }
            return true;
        }
        return true;
    }
            
    public String getReturnMessage() {
        return returnMessage.toString();
    }

    public int getExpectedResponseCount() {
        return expectedResponses.size();
    }

    public int getRemainingResponseCount() {
        int remaining=0;
        Enumeration e = expectedResponses.keys();
        while (e.hasMoreElements() ) {
            OneResponse o= (OneResponse)expectedResponses.get(e.nextElement());
            int val = o.getMsgCount();
            if ( val != 0 ) {
                remaining+=val;
            }
        }
        return remaining;
    }

    public String getRemainingResponseList() {
        StringBuffer remaining = new StringBuffer();
        Enumeration e = expectedResponses.keys();
        while (e.hasMoreElements() ) {
            String s  = (String)e.nextElement();
            OneResponse o= (OneResponse)expectedResponses.get(s);
            int val = o.getMsgCount();
            if ( val != 0 ) {
                remaining.append("\tType "+s+"\t"+val+" remaining messages.\n");
            }
        }
        return remaining.toString();
    }

}
