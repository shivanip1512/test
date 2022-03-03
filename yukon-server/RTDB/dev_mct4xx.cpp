#include "precompiled.h"

#include <boost/regex.hpp>
#include <limits>

#include "dev_mct4xx.h"
#include "dev_mct470.h"  //  for sspec information
#include "dev_mct410.h"  //  for sspec information

#include "devicetypes.h"
#include "numstr.h"
#include "config_data_mct.h"

#include "ctidate.h"
#include "date_utility.h"
#include "std_helper.h"

#include <boost/algorithm/string/replace.hpp>
#include <boost/algorithm/string/predicate.hpp>

using namespace Cti::Protocols;
using namespace Cti::Devices::Commands;
using std::string;
using std::endl;
using std::list;
using std::set;
using std::map;

namespace Cti::Devices {

const char *Mct4xxDevice::PutConfigPart_basic           = "basic";
const char *Mct4xxDevice::PutConfigPart_all             = "all";
const char *Mct4xxDevice::PutConfigPart_tou             = "tou";
const char *Mct4xxDevice::PutConfigPart_dst             = "dst";
const char *Mct4xxDevice::PutConfigPart_timezone        = "timezone";
const char *Mct4xxDevice::PutConfigPart_vthreshold      = "vthreshold";
const char *Mct4xxDevice::PutConfigPart_demand_lp       = "demandlp";
const char *Mct4xxDevice::PutConfigPart_options         = "options";
const char *Mct4xxDevice::PutConfigPart_configbyte      = "configbyte";
const char *Mct4xxDevice::PutConfigPart_time_adjust_tolerance  = "timeadjusttolerance";
const char *Mct4xxDevice::PutConfigPart_addressing      = "addressing";
const char *Mct4xxDevice::PutConfigPart_disconnect      = "disconnect";
const char *Mct4xxDevice::PutConfigPart_holiday         = "holiday";
const char *Mct4xxDevice::PutConfigPart_usage           = "usage";
const char *Mct4xxDevice::PutConfigPart_llp             = "llp";
const char *Mct4xxDevice::PutConfigPart_lpchannel       = "lpchannel";
const char *Mct4xxDevice::PutConfigPart_relays          = "relays";
const char *Mct4xxDevice::PutConfigPart_precanned_table = "precannedtable";
const char *Mct4xxDevice::PutConfidPart_spid            = "spid";
const char *Mct4xxDevice::PutConfigPart_centron         = "centron";
const char *Mct4xxDevice::PutConfigPart_dnp             = "dnp";
const char *Mct4xxDevice::PutConfigPart_display         = "display";
const char *Mct4xxDevice::PutConfigPart_phaseloss       = "phaseloss";
const char *Mct4xxDevice::PutConfigPart_meter_parameters = "meterparameters";
const char *Mct4xxDevice::PutConfigPart_freeze_day      = "freezeday";

const std::string Mct4xxDevice::ErrorText_OutOfRange = "Requested interval outside of valid range";

const std::string PeakString_Day = "day";
const std::string PeakString_Hour = "hour";
const std::string PeakString_Interval = "interval";

const Mct4xxDevice::CommandSet Mct4xxDevice::_commandStore = Mct4xxDevice::initCommandStore();

const CtiDate                  Mct4xxDevice::DawnOfTime_Date = CtiDate(CtiTime(Mct4xxDevice::DawnOfTime_UtcSeconds));

/** Map to translate Device Config Schedules to an enuneration */
const std::map<std::string, long> Mct4xxDevice::scheduleMap {
    {"SCHEDULE_1", 0},
    {"SCHEDULE_2", 1},
    {"SCHEDULE_3", 2},
    {"SCHEDULE_4", 3}
};

/** Map to translate Device Config Timezones to an enuneration */
const std::map<std::string, long> Mct4xxDevice::timezoneMap {
    {"NORONHA", -2},
    {"SAO_PAULO", -3},
    {"MANAUS", -4},
    {"NEW_YORK", -5},
    {"CHICAGO", -6},
    {"DENVER", -7},
    {"LOS_ANGELES", -8},
    {"ANCHORAGE", -9},
    {"HONOLULU", -10}
};

bool Mct4xxDevice::llp_peak_report_interest_t::tryContinueRequest(long &incoming_request)
{
    //  The low bit means "locked"
    //  Existing in-progress request
    if( incoming_request % 2 )
    {
        long potential_request = incoming_request;

        if( request_state.compare_exchange_strong(potential_request, incoming_request + 2) )
        {
            incoming_request += 2;

            return true;
        }
    }

    return false;
}

bool Mct4xxDevice::llp_peak_report_interest_t::tryBeginRequest(long &incoming_request)
{
    if( ! incoming_request )
    {
        long original_request = request_state;

        //  The low bit means "locked"
        //  No existing in-progress request
        if( ! (original_request % 2) )
        {
            if( request_state.compare_exchange_strong(original_request, original_request + 1) )
            {
                incoming_request = original_request + 1;

                no_overlap = false;

                return true;
            }
        }
    }

    return false;
}


bool Mct4xxDevice::llp_peak_report_interest_t::tryEndRequest(long request)
{
    //  The low bit means "locked"
    if( request % 2)
    {
        if( request_state.compare_exchange_strong(request, request + 1) )
        {
            end_date = DawnOfTime_Date;

            return true;
        }
    }

    return false;
}


Mct4xxDevice::Mct4xxDevice()
{
    _llpInterest.time    = 0;
    _llpInterest.channel = 0;

    _llpRequest.begin      = 0;
    _llpRequest.end        = 0;
    _llpRequest.channel    = 0;
    _llpRequest.retry      = 0;

    _llpPeakInterest.channel  = 0;
    _llpPeakInterest.range    = 0;
    _llpPeakInterest.end_date = DawnOfTime_Date;

    for( int i = 0; i < LPChannels; i++ )
    {
        //  initialize them to 0
        _lp_info[i].collection_point = 0;
        _lp_info[i].current_schedule = 0;
    }
}

const Mct4xxDevice::error_map Mct4xxDevice::error_codes {
        { 0xfffffffe, error_details("Meter communications problem",                 InvalidQuality) },
        { 0xfffffffd, error_details("No data yet available for requested interval", InvalidQuality) },
        { 0xfffffffc, error_details("No data yet available for requested interval", InvalidQuality) },
        { 0xfffffffa, error_details(ErrorText_OutOfRange,                           InvalidQuality) },
        { 0xfffffff8, error_details("Device filler",                                DeviceFillerQuality) },
        { 0xfffffff6, error_details("Power failure occurred during part or all of this interval",   PowerfailQuality) },
        { 0xfffffff4, error_details("Power restored during this interval",          PartialIntervalQuality) },
        { 0xffffffe1, error_details("Overflow",                                     OverflowQuality) },
        { 0xffffffe0, error_details("Overflow",                                     OverflowQuality) } };


Mct4xxDevice::CommandSet Mct4xxDevice::initCommandStore()
{
    namespace EP = EmetconProtocol;

    CommandSet cs;

    cs.insert(CommandStore(EP::GetValue_TOUPeak,        EP::IO_Function_Read,  FuncRead_TOUBasePos,        FuncRead_TOULen));

    //  This is the default TOU reset command - the command that zeroes the rates (Command_TOUResetZero) is assigned
    //    in executePutValue() if needed
    cs.insert(CommandStore(EP::PutValue_TOUReset,       EP::IO_Write,          Command_TOUReset,           0));

    cs.insert(CommandStore(EP::PutValue_ResetPFCount,   EP::IO_Write,          Command_PowerfailReset,     0));

    cs.insert(CommandStore(EP::PutConfig_TSync,         EP::IO_Function_Write, FuncWrite_TSyncPos,         FuncWrite_TSyncLen));

    cs.insert(CommandStore(EP::PutConfig_TOUEnable,     EP::IO_Write,          Command_TOUEnable,          0));
    cs.insert(CommandStore(EP::PutConfig_TOUDisable,    EP::IO_Write,          Command_TOUDisable,         0));

    cs.insert(CommandStore(EP::PutConfig_LoadProfileInterest,
                                                        EP::IO_Function_Write, FuncWrite_LLPInterestPos,   FuncWrite_LLPInterestLen));

    cs.insert(CommandStore(EP::PutConfig_LoadProfileReportPeriod,
                                                        EP::IO_Function_Write, FuncWrite_LLPPeakInterestPos, FuncWrite_LLPPeakInterestLen));

    return cs;
}

string Mct4xxDevice::printTimestamp(unsigned long timestamp)
{
    string retval;

    if( timestamp > DawnOfTime_UtcSeconds )
    {
        retval = CtiTime(timestamp).asString();
    }
    else
    {
        retval = "[invalid time (" + CtiNumStr(timestamp).hex().zpad(8) + ")]";
    }

    return retval;
}

string Mct4xxDevice::printDate(const CtiDate &dt)
{
    string retval;

    if( dt > DawnOfTime_Date )
    {
        retval = dt.asStringMDY();
    }
    else
    {
        retval = "[invalid date (" + dt.asString() + ")]";
    }

    return retval;
}

string Mct4xxDevice::printDate(unsigned long timestamp)
{
    string retval;

    if( timestamp > DawnOfTime_UtcSeconds )
    {
        CtiDate date_to_print;

        date_to_print = CtiDate(CtiTime(timestamp));

        int month,
            day   = date_to_print.dayOfMonth(),
            year  = date_to_print.year();

        retval = CtiNumStr(date_to_print.month()).zpad(2)      + "/" +
                 CtiNumStr(day).zpad(2) + "/" +
                 CtiNumStr(year);
    }
    else
    {
        retval = "[invalid date (" + CtiNumStr(timestamp).hex().zpad(8) + ")]";
    }

    return retval;
}


bool Mct4xxDevice::getOperation( const UINT &cmd, BSTRUCT &bst ) const
{
    if( getOperationFromStore(_commandStore, cmd, bst) )
    {
        return true;
    }

    return Parent::getOperation(cmd, bst);
}


bool Mct4xxDevice::sspecAtLeast(const int rev_desired) const
{
    int sspec = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec);
    int rev   = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision);

    return sspecValid(sspec, rev) && rev >= rev_desired;
}


unsigned char Mct4xxDevice::crc8( const unsigned char *buf, unsigned int len )
{
    const unsigned char llpcrc_poly = 0x07;

    /* Function to calculate the 8 bit crc of the channel of interest
         and the period of interest start time.  This 8 bit crc will
         be sent back in byte 1 of a long load profile read.

       Parameters: uint8_t channel - This is the channel that is going
                                       to be returned.
                   uint8_t *time - A pointer to the period of interest start time

       Return: uint8_t crc
       */

    unsigned short crc = 0;
    unsigned char  current_byte;

    // Walk through the bytes of data
    for( int i = 0; i <= len; i++ )
    {
        if( i < len )
        {
            current_byte = buf[i];
        }
        else
        {
            current_byte = 0;
        }

        // Walk through each bit of the byte
        for(int bit = 0; bit < 8; bit++ )
        {
            // Move the data bits into the crc
            crc <<= 1;
            if( current_byte & 0x80 )
            {
                crc |= 0x0001;
            }

            // Do the crc calculation
            if( crc & 0x0100 )
            {
                crc ^= llpcrc_poly;
            }

            // Move the data byte ahead 1 bit
            current_byte <<= 1;
        }
    }

    return (unsigned char)(crc & 0xff);
}


Mct4xxDevice::frozen_point_info Mct4xxDevice::decodePulseAccumulator( const unsigned char *buf, unsigned len, const unsigned char *freeze_counter )
{
    return freeze_counter ?
        getData(buf, len, ValueType_FrozenAccumulator) :
        getData(buf, len, ValueType_Accumulator);
}


Mct4xxDevice::frozen_point_info Mct4xxDevice::getData( const unsigned char *buf, const unsigned len, const ValueType4xx vt )
{
    unsigned long error_code = 0xffffffff;  //  filled with 0xff because some data types are less than 32 bits

    long long value = 0;
    frozen_point_info  retval;

    for( int i = 0; i < len; i++ )
    {
        //  input data is in MSB order
        value      = value      << 8 | buf[i];
        error_code = error_code << 8 | buf[i];
    }

    retval.freeze_bit = 0;

    switch( vt )
    {
        case ValueType_FrozenAccumulator:
        case ValueType_Accumulator:
        {
            if( value >= MaxAccumulatorValue )
            {
                return getDataError(error_code, error_codes);
            }

            break;
        }
    }

    retval.value   = value;
    retval.quality = NormalQuality;

    return retval;
}


Mct4xxDevice::frozen_point_info Mct4xxDevice::getDataError( unsigned error_code, const Mct4xxDevice::error_map &error_codes )
{
    frozen_point_info pi;

    pi.value = 0;
    pi.freeze_bit = 0;

    error_map::const_iterator error_itr = error_codes.find(error_code);

    if( error_itr != error_codes.end() )
    {
        pi.quality     = error_itr->second.quality;
        pi.description = error_itr->second.description;
    }
    else
    {
        pi.quality     = InvalidQuality;
        pi.description = "Unknown/reserved error [" + CtiNumStr(error_code).hex() + "]";
    }

    return pi;
}

class OutMessPtrDeleter
{
    OUTMESS **_ref;

public:

    OutMessPtrDeleter( OUTMESS *&om_ptr ) : _ref(&om_ptr)  {}

    void release()  {  _ref = 0;  }

    ~OutMessPtrDeleter()
    {
        if( _ref )
        {
            delete *_ref;
            *_ref = 0;
        }
    }
};


std::unique_ptr<CtiReturnMsg> Mct4xxDevice::makeReturnMsg(const std::string &message, const YukonError_t code, const PIL_ECHO &request) const
{
    return makeReturnMsg(message, code, request, ExpectMore::False);
}

std::unique_ptr<CtiReturnMsg> Mct4xxDevice::makeReturnMsg(const std::string &message, const YukonError_t code, const PIL_ECHO &request, const ExpectMore e) const
{
    auto retMsg =
            std::make_unique<CtiReturnMsg>(
                    getID(),
                    request,
                    getName() + " / " + message,
                    code);

    retMsg->setConnectionHandle(request.Connection);

    if( e == ExpectMore::True )
    {
        retMsg->setExpectMore(true);
    }

    return std::move(retMsg);
}


