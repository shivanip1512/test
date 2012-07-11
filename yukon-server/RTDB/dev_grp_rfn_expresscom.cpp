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
#include "rfnbroadcastmessage.h"

using std::string;
using std::endl;
using std::list;

INT CtiDeviceGroupRfnExpresscom::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList)
{
    INT   nRet = NoError;
    string resultString;

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

    if( (nRet = extractGroupAddressing(pReq, parse, OutMessage, vgList, retList, resultString)) != NoError )
    {
        // extractGroupAddressing generates its own error return to the caller
        return nRet;
    }

    CtiProtocolExpresscom  xcom;
    xcom.parseAddressing(parse);                    // The parse holds all the addressing for the group.
    nRet = xcom.parseRequest(parse);

    if(nRet != NoError || xcom.entries() <= 0)
    {
        if (nRet == NoError)
        {
            nRet = BADPARAM;
        }

        resultString = "Did not transmit Expresscom commands. Error " + CtiNumStr(nRet) + " - " + FormatError(nRet);
        retList.push_back(CTIDBG_new CtiReturnMsg(getID(), string(OutMessage->Request.CommandStr), resultString, nRet, 0, 0, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec()));

        string desc = "Route: RF Network";
        string actn = "FAILURE: Command \"" + parse.getCommandStr() + "\" failed on route. Error " + CtiNumStr(nRet) + " - " + FormatError(nRet);
        vgList.push_back(CTIDBG_new CtiSignalMsg(0, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));

        return nRet;
    }

    xcom.setUseCRC(false);
    xcom.setUseASCII(false);

    std::vector<unsigned char> payload;
    xcom.getFullMessage(payload);

    using namespace Cti::Messaging;
    using namespace Cti::Messaging::Rfn;
    std::auto_ptr<StreamableMessage> message(RfnBroadcastMessage::createMessage(OutMessage->Priority, RfnBroadcastMessage::RfnMessageClass::DemandResponse, OutMessage->ExpirationTime - CtiTime::now().seconds(), payload));

    gActiveMQConnection.enqueueMessage(ActiveMQConnectionManager::Queue_RfnBroadcast, message);

    reportAndLogControlStart(parse, vgList, OutMessage->Request.CommandStr);

    resultString = string(" Expresscom command (") + CtiNumStr(payload.size()) + " bytes) sent on RF network";

    if(!gConfigParms.isTrue("HIDE_PROTOCOL"))
    {
        resultString += " \n" + xcom.getMessageAsString();
    }

    retList.push_back(CTIDBG_new CtiReturnMsg(getID(), string(OutMessage->Request.CommandStr), resultString, nRet, 0, 0, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec()));

    return nRet;

}

string CtiDeviceGroupRfnExpresscom::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, ECV.routeid, ECV.serialnumber, "
                                     "ECV.serviceaddress, ECV.geoaddress, ECV.substationaddress, ECV.feederaddress, "
                                     "ECV.zipcodeaddress, ECV.udaddress, ECV.programaddress, ECV.splinteraddress, "
                                     "ECV.addressusage, ECV.relayusage, ECV.protocolpriority "
                                   "FROM YukonPAObject YP, Device DV, ExpressComAddress_View ECV "
                                   "WHERE upper (YP.type) = 'RFN EXPRESSCOM GROUP' AND YP.paobjectid = ECV.lmgroupid AND "
                                     "YP.paobjectid = DV.deviceid";

    return sqlCore;
}
