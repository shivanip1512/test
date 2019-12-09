package com.cannontech.common.rfn.message.metadatamulti;

import java.io.Serializable;
import java.util.Map;

public class RfnMetadataMultiQueryResult implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    // The resultType will always be OK as long as NM can find a node for the device.
    // A query is not treated as a failure if it can’t find any data for a metadataMulti (
    //     e.x., no primary gateway).
    // A query may fail due to NM DB error or service error. In that case,
    //     the whole responseType is treated as not OK.
    private RfnMetadataMultiQueryResultType resultType;

    // The following result message is per device.
    // NM will do its best to provide more info, especially in case of not OK, besides
    //     being indicated by the resultType's constant name (i.e., NM_ENTITY_NOT_FOUND).
    private String resultMessage;

    // The entry of the following Map will match the element of the request rfnMetadatas Set.
    // Sometimes you may get a null value for some entries, e.x.,
    //     when a device has no primary gateway, its PrimaryGatewayComm is null.
    private Map<RfnMetadataMulti, Object> metadatas;

    public RfnMetadataMultiQueryResultType getResultType() {
        return resultType;
    }

    public void setResultType(RfnMetadataMultiQueryResultType resultType) {
        this.resultType = resultType;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public Map<RfnMetadataMulti, Object> getMetadatas() {
        return metadatas;
    }

    public void setMetadatas(Map<RfnMetadataMulti, Object> metadatas) {
        this.metadatas = metadatas;
    }
    
    public boolean isValidResultForMulti(RfnMetadataMulti multi) {
       return this.getResultType() == RfnMetadataMultiQueryResultType.OK
                && this.getMetadatas() != null && this.getMetadatas().containsKey(multi) && this.getMetadatas().get(multi) != null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((metadatas == null) ? 0 : metadatas.hashCode());
        result = prime * result + ((resultMessage == null) ? 0 : resultMessage.hashCode());
        result = prime * result + ((resultType == null) ? 0 : resultType.hashCode());
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
        RfnMetadataMultiQueryResult other = (RfnMetadataMultiQueryResult) obj;
        if (metadatas == null) {
            if (other.metadatas != null)
                return false;
        } else if (!metadatas.equals(other.metadatas))
            return false;
        if (resultMessage == null) {
            if (other.resultMessage != null)
                return false;
        } else if (!resultMessage.equals(other.resultMessage))
            return false;
        if (resultType != other.resultType)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return String
            .format("RfnMetadataMultiQueryResult [resultType=%s, resultMessage=%s, metadatas=%s]",
                    resultType,
                    resultMessage,
                    metadatas);
    }
}