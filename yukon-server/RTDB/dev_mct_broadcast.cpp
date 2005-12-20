/*-----------------------------------------------------------------------------*
*
* File:   dev_mct_broadcast
*
* Date:   2/7/2003
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.18 $
* DATE         :  $Date: 2005/12/20 17:20:24 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dev_mct_broadcast.h"
#include "logger.h"
#include "numstr.h"
#include "porter.h"

#include "dev_mct.h"     //  for freeze commands
#include "dev_mct31x.h"  //  for IED scanning capability
#include "dev_mct410.h"
#include "ctidate.h"
#include "ctitime.h"
#include "rwutil.h"


using Cti::Protocol::Emetcon;


set< CtiDLCCommandStore > CtiDeviceMCTBroadcast::_commandStore;

void CtiDeviceMCTBroadcast::getSQL(RWDBDatabase &db,  RWDBTable &keyTable, RWDBSelector &selector)
{
    Inherited::getSQL(db, keyTable, selector);

    selector.where( rwdbUpper(keyTable["type"]) == "MCT BROADCAST" && selector.where() );
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
    int nRet = NoError;
    RWTPtrSlist< OUTMESS > tmpOutList;

    switch( parse.getCommand( ) )
    {
        case PutStatusRequest:
        {
            nRet = executePutStatus( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case PutConfigRequest:
        {
            nRet = executePutConfig( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case PutValueRequest:
        {
            nRet = executePutValue( pReq, parse, OutMessage, vgList, retList, tmpOutList );
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
                dout << CtiTime( ) << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "Unsupported command on EMETCON route. Command = " << parse.getCommand( ) << endl;
            }

            nRet = NoMethod;

            break;
        }
    }

    if( nRet != NORMAL )
    {
        string resultString;

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime( ) << " Couldn't come up with an operation for device " << getName( ) << endl;
            dout << CtiTime( ) << "   Command: " << pReq->CommandString( ) << endl;
        }

        resultString = "NoMethod or invalid command. (" + string(__FILE__) + ")";
        retList.insert( CTIDBG_new CtiReturnMsg(getID( ), string(OutMessage->Request.CommandStr), resultString, nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered( )) );
    }
    else
    {
        if(OutMessage != NULL)
        {
            tmpOutList.append( OutMessage );
            OutMessage = NULL;
        }

        executeOnDLCRoute(pReq, parse, OutMessage, tmpOutList, vgList, retList, outList, false);
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
    string temp;
    CtiTime NowTime;
    CtiDate NowDate(NowTime);  //  unlikely they'd be out of sync, but just to make sure...

    CtiReturnMsg *errRet = CTIDBG_new CtiReturnMsg(getID( ), string(OutMessage->Request.CommandStr), string(), nRet, OutMessage->Request.RouteID, OutMessage->Request.MacroOffset, OutMessage->Request.Attempt, OutMessage->Request.TrxID, OutMessage->Request.UserID, OutMessage->Request.SOE, RWOrdered( ));

    if(parse.isKeyValid("rawloc"))
    {
        function = Emetcon::PutConfig_Raw;

        OutMessage->Buffer.BSt.Function = parse.getiValue("rawloc");

        temp = parse.getsValue("rawdata");
        if( temp.length() > 15 )
        {
            //  trim string to be 15 bytes long
            temp.erase( 15 );
        }

        OutMessage->Buffer.BSt.Length = temp.length();
        for( int i = 0; i < temp.length(); i++ )
        {
            OutMessage->Buffer.BSt.Message[i] = temp[i];
        }

        if( parse.isKeyValid("rawfunc") )
        {
            OutMessage->Buffer.BSt.IO = Emetcon::IO_Function_Write;
        }
        else
        {
            OutMessage->Buffer.BSt.IO = Emetcon::IO_Write;
        }

        found = true;
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
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
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
    string temp;
    CtiTime NowTime;
    CtiDate NowDate(NowTime);  //  unlikely they'd be out of sync, but just to make sure...

    INT function;

    OutMessage->Buffer.BSt.Message[0] = 0;
    OutMessage->Buffer.BSt.Message[1] = 0;
    OutMessage->Buffer.BSt.Message[2] = 0;

    // Load all the other stuff that is needed
    OutMessage->DeviceID  = getID();
    OutMessage->TargetID  = getID();
    OutMessage->Port      = getPortID();
    OutMessage->Remote    = getAddress();
    OutMessage->TimeOut   = 2;
    OutMessage->Retry     = 2;

    OutMessage->Request.RouteID = getRouteID();

    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    if( parse.getFlags() & CMD_FLAG_PS_RESET )
    {
        function = Emetcon::PutStatus_Reset;
        found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
    }
    else if( parse.isKeyValid("freeze") )
    {
        //  if(!_lastFreeze)
        //    _lastFreeze = true;

        if( parse.isKeyValid("voltage") )
        {
            if( parse.getiValue("freeze") == 1 )
            {
                function = Emetcon::PutStatus_FreezeVoltageOne;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            }
            else if( parse.getiValue("freeze") == 2 )
            {
                function = Emetcon::PutStatus_FreezeVoltageTwo;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            }
        }
        else
        {
            if( parse.getiValue("freeze") == 1 )
            {
                function = Emetcon::PutStatus_FreezeOne;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            }
            else if( parse.getiValue("freeze") == 2 )
            {
                function = Emetcon::PutStatus_FreezeTwo;
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);
            }
        }

        if( found )
        {
            //  create one for the MCT 400 series, too
            OUTMESS *MCT400OutMessage = CTIDBG_new OUTMESS(*OutMessage);

            //  these are all "command" type messages - a zero-length write, so it's safe to muck about here
            MCT400OutMessage->Sequence = MCT400OutMessage->Buffer.BSt.Function;

            MCT400OutMessage->Buffer.BSt.Length     = 2;
            MCT400OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;
            //  this is a little tricky - watch as we carefully swap it out...
            MCT400OutMessage->Buffer.BSt.Message[1] = MCT400OutMessage->Buffer.BSt.Function;
            //  ...  right before we stomp over the original location
            MCT400OutMessage->Buffer.BSt.Function   = CtiDeviceMCT410::MCT4XX_FuncWrite_Command;

            if( stringContainsIgnoreCase(parse.getCommandStr()," all") )
            {
                //  the MCT 400 message is in ADDITION to the normal command
                outList.append(MCT400OutMessage);
            }
            else
            {
                //  the MCT 400 message REPLACES the normal command (kinda backward, but it works)
                delete OutMessage;
                OutMessage = MCT400OutMessage;
            }

            MCT400OutMessage = 0;
        }
    }

    if(!found)
    {
        nRet = NoMethod;
    }
    else
    {
        OutMessage->Sequence = function;     // Helps us figure it out later!
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

    string command_string(OutMessage->Request.CommandStr);

    INT function;

    bool found = false;


    if(parse.getFlags() & CMD_FLAG_PV_PWR)
    {
        if(parse.getFlags() & CMD_FLAG_PV_RESET)
        {
            if( command_string.find(" 400")!=string::npos )
            {
                OutMessage->Buffer.BSt.Function = CtiDeviceMCT410::MCT4XX_Command_PowerfailReset;
                OutMessage->Buffer.BSt.Length = 0;
                OutMessage->Buffer.BSt.IO = Emetcon::IO_Write;

                found = true;
            }
        }
    }
    else if(parse.getFlags() & CMD_FLAG_PV_IED)     //  This parse has the token "IED" in it!
    {
        if(parse.getFlags() & CMD_FLAG_PV_RESET)
        {
            int iedtype = ((CtiDeviceMCT31X *)this)->getIEDPort().getIEDType();

            function = Emetcon::PutValue_IEDReset;

            if( getType() == TYPEMCT360 || getType() == TYPEMCT370 )
            {
                found = getOperation(function, OutMessage->Buffer.BSt.Function, OutMessage->Buffer.BSt.Length, OutMessage->Buffer.BSt.IO);

                switch( iedtype )
                {
                    case CtiTableDeviceMCTIEDPort::AlphaPowerPlus:
                    {
                        OutMessage->Buffer.BSt.Function   = CtiDeviceMCT31X::MCT360_AlphaResetPos;
                        OutMessage->Buffer.BSt.Length     = CtiDeviceMCT31X::MCT360_AlphaResetLen;
                        OutMessage->Buffer.BSt.Message[0] = 60;  //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                        OutMessage->Buffer.BSt.Message[1] = 1;   //  Demand Reset  function code for the Alpha
                        break;
                    }

                    case CtiTableDeviceMCTIEDPort::LandisGyrS4:
                    {
                        OutMessage->Buffer.BSt.Function   = CtiDeviceMCT31X::MCT360_LGS4ResetPos;
                        OutMessage->Buffer.BSt.Length     = CtiDeviceMCT31X::MCT360_LGS4ResetLen;
                        OutMessage->Buffer.BSt.Message[0] = CtiDeviceMCT31X::MCT360_LGS4ResetID;
                        OutMessage->Buffer.BSt.Message[1] = 60;    //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                        OutMessage->Buffer.BSt.Message[2] = 0x2B;  //  Demand Reset function code for the LG S4
                        break;
                    }

                    case CtiTableDeviceMCTIEDPort::GeneralElectricKV:
                    {
                        OutMessage->Buffer.BSt.Function   = CtiDeviceMCT31X::MCT360_GEKVResetPos;
                        OutMessage->Buffer.BSt.Length     = CtiDeviceMCT31X::MCT360_GEKVResetLen;
                        OutMessage->Buffer.BSt.Message[0] = CtiDeviceMCT31X::MCT360_GEKVResetID;
                        OutMessage->Buffer.BSt.Message[1] = 60;    //  delay timer won't allow a reset for 15 minutes (in 15 sec ticks)
                        OutMessage->Buffer.BSt.Message[2] = 0x00;  //  sequence, standard proc, and uppoer bits of proc are 0
                        OutMessage->Buffer.BSt.Message[3] = 0x09;  //  procedure 9
                        OutMessage->Buffer.BSt.Message[4] = 0x01;  //  parameter length 1
                        OutMessage->Buffer.BSt.Message[5] = 0x01;  //  demand reset bit set
                        break;
                    }

                    default:
                    {
                        {
                            CtiLockGuard<CtiLogger> doubt_guard(dout);
                            dout << CtiTime() << " **** Invalid IED type " << iedtype << " on device \'" << getName() << "\' **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        }
                        break;
                    }
                }
            }

            if(found)
            {
                // Load all the other stuff that is needed
                OutMessage->DeviceID  = getID();
                OutMessage->TargetID  = getID();
                OutMessage->Port      = getPortID();
                OutMessage->Remote    = getAddress();
                OutMessage->TimeOut   = 2;
                OutMessage->Sequence  = function;         //  Helps us figure it out later
                OutMessage->Retry     = 2;

                OutMessage->Request.RouteID   = getRouteID();
                strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
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


    cs._cmd     = Emetcon::PutStatus_Reset;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair((int)MCTBCAST_ResetPF, (int)MCTBCAST_ResetPFLen);
    _commandStore.insert(cs);

    cs._cmd     = Emetcon::PutStatus_FreezeOne;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair((int)CtiDeviceMCT::MCT_Command_FreezeOne, 0);
    _commandStore.insert(cs);

    cs._cmd     = Emetcon::PutStatus_FreezeTwo;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair((int)CtiDeviceMCT::MCT_Command_FreezeTwo, 0);
    _commandStore.insert(cs);

    cs._cmd     = Emetcon::PutValue_IEDReset;
    cs._io      = Emetcon::IO_Function_Write;
    cs._funcLen = make_pair(0, 0);
    _commandStore.insert(cs);

    cs._cmd     = Emetcon::PutStatus_FreezeVoltageOne;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)CtiDeviceMCT410::MCT4XX_Command_FreezeVoltageOne, 0);
    _commandStore.insert(cs);

    cs._cmd     = Emetcon::PutStatus_FreezeVoltageTwo;
    cs._io      = Emetcon::IO_Write;
    cs._funcLen = make_pair((int)CtiDeviceMCT410::MCT4XX_Command_FreezeVoltageTwo, 0);
    _commandStore.insert(cs);


    return failed;
}

bool CtiDeviceMCTBroadcast::getOperation( const UINT &cmd, USHORT &function, USHORT &length, USHORT &io )
{
    bool found = false;

    if(_commandStore.empty())  // Must initialize!
    {
        initCommandStore();
    }

    DLCCommandSet::iterator itr = _commandStore.find(CtiDLCCommandStore(cmd));

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

INT CtiDeviceMCTBroadcast::ResultDecode(INMESS *InMessage, CtiTime &TimeNow, RWTPtrSlist< CtiMessage > &vgList, RWTPtrSlist< CtiMessage > &retList, RWTPtrSlist< OUTMESS > &outList)
{
    INT status = NORMAL;


    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    return status;
}

LONG CtiDeviceMCTBroadcast::getAddress() const
{
    return CarrierSettings.getAddress() + MCTBCAST_LeadMeterOffset;
}

