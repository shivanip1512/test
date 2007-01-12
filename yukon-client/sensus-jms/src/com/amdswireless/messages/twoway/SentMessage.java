package com.amdswireless.messages.twoway;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.amdswireless.messages.rx.AppMessageTypeE;
import com.amdswireless.messages.rx.AppMessageTypeETier0;
import com.amdswireless.messages.rx.AppMessageTypeETier7;
import com.amdswireless.messages.rx.DataMessage;
import com.amdswireless.messages.tx.TxMsg;
import com.amdswireless.messages.tx.TxSingleTierReadMsg;
import com.amdswireless.messages.twoway.OneResponse;
import com.amdswireless.messages.twoway.Route;
import org.apache.log4j.Logger;

public class SentMessage implements java.io.Serializable {
    private static Logger log = Logger.getLogger("twoway");
    private static final long serialVersionUID = 2L;
    private int repId;
    private int appSeq;
    private Date transmitTime;
    private Date startTime;
    private Date expireTime;
    private Date jmsTransitStart;
    private EngineParameters engineParams;
    private int reqType;
/*
    private int numIterations;
    private int numRoutes;
    private int deltaRoutes = 6000;
    private int deltaIterations = 6000;
    private int postTransmitWait = 150000;
*/
    private int transmissionCount = 0;
    private int transmissionFailureCount = 0;
    // routeSelectMode selects interleave versus iteration mode
    // It is used in the getNextRoute method to decide how to 
    // select the next route
    private int routeSelectMode = 0;
    private TxMsg txMsg;
    private String correlationId;
    private String requestor;
    private String username;
    private String xml; //this is the text of the original XML message that came into the TransmitAgent
    // the routes array list contains all known routes to the endpoint, ordered
    // by quality (a magic number calculated elsewhere)
    private ArrayList<Route> routes;
    private ExpectedResponses expectedResponses = new ExpectedResponses();
    private Vector<DataMessage> responses = new Vector<DataMessage>();
    private Hashtable<String,Boolean> tiersReceived = new Hashtable<String,Boolean>();
    private int tier=-1;
    private int subtier=-1;
    private boolean multiTier=false;
    private int currentRoute;
    private int currentIteration;
    private int tiersExpected=0;
    private int subtiersExpected=0;
    private String lastTxKey = null;

    public SentMessage(int l, TxMsg txMsg, int a, int r, java.util.Date t, String cid ) {
        this.repId=l;
        this.txMsg=txMsg;
        this.appSeq=a;
        this.startTime=t;
        this.reqType=r;
        this.correlationId=cid;
        this.engineParams = new EngineParameters();
    }

    public void setRoutes( ArrayList<Route> r, int numroutes, int iterations ) {
        this.routes = r;
        this.engineParams.setRoutesToTry(numroutes);
        this.engineParams.setTriesPerRoute(iterations);
        int i=0;
        while ( routes.size() < numroutes ) {
            Route route = routes.get(i);
            routes.add(route);
            i++;
        }
    }

    public ArrayList<Route> getRoutes() {
        return routes;
    }

    public Route getRoute(int i) {
        if ( routes.size() > i ) {
            return (Route)routes.get(i);
        } else {
            return null;
        }
    }

    public void resetCounters() {
        this.currentRoute=0;
        this.currentIteration=0;
        this.transmissionCount=0;
    }

    public Route getNextRoute() throws RouteLookupException {
        Route returnRoute = null;
        if ( routes.size() == 0 ) {
            return returnRoute;
        }
        if ( routeSelectMode == 0 ) {
            if ( routes.size() == 0 ) {
                throw new RouteLookupException("There are no routes in the Array List");
            }
            if ( routes.size() < currentRoute ) {
                currentRoute=0;
            }
            if ( currentRoute > routes.size() ) {
                currentRoute = routes.size()-1;
            }
            try {
                returnRoute = (Route)routes.get(currentRoute);
            } catch ( Exception ex ) {
                throw new RouteLookupException("Route number "+currentRoute+" doesn't seem to exist:  "+ex);
            }
            currentRoute++;
            if ( currentRoute >= engineParams.getRoutesToTry() ) {
                currentRoute=0;
                currentIteration++;
            }
        } else if ( routeSelectMode == 1 ){
            currentIteration++;
            if ( currentIteration >= engineParams.getTriesPerRoute() ) {
                currentIteration = 0;
                currentRoute++;
            }
            if ( currentRoute >= engineParams.getRoutesToTry() ) {
                currentRoute=0;
            }
            returnRoute = (Route)routes.get(currentRoute);
        }
        return returnRoute;
    }

    public void setRouteSelectMode(int i) {
        this.routeSelectMode=i;
    }

    public int getRouteSelectMode() {
        return this.routeSelectMode;
    }

