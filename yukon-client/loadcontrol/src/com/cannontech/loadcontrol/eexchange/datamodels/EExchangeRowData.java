package com.cannontech.loadcontrol.eexchange.datamodels;

/**
 * Insert the type's description here.
 * Creation date: (7/31/2001 1:30:29 PM)
 * @author: 
 */
import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply;
import com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer;

public class EExchangeRowData 
{
	private LMEnergyExchangeCustomer customer = null;
	private LMEnergyExchangeCustomerReply ownerReply = null;
/**
 * EExchangeRowData constructor comment.
 */
public EExchangeRowData( LMEnergyExchangeCustomer newCustomer ) 
{
	super();
	setCustomer( newCustomer );
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 1:32:34 PM)
 * @return com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer
 */
public com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer getCustomer() {
	return customer;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2001 2:10:17 PM)
 * @return java.lang.String
 */
public static String getInvalidStateMsg() 
{
	return "       CustomerReplyVector is null or\n" +
			 "       the Hourly Reply for the Customer has too many values.";
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 11:22:58 AM)
 * @return java.lang.String
 */
public synchronized String getOfferIDString() 
{
	return String.valueOf(getOwnerReply().getOfferID()) + "-"+
			 String.valueOf(getOwnerReply().getRevisionNumber());
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 1:32:34 PM)
 * @return com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply
 */
public synchronized com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply getOwnerReply() {
	return ownerReply;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 1:47:59 PM)
 * @return java.lang.Double
 */
public synchronized Double getTotal() 
{
	if( getCustomer() == null
		 || getOwnerReply() == null
		 || getOwnerReply().getEnergyExchangeHourlyCustomer() == null )
		return new Double(0.0);

	double total = 0;
	for( int i = 0; i < getOwnerReply().getEnergyExchangeHourlyCustomer().size(); i++ )
	{
		LMEnergyExchangeHourlyCustomer hrCust = (LMEnergyExchangeHourlyCustomer)
								getOwnerReply().getEnergyExchangeHourlyCustomer().get(i);

		total += hrCust.getAmountCommitted().doubleValue();
	}
		
	return new Double(total);
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2001 1:28:34 PM)
 * @return boolean
 */
public boolean isValidState() 
{
	return getOwnerReply() != null
			 && getOwnerReply().getEnergyExchangeHourlyCustomer().size() <= 23;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 1:32:34 PM)
 * @param newCustomer com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer
 */
public synchronized void setCustomer(com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer newCustomer) {
	customer = newCustomer;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 1:32:34 PM)
 * @param newOwnerReply com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply
 */
public synchronized void setOwnerReply(com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply newOwnerReply) 
{
	for( int i = 0; i < getCustomer().getEnergyExchangeCustomerReplies().size(); i++ )
	{
		//the owner reply object must occur in the EnergyExchangeCustomer vector
		//   this is why we use the == operator instead of the .equals(Object) method
		if( getCustomer().getEnergyExchangeCustomerReplies().get(i) == newOwnerReply )
		{
			ownerReply = newOwnerReply;
			return;
		}
	}

	throw new IllegalArgumentException("The OwnerCustomerReply must exist as a reply in the LMEnergyExchangeCustomer " +
		"Vector." );
}
}
