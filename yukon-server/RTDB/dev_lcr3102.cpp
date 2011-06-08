#include "yukon.h"

#include "dev_lcr3102.h"
#include "ctidate.h"
#include "date_utility.h"
#include "ctitokenizer.h"

using std::string;
using std::endl;
using std::list;
using Cti::Protocols::EmetconProtocol;
using namespace Cti::Devices::Commands;

namespace Cti {
namespace Devices {

const Lcr3102Device::CommandSet  Lcr3102Device::_commandStore = Lcr3102Device::initCommandStore();


Lcr3102Device::Lcr3102Device( )
{

}


Lcr3102Device::Lcr3102Device( const Lcr3102Device &aRef )
{
    *this = aRef;
}


Lcr3102Device& Lcr3102Device::operator=( const Lcr3102Device &aRef )
{
    if(this != &aRef)
    {
        Inherited::operator=(aRef);
//????   Is this needed?
//        CtiLockGuard<CtiMutex> guard(_classMutex);            // Protect this device!
    }
    return *this;
}


INT Lcr3102Device::ErrorDecode( const INMESS &InMessage, const CtiTime TimeNow, std::list< CtiMessage* > &retList )
{
    INT retCode = NOTNORMAL;

    if( InMessage.Sequence == EmetconProtocol::Scan_Integrity )
    {
        resetScanFlag(ScanRateIntegrity);
    }

    return retCode;
}


INT Lcr3102Device::ResultDecode( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NOTNORMAL;

    switch(InMessage->Sequence)
    {
        case EmetconProtocol::Scan_Integrity:
        case EmetconProtocol::GetValue_IntervalLast:
        {
            status = decodeGetValueIntervalLast( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::GetValue_Runtime:
        case EmetconProtocol::GetValue_Shedtime:
        {
            status = decodeGetValueHistoricalTime( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::GetValue_Temperature:
        {
            status = decodeGetValueTemperature( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::GetValue_TransmitPower:
        {
            status = decodeGetValueTransmitPower( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::GetValue_ControlTime:
        {
            status = decodeGetValueControlTime( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::GetValue_XfmrHistoricalCT:
        {
            status = decodeGetValueXfmrHistoricalRuntime( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::GetValue_PropCount:
        {
            status = decodeGetValuePropCount( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::GetValue_DutyCycle:
        {
            status = decodeGetValueDutyCycle( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::PutConfig_Raw:
        {
            status = decodePutConfig( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::GetConfig_Softspec:
        {
            status = decodeGetConfigSoftspec( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::GetConfig_Time:
        {
            status = decodeGetConfigTime( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::GetConfig_Addressing:
        {
            status = decodeGetConfigAddressing( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        case EmetconProtocol::GetConfig_Raw:
        {
            status = decodeGetConfigRaw( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        default:
        {
            status = Inherited::ResultDecode( InMessage, TimeNow, vgList, retList, outList);

            if(status != NORMAL)
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << " IM->Sequence = " << InMessage->Sequence << " " << getName() << endl;
            }

            break;
        }
    }

    return status;
}

INT Lcr3102Device::decodeGetValueTemperature( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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

        int txTemp, boxTemp;

        decodeMessageTemperature(DSt->Message, txTemp, boxTemp);

        string results = getName() + " / TX Temperature: " + CtiNumStr(((double)txTemp)/100.0, 1) + " degrees Centigrade\n"
                       + getName() + " / Ambient Box Temperature: " + CtiNumStr(((double)boxTemp)/100.0, 1) + " degrees Centigrade";

        ReturnMsg->setResultString(results);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

INT Lcr3102Device::decodeGetValueTransmitPower( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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

        int transmitPower;

        decodeMessageTransmitPower(DSt->Message, transmitPower);

        string results = getName() + " / Current Transmit Power: " + CtiNumStr(transmitPower) + " percent";

        ReturnMsg->setResultString(results);

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

INT Lcr3102Device::decodeGetValueDutyCycle(INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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
        int dutyCycle, currentTransformer;
        decodeMessageDutyCycle(DSt->Message, dutyCycle, currentTransformer);

        /* A nice little workaround. Firmware isn't working as intended right now and isn't correctly
           giving back the Current Transformer number from the message in the function above. This
           statement allows us to get the correct CT from the function we sent originally.
           In the future we will probably still want to rely on the message decoding to get the CT
           back since the message from the LCR SHOULD return the correct CT... but for now this is a
           reasonable fix.                                                                              */
        currentTransformer = InMessage->Return.ProtocolInfo.Emetcon.Function - FuncRead_DutyCyclePos + 1;

        int point_base = PointOffset_DutyCycleBase;

        if(currentTransformer == 1 || currentTransformer == 2)
        {
            pi.value = double(dutyCycle) / 60.0;
            pi.quality = NormalQuality;
            pi.description = "Duty Cycle CT " + CtiNumStr(currentTransformer);

            int point_offset = point_base + currentTransformer - 1;

            insertPointDataReport(AnalogPointType, point_offset, ReturnMsg, pi, pi.description);

            decrementGroupMessageCount(InMessage->Return.UserID, (long)InMessage->Return.Connection);
            retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Value " << currentTransformer << " is not a valid current transformer number" << endl;

            return BADPARAM;
        }
    }

    return status;
}

INT Lcr3102Device::decodeGetValueIntervalLast( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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
INT Lcr3102Device::decodeGetValueHistoricalTime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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

        if( InMessage->Sequence == EmetconProtocol::GetValue_Shedtime )
        {
            point_base = PointOffset_ShedtimeBase;
            function  -= FuncRead_ShedtimePos; // function is now 0-11
            identifier = "Shedtime";
        }
        else if( InMessage->Sequence == EmetconProtocol::GetValue_Runtime )
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

INT Lcr3102Device::decodeGetValueXfmrHistoricalRuntime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT status = NOTNORMAL;

    DSTRUCT      *DSt       = &InMessage->Buffer.DSt;
    BSTRUCT       BSt;
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

        // Assuming this is not a point, let's just dump the data to the screen for now.

        std::vector<point_info> runtimeHours;

        decodeMessageXfmrHistoricalRuntime(InMessage->Buffer.DSt, runtimeHours);

        int currentTransformer = InMessage->Return.ProtocolInfo.Emetcon.Function - FuncRead_XfmrHistoricalCT1Pos + 1;

        if(currentTransformer == 1 || currentTransformer == 2)
        {
            string results;
            int counter = 1;

            for each(const point_info &pi in runtimeHours)
            {
                int runtimeMins = pi.value;

                results += getName() + " / Historical Runtime CT " + CtiNumStr(currentTransformer) + ": Hour -"
                         + CtiNumStr(counter) + ": " + CtiNumStr((double)runtimeMins / 60.0, 1) + " percent";

                if( runtimeMins > 0x3c && runtimeMins < 0x3f)
                {
                    // This is strange. The 'invalid data' return should give us 0x3F, so this returned a number higher
                    // than the 60 minutes in an hour but less than the 0x3F case.
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << "The data returned was outside the valid range: " << runtimeMins << endl;
                    }

                    results += " - Data outside valid range";

                }
                else if( runtimeMins == 0x3f)
                {
                    // Invalid data
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << "Invalid data returned: " << runtimeMins << endl;
                    }

                    results += " - Invalid data";
                }

                results += "\n";
                counter++;
            }

            ReturnMsg->setResultString(results);
            retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << "Value" << currentTransformer << " is not a valid CT identifier" << endl;

            return BADPARAM;
        }
    }

    return status;
}

INT Lcr3102Device::decodeGetValueControlTime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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

        int relay = DSt->Message[0]; // Relay is 1-4 here

        if( relay > 0 && relay < 5 )
        {
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
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " Relay number " << relay << " is invalid. Point has not been inserted." << endl;

            return BADRANGE;
        }
    }

    return status;
}

INT Lcr3102Device::decodeGetValuePropCount( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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

INT Lcr3102Device::decodePutConfig( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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
            case EmetconProtocol::PutConfig_Raw:
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

INT Lcr3102Device::decodeGetConfigRaw( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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

        if(DSt->Message[0] == 0xff)
        {
            // The device responded with an error device...possibly. Alert the user that this may be the case.
            int sspec = DSt->Message[1];
            int rev   = DSt->Message[2];

            string errorMessage;

            errorMessage += getName()
                         + " received a possible error report message from the device: "
                         + "\n"
                         + "\tSoftware Spec: " + CtiNumStr(sspec)
                         + "\n"
                         + "\tRevision: " + CtiNumStr(((double)rev) / 10.0, 1)
                         + "\n";

            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " " << errorMessage << endl;

            results += errorMessage + "\n";
        }

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

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( results );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
            }

    return status;
}

INT Lcr3102Device::decodeGetConfigSoftspec( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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

        int sspec, rev, serial, spid, geo;

        decodeMessageSoftspec(DSt->Message, sspec, rev, serial, spid, geo);

        results = getName() + " / Software Specification " + CtiNumStr(sspec)
                + " rev " + CtiNumStr(((double)rev) / 10.0, 1) + "\n"
                + getName() + " / Serial Number: " + CtiNumStr(serial) + "\n"
                + getName() + " / SPID Address: " + CtiNumStr(spid) + "\n"
                + getName() + " / Geo Address: " + CtiNumStr(geo);

        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SSpec,           (long)sspec);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SSpecRevision,   (long)rev);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SerialAddress,   (long)serial);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_Spid,            (long)spid);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_GeoAddress,      (long)geo);

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( results );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
        }

    return status;
}

INT Lcr3102Device::decodeGetConfigAddressing( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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
        int function = InMessage->Return.ProtocolInfo.Emetcon.Function;

        if(function == DataRead_AddressInfoPos)
        {
            int programAddressRelay1, programAddressRelay2, programAddressRelay3, programAddressRelay4,
                splinterAddressRelay1, splinterAddressRelay2, splinterAddressRelay3, splinterAddressRelay4;

            decodeMessageAddress(DSt->Message,
                                 programAddressRelay1, programAddressRelay2, programAddressRelay3, programAddressRelay4,
                                 splinterAddressRelay1, splinterAddressRelay2, splinterAddressRelay3, splinterAddressRelay4);

            results = getName() + " / Program Address Relay 1:  " + CtiNumStr(programAddressRelay1 )  + "\n"
                    + getName() + " / Program Address Relay 2:  " + CtiNumStr(programAddressRelay2 )  + "\n"
                    + getName() + " / Program Address Relay 3:  " + CtiNumStr(programAddressRelay3 )  + "\n"
                    + getName() + " / Program Address Relay 4:  " + CtiNumStr(programAddressRelay4 )  + "\n"
                    + getName() + " / Splinter Address Relay 1: " + CtiNumStr(splinterAddressRelay1) + "\n"
                    + getName() + " / Splinter Address Relay 2: " + CtiNumStr(splinterAddressRelay2) + "\n"
                    + getName() + " / Splinter Address Relay 3: " + CtiNumStr(splinterAddressRelay3) + "\n"
                    + getName() + " / Splinter Address Relay 4: " + CtiNumStr(splinterAddressRelay4);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay1,    (long)programAddressRelay1);
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay2,    (long)programAddressRelay2);
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay3,    (long)programAddressRelay3);
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay4,    (long)programAddressRelay4);
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay1,   (long)splinterAddressRelay1);
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay2,   (long)splinterAddressRelay2);
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay3,   (long)splinterAddressRelay3);
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay4,   (long)splinterAddressRelay4);
        }
        else if(function == DataRead_SubstationDataPos)
        {
            int substation, feeder, zipcode, uda;

            decodeMessageSubstation(DSt->Message, substation, feeder, zipcode, uda);

            results = getName() + " / Substation Address: "     + CtiNumStr(substation) + "\n"
                    + getName() + " / Feeder Address: "         + CtiNumStr(feeder)     + "\n"
                    + getName() + " / Zip Code: "               + CtiNumStr(zipcode)    + "\n"
                    + getName() + " / User Defined Address: "   + CtiNumStr(uda);

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_Substation,  (long)substation);
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_Feeder,      (long)feeder);
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_ZipCode,     (long)zipcode);
            setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_Uda,         (long)uda);
        }
        else
        {
            // Somehow we didn't get back either of the two designated functions. Bad!
            return NOTNORMAL;
        }

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( results );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

INT Lcr3102Device::decodeGetConfigTime( INMESS *InMessage, CtiTime &TimeNow, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
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
        CtiTime time;

        decodeMessageTime(DSt->Message, time);

        results = getName() + " / Current Time: " + time.asString();

        ReturnMsg->setUserMessageId(InMessage->Return.UserID);
        ReturnMsg->setResultString( results );

        retMsgHandler( InMessage->Return.CommandStr, status, ReturnMsg, vgList, retList );
    }

    return status;
}

void Lcr3102Device::decodeMessageXfmrHistoricalRuntime( DSTRUCT DSt, std::vector<point_info> &runtimeHours)
{
    // The runtimeHours vector will contain the CT historical runtime data in order from the most
    // distant hour to the most recent.

    point_info pi;
    const int NumHours = 8;
    const int bufferSize = std::min(DSt.Length - 1, 36 - 1);

    for(int i = 0; i < NumHours; i++)
    {
        pi = getSixBitValueFromBuffer(DSt.Message, i, bufferSize);

        runtimeHours.push_back(pi);
    }
}

void Lcr3102Device::decodeMessageSoftspec( BYTE Message[], int &sspec, int &rev, int &serial, int &spid, int &geo )
{
    sspec  = Message[0] | (Message[4] << 8);
    rev    = Message[1];
    serial = (Message[5] << 24) | (Message[6] << 16) | (Message[7] << 8) | Message[8];
    spid   = (Message[9] << 8) | Message[10];
    geo    = (Message[11] << 8) | Message[12];
}

void Lcr3102Device::decodeMessageAddress( BYTE Message[], int &prgAddr1, int &prgAddr2, int &prgAddr3, int &prgAddr4, int &splAddr1, int &splAddr2, int &splAddr3, int &splAddr4 )
{
    prgAddr1 = Message[0];
    prgAddr2 = Message[1];
    prgAddr3 = Message[2];
    prgAddr4 = Message[3];
    splAddr1 = Message[4];
    splAddr2 = Message[5];
    splAddr3 = Message[6];
    splAddr4 = Message[7];
}

void Lcr3102Device::decodeMessageSubstation( BYTE Message[], int &substation, int &feeder, int &zip, int &uda )
{
    substation = (Message[0] << 8) | Message[1]; // Substation Address
    feeder = (Message[2] << 8) | Message[3]; // Feeder Address
    zip = (Message[4] << 16) | (Message[5] << 8) | Message[6]; // Zip Code
    uda = (Message[7] << 8) | Message[8]; // User Defined Address
}

void Lcr3102Device::decodeMessageTime( BYTE Message[], CtiTime &time )
{
    int utcSeconds = (Message[0] << 24) | (Message[1] << 16) | (Message[2] << 8) | Message[3];

    time = CtiTime::fromLocalSeconds(utcSeconds);
}

void Lcr3102Device::decodeMessageTransmitPower( BYTE Message[], int &transmitPower )
{
    transmitPower = Message[0];
}

void Lcr3102Device::decodeMessageTemperature( BYTE Message[], int &txTemp, int &boxTemp )
{
    txTemp = (Message[0] << 8) | Message[1]; // TX Temp
    boxTemp = (Message[2] << 8) | Message[3]; // Box Temp
}

void Lcr3102Device::decodeMessageDutyCycle( BYTE Message[], int &dutyCycle, int &transformer )
{
    transformer = Message[0];
    dutyCycle   = Message[1];
}

Lcr3102Device::CommandSet Lcr3102Device::initCommandStore()
{
    CommandSet cs;

    cs.insert(CommandStore(EmetconProtocol::Scan_Integrity,             EmetconProtocol::IO_Function_Read, FuncRead_LastIntervalPos,      FuncRead_LastIntervalLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_IntervalLast,      EmetconProtocol::IO_Function_Read, FuncRead_LastIntervalPos,      FuncRead_LastIntervalLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_Runtime,           EmetconProtocol::IO_Function_Read, FuncRead_RuntimePos,           FuncRead_RuntimeLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_Shedtime,          EmetconProtocol::IO_Function_Read, FuncRead_ShedtimePos,          FuncRead_ShedtimeLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_PropCount,         EmetconProtocol::IO_Function_Read, FuncRead_PropCountPos,         FuncRead_PropCountLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_ControlTime,       EmetconProtocol::IO_Function_Read, FuncRead_ControlTimePos,       FuncRead_ControlTimeLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_XfmrHistoricalCT,  EmetconProtocol::IO_Function_Read, FuncRead_XfmrHistoricalCT1Pos, FuncRead_XfmrHistoricalLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_DutyCycle,         EmetconProtocol::IO_Function_Read, FuncRead_DutyCyclePos,         FuncRead_DutyCycleLen));
    cs.insert(CommandStore(EmetconProtocol::PutConfig_Raw,              EmetconProtocol::IO_Write,         0,                             0));    // filled in later
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Raw,              EmetconProtocol::IO_Read,          0,                             0));    // ...ditto

    /****************************** Data Reads *****************************/
    cs.insert(CommandStore(EmetconProtocol::GetValue_TransmitPower,   EmetconProtocol::IO_Read, DataRead_TransmitPowerPos,  DataRead_TransmitPowerLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Time,           EmetconProtocol::IO_Read, DataRead_DeviceTimePos,     DataRead_DeviceTimeLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Softspec,       EmetconProtocol::IO_Read, DataRead_SoftspecPos,       DataRead_SoftspecLen));
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Addressing,     EmetconProtocol::IO_Read, DataRead_SubstationDataPos, DataRead_SubstationDataLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_Temperature,     EmetconProtocol::IO_Read, DataRead_TemperaturePos,    DataRead_TemperatureLen));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_Raw, EmetconProtocol::IO_Write, 0, 0));    // filled in later
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Raw, EmetconProtocol::IO_Read,  0, 0));    // ...ditto

    return cs;
}

