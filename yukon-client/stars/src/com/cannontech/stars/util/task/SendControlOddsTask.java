package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.Date;
import java.util.StringTokenizer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonListFuncs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.database.data.lite.stars.LiteApplianceCategory;
import com.cannontech.database.data.lite.stars.LiteCustomerContact;
import com.cannontech.database.data.lite.stars.LiteLMProgram;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.database.data.lite.stars.LiteWebConfiguration;
import com.cannontech.stars.util.ServerUtils;
import com.cannontech.stars.web.servlet.SOAPServer;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SendControlOddsTask implements Runnable {
	
	private static final String subject = "Today's Odds For Control";
	private static final String header =
			"Program Enrollment              Odds for Control\r\n" +
			"================================================================\r\n";
	private static final String blanks = "                                ";
	private static final String footer = "To unsubscribe from the notification list, " +
			"please go to http://www.wisewatts.com and login with your username and password. " +
			"On the first page (or the \"General\" link), uncheck the notification box and click \"Submit\".";
	
	private int energyCompanyID = 0;
	
	public SendControlOddsTask(int energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		CTILogger.info( "*** Start SendControlOdds task ***" );
		
		LiteStarsEnergyCompany energyCompany = SOAPServer.getEnergyCompany( energyCompanyID );
		String from = energyCompany.getEnergyCompanySetting(
				com.cannontech.roles.yukon.EnergyCompanyRole.ADMIN_EMAIL_ADDRESS );
		
		ArrayList progList = new ArrayList();	// Programs that are eligible for notification
		ArrayList categories = energyCompany.getAllApplianceCategories();
		for (int i = 0; i < categories.size(); i++) {
			LiteApplianceCategory category = (LiteApplianceCategory) categories.get(i);
			for (int j = 0; j < category.getPublishedPrograms().length; j++) {
				LiteLMProgram program = category.getPublishedPrograms()[j];
				if (program.getChanceOfControlID() != CtiUtilities.NONE_ID)
					progList.add( program );
			}
		}
		
		if (progList.size() > 0) {
			StringBuffer sql = new StringBuffer( "SELECT DISTINCT app.AccountID " )
					.append( "FROM ApplianceBase app, ECToAccountMapping map " )
					.append( "WHERE map.EnergyCompanyID = " )
					.append( energyCompanyID )
					.append( " AND map.AccountID = app.AccountID AND (" )
					.append( "app.LMProgramID = " )
					.append( ((LiteLMProgram) progList.get(0)).getProgramID() );
			for (int i = 1; i < progList.size(); i++) {
				LiteLMProgram program = (LiteLMProgram) progList.get(i);
				sql.append(" OR app.LMProgramID = ").append(program.getProgramID());
			}
			sql.append(")");
			
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql.toString(), CtiUtilities.getDatabaseAlias() );
			try {
				stmt.execute();
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int accountID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					LiteStarsCustAccountInformation accountInfo = energyCompany.getCustAccountInformation( accountID, true );
					
					LiteCustomerContact primContact = energyCompany.getCustomerContact( accountInfo.getCustomer().getPrimaryContactID() );
					if (primContact.getEmail() == null || !primContact.getEmail().isEnabled())
						continue;
					
					ArrayList activeProgs = new ArrayList();	// List of all the active programs
					for (int j = 0; j < accountInfo.getLmPrograms().size(); j++) {
						LiteStarsLMProgram program = (LiteStarsLMProgram) accountInfo.getLmPrograms().get(j);
						if (progList.contains( program.getLmProgram() ) && program.isInService())
							activeProgs.add( program );
					}
					if (activeProgs.size() == 0) continue;
					
					StringTokenizer st = new StringTokenizer( primContact.getEmail().getNotification(), "," );
					ArrayList toList = new ArrayList();
					while (st.hasMoreTokens())
						toList.add( st.nextToken() );
					String[] to = new String[ toList.size() ];
					toList.toArray( to );
					
					StringBuffer text = new StringBuffer( header );
					for (int j = 0; j < activeProgs.size(); j++) {
						LiteStarsLMProgram program = (LiteStarsLMProgram) activeProgs.get(j);
						String progName = program.getLmProgram().getProgramName();
						// Temporarily use the "URL" field in YukonWebConfiguration table for program alias
						LiteWebConfiguration liteConfig = SOAPServer.getWebConfiguration( program.getLmProgram().getWebSettingsID() );
						if (liteConfig.getUrl() != null && liteConfig.getUrl().length() > 0)
							progName = liteConfig.getUrl();
						String ctrlOdds = YukonListFuncs.getYukonListEntry( program.getLmProgram().getChanceOfControlID() ).getEntryText();
						
						text.append( progName );
						text.append( blanks.substring(progName.length()) );
						text.append( ctrlOdds );
						text.append( "\r\n" );
					}
					text.append( "\r\n\r\n\r\n" ).append( footer );
					
					try {
						ServerUtils.sendEmailMsg( from, to, null, subject, text.toString() );
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			catch (com.cannontech.common.util.CommandExecutionException e) {
				e.printStackTrace();
			}
		}
				
		CTILogger.info( "*** End SendControlOdds task ***" );
	}

}
