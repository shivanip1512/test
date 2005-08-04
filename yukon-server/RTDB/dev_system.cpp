/*-----------------------------------------------------------------------------*
*
* File:   dev_system
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/dev_system.cpp-arc  $
* REVISION     :  $Revision: 1.22 $
* DATE         :  $Date: 2005/08/04 18:16:19 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"
#include "cparms.h"



#include "dev_system.h"
#include "logger.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"
#include "msg_signal.h"
#include "pt_base.h"
#include "connection.h"
#include "cmdparse.h"
#include "prot_versacom.h"
#include "rte_base.h"
#include "rte_xcu.h"
#include "porter.h"
#include "pointtypes.h"
#include "mgr_route.h"

#include "numstr.h"

#define DEBUG_PRINT_DECODE 0


INT CtiDeviceSystem::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;
    INT iTemp;

    CtiRouteSPtr Route;

    /*
     * OK, these are the items we are about to set out to perform..  Any additional signals will
     * be added into the list upon completion of the Execute!
     */
    if(parse.getActionItems().entries())
    {
        for( size_t offset = 0; offset < parse.getActionItems().entries(); offset++)
        {
            RWCString actn = parse.getActionItems()[offset];
            RWCString desc = getDescription(parse);

            vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
        }
    }

    if(parse.getiValue("type") == ProtocolEnergyProType)
    {
        OutMessage->TargetID = parse.getiValue("serial");
        OutMessage->MessageFlags |= MSGFLG_ROUTE_TO_PORTER_GATEWAY_THREAD;   // Take the shortcut!

        retList.insert( CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), "Gateway Request Submitted.  Results are async.",  status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered()));

        outList.insert( OutMessage );
        OutMessage = 0;
    }
    else
    {
        switch(parse.getCommand())
        {
        case PutConfigRequest:
            {
                switch(iTemp = parse.getiValue("type"))
                {
                case ProtocolVersacomType:
                    {
                        OutMessage->EventCode |= VERSACOM;
                        break;
                    }
                case ProtocolExpresscomType:
                    {
                        int xcserial = parse.getiValue("serial");

                        parse.setValue("xc_serial", xcserial);

                        if( INT_MIN == xcserial )
                        {
                            RWCString   problem;

                            if( INT_MIN == xcserial )
                            {
                                problem = RWCString("Invalid Request: Serial number not specified");
                            }

                            status = CtiInvalidRequest;

                            vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT, pReq->getSOE(), getDescription(parse), problem, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                            retList.insert( CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), problem,  status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered()));
                        }

                        OutMessage->Retry = 2;                      // Default to two tries per route!

                        break;
                    }
                case ProtocolSA105Type:
                case ProtocolSA205Type:
                case ProtocolSA305Type:
                    {
                        OutMessage->EventCode |= NORESULT;
                        OutMessage->Retry = gConfigParms.getValueAsInt("PORTER_SA_REPEATS", 1);
                        break;
                    }
                case ProtocolFisherPierceType:
                default:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Protocol type " << iTemp << endl;
                        }
                        break;
                    }
                }

                break;
            }
        case ControlRequest:
            {
                switch(iTemp = parse.getiValue("type"))
                {
                case ProtocolVersacomType:
                    {
                        memset(&(OutMessage->Buffer.VSt), 0, sizeof(VSTRUCT));

                        if(
                          INT_MIN == (OutMessage->Buffer.VSt.Address = parse.getiValue("serial")) ||
                          (
                          INT_MIN == (OutMessage->Buffer.VSt.RelayMask = parse.getiValue("relaymask")) &&
                          !(parse.getFlags() & (CMD_FLAG_CTL_OPEN | CMD_FLAG_CTL_CLOSE))
                          )
                          )
                        {
                            RWCString   problem;

                            if( INT_MIN == OutMessage->Buffer.VSt.Address )
                            {
                                problem = RWCString("Invalid Request: Serial number not specified");
                            }
                            else
                            {
                                problem = RWCString("Invalid Request: (Relay 1,2,3) | (OPEN|CLOSE) not specified");
                            }

                            status = CtiInvalidRequest;

                            vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT, pReq->getSOE(), getDescription(parse), problem, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                            retList.insert( CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), problem,  status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered()));
                        }

                        /*
                         *  The VERSACOM tag is CRITICAL in that it indicates to the subsequent stages which
                         *  control path to take with this OutMessage!
                         */
                        OutMessage->EventCode    = VERSACOM | NORESULT;
                        OutMessage->Retry        = 2;                      // Default to two tries per route!

                        break;
                    }
                case ProtocolExpresscomType:
                    {
                        int xcserial = parse.getiValue("serial");
                        int xcrelaymask = parse.getiValue("relaymask", 1);

                        parse.setValue("xc_serial", xcserial);
                        parse.setValue("relaymask", xcrelaymask);

                        if( INT_MIN == xcserial )
                        {
                            RWCString   problem;

                            if( INT_MIN == xcserial )
                            {
                                problem = RWCString("Invalid Request: Serial number not specified");
                            }
                            else
                            {
                                problem = RWCString("Invalid Request: (Load 1,2,3...) not specified");
                            }

                            status = CtiInvalidRequest;

                            vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT, pReq->getSOE(), getDescription(parse), problem, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                            retList.insert( CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), problem,  status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered()));
                        }

                        OutMessage->Retry = 2;                      // Default to two tries per route!
                        break;
                    }
                case ProtocolSA105Type:
                case ProtocolSA205Type:
                case ProtocolSA305Type:
                    {
                        OutMessage->EventCode |= NORESULT;
                        OutMessage->Retry = gConfigParms.getValueAsInt("PORTER_SA_REPEATS", 1);
                        break;
                    }
                case ProtocolFisherPierceType:
                default:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") Control type " << iTemp << endl;
                        }
                        break;
                    }
                }
                break;
            }
        case PutStatusRequest:
            {
                switch(iTemp = parse.getiValue("type"))
                {
                case ProtocolVersacomType:
                    {
                        BOOL        error = TRUE;
                        RWCString   problem(parse.getCommandStr() + ": " + FormatError(CtiInvalidRequest));

                        memset(&(OutMessage->Buffer.VSt), 0, sizeof(VSTRUCT));

                        if(INT_MIN == (OutMessage->Buffer.VSt.Address = parse.getiValue("serial")))
                        {
                            problem = RWCString("Invalid Request: Serial number not specified");
                        }
                        if(INT_MIN != parse.getiValue("proptest"))
                        {
                            OutMessage->Buffer.VSt.PropDIT = parse.getiValue("proptest");
                            error = FALSE;
                        }
                        else if(INT_MIN != parse.getiValue("ovuv"))
                        {
                            error = FALSE;
                        }

                        if(error)
                        {
                            status = CtiInvalidRequest;
                            vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT, pReq->getSOE(), getDescription(parse), problem, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                            retList.insert( CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), problem, status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered()));
                        }

                        /*
                         *  The VERSACOM tag is CRITICAL in that it indicates to the subsequent stages which
                         *  control path to take with this OutMessage!
                         */
                        OutMessage->EventCode    = VERSACOM | NORESULT;

                        break;
                    }
                case ProtocolExpresscomType:
                    {
                        int xcserial = parse.getiValue("serial");

                        parse.setValue("xc_serial", xcserial);

                        if( INT_MIN == xcserial )
                        {
                            RWCString   problem;

                            if( INT_MIN == xcserial )
                            {
                                problem = RWCString("Invalid Request: Serial number not specified");
                            }

                            status = CtiInvalidRequest;

                            vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT, pReq->getSOE(), getDescription(parse), problem, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                            retList.insert( CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), problem,  status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered()));
                        }

                        OutMessage->Retry = 2;                      // Default to two tries per route!

                        break;
                    }
                case ProtocolFisherPierceType:
                case ProtocolSA105Type:
                case ProtocolSA205Type:
                case ProtocolSA305Type:
                default:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ") PutConfig type " << iTemp << endl;
                        }
                        break;
                    }
                }
                break;
            }
        case GetStatusRequest:
        case LoopbackRequest:
        case GetValueRequest:
        case PutValueRequest:
        case GetConfigRequest:
        default:
            {
                status = NoExecuteRequestMethod;

                retList.insert( CTIDBG_new CtiReturnMsg(getID(), RWCString(OutMessage->Request.CommandStr), RWCString("System Devices do not support this command (yet?)"), status, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered()));
                break;
            }
        }
    }

    if( !status && OutMessage )
    {
        if( pReq->RouteId() )      // It has been requested on a route....
        {
            if( (Route = getRoute(pReq->RouteId())) )
            {
                OUTMESS *NewOMess = CTIDBG_new OUTMESS(*OutMessage); // Construct and copy.

                if(NewOMess)
                {
                    Route->ExecuteRequest(pReq, parse, NewOMess, vgList, retList, outList);

                    if(NewOMess)
                    {
                        if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                            dout << "  Route " << Route->getName() << " did not clean up his mess." << endl;
                        }
                        delete NewOMess;
                        NewOMess = 0;
                    }
                }
                else
                {
                    status = MEMORY;
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime() << " Memory error " << __FILE__ << " (" << __LINE__ << ")" << endl;
                    }
                }

            }
            else
            {
                status = BADROUTE;
                retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                                 RWCString(OutMessage->Request.CommandStr),
                                                 RWCString("Bad route specified for execution on the \"system\" device"),
                                                 status,
                                                 OutMessage->Request.RouteID,
                                                 OutMessage->Request.MacroOffset,
                                                 OutMessage->Request.Attempt,
                                                 OutMessage->Request.TrxID,
                                                 OutMessage->Request.UserID,
                                                 OutMessage->Request.SOE,
                                                 RWOrdered()));

            }
        }
        else        // OK, we need to hit each and every default route in the system...
        {
            CtiRouteManager::LockGuard guard(_routeMgr->getMux());

            if(_routeMgr->empty())
            {
                vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_LOADMANAGEMENT,
                                               pReq->getSOE(),
                                               getDescription(parse),
                                               "System has no default routes",
                                               GeneralLogType,
                                               SignalEvent,
                                               pReq->getUser()));

                retList.insert( CTIDBG_new CtiReturnMsg(getID(),
                                                 RWCString(OutMessage->Request.CommandStr),
                                                 "System has no default routes",
                                                 status,
                                                 OutMessage->Request.RouteID,
                                                 OutMessage->Request.MacroOffset,
                                                 OutMessage->Request.Attempt,
                                                 OutMessage->Request.TrxID,
                                                 OutMessage->Request.UserID,
                                                 OutMessage->Request.SOE));

            }
            else
            {
                try
                {
                    CtiRouteManager::spiterator rte_itr;

                    for(rte_itr = _routeMgr->begin(); rte_itr != _routeMgr->end(); CtiRouteManager::nextPos(rte_itr))
                    {
                        Route = rte_itr->second;

                        switch(Route->getType())
                        {
                        case RouteTypeCCU:         // These route types may be considered default routes
                        case RouteTypeTCU:
                        case RouteTypeLCU:
                        case RouteTypeRepeater:
                        case RouteTypeVersacom:
                        case RouteTypeTap:
                        case RouteTypeWCTP:
                        case RouteTypeSNPP:
                        case RouteTypeRTC:
                        case RouteTypeSeriesVLMI:
                            {
                                if( Route->isDefaultRoute() )
                                {
                                    OUTMESS *NewOMess = CTIDBG_new OUTMESS(*OutMessage); // Construct and copy.

                                    if(NewOMess)
                                    {
                                        Route->ExecuteRequest(pReq, parse, NewOMess, vgList, retList, outList);
                                    }
                                    else
                                    {
                                        status = MEMORY;
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " Memory error " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                        }
                                    }

                                    if(NewOMess)
                                    {
                                        if(getDebugLevel() & DEBUGLEVEL_LUDICROUS)
                                        {
                                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                                            dout << "  Route " << Route->getName() << " did not clean up his mess." << endl;
                                        }
                                        delete NewOMess;
                                        NewOMess = 0;
                                    }
                                }

                                break;
                            }
                        case RouteTypeMacro:
                        default:
                            {
                                break;
                            }
                        }
                    }
                }
                catch(...)
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }

        }
    }

    if(OutMessage != NULL)                // And get rid of our memory....
    {
        delete OutMessage;
        OutMessage = NULL;
    }


    return status;
}