    public void tgbDown(int tgbId) {
        int i=0;
        while ( i<routes.size() ) {
            Route oneRoute = (Route)routes.get(i);
            if ( oneRoute.getTgbId() == tgbId ) {
                routes.remove(i);
            } else {
                i++;
            }
        }
        // if we've deleted ALL our routes, we're screwed, and
        // the route count will be zero.  SEP.
        if ( routes.size() == 0 ) {
            return;
        }
        // pad the route list back up to the proper size
        // (this was a NASTY bug that took hours to find,
        // kudos to kevo for helping me find it)
        i=0;
        while ( routes.size() < engineParams.getRoutesToTry() ) {
            Route route = (Route)routes.get(i);
            routes.add(route);
            i++;
        }
    }

    public int getRemainingRoutes() {
        return routes.size();
    }

    public void setTxMsg( TxMsg t ) {
        this.txMsg = t;
    }

    public TxMsg getTxMsg() {
        // if this is a multi-tier request, we need to search
        // through the list of tiers received for the next
        // tier that we need to fetch, then alter the TxMsg to
        // request this next tier
        if ( multiTier ) {
            log.debug("Multitier request for "+repId+" being processed, moving on to next tier of "+tiersReceived.size());
            Enumeration k = tiersReceived.keys();
            while ( k.hasMoreElements() ) {
                String c = (String)k.nextElement();
                Boolean b = (Boolean)tiersReceived.get(c);
                if ( b.booleanValue() == false ) {
                    log.debug("Repid: "+repId+" tier "+c+" complete:  "+b );
                    String[] tokens = c.split(":");
                    int tier=0;
                    int subtier=0;
                    try {
                        tier = Integer.parseInt(tokens[0]);
                        subtier = Integer.parseInt(tokens[1]);
                    } catch ( NumberFormatException nfe ) {
                        log.error("Unable to extract tier/subtier information to determine next target:  "+nfe );
                    }
                    log.debug("Creating new TxSingleTierReadMsg");
                    TxSingleTierReadMsg newMesg = new TxSingleTierReadMsg();
                    log.debug("Setting tier to "+tier );
                    newMesg.setTier((short)tier);
                    log.debug("Setting subtier to "+subtier );
                    newMesg.setSubtier((short)subtier);
                    newMesg.setRepId(this.repId);
                    txMsg = newMesg;
                    log.debug("Next Tier for "+repId+" is "+tier+":"+subtier );
                    break;
                }
            }
        }
        return this.txMsg;
    }

    public void setExpectedResponses( ExpectedResponses e ) {
        this.expectedResponses = e;
    }

    public ExpectedResponses getExpectedResponses() {
        return this.expectedResponses;
    }

    public Hashtable<String,OneResponse> getExpectedResponseHash() {
        return expectedResponses.getHash();
    }

    public String getKey() {
        return new String( Long.toString(repId) );
    }

    public int getRepId() {
        return this.repId;
    }

    public int getAppSequence() {
        return this.appSeq;
    }

    public java.util.Date getStartTime() {
        return this.startTime;
    }

    public void setJmsTransitStart() {
        this.jmsTransitStart = new Date();
    }

    public void setJmsTransitStart(Date d) {
        this.jmsTransitStart = d;
    }

    public Date getJmsTransitStart() {
        return this.jmsTransitStart;
    }

    public int getReqType() {
        return this.reqType;
    }

    public String getRequestor() {
        return this.requestor;
    }

    public void setRequestor(String r) {
        this.requestor=r;
    }

    public String getUsername() {
        return this.username;
    }
    public void setUsername(String username) {
        this.username=username;
    }

    public String getCorrelationId() {
        return this.correlationId;
    }

    public String getXML() {
        return this.xml;
    }

    public Date getTxTime() {
        return this.transmitTime;
    }

    public void setTxTime(Date t) {
        this.transmitTime=t;
    }

    public void setSubtier(int i) {
        this.subtier=i;
    }

    public void setTier(int i) {
        this.tier=i;
    }

    public int getSubtier() {
        return this.subtier;
    }

    public int getTier() {
        return this.tier;
    }

    public int getTiersExpected() {
        return this.tiersExpected;
    }

    public int getReceivedTierCount() {
        Enumeration e = tiersReceived.keys();
        int trueCount = 0;
        while ( e.hasMoreElements() ) {
            boolean valueTrue = tiersReceived.get((String)e.nextElement()).booleanValue();
            if ( valueTrue ) {
                trueCount++;
            }
        }
        return trueCount;
    }

