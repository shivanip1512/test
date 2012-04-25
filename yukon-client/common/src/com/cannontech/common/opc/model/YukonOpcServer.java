package com.cannontech.common.opc.model;

import com.cannontech.common.opc.impl.YukonOpcConnectionImpl;
import com.netmodule.jpc.driver.opc.OpcGroup;
import com.netmodule.jpc.driver.opc.OpcServer;
import com.netmodule.jpc.driver.opc.OpcServerStatus;

public class YukonOpcServer {

    private OpcServer opcServer;
    private String statusItemName;

    public YukonOpcServer (OpcServer opcServer, String statusItemName) {
        this.opcServer = opcServer;
        this.statusItemName = statusItemName;
    }
    
    public OpcGroup addGroup(String arg0, int arg1, int arg2) {
        OpcGroup opcGroup = opcServer.addGroup(arg0, arg1, arg2);
        
        //Null means problem with server
        if (opcGroup != null) {
            registerExtraItem(opcGroup);
        }        
        
        return opcGroup;
    }

    public OpcGroup addGroup(String arg0, int arg1) {
        OpcGroup opcGroup = opcServer.addGroup(arg0, arg1);

        //Null means problem with server
        if (opcGroup != null) {
            registerExtraItem(opcGroup);
        }
        
        return opcGroup;
    }

    /**
     * This will register for a static item. Added to guarantee that the Handle returned
     * by the server will be greater than Zero. OSI PI starts counting at Zero and NetModule 
     * calls Zero an error.
     * 
     * @return
     */
    private void registerExtraItem(OpcGroup opcGroup) {
        int ret [] = opcGroup.addItems(new String[]{statusItemName}, new int[]{YukonOpcConnectionImpl.yukonOpcStatusItemId});
        int i = 0;
        i++;
        return;
    }
    
    public String[] browse(int arg0, String arg1) {
        return opcServer.browse(arg0, arg1);
    }

    public int changeBrowsePosition(int arg0, String arg1) {
        return opcServer.changeBrowsePosition(arg0, arg1);
    }

    public boolean connect(String arg0, String arg1) {
        return opcServer.connect(arg0, arg1);
    }

    public boolean connect(String arg0) {
        return opcServer.connect(arg0);
    }

    public void disconnect() {
        opcServer.disconnect();
    }

    public boolean equals(Object obj) {
        return opcServer.equals(obj);
    }

    public String[] getAllItemIDs() {
        return opcServer.getAllItemIDs();
    }

    public int getQueryOrganization() {
        return opcServer.getQueryOrganization();
    }

    public OpcServerStatus getStatus() {
        return opcServer.getStatus();
    }

    public int hashCode() {
        return opcServer.hashCode();
    }

    public boolean isConnected() {
        return opcServer.isConnected();
    }

    public int registerShutdownCallback(Runnable arg0) {
        return opcServer.registerShutdownCallback(arg0);
    }

    public int removeGroup(OpcGroup arg0) {
        return opcServer.removeGroup(arg0);
    }

    public void removeShutdownCallback(Runnable arg0) {
        opcServer.removeShutdownCallback(arg0);
    }

    public String toString() {
        return opcServer.toString();
    }

}
