#include "precompiled.h"

#include "cmdparse.h"
#include "dev_grp_golay.h"
#include "expresscom.h"
#include "logger.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "numstr.h"
#include "utility.h"

using std::string;
using std::endl;
using std::list;

CtiDeviceGroupGolay::CtiDeviceGroupGolay()
{
}

const CtiTableSASimpleGroup& CtiDeviceGroupGolay::getLoadGroup() const
{
    return _loadGroup;
}

LONG CtiDeviceGroupGolay::getRouteID()
{
    return _loadGroup.getRouteId();
}

//===================================================================================================================
// This method determines what should be displayed in the "Description" column
// of the systemlog table when something happens to this device
//===================================================================================================================
string CtiDeviceGroupGolay::getDescription(const CtiCommandParser & parse) const
{
    CHAR  op_name[20];
    INT   mask = 1;
    string tmpStr;


    tmpStr = "Group: " + getName();

    return tmpStr;
}

string CtiDeviceGroupGolay::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, LMS.groupid, LMS.routeid, "
                                     "LMS.operationaladdress, LMS.nominaltimeout, LMS.markindex, LMS.spaceindex "
                                   "FROM YukonPAObject YP, Device DV, LMGroupSASimple LMS "
                                   "WHERE upper (YP.type) = 'GOLAY GROUP' AND YP.paobjectid = LMS.groupid AND "
                                     "YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void CtiDeviceGroupGolay::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    _loadGroup.DecodeDatabaseReader(rdr);
}

YukonError_t CtiDeviceGroupGolay::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::None;
    string resultString;

    CtiRouteSPtr Route;

    parse.setValue("type", ProtocolGolayType);
    parse.parse();

    bool control = (parse.getFlags() & CMD_FLAG_CTL_SHED);

    if(!control)
    {
        nRet = ClientErrors::BadParameter;

        resultString = " Cannot control Golay groups except with command \"control shed\"  :" + getName();
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                     string(OutMessage->Request.CommandStr),
                                                     resultString,
                                                     nRet,
                                                     OutMessage->Request.RouteID,
                                                     OutMessage->Request.RetryMacroOffset,
                                                     OutMessage->Request.Attempt,
                                                     OutMessage->Request.GrpMsgID,
                                                     OutMessage->Request.UserID,
                                                     OutMessage->Request.SOE,
                                                     CtiMultiMsg_vec());
        retList.push_back( pRet );

        CTILOG_ERROR(dout, resultString);
    }
    else
    {
        parse.setValue("shed", _loadGroup.getNominalTimeout());

        // Set these here in case they are not set elsewhere.
        parse.setValue("control_interval", _loadGroup.getNominalTimeout() );
        parse.setValue("control_reduction", 100 );

        parse.setValue("sa_codesimple", _loadGroup.getOperationalAddress());
        parse.setValue("sa_golaybase", _loadGroup.getGolayOperationalAddress());
        if(!parse.isKeyValid("sa_function"))
            parse.setValue("sa_function", _loadGroup.getFunction(control));

        if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
        {
            OutMessage->TargetID = getID();
            OutMessage->MessageFlags |= MessageFlag_ApplyExclusionLogic;
            OutMessage->Retry = 0;
            OutMessage->ExpirationTime = CtiTime().seconds() + (_loadGroup.getNominalTimeout() >= 900 ? _loadGroup.getNominalTimeout() : 900); // Time this out in 15 minutes or the setting.

            reportActionItemsToDispatch(pReq, parse, vgList);

            //
            //  Form up the reply here since the ExecuteRequest function might consume the OutMessage.
            //
            CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                         string(OutMessage->Request.CommandStr),
                                                         Route->getName(),
                                                         nRet,
                                                         OutMessage->Request.RouteID,
                                                         OutMessage->Request.RetryMacroOffset,
                                                         OutMessage->Request.Attempt,
                                                         OutMessage->Request.GrpMsgID,
                                                         OutMessage->Request.UserID,
                                                         OutMessage->Request.SOE,
                                                         CtiMultiMsg_vec());

            // Start the control request on its route(s)
            if( (nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
            {
                resultString = "ERROR " + CtiNumStr(nRet).spad(3) + string(" performing command on route ") + Route->getName();
                pRet->setStatus(nRet);
                pRet->setResultString(resultString);
                retList.push_back( pRet );
            }
            else
            {
                if(parse.getCommand() == ControlRequest)
                    reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction", 100), vgList, removeCommandDynamicText(parse.getCommandStr()) );

                delete pRet;
            }
        }
        else
        {
            nRet = ClientErrors::NoRouteGroupDevice;

            resultString = " ERROR: Route or Route Transmitter not available for group device " + getName();
            CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                         string(OutMessage->Request.CommandStr),
                                                         resultString,
                                                         nRet,
                                                         OutMessage->Request.RouteID,
                                                         OutMessage->Request.RetryMacroOffset,
                                                         OutMessage->Request.Attempt,
                                                         OutMessage->Request.GrpMsgID,
                                                         OutMessage->Request.UserID,
                                                         OutMessage->Request.SOE,
                                                         CtiMultiMsg_vec());
            retList.push_back( pRet );

            CTILOG_ERROR(dout, resultString);
        }
    }

    return nRet;
}



