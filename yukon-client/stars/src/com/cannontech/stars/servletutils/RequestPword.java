package com.cannontech.stars.servletutils;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiProperties;
import com.cannontech.database.cache.functions.ContactFuncs;
import com.cannontech.database.cache.functions.EnergyCompanyFuncs;
import com.cannontech.database.cache.functions.YukonUserFuncs;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.stars.web.servlet.SOAPServer;
import com.cannontech.tools.email.EmailMessage;

/**
 * @author rneuharth
 *
 * Used to handle requests for passwords and send the requests to 
 * the email address of the Energy Company that owns this Contact, YukonUser
 * or Customer. If we can not determine which Energy Company to use, we send
 * the request to the MasterMail, supplied in:
 * 
 *   CtiProperties.KEY_LOGIN_PAGE_HELP_EMAIL
 *  
 * NOTE: Can't put this class in common since it uses STARS
 */
public class RequestPword
{
	private static final String CR = System.getProperty("line.separator");
	private String INVALID_URI = null;
	private String SUCCESS_URI = null;
	
	private String userName = null;
	private String email = null;
	private String fName = null;
	private String lName = null;
	private String accNum = null;		
	private String masterMail = null;
	private String notes = null;
	
	private final String[] allParams;
	private Vector foundData = new Vector(8);


	private transient int state = RET_FAILED;
	private transient String resultString = "";
	private transient String subject = "Password Request";


	public static final int RET_FAILED = 0;
	public static final int RET_SUCCESS = 1;


	/**
	 * 
	 */
	public RequestPword( String userName_, String email_, String fName_, String lName_, String accNum_ )
	{
		super();
		userName = userName_;
		email = email_;
		fName = fName_;
		lName = lName_;
		accNum = accNum_;
		
		allParams = new String[] { userName, email, fName, lName, accNum };


		Object o = CtiProperties.getInstance().get(CtiProperties.KEY_LOGIN_PAGE_HELP_EMAIL);
		if( o != null )
			masterMail = o.toString();
	}
	
	public int getResultState()
	{
		return state;
	}

	public String getResultString()
	{
		return resultString;
	}


