package com.cannontech.cbc.oneline.model;

import com.cannontech.cbc.oneline.view.OneLineDrawing;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.capcontrol.CBCSettingsRole;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxLine;

public abstract class OnelineObject {
    protected LiteYukonUser user;
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

    public void setUser(final LiteYukonUser user) {
        this.user = user;
        commandsFlag = false;
        editFlag = DaoFactory.getAuthDao().checkRoleProperty(user,CBCSettingsRole.CBC_DATABASE_EDIT);
    }
    
    public final LiteYukonUser getUser() {
        return user;
    }

	public boolean isEditFlag() {
		return editFlag;
	}

	public boolean isCommandsFlag() {
		return commandsFlag;
	}

}
