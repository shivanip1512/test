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
* REVISION     :  $Revision: 1.29.2.1 $
* DATE         :  $Date: 2008/11/19 15:21:27 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "precompiled.h"


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

using std::string;
using std::endl;
using std::list;

CtiDeviceGroupRipple::CtiDeviceGroupRipple() :
_rsvp(0)
{
}

CtiDeviceGroupRipple::~CtiDeviceGroupRipple()
{
    if(_rsvp) delete _rsvp;
}

LONG CtiDeviceGroupRipple::getRouteID()
{
    CtiLockGuard<CtiMutex> guard(_classMutex);
    return _rippleTable.getRouteID();
}

string CtiDeviceGroupRipple::getDescription(const CtiCommandParser & parse) const
{
    char tdesc[256];
    sprintf(tdesc, "Group: %s", getName().c_str());
    return string(tdesc);
}

string CtiDeviceGroupRipple::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, YP.disableflag, "
                                     "DV.deviceid, DV.alarminhibit, DV.controlinhibit, LGR.shedtime, LGR.controlvalue, "
                                     "LGR.restorevalue, LGR.routeid "
                                   "FROM YukonPAObject YP, Device DV, LMGroupRipple LGR "
                                   "WHERE YP.paobjectid = LGR.deviceid AND YP.paobjectid = DV.deviceid";

    return sqlCore;
}

void CtiDeviceGroupRipple::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
    _rippleTable.DecodeDatabaseReader(rdr);
}

YukonError_t CtiDeviceGroupRipple::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::None;
    CtiRouteSPtr Route;

    if( (Route = getRoute( getRouteID() )) )            // This is "this's" route
    {
        setOutMessageTargetID( OutMessage->TargetID );          // This is the Device which is targeted.
        setOutMessageLMGID( OutMessage->DeviceIDofLMGroup );    // This is the LM Group which started this mess
        setOutMessageTrxID( OutMessage->TrxID );                // This is the LM Group which started this mess
        initTrxID( OutMessage->TrxID, parse, vgList );                 // Be sure to accept, or create a new TrxID.

        OutMessage->MessageFlags |= MessageFlag_ApplyExclusionLogic;
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
        if(parse.getActionItems().size())
        {
            for(std::list< string >::const_iterator itr = parse.getActionItems().begin();
                 itr != parse.getActionItems().end();
                 ++itr )
            {
                string actn = *itr;
                string desc = getDescription(parse);

                CtiPointStatusSPtr pControlStatus = boost::static_pointer_cast<CtiPointStatus>(getDeviceControlPointOffsetEqual( GRP_CONTROL_STATUS ));
                LONG pid = ( (pControlStatus) ? pControlStatus->getPointID() : SYS_PID_LOADMANAGEMENT );
                vgList.push_back(CTIDBG_new CtiSignalMsg(pid, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
            }
        }

        if( parse.getControlled() )
        {
            OutMessage->ExpirationTime = CtiTime().seconds() + gConfigParms.getValueAsInt(GROUP_CONTROL_EXPIRATION, 1200);
        }

        /*
         *  Form up the reply here since the ExecuteRequest funciton will consume the OutMessage.
         */
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
            CHAR  Temp[80];
            sprintf(Temp, "ERROR %3d performing command on route %s", nRet,  Route->getName().c_str());
            pRet->setStatus(nRet);
            pRet->setResultString(Temp);
            retList.push_back( pRet );
        }
        else
        {
            delete pRet;
        }
    }
    else
    {
        nRet = ClientErrors::NoRouteGroupDevice;

        string Reply = " ERROR: Route or Route Transmitter not available for group device " + getName();

        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                     string(OutMessage->Request.CommandStr),
                                                     Reply,
                                                     nRet,
                                                     OutMessage->Request.RouteID,
                                                     OutMessage->Request.RetryMacroOffset,
                                                     OutMessage->Request.Attempt,
                                                     OutMessage->Request.GrpMsgID,
                                                     OutMessage->Request.UserID,
                                                     OutMessage->Request.SOE,
                                                     CtiMultiMsg_vec());
        retList.push_back( pRet );

        CTILOG_ERROR(dout, Reply);
    }

    return nRet;
}

/*
 *  Used by the macro group stuff to generate a new bit pattern comprised of each member RIPPLE GROUP'S
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
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
    }
}

INT CtiDeviceGroupRipple::processTrxID( int trx, CtiMessageList  &vgList )
{
    INT count = getResponsesOnTrxID();
    CtiPointSPtr pPoint;

    bool erdb = gConfigParms.isTrue("EASTRIVER_DEBUG");

    if( trx == getCurrentTrxID() )
    {
        setResponsesOnTrxID( ++count );

        /*
         *  This is the GOOD TRANSMISSON counter for the group.
         */
        if ((pPoint = getDevicePointOffsetTypeEqual(1, AnalogPointType)))
        {
            // We have a point match here...
            DOUBLE val = 0.0;

            if (pPoint->isNumeric())
            {
                val = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( (DOUBLE)count );
            }

            //create a new data message
            CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), val, NormalQuality, pPoint->getType(), string( getName() + " / " +  pPoint->getName() + CtiNumStr(val) ));

            if (pData != NULL)
            {
                vgList.push_back( pData );
            }
        }

        if(_isShed == CONTROLLED || _isShed == UNCONTROLLED)
        {
            CtiLockGuard<CtiMutex> guard(_classMutex);

            LONG shedtime = _rippleTable.getShedTime();
            int controlpercent = 100;
            if(_isShed == UNCONTROLLED)
            {
                shedtime = RESTORE_DURATION;
                controlpercent = 0;
            }

            reportControlStart( _isShed, shedtime, controlpercent, vgList, getLastCommand() );
            setShed( STATE_INVALID );   // This keeps me from sending this multiple times for a single control.
        }
        else if( erdb )
        {
            CTILOG_WARN(dout, getName() <<" is probably setShed(INVALID)");
        }
    }
    else if(erdb)
    {
        CTILOG_ERROR(dout, getName() <<": TrxID is not equal to the current group TrxID");
    }

    return count;
}

