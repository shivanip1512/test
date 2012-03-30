package com.cannontech.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.porter.message.Request;
import com.cannontech.user.YukonUserContext;

public interface LoadProfileService {
    /**
     * Initiate a profile collection request.  This service will return a "request id" which can be
     * used to cancel the request later if needed.
     */
    public long initiateLoadProfile(LiteYukonPAObject device, int channel, Date start, Date stop, 
                                    CompletionCallback callback, YukonUserContext userContext);

    /**
     * Get a list of load profile requests in process.
     */
    public Collection<ProfileRequestInfo> getPendingLoadProfileRequests(YukonPao device);

    /**
     * Cancel the given load profile request.  The requestId is the value returned from
     * {@link #initiateLoadProfile(LiteYukonPAObject, int, Date, Date, CompletionCallback, YukonUserContext)}.
     */
    public boolean removePendingLoadProfileRequest(YukonPao device, long requestId,
                                                   YukonUserContext userContext);

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
