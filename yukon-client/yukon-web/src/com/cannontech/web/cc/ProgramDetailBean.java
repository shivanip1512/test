package com.cannontech.web.cc;

import java.util.ArrayList;
import java.util.Collections;
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
import com.cannontech.cc.service.ProgramService;
import com.cannontech.cc.service.StrategyBase;
import com.cannontech.cc.service.StrategyFactory;
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
    private boolean programDeletable;
    
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
        program = programService.getProgram(programId);
        updateData();
        
        return "programDetail";
    }
    
    public String createNewProgram() {
        program = new Program();
        
        programParameters = Collections.emptyList();
        assignedGroups = Collections.emptyList();
        
        
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

        StrategyBase strategy = getStrategyFactory().getStrategy(getProgram());
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
        programService.saveProgram(program, programParameters, assignedGroups);
    }
    
    public String save() {
        doSave();
        return "programList";
    }
    
    public String saveNew() {
        doSave();
        updateData();
        return "programList";
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

}
