/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_ripple
*
* Date:   10/4/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_grp_ripple.cpp-arc  $
* REVISION     :  $Revision: 1.12 $
* DATE         :  $Date: 2005/02/17 23:33:26 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"



#include "cparms.h"
#include "dev_grp_ripple.h"
#include "mgr_route.h"
#include "msg_lmcontrolhistory.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "msg_cmd.h"
#include "numstr.h"
#include "pt_numeric.h"
#include "pt_status.h"
#include "porter.h"
#include "cmdparse.h"
#include "device.h"

CtiDeviceGroupRipple::CtiDeviceGroupRipple() :
_rsvp(0)
{
}

CtiDeviceGroupRipple::CtiDeviceGroupRipple(const CtiDeviceGroupRipple& aRef) :
_rsvp(0)
{
    *this = aRef;
}

CtiDeviceGroupRipple::~CtiDeviceGroupRipple()
{
    if(_rsvp) delete _rsvp;
}

CtiDeviceGroupRipple& CtiDeviceGroupRipple::operator=(const CtiDeviceGroupRipple& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
        _rippleTable = aRef.getRippleTable();
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }
    }
    return *this;
}


CtiDeviceGroupRipple& CtiDeviceGroupRipple::setRippleTable(const CtiTableRippleLoadGroup& aRef)
{
    LockGuard gd(monitor());
    _rippleTable = aRef;
    return *this;
}

LONG CtiDeviceGroupRipple::getRouteID()
{
    LockGuard gd(monitor());
    return _rippleTable.getRouteID();
}

RWCString CtiDeviceGroupRipple::getDescription(const CtiCommandParser & parse) const
{
    char tdesc[256];
    sprintf(tdesc, "Group: %s", getName().data());
    return RWCString(tdesc);
}

void CtiDeviceGroupRipple::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableRippleLoadGroup::getSQL(db, keyTable, selector);
}
void CtiDeviceGroupRipple::DecodeDatabaseReader(RWDBReader &rdr)
{
    if(getDebugLevel() & 0x0800)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
    _rippleTable.DecodeDatabaseReader(rdr);
}

INT CtiDeviceGroupRipple::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT   nRet = NoError;
    CHAR  Temp[80];
    CtiRouteSPtr Route;

    if( (Route = getRoute( getRouteID() )) )            // This is "this's" route
    {
        setOutMessageTargetID( OutMessage->TargetID );          // This is the Device which is targeted.
        setOutMessageLMGID( OutMessage->DeviceIDofLMGroup );    // This is the LM Group which started this mess
        setOutMessageTrxID( OutMessage->TrxID );                // This is the LM Group which started this mess
        initTrxID( OutMessage->TrxID, parse, vgList );                 // Be sure to accept, or create a CTIDBG_new TrxID.

        OutMessage->EventCode   = RIPPLE | NORESULT;
        OutMessage->Retry       = 2;                            // Default to two tries per route!

        if(OutMessage->Buffer.RSt.Message[0] == (BYTE)0)        // If not, we will assume a higher power has set it for us!
        {
            _rippleTable.copyMessage( OutMessage->Buffer.RSt.Message, _isShed == CONTROLLED );
        }

        /*
         * OK, these are the items we are about to set out to perform..  Any additional signals will
         * be added into the list upon completion of the Execute!
         */
        if(parse.getActionItems().entries())
        {
            for(size_t i = 0; i < parse.getActionItems().entries(); i++)
            {
                RWCString actn = parse.getActionItems()[i];
                RWCString desc = getDescription(parse);

                CtiPointStatus *pControlStatus = (CtiPointStatus*)getDeviceControlPointOffsetEqual( GRP_CONTROL_STATUS );
                LONG pid = ( (pControlStatus != 0) ? pControlStatus->getPointID() : SYS_PID_LOADMANAGEMENT );
                vgList.insert(CTIDBG_new CtiSignalMsg(pid, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
            }
        }

        /*
         *  Form up the reply here since the ExecuteRequest funciton will consume the OutMessage.
         */
        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), Route->getName(), nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());

        // Start the control request on its route(s)
        if( (nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
        {
            sprintf(Temp, "ERROR %3d performing command on route %s", nRet,  Route->getName().data());
            pRet->setStatus(nRet);
            pRet->setResultString(Temp);
            retList.insert( pRet );
        }
        else
        {
            delete pRet;
        }
    }
    else
    {
        nRet = NoRouteGroupDevice;

        RWCString Temp (" ERROR: Route or Route Transmitter not available for group device " + getName());
        RWCString Reply = RWCString(Temp);

        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), Reply, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered());
        retList.insert( pRet );

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << Temp << endl;
        }
    }

    return nRet;
}

