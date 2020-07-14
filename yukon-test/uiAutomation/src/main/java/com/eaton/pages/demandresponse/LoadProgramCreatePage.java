package com.eaton.pages.demandresponse;

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

public class LoadProgramCreatePage extends PageBase {

    private static final String PARENT_NAME = "gear-create-popup-LM_DIRECT_PROGRAM";

    public LoadProgramCreatePage(DriverExtensions driverExt) {
        super(driverExt);
        
        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_PROGRAM_CREATE;
    }

    // General
    public TextEditElement getName() {
        return new TextEditElement(this.driverExt, "name");
    }

    public DropDownElement getType() {
        return new DropDownElement(this.driverExt, "type");
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

        return new CreateDirectPrgmGearModal(this.driverExt, Optional.empty(), Optional.of(PARENT_NAME));
    }

    public CreateEcobeePrgmGearModal showCreateEcobeePrgmGearModal() {
        getGearsCreateBtn().click();

        return new CreateEcobeePrgmGearModal(this.driverExt, Optional.empty(), Optional.of(PARENT_NAME));
    }

    public CreateHoneywellPrgmGearModal showCreateHoneywellPrgmGearModal() {
        getGearsCreateBtn().click();

        return new CreateHoneywellPrgmGearModal(this.driverExt, Optional.empty(), Optional.of(PARENT_NAME));
    }

    public CreateItronPrgmGearModal showCreateItronPrgmGearModal() {
        getGearsCreateBtn().click();

        return new CreateItronPrgmGearModal(this.driverExt, Optional.empty(), Optional.of(PARENT_NAME));
    }

    public CreateMeterDisconnectPrgmModal showCreateMeterDiconnectPrgmModal() {
        getGearsCreateBtn().click();

        return new CreateMeterDisconnectPrgmModal(this.driverExt, Optional.empty(), Optional.of(PARENT_NAME));
    }

    public CreateSepPrgmGearModal showCreateSepPrgmGearModal() {
        getGearsCreateBtn().click();

        return new CreateSepPrgmGearModal(this.driverExt, Optional.empty(), Optional.of(PARENT_NAME));
    }

    public LoadGroupsTab getLoadGroupTab() {
        return new LoadGroupsTab(this.driverExt);
    }
}
