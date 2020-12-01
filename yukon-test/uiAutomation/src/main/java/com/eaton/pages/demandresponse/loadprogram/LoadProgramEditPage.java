package com.eaton.pages.demandresponse.loadprogram;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.elements.modals.gears.CreateDirectPrgmGearModal;
import com.eaton.elements.modals.gears.CreateEcobeePrgmGearModal;
import com.eaton.elements.modals.gears.CreateHoneywellPrgmGearModal;
import com.eaton.elements.modals.gears.CreateItronPrgmGearModal;
import com.eaton.elements.modals.gears.CreateMeterDisconnectPrgmModal;
import com.eaton.elements.modals.gears.CreateSepPrgmGearModal;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class LoadProgramEditPage extends PageBase {

    private TextEditElement name;
    private static final String PARENT_NAME = "gear-create-popup-LM_DIRECT_PROGRAM";

    public LoadProgramEditPage(DriverExtensions driverExt, int id) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_PROGRAM_EDIT + id + Urls.EDIT;

        name = new TextEditElement(this.driverExt, "name");
    }

    // General
    public TextEditElement getName() {
        return name;
    }

    public DropDownElement getOperationalState() {
        return new DropDownElement(this.driverExt, "operationalState");
    }

    public DropDownElement getConstraint() {
        return new DropDownElement(this.driverExt, "constraint.constraintId");
    }

    // Trigger Threshold Settings
    public TextEditElement getTriggerOffset() {
        return new TextEditElement(this.driverExt, "triggerOffset");
    }

    public TextEditElement getRestoreOffset() {
        return new TextEditElement(this.driverExt, "restoreOffset");
    }
    
    //Gears
    //TODO add gears section to be able to remove a gear

    // Control Window
    public TrueFalseCheckboxElement getUseWindowOne() {
        return new TrueFalseCheckboxElement(this.driverExt, "controlWindowOne");
    }

    public TrueFalseCheckboxElement getUseWindowTwo() {
        return new TrueFalseCheckboxElement(this.driverExt, "controlWindowTwo");
    }

    public Button getSaveBtn() {
        return new Button(this.driverExt, "Save");
    }

    public Button getCancelBtn() {
        return new Button(this.driverExt, "Cancel");
    }

    public Button getGearsCreateBtn() {
        return new Button(this.driverExt, "Create");
    }

    public CreateDirectPrgmGearModal showCreateDirectPrgmGearsModal() {
        getGearsCreateBtn().click();

        return new CreateDirectPrgmGearModal(this.driverExt, Optional.of(PARENT_NAME), Optional.empty());
    }

    public CreateEcobeePrgmGearModal showCreateEcobeePrgmGearModal() {
        getGearsCreateBtn().click();

        return new CreateEcobeePrgmGearModal(this.driverExt, Optional.of(PARENT_NAME), Optional.empty());
    }

    public CreateHoneywellPrgmGearModal showCreateHoneywellPrgmGearModal() {
        getGearsCreateBtn().click();

        return new CreateHoneywellPrgmGearModal(this.driverExt, Optional.of(PARENT_NAME), Optional.empty());
    }

    public CreateItronPrgmGearModal showCreateItronPrgmGearModal() {
        getGearsCreateBtn().click();

        return new CreateItronPrgmGearModal(this.driverExt, Optional.of(PARENT_NAME), Optional.empty());
    }

    public CreateMeterDisconnectPrgmModal showCreateMeterDiconnectPrgmModal() {
        getGearsCreateBtn().click();

        return new CreateMeterDisconnectPrgmModal(this.driverExt, Optional.of(PARENT_NAME), Optional.empty());
    }

    public CreateSepPrgmGearModal showCreateSepPrgmGearModal() {
        getGearsCreateBtn().click();

        return new CreateSepPrgmGearModal(this.driverExt, Optional.of(PARENT_NAME), Optional.empty());
    }

    public LoadGroupsTab getLoadGroupTab() {
        return new LoadGroupsTab(this.driverExt);
    }
}