std::unique_ptr<CtiMessage> Mct4xxDevice::makeLpResumeRequest(const OUTMESS &OutMessage)
{
    bool hasRequiredInfo = true;

    hasRequiredInfo &= hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_Channel);
    hasRequiredInfo &= hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestBegin);
    hasRequiredInfo &= hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestEnd);

    if( ! hasRequiredInfo )
    {
        return makeReturnMsg(
                    "Missing one of the following:"
                        "\nKey_MCT_LLPInterest_Channel"
                        "\nKey_MCT_LLPInterest_RequestBegin"
                        "\nKey_MCT_LLPInterest_RequestEnd",
                    ClientErrors::MissingParameter,
                    OutMessage.Request);
    }

    const int channel = getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_Channel);

    const CtiTime time_begin(getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestBegin)),
                  time_end  (getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestEnd));

    auto lpRequest =
            std::make_unique<CtiRequestMsg>(
                    getID(),
                    "getvalue lp channel " + boost::lexical_cast<std::string>(channel)
                        + " " + time_begin.asString()
                        + " " + time_end.asString());

    lpRequest->setMessagePriority(ScanPriority_LoadProfile);

    return std::move(lpRequest);
}


YukonError_t appendMsgTo(CtiDeviceSingle::CtiMessageList &msgList, std::unique_ptr<CtiMessage> &&msg)
{
    msgList.push_back(msg.release());

    return ClientErrors::None;
}


YukonError_t Mct4xxDevice::executeGetValue(CtiRequestMsg *pReq,  CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::NoMethod;

    bool found = false;

    static const string str_lp_command = "lp_command";
    if( parse.isKeyValid(str_lp_command) )  //  load profile
    {
        int year, month, day, hour, minute;

        string cmd = parse.getsValue(str_lp_command);

        if( cmd == "status" )
        {
            string lp_status_string = "Load profile request status:";

            if( _llpRequest.request_id )
            {
                lp_status_string += "\nRequested channel: " + CtiNumStr(_llpRequest.channel + 1);
                lp_status_string += "\nCurrent interval: " + printTimestamp(_llpRequest.begin);
                lp_status_string += "\nEnding interval:  " + printTimestamp(_llpRequest.end);
            }
            else
            {
                lp_status_string += "\nNo active load profile requests for this device";
            }

            OutMessPtrDeleter d(OutMessage);

            return appendMsgTo(retList, makeReturnMsg(
                        lp_status_string,
                        ClientErrors::None,
                        OutMessage->Request));
        }
        else if( cmd == "cancel" )
        {
            bool cancelled = _llpRequest.request_id.exchange(0);

            //  clear out the request times so we don't restart this request on startup
            _llpRequest.end = 0;

            purgeDynamicPaoInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestBegin);
            purgeDynamicPaoInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestEnd);

            OutMessPtrDeleter d(OutMessage);

            return appendMsgTo(retList, makeReturnMsg(
                        cancelled
                            ? "Load profile request cancelled"
                            : "No active load profile requests to cancel",
                        ClientErrors::None,
                        OutMessage->Request));
        }
        else if( cmd == "resume" )
        {
            OutMessPtrDeleter d(OutMessage);

            return appendMsgTo(retList, makeLpResumeRequest(*OutMessage));
        }
        else
        {
            int request_channel = parse.getiValue("lp_channel");

            request_channel--;

            if( request_channel <  0 ||
                request_channel >= LPChannels )
            {
                OutMessPtrDeleter d(OutMessage);

                return appendMsgTo(retList, makeReturnMsg(
                            "Bad channel specification - Acceptable values:  1-4",
                            ClientErrors::NoMethod,
                            OutMessage->Request));
            }
            else if( needsChannelConfig(request_channel) )
            {
                appendMsgTo(retList, makeReturnMsg(
                    "Command requires channel configuration, but it has not been stored.  Attempting to retrieve it automatically.",
                    ClientErrors::NeedsChannelConfig,
                    OutMessage->Request,
                    ExpectMore::True));

                if( requestChannelConfig(request_channel, *pReq, outList, retList) )
                {
                    OutMessPtrDeleter d(OutMessage);

                    return ExecutionComplete;
                }
            }
            else
            {
                const int interval_len = getLoadProfileInterval(request_channel);

                if( interval_len <= 0 )
                {
                    // The interval length is invalid. We cannot execute.

                    OutMessPtrDeleter d(OutMessage);

                    return appendMsgTo(retList, makeReturnMsg(
                                "Load profile request status:"
                                    "\nChannel " + CtiNumStr(request_channel + 1) + " LP Interval returned " + CtiNumStr(interval_len) + "."
                                    "\nRetrieve the correct LP Interval and attempt the request again.",
                                ClientErrors::MissingConfig,
                                OutMessage->Request));
                }
                else
                {
                    const int block_len = interval_len * 6;

                    //  grab the beginning date
                    CtiTokenizer date_tok(parse.getsValue("lp_date_start"));
                    month = atoi(date_tok("-/").data());
                    day   = atoi(date_tok("-/").data());
                    year  = atoi(date_tok("-/").data());

                    //  note that this code assumes that the current century is 20xx - this will need to change in 2100
                    if( year < 100 )    year += 2000;

                    if( year > 2099 || year < 2000 )
                    {
                        OutMessPtrDeleter d(OutMessage);

                        return appendMsgTo(retList, makeReturnMsg(
                                    "Bad start date \"" + parse.getsValue("lp_date_start") + "\"",
                                    ClientErrors::NoMethod,
                                    OutMessage->Request));
                    }
                    else if( !cmd.compare("lp") )
                    {
                        long candidate_id = 0;

                        while( !candidate_id )
                        {
                            candidate_id = ++_llpRequest.candidate_request_id;
                        }

                        long existing_id = pReq->OptionsField();

                        //  _llpRequest.request_id will be 0 if there is no command in progress, and OptionsField will be 0 if it is the first message
                        if( ! _llpRequest.request_id.compare_exchange_strong(existing_id, candidate_id) )
                        {
                            OutMessPtrDeleter d(OutMessage);

                            return appendMsgTo(retList, makeReturnMsg(
                                        existing_id == 0
                                            ? "Long load profile request was cancelled"
                                            : "Long load profile request already in progress - use \"getvalue lp cancel\" to cancel"
                                                  "\nRequested channel: " + CtiNumStr(_llpRequest.channel + 1) +
                                                  "\nCurrent interval: " + printTimestamp(_llpRequest.begin) +
                                                  "\nEnding interval:  " + printTimestamp(_llpRequest.end),
                                        ClientErrors::Abnormal,
                                        OutMessage->Request));
                        }
                        else
                        {
                            if( ! existing_id )
                            {
                                //  reset the retry count, this is the initial request
                                _llpRequest.retry = 0;
                            }

                            CtiTime time_start, time_end;

                            OutMessage->Sequence = EmetconProtocol::GetValue_LoadProfile;

                            found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);

                            //  save it for later - we'll use it if we have to continue the command
                            OutMessage->Request.OptionsField = candidate_id;

                            //  grab the beginning time, if available
                            if( parse.isKeyValid("lp_time_start") )
                            {
                                CtiTokenizer time_start_tok(parse.getsValue("lp_time_start"));
                                hour   = atoi(time_start_tok(":").data());
                                minute = atoi(time_start_tok(":").data());
                            }
                            else
                            {
                                //  otherwise, default to midnight
                                hour   = 0;
                                minute = 0;
                            }

                            time_start = CtiTime(CtiDate(day, month, year), hour, minute);

                            //  grab the end date, if available
                            if( parse.isKeyValid("lp_date_end") )
                            {
                                CtiTokenizer date_end_tok(parse.getsValue("lp_date_end"));

                                month = atoi(date_end_tok("-/").data());
                                day   = atoi(date_end_tok("-/").data());
                                year  = atoi(date_end_tok("-/").data());
                                //  note that this code assumes that the current century is 20xx - this will need to change in 2100
                                if( year < 100 )    year += 2000;

                                //  grab the end time, if available
                                if( parse.isKeyValid("lp_time_end") )
                                {
                                    CtiTokenizer time_end_tok(parse.getsValue("lp_time_end"));

                                    hour   = atoi(time_end_tok(":").data());
                                    minute = atoi(time_end_tok(":").data());

                                    time_end  = CtiTime(CtiDate(day, month, year), hour, minute);
                                }
                                else
                                {
                                    //  otherwise, default to the end of the day
                                    time_end  = CtiTime(CtiDate(day, month, year));
                                    time_end += 86400;  //  end of the day/beginning of the next day
                                }
                            }
                            else
                            {
                                //  otherwise default to the end of the block
                                time_end  = time_start;

                                if( parse.isKeyValid("lp_time_start") )
                                {
                                    //  did they want a specific time?
                                    time_end += block_len;
                                }
                                else
                                {
                                    //  no time specified, they must've wanted a whole day
                                    time_end += 86400;
                                }
                            }

                            if( !time_start.isValid() || !time_end.isValid() || (time_start >= time_end) )
                            {
                                std::string time_error_string = "Invalid date/time for LP request (" + parse.getsValue("lp_date_start");

                                if( parse.isKeyValid("lp_time_start") )
                                {
                                    time_error_string += " @ " + parse.getsValue("lp_time_start");
                                }

                                if( parse.isKeyValid("lp_date_end") )
                                {
                                    time_error_string += " - ";

                                    time_error_string += parse.getsValue("lp_date_end");

                                    if( parse.isKeyValid("lp_time_end") )
                                    {
                                        time_error_string += " @ " + parse.getsValue("lp_time_end");
                                    }
                                }

                                time_error_string += ")";

                                //  reset, we're not executing any more
                                _llpRequest.request_id.compare_exchange_strong(candidate_id, 0);

                                OutMessPtrDeleter d(OutMessage);

                                return appendMsgTo(retList, makeReturnMsg(
                                            time_error_string,
                                            ClientErrors::BadParameter,
                                            OutMessage->Request));
                            }
                            else
                            {
                                //  align to the beginning of an interval
                                _llpRequest.begin = time_start.seconds() - (time_start.seconds() % interval_len);

                                //  align this to the beginning of an interval as well
                                _llpRequest.end   = time_end.seconds()   - (time_end.seconds()   % interval_len);

                                _llpRequest.channel = request_channel;

                                //  this is the number of seconds from the current pointer
                                unsigned long relative_time = _llpRequest.begin - _llpInterest.time;

                                if( OutMessage->Request.Connection && strstr(OutMessage->Request.CommandStr, " background") )
                                {
                                    appendMsgTo(retList, makeReturnMsg(
                                            "Load profile request submitted for background processing - use \"getvalue lp status\" to check progress",
                                            ClientErrors::None,
                                            OutMessage->Request));

                                    pReq->setConnectionHandle(ConnectionHandle::none);

                                    OutMessage->Priority = 8;
                                    //  make sure the OM doesn't report back to Commander
                                    OutMessage->Request.Connection.reset();
                                }

                                if( (_llpRequest.channel == _llpInterest.channel) &&  //  correct channel
                                    (relative_time < (16 * block_len))        &&  //  within 16 blocks
                                    !(relative_time % block_len) )                //  aligned
                                {
                                    //  it's aligned (and close enough) to the block we're pointing at
                                    OutMessage->Buffer.BSt.Function  = 0x40;
                                    OutMessage->Buffer.BSt.Function += relative_time / block_len;

                                    OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;
                                    OutMessage->Buffer.BSt.Length   = 13;
                                }
                                else if( !strstr(OutMessage->Request.CommandStr, " read") )
                                {
                                    //  we need to set the period of interest, so reuse the request message

                                    std::string interestStr(pReq->CommandString());

                                    replaceString(interestStr, "getvalue",   "putconfig emetcon");
                                    replaceString(interestStr, "lp channel", "llp interest channel");

                                    CtiRequestMsg *interestReq = new CtiRequestMsg(*pReq);

                                    interestReq->setCommandString(interestStr);
                                    interestReq->setOptionsField(candidate_id);
                                    interestReq->setConnectionHandle(pReq->getConnectionHandle());

                                    retList.push_back(interestReq);

                                    OutMessPtrDeleter d(OutMessage);

                                    return appendMsgTo(retList, makeReturnMsg(
                                                "Sending load profile period of interest",
                                                ClientErrors::None,
                                                OutMessage->Request,
                                                ExpectMore::True));
                                }
                                else
                                {
                                    //  trying to read, but we're not aligned - something must've gone wrong
                                    _llpInterest.time    = 0;
                                    _llpInterest.channel = 0;

                                    _llpRequest.begin   = 0;
                                    _llpRequest.end     = 0;
                                    _llpRequest.channel = 0;

                                    //  reset, we're not executing any more
                                    _llpRequest.request_id.compare_exchange_strong(candidate_id, 0);

                                    OutMessPtrDeleter d(OutMessage);

                                    return appendMsgTo(retList, makeReturnMsg(
                                                "Long load profile read setup error",
                                                ClientErrors::InvalidTimestamp,
                                                OutMessage->Request));
                                }

                                const auto blocks = (_llpRequest.end - _llpRequest.begin) / block_len;

                                //  This is parsed by the web server to determine how many messages to expect from us
                                appendMsgTo(retList, makeReturnMsg(
                                        "Reading " + std::to_string(blocks) + (blocks == 1 ? " block" : " blocks"),
                                        ClientErrors::None,
                                        OutMessage->Request,
                                        ExpectMore::True));

                                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_Time,         _llpInterest.time);
                                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_Channel,      _llpInterest.channel + 1);
                                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestBegin, _llpRequest.begin);
                                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestEnd,   _llpRequest.end);

                                return ClientErrors::None;
                            }
                        }
                    }
                    else if( !cmd.compare("peak") )
                    {
                        const CtiDate today = CtiDate();
                        const CtiDate request_date  = CtiDate(day,month,year);
                        const int     request_range = parse.getiValue("lp_range");

                        typedef map<string, char> PeakTypes;

                        static const PeakTypes peakLookup {
                            { PeakString_Day,      FuncRead_LLPPeakDayPos },
                            { PeakString_Hour,     FuncRead_LLPPeakHourPos },
                            { PeakString_Interval, FuncRead_LLPPeakIntervalPos } };

                        PeakTypes::const_iterator peakType = peakLookup.find(parse.getsValue("lp_peaktype"));

                        if( request_date > today )  //  must begin on or before today
                        {
                            OutMessPtrDeleter d(OutMessage);

                            return appendMsgTo(retList, makeReturnMsg(
                                        "Invalid date for peak request: cannot be after today (" + request_date.asStringMDY() + ")",
                                        ClientErrors::InvalidDate,
                                        OutMessage->Request));
                        }
                        else if( request_range <= 0 ||
                                 request_range >= 1000 )
                        {
                            OutMessPtrDeleter d(OutMessage);

                            return appendMsgTo(retList, makeReturnMsg(
                                        "Invalid range for peak request: must be 1-999 (" + CtiNumStr(request_range) + ")",
                                        ClientErrors::BadParameter,
                                        OutMessage->Request));
                        }
                        else if( ! hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec) ||
                                 ! hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision) )
                        {
                            //  we need to read the SSPEC out of the meter
                            executeBackgroundRequest("getconfig model", *OutMessage, outList);

                            OutMessPtrDeleter d(OutMessage);

                            return appendMsgTo(retList, makeReturnMsg(
                                        "SSPEC revision not retrieved yet, attempting to read it automatically; please retry command in a few minutes",
                                        ClientErrors::VerifySSPEC,
                                        OutMessage->Request));
                        }
                        else if( !isSupported(Feature_LoadProfilePeakReport) )
                        {
                            OutMessPtrDeleter d(OutMessage);

                            return appendMsgTo(retList, makeReturnMsg(
                                        "Load profile reporting not supported for this device's SSPEC revision",
                                        ClientErrors::InvalidSSPEC,
                                        OutMessage->Request));
                        }
                        else if( peakType == peakLookup.end() )
                        {
                            OutMessPtrDeleter d(OutMessage);

                            return ClientErrors::NoMethod;
                        }

                        OutMessage->Sequence = EmetconProtocol::GetValue_LoadProfilePeakReport;
                        found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);

                        if( found )
                        {
                            long requestId = pReq->OptionsField();

                            const bool locked =
                                requestId
                                    ? _llpPeakInterest.tryContinueRequest(requestId)
                                    : _llpPeakInterest.tryBeginRequest(requestId);

                            if( ! locked )
                            {
                                OutMessPtrDeleter d(OutMessage);

                                return appendMsgTo(retList, makeReturnMsg(
                                            "Load profile peak request already in progress",
                                            ClientErrors::CommandAlreadyInProgress,
                                            OutMessage->Request));
                            }

                            const unsigned char request_peaktype = peakType->second;

                            const CtiDate request_date = CtiDate(day, month, year);

                            if( _llpPeakInterest.no_overlap &&
                                request_date     == _llpPeakInterest.end_date &&
                                request_channel  == _llpPeakInterest.channel  &&
                                request_range    == _llpPeakInterest.range    &&
                                request_peaktype == _llpPeakInterest.peak_type )
                            {
                                OutMessage->Sequence = EmetconProtocol::GetValue_LoadProfilePeakReport;

                                OutMessage->Buffer.BSt.Function = request_peaktype;
                                OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;
                                OutMessage->Buffer.BSt.Length   = 13;
                            }
                            else
                            {
                                _llpPeakInterest.end_date   = request_date;
                                _llpPeakInterest.channel    = request_channel;
                                _llpPeakInterest.range      = request_range;
                                _llpPeakInterest.peak_type  = request_peaktype;

                                //  We need to verify the existing peak is outside the requested interval
                                //    by reading the existing peak.
                                //  If the existing peak is overlapped by the new range, the decode will attempt to reset the peak.
                                //    If not, it will set no_overlap = true and reissue the read.
                                OutMessage->Sequence = EmetconProtocol::GetConfig_LoadProfileExistingPeak;

                                found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);

                                OutMessage->Buffer.BSt.Function = request_peaktype;
                            }

                            OutMessage->Request.OptionsField = requestId;

                            nRet = ClientErrors::None;
                        }
                    }
                }
            }
        }
    }
    else
    {
        return Parent::executeGetValue(pReq, parse, OutMessage, vgList, retList, outList);
    }

    if( found )
    {
        populateDlcOutMessage(*OutMessage);

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().data(), COMMAND_STR_SIZE);

        nRet = ClientErrors::None;
    }

    return nRet;
}

