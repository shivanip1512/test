/*
*   Copyright (c) 2009 Cooper Power Systems EAS. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "dev_lcr3102.h"


namespace Cti       {
namespace Devices    {


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

    bool broadcast = false;
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
        case ScanRequest:
        {
            nRet = executeScan( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case GetConfigRequest:
        {
            nRet = executeGetConfig( pReq, parse, OutMessage, vgList, retList, tmpOutList );
            break;
        }
        case PutConfigRequest:
        {
            nRet = executePutConfig( pReq, parse, OutMessage, vgList, retList, tmpOutList );
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

        nRet = executeOnDLCRoute(pReq, parse, tmpOutList, vgList, retList, outList, broadcast);
    }

    return nRet;
}


INT LCR3102::ErrorDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList, bool &overrideExpectMore )
{
    INT retCode = NOTNORMAL;

    if( InMessage->Sequence == Emetcon::Scan_Integrity )
    {
        resetScanFlag(ScanRateIntegrity);
    }

    return retCode;
}


INT LCR3102::ResultDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NOTNORMAL;

    switch(InMessage->Sequence)
    {
        case Emetcon::Scan_Integrity:
        case Emetcon::GetValue_IntervalLast:
        {
            status = decodeGetValueIntervalLast( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case Emetcon::GetValue_Runtime:
        case Emetcon::GetValue_Shedtime:
        {
            status = decodeGetValueHistoricalTime( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case Emetcon::GetValue_ControlTime:
        {
            status = decodeGetValueControlTime( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case Emetcon::GetValue_PropCount:
        {
            status = decodeGetValuePropCount( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case Emetcon::PutConfig_Raw:
        {
            status = decodePutConfig( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case Emetcon::GetConfig_Raw:
        {
            status = decodeGetConfig( InMessage, TimeNow, vgList, retList, outList );
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

        // The relay_info is defined as M2M2M1M1R4R3R2R1 where
        // Mx is the multiplier (00 = kw, 01 = 1/10, 10=1/100, 11 = 1/1000) and
        // Rx is the relay number, of which 2 can be set
        int   ct_number     = 1;
        int   message_index = 1;
        int   relay_number  = 1;
        UCHAR relay_info    = DSt->Message[0];
        UCHAR multiplier_specifier;

        for( UCHAR relay_mask = 0x01; relay_mask < 0x10 && message_index < 5; relay_mask <<= 1, relay_number++ )
        {
            if( relay_info & relay_mask )
            {
                if( ct_number == 1 )
                {
                    message_index = 1;
                    multiplier_specifier = (relay_info >> 4) & 0x03;
                }
                else // ct_number == 2
                {
                    message_index = 3;
                    multiplier_specifier = (relay_info >> 6) & 0x03;
                }

                int    value = (DSt->Message[message_index] << 8) | DSt->Message[message_index + 1];
                double multiplier = 1 / pow((double)10, (double)multiplier_specifier);

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
                    results    += CtiNumStr(pi.value, multiplier_specifier);
                }

                pi.description = "Last Interval kW Relay " + CtiNumStr(relay_number);

                int point_offset = PointOffset_LastIntervalBase + relay_number - 1;

                insertPointDataReport(DemandAccumulatorPointType, point_offset, ReturnMsg,
                                      pi, "Last Interval kW Relay " + CtiNumStr(relay_number));
                ct_number++;
            }
        }

        ReturnMsg->setResultString(results);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    resetScanFlag(ScanRateIntegrity);

    return status;
}

//Decodes the getvalue shedtime/runtime read. All points are generated with a end of interval timestamp.
INT LCR3102::decodeGetValueHistoricalTime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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

        int relay   = 0; // Relay is 1-4 here
        int readNum = 0; // Read 1-3, 1 = hr 0-16, ect..
        int function = InMessage->Return.ProtocolInfo.Emetcon.Function;
        int point_base = 0;
        string identifier;
        const int relay_count = 4;

        if( InMessage->Sequence == Emetcon::GetValue_Shedtime )
        {
            point_base = PointOffset_ShedtimeBase;
            function  -= FuncRead_ShedtimePos; // function is now 0-11
            identifier = "Shedtime";
        }
        else if( InMessage->Sequence == Emetcon::GetValue_Runtime )
        {
            point_base = PointOffset_RuntimeBase;
            function  -= FuncRead_RuntimePos; // function is now 0-11
            identifier = "Runtime";
        }
        else
        {
            return NOTNORMAL;
        }

        readNum   = function / 4 + 1;

        //There are 4 relays
        for( int i = 0; i < relay_count; i++ )
        {
            // With reads for each relay seperated by 4, this checks for a different relay each time
            if( (function-i) % relay_count == 0 )
            {
                relay = i + 1;
                break;
            }
        }

        if( relay == 0 )
        {
            return ErrorInvalidData;
        }

        //Flags = "? ? ? H R4 R3 R2 R1" in binary, H is all we really need.
        unsigned char flags = DSt->Message[0];
        bool responseInSecondHalfHour = flags & 0x10;
        CtiTime firstMessageTime;

        //This time eventually has to be on the hour, remove the seconds
        firstMessageTime.addSeconds(-1*firstMessageTime.second());

        // If we are less than 15 after the hour, and the message indicates
        // it is 30-60 minutes after the hour, we assume WE are ahead an hour.
        if( firstMessageTime.minute() < 15 && responseInSecondHalfHour )
        {
            firstMessageTime.addMinutes(-60);
        }

        // If we are 45 minutes or more before the hour, and the message indicates
        // it is 0-30 minutes after the hour, we assume THEY are ahead an hour.
        if( firstMessageTime.minute() > 45 && !responseInSecondHalfHour )
        {
            firstMessageTime.addMinutes(60);
        }

        firstMessageTime.addMinutes(-1*firstMessageTime.minute()); // align with hour
         //Align with the read, each function drops off 16 hours. Why doesnt CtiTime have addHours??
        firstMessageTime.addMinutes(-1*60*16*(readNum-1));

        //8 bits per byte, 6 bits per time fame
        int numberOfTimesReturned = (DSt->Length-1) * 8 / 6;
        if( readNum == 3  )
        {
            numberOfTimesReturned = std::min(4, numberOfTimesReturned);
        }

        CtiTime pointTime = firstMessageTime;
        point_info pi;

        string results = getName() + " / Hourly " + identifier;
        ReturnMsg->setResultString(results);

        for( int i = 0; i < numberOfTimesReturned; i++ )
        {
            pi = getSixBitValueFromBuffer(DSt->Message + 1, i, std::min(DSt->Length - 1, 36 - 1));

            int point_offset = point_base + relay - 1;

            insertPointDataReport(AnalogPointType, point_offset, ReturnMsg,
                                  pi, identifier + " Load " + CtiNumStr(relay), pointTime);

            pointTime.addMinutes(-1*60); // subtract an hour for each value
        }

        decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);
        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

INT LCR3102::decodeGetValueControlTime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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

        int relay         = DSt->Message[0]; // Relay is 1-4 here
        int point_base    = PointOffset_ControlTimeBase;
        string identifier = "Control Time Remaining";

        const double halfSecondsPerSecond = 2.0;

        point_info pi;

        // The LCR 3102 returns the control time remaining in half seconds: we want to return seconds.
        int half_seconds = (DSt->Message[1] << 24) | (DSt->Message[2] << 16) | (DSt->Message[3] << 8) | (DSt->Message[4]);

        pi.value       = half_seconds / halfSecondsPerSecond; // Convert to seconds. Is this the unit we want for this?
        pi.quality     = NormalQuality;
        pi.freeze_bit  = false;
        pi.description = "Control Time Remaining Relay " + CtiNumStr(relay);

        string results = getName() + " / " + identifier;
        ReturnMsg->setResultString(results);

        int point_offset = point_base + relay - 1;

        insertPointDataReport(AnalogPointType, point_offset, ReturnMsg, pi, identifier + " Relay " + CtiNumStr(relay));

        decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);
        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

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

INT LCR3102::decodePutConfig( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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

        string results;

        switch( InMessage->Sequence )
        {
            case Emetcon::PutConfig_Raw:
            {
                results = getName() + " / Raw bytes sent";
                break;
            }
            default:
            {

            }
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( results );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

INT LCR3102::decodeGetConfig( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NOTNORMAL;

    DSTRUCT      *DSt       = &InMessage->Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    CtiCommandParser parse(InMessage->Return.CommandStr);

    if(!(status = decodeCheckErrorReturn(InMessage, retList, outList)))
    {
        // No error occured, we must do a real decode!

        if((ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage->Return.CommandStr)) == NULL)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Could NOT allocate memory " << __FILE__ << " (" << __LINE__ << ") " << endl;

            return MEMORY;
        }

        string results;

        switch( InMessage->Sequence )
        {
            case Emetcon::GetConfig_Raw:
            {
                int rawloc = parse.getiValue("rawloc");

                int rawlen = parse.isKeyValid("rawlen")
                    ? std::min(parse.getiValue("rawlen"), 13)       // max 13 bytes...
                    : DSt->Length;

                if( parse.isKeyValid("rawfunc") )
                {
                    for( int i = 0; i < rawlen; i++ )
                    {
                        results += getName()
                                + " / FR " + CtiNumStr(rawloc).xhex().zpad(2)
                                + " byte " + CtiNumStr(i).zpad(2)
                                + " : "    + CtiNumStr((int)DSt->Message[i]).xhex().zpad(2)
                                + "\n";
                    }
                }
                else
                {
                    for( int i = 0; i < rawlen; i++ )
                    {
                        results += getName()
                                + " / byte " + CtiNumStr(rawloc + i).xhex().zpad(2)
                                + " : "      + CtiNumStr((int)DSt->Message[i]).xhex().zpad(2)
                                + "\n";
                    }
                }
                break;
            }
            default:
            {

            }
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( results );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

LCR3102::CommandSet LCR3102::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(Emetcon::Scan_Integrity,        Emetcon::IO_Function_Read, FuncRead_LastIntervalPos, FuncRead_LastIntervalLen));
    cs.insert(CommandStore(Emetcon::GetValue_IntervalLast, Emetcon::IO_Function_Read, FuncRead_LastIntervalPos, FuncRead_LastIntervalLen));
    cs.insert(CommandStore(Emetcon::GetValue_Runtime,      Emetcon::IO_Function_Read, FuncRead_RuntimePos,      FuncRead_RuntimeLen));
    cs.insert(CommandStore(Emetcon::GetValue_Shedtime,     Emetcon::IO_Function_Read, FuncRead_ShedtimePos,     FuncRead_ShedtimeLen));
    cs.insert(CommandStore(Emetcon::GetValue_PropCount,    Emetcon::IO_Function_Read, FuncRead_PropCountPos,    FuncRead_PropCountLen));
    cs.insert(CommandStore(Emetcon::GetValue_ControlTime,  Emetcon::IO_Function_Read, FuncRead_ControlTimePos,  FuncRead_ControlTimeLen));
    cs.insert(CommandStore(Emetcon::PutConfig_Raw,         Emetcon::IO_Write,         0,                        0));    // filled in later
    cs.insert(CommandStore(Emetcon::GetConfig_Raw,         Emetcon::IO_Read,          0,                        0));    // ...ditto

    return cs;
}

INT LCR3102::IntegrityScan(CtiRequestMsg *pReq,
                                CtiCommandParser &parse,
                                OUTMESS *&OutMessage,
                                list< CtiMessage* > &vgList,
                                list< CtiMessage* > &retList,
                                list< OUTMESS* > &outList,
                                INT ScanPriority)
{
    INT status = NORMAL;

    if(OutMessage != NULL)
    {
        if(getOperation(Emetcon::Scan_Integrity, OutMessage->Buffer.BSt))
        {
            // Load all the other stuff that is needed
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = Emetcon::Scan_Integrity;
            OutMessage->Retry     = 2;
            OutMessage->Request.RouteID   = getRouteID();
            OutMessage->Request.MacroOffset = 0;

            // Tell the porter side to complete the assembly of the message.
            OutMessage->Request.BuildIt = TRUE;
            strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
            outList.push_back(OutMessage);
            OutMessage = NULL;

            setScanFlag(ScanRateIntegrity, true);
        }
        else
        {
            delete OutMessage;
            OutMessage = NULL;

            status = NoMethod;

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Command lookup failed **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << " Device " << getName() << endl;
        }
    }

    return status;
}


INT LCR3102::executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT nRet = NoMethod;

    int  function = -1;
    bool found    = false;
    bool multiple_messages = false;

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
    else if(parse.getFlags() & CMD_FLAG_GV_CONTROLTIME)
    {
        function = Emetcon::GetValue_ControlTime;
        found = getOperation(function, OutMessage->Buffer.BSt);

        int load = parseLoadValue(parse);

        if( load == -1 )
        {
            nRet = BADPARAM;
        }
        else
        {
            OutMessage->Buffer.BSt.Function += (load - 1);
        }
    }
    else if(parse.getFlags() & CMD_FLAG_GV_RUNTIME || parse.getFlags() & CMD_FLAG_GV_SHEDTIME)
    {
        if(parse.getFlags() & CMD_FLAG_GV_RUNTIME)
        {
            function = Emetcon::GetValue_Runtime;
        }
        else
        {
            function = Emetcon::GetValue_Shedtime;
        }

        found = getOperation(function, OutMessage->Buffer.BSt);

        int load = parseLoadValue(parse);
        int previous = parsePreviousValue(parse);

        if( load == -1 || previous == -1)
        {
            nRet = BADPARAM;
        }
        else
        {
            // pack relay number into OutMessages Function
            OutMessage->Buffer.BSt.Function += (load - 1);

            // packed 6 bit data
            int total_bits = previous * 6;

                                   // round up
            int remaining_bytes = (total_bits + 7) / 8;
            int total_messages  = (remaining_bytes + 11) / 12;

            if(total_messages > 1)  multiple_messages = true;

            // for all but the last message - send a full 13 byte request - 12 data bytes and 1 Flags byte
            for(int i = 0; i < total_messages - 1; i++)
            {
                if( found )
                {
                    OutMessage->DeviceID  = getID();
                    OutMessage->TargetID  = getID();
                    OutMessage->Port      = getPortID();
                    OutMessage->Remote    = getAddress();
                    OutMessage->TimeOut   = 2;
                    OutMessage->Sequence  = function;         // Helps us figure it out later!
                    OutMessage->Retry     = 2;
                    OutMessage->Request.RouteID   = getRouteID();

                    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
                    outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
                    incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());

                    // deduct out the data bytes we should get from the above message from our total
                    remaining_bytes -= 12;

                    // go to next function to get next set of data bytes
                    OutMessage->Buffer.BSt.Function += 4;
                }
            }

            // setup remaining message for the leftover bytes we need - don't forget the Flags byte
            OutMessage->Buffer.BSt.Length = remaining_bytes + 1;
        }
    }


    if(!found)
    {
        if( nRet == NORMAL )
        {
            nRet = NoMethod;
        }
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
        if(multiple_messages)
        {
            incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());
        }

        nRet = NoError;
    }

    return nRet;
}

INT LCR3102::executeScan(CtiRequestMsg                  *pReq,
                              CtiCommandParser               &parse,
                              OUTMESS                        *&OutMessage,
                              list< CtiMessage* >      &vgList,
                              list< CtiMessage* >      &retList,
                              list< OUTMESS* >         &outList)
{
    bool found = false;
    INT  nRet  = NoError;
    string tester;

    INT            function;
    unsigned short stub;

    // The following switch fills in the BSTRUCT's Function, Length, and IO parameters.
    switch(parse.getiValue("scantype"))
    {
        case ScanRateIntegrity:
        {
            function = Emetcon::Scan_Integrity;
            found = getOperation(Emetcon::Scan_Integrity, OutMessage->Buffer.BSt);
            break;
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
        OutMessage->Remote    = getAddress();
        OutMessage->TimeOut   = 2;
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Retry     = 2;
        OutMessage->Request.RouteID   = getRouteID();

        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}

INT LCR3102::executeGetConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT nRet = NoMethod;

    int  function = -1;
    bool found    = false;

    if(parse.isKeyValid("rawloc"))
    {
        function = Emetcon::GetConfig_Raw;
        found = getOperation(function, OutMessage->Buffer.BSt);

        OutMessage->Buffer.BSt.Function = parse.getiValue("rawloc");
        if( parse.isKeyValid("rawfunc") )
        {
            OutMessage->Buffer.BSt.IO = Emetcon::IO_Function_Read;
        }
        OutMessage->Buffer.BSt.Length = std::min(parse.getiValue("rawlen", 13), 13);    //  default (and maximum) is 13 bytes
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
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = NoError;
    }

    return nRet;
}

INT LCR3102::executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT nRet = NoMethod;

    int  function = -1;
    bool found    = false;

    if(parse.isKeyValid("rawloc"))
    {
        function = Emetcon::PutConfig_Raw;
        found = getOperation(function, OutMessage->Buffer.BSt);

        OutMessage->Buffer.BSt.Function = parse.getiValue("rawloc");
        if( parse.isKeyValid("rawfunc") )
        {
            OutMessage->Buffer.BSt.IO = Emetcon::IO_Function_Write;
        }

        string rawData = parse.getsValue("rawdata");

        OutMessage->Buffer.BSt.Length = std::min( rawData.length(), 15u );  //  default (and maximum) is 15 bytes

        rawData.copy( (char *)OutMessage->Buffer.BSt.Message, OutMessage->Buffer.BSt.Length );
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
        OutMessage->Sequence  = function;         // Helps us figure it out later!
        OutMessage->Retry     = 2;

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = NoError;
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

// Finds the "previous hours" requested. Handles special values.
// 12, 24, and 36 are the default values in commander and are translated as:
// "Do 1 full read, do 2 full reads, and do 3 full reads"
int LCR3102::parsePreviousValue(CtiCommandParser &parse)
{
    int previous = -1;  // signifies BADPARAM

    if(parse.isKeyValid("previous_hours"))
    {
        previous = parse.getiValue("previous_hours");
    }
    else
    {
        previous = 16;  // default if none given
    }

    if( previous == 12 )
    {
        previous = 16;
    }
    else if( previous == 24 )
    {
        previous = 32;
    }

    return (previous >= 1 && previous <= 36) ? previous : -1;
}

// Parses the buffer as having 6 bit values. 0x3F is considered an error
// Returns the valuePosition'th 6-bit value from the buffer
// All position values are 0 based!
CtiDeviceSingle::point_info LCR3102::getSixBitValueFromBuffer(unsigned char buffer[], unsigned int valuePosition, unsigned int bufferSize)
{
    point_info retVal;
    retVal.freeze_bit = false;
    retVal.quality    = InvalidQuality;
    retVal.value      = 0x3F;

    const int bitsPerByte = 8;
    const int bitsPerValue = 6;

    int startByte = valuePosition * bitsPerValue / bitsPerByte;
    int bitsInStartByte  = std::min<int>(bitsPerByte - (valuePosition * bitsPerValue % bitsPerByte), bitsPerValue);
    int bitsInSecondByte = bitsPerValue - bitsInStartByte;

    if( (startByte + (bitsInSecondByte ? 1 : 0)) < bufferSize )
    {
        unsigned char rawData;
        if( bitsInStartByte == bitsPerValue )
        {
            rawData = (buffer[startByte] >> bitsPerByte - bitsPerValue - (valuePosition * bitsPerValue % bitsPerByte));
        }
        else
        {
            rawData = (buffer[startByte] << bitsInSecondByte);
        }

        if( bitsInSecondByte > 0 )
        {
            rawData |= (buffer[startByte+1] >> (bitsPerByte-bitsInSecondByte));
        }

        unsigned int bitMask = (1 << bitsPerValue) - 1;
        retVal.value = rawData & bitMask;
    }

    // 0x3F is both the default value and the error code for 6 bit values.
    // It is equivalent to 0xFF for 8 bit values
    if( retVal.value != 0x3F )
    {
        retVal.quality = NormalQuality;
    }

    return retVal;
}

// Returns only the 22 bits of emetcon address.
// This is called by executeOnDLCRoute so cannot be renamed
LONG LCR3102::getAddress() const
{
    return Inherited::getAddress() & 0x003FFFFF;
}


}       // namespace Devices
}       // namespace Cti

