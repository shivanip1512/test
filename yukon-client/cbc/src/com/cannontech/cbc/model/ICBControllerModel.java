package com.cannontech.cbc.model;

import java.sql.SQLException;

import com.cannontech.cbc.exceptions.MultipleDevicesOnPortException;
import com.cannontech.cbc.exceptions.PortDoesntExistException;
import com.cannontech.cbc.exceptions.SameMasterSlaveCombinationException;
import com.cannontech.cbc.exceptions.SerialNumberExistsException;
import com.cannontech.common.device.config.model.DNPConfiguration;
import com.cannontech.database.data.device.RemoteBase;
import com.cannontech.database.data.pao.YukonPAObject;

public interface ICBControllerModel {

    public abstract boolean isEditingIntegrity();

    public abstract boolean isEditingException();

    public abstract void setEditingIntegrity(boolean val);

    public abstract void setEditingException(boolean val);

    public abstract boolean isTwoWay();

    public abstract boolean isOneWay();

    public abstract YukonPAObject getPaoCBC();

    public abstract void setPaoCBC(YukonPAObject deviceCBC);

    public abstract void retrieveDB();

    //public abstract TreeNode getPointList();

    public abstract String getSelectedPointFormatString();

    public abstract String getCbcControllerStatusMessage();

    public abstract void setCbcControllerStatusMessage(String msgStr);

    //public abstract HtmlTree getPointTree();

    // public abstract void setPointTree(HtmlTree pointTree);

    public abstract boolean isEditingController();

    public abstract void setEditingController(boolean editingController);

    public abstract void checkForErrors() throws PortDoesntExistException,
            MultipleDevicesOnPortException,
            SameMasterSlaveCombinationException, SQLException,
            SerialNumberExistsException;

    //public abstract void pointClick(ActionEvent ae);

    //public abstract void addPointClick(ActionEvent ae);

    //public abstract void deletePointClick(ActionEvent ae);

    //public abstract void setPointList(TreeNode pointList);

    public abstract long getSerialNumber();

    public abstract void setSerialNumber(long serialNumber);

    public abstract void resetSerialNumber();

    public abstract boolean isDevice702X();
    
    public abstract boolean isDevice802X();

    public abstract boolean isDevice701X();
    
    public abstract boolean isDeviceDNP();

    public abstract int getDeviceType();

    public abstract void setDeviceType(int deviceType);

    public abstract boolean isTcpPort();

    public abstract boolean isTcpPort(RemoteBase cbc);
    
    public abstract DNPConfiguration getDnpConfiguration();
}