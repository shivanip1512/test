/**
 * cttp_CommandResponseType.java
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

import com.altova.xml.types.*;

public class cttp_CommandResponseType extends com.altova.xml.Node {
	public cttp_CommandResponseType() {
		super();
	}

	public cttp_CommandResponseType(cttp_CommandResponseType node) {
		super(node);
	}

	public cttp_CommandResponseType(org.w3c.dom.Node node) {
		super(node);
	}

	public cttp_CommandResponseType(org.w3c.dom.Document doc) {
		super(doc);
	}
	public void adjustPrefix() {
		int count;
		count = getDomChildCount(Attribute, null, "timestamp");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "timestamp", i);
			internalAdjustPrefix(tmpNode, false);
		}
		count = getDomChildCount(Attribute, null, "commandTrackingCode");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "commandTrackingCode", i);
			internalAdjustPrefix(tmpNode, false);
		}
	}

	public int gettimestampMinCount() {
		return 1;
	}

	public int gettimestampMaxCount() {
		return 1;
	}

	public int gettimestampCount() {
		return getDomChildCount(Attribute, null, "timestamp");
	}

	public boolean hastimestamp() {
		return hasDomChild(Attribute, null, "timestamp");
	}

	public SchemaString gettimestampAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Attribute, null, "timestamp", index)));
	}

	public SchemaString gettimestamp() throws Exception {
		return gettimestampAt(0);
	}

	public void removetimestampAt(int index) {
		removeDomChildAt(Attribute, null, "timestamp", index);
	}

	public void removetimestamp() {
		while (hastimestamp())
			removetimestampAt(0);
	}

	public void addtimestamp(SchemaString value) {
		setDomChild(Attribute, null, "timestamp", value.toString());
	}

	public void addtimestamp(String value) throws Exception {
		addtimestamp(new SchemaString(value));
	}

	public void inserttimestampAt(SchemaString value, int index) {
		insertDomChildAt(Attribute, null, "timestamp", index, value.toString());
	}

	public void inserttimestampAt(String value, int index) throws Exception {
		inserttimestampAt(new SchemaString(value), index);
	}

	public void replacetimestampAt(SchemaString value, int index) {
		replaceDomChildAt(Attribute, null, "timestamp", index, value.toString());
	}

	public void replacetimestampAt(String value, int index) throws Exception {
		replacetimestampAt(new SchemaString(value), index);
	}

	public int getcommandTrackingCodeMinCount() {
		return 1;
	}

	public int getcommandTrackingCodeMaxCount() {
		return 1;
	}

	public int getcommandTrackingCodeCount() {
		return getDomChildCount(Attribute, null, "commandTrackingCode");
	}

	public boolean hascommandTrackingCode() {
		return hasDomChild(Attribute, null, "commandTrackingCode");
	}

	public SchemaString getcommandTrackingCodeAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Attribute, null, "commandTrackingCode", index)));
	}

	public SchemaString getcommandTrackingCode() throws Exception {
		return getcommandTrackingCodeAt(0);
	}

	public void removecommandTrackingCodeAt(int index) {
		removeDomChildAt(Attribute, null, "commandTrackingCode", index);
	}

	public void removecommandTrackingCode() {
		while (hascommandTrackingCode())
			removecommandTrackingCodeAt(0);
	}

	public void addcommandTrackingCode(SchemaString value) {
		setDomChild(Attribute, null, "commandTrackingCode", value.toString());
	}

	public void addcommandTrackingCode(String value) throws Exception {
		addcommandTrackingCode(new SchemaString(value));
	}

	public void insertcommandTrackingCodeAt(SchemaString value, int index) {
		insertDomChildAt(Attribute, null, "commandTrackingCode", index, value.toString());
	}

	public void insertcommandTrackingCodeAt(String value, int index) throws Exception {
		insertcommandTrackingCodeAt(new SchemaString(value), index);
	}

	public void replacecommandTrackingCodeAt(SchemaString value, int index) {
		replaceDomChildAt(Attribute, null, "commandTrackingCode", index, value.toString());
	}

	public void replacecommandTrackingCodeAt(String value, int index) throws Exception {
		replacecommandTrackingCodeAt(new SchemaString(value), index);
	}
}
