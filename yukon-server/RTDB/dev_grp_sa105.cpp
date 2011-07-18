/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_sa105
*
* Date:   3/25/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.24 $
* DATE         :  $Date: 2008/10/28 19:21:42 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"

#include "cmdparse.h"
#include "cparms.h"
#include "dev_grp_sa105.h"
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

//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupSA105::CtiDeviceGroupSA105()
{

}

//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupSA105::CtiDeviceGroupSA105(const CtiDeviceGroupSA105& aRef)
{
   *this = aRef;
}

//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupSA105::~CtiDeviceGroupSA105()
{
}

//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupSA105& CtiDeviceGroupSA105::operator=(const CtiDeviceGroupSA105& aRef)
{
   if( this != &aRef )
   {
       Inherited::operator=( aRef );

       {
           CtiLockGuard<CtiLogger> doubt_guard(dout);
           dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
       }
   }

   return *this;
}

//====================================================================================================================
//====================================================================================================================

CtiTableSA205105Group CtiDeviceGroupSA105::getLoadGroup( void ) const
{
   return( _loadGroup );
}

//====================================================================================================================
//====================================================================================================================

CtiTableSA205105Group& CtiDeviceGroupSA105::getLoadGroup( void )
{
   return( _loadGroup );
}

//====================================================================================================================
//====================================================================================================================

CtiDeviceGroupSA105& CtiDeviceGroupSA105::setLoadGroup(const CtiTableSA205105Group& aRef)
{
   _loadGroup = aRef;
   return *this;
}

//====================================================================================================================
//====================================================================================================================

LONG CtiDeviceGroupSA105::getRouteID( void )
{
    return( _loadGroup.getRouteId() );
}

//====================================================================================================================
//====================================================================================================================

string CtiDeviceGroupSA105::getDescription(const CtiCommandParser & parse) const
{
    string tmpStr;

    tmpStr = "Group: " + getName();

    return tmpStr;
}

//====================================================================================================================
//====================================================================================================================

string CtiDeviceGroupSA105::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, SA1.groupid, SA1.routeid, "
                                     "SA1.operationaladdress, SA1.loadnumber "
                                   "FROM YukonPAObject YP, Device DV, LMGroupSA205105 SA1 "
                                   "WHERE upper (YP.type) = 'SA-205 GROUP' AND YP.paobjectid = SA1.groupid AND "
                                     "YP.paobjectid = DV.deviceid";

    return sqlCore;
}

//====================================================================================================================
//====================================================================================================================

void CtiDeviceGroupSA105::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    _loadGroup.DecodeDatabaseReader(rdr);
}

//====================================================================================================================
//====================================================================================================================

INT CtiDeviceGroupSA105::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT   nRet = NoError;
    string resultString;

    CtiRouteSPtr Route;

    parse.setValue("type", ProtocolSA105Type);
    parse.parse();

    bool control = (parse.getFlags() & (CMD_FLAG_CTL_SHED | CMD_FLAG_CTL_CYCLE));
    int func = _loadGroup.getFunction(control);

    parse.setValue("sa_opaddress", atoi(_loadGroup.getOperationalAddress().c_str()));
    parse.setValue("sa_function", func);

    if( !control && func == 1 && findStringIgnoreCase(gConfigParms.getValueAsString("PROTOCOL_SA_RESTORE123"), "true") )
    {
        // restores on Function 3 must be handled with a 7.5m shed!
        parse.setValue("sa_restore", TRUE);
        parse.setValue("control_interval", 450);
        parse.setValue("control_reduction", 100 );
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
        INT repeat     = parse.getiValue("cycle_count", 8);

        // Add these two items to the list for control accounting!
        parse.setValue("control_reduction", parse.getiValue("cycle", 0) );
        parse.setValue("control_interval", 60 * period * repeat);
    }


    if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
    {
        OutMessage->TargetID = getID();
        OutMessage->MessageFlags |= MessageFlag_ApplyExclusionLogic;
        OutMessage->Retry = gConfigParms.getValueAsInt("PORTER_SA_REPEATS", 1);
        OutMessage->ExpirationTime = (control ? CtiTime().seconds() + parse.getiValue("control_interval", 300) : 0); // Time this out in 5 minutes or the setting.

        reportActionItemsToDispatch(pReq, parse, vgList);

        //
        //  Form up the reply here since the ExecuteRequest function will consume the
        //  OutMessage.
        //
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), string(OutMessage->Request.CommandStr), Route->getName(), nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());

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
        nRet = NoRouteGroupDevice;

        resultString = " ERROR: Route or Route Transmitter not available for group device " + getName();
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), string(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.GrpMsgID, OutMessage->Request.UserID, OutMessage->Request.SOE, CtiMultiMsg_vec());
        retList.push_back( pRet );

        if(OutMessage)
        {
            delete OutMessage;
            OutMessage = NULL;
        }

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << resultString << endl;
        }
    }

    return nRet;
}


