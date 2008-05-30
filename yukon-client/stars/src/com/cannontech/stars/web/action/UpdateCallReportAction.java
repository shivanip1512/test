package com.cannontech.stars.web.action;

import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.soap.SOAPMessage;

import com.cannontech.clientutils.CTILogger;
import com.cannontech.database.Transaction;
import com.cannontech.database.data.lite.stars.LiteStarsCustAccountInformation;
import com.cannontech.database.db.stars.report.CallReportBase;
import com.cannontech.servlet.YukonUserContextUtils;
import com.cannontech.stars.util.ServletUtils;
import com.cannontech.stars.util.WebClientException;
import com.cannontech.stars.web.StarsYukonUser;
import com.cannontech.stars.xml.StarsFactory;
import com.cannontech.stars.xml.serialize.CallType;
import com.cannontech.stars.xml.serialize.StarsCallReport;
import com.cannontech.stars.xml.serialize.StarsCallReportHistory;
import com.cannontech.stars.xml.serialize.StarsCustAccountInformation;
import com.cannontech.stars.xml.serialize.StarsFailure;
import com.cannontech.stars.xml.serialize.StarsOperation;
import com.cannontech.stars.xml.serialize.StarsUpdateCallReport;
import com.cannontech.stars.xml.serialize.StarsUpdateCallReportResponse;
import com.cannontech.stars.xml.util.SOAPUtil;
import com.cannontech.stars.xml.util.StarsConstants;
import com.cannontech.user.YukonUserContext;

