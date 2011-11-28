package com.cannontech.common.fdr;

import com.cannontech.common.i18n.DisplayableEnum;

public enum FdrInterfaceType implements DisplayableEnum {
	INET(1),
	ACS(2),
	VALMET(3),
	CYGNET(4),
	STEC(5),
	RCCS(6),
	TRISTATE(7),
	RDEX(8),
	SYSTEM(9),
	DSM2IMPORT(10),
	TELEGYR(11),
	TEXTIMPORT(12),
	TEXTEXPORT(13),
	EMPTY(14),
	EMPTY2(15),
	LODESTAR_STD(16),
	LODESTAR_ENH(17),
	DSM2FILEIN(18),
	XA21LM(19),
	BEPC(20),
	PI(21),
	LIVEDATA(22),
	ACSMULTI(23),
	WABASH(24),
	TRISTATESUB(25),
	OPC(26),
	MULTISPEAK_LM(27),
	DNPSLAVE(28),
	VALMETMULTI(29);
	
	private final int pos;
	private final String keyPrefix = "yukon.web.modules.amr.fdrTranslationManagement.interfaces.";
	
	FdrInterfaceType(int pos) {
		this.pos = pos;
	}
	
	public int getValue() {
		return pos;
	}
	
	public static FdrInterfaceType getById(int id) {
	    for(FdrInterfaceType interfaceType : FdrInterfaceType.values()) {
	        if(id == interfaceType.getValue()) {
	            return interfaceType;
	        }
	    }
	    return null;
	}
	
	@Override
    public String getFormatKey() {
        return keyPrefix + name();
    }
}
