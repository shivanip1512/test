
/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_sadigital
*
* Date:   4/21/2004
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2004/06/28 16:40:40 $
*
* HISTORY      :
* $Log: dev_grp_sadigital.cpp,v $
* Revision 1.4  2004/06/28 16:40:40  cplender
* Added toUpper on the string responses to FORCE case insensitivity.
*
* Revision 1.3  2004/06/23 14:13:44  cplender
* Added control_interval and control_reduction to the grp so the protocol doesn't need to set it.
*
* Revision 1.2  2004/05/10 22:35:28  cplender
* Controls require
* OutMessage->MessageFlags |= MSGFLG_APPLY_EXCLUSION_LOGIC
*
* Revision 1.1  2004/04/29 20:24:40  cplender
* IR
*
*
* Copyright (c) 2004 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)

#include "cmdparse.h"
#include "dev_grp_sadigital.h"
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

CtiDeviceGroupSADigital::CtiDeviceGroupSADigital()
{
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupSADigital::CtiDeviceGroupSADigital(const CtiDeviceGroupSADigital& aRef)
{
    *this = aRef;
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupSADigital::~CtiDeviceGroupSADigital()
{
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupSADigital& CtiDeviceGroupSADigital::operator=(const CtiDeviceGroupSADigital& aRef)
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

CtiTableSASimpleGroup CtiDeviceGroupSADigital::getLoadGroup() const
{
    return _loadGroup;
}

//===================================================================================================================
//===================================================================================================================

CtiTableSASimpleGroup& CtiDeviceGroupSADigital::getLoadGroup()
{
    return _loadGroup;
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupSADigital& CtiDeviceGroupSADigital::setLoadGroup(const CtiTableSASimpleGroup& aRef)
{
    _loadGroup = aRef;
    return *this;
}

//===================================================================================================================
//===================================================================================================================

LONG CtiDeviceGroupSADigital::getRouteID()
{
    return _loadGroup.getRouteId();
}

//===================================================================================================================
// This method determines what should be displayed in the "Description" column
// of the systemlog table when something happens to this device
//===================================================================================================================
RWCString CtiDeviceGroupSADigital::getDescription(const CtiCommandParser & parse) const
{
    CHAR  op_name[20];
    INT   mask = 1;
    RWCString tmpStr;


    tmpStr = "Group: " + getName();

    return tmpStr;
}

//===================================================================================================================
//===================================================================================================================

void CtiDeviceGroupSADigital::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableSASimpleGroup::getSQL(db, keyTable, selector);

    selector.where( rwdbUpper(keyTable["type"]) == RWDBExpr("SA-DIGITAL GROUP") && selector.where() );
}

//===================================================================================================================
//===================================================================================================================

void CtiDeviceGroupSADigital::DecodeDatabaseReader(RWDBReader &rdr)
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

INT CtiDeviceGroupSADigital::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT   nRet = NoError;
    RWCString resultString;

    CtiRouteSPtr Route;

    parse.setValue("type", ProtocolSADigitalType);
    parse.parse();

    bool control = (parse.getFlags() & CMD_FLAG_CTL_SHED);

    if(!control)
    {
        nRet = BADPARAM;

        resultString = " Cannot control SA Digital groups except with command \"control shed\"  :" + getName();
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

        if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
        {
            OutMessage->TargetID = getID();
            OutMessage->MessageFlags |= MSGFLG_APPLY_EXCLUSION_LOGIC;

            //
            // OK, these are the items we are about to set out to perform..  Any additional signals will
            // be added into the list upon completion of the Execute!
            //
            if(parse.getActionItems().entries())
            {
                for(size_t offset = 0 ; offset < parse.getActionItems().entries(); offset++)
                {
                    RWCString actn = parse.getActionItems()[offset];
                    RWCString desc = getDescription(parse);

                    _lastCommand = actn;    // This might just suck!  I guess I am expecting only one (today) and building for the future..?

                    CtiPointStatus *pControlStatus = (CtiPointStatus*)getDeviceControlPointOffsetEqual( GRP_CONTROL_STATUS );
                    LONG pid = ( (pControlStatus != 0) ? pControlStatus->getPointID() : SYS_PID_LOADMANAGEMENT );

                    vgList.insert(CTIDBG_new CtiSignalMsg(pid, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                }
            }

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
                    reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction"), vgList, getLastCommand() );

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

RWCString CtiDeviceGroupSADigital::getPutConfigAssignment(UINT level)
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



