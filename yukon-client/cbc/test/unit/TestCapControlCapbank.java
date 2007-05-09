package unit;

import junit.framework.TestCase;
import mock.MockCBCWizardModel;
import mock.MockCapControlForm;

import com.cannontech.database.data.pao.PAOGroups;

public class TestCapControlCapbank extends TestCase {
    
    
    public void testCreateCapBankWithRandomName() {
        MockCapControlForm form = new MockCapControlForm();
        MockCBCWizardModel cbcWizardModel = (MockCBCWizardModel) form.getWizData();
        int uniqueNumber = (int) (Math.random() * 1000000);
        String capBankName = "Test Cap " + uniqueNumber;
        cbcWizardModel.setName(capBankName);
        form.initWizard(PAOGroups.CAPBANK);
        String itemID = form.create();
        int actual = Integer.parseInt(itemID);
        if (actual == -1)
            fail("Could not create CapControl Object");
        else
            assertSame("CapBank:" + capBankName + " was created successfully!",
                       true,
                       true);
    }
}
