package com.cannontech.servlet.logic;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;

/**
 * @author rneuharth
 *
 * Used to handle requests for passwords and send the requests to 
 * the email address of the Energy Company that owns this Contact, YukonUser
 * or Customer. If we can not determine which Energy Company to use, we send
 * the request to the MasterMail, supplied in:
 * 
 *  
 * NOTE: Can't put this class in common since it uses STARS
 */
public class StarsRequestPword extends RequestPword {
	private String accNum = null;		

	/**
	 * 
	 */
	public StarsRequestPword( String userName_, String email_, String fName_, String lName_, String accNum_ ) {
		super(
			userName_,
			email_,
			fName_,
			lName_ );	

		accNum = accNum_;

		String[] tParams = new String[ allParams.length + 1 ];
		tParams[tParams.length-1] = accNum_;

		System.arraycopy( allParams, 0, tParams, 0, allParams.length );
		allParams = tParams;	
	}
	
	public void doRequest() {
		try {
			//uses STARS functionality
			if( accNum != null ) {
				//we may continue after this, remove all the stored data
				foundData.clear();
				
				List engrComps = StarsDatabaseCache.getInstance().getAllEnergyCompanies();
				LiteStarsEnergyCompany eComp = null;
				ArrayList allCustAccts = new ArrayList(8);

				for( int i = 0; i < engrComps.size(); i++ ) {
					LiteStarsEnergyCompany lsec = (LiteStarsEnergyCompany)engrComps.get(i);
					
					LiteStarsCustAccountInformation lCustInfo =
							lsec.searchAccountByAccountNo( accNum );
					
					if( lCustInfo != null ) {
						allCustAccts.add( lCustInfo );
						eComp = lsec;
					}
				}
					
				if( allCustAccts.size() == 1 ) {
					//only 1 found, this is good
					LiteStarsCustAccountInformation lCustInf = 
						(LiteStarsCustAccountInformation)allCustAccts.get(0);

					LiteContact lc = (LiteContact)
						DaoFactory.getContactDao().getContact( lCustInf.getCustomer().getPrimaryContactID() );

					LiteYukonUser user =
						DaoFactory.getYukonUserDao().getLiteYukonUser( lc.getLoginID() );
					
					foundData.add( " User Name: " + user.getUsername() );					
					foundData.add( " Contact Name: " + lc.getContFirstName() + " " + lc.getContLastName() );					
					
					//we must get the Yukon lite energy company for the stars lite energy company
					LiteEnergyCompany lEnrgy =
						DaoFactory.getEnergyCompanyDao().getEnergyCompany( eComp.getEnergyCompanyID().intValue() );

					processEnergyCompanies( new LiteEnergyCompany[] { lEnrgy } );
				}
				else if( allCustAccts.size() < 1 ) {
					setState( RET_FAILED, "Account Number not found, try again" );					
				}
				else {
					setState( RET_FAILED, "More than one account number found, forwarding request onto the WebMaster" );
					subject = "WebMaster: " + subject;
					foundData.add( " " + getResultString() );
					foundData.add( " Number of Account Numbers for this Account: " + allCustAccts.size() );
					for( int i = 0; i < allCustAccts.size(); i++ ) {
						LiteStarsCustAccountInformation lCstInfo =
							(LiteStarsCustAccountInformation)allCustAccts.get(i);
						
						foundData.add( "   Account # " + i + ": " + lCstInfo.getCustomerAccount().getAccountNumber() );
					}

					sendEmails( new String[] { masterMail }, genBody() );
				}
			}
			
			//try the parents functionality
			if( getState() != RET_SUCCESS ) {
				super.doRequest();
			}
		}
        //TODO: This whole servlet should be overhauled to improve exception handling.  For now, will just
        //change the text of the error message.
		catch( Exception e ) {
			//send this request with all its data to CTI
			setState( RET_FAILED, "The information you supplied does not match a known user.  Please verify that the information you have entered is correct." );
			subject = "WebMaster: " + subject;

			CTILogger.error( e.getMessage(), e );
		}
	}

	protected LiteEnergyCompany[] processContact( LiteContact lCont_ ) {
		LiteCustomer liteCust = DaoFactory.getContactDao().getCustomer( lCont_.getContactID() );
		
		if (liteCust.getEnergyCompanyID() != -1) {
			LiteEnergyCompany liteComp = DaoFactory.getEnergyCompanyDao().getEnergyCompany( liteCust.getEnergyCompanyID() );
			return new LiteEnergyCompany[] { liteComp };
		}
		
		// Try the parent's functionality
		return super.processContact( lCont_ );
	}
}