    public void processMessage( DataMessage msg ) {
        String msgClass = msg.getMsgClass();
        int returnAppSeq = msg.getAppSeq();
        log.info("Processing a "+msgClass+" message from repid "+this.repId );
        // first, check to see if we are even interested in
        // messages of this type.  if we're not, return
        if ( expectedResponses.get(msgClass) == 0 ) {
            return;
        }
        // Here is a kludge to fix AppMessageTypeF responses.
        // Because the turn-around time for Type F (raw) messages
        // is so short, it's possible that we get the response to a
        // previous request and view it as a response to the current
        // request.  Fortunately, the meter returns the same AppSeq
        // that the REQUEST contained.  So, if this response does not
        // contain the same appseq as the request, this is a bogus message
        // and we need to ignore it.
        if ( msgClass.equals("RAW") ) {
            if (returnAppSeq != this.appSeq ) {
                log.warn("Type F message AppSeq ("+returnAppSeq+") does not match req appseq ("+this.appSeq+"). Tossing msg");
                return;
            }
        }
        // non-type-E messages are low-hanging fruit
        // do them first
        if ( !msgClass.equals("TIER") ) {
            // this is easy, really.
            // mark this messagetype as recieved in the
            // expected Responses hash, and add
            // this response (in object form)
            // to the array of responses we're going to
            // return eventually
            expectedResponses.messageReceived(msgClass);
            log.debug("Adding type "+msgClass+" to response vector for repid "+this.repId);
            responses.add(msg);
            return;
        }
        if ( msg instanceof AppMessageTypeE ) {
            // bang, once we get a single tier, we
            // will never ping for a multi-tier message again
            // so, we now mark this as multi-tier true
            setMultiTier(true);
            resetCounters();
            log.info("Got a type E message from "+this.repId );
            if ( msg instanceof AppMessageTypeETier0 ) {
                AppMessageTypeETier0 eMsg = (AppMessageTypeETier0)msg;
                tiersExpected = eMsg.getTierCount();
                subtiersExpected = eMsg.getSubtierCount();
                log.info("Got a Tier "+eMsg.getTier()+":"+eMsg.getSubtier()+" message from "+this.repId+" for "+tiersExpected+":"+subtiersExpected );
                if ( tiersReceived.size() < ((tiersExpected+1)*subtiersExpected+1) ) {
                    log.debug("Creating new tier hash because "+tiersReceived.size()+" < "+((tiersExpected+1)*subtiersExpected+1));
                    // we've never seen tier messages from this meter during
                    // this request, so we have to set up the tier hash
                    // we will use this hash to make sure we get each tier
                    // and subtier, and append them only once to the output
                    // ** IF tier and subtier ARE > -1, than this
                    // is a SINGLE TIER read request.
                    if ( this.tier>-1 && this.subtier>-1 ) {
                        String key = eMsg.getTier()+":"+eMsg.getSubtier();
                        if ( !tiersReceived.containsKey(key)) {
                            tiersReceived.put(key, new Boolean(false));
                        }
                    } else {
                        for (int i=0; i<=tiersExpected; i++ ) {
                            for (int j=0; j<subtiersExpected; j++ ) {
                                String key = i+":"+j;
                                if ( !tiersReceived.containsKey(key)) {
                                    tiersReceived.put(key, new Boolean(false));
                                }
                                log.debug("Tier status "+i+":"+j+" is "+tiersReceived.get(key) );
                            }
                        }
                        // we'll always get a tier7 message, so tack it on now
                        if ( !tiersReceived.containsKey("7:0")) {
                            tiersReceived.put(new String("7:0"), new Boolean(false));
                        }
                    }
                    // so, we have a hash keyed on tier:subtier
                    // indicating whether or not we have each subtier
                    // we also need to set up the ExpectedResponses
                    // counter to help us count down the appropriate
                    // number of messages.
                    OneResponse tierResponse = new OneResponse(tiersReceived.size(), true);
                    expectedResponses.set("TIER", tierResponse );
                }
                // since we're here, we know we have our 
                // hash of received Tiers set up correctly, and we can
                // store this message in the response vector if we've
                // never seen it before.
                String tierKey = eMsg.getTier()+":"+eMsg.getSubtier();
                if ( (Boolean)tiersReceived.get(tierKey).booleanValue() == false ) {
                    tiersReceived.put(tierKey,new Boolean(true));
                    responses.add(eMsg);
                }
                // so, we have a hash keyed on tier:subtier
                // indicating whether or not we have each subtier
                // we also need to set up the ExpectedResponses
                // counter to help us count down the appropriate
                // number of messages.  For every tier message we
                // receive, we step through the theirs and make sure
                // the appropriate number of messages remaining
                // is represented in the expectedResponses table
                OneResponse tierResponse = new OneResponse(tiersReceived.size(), true);
                expectedResponses.set("TIER", tierResponse );
                Enumeration e = tiersReceived.keys();
                while ( e.hasMoreElements() ) {
                    String key = (String)e.nextElement();
                    if ( tiersReceived.get(key).booleanValue() ) {
                        expectedResponses.messageReceived("TIER");
                    }
                }
            }
            if ( msg instanceof AppMessageTypeETier7 ) {
                // it looks like it's possible to get a tier 7 message
                // before I get any other messages.  To combat this, I'll
                // create a 'phantom' tier 0:0 message so we don't return
                // immediately
                if ( !tiersReceived.containsKey("0:0")) {
                    tiersReceived.put("0:0", new Boolean(false));
                }
                log.info("Got a Tier 7 message from "+this.repId );
                String tierKey = "7:0";
                // if we've already gotten the tier 7 message, don't store it again
                // the front end doesn't like dupes
                if ( (!tiersReceived.containsKey(tierKey))) {
                    tiersReceived.put(tierKey, new Boolean(true));
                    OneResponse tierResponse = new OneResponse(tiersReceived.size(), true);
                    expectedResponses.set("TIER", tierResponse );
                    expectedResponses.messageReceived("TIER");
                    responses.add(msg);
                }
                if ((Boolean)tiersReceived.get(tierKey).booleanValue() == false ) {
                    expectedResponses.messageReceived("TIER");
                    tiersReceived.put(tierKey, new Boolean(true));
                    responses.add(msg);
                }
            }
        }
    }

