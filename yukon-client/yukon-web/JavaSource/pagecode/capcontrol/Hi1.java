/*
 * Created on Oct 27, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package pagecode.capcontrol;

import pagecode.PageCodeBase;

import com.ibm.faces.component.html.HtmlScriptCollector;
import javax.faces.component.html.HtmlOutputText;
import com.ibm.faces.component.html.HtmlOutputLinkEx;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlSelectOneMenu;
import com.cannontech.cbc.web.CapControlWebAnnex;
import com.cannontech.database.cache.DefaultDatabaseCache;
/**
 * @author ryan
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Hi1 extends PageCodeBase {

	protected HtmlScriptCollector scriptCollector1;
	protected HtmlOutputText textPending;
	protected HtmlOutputLinkEx linkExHome;
	protected HtmlOutputText textHome;
	protected HtmlOutputLinkEx linkExLogOff;
	protected HtmlOutputText textLogOff;
	protected HtmlForm formArea;
	protected HtmlOutputText textSubs;
	protected HtmlSelectOneMenu menuArea;
	protected CapControlWebAnnex cbcAnnex;
	protected DefaultDatabaseCache DBCache;
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
	protected HtmlSelectOneMenu getMenuArea() {
		if (menuArea == null) {
			menuArea = (HtmlSelectOneMenu) findComponentInRoot("menuArea");
		}
		return menuArea;
	}
	/** 
	* @author WebSphere Studio
	* @beanName cbcAnnex
	* @managed-bean true
	* @beanClass com.cannontech.cbc.web.CapControlWebAnnex
	*/
	public CapControlWebAnnex getCbcAnnex()
	{
		if (cbcAnnex == null)
		{
			cbcAnnex = new CapControlWebAnnex();
			cbcAnnex =
				(CapControlWebAnnex)getFacesContext()
					.getApplication()
					.createValueBinding("#{cbcAnnex}")
					.getValue(getFacesContext());
		}
		return cbcAnnex;
	}
	public void setCbcAnnex(CapControlWebAnnex cbcAnnex)
	{
		this.cbcAnnex = cbcAnnex;
	}
	/** 
	* @author WebSphere Studio
	* @beanName DBCache
	* @managed-bean true
	* @beanClass com.cannontech.database.cache.DefaultDatabaseCache
	*/
	public DefaultDatabaseCache getDBCache()
	{
		if (DBCache == null)
		{
			DBCache = new DefaultDatabaseCache();
			DBCache =
				(DefaultDatabaseCache)getFacesContext()
					.getApplication()
					.createValueBinding("#{DBCache}")
					.getValue(getFacesContext());
		}
		return DBCache;
	}
	public void setDBCache(DefaultDatabaseCache DBCache)
	{
		this.DBCache = DBCache;
	}
}