/*-----------------------------------------------------------------------------*
*
* File:   dev_grp_emetcon
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_grp_emetcon.cpp-arc  $
* REVISION     :  $Revision: 1.13 $
* DATE         :  $Date: 2004/12/01 20:12:49 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786 )


#include <windows.h>

#include "dsm2.h"
#include "porter.h"

#include "yukon.h"
#include "pt_base.h"
#include "pt_status.h"
#include "master.h"

#include "connection.h"

#include "logger.h"
#include "mgr_route.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_pdata.h"
#include "cmdparse.h"
#include "dev_grp_emetcon.h"

#include "numstr.h"


VOID ParserToAStruct(CtiCommandParser &parse, ASTRUCT *ASt)
{
    INT Flags = parse.getFlags();
    int time  = 0;

    if(Flags & CMD_FLAG_CTL_SHED)
    {
        time = parse.getiValue("shed", 0);
    }

    if(Flags & CMD_FLAG_CTL_RESTORE || time == 0)
    {
        ASt->Function = A_RESTORE;
    }

    ASt->Time = resolveAWordTime( time );

}

LONG CtiDeviceGroupEmetcon::getRouteID()      // Must be defined!
{
    return EmetconGroup.getRouteID();
}


INT CtiDeviceGroupEmetcon::ExecuteRequest(CtiRequestMsg                  *pReq,
                                          CtiCommandParser               &parse,
                                          OUTMESS                        *&OutMessage,
                                          RWTPtrSlist< CtiMessage >      &vgList,
                                          RWTPtrSlist< CtiMessage >      &retList,
                                          RWTPtrSlist< OUTMESS >         &outList)
{
    INT   nRet = NoError;
    RWCString resultString;

    CtiRouteSPtr Route;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*) (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    if( (Route = getRoute( getRouteID() )) )    // This is "this's" route
    {
        switch(parse.getCommand())
        {
        case ControlRequest:
            {
                OutMessage->TargetID            = getID();
                OutMessage->Retry               = 2;                      // Default to two tries per route!

                OutMessage->Buffer.ASt.Group    = getEmetconGroup().getEmetconAddress();
                OutMessage->Buffer.ASt.Function = getEmetconGroup().getRelay();

                if( parse.isKeyValid("relaynext") )
                {
                    if( OutMessage->Buffer.ASt.Function == A_SHED_A )
                        OutMessage->Buffer.ASt.Function = A_SHED_B;
                    else if( OutMessage->Buffer.ASt.Function == A_SHED_B )
                        OutMessage->Buffer.ASt.Function = A_SHED_C;
                    else if( OutMessage->Buffer.ASt.Function == A_SHED_C )
                        OutMessage->Buffer.ASt.Function = A_SHED_A;
                }

                ParserToAStruct(parse, &(OutMessage->Buffer.ASt));

                OutMessage->EventCode    = AWORD | ACTIN | NOWAIT | NORESULT;

                /*
                 * OK, these are the items we are about to set out to perform..  Any additional signals will
                 * be added into the list upon completion of the Execute!
                 */
                if(parse.getActionItems().entries())
                {
                    for(size_t offset = 0; offset < parse.getActionItems().entries(); offset++ )
                    {
                        RWCString actn = parse.getActionItems()[offset];
                        RWCString desc = getDescription(parse);

                        _lastCommand = generateCommandString(OutMessage);

                        CtiPointStatus *pControlStatus = (CtiPointStatus*)getDeviceControlPointOffsetEqual( GRP_CONTROL_STATUS );
                        LONG pid = ( (pControlStatus != 0) ? pControlStatus->getPointID() : SYS_PID_LOADMANAGEMENT );

                        vgList.insert(CTIDBG_new CtiSignalMsg(pid, pReq->getSOE(), desc, _lastCommand, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                    }
                }

                /*
                 *  Form up the reply here since the ExecuteRequest funciton will consume the
                 *  OutMessage.
                 */
                resultString = "Control command submitted on route " + CtiNumStr(Route->getRouteID()) + ": " + Route->getName();

                CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                      RWCString(OutMessage->Request.CommandStr),
                                                      resultString,
                                                      nRet,
                                                      OutMessage->Request.RouteID,
                                                      OutMessage->Request.MacroOffset,
                                                      OutMessage->Request.Attempt,
                                                      OutMessage->Request.TrxID,
                                                      OutMessage->Request.UserID,
                                                      OutMessage->Request.SOE,
                                                      RWOrdered());

                if( Route->getType() == VersacomRouteType )
                {
                    OutMessage->EventCode    |= ENCODED;    // Set this so that versacom works
                }

                // Get a control request done maybe?
                if( !(nRet = Route->ExecuteRequest(pReq, parse, OutMessage, vgList, retList, outList)) )
                {
                    if(parse.getCommand() == ControlRequest)
                        reportControlStart( parse.getControlled(), parse.getiValue("control_interval"), parse.getiValue("control_reduction", 100), vgList, getLastCommand() );

                    pRet->setStatus(NORMAL);
                }
                else     // An error occured in the processing/communication
                {
                    resultString = "ERROR " + CtiNumStr(nRet).spad(3) + " performing control on route " + CtiNumStr(Route->getRouteID());
                    pRet->setStatus(nRet);
                    pRet->setResultString(resultString);
                }

                retList.insert( pRet );

                break;
            }
        case GetStatusRequest:
        case LoopbackRequest:
        case GetValueRequest:
        case PutValueRequest:
        case PutStatusRequest:
        case GetConfigRequest:
        case PutConfigRequest:
        default:
            {
                nRet = NoExecuteRequestMethod;

                CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                                      RWCString(OutMessage->Request.CommandStr),
                                                      RWCString("EMETCON GROUP Class devices do not support this command (yet?)"),
                                                      nRet,
                                                      OutMessage->Request.RouteID,
                                                      OutMessage->Request.MacroOffset,
                                                      OutMessage->Request.Attempt,
                                                      OutMessage->Request.TrxID,
                                                      OutMessage->Request.UserID,
                                                      OutMessage->Request.SOE,
                                                      RWOrdered());

                retList.insert( pRet );

                vgList.insert( CTIDBG_new CtiSignalMsg(SYS_PID_SYSTEM, OutMessage->Request.SOE, "Unsupported command", "ERROR", GeneralLogType, SignalEvent, pReq->getUser()));


                if(OutMessage)
                {
                    delete OutMessage;
                    OutMessage = NULL;
                }
                cout << "Unsupported command" << endl;
                break;
            }
        }
    }
    else
    {
        nRet = NoRouteGroupDevice;

        resultString = " ERROR: Route or Route Transmitter not available for group device " + getName();

        CtiReturnMsg* pRet = CTIDBG_new CtiReturnMsg(getID(),
                                              RWCString(OutMessage->Request.CommandStr),
                                              resultString,
                                              nRet,
                                              OutMessage->Request.RouteID,
                                              OutMessage->Request.MacroOffset,
                                              OutMessage->Request.Attempt,
                                              OutMessage->Request.TrxID,
                                              OutMessage->Request.UserID,
                                              OutMessage->Request.SOE,
                                              RWOrdered());

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

RWCString CtiDeviceGroupEmetcon::getDescription(const CtiCommandParser & parse) const
{
    RWCString  ch_relay("Group: " + getName());

    if(EmetconGroup.getRelay() == A_SHED_A) // 0)
    {
        ch_relay += " Relay: A";
    }
    else if(EmetconGroup.getRelay() == A_SHED_B) // 1)
    {
        ch_relay += " Relay: B";
    }
    else if(EmetconGroup.getRelay() == A_SHED_C)
    {
        ch_relay += " Relay: C";
    }
    else if(EmetconGroup.getRelay() == A_SHED_D)
    {
        ch_relay += " Relay: D";
    }
    else if(EmetconGroup.getRelay() == A_RESTORE)
    {
        ch_relay += " Restore";
    }
    else if(EmetconGroup.getRelay() == A_SCRAM)
    {
        ch_relay += " Scram";
    }
    else if(EmetconGroup.getRelay() == A_LATCH_OPEN)
    {
        ch_relay += " Latch Open";
    }
    else if(EmetconGroup.getRelay() == A_LATCH_CLOSE)
    {
        ch_relay += " Latch Close";
    }
    else
    {
        ch_relay += " DB Error. Invalid Emetcon entry.";
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  " << ch_relay << endl;
        }

    }

    return ch_relay;
}

