package com.amdswireless.messages.twoway;

public class SendNotification implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private int repId;
    private int appSequence;
    private int toi;
    private int tgb;
    private java.util.Date timestamp;

    public SendNotification( int l, java.util.Date t, int as, int r, int tg ) {
        this.repId = l;
        this.timestamp = t;
        this.appSequence = as;
        this.toi=r;
        this.tgb=tg;
    }

    public String getKey() {
        // we're just using REPID as the key, since AppSeq is not universally returned
        //return new String(repId+":"+appSequence);
        return new String(Long.toString(repId));
    }

    public int getRepId() {
        return this.repId;
    }

    public int getAppSequence() {
        return this.appSequence;
    }

    public java.util.Date getTimestamp() {
        return this.timestamp;
    }

    public int getToi() {
        return this.toi;
    }

    public int getTgbId() {
        return this.tgb;
    }

}
