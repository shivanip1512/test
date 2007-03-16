package com.cannontech.core.service;

import java.util.Collection;
import java.util.Date;

import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.message.porter.message.Request;

public interface LongLoadProfileService {
    public void initiateLongLoadProfile(LiteYukonPAObject device, int channel, Date start, Date stop, String emailAddress);
    public void initiateLongLoadProfile(LiteYukonPAObject device, int channel, Date start, Date stop, Runnable runner);
    public Collection<ProfileRequestInfo> getPendingLongLoadProfileRequests(LiteYukonPAObject device);
    
    public class ProfileRequestInfo {
        public Runnable runner;
        public Date from;
        public Date to;
        public Request request;
    }
}
