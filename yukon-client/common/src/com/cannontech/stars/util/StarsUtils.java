/*
 * Created on Dec 22, 2004
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package com.cannontech.stars.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.AuthDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.lite.LiteContactNotification;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.roles.consumer.ResidentialCustomerRole;
import com.cannontech.spring.YukonSpringHook;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteLMProgramWebPublishing;
import com.cannontech.stars.database.data.lite.LiteServiceCompany;
import com.cannontech.stars.database.data.lite.LiteSubstation;
import com.cannontech.stars.database.data.lite.LiteWebConfiguration;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsEnrLMProgram;
import com.cannontech.stars.xml.serialize.StarsInventories;
import com.cannontech.stars.xml.serialize.StarsInventory;
import com.google.common.collect.Lists;

public class StarsUtils {
    
    // Directory for all the temporary files
    private static final String STARS_TEMP_DIR = "stars_temp";
    private static final String SWITCH_BATCH_PATH = "switch.batch.path";
    
	// If date in database is earlier than this, than the date is actually empty
	public static long VERY_EARLY_TIME = 1000 * 3600 * 24;
    
    public static final String MCT_BASE_DESIGNATION = "yukon";
    public static final String METER_BASE_DESIGNATION = "stars";
    
    public static final String BATCH_SWITCH_COMMAND_AUTO = "auto";
    public static final String BATCH_SWITCH_COMMAND_MANUAL = "manual";
    
	public static final SimpleDateFormat starsDateFormat = new SimpleDateFormat("yyyyMMdd");
	public static final SimpleDateFormat starsTimeFormat = new SimpleDateFormat("HHmm");
	private static final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("MM/dd/yy HH:mm");
	
	// Default sender email address from Stars
	public static final String ADMIN_EMAIL_ADDRESS = "info@cannontech.com";
	
	public static final Comparator<YukonListEntry> YUK_LIST_ENTRY_ALPHA_CMPTR = new Comparator<YukonListEntry>() {
		@Override
        public int compare(YukonListEntry entry1, YukonListEntry entry2) {
			int res = entry1.getEntryText().compareToIgnoreCase(entry2.getEntryText());
			if (res == 0) res = entry1.getEntryID() - entry2.getEntryID();
			return res;
		}
	};
	
	public static final Comparator<LiteServiceCompany> SERVICE_COMPANY_CMPTR = new Comparator<LiteServiceCompany>() {
		@Override
        public int compare(LiteServiceCompany o1, LiteServiceCompany o2) {
			int res = o1.getCompanyName().compareToIgnoreCase(o2.getCompanyName());
			if (res == 0) res = o1.getCompanyID() - o2.getCompanyID();
			return res;
		}
	};
	
	public static final Comparator<LiteSubstation> SUBSTATION_CMPTR = new Comparator<LiteSubstation>() {
		@Override
        public int compare(LiteSubstation o1, LiteSubstation o2) {
			LiteSubstation sub1 = o1;
			LiteSubstation sub2 = o2;
			int res = sub1.getSubstationName().compareToIgnoreCase(sub2.getSubstationName());
			if (res == 0) res = sub1.getSubstationID() - sub2.getSubstationID();
			return res;
		}
	};
	
	/**
	 * Return date in the format of MM/dd/yy HH:mm in the specified time zone
	 */
    public static String formatDate(Date date, TimeZone tz) {
        tz = tz == null ? TimeZone.getDefault() : tz;
		dateTimeFormat.setTimeZone(tz);
		return dateTimeFormat.format(date);
	}
	
	public static Date translateDate(long time) {
		if (time < VERY_EARLY_TIME) return null;
		return new Date(time);
	}
	
    public static Timestamp translateTstamp(long time) {
        if (time < VERY_EARLY_TIME) return null;
        return new Timestamp(time);
    }
    
	public static String forceNotNull(String str) {
		return (str == null) ? "" : str.trim();
	}
	
	public static String forceNotNone(String str) {
		String str1 = forceNotNull(str);
		return (str1.equalsIgnoreCase("(none)")) ? "" : str1;
	}
	
	private static String[] readLines(Reader reader, boolean returnEmpty) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(reader);
			
			ArrayList<String> lines = Lists.newArrayList();
			String line = null;
			
			while ((line = br.readLine()) != null) {
				if (!returnEmpty && (line.trim().equals("") || line.charAt(0) == '#'))
					continue;
				lines.add(line);
			}
			
			String[] lns = new String[ lines.size() ];
			lines.toArray(lns);
			return lns;
		} finally {
			if (br != null) br.close();
		}
	}
	
	public static String[] readInputStream(InputStream is, boolean returnEmpty) {
		try {
			return readLines(new InputStreamReader(is), returnEmpty);
		}
		catch (IOException e) {
			CTILogger.error(e.getMessage(), e);
		}
		
		return null;
	}
	
	public static String[] readFile(File file, boolean returnEmpty) {
		if (file.exists()) {
			try {
				return readLines(new FileReader(file), returnEmpty);
			}
			catch (IOException e) {
				CTILogger.error(e.getMessage(), e);
			}
		} else {
			CTILogger.error("Unable to find file \"" + file.getPath() + "\"");
		}
		
		return null;
	}
	
	public static String[] readFile(File file) {
		return readFile(file, true);
	}
	
	public static void writeFile(File file, String[] lines) throws IOException {
		PrintWriter fw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		
		for (int i = 0; i < lines.length; i++) {
			fw.println(lines[i]);
		}
		
		fw.close();
	}
	
	public static String[] splitString(String str, String delim) {
		StreamTokenizer st = new StreamTokenizer(new StringReader(str));
		st.resetSyntax();
		st.wordChars(0, 255);
		st.quoteChar('"');
		for (int i = 0; i < delim.length(); i++)
			st.ordinaryChar(delim.charAt(i));
		
		ArrayList<String> tokenList = Lists.newArrayList();
		boolean isDelimLast = true;	// Whether the last token is a deliminator
		
		try {
			while (st.nextToken() != StreamTokenizer.TT_EOF) {
				if (isDelimLast) {
					if (st.ttype == StreamTokenizer.TT_WORD || st.ttype == '"') {
						tokenList.add(st.sval);
						isDelimLast = false;
					}
					else if (delim.indexOf(st.ttype) >= 0) {
						tokenList.add("");
					}
				}
				else {
					if (delim.indexOf(st.ttype) >= 0)
						isDelimLast = true;
				}
			}
		}
		catch (IOException e) {
			CTILogger.error(e.getMessage(), e);
			return null;
		}
		
		// If the line ends with a comma, add an empty string to the column list 
		if (isDelimLast) tokenList.add("");
		
		String[] tokens = new String[ tokenList.size() ];
		tokenList.toArray(tokens);
		return tokens;
	}
	
	/**
	 * Compares two readableInstants to see if the first readableInstant is before the
	 * second readableInstant.  This method will also treat any null readableInstants 
	 * as an instant of right now.
	 */
	public static boolean isReadableInsantBefore(ReadableInstant readableInstant1, 
                                                    ReadableInstant readableInstant2) {
	    ReadableInstant now = new Instant();
        ReadableInstant ri1 = (readableInstant1 != null) ? readableInstant1 : now;
        ReadableInstant ri2 = (readableInstant2 != null) ? readableInstant2 : now;
        return (ri1.isBefore(ri2));
    }

	/**
     * Compares two readableInstants to see if the first readableInstant is equal to the
     * second readableInstant.  This method will also treat any null readableInstants 
     * as an instant of right now.
     */
    public static boolean isReadableInsantEqual(ReadableInstant readableInstant1, 
                                                   ReadableInstant readableInstant2) {
        ReadableInstant now = new Instant();
        ReadableInstant ri1 = (readableInstant1 != null) ? readableInstant1 : now;
        ReadableInstant ri2 = (readableInstant2 != null) ? readableInstant2 : now;
        
        return ri1.isEqual(ri2);
    }

	public static String getNotification(LiteContactNotification liteNotif) {
		String notification = (liteNotif == null)? null : liteNotif.getNotification();
		return StarsUtils.forceNotNull(notification);
	}

	/**
	 * Returns name as "FirstName LastName"
	 * If only firstName, returns "firstname"
	 * If only lastName, returns "lastname"
     * If both empty, returns empty string 
	 */
	public static String formatName(String contactFirstName, String contactLastName) {
		StringBuffer name = new StringBuffer();
		
		String firstName = StarsUtils.forceNotNone(contactFirstName);
	    String lastName = StarsUtils.forceNotNone(contactLastName);
	    boolean hasLastName = StringUtils.isNotBlank(lastName); 

		if (StringUtils.isNotBlank(firstName)) {
			name.append(firstName);
		
    		// add a space only if we have a firstname and a lastname
    		if (hasLastName) {
    		    name.append(" ");
    		}
		}
		
		if (hasLastName) {
			name.append(lastName);
		}
		
		return name.toString();
	}

	public static String getPublishedProgramName(LiteLMProgramWebPublishing liteProg) {
		String progName = CtiUtilities.STRING_NONE;
		
		//display user-friendly name if exists, else Yukon name
		LiteWebConfiguration liteConfig = StarsDatabaseCache.getInstance().getWebConfiguration(liteProg.getWebSettingsID());
		if (liteConfig != null) {
		    String[] dispNames = StarsUtils.splitString(liteConfig.getAlternateDisplayName(), ",");
		    if (dispNames.length > 0 && dispNames[0].length() > 0)
		        progName = dispNames[0];
		}
		
		if (StringUtils.isBlank(progName) || progName.equalsIgnoreCase(CtiUtilities.STRING_NONE)) {
		    if (liteProg.getDeviceID() > 0) {
		        try {
		            progName = YukonSpringHook.getBean(PaoDao.class).getYukonPAOName(liteProg.getDeviceID());
		        } catch(NotFoundException e) {
		            CTILogger.error(e.getMessage(), e);
		        }
		    }		    
		}
		
		return progName;
	}
	
	public static Comparator<StarsEnrLMProgram> createLMProgramComparator() {
	    Comparator<StarsEnrLMProgram> comp = new Comparator<StarsEnrLMProgram>() {
	        @Override
	        public int compare(StarsEnrLMProgram o1, StarsEnrLMProgram o2) {
                Integer order1 = o1.getProgramOrder();
                Integer order2 = o2.getProgramOrder();
	            int result = order1.compareTo(order2);
	            return result;
	        }
	    };
	    return comp;
	}
	
	/**
	 * @deprecated There should never be any need to call these if the role properties are well designed.
	 */
	@Deprecated
	public static boolean isOperator(LiteYukonUser user) {
	    return !isResidentialCustomer(user) &&
	    YukonSpringHook.getBean(EnergyCompanyDao.class).getEnergyCompany(user) != null;
	}

    /**
     * @deprecated There should never be any need to call these if the role properties are well designed.
     */
	@Deprecated
	public static boolean isResidentialCustomer(LiteYukonUser user) {
		return YukonSpringHook.getBean(AuthDao.class).checkRole(user, ResidentialCustomerRole.ROLEID);
	}
	
	public static String getStarsTempDir() {
        final String fs = System.getProperty("file.separator");
        String serverBase = null;
        
        String catBase = System.getProperty("catalina.base");
        if (catBase != null) {
            serverBase = catBase;
        } 
        else {
            String yukonBase = System.getProperty("yukon.base");
            if (yukonBase != null) {
                serverBase = yukonBase;
            } 
            else {
                serverBase = "C:" + fs + "yukon";
            }
        }
        
        return serverBase + fs + STARS_TEMP_DIR;
    }
	
	public static String getFileWriteSwitchConfigDir() {
        String dirBase = null;
        
        String temp = System.getProperty(SWITCH_BATCH_PATH);
        if (temp != null) {
            dirBase = temp;
        } else {
            dirBase = StarsUtils.getStarsTempDir();
        }
        
        return dirBase;
    }
	
	public static void populateInventoryFields(StarsCustAccountInformation starsAcctInfo, StarsInventory starsInv) {
        StarsInventories inventories = starsAcctInfo.getStarsInventories();

        for (int i = 0; i < inventories.getStarsInventoryCount(); i++) {
            StarsInventory inv = inventories.getStarsInventory(i);
            if (inv.getInventoryID() == starsInv.getInventoryID()) {
                inventories.setStarsInventory(i, starsInv);
                break;
            }
        }
    }
}