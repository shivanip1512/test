#include "precompiled.h"

#include "cmd_lcr3102_hourlyDataLog.h"

#include "dsm2err.h"

using std::string;
using std::vector;

namespace Cti {
namespace Devices {
namespace Commands {

Lcr3102HourlyDataLogCommand::Lcr3102HourlyDataLogCommand(ctitime_t utcStartSeconds) :
    _utcSeconds(utcStartSeconds),
    _state(State_WriteStartTime),
    _function(Write_HourlyDataLogStartTime),
    _retries(2)
{
}

DlcCommand::emetcon_request_ptr Lcr3102HourlyDataLogCommand::executeCommand(const CtiTime now)
{
    return makeRequest(now);
}

DlcCommand::emetcon_request_ptr Lcr3102HourlyDataLogCommand::makeRequest(const CtiTime now)
{
    if(_state == State_WriteStartTime)
    {
        Bytes payload;

        payload.push_back(_utcSeconds >> 24);
        payload.push_back(_utcSeconds >> 16);
        payload.push_back(_utcSeconds >> 8);
        payload.push_back(_utcSeconds);

        return std::make_unique<write_request_t>(_function, payload);
    }
    else if(_state == State_ConfirmStartTime)
    {
        return std::make_unique<read_request_t>(_function, ReadLength_StartTimeConfirm);
    }
    else
    {
        return std::make_unique<read_request_t>(_function, ReadLength_HourlyDataLog);
    }
}

DlcCommand::request_ptr Lcr3102HourlyDataLogCommand::decodeCommand(CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, string &description, vector<point_data> &points)
{
    if(_state == State_WriteStartTime)
    {
         _state    = State_ConfirmStartTime;
         _function = Read_StartTimeConfirm;
        return makeRequest(now);
    }
    else if(_state == State_ConfirmStartTime)
    {
        unsigned int startTime = getValueFromBits_bEndian(*payload, 0, 32);

        if(startTime != _utcSeconds)
        {
            description = "Device did not respond with the correct hourly log start time (" + CtiNumStr(startTime).xhex() + ")";
            throw CommandException(ClientErrors::InvalidTimestamp, description);
        }
        else
        {
            _state    = State_ReadIntervalData;
            _function = Read_FirstHourlyDataLogInterval;
            return makeRequest(now);
        }
    }
    else
    {
        const unsigned char flags = getValueFromBits_bEndian(*payload, 0, 8);

        validateFlags(flags);

        // Grab the hourly data from the payload. There are 12 items of 6-bit packed data.
        vector<unsigned> data = getValueVectorFromBits_bEndian(*payload, 8, 6, 12);

        getDescription(data, description);

        if(_function < Read_FourthHourlyDataLogInterval)
        {
            _function++;
            return makeRequest(now);
        }
        else
        {
            description += "\nHourly data log read complete.";
            return nullptr;
        }
    }
}

// throws CommandException
void Lcr3102HourlyDataLogCommand::validateFlags(const unsigned char &flags)
{
    /*
     * This function interprets the flags returned from the LCR 3102.
     *  Flag byte XXIERSTs
     *      XX is unused
     *      I - if set the log is doing 15 minute intervals.  Clear means the log is hourly interval
     *      E - if set, indicates there is an error with the requested data log time or number of elements
     *      R - if set, the first six bits are runtime
     *      S - if set, the next six bits are shedtime
     *      T - if set, the next six bits are Temperature (thermostat)
     *          Offset by 40 degrees (range is from 40 to 103)
     *      s - if set, the next six bits are setpoint (thermostat)
     *          Offset by 40 degress (range is from 40 to 103)
     *
     */

    std::string description;

    if(flags & Flag_Error)
    {
        description = "Device responded with a data log dispute. (" + CtiNumStr(flags).xhex(2) + ")";
        throw CommandException(ClientErrors::Abnormal, description);
    }

    // These bits should NEVER be set from the LCR since it doesn't support them.
    if(flags & Flag_Setpoint || flags & Flag_Temperature)
    {
        description = "Device responded with invalid data types (" + CtiNumStr(flags).xhex(2) + ")";
        throw CommandException(ClientErrors::Abnormal, description);
    }

    // These bits should ALWAYS be set from the LCR.
    if(!(flags & Flag_ShedTime) || !(flags & Flag_RunTime))
    {
        description = "Device did not respond with expected shedtime and runtime data (" + CtiNumStr(flags).xhex(2) + ")";
        throw CommandException(ClientErrors::Abnormal, description);
    }
}

void Lcr3102HourlyDataLogCommand::getDescription(std::vector<unsigned> data, std::string &description)
{
    // There are exactly 12 items in the vector, otherwise we would have thrown.
    for(int i = 0; i < ReadLength_ExpectedData; i++)
    {
        int runtimeVal  = data[2*i];
        int shedTimeVal = data[2*i+1];

        description += "Hour " + CtiNumStr((_function - Read_FirstHourlyDataLogInterval) * 6 + i) + " - ";
        if((runtimeVal + shedTimeVal) > 60)
        {
            description += "ERROR: Runtime(" + CtiNumStr(runtimeVal).xhex(2) + "), ";
            description += "Shedtime(" + CtiNumStr(shedTimeVal).xhex(2) + ")\n";
        }
        else
        {
            description += "Runtime: " + CtiNumStr(runtimeVal) + " minutes, ";
            description += "Shedtime: " + CtiNumStr(shedTimeVal) + " minutes\n";
        }
    }
}

//  throws CommandException
DlcCommand::request_ptr Lcr3102HourlyDataLogCommand::error(const CtiTime now, const YukonError_t error_code, string &description)
{
    if( description.empty() )
    {
        description = GetErrorString(error_code);
    }

    description += "\n";

    if( _retries > 0 )
    {
        _retries--;

        description += "Retrying (" + CtiNumStr(_retries) + " remaining)";

        return makeRequest(now);
    }
    else
    {
        throw CommandException(error_code, description + "Retries exhausted");
    }
}

}
}
}

