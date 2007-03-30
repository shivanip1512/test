package miscl;


import junit.framework.TestCase;

import com.cannontech.web.editor.CapControlForm;

/**
 * example to to create cap control objects
 * @author ekhazon
 *
 */
public class TestSubCreation extends TestCase {

    CapControlForm form = new CapControlForm();

    public void testCreateSub () {
        CapControlForm form = new CapControlForm();
        form.getWizData().setName("Elliot's Sub 11");
        form.initWizard (4000);
        form.create();
    
}


}