YukonError_t Mct4xxDevice::executeGetConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    if(parse.isKeyValid("install"))
    {
        const auto readStatus = executeInstallReads(pReq, parse, OutMessage, vgList, retList, outList);
            
        return readStatus;
    }

    if( parse.isKeyValid("tou") )
    {
        if( parse.isKeyValid("tou_schedule") )
        {
            const int schedulenum = parse.getiValue("tou_schedule");

            switch( schedulenum )
            {
                case 1:
                case 2:
                {
                    OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule12Pos;
                    OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule12Len;
                    OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;
                    break;
                }
                case 3:
                case 4:
                {
                    OutMessage->Buffer.BSt.Function = FuncRead_TOUSwitchSchedule34Pos;
                    OutMessage->Buffer.BSt.Length   = FuncRead_TOUSwitchSchedule34Len;
                    OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;
                    break;
                }
                default:
                {
                    retList.push_back(
                            new CtiReturnMsg(
                                    getID( ),
                                    OutMessage->Request,
                                    "invalid schedule number " + CtiNumStr(schedulenum),
                                    ClientErrors::NoMethod));

                    return ClientErrors::NoMethod;
                }
            }
        }
        else
        {
            OutMessage->Buffer.BSt.Function = FuncRead_TOUStatusPos;
            OutMessage->Buffer.BSt.Length   = FuncRead_TOUStatusLen;
            OutMessage->Buffer.BSt.IO       = EmetconProtocol::IO_Function_Read;
        }

        OutMessage->Sequence = EmetconProtocol::GetConfig_TOU;

        populateDlcOutMessage(*OutMessage);

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        return ClientErrors::None;
    }

    return Parent::executeGetConfig(pReq, parse, OutMessage, vgList, retList, outList);
}


YukonError_t Mct4xxDevice::executeGetStatus(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList )
{
    if( parse.isKeyValid("freeze") )
    {
        if( ! getOperation(EmetconProtocol::GetStatus_Freeze, OutMessage->Buffer.BSt) )
        {
            return ClientErrors::NoMethod;
        }

        OutMessage->Sequence = EmetconProtocol::GetStatus_Freeze;

        populateDlcOutMessage(*OutMessage);

        OutMessage->Request.RouteID   = getRouteID();
        strncpy(OutMessage->Request.CommandStr, pReq->CommandString().c_str(), COMMAND_STR_SIZE);

        return ClientErrors::None;
    }

    return Parent::executeGetStatus(pReq, parse, OutMessage, vgList, retList, outList);
}


//  this is for TOU putconfig ease
struct ratechange_t
{
    int schedule;
    int time;
    int rate;

    bool operator<(const ratechange_t &rhs) const
    {
        bool retval = false;

        if( schedule < rhs.schedule || (schedule == rhs.schedule && time < rhs.time) )
        {
            retval = true;
        }

        return retval;
    }
};


YukonError_t Mct4xxDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    return Mct4xxDevice::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList, false);
}


YukonError_t Mct4xxDevice::executeInstallReads(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    return Mct4xxDevice::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList, true);
}


