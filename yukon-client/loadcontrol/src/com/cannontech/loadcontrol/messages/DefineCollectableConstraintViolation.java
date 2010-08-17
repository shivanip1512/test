package com.cannontech.loadcontrol.messages;

import java.util.Date;

import com.cannontech.message.util.VectorExtract;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefineCollectableConstraintViolation extends com.cannontech.message.util.DefineCollectableMessage {

	// RogueWave classId
	public static final int CTILMCONSTRAINTVIOLATION_ID = 639;

	public DefineCollectableConstraintViolation() {
		super();
	}

	public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException {
		return new ConstraintViolation();
	}

	public int getCxxClassId() {
		return CTILMCONSTRAINTVIOLATION_ID;
	}

	public String getCxxStringId() {
		return DefineCollectable.NO_STRINGID;
	}

	public Class<ConstraintViolation> getJavaClass() {
		return ConstraintViolation.class;
	}

	public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
		ConstraintViolation cc = (ConstraintViolation) obj;

		cc.setErrorCode(ConstraintError.getByErrorCode((int) vstr.extractUnsignedInt()));
		cc.setDoubleParams(VectorExtract.extractDoubleList(vstr, polystr));
		cc.setIntegerParams(VectorExtract.extractIntList(vstr, polystr));
		cc.setStringParams(VectorExtract.extractList(vstr, SimpleMappings.CString, String.class));
		cc.setDateTimeParams(VectorExtract.extractList(vstr, SimpleMappings.Time, Date.class));
	}

	public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
		/* This saveGuts isn't implemented */
	}
}
