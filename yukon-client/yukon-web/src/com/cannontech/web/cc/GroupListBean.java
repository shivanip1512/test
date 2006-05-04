package com.cannontech.web.cc;

import java.util.List;

import com.cannontech.cc.model.Group;
import com.cannontech.cc.service.GroupService;
import com.cannontech.database.data.lite.LiteYukonUser;

public class GroupListBean {
    private LiteYukonUser yukonUser;
    private GroupService groupService;
    
    public GroupListBean() {
    }
    
    public List<Group> getGroupList() {
        return groupService.getAllGroups(getYukonUser());
    }
    
    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }
    
    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }

    public GroupService getGroupService() {
        return groupService;
    }

    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    public String createNewProgram() {
        return "create";
    }
}