YukonError_t Mct4xxDevice::executePutConfig(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    bool  found = false;
    YukonError_t nRet = ClientErrors::None;

    auto errRet =
        std::make_unique<CtiReturnMsg>(
               getID(),
               OutMessage->Request.CommandStr,
               string(),
               nRet,
               OutMessage->Request.RouteID,
               OutMessage->Request.RetryMacroOffset,
               OutMessage->Request.Attempt,
               OutMessage->Request.GrpMsgID,
               OutMessage->Request.UserID,
               OutMessage->Request.SOE,
               CtiMultiMsg_vec());

    if( parse.isKeyValid("install") )
    {
        found = true;

        if( parse.getsValue("installvalue") == PutConfigPart_basic
            || parse.getsValue("installvalue") == PutConfigPart_all )
        {
            ConfigPartsList tempList = getPartsList();
            const auto ignored = executePutConfigMultiple(tempList, pReq, parse, OutMessage, vgList, retList, outList, readsOnly);
        }
        else
        {
            strncpy(OutMessage->Request.CommandStr, (pReq->CommandString()).c_str(), COMMAND_STR_SIZE);
            const auto ignored = executePutConfigSingle(pReq, parse, OutMessage, vgList, retList, outList, readsOnly);
        }

        incrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle(), outList.size());

        if(OutMessage!=NULL)
        {
            delete OutMessage;
            OutMessage = NULL;
        }
    }
    else if( parse.isKeyValid("holiday_offset") )
    {
        const auto function = EmetconProtocol::PutConfig_Holiday;

        if( found = getOperation(function, OutMessage->Buffer.BSt) )
        {
            unsigned long holidays[3];
            int holiday_count = 0;

            int holiday_offset = parse.getiValue("holiday_offset");

            OutMessage->Sequence = Cti::Protocols::EmetconProtocol::PutConfig_Holiday;

            //  grab up to three potential dates
            for( int i = 0; i < 3 && parse.isKeyValid("holiday_date" + CtiNumStr(i)); i++ )
            {
                CtiDate holiday_date = parseDateString(parse.getsValue("holiday_date" + CtiNumStr(i)));

                if( holiday_date.isValid() && holiday_date > CtiDate::now() )
                {
                    holidays[holiday_count++] = CtiTime(holiday_date).seconds();
                }
            }

            if( holiday_offset >= 1 && holiday_offset <= 3 )
            {
                if( holiday_count > 0 )
                {
                    //  change to 0-based offset;  it just makes things easier
                    holiday_offset--;

                    if( holiday_count > (3 - holiday_offset) )
                    {
                        holiday_count = 3 - holiday_offset;
                    }

                    OutMessage->Buffer.BSt.Function += holiday_offset * 4;
                    OutMessage->Buffer.BSt.Length    = holiday_count  * 4;

                    for( int i = 0; i < holiday_count; i++ )
                    {
                        OutMessage->Buffer.BSt.Message[i*4+0] = holidays[i] >> 24;
                        OutMessage->Buffer.BSt.Message[i*4+1] = holidays[i] >> 16;
                        OutMessage->Buffer.BSt.Message[i*4+2] = holidays[i] >>  8;
                        OutMessage->Buffer.BSt.Message[i*4+3] = holidays[i] >>  0;
                    }
                }
                else
                {
                    found = false;

                    errRet->setResultString("Specified dates are invalid");
                    errRet->setStatus(ClientErrors::NoMethod);
                    retList.push_back(errRet.release());
                }
            }
            else
            {
                found = false;

                errRet->setResultString("Invalid holiday offset specified");
                errRet->setStatus(ClientErrors::NoMethod);
                retList.push_back(errRet.release());
            }
        }
    }
    else if( parse.isKeyValid("tou") )
    {
        if( parse.isKeyValid("tou_enable") )
        {
            const auto function = EmetconProtocol::PutConfig_TOUEnable;
            found = getOperation(function, OutMessage->Buffer.BSt);
            OutMessage->Sequence = function;
        }
        else if( parse.isKeyValid("tou_disable") )
        {
            const auto function = EmetconProtocol::PutConfig_TOUDisable;
            found = getOperation(function, OutMessage->Buffer.BSt);
            OutMessage->Sequence = function;
        }
        else if( parse.isKeyValid("tou_days") )
        {
            set< ratechange_t > ratechanges;
            int default_rate, day_schedules[8];

            string schedule_name, change_name, daytable(parse.getsValue("tou_days"));

            switch( parse.getsValue("tou_default").data()[0] )
            {
                case 'a':   default_rate =  0;  break;
                case 'b':   default_rate =  1;  break;
                case 'c':   default_rate =  2;  break;
                case 'd':   default_rate =  3;  break;
                default:    default_rate = -1;  break;
            }

            if( default_rate < 0 )
            {
                CTILOG_ERROR(dout, "TOU default rate \""<< parse.getsValue("tou_default") <<"\" specified is invalid for device \""<< getName() <<"\"");
            }
            else
            {
                if( daytable.length() < 8 || daytable.find_first_not_of("1234") != string::npos )
                {
                    CTILOG_ERROR(dout, "day table \"" << daytable << "\" specified is invalid for device \"" << getName() << "\"");
                }
                else
                {
                    for( int day = 0; day < 8; day++ )
                    {
                        day_schedules[day] = atoi(daytable.substr(day, 1).data()) - 1;
                    }

                    int schedulenum = 0;
                    schedule_name.assign("tou_schedule_");
                    schedule_name.append(CtiNumStr(schedulenum).zpad(2));
                    while(parse.isKeyValid(schedule_name.data()))
                    {
                        int schedule_number = parse.getiValue(schedule_name.data());

                        if( schedule_number > 0 && schedule_number <= 4 )
                        {
                            int changenum = 0;
                            change_name.assign(schedule_name);
                            change_name.append("_");
                            change_name.append(CtiNumStr(changenum).zpad(2));
                            while(parse.isKeyValid(change_name.data()))
                            {
                                string ratechangestr = parse.getsValue(change_name.data()).data();
                                int rate, hour, minute;

                                switch(ratechangestr.at(0))
                                {
                                    case 'a':   rate =  0;  break;
                                    case 'b':   rate =  1;  break;
                                    case 'c':   rate =  2;  break;
                                    case 'd':   rate =  3;  break;
                                    default:    rate = -1;  break;
                                }

                                hour   = atoi(ratechangestr.substr(2).data());

                                int minute_index = ratechangestr.substr(4).find_first_not_of(":") + 4;
                                minute = atoi(ratechangestr.substr(minute_index).data());

                                if( rate   >= 0 &&
                                    hour   >= 0 && hour   < 24 &&
                                    minute >= 0 && minute < 60 )
                                {
                                    ratechange_t ratechange;

                                    ratechange.schedule = schedule_number - 1;
                                    ratechange.rate = rate;
                                    ratechange.time = hour * 3600 + minute * 60;

                                    ratechanges.insert(ratechange);
                                }
                                else
                                {
                                    CTILOG_ERROR(dout, "schedule \""<< schedule_number <<"\" has invalid rate change \""<< ratechangestr <<"\"for device \""<< getName() <<"\"");
                                }

                                changenum++;
                                change_name.assign(schedule_name);
                                change_name.append("_");
                                change_name.append(CtiNumStr(changenum).zpad(2));
                            }
                        }
                        else
                        {
                            CTILOG_ERROR(dout, "schedule \""<< schedule_number <<"\" specified is out of range for device \""<< getName() << "\"");
                        }

                        schedulenum++;
                        schedule_name.assign("tou_schedule_");
                        schedule_name.append(CtiNumStr(schedulenum).zpad(2));
                    }

                    OUTMESS *TOU_OutMessage1 = CTIDBG_new OUTMESS(*OutMessage),
                            *TOU_OutMessage2 = CTIDBG_new OUTMESS(*OutMessage);

                    TOU_OutMessage1->Sequence = Cti::Protocols::EmetconProtocol::PutConfig_TOU;
                    TOU_OutMessage2->Sequence = Cti::Protocols::EmetconProtocol::PutConfig_TOU;

                    TOU_OutMessage1->Buffer.BSt.Function = FuncWrite_TOUSchedule1Pos;
                    TOU_OutMessage1->Buffer.BSt.Length   = FuncWrite_TOUSchedule1Len;

                    TOU_OutMessage2->Buffer.BSt.Function = FuncWrite_TOUSchedule2Pos;
                    TOU_OutMessage2->Buffer.BSt.Length   = FuncWrite_TOUSchedule2Len;

                    TOU_OutMessage1->Buffer.BSt.IO = Cti::Protocols::EmetconProtocol::IO_Function_Write;
                    TOU_OutMessage2->Buffer.BSt.IO = Cti::Protocols::EmetconProtocol::IO_Function_Write;

                    std::set< ratechange_t >::iterator itr;

                    //  There's much more intelligence and safeguarding that could be added to the below,
                    //    but it's a temporary fix, to be handled soon by the proper MCT Configs,
                    //    so I don't think it's worth it at the moment to add all of the smarts.
                    //  We'll handle a good string, and kick out on anything else.

                    int durations[4][5], rates[4][6];
                    for( int i = 0; i < 4; i++ )
                    {
                        for( int j = 0; j < 6; j++ )
                        {
                            if( j < 5 )
                            {
                                durations[i][j] = 255;
                            }

                            rates[i][j] = default_rate;
                        }
                    }

                    int current_schedule = -1;
                    int offset = 0, time_offset = 0;
                    for( itr = ratechanges.begin(); itr != ratechanges.end(); itr++ )
                    {
                        const ratechange_t &rc = *itr;

                        if( rc.schedule != current_schedule )
                        {
                            offset      = 0;
                            time_offset = 0;

                            current_schedule = rc.schedule;
                        }
                        else
                        {
                            offset++;
                        }

                        if( offset > 5 )
                        {
                            CTILOG_ERROR(dout, "invalid offset")
                            continue;
                        }

                        if( rc.schedule < 0 || rc.schedule > 3 )
                        {
                            CTILOG_ERROR(dout, "invalid schedule");
                            continue;
                        }

                        if( offset == 0 && rc.time == 0 )
                        {
                            //  this is a special case, because we can't access
                            //    durations[rc.schedule][offset-1] yet - offset isn't 1 yet

                            rates[rc.schedule][0] = rc.rate;
                        }
                        else
                        {
                            if( offset == 0 )
                            {
                                CTILOG_WARN(dout, "first rate change time for schedule ("<< rc.schedule <<") is not midnight, assuming default rate ("<< default_rate <<") for midnight until ("<< rc.time <<") for device \""<< getName() <<"\"");

                                //  rates[rc.schedule][0] was already initialized to default_rate, so just move along
                                offset++;
                            }

                            durations[rc.schedule][offset - 1] = (rc.time - time_offset) / 300;
                            rates[rc.schedule][offset] = rc.rate;

                            time_offset = rc.time - (rc.time % 300);  //  make sure we don't miss the 5-minute marks
                        }

                        if( (offset + 1) <= 5 )
                        {
                            //  this is to work around the 255 * 5 min limitation for switches - this way it doesn't
                            //    jump back to the default rate if only a midnight rate is specified
                            rates[rc.schedule][offset + 1] = rc.rate;
                        }
                    }


                    for( offset = 0; offset < 8; offset++ )
                    {
                        //  write the day table

                        int byte = 1 - (offset / 4);
                        int bitoffset = (2 * offset) % 8;

                        TOU_OutMessage1->Buffer.BSt.Message[byte] |= (day_schedules[offset] & 0x03) << bitoffset;
                    }

                    for( offset = 0; offset < 5; offset++ )
                    {
                        //  write the durations

                        TOU_OutMessage1->Buffer.BSt.Message[offset + 2] = durations[0][offset];
                        TOU_OutMessage1->Buffer.BSt.Message[offset + 9] = durations[1][offset];

                        TOU_OutMessage2->Buffer.BSt.Message[offset + 0] = durations[2][offset];
                        TOU_OutMessage2->Buffer.BSt.Message[offset + 7] = durations[3][offset];
                    }

                    //  write the rates for schedules 1 and 2
                    TOU_OutMessage1->Buffer.BSt.Message[7]  = ((rates[1][5] & 0x03)  << 6) |
                                                              ((rates[1][4] & 0x03)  << 4) |
                                                              ((rates[0][5] & 0x03)  << 2) |
                                                              ((rates[0][4] & 0x03)  << 0);

                    TOU_OutMessage1->Buffer.BSt.Message[8]  = ((rates[0][3] & 0x03)  << 6) |
                                                              ((rates[0][2] & 0x03)  << 4) |
                                                              ((rates[0][1] & 0x03)  << 2) |
                                                              ((rates[0][0] & 0x03)  << 0);

                    TOU_OutMessage1->Buffer.BSt.Message[14] = ((rates[1][3] & 0x03)  << 6) |
                                                              ((rates[1][2] & 0x03)  << 4) |
                                                              ((rates[1][1] & 0x03)  << 2) |
                                                              ((rates[1][0] & 0x03)  << 0);

                    //  write the rates for schedule 3
                    TOU_OutMessage2->Buffer.BSt.Message[5]  = ((rates[2][5] & 0x03)  << 2) |
                                                              ((rates[2][4] & 0x03)  << 0);

                    TOU_OutMessage2->Buffer.BSt.Message[6]  = ((rates[2][3] & 0x03)  << 6) |
                                                              ((rates[2][2] & 0x03)  << 4) |
                                                              ((rates[2][1] & 0x03)  << 2) |
                                                              ((rates[2][0] & 0x03)  << 0);

                    //  write the rates for schedule 4
                    TOU_OutMessage2->Buffer.BSt.Message[12] = ((rates[3][5] & 0x03)  << 2) |
                                                              ((rates[3][4] & 0x03)  << 0);

                    TOU_OutMessage2->Buffer.BSt.Message[13] = ((rates[3][3] & 0x03)  << 6) |
                                                              ((rates[3][2] & 0x03)  << 4) |
                                                              ((rates[3][1] & 0x03)  << 2) |
                                                              ((rates[3][0] & 0x03)  << 0);

                    TOU_OutMessage2->Buffer.BSt.Message[14] = default_rate;

                    outList.push_back(TOU_OutMessage2);
                    outList.push_back(TOU_OutMessage1);

                    TOU_OutMessage1 = 0;
                    TOU_OutMessage2 = 0;

                    delete OutMessage;  //  we didn't use it, we made our own
                    OutMessage = 0;

                    found = true;
                }
            }
        }
    }
    else if( parse.isKeyValid("timezone_offset") ||
             parse.isKeyValid("timezone_name") )
    {
        if( found = getOperation(EmetconProtocol::PutConfig_TimeZoneOffset, OutMessage->Buffer.BSt) )
        {
            int timezone_blocks = 0;

            if( parse.isKeyValid("timezone_offset") )
            {
                double timezone_offset = parse.getdValue("timezone_offset", -999.0);

                timezone_blocks = (int)(timezone_offset * 4.0);
            }
            if( parse.isKeyValid("timezone_name") )
            {
                string timezone_name = parse.getsValue("timezone_name");

                if( !timezone_name.empty() )
                {
                    switch( timezone_name.at(0) )
                    {
                        case 'h':   timezone_blocks = -10 * 4;   break;  //  hawaiian time
                        case 'a':   timezone_blocks =  -9 * 4;   break;  //  alaskan time
                        case 'p':   timezone_blocks =  -8 * 4;   break;  //  pacific time
                        case 'm':   timezone_blocks =  -7 * 4;   break;  //  mountain time
                        case 'c':   timezone_blocks =  -6 * 4;   break;  //  central time
                        case 'e':   timezone_blocks =  -5 * 4;   break;  //  eastern time
                    }
                }
                else
                {
                    CTILOG_ERROR(dout, "timezone name empty");
                }
            }

            OutMessage->Sequence = EmetconProtocol::PutConfig_TimeZoneOffset;
            OutMessage->Buffer.BSt.Message[0] = timezone_blocks;
        }
        else
        {
            nRet = ClientErrors::NoMethod;
        }
    }
    else if( parse.isKeyValid("llp interest channel") )
    {
        unsigned request_channel = parse.getiValue("llp interest channel");

        CtiTokenizer date_tok(parse.getsValue("llp interest date"));
        int month = atoi(date_tok("-/").data());
        int day   = atoi(date_tok("-/").data());
        int year  = atoi(date_tok("-/").data());

        //  note that this code assumes that the current century is 20xx - this will need to change in 2100
        if( year < 100 ) year += 2000;

        CtiTokenizer time_tok(parse.getsValue("llp interest time"));
        int hour   = atoi(time_tok(":").data());  //  if this is empty, it returns zeroes, which is fine
        int minute = atoi(time_tok(":").data());  //  if this is empty, it returns zeroes, which is fine

        CtiTime interest_time(CtiDate(day, month, year), hour, minute);

        const auto function = EmetconProtocol::PutConfig_LoadProfileInterest;

        found = getOperation(function, OutMessage->Buffer.BSt);

        const int interval_len = getLoadProfileInterval(request_channel - 1);

        long existing_id = pReq->OptionsField();

        // Only do things if our interval length is valid!
        if( interval_len <= 0 )
        {
            //  reset, we're not executing any more
            _llpRequest.request_id.compare_exchange_strong(existing_id, 0);

            string temp =
                getName() + " / Load profile request status: \n"
                "Channel " + CtiNumStr(request_channel) + " LP Interval returned " + CtiNumStr(interval_len) + ".\n"
                "Retrieve the correct LP Interval and attempt the request again.";

            insertReturnMsg(ClientErrors::NeedsChannelConfig, OutMessage, retList, temp);

            nRet = ExecutionComplete;
        }
        else if( ! is_valid_time(interest_time) )
        {
            //  reset, we're not executing any more
            _llpRequest.request_id.compare_exchange_strong(existing_id, 0);

            string temp = "Bad start time \"" + parse.getsValue("llp interest date") + " " + parse.getsValue("llp interest time") + "\"";

            insertReturnMsg(ClientErrors::InvalidDate, OutMessage, retList, temp);

            nRet = ExecutionComplete;
        }
        else if( !request_channel || request_channel > LPChannels )
        {
            //  reset, we're not executing any more
            _llpRequest.request_id.compare_exchange_strong(existing_id, 0);

            insertReturnMsg(ClientErrors::BadParameter, OutMessage, retList, "Bad channel \"" + CtiNumStr(request_channel) + "\"");

            nRet = ExecutionComplete;
        }
        else
        {
            //  align to the beginning of an interval
            interest_time -= interest_time.seconds() % interval_len;

            _llpInterest.time    = interest_time.seconds();
            _llpInterest.channel = request_channel - 1;

            unsigned long interval_beginning_time = _llpInterest.time - interval_len;

            OutMessage->Sequence = function;

            //  this request came from a "getvalue lp" command, so make sure to report ExpectMore during Route->ExecuteRequest()
            if( OutMessage->Request.OptionsField = pReq->OptionsField() )
            {
                OutMessage->MessageFlags |= MessageFlag_ExpectMore;
            }

            OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

            OutMessage->Buffer.BSt.Message[1] = request_channel;

            OutMessage->Buffer.BSt.Message[2] = interval_beginning_time >> 24;
            OutMessage->Buffer.BSt.Message[3] = interval_beginning_time >> 16;
            OutMessage->Buffer.BSt.Message[4] = interval_beginning_time >>  8;
            OutMessage->Buffer.BSt.Message[5] = interval_beginning_time;
        }
    }
    else if( parse.isKeyValid("llp peak interest channel") )
    {
        typedef llp_peak_report_interest_t llp_pri;

        unsigned request_channel = parse.getiValue("llp peak interest channel");
        unsigned request_range   = parse.getiValue("llp peak interest range");

        CtiTokenizer request_date_tok(parse.getsValue("llp peak interest date"));
        int month = atoi(request_date_tok("-/").data());
        int day   = atoi(request_date_tok("-/").data());
        int year  = atoi(request_date_tok("-/").data());

        CtiDate request_date(day, month, year);

        long requestId = pReq->OptionsField();

        if( ! _llpPeakInterest.tryContinueRequest(requestId) )
        {
            //  This is internal-only, so NoMethod is true for anyone requesting this outside of a getvalue lp peak command
            return ClientErrors::NoMethod;
        }
        else if( request_date < DawnOfTime_Date )
        {
            std::stringstream error;

            error << getName() << " / Load profile peak report status: \n";
            error << "Bad date specified: " << parse.getsValue("llp peak interest date");

            insertReturnMsg(ClientErrors::NeedsChannelConfig, OutMessage, retList, error.str());

            _llpPeakInterest.tryEndRequest(requestId);

            nRet = ExecutionComplete;
        }
        else
        {
            OutMessage->Sequence = EmetconProtocol::PutConfig_LoadProfileReportPeriod;

            found = getOperation(OutMessage->Sequence, OutMessage->Buffer.BSt);

            OutMessage->Request.OptionsField = requestId;

            //  End of the day
            const long utc_time = CtiTime(request_date + 1).seconds();

            OutMessage->MessageFlags |= MessageFlag_ExpectMore;

            OutMessage->Buffer.BSt.Message[0] = gMCT400SeriesSPID;

            OutMessage->Buffer.BSt.Message[1] = request_channel;

            OutMessage->Buffer.BSt.Message[2] = utc_time >> 24;
            OutMessage->Buffer.BSt.Message[3] = utc_time >> 16;
            OutMessage->Buffer.BSt.Message[4] = utc_time >>  8;
            OutMessage->Buffer.BSt.Message[5] = utc_time;

            if( request_range <= 0xff )
            {
                OutMessage->Buffer.BSt.Message[6] = request_range & 0xff;

                OutMessage->Buffer.BSt.Message[7] = 0;
                OutMessage->Buffer.BSt.Message[8] = 0;
            }
            else
            {
                OutMessage->Buffer.BSt.Message[6] = 0;

                OutMessage->Buffer.BSt.Message[7] = (request_range >> 8) & 0xff;
                OutMessage->Buffer.BSt.Message[8] = (request_range     ) & 0xff;
            }

            if( ! _llpPeakInterest.no_overlap )
            {
                _llpPeakInterest.tryEndRequest(requestId);
            }
        }
    }

    if( !found )
    {
        nRet = Parent::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}


