#pragma warning( disable : 4786 )

/*-----------------------------------------------------------------------------*
 *
 * File:   dev_ion.cpp
 *
 * Class:  CtiDeviceION
 * Date:   07/02/2001
 *
 * Author: Matthew Fisher
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include <rw/rwtime.h>
#include <rw/rwdate.h>

#include "yukon.h"
#include "porter.h"

#include "dev_ion.h"

#include "pt_base.h"
#include "pt_status.h"
#include "pt_analog.h"
#include "pt_accum.h"

#include "msg_pcreturn.h"
#include "msg_cmd.h"
#include "msg_pdata.h"
#include "msg_multi.h"
#include "cmdparse.h"

#include "dlldefs.h"

#include "dupreq.h"
#include "dialup.h"
#include "portsup.h"

#include "logger.h"
#include "guard.h"


INT CtiDeviceION::ExecuteRequest( CtiRequestMsg              *pReq,
                                  CtiCommandParser           &parse,
                                  OUTMESS                   *&OutMessage,
                                  RWTPtrSlist< CtiMessage >  &vgList,
                                  RWTPtrSlist< CtiMessage >  &retList,
                                  RWTPtrSlist< OUTMESS >     &outList)
{
    INT   nRet = NoError;
    RWCString resultString;
//    CtiRoute *Route = NULL;

    bool found = false;

    switch( parse.getCommand() )
    {
    case ScanRequest:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Picked ScanRequest" << endl;
            }
            nRet = executeScan(pReq, parse, OutMessage, vgList, retList, outList);
            break;
        }
/*    case GetValueRequest:
       {
          nRet = executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
          break;
       }
    case PutValueRequest:
       {
          nRet = executePutValue(pReq, parse, OutMessage, vgList, retList, outList);
          break;
       }
    case ControlRequest:
       {
          nRet = executeControl(pReq, parse, OutMessage, vgList, retList, outList);
          break;
       }
    case GetStatusRequest:
       {
          nRet = executeGetStatus(pReq, parse, OutMessage, vgList, retList, outList);
          break;
       }
    case GetConfigRequest:
       {
          nRet = executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
          break;
       }
    case PutConfigRequest:
       {
          nRet = executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
          break;
       }
    case PutStatusRequest:
*/      default:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command. Command = " << parse.getCommand() << endl;
            }
            nRet = NoMethod;

            break;
        }
    }

    if(nRet != NoError)
    {
       {
          CtiLockGuard<CtiLogger> doubt_guard(dout);
          dout << RWTime() << " Couldn't come up with an operation for device " << getName() << endl;
          dout << RWTime() << "   Command: " << pReq->CommandString() << endl;
       }

       resultString = "NoMethod or invalid command.";
       retList.insert(new CtiReturnMsg(getID(),
                                       RWCString(OutMessage->Request.CommandStr),
                                       resultString,
                                       nRet,
                                       OutMessage->Request.RouteID,
                                       OutMessage->Request.MacroOffset,
                                       OutMessage->Request.Attempt,
                                       OutMessage->Request.TrxID,
                                       OutMessage->Request.UserID,
                                       OutMessage->Request.SOE,
                                       RWOrdered()));
    }

    return nRet;
}



INT CtiDeviceION::executeScan( CtiRequestMsg              *pReq,
                               CtiCommandParser           &parse,
                               OUTMESS                   *&OutMessage,
                               RWTPtrSlist< CtiMessage >  &vgList,
                               RWTPtrSlist< CtiMessage >  &retList,
                               RWTPtrSlist< OUTMESS >     &outList )
{
   INT   nRet = NoError;
   CHAR Temp[80];

   INT function;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << " in executeScan " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

   switch(parse.getiValue("scantype"))
   {
   case ScanRateStatus:
   case ScanRateGeneral:
      {
          nRet = GeneralScan( pReq, parse, OutMessage, vgList, retList, outList );
          break;
      }
/*   case ScanRateAccum:
      {
         function = CtiProtocolEmetcon::Scan_Accum;
         found = getOperation(CtiProtocolEmetcon::Scan_Accum, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
         break;
      }
   case ScanRateIntegrity:
      {
         function = CtiProtocolEmetcon::Scan_Integrity;
         found = getOperation(CtiProtocolEmetcon::Scan_Integrity, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
         break;
      }
*/   default:
      {
          nRet = NoMethod;
          break;
      }
   }

/*
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
      OutMessage->Remote    = getAddress();
      OutMessage->RouteID   = getRouteID();
      OutMessage->Priority  = MAXPRIORITY - 4;
      OutMessage->TimeOut   = 2;
      OutMessage->Sequence  = function;     // Helps us figure it out later!
      OutMessage->Retry     = 3;

      // Tell the porter side to complete the assembly of the message.
      strcpy(OutMessage->Request.CommandStr, pReq->CommandString());
   }
*/
   return nRet;
}



