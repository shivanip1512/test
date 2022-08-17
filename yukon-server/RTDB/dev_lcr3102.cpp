#include "precompiled.h"

#include "dev_lcr3102.h"
#include "ctidate.h"
#include "date_utility.h"
#include "database_writer.h"
#include "database_exceptions.h"

#include <boost/optional.hpp>

using std::string;
using std::endl;
using std::list;
using std::deque;
using namespace Cti::Protocols;
using namespace Cti::Devices::Commands;

namespace Cti {
namespace Devices {

const Lcr3102Device::CommandSet  Lcr3102Device::_commandStore = Lcr3102Device::initCommandStore();


Lcr3102Device::Lcr3102Device( )
{

}

YukonError_t Lcr3102Device::ErrorDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList )
{
    if( InMessage.Sequence == EmetconProtocol::Scan_Integrity )
    {
        resetScanFlag(ScanRateIntegrity);
    }

    return ClientErrors::None;
}


YukonError_t Lcr3102Device::ResultDecode( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    switch(InMessage.Sequence)
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
        case EmetconProtocol::Command_Loop:
        {
            status = decodeLoopback( InMessage, TimeNow, vgList, retList, outList );
            break;
        }
        default:
        {
            status = Inherited::ResultDecode( InMessage, TimeNow, vgList, retList, outList);

            if( status )
            {
                CTILOG_DEBUG(dout, "IM->Sequence = "<< InMessage.Sequence <<" for "<< getName());
            }
        }
    }

    if ( status == ClientErrors::None )
    {
        updateLastCommsTime( InMessage.Sequence, TimeNow );
    }

    return status;
}

YukonError_t Lcr3102Device::decodeGetValueTemperature( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    int txTemp, boxTemp;

    decodeMessageTemperature(DSt->Message, txTemp, boxTemp);

    string results = getName() + " / TX Temperature: " + CtiNumStr(((double)txTemp)/100.0, 1) + " degrees Centigrade\n"
                   + getName() + " / Ambient Box Temperature: " + CtiNumStr(((double)boxTemp)/100.0, 1) + " degrees Centigrade";

    ReturnMsg->setResultString(results);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Lcr3102Device::decodeGetValueTransmitPower( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    int transmitPower;

    decodeMessageTransmitPower(DSt->Message, transmitPower);

    string results = getName() + " / Current Transmit Power: " + CtiNumStr(transmitPower) + " percent";

    ReturnMsg->setResultString(results);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Lcr3102Device::decodeGetValueDutyCycle(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    point_info pi;
    int dutyCycle, currentTransformer;
    decodeMessageDutyCycle(DSt->Message, dutyCycle, currentTransformer);

    /* A nice little workaround. Firmware isn't working as intended right now and isn't correctly
       giving back the Current Transformer number from the message in the function above. This
       statement allows us to get the correct CT from the function we sent originally.
       In the future we will probably still want to rely on the message decoding to get the CT
       back since the message from the LCR SHOULD return the correct CT... but for now this is a
       reasonable fix.                                                                              */
    currentTransformer = InMessage.Return.ProtocolInfo.Emetcon.Function - FuncRead_DutyCyclePos + 1;

    int point_base = PointOffset_DutyCycleBase;

    if(currentTransformer == 1 || currentTransformer == 2)
    {
        pi.value = double(dutyCycle) / 60.0;
        pi.quality = NormalQuality;
        pi.description = "Duty Cycle CT " + CtiNumStr(currentTransformer);

        int point_offset = point_base + currentTransformer - 1;

        insertPointDataReport(AnalogPointType, point_offset, ReturnMsg, pi, pi.description);

        decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);
        retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );
    }
    else
    {
        CTILOG_ERROR(dout, "Value "<< currentTransformer <<" is not a valid current transformer number");

        return ClientErrors::BadParameter;
    }

    return status;
}

YukonError_t Lcr3102Device::decodeGetValueIntervalLast( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    point_info pi;

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

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    resetScanFlag(ScanRateIntegrity);

    return status;
}

