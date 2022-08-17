package com.cannontech.web.dev;

public class EcobeeDataConfiguration {

    private int registerDevice;
    private int moveDevice;
    private int createSet;
    private int moveSet;
    private int sendDR;
    private int sendRestore;
    private int hierarchy;
    private int authenticate;
    private int removeSet;
    private int runtimeReport;
    private int assignThermostat;

    public EcobeeDataConfiguration(int registerDevice, int moveDevice, int createSet, int moveSet, int sendDR,
            int sendRestore, int hierarchy, int authenticate, int removeSet, int runtimeReport, int assignThermostat) {
        this.registerDevice = registerDevice;
        this.moveDevice = moveDevice;
        this.createSet = createSet;
        this.moveSet = moveSet;
        this.sendDR = sendDR;
        this.sendRestore = sendRestore;
        this.hierarchy = hierarchy;
        this.authenticate = authenticate;
        this.removeSet = removeSet;
        this.runtimeReport = runtimeReport;
        this.assignThermostat = assignThermostat;
    }
    
    public void setEcobeeDataConfiguration(int registerDevice, int moveDevice, int createSet, int moveSet, int sendDR,
            int sendRestore, int hierarchy, int authenticate, int removeSet, int runtimeReport, int assignThermostat){
        this.registerDevice = registerDevice;
        this.moveDevice = moveDevice;
        this.createSet = createSet;
        this.moveSet = moveSet;
        this.sendDR = sendDR;
        this.sendRestore = sendRestore;
        this.hierarchy = hierarchy;
        this.authenticate = authenticate;
        this.removeSet = removeSet;
        this.runtimeReport = runtimeReport;
        this.assignThermostat = assignThermostat;    	
    }

	public int getRegisterDevice() {
		return registerDevice;
	}

	public int getMoveDevice() {
		return moveDevice;
	}

	public int getCreateSet() {
		return createSet;
	}

	public int getMoveSet() {
		return moveSet;
	}

	public int getSendDR() {
		return sendDR;
	}

	public int getSendRestore() {
		return sendRestore;
	}

	public int getHierarchy() {
		return hierarchy;
	}

	public int getAuthenticate() {
		return authenticate;
	}

	public int getRemoveSet() {
		return removeSet;
	}

	public int getRuntimeReport() {
		return runtimeReport;
	}

	public int getAssignThermostat() {
		return assignThermostat;
	}


}