CtiDeviceGroupEmetcon::CtiDeviceGroupEmetcon()
{
}

CtiDeviceGroupEmetcon::CtiDeviceGroupEmetcon(const CtiDeviceGroupEmetcon& aRef)
{
    *this = aRef;
}

CtiDeviceGroupEmetcon::~CtiDeviceGroupEmetcon() {}

CtiDeviceGroupEmetcon& CtiDeviceGroupEmetcon::operator=(const CtiDeviceGroupEmetcon& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);

        EmetconGroup = aRef.getEmetconGroup();
    }
    return *this;
}

CtiTableEmetconLoadGroup   CtiDeviceGroupEmetcon::getEmetconGroup() const      { return EmetconGroup;}
CtiTableEmetconLoadGroup&  CtiDeviceGroupEmetcon::getEmetconGroup()            { return EmetconGroup;}

CtiDeviceGroupEmetcon&     CtiDeviceGroupEmetcon::setEmetconGroup(const CtiTableEmetconLoadGroup& aRef)
{
    EmetconGroup = aRef;
    return *this;
}

void CtiDeviceGroupEmetcon::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
    CtiTableEmetconLoadGroup::getSQL(db, keyTable, selector);
}

void CtiDeviceGroupEmetcon::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & 0x0800 )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    EmetconGroup.DecodeDatabaseReader(rdr);
}

RWCString CtiDeviceGroupEmetcon::generateCommandString(OUTMESS *&OutMessage)
{
    RWCString str("SHED ");

    RWCString tmstr;

    switch(OutMessage->Buffer.ASt.Time)
    {
    case TIME_7_5:
        {
            tmstr = RWCString(" 7.5M");
            break;
        }
    case TIME_15:
        {
            tmstr = RWCString(" 15M");
            break;
        }
    case TIME_30:
        {
            tmstr = RWCString(" 30M");
            break;
        }
    case TIME_60:
        {
            tmstr = RWCString(" 60M");
            break;
        }
    default:
        {
            tmstr = RWCString("RESTORE");
            break;
        }
    }

    switch(OutMessage->Buffer.ASt.Function)
    {
    case A_SHED_A:
        {
            str += RWCString("Relay A") + tmstr;
            break;
        }
    case A_SHED_B:
        {
            str += RWCString("Relay B") + tmstr;
            break;
        }
    case A_SHED_C:
        {
            str += RWCString("Relay C") + tmstr;
            break;
        }
    case A_SHED_D:
        {
            str += RWCString("Relay D") + tmstr;
            break;
        }
    case A_LATCH_OPEN:
        {
            str += RWCString("OPEN");
            break;
        }
    case A_LATCH_CLOSE:
        {
            str += RWCString("CLOSE ");
            break;
        }
    case A_SCRAM:
        {
            str += RWCString("SCRAM ");
            break;
        }
    case A_RESTORE:
    default:
        {
            str = RWCString("RESTORE");
            break;
        }
    }

    return str;
}
