package com.cannontech.loadcontrol.data;

import com.roguewave.vsj.DefineCollectable;

/**
 * Collectable class for LMGroupSA305
 * @author aaron
 */
public class DefColl_LMGroupSA305 extends DefColl_LMDirectGroupBase {
	//The roguewa class id
	private static int CTILMGROUPSA305_ID = 632;

	public DefColl_LMGroupSA305() {
		super();
	}

	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
		return new LMGroupSA305();
	}

	public int getCxxClassId() {
		return CTILMGROUPSA305_ID;
	}

	public String getCxxStringId() {
		return DefineCollectable.NO_STRINGID;
	}

	public Class getJavaClass() {
		return LMGroupSA305.class;
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
