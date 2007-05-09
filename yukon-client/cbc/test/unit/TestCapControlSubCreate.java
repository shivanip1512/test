package unit;

import com.cannontech.database.data.pao.CapControlTypes;

import junit.framework.TestCase;
import mock.MockCBCWizardModel;
import mock.MockCapControlForm;

public class TestCapControlSubCreate extends TestCase {

    public void testCreateSubWithRandomName() {
        MockCapControlForm form = new MockCapControlForm();
        MockCBCWizardModel cbcWizardModel = (MockCBCWizardModel) form.getWizData();
        int uniqueNumber = (int) (Math.random() * 1000000);
        String subName = "Test Sub " + uniqueNumber;
        cbcWizardModel.setName(subName);
        form.initWizard(CapControlTypes.CAP_CONTROL_SUBBUS);
        String itemID = form.create();
        int actual = Integer.parseInt(itemID);
        if (actual == -1)
            fail("Could not create CapControl Object");
        else
            assertSame("Sub:" + subName + " was created successfully!",
                       true,
                       true);
    }

}
