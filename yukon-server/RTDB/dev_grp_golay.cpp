#include "yukon.h"

/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_golay
*
* Date:   4/21/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.9 $
* DATE         :  $Date: 2005/02/10 23:23:59 $
*
* HISTORY      :
* $Log: dev_grp_golay.cpp,v $
* Revision 1.9  2005/02/10 23:23:59  alauinger
* Build with precompiled headers for speed.  Added #include yukon.h to the top of every source file, added makefiles to generate precompiled headers, modified makefiles to make pch happen, and tweaked a few cpp files so they would still build
*
* Revision 1.8  2005/01/27 17:50:44  cplender
* Added method reportActionItemsToDispatch()
*
* Revision 1.7  2004/12/02 22:15:13  cplender
* Added OM-ExpirationTime to the device queue processing.
*
* Revision 1.6  2004/12/01 20:12:49  cplender
* Default "control_reduction" is 100, not -1.
*
* Revision 1.5  2004/06/28 16:40:40  cplender
* Added toUpper on the string responses to FORCE case insensitivity.
*
* Revision 1.4  2004/06/23 13:16:58  cplender
* Added control_interval and control_reduction to the grp so the protocol doesn't need to set it.
*
* Revision 1.3  2004/05/24 13:49:46  cplender
* Set retries to 0 for all but 205 commands.
*
* Revision 1.2  2004/05/10 22:35:28  cplender
* Controls require
* OutMessage->MessageFlags |= MSGFLG_APPLY_EXCLUSION_LOGIC
*
* Revision 1.1  2004/04/29 20:23:49  cplender
* IR
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


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

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupGolay::CtiDeviceGroupGolay()
{
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupGolay::CtiDeviceGroupGolay(const CtiDeviceGroupGolay& aRef)
{
    *this = aRef;
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupGolay::~CtiDeviceGroupGolay()
{
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupGolay& CtiDeviceGroupGolay::operator=(const CtiDeviceGroupGolay& aRef)
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

CtiTableSASimpleGroup CtiDeviceGroupGolay::getLoadGroup() const
{
    return _loadGroup;
}

//===================================================================================================================
//===================================================================================================================

CtiTableSASimpleGroup& CtiDeviceGroupGolay::getLoadGroup()
{
    return _loadGroup;
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupGolay& CtiDeviceGroupGolay::setLoadGroup(const CtiTableSASimpleGroup& aRef)
{
    _loadGroup = aRef;
    return *this;
}

//===================================================================================================================
//===================================================================================================================

LONG CtiDeviceGroupGolay::getRouteID()
{
    return _loadGroup.getRouteId();
}

//===================================================================================================================
// This method determines what should be displayed in the "Description" column
// of the systemlog table when something happens to this device
//===================================================================================================================
RWCString CtiDeviceGroupGolay::getDescription(const CtiCommandParser & parse) const
{
    CHAR  op_name[20];
    INT   mask = 1;
    RWCString tmpStr;


    tmpStr = "Group: " + getName();

    return tmpStr;
}

//===================================================================================================================
//===================================================================================================================

void CtiDeviceGroupGolay::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableSASimpleGroup::getSQL(db, keyTable, selector);

    selector.where( rwdbUpper(keyTable["type"]) == RWDBExpr("GOLAY GROUP") && selector.where() );
}

//===================================================================================================================
//===================================================================================================================

void CtiDeviceGroupGolay::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & 0x0800 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
    _loadGroup.DecodeDatabaseReader(rdr);
}

//===================================================================================================================
//===================================================================================================================

INT CtiDeviceGroupGolay::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT   nRet = NoError;
    RWCString resultString;

    CtiRouteSPtr Route;

    parse.setValue("type", ProtocolGolayType);
    parse.parse();

    bool control = (parse.getFlags() & CMD_FLAG_CTL_SHED);

    if(!control)
    {
        nRet = BADPARAM;

        resultString = " Cannot control Golay groups except with command \"control shed\"  :" + getName();
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
    else
    {
        parse.setValue("shed", _loadGroup.getNominalTimeout());

        // Set these here in case they are not set elsewhere.
        parse.setValue("control_interval", _loadGroup.getNominalTimeout() );
        parse.setValue("control_reduction", 100 );

        parse.setValue("sa_codesimple", _loadGroup.getOperationalAddress());
        if(!parse.isKeyValid("sa_function"))
            parse.setValue("sa_function", 1 );      // This may need a peek CGP // _loadGroup.getFunction(control));

        if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
        {
            OutMessage->TargetID = getID();
            OutMessage->MessageFlags |= MSGFLG_APPLY_EXCLUSION_LOGIC;
            OutMessage->Retry = 0;
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
    }

    return nRet;
}

//===================================================================================================================
//===================================================================================================================

RWCString CtiDeviceGroupGolay::getPutConfigAssignment(UINT level)
{
    RWCString assign = RWCString("sasimple assign");/* +
                       " U" + CtiNumStr(_loadGroup.getUtility()) +
                       " G" + CtiNumStr(_loadGroup.getGroup()) +
                       " D" + CtiNumStr(_loadGroup.getDivision()) +
                       " S" + CtiNumStr(_loadGroup.getSubstation()) +
                       " F" + CtiNumStr(_loadGroup.getRateFamily()) +
                       " M" + CtiNumStr(_loadGroup.getRateMember());
*/
    return  assign;
}



