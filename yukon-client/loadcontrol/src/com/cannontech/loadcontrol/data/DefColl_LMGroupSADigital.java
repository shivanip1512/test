package com.cannontech.loadcontrol.data;

import com.roguewave.vsj.DefineCollectable;

/**
 * Collectable class for LMGroupSADigital
 * @author aaron
 */
public class DefColl_LMGroupSADigital extends DefColl_LMDirectGroupBase {
	//The roguewa class id
	private static int CTILMGROUPSADIGITAL_ID = 633;

	public DefColl_LMGroupSADigital() {
		super();
	}

	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
		return new LMGroupSADigital();
	}

	public int getCxxClassId() {
		return CTILMGROUPSADIGITAL_ID;
	}

	public String getCxxStringId() {
		return DefineCollectable.NO_STRINGID;
	}

	public Class getJavaClass() {
		return LMGroupSADigital.class;
	}

	public void restoreGuts(
		Object obj,
		com.roguewave.vsj.VirtualInputStream vstr,
		com.roguewave.vsj.CollectableStreamer polystr)
		throws java.io.IOException {
		super.restoreGuts(obj, vstr, polystr);
	}

	public void saveGuts(
		Object obj,
		com.roguewave.vsj.VirtualOutputStream vstr,
		com.roguewave.vsj.CollectableStreamer polystr)
		throws java.io.IOException {
		super.saveGuts(obj, vstr, polystr);
	}
}
