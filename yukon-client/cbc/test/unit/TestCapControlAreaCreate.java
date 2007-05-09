package unit;

import junit.framework.TestCase;
import mock.MockCBCWizardModel;
import mock.MockCapControlForm;

import com.cannontech.database.data.pao.CapControlTypes;

public class TestCapControlAreaCreate extends TestCase {

    public void testCreateAreaWithRandomName() {
        MockCapControlForm form = new MockCapControlForm();
        MockCBCWizardModel cbcWizardModel = (MockCBCWizardModel) form.getWizData();
        int uniqueNumber = (int) (Math.random() * 1000000);
        String areaName = "Test Area " + uniqueNumber;
        cbcWizardModel.setName(areaName);
        form.initWizard(CapControlTypes.CAP_CONTROL_AREA);
        String itemID = form.create();
        int actual = Integer.parseInt(itemID);
        if (actual == -1)
            fail("Could not create CapControl Object");
        else
            assertSame("Area:" + areaName + " was created successfully!",
                       true,
                       true);
    }

}
