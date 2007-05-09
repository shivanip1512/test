package unit;

import junit.framework.TestCase;
import mock.MockCBCWizardModel;
import mock.MockCapControlForm;

import com.cannontech.database.data.pao.CapControlTypes;

public class TestCapControlFeederCreate extends TestCase {

    public void testCreateFeederWithRandomName() {
        MockCapControlForm form = new MockCapControlForm();
        MockCBCWizardModel cbcWizardModel = (MockCBCWizardModel) form.getWizData();
        int uniqueNumber = (int) (Math.random() * 1000000);
        String feederName = "Test Feeder " + uniqueNumber;
        cbcWizardModel.setName(feederName);
        form.initWizard(CapControlTypes.CAP_CONTROL_FEEDER);
        String itemID = form.create();
        int actual = Integer.parseInt(itemID);
        if (actual == -1)
            fail("Could not create CapControl Object");
        else
            assertSame("Feeder:" + feederName + " was created successfully!",
                       true,
                       true);
    }

}