	public void doRequest()
	{

		try
		{
			//unique system wide email address
			if( email != null )
			{
				//we may continue after this, remove all the stored data
				foundData.clear();
				
				LiteContact lc = ContactFuncs.getContactByEmailNotif( email );
				if( lc == null )
					setState( RET_FAILED, "Email not found, try again" );
				else
				{
					foundData.add( " Contact Name: " + lc.getContFirstName() + " " + lc.getContLastName() );
					foundData.add( " User Name: " + YukonUserFuncs.getLiteYukonUser(lc.getLoginID()).getUsername() );

					LiteEnergyCompany[] cmps = processContact( lc );
					processEnergyCompanies( cmps );
				}
			}
				
			//unique system wide user name
			if( state != RET_SUCCESS && userName != null )
			{
				//we may continue after this, remove all the stored data
				foundData.clear();
				
				LiteYukonUser user = YukonUserFuncs.getLiteYukonUser( userName );
				if( user == null )
					setState( RET_FAILED, "User Name not found, try again" );
				else
				{
					foundData.add( " User Name: " + user.getUsername() );					

					LiteContact lc = YukonUserFuncs.getLiteContact( user.getUserID() );
					if( lc == null )
						setState( RET_FAILED, "User Name not found, try again" );
	
					foundData.add( " Contact Name: " + lc.getContFirstName() + " " + lc.getContLastName() );					

					LiteEnergyCompany[] cmps = processContact( lc );					
					processEnergyCompanies( cmps );
				}
			}
				
			//uses STARS functionality
			if( state != RET_SUCCESS && accNum != null )
			{
				//we may continue after this, remove all the stored data
				foundData.clear();
				
				List engrComps = SOAPServer.getAllEnergyCompanies();
				LiteStarsEnergyCompany eComp = null;
				ArrayList allCustAccts = new ArrayList(8);

				for( int i = 0; i < engrComps.size(); i++ )
				{
					LiteStarsEnergyCompany lsec = (LiteStarsEnergyCompany)engrComps.get(i);
					
					LiteStarsCustAccountInformation lCustInfo =
							lsec.searchByAccountNumber( accNum );
					
					if( lCustInfo != null )
					{
						allCustAccts.add( lCustInfo );
						eComp = lsec;
					}
				}
					
				if( allCustAccts.size() == 1 )
				{
					//only 1 found, this is good
					LiteStarsCustAccountInformation lCustInf = 
						(LiteStarsCustAccountInformation)allCustAccts.get(0);

					LiteContact lc = (LiteContact)
						ContactFuncs.getContact( lCustInf.getCustomer().getPrimaryContactID() );

					LiteYukonUser user =
						YukonUserFuncs.getLiteYukonUser( lc.getLoginID() );
					
					foundData.add( " User Name: " + user.getUsername() );					
					foundData.add( " Contact Name: " + lc.getContFirstName() + " " + lc.getContLastName() );					
					
					//we must get the Yukon lite energy company for the stars lite energy company
					LiteEnergyCompany lEnrgy =
						EnergyCompanyFuncs.getEnergyCompany( eComp.getEnergyCompanyID().intValue() );

					processEnergyCompanies( new LiteEnergyCompany[] { lEnrgy } );
				}
				else if( allCustAccts.size() < 1 )
				{
					setState( RET_FAILED, "Account Number not found, try again" );					
				}
				else
				{
					setState( RET_FAILED, "More than one account number found, forwarding request onto the WebMaster" );
					subject = "WebMaster: " + subject;
					foundData.add( " " + getResultString() );
					foundData.add( " Number of Account Numbers for this Account: " + allCustAccts.size() );
					for( int i = 0; i < allCustAccts.size(); i++ )
					{
						LiteStarsCustAccountInformation lCstInfo =
							(LiteStarsCustAccountInformation)allCustAccts.get(i);
						
						foundData.add( "   Account # " + i + ": " + lCstInfo.getCustomerAccount().getAccountNumber() );
					}

					sendEmails( new String[] { masterMail }, genBody() );
				}


			}
				
			if( state != RET_SUCCESS && fName != null )
			{
				//we may continue after this, remove all the stored data
				foundData.clear();
				
				LiteContact[] lConts = ContactFuncs.getContactsByFName( fName );
				if( lConts.length == 1 )
				{
					foundData.add( " Contact Name: " + lConts[0].getContFirstName() + " " + lConts[0].getContLastName() );					
					foundData.add( " User Name: " + YukonUserFuncs.getLiteYukonUser(lConts[0].getLoginID()).getUsername() );

					LiteEnergyCompany[] cmps = processContact( lConts[0] );					
					processEnergyCompanies( cmps );
				}
				else if( lConts.length < 1 )
				{
					setState( RET_FAILED, "First name not found, try again" );
				}
				else //many contacts found to have this same first name
				{
					//if we also have a last name, try to match BOTH names
					if( lName != null )
					{
						for( int i = 0; i < lConts.length; i++ )
						{
							if( lName.equalsIgnoreCase(lConts[i].getContLastName()) )
							{
								foundData.add( " Contact Name: " + lConts[i].getContFirstName() + " " + lConts[i].getContLastName() );					
								foundData.add( " User Name: " + YukonUserFuncs.getLiteYukonUser(lConts[i].getLoginID()).getUsername() );
								
								LiteEnergyCompany[] cmps = processContact( lConts[i] );
								processEnergyCompanies( cmps );									
							}
						}
					}
					else
					{
						setState( RET_FAILED, "More than one first name found, forwarding request onto the WebMaster" );
						subject = "WebMaster: " + subject;
						foundData.add( " " + getResultString() );
						foundData.add( " Number of Contacts for this First Name: " + lConts.length );
						for( int i = 0; i < lConts.length; i++ )
							foundData.add( "   Contacts " + i + ": " + lConts[i].toString() );

						sendEmails( new String[] { masterMail }, genBody() );
					}
						
				}
					
					
			}
				
			if( state != RET_SUCCESS && lName != null )
			{
				//we may continue after this, remove all the stored data
				foundData.clear();
				
				LiteContact[] lConts = ContactFuncs.getContactsByLName( lName );
				if( lConts.length == 1 )
				{
					foundData.add( " Contact Name: " + lConts[0].getContFirstName() + " " + lConts[0].getContLastName() );					
					foundData.add( " User Name: " + YukonUserFuncs.getLiteYukonUser(lConts[0].getLoginID()).getUsername() );
					
					LiteEnergyCompany[] cmps = processContact( lConts[0] );					
					processEnergyCompanies( cmps );						
				}
				else if( lConts.length < 1 )
				{
					setState( RET_FAILED, "Last name not found, try again" );
				}
				else //many contacts found to have this same first name
				{
					//if we also have a first name, try to match BOTH names
					if( fName != null )
					{
						for( int i = 0; i < lConts.length; i++ )
						{
							if( fName.equalsIgnoreCase(lConts[i].getContFirstName()) )
							{
								foundData.add( " Contact Name: " + lConts[i].getContFirstName() + " " + lConts[i].getContLastName() );					
								foundData.add( " User Name: " + YukonUserFuncs.getLiteYukonUser(lConts[i].getLoginID()).getUsername() );

								LiteEnergyCompany[] cmps = processContact( lConts[i] );					
								processEnergyCompanies( cmps );									
							}
						}
					}
					else
					{						
						setState( RET_FAILED, "More than one last name found, forwarding request onto the WebMaster" );
						subject = "WebMaster: " + subject;
						foundData.add( " " + getResultString() );
						foundData.add( " Number of Contacts for this Last Name: " + lConts.length );
						for( int i = 0; i < lConts.length; i++ )
							foundData.add( "   Contacts " + i + ": " + lConts[i].toString() );

						sendEmails( new String[] { masterMail }, genBody() );
					}
				}
					
					
			}




		}
		catch( Exception e )
		{
			//send this request with all its data to CTI
			setState( RET_FAILED, "Unknown error occured, please contact the WebMaster for more details" );
			subject = "WebMaster: " + subject;

			CTILogger.error( e.getMessage(), e );
		}

	}
	
	
	private LiteEnergyCompany[] processContact( LiteContact lCont_ )
	{
		LiteCICustomer lCust = ContactFuncs.getCICustomer( lCont_.getContactID() );

		//no customer found, we have some issues
		if( lCust == null )
		{
			CTILogger.error(
				" Unable to find a customer parent for the following contact: " 
				+ lCont_.getContFirstName() + " " + lCont_.getContLastName() );
						
			return null;
		}


		foundData.add( " Customer Name: " + lCust.getCompanyName() );					

		LiteEnergyCompany[] cmp = 
			EnergyCompanyFuncs.getEnergyCompaniesByCustomer( lCust.getCustomerID() );
	
		return cmp;		
	}

