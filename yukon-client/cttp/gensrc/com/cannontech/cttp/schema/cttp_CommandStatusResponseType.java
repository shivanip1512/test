/**
 * cttp_CommandStatusResponseType.java
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


public class cttp_CommandStatusResponseType extends com.altova.xml.Node {
	public cttp_CommandStatusResponseType() {
		super();
	}

	public cttp_CommandStatusResponseType(cttp_CommandStatusResponseType node) {
		super(node);
	}

	public cttp_CommandStatusResponseType(org.w3c.dom.Node node) {
		super(node);
	}

	public cttp_CommandStatusResponseType(org.w3c.dom.Document doc) {
		super(doc);
	}
	public void adjustPrefix() {
		int count;
		count = getDomChildCount(Element, null, "cttp-Originator");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "cttp-Originator", i);
			internalAdjustPrefix(tmpNode, false);
			new cttp_OriginatorType(tmpNode).adjustPrefix();
		}
		count = getDomChildCount(Element, null, "cttp-Command");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "cttp-Command", i);
			internalAdjustPrefix(tmpNode, false);
			new cttp_CommandType(tmpNode).adjustPrefix();
		}
		count = getDomChildCount(Element, null, "cttp-CommandStatus");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "cttp-CommandStatus", i);
			internalAdjustPrefix(tmpNode, false);
			new cttp_CommandStatusType(tmpNode).adjustPrefix();
		}
		count = getDomChildCount(Element, null, "cttp-Failure");
		for (int i = 0; i < count; i++) {
			org.w3c.dom.Node tmpNode = getDomChildAt(Element, null, "cttp-Failure", i);
			internalAdjustPrefix(tmpNode, false);
			new cttp_FailureType(tmpNode).adjustPrefix();
		}
	}

	public int getcttp_OriginatorMinCount() {
		return 1;
	}

	public int getcttp_OriginatorMaxCount() {
		return 1;
	}

	public int getcttp_OriginatorCount() {
		return getDomChildCount(Element, null, "cttp-Originator");
	}

	public boolean hascttp_Originator() {
		return hasDomChild(Element, null, "cttp-Originator");
	}

	public cttp_OriginatorType getcttp_OriginatorAt(int index) throws Exception {
		return new cttp_OriginatorType(getDomChildAt(Element, null, "cttp-Originator", index));
	}

	public cttp_OriginatorType getcttp_Originator() throws Exception {
		return getcttp_OriginatorAt(0);
	}

	public void removecttp_OriginatorAt(int index) {
		removeDomChildAt(Element, null, "cttp-Originator", index);
	}

	public void removecttp_Originator() {
		while (hascttp_Originator())
			removecttp_OriginatorAt(0);
	}

	public void addcttp_Originator(cttp_OriginatorType value) {
		appendDomElement(null, "cttp-Originator", value);
	}

	public void insertcttp_OriginatorAt(cttp_OriginatorType value, int index) {
		insertDomElementAt(null, "cttp-Originator", index, value);
	}

	public void replacecttp_OriginatorAt(cttp_OriginatorType value, int index) {
		replaceDomElementAt(null, "cttp-Originator", index, value);
	}

	public int getcttp_CommandMinCount() {
		return 1;
	}

	public int getcttp_CommandMaxCount() {
		return 1;
	}

	public int getcttp_CommandCount() {
		return getDomChildCount(Element, null, "cttp-Command");
	}

	public boolean hascttp_Command() {
		return hasDomChild(Element, null, "cttp-Command");
	}

	public cttp_CommandType getcttp_CommandAt(int index) throws Exception {
		return new cttp_CommandType(getDomChildAt(Element, null, "cttp-Command", index));
	}

	public cttp_CommandType getcttp_Command() throws Exception {
		return getcttp_CommandAt(0);
	}

	public void removecttp_CommandAt(int index) {
		removeDomChildAt(Element, null, "cttp-Command", index);
	}

	public void removecttp_Command() {
		while (hascttp_Command())
			removecttp_CommandAt(0);
	}

	public void addcttp_Command(cttp_CommandType value) {
		appendDomElement(null, "cttp-Command", value);
	}

	public void insertcttp_CommandAt(cttp_CommandType value, int index) {
		insertDomElementAt(null, "cttp-Command", index, value);
	}

	public void replacecttp_CommandAt(cttp_CommandType value, int index) {
		replaceDomElementAt(null, "cttp-Command", index, value);
	}

	public int getcttp_CommandStatusMinCount() {
		return 1;
	}

	public int getcttp_CommandStatusMaxCount() {
		return 1;
	}

	public int getcttp_CommandStatusCount() {
		return getDomChildCount(Element, null, "cttp-CommandStatus");
	}

	public boolean hascttp_CommandStatus() {
		return hasDomChild(Element, null, "cttp-CommandStatus");
	}

	public cttp_CommandStatusType getcttp_CommandStatusAt(int index) throws Exception {
		return new cttp_CommandStatusType(getDomChildAt(Element, null, "cttp-CommandStatus", index));
	}

	public cttp_CommandStatusType getcttp_CommandStatus() throws Exception {
		return getcttp_CommandStatusAt(0);
	}

	public void removecttp_CommandStatusAt(int index) {
		removeDomChildAt(Element, null, "cttp-CommandStatus", index);
	}

	public void removecttp_CommandStatus() {
		while (hascttp_CommandStatus())
			removecttp_CommandStatusAt(0);
	}

	public void addcttp_CommandStatus(cttp_CommandStatusType value) {
		appendDomElement(null, "cttp-CommandStatus", value);
	}

	public void insertcttp_CommandStatusAt(cttp_CommandStatusType value, int index) {
		insertDomElementAt(null, "cttp-CommandStatus", index, value);
	}

	public void replacecttp_CommandStatusAt(cttp_CommandStatusType value, int index) {
		replaceDomElementAt(null, "cttp-CommandStatus", index, value);
	}

	public int getcttp_FailureMinCount() {
		return 1;
	}

	public int getcttp_FailureMaxCount() {
		return 1;
	}

	public int getcttp_FailureCount() {
		return getDomChildCount(Element, null, "cttp-Failure");
	}

	public boolean hascttp_Failure() {
		return hasDomChild(Element, null, "cttp-Failure");
	}

	public cttp_FailureType getcttp_FailureAt(int index) throws Exception {
		return new cttp_FailureType(getDomChildAt(Element, null, "cttp-Failure", index));
	}

	public cttp_FailureType getcttp_Failure() throws Exception {
		return getcttp_FailureAt(0);
	}

	public void removecttp_FailureAt(int index) {
		removeDomChildAt(Element, null, "cttp-Failure", index);
	}

	public void removecttp_Failure() {
		while (hascttp_Failure())
			removecttp_FailureAt(0);
	}

	public void addcttp_Failure(cttp_FailureType value) {
		appendDomElement(null, "cttp-Failure", value);
	}

	public void insertcttp_FailureAt(cttp_FailureType value, int index) {
		insertDomElementAt(null, "cttp-Failure", index, value);
	}

	public void replacecttp_FailureAt(cttp_FailureType value, int index) {
		replaceDomElementAt(null, "cttp-Failure", index, value);
	}
}