INT CtiDeviceGroupRipple::initTrxID( int trx, CtiCommandParser &parse, CtiMessageList  &vgList )
{
    CtiPointSPtr pPoint;

    setResponsesOnTrxID(0);
    setTrxID(trx);

    _isShed = parse.getControlled();
    if(parse.getActionItems().size() > 0 )
    {
        _lastCommand = *(parse.getActionItems().begin());    // This might just suck!  I guess I am expecting only one (today) and building for the future..?
    }

    if (pPoint = getDevicePointOffsetTypeEqual(1, AnalogPointType))
    {
        // We have a point match here...
        DOUBLE val = 0.0;

        if (pPoint->isNumeric())
        {
            val = boost::static_pointer_cast<CtiPointNumeric>(pPoint)->computeValueForUOM( (DOUBLE)0.0 );
        }

        string resString( getName() + " / " +  pPoint->getName() + CtiNumStr(val) );

        //create a new data message
        CtiPointDataMsg *pData = CTIDBG_new CtiPointDataMsg(pPoint->getPointID(), val, NormalQuality, pPoint->getType(), resString);

        if (pData != NULL)
        {
            vgList.push_back( pData );
        }
    }

    return ClientErrors::None;
}

bool CtiDeviceGroupRipple::isShedProtocolParent(CtiDeviceBase *otherdev)
{
    bool bstatus = false;

    CtiDeviceGroupRipple *otherGroup = (CtiDeviceGroupRipple *)otherdev;

    // The only ripple groups that can support any type of heirarchy are Minnkota Landis & Gyr LCRs.  The have a universal group which can control area codes.
    string mybits, otherbits;

    {
        CtiLockGuard<CtiMutex> guard(_classMutex);
        mybits = _rippleTable.getControlBits();
    }

    {
        CtiLockGuard<CtiMutex> guard(otherGroup->_classMutex);
        otherbits = otherGroup->_rippleTable.getControlBits();
    }

    string thegroup = (char*)mybits[(size_t)0, (size_t)10];
    string agroup = (char*)otherbits[(size_t)0, (size_t)10];

    string thearea = (char*)mybits[(size_t)10, (size_t)6];
    string aarea = (char*)otherbits[(size_t)10, (size_t)6];

    string parentDO = (char*)mybits[(size_t)16, (size_t)(mybits.length()-16)];
    string childDO = (char*)otherbits[(size_t)16, (size_t)(otherbits.length()-16)];

    // First 10 bits are the group!  They must match.
    if(thegroup == agroup)
    {
        if( !thearea.compare("000000") || thearea == aarea)  // This is a universal group or a match on the area.
        {
            bstatus = matchRippleDoubleOrders(parentDO, childDO);
            if(bstatus)
            {
                CtiMessageList vgList;

                CtiLockGuard<CtiMutex> guard(otherGroup->_classMutex);

                otherGroup->reportControlStart( true, otherGroup->_rippleTable.getShedTime(), 100, vgList, "control shed" );
                if(vgList.size())
                {
                    CtiMessage *pMsg = vgList.back();
                    otherGroup->setRsvpToDispatch(pMsg);   // Removes and returns the first list item...
                }
                delete_container(vgList);
                vgList.clear();
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
    string mybits, otherbits;

    {
        CtiLockGuard<CtiMutex> guard(_classMutex);
        mybits = _rippleTable.getRestoreBits();
    }

    {
        CtiLockGuard<CtiMutex> guard(otherGroup->_classMutex);
        otherbits = otherGroup->_rippleTable.getRestoreBits();
    }

    string thegroup = (char*)mybits[(size_t)0, (size_t)10];
    string agroup = (char*)otherbits[(size_t)0, (size_t)10];

    string thearea = (char*)mybits[(size_t)10, (size_t)6];
    string aarea = (char*)otherbits[(size_t)10, (size_t)6];

    string parentDO = (char*)mybits[(size_t)16, (size_t)(mybits.length()-16)];
    string childDO = (char*)otherbits[(size_t)16, (size_t)(otherbits.length()-16)];


    // First 10 bits are the group!  They must match.
    if(thegroup == agroup)
    {
        if( !thearea.compare("000000") || thearea == aarea)  // This is a universal group or a match on the area.
        {
            bstatus = matchRippleDoubleOrders(parentDO, childDO);
            if(bstatus)
            {
                CtiMessageList vgList;
                otherGroup->reportControlStart( false, RESTORE_DURATION, 0, vgList, "control restore" );

                if(vgList.size())
                {
                    CtiMessage *pMsg = vgList.back();
                    otherGroup->setRsvpToDispatch(pMsg);   // Removes and returns the first list item...
                }
                delete_container(vgList);
                vgList.clear();
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

bool CtiDeviceGroupRipple::matchRippleDoubleOrders(string parentDO, string childDO) const
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
