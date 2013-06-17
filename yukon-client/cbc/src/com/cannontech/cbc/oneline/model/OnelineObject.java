package com.cannontech.cbc.oneline.model;

import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.message.capcontrol.streamable.SubBus;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.user.YukonUserContext;
import com.loox.jloox.LxLine;

public abstract class OnelineObject {
    protected YukonUserContext userContext;
    protected Integer paoId;
    protected SubBus subBus;
    protected OneLineDrawing drawing;
	private boolean editFlag = false;
	private boolean commandsFlag = false;

    public abstract LxLine getRefLnBelow();
    
    public abstract LxLine getRefLnAbove();
    
    public abstract void draw();
    
    public abstract void addDrawing(OneLineDrawing d);

    public final OneLineDrawing getDrawing() {
        return drawing;
    }
    
    public final SubBus getSubBus() {
        return subBus;
    }
    
    public final Integer getPaoId() {
        return paoId;
    }
    
    public final void setPaoId(Integer paoId) {
        this.paoId = paoId;
    }

    public void setYukonUserContext(final YukonUserContext userContext) {
        this.userContext = userContext;
        commandsFlag = false;
        RolePropertyDao rolePropertyDao = YukonSpringHook.getBean("rolePropertyDao", RolePropertyDao.class);
        editFlag = rolePropertyDao.checkProperty(YukonRoleProperty.CBC_DATABASE_EDIT, userContext.getYukonUser());
    }
    
    public final YukonUserContext getYukonUserContext() {
        return userContext;
    }

	public boolean isEditFlag() {
		return editFlag;
	}

	public boolean isCommandsFlag() {
		return commandsFlag;
	}

}