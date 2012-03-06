package com.cannontech.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;
import com.cannontech.user.YukonUserContext;

public interface LoadProfileService {
    public void initiateLoadProfile(LiteYukonPAObject device, 
                                        int channel, 
                                        Date start, 
                                        Date stop, 
                                        CompletionCallback callback,
                                        YukonUserContext userContext);
    public Collection<ProfileRequestInfo> getPendingLoadProfileRequests(LiteYukonPAObject device);
    
    public boolean removePendingLoadProfileRequest(LiteYukonPAObject device, long requestId, YukonUserContext userContext);
    
    public void printSizeOfCollections(int deviceId);
    
    public Double calculatePercentDone(Long requestId); 
    
    public String getLastReturnMsg(long requestId); 
    
    public class ProfileRequestInfo {
        public CompletionCallback callback;
        public Date from;
        public Date to;
        public Request request;
        public long requestId;
        public int channel;
        public String userName;
        public Double percentDone;
       
    }
    
     public static interface CompletionCallback{
        public void onSuccess(String successInfo);
        public void onFailure(int returnStatus, String resultString);
        public void onCancel(LiteYukonUser cancelUser);
    }
     
    public List<Map<String, String>> getPendingRequests(LiteYukonPAObject device,  YukonUserContext userContext);
}