    public boolean isComplete() {
        return expectedResponses.isComplete();
    }

    public Vector<DataMessage> getResponses() {
        return responses;
    }

    public int getNumRoutes() {
    	return engineParams.getRoutesToTry();
    	//return this.numRoutes;
    }

    public int getNumIterations() {
    	return engineParams.getTriesPerRoute();
        //return this.numIterations;
    }

    public int getDeltaRoutes() {
    	return engineParams.getWaitBetweenRouteSeconds()*1000;
        //return this.deltaRoutes;
    }

    public void setDeltaRoutes(int i) {
    	engineParams.setWaitBetweenRouteSeconds(i/1000);
        //this.deltaRoutes=i;
    }

    public int getDeltaIterations() {
    	return engineParams.getWaitBetweenIterationSeconds()*1000;
        //return this.deltaIterations;
    }

    public void setDeltaIterations(int i) {
    	engineParams.setWaitBetweenIterationSeconds(i/1000);
        //this.deltaIterations=i;
    }

    public int getTransmissionCount() {
        return this.transmissionCount;
    }

    public void setTransmissionCount(int i) {
        this.transmissionCount=i;
    }

    public void incrementTransmissionCount() {
        this.transmissionCount++;
    }

    public boolean isExhausted() {
        if ( transmissionCount >= engineParams.getRoutesToTry() * engineParams.getTriesPerRoute() ) {
            return true;
        }
        return false;
    }

    public int getCurrentRoute() {
        return this.currentRoute;
    }

    public int getCurrentIteration() {
        return this.currentIteration;
    }

    public int getSleepTime() {
        // if we're looking for TIER messages, the sleep time can be calculated
        // because we know that a full-tier ping takes 3*[ (1+NT)*ST+1 seconds,
        // where NT is number of tiers, and ST is number of subtiers
        // A new tier is transmitted every 3 seconds.
        if ( this.tiersExpected > 0 && !multiTier ) {
            int t = 3 * ( ( 1+this.tiersExpected) * this.subtiersExpected + 1 );
            int waitTime = (t - getReceivedTierCount()*3) * 1000;
            if ( waitTime < 0 ) {
                waitTime = engineParams.getWaitBetweenRouteSeconds()*1000;
            }
            return waitTime;
        }
        if ( transmissionCount == engineParams.getRoutesToTry() * engineParams.getTriesPerRoute() ) {
            return engineParams.getWaitBetweenIterationSeconds()*1000;
        } else {
            return engineParams.getWaitBetweenRouteSeconds()*1000;
        }
    }

    public void setPostTransmitWait(int i) {
    	engineParams.setFinalWaitSeconds(i/1000);
        //this.postTransmitWait=i;
    }

    public int getPostTransmitWait() {
        return engineParams.getFinalWaitSeconds()*1000;
    }

    public void setExpireTime(Date d) {
        this.expireTime=d;
    }

    public Date getExpireTime() {
        return this.expireTime;
    }

    public void incrementTransmissionFailureCount() {
        this.transmissionFailureCount++;
    }

    public int getTransmissionFailureCount() {
        return this.transmissionFailureCount;
    }

    public void setMultiTier(boolean b) {
        log.debug("Setting sent message for "+repId+" to be multitier "+b );
        this.multiTier=b;
    }

    public boolean isMultiTier() {
        return this.multiTier;
    }

    public String getLastTxKey() {
        return lastTxKey;
    }

    public void setLastTxKey(String lastTxKey) {
        this.lastTxKey = lastTxKey;
    }
}

