package com.cannontech.loadcontrol.data;

import com.roguewave.vsj.DefineCollectable;

/**
 * Collectable class for LMGroupSADigitalOrGolay
 * @author aaron
 */
public class DefColl_LMGroupSADigitalOrGolay extends DefColl_LMDirectGroupBase {
	//The roguewave class id
	private static int CTILMGROUPSADIGITALORGOLAY_ID = 631;

	public DefColl_LMGroupSADigitalOrGolay() {
		super();
	}

	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
		return new LMGroupSA205Or105();
	}

	public int getCxxClassId() {
		return CTILMGROUPSADIGITALORGOLAY_ID;
	}

	public String getCxxStringId() {
		return DefineCollectable.NO_STRINGID;
	}

	public Class getJavaClass() {
		return LMGroupSADigitalOrGolay.class;
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
