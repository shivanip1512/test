
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_sasimple
*
* Date:   3/25/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/04/05 19:50:26 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "cmdparse.h"
#include "dev_grp_sasimple.h"
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

CtiDeviceGroupSASimple::CtiDeviceGroupSASimple()
{
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupSASimple::CtiDeviceGroupSASimple(const CtiDeviceGroupSASimple& aRef)
{
    *this = aRef;
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupSASimple::~CtiDeviceGroupSASimple()
{
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupSASimple& CtiDeviceGroupSASimple::operator=(const CtiDeviceGroupSASimple& aRef)
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

CtiTableSASimpleGroup CtiDeviceGroupSASimple::getLoadGroup() const
{
    return _loadGroup;
}

//===================================================================================================================
//===================================================================================================================

CtiTableSASimpleGroup& CtiDeviceGroupSASimple::getLoadGroup()
{
    return _loadGroup;
}

//===================================================================================================================
//===================================================================================================================

CtiDeviceGroupSASimple& CtiDeviceGroupSASimple::setLoadGroup(const CtiTableSASimpleGroup& aRef)
{
    _loadGroup = aRef;
    return *this;
}

//===================================================================================================================
//===================================================================================================================

LONG CtiDeviceGroupSASimple::getRouteID()
{
    return _loadGroup.getRouteId();
}

//===================================================================================================================
// This method determines what should be displayed in the "Description" column
// of the systemlog table when something happens to this device
//===================================================================================================================
RWCString CtiDeviceGroupSASimple::getDescription(const CtiCommandParser & parse) const
{
    CHAR  op_name[20];
    INT   mask = 1;
    RWCString tmpStr;


    tmpStr = "Group: " + getName();

    return tmpStr;
}

//===================================================================================================================
//===================================================================================================================

void CtiDeviceGroupSASimple::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableSASimpleGroup::getSQL(db, keyTable, selector);

    selector.where( keyTable["type"] == RWDBExpr("SA-305 GROUP") && selector.where() );
}

//===================================================================================================================
//===================================================================================================================

void CtiDeviceGroupSASimple::DecodeDatabaseReader(RWDBReader &rdr)
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

INT CtiDeviceGroupSASimple::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT   nRet = NoError;
    RWCString resultString;

    CtiRouteSPtr Route;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*) (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */
/*    parse.setValue("type", ProtocolSA305Type);

    int serial = 0;
    int group = 0;
    int div = 0;
    int sub = 0;

    serial = (int)(getLoadGroup().getIndividual());

    //  These elements are mandatory for any communicatino 
    parse.setValue("sa_utility", getLoadGroup().getUtility());
    parse.setValue("sa_ratefamily", getLoadGroup().getRateFamily());
    parse.setValue("sa_ratemember", getLoadGroup().getRateMember());
    parse.setValue("sa_hierarchy", getLoadGroup().getHierarchy());
    parse.setValue("serial", serial);

    RWCString au = getLoadGroup().getAddressUsage();

    if(au.contains("R", RWCString::ignoreCase))     // This is a group addressed command
    {
        parse.setValue("sa_addressusage", TRUE);
    }

    if(au.contains("G", RWCString::ignoreCase))group = (int)(getLoadGroup().getGroup());
    if(au.contains("D", RWCString::ignoreCase))div = (int)(getLoadGroup().getDivision());
    if(au.contains("S", RWCString::ignoreCase))sub = (int)(getLoadGroup().getSubstation());

    // These elements are gravy
    parse.setValue("sa_group", group);
    parse.setValue("sa_division", div);
    parse.setValue("sa_substation", sub);

    parse.setValue("sa_function", getLoadGroup().getFunction());

    if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
    {
        OutMessage->TargetID = getID();

        int serial = (int)(getLoadGroup().getIndividual());

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
 */
    return nRet;
}

//===================================================================================================================
//===================================================================================================================

RWCString CtiDeviceGroupSASimple::getPutConfigAssignment(UINT level)
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



