package com.amdswireless.messages.twoway;

public class TableMT29 {
	private int repId;
	private MT29OverRideEntry[] overRideEntry = new MT29OverRideEntry[2];

	public TableMT29(int r) {
		this.repId = r;
	}
	
	public MT29OverRideEntry getOverRideEntry(int i) {
		if ( i > 1 ) { i=1;}
		if ( i < 0 ) { i=0;}
		return overRideEntry[i];
 	}
	
	public void setOverRideEntry(int i, MT29OverRideEntry ovr) {
		if ( i > 1 ) { i=1;}
		if ( i < 0 ) { i=0;}
		overRideEntry[i]=ovr;
	}

	public int getRepId() {
		return repId;
	}
	
}
