package com.cannontech.loadcontrol.data;

import com.roguewave.vsj.DefineCollectable;

/**
 * Collectable class for LMGroupGolay
 * @author aaron
 */
public class DefColl_LMGroupGolay extends DefColl_LMDirectGroupBase {
	//The roguewa class id
	private static int CTILMGROUPGOLAY_ID = 633;

	public DefColl_LMGroupGolay() {
		super();
	}

	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
		return new LMGroupGolay();
	}

	public int getCxxClassId() {
		return CTILMGROUPGOLAY_ID;
	}

	public String getCxxStringId() {
		return DefineCollectable.NO_STRINGID;
	}

	public Class getJavaClass() {
		return LMGroupGolay.class;
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
