package com.cannontech.stars.core.model;

import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.data.lite.LiteCICustomer;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingDao;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;

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
	protected List<String> foundData = new ArrayList<String>(8);


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
		
		allParams = new String[] {userName, email};

		GlobalSettingDao globalSettingDao = YukonSpringHook.getBean(GlobalSettingDao.class);
		
        /*
         * Now that we no longer return a defaultValue automatically on a getRolePropertyValue, we
         * will get a null if the user is null.  The user SHOULD be null in this case, since nobody
         * should be logged in.  Let's try to avoid the error.
         */
		String mail = globalSettingDao.getString(GlobalSettingType.MAIL_FROM_ADDRESS);
		if(mail != null) {
			masterMail = mail;
		}
	}

	public String getResultString()
	{
		return resultString;
	}
    
    private void handleEmail() {
        //unique system wide email address
        if (email != null) {
            //we may continue after this, remove all the stored data
            foundData.clear();
            
            LiteContact liteContact = YukonSpringHook.getBean(ContactDao.class).findContactByEmail(email);
            if (liteContact == null) {
                setState( RET_FAILED, "EMAIL_NOT_FOUND");
            } else { //found exactly one
                foundData.add( " Contact Name: " + liteContact.getContFirstName() + " " + liteContact.getContLastName() );
                foundData.add( " Username: " + YukonSpringHook.getBean(YukonUserDao.class).getLiteYukonUser(liteContact.getLoginID()).getUsername() );

                List<EnergyCompany> cmps = processContact(liteContact);
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
            
            LiteYukonUser user = YukonSpringHook.getBean(YukonUserDao.class).findUserByUsername( userName );
            if( user == null ) {
                setState( RET_FAILED, "USER_NOT_FOUND" );
            } else
            {
                foundData.add( " Username: " + user.getUsername() );                   

                LiteContact lc = YukonSpringHook.getBean(YukonUserDao.class).getLiteContact( user.getUserID() );
                if( lc == null )
                {
                    setState( RET_FAILED, "CONTACT_NOT_FOUND" );
                }
                else
                {
                    foundData.add( " Contact Name: " + lc.getContFirstName() + " " + lc.getContLastName() );                    
    
                    List<EnergyCompany> cmps = processContact(lc);
                    processEnergyCompanies(cmps);
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
	
	protected List<EnergyCompany> processContact(LiteContact lCont_)
	{
		LiteCICustomer lCust = YukonSpringHook.getBean(ContactDao.class).getCICustomer( lCont_.getContactID() );

		//no customer found, we have some issues
		if( lCust == null )
		{
			CTILogger.error(
				" Unable to find a customer parent for the following contact: " 
				+ lCont_.getContFirstName() + " " + lCont_.getContLastName() );
						
			return null;
		}


		foundData.add( " Customer Name: " + lCust.getCompanyName() );

		List<EnergyCompany> energyCompanies = 
		        YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompaniesByCiCustomer(lCust.getCustomerID());
		
		return energyCompanies;
	}

	/**
	 * Success of this method returns SUCCESS_URI,
	 *   while no success returns INVALID_URI with the error message.
	 * 
	 * @param comps_
	 * @param allParams_
	 * @return
	 */
	protected void processEnergyCompanies(List<EnergyCompany> comps_)
	{

		if( comps_ == null || comps_.size() <= 0 )
		{
			//do something here, dont know what for now
			setState( RET_FAILED, "COMPANY_NOT_FOUND" );
		}			
		else if( comps_.size() == 1 )
		{			
			foundData.add( " Energy Company Name: " + comps_.get(0).getName() );					
			
			String[] emails =
					YukonSpringHook.getBean(ContactDao.class).getAllEmailAddresses( comps_.get(0).getContactId() );

			for( int i = 0; i < emails.length; i++ )
			{
				CTILogger.debug( "Found email in EnergyCompany (" +
					comps_.get(0).getName() + ") : " + emails[i] );					
			}


			if( emails.length > 0 ) {
                sendEmails( emails, genBody() );
            }
			
			setState( RET_SUCCESS, "" ); //success!!
		}
		else //2 or more EnergyCompanies found
		{
			//do something here, dont know what for now
			setState( RET_FAILED, "MULTIPLE_COMPANIES" );
			subject = "WebMaster: " + subject;
			foundData.add( " " + getResultString() );
			foundData.add( " Number of Energy Companies for this Data: " + comps_.size() );

			for( int i = 0; i < comps_.size(); i++ ) {
                foundData.add( "   Energy Company " + i + ": " + comps_.get(i).getName() );
            }
			
			sendEmails( new String[] { masterMail }, genBody() );			
		}

	}

	protected void sendEmails( String[] emailTO_, final String body_ )
	{
		try
		{
	        String to = StringUtils.join(emailTO_, ",");
	        EmailMessage message = new EmailMessage(InternetAddress.parse(to), subject, body_);
		    EmailService emailService = YukonSpringHook.getBean(EmailService.class);
			emailService.sendMessage(message);
		}
		catch( Exception e )
		{
			CTILogger.error( e.getMessage(), e );
		}

	}


	protected String genBody()
	{
		StringBuffer body = new StringBuffer("A password request has been made from a user that provided the following information:").append(CR);
		for (int i = 0; i < allParams.length; i++) {
		    String param = allParams[i];
			if (param != null) {
				switch (i) {
					case 0:
					    body.append(" Username: ").append(param).append(CR);
					break;

					case 1:
					    body.append(" Email: ").append(param).append(CR);
					break;

					case 2:
					    body.append(" Account #: ").append(param).append(CR);
					break;
				}
				
			}				
		}
		
		if (StringUtils.isNotBlank(fName)) {
		    body.append(" First Name: ").append(fName).append(CR);
		}
		if (StringUtils.isNotBlank(lName)) {
		    body.append(" Last Name: ").append(lName).append(CR);
		}
		if (StringUtils.isNotBlank(getEnergyCompany())) {
		    body.append(" Energy Provider: ").append(getEnergyCompany()).append(CR);
		}
		if (StringUtils.isNotBlank(getNotes())) {
		    body.append(" Notes: ").append(getNotes()).append(CR);
		}

		body.append(CR).append("The following information is what was found for this user:").append(CR);
			
		for (String data : foundData) {
            body.append(data).append(CR);
        }

		return body.toString();
	}

	/**
	 * If all params are NULL, we have INVALID data.
	 * @return boolean validParams
	 */
	public boolean isValidParams()
	{
		boolean isValid = false;
		for (String param : allParams) {
			isValid |= !StringUtils.isEmpty(param);
		}
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
	
	public String getNotes()
	{
		return notes;
	}

	public void setNotes(String string)
	{
		notes = string;
	}

    public void setEnergyCompany(String enrgyCompany)
    {
        energyCompany = enrgyCompany;
    }

    public String getEnergyCompany()
    {
        return energyCompany;
    }

}
