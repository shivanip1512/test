/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_sa205
*
* Date:   3/25/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.29 $
* DATE         :  $Date: 2008/10/28 19:21:42 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "cparms.h"
#include "cmdparse.h"
#include "dev_grp_sa205.h"
#include "expresscom.h"
#include "logger.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "numstr.h"
#include "prot_sa3rdparty.h"
#include "utility.h"

using std::string;
using std::endl;
using std::list;

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupSA205::CtiDeviceGroupSA205() :
_lastSTime(0),
_lastCTime(0),
_onePeriodLeft(YUKONEOT)
{
}

//===================================================================================================================
//===================================================================================================================

const CtiTableSA205105Group& CtiDeviceGroupSA205::getLoadGroup() const
{
    return _loadGroup;
}

//===================================================================================================================
//===================================================================================================================

LONG CtiDeviceGroupSA205::getRouteID()
{
    return _loadGroup.getRouteId();
}

/*****************************************************************************
 * This method determines what should be displayed in the "Description" column
 * of the systemlog table when something happens to this device
 *****************************************************************************/
string CtiDeviceGroupSA205::getDescription(const CtiCommandParser & parse) const
{
    CHAR  op_name[20];
    INT   mask = 1;
    string tmpStr;


    tmpStr = "Group: " + getName();

    return tmpStr;
}

//===================================================================================================================
//===================================================================================================================

string CtiDeviceGroupSA205::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, SA2.groupid, SA2.routeid, "
                                     "SA2.operationaladdress, SA2.loadnumber "
                                   "FROM YukonPAObject YP, Device DV, LMGroupSA205105 SA2 "
                                   "WHERE upper (YP.type) = 'SA-205 GROUP' AND YP.paobjectid = SA2.groupid AND "
                                     "YP.paobjectid = DV.deviceid";

    return sqlCore;
}

//===================================================================================================================
//===================================================================================================================

void CtiDeviceGroupSA205::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }
    _loadGroup.DecodeDatabaseReader(rdr);
}

//===================================================================================================================
//===================================================================================================================

