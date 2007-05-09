package unit;

import junit.framework.TestCase;
import mock.MockCBCWizardModel;
import mock.MockCapControlForm;

import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.database.data.pao.PAOGroups;

public class TestCapControlCBC extends TestCase {
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    //make sure you set the port id that has an entry in
    //PortSettings
    //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private static final int portID = 1321;

    public void testCreateCBC702xWithRandomName()
            throws PortDoesntExistException {
        MockCapControlForm form = new MockCapControlForm();
        MockCBCWizardModel cbcWizardModel = (MockCBCWizardModel) form.getWizData();
        int uniqueNumber = (int) (Math.random() * 1000000);
        String cbcName = "Test CBC 702x" + uniqueNumber;
        cbcWizardModel.setName(cbcName);
        cbcWizardModel.setPortID(portID);
        form.initWizard(PAOGroups.CBC_7020);
        String itemID = form.create();
        int actual = Integer.parseInt(itemID);
        if (actual == -1) {
            throw new PortDoesntExistException("Could not create CBC 702x Object");
        } else
            assertSame("CBC 702x:" + cbcName + " was created successfully!",
                       true,
                       true);
    }

    public void testCreateCBC701xWithRandomName() {
        MockCapControlForm form = new MockCapControlForm();
        MockCBCWizardModel cbcWizardModel = (MockCBCWizardModel) form.getWizData();
        int uniqueNumber = (int) (Math.random() * 1000000);
        String cbcName = "Test CBC 701x " + uniqueNumber;
        cbcWizardModel.setName(cbcName);
        form.initWizard(PAOGroups.CBC_7010);
        String itemID = form.create();
        int actual = Integer.parseInt(itemID);
        if (actual == -1) {
            fail("Could not create CBC 701x Object");
        } else
            assertSame("CBC:" + cbcName + " was created successfully!",
                       true,
                       true);
    }

    public void testCreateCBCVersacomWithRandomName() {
        MockCapControlForm form = new MockCapControlForm();
        MockCBCWizardModel cbcWizardModel = (MockCBCWizardModel) form.getWizData();
        int uniqueNumber = (int) (Math.random() * 1000000);
        String cbcName = "Test CBC Versacom " + uniqueNumber;
        cbcWizardModel.setName(cbcName);
        form.initWizard(PAOGroups.CAPBANKCONTROLLER);
        String itemID = form.create();
        int actual = Integer.parseInt(itemID);
        if (actual == -1) {
            fail("Could not create CBC Versacom Object");
        } else
            assertSame("CBC:" + cbcName + " was created successfully!",
                       true,
                       true);
    }

    public void testCreateCBCExpresscomWithRandomName() {
        MockCapControlForm form = new MockCapControlForm();
        MockCBCWizardModel cbcWizardModel = (MockCBCWizardModel) form.getWizData();
        int uniqueNumber = (int) (Math.random() * 1000000);
        String cbcName = "Test CBC Expresscom " + uniqueNumber;
        cbcWizardModel.setName(cbcName);
        form.initWizard(PAOGroups.CBC_EXPRESSCOM);
        String itemID = form.create();
        int actual = Integer.parseInt(itemID);
        if (actual == -1) {
            fail("Could not create CBC Expresscom Object");
        } else
            assertSame("CBC:" + cbcName + " was created successfully!",
                       true,
                       true);
    }
}
