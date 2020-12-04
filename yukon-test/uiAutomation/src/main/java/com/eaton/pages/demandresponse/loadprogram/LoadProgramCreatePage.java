package com.eaton.pages.demandresponse.loadprogram;

import java.util.Optional;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.Section;
import com.eaton.elements.SelectBoxElement;
import com.eaton.elements.SwitchBtnYesNoElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TimePickerElement;
import com.eaton.elements.modals.gears.CreateDirectPrgmGearModal;
import com.eaton.elements.modals.gears.CreateEcobeePrgmGearModal;
import com.eaton.elements.modals.gears.CreateHoneywellPrgmGearModal;
import com.eaton.elements.modals.gears.CreateItronPrgmGearModal;
import com.eaton.elements.modals.gears.CreateMeterDisconnectPrgmModal;
import com.eaton.elements.modals.gears.CreateSepPrgmGearModal;
import com.eaton.elements.tabs.LoadGroupsTab;
import com.eaton.framework.DriverExtensions;
import com.eaton.framework.SeleniumTestSetup;
import com.eaton.framework.Urls;
import com.eaton.pages.PageBase;

public class LoadProgramCreatePage extends PageBase {

    private static final String DESCRIBEDBY = "gear-create-popup-LM_DIRECT_PROGRAM";
    private static final String ITRON_GEAR_MODAL_DESCRIBEDBY = "gear-create-popup-LM_ITRON_PROGRAM";

    private TimePickerElement startTimeWindowOne;
    private TimePickerElement stopTimeWindowOne;
    private TimePickerElement startTimeWindowTwo;
    private TimePickerElement stopTimeWindowTwo;

    public LoadProgramCreatePage(DriverExtensions driverExt) {
        super(driverExt);

        requiresLogin = true;
        pageUrl = Urls.DemandResponse.LOAD_PROGRAM_CREATE;
        startTimeWindowOne = new TimePickerElement(this.driverExt, "startTimeWindowOne_inputField");
        stopTimeWindowOne = new TimePickerElement(this.driverExt, "stopTimeWindowOne_inputField");
        startTimeWindowTwo = new TimePickerElement(this.driverExt, "startTimeWindowTwo_inputField");
        stopTimeWindowTwo = new TimePickerElement(this.driverExt, "stopTimeWindowTwo_inputField");
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
    public SwitchBtnYesNoElement getUseWindowOne() {
        return new SwitchBtnYesNoElement(this.driverExt, "controlWindowOne");
    }

    public TimePickerElement getStartTimeWindowOne() {
        return startTimeWindowOne;
    }

    public TimePickerElement getStopTimeWindowOne() {
        return stopTimeWindowOne;
    }

    public TimePickerElement getStartTimeWindowTwo() {
        return startTimeWindowTwo;
    }

    public TimePickerElement getStopTimeWindowTwo() {
        return stopTimeWindowTwo;
    }

    public SwitchBtnYesNoElement getUseWindowTwo() {
        return new SwitchBtnYesNoElement(this.driverExt, "controlWindowTwo");
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
    
    private void clickCreateGearAndWait(String describedBy) {
        getGearsCreateBtn().click();
        
        SeleniumTestSetup.waitUntilModalOpenByDescribedBy(describedBy); 
    }
    
    private void clickCreateGearAndWaitDisplayBlock(String describedBy) {
        getGearsCreateBtn().click();
        
        SeleniumTestSetup.waitUntilModalOpenDisplayBlock(describedBy);
    }

    public CreateDirectPrgmGearModal showCreateDirectPrgmGearsModal() {
        clickCreateGearAndWait(DESCRIBEDBY);

        return new CreateDirectPrgmGearModal(this.driverExt, Optional.empty(), Optional.of(DESCRIBEDBY));
    }

    public CreateEcobeePrgmGearModal showCreateEcobeePrgmGearModal() {
        clickCreateGearAndWait(DESCRIBEDBY);

        return new CreateEcobeePrgmGearModal(this.driverExt, Optional.empty(), Optional.of(DESCRIBEDBY));
    }

    public CreateHoneywellPrgmGearModal showCreateHoneywellPrgmGearModal() {
        clickCreateGearAndWait("gear-create-popup-LM_HONEYWELL_PROGRAM");

        return new CreateHoneywellPrgmGearModal(this.driverExt, Optional.empty(), Optional.of("gear-create-popup-LM_HONEYWELL_PROGRAM"));
    }

    public CreateItronPrgmGearModal showCreateItronPrgmGearModal(Optional<Integer> count) {
        Integer c = count.orElse(1);
        if (c.equals(1)) {
            clickCreateGearAndWait(ITRON_GEAR_MODAL_DESCRIBEDBY);
        } else {
            clickCreateGearAndWaitDisplayBlock(ITRON_GEAR_MODAL_DESCRIBEDBY);
        }
        

        return new CreateItronPrgmGearModal(this.driverExt, Optional.empty(), Optional.of(ITRON_GEAR_MODAL_DESCRIBEDBY));
    }

    public CreateMeterDisconnectPrgmModal showCreateMeterDiconnectPrgmModal() {
        clickCreateGearAndWait("gear-create-popup-LM_METER_DISCONNECT_PROGRAM");

        return new CreateMeterDisconnectPrgmModal(this.driverExt, Optional.empty(), Optional.of("gear-create-popup-LM_METER_DISCONNECT_PROGRAM"));
    }

    public CreateSepPrgmGearModal showCreateSepPrgmGearModal() {
        clickCreateGearAndWait(DESCRIBEDBY);

        return new CreateSepPrgmGearModal(this.driverExt, Optional.empty(), Optional.of(DESCRIBEDBY));
    }

    public SelectBoxElement getGears() {
        Section section = new Section(this.driverExt, "Gears");

        return new SelectBoxElement(this.driverExt, section.getSection());
    }

    public LoadGroupsTab getLoadGroupTab() {
        return new LoadGroupsTab(this.driverExt);
    }
}
