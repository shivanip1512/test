/*
 * Created on Oct 19, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package pagecode.capcontrol;

import com.cannontech.cbc.web.CapControlWebAnnex;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.yukon.cbc.SubBus;

import pagecode.PageCodeBase;
import javax.faces.component.UIColumn;
import javax.faces.component.html.HtmlOutputText;
import com.ibm.faces.component.html.HtmlOutputLinkEx;
import javax.faces.component.html.HtmlCommandLink;
import com.ibm.faces.component.html.HtmlScriptCollector;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.component.UIParameter;
/**
 * @author ryan
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Xsubs extends PageCodeBase {
	protected CapControlWebAnnex cbcAnnex;
	/** 
	* @author WebSphere Studio
	* @beanName cbcAnnex
	* @managed-bean true
	* @beanClass com.cannontech.cbc.web.CapControlWebAnnex
	*/
	public CapControlWebAnnex getCbcAnnex() {
		if (cbcAnnex == null) {
			cbcAnnex = new CapControlWebAnnex();
			cbcAnnex =
				(CapControlWebAnnex) getFacesContext()
					.getApplication()
					.createValueBinding("#{cbcAnnex}")
					.getValue(getFacesContext());
		}
		return cbcAnnex;
	}
	public void setCbcAnnex(CapControlWebAnnex cbcAnnex) {
		this.cbcAnnex = cbcAnnex;
	}
	protected UIColumn column0;
	protected HtmlOutputLinkEx 												id;
	protected UIColumn getColumn0() {
		if (column0 == null) {
			column0 = (UIColumn) findComponentInRoot("column0");
		}
		return column0;
	}
	protected HtmlCommandLink linkFeederListtt;
	protected HtmlCommandLink link1;
	protected HtmlScriptCollector scriptCollector1;
	protected HtmlOutputText textPending;
	protected HtmlOutputLinkEx linkExHome;
	protected HtmlOutputText textHome;
	protected HtmlOutputLinkEx linkExLogOff;
	protected HtmlOutputText textLogOff;
	protected HtmlForm formArea;
	protected HtmlOutputText textSubs;
	protected HtmlScriptCollector scriptCollector2;
	protected HtmlForm form1;
	protected HtmlDataTable tableSubs;
	protected HtmlOutputText text1;
	protected HtmlSelectOneMenu menuArea;
	protected UIColumn column1;

	protected HtmlOutputText text13;
	protected HtmlCommandLink linkFeederList;
	protected UIParameter param1;
	protected HtmlOutputText text2;
	protected HtmlOutputText text12;
	protected UIColumn column2;
	protected HtmlCommandLink linkSubCntrl;
	protected UIParameter param2;
	protected HtmlOutputText text3;
	protected UIColumn column3;
	protected HtmlOutputText text11;
	protected HtmlOutputText text4;
	protected UIColumn column4;
	protected HtmlOutputText text14;
	protected HtmlOutputText text5;
	protected UIColumn column5;
	protected HtmlOutputText text15;
	protected HtmlOutputText text6;
	protected UIColumn column6;
	protected HtmlOutputText text16;
	protected HtmlOutputText text7;
	protected UIColumn column7;
	protected HtmlOutputText text17;
	protected HtmlOutputText text8;
	protected UIColumn column8;
	protected HtmlOutputText text18;
	protected HtmlOutputText text9;
	protected UIColumn column9;
	protected HtmlOutputText text10;
	protected UIColumn column10;
	protected HtmlCommandLink getLinkFeederListtt() {
		if (linkFeederListtt == null) {
			linkFeederListtt =
				(HtmlCommandLink) findComponentInRoot("linkFeederListtt");
		}
		return linkFeederListtt;
	}
	protected HtmlCommandLink getLink1() {
		if (link1 == null) {
			link1 = (HtmlCommandLink) findComponentInRoot("link1");
		}
		return link1;
	}
	public String doLinkFeederListAction() {
		// Type Java code to handle command event here
		// Note, this code must return an object of type String (or null)
		
		//getRequestScope().put( "paoID", getParam1().getValue() );
		
		SubBus selBus = 
			getCbcAnnex().getSubTableModel().getSubBus(
				Integer.parseInt(getParam1().getValue().toString()) );

		CTILogger.info("   param1=" + getParam1().getValue() );
		CTILogger.info("   param2=" + getParam2().getValue() );
		CTILogger.info("   selBus=" + selBus );

		if( selBus == null )
			return null;

		getCbcAnnex().getFeederTableModel().setCurrentSubBus( selBus );

//		getCbcAnnex().getFeederTableModel().set
//		paoID = new Integer(strID);
//		subBus = subBusMdl.getSubBus(paoID.intValue());
//		feederMdl.setCurrentSubBus( subBus );		

		return "feeders";
	}
	public String doLinkSubCntrl() {
		// Type Java code to handle command event here
		// Note, this code must return an object of type String (or null)

		return "capcontrols";
	}
	protected HtmlScriptCollector getScriptCollector1() {
		if (scriptCollector1 == null) {
			scriptCollector1 =
				(HtmlScriptCollector) findComponentInRoot("scriptCollector1");
		}
		return scriptCollector1;
	}
	protected HtmlOutputText getTextPending() {
		if (textPending == null) {
			textPending = (HtmlOutputText) findComponentInRoot("textPending");
		}
		return textPending;
	}
	protected HtmlOutputLinkEx getLinkExHome() {
		if (linkExHome == null) {
			linkExHome = (HtmlOutputLinkEx) findComponentInRoot("linkExHome");
		}
		return linkExHome;
	}
	protected HtmlOutputText getTextHome() {
		if (textHome == null) {
			textHome = (HtmlOutputText) findComponentInRoot("textHome");
		}
		return textHome;
	}
	protected HtmlOutputLinkEx getLinkExLogOff() {
		if (linkExLogOff == null) {
			linkExLogOff =
				(HtmlOutputLinkEx) findComponentInRoot("linkExLogOff");
		}
		return linkExLogOff;
	}
	protected HtmlOutputText getTextLogOff() {
		if (textLogOff == null) {
			textLogOff = (HtmlOutputText) findComponentInRoot("textLogOff");
		}
		return textLogOff;
	}
	protected HtmlForm getFormArea() {
		if (formArea == null) {
			formArea = (HtmlForm) findComponentInRoot("formArea");
		}
		return formArea;
	}
	protected HtmlOutputText getTextSubs() {
		if (textSubs == null) {
			textSubs = (HtmlOutputText) findComponentInRoot("textSubs");
		}
		return textSubs;
	}
	protected HtmlScriptCollector getScriptCollector2() {
		if (scriptCollector2 == null) {
			scriptCollector2 =
				(HtmlScriptCollector) findComponentInRoot("scriptCollector2");
		}
		return scriptCollector2;
	}
	protected HtmlForm getForm1() {
		if (form1 == null) {
			form1 = (HtmlForm) findComponentInRoot("form1");
		}
		return form1;
	}
	protected HtmlDataTable getTableSubs() {
		if (tableSubs == null) {
			tableSubs = (HtmlDataTable) findComponentInRoot("tableSubs");
		}
		return tableSubs;
	}
	protected HtmlOutputText getText1() {
		if (text1 == null) {
			text1 = (HtmlOutputText) findComponentInRoot("text1");
		}
		return text1;
	}
	protected HtmlSelectOneMenu getMenuArea() {
		if (menuArea == null) {
			menuArea = (HtmlSelectOneMenu) findComponentInRoot("menuArea");
		}
		return menuArea;
	}
	protected UIColumn getColumn1() {
		if (column1 == null) {
			column1 = (UIColumn) findComponentInRoot("column1");
		}
		return column1;
	}
	protected HtmlOutputText getText13()
	{
		if (text13 == null)
		{
			text13 = (HtmlOutputText)findComponentInRoot("text13");
		}
		return text13;
	}
	protected HtmlCommandLink getLinkFeederList()
	{
		if (linkFeederList == null)
		{
			linkFeederList =
				(HtmlCommandLink)findComponentInRoot("linkFeederList");
		}
		return linkFeederList;
	}
	protected UIParameter getParam1()
	{
		if (param1 == null)
		{
			param1 = (UIParameter)findComponentInRoot("param1");
		}
		return param1;
	}
	protected HtmlOutputText getText2()
	{
		if (text2 == null)
		{
			text2 = (HtmlOutputText)findComponentInRoot("text2");
		}
		return text2;
	}
	protected HtmlOutputText getText12()
	{
		if (text12 == null)
		{
			text12 = (HtmlOutputText)findComponentInRoot("text12");
		}
		return text12;
	}
	protected UIColumn getColumn2()
	{
		if (column2 == null)
		{
			column2 = (UIColumn)findComponentInRoot("column2");
		}
		return column2;
	}
	protected HtmlCommandLink getLinkSubCntrl()
	{
		if (linkSubCntrl == null)
		{
			linkSubCntrl = (HtmlCommandLink)findComponentInRoot("linkSubCntrl");
		}
		return linkSubCntrl;
	}
	protected UIParameter getParam2()
	{
		if (param2 == null)
		{
			param2 = (UIParameter)findComponentInRoot("param2");
		}
		return param2;
	}
	protected HtmlOutputText getText3()
	{
		if (text3 == null)
		{
			text3 = (HtmlOutputText)findComponentInRoot("text3");
		}
		return text3;
	}
	protected UIColumn getColumn3()
	{
		if (column3 == null)
		{
			column3 = (UIColumn)findComponentInRoot("column3");
		}
		return column3;
	}
	protected HtmlOutputText getText11()
	{
		if (text11 == null)
		{
			text11 = (HtmlOutputText)findComponentInRoot("text11");
		}
		return text11;
	}
	protected HtmlOutputText getText4()
	{
		if (text4 == null)
		{
			text4 = (HtmlOutputText)findComponentInRoot("text4");
		}
		return text4;
	}
	protected UIColumn getColumn4()
	{
		if (column4 == null)
		{
			column4 = (UIColumn)findComponentInRoot("column4");
		}
		return column4;
	}
	protected HtmlOutputText getText14()
	{
		if (text14 == null)
		{
			text14 = (HtmlOutputText)findComponentInRoot("text14");
		}
		return text14;
	}
	protected HtmlOutputText getText5()
	{
		if (text5 == null)
		{
			text5 = (HtmlOutputText)findComponentInRoot("text5");
		}
		return text5;
	}
	protected UIColumn getColumn5()
	{
		if (column5 == null)
		{
			column5 = (UIColumn)findComponentInRoot("column5");
		}
		return column5;
	}
	protected HtmlOutputText getText15()
	{
		if (text15 == null)
		{
			text15 = (HtmlOutputText)findComponentInRoot("text15");
		}
		return text15;
	}
	protected HtmlOutputText getText6()
	{
		if (text6 == null)
		{
			text6 = (HtmlOutputText)findComponentInRoot("text6");
		}
		return text6;
	}
	protected UIColumn getColumn6()
	{
		if (column6 == null)
		{
			column6 = (UIColumn)findComponentInRoot("column6");
		}
		return column6;
	}
	protected HtmlOutputText getText16()
	{
		if (text16 == null)
		{
			text16 = (HtmlOutputText)findComponentInRoot("text16");
		}
		return text16;
	}
	protected HtmlOutputText getText7()
	{
		if (text7 == null)
		{
			text7 = (HtmlOutputText)findComponentInRoot("text7");
		}
		return text7;
	}
	protected UIColumn getColumn7()
	{
		if (column7 == null)
		{
			column7 = (UIColumn)findComponentInRoot("column7");
		}
		return column7;
	}
	protected HtmlOutputText getText17()
	{
		if (text17 == null)
		{
			text17 = (HtmlOutputText)findComponentInRoot("text17");
		}
		return text17;
	}
	protected HtmlOutputText getText8()
	{
		if (text8 == null)
		{
			text8 = (HtmlOutputText)findComponentInRoot("text8");
		}
		return text8;
	}
	protected UIColumn getColumn8()
	{
		if (column8 == null)
		{
			column8 = (UIColumn)findComponentInRoot("column8");
		}
		return column8;
	}
	protected HtmlOutputText getText18()
	{
		if (text18 == null)
		{
			text18 = (HtmlOutputText)findComponentInRoot("text18");
		}
		return text18;
	}
	protected HtmlOutputText getText9()
	{
		if (text9 == null)
		{
			text9 = (HtmlOutputText)findComponentInRoot("text9");
		}
		return text9;
	}
	protected UIColumn getColumn9()
	{
		if (column9 == null)
		{
			column9 = (UIColumn)findComponentInRoot("column9");
		}
		return column9;
	}
	protected HtmlOutputText getText10()
	{
		if (text10 == null)
		{
			text10 = (HtmlOutputText)findComponentInRoot("text10");
		}
		return text10;
	}
	protected UIColumn getColumn10()
	{
		if (column10 == null)
		{
			column10 = (UIColumn)findComponentInRoot("column10");
		}
		return column10;
	}
}