INT CtiDeviceION::GeneralScan( CtiRequestMsg              *pReq,
                               CtiCommandParser           &parse,
                               OUTMESS                   *&OutMessage,
                               RWTPtrSlist< CtiMessage >  &vgList,
                               RWTPtrSlist< CtiMessage >  &retList,
                               RWTPtrSlist< OUTMESS >     &outList,
                               INT                         ScanPriority )
{
    INT status = NORMAL;

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << RWTime() << "in GeneralScan " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    if( OutMessage != NULL )
    {
        //  maybe someday we can just set the command instead of passing it via the OutMessage...
        setCurrentCommand( CmdScanData );

        OutMessage->Buffer.DUPReq.Command[0] = CmdScanData;
        OutMessage->Buffer.DUPReq.LP_Time    = getLastLPTime( ).seconds( );

        //  load all appropriate data
        OutMessage->DeviceID  = getID( );
        OutMessage->Port      = getPortID( );
        OutMessage->Remote    = getAddress( );

        EstablishOutMessagePriority( OutMessage, ScanPriority );

        //  if this is a slave, drop the priority
//      if( !(getIED( ).isMaster( )) )
        if( !isMaster( ) )
            OutMessage->Priority--;

        OutMessage->TimeOut   = 2;
        OutMessage->EventCode = RESULT | ENCODED;
        OutMessage->Sequence  = 0;
        OutMessage->Retry     = 3;

        outList.insert(OutMessage);

        OutMessage = NULL;
    }
    else
    {
        status = MEMORY;
    }

    return status;
}


INT CtiDeviceION::generateCommand( CtiXfer &Transfer )
{
    INT  retCode = NORMAL;

    switch( getCurrentCommand( ) )
    {
        case CmdSelectMeter:
        case CmdScanData:
        case CmdLoadProfileData:
            {
                resolveNextState( );

                _dllLayer->outFrame( Transfer.getOutBuffer( ), &Transfer.getOutCount( ) );
                Transfer.setCRCFlag( 0 );
                Transfer.setInCountExpected( 250 );
                Transfer.setNonBlockingReads( true );  //  porter does the reading, and he doesn't know
                                                       //    to look at the first bytes for the length
                                                       //    of the packet - so we blindly take what we're given.
                break;
            }

        default:
            {
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState( StateScanAbort );
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") Invalid command " << getCurrentCommand( ) << " scanning " << getName( ) << endl;
                }
                retCode = StateScanAbort;
                break;
            }
    }

    return retCode;
}


INT CtiDeviceION::decodeResponse( CtiXfer &Transfer, INT commReturnValue )
{
    INT  retCode = NORMAL;

    switch( getCurrentCommand( ) )
    {
        case CmdSelectMeter:
        case CmdScanData:
        case CmdLoadProfileData:
            {
                _dllLayer->inFrame( Transfer.getInBuffer( ), *(Transfer.getInCountActual( )) );

                resolveNextState( );

                break;
            }

        default:
            {
                Transfer.setOutCount( 0 );
                Transfer.setInCountExpected( 0 );
                setCurrentState( StateScanAbort );
                {
                    CtiLockGuard<CtiLogger> dout_guard( dout );
                    dout << RWTime( ) << " (" << __LINE__ << ") Invalid command " << getCurrentCommand( ) << " scanning " << getName( ) << endl;
                }
                retCode = StateScanAbort;
                break;
            }
    }

    return retCode;
}


