/*
*   Copyright (c) 2009 Cooper Power Systems EAS. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dev_lcr3102.h"


namespace Cti       {
namespace Device    {


using Protocol::Emetcon;

const LCR3102::CommandSet  LCR3102::_commandStore = LCR3102::initCommandStore();


LCR3102::LCR3102( )
{

}


LCR3102::LCR3102( const LCR3102 &aRef )
{
    *this = aRef;
}


LCR3102::~LCR3102( )
{

}


LCR3102& LCR3102::operator=( const LCR3102 &aRef )
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
//????   Is this needed?
//        CtiLockGuard<CtiMutex> guard(_classMutex);            // Protect this device!
    }
    return *this;
}


INT LCR3102::ExecuteRequest ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    int nRet = NoMethod;

    bool twoWay = false;
    list< OUTMESS* > tmpOutList;
 
    if( OutMessage )
    {
        EstablishOutMessagePriority( OutMessage, MAXPRIORITY - 4 );
    }

    switch( parse.getCommand( ) )
    {
        case GetValueRequest:
        {
            nRet = executeGetValue( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case GetConfigRequest:
        case LoopbackRequest:
        case ScanRequest:
        case PutValueRequest:
        case ControlRequest:
        case GetStatusRequest:
        case PutStatusRequest:
        case PutConfigRequest:
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

        resultString = "NoMethod or invalid command.";
        retList.push_back( CTIDBG_new CtiReturnMsg(getID( ),
                                                string(OutMessage->Request.CommandStr),
                                                resultString,
                                                nRet,
                                                OutMessage->Request.RouteID,
                                                OutMessage->Request.MacroOffset,
                                                OutMessage->Request.Attempt,
                                                OutMessage->Request.GrpMsgID,
                                                OutMessage->Request.UserID,
                                                OutMessage->Request.SOE,
                                                CtiMultiMsg_vec( )) );
    }
    else
    {
        if(OutMessage != NULL)
        {
            tmpOutList.push_back( OutMessage );
            OutMessage = NULL;
        }

        nRet = executeOnDLCRoute(pReq, parse, OutMessage, tmpOutList, vgList, retList, outList, twoWay);
    }

    return nRet;
}


INT LCR3102::ErrorDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool &overrideExpectMore )
{
    INT retCode = NOTNORMAL;


    return retCode;
}


INT LCR3102::ResultDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NOTNORMAL;

    switch(InMessage->Sequence)
    {
        case Emetcon::GetValue_IntervalLast:
        {
            status = decodeGetValueIntervalLast( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case Emetcon::GetValue_Runtime:
        {
            status = decodeGetValueRuntime( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case Emetcon::GetValue_Shedtime:
        {
            status = decodeGetValueShedtime( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case Emetcon::GetValue_PropCount:
        {
            status = decodeGetValuePropCount( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        default:
        {
            // some error reporting goes here...

        }
    }

    return status;
}


INT LCR3102::decodeGetValueIntervalLast( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NOTNORMAL;

    DSTRUCT      *DSt       = &InMessage->Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        point_info pi;
        pi.freeze_bit = false;

        string results = getName() + " / Last Interval kW:";

        int   message_index   = 1;
        UCHAR relay_info      = DSt->Message[0];
        UCHAR multiplier_mask = 0xC0;
        int   relay_number    = 1;

        for( UCHAR relay_mask = 0x01; relay_mask < 0x10; relay_mask <<= 1, relay_number++ )
        {
            if( relay_info & relay_mask )
            {
                int value = (DSt->Message[message_index] << 8) | DSt->Message[message_index + 1];
                message_index += 2;

                double multiplier = ( relay_info & multiplier_mask ) ? 1.0 : 0.1;
                multiplier_mask >>= 2;

                results += "  Relay " + CtiNumStr(relay_number) + ": ";

                if( value == 0xFFFF )
                {
                    pi.value   = 0.0;
                    pi.quality = InvalidQuality;
                    results    += "Invalid Data";
                }
                else
                {
                    pi.value   = value * multiplier;
                    pi.quality = NormalQuality;
                    results    += CtiNumStr(pi.value);
                }

                pi.description = "Last Interval kW Relay " + CtiNumStr(relay_number);

                int point_offset = PointOffset_LastIntervalBase + relay_number - 1;

                insertPointDataReport(DemandAccumulatorPointType, point_offset, ReturnMsg,
                                      pi, "LastIntervalkWRelay" + CtiNumStr(relay_number));
            }
        }

        ReturnMsg->setResultString(results);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


INT LCR3102::decodeGetValueRuntime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NOTNORMAL;

    return status;
}


INT LCR3102::decodeGetValueShedtime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NOTNORMAL;

    return status;
}


INT LCR3102::decodeGetValuePropCount( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NOTNORMAL;

    DSTRUCT      *DSt       = &InMessage->Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);

        point_info pi;

        pi.value       = DSt->Message[0];
        pi.quality     = NormalQuality;
        pi.freeze_bit  = false;
        pi.description = "Propagation Counter";

        insertPointDataReport(AnalogPointType, PointOffset_PropCount, ReturnMsg, pi, "PropCount");

        ReturnMsg->setResultString(getName() + " / Propagation Counter: " + CtiNumStr(pi.value));

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}


LCR3102::CommandSet LCR3102::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(Emetcon::GetValue_IntervalLast, Emetcon::IO_Function_Read, FuncRead_LastIntervalPos, FuncRead_LastIntervalLen));
    cs.insert(CommandStore(Emetcon::GetValue_Runtime,      Emetcon::IO_Function_Read, FuncRead_RuntimePos,      FuncRead_RuntimeLen));
    cs.insert(CommandStore(Emetcon::GetValue_Shedtime,     Emetcon::IO_Function_Read, FuncRead_ShedtimePos,     FuncRead_ShedtimeLen));
    cs.insert(CommandStore(Emetcon::GetValue_PropCount,    Emetcon::IO_Function_Read, FuncRead_PropCountPos,    FuncRead_PropCountLen));

    return cs;
}


INT LCR3102::executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT nRet = NoMethod;

    int  function = -1;
    bool found    = false;

    if(parse.getFlags() & CMD_FLAG_GV_DEMAND)
    {
        function = Emetcon::GetValue_IntervalLast;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.getFlags() & CMD_FLAG_GV_PROPCOUNT)
    {
        function = Emetcon::GetValue_PropCount;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.getFlags() & CMD_FLAG_GV_RUNTIME)
    {
        function = Emetcon::GetValue_PropCount;
        found = getOperation(function, OutMessage->Buffer.BSt);

        int load = parseLoadValue(parse);
        int previous = parsePreviousValue(parse);

        if( load == -1 || previous == -1)
        {
            nRet = BADPARAM;
        }
        else
        {
            // use load and previous to calculate the actual function number
            OutMessage->Buffer.BSt.Function += ((previous / 12) - 1) * 4 + (load - 1);
    
            // if previous is 36 we only get back 4 bytes - not 13
            if(previous == 36)
            {
                OutMessage->Buffer.BSt.Length = 4;
            }
    
            // more....
            
        }
    }
    else if(parse.getFlags() & CMD_FLAG_GV_SHEDTIME)
    {
        function = Emetcon::GetValue_PropCount;
        found = getOperation(function, OutMessage->Buffer.BSt);

        int load = parseLoadValue(parse);
        int previous = parsePreviousValue(parse);

        if( load == -1 || previous == -1)
        {
            nRet = BADPARAM;
        }
        else
        {
            // use load and previous to calculate the actual function number
            OutMessage->Buffer.BSt.Function += ((previous / 12) - 1) * 4 + (load - 1);

            // if previous is 36 we only get back 4 bytes - not 13
            if(previous == 36)
            {
                OutMessage->Buffer.BSt.Length = 4;
            }

            // more....

        }
    }

    if(nRet != BADPARAM)
    {
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
            OutMessage->Sequence  = function;         // Helps us figure it out later!
            OutMessage->Retry     = 2;
    
            OutMessage->Request.RouteID   = getRouteID();
            strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    
            nRet = NoError;
        }
    }

    return nRet;
}


bool LCR3102::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
   bool found = false;

   if(_commandStore.empty())
   {
      initCommandStore();
   }

   CommandSet::const_iterator itr = _commandStore.find(CommandStore(cmd));

   if( itr != _commandStore.end() )
   {
      bst.Function  = itr->function;
      bst.Length    = itr->length;
      bst.IO        = itr->io;

      found = true;
   }

   return found;
}


int LCR3102::parseLoadValue(CtiCommandParser &parse)
{
    int load = -1;      // signifies BADPARAM

    if(parse.isKeyValid("load"))
    {
        load = parse.getiValue("load");
    }
    else
    {
        load = 1;       // default if none given
    }

    return (load >= 1 && load <= 4) ? load : -1;
}


int LCR3102::parsePreviousValue(CtiCommandParser &parse)
{
    int previous = -1;  // signifies BADPARAM

    if(parse.isKeyValid("previous_hours"))
    {
        previous = parse.getiValue("previous_hours");
    }
    else
    {
        previous = 12;  // default if none given
    }

    return (previous >= 1 && previous <= 36) ? previous : -1;
}




}       // namespace Device
}       // namespace Cti

