/*-----------------------------------------------------------------------------*
*
* File:   dev_mct_broadcast
*
* Date:   2/7/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/06/27 21:06:56 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)


#include "dev_mct_broadcast.h"
#include "logger.h"
#include "numstr.h"
#include "porter.h"

#include "dev_mct31x.h"  //  for IED scanning capability

set< CtiDLCCommandStore > CtiDeviceMCTBroadcast::_commandStore;

void CtiDeviceMCTBroadcast::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);

    selector.where( keyTable["type"] == "MCT Broadcast" && selector.where() );
}


void CtiDeviceMCTBroadcast::DecodeDatabaseReader(RWDBReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);
}



INT CtiDeviceMCTBroadcast::ExecuteRequest( CtiRequestMsg              *pReq,
                                           CtiCommandParser           &parse,
                                           OUTMESS                   *&OutMessage,
                                           RWTPtrSlist< CtiMessage >  &vgList,
                                           RWTPtrSlist< CtiMessage >  &retList,
                                           RWTPtrSlist< OUTMESS >     &outList )
{
    INT        nRet  = NoError;
    CtiRouteSPtr Route;
    RWCString  resultString;
    long routeID;
    char Temp[80];

    bool found = false;
    CtiReturnMsg* pRet = 0;

    switch( parse.getCommand( ) )
    {
    case PutStatusRequest:
        {
            nRet = executePutStatus( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
    case PutConfigRequest:
        {
            nRet = executePutConfig( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
    case PutValueRequest:
        {
            nRet = executePutValue( pReq, parse, OutMessage, vgList, retList, outList );
            break;
        }
    case LoopbackRequest:
    case ScanRequest:
    case GetValueRequest:
    case ControlRequest:
    case GetConfigRequest:
    case GetStatusRequest:
    default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command on EMETCON route. Command = " << parse.getCommand( ) << endl;
            }
            nRet = NoMethod;

            break;
        }
    }

    if( nRet != NORMAL )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime( ) << " Couldn't come up with an operation for device " << getName( ) << endl;
            dout << RWTime( ) << "   Command: " << pReq->CommandString( ) << endl;
        }

        resultString = "NoMethod or invalid command. (" + RWCString(__FILE__) + ")";
        retList.insert( CTIDBG_new CtiReturnMsg(getID( ), RWCString(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered( )) );
    }
    else
    {
        if(OutMessage != NULL)
        {
            outList.append( OutMessage );
            OutMessage = NULL;
        }
        /*
         ***************************** PASS OFF TO ROUTE BEYOND THIS POINT ****************************************
         */

        for(int i = outList.entries() ; i > 0; i-- )
        {
            OUTMESS *pOut = outList.get();

            if( pReq->RouteId() )
                routeID = pReq->RouteId();
            else
                routeID = getRouteID();

            pOut->Request.RouteID = routeID;

            EstablishOutMessagePriority( pOut, MAXPRIORITY - 4 );

            if( (Route = CtiDeviceBase::getRoute( pOut->Request.RouteID )) )
            {
                pOut->TargetID  = getID();
                pOut->EventCode = BWORD | WAIT;

                if( parse.isKeyValid("noqueue") )
                {
                    pOut->EventCode |= DTRAN;
                }

                pOut->Buffer.BSt.Address      = getAddress() + LEADMETER_OFFSET;     // The DLC address of the MCT.
                pOut->Buffer.BSt.DeviceType   = getType();
                pOut->Buffer.BSt.SSpec        = 0;                              // FIX FIX FIX ??? 2/10/03 CGP // getSSpec();

                /*
                 * OK, these are the items we are about to set out to perform..  Any additional signals will
                 * be added into the list upon completion of the Execute!
                 */
                if(parse.getActionItems().entries())
                {
                    for(size_t offset = offset; offset < parse.getActionItems().entries(); offset++)
                    {
                        RWCString actn = parse.getActionItems()[offset];
                        RWCString desc = getDescription(parse);

                        vgList.insert(CTIDBG_new CtiSignalMsg(SYS_PID_SYSTEM, pReq->getSOE(), desc, actn, LoadMgmtLogType, SignalEvent, pReq->getUser()));
                    }
                }

                /*
                 *  Form up the reply here since the ExecuteRequest funciton will consume the
                 *  OutMessage.
                 */
                pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(pOut->Request.CommandStr), Route->getName(), nRet, pOut->Request.RouteID, pOut->Request.MacroOffset, pOut->Request.Attempt, pOut->Request.TrxID, pOut->Request.UserID, pOut->Request.SOE, RWOrdered());
                // Start the control request on its route(s)
                if( (nRet = Route->ExecuteRequest(pReq, parse, pOut, vgList, retList, outList)) )
                {
                    resultString = "ERROR " + CtiNumStr(nRet) + " performing command on route " + Route->getName().data() + "\n" + FormatError(nRet);
                    pRet->setResultString(resultString);
                    pRet->setStatus( nRet );
                }
                else
                {
                    delete pRet;
                    pRet = 0;
                }
            }
            else if( getRouteManager() == 0 )   // If there is no route manager, we need porter to do the route work!
            {
                // Tell the porter side to complete the assembly of the message.
                pOut->Request.BuildIt = TRUE;
                strncpy(pOut->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);

                outList.insert( pOut );         // May porter have mercy.
                pOut = 0;
            }
            else
            {
                nRet = BADROUTE;
                resultString = "ERROR: Route or Route Transmitter not available for device " + getName();
                pRet = CTIDBG_new CtiReturnMsg(getID(), RWCString(pOut->Request.CommandStr), resultString, nRet, pOut->Request.RouteID, pOut->Request.MacroOffset, pOut->Request.Attempt, pOut->Request.TrxID, pOut->Request.UserID, pOut->Request.SOE, RWOrdered());
            }

            if(pRet)
            {
                retList.insert( pRet );
            }

            if( pOut )
            {
                delete pOut;
            }
        }
    }


    return nRet;
}