void CtiDeviceION::resolveNextState( void )
{
    CtiIONDataLinkLayer::DLLExternalStatus dllStatus;

    dllStatus = _dllLayer->getStatus( );

    //  if we're coming in here after the handshake, or we're doing another command
    if( getIONState( ) == IONStateUninitialized ||
        getIONState( ) == IONStateComplete )
    {
        //  this is only used for the external appearance of the device...
        setCurrentState( StateScanValueSet1 );
        //  ...  all real state info is kept inside the IONState types.
        setIONState( IONStateInit );
    }

    //  we only compute new states when we're uninitialized or
    //    done sending or receiving a whole message
    if( dllStatus == CtiIONDataLinkLayer::InDataComplete  ||
        dllStatus == CtiIONDataLinkLayer::OutDataComplete ||
        dllStatus == CtiIONDataLinkLayer::Uninitialized )
    {
        switch( getCurrentCommand( ) )
        {
            case CmdSelectMeter:
            {
                resolveNextStateSelectMeter( );
                break;
            }
            case CmdScanData:
            {
                resolveNextStateScanData( );
                break;
            }
            case CmdLoadProfileData:
            {
                resolveNextStateLoadProfile( );
                break;
            }
        }
    }

    if( getIONState( ) == IONStateComplete )
    {
        setCurrentState( StateScanComplete );
    }
    else if( getIONState( ) == IONStateAbort )
    {
        setCurrentState( StateScanAbort );
    }
}



void CtiDeviceION::resolveNextStateSelectMeter( void )
{
    switch( getIONState( ) )
    {
//        case IONStateInit:
//            {
//                break;
//            }
        default:
            setIONState( IONStateComplete );
    }
}


void CtiDeviceION::resolveNextStateScanData( void )
{
    CtiIONStatement *tmpStatement;
    CtiIONProgram   *tmpProgram;
    CtiIONMethod    *tmpMethod;
    unsigned char   *tmpBuf;

    switch( getIONState( ) )
    {
        case IONStateInit:
        case IONStateRequestFeatureManagerInfo:
            {
                tmpMethod = new CtiIONMethod( CtiIONMethod::ReadModuleSetupHandles );  //  ReadManagedClass
                tmpStatement = new CtiIONStatement( IONFeatureManagerHandle, tmpMethod );  // ustabeed 132
                tmpProgram = new CtiIONProgram( tmpStatement );
                _dsBuf = new CtiIONDataStream( );
                _dsBuf->appendItem( tmpProgram );
                _appLayer->init( *_dsBuf );
                _netLayer->init( *_appLayer, 0, 20000, 102 );
                _dllLayer->setToOutput( *_netLayer, _netLayer->getSrcID( ), _netLayer->getDstID( ) );

                //  also deletes the contained tmpProgram and tmpMethod
                delete _dsBuf;

                setIONState( IONStateReceiveFeatureManagerInfo );

                break;
            }

        case IONStateReceiveFeatureManagerInfo:
            {
                _dllLayer->setToInput( );

                setIONState( IONStateRequestManagerInfo );

                break;
            }

        case IONStateRequestManagerInfo:
            {
                _netLayer->init( *_dllLayer );
                _appLayer->init( *_netLayer );

                if( (tmpBuf = new unsigned char( _appLayer->getPayloadLength( ) )) != NULL )
                {
                    _appLayer->putPayload( tmpBuf );
                    _dsBuf = new CtiIONDataStream( tmpBuf, _appLayer->getPayloadLength( ) );
                    delete tmpBuf;

                    for( int i = 0; i < _dsBuf->getItemCount( ); i++ )
                    {
                        cout << _dsBuf->getItem(i)->getType( ) << endl;
                    }
                }

                setIONState( IONStateComplete );

                break;
            }

        case IONStateReceiveManagerInfo:
            {
                _dllLayer->setToInput( );
                break;
            }

        case IONStateRequestModuleInfo:
            {
                break;
            }

        case IONStateReceiveModuleInfo:
            {
                break;
            }

        case IONStateRequestData:
            {
                break;
            }

        case IONStateReceiveData:
            {
                break;
            }
    }
}


