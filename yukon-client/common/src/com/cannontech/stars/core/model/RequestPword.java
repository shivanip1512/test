package com.cannontech.stars.core.model;

import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.system.YukonSetting;
import com.cannontech.system.dao.YukonSettingsDao;
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
	protected List<String> foundData = new Vector<String>(8);


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

		YukonSettingsDao yukonSettingsDao = YukonSpringHook.getBean("yukonSettingsDao", YukonSettingsDao.class);

		
        /*
         * Now that we no longer return a defaultValue automatically on a getRolePropertyValue, we
         * will get a null if the user is null.  The user SHOULD be null in this case, since nobody
         * should be logged in.  Let's try to avoid the error.
         */
		String mail = yukonSettingsDao.getSettingStringValue(YukonSetting.MAIL_FROM_ADDRESS);
		if(mail != null) {
			masterMail = mail;
		}
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
                setState( RET_FAILED, "EMAIL_NOT_FOUND" );
            else
            {
                foundData.add( " Contact Name: " + lc.getContFirstName() + " " + lc.getContLastName() );
                foundData.add( " Username: " + DaoFactory.getYukonUserDao().getLiteYukonUser(lc.getLoginID()).getUsername() );

                LiteEnergyCompany[] cmps = processContact( lc );
                processEnergyCompanies( cmps );
            }
        }
        
    }
    
    private void handleUserName()
    {
        //unique system wide username
        if( userName != null )
        {
            //we may continue after this, remove all the stored data
            foundData.clear();
            
            LiteYukonUser user = DaoFactory.getYukonUserDao().findUserByUsername( userName );
            if( user == null )
                setState( RET_FAILED, "USER_NOT_FOUND" );
            else
            {
                foundData.add( " Username: " + user.getUsername() );                   

                LiteContact lc = DaoFactory.getYukonUserDao().getLiteContact( user.getUserID() );
                if( lc == null )
                {
                    setState( RET_FAILED, "CONTACT_NOT_FOUND" );
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


    
    
	public void doRequest()
	{

		try
		{
			//unique system wide email address
            handleEmail();
				
			//unique system wide username
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
		//TODO: This should be overhauled to improve exception handling.  For now, will just
        //change the text of the error message.
		catch( Exception e )
		{
			//send this request with all its data to CTI
			setState( RET_FAILED, "DOES_NOT_MATCH" );
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
			setState( RET_FAILED, "COMPANY_NOT_FOUND" );
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
			setState( RET_FAILED, "MULTIPLE_COMPANIES" );
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
					body += " Username    : " + allParams[i] + CR;
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
			isValid |= !StringUtils.isEmpty(allParams[i]);

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