INT CtiDeviceMCTBroadcast::executePutConfig(CtiRequestMsg                  *pReq,
                                            CtiCommandParser               &parse,
                                            OUTMESS                        *&OutMessage,
                                            RWTPtrSlist< CtiMessage >      &vgList,
                                            RWTPtrSlist< CtiMessage >      &retList,
                                            RWTPtrSlist< OUTMESS >         &outList)
{
    bool  found = false;
    INT   function = 0;
    INT   nRet = NoError;
    int   intervallength;
    RWCString temp;
    RWTime NowTime;
    RWDate NowDate(NowTime);  //  unlikely they'd be out of sync, but just to make sure...

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ), RWCString(OutMessage->Request.CommandStr), RWCString(), nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered( ));

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress() + LEADMETER_OFFSET;
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);
    }

    return nRet;
}

INT CtiDeviceMCTBroadcast::executePutStatus(CtiRequestMsg                  *pReq,
                                            CtiCommandParser               &parse,
                                            OUTMESS                        *&OutMessage,
                                            RWTPtrSlist< CtiMessage >      &vgList,
                                            RWTPtrSlist< CtiMessage >      &retList,
                                            RWTPtrSlist< OUTMESS >         &outList)
{
    bool  found = false;
    INT   nRet = NoError;
    int   intervallength;
    RWCString temp;
    RWTime NowTime;
    RWDate NowDate(NowTime);  //  unlikely they'd be out of sync, but just to make sure...

    INT function;

    OutMessage->Buffer.BSt.Message[0] = 0;
    OutMessage->Buffer.BSt.Message[1] = 0;
    OutMessage->Buffer.BSt.Message[2] = 0;

    if(parse.getFlags() & CMD_FLAG_PS_RESET )
    {
        function = CtiProtocolEmetcon::PutStatus_Reset;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if(parse.getFlags() & (CMD_FLAG_PS_FREEZEZERO | CMD_FLAG_PS_FREEZEONE))
    {
        if(!_lastFreeze)
        {
            _lastFreeze = true;

            function = CtiProtocolEmetcon::PutStatus_ResetZero;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
        else
        {
            _lastFreeze = false;

            function = CtiProtocolEmetcon::PutStatus_ResetZero;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
        }
    }

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        // Load all the other stuff that is needed
        OutMessage->DeviceID  = getID();
        OutMessage->TargetID  = getID();
        OutMessage->Port      = getPortID();
        OutMessage->Remote    = getAddress() + LEADMETER_OFFSET;
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();

        strncpy(OutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);
    }

    return nRet;
}


INT CtiDeviceMCTBroadcast::executePutValue(CtiRequestMsg                  *pReq,
                                           CtiCommandParser               &parse,
                                           OUTMESS                        *&OutMessage,
                                           RWTPtrSlist< CtiMessage >      &vgList,
                                           RWTPtrSlist< CtiMessage >      &retList,
                                           RWTPtrSlist< OUTMESS >         &outList)
{
    INT    nRet = NoError,
           i;
    long   rawPulses;
    double dial;

    INT function;

    bool found = false;


    if(parse.getFlags() & CMD_FLAG_PV_IED)     // This parse has the token IED in it!
    {
        //  currently only know how to reset IEDs
        if(parse.getFlags() & CMD_FLAG_PV_RESET)
        {
            function = CtiProtocolEmetcon::PutValue_IEDReset;
            found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

            if(found)
            {
                // Load all the other stuff that is needed
                OutMessage->DeviceID  = getID();
                OutMessage->TargetID  = getID();
                OutMessage->Port      = getPortID();
                OutMessage->Remote    = getAddress();
                OutMessage->TimeOut   = 2;
                OutMessage->Sequence  = function;         // Helps us figure it out later!
                OutMessage->Retry     = 2;

                OutMessage->Request.RouteID   = getRouteID();
                strncpy(OutMessage->Request.CommandStr, pReq->CommandString(), COMMAND_STR_SIZE);
            }

            if( getType() == TYPEMCT360 || getType() == TYPEMCT370 )
            {
                switch( ((CtiDeviceMCT31X *)this)->getIEDPort().getIEDType() )
                {
                    case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                        OutMessage->Buffer.BSt.Function   = CtiDeviceMCT31X::MCT360_AlphaResetPos;
                        OutMessage->Buffer.BSt.Length     = CtiDeviceMCT31X::MCT360_AlphaResetLen;
                        OutMessage->Buffer.BSt.Message[0] = 60;  //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                        OutMessage->Buffer.BSt.Message[1] = 1;   //  Demand Reset  function code for the Alpha
                        break;

                    case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                        OutMessage->Buffer.BSt.Function   = CtiDeviceMCT31X::MCT360_LGS4ResetPos;
                        OutMessage->Buffer.BSt.Length     = CtiDeviceMCT31X::MCT360_LGS4ResetLen;
                        OutMessage->Buffer.BSt.Message[0] = 3;     //  MCT's LG command identifier
                        OutMessage->Buffer.BSt.Message[1] = 60;    //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                        OutMessage->Buffer.BSt.Message[2] = 0x2B;  //  Demand Reset function code for the LG S4
                        break;

                    default:
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        break;
                }
            }
        }
    }

    if(!found)
    {
        nRet = NoMethod;
    }

    return nRet;
}

//
//  My apologies to those who follow.
//
bool CtiDeviceMCTBroadcast::initCommandStore()
{
    bool failed = false;
    CtiDLCCommandStore cs;


    cs._cmd     = CtiProtocolEmetcon::PutStatus_Reset;
    cs._io      = IO_FCT_WRITE;
    cs._funcLen = make_pair( (int)MCTBCAST_ResetPF, (int)MCTBCAST_ResetPFLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutStatus_ResetZero;
    cs._io      = IO_FCT_WRITE;
    cs._funcLen = make_pair( (int)MCTBCAST_FreezeZero, (int)MCTBCAST_FreezeLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutStatus_ResetOne;
    cs._io      = IO_FCT_WRITE;
    cs._funcLen = make_pair( (int)MCTBCAST_FreezeOne, (int)MCTBCAST_FreezeLen );
    _commandStore.insert( cs );

    cs._cmd     = CtiProtocolEmetcon::PutValue_IEDReset;
    cs._io      = IO_FCT_WRITE;
    cs._funcLen = make_pair( 0, 0 );
    _commandStore.insert( cs );


    return failed;
}

bool CtiDeviceMCTBroadcast::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    if(_commandStore.empty())  // Must initialize!
    {
        initCommandStore();
    }

    CTICMDSET::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

    if( itr != _commandStore.end() )    // It's prego!
    {
        CtiDLCCommandStore &cs = *itr;
        function = cs._funcLen.first;           // Copy over the found funcLen pair!
        length = cs._funcLen.second;           // Copy over the found funcLen pair!
        io = cs._io;
        found = true;
    }

    return found;
}

INT CtiDeviceMCTBroadcast::ResultDecode(INMESS *InMessage, RWTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}


