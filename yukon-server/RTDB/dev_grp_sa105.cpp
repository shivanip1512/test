#include "yukon.h"


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
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2005/02/10 23:23:59 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
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
           dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

RWCString CtiDeviceGroupSA105::getDescription(const CtiCommandParser & parse) const
{
    RWCString tmpStr;

    tmpStr = "Group: " + getName();

    return tmpStr;
}

//====================================================================================================================
//====================================================================================================================

void CtiDeviceGroupSA105::getSQL( RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector )
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableSA205105Group::getSQL(db, keyTable, selector);

    selector.where( rwdbUpper(keyTable["type"]) == RWDBExpr("SA-205 GROUP") && selector.where() );
}

//====================================================================================================================
//====================================================================================================================

void CtiDeviceGroupSA105::DecodeDatabaseReader( RWDBReader &rdr )
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & 0x0800 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    _loadGroup.DecodeDatabaseReader(rdr);
}

//====================================================================================================================
//====================================================================================================================

INT CtiDeviceGroupSA105::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT   nRet = NoError;
    RWCString resultString;

    CtiRouteSPtr Route;

    parse.setValue("type", ProtocolSA105Type);
    parse.parse();

    bool control = (parse.getFlags() & (CMD_FLAG_CTL_SHED | CMD_FLAG_CTL_CYCLE));

    parse.setValue("sa_opaddress", atoi(_loadGroup.getOperationalAddress().data()));
    parse.setValue("sa_function", _loadGroup.getFunction(control));

    if((CMD_FLAG_CTL_ALIASMASK & parse.getFlags()) == CMD_FLAG_CTL_SHED)
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

//====================================================================================================================
//====================================================================================================================

RWCString CtiDeviceGroupSA105::getPutConfigAssignment( UINT level )
{
    RWCString assign = RWCString("sa105 assign");/* +
                             " U" + CtiNumStr(_loadGroup.getUtility()) +
                             " G" + CtiNumStr(_loadGroup.getGroup()) +
                             " D" + CtiNumStr(_loadGroup.getDivision()) +
                             " S" + CtiNumStr(_loadGroup.getSubstation()) +
                             " F" + CtiNumStr(_loadGroup.getRateFamily()) +
                             " M" + CtiNumStr(_loadGroup.getRateMember());
                                                                  */
    return  assign;
}

