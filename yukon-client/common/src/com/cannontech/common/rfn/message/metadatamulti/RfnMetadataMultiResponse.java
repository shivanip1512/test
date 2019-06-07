package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.Map;

import com.cannontech.common.rfn.message.RfnIdentifier;

/**
 * Response to RfnMetadataMultiRequest.
 * 
 * JMS Queue name: 
 *     the temporary(i.e., reply-To) queue of the RfnMetadataMultiRequest.
 */
public class RfnMetadataMultiResponse implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private final String requestID; // to correlate response to request
    
    // the following two fields support response truncation
    private final int totalSegments;
    
    private final int segmentNumber;
    
    // If responseType is not OK, suggest to ignore the whole response.
    private RfnMetadataMultiResponseType responseType;

    // The following response message is for the whole response.
    // NM will do its best to provide more info, especially in case of not OK, besides
    //     being indicated by the responseType's constant name (e.x., NM_DB_ERROR).
    private String responseMessage;
    
    // You will get a RfnMetadataMultiQueryResult object for each device
    //     you specified in the request.
    // Note: some result may be an error type.
    private Map<RfnIdentifier, RfnMetadataMultiQueryResult> queryResults;

    public RfnMetadataMultiResponse(String requestID, int totalSegments, int segmentNumber) {
        super();
        this.requestID = requestID;
        this.totalSegments = totalSegments;
        this.segmentNumber = segmentNumber;
    }

    public RfnMetadataMultiResponseType getResponseType() {
        return responseType;
    }

    public void setResponseType(RfnMetadataMultiResponseType responseType) {
        this.responseType = responseType;
    }

    public String getResponseMessage() {
        return responseMessage;
    }

    public void setResponseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
    }

    public Map<RfnIdentifier, RfnMetadataMultiQueryResult> getQueryResults() {
        return queryResults;
    }

    public void setQueryResults(Map<RfnIdentifier, RfnMetadataMultiQueryResult> queryResults) {
        this.queryResults = queryResults;
    }

    public String getRequestID() {
        return requestID;
    }

    public int getTotalSegments() {
        return totalSegments;
    }

    public int getSegmentNumber() {
        return segmentNumber;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((queryResults == null) ? 0 : queryResults.hashCode());
        result = prime * result + ((requestID == null) ? 0 : requestID.hashCode());
        result = prime * result + ((responseMessage == null) ? 0 : responseMessage.hashCode());
        result = prime * result + ((responseType == null) ? 0 : responseType.hashCode());
        result = prime * result + segmentNumber;
        result = prime * result + totalSegments;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RfnMetadataMultiResponse other = (RfnMetadataMultiResponse) obj;
        if (queryResults == null) {
            if (other.queryResults != null)
                return false;
        } else if (!queryResults.equals(other.queryResults))
            return false;
        if (requestID == null) {
            if (other.requestID != null)
                return false;
        } else if (!requestID.equals(other.requestID))
            return false;
        if (responseMessage == null) {
            if (other.responseMessage != null)
                return false;
        } else if (!responseMessage.equals(other.responseMessage))
            return false;
        if (responseType != other.responseType)
            return false;
        if (segmentNumber != other.segmentNumber)
            return false;
        if (totalSegments != other.totalSegments)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("RfnMetadataMultiResponse [requestID=%s, totalSegments=%s, segmentNumber=%s, responseType=%s, responseMessage=%s, queryResults=%s]",
                    requestID,
                    totalSegments,
                    segmentNumber,
                    responseType,
                    responseMessage,
                    queryResults);
    }
}