INT Lcr3102Device::IntegrityScan(CtiRequestMsg *pReq,
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
        if(getOperation(EmetconProtocol::Scan_Integrity, OutMessage->Buffer.BSt))
        {
            // Load all the other stuff that is needed
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = EmetconProtocol::Scan_Integrity;
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


INT Lcr3102Device::executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT nRet = NoMethod;

    int  function = -1;
    bool found    = false;
    bool multiple_messages = false;

    if(parse.getFlags() & CMD_FLAG_GV_DEMAND)
    {
        function = EmetconProtocol::GetValue_IntervalLast;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.getFlags() & CMD_FLAG_GV_PROPCOUNT)
    {
        function = EmetconProtocol::GetValue_PropCount;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.getFlags() & CMD_FLAG_GV_CONTROLTIME)
    {
        function = EmetconProtocol::GetValue_ControlTime;
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
    else if(parse.getFlags() & CMD_FLAG_GV_TEMPERATURE)
    {
        function = EmetconProtocol::GetValue_Temperature;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.getFlags() & CMD_FLAG_GV_PFCOUNT)
    {
        function = EmetconProtocol::GetValue_TransmitPower;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    else if(parse.getFlags() & CMD_FLAG_GV_XFMR_HISTORICAL_RUNTIME)
    {
        function = EmetconProtocol::GetValue_XfmrHistoricalCT;
        found = getOperation(function, OutMessage->Buffer.BSt);
        int load = parseLoadValue(parse);

        if( load == -1 )
        {
            nRet = BADPARAM;
        }
        else
        {
            OutMessage->Buffer.BSt.Function += load - 1;
        }
    }
    else if(parse.getFlags() & CMD_FLAG_GV_DUTYCYCLE)
    {
        function = EmetconProtocol::GetValue_DutyCycle;
        found = getOperation(function, OutMessage->Buffer.BSt);

        int load = parseLoadValue(parse);

        if(load == -1)
        {
            nRet = BADPARAM;
        }
        else
        {
            OutMessage->Buffer.BSt.Function += (load - 1);
        }
    }
    else if(parse.getFlags() & CMD_FLAG_GV_TAMPER_INFO)
    {
        // Redo the parse string to be the expresscom string.
        string xcomRequest = "getvalue xcom tamper info serial " + CtiNumStr(getSerial());

        parse = CtiCommandParser(xcomRequest);
        parse.setValue("xc_serial", getSerial());

        DlcCommandSPtr tamperRead(new Lcr3102TamperReadCommand());

        found = tryExecuteCommand(*OutMessage, tamperRead);

        function = OutMessage->Sequence;
    }
    else if(parse.getFlags() & CMD_FLAG_GV_DR_SUMMARY)
    {
        string xcomRequest = "getvalue xcom dr summary serial " + CtiNumStr(getSerial());

        parse = CtiCommandParser(xcomRequest);
        parse.setValue("xc_serial", getSerial());

        DlcCommandSPtr tamperRead(new Lcr3102DemandResponseSummaryCommand());

        found = tryExecuteCommand(*OutMessage, tamperRead);

        function = OutMessage->Sequence;
    }
    else if(parse.getFlags() & CMD_FLAG_GV_HOURLY_LOG)
    {
        // Grab the info from the parser!
        CtiDate date = parseDateValue(parse.getsValue("hourly_log_date"));

        CtiTokenizer time_tokenizer(parse.getsValue("hourly_log_time"));
        int startHour = atoi(time_tokenizer(":").data());
        int startMin  = atoi(time_tokenizer(":").data());
        int startSec  = atoi(time_tokenizer(":").data());

        CtiTime executeTime = CtiTime(date, startHour, startMin, startSec);

        DlcCommandSPtr hourlyLogRead(new Lcr3102HourlyDataLogCommand(executeTime.seconds()));

        found = tryExecuteCommand(*OutMessage, hourlyLogRead);

        function = OutMessage->Sequence;
    }
    else if(parse.getFlags() & CMD_FLAG_GV_RUNTIME || parse.getFlags() & CMD_FLAG_GV_SHEDTIME)
    {
        if(parse.getFlags() & CMD_FLAG_GV_RUNTIME)
        {
            function = EmetconProtocol::GetValue_Runtime;
        }
        else
        {
            function = EmetconProtocol::GetValue_Shedtime;
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

INT Lcr3102Device::executeScan(CtiRequestMsg                  *pReq,
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
            function = EmetconProtocol::Scan_Integrity;
            found = getOperation(EmetconProtocol::Scan_Integrity, OutMessage->Buffer.BSt);
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

INT Lcr3102Device::executeGetConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT nRet = NoMethod;

    int  function = -1;
    bool found    = false;
    bool multiple_messages = false;

    if(parse.isKeyValid("rawloc"))
    {
        function = EmetconProtocol::GetConfig_Raw;
        found = getOperation(function, OutMessage->Buffer.BSt);

        OutMessage->Buffer.BSt.Function = parse.getiValue("rawloc");
        if( parse.isKeyValid("rawfunc") )
        {
            OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Read;
        }
        OutMessage->Buffer.BSt.Length = std::min(parse.getiValue("rawlen", 13), 13);    //  default (and maximum) is 13 bytes
    }
    if(parse.isKeyValid("install"))
    {
        function = EmetconProtocol::GetConfig_Softspec;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    if(parse.isKeyValid("addressing"))
    {
        // This one is special. Two requests will be sent here, data reads 0x01 and 0x02.
        function = EmetconProtocol::GetConfig_Addressing;
        found = getOperation(function, OutMessage->Buffer.BSt);

        if(found)
        {
            multiple_messages = true;

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

            // go to next function to get next set of data bytes
            OutMessage->Buffer.BSt.Function += 1;
        }
    }
    if(parse.isKeyValid("time"))
    {
        function = EmetconProtocol::GetConfig_Time;
        found = getOperation(function, OutMessage->Buffer.BSt);
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
        if(multiple_messages)
        {
            incrementGroupMessageCount(pReq->UserMessageId(), (long)pReq->getConnectionHandle());
        }

        nRet = NoError;
    }

    return nRet;
}

INT Lcr3102Device::executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList )
{
    INT nRet = NoMethod;

    int  function = -1;
    bool found    = false;

    if(parse.isKeyValid("rawloc"))
    {
        function = EmetconProtocol::PutConfig_Raw;
        found = getOperation(function, OutMessage->Buffer.BSt);

        OutMessage->Buffer.BSt.Function = parse.getiValue("rawloc");
        if( parse.isKeyValid("rawfunc") )
        {
            OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Function_Write;
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

bool Lcr3102Device::getOperation( const UINT &cmd, BSTRUCT &bst ) const
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


int Lcr3102Device::parseLoadValue(CtiCommandParser &parse)
{
    int load = parse.getiValue("load", 1);

    return (load >= 1 && load <= 4) ? load : -1;
}

// Finds the "previous hours" requested. Handles special values.
// 12, 24, and 36 are the default values in commander and are translated as:
// "Do 1 full read, do 2 full reads, and do 3 full reads"
int Lcr3102Device::parsePreviousValue(CtiCommandParser &parse)
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
CtiDeviceSingle::point_info Lcr3102Device::getSixBitValueFromBuffer(unsigned char buffer[], unsigned int valuePosition, unsigned int bufferSize)
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
LONG Lcr3102Device::getAddress() const
{
    return Inherited::getAddress() & 0x003FFFFF;
}

LONG Lcr3102Device::getSerial() const
{
    return Inherited::getAddress(); // Necessary for the expresscom commands! Need the whole address, not just 22 bits!
}


}       // namespace Devices
}       // namespace Cti

