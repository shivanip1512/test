#pragma once

#include "cmd_rfn.h"
#include "boost/array.hpp"

namespace Cti {
namespace Devices {
namespace Commands {

/**
 * RFN TOU configuration command base class
 */
class IM_EX_DEVDB RfnTouConfigurationCommand : public RfnCommand
{
public:

    virtual RfnResult decodeCommand(const CtiTime now, const RfnResponse &response);
    virtual RfnResult error(const CtiTime now, const YukonError_t error_code);

    std::vector<CtiTableDynamicPaoInfo> getPaoInfo() const;

protected:

    std::vector<CtiTableDynamicPaoInfo> _paoInfo;

    RfnTouConfigurationCommand();

    virtual unsigned char getCommandCode() const;

    // this is an abstract class : the following functions are implemented in the derived class
    // virtual unsigned char getOperation() const;
    // virtual unsigned char getData() const;

    virtual void decodeTlv( RfnResult& result, const TypeLengthValue& tlv ) = 0;
};

/**
 * RFN TOU configuration command schedule
 */
class IM_EX_DEVDB RfnTouScheduleConfigurationCommand : public RfnTouConfigurationCommand
{
public:

    enum ScheduleE
    {
        Schedule1 = 0,
        Schedule2,
        Schedule3,
        Schedule4,
    };

    enum RateE
    {
        RateA = 0,
        RateB,
        RateC,
        RateD,
    };

    typedef boost::array<unsigned short, 5>   dailyTimesT;
    typedef boost::array<RateE, 6>            dailyRatesT;

    typedef boost::array<ScheduleE, 8>        dayTableT;
    typedef std::map<ScheduleE, dailyTimesT>  timesT;
    typedef std::map<ScheduleE, dailyRatesT>  ratesT;
    typedef RateE                             defaultRateT;

    struct Schedule
    {
        dayTableT     _dayTable;
        timesT        _times;
        ratesT        _rates;
        defaultRateT  _defaultRate;
    };

    RfnTouScheduleConfigurationCommand(); // read
    RfnTouScheduleConfigurationCommand( const Schedule &schedule ); // write

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnResult& result, const TypeLengthValue& tlv );

    void decodeDayTable            ( RfnResult& result, const Bytes& value );
    void decodeDefaultTouRate      ( RfnResult& result, const Bytes& value );
    void decodeScheduleSwitchTimes ( RfnResult& result, const Bytes& value, const unsigned schedule_nbr );
    void decodeScheduleRates       ( RfnResult& result, const Bytes& value, const unsigned schedule_nbr );

private:

    boost::optional<Schedule> const _schedule_to_send;
};

/**
 * RFN TOU configuration command holiday
 */
class IM_EX_DEVDB RfnTouHolidayConfigurationCommand : public RfnTouConfigurationCommand
{
public:

    typedef boost::array<unsigned long, 3> holidaysT;

    RfnTouHolidayConfigurationCommand(); // read
    RfnTouHolidayConfigurationCommand( const holidaysT &holidays ); // write

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnResult& result, const TypeLengthValue& tlv );

    void decodeHoliday( RfnResult& result, const Bytes& value );

private:

    boost::optional<holidaysT> const _holidays_to_send;
};

/**
 * RFN TOU configuration command enable/disable
 */
class IM_EX_DEVDB RfnTouEnableConfigurationCommand : public RfnTouConfigurationCommand
{
public:

    RfnTouEnableConfigurationCommand(); // read
    RfnTouEnableConfigurationCommand( bool bEnableTou ); // write

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnResult& result, const TypeLengthValue& tlv );

private:

    boost::optional<bool> const _enableTou_to_send;
};

}
}
}
