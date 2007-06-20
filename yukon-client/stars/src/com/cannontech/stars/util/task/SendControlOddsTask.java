package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.cache.StarsDatabaseCache;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.stars.LiteLMProgramWebPublishing;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.data.lite.stars.LiteStarsEnergyCompany;
import com.cannontech.database.data.lite.stars.LiteStarsLMProgram;
import com.cannontech.roles.operator.ConsumerInfoRole;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.util.ServletUtil;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SendControlOddsTask implements Runnable {
	
	private int energyCompanyID = 0;
	
	public SendControlOddsTask(int energyCompanyID) {
		this.energyCompanyID = energyCompanyID;
	}
	
	/**
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		CTILogger.info( "*** Start SendControlOdds task ***" );
		
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyID );
		
		String LINE_SEPARATOR = System.getProperty("line.separator");
		String blanks = "                                ";
		String ctrlOddsText = energyCompany.getEnergyCompanySetting( ConsumerInfoRole.WEB_TEXT_ODDS_FOR_CONTROL );
		
		String subject = "Today's " + ServletUtil.capitalizeAll( ctrlOddsText );
		
		String header = "Program Enrollment              " + ServletUtil.capitalizeAll( ctrlOddsText ) + LINE_SEPARATOR
					  + "================================================================" + LINE_SEPARATOR;
		
		String footer = "To unsubscribe from the notification list, please go to "
					  + "http://www.wisewatts.com and login with your username and password. "
					  + "On the first page (or the \"General\" link), uncheck the notification box and click \"Submit\".";
		
		ArrayList progList = new ArrayList();	// Programs that are eligible for notification
		ArrayList programs = energyCompany.getAllPrograms();
		for (int i = 0; i < programs.size(); i++) {
			LiteLMProgramWebPublishing program = (LiteLMProgramWebPublishing) programs.get(i);
			if (program.getChanceOfControlID() != CtiUtilities.NONE_ZERO_ID)
				progList.add( program );
		}
		
		if (progList.size() > 0) {
			StringBuffer sql = new StringBuffer( "SELECT DISTINCT app.AccountID " )
					.append( "FROM ApplianceBase app, ECToAccountMapping map " )
					.append( "WHERE map.EnergyCompanyID = " )
					.append( energyCompanyID )
					.append( " AND map.AccountID = app.AccountID AND (" )
					.append( "app.ProgramID = " )
					.append( ((LiteLMProgramWebPublishing) progList.get(0)).getProgramID() );
			for (int i = 1; i < progList.size(); i++) {
				LiteLMProgramWebPublishing program = (LiteLMProgramWebPublishing) progList.get(i);
				sql.append(" OR app.ProgramID = ").append(program.getProgramID());
			}
			sql.append(")");
			
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql.toString(), CtiUtilities.getDatabaseAlias() );
			try {
				stmt.execute();
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int accountID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					LiteStarsCustAccountInformation accountInfo = energyCompany.getCustAccountInformation( accountID, true );
					
					LiteContact primContact = DaoFactory.getContactDao().getContact( accountInfo.getCustomer().getPrimaryContactID() );
					LiteContactNotification email = DaoFactory.getContactDao().getContactNotification(
							primContact, com.cannontech.common.constants.YukonListEntryTypes.YUK_ENTRY_ID_EMAIL );
					if (email == null || email.getDisableFlag().equalsIgnoreCase("Y"))
						continue;
					
					ArrayList activeProgs = new ArrayList();	// List of all the active programs
					for (int j = 0; j < accountInfo.getPrograms().size(); j++) {
						LiteStarsLMProgram program = (LiteStarsLMProgram) accountInfo.getPrograms().get(j);
						if (progList.contains( program.getPublishedProgram() ) && program.isInService())
							activeProgs.add( program );
					}
					if (activeProgs.size() == 0) continue;
					
					StringTokenizer st = new StringTokenizer( email.getNotification(), "," );
					ArrayList toList = new ArrayList();
					while (st.hasMoreTokens())
						toList.add( st.nextToken() );
					String[] to = new String[ toList.size() ];
					toList.toArray( to );
					
					StringBuffer text = new StringBuffer( header );
					for (int j = 0; j < activeProgs.size(); j++) {
						LiteStarsLMProgram program = (LiteStarsLMProgram) activeProgs.get(j);
						String progName = StarsUtils.getPublishedProgramName( program.getPublishedProgram() );
						String ctrlOdds = DaoFactory.getYukonListDao().getYukonListEntry( program.getPublishedProgram().getChanceOfControlID() ).getEntryText();
						
						text.append( progName );
						text.append( blanks.substring(progName.length()) );
						text.append( ctrlOdds );
						text.append( LINE_SEPARATOR );
					}
					text.append( LINE_SEPARATOR );
					text.append( LINE_SEPARATOR );
					text.append( LINE_SEPARATOR );
					text.append( footer );
					
					try {
						EmailMessage emailMsg = new EmailMessage( to, subject, text.toString() );
						emailMsg.setFrom( energyCompany.getAdminEmailAddress() );
						emailMsg.send();
					}
					catch (Exception e) {
						com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
					}
				}
			}
			catch (com.cannontech.common.util.CommandExecutionException e) {
				com.cannontech.clientutils.CTILogger.error( e.getMessage(), e );
			}
		}
				
		CTILogger.info( "*** End SendControlOdds task ***" );
	}

}