/**
 * @author yao
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class UpdateCallReportAction implements ActionBase {

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#build(HttpServletRequest, HttpSession)
	 */
	public SOAPMessage build(HttpServletRequest req, HttpSession session) {
		try {
			YukonUserContext yukonUserContext = YukonUserContextUtils.getYukonUserContext(req);
			TimeZone tz = yukonUserContext.getTimeZone();

			StarsUpdateCallReport updateCall = new StarsUpdateCallReport();
			updateCall.setCallID(Integer.parseInt(req.getParameter("CallID")));
			if (req.getParameter("CallNo") != null)
				updateCall.setCallNumber(req.getParameter("CallNo"));
			if (req.getParameter("TakenBy") != null)
				updateCall.setTakenBy(req.getParameter("TakenBy"));
			if (req.getParameter("Description") != null)
				updateCall.setDescription(req.getParameter("Description"));

			if (req.getParameter("CallDate") != null) {
				java.util.Date callDate =
					ServletUtils.parseDateTime(
						req.getParameter("CallDate"),
						req.getParameter("CallTime"),
						tz);
				if (callDate == null)
					throw new WebClientException(
						"Invalid date/time format '"
							+ req.getParameter("CallDate")
							+ " "
							+ req.getParameter("CallTime")
							+ "', the date/time should be in the form of 'mm/dd/yy hh:mm'");
				updateCall.setCallDate(callDate);
			}

			if (req.getParameter("CallType") != null) {
				CallType callType = new CallType();
				callType.setEntryID(
					Integer.parseInt(req.getParameter("CallType")));
				updateCall.setCallType(callType);
			}

			StarsOperation operation = new StarsOperation();
			operation.setStarsUpdateCallReport(updateCall);

			return SOAPUtil.buildSOAPMessage(operation);
		} catch (WebClientException e) {
			session.setAttribute(
				ServletUtils.ATT_ERROR_MESSAGE,
				e.getMessage());
		} catch (Exception e) {
			CTILogger.error(e.getMessage(), e);
			session.setAttribute(
				ServletUtils.ATT_ERROR_MESSAGE,
				"Invalid request parameters");
		}

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#process(SOAPMessage, HttpSession)
	 */
	public SOAPMessage process(SOAPMessage reqMsg, HttpSession session) {
		StarsOperation respOper = new StarsOperation();

		try {
			StarsOperation reqOper = SOAPUtil.parseSOAPMsgForOperation(reqMsg);

			StarsYukonUser user =
				(StarsYukonUser) session.getAttribute(
					ServletUtils.ATT_STARS_YUKON_USER);
			if (user == null) {
				respOper.setStarsFailure(
					StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_SESSION_INVALID,
						"Session invalidated, please login again"));
				return SOAPUtil.buildSOAPMessage(respOper);
			}

			LiteStarsCustAccountInformation accountInfo =
				(LiteStarsCustAccountInformation) session.getAttribute(
					ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);

			StarsUpdateCallReport updateCall =
				reqOper.getStarsUpdateCallReport();

			StarsCallReport starsCall = null;
			for (int i = 0;
				i < accountInfo.getCallReportHistory().size();
				i++) {
				StarsCallReport call =
					(StarsCallReport) accountInfo.getCallReportHistory().get(i);
				if (call.getCallID() == updateCall.getCallID()) {
					starsCall = call;
					break;
				}
			}

			if (updateCall.getCallNumber() != null
				&& !updateCall.getCallNumber().equals(starsCall.getCallNumber())
				&& CallReportBase.callNumberExists(
					updateCall.getCallNumber(),
					new Integer(user.getEnergyCompanyID()))) {
				respOper.setStarsFailure(
					StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_INVALID_PRIMARY_FIELD,
						"Call # already exists, please enter a different one"));
				return SOAPUtil.buildSOAPMessage(respOper);
			}

			com.cannontech.database.db.stars.report.CallReportBase callDB =
				new com.cannontech.database.db.stars.report.CallReportBase();
			StarsFactory.setCallReportBase(callDB, starsCall);
			callDB.setCallID(new Integer(starsCall.getCallID()));
			callDB.setAccountID(
				new Integer(accountInfo.getCustomerAccount().getAccountID()));
			StarsFactory.setCallReportBase(callDB, updateCall);

			callDB =
				(com
					.cannontech
					.database
					.db
					.stars
					.report
					.CallReportBase) Transaction
					.createTransaction(Transaction.UPDATE, callDB)
					.execute();

			StarsCallReport call = StarsFactory.newStarsCallReport(callDB);
			StarsUpdateCallReportResponse resp =
				new StarsUpdateCallReportResponse();
			resp.setStarsCallReport(call);

			respOper.setStarsUpdateCallReportResponse(resp);
			return SOAPUtil.buildSOAPMessage(respOper);
		} catch (Exception e) {
			CTILogger.error(e.getMessage(), e);

			try {
				respOper.setStarsFailure(
					StarsFactory.newStarsFailure(
						StarsConstants.FAILURE_CODE_OPERATION_FAILED,
						"Cannot update the call reports"));
				return SOAPUtil.buildSOAPMessage(respOper);
			} catch (Exception e2) {
				CTILogger.error(e2.getMessage(), e2);
			}
		}

		return null;
	}

	/**
	 * @see com.cannontech.stars.web.action.ActionBase#parse(SOAPMessage, SOAPMessage, HttpSession)
	 */
	public int parse(
		SOAPMessage reqMsg,
		SOAPMessage respMsg,
		HttpSession session) {
		try {
			StarsOperation operation =
				SOAPUtil.parseSOAPMsgForOperation(respMsg);

			StarsFailure failure = operation.getStarsFailure();
			if (failure != null) {
				session.setAttribute(
					ServletUtils.ATT_ERROR_MESSAGE,
					failure.getDescription());
				return failure.getStatusCode();
			}

			StarsCustAccountInformation accountInfo =
				(StarsCustAccountInformation) session.getAttribute(
					ServletUtils.TRANSIENT_ATT_LEADING
						+ ServletUtils.ATT_CUSTOMER_ACCOUNT_INFO);

			StarsCallReportHistory callHist =
				accountInfo.getStarsCallReportHistory();
			StarsCallReport call =
				operation
					.getStarsUpdateCallReportResponse()
					.getStarsCallReport();

			for (int i = 0; i < callHist.getStarsCallReportCount(); i++) {
				if (callHist.getStarsCallReport(i).getCallID()
					== call.getCallID()) {
					callHist.setStarsCallReport(i, call);
					break;
				}
			}

			return 0;
		} catch (Exception e) {
			CTILogger.error(e.getMessage(), e);
		}

		return StarsConstants.FAILURE_CODE_RUNTIME_ERROR;
	}

}
