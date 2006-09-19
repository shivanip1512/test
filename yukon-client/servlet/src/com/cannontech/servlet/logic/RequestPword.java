package com.cannontech.servlet.logic;

import java.util.Vector;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.login.ClientSession;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.yukon.SystemRole;
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
	private String notes = null;
    private String energyCompany = null;

	private int state = RET_FAILED;
	private String resultString = "";


	//allow sub classes access to these attributes
	protected String[] allParams = null;
	protected String masterMail = null;
	protected String subject = "Password Request";
	protected Vector foundData = new Vector(8);


	public static final int RET_FAILED = 0;
	public static final int RET_SUCCESS = 1;


	/**
	 * 
	 */
	public RequestPword( String userName_, String email_, String fName_, String lName_ )
	{
		super();
		userName = userName_;
		email = email_;
		fName = fName_;
		lName = lName_;
		
		allParams = new String[] { userName, email, fName, lName };

        /*
         * Now that we no longer return a defaultValue automatically on a getRolePropertyValue, we
         * will get a null if the user is null.  The user SHOULD be null in this case, since nobody
         * should be logged in.  Let's try to avoid the error.
         */
		Object o;
        if(ClientSession.getInstance().getUser() == null) {
            o = DaoFactory.getRoleDao().getGlobalPropertyValue(SystemRole.MAIL_FROM_ADDRESS);
        }
        else {
            o = ClientSession.getInstance().getRolePropertyValue(                            
							SystemRole.MAIL_FROM_ADDRESS );
        }
		if( o != null )
			masterMail = o.toString();
	}

	public String getResultString()
	{
		return resultString;
	}
    
    private void handleEmail()
    {
        //unique system wide email address
        if( email != null )
        {
            //we may continue after this, remove all the stored data
            foundData.clear();
            
            LiteContact lc = DaoFactory.getContactDao().getContactByEmailNotif( email );
            if( lc == null )
                setState( RET_FAILED, "Contact for Email not found, try again" );
            else
            {
                foundData.add( " Contact Name: " + lc.getContFirstName() + " " + lc.getContLastName() );
                foundData.add( " User Name: " + DaoFactory.getYukonUserDao().getLiteYukonUser(lc.getLoginID()).getUsername() );

                LiteEnergyCompany[] cmps = processContact( lc );
                processEnergyCompanies( cmps );
            }
        }
        
    }
    
    private void handleUserName()
    {
        //unique system wide user name
        if( userName != null )
        {
            //we may continue after this, remove all the stored data
            foundData.clear();
            
            LiteYukonUser user = DaoFactory.getYukonUserDao().getLiteYukonUser( userName );
            if( user == null )
                setState( RET_FAILED, "User Name not found, try again" );
            else
            {
                foundData.add( " User Name: " + user.getUsername() );                   

                LiteContact lc = DaoFactory.getYukonUserDao().getLiteContact( user.getUserID() );
                if( lc == null )
                {
                    setState( RET_FAILED, "Contact for User Name not found, try again" );
                }
                else
                {
                    foundData.add( " Contact Name: " + lc.getContFirstName() + " " + lc.getContLastName() );                    
    
                    LiteEnergyCompany[] cmps = processContact( lc );                    
                    processEnergyCompanies( cmps );
                }
            }
        }

    }

    private void handleFName()
    {
        if( fName != null )
        {
            //we may continue after this, remove all the stored data
            foundData.clear();
            
            LiteContact[] lConts = DaoFactory.getContactDao().getContactsByFName( fName );
            if( lConts.length == 1 )
            {
                //if we also have a last name, try to match BOTH names
                if( lName != null )
                {
                    if( lName.equalsIgnoreCase(lConts[0].getContLastName()) )
                    {
                        foundData.add( " Contact Name: " + lConts[0].getContFirstName() + " " + lConts[0].getContLastName() );                  
                        foundData.add( " User Name: " + DaoFactory.getYukonUserDao().getLiteYukonUser(lConts[0].getLoginID()).getUsername() );
                        
                        LiteEnergyCompany[] cmps = processContact( lConts[0] );
                        processEnergyCompanies( cmps );                                 
                    }
                    else
                    {
                        setState( RET_FAILED, "Name not found, try again" );
                    }
                    
                }
                else
                {
                    foundData.add( " Contact Name: " + lConts[0].getContFirstName() + " " + lConts[0].getContLastName() );                  
                    foundData.add( " User Name: " + DaoFactory.getYukonUserDao().getLiteYukonUser(lConts[0].getLoginID()).getUsername() );

                    LiteEnergyCompany[] cmps = processContact( lConts[0] );                 
                    processEnergyCompanies( cmps );
                }
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
                            foundData.add( " User Name: " + DaoFactory.getYukonUserDao().getLiteYukonUser(lConts[i].getLoginID()).getUsername() );
                            
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

    }

    
    private void handleLName()
    {
        if( lName != null )
        {
            //we may continue after this, remove all the stored data
            foundData.clear();
            
            LiteContact[] lConts = DaoFactory.getContactDao().getContactsByLName( lName );
            if( lConts.length == 1 )
            {
                //if we also have a first name, try to match BOTH names
                if( fName != null )
                {
                    if( fName.equalsIgnoreCase(lConts[0].getContFirstName()) )
                    {
                        foundData.add( " Contact Name: " + lConts[0].getContFirstName() + " " + lConts[0].getContLastName() );                  
                        foundData.add( " User Name: " + DaoFactory.getYukonUserDao().getLiteYukonUser(lConts[0].getLoginID()).getUsername() );
                        LiteEnergyCompany[] cmps = processContact( lConts[0] );
                        processEnergyCompanies( cmps );                                 
                    }
                    else
                    {
                        setState( RET_FAILED, "Name not found, try again" );
                    }                    
                }
                else
                {
                    foundData.add( " Contact Name: " + lConts[0].getContFirstName() + " " + lConts[0].getContLastName() );                  
                    foundData.add( " User Name: " + DaoFactory.getYukonUserDao().getLiteYukonUser(lConts[0].getLoginID()).getUsername() );
                    
                    LiteEnergyCompany[] cmps = processContact( lConts[0] );                 
                    processEnergyCompanies( cmps );
                }
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
                            foundData.add( " User Name: " + DaoFactory.getYukonUserDao().getLiteYukonUser(lConts[i].getLoginID()).getUsername() );

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
    
	public void doRequest()
	{

		try
		{
			//unique system wide email address
            handleEmail();
				
			//unique system wide user name
			if( getState() != RET_SUCCESS )
			{
                handleUserName();
			}

			//remove for now since search by first name / last name is not safe.
			/*
			if( getState() != RET_SUCCESS )
                handleFName();
				
			if( getState() != RET_SUCCESS )
                handleLName();
            */


		}
		catch( Exception e )
		{
			//send this request with all its data to CTI
			setState( RET_FAILED, "Unknown error occured, please contact the WebMaster for more details" );
			subject = "WebMaster: " + subject;

			CTILogger.error( e.getMessage(), e );
		}

	}
	
	
	protected LiteEnergyCompany[] processContact( LiteContact lCont_ )
	{
		LiteCICustomer lCust = DaoFactory.getContactDao().getCICustomer( lCont_.getContactID() );

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
			DaoFactory.getEnergyCompanyDao().getEnergyCompaniesByCustomer( lCust.getCustomerID() );
	
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
	protected void processEnergyCompanies( LiteEnergyCompany[] comps_ )
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
					DaoFactory.getContactDao().getAllEmailAddresses( comps_[0].getPrimaryContactID() );

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

	protected void sendEmails( String[] emailTO_, final String body_ )
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


	protected String genBody()
	{
		String body =
			"A password request has been made from a user that provided the following information:" + CR;
		for( int i = 0; i < allParams.length; i++ )
		{
			if( allParams[i] != null )
			{
				switch( i )
				{
					case 0:
					body += " User Name    : " + allParams[i] + CR;
					break;

					case 1:
					body += " Email        : " + allParams[i] + CR;
					break;

					case 2:
					body += " First Name   : " + allParams[i] + CR;
					break;

					case 3:
					body += " Last Name    : " + allParams[i] + CR;
					break;

					case 4:
					body += " Account Num. : " + allParams[i] + CR;
					break;
				}
				
			}				
		}
		
			
		body += CR + "The following information is what was found for this user:" + CR;
			
		for( int i = 0; i < foundData.size(); i++ )
			body += foundData.get(i).toString() + 
					  (i == (foundData.size()-1) ? "" : CR);

        if( getEnergyCompany() != null )
            body += CR + CR + 
                    "The user supplied the following Energy Company: " + getEnergyCompany();

        if( getNotes() != null )
			body += CR + CR + 
					"The user supplied the following notes:" + CR + getNotes();
        
        
		return body;
	}

	/**
	 * If all params are NULL, we have INVALID data.
	 * @return boolean validParams
	 */
	public boolean isValidParams()
	{
		boolean isValid = false;
		for( int i = 0; i < allParams.length; i++ )
			isValid |= allParams[i] != null;

		return isValid;
	}
	
	protected void setState( int state_, String msg_ )
	{
		state = state_;
		resultString = msg_;
	}

	public int getState()
	{
		return state;
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

    /**
     * @param string
     */
    public void setEnergyCompany(String enrgyCompany)
    {
        energyCompany = enrgyCompany;
    }

    /**
     * @param string
     */
    public String getEnergyCompany()
    {
        return energyCompany;
    }

}
