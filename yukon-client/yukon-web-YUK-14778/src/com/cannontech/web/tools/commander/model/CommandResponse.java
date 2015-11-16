package com.cannontech.web.tools.commander.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.clientutils.commander.MessageType;
import com.cannontech.message.dispatch.message.PointData;
import com.cannontech.message.porter.message.Return;
import com.cannontech.message.util.Message;

public final class CommandResponse {
    
    private final int id;
    private final long timestamp;
    private final MessageType type;
    private final int status;  // error-code.xml code number
    private final List<String> results;
    private final int expectMore;
    
    private CommandResponse(int id, long timestamp, MessageType type, int status, List<String> results, int expectMore) {
        this.id = id;
        this.timestamp = timestamp;
        this.type = type;
        this.status = status;
        this.results = results;
        this.expectMore = expectMore;
    }
    
    public int getId() {
        return id;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public MessageType getType() {
        return type;
    }
    
    public int getStatus() {
        return status;
    }
    
    public List<String> getResults() {
        return results;
    }
    
    public int getExpectMore() {
        return expectMore;
    }
    
    public static CommandResponse of(int id, Return rtn) {
        
        MessageType type = MessageType.getMessageType(rtn.getStatus());
        List<String> messages = new ArrayList<>();
        // Add any point data
        for (Message m : rtn.getMessages()) {
            
            if (m instanceof PointData) {
                
                PointData pd = (PointData) m;
                if (StringUtils.isNotBlank(pd.getStr())) {
                    messages.add(pd.getStr());
                }
            }
        }
        
        if (StringUtils.isNotBlank(rtn.getResultString())) {
            String[] lines = rtn.getResultString().split("\n");
            for(String line : lines) messages.add(line);
        }
        
        CommandResponse resp = new CommandResponse(id, rtn.getTimeStamp().getTime(), type, rtn.getStatus(), messages, 
                rtn.getExpectMore());
        
        return resp;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + expectMore;
        result = prime * result + id;
        result = prime * result + ((results == null) ? 0 : results.hashCode());
        result = prime * result + status;
        result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
        result = prime * result + ((type == null) ? 0 : type.hashCode());
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
        CommandResponse other = (CommandResponse) obj;
        if (expectMore != other.expectMore)
            return false;
        if (id != other.id)
            return false;
        if (results == null) {
            if (other.results != null)
                return false;
        } else if (!results.equals(other.results))
            return false;
        if (status != other.status)
            return false;
        if (timestamp != other.timestamp)
            return false;
        if (type != other.type)
            return false;
        return true;
    }
    
    @Override
    public String toString() {
        return String.format("CommandResponse [id=%s, timestamp=%s, type=%s, status=%s, results=%s, expectMore=%s]", 
                id, timestamp, type, status, results, expectMore);
    }
    
}