package com.cannontech.core.service;

import java.util.Collection;
import java.util.Date;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;

public interface LongLoadProfileService {
    public void initiateLongLoadProfile(LiteYukonPAObject device, 
                                        int channel, 
                                        Date start, 
                                        Date stop, 
                                        CompletionCallback runner);
    public Collection<ProfileRequestInfo> getPendingLongLoadProfileRequests(LiteYukonPAObject device);
    
    public boolean removePendingLongLoadProfileRequest(LiteYukonPAObject device, long requestId, LiteYukonUser user);
    
    public void printSizeOfCollections(int deviceId);
    
    public class ProfileRequestInfo {
        public CompletionCallback runner;
        public Date from;
        public Date to;
        public Request request;
        public long requestId;
        public int channel;
       
    }
    
     public static interface CompletionCallback{
        public void onSuccess(String successInfo);
        public void onFailure(int returnStatus, String resultString);
        public void onCancel(LiteYukonUser cancelUser);
    }
}