/*
 *  Used by the macro group stuff to generate a CTIDBG_new bit pattern comprised of each member RIPPLE GROUP'S
 *  bit pattern.
 */
void CtiDeviceGroupRipple::contributeToBitPattern(BYTE *bptr, bool shed) const
{
    try
    {
        _rippleTable.copyMessage( bptr, shed );
    }
    catch(...)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }
}

INT CtiDeviceGroupRipple::processTrxID( int trx, RWTPtrSlist< CtiMessage >  &vgList )
{
    INT count = getResponsesOnTrxID();
    CtiPoint *pPoint;

    bool erdb = !gConfigParms.getValueAsString("EASTRIVER_DEBUG").compareTo("true", RWCString::ignoreCase);

    if( trx == getCurrentTrxID() )
    {
        setResponsesOnTrxID( ++count );

        /*
         *  This is the GOOD TRANSMISSON counter for the group.
         */
        if ((pPoint = (CtiPoint*)getDevicePointOffsetTypeEqual(1, AnalogPointType)) != NULL)
        {
            // We have a point match here...
            DOUBLE val = 0.0;

            if (pPoint->isNumeric())
            {
                val = ((CtiPointNumeric*)pPoint)->computeValueForUOM( (DOUBLE)count );
            }

            //create a CTIDBG_new data message
            CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), val, NormalQuality, pPoint->getType(), RWCString( getName() + " / " +  pPoint->getName() + CtiNumStr(val) ));

            if (pData != NULL)
            {
                vgList.insert( pData );
            }
        }

        if(_isShed == CONTROLLED || _isShed == UNCONTROLLED)
        {
            reportControlStart( _isShed, getRippleTable().getShedTime(), 100, vgList, getLastCommand() );
            setShed( INVALID );   // This keeps me from sending this multiple times for a single control.
        }
        else if( erdb )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  " << getName() << " is probably setShed(INVALID)" << endl;
            }
        }
    }
    else if(erdb)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  " << getName() << ": TrxID is not equal to the current group TrxID" << endl;
        }
    }

    return count;
}

INT CtiDeviceGroupRipple::initTrxID( int trx, CtiCommandParser &parse, RWTPtrSlist< CtiMessage >  &vgList )
{
    CtiPoint *pPoint = NULL;

    setResponsesOnTrxID(0);
    setTrxID(trx);

    _isShed = parse.getControlled();
    if(parse.getActionItems().entries() > 0 )
    {
        _lastCommand = parse.getActionItems()[0];    // This might just suck!  I guess I am expecting only one (today) and building for the future..?
    }

    if ((pPoint = (CtiPoint*)getDevicePointOffsetTypeEqual(1, AnalogPointType)) != NULL)
    {
        // We have a point match here...
        DOUBLE val = 0.0;

        if (pPoint->isNumeric())
        {
            val = ((CtiPointNumeric*)pPoint)->computeValueForUOM( (DOUBLE)0.0 );
        }

        RWCString resString( getName() + " / " +  pPoint->getName() + CtiNumStr(val) );

        //create a CTIDBG_new data message
        CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), val, NormalQuality, pPoint->getType(), resString);

        if (pData != NULL)
        {
            vgList.insert( pData );
        }
    }

    return NORMAL;
}