YukonError_t CtiDeviceGroupSA205::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    bool gracefulrestore = false;
    YukonError_t nRet = ClientErrors::None;
    string resultString;
    CtiRouteSPtr Route;
    CtiTime now;

    ULONG etime = 0;

    // These are stored and forwarded in case there is a restore or terminate operation required.
    parse.setValue("sa205_last_stime", _lastSTime);
    parse.setValue("sa205_last_ctime", _lastCTime);
    parse.setValue("sa205_one_period_time", (DOUBLE)_onePeriodLeft.seconds());
    parse.setValue("type", ProtocolSA205Type);
    parse.parse();

    bool control = (parse.getFlags() & (CMD_FLAG_CTL_SHED | CMD_FLAG_CTL_CYCLE));

    if((parse.getFlags() & CMD_FLAG_CTL_TERMINATE))      // This is a terminate that SHOULD go out as a 0 repeat control cycle!
    {
        /*
         * If the previous control period has not completed and a terminate is sent,
         * we send a repeat cycle control with zero repeats.
         *
         * Up until the last period, a graceful restore should repeat the cycle and set repeats to zero.
         * In the last period, a graceful restore should do nothing.
         * Beyond the last period, a graceful restore should do nothing.
         */

        if(findStringIgnoreCase(parse.getCommandStr(), " abrupt"))
        {
            control = false;
        }
        else if(now < _onePeriodLeft)
        {
            CTILOG_INFO(slog, getName() <<" Terminate control needed. Setting the cycle counts to zero to end the control within the next period.");

            CTILOG_INFO(dout, getName() <<" Terminate control needed. Setting the cycle counts to zero to end the control within the next period. "
                    "Last interval of previous control begins at... "<< _onePeriodLeft
                    );

            control = true; // Cause the protocol's function to remain a control type command, (not restore).
            parse.setValue("cycle_count", 0);       // Do a repeat of the last control but with 0 counts!
        }
        else
        {
            gracefulrestore = true;                 // Prevent anything from being sent control or restore-wise.
        }
    }

    int func = _loadGroup.getFunction(control);

    parse.setValue("sa_opaddress", atoi(_loadGroup.getOperationalAddress().c_str()));
    parse.setValue("sa_function", func);

    // Recover the "new" s/ctime.
    CtiProtocolSA3rdParty prot;
    prot.parseCommand(parse);
    _lastSTime = prot.getStrategySTime();
    _lastCTime = prot.getStrategyCTime();
    _onePeriodLeft = prot.getStrategyOnePeriodTime();

    if(gracefulrestore)
    {
        parse.setValue("control_interval", 0);
        parse.setValue("control_reduction", 0 );
        parse.setValue("cycle_count", 0);

        resultString = CtiTime().asString() + " " + getName() + " is within the  graceful restore period.  No action is required to terminate the cycling. Use \"abrupt\" to force command.";

        CTILOG_INFO(slog, resultString);

        CTILOG_INFO(dout, resultString);

        nRet = ClientErrors::BadParameter;
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

        reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction", 0), vgList, removeCommandDynamicText(parse.getCommandStr()) );
        return nRet;
    }
    else if((CMD_FLAG_CTL_ALIASMASK & parse.getFlags()) == CMD_FLAG_CTL_RESTORE)
    {
        parse.setValue("control_interval", 0);
        parse.setValue("control_reduction", 0 );
        if(func == 1 && findStringIgnoreCase(gConfigParms.getValueAsString("PROTOCOL_SA_RESTORE123"),"true"))
        {
            // restores on Function 1 (Relay 3) must be handled with a 7.5m shed!
            parse.setValue("sa_restore", TRUE);
        }

        parse.setValue("cycle_count", 0);
    }
    else if((CMD_FLAG_CTL_ALIASMASK & parse.getFlags()) == CMD_FLAG_CTL_TERMINATE)
    {
        parse.setValue("control_interval", 0);
        parse.setValue("control_reduction", 0 );
        parse.setValue("cycle_count", 0);
    }
    else if((CMD_FLAG_CTL_ALIASMASK & parse.getFlags()) == CMD_FLAG_CTL_SHED)
    {
        int shed_seconds = parse.getiValue("shed",86400);
        if(shed_seconds >= 0)
        {
            // Add these two items to the list for control accounting!
            parse.setValue("control_interval", shed_seconds);
            parse.setValue("control_reduction", 100 );
            etime = now.seconds() + shed_seconds;
        }
        else
            nRet = ClientErrors::BadParameter;

    }
    else if((CMD_FLAG_CTL_ALIASMASK & parse.getFlags()) == CMD_FLAG_CTL_CYCLE)
    {
        INT period     = parse.getiValue("cycle_period", 30);
        INT repeat     = parse.getiValue("cycle_count", 8);  repeat = repeat > 7 ? 7 : repeat;

        // Add these two items to the list for control accounting!
        parse.setValue("control_reduction", parse.getiValue("cycle", 0) );
        parse.setValue("control_interval", 60 * period * (repeat+1));
        etime = now.seconds() + (60 * period * (repeat+1));
    }
    else
    {
        CTILOG_ERROR(dout, "No method / Bad syntax for "<< parse.getCommandStr());

        return nRet;
    }

    if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
    {
        OutMessage->TargetID = getID();
        OutMessage->MessageFlags |= MessageFlag_ApplyExclusionLogic;
        OutMessage->Retry = gConfigParms.getValueAsInt("PORTER_SA_REPEATS", 1);
        OutMessage->ExpirationTime = etime;

        reportActionItemsToDispatch(pReq, parse, vgList);

        //
        //  Form up the reply here since the ExecuteRequest function will consume the
        //  OutMessage.
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

        if(OutMessage)
        {
            delete OutMessage;
            OutMessage = NULL;
        }

        CTILOG_ERROR(dout, resultString);
    }

    return nRet;
}