YukonError_t Mct4xxDevice::executePutValue(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    bool  found = false;
    YukonError_t nRet = ClientErrors::None;

    if( parse.isKeyValid("reset") && parse.isKeyValid("tou") )
    {
        found = getOperation(EmetconProtocol::PutValue_TOUReset, OutMessage->Buffer.BSt);

        if( parse.isKeyValid("tou_zero") )
        {
            OutMessage->Buffer.BSt.Function = Command_TOUResetZero;
        }

        OutMessage->Sequence = EmetconProtocol::PutValue_TOUReset;

        if( !found )
        {
            nRet = ClientErrors::NoMethod;
        }
    }
    else
    {
        nRet = Parent::executePutValue(pReq, parse, OutMessage, vgList, retList, outList);
    }

    return nRet;
}


int Mct4xxDevice::executePutConfigMultiple(ConfigPartsList &partsList,
                                           CtiRequestMsg *pReq,
                                           CtiCommandParser &parse,
                                           OUTMESS *&OutMessage,
                                           CtiMessageList &vgList,
                                           CtiMessageList &retList,
                                           OutMessageList &outList,
                                           bool readsOnly)
{
    const bool isForce = parse.isKeyValid("force");
    const bool isVerify = parse.isKeyValid("verify");

    int ret = ClientErrors::NoMethod;
    std::vector<std::string> nonCurrentConfigParts;
    std::vector<std::string> missingConfigParts;

    if (getDeviceConfig())
    {
        if(!partsList.empty())
        {
            ret = ClientErrors::None;
            CtiRequestMsg tempReq(*pReq);

            populateDlcOutMessage(*OutMessage);
            OutMessage->Priority  = MAXPRIORITY-4;//standard seen in rest of devices.
            OutMessage->Sequence = Protocols::EmetconProtocol::PutConfig_Install;  //  this will be handled by the putconfig decode - basically, a no-op
            OutMessage->Request.RouteID   = getRouteID();

            for( const auto configPart : partsList )
            {
                if( configPart != PutConfigPart_all)  //  preventing infinite loop
                {
                    string tempString = "putconfig install ";
                    tempString += configPart;
                    if( isForce )
                    {
                        tempString += " force";
                    }
                    else if( isVerify )
                    {
                        tempString += " verify";
                    }
                    tempReq.setCommandString(tempString);
                    tempReq.setConnectionHandle(pReq->getConnectionHandle());

                    CtiCommandParser parseSingle(tempReq.CommandString());
                    if( auto retCode = executePutConfigSingle(&tempReq, parseSingle, OutMessage, vgList, retList, outList, readsOnly ) )
                    {
                        if ( retCode == ClientErrors::NoConfigData )
                        {
                            missingConfigParts.push_back(configPart);
                            ret = ClientErrors::NoConfigData;
                        }
                        else
                        {
                            nonCurrentConfigParts.push_back(configPart);
                            ret = ClientErrors::ConfigNotCurrent;
                        }
                    }
                }
            }
        }
    }
    else
    {
        CTILOG_ERROR(dout, "Device "<< getName() <<" will not be configured since it is not assigned to a configuration");

        CtiReturnMsg * retMsg = CTIDBG_new CtiReturnMsg(getID( ),
                                string(OutMessage->Request.CommandStr),
                                "ERROR: Device not assigned to a config.",
                                ClientErrors::NoConfigData,
                                OutMessage->Request.RouteID,
                                OutMessage->Request.RetryMacroOffset,
                                OutMessage->Request.Attempt,
                                OutMessage->Request.GrpMsgID,
                                OutMessage->Request.UserID,
                                OutMessage->Request.SOE,
                                CtiMultiMsg_vec( ));

        retList.push_back( retMsg );
    }

    if( ret )
    {
        // return the more serious error despite what is in 'ret' as its current value, as it is completely
        //  configparts order dependent in the case of multiple errors.

        std::string errorMessage;

        if ( ! missingConfigParts.empty() )
        {
            CTILOG_ERROR(dout, "Device " << getName() << " has an incomplete configuration.");

            ret = ClientErrors::NoConfigData;
            errorMessage = "ERROR: Config Part(s) " + boost::join(missingConfigParts, ", ") + " are incomplete.";
        }
        else
        {
            CTILOG_ERROR(dout, "Device " << getName() << " has non-current configuration.");

            errorMessage = "ERROR: Config Part(s) " + boost::join(nonCurrentConfigParts, ", ") + " not current.";
        }

        auto retMsg = std::make_unique<CtiReturnMsg>(
            getID( ),
            string(OutMessage->Request.CommandStr),
            errorMessage,
            ret,
            OutMessage->Request.RouteID,
            OutMessage->Request.RetryMacroOffset,
            OutMessage->Request.Attempt,
            OutMessage->Request.GrpMsgID,
            OutMessage->Request.UserID,
            OutMessage->Request.SOE );

        retList.push_back( retMsg.release() );
    }

    if( isVerify )
    {
        decrementGroupMessageCount(pReq->UserMessageId(), pReq->getConnectionHandle());
    }

    return ret;
}

int Mct4xxDevice::executePutConfigSingle(CtiRequestMsg *pReq,
                                         CtiCommandParser &parse,
                                         OUTMESS *&OutMessage,
                                         CtiMessageList &vgList,
                                         CtiMessageList &retList,
                                         OutMessageList &outList,
                                         bool readsOnly)
{
    populateDlcOutMessage(*OutMessage);
    OutMessage->Priority  = MAXPRIORITY-4;//standard seen in rest of devices.
    OutMessage->Sequence = Cti::Protocols::EmetconProtocol::PutConfig_Install;  //  this will be handled by the putconfig decode - basically, a no-op
    OutMessage->Request.RouteID   = getRouteID();

    string installValue = parse.getsValue("installvalue");

    int nRet = ClientErrors::None;

    using PutConfigMethod = YukonError_t (Self::*)(CtiRequestMsg *, CtiCommandParser &, OUTMESS *&, CtiMessageList &, CtiMessageList &, OutMessageList &, bool);

    static const auto PutConfigMethods = std::map<std::string, PutConfigMethod>{
        { PutConfigPart_addressing,              &Self::executePutConfigInstallAddressing},
        { PutConfigPart_configbyte,              &Self::executePutConfigConfigurationByte},
        { PutConfigPart_demand_lp,               &Self::executePutConfigDemandLP},
        { PutConfigPart_disconnect,              &Self::executePutConfigInstallDisconnect},
        { PutConfigPart_display,                 &Self::executePutConfigDisplay},
        { PutConfigPart_dst,                     &Self::executePutConfigInstallDST},
        { PutConfigPart_freeze_day,              &Self::executePutConfigInstallFreezeDay},
        { PutConfigPart_lpchannel,               &Self::executePutConfigLoadProfileChannel},
        { PutConfigPart_meter_parameters,        &Self::executePutConfigMeterParameters},
        { PutConfigPart_phaseloss,               &Self::executePutConfigInstallPhaseLoss},
        { PutConfigPart_precanned_table,         &Self::executePutConfigPrecannedTable},
        { PutConfigPart_relays,                  &Self::executePutConfigRelays},
        { PutConfidPart_spid,                    &Self::executePutConfigSpid},
        { PutConfigPart_time_adjust_tolerance,   &Self::executePutConfigTimeAdjustTolerance},
        { PutConfigPart_timezone,                &Self::executePutConfigInstallTimezone},
        { PutConfigPart_tou,                     &Self::executePutConfigTOU}
    };

    if( const auto putConfigMethod = mapFind(PutConfigMethods, installValue) )
    {
        nRet = (this->**putConfigMethod)(pReq, parse, OutMessage, vgList, retList, outList, readsOnly);
    }
    else
    {   //Not sure if this is correct, this could just return NoMethod. This is here
        //just in case anyone wants to use a putconfig install  for anything but configs.
        //nRet = Parent::executePutConfig(pReq, parse, OutMessage, vgList, retList, outList);
        nRet = ClientErrors::NoMethod;
    }

    if( nRet )
    {
        std::string resultString;

        if( nRet == ClientErrors::NoConfigData )
        {
            resultString = "ERROR: Invalid config data. Config name:" + installValue;

            CTILOG_ERROR(dout, "Device "<< getName() <<" had no configuration for config: "<< installValue);
        }
        else if( nRet == ClientErrors::ConfigCurrent )
        {
            resultString = "Config " + installValue + " is current.";
            nRet = ClientErrors::None; //This is an OK return! Note that nRet is no longer propogated!
        }
        else if( nRet == ClientErrors::ConfigNotCurrent )
        {
            resultString = "Config " + installValue + " is NOT current.";
        }
        else
        {
            resultString = "ERROR: NoMethod or invalid config. Config name:" + installValue;

            CTILOG_ERROR(dout, "Device "<< getName() <<" had a configuration error using config "<< installValue);
        }

        CtiReturnMsg * retMsg = CTIDBG_new CtiReturnMsg(getID( ),
                                string(OutMessage->Request.CommandStr),
                                resultString,
                                nRet,
                                OutMessage->Request.RouteID,
                                OutMessage->Request.RetryMacroOffset,
                                OutMessage->Request.Attempt,
                                OutMessage->Request.GrpMsgID,
                                OutMessage->Request.UserID,
                                OutMessage->Request.SOE,
                                CtiMultiMsg_vec( ));

        retList.push_back( retMsg );
    }

    return nRet;
}


YukonError_t Mct4xxDevice::decodePutConfig(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    string resultString;

    std::unique_ptr<CtiReturnMsg> ReturnMsg(CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr));

    switch( InMessage.Sequence )
    {
        case EmetconProtocol::PutConfig_Install:
        {
            if(InMessage.Buffer.DSt.Length>0)
            {
                resultString = "Config data received: ";
                for(int i = 0; i<InMessage.Buffer.DSt.Length;i++)
                {
                    resultString.append( (CtiNumStr(InMessage.Buffer.DSt.Message[i]).hex().zpad(2)).toString(),0,2);
                }
            }
            ReturnMsg->setUserMessageId(InMessage.Return.UserID);
            ReturnMsg->setResultString( resultString );

            // note that at the moment the only putconfig command with a group message count is "putconfig install all".
            decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);
            if (InMessage.MessageFlags & MessageFlag_ExpectMore || getGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection)!=0)
            {
                ReturnMsg->setExpectMore(true);
            }

            retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList );

            break;
        }

        case EmetconProtocol::PutConfig_LoadProfileReportPeriod:
        {
            long requestId = InMessage.Return.OptionsField;

            if( ! _llpPeakInterest.tryContinueRequest(requestId) )
            {
                //  Someone else got here first, just disappear.
                //    Multiple putconfig decodes are expected when broadcast on a macro with multiple subroutes.
                return ClientErrors::None;
            }

            const int interval_len = getLoadProfileInterval(_llpPeakInterest.channel);

            ReturnMsg->setUserMessageId(InMessage.Return.UserID);
            ReturnMsg->setConnectionHandle(InMessage.Return.Connection);

            if( interval_len <= 0 )
            {
                status = ClientErrors::MissingConfig;

                resultString =
                    getName() + " / Channel " + CtiNumStr(_llpPeakInterest.channel + 1)
                    + " LP Interval returned " + CtiNumStr(interval_len) + ".\n"
                    "Verify the LP Interval and attempt the request again.";

                ReturnMsg->setResultString(resultString);

                retMsgHandler(InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList);

                _llpPeakInterest.tryEndRequest(requestId);
            }
            else
            {
                int delay = getUsageReportDelay(interval_len, _llpPeakInterest.range);

                if( delay > 2 && !strstr(InMessage.Return.CommandStr, " noqueue") )
                {
                    //  take two seconds off if it's queued
                    delay -= 2;
                }

                std::stringstream request;

                typedef map<char, string> PeakStrings;

                static const PeakStrings peakLookup {
                    { FuncRead_LLPPeakDayPos,      PeakString_Day },
                    { FuncRead_LLPPeakHourPos,     PeakString_Hour },
                    { FuncRead_LLPPeakIntervalPos, PeakString_Interval } };

                PeakStrings::const_iterator peakString = peakLookup.find(_llpPeakInterest.peak_type);

                //  EmetconProtocol::GetValue_LoadProfilePeakReport
                request << "getvalue lp peak " << peakString->second;
                request << " channel " << _llpPeakInterest.channel + 1;
                request << " " << _llpPeakInterest.end_date.asStringMDY();
                request << " " << _llpPeakInterest.range;

                if( strstr(InMessage.Return.CommandStr, " noqueue") )
                {
                    request << " noqueue";
                }

                CtiRequestMsg *newReq = new CtiRequestMsg(getID(),
                                                          request.str(),
                                                          InMessage.Return.UserID,
                                                          InMessage.Return.GrpMsgID,
                                                          0,  //  Do not specify a routeid, PIL will grab it from the device
                                                          MacroOffset::none,  //  Do not specify a macro offset, PIL will calculate it
                                                          0,
                                                          requestId,  //  smuggle the request ID in OptionsField
                                                          InMessage.Priority);

                newReq->setConnectionHandle(InMessage.Return.Connection);

                //  set it to execute in the future
                newReq->setMessageTime(CtiTime::now().seconds() + delay);

                retList.push_back(newReq);

                resultString =
                    getName() + " / delaying " + CtiNumStr(delay)
                    + " seconds for device peak report processing (until "
                    + (CtiTime::now() + delay).asString() + ")";

                ReturnMsg->setExpectMore(true);

                ReturnMsg->setResultString(resultString);

                retMsgHandler(InMessage.Return.CommandStr, status, ReturnMsg.release(), vgList, retList);
            }

            break;
        }

        case EmetconProtocol::PutConfig_LoadProfileInterest:
        {
            //  was this part of a long load profile read?
            if( InMessage.Return.OptionsField )
            {
                if( InMessage.Return.OptionsField == _llpRequest.request_id )
                {
                    std::string lp_read(InMessage.Return.CommandStr);

                    if( containsString(lp_read, "putconfig emetcon") &&
                        containsString(lp_read, "llp interest channel") )
                    {
                        replaceString(lp_read, "putconfig emetcon",    "getvalue");
                        replaceString(lp_read, "llp interest channel", "lp channel");

                        //  now do the actual read
                        CtiRequestMsg *newReq = new CtiRequestMsg(getID(),
                                                                  lp_read,
                                                                  InMessage.Return.UserID,
                                                                  InMessage.Return.GrpMsgID,
                                                                  getRouteID(),  //  make sure that we start over on any macro routes
                                                                  MacroOffset::none,  //  this will be recalculated by PIL
                                                                  0,
                                                                  InMessage.Return.OptionsField,  //  make sure to include the OptionsField to convey the request ID
                                                                  InMessage.Priority);

                        newReq->setConnectionHandle(InMessage.Return.Connection);
                        newReq->setCommandString(newReq->CommandString() + " read");

                        if( getType() == TYPEMCT470 || isMct430(getType()) )
                        {
                            unsigned fixed_delay = gConfigParms.getValueAsULong("PORTER_MCT470_LLP_READ_DELAY", 45);

                            //  set it to execute in the future
                            if( !strstr(InMessage.Return.CommandStr, " noqueue") )
                            {
                                //  take two seconds off if it's queued
                                newReq->setMessageTime(CtiTime::now().seconds() + fixed_delay - 2);
                            }
                            else
                            {
                                newReq->setMessageTime(CtiTime::now().seconds() + fixed_delay);
                            }

                            ReturnMsg->setUserMessageId(InMessage.Return.UserID);
                            ReturnMsg->setConnectionHandle(InMessage.Return.Connection);
                            ReturnMsg->setResultString(getName() + " / delaying " + CtiNumStr(fixed_delay) + " seconds for IED LP scan (until " + (CtiTime::now() + fixed_delay).asString() + ")");

                            retMsgHandler(InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg.release(), vgList, retList, true);
                        }

                        retList.push_back(newReq);
                    }
                    else
                    {
                        string error_string;

                        error_string += getName();
                        error_string += " / period of interest sent, but cannot continue LLP read - command string \"";
                        error_string += lp_read;
                        error_string += "\" does not contain \"putconfig emetcon\" and \"lp channel\"";

                        ReturnMsg->setResultString(error_string);

                        retMsgHandler(InMessage.Return.CommandStr, ClientErrors::BadParameter, ReturnMsg.release(), vgList, retList);
                    }
                }
            }
            else
            {
                ReturnMsg->setResultString(getName() + " / period of interest sent");

                retMsgHandler(InMessage.Return.CommandStr, ClientErrors::None, ReturnMsg.release(), vgList, retList);
            }

            break;
        }

        default:
        {
            status = Parent::decodePutConfig(InMessage,TimeNow,vgList,retList,outList);
            break;
        }
    }

    return status;
}


