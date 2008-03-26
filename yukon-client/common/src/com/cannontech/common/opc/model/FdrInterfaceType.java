package com.cannontech.common.opc.model;

public enum FdrInterfaceType {
	INET(1),
	ACS(2),
	VALMET(3),
	CYGNET(4),
	STEC(5),
	RCCS(6),
	TRISTATE(7),
	RDEX(8),
	SYSTEM(9),
	DMS2IMPORT(10),
	TELEGYR(11),
	TEXTIMPORT(12),
	TEXTEXPORT(13),
	EMPTY(14),
	EMPTY2(15),
	LODESTAR_STD(16),
	LODESTAR_ENH(17),
	DMS2FILEIN(18),
	XA21LM(19),
	BEPC(20),
	PI(21),
	LIVEDATA(22),
	ACSMULTI(23),
	WABASH(24),
	OPC(25);
	
	private final int pos;
	
	FdrInterfaceType(int pos) {
		this.pos = pos;
	}
	
	public int getValue() {
		return pos;
	}
}
