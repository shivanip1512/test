package com.cannontech.web.cc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import com.cannontech.cc.model.AvailableProgramGroup;
import com.cannontech.cc.model.Group;
import com.cannontech.cc.model.Program;
import com.cannontech.cc.model.ProgramParameter;
import com.cannontech.cc.model.ProgramType;
import com.cannontech.cc.service.CICurtailmentStrategy;
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.core.dao.NotificationGroupDao;
import com.cannontech.database.data.lite.LiteComparators;
import com.cannontech.database.data.lite.LiteNotificationGroup;
import com.cannontech.database.data.lite.LiteYukonUser;

public class ProgramDetailBean {
    private LiteYukonUser yukonUser;
    private ProgramService programService;
    private CommercialCurtailmentBean commercialCurtailmentBean;
    private Program program = null;
    private DataModel assignedGroupModel = new ListDataModel();
    private DataModel unassignedGroupModel = new ListDataModel();
    private StrategyFactory strategyFactory;
    private List<ProgramParameter> programParameters;
    private List<Group> assignedGroups;
    private ArrayList<Group> unassignedGroups;
    private List<LiteNotificationGroup> assignedNotificationGroups;
    private List<LiteNotificationGroup> unassignedNotificationGroups;
    private LiteNotificationGroup selectedNotificationGroup;
    private boolean programDeletable;
    private NotificationGroupDao notificationGroupDao;
    
    public StrategyFactory getStrategyFactory() {
        return strategyFactory;
    }

