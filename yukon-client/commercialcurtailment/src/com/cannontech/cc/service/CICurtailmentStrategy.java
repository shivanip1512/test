package com.cannontech.cc.service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

import com.cannontech.cc.model.BaseEvent;
import com.cannontech.cc.model.CICustomerStub;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.service.builder.EventBuilderBase;
import com.cannontech.cc.service.builder.VerifiedNotifCustomer;
import com.cannontech.common.exception.PointException;

public interface CICurtailmentStrategy {

    public void setGroupService(GroupService groupService);

    public String getMethodKey();

    /** Meant to be used to determine if an event is NOT cancelled or
     *  NOT an accounting-only event or NOT a suppressed event.
     * @return
     */
    public boolean isConsideredActive(BaseEvent event);

    public List<VerifiedNotifCustomer> getVerifiedCustomerList(EventBuilderBase builder,
                                                               Collection<Group> selectedGroups);

    public List<ProgramParameter> getParameters(Program program);

    public List<? extends BaseEvent> getEventsForProgram(Program program);

    public BigDecimal getCurrentLoad(CICustomerStub customer) throws PointException;

}