bool CtiDeviceGroupRipple::isShedProtocolParent(CtiDeviceBase *otherdev)
{
    bool bstatus = false;

    CtiDeviceGroupRipple *otherGroup = (CtiDeviceGroupRipple *)otherdev;

    // The only ripple groups that can support any type of heirarchy are Minnkota Landis & Gyr LCRs.  The have a universal group which can control area codes.

    RWCString mybits = getRippleTable().getControlBits();
    RWCString otherbits = otherGroup->getRippleTable().getControlBits();

    RWCString thegroup = mybits((size_t)0, (size_t)10);
    RWCString agroup = otherbits((size_t)0, (size_t)10);

    RWCString thearea = mybits((size_t)10, (size_t)6);
    RWCString aarea = otherbits((size_t)10, (size_t)6);

    RWCString parentDO = mybits((size_t)16, (size_t)(mybits.length()-16));
    RWCString childDO = otherbits((size_t)16, (size_t)(otherbits.length()-16));

    // First 10 bits are the group!  They must match.
    if(thegroup == agroup)
    {
        if( !thearea.compareTo("000000") || thearea == aarea)  // This is a universal group or a match on the area.
        {
            bstatus = matchRippleDoubleOrders(parentDO, childDO);
            if(bstatus)
            {
                RWTPtrSlist< CtiMessage > vgList;
                otherGroup->reportControlStart( true, otherGroup->getRippleTable().getShedTime(), 100, vgList, "control shed" );
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") " << vgList.entries() << endl;
                }
                if(vgList.entries())
                {
                    CtiMessage *pMsg = vgList.removeLast();
                    otherGroup->setRsvpToDispatch(pMsg);   // Removes and returns the first list item...
                }
                vgList.clearAndDestroy();
            }
        }
    }

    return bstatus;
}

bool CtiDeviceGroupRipple::isRestoreProtocolParent(CtiDeviceBase *otherdev)
{
    bool bstatus = false;

    CtiDeviceGroupRipple *otherGroup = (CtiDeviceGroupRipple *)otherdev;

    // The only ripple groups that can support any type of heirarchy are Minnkota Landis & Gyr LCRs.  The have a universal group which can control area codes.

    RWCString mybits = getRippleTable().getRestoreBits();
    RWCString otherbits = otherGroup->getRippleTable().getRestoreBits();

    RWCString thegroup = mybits((size_t)0, (size_t)10);
    RWCString agroup = otherbits((size_t)0, (size_t)10);

    RWCString thearea = mybits((size_t)10, (size_t)6);
    RWCString aarea = otherbits((size_t)10, (size_t)6);

    RWCString parentDO = mybits((size_t)16, (size_t)(mybits.length()-16));
    RWCString childDO = otherbits((size_t)16, (size_t)(otherbits.length()-16));


    // First 10 bits are the group!  They must match.
    if(thegroup == agroup)
    {
        if( !thearea.compareTo("000000") || thearea == aarea)  // This is a universal group or a match on the area.
        {
            bstatus = matchRippleDoubleOrders(parentDO, childDO);
            if(bstatus)
            {
                RWTPtrSlist< CtiMessage > vgList;
                otherGroup->reportControlStart( false, 0, 100, vgList, "control restore" );

                if(vgList.entries())
                {
                    CtiMessage *pMsg = vgList.removeLast();
                    otherGroup->setRsvpToDispatch(pMsg);   // Removes and returns the first list item...
                }
                vgList.clearAndDestroy();
            }
        }
    }

    return bstatus;
}

void CtiDeviceGroupRipple::setRsvpToDispatch(CtiMessage *&rsvp)
{
    if(_rsvp) delete _rsvp;
    _rsvp = rsvp;
    rsvp = 0;
    return;
}


CtiMessage* CtiDeviceGroupRipple::rsvpToDispatch( bool clearMessage )
{
    CtiMessage *tMsg = 0;

    if(_rsvp)
    {
        if(clearMessage) {
            tMsg = _rsvp;
            _rsvp = 0;
        } else {
            tMsg = _rsvp->replicateMessage();
        }
    }

    return tMsg;
}

bool CtiDeviceGroupRipple::matchRippleDoubleOrders(RWCString parentDO, RWCString childDO) const
{
    bool match = true;

    for(size_t i = 0; i < childDO.length(); i++)
    {
        if( (char)childDO[i] == '1' && (char)parentDO[i] != (char)childDO[i] )
        {
            match = false;   // The child has bits set that the "parent" does not.  That's not my parent then...
            break;
        }
    }

    return match;
}