    public void setStrategyFactory(StrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public Program getProgram() {
        return program;
    }
    
    public String editProgram() {
        ExternalContext externalContext = 
            FacesContext.getCurrentInstance().getExternalContext();
        String programIdStr = 
            (String) externalContext.getRequestParameterMap().get("programId");
        int programId = Integer.parseInt(programIdStr);
        program = programService.getProgramById(programId);
        updateData();
        
        return "programDetail";
    }
    
    public String createNewProgram() {
        program = new Program();
        
        programParameters = Collections.emptyList();
        assignedGroups = Collections.emptyList();
        assignedNotificationGroups = Collections.emptyList();
        
        program.setIdentifierPrefix("EVENT-");
        program.setLastIdentifier(0);
        
        return "create";
    }
    

    protected void updateData() {
        List<AvailableProgramGroup> availableProgramGroups = 
            programService.getAvailableProgramGroups(getProgram());
        assignedGroups = new ArrayList<Group>(availableProgramGroups.size());
        for (AvailableProgramGroup apg : availableProgramGroups) {
            assignedGroups.add(apg.getGroup());
        }
        assignedGroupModel.setWrappedData(assignedGroups);

    
        Set<Group> allGroups = programService.getUnassignedGroups(getProgram());
        unassignedGroups = new ArrayList<Group>(allGroups);
        unassignedGroupModel.setWrappedData(unassignedGroups);
        
        assignedNotificationGroups = 
            new ArrayList<LiteNotificationGroup>(programService.getAssignedNotificationGroups(getProgram()));
        Collections.sort(assignedNotificationGroups, LiteComparators.liteNameComparator);

        Set<LiteNotificationGroup> allNotificationGroups = notificationGroupDao.getAllNotificationGroups();
        allNotificationGroups.removeAll(assignedNotificationGroups);
        unassignedNotificationGroups = new ArrayList<LiteNotificationGroup>(allNotificationGroups);
        Collections.sort(unassignedNotificationGroups, LiteComparators.liteNameComparator);

        CICurtailmentStrategy strategy = getStrategyFactory().getStrategy(getProgram());
        programParameters = strategy.getParameters(getProgram());
        
        programDeletable = !programService.isEventsExistForProgram(getProgram());
    }
    
    public Boolean getProgramDeletable() {
        return programDeletable;
    }
    
    public void setProgram(Program program) {
        this.program = program;
    }
    
    public ProgramService getProgramService() {
        return programService;
    }

    public void setProgramService(ProgramService programService) {
        this.programService = programService;
    }

    public LiteYukonUser getYukonUser() {
        return yukonUser;
    }

    public void setYukonUser(LiteYukonUser yukonUser) {
        this.yukonUser = yukonUser;
    }

    public List<SelectItem> getAvailableTypes() {
        List<ProgramType> programTypes = programService.getProgramTypeList(getYukonUser());
        List<SelectItem> selectItems = new ArrayList<SelectItem>(programTypes.size());
        for (ProgramType type : programTypes) {
            selectItems.add(new SelectItem(type, type.getName()));
        }
        return selectItems;
    }
    
    public void doSave() {
        Set<LiteNotificationGroup> assignedNotifGroupsSet = new HashSet<LiteNotificationGroup>(assignedNotificationGroups);
        programService.saveProgram(program, programParameters, assignedGroups, assignedNotifGroupsSet);
    }
    
    public String save() {
        doSave();
        return "programList";
    }
    
    public String saveNew() {
        doSave();
        updateData();
        return "programDetail";
    }
    
    public String apply() {
        doSave();
        return null;
    }
    
    public String cancel() {
        return "programList";
    }
    
    public void deleteGroup(ActionEvent event) {
        Group toDelete = (Group) assignedGroupModel.getRowData();
        assignedGroups.remove(toDelete);
        unassignedGroups.add(toDelete);
    }
    
    public void addGroup(ActionEvent event) {
        Group toAdd = (Group) unassignedGroupModel.getRowData();
        unassignedGroups.remove(toAdd);
        assignedGroups.add(toAdd);
    }

    public void deleteNotificationGroup(ActionEvent event) {
        assignedNotificationGroups.remove(selectedNotificationGroup);
        unassignedNotificationGroups.add(selectedNotificationGroup);
    }
    
    public void addNotificationGroup(ActionEvent event) {
        unassignedNotificationGroups.remove(selectedNotificationGroup);
        assignedNotificationGroups.add(selectedNotificationGroup);
    }
    
    public String delete() {
        programService.deleteProgram(program);
        return "programList";
    }

    public DataModel getAssignedGroupModel() {
        return assignedGroupModel;
    }

    public void setAssignedGroupModel(DataModel availableGroupModel) {
        this.assignedGroupModel = availableGroupModel;
    }

    public DataModel getunassignedGroupModel() {
        return unassignedGroupModel;
    }

    public void setUnassignedGroupModel(DataModel unassignedGroupModel) {
        this.unassignedGroupModel = unassignedGroupModel;
    }

    public List<ProgramParameter> getProgramParameters() {
        return programParameters;
    }

    public void setProgramParameters(List<ProgramParameter> programParameters) {
        this.programParameters = programParameters;
    }

    public CommercialCurtailmentBean getCommercialCurtailmentBean() {
        return commercialCurtailmentBean;
    }

    public void setCommercialCurtailmentBean(CommercialCurtailmentBean commercialCurtailmentBean) {
        this.commercialCurtailmentBean = commercialCurtailmentBean;
    }

    public List<LiteNotificationGroup> getAssignedNotificationGroups() {
        return assignedNotificationGroups;
    }

    public void setAssignedNotificationGroups(List<LiteNotificationGroup> assignedNotificationGroups) {
        this.assignedNotificationGroups = assignedNotificationGroups;
    }

    public List<LiteNotificationGroup> getUnassignedNotificationGroups() {
        return unassignedNotificationGroups;
    }

    public void setUnassignedNotificationGroups(List<LiteNotificationGroup> unassignedNotificationGroups) {
        this.unassignedNotificationGroups = unassignedNotificationGroups;
    }

    public NotificationGroupDao getNotificationGroupDao() {
        return notificationGroupDao;
    }

    public void setNotificationGroupDao(NotificationGroupDao notificationGroupDao) {
        this.notificationGroupDao = notificationGroupDao;
    }

    public LiteNotificationGroup getSelectedNotificationGroup() {
        return selectedNotificationGroup;
    }

    public void setSelectedNotificationGroup(LiteNotificationGroup selectedNotificationGroup) {
        this.selectedNotificationGroup = selectedNotificationGroup;
    }

}
