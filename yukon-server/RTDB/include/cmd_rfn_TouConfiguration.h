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

    virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response);

    enum Rate
    {
        RateA,
        RateB,
        RateC,
        RateD
    };

    enum TouState
    {
        TouEnable,
        TouDisable
    };

    static const std::string SchedulePrefix;

    boost::optional<TouState> getTouStateReceived() const;

protected:

    RfnTouConfigurationCommand();

    virtual unsigned char getCommandCode() const;

    // methods implemented in derived classes
    virtual unsigned char getOperation() const = 0;
    virtual Bytes         getCommandData() = 0;

    virtual void decodeTlv( RfnCommandResult& result, const TypeLengthValue& tlv ) = 0;

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

    typedef std::vector<std::string>  DayTable;
    typedef std::vector<std::string>  DailyTimes;
    typedef std::vector<std::string>  DailyRates;

    typedef std::map<ScheduleNbr, DailyTimes>  Times;
    typedef std::map<ScheduleNbr, DailyRates>  Rates;

    struct Schedule
    {
        DayTable     _dayTable;
        std::string  _defaultRate;
        Times        _times;
        Rates        _rates;
    };

    static ScheduleNbr resolveScheduleName( const std::string & scheduleName );

    boost::optional<Schedule> getTouScheduleReceived() const;

protected:

    virtual unsigned char getOperation() const = 0;
    virtual Bytes         getCommandData() = 0;

    virtual void decodeTlv( RfnCommandResult& result, const TypeLengthValue& tlv );

    void decodeDayTable            ( RfnCommandResult& result, const Bytes& value );
    void decodeDefaultTouRate      ( RfnCommandResult& result, const Bytes& value );
    void decodeScheduleSwitchTimes ( RfnCommandResult& result, const Bytes& value, const ScheduleNbr schedule_nbr );
    void decodeScheduleRates       ( RfnCommandResult& result, const Bytes& value, const ScheduleNbr schedule_nbr );

    boost::optional<Schedule> _schedule_received;
};

class IM_EX_DEVDB RfnTouScheduleSetConfigurationCommand : public RfnTouScheduleConfigurationCommand,
       InvokerFor<RfnTouScheduleSetConfigurationCommand>
{
public:

    RfnTouScheduleSetConfigurationCommand( const Schedule &schedule_to_send );

    const Schedule schedule_to_send;

private:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    Bytes createCommandData( const Schedule & schedule_to_send );

    const Bytes _commandData_to_send;
};

class IM_EX_DEVDB RfnTouScheduleGetConfigurationCommand : public RfnTouScheduleConfigurationCommand,
       InvokerFor<RfnTouScheduleGetConfigurationCommand>
{
public:

    RfnTouScheduleGetConfigurationCommand();

private:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();
};

/**
 * RFN TOU configuration command holiday
 */
class IM_EX_DEVDB RfnTouHolidayConfigurationCommand : public RfnTouConfigurationCommand,
       InvokerFor<RfnTouHolidayConfigurationCommand>
{
public:

    typedef boost::array<CtiDate, 3> Holidays;

    RfnTouHolidayConfigurationCommand(); // read
    RfnTouHolidayConfigurationCommand( const Holidays &holidays_to_send ); // write

    boost::optional<Holidays> getHolidaysReceived() const;

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnCommandResult& result, const TypeLengthValue& tlv );

    void decodeHoliday( RfnCommandResult& result, const Bytes& value );

private:

    boost::optional<Holidays> const _holidays_to_send;
    boost::optional<Holidays>       _holidays_received;
};

/**
 * RFN TOU configuration command enable/disable
 */
class IM_EX_DEVDB RfnTouStateConfigurationCommand : public RfnTouConfigurationCommand,
       InvokerFor<RfnTouStateConfigurationCommand>
{
public:

    RfnTouStateConfigurationCommand(); // read
    RfnTouStateConfigurationCommand( TouState touState_to_send ); // write

    boost::optional<TouState> getTouState() const;

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnCommandResult& result, const TypeLengthValue& tlv );

private:

    boost::optional<TouState> const _touState_to_send;
};

/**
 * RFN TOU set Holiday active
 */
class IM_EX_DEVDB RfnTouSetHolidayActiveCommand : public RfnTouConfigurationCommand, NoResultHandler
{
public:

    RfnTouSetHolidayActiveCommand(); // write

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnCommandResult& result, const TypeLengthValue& tlv );
};

/**
 * RFN TOU cancel Holiday active
 */
class IM_EX_DEVDB RfnTouCancelHolidayActiveCommand : public RfnTouConfigurationCommand, NoResultHandler
{
public:

    RfnTouCancelHolidayActiveCommand(); // write

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnCommandResult& result, const TypeLengthValue& tlv );
};

/**
 * RFN TOU reset registers
 */
class IM_EX_DEVDB RfnTouResetCommand : public RfnTouConfigurationCommand, NoResultHandler
{
public:

    RfnTouResetCommand(); // write

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnCommandResult& result, const TypeLengthValue& tlv );
};

/**
 * RFN TOU reset registers and set to current reading
 */
class IM_EX_DEVDB RfnTouResetZeroCommand : public RfnTouConfigurationCommand, NoResultHandler
{
public:

    RfnTouResetZeroCommand(); // write

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnCommandResult& result, const TypeLengthValue& tlv );
};

/**
 * RFN TOU critical peak command
 */
class IM_EX_DEVDB RfnTouCriticalPeakCommand : public RfnTouConfigurationCommand, NoResultHandler
{
public:

    RfnTouCriticalPeakCommand( const std::string & rate, const unsigned hour, const unsigned minute );

    virtual void prepareCommandData( const CtiTime & now );

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnCommandResult& result, const TypeLengthValue& tlv );

private:

    Rate        _rate;
    unsigned    _hour,
                _minute;
    CtiTime     _utcExpireTime;
};

/**
 * RFN TOU cancel critical peak command
 */
class IM_EX_DEVDB RfnTouCancelCriticalPeakCommand : public RfnTouConfigurationCommand, NoResultHandler
{
public:

    RfnTouCancelCriticalPeakCommand();

protected:

    virtual unsigned char getOperation() const;
    virtual Bytes         getCommandData();

    virtual void decodeTlv( RfnCommandResult& result, const TypeLengthValue& tlv );

};

}
}
}
