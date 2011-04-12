/*
 * Created on Nov 5, 2003
 */
package com.cannontech.cttp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.cttp.schema.cttp_FailureType;
import com.cannontech.cttp.schema.cttp_OperationType;
import com.cannontech.cttp.schema.cttp_UserLoginType;
import com.cannontech.cttp.schema.cttp_dtd_v0r1Doc;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.device.lm.LMGroup;
import com.cannontech.database.data.device.lm.MacroGroup;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.database.db.macro.GenericMacro;
import com.cannontech.database.db.pao.PAOowner;

/**
 * @author aaron
 */
public class Cttp {
	
	public static final String CTTP_VERSION = "CTTP-V0R1";
	
	// CTTP Response codes, see CTTP spec for meanings.
	public static final int INPUT_CANNOT_BE_PARSED = 300;
	public static final int DTD_NOT_FOUND = 301;
    public static final int XML_VALIDATION_ERROR = 302;
	public static final int VERSION_FORMAT_INCORRECT = 303;
	public static final int VERSION_NOT_SUPPORTED = 304;
	public static final int XML_DOES_NOT_CONTAIN_REQUEST = 305;
	
	public static final int UNKNOWN_USERID = 400;
	public static final int INCORRECT_PASSWORD = 401;
	public static final int UNKNOWN_GROUPID = 403;
	public static final int UNKNOWN_TRACKING_CODE = 404;
	public static final int DUPLICATE_GROUPID = 405;
	public static final int UNKOWN_UTILITYID = 406;
	public static final int GROUP_ASSOCIATED_WITH_DIFFERENT_UTLIITY = 407;
	public static final int COMMAND_SEND_FROM_DIFFERENT_UTLITIY = 408;
	
	public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss";
		
	public static final String CTTP_SERVER_STATUS_REQUEST = "CTTP SERVER STATUS REQUEST";
	public static final String CTTP_GROUP_STATUS_REQUEST = "CTTP GROUP STATUS REQUEST";
	public static final String CTTP_SUBMIT_COMMAND_REQUEST = "CTTP SUBMIT COMMAND REQUEST";
	public static final String CTTP_COMMAND_STATUS_REQUEST = "CTTP COMMAND STATUS REQUEST";
	
	private CttpMessageHandlerProvider messageHandlerProvider = new CttpMessageHandlerProvider();
	
