package com.cannontech.stars.honeywell.action;

import javax.servlet.http.*;
import javax.xml.soap.*;
import com.cannontech.stars.web.action.*;

import java.io.*;
import java.util.*;
import com.cannontech.stars.xml.util.*;
import com.cannontech.stars.xml.serialize.*;
import com.cannontech.stars.xml.serialize.types.*;
import com.cannontech.stars.honeywell.serialize.*;
import com.cannontech.database.Transaction;

/**
 * <p>Title: SearchCustAccountAction.java</p>
 * <p>Description: </p>
 * <p>Generation Time: Jul 31, 2002 11:52:50 AM</p>
 * <p>Company: Cannon Technologies Inc.</p>
 * @author yao
 * @version 1.0
 */
public class SearchCustAccountAction extends ActionBase {
	
	private static final String SOAP_SERVER_URL = "http://205.167.68.23/aus1/service1.asmx";
	
	private static SOAPElementFactory elmtFac = null;
	
	private SOAPMessenger soapMsgr = new SOAPMessenger( SOAP_SERVER_URL );

	private SOAPElementFactory getElementFactory()	throws Exception {
		if (elmtFac == null)
			elmtFac = SOAPElementFactory.newInstance();
		return elmtFac;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try { 
			SOAPMessage message = SOAPUtil.createMessage();
			message.getMimeHeaders().addHeader("SOAPAction" , "http://hdmc.com/ws/GetALL");
			
			SOAPEnvelope env = message.getSOAPPart().getEnvelope();
			SOAPElement elem = getElementFactory().create("GetALL" , "" , "http://hdmc.com/ws/");
			env.getBody().addChildElement(elem);
			
			return message;
		}
		catch (Exception e)  {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		try { 
			return soapMsgr.call(reqMsg);
		}
		catch (Exception  e)  {
			e.printStackTrace();
		}
		
		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, HttpSession)
	 */
	public int parse(SOAPMessage respMsg, HttpSession session) {
		int appCnt = 1;
		int invCnt = 1;
		Hashtable invTable = new Hashtable();
		Hashtable progTable = new Hashtable();
		
		try { 
			SOAPEnvelope env = respMsg.getSOAPPart().getEnvelope();
			Iterator it = env.getBody().getChildElements();
			SOAPElement element = null;
			
			while (it.hasNext()) {
				SOAPElement elem = (SOAPElement) it.next();
				if (elem.getElementName().getLocalName().equals("GetALLResponse")) {
					element = elem;
					break;
				}
			}
			
			if (element == null) { 
				XMLUtil.getLogger( SearchCustAccountAction.class ).debug("GetALLResponse Not Found");
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			}
			
			it = element.getChildElements();
			element = null;
			
			while (it.hasNext()) {
				SOAPElement elem = (SOAPElement) it.next();
				if ( elem.getElementName().getLocalName().equals("GetALLResult") ) {
					element = elem;
					break;
				}
			}
			
			if (element == null) { 
				XMLUtil.getLogger( SearchCustAccountAction.class ).debug("GetALLResult Not Found");
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			}
			
			it = element.getChildElements();
			element = null;
			
			while (it.hasNext()) {
				SOAPElement elem = (SOAPElement) it.next();
				if ( elem.getElementName().getLocalName().equals("diffgram") ) {
					element = elem;
					break;
				}
			}
			
			if ( element == null ) { 
				XMLUtil.getLogger( SearchCustAccountAction.class ).debug("diffgram Not Found");
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			}
			
			it = element.getChildElements();
			element = null;
			
			while (it.hasNext()) {
				SOAPElement elem = (SOAPElement) it.next();
				if ( elem.getElementName().getLocalName().equals("DataSetALL") ) {
					element = elem;
					break;
				}
			}
			
			if ( element == null ) { 
				XMLUtil.getLogger( SearchCustAccountAction.class ).debug("DataSetALL Not Found");
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			}
			
			String elemStr = SOAPUtil.parseSOAPElement(respMsg , element.getElementName());
			StringReader sr = new StringReader( elemStr );
			DataSetALL dataSet = DataSetALL.unmarshal( sr );
			if ( dataSet.getDataSetALLItemCount() == 0 ) { 
				XMLUtil.getLogger( SearchCustAccountAction.class ).debug("DataSetALL is empty");
				return StarsConstants.FAILURE_CODE_NODE_NOT_FOUND;
			}
			
			StarsCustAccountInfo acctInfo = new StarsSearchCustomerAccountResponse();
			acctInfo.setStarsCustomerAccount( new StarsCustomerAccount() );
			acctInfo.setStarsAppliances( new StarsAppliances() );
			acctInfo.setStarsInventories( new StarsInventories() );
			acctInfo.setStarsLMPrograms( new StarsLMPrograms() );

			for (int i = 0; i < dataSet.getDataSetALLItemCount(); i++) {
				DataSetALLItem item = dataSet.getDataSetALLItem(i);
				
				if ( item.getCustomerInformation() != null ) {
					CustomerInformation hnwlCustInfo = item.getCustomerInformation();
					StarsCustomerAccount account = acctInfo.getStarsCustomerAccount();
					
					account.setIsCommercial(false);
					account.setCompany("");
					account.setAccountNumber( hnwlCustInfo.getACCOUNTNUMBER() );
					account.setAccountNotes( hnwlCustInfo.getACCOUNTNOTES() );
					account.setPropertyNumber( hnwlCustInfo.getPROPERTYNUMBER() );
					account.setPropertyNotes( hnwlCustInfo.getPROPERTYNOTES() );
					
					StreetAddress propAddr = new StreetAddress();
					propAddr.setStreetAddr1( hnwlCustInfo.getSTREETADDRESS1() );
					propAddr.setStreetAddr2( hnwlCustInfo.getSTREETADDRESS2() );
					propAddr.setCity( hnwlCustInfo.getSTREETADDRESSCITY() );
					propAddr.setState( hnwlCustInfo.getSTREETADDRESSSTATECODE() );
					propAddr.setZip( hnwlCustInfo.getSTREETADDRESSZIPCODE() );
					account.setStreetAddress( propAddr );
					
					StarsSiteInformation siteInfo = new StarsSiteInformation();
					siteInfo.setSubstationName( hnwlCustInfo.getSUBSTATIONNAME() );
					siteInfo.setFeeder( hnwlCustInfo.getFEEDER() );
					siteInfo.setPole( hnwlCustInfo.getPOLE() );
					siteInfo.setTransformerSize( hnwlCustInfo.getTRANSFORMERSIZE() );
					siteInfo.setServiceVoltage( hnwlCustInfo.getSERVICEVOLTAGE() );
					account.setStarsSiteInformation( siteInfo );
					
					BillingAddress billAddr = new BillingAddress();
					billAddr.setStreetAddr1( hnwlCustInfo.getBILLINGADDRESS1() );
					billAddr.setStreetAddr2( hnwlCustInfo.getBILLINGADDRESS2() );
					billAddr.setCity( hnwlCustInfo.getBILLINGADDRESSCITY() );
					billAddr.setState( hnwlCustInfo.getBILLINGADDRESSSTATECODE() );
					billAddr.setZip( hnwlCustInfo.getBILLINGADDRESSZIPCODE() );
					account.setBillingAddress( billAddr );
				}
				else if ( item.getCustomerContact() != null ) {
					CustomerContact hnwlContact = item.getCustomerContact();
					StarsCustomerAccount account = acctInfo.getStarsCustomerAccount();
					
					PrimaryContact primContact = new PrimaryContact();
					primContact.setLastName( hnwlContact.getLASTNAME() );
					primContact.setFirstName( hnwlContact.getFIRSTNAME() );
					primContact.setHomePhone( hnwlContact.getPHONE1() );
					primContact.setWorkPhone( hnwlContact.getPHONE2() );
					account.setPrimaryContact( primContact );
				}
				else if ( item.getLMAppliance() != null ) {
					LMAppliance hnwlApp = item.getLMAppliance();					
					StarsAppliance app = new StarsAppliance();
					app.setApplianceID( appCnt++ );
					app.setNotes( hnwlApp.getNOTES() );
					
					Integer progID = HoneywellApplianceToLMProgramMapping.getLMProgramID( hnwlApp.getAPPLIANCE_ID() );
					if ( progID != null ) {
						app.setLmProgramID( progID.intValue() );
						StarsLMProgram program = (StarsLMProgram) progTable.get( progID );
						if ( program == null ) {
							com.cannontech.database.data.device.lm.LMProgramBase prog = new com.cannontech.database.data.device.lm.LMProgramDirect();
							prog.setPAObjectID( progID );
							Transaction transaction = Transaction.createTransaction( Transaction.RETRIEVE, prog );
							transaction.execute();
							
							program = new StarsLMProgram();
							program.setProgramID( progID.intValue() );
							program.setProgramName( prog.getPAOName() );
							
							Integer groupID = HoneywellLMHardwareConfiguration.getAddressingGroupID( hnwlApp.getINVENTORYID() , hnwlApp.getAPPLIANCE_ID() );
							if ( groupID != null ) {
								program.setGroupID( groupID.intValue() );
								StarsLMControlHistory ctrlHist = LMControlHistory.getStarsLMControlHistory(groupID , StarsCtrlHistPeriod.PASTDAY , true);
								program.setStarsLMControlHistory( ctrlHist );
							}
							else { 
								program.setGroupID(0);
							}
							
							StarsLMPrograms programs = acctInfo.getStarsLMPrograms();
							programs.addStarsLMProgram( program );
							progTable.put(progID , program);
						}
					}
					else { 
						app.setLmProgramID(0);
					}
					
					Integer invID = (Integer) invTable.get( hnwlApp.getINVENTORYID() );
					if ( invID == null ) {
						invID = new Integer( invCnt++ );
						invTable.put(hnwlApp.getINVENTORYID() , invID);
					}
					
					app.setInventoryID( invID.intValue() );
					app.setCategoryDescription( hnwlApp.getDESCRIPTION() );
					String category = HoneywellToYukonApplianceCategoryMapping.getYukonApplianceCategory( hnwlApp.getTYPE_CD() );
					if ( category != null ) {
						app.setApplianceCategory( category );
					}
					else { 
						app.setApplianceCategory("(Unknown)");
					}
					
					StarsAppliances appliances = acctInfo.getStarsAppliances();
					appliances.addStarsAppliance( app );
				}
				else if ( item.getLMHardware() != null ) {
					LMHardware hnwlHw= item.getLMHardware();
					StarsLMHardware lmHw = new StarsLMHardware();
					
					Integer invID = (Integer) invTable.get( hnwlHw.getINVENTORYID() );
					if ( invID == null ) {
						invID = new Integer( invCnt++ );
						invTable.put( hnwlHw.getINVENTORYID(), invID );
					}
					
					lmHw.setInventoryID( invID.intValue() );
					lmHw.setCategory( "OneWayReceiver" );
					lmHw.setInstallationCompany( hnwlHw.getINSTALLATIONCOMPANYNAME() );
					if ( hnwlHw.getRECEIVEDATE() != null )
						lmHw.setReceiveDate( new org.exolab.castor.types.Date(hnwlHw.getRECEIVEDATE()) );
					else if ( hnwlHw.getINSTALLDATE() != null )
						lmHw.setInstallDate( new org.exolab.castor.types.Date(hnwlHw.getINSTALLDATE()) );
					else if ( hnwlHw.getREMOVEDATE() != null )
						lmHw.setRemoveDate( new org.exolab.castor.types.Date(hnwlHw.getREMOVEDATE()) );
					lmHw.setAltTrackingNumber( hnwlHw.getALTERNATETRACKINGNUMBER() );
					lmHw.setVoltage( hnwlHw.getVOLTAGE() );
					lmHw.setNotes( hnwlHw.getNOTES() );
					lmHw.setManufactureSerialNumber( hnwlHw.getMANUFACTURERSERIALNUMBER() );
					lmHw.setLMDeviceType( hnwlHw.getLMDEVICETYPE() );
					
					StarsInventories inventories = acctInfo.getStarsInventories();
					inventories.addStarsLMHardware( lmHw );
				}
			}
			
			session.setAttribute("CUSTOMER_ACCOUNT_INFORMATION" , acctInfo);
			return 0;
		}
		catch (Exception  e)  {
			e.printStackTrace();
		}
		
		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
