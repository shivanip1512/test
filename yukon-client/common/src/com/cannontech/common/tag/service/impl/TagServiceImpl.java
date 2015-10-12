package com.cannontech.common.tag.service.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.joda.time.Duration;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.tag.service.TagService;
import com.cannontech.common.util.BootstrapUtils;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.DispatchClientConnection;
import com.cannontech.message.dispatch.message.Multi;
import com.cannontech.message.dispatch.message.PointRegistration;
import com.cannontech.message.dispatch.message.Registration;
import com.cannontech.tags.Tag;
import com.cannontech.tags.TagManager;
import com.cannontech.yukon.conns.ConnPool;
import com.google.common.collect.Lists;

public class TagServiceImpl implements TagService{

    private TagManager tagManager;
    @Autowired private ConnPool connPool;
    
    @Override
    public void createTag(int pointId, int tagId, String description, LiteYukonUser user) throws Exception {
        tagManager.createTag(pointId, tagId, user.getUsername(), description, "", "");
    }
    
    @Override
    public void removeTag(int pointId, int instanceId,  LiteYukonUser user) throws Exception {
        Tag tag = tagManager.getTag(pointId, instanceId);
        tagManager.removeTag(tag, user.getUsername());
    }
    
    @Override
    public List<Tag> getTags(int pointId) {
        return Lists.newArrayList(tagManager.getTags(pointId));
    }
    
    @Override
    public void updateTag(Tag tag, LiteYukonUser user) throws Exception{
        tagManager.updateTag(tag, user.getUsername());
    }
    
    @PostConstruct
    public void initialize() {
        DispatchClientConnection dispatchConnection = (DispatchClientConnection) connPool.getDefDispatchConn();
        Multi multi = new Multi();
        Registration reg = new Registration();
        reg.setAppName(BootstrapUtils.getApplicationName());
        reg.setAppIsUnique(0);
        reg.setAppKnownPort(0);
        reg.setAppExpirationDelay((int) Duration.standardMinutes(15).getStandardSeconds());
        reg.setPriority(15);

        PointRegistration pReg = new PointRegistration();
        pReg.setRegFlags(PointRegistration.REG_ALARMS);

        multi.getVector().addElement(reg);
        multi.getVector().addElement(pReg);

        dispatchConnection.setRegistrationMsg(multi);
        tagManager = new TagManager(dispatchConnection);
    }
}
