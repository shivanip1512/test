/*
 * Created on Oct 14, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package pagecode.capcontrol;

import javax.faces.event.ValueChangeEvent;
import pagecode.PageCodeBase;
import com.cannontech.cbc.web.CapControlWebAnnex;
import com.cannontech.servlet.CBCConnServlet;
import com.cannontech.yukon.cbc.CBCClientConnection;
import javax.faces.component.html.HtmlOutputText;
import com.ibm.faces.component.html.HtmlOutputLinkEx;
import com.ibm.faces.component.html.HtmlScriptCollector;
import com.cannontech.cbc.CBCDisplay;
import javax.faces.component.html.HtmlForm;
import javax.faces.component.html.HtmlSelectOneMenu;
import javax.faces.context.FacesContext;
/**
 * @author ryan
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class Cbctemplate extends PageCodeBase {
	protected CapControlWebAnnex cbcAnnex;
	protected HtmlOutputText textPending;
	protected HtmlOutputLinkEx linkExHome;
	protected HtmlOutputText textHome;
	protected HtmlOutputLinkEx linkExLogOff;
	protected HtmlOutputText textLogOff;
	protected HtmlScriptCollector scriptCollector1;
	protected CBCDisplay cbcDisplay;
	protected HtmlForm formArea;
	protected HtmlOutputText textSubs;
	protected HtmlSelectOneMenu menuArea;
	public void handleMenuAreaValueChange(ValueChangeEvent valueChangedEvent) {
		// Type Java code to handle value changed event here
		// Note,  valueChangeEvent contains new and old values
		
		getCbcAnnex().getSubTableModel().setFilter( valueChangedEvent.getNewValue().toString() );
		FacesContext fc = getFacesContext().getCurrentInstance();
		
		//send the user to the subs page
		fc.getApplication().getNavigationHandler().handleNavigation(
			fc, null, "subs" );
		
		return;
	}
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
					
			//if we are just starting, we must have an area
			if( getCbcAnnex().getSubTableModel().getAreaNames().size() > 0
				&& getSessionScope().get("area") == null)
			{
				getSessionScope().put( "area", getCbcAnnex().getSubTableModel().getAreaNames().get(0).toString() );
				getCbcAnnex().getSubTableModel().setFilter( getSessionScope().get("area").toString() );				
			}
		}
		return cbcAnnex;
	}
	public void setCbcAnnex(CapControlWebAnnex cbcAnnex) {
		this.cbcAnnex = cbcAnnex;
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
	protected HtmlScriptCollector getScriptCollector1() {
		if (scriptCollector1 == null) {
			scriptCollector1 =
				(HtmlScriptCollector) findComponentInRoot("scriptCollector1");
		}
		return scriptCollector1;
	}
	/** 
	* @author WebSphere Studio
	* @beanName cbcDisplay
	* @managed-bean true
	* @beanClass com.cannontech.cbc.CBCDisplay
	*/
	public CBCDisplay getCbcDisplay() {
		if (cbcDisplay == null) {
			cbcDisplay = new CBCDisplay();
			cbcDisplay =
				(CBCDisplay) getFacesContext()
					.getApplication()
					.createValueBinding("#{cbcDisplay}")
					.getValue(getFacesContext());
		}
		return cbcDisplay;
	}
	public void setCbcDisplay(CBCDisplay cbcDisplay) {
		this.cbcDisplay = cbcDisplay;
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
}