using namespace Config;

void Mct4xxDevice::insertConfigReadOutMessage(const char *commandString, const OUTMESS &outMessageTemplate, OutMessageList &outList)
{
    OUTMESS *readOutMessage = new OUTMESS(outMessageTemplate);
    readOutMessage->Priority         -= 1;//decrease for read. Only want read after a successful write.
    strcpy(readOutMessage->Request.CommandStr, commandString);
    if( strstr(outMessageTemplate.Request.CommandStr, "noqueue") )
    {
        strcat(readOutMessage->Request.CommandStr, " noqueue");
    }
    outList.push_back(readOutMessage);
}

YukonError_t Mct4xxDevice::executePutConfigRelays (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

YukonError_t Mct4xxDevice::executePutConfigDemandLP (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

YukonError_t Mct4xxDevice::executePutConfigLoadProfileChannel (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

YukonError_t Mct4xxDevice::executePutConfigDisplay (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

YukonError_t Mct4xxDevice::executePutConfigInstallPhaseLoss (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

YukonError_t Mct4xxDevice::executePutConfigInstallAddressing (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

YukonError_t Mct4xxDevice::executePutConfigInstallDST (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

YukonError_t Mct4xxDevice::executePutConfigMeterParameters(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

YukonError_t Mct4xxDevice::executePutConfigInstallDisconnect(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

YukonError_t Mct4xxDevice::executePutConfigInstallFreezeDay(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

YukonError_t Mct4xxDevice::executePutConfigConfigurationByte(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

YukonError_t Mct4xxDevice::executePutConfigInstallTimezone(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if( ! deviceConfig )
    {
        return ClientErrors::NoConfigData;
    }

    if( ! readsOnly )
    {
        const boost::optional<long> timezoneOffset =
                deviceConfig->findValue<long>(MCTStrings::TimeZoneOffset, timezoneMap);

        if( ! getOperation(EmetconProtocol::PutConfig_TimeZoneOffset, OutMessage->Buffer.BSt) )
        {
            CTILOG_ERROR(dout, "Operation PutConfig_TimeZoneOffset not found");

            return ClientErrors::NoMethod;
        }

        if( ! timezoneOffset )
        {
            CTILOG_ERROR(dout, "no or bad timezoneOffset value stored");

            return ClientErrors::NoConfigData;
        }

        const unsigned char timezoneByte = *timezoneOffset * 4; //The timezone offset in the mct is in 15 minute increments.

        if( ! parse.isKeyValid("force")
            && CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeZoneOffset) == timezoneByte)
        {
            return ClientErrors::ConfigCurrent;
        }

        if( parse.isKeyValid("verify") )
        {
            return ClientErrors::ConfigNotCurrent;
        }

        //  the bstruct IO is set above by getOperation()
        OutMessage->Buffer.BSt.Message[0] = timezoneByte;
        outList.push_back( new OUTMESS(*OutMessage) );
    }

    if( getOperation(EmetconProtocol::PutConfig_TimeZoneOffset, OutMessage->Buffer.BSt) )
    {
        OutMessage->Buffer.BSt.IO = EmetconProtocol::IO_Read;

        insertConfigReadOutMessage("getconfig install timezone", *OutMessage, outList);
    }

    return ClientErrors::None;
}

YukonError_t Mct4xxDevice::executePutConfigSpid(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    YukonError_t nRet = ClientErrors::None;
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if(deviceConfig)
    {
        if( ! readsOnly )
        {
            const boost::optional<long>
                spid = deviceConfig->findValue<long>(MCTStrings::ServiceProviderID);

            if(!getOperation(EmetconProtocol::PutConfig_SPID, OutMessage->Buffer.BSt))
            {
                CTILOG_ERROR(dout, "Operation PutConfig_SPID not found");

                nRet = ClientErrors::NoConfigData;
            }
            else if( ! spid )
            {
                CTILOG_ERROR(dout, "no or bad spid value stored");

                nRet = ClientErrors::NoConfigData;
            }
            else
            {

                if(parse.isKeyValid("force")
                   || CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_AddressServiceProviderID) != spid)
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        //the bstruct IO is set above by getOperation()
                        OutMessage->Buffer.BSt.Message[0] = *spid;
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        nRet = ClientErrors::None;
                    }
                    else
                    {
                        nRet = ClientErrors::ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ClientErrors::ConfigCurrent;
                }
            }
        }
        //Either we sent the put ok, or we are doing a read to get into here.
        if (nRet == ClientErrors::None)
        {
            if(getOperation(EmetconProtocol::PutConfig_SPID, OutMessage->Buffer.BSt))
            {
                OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Read;

                insertConfigReadOutMessage("getconfig install spid", *OutMessage, outList);
            }
        }
    }
    else
    {
        nRet = ClientErrors::NoConfigData;
    }

    return nRet;
}

YukonError_t Mct4xxDevice::executePutConfigTimeAdjustTolerance(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    YukonError_t nRet = ClientErrors::None;
    DeviceConfigSPtr deviceConfig = getDeviceConfig();

    if (deviceConfig)
    {
        if( ! readsOnly )
        {
            const boost::optional<long>
                timeAdjustTolerance = deviceConfig->findValue<long>(MCTStrings::TimeAdjustTolerance);

            if(!getOperation(EmetconProtocol::PutConfig_TimeAdjustTolerance, OutMessage->Buffer.BSt))
            {
                CTILOG_ERROR(dout, "Operation PutConfig_TimeAdjustTolerance not found");

                nRet = ClientErrors::NoConfigData;
            }
            else if ( ! timeAdjustTolerance )
            {
                CTILOG_ERROR(dout, "no or bad timeAdjustTolerance value stored");

                nRet = ClientErrors::NoConfigData;
            }
            else
            {
                if (parse.isKeyValid("force") ||
                    CtiDeviceBase::getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_TimeAdjustTolerance) != timeAdjustTolerance )
                {
                    if( !parse.isKeyValid("verify") )
                    {
                        //the bstruct IO is set above by getOperation()
                        OutMessage->Buffer.BSt.Message[0] = *timeAdjustTolerance;
                        outList.push_back( CTIDBG_new OUTMESS(*OutMessage) );

                        nRet = ClientErrors::None;
                    }
                    else
                    {
                        nRet = ClientErrors::ConfigNotCurrent;
                    }
                }
                else
                {
                    nRet = ClientErrors::ConfigCurrent;
                }
            }
        }
        //Either we sent the put ok, or we are doing a read to get into here.
        if (nRet == ClientErrors::None)
        {
            if(getOperation(EmetconProtocol::PutConfig_TimeAdjustTolerance, OutMessage->Buffer.BSt))
            {
                OutMessage->Buffer.BSt.IO         = EmetconProtocol::IO_Read;

                insertConfigReadOutMessage("getconfig install timeadjusttolerance", *OutMessage, outList);
            }
        }
    }
    else
    {
        nRet = ClientErrors::NoConfigData;
    }

    return nRet;
}

/*
    This is only configured under 470.
*/
YukonError_t Mct4xxDevice::executePutConfigPrecannedTable    (CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

/*
    This is only configured under 470 for now.
*/
YukonError_t Mct4xxDevice::executePutConfigTOU(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,CtiMessageList &vgList,CtiMessageList &retList,OutMessageList &outList, bool readsOnly)
{
    return ClientErrors::NoMethod;
}

/*
//Sounds like this will not be supported.
int Mct4xxDevice::executePutConfigDNP(CtiRequestMsg *pReq,CtiCommandParser &parse,OUTMESS *&OutMessage,MessageList &vgList,MessageList &retList,OutMessageList &outList)
{
    return NoMethod;
}
*/

YukonError_t Mct4xxDevice::decodeGetConfigTime(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    std::string resultString;
    unsigned long timestamp;
    char timezone_offset;

    if( InMessage.Sequence == EmetconProtocol::GetConfig_Time )
    {
        timezone_offset = InMessage.Buffer.DSt.Message[0];

        timestamp = InMessage.Buffer.DSt.Message[1] << 24 |
                    InMessage.Buffer.DSt.Message[2] << 16 |
                    InMessage.Buffer.DSt.Message[3] <<  8 |
                    InMessage.Buffer.DSt.Message[4];

        resultString  = getName() + " / Current Time: " + printTimestamp(timestamp) + "\n";

        resultString += getName() + " / Timezone Offset: ";

        if( timezone_offset <=  48 &&
            timezone_offset >= -48 )
        {
            resultString += CtiNumStr(((float)timezone_offset) / 4.0, 2) + " hours\n";
        }
        else if( timezone_offset == 0x7f )
        {
            resultString += "(uninitialized [0x7f])\n";
        }
        else
        {
            resultString += "(invalid [" + CtiNumStr(timezone_offset).xhex(2) + "])\n";
        }
    }
    else if( InMessage.Sequence == EmetconProtocol::GetConfig_TSync )
    {
        timestamp = InMessage.Buffer.DSt.Message[0] << 24 |
                    InMessage.Buffer.DSt.Message[1] << 16 |
                    InMessage.Buffer.DSt.Message[2] <<  8 |
                    InMessage.Buffer.DSt.Message[3];

        resultString = getName() + " / Time Last Synced at: " + printTimestamp(timestamp);
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


YukonError_t Mct4xxDevice::decodeGetValueLoadProfile(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    string resultString;
    bool   expectMore = false;

    //  add error handling for automated load profile retrieval... !

    CtiReturnMsg    *ReturnMsg = NULL;  // Message sent to VanGogh, inherits from Multi

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    const int interval_len = getLoadProfileInterval(_llpRequest.channel);

    const unsigned long interval_beginning_time = _llpInterest.time - interval_len;

    const unsigned char interest[5] =
    {
        static_cast<const unsigned char>(interval_beginning_time >> 24),
        static_cast<const unsigned char>(interval_beginning_time >> 16),
        static_cast<const unsigned char>(interval_beginning_time >>  8),
        static_cast<const unsigned char>(interval_beginning_time),
        static_cast<const unsigned char>(_llpRequest.channel + 1)
    };

    if( InMessage.Return.OptionsField != _llpRequest.request_id && !_llpRequest.request_id )
    {
        resultString += "Load profile request cancelled\n";
    }
    else if( interval_len <= 0 )
    {
        resultString =
            "Channel " + CtiNumStr(_llpRequest.channel + 1) + " LP Interval returned " + CtiNumStr(interval_len) + ".\n"
            "Retrieve the correct LP Interval and attempt the request again.";

        status = ClientErrors::MissingConfig;

        long existing_id = InMessage.Return.OptionsField;

        //  reset, we're not executing any more
        _llpRequest.request_id.compare_exchange_strong(existing_id, 0);
    }
    else if( crc8(interest, 5) == DSt->Message[0] )
    {
        //  if we succeeded, we should be okay for successive reads...
        _llpRequest.retry = 0;

        string point_name = "LP channel ";
        point_name += CtiNumStr(_llpRequest.channel + 1);

        for( int i = 0; i < 6; i++, _llpRequest.begin += interval_len )
        {
            point_info pi = getLoadProfileData(_llpRequest.channel, interval_len, DSt->Message + (i * 2) + 1, 2);

            insertPointDataReport(DemandAccumulatorPointType, PointOffset_LoadProfileOffset + 1 + _llpRequest.channel,
                                  ReturnMsg, pi, point_name, _llpRequest.begin, 1.0, TAG_POINT_LOAD_PROFILE_DATA);
        }

        if( _llpRequest.begin < _llpRequest.end )
        {
            CtiTime time_begin(_llpRequest.begin),
                    time_end  (_llpRequest.end);

            std::string lp_request_str = "getvalue lp ";

            lp_request_str += "channel " + CtiNumStr(_llpRequest.channel + 1) + " " + time_begin.asString() + " " + time_end.asString();

            //  if it's a background message, it's queued
            if(      strstr(InMessage.Return.CommandStr, " background") )   lp_request_str += " background";
            else if( strstr(InMessage.Return.CommandStr, " noqueue") )      lp_request_str += " noqueue";

            expectMore = true;
            CtiRequestMsg newReq(getID(),
                                 lp_request_str,
                                 InMessage.Return.UserID,
                                 InMessage.Return.GrpMsgID,
                                 getRouteID(),
                                 selectInitialMacroRouteOffset(getRouteID()),  //  this bypasses PIL, so we need to calculate this
                                 0,
                                 InMessage.Return.OptionsField,  //  communicate our request ID back to executeGetValueLoadProfile()
                                 InMessage.Priority);

            //  this may be empty if it's a background request, but assign it anyway
            newReq.setConnectionHandle(InMessage.Return.Connection);

            beginExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);
        }
        else
        {
            resultString = "Load profile request complete\n";

            long existing_id = InMessage.Return.OptionsField;

            //  reset, we're not executing any more
            if( _llpRequest.request_id.compare_exchange_strong(existing_id, 0) )
            {
                purgeDynamicPaoInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestBegin);
                purgeDynamicPaoInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_RequestEnd);
            }
        }
    }
    else
    {
        resultString = "Load Profile Interest check does not match";

        if( _llpRequest.retry < LPRetries )
        {
            _llpInterest.time = 0;
            _llpRequest.retry++;

            setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_Time, _llpInterest.time);

            resultString += ", retrying";

            // new command string must remove "read" or else it is not considered a retry.
            std::string newCommandStr = InMessage.Return.CommandStr;
            removeString(newCommandStr, " read");

            expectMore = true;
            CtiRequestMsg newReq(getID(),
                                 newCommandStr,
                                 InMessage.Return.UserID,
                                 InMessage.Return.GrpMsgID,
                                 getRouteID(),
                                 selectInitialMacroRouteOffset(getRouteID()),  //  this bypasses PIL, so we need to calculate this
                                 0,
                                 InMessage.Return.OptionsField,  //  communicate our request ID back to executeGetValueLoadProfile()
                                 InMessage.Priority);

            newReq.setConnectionHandle(InMessage.Return.Connection);

            beginExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);
        }
        else
        {
            resultString += " - try message again";

            long existing_id = InMessage.Return.OptionsField;

            //  reset, we're not executing any more
            if( _llpRequest.request_id.compare_exchange_strong(existing_id, 0) )
            {
                //  reset our internal time of interest - it didn't match, so we'll have to send it again
                _llpInterest.time = 0;
                _llpRequest.retry = 0;

                setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_LLPInterest_Time, _llpInterest.time);
            }
        }
    }

    //  this is gross
    if( !ReturnMsg->ResultString().empty() )
    {
        resultString = ReturnMsg->ResultString() + "\n" + resultString;
    }

    ReturnMsg->setResultString(resultString);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList, expectMore );

    return status;
}


YukonError_t Mct4xxDevice::decodeGetConfigTOU(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    CtiReturnMsg *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi
    string resultString;
    unsigned long timestamp;
    CtiTime tmpTime;

    int schedulenum = parse.getiValue("tou_schedule");

    if( schedulenum > 0 && schedulenum <= 4 )
    {
        long timeArray[2][5];
        long rateArray[2][6];
        schedulenum -= (schedulenum - 1) % 2;

        for( int offset = 0; offset < 2; offset++ )
        {
            int rates, current_rate, previous_rate, byte_offset, time_offset;

            resultString += getName() + " / TOU Schedule " + CtiNumStr(schedulenum + offset) + ":\n";

            if( offset == 0 )
            {
                rates = ((InMessage.Buffer.DSt.Message[5] & 0x0f) << 8) | InMessage.Buffer.DSt.Message[6];
                byte_offset = 0;
            }
            else
            {
                rates = ((InMessage.Buffer.DSt.Message[5] & 0xf0) << 4) | InMessage.Buffer.DSt.Message[12];
                byte_offset = 7;
            }

            rateArray[offset][0] = (rates)       & 0x03;
            rateArray[offset][1] = (rates >>  2) & 0x03;
            rateArray[offset][2] = (rates >>  4) & 0x03;
            rateArray[offset][3] = (rates >>  6) & 0x03;
            rateArray[offset][4] = (rates >>  8) & 0x03;
            rateArray[offset][5] = (rates >> 10) & 0x03;

            current_rate = rates & 0x03;
            resultString += "00:00: ";
            resultString += (char)('A' + current_rate);
            resultString += "\n";
            rates >>= 2;

            time_offset = 0;
            previous_rate = current_rate;
            for( int switchtime = 0; switchtime < 5; switchtime++ )
            {
                int hour, minute;

                time_offset += InMessage.Buffer.DSt.Message[byte_offset + switchtime] * 300;
                timeArray[offset][switchtime] = InMessage.Buffer.DSt.Message[byte_offset + switchtime];

                hour   = time_offset / 3600;
                minute = (time_offset / 60) % 60;

                current_rate = rates & 0x03;

                if( (hour <= 23) && (current_rate != previous_rate) )
                {
                    resultString += CtiNumStr(hour).zpad(2) + ":" + CtiNumStr(minute).zpad(2) + ": " + (char)('A' + current_rate) + "\n";
                }

                previous_rate = current_rate;

                rates >>= 2;
            }

            resultString += "- end of day - \n\n";
        }

        string dayScheduleA, dayScheduleB;
        createTOUDayScheduleString(dayScheduleA, timeArray[0], rateArray[0]);
        createTOUDayScheduleString(dayScheduleB, timeArray[1], rateArray[1]);

        if( schedulenum < 2 )
        {
            CtiDeviceBase::setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, dayScheduleA);
            CtiDeviceBase::setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, dayScheduleB);
        }
        else
        {
            CtiDeviceBase::setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, dayScheduleA);
            CtiDeviceBase::setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, dayScheduleB);
        }

    }
    else
    {
        resultString = getName() + " / TOU Status:\n\n";

        timestamp = InMessage.Buffer.DSt.Message[6] << 24 |
                    InMessage.Buffer.DSt.Message[7] << 16 |
                    InMessage.Buffer.DSt.Message[8] <<  8 |
                    InMessage.Buffer.DSt.Message[9];

        resultString += "Current time: " + CtiTime(timestamp).asString() + "\n";

        int tz_offset = (char)InMessage.Buffer.DSt.Message[10] * 15;

        resultString += "Time zone offset: " + CtiNumStr((float)tz_offset / 60.0, 1) + " hours ( " + CtiNumStr(tz_offset) + " minutes)\n";

        if( InMessage.Buffer.DSt.Message[3] & 0x80 )
        {
            resultString += "Critical peak active\n";
        }
        if( InMessage.Buffer.DSt.Message[4] & 0x80 )
        {
            resultString += "Holiday active\n";
        }
        if( InMessage.Buffer.DSt.Message[4] & 0x40 )
        {
            resultString += "DST active\n";
        }

        resultString += "Current rate: " + string(1, (char)('A' + (InMessage.Buffer.DSt.Message[3] & 0x7f))) + "\n";

        resultString += "Current schedule: " + CtiNumStr((int)(InMessage.Buffer.DSt.Message[4] & 0x03) + 1) + "\n";
/*
        resultString += "Current switch time: ";

        if( InMessage.Buffer.DSt.Message[5] == 0xff )
        {
            resultString += "not active\n";
        }
        else
        {
             resultString += CtiNumStr((int)InMessage.Buffer.DSt.Message[5]) + "\n";
        }
*/
        resultString += "Default rate: ";

        if( InMessage.Buffer.DSt.Message[2] == 0xff )
        {
            resultString += "No TOU active\n";
        }
        else
        {
            resultString += string(1, (char)('A' + InMessage.Buffer.DSt.Message[2])) + "\n";
        }

        resultString += "\nDay table: \n";

        char *(daynames[8]) = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Holiday"};

        for( int i = 0; i < 8; i++ )
        {
            int dayschedule = InMessage.Buffer.DSt.Message[1 - i/4] >> ((i % 4) * 2) & 0x03;

            resultString += "Schedule " + CtiNumStr(dayschedule + 1) + " - " + daynames[i] + "\n";
        }
    }

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);
    ReturnMsg->setResultString(resultString);

    decrementGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection);

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList, getGroupMessageCount(InMessage.Return.UserID, InMessage.Return.Connection));

    return status;
}