//Decodes the getvalue shedtime/runtime read. All points are generated with a end of interval timestamp.
YukonError_t Lcr3102Device::decodeGetValueHistoricalTime( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    int relay   = 0; // Relay is 1-4 here
    int readNum = 0; // Read 1-3, 1 = hr 0-16, ect..
    int function = InMessage.Return.ProtocolInfo.Emetcon.Function;
    int point_base = 0;
    string identifier;
    const int relay_count = 4;

    if( InMessage.Sequence == EmetconProtocol::GetValue_Shedtime )
    {
        point_base = PointOffset_ShedtimeBase;
        function  -= FuncRead_ShedtimePos; // function is now 0-11
        identifier = "Shedtime";
    }
    else if( InMessage.Sequence == EmetconProtocol::GetValue_Runtime )
    {
        point_base = PointOffset_RuntimeBase;
        function  -= FuncRead_RuntimePos; // function is now 0-11
        identifier = "Runtime";
    }
    else
    {
        return ClientErrors::Abnormal;
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
        return ClientErrors::InvalidData;
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

    boost::optional<CtiTime>    latestRuntimeTimestamp;

    for( int i = 0; i < numberOfTimesReturned; i++ )
    {
        pi = getSixBitValueFromBuffer(DSt->Message + 1, i, std::min(DSt->Length - 1, 36 - 1));

        int point_offset = point_base + relay - 1;

        insertPointDataReport(AnalogPointType, point_offset, ReturnMsg,
                              pi, identifier + " Load " + CtiNumStr(relay), pointTime);

        // a non-zero runtime -- with an uninitialized latest runtime timestamp
        //      since timestamps are computed in reverse order the latest one comes first
        //      quality should be NormalQuality, invalid data is non-zero but marked with InvalidQuality
        if ( point_base == PointOffset_RuntimeBase &&
             pi.value != 0 &&
             pi.quality == NormalQuality &&
             ! latestRuntimeTimestamp )
        {
            latestRuntimeTimestamp = pointTime;
        }

        pointTime.addMinutes(-1*60); // subtract an hour for each value
    }

    if ( latestRuntimeTimestamp )
    {
        _dynamicComms.updateRelayRuntime( relay, *latestRuntimeTimestamp );
    }

    decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);
    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Lcr3102Device::decodeGetValueXfmrHistoricalRuntime( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    BSTRUCT       BSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    // Assuming this is not a point, let's just dump the data to the screen for now.

    std::vector<point_info> runtimeHours;

    decodeMessageXfmrHistoricalRuntime(InMessage.Buffer.DSt, runtimeHours);

    int currentTransformer = InMessage.Return.ProtocolInfo.Emetcon.Function - FuncRead_XfmrHistoricalCT1Pos + 1;

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
                CTILOG_ERROR(dout, "The data returned was outside the valid range: "<< runtimeMins );

                results += " - Data outside valid range";

            }
            else if( runtimeMins == 0x3f)
            {
                // Invalid data
                CTILOG_ERROR(dout, "Invalid data returned: "<< runtimeMins);

                results += " - Invalid data";
            }

            results += "\n";
            counter++;
        }

        ReturnMsg->setResultString(results);
        retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );
    }
    else
    {
        CTILOG_ERROR(dout, "Value "<< currentTransformer <<" is not a valid CT identifier");

        return ClientErrors::BadParameter;
    }

    return status;
}

YukonError_t Lcr3102Device::decodeGetValueControlTime( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    int relay = DSt->Message[0];
    if (!hasDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SSpecRevision))
    {
        CTILOG_ERROR(dout, "Device "<< getName() <<" is missing SSPEC revision number. Unable to decode control time data.");

        return ClientErrors::VerifySSPEC;
    }
    else
    {
        long revision = getDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SSpecRevision);
        if (revision > 10)
        {
            /**
             * Revision 1.0 sent back the relay value in the range of 1-4.
             * Revision 1.1+ sends the relay in the range of 0-3. In this
             * case we need to add one to the relay value received from the
             * LCR to match the way the 1.0 revision was decoded.
             */
            relay += 1;
        }
    }

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
        pi.description = "Control Time Remaining Relay " + CtiNumStr(relay);

        string results = getName() + " / " + identifier;
        ReturnMsg->setResultString(results);

        int point_offset = point_base + relay - 1;

        insertPointDataReport(AnalogPointType, point_offset, ReturnMsg, pi, identifier + " Relay " + CtiNumStr(relay));

        decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);
        retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );
    }
    else
    {
        CTILOG_ERROR(dout, "Relay number "<< relay <<" is invalid. Point has not been inserted");

        return ClientErrors::BadRange;
    }

    return status;
}

