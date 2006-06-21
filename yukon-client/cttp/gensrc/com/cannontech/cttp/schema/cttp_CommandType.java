/**
 * cttp_CommandType.java
 *
 * This file was generated by XMLSPY 2004 Enterprise Edition.
 *
 * YOU SHOULD NOT MODIFY THIS FILE, BECAUSE IT WILL BE
 * OVERWRITTEN WHEN YOU RE-RUN CODE GENERATION.
 *
 * Refer to the XMLSPY Documentation for further details.
 * http://www.altova.com/xmlspy
 */


package com.cannontech.cttp.schema;

import com.altova.xml.types.SchemaString;

public class cttp_CommandType extends com.altova.xml.Node {
	public cttp_CommandType() {
		super();
	}

	public cttp_CommandType(cttp_CommandType node) {
		super(node);
	}

	public cttp_CommandType(org.w3c.dom.Node node) {
		super(node);
	}

	public cttp_CommandType(org.w3c.dom.Document doc) {
		super(doc);
	}
	public void adjustPrefix() {
		int count;
		count = getDomChildCount(Element, null, "cttp-OffsetCommand");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "cttp-OffsetCommand", i);
			internalAdjustPrefix(tmpNode, false);
			new cttp_OffsetCommandType(tmpNode).adjustPrefix();
		}
		count = getDomChildCount(Element, null, "cttp-ClearCommand");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "cttp-ClearCommand", i);
			internalAdjustPrefix(tmpNode, false);
		}
	}

	public int getcttp_OffsetCommandMinCount() {
		return 1;
	}

	public int getcttp_OffsetCommandMaxCount() {
		return 1;
	}

	public int getcttp_OffsetCommandCount() {
		return getDomChildCount(Element, null, "cttp-OffsetCommand");
	}

	public boolean hascttp_OffsetCommand() {
		return hasDomChild(Element, null, "cttp-OffsetCommand");
	}

	public cttp_OffsetCommandType getcttp_OffsetCommandAt(int index) throws Exception {
		return new cttp_OffsetCommandType(getDomChildAt(Element, null, "cttp-OffsetCommand", index));
	}

	public cttp_OffsetCommandType getcttp_OffsetCommand() throws Exception {
		return getcttp_OffsetCommandAt(0);
	}

	public void removecttp_OffsetCommandAt(int index) {
		removeDomChildAt(Element, null, "cttp-OffsetCommand", index);
	}

	public void removecttp_OffsetCommand() {
		while (hascttp_OffsetCommand())
			removecttp_OffsetCommandAt(0);
	}

	public void addcttp_OffsetCommand(cttp_OffsetCommandType value) {
		appendDomElement(null, "cttp-OffsetCommand", value);
	}

	public void insertcttp_OffsetCommandAt(cttp_OffsetCommandType value, int index) {
		insertDomElementAt(null, "cttp-OffsetCommand", index, value);
	}

	public void replacecttp_OffsetCommandAt(cttp_OffsetCommandType value, int index) {
		replaceDomElementAt(null, "cttp-OffsetCommand", index, value);
	}

	public int getcttp_ClearCommandMinCount() {
		return 1;
	}

	public int getcttp_ClearCommandMaxCount() {
		return 1;
	}

	public int getcttp_ClearCommandCount() {
		return getDomChildCount(Element, null, "cttp-ClearCommand");
	}

	public boolean hascttp_ClearCommand() {
		return hasDomChild(Element, null, "cttp-ClearCommand");
	}

	public SchemaString getcttp_ClearCommandAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Element, null, "cttp-ClearCommand", index)));
	}

	public SchemaString getcttp_ClearCommand() throws Exception {
		return getcttp_ClearCommandAt(0);
	}

	public void removecttp_ClearCommandAt(int index) {
		removeDomChildAt(Element, null, "cttp-ClearCommand", index);
	}

	public void removecttp_ClearCommand() {
		while (hascttp_ClearCommand())
			removecttp_ClearCommandAt(0);
	}

	public void addcttp_ClearCommand(SchemaString value) {
		setDomChild(Element, null, "cttp-ClearCommand", value.toString());
	}

	public void addcttp_ClearCommand(String value) throws Exception {
		addcttp_ClearCommand(new SchemaString(value));
	}

	public void insertcttp_ClearCommandAt(SchemaString value, int index) {
		insertDomChildAt(Element, null, "cttp-ClearCommand", index, value.toString());
	}

	public void insertcttp_ClearCommandAt(String value, int index) throws Exception {
		insertcttp_ClearCommandAt(new SchemaString(value), index);
	}

	public void replacecttp_ClearCommandAt(SchemaString value, int index) {
		replaceDomChildAt(Element, null, "cttp-ClearCommand", index, value.toString());
	}

	public void replacecttp_ClearCommandAt(String value, int index) throws Exception {
		replacecttp_ClearCommandAt(new SchemaString(value), index);
	}
}
