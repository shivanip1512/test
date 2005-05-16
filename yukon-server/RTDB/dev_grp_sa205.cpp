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
* REVISION     :  $Revision: 1.16 $
* DATE         :  $Date: 2005/05/16 22:27:15 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

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

CtiDeviceGroupSA205::CtiDeviceGroupSA205(const CtiDeviceGroupSA205& aRef) :
_lastSTime(0),
_lastCTime(0),
_onePeriodLeft(YUKONEOT)
{
    *this = aRef;
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupSA205::~CtiDeviceGroupSA205()
{
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupSA205& CtiDeviceGroupSA205::operator=(const CtiDeviceGroupSA205& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        _loadGroup = aRef.getLoadGroup();
    }

    return *this;
}

//===================================================================================================================
//===================================================================================================================

CtiTableSA205105Group CtiDeviceGroupSA205::getLoadGroup() const
{
    return _loadGroup;
}

//===================================================================================================================
//===================================================================================================================

CtiTableSA205105Group& CtiDeviceGroupSA205::getLoadGroup()
{
    return _loadGroup;
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupSA205& CtiDeviceGroupSA205::setLoadGroup(const CtiTableSA205105Group& aRef)
{
    _loadGroup = aRef;
    return *this;
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
RWCString CtiDeviceGroupSA205::getDescription(const CtiCommandParser & parse) const
{
    CHAR  op_name[20];
    INT   mask = 1;
    RWCString tmpStr;


    tmpStr = "Group: " + getName();

    return tmpStr;
}

//===================================================================================================================
//===================================================================================================================

void CtiDeviceGroupSA205::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableSA205105Group::getSQL(db, keyTable, selector);

    selector.where( rwdbUpper(keyTable["type"]) == RWDBExpr("SA-205 GROUP") && selector.where() );
}

//===================================================================================================================
//===================================================================================================================

void CtiDeviceGroupSA205::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    _loadGroup.DecodeDatabaseReader(rdr);
}

//===================================================================================================================
//===================================================================================================================

INT CtiDeviceGroupSA205::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    bool gracefulrestore = false;
    INT   nRet = NoError;
    RWCString resultString;
    CtiRouteSPtr Route;
    RWTime now;

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

        if(parse.getCommandStr().contains(" abrupt", RWCString::ignoreCase))
        {
            control = false;
        }
        else if(now < _onePeriodLeft)
        {
            {
                CtiLockGuard<CtiLogger> slog_guard(slog);
                slog << RWTime() << " " << getName() << " Terminate control needed.  Setting the cycle counts to zero to end the control within the next period." << endl;
            }
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " " << getName() << " Terminate control needed.  Setting the cycle counts to zero to end the control within the next period." << endl;
                dout << RWTime() << " " << getName() << " Last interval of previous control begins at... " << _onePeriodLeft << endl;
            }

            control = true; // Cause the protocol's function to remain a control type command, (not restore).
            parse.setValue("cycle_count", 0);       // Do a repeat of the last control but with 0 counts!
        }
        else
        {
            gracefulrestore = true;                 // Prevent anything from being sent control or restore-wise.
        }
    }

    int func = _loadGroup.getFunction(control);

    parse.setValue("sa_opaddress", atoi(_loadGroup.getOperationalAddress().data()));
    parse.setValue("sa_function", func);

    // Recover the "new" s/ctime.
    CtiProtocolSA3rdParty prot;
    prot.parseCommand(parse);
    _lastSTime = prot.getStrategySTime();
    _lastCTime = prot.getStrategyCTime();
    _onePeriodLeft = prot.getStrategyOnePeriodTime();

    if(gracefulrestore)
    {
        resultString = RWTime().asString() + " " + getName() + " is within the  graceful restore period.  No action is required to terminate the cycling. Use \"abrupt\" to force command.";
        {
            CtiLockGuard<CtiLogger> slog_guard(slog);
            slog << resultString << endl;
        }
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << resultString << endl;
        }


        nRet = BADPARAM;
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());
        retList.insert( pRet );

        return nRet;
    }
    else if((CMD_FLAG_CTL_ALIASMASK & parse.getFlags()) == CMD_FLAG_CTL_RESTORE)
    {
        if(func == 1 && gConfigParms.getValueAsString("PROTOCOL_SA_RESTORE123").contains("true", RWCString::ignoreCase))
        {
            // restores on Function 1 (Relay 3) must be handled with a 7.5m shed!
            parse.setValue("sa_restore", TRUE);
            parse.setValue("control_interval", 450);
            parse.setValue("control_reduction", 100 );
        }

        parse.setValue("cycle_count", 0);
    }
    else if((CMD_FLAG_CTL_ALIASMASK & parse.getFlags()) == CMD_FLAG_CTL_TERMINATE)
    {
        parse.setValue("cycle_count", 0);
    }
    else if((CMD_FLAG_CTL_ALIASMASK & parse.getFlags()) == CMD_FLAG_CTL_SHED)
    {
        int shed_seconds = parse.getiValue("shed",86400);
        if(shed_seconds >= 0)
        {
            // Add these two items to the list for control accounting!
            parse.setValue("control_interval", parse.getiValue("shed"));
            parse.setValue("control_reduction", 100 );
        }
        else
            nRet = BADPARAM;

    }
    else if((CMD_FLAG_CTL_ALIASMASK & parse.getFlags()) == CMD_FLAG_CTL_CYCLE)
    {
        INT period     = parse.getiValue("cycle_period", 30);
        INT repeat     = parse.getiValue("cycle_count", 8);  repeat = repeat > 7 ? 7 : repeat;

        // Add these two items to the list for control accounting!
        parse.setValue("control_reduction", parse.getiValue("cycle", 0) );
        parse.setValue("control_interval", 60 * period * repeat);
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** No method / Bad syntax for " << parse.getCommandStr() << endl;;
        }

        return nRet;
    }

    if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
    {
        OutMessage->TargetID = getID();
        OutMessage->MessageFlags |= MSGFLG_APPLY_EXCLUSION_LOGIC;
        OutMessage->Retry = gConfigParms.getValueAsInt("PORTER_SA_REPEATS", 1);
        OutMessage->ExpirationTime = RWTime().seconds() + parse.getiValue("control_interval", 300); // Time this out in 5 minutes or the setting.

        reportActionItemsToDispatch(pReq, parse, vgList);

        //
        //  Form up the reply here since the ExecuteRequest function will consume the
        //  OutMessage.
        //
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), Route->getName(), nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());

        // Start the control request on its route(s)
        if( (nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
        {
            resultString = "ERROR " + CtiNumStr(nRet).spad(3) + " performing command on route " + Route->getName();
            pRet->setStatus(nRet);
            pRet->setResultString(resultString);
            retList.insert( pRet );
        }
        else
        {
            if(parse.getCommand() == ControlRequest)
                reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction", 100), vgList, getLastCommand() );

            delete pRet;
        }
    }
    else
    {
        nRet = NoRouteGroupDevice;

        resultString = " ERROR: Route or Route Transmitter not available for group device " + getName();
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());
        retList.insert( pRet );

        if(OutMessage)
        {
            delete OutMessage;
            OutMessage = NULL;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << resultString << endl;
        }
    }

    return nRet;
}

//===================================================================================================================
//===================================================================================================================

RWCString CtiDeviceGroupSA205::getPutConfigAssignment( UINT level )
{
    RWCString assign = RWCString("sa205 assign");/* +
                       " U" + CtiNumStr(_loadGroup.getUtility()) +
                       " G" + CtiNumStr(_loadGroup.getGroup()) +
                       " D" + CtiNumStr(_loadGroup.getDivision()) +
                       " S" + CtiNumStr(_loadGroup.getSubstation()) +
                       " F" + CtiNumStr(_loadGroup.getRateFamily()) +
                       " M" + CtiNumStr(_loadGroup.getRateMember());
                                                                    */
    return  assign;
}



