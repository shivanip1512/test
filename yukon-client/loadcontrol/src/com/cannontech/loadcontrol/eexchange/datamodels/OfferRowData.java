package com.cannontech.loadcontrol.eexchange.datamodels;

/**
 * Insert the type's description here.
 * Creation date: (7/31/2001 10:52:47 AM)
 * @author: 
 */
import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeCustomerReply;
import com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyCustomer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeHourlyOffer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeOffer;
import com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision;
import com.cannontech.loadcontrol.data.LMProgramEnergyExchange;

public class OfferRowData 
{
	private LMProgramEnergyExchange energyExchangeProgram = null;
	private LMEnergyExchangeOffer ownerOffer = null;
	private LMEnergyExchangeOfferRevision currentRevision = null;
	
/**
 * OfferRowData constructor comment.
 */
public OfferRowData( LMProgramEnergyExchange exchPrg ) 
{
	super();

	setEnergyExchangeProgram(exchPrg);
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 2:34:50 PM)
 * @return boolean
 * @param o java.lang.Object
 */
public boolean equals(Object o) 
{
	return ( (o != null) &&
			   (o instanceof OfferRowData) &&
  			   ((OfferRowData)o).getEnergyExchangeProgram().equals(getEnergyExchangeProgram()) &&
			   ((OfferRowData)o).getOwnerOffer().equals(getOwnerOffer()) );
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 11:29:06 AM)
 * @return java.lang.Float
 */
public synchronized Double getCommittedTotal() 
{
	if( getEnergyExchangeProgram().getLoadControlGroupVector() == null )
		return new Double(0.0);

	double ret = 0.0;
	for( int i = 0; i < getEnergyExchangeProgram().getLoadControlGroupVector().size(); i++ )
	{
		LMEnergyExchangeCustomer cust = (LMEnergyExchangeCustomer)getEnergyExchangeProgram().getLoadControlGroupVector().get(i);

		for( int j = 0; j < cust.getEnergyExchangeCustomerReplies().size(); j++ )		
		{
			LMEnergyExchangeCustomerReply reply = (LMEnergyExchangeCustomerReply)cust.getEnergyExchangeCustomerReplies().get(j);

			for( int h = 0; h < reply.getEnergyExchangeHourlyCustomer().size(); h++ )
			{ //24 iterations
				LMEnergyExchangeHourlyCustomer hrCustomer = (LMEnergyExchangeHourlyCustomer)reply.getEnergyExchangeHourlyCustomer().get(h);
				ret += hrCustomer.getAmountCommitted().doubleValue();
			}
			
		}

		
	}

	
	return new Double(ret);
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 11:20:37 AM)
 * @return com.cannontech.loadcontrol.data.LMEnergyExchangeOfferRevision
 */
public synchronized LMEnergyExchangeOfferRevision getCurrentRevision() 
{
	return currentRevision;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 10:54:25 AM)
 * @return com.cannontech.loadcontrol.data.LMProgramEnergyExchange
 */
public com.cannontech.loadcontrol.data.LMProgramEnergyExchange getEnergyExchangeProgram() {
	return energyExchangeProgram;
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2001 2:12:19 PM)
 * @return java.lang.String
 */
public static String getInvalidStateMsg() 
{
	return "       EnergyExchangeProgram is null, CustomerReply is null or\n" +
	 		 "       the  Hourly Reply for the Customer has too many values.";
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 11:22:58 AM)
 * @return java.lang.String
 */
public String getOfferIDString() 
{
	return String.valueOf(getOwnerOffer().getOfferID()) + "-"+
			 String.valueOf(getCurrentRevision().getRevisionNumber());
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 10:57:47 AM)
 * @return com.cannontech.loadcontrol.data.LMEnergyExchangeOffer
 */
public com.cannontech.loadcontrol.data.LMEnergyExchangeOffer getOwnerOffer() {
	return ownerOffer;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 11:29:06 AM)
 * @return java.lang.Float
 */
public synchronized Double getTargetTotal() 
{
	if( getOwnerOffer().getEnergyExchangeOfferRevisions() == null )
		return new Double(0.0);

	double ret = 0.0;
	for( int j = 0; j < getCurrentRevision().getEnergyExchangeHourlyOffers().size(); j++ )		
	{ //24 iterations
		LMEnergyExchangeHourlyOffer hrOffer = (LMEnergyExchangeHourlyOffer)getCurrentRevision().getEnergyExchangeHourlyOffers().get(j);
		ret += hrOffer.getAmountRequested().doubleValue();
	}

	return new Double(ret);
}
/**
 * Insert the method's description here.
 * Creation date: (8/1/2001 1:28:34 PM)
 * @return boolean
 */
public boolean isValidState() 
{
	return getEnergyExchangeProgram() != null
			 && getOwnerOffer() != null
			 && getOwnerOffer().getEnergyExchangeOfferRevisions().size() <= 0;
}
/**
 * Insert the method's description here.
 * Creation date: (8/2/2001 10:32:54 AM)
 */
// set our current revision to the last revision in the Vector
private synchronized void setCurrentRevision() 
{
	currentRevision = (LMEnergyExchangeOfferRevision)getOwnerOffer().getEnergyExchangeOfferRevisions().get(
		       getOwnerOffer().getEnergyExchangeOfferRevisions().size()-1);

}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 10:54:25 AM)
 * @param newEnergyExchangeProgram com.cannontech.loadcontrol.data.LMProgramEnergyExchange
 */
public synchronized void setEnergyExchangeProgram(com.cannontech.loadcontrol.data.LMProgramEnergyExchange newEnergyExchangeProgram) {
	energyExchangeProgram = newEnergyExchangeProgram;
}
/**
 * Insert the method's description here.
 * Creation date: (7/31/2001 10:57:47 AM)
 * @param newOwnerOffer com.cannontech.loadcontrol.data.LMEnergyExchangeOffer
 */
public synchronized void setOwnerOffer(com.cannontech.loadcontrol.data.LMEnergyExchangeOffer newOwnerOffer) 
{
	for( int i = 0; i < getEnergyExchangeProgram().getEnergyExchangeOffers().size(); i++ )
	{
		//the owner offer object must occur in the EnergyExchangeProgram
		//   this is why we use the == operator instead of the .equals(Object) method
		if( getEnergyExchangeProgram().getEnergyExchangeOffers().get(i) == newOwnerOffer )
		{
			ownerOffer = newOwnerOffer;

			// set our current revision to the last revision in the Vector
			setCurrentRevision();
			return;
		}

	}

	throw new IllegalArgumentException("The OwnerOffer must exist as an offer in the EnergyExchangePrograms " +
		"OfferVector." );
}
}
