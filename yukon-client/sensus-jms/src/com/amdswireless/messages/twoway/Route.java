package com.amdswireless.messages.twoway;

import java.io.Serializable;
import java.net.URL;

/* Route.java
 * 06-20-2005 johng
 *
 * This class encapsulates the tgbId and buddyId (if any)
 * that a message will use to route to a repId.  
 * Instances of this class are passed back in an ArrayList
 * from the RetrieveRoute class.
 */

public class Route implements Serializable {
    private static final long serialVersionUID = 1L;
    private int tgbId;
    private int buddyId;
    private int ncId = 1;
    private URL url;
    private int transmissions =0;

    public Route(int tgbId, int buddyId,URL u) {
        this.tgbId=tgbId;
        this.buddyId=buddyId;
        this.url=u;
    }

    public int getTgbId() {
        return this.tgbId;
    }

    public int getBuddyId() {
        return this.buddyId;
    }

    public int getNcId() {
        return this.ncId;
    }

    public void setNcId(int i) {
        this.ncId=i;
    }

    public URL getUrl() {
        return this.url;
    }

    public void setUrl(URL u) {
        this.url=u;
    }

    public void incrementTransmissions() {
        transmissions++;
    }

    public int getTransmissionCount() {
        return transmissions;
    }

}