YukonError_t Lcr3102Device::decodeGetValuePropCount( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    point_info pi;

    pi.value       = DSt->Message[0];
    pi.quality     = NormalQuality;
    pi.description = "Propagation Counter";

    insertPointDataReport(AnalogPointType, PointOffset_PropCount, ReturnMsg, pi, "PropCount");

    ReturnMsg->setResultString(getName() + " / Propagation Counter: " + CtiNumStr(pi.value));

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Lcr3102Device::decodePutConfig( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    string results;

    switch( InMessage.Sequence )
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

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString( results );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Lcr3102Device::decodeGetConfigRaw( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    CtiCommandParser parse(InMessage.Return.CommandStr);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

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

        CTILOG_ERROR(dout, errorMessage);

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

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString( results );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Lcr3102Device::decodeGetConfigSoftspec( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    CtiCommandParser parse(InMessage.Return.CommandStr);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

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

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString( results );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

/**
 Process the responses from a GetConfigAddressing command.
 Records the results in the database as soon as all 3 responses come in.
*/
YukonError_t Lcr3102Device::decodeGetConfigAddressing( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    CtiCommandParser parse(InMessage.Return.CommandStr);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    string results;
    int function = InMessage.Return.ProtocolInfo.Emetcon.Function;

    // When we sent the getConfigAddress command, we sent 3 commands with 3 different functions under the covers.
    // Figure out which command this response is from.
    if(function == DataRead_AddressInfoPos)
    {
        decodeMessageAddress(DSt->Message,
            programAddressRelay[0], programAddressRelay[1], programAddressRelay[2], programAddressRelay[3],
            splinterAddressRelay[0], splinterAddressRelay[1], splinterAddressRelay[2], splinterAddressRelay[3]);

        results = getName() + " / Program Address Relay 1:  " + CtiNumStr(programAddressRelay[0]) + "\n"
            + getName() + " / Program Address Relay 2:  " + CtiNumStr(programAddressRelay[1]) + "\n"
            + getName() + " / Program Address Relay 3:  " + CtiNumStr(programAddressRelay[2]) + "\n"
            + getName() + " / Program Address Relay 4:  " + CtiNumStr(programAddressRelay[3]) + "\n"
            + getName() + " / Splinter Address Relay 1: " + CtiNumStr(splinterAddressRelay[0]) + "\n"
            + getName() + " / Splinter Address Relay 2: " + CtiNumStr(splinterAddressRelay[1]) + "\n"
            + getName() + " / Splinter Address Relay 3: " + CtiNumStr(splinterAddressRelay[2]) + "\n"
            + getName() + " / Splinter Address Relay 4: " + CtiNumStr(splinterAddressRelay[3]);

        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay1, (long)programAddressRelay[0]);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay2, (long)programAddressRelay[1]);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay3, (long)programAddressRelay[2]);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_ProgramAddressRelay4, (long)programAddressRelay[3]);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay1, (long)splinterAddressRelay[0]);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay2, (long)splinterAddressRelay[1]);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay3, (long)splinterAddressRelay[2]);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SplinterAddressRelay4, (long)splinterAddressRelay[3]);

        // Mark that we have received part 1
        lastAddressingMessage1 = InMessage.Time;
    }
    else if(function == DataRead_SubstationDataPos)
    {
        decodeMessageSubstation(DSt->Message, substation, feeder, zipcode, uda);

        results = getName() + " / Substation Address: "     + CtiNumStr(substation) + "\n"
                + getName() + " / Feeder Address: "         + CtiNumStr(feeder)     + "\n"
                + getName() + " / Zip Code: "               + CtiNumStr(zipcode)    + "\n"
                + getName() + " / User Defined Address: "   + CtiNumStr(uda);

        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_Substation,  (long)substation);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_Feeder,      (long)feeder);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_ZipCode,     (long)zipcode);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_Uda,         (long)uda);

        // Mark that we have received part 2
        lastAddressingMessage2 = InMessage.Time;
    }
    else if(function == DataRead_SoftspecPos)
    {
        decodeMessageSoftspec(DSt->Message, sspec, rev, serial, spid, geo);

        results = getName() + " / Software Specification " + CtiNumStr(sspec)
            + " rev " + CtiNumStr(((double)rev) / 10.0, 1) + "\n"
            + getName() + " / Serial Number: " + CtiNumStr(serial) + "\n"
            + getName() + " / SPID Address: " + CtiNumStr(spid) + "\n"
            + getName() + " / Geo Address: " + CtiNumStr(geo);

        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SSpec, (long)sspec);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SSpecRevision, (long)rev);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SerialAddress, (long)serial);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_Spid, (long)spid);
        setDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_GeoAddress, (long)geo);

        // Mark that we have received part 3
        lastAddressingMessage3 = InMessage.Time;
    }
    else
    {
        // Somehow we did got back something we didn't expect. Bad!
        return ClientErrors::Abnormal;
    }

    // If the last Addressing Messages were less than 5 minutes ago, 
    CtiTime fiveMinAgo = CtiTime::now() - 300;

    // And we've received all 3 responces
    if(lastAddressingMessage1 && *lastAddressingMessage1 > fiveMinAgo &&
        lastAddressingMessage2 && *lastAddressingMessage2 > fiveMinAgo &&
        lastAddressingMessage3 && *lastAddressingMessage3 > fiveMinAgo)
    {
        // Write the information to the database
        Database::DatabaseConnection conn;
        if(conn.isValid())
        {
            Database::DatabaseWriter writer(conn);
            // Unique key
            int id = SynchronizedIdGen("ReportedAddressExpressCom", 1);

            writeAddress(writer, id, InMessage.DeviceID);
            writeRelay(writer, id, 0);
            writeRelay(writer, id, 1);
            writeRelay(writer, id, 2);
            writeRelay(writer, id, 3);

            // Once we've written the values, don't do it again from this data
            lastAddressingMessage1.reset();
            lastAddressingMessage2.reset();
            lastAddressingMessage3.reset();
        }
        else
        {
            CTILOG_ERROR(dout, "Invalid Connection to Database");
        }
    }

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString( results );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