bool Mct4xxDevice::isProfileTablePointerCurrent(const unsigned char table_pointer, const CtiTime TimeNow, const unsigned interval_len) const
{
    const unsigned long expected_table_pointer = ((TimeNow.seconds() / interval_len) % 96) + 1;

    //  the table pointer needs to indicate it was within the same block of 6 intervals as we expect
    return ((table_pointer + 5) / 6) == ((expected_table_pointer + 5) / 6);
}


YukonError_t Mct4xxDevice::decodeScanLoadProfile(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;

    const DSTRUCT *DSt  = &InMessage.Buffer.DSt;

    string         val_report;
    int            channel, block;
    unsigned long  timestamp, pulses;
    point_info   pi;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    CtiReturnMsg    *ret_msg = 0;  // Message sent to VanGogh, inherits from Multi
    CtiPointDataMsg *pdata   = 0;

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "Load Profile Scan Decode for \"" << getName() << "\"");
    }

    ret_msg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ret_msg->setUserMessageId(InMessage.Return.UserID);

    if( (channel = parse.getiValue("scan_loadprofile_channel", 0)) &&
        (block   = parse.getiValue("scan_loadprofile_block",   0)) )
    {
        //  parse is 1-based, we need it 0-based
        channel--;

        const int interval_len = getLoadProfileInterval(channel);

        //  this is where the block started...
        timestamp  = TimeNow.seconds();
        timestamp -= interval_len * 6 * block;
        timestamp -= timestamp % (interval_len * 6);

        if( interval_len <= 0 )
        {
            CTILOG_ERROR(dout, "LP error for device \"" << getName() <<"\" - invalid interval_len ("<< interval_len <<")"<<
                    endl << "commandstr = "<< InMessage.Return.CommandStr
                    );
/*
            if( isIedChannel(channel) )
            {
                //  We need to re-read this value from the IED
                purgeDynamicPaoInfo(CtiTableDynamicPaoInfo::Key_MCT_IEDLoadProfileInterval);
            }
*/
            status = ClientErrors::MissingConfig;
        }
        //  make sure we're getting the same block we're expecting
        else if( ! isProfileTablePointerCurrent(DSt->Message[0], TimeNow, interval_len) )
        {
            Cti::FormattedList itemList;
            itemList.add("Table Pointer") << (unsigned)DSt->Message[0];
            itemList.add("TimeNow")       << TimeNow;
            itemList.add("interval_len")  << interval_len;
            itemList.add("commandstr")    << InMessage.Return.CommandStr;

            CTILOG_ERROR(dout, "LP error for device \""<< getName() <<"\""<<
                    itemList
                    );

            status = ClientErrors::InvalidTimestamp;
        }
        else if( timestamp != _lp_info[channel].collection_point )
        {
            Cti::FormattedList itemList;
            itemList.add("calculated timestamp") << CtiTime(timestamp);
            itemList.add("collection_point")     << CtiTime(_lp_info[channel].collection_point);
            itemList.add("commandstr")           << InMessage.Return.CommandStr;

            CTILOG_ERROR(dout, "LP error for device \""<< getName() <<"\", for channel "<< channel <<
                    itemList
                    );

            status = ClientErrors::InvalidTimestamp;
        }
        else
        {
            if( !getDevicePointOffsetTypeEqual(PointOffset_LoadProfileOffset + channel + 1, DemandAccumulatorPointType) )
            {
                CTILOG_ERROR(dout, "no load profile point defined for \"" + getName() + "\"");

                ret_msg->setResultString("No load profile point defined for '" + getName() + "'");
            }
            else
            {
                for( int offset = 5; offset >= 0; offset-- )
                {
                    pi = getLoadProfileData(channel, interval_len, DSt->Message + offset*2 + 1, 2);

                    insertPointDataReport(DemandAccumulatorPointType, PointOffset_LoadProfileOffset + channel + 1,
                                          ret_msg, pi, "", timestamp + interval_len * (6 - offset), 1.0, TAG_POINT_LOAD_PROFILE_DATA);
                }
            }

            //  unnecessary?
            setLastLPTime (timestamp + interval_len * 6);

            _lp_info[channel].collection_point = timestamp + interval_len * 6;
        }
    }
    else
    {
        CTILOG_ERROR(dout, "missing scan_loadprofile token for \"" << getName() << "\"");

        ret_msg->setResultString("Malformed LP command string for '" + getName() + "'");
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ret_msg, vgList, retList );

    return status;
}


