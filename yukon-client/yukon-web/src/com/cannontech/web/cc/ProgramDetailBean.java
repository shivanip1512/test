package com.cannontech.web.cc;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
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
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyBase;
import com.cannontech.cc.service.StrategyFactory;
import com.cannontech.database.data.lite.LiteYukonUser;

public class ProgramDetailBean {
    private LiteYukonUser yukonUser;
    private ProgramService programService;
    private Program program = null;
    private DataModel assignedGroupModel = new ListDataModel();
    private DataModel unassignedGroupModel = new ListDataModel();
    private StrategyFactory strategyFactory;
    private List<ProgramParameter> programParameters;
    private List<Group> assignedGroups;
    private ArrayList<Group> unassignedGroups;
    
    public StrategyFactory getStrategyFactory() {
        return strategyFactory;
    }

    public void setStrategyFactory(StrategyFactory strategyFactory) {
        this.strategyFactory = strategyFactory;
    }

    public Program getProgram() {
        return program;
    }
    
    public String editEvent() {
        ExternalContext externalContext = 
            FacesContext.getCurrentInstance().getExternalContext();
        String programIdStr = 
            (String) externalContext.getRequestParameterMap().get("programId");
        int programId = Integer.parseInt(programIdStr);
        program = programService.getProgram(programId);
        updateGroupData();
        
        return "programDetail";
    }

    protected void updateGroupData() {
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
        programService.saveProgram(program, programParameters, assignedGroups);
    }
    
    public String save() {
        doSave();
        return "success";
    }
    
    public String apply() {
        doSave();
        return null;
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

    public String delete() {
        programService.deleteProgram(program);
        return "success";
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
        StrategyBase strategy = getStrategyFactory().getStrategy(getProgram());
        programParameters = strategy.getParameters(getProgram());
        return programParameters;
    }

    public void setProgramParameters(List<ProgramParameter> programParameters) {
        this.programParameters = programParameters;
    }

    public Properties getLabels() {
        StrategyBase strategy = getStrategyFactory().getStrategy(getProgram());
        return strategy.getViewLabels();
    }

}