/**
    Write LCR config info to the ReportedAddressExpressCom table.
    Uses id as key.  Writes from the device objects data.
*/
void Lcr3102Device::writeAddress(Database::DatabaseWriter &writer, int id, long devid)
{
    try
    {
        static const std::string sql =
            "insert into ReportedAddressExpressCom "
            "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        writer.setCommandText(sql);

        writer << id;
        writer << devid;
        writer << *lastAddressingMessage1;
        writer << spid;
        writer << geo;
        writer << substation;
        writer << feeder;
        writer << zipcode;
        writer << uda;
        writer << -1;                       /* This is not available, so set it to a useless value. */

        writer.executeWithDatabaseException();
    }
    catch(std::runtime_error &e)
    {
        CTILOG_EXCEPTION_ERROR(dout, e);
    }
    catch(Database::DatabaseException &)
    {
        // Swallow the database exception without logging
    }
}

/**
    Write relay addresses to the ReportedAddressRelayExpressCom table. 
    Uses id as key.  Writes from the device objects data.
    Relay is indexed from 0.  If both relay addresses are 0, they are unused, so skip the write.
*/
void Lcr3102Device::writeRelay(Database::DatabaseWriter &writer, int id, int relay)
{
    if(relay < 0 || relay > 4)
    {
        CTILOG_ERROR(dout, "Invalid relay number specified");
        return;
    }

    if(programAddressRelay[relay] > 0 && splinterAddressRelay[relay] > 0)
    {
        try
        {
            static const std::string sql =
                "insert into ReportedAddressRelayExpressCom "
                "values (?, ?, ?, ?)";

            writer.setCommandText(sql);

            writer << id;
            writer << relay;
            writer << programAddressRelay[relay];
            writer << splinterAddressRelay[relay];

            writer.executeWithDatabaseException();
        }
        catch(std::runtime_error &e)
        {
            CTILOG_EXCEPTION_ERROR(dout, e);
        }
        catch(Database::DatabaseException &)
        {
            // Swallow the database exception without logging
        }
    }
}

YukonError_t Lcr3102Device::decodeGetConfigTime( const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT      *DSt       = &InMessage.Buffer.DSt;
    CtiReturnMsg *ReturnMsg = NULL;     // Message sent to VanGogh, inherits from Multi

    CtiCommandParser parse(InMessage.Return.CommandStr);

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

    string results;
    CtiTime time;

    decodeMessageTime(DSt->Message, time);

    results = getName() + " / Current Time: " + time.asString();

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString( results );

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}

YukonError_t Lcr3102Device::decodeLoopback(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    auto ReturnMsg = std::make_unique<CtiReturnMsg>(getID(), InMessage.Return.CommandStr);

    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(getName() + " / successful ping");

    retMsgHandler(InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg.release(), vgList, retList);

    return ClientErrors::None;
}

void Lcr3102Device::decodeMessageXfmrHistoricalRuntime( const DSTRUCT DSt, std::vector<point_info> &runtimeHours)
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

