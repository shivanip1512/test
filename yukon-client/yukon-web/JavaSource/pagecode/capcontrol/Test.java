package pagecode.capcontrol;

import pagecode.PageCodeBase;
import com.ibm.faces.component.html.HtmlScriptCollector;
import javax.faces.component.html.HtmlForm;
import com.ibm.faces.bf.component.html.HtmlTree;
import com.cannontech.database.cache.DefaultDatabaseCache;
import com.ibm.faces.bf.component.html.HtmlClientData;
import com.ibm.faces.bf.component.html.HtmlTreeNodeAttr;
/**
 * @author ryan
 *
 * Comment
 */
public class Test extends PageCodeBase {

	protected HtmlScriptCollector scriptCollector1;
	protected HtmlForm form1;
	protected HtmlTree tree1;
	protected DefaultDatabaseCache DBCache;
	protected HtmlClientData clientData1;
	protected HtmlTreeNodeAttr treenodeattr1;
	protected HtmlScriptCollector getScriptCollector1()
	{
		if (scriptCollector1 == null)
		{
			scriptCollector1 =
				(HtmlScriptCollector)findComponentInRoot("scriptCollector1");
		}
		return scriptCollector1;
	}
	protected HtmlForm getForm1()
	{
		if (form1 == null)
		{
			form1 = (HtmlForm)findComponentInRoot("form1");
		}
		return form1;
	}
	protected HtmlTree getTree1()
	{
		if (tree1 == null)
		{
			tree1 = (HtmlTree)findComponentInRoot("tree1");
		}
		return tree1;
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
	protected HtmlClientData getClientData1()
	{
		if (clientData1 == null)
		{
			clientData1 = (HtmlClientData)findComponentInRoot("clientData1");
		}
		return clientData1;
	}
	protected HtmlTreeNodeAttr getTreenodeattr1()
	{
		if (treenodeattr1 == null)
		{
			treenodeattr1 =
				(HtmlTreeNodeAttr)findComponentInRoot("treenodeattr1");
		}
		return treenodeattr1;
	}
}