YukonError_t Mct4xxDevice::decodeGetValuePeakDemand(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t status = ClientErrors::None;
    int        channel,
               pointoffset;
    frozen_point_info pi_kw,
               pi_kw_time,
               pi_kwh;
    CtiTime    kw_time,
               kwh_time;
    string     result_string;

    CtiTableDynamicPaoInfo::PaoInfoKeys key_peak_timestamp;

    CtiCommandParser parse(InMessage.Return.CommandStr);

    const DSTRUCT *DSt   = &InMessage.Buffer.DSt;

    CtiReturnMsg    *ReturnMsg = NULL;    // Message sent to VanGogh, inherits from Multi

    if( getMCTDebugLevel(DebugLevel_Scanrates) )
    {
        CTILOG_DEBUG(dout, "TOU/Peak Demand Decode for \""<< getName() <<"\"");
    }

    /*
        Computed TOU point offsets, for reference:

            pulse accumulators:

                kwh rate a: 101
                kwh rate b: 121
                kwh rate c: 141
                kwh rate d: 161

            demand accumulators:

                peak kw rate a: 111
                peak kw rate b: 131
                peak kw rate c: 151
                peak kw rate d: 171

    */

    pointoffset = channel = 1;

    if( parse.getiValue("channel") == 2 )
    {
        pointoffset = channel = 2;
    }
    else if( parse.getiValue("channel") == 3 )
    {
        pointoffset = channel = 3;
    }

    if( parse.getFlags() & (CMD_FLAG_GV_RATEMASK ^ CMD_FLAG_GV_RATET) )
    {
        pointoffset += PointOffset_TOUBase;

        //  need to add smarts for multiple channels when applicable
        if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  pointoffset += 0; //  no increment for rate A
        else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  pointoffset += PointOffset_RateOffset * 1;
        else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  pointoffset += PointOffset_RateOffset * 2;
        else if( parse.getFlags() & CMD_FLAG_GV_RATED )  pointoffset += PointOffset_RateOffset * 3;
    }

    if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  key_peak_timestamp = CtiTableDynamicPaoInfo::Key_FrozenRateAPeakTimestamp;
    else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  key_peak_timestamp = CtiTableDynamicPaoInfo::Key_FrozenRateBPeakTimestamp;
    else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  key_peak_timestamp = CtiTableDynamicPaoInfo::Key_FrozenRateCPeakTimestamp;
    else if( parse.getFlags() & CMD_FLAG_GV_RATED )  key_peak_timestamp = CtiTableDynamicPaoInfo::Key_FrozenRateDPeakTimestamp;
    else if( parse.getiValue("channel") == 2 )       key_peak_timestamp = CtiTableDynamicPaoInfo::Key_FrozenDemand2PeakTimestamp;
    else if( parse.getiValue("channel") == 3 )       key_peak_timestamp = CtiTableDynamicPaoInfo::Key_FrozenDemand3PeakTimestamp;
    else                                             key_peak_timestamp = CtiTableDynamicPaoInfo::Key_FrozenDemandPeakTimestamp;

    ReturnMsg = new CtiReturnMsg(getID(), InMessage.Return.CommandStr);
    ReturnMsg->setUserMessageId(InMessage.Return.UserID);

    const unsigned char *freeze_counter = 0;

    if( parse.getFlags() & CMD_FLAG_FROZEN )
    {
        freeze_counter = DSt->Message + 9;
    }

    if( parse.getFlags() & (CMD_FLAG_GV_RATEMASK ^ CMD_FLAG_GV_RATET) )
    {
        //  TOU memory layout
        pi_kwh         = getAccumulatorData(DSt->Message, 3, freeze_counter);

        pi_kw          = getDemandData(DSt->Message + 3, 2, freeze_counter);
        pi_kw_time     = getData(DSt->Message + 5, 4, ValueType_Raw);
    }
    else
    {
        //  normal peak memory layout
        pi_kw          = getDemandData(DSt->Message, 2, freeze_counter);
        pi_kw_time     = getData(DSt->Message + 2, 4, ValueType_Raw);

        pi_kwh         = getAccumulatorData(DSt->Message + 6, 3, freeze_counter);
    }

    //  turn raw pulses into a demand reading - TOU reads use LP interval
    if( InMessage.Sequence == EmetconProtocol::GetValue_TOUPeak )
    {
        const int interval_len = getLoadProfileInterval(channel);

        if( interval_len <= 0 )
        {
            CTILOG_ERROR(dout, "invalid interval_len ("<< interval_len <<") for device \""<< getName() <<"\"");

            pi_kw.quality = InvalidQuality;
            pi_kw.description = "Invalid demand interval, cannot adjust reading";
        }
        else
        {
            pi_kw.value *= double(3600 / interval_len);
        }
    }
    else
    {
        pi_kw.value *= double(3600 / getDemandInterval());
    }

    kw_time      = CtiTime(pi_kw_time.value);

    if( freeze_counter )
    {
        string freeze_error;

        //  this check is mainly for the frozen kWh reading
        if( status = checkFreezeLogic(TimeNow, *freeze_counter, freeze_error) )
        {
            result_string += freeze_error + "\n";

            pi_kwh.quality = InvalidQuality;
            pi_kwh.value = 0;

            switch( status )
            {
                case ClientErrors::FreezeNotRecorded:    pi_kwh.description = "Last freeze not stored";  break;
                case ClientErrors::InvalidFreezeCounter: pi_kwh.description = "Invalid freeze counter";  break;
            }
        }
        else if( pi_kwh.freeze_bit != getExpectedFreezeParity() )
        {
            CTILOG_ERROR(dout, "incoming freeze parity bit ("<< pi_kwh.freeze_bit <<") does not match expected freeze bit ("<< getExpectedFreezeParity() <<") on device \""<< getName() <<"\" - not sending data");

            result_string += "Invalid freeze parity (" + CtiNumStr(pi_kwh.freeze_bit).toString() + ") != (" + CtiNumStr(getExpectedFreezeParity()).toString() + "), last recorded freeze sent at " + getLastFreezeTimestamp(TimeNow).asString() + "\n";
            status = ClientErrors::InvalidFrozenReadingParity;

            pi_kwh.description = "Invalid freeze parity";
            pi_kwh.quality = InvalidQuality;
            pi_kwh.value = 0;
        }
        else
        {
            kwh_time  = getLastFreezeTimestamp(TimeNow);
            kwh_time -= kwh_time.seconds() % 60;
        }

        if( kw_time.is_special() )
        {
            //  defer to the kWh errors
            if( !status )
            {
                status = ClientErrors::InvalidFrozenPeakTimestamp;
            }

            pi_kw.description = "Invalid peak timestamp";
            pi_kw.quality = InvalidQuality;
            pi_kw.value = 0;
        }
        else if( hasDynamicInfo(key_peak_timestamp) && getDynamicInfo(key_peak_timestamp) > kw_time.seconds() )
        {
            CTILOG_ERROR(dout, "new KW peak time \""<< kw_time <<"\" is before old KW peak time \""<< CtiTime(getDynamicInfo(key_peak_timestamp)) <<", not sending data");

            //  defer to the kWh errors
            if( !status )
            {
                status = ClientErrors::InvalidFrozenPeakTimestamp;
            }

            result_string += "New peak is before old peak (" + kw_time.asString() + ") < (" + CtiTime(getDynamicInfo(key_peak_timestamp)).asString() + ")\n";

            pi_kw.description = "Invalid peak timestamp";
            pi_kw.quality = InvalidQuality;
            pi_kw.value = 0;
        }
        else if( getLastFreezeTimestamp(TimeNow) < kw_time.seconds() )
        {
            CTILOG_ERROR(dout, "KW peak time \""<< kw_time <<"\" is after KW freeze time \""<< getLastFreezeTimestamp(TimeNow) <<", not sending data");

            //  defer to the kWh errors
            if( !status )
            {
                status = ClientErrors::InvalidFrozenPeakTimestamp;
            }

            result_string += "Peak timestamp after freeze  (" + kw_time.asString() + ") > (" + getLastFreezeTimestamp(TimeNow).asString() + ")\n";

            pi_kw.description = "Invalid peak timestamp";
            pi_kw.quality = InvalidQuality;
            pi_kw.value = 0;
        }
        else
        {
            setDynamicInfo(key_peak_timestamp, (unsigned long) kw_time.seconds());
        }
    }
    else
    {
        //  just a normal peak read, no freeze-related work needed

        kwh_time  = TimeNow;
    }

    string peak_demand_str   = "Peak Demand",
           meter_reading_str = "Meter Reading",
           rate_str;

    if(      parse.getFlags() & CMD_FLAG_GV_RATEA )  rate_str = " rate A";
    else if( parse.getFlags() & CMD_FLAG_GV_RATEB )  rate_str = " rate B";
    else if( parse.getFlags() & CMD_FLAG_GV_RATEC )  rate_str = " rate C";
    else if( parse.getFlags() & CMD_FLAG_GV_RATED )  rate_str = " rate D";

    if( !rate_str.empty() )
    {
        peak_demand_str   += rate_str;
        meter_reading_str += rate_str;
    }

    if( parse.getiValue("channel") > 1 )
    {
        string channel_str = "Channel " + CtiNumStr(parse.getiValue("channel")) + " ";

        peak_demand_str   = channel_str + peak_demand_str;
        meter_reading_str = channel_str + meter_reading_str;
    }

    //  If it's not a TOU read OR the MCT supports TOU peaks, include the peak data
    if( !(parse.getFlags() & (CMD_FLAG_GV_RATEMASK ^ CMD_FLAG_GV_RATET)) || isSupported(Feature_TouPeaks) )
    {
        insertPointDataReport(DemandAccumulatorPointType, pointoffset + PointOffset_PeakOffset,
                              ReturnMsg, pi_kw, peak_demand_str, kw_time);
        if ( freeze_counter )
        {
            insertPointDataReport(DemandAccumulatorPointType, pointoffset + PointOffset_PeakFrozenOffset,
                                  ReturnMsg, pi_kw, peak_demand_str, kw_time);
        }
    }

    if( !status )
    {
        // Only send the point report if we don't have an error after processing.
        insertPointDataReport(
           PulseAccumulatorPointType,
           pointoffset,
           ReturnMsg,
           pi_kwh,
           meter_reading_str,
           kwh_time,
           0.1,
           TAG_POINT_MUST_ARCHIVE);
    }
    else
    {
        CTILOG_ERROR(dout, "Peak demand decode for \""<< getName() <<"\" resulted in a status code of "<< status <<", not inserting kWh point data.");
    }

    if( !result_string.empty() )
    {
        //  we want any error messages to follow at the end
        ReturnMsg->setResultString(ReturnMsg->ResultString() + "\n" + result_string);
    }

    retMsgHandler( InMessage.Return.CommandStr, status, ReturnMsg, vgList, retList );

    return status;
}


void Mct4xxDevice::handleCommandResult(const Mct4xxCommand &cmd)
{
    cmd.invokeResultHandler(static_cast<Mct4xxCommand::ResultHandler &>(*this));
}


YukonError_t Mct4xxDevice::ModelDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    using DecodeMethod = YukonError_t (Mct4xxDevice::*)(const INMESS &, const CtiTime, CtiMessageList &, CtiMessageList &, OutMessageList &);

    static const std::map<int, DecodeMethod> DecodeMethods {
        { EmetconProtocol::GetConfig_Time,        &Self::decodeGetConfigTime },
        { EmetconProtocol::GetConfig_TSync,       &Self::decodeGetConfigTime },
        { EmetconProtocol::GetConfig_TOU,         &Self::decodeGetConfigTOU  },
        { EmetconProtocol::GetStatus_Freeze,      &Self::decodeGetStatusFreeze },
        { EmetconProtocol::GetValue_TOUPeak,      &Self::decodeGetValuePeakDemand },
        { EmetconProtocol::GetValue_LoadProfile,  &Self::decodeGetValueLoadProfile },
        { EmetconProtocol::PutConfig_LoadProfileReportPeriod, &Self::decodePutConfig },
        { EmetconProtocol::Scan_LoadProfile,      &Self::decodeScanLoadProfile }};

    if( const auto method = mapFind(DecodeMethods, InMessage.Sequence) )
    {
        return (this->*(*method))(InMessage, TimeNow, vgList, retList, outList);
    }

    return Parent::ModelDecode(InMessage, TimeNow, vgList, retList, outList);
}


YukonError_t Mct4xxDevice::SubmitRetry(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    switch( InMessage.Sequence )
    {
        case EmetconProtocol::GetValue_LoadProfile:
        {
            if( _llpRequest.retry < LPRetries )
            {
                _llpRequest.retry++;

                // new command string must remove "read" or else it is not considered a retry.
                std::string newCommandStr = InMessage.Return.CommandStr;
                removeString(newCommandStr, " read");

                CtiRequestMsg newReq(getID(),
                                     newCommandStr,
                                     InMessage.Return.UserID,
                                     InMessage.Return.GrpMsgID,
                                     getRouteID(),
                                     selectInitialMacroRouteOffset(getRouteID()),  //  this bypasses PIL, so we need to calculate this
                                     0,
                                     InMessage.Return.OptionsField,  //  communicate our request ID back to executeGetValueLoadProfile()
                                     InMessage.Priority);

                //  this may be empty if it's a background request, but assign it anyway
                newReq.setConnectionHandle(InMessage.Return.Connection);

                beginExecuteRequest(&newReq, CtiCommandParser(newReq.CommandString()), vgList, retList, outList);

                CtiReturnMsg *ReturnMsg = CTIDBG_new CtiReturnMsg(getID(), InMessage.Return.CommandStr);

                ReturnMsg->setUserMessageId(InMessage.Return.UserID);
                ReturnMsg->setResultString("Load profile retry submitted");

                ReturnMsg->setExpectMore(true);

                retList.push_back(ReturnMsg);
            }
            else
            {
                long existing_id = InMessage.Return.OptionsField;

                //  reset, we're not executing any more
                _llpRequest.request_id.compare_exchange_strong(existing_id, 0);
            }

            return ClientErrors::None;
        }
    }

    return Parent::SubmitRetry(InMessage, TimeNow, vgList, retList, outList);
}


YukonError_t Mct4xxDevice::ErrorDecode(const INMESS &InMessage, const CtiTime TimeNow, CtiMessageList &retList)
{
    switch( InMessage.Sequence )
    {
        case EmetconProtocol::PutConfig_LoadProfileReportPeriod:
        case EmetconProtocol::GetValue_LoadProfilePeakReport:
        case EmetconProtocol::GetConfig_LoadProfileExistingPeak:
        {
            _llpPeakInterest.tryEndRequest(InMessage.Return.OptionsField);

            return ClientErrors::None;
        }
    }

    return Parent::ErrorDecode(InMessage, TimeNow, retList);
}


unsigned Mct4xxDevice::loadTimeSync(unsigned char *buf)
{
    CtiTime now;
    unsigned long time = now.seconds();

    buf[0] = gMCT400SeriesSPID;  //  global SPID
    buf[1] = (time >> 24) & 0x000000ff;
    buf[2] = (time >> 16) & 0x000000ff;
    buf[3] = (time >>  8) & 0x000000ff;
    buf[4] =  time        & 0x000000ff;
    buf[5] = now.isDST();

    return FuncWrite_TSyncLen;
}


void Mct4xxDevice::createTOUDayScheduleString(string &schedule, long (&times)[5], long (&rates)[6])
{
    for( int i=0; i<5; i++ )
    {
        schedule += CtiNumStr(times[i]);
        schedule += ', ';
    }
    for( int i=1; i<6; i++ )
    {
        schedule += CtiNumStr(rates[i]);
        schedule += ', ';
    }
    schedule += CtiNumStr(rates[0]);  //  put the first element last to match legacy DynamicPaoInfo
}


bool Mct4xxDevice::is_valid_time( const CtiTime timestamp ) const
{
    bool retval = false;

    //  between 2000-jan-01 and tomorrow
    retval = (timestamp > DawnOfTime_UtcSeconds) &&
             (timestamp < (CtiTime::now() + 86400));

    return retval;
}

}