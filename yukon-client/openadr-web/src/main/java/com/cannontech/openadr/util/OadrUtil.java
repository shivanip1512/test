package com.cannontech.openadr.util;

import org.oasis_open.docs.ns.energyinterop._201110.EiResponse;
import org.oasis_open.docs.ns.energyinterop._201110.EventResponses;
import org.oasis_open.docs.ns.energyinterop._201110.EventResponses.EventResponse;
import org.oasis_open.docs.ns.energyinterop._201110.OptTypeType;
import org.oasis_open.docs.ns.energyinterop._201110.QualifiedEventIDType;
import org.oasis_open.docs.ns.energyinterop._201110.payloads.EiCreatedEvent;
import org.openadr.oadr_2_0a._2012._07.OadrCreatedEvent;

import com.cannontech.openadr.OadrResponseCode;

public class OadrUtil {
    public static EventResponse createEventResponse(String eventId, String responseCode, long modifNum, String requestId) {
        EventResponse eventResponse = new EventResponse();
        
        // Opt Type
        {
            eventResponse.setOptType(OptTypeType.OPT_OUT);
        }
        
        // Response Code
        {
            eventResponse.setResponseCode(responseCode);
        }
        
        // Qualified Event Id
        {
            QualifiedEventIDType qualifiedId = new QualifiedEventIDType();
            qualifiedId.setEventID(eventId);
            qualifiedId.setModificationNumber(modifNum);
            eventResponse.setQualifiedEventID(qualifiedId);
        }
        
        // Request Id
        {
            eventResponse.setRequestID(requestId);
        }
        
        return eventResponse;
    }
    
    public static OadrCreatedEvent createOadrCreatedEvent(String requestId, EventResponses eventResponses,
                                                          String venId) {
        OadrCreatedEvent createdEvent = new OadrCreatedEvent();
        EiCreatedEvent eiCreatedEvent = new EiCreatedEvent();
        
        // VenID
        {
            eiCreatedEvent.setVenID(venId);
        }

        // EiResponse
        {
            EiResponse eiResponse = new EiResponse();
            
            eiResponse.setRequestID(requestId);
            eiResponse.setResponseCode(OadrResponseCode.SUCCESS.codeString());
            
            eiCreatedEvent.setEiResponse(eiResponse);
        }
        
        // EiEventResponses
        {
            eiCreatedEvent.setEventResponses(eventResponses);
        }
        
        createdEvent.setEiCreatedEvent(eiCreatedEvent);
        
        return createdEvent;
    }
    
    public static OadrCreatedEvent createOadrCreatedEvent(String requestId, String venId, OadrResponseCode responseCode) {
        OadrCreatedEvent createdEvent = new OadrCreatedEvent();
        EiCreatedEvent eiCreatedEvent = new EiCreatedEvent();
        
        // VenID
        {
            eiCreatedEvent.setVenID(venId);
        }

        // EiResponse
        {
            EiResponse eiResponse = new EiResponse();
            
            eiResponse.setRequestID(requestId);
            eiResponse.setResponseCode(responseCode.codeString());
            
            eiCreatedEvent.setEiResponse(eiResponse);
        }
        
        createdEvent.setEiCreatedEvent(eiCreatedEvent);
        
        return createdEvent;
    }
}
