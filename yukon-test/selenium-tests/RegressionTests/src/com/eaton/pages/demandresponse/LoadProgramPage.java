package com.eaton.pages.demandresponse;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.eaton.elements.Button;
import com.eaton.elements.DropDownElement;
import com.eaton.elements.TextEditElement;
import com.eaton.elements.TrueFalseCheckboxElement;
import com.eaton.elements.modals.gears.CreateDirectPrgmGearModal;
import com.eaton.elements.modals.gears.CreateEcobeePrgmGearModal;
import com.eaton.elements.modals.gears.CreateHoneywellPrgmGearModal;
import com.eaton.elements.modals.gears.CreateItronPrgmGearModal;
import com.eaton.elements.modals.gears.CreateMeterDiconnectPrgmModal;
import com.eaton.elements.modals.gears.CreateSepPrgmGearModal;
import com.eaton.pages.PageBase;

public class LoadProgramPage extends PageBase {

    private TextEditElement name;
    private DropDownElement type;

    public LoadProgramPage(WebDriver driver, String pageUrl) {
        super(driver, pageUrl);

        name = new TextEditElement(this.driver, "name", null);
        type = new DropDownElement(this.driver, "type", null);
    }

    public String getTitle() {
        return this.driver.findElement(By.cssSelector(".page-heading")).getText();
    }

    // General
    public TextEditElement getName() {
        return name;
    }

    public DropDownElement getType() {
        return type;
    }

    public DropDownElement getOperationalState() {
        return new DropDownElement(this.driver, "operationalState", null);
    }

    public DropDownElement getConstraint() {
        return new DropDownElement(this.driver, "constraint.constraintId", null);
    }

    // Trigger Threshold Settings
    public TextEditElement getTriggerOffset() {
        return new TextEditElement(this.driver, "triggerOffset", null);
    }

    public TextEditElement getRestoreOffset() {
        return new TextEditElement(this.driver, "restoreOffset", null);
    }

    // Gears
    // add gear section

    // Control Window
    public TrueFalseCheckboxElement getUseWindowOne() {
        return new TrueFalseCheckboxElement(this.driver, "controlWindowOne", null);
    }

    public TrueFalseCheckboxElement getUseWindowTwo() {
        return new TrueFalseCheckboxElement(this.driver, "controlWindowTwo", null);
    }

    public Button getSaveBtn() {
        return new Button(this.driver, "Save", null);
    }

    public Button getCancelBtn() {
        return new Button(this.driver, "Cancel", null);
    }

    public Button getCreateBtn() {
        return new Button(this.driver, "Create", null);
    }

    public CreateDirectPrgmGearModal showCreateDirectPrgmGearsModal() {
        getCreateBtn().click();

        return new CreateDirectPrgmGearModal(this.driver, "gear-create-popup-LM_DIRECT_PROGRAM");
    }

    public CreateEcobeePrgmGearModal showCreateEcobeePrgmGearModal() {
        getCreateBtn().click();

        return new CreateEcobeePrgmGearModal(this.driver, "gear-create-popup-LM_DIRECT_PROGRAM");
    }

    public CreateHoneywellPrgmGearModal showCreateHoneywellPrgmGearModal() {
        getCreateBtn().click();

        return new CreateHoneywellPrgmGearModal(this.driver, "gear-create-popup-LM_DIRECT_PROGRAM");
    }

    public CreateItronPrgmGearModal showCreateItronPrgmGearModal() {
        getCreateBtn().click();

        return new CreateItronPrgmGearModal(this.driver, "gear-create-popup-LM_DIRECT_PROGRAM");
    }

    public CreateMeterDiconnectPrgmModal showCreateMeterDiconnectPrgmModal() {
        getCreateBtn().click();

        return new CreateMeterDiconnectPrgmModal(this.driver, "gear-create-popup-LM_DIRECT_PROGRAM");
    }

    public CreateSepPrgmGearModal showCreateSepPrgmGearModal() {
        getCreateBtn().click();

        return new CreateSepPrgmGearModal(this.driver, "gear-create-popup-LM_DIRECT_PROGRAM");
    }
}