	/**
	 * Success of this method returns SUCCESS_URI,
	 *   while no success returns INVALID_URI with the error message.
	 * 
	 * @param comps_
	 * @param allParams_
	 * @return
	 */
	private void processEnergyCompanies( LiteEnergyCompany[] comps_ )
	{

		if( comps_ == null || comps_.length <= 0 )
		{
			//do something here, dont know what for now
			setState( RET_FAILED, "No energy company OR customer found for the entered data" );
		}			
		else if( comps_.length == 1 )
		{			
			foundData.add( " Energy Company Name: " + comps_[0].getName() );					
			
			String[] emails =
					ContactFuncs.getAllEmailAddresses( comps_[0].getPrimaryContactID() );

			for( int i = 0; i < emails.length; i++ )
			{
				CTILogger.debug( "Found email in EnergyCompany (" +
					comps_[0].getName() + ") : " + emails[i] );					
			}


			if( emails.length > 0 )
				sendEmails( emails, genBody() );
			
			setState( RET_SUCCESS, "" ); //success!!
		}
		else //2 or more EnergyCompanies found
		{
			//do something here, dont know what for now
			setState( RET_FAILED, "More than one energy company found, forwarding request onto the WebMaster" );
			subject = "WebMaster: " + subject;
			foundData.add( " " + getResultString() );
			foundData.add( " Number of Energy Companies for this Data: " + comps_.length );

			for( int i = 0; i < comps_.length; i++ )
				foundData.add( "   Energy Company " + i + ": " + comps_[i].getName() );
			
			sendEmails( new String[] { masterMail }, genBody() );			
		}

	}

	private void sendEmails( String[] emailTO_, final String body_ )
	{
		EmailMessage msg = new EmailMessage(
			emailTO_,
			subject,
			body_ );

		try
		{
			msg.send();
		}
		catch( Exception e )
		{
			CTILogger.error( e.getMessage(), e );
		}

	}


	private String genBody()
	{
		String body =
			"A password request has been made from a user that provided the following information:" + CR +
			(allParams[0] == null ? "" : " User Name    : " + allParams[0] + CR ) +
			(allParams[1] == null ? "" : " Email        : " + allParams[1] + CR ) +
			(allParams[2] == null ? "" : " First Name   : " + allParams[2] + CR ) +
			(allParams[3] == null ? "" : " Last Name    : " + allParams[3] + CR ) +
			(allParams[4] == null ? "" : " Account Num. : " + allParams[4] + CR ) +
			CR +
			"The following information is what was found for this user:" + CR;
			
		for( int i = 0; i < foundData.size(); i++ )
			body += foundData.get(i).toString() + 
					  (i == (foundData.size()-1) ? "" : CR);

		if( getNotes() != null )
			body += CR + CR + 
					"The user supplied the following notes:" + CR + getNotes();


		return body;
	}

	public boolean isValidParams()
	{
		return 
			!(userName == null && email == null && fName == null
			  && lName == null && accNum == null);
	}
	
	private void setState( int state_, String msg_ )
	{
		state = state_;
		resultString = msg_;
	}

	/**
	 * @return
	 */
	public String getNotes()
	{
		return notes;
	}

	/**
	 * @param string
	 */
	public void setNotes(String string)
	{
		notes = string;
	}

}
