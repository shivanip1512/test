package com.cannontech.stars.core.model;

import java.util.ArrayList;
import java.util.List;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteCustomer;
import com.cannontech.database.data.lite.LiteEnergyCompany;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;

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
	private StarsCustAccountInformationDao starsCustAccountInformationDao;
	
	public StarsRequestPword( String userName, String email, String fName, String lName, String accNum, StarsCustAccountInformationDao starsCustAccountInformationDao ) {
		super(
			userName,
			email,
			fName,
			lName );	

		this.accNum = accNum;

		String[] tParams = new String[ allParams.length + 1 ];
		tParams[tParams.length-1] = accNum;

		System.arraycopy( allParams, 0, tParams, 0, allParams.length );
		allParams = tParams;
		
		this.starsCustAccountInformationDao = starsCustAccountInformationDao;
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

					LiteAccountInfo lCustInfo = null;
					List<Object> accounts = lsec.searchAccountByAccountNumber(accNum, false, true);
					
					lCustInfo = searchForMatchingAccount(accounts, lsec);
					
					if( lCustInfo != null ) {
						allCustAccts.add( lCustInfo );
						eComp = lsec;
					}
				}
					
				if( allCustAccts.size() == 1 ) {
					//only 1 found, this is good
					LiteAccountInfo lCustInf = 
						(LiteAccountInfo)allCustAccts.get(0);

					LiteContact lc = DaoFactory.getContactDao().getContact( lCustInf.getCustomer().getPrimaryContactID() );

					LiteYukonUser user = DaoFactory.getYukonUserDao().getLiteYukonUser( lc.getLoginID() );
					
					foundData.add( " Username: " + user.getUsername() );					
					foundData.add( " Contact Name: " + lc.getContFirstName() + " " + lc.getContLastName() );					
					
					//we must get the Yukon lite energy company for the stars lite energy company
					LiteEnergyCompany lEnrgy =
						DaoFactory.getEnergyCompanyDao().getEnergyCompany( eComp.getEnergyCompanyId());

					processEnergyCompanies( new LiteEnergyCompany[] { lEnrgy } );
				}
				else if( allCustAccts.size() < 1 ) {
					setState( RET_FAILED, "NO_ACCOUNT" );					
				}
				else {
					setState( RET_FAILED, "MULTIPLE_ACCOUNTS" );
					subject = "WebMaster: " + subject;
					foundData.add( " " + getResultString() );
					foundData.add( " Number of Account Numbers for this Account: " + allCustAccts.size() );
					for( int i = 0; i < allCustAccts.size(); i++ ) {
						LiteAccountInfo lCstInfo =
							(LiteAccountInfo)allCustAccts.get(i);
						
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
			setState( RET_FAILED, "DOES_NOT_MATCH" );
			subject = "WebMaster: " + subject;

			CTILogger.error( e.getMessage(), e );
		}
	}

	private LiteAccountInfo searchForMatchingAccount(List<Object> accounts, LiteStarsEnergyCompany lsec) {
	    LiteAccountInfo lCustInfo = null;
	    for(Object object : accounts) {
            if(object instanceof Integer) {
                Integer accountId = (Integer) object;
                lCustInfo = starsCustAccountInformationDao.getById(accountId, lsec.getEnergyCompanyId());
                return lCustInfo;
            }
        }
	    return null;
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