void Lcr3102Device::decodeMessageSoftspec( const BYTE Message[], int &sspec, int &rev, int &serial, int &spid, int &geo )
{
    sspec  = Message[0] | (Message[4] << 8);
    rev    = Message[1];
    serial = (Message[5] << 24) | (Message[6] << 16) | (Message[7] << 8) | Message[8];
    spid   = (Message[9] << 8) | Message[10];
    geo    = (Message[11] << 8) | Message[12];
}

void Lcr3102Device::decodeMessageAddress( const BYTE Message[], int &prgAddr1, int &prgAddr2, int &prgAddr3, int &prgAddr4, int &splAddr1, int &splAddr2, int &splAddr3, int &splAddr4 )
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

void Lcr3102Device::decodeMessageSubstation( const BYTE Message[], int &substation, int &feeder, int &zip, int &uda )
{
    substation = (Message[0] << 8) | Message[1]; // Substation Address
    feeder = (Message[2] << 8) | Message[3]; // Feeder Address
    zip = (Message[4] << 16) | (Message[5] << 8) | Message[6]; // Zip Code
    uda = (Message[7] << 8) | Message[8]; // User Defined Address
}

void Lcr3102Device::decodeMessageTime( const BYTE Message[], CtiTime &time )
{
    int utcSeconds = (Message[0] << 24) | (Message[1] << 16) | (Message[2] << 8) | Message[3];

    time = CtiTime::fromLocalSeconds(utcSeconds);
}

void Lcr3102Device::decodeMessageTransmitPower( const BYTE Message[], int &transmitPower )
{
    transmitPower = Message[0];
}

void Lcr3102Device::decodeMessageTemperature( const BYTE Message[], int &txTemp, int &boxTemp )
{
    txTemp = (Message[0] << 8) | Message[1]; // TX Temp
    boxTemp = (Message[2] << 8) | Message[3]; // Box Temp
}

void Lcr3102Device::decodeMessageDutyCycle( const BYTE Message[], int &dutyCycle, int &transformer )
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
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Model,          EmetconProtocol::IO_Read, DataRead_SoftspecPos,       DataRead_SoftspecLen)); // GetConfig_Model will mimic GetConfig_Softspec for LCR devices.
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Addressing,     EmetconProtocol::IO_Read, DataRead_SubstationDataPos, DataRead_SubstationDataLen));
    cs.insert(CommandStore(EmetconProtocol::GetValue_Temperature,     EmetconProtocol::IO_Read, DataRead_TemperaturePos,    DataRead_TemperatureLen));

    cs.insert(CommandStore(EmetconProtocol::Command_Loop,             EmetconProtocol::IO_Read, DataRead_LoopbackPos,       DataRead_LoopbackLen));

    cs.insert(CommandStore(EmetconProtocol::PutConfig_Raw, EmetconProtocol::IO_Write, 0, 0));    // filled in later
    cs.insert(CommandStore(EmetconProtocol::GetConfig_Raw, EmetconProtocol::IO_Read,  0, 0));    // ...ditto

    return cs;
}

YukonError_t Lcr3102Device::IntegrityScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, INT ScanPriority)
{
    YukonError_t status = ClientErrors::None;

    if(OutMessage != NULL)
    {
        if(getOperation(EmetconProtocol::Scan_Integrity, OutMessage->Buffer.BSt))
        {
            populateDlcOutMessage(*OutMessage);
            EstablishOutMessagePriority( OutMessage, ScanPriority );
            OutMessage->Sequence  = EmetconProtocol::Scan_Integrity;
            OutMessage->Request.RouteID   = getRouteID();
            OutMessage->Request.RetryMacroOffset = MacroOffset::none;

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

            status = ClientErrors::NoMethod;

            CTILOG_ERROR(dout, "Command lookup failed for Device "<< getName());
        }
    }

    return status;
}


YukonError_t Lcr3102Device::executeLoopback (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    const auto function = EmetconProtocol::Command_Loop;

    if (!getOperation(function, OutMessage->Buffer.BSt))
    {
        return ClientErrors::NoMethod;
    }

    populateDlcOutMessage(*OutMessage);
    OutMessage->Sequence = function;     // Helps us figure it out later!
    OutMessage->Request.RouteID = getRouteID();

    strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

    for (int i = 0; i < parse.getiValue("count"); i++)
    {
        outList.push_back(new OUTMESS(*OutMessage));
    }
        
    delete OutMessage;
    OutMessage = NULL;

    return ClientErrors::None;
}


