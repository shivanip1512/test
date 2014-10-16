#include "precompiled.h"

#include "cmdparse.h"
#include "dev_grp_rfn_expresscom.h"
#include "expresscom.h"
#include "logger.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "numstr.h"
#include "utility.h"
#include "devicetypes.h"
#include "amq_connection.h"
#include "RfnBroadcastMessage.h"
#include "RfnBroadcastReplyMessage.h"
#include "ctistring.h"

#include "std_helper.h"

using std::string;
using std::endl;
using std::list;

using Cti::Logging::Vector::Hex::operator<<;
using Cti::MacroOffset;

YukonError_t CtiDeviceGroupRfnExpresscom::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::None;
    string resultString;

    static const int DEFAULT_EXPIRATION_20_MINUTES = 20*60;

    if( parse.isKeyValid("asciiraw") && gConfigParms.isTrue("ALLOW_RAW_PAGE_MESSAGES") )
    {
        string outputValue = parse.getsValue("asciiraw");

        std::vector<unsigned char> payload(outputValue.begin(), outputValue.end());

        sendDRMessage(OutMessage->Priority, DEFAULT_EXPIRATION_20_MINUTES, payload);

        resultString = "Device: " + getName() + " -- Raw ASCII Command sent \n\"" + outputValue + "\"";
        retList.push_back(CTIDBG_new CtiReturnMsg(getID(),
                                                  string(OutMessage->Request.CommandStr),
                                                  resultString,
                                                  nRet,
                                                  0,
                                                  MacroOffset::none,
                                                  OutMessage->Request.Attempt,
                                                  OutMessage->Request.GrpMsgID,
                                                  OutMessage->Request.UserID,
                                                  OutMessage->Request.SOE,
                                                  CtiMultiMsg_vec()));

        return nRet;
    }
    else if( parse.isKeyValid("hexraw") && gConfigParms.isTrue("ALLOW_RAW_PAGE_MESSAGES") )
    {
        CtiString outputValue = parse.getsValue("hexraw");
        if( (outputValue.size()%2) != 0 )
        {
            outputValue.append("0");
        }
        std::vector<unsigned char> payload;
        for(int i = 0; i < outputValue.size()/2; i++)
        {
            payload.push_back(strtoul(outputValue.substr(i*2,2).c_str(), NULL, 16));
        }

        sendDRMessage(OutMessage->Priority, DEFAULT_EXPIRATION_20_MINUTES, payload);

        resultString = "Device: " + getName() + " -- Raw hex Command sent \n\"" + (string)outputValue + "\"";
        retList.push_back(CTIDBG_new CtiReturnMsg(getID(),
                                                  string(OutMessage->Request.CommandStr),
                                                  resultString,
                                                  nRet,
                                                  0,
                                                  MacroOffset::none,
                                                  OutMessage->Request.Attempt,
                                                  OutMessage->Request.GrpMsgID,
                                                  OutMessage->Request.UserID,
                                                  OutMessage->Request.SOE,
                                                  CtiMultiMsg_vec()));

        return nRet;
    }
    else
    {

        /*
         *  This method should only be called by the dev_base method
         *   ExecuteRequest(CtiReturnMsg*) (NOTE THE DIFFERENCE IN ARGS)
         *   That method prepares an outmessage for submission to the internals..
         */
        if(parse.getiValue("type") != ProtocolExpresscomType)
        {
            parse.setValue("type", ProtocolExpresscomType);
            parse.parse();  // reparse for xcom specific data items....  This is required in case we got here from a group macro.
        }

        if( nRet = extractGroupAddressing(pReq, parse, OutMessage, vgList, retList, resultString) )
        {
            // extractGroupAddressing generates its own error return to the caller
            return nRet;
        }

        CtiProtocolExpresscom  xcom;
        xcom.parseAddressing(parse);                    // The parse holds all the addressing for the group.
        nRet = xcom.parseRequest(parse);

        if(nRet || xcom.entries() <= 0)
        {
            if (nRet == ClientErrors::None)
            {
                nRet = ClientErrors::BadParameter;
            }

            resultString = "Did not transmit Expresscom commands. Error " + CtiNumStr(nRet) + " - " + GetErrorString(nRet);
            retList.push_back(CTIDBG_new CtiReturnMsg(getID(),
                                                      string(OutMessage->Request.CommandStr),
                                                      resultString,
                                                      nRet,
                                                      0,
                                                      MacroOffset::none,
                                                      OutMessage->Request.Attempt,
                                                      OutMessage->Request.GrpMsgID,
                                                      OutMessage->Request.UserID,
                                                      OutMessage->Request.SOE,
                                                      CtiMultiMsg_vec()));

            string desc = "Route: RF Network";
            string actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route. Error " + CtiNumStr(nRet) + " - " + GetErrorString(nRet);
            vgList.push_back(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));

            return nRet;
        }

        xcom.setUseCRC(false);
        xcom.setUseASCII(true);

        std::vector<unsigned char> payload;
        xcom.getFullMessage(payload);

        sendDRMessage(OutMessage->Priority, OutMessage->ExpirationTime - CtiTime::now().seconds(), payload);

        reportAndLogControlStart(parse, vgList, OutMessage->Request.CommandStr);

        resultString = string(" Expresscom command (") + CtiNumStr(payload.size()) + " bytes) sent on RF network";

        if(!gConfigParms.isTrue("HIDE_PROTOCOL"))
        {
            resultString += " \n" + xcom.getMessageAsString();
        }

        retList.push_back(CTIDBG_new CtiReturnMsg(getID(),
                                                  string(OutMessage->Request.CommandStr),
                                                  resultString,
                                                  nRet,
                                                  0,
                                                  MacroOffset::none,
                                                  OutMessage->Request.Attempt,
                                                  OutMessage->Request.GrpMsgID,
                                                  OutMessage->Request.UserID,
                                                  OutMessage->Request.SOE,
                                                  CtiMultiMsg_vec()));

        return nRet;
    }
}

