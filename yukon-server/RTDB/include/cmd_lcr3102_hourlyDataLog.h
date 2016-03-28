#pragma once

#include "cmd_lcr3102.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB Lcr3102HourlyDataLogCommand : public Lcr3102Command
{

protected:

    void validateFlags(const unsigned char &flags);

private:

    enum States
    {
        State_WriteStartTime,
        State_ConfirmStartTime,
        State_ReadIntervalData
    };

    ctitime_t _utcSeconds;

    int _retries;

    int _function;

    States _state;

    enum Flags
    {
        Flag_Setpoint    = 0x01,
        Flag_Temperature = 0x02,
        Flag_ShedTime    = 0x04,
        Flag_RunTime     = 0x08,
        Flag_Error       = 0x10,
        Flag_FifteenMinuteInterval = 0x20
    };

    enum ReadLengths
    {
        ReadLength_HourlyDataLog    = 10, // 1 byte for flags, 9 bytes for 6 hours of 6-bit packed data.
        ReadLength_StartTimeConfirm = 4,  // 4 bytes for the UTC seconds.
        ReadLength_ExpectedData     = 6   // Number of hours of data expected for each read.
    };

    enum FunctionReads
    {
        Read_FirstHourlyDataLogInterval  = 0x183,
        Read_SecondHourlyDataLogInterval = 0x184,
        Read_ThirdHourlyDataLogInterval  = 0x185,
        Read_FourthHourlyDataLogInterval = 0x186
    };

    enum DataReads
    {
        Read_StartTimeConfirm = 0x04
    };

    enum FunctionWrites
    {
        Write_HourlyDataLogStartTime = 0x186
    };

    emetcon_request_ptr makeRequest(const CtiTime now);

    void getDescription(std::vector<unsigned> data, std::string &description);

public:

    Lcr3102HourlyDataLogCommand(ctitime_t utcStartSeconds);

    virtual emetcon_request_ptr executeCommand(const CtiTime now);
    virtual request_ptr decodeCommand (const CtiTime now, const unsigned function, const boost::optional<Bytes> &payload, std::string &description, std::vector<point_data> &points);
    virtual request_ptr error         (const CtiTime now, const YukonError_t error_code, std::string &description);

};

}
}
}
