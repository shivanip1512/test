package com.cannontech.loadcontrol.data;

/**
 */
import com.roguewave.tools.v2_0.Comparator;
import com.roguewave.vsj.DefineCollectable;
import com.roguewave.vsj.streamer.SimpleMappings;

public class DefColl_LMCICustomerBase implements DefineCollectable
{
/**
 * DefineCollectableSchedule constructor comment.
 */
public DefColl_LMCICustomerBase() {
	super();
}

/**
 * getComparator method comment.
 */
public com.roguewave.tools.v2_0.Comparator getComparator() 
{
	return new Comparator() 
	{
		public int compare(Object x, Object y) 
		{
			return (int) (((LMCICustomerBase)x).getCustomerID().longValue() - ((LMCICustomerBase)y).getCustomerID().longValue() );
		}
	};
	
}

/**
 * This method is called from CollectableStreamer to create a new instance
 * of Schedule.
 */
public Object create(com.roguewave.vsj.VirtualInputStream vstr) throws java.io.IOException
{
	return new LMCurtailCustomer();
}
/**
 * getCxxClassId method comment.
 */
public int getCxxClassId() {
	Exception e = new Exception(this.getClass().getName() + ".getCxxClassId() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	return -1;
}
/**
 * getCxxStringId method comment.
 */
public String getCxxStringId() {
	Exception e = new Exception(this.getClass().getName() + ".getCxxStringId() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	return DefineCollectable.NO_STRINGID;
}
/**
 * getJavaClass method comment.
 */
public Class getJavaClass() {
	Exception e = new Exception(this.getClass().getName() + ".getJavaClass() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
	return LMCICustomerBase.class;
}
/**
 * restoreGuts method comment.
 */
public void restoreGuts(Object obj, com.roguewave.vsj.VirtualInputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException 
{
	LMCICustomerBase custBase = (LMCICustomerBase) obj;
	
	Long customerID = new Long(vstr.extractLong());
	String companyName = (String) vstr.restoreObject(SimpleMappings.CString);
	Double customerDemandLevel = new Double(vstr.extractDouble());
	Double curtailAmount = new Double(vstr.extractDouble());
	String curtailmentAgreement = (String) vstr.restoreObject(SimpleMappings.CString);
	String timeZone = (String) vstr.restoreObject(SimpleMappings.CString);
	Long customerOrder = new Long(vstr.extractLong());
	
	custBase.setCustomerID(customerID);
	custBase.setCompanyName(companyName);
	custBase.setCustomerDemandLevel(customerDemandLevel);
	custBase.setCurtailAmount(curtailAmount);
	custBase.setCurtailmentAgreement(curtailmentAgreement);
	custBase.setTimeZone(timeZone);
	custBase.setCustomerOrder(customerOrder);
}
/**
 * saveGuts method comment.
 */
public void saveGuts(Object obj, com.roguewave.vsj.VirtualOutputStream vstr, com.roguewave.vsj.CollectableStreamer polystr) throws java.io.IOException {
	Exception e = new Exception(this.getClass().getName() + ".saveGuts() should Never be called");
	com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
}
}