void logResponse(const unsigned short outboundMessageId, const std::vector<unsigned char> outboundMessagePayload, const Cti::Messaging::Rfn::RfnBroadcastReplyMessage &reply)
{
    Cti::StreamBuffer output;

    output <<"Received RFN broadcast reply message";
    output << endl <<"Outbound ID "<< outboundMessageId <<", outbound payload ["<< outboundMessagePayload <<"]";
    output << endl <<"Reply results: ";

    if( reply.gatewayResults.empty() )
    {
        output <<"[empty]";
    }
    else
    {
        std::map<boost::int64_t, const Cti::Messaging::Rfn::BroadcastResult *>::const_iterator itr;

        for( itr = reply.gatewayResults.begin(); itr != reply.gatewayResults.end(); ++itr )
        {
            output <<"[GW ID: "<< itr->first <<", "<< itr->second->description <<"] ";
        }
    }

    CTILOG_INFO(dout, output);
}

void CtiDeviceGroupRfnExpresscom::sendDRMessage(int priority, int expirationDuration, std::vector<unsigned char> &payload)
{
    using namespace Cti::Messaging;
    using namespace Cti::Messaging::Rfn;
    using Cti::Messaging::ActiveMQ::Queues::OutboundQueue;

    std::auto_ptr<const RfnBroadcastMessage> rfnBroadcastMessage(
            RfnBroadcastMessage::createMessage(
                    priority,
                    RfnBroadcastMessage::RfnMessageClass::DemandResponse,
                    expirationDuration,
                    payload));

    ActiveMQConnectionManager::CallbackFor<RfnBroadcastReplyMessage>::type callback =
            boost::bind(&logResponse, rfnBroadcastMessage->messageId, rfnBroadcastMessage->payload, _1);

    StreamableMessage::auto_type streamableMessage(rfnBroadcastMessage);

    ActiveMQConnectionManager::enqueueMessageWithCallbackFor<RfnBroadcastReplyMessage>(OutboundQueue::RfnBroadcast, streamableMessage, callback);
}

std::string CtiDeviceGroupRfnExpresscom::getSQLCoreStatement() const
{
    static const std::string sqlCore =
        "SELECT"
            " YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag,"
            " DV.deviceid, DV.alarminhibit, DV.controlinhibit, ECV.routeid, ECV.serialnumber,"
            " ECV.serviceaddress, ECV.geoaddress, ECV.substationaddress, ECV.feederaddress,"
            " ECV.zipcodeaddress, ECV.udaddress, ECV.programaddress, ECV.splinteraddress,"
            " ECV.addressusage, ECV.relayusage, ECV.protocolpriority"
        " FROM"
            " YukonPAObject YP, Device DV, ExpressComAddress_View ECV"
        " WHERE"
            " upper (YP.type) = 'RFN EXPRESSCOM GROUP'"
            " AND YP.paobjectid = ECV.lmgroupid"
            " AND YP.paobjectid = DV.deviceid";

    return sqlCore;
}