YukonError_t Lcr3102Device::executeGetValue ( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t nRet = ClientErrors::NoMethod;

    int  function = -1;
    bool found    = false;

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
        if (!hasDynamicInfo(CtiTableDynamicPaoInfo::Key_LCR_SSpecRevision))
        {
            executeBackgroundRequest("getconfig install", OutMessage, outList);

            CtiReturnMsg *ReturnMsg =
                new CtiReturnMsg(
                   getID(),
                   OutMessage->Request.CommandStr,
                   getName() + " / SSPEC revision not retrieved yet, attempting to read it automatically; please retry command in a few minutes");

            ReturnMsg->setUserMessageId(OutMessage->Request.UserID);
            ReturnMsg->setConnectionHandle(OutMessage->Request.Connection);

            retMsgHandler( OutMessage->Request.CommandStr, ClientErrors::VerifySSPEC, ReturnMsg, vgList, retList );

            delete OutMessage;
            OutMessage = 0;
            found = false;
            nRet  = ExecutionComplete;
        }
        else
        {
            function = EmetconProtocol::GetValue_ControlTime;
            found = getOperation(function, OutMessage->Buffer.BSt);

            int load = parseLoadValue(parse);

            if( load == -1 )
            {
                nRet = ClientErrors::BadParameter;
            }
            else
            {
                OutMessage->Buffer.BSt.Function += (load - 1);
            }
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
            nRet = ClientErrors::BadParameter;
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
            nRet = ClientErrors::BadParameter;
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

        auto tamperRead = std::make_unique<Lcr3102TamperReadCommand>();

        found = !! tryExecuteCommand(*OutMessage, std::move(tamperRead));

        function = OutMessage->Sequence;
    }
    else if(parse.getFlags() & CMD_FLAG_GV_DR_SUMMARY)
    {
        string xcomRequest = "getvalue xcom dr summary serial " + CtiNumStr(getSerial());

        parse = CtiCommandParser(xcomRequest);
        parse.setValue("xc_serial", getSerial());

        auto tamperRead = std::make_unique<Lcr3102DemandResponseSummaryCommand>();

        found = !! tryExecuteCommand(*OutMessage, std::move(tamperRead));

        function = OutMessage->Sequence;
    }
    else if(parse.getFlags() & CMD_FLAG_GV_HOURLY_LOG)
    {
        // Grab the info from the parser!
        CtiDate date = parseDateString(parse.getsValue("hourly_log_date"));

        const boost::optional<TimeParts> timeParts = parseTimeString(parse.getsValue("hourly_log_time"));

        if( timeParts )
        {
            CtiTime executeTime = CtiTime(date, timeParts->hour, timeParts->minute, timeParts->second);

            auto hourlyLogRead = std::make_unique<Lcr3102HourlyDataLogCommand>(executeTime.seconds());

            found = !! tryExecuteCommand(*OutMessage, std::move(hourlyLogRead));

            function = OutMessage->Sequence;
        }
    }
    else if(parse.getFlags() & CMD_FLAG_GV_RUNTIME || parse.getFlags() & CMD_FLAG_GV_SHEDTIME)
    {
        return executeGetValueHistorical(pReq, parse, OutMessage, outList);
    }

    if(found)
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = ClientErrors::None;
    }

    return nRet;
}

YukonError_t Lcr3102Device::executeScan(CtiRequestMsg                  *pReq,
                                        CtiCommandParser               &parse,
                                        OUTMESS                        *&OutMessage,
                                        CtiMessageList      &vgList,
                                        CtiMessageList      &retList,
                                        OutMessageList         &outList)
{
    bool found = false;
    YukonError_t nRet  = ClientErrors::None;
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
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;     // Helps us figure it out later!
        OutMessage->Request.RouteID   = getRouteID();

        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
    }

    return nRet;
}

YukonError_t Lcr3102Device::executeGetConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t nRet = ClientErrors::NoMethod;

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
    if(parse.isKeyValid("install") || parse.isKeyValid("model"))
    {
        function = EmetconProtocol::GetConfig_Softspec;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }
    if(parse.isKeyValid("addressing"))
    {
        // This one is special. Three requests will be sent here, data reads 0x00, 0x01 and 0x02.
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

            // Queue the read of function DataRead_SoftspecPos
            OutMessage->Buffer.BSt.Function = DataRead_SoftspecPos;
            strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
            outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
            incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle());

            // Queue the read of function DataRead_SubstationDataPos
            OutMessage->Buffer.BSt.Function = DataRead_SubstationDataPos;
            strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
            outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
            incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle());

            // Let the remander of the method queue the read of relay addresses function DataRead_AddressInfoPos
            OutMessage->Buffer.BSt.Function = DataRead_AddressInfoPos;
        }
    }
    if(parse.isKeyValid("time"))
    {
        function = EmetconProtocol::GetConfig_Time;
        found = getOperation(function, OutMessage->Buffer.BSt);
    }

    if(!found)
    {
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);
        if(multiple_messages)
        {
            incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle());
        }

        nRet = ClientErrors::None;
    }

    return nRet;
}

