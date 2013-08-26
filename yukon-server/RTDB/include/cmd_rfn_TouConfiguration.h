#pragma once

#include "cmd_rfn.h"
#include "boost/array.hpp"
#include "ctidate.h"
#include "ctitime.h"

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

    enum TouState
    {
        TouEnable,
        TouDisable
    };

    boost::optional<TouState> getTouStateReceived() const;

protected:

    RfnTouConfigurationCommand();

    virtual unsigned char getCommandCode() const;

    // methods implemented in derived classes
    virtual unsigned char getOperation() const = 0;
    virtual Bytes         getCommandData() = 0;

    virtual void decodeTlv( RfnResult& result, const TypeLengthValue& tlv ) = 0;

    boost::optional<TouState> _touState_received;
};

/**
 * RFN TOU configuration command schedule
 */
class IM_EX_DEVDB RfnTouScheduleConfigurationCommand : public RfnTouConfigurationCommand
{
public:

    enum ScheduleNbr
    {
        Schedule1,
        Schedule2,
        Schedule3,
        Schedule4,
    };

    enum Rate
    {
        RateA,
        RateB,
        RateC,
        RateD,
    };

    typedef boost::array<ScheduleNbr, 8>       DayTable;

    typedef boost::array<unsigned short, 5>    DailyTimes;
    typedef boost::array<Rate, 6>              DailyRates;

    typedef std::map<ScheduleNbr, DailyTimes>  Times;
    typedef std::map<ScheduleNbr, DailyRates>  Rates;

    struct Schedule
    {
        boost::optional<DayTable>  _dayTable;
        boost::optional<Rate>      _defaultRate;
        Times                      _times;
        Rates                      _rates;
    };

    RfnTouScheduleConfigurationCommand(); // read
    RfnTouScheduleConfigurationCommand( const Schedule &schedule_to_send ); // write

    boost::optional<Schedule> getTouScheduleReceived() const;

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnResult& result, const TypeLengthValue& tlv );

    void decodeDayTable            ( RfnResult& result, const Bytes& value );
    void decodeDefaultTouRate      ( RfnResult& result, const Bytes& value );
    void decodeScheduleSwitchTimes ( RfnResult& result, const Bytes& value, const ScheduleNbr schedule_nbr );
    void decodeScheduleRates       ( RfnResult& result, const Bytes& value, const ScheduleNbr schedule_nbr );

private:

    boost::optional<Schedule> const _schedule_to_send;
    boost::optional<Schedule>       _schedule_received;
};

/**
 * RFN TOU configuration command holiday
 */
class IM_EX_DEVDB RfnTouHolidayConfigurationCommand : public RfnTouConfigurationCommand
{
public:

    typedef boost::array<CtiDate, 3> Holidays;

    RfnTouHolidayConfigurationCommand(); // read
    RfnTouHolidayConfigurationCommand( const Holidays &holidays_to_send ); // write

    boost::optional<Holidays> getHolidaysReceived() const;

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnResult& result, const TypeLengthValue& tlv );

    void decodeHoliday( RfnResult& result, const Bytes& value );

private:

    boost::optional<Holidays> const _holidays_to_send;
    boost::optional<Holidays>       _holidays_received;
};

/**
 * RFN TOU configuration command enable/disable
 */
class IM_EX_DEVDB RfnTouEnableConfigurationCommand : public RfnTouConfigurationCommand
{
public:

    RfnTouEnableConfigurationCommand(); // read
    RfnTouEnableConfigurationCommand( TouState touState_to_send ); // write

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnResult& result, const TypeLengthValue& tlv );

private:

    boost::optional<TouState> const _touState_to_send;
};

/**
 * RFN TOU configuration command enable/disable
 */
class IM_EX_DEVDB RfnTouHolidayActiveConfigurationCommand : public RfnTouConfigurationCommand
{
public:

    enum HolidayActive
    {
        SetHolidayActive,
        CancelHolidayActive,
    };

    RfnTouHolidayActiveConfigurationCommand( HolidayActive holidayActive_to_send ); // write

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnResult& result, const TypeLengthValue& tlv );

private:

    HolidayActive const _holidayActive_to_send;
};

/**
 * RFN TOU critical peak command
 */
class IM_EX_DEVDB RfnTouCriticalPeakCommand : public RfnTouConfigurationCommand
{
public:

    enum Rate
    {
        RateA,
        RateB,
        RateC,
        RateD,
    };

    RfnTouCriticalPeakCommand( const Rate rate, const CtiTime & utcExpireTime );

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnResult& result, const TypeLengthValue& tlv );

private:

    Rate    _rate;
    CtiTime _utcExpireTime;
};

/**
 * RFN TOU cancel critical peak command
 */
class IM_EX_DEVDB RfnTouCancelCriticalPeakCommand : public RfnTouConfigurationCommand
{
public:

    RfnTouCancelCriticalPeakCommand();

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnResult& result, const TypeLengthValue& tlv );

};

}
}
}
