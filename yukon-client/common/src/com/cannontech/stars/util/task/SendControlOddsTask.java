package com.cannontech.stars.util.task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.mail.internet.InternetAddress;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.model.ContactNotificationType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ContactDao;
import com.cannontech.core.dao.ContactNotificationDao;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.database.data.lite.LiteContact;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.core.dao.StarsCustAccountInformationDao;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteAccountInfo;
import com.cannontech.stars.database.data.lite.LiteLMProgramWebPublishing;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.database.data.lite.LiteStarsLMProgram;
import com.cannontech.stars.energyCompany.EnergyCompanySettingType;
import com.cannontech.stars.energyCompany.dao.EnergyCompanySettingDao;
import com.cannontech.stars.util.StarsUtils;
import com.cannontech.tools.email.EmailMessage;
import com.cannontech.tools.email.EmailService;

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
	@Override
    public void run() {
		CTILogger.info( "*** Start SendControlOdds task ***" );
		
		LiteStarsEnergyCompany energyCompany = StarsDatabaseCache.getInstance().getEnergyCompany( energyCompanyID );
		
		String LINE_SEPARATOR = System.getProperty("line.separator");

		List<LiteLMProgramWebPublishing> progList = new ArrayList<LiteLMProgramWebPublishing>();	// Programs that are eligible for notification
        Iterable<LiteLMProgramWebPublishing> programs = energyCompany.getAllPrograms();
        for (final LiteLMProgramWebPublishing program : programs) {
            if (program.getChanceOfControlID() != CtiUtilities.NONE_ZERO_ID) {
                progList.add( program );
            }
        }
		
		if (progList.size() > 0) {
			StringBuffer sql = new StringBuffer( "SELECT DISTINCT app.AccountID " )
					.append( "FROM ApplianceBase app, ECToAccountMapping map " )
					.append( "WHERE map.EnergyCompanyID = " )
					.append( energyCompanyID )
					.append( " AND map.AccountID = app.AccountID AND (" )
					.append( "app.ProgramID = " )
					.append( progList.get(0).getProgramID() );
			for (int i = 1; i < progList.size(); i++) {
				LiteLMProgramWebPublishing program = progList.get(i);
				sql.append(" OR app.ProgramID = ").append(program.getProgramID());
			}
			sql.append(")");
			
			com.cannontech.database.SqlStatement stmt = new com.cannontech.database.SqlStatement(
					sql.toString(), CtiUtilities.getDatabaseAlias() );
			try {
				stmt.execute();
				
				StarsCustAccountInformationDao starsCustAccountInformationDao = 
	                    YukonSpringHook.getBean("starsCustAccountInformationDao", StarsCustAccountInformationDao.class);
				EnergyCompanySettingDao ecSettingDao = YukonSpringHook.getBean(EnergyCompanySettingDao.class);
				
				for (int i = 0; i < stmt.getRowCount(); i++) {
					int accountID = ((java.math.BigDecimal) stmt.getRow(i)[0]).intValue();
					LiteAccountInfo accountInfo =
						starsCustAccountInformationDao.getByAccountId(accountID);
					
					LiteContact primContact = YukonSpringHook.getBean(ContactDao.class).getContact( accountInfo.getCustomer().getPrimaryContactID() );
					LiteContactNotification email = YukonSpringHook.getBean(ContactNotificationDao.class).getFirstNotificationForContactByType(
							primContact, ContactNotificationType.EMAIL );
					if (email == null || email.getDisableFlag().equalsIgnoreCase("Y")) {
                        continue;
                    }
					
					List<LiteStarsLMProgram> activeProgs = new ArrayList<LiteStarsLMProgram>();	// List of all the active programs
					for (int j = 0; j < accountInfo.getPrograms().size(); j++) {
						LiteStarsLMProgram program = accountInfo.getPrograms().get(j);
						if (progList.contains( program.getPublishedProgram() ) && program.isInService()) {
                            activeProgs.add( program );
                        }
					}
					if (activeProgs.size() == 0) {
                        continue;
                    }
					
					Map<String, String> programOddsMap = new HashMap<String, String>();
					int maxProgramNameLength = 18; // length of 'Program Enrollment' header
					for (int j = 0; j < activeProgs.size(); j++) {
						LiteStarsLMProgram program = activeProgs.get(j);
						String progName = StarsUtils.getPublishedProgramName( program.getPublishedProgram() );
						String ctrlOdds = YukonSpringHook.getBean(YukonListDao.class).getYukonListEntry( program.getPublishedProgram().getChanceOfControlID() ).getEntryText();
						
						int currentProgramNameLength = progName.length();
						if(currentProgramNameLength > maxProgramNameLength) {
							maxProgramNameLength = currentProgramNameLength;
						}
						programOddsMap.put(progName, ctrlOdds);

					}

					String ctrlOddsText = WordUtils.capitalize("odds for control");
					String subject = "Today's " + ctrlOddsText;
					
					StringBuffer messageText = new StringBuffer();

					String columnHeader = 
						StringUtils.rightPad("Program Enrollment", maxProgramNameLength + 2, " "); 
					messageText.append(columnHeader);
					messageText.append(ctrlOddsText);
					messageText.append( LINE_SEPARATOR );
					
					String lineText = StringUtils.leftPad("  ", maxProgramNameLength + 2, "=");
					lineText = 
						StringUtils.rightPad(lineText, lineText.length() + ctrlOddsText.length(), "=");
					messageText.append(lineText);
					messageText.append( LINE_SEPARATOR );
					
					for (String programName : programOddsMap.keySet()) {
						messageText.append(
								StringUtils.rightPad(programName, maxProgramNameLength + 2));
						messageText.append(programOddsMap.get(programName));
						messageText.append( LINE_SEPARATOR );
					}
					
					messageText.append( LINE_SEPARATOR );
					messageText.append( LINE_SEPARATOR );
					messageText.append( LINE_SEPARATOR );
					
					messageText.append("To unsubscribe from the notification list, please contact your program administrator or "
							  + "login to your account and do the following. "
							  + "On the first page (or the \"General\" link), uncheck the notification box and click \"Submit\".");
					
					messageText.append( LINE_SEPARATOR );
					messageText.append( LINE_SEPARATOR );
					
					try {
				        EmailService emailService = YukonSpringHook.getBean(EmailService.class);
				        String adminEmailAddress = ecSettingDao.getString(EnergyCompanySettingType.ADMIN_EMAIL_ADDRESS, energyCompany.getEnergyCompanyId());
				        if (adminEmailAddress == null || adminEmailAddress.trim().length() == 0) {
				            adminEmailAddress = StarsUtils.ADMIN_EMAIL_ADDRESS;
				        }
				        EmailMessage message = new EmailMessage(new InternetAddress(adminEmailAddress),
				                       InternetAddress.parse(email.getNotification()), subject, messageText.toString());
						emailService.sendMessage(message);
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