	private static final SimpleDateFormat cttpDateFormat = new SimpleDateFormat(DATE_FORMAT_PATTERN);
	static {
		cttpDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	//	Prefix any tracking ids with this
	public static final String TRACKING_ID_PREFIX = "cannontech";
	
	private static ThreadLocal cttpHolder = new ThreadLocal();
	
	//return a threadlocal instance
	public static Cttp getInstance() {
		Cttp cttp = (Cttp) cttpHolder.get();
		if(cttp == null) {
			cttp = new Cttp();
			cttpHolder.set(cttp);
		}
		return cttp;
	}
	
	private Cttp() {
	}
	
	/**
	 * Check cttp-UserLogin
	 * Handle the request
	 * @param is
	 * @param out
	 * @throws Exception
	 */
	public void handleMessage(InputStream is, OutputStream os) throws Exception {
		BufferedReader rdr = new BufferedReader(new InputStreamReader(is));
		String in;
		StringBuffer inMsg = new StringBuffer();
		while( (in = rdr.readLine())!= null)  {
			inMsg.append(in);
		}
		
		//if(inMsg.length() == 0) 
		//	return;
			
		CTILogger.info("CTTP INCOMING:");
		CTILogger.info(inMsg.toString());
			
		//Check login		
		cttp_dtd_v0r1Doc doc = new cttp_dtd_v0r1Doc();
		cttp_OperationType cttpOpIn = new cttp_OperationType(doc.load(new ByteArrayInputStream(inMsg.toString().getBytes())));
		cttp_UserLoginType userLogin = cttpOpIn.getcttp_UserLogin();
		
		//TODO: redundant code here?
		String username = userLogin.getuserID().toString().trim();
		String password = userLogin.getuserPassword().toString().trim();
		LiteYukonUser user = DaoFactory.getAuthDao().login(username,password);
		
//		if(user == null) {
			//CTILogger.info("cttp-UserLogin failure, userid: " + username + " password: " + password + " utilityid: " + userLogin.getutilityID().toString());
			//sendLoginFailure(ordr);
			//return;
	//	}

		CttpRequest req = new CttpRequest(user, cttpOpIn);
		CttpMessageHandler msgHandler = 
			messageHandlerProvider.getMessageHandler(req);
		
		CttpResponse resp = msgHandler.handleMessage(req);
	
	
		cttp_dtd_v0r1Doc cttpDoc = new cttp_dtd_v0r1Doc();
		cttpDoc.setRootElementName(null, "cttp-Operation");
	
		cttpDoc.save(os, resp.getCttpOperation());	
		
		CTILogger.info("CTTP OUTGOING:");
		cttpDoc.save(System.out, resp.getCttpOperation());
	}
	
	/**
	 * Format a java date into a cttp date string.
	 * SimpleDateFormats aren't thread safe is why I chose this method.
	 * @param dateStr
	 * @return
	 */
	public static String formatCTTPDate(Date date) {
		synchronized(cttpDateFormat) {
			return cttpDateFormat.format(date);
		}
	}
	
	/**
	 * Creates a java date base on a cttp date string.
	 * SimpleDateFormats aren't thread safe is why I chose this method
	 * @param dateStr
	 * @return
	 */
	public static Date parseCTTPDate(String dateStr) throws ParseException {
		synchronized(cttpDateFormat) {
			return cttpDateFormat.parse(dateStr);
		}
	}
	
	public static ArrayList retrieveLMGroups(LiteYukonUser user) throws SQLException{
		  PAOowner[] pao = null;
		  Connection conn = null;
		  try {	
			  conn = 	PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
			  pao = PAOowner.getAllPAOownerChildren(new Integer(user.getLiteID()), conn);
		  }
		  finally {
			  if(conn != null) conn.close();
		  }

		  ArrayList lmGroups = new ArrayList();				
		  if(pao != null) {
			  for(int i = 0; i < pao.length; i++) {
				  Integer childID = pao[i].getChildID();
				  LiteYukonPAObject litePao = DaoFactory.getPaoDao().getLiteYukonPAO(childID.intValue());
				  if(litePao != null) {
				  	 
					if(litePao.getPaoType() == PaoType.LM_GROUP_VERSACOM ||
					   litePao.getPaoType() == PaoType.LM_GROUP_EXPRESSCOMM ||
					   litePao.getPaoType() == PaoType.MACRO_GROUP ) {
						  lmGroups.add(litePao);
					}
				  }
			  }
		 }
		 return lmGroups;
	}
	/**
	 * Returns a list List<LiteYukonPAObject that represetn the the lm groups in a macro group.
	 * @param macroGroup
	 * @return
	 */
	public static List retrieveLMGroupChildren(int macroID) {
		List retList = new ArrayList();
		MacroGroup mg = (MacroGroup) CttpCmdCache.getInstance().retrieveGroup(macroID);
		Iterator mIter = mg.getMacroGroupVector().iterator();
		while(mIter.hasNext()) {
			GenericMacro genMac = (GenericMacro) mIter.next();
			LiteYukonPAObject lg = DaoFactory.getPaoDao().getLiteYukonPAO(genMac.getChildID().intValue());
			retList.add(lg);
		}
		return retList;
	}
	
	/**
	 * Convenience method to build a cttpfailure
	 * @param cttpFailureCode
	 * @param reason
	 * @return
	 * @throws Exception
	 */
	public static cttp_FailureType makeFailure(int cttpFailureCode, String reason) throws Exception {
		cttp_FailureType cttpFailure = new cttp_FailureType();
		cttpFailure.adderrorCode(Integer.toString(cttpFailureCode));
		cttpFailure.adderrorText(reason);
		return cttpFailure;
	}
	
	public static int getNumberOfStatsInGroup(LMGroup lmGroup) {
		int total = 0;
		if(lmGroup instanceof MacroGroup) {
			MacroGroup mg = (MacroGroup) lmGroup;
			Iterator iter = mg.getMacroGroupVector().iterator();
			while(iter.hasNext()) {
				GenericMacro gm = (GenericMacro) iter.next();
				LMGroup childGroup = CttpCmdCache.getInstance().retrieveGroup(gm.getChildID().intValue());
				total += childGroup.getLmGroup().getKwCapacity().intValue();
			}
		}
		else {
			total = lmGroup.getLmGroup().getKwCapacity().intValue();
		}
		
		return total;
	}
	/*
	public static LMGroup[] getMacroGroupChildren(MacroGroup mGrp) {
		LMGroup[] grpArr = new LMGroup[mGrp.getMacroGroupVector().size()];
		
	 	Iterator iter = mGrp.getMacroGroupVector().iterator();
		for(int i = 0; i < mGrp.getMacroGroupVector().size(); i++) {
			GenericMacro gm = (GenericMacro) mGrp.getMacroGroupVector().get(i);
			LMGroup cg = CttpCmdCache.getInstance().retrieveGroup(gm.getChildID().intValue());
			grpArr[i] = cg;
		}
		return grpArr;
	}*/
}