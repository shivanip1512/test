package com.cannontech.loadcontrol.data;

import com.roguewave.vsj.DefineCollectable;

/**
 * Collectable class for LMGroupSA105
 * @author aaron
 */
public class DefColl_LMGroupSA105 extends DefColl_LMDirectGroupBase {
	//The roguewa class id
	private static int CTILMGROUPSA105_ID = 631;

	public DefColl_LMGroupSA105() {
		super();
	}

	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
		return new LMGroupSA105();
	}

	public int getCxxClassId() {
		return CTILMGROUPSA105_ID;
	}

	public String getCxxStringId() {
		return DefineCollectable.NO_STRINGID;
	}

	public Class getJavaClass() {
		return LMGroupSA105.class;
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
