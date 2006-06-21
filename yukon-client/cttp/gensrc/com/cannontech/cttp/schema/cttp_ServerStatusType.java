/**
 * cttp_ServerStatusType.java
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

public class cttp_ServerStatusType extends com.altova.xml.Node {
	public cttp_ServerStatusType() {
		super();
	}

	public cttp_ServerStatusType(cttp_ServerStatusType node) {
		super(node);
	}

	public cttp_ServerStatusType(org.w3c.dom.Node node) {
		super(node);
	}

	public cttp_ServerStatusType(org.w3c.dom.Document doc) {
		super(doc);
	}
	public void adjustPrefix() {
		int count;
		count = getDomChildCount(Attribute, null, "timestamp");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "timestamp", i);
			internalAdjustPrefix(tmpNode, false);
		}
		count = getDomChildCount(Attribute, null, "version");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "version", i);
			internalAdjustPrefix(tmpNode, false);
		}
		count = getDomChildCount(Attribute, null, "owner");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "owner", i);
			internalAdjustPrefix(tmpNode, false);
		}
		count = getDomChildCount(Attribute, null, "supportContact");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "supportContact", i);
			internalAdjustPrefix(tmpNode, false);
		}
		count = getDomChildCount(Attribute, null, "supportPhone");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "supportPhone", i);
			internalAdjustPrefix(tmpNode, false);
		}
		count = getDomChildCount(Attribute, null, "supportEmail");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Attribute, null, "supportEmail", i);
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

	public int getversionMinCount() {
		return 1;
	}

	public int getversionMaxCount() {
		return 1;
	}

	public int getversionCount() {
		return getDomChildCount(Attribute, null, "version");
	}

	public boolean hasversion() {
		return hasDomChild(Attribute, null, "version");
	}

	public SchemaString getversionAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Attribute, null, "version", index)));
	}

	public SchemaString getversion() throws Exception {
		return getversionAt(0);
	}

	public void removeversionAt(int index) {
		removeDomChildAt(Attribute, null, "version", index);
	}

	public void removeversion() {
		while (hasversion())
			removeversionAt(0);
	}

	public void addversion(SchemaString value) {
		setDomChild(Attribute, null, "version", value.toString());
	}

	public void addversion(String value) throws Exception {
		addversion(new SchemaString(value));
	}

	public void insertversionAt(SchemaString value, int index) {
		insertDomChildAt(Attribute, null, "version", index, value.toString());
	}

	public void insertversionAt(String value, int index) throws Exception {
		insertversionAt(new SchemaString(value), index);
	}

	public void replaceversionAt(SchemaString value, int index) {
		replaceDomChildAt(Attribute, null, "version", index, value.toString());
	}

	public void replaceversionAt(String value, int index) throws Exception {
		replaceversionAt(new SchemaString(value), index);
	}

	public int getownerMinCount() {
		return 1;
	}

	public int getownerMaxCount() {
		return 1;
	}

	public int getownerCount() {
		return getDomChildCount(Attribute, null, "owner");
	}

	public boolean hasowner() {
		return hasDomChild(Attribute, null, "owner");
	}

	public SchemaString getownerAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Attribute, null, "owner", index)));
	}

	public SchemaString getowner() throws Exception {
		return getownerAt(0);
	}

	public void removeownerAt(int index) {
		removeDomChildAt(Attribute, null, "owner", index);
	}

	public void removeowner() {
		while (hasowner())
			removeownerAt(0);
	}

	public void addowner(SchemaString value) {
		setDomChild(Attribute, null, "owner", value.toString());
	}

	public void addowner(String value) throws Exception {
		addowner(new SchemaString(value));
	}

	public void insertownerAt(SchemaString value, int index) {
		insertDomChildAt(Attribute, null, "owner", index, value.toString());
	}

	public void insertownerAt(String value, int index) throws Exception {
		insertownerAt(new SchemaString(value), index);
	}

	public void replaceownerAt(SchemaString value, int index) {
		replaceDomChildAt(Attribute, null, "owner", index, value.toString());
	}

	public void replaceownerAt(String value, int index) throws Exception {
		replaceownerAt(new SchemaString(value), index);
	}

	public int getsupportContactMinCount() {
		return 1;
	}

	public int getsupportContactMaxCount() {
		return 1;
	}

	public int getsupportContactCount() {
		return getDomChildCount(Attribute, null, "supportContact");
	}

	public boolean hassupportContact() {
		return hasDomChild(Attribute, null, "supportContact");
	}

	public SchemaString getsupportContactAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Attribute, null, "supportContact", index)));
	}

	public SchemaString getsupportContact() throws Exception {
		return getsupportContactAt(0);
	}

	public void removesupportContactAt(int index) {
		removeDomChildAt(Attribute, null, "supportContact", index);
	}

	public void removesupportContact() {
		while (hassupportContact())
			removesupportContactAt(0);
	}

	public void addsupportContact(SchemaString value) {
		setDomChild(Attribute, null, "supportContact", value.toString());
	}

	public void addsupportContact(String value) throws Exception {
		addsupportContact(new SchemaString(value));
	}

	public void insertsupportContactAt(SchemaString value, int index) {
		insertDomChildAt(Attribute, null, "supportContact", index, value.toString());
	}

	public void insertsupportContactAt(String value, int index) throws Exception {
		insertsupportContactAt(new SchemaString(value), index);
	}

	public void replacesupportContactAt(SchemaString value, int index) {
		replaceDomChildAt(Attribute, null, "supportContact", index, value.toString());
	}

	public void replacesupportContactAt(String value, int index) throws Exception {
		replacesupportContactAt(new SchemaString(value), index);
	}

	public int getsupportPhoneMinCount() {
		return 1;
	}

	public int getsupportPhoneMaxCount() {
		return 1;
	}

	public int getsupportPhoneCount() {
		return getDomChildCount(Attribute, null, "supportPhone");
	}

	public boolean hassupportPhone() {
		return hasDomChild(Attribute, null, "supportPhone");
	}

	public SchemaString getsupportPhoneAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Attribute, null, "supportPhone", index)));
	}

	public SchemaString getsupportPhone() throws Exception {
		return getsupportPhoneAt(0);
	}

	public void removesupportPhoneAt(int index) {
		removeDomChildAt(Attribute, null, "supportPhone", index);
	}

	public void removesupportPhone() {
		while (hassupportPhone())
			removesupportPhoneAt(0);
	}

	public void addsupportPhone(SchemaString value) {
		setDomChild(Attribute, null, "supportPhone", value.toString());
	}

	public void addsupportPhone(String value) throws Exception {
		addsupportPhone(new SchemaString(value));
	}

	public void insertsupportPhoneAt(SchemaString value, int index) {
		insertDomChildAt(Attribute, null, "supportPhone", index, value.toString());
	}

	public void insertsupportPhoneAt(String value, int index) throws Exception {
		insertsupportPhoneAt(new SchemaString(value), index);
	}

	public void replacesupportPhoneAt(SchemaString value, int index) {
		replaceDomChildAt(Attribute, null, "supportPhone", index, value.toString());
	}

	public void replacesupportPhoneAt(String value, int index) throws Exception {
		replacesupportPhoneAt(new SchemaString(value), index);
	}

	public int getsupportEmailMinCount() {
		return 1;
	}

	public int getsupportEmailMaxCount() {
		return 1;
	}

	public int getsupportEmailCount() {
		return getDomChildCount(Attribute, null, "supportEmail");
	}

	public boolean hassupportEmail() {
		return hasDomChild(Attribute, null, "supportEmail");
	}

	public SchemaString getsupportEmailAt(int index) throws Exception {
		return new SchemaString(getDomNodeValue(getDomChildAt(Attribute, null, "supportEmail", index)));
	}

	public SchemaString getsupportEmail() throws Exception {
		return getsupportEmailAt(0);
	}

	public void removesupportEmailAt(int index) {
		removeDomChildAt(Attribute, null, "supportEmail", index);
	}

	public void removesupportEmail() {
		while (hassupportEmail())
			removesupportEmailAt(0);
	}

	public void addsupportEmail(SchemaString value) {
		setDomChild(Attribute, null, "supportEmail", value.toString());
	}

	public void addsupportEmail(String value) throws Exception {
		addsupportEmail(new SchemaString(value));
	}

	public void insertsupportEmailAt(SchemaString value, int index) {
		insertDomChildAt(Attribute, null, "supportEmail", index, value.toString());
	}

	public void insertsupportEmailAt(String value, int index) throws Exception {
		insertsupportEmailAt(new SchemaString(value), index);
	}

	public void replacesupportEmailAt(SchemaString value, int index) {
		replaceDomChildAt(Attribute, null, "supportEmail", index, value.toString());
	}

	public void replacesupportEmailAt(String value, int index) throws Exception {
		replacesupportEmailAt(new SchemaString(value), index);
	}
}