void CtiDeviceION::resolveNextStateLoadProfile( void )
{
    switch( getIONState( ) )
    {
        default:
            setIONState( IONStateComplete );
            break;

/*        case IONStateInit:
            {
                break;
            }
 */
    }
}


int CtiDeviceION::copyLoadProfileData( BYTE *aInMessBuffer, ULONG &aTotalBytes )
{
    return NoError;
}


int CtiDeviceION::allocateDataBins( OUTMESS *outMess )
{
    YukonError_t retCode;

    //  setTotalByteCount( 0 );

    _dllLayer = new CtiIONDataLinkLayer;
    _netLayer = new CtiIONNetworkLayer;
    _appLayer = new CtiIONApplicationLayer;

    if( _dllLayer != NULL &&
        _netLayer != NULL &&
        _appLayer != NULL )
    {
        //  set the command from the outMess command
        setCurrentCommand( (CtiDeviceIED::CtiMeterCmdStates_t)outMess->Buffer.DUPReq.Command[0] );

        //  grab the lastLPTime
        _lastLPTime = outMess->Buffer.DUPReq.LP_Time;

        //  this guy simply responds to requests - currently, passwords aren't required for data retrieval
        //    setting this now makes it bypass the handshake routine later in porter
        setCurrentState( StateHandshakeComplete );

        retCode = NoError;
    }
    else
    {
        //  abort - we can't allocate the needed network layers.
        setCurrentState( StateHandshakeAbort );

        //  CommunicateDevice doesn't currently have error checking around allocateDataBins;
        //    if/when it does, we can return this instead of aborting the handshake
//        retCode = MemoryError;

        retCode = NoError;
    }

    return retCode;
}


int CtiDeviceION::freeDataBins( void )
{
    if( _dllLayer != NULL )
        delete _dllLayer;
    _dllLayer = NULL;

    if( _netLayer != NULL )
        delete _netLayer;
    _netLayer = NULL;

    if( _appLayer != NULL )
        delete _appLayer;
    _appLayer = NULL;

    return NORMAL;
}


int CtiDeviceION::ResultDecode( INMESS *InMessage,
                                RWTime &TimeNow,
                                RWTPtrSlist< CtiMessage >   &vgList,
                                RWTPtrSlist< CtiMessage > &retList,
                                RWTPtrSlist< OUTMESS > &outList )
{
    // intialize the command based on the in message
    setCurrentCommand( (CtiMeterCmdStates_t)InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[0] );
    setCurrentState  ( (CtiMeterMachineStates_t)InMessage->Buffer.DUPSt.DUPRep.ReqSt.Command[1] );

    switch( getCurrentCommand( ) )
    {
        case CmdScanData:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime( ) << " Scan decode for device " << getName( ) << " in progress " << endl;
                }

                decodeResultScan( InMessage, TimeNow, vgList, retList, outList );
                break;
            }
        case CmdLoadProfileData:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime( ) << " LP decode for device " << getName( ) << " in progress " << endl;
                }

                // just in case we're getting an empty message
                if( getCurrentState( ) == StateScanReturnLoadProfile )
                {
                    decodeResultLoadProfile( InMessage, TimeNow, vgList, retList, outList );
                }
                else
                {
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << RWTime( ) << " LP decode failed device " << getName( ) << " invalid state " << endl;
                    }
                }
                break;
            }
        default:
            {
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime( ) << " (" << __LINE__ << ") *** ERROR *** Invalid decode for " << getName( ) << endl;
                }
            }
    }

    return NORMAL;
}


INT CtiDeviceION::decodeResultScan( INMESS *InMessage,
                                    RWTime &TimeNow,
                                    RWTPtrSlist< CtiMessage > &vgList,
                                    RWTPtrSlist< CtiMessage > &retList,
                                    RWTPtrSlist< OUTMESS >    &outList )
{
return NoError;
}


INT CtiDeviceION::decodeResultLoadProfile( INMESS *InMessage,
                                           RWTime &TimeNow,
                                           RWTPtrSlist< CtiMessage > &vgList,
                                           RWTPtrSlist< CtiMessage > &retList,
                                           RWTPtrSlist< OUTMESS >    &outList )
{
return NoError;
}