YukonError_t Lcr3102Device::executePutConfig( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    YukonError_t nRet = ClientErrors::NoMethod;

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
        nRet = ClientErrors::NoMethod;
    }
    else
    {
        populateDlcOutMessage(*OutMessage);
        OutMessage->Sequence  = function;         // Helps us figure it out later!

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        nRet = ClientErrors::None;
    }

    return nRet;
}

YukonError_t Lcr3102Device::executeGetValueHistorical( CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, OutMessageList &outList )
{
    YukonError_t nRet = ClientErrors::NoMethod;
    int function = -1;

    if(parse.getFlags() & CMD_FLAG_GV_RUNTIME)
    {
        function = EmetconProtocol::GetValue_Runtime;
    }
    else
    {
        function = EmetconProtocol::GetValue_Shedtime;
    }

    bool found = getOperation(function, OutMessage->Buffer.BSt);

    int load = parseLoadValue(parse);
    int previous = parsePreviousValue(parse);

    if( load == -1 || previous == -1)
    {
        nRet = ClientErrors::BadParameter;
    }
    else
    {
        // packed 6 bit data
        int total_bits = previous * 6;

        // round up
        int remaining_bytes = (total_bits + 7) / 8;
        int total_messages  = (remaining_bytes + 11) / 12;

        if (found)
        {
            // pack relay number into OutMessages Function
            OutMessage->Buffer.BSt.Function += (load - 1);

            // Set up the OutMessage.
            OutMessage->DeviceID  = getID();
            OutMessage->TargetID  = getID();
            OutMessage->Port      = getPortID();
            OutMessage->Remote    = getAddress();
            OutMessage->TimeOut   = 2;
            OutMessage->Sequence  = function;         // Helps us figure it out later!
            OutMessage->Retry     = 2;
            OutMessage->Request.RouteID = getRouteID();

            /**
             * Handle the retries if necessary.
             *
             *  If optionsField is 0, we aren't a retry: Continue function
             *      normally.
             *  If optionsField is 1, we're a retry of the first "part" of
             *      this command, hours 0-15, (function 0xB0/0xC0) plus the
             *      load value which has been added previously. No need to
             *      modify the length or function value as they were already
             *      set in the getOperation function call.
             *  If optionsField is 2, we're a retry of the second "part" of
             *      this command, hours 16-31, (function 0xB4/0xC4) plus the
             *      load value which has been added previously. Increment
             *      the function value by four and leave the length at 13.
             *  If optionsField is 3, we're a retry of the third "part" of
             *      this command, hours 32-35, (function 0xB8/0xC8) plus the
             *      load value which has been added previously. Increment
             *      the function value by eight and set the length to four.
             *  Any other optionsField value is invalid and will result in
             *      treating this message as though it were the original
             *      attempts.
             *
             *  The switch statement below uses fall-throughs to achieve the
             *      functionality noted above.
             */
            int optionsField = pReq->OptionsField();
            switch (optionsField)
            {
                case 3:
                {
                    // Third retry is 4 6-bit packed hours and a flags byte.
                    OutMessage->Buffer.BSt.Length = 4;
                    OutMessage->Buffer.BSt.Function += 4;
                    // Fall through!
                }
                case 2:
                {
                    // Length is already correct, increment the function.
                    OutMessage->Buffer.BSt.Function += 4;
                    // Fall through!
                }
                case 1:
                {
                    // Length and function are both correct, set total_messages to one for any retry.
                    total_messages = 1;

                    // Put the optionsField value into the OutMessage in case of future retries.
                    OutMessage->Request.OptionsField = optionsField;
                }
                case 0:
                {
                    // Nothing to do here.
                    break;
                }
                default:
                {
                    CTILOG_ERROR(dout, "Invalid optionsField value "<< optionsField <<" for device "<< getName() <<
                            ". Treating this message as an initial attempt"
                            );
                }
            }

            if (total_messages == 1)
            {
                // Don't need to mess with anything here.
                strncpy(OutMessage->Request.CommandStr, parse.getCommandStr().c_str(), COMMAND_STR_SIZE);
                outList.push_back(CTIDBG_new OUTMESS(*OutMessage));
            }
            else
            {
                // For all but the last message - send a full 13 byte request - 12 data bytes and 1 Flags byte
                for (int i = 0; i < total_messages; i++)
                {
                    // Store which read this is for retries.
                    OutMessage->Request.OptionsField = i + 1;

                    // The first two reads are 13 bytes always, the third read is always 4 bytes.
                    OutMessage->Buffer.BSt.Length = (OutMessage->Request.OptionsField != 3) ? 13 : 4;

                    strncpy(OutMessage->Request.CommandStr, parse.getCommandStr().c_str(), COMMAND_STR_SIZE);

                    outList.push_back(new OUTMESS(*OutMessage));
                    incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle());

                    // go to next function to get next set of data bytes
                    OutMessage->Buffer.BSt.Function += 4;
                }
            }

            delete OutMessage;
            OutMessage = NULL;

            nRet = ClientErrors::None;
        }
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
CtiDeviceSingle::point_info Lcr3102Device::getSixBitValueFromBuffer(const unsigned char buffer[], unsigned int valuePosition, unsigned int bufferSize)
{
    point_info retVal;
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


std::string Lcr3102Device::getSQLCoreStatement() const
{
    static const std::string sql =
        "SELECT "
            "YP.paobjectid, "
            "YP.category, "
            "YP.paoclass, "
            "YP.paoname, "
            "YP.type, "
            "YP.disableflag, "
            "DV.deviceid, "
            "DV.alarminhibit, "
            "DV.controlinhibit, "
            "DCS.address, "
            "RTS.routeid, "
            "DLP.lastintervaldemandrate, "
            "DLP.loadprofiledemandrate, "
            "DLP.loadprofilecollection, "
            "DLP.voltagedmdinterval, "
            "DLP.voltagedmdrate, "
            "MCT.deviceid, "
            "MCT.connectedied, "
            "MCT.password, "
            "MCT.iedscanrate, "
            "MCT.defaultdataclass, "
            "MCT.defaultdataoffset, "
            "MCT.realtimescan, "
            "DLCRC.deviceid AS LcrCommsExist "
        "FROM "
            "YukonPAObject YP "
                "JOIN Device DV ON YP.paobjectid = DV.deviceid "
                "JOIN DeviceLoadProfile DLP ON YP.paobjectid = DLP.deviceid "
                "JOIN DeviceCarrierSettings DCS ON YP.paobjectid = DCS.deviceid "
                "LEFT OUTER JOIN DeviceRoutes RTS ON YP.paobjectid = RTS.deviceid "
                "LEFT OUTER JOIN DeviceMCTIEDPort MCT ON YP.paobjectid = MCT.deviceid "
                "LEFT OUTER JOIN DynamicLcrCommunications DLCRC ON YP.paobjectid = DLCRC.deviceid "
        "WHERE "
            "1 = 1";

    return sql;
}


void Lcr3102Device::DecodeDatabaseReader( RowReader & rdr )
{
    if ( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }

    Inherited::DecodeDatabaseReader(rdr);

    _dynamicComms.DecodeDatabaseReader(rdr);
}


void Lcr3102Device::updateLastCommsTime( const INT sequence_id, const CtiTime & current_time )
{
    switch ( sequence_id )
    {
        case EmetconProtocol::Scan_Integrity:

        case EmetconProtocol::GetValue_IntervalLast:
        case EmetconProtocol::GetValue_Runtime:
        case EmetconProtocol::GetValue_Shedtime:
        case EmetconProtocol::GetValue_Temperature:
        case EmetconProtocol::GetValue_TransmitPower:
        case EmetconProtocol::GetValue_ControlTime:
        case EmetconProtocol::GetValue_XfmrHistoricalCT:
        case EmetconProtocol::GetValue_PropCount:
        case EmetconProtocol::GetValue_DutyCycle:

        case EmetconProtocol::GetConfig_Softspec:
        case EmetconProtocol::GetConfig_Time:
        case EmetconProtocol::GetConfig_Addressing:
        case EmetconProtocol::GetConfig_Raw:
        {
            _dynamicComms.updateLastCommsTime( current_time );

            break;
        }
        default:
        {
            // don't update the last communicated time for any command not listed above
            break;
        }
    }
}


}       // namespace Devices
}       // namespace Cti

