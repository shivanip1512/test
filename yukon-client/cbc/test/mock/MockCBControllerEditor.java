package mock;

import java.sql.SQLException;

import com.cannontech.cbc.exceptions.MultipleDevicesOnPortException;
import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.cbc.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.cbc.exceptions.SerialNumberExistsException;
import com.cannontech.cbc.model.ICBControllerModel;
import com.cannontech.database.data.pao.YukonPAObject;

public class MockCBControllerEditor implements ICBControllerModel {

    /*
     * public void addPointClick(ActionEvent ae) { throw new
     * UnsupportedOperationException(); }
     */

    public void checkForErrors() throws PortDoesntExistException,
            MultipleDevicesOnPortException,
            SameMasterSlaveCombinationException, SQLException,
            SerialNumberExistsException {
        throw new UnsupportedOperationException();

    }

    /*
     * public void deletePointClick(ActionEvent ae) { throw new
     * UnsupportedOperationException(); }
     */

    public String getCbcControllerStatusMessage() {
        throw new UnsupportedOperationException();
    }

    public int getDeviceType() {
        throw new UnsupportedOperationException();
    }

    public YukonPAObject getPaoCBC() {
        throw new UnsupportedOperationException();
    }

    /*
     * public TreeNode getPointList() { throw new
     * UnsupportedOperationException(); } public HtmlTree getPointTree() { throw
     * new UnsupportedOperationException(); }
     */

    public String getSelectedPointFormatString() {
        throw new UnsupportedOperationException();
    }

    public long getSerialNumber() {
        throw new UnsupportedOperationException();
    }

    public boolean isDevice701X() {
        throw new UnsupportedOperationException();
    }

    public boolean isDevice702X() {
        throw new UnsupportedOperationException();
    }

    public boolean isEditingController() {
        throw new UnsupportedOperationException();
    }

    public boolean isEditingException() {
        throw new UnsupportedOperationException();
    }

    public boolean isEditingIntegrity() {
        throw new UnsupportedOperationException();
    }

    public boolean isOneWay() {
        throw new UnsupportedOperationException();
    }

    public boolean isTwoWay() {
        throw new UnsupportedOperationException();
    }

    /*
     * public void pointClick(ActionEvent ae) { throw new
     * UnsupportedOperationException(); }
     */
    public void resetSerialNumber() {
        throw new UnsupportedOperationException();

    }

    public void retrieveDB() {
        throw new UnsupportedOperationException();

    }

    public void setCbcControllerStatusMessage(String msgStr) {
        throw new UnsupportedOperationException();

    }

    public void setDeviceType(int deviceType) {
        throw new UnsupportedOperationException();

    }

    public void setEditingController(boolean editingController) {
        throw new UnsupportedOperationException();

    }

    public void setEditingException(boolean val) {
        throw new UnsupportedOperationException();

    }

    public void setEditingIntegrity(boolean val) {
        throw new UnsupportedOperationException();

    }

    public void setPaoCBC(YukonPAObject deviceCBC) {
        throw new UnsupportedOperationException();

    }

    /*
     * public void setPointList(TreeNode pointList) { throw new
     * UnsupportedOperationException(); } public void setPointTree(HtmlTree
     * pointTree) { throw new UnsupportedOperationException(); }
     */

    public void setSerialNumber(long serialNumber) {
        throw new UnsupportedOperationException();

    }

    @Override
    public boolean isDeviceDNP() {
        return false;
    }

}