RWCString CtiDeviceSystem::getDescription(const CtiCommandParser &parse) const
{
    RWCString resultString;
    INT   ser;
    INT   relays;
    INT   mask = 1;

    if( INT_MIN != (ser = parse.getiValue("serial")) )    // Is the serial number defined?
    {
        if(INT_MIN != (relays = parse.getiValue("relaymask")))
        {
            resultString =  "Serial: " + CtiNumStr(ser).spad(10) + " Relay:";

            for(int i = 0; i < 32; i++)
            {
                if(relays & (mask << i))
                {
                    resultString += " r" + CtiNumStr(i+1);
                }
            }
        }
        else
        {
            resultString = "Serial: " + CtiNumStr(ser).spad(10);
        }
    }
    else
    {
        resultString = "Device: " + getName();
    }

    return resultString;
}

CtiDeviceSystem::CtiDeviceSystem() {}

CtiDeviceSystem::CtiDeviceSystem(const CtiDeviceSystem& aRef)
{
    *this = aRef;
}

CtiDeviceSystem::~CtiDeviceSystem() {}

CtiDeviceSystem& CtiDeviceSystem::operator=(const CtiDeviceSystem& aRef)
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
    }
    return *this;
}

void CtiDeviceSystem::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);
}

void CtiDeviceSystem::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled
}
