#include "precompiled.h"

#include "std_helper.h"
#include "cmd_rfn_ConfigNotification.h"
#include "cmd_rfn_helper.h"

#include <boost/range/adaptor/map.hpp>
#include <boost/range/numeric.hpp>

using namespace std;
using namespace std::string_literals;

namespace Cti {
namespace Devices {
namespace Commands {

RfnCommandResult RfnConfigNotificationCommand::decodeCommand(const CtiTime now, const RfnResponsePayload & response)
{
    constexpr int HeaderLength = 3;

    validate(Condition(response.size() >= HeaderLength, ClientErrors::DataMissing)
        << "Response smaller than header length (" << response.size() << " < " << HeaderLength << ")");

    validate(Condition(response[0] == Command_DeviceConfigurationResponse, ClientErrors::UnknownCommandReceived)
        << "Unexpected response command (" << (int)response[0] << " != " << Command_DeviceConfigurationResponse);

    uint16_t tlvCount = response[1] << 8 | response[2];

    if( ! tlvCount )
    {
        return "Empty";
    }

    std::vector<TLV> tlvs;

    size_t pos = 3;

    for( int i = 0; i < tlvCount; i++ )
    {
        constexpr auto TlvHeaderLength = 4;

        validate(Condition(pos + TlvHeaderLength <= response.size(), ClientErrors::DataMissing)
            << "Incomplete TLV header (" << (pos + TlvHeaderLength) << " > " << response.size());

        TLV tlv {
            static_cast<uint16_t>(response[pos + 0] << 8 | response[pos + 1]),
            static_cast<uint16_t>(response[pos + 2] << 8 | response[pos + 3]) };

        pos += TlvHeaderLength;

        validate(Condition(pos + tlv.length <= response.size(), ClientErrors::DataMissing)
            << "Incomplete TLV data (" << (pos + tlv.length) << " > " << response.size());

        const auto dataBegin = response.cbegin() + pos;

        tlv.data.assign(dataBegin, dataBegin + tlv.length);

        pos += tlv.length;

        tlvs.emplace_back(std::move(tlv));
    }

    return decodeTlvs(std::move(tlvs));
}

std::string RfnConfigNotificationCommand::decodeTlvs(const std::vector<TLV> tlvs)
{
    using Self = RfnConfigNotificationCommand;

    auto tlvMethods = 
    {
        &Self::decodeTouEnableDisable,
        &Self::decodeTouSchedule,
        &Self::decodeTouHoliday,
        &Self::decodeDemandFreezeDay,
        &Self::decodeIntervalRecordingReporting,
        &Self::decodeChannelSelection,
        &Self::decodeDisconnect,
        &Self::decodeVoltageProfile,
        &Self::decodeC2sxDisplay,
        &Self::decodeFocusAlDisplay,
        &Self::decodeOvUvAlarm,
        &Self::decodeTemperature,
    };
    
    std::vector<std::string> results;

    for( const auto & tlv : tlvs )
    {
        if( tlv.type <= tlvMethods.size() )
        {
            try
            {
                validate(Condition( ! tlv.data.empty(), ClientErrors::DataMissing)
                    << "Empty payload for TLV type " << tlv.type);

                const auto method = *(tlvMethods.begin() + tlv.type - 1);

                results.emplace_back((this->*method)(tlv.data));
            }
            catch( const YukonErrorException &ex )
            {
                results.emplace_back(ex.error_description);
            }
        }
        else
        {
            results.emplace_back("Unknown TLV type " + std::to_string(tlv.type));
        }
    }

    for( auto & s : results )
    {
        //  indent the results
        boost::replace_all(s, "\n", "\n    ");
    }

    return boost::join(results, "\n");
}


std::string RfnConfigNotificationCommand::decodeTouEnableDisable(Bytes payload)
{
    touEnabled = payload[0]
        ? RfnTouConfigurationCommand::TouEnable
        : RfnTouConfigurationCommand::TouDisable;

    return "TOU enable configuration:\n"s + 
        (touEnabled == RfnTouConfigurationCommand::TouEnable
            ? "TOU enabled"
            : "TOU disabled");
}

std::string RfnConfigNotificationCommand::decodeTouSchedule(Bytes payload)
{
    validate(Condition(payload.size() >= 56, ClientErrors::InvalidData)
        << "TOU Schedule payload too small - (" << payload.size() << " < 56)");

    using T = RfnTouScheduleConfigurationCommand;

    T::Schedule s;

    const auto Prefix = RfnTouConfigurationCommand::SchedulePrefix;

    FormattedList l;

    std::vector<int> schedules;

    for( auto day = 0; day < 8; day++ )
    {
        const auto scheduleNumber = getValueFromBits_lEndian(payload, day * 3, 3);

        s._dayTable.push_back(Prefix + std::to_string(scheduleNumber + 1));

        schedules.push_back(scheduleNumber + 1);
    }

    l.add("Day table") << boost::join(schedules | boost::adaptors::transformed([](int i) { return std::to_string(i); }), ", ");

    size_t pos = 3;

    for( auto schedule : { T::Schedule1, T::Schedule2, T::Schedule3, T::Schedule4 } )
    {
        T::DailyTimes switchTimes;

        for( auto switchTime = 0; switchTime < 5; switchTime++ )
        {
            uint16_t minutes = payload[pos] << 8 | payload[pos + 1];

            pos += 2;

            switchTimes.emplace_back(CtiNumStr(minutes / 60).zpad(2) + ":" + CtiNumStr(minutes % 60).zpad(2));
        }

        l.add("Schedule " + std::to_string(schedule + 1) + " switch times") << boost::join(switchTimes, ", ");

        s._times.emplace(schedule, std::move(switchTimes));
    }

    for( auto schedule : { T::Schedule1, T::Schedule2, T::Schedule3, T::Schedule4 } )
    {
        T::DailyRates rates;

        for( auto timeRate = 0; timeRate < 6; timeRate++ )
        {
            auto rate = getValueFromBits_lEndian(payload, pos * 8 + timeRate * 3, 3);

            rates.emplace_back(1, rate + 'A');
        }

        pos += 3;

        l.add("Schedule " + std::to_string(schedule + 1) + " rates") << boost::join(rates, ", ");

        s._rates.emplace(schedule, std::move(rates));
    }

    s._defaultRate = std::string(1, payload[55] + 'A');

    l.add("Default rate") << s._defaultRate;

    touSchedule = s;

    return "TOU schedule configuration:" + l.toString();
}

std::string RfnConfigNotificationCommand::decodeTouHoliday(Bytes payload)
{
    validate(Condition(payload.size() >= 12, ClientErrors::InvalidData)
        << "TOU Holiday payload too small - (" << payload.size() << " < 12)");

    std::array<CtiDate, 3> dates;

    std::string description = "TOU holiday configuration:";

    for( size_t dateIndex = 0; dateIndex < 3; ++dateIndex )
    {
        const uint32_t date =
            payload[dateIndex * 4 + 0] << 24 | 
            payload[dateIndex * 4 + 1] << 16 |
            payload[dateIndex * 4 + 2] <<  8 |
            payload[dateIndex * 4 + 3];

        dates[dateIndex] = CtiTime(date);

        description += "\n Date " + std::to_string(dateIndex + 1) + " - " + dates[dateIndex].asString();
    }

    touHolidays = dates;

    return description;
}

std::string RfnConfigNotificationCommand::decodeDemandFreezeDay(Bytes payload)
{
    demandFreezeDay = payload[0];

    return "Demand freeze configuration:\nDemand freeze day: " + std::to_string(*demandFreezeDay);
}

std::string RfnConfigNotificationCommand::decodeIntervalRecordingReporting(Bytes payload)
{
    validate(Condition(payload.size() >= 9, ClientErrors::DataMissing)
        << "Interval Recording payload too small - (" << payload.size() << " < " << 9);

    const auto intervalCount = payload[8];
    const auto intervalSize = intervalCount * 2;

    validate(Condition(payload.size() >= 9 + intervalSize, ClientErrors::DataMissing)
        << "Interval Recording payload too small - (" << payload.size() << " < " << 9 + intervalSize);

    IntervalRecording r;

    FormattedList l;

    r.recordingInterval = payload[0] | payload[1] << 8 | payload[2] << 16 | payload[3] << 24;
    r.reportingInterval = payload[4] | payload[5] << 8 | payload[6] << 16 | payload[7] << 24;

    l.add("Recording interval") << std::chrono::seconds( r.recordingInterval );
    l.add("Reporting interval") << std::chrono::seconds( r.reportingInterval );

    auto pos = 9;

    for( auto metric = 0; metric < intervalCount; metric++ )
    {
        const auto metricId = payload[pos] | payload[pos + 1] << 8;

        if( ! r.intervalMetrics.insert(metricId).second )
        {
            CTILOG_WARN(dout, "Duplicate metric ID " << metricId);
        }

        pos += 2;
    }

    l.add("Interval metrics") << boost::join(r.intervalMetrics | boost::adaptors::transformed([](int i) { return std::to_string(i); }), ", ");

    intervalRecording = r;

    return "Interval recording configuration:" + l.toString();
}

std::string RfnConfigNotificationCommand::decodeChannelSelection(Bytes payload)
{
    auto channelCount = payload[0];
    auto channelSize = channelCount * 2;

    validate(Condition(payload.size() > channelSize, ClientErrors::DataMissing)
        << "Channel Selection payload too small - (" << payload.size() << " < " << channelSize);

    RfnChannelConfigurationCommand::MetricIds metrics;

    for( auto pos = 1; pos < channelSize; pos += 2 )
    {
        const auto metricId = payload[pos] | payload[pos + 1] << 8;

        if( ! metrics.insert(metricId).second )
        {
            CTILOG_WARN(dout, "Duplicate metric ID " << metricId);
        }
    }

    channelSelections = metrics;

    return "Channel selection configuration:"
        "\nMetric IDs: " + boost::join(metrics | boost::adaptors::transformed([](int i) { return std::to_string(i); }), ", ");
}

std::string RfnConfigNotificationCommand::decodeDisconnect(Bytes payload)
{
    using RDCC = RfnRemoteDisconnectConfigurationCommand;

    validate(Condition(payload.size() >= 2, ClientErrors::DataMissing)
        << "Disconnect payload too small - (" << payload.size() << " < " << 2);

    Disconnect d;

    auto mode = payload[0];

    d.reconnect = payload[1] ? RDCC::Reconnect_Immediate : RDCC::Reconnect_Arm;

    FormattedList l;

    switch( mode )
    {
    case 1:
    {
        l.add("Disconnect mode") << "on demand";

        d.disconnectMode = RDCC::DisconnectMode_OnDemand;

        l.add("Reconnect method") << payload[1];

        break;
    }
    case 2:
    {
        validate(Condition(payload.size() >= 6, ClientErrors::DataMissing)
            << "Disconnect payload too small - (" << payload.size() << " < " << 6);

        l.add("Disconnect mode") << "demand threshold";

        d.disconnectMode = RDCC::DisconnectMode_DemandThreshold;

        d.demandInterval  = payload[2];
        d.demandThreshold = payload[3] * 0.1;
        d.connectDelay    = payload[4];
        d.maxDisconnects  = payload[5];

        l.add("Reconnect method") << payload[1];
        l.add("Demand interval")  << std::chrono::minutes(d.demandInterval);
        l.add("Demand threshold") << d.demandThreshold << "kW";
        l.add("Connect delay")    << std::chrono::minutes(d.connectDelay);
        l.add("Max disconnects")  << d.maxDisconnects;

        break;
    }
    case 3:
    {
        validate(Condition(payload.size() >= 6, ClientErrors::DataMissing)
            << "Disconnect payload too small - (" << payload.size() << " < " << 6);

        l.add("Disconnect mode") << "cycling";

        d.disconnectMode = RDCC::DisconnectMode_Cycling;

        d.disconnectTime = payload[2] | payload[3] << 8;
        d.connectTime    = payload[4] | payload[5] << 8;

        l.add("Reconnect method") << payload[1];
        l.add("Disconnect time") << std::chrono::minutes(d.disconnectTime.value());
        l.add("Connect time")    << std::chrono::minutes(d.connectTime.value());

        break;
    }
    default:
        throw YukonErrorException(ClientErrors::InvalidData, "Unknown disconnect mode " + std::to_string(mode));
    }

    disconnect = d;

    return "Disconnect configuration:" + l.toString();
}

std::string RfnConfigNotificationCommand::decodeVoltageProfile(Bytes payload)
{
    validate(Condition(payload.size() >= 2, ClientErrors::DataMissing)
        << "Voltage Profile payload too small - (" << payload.size() << " < " << 2);

    VoltageProfile vp;

    vp.voltageDemandInterval  = payload[0] * RfnVoltageProfileConfigurationCommand::SecondsPerIncrement;
    vp.voltageProfileInterval = payload[1];

    voltageProfile = vp;

    return "Voltage profile configuration:" + FormattedList::of(
        "Voltage demand interval",  std::chrono::seconds(vp.voltageDemandInterval),
        "Voltage profile interval", std::chrono::minutes(vp.voltageProfileInterval));
}

std::string RfnConfigNotificationCommand::decodeC2sxDisplay(Bytes payload)
{
    auto displayItemCount = payload[0];
    auto payloadSize = displayItemCount * 2 + 1;

    validate(Condition(payload.size() >= payloadSize, ClientErrors::DataMissing)
        << "C2SX Display payload too small - (" << payload.size() << " < " << payloadSize);

    C2sxDisplay d;

    FormattedList l;

    for( auto pos = 1; pos < payloadSize - 1; pos += 2 )
    {
        auto slot  = payload[pos];
        auto value = payload[pos + 1];

        switch( slot )
        {
        case RfnCentronLcdConfigurationCommand::Slot_CycleDelay:
            d.lcdCycleTime = value;
            l.add("Cycle time") << value;
            break;
        case RfnCentronLcdConfigurationCommand::Slot_DigitConfiguration:
            d.displayDigits = value;
            l.add("Display digits") << value;
            break;
        case RfnCentronLcdConfigurationCommand::Slot_DisconnectDisplay:
            d.disconnectDisplay = value;
            l.add("Disconnect display") << value;
            break;
        default:
            d.displayItems.emplace(slot, value);
            l.add("Slot " + std::to_string(slot)) << value;
            break;
        }
    }

    c2sxDisplay = d;

    return "C2SX display:" + l.toString();
}

using FL = RfnFocusAlLcdConfigurationCommand;

const std::map<uint8_t, FL::Metrics> focusMetricLookup {
    { 0x00, FL::deliveredKwh6x1,  },
    { 0x01, FL::deliveredKwh5x1,  },
    { 0x02, FL::deliveredKwh4x1,  },
    { 0x03, FL::deliveredKwh4x10, },
    { 0x04, FL::reverseKwh6x1,    },
    { 0x05, FL::reverseKwh5x1,    },
    { 0x06, FL::reverseKwh4x1,    },
    { 0x07, FL::receivedKwh4x10,  },
    { 0x08, FL::totalKwh6x1,      },
    { 0x09, FL::totalKwh5x1,      },
    { 0x0a, FL::totalKwh4x1,      },
    { 0x0b, FL::totalKwh4x10,     },
    { 0x0c, FL::netKwh6x1,        },
    { 0x0d, FL::netKwh5x1,        },
    { 0x0e, FL::netKwh4x1,        },
    { 0x0f, FL::netKwh4x10,       },
    { 0x10, FL::diagnosticFlags,  },
    { 0x11, FL::allSegments,      }, 
    { 0x12, FL::firmwareVersion,  } };

std::string RfnConfigNotificationCommand::decodeFocusAlDisplay(Bytes payload)
{
    auto displayItemCount = payload[0];
    auto payloadSize = displayItemCount * 3 + 2;

    validate(Condition(payload.size() >= payloadSize, ClientErrors::DataMissing)
        << "Focus AL Display payload too small - (" << payload.size() << " < " << payloadSize);

    FocusDisplay d;

    d.displayItemDuration = payload[1];

    FormattedList l;

    l.add("Display time") << std::chrono::seconds(d.displayItemDuration);

    const auto convertChar = [](unsigned char c) {
        if( c >= 0x30 && c <= 0x39 )  return (char)c;
        if( c == 0x3a )  return '$';  //  all segments
        if( c == 0x3b )  return '*';  
        if( c == 0x3c )  return '_';
        if( c == 0x3d || c == 0x3e )  return (char)c;
        if( c == 0x3f )  return '+';
        if( c == 0x40 )  return '-';
        if( c >= 0x41 && c <= 0x5a )  return (char)c;
        return '?';
    };

    for( auto pos = 2; pos < payloadSize - 3; pos += 3 )
    {
        auto metric = mapFind(focusMetricLookup, payload[pos]);

        validate( Condition( !! metric, ClientErrors::InvalidData )
            << "Unknown display metric (" << payload[pos] << ")");

        d.displayItems.emplace_back(*metric);

        l.add("Slot " + std::to_string(payload[pos]))
            << convertChar(payload[pos + 1]) << " "
            << convertChar(payload[pos + 2]);
    }

    focusDisplay = d;

    return "Focus AL display:" + l.toString();
}

std::string RfnConfigNotificationCommand::decodeOvUvAlarm(Bytes payload)
{
    validate(Condition(payload.size() >= 18, ClientErrors::DataMissing)
        << "Focus AL Display payload too small - (" << payload.size() << " < " << 18);

    RfnGetOvUvAlarmConfigurationCommand::AlarmConfiguration a;

    const auto meterId = payload[0];
    const auto eventId = payload[1] | payload[2] << 8;
    a.ovuvEnabled                = payload[3];
    a.ovuvAlarmReportingInterval = payload[4];
    a.ovuvAlarmRepeatInterval    = payload[5];
    a.ovuvAlarmRepeatCount       = payload[6];
    const auto clearAlarmRepeatCount = payload[7];
    const auto severity          = payload[8];
    double setThresholdValue =
        payload[9] |
        payload[10]  << 8 |
        payload[11] << 16 |
        payload[12] << 24;
    setThresholdValue /= 1000.0;
    const auto unitOfMeasure = payload[13];
    const auto uomModifier1  = payload[14] | payload[15] << 8;
    const auto uomModifier2  = payload[16] | payload[17] << 8;

    if( eventId == RfnOvUvConfigurationCommand::EventID::OverVoltage )
    {
        a.ovThreshold = setThresholdValue;
    }
    else
    {
        a.uvThreshold = setThresholdValue;
    }

    FormattedList l;

    l.add("Meter ID") << meterId;
    l.add("Event ID") << eventId;
    l.add("OV/UV alarming enabled")       << a.ovuvEnabled;
    l.add("New alarm reporting interval") << std::chrono::minutes(a.ovuvAlarmReportingInterval);
    l.add("Alarm repeat interval")        << std::chrono::minutes(a.ovuvAlarmRepeatInterval);
    l.add("Set alarm repeat count")       << a.ovuvAlarmRepeatCount;
    l.add("Clear alarm repeat count")     << clearAlarmRepeatCount;
    l.add("Severity")                     << severity;
    l.add("Set threshold value")          << setThresholdValue;
    l.add("Unit of measure") << unitOfMeasure;
    l.add("UOM modifier 1")  << uomModifier1;
    l.add("UOM modifier 2")  << uomModifier2;

    ovuv = a;

    return "OV/UV configuration:" + l.toString();
}

std::string RfnConfigNotificationCommand::decodeTemperature(Bytes payload)
{
    validate(Condition(payload.size() >= 7, ClientErrors::DataMissing)
        << "Temperature payload too small - (" << payload.size() << " < " << 7);

    RfnTemperatureAlarmCommand::AlarmConfiguration a;

    a.alarmEnabled               = payload[0];
    a.alarmHighTempThreshold     = payload[1] | payload[2] << 8;
    auto lowTemperatureThreshold = payload[3] | payload[4] << 8;
    a.alarmRepeatInterval        = payload[5];
    a.alarmRepeatCount           = payload[6];

    FormattedList l;

    l.add("Temperature alarming enabled") << a.alarmEnabled;
    l.add("High temp threshold") << a.alarmHighTempThreshold;
    l.add("Low temp threshold") << lowTemperatureThreshold;
    l.add("Repeat interval") << std::chrono::minutes(a.alarmRepeatInterval);
    l.add("Max repeats") << a.alarmRepeatCount;

    temperature = a;

    return "Temperature alarm configuration:" + l.toString();
}

std::string RfnConfigNotificationCommand::getCommandName()
{
    return "Device Configuration Request";
}

auto RfnConfigNotificationCommand::getApplicationServiceId() const -> ASID
{
    return ASID::HubMeterCommandSet;
}

unsigned char RfnConfigNotificationCommand::getUnsolicitedCommandCode() 
{ 
    return Command_DeviceConfigurationResponse; 
}

unsigned char RfnConfigNotificationCommand::getCommandCode() const 
{ 
    return Command_DeviceConfigurationRequest; 
}

unsigned char RfnConfigNotificationCommand::getOperation() const 
{ 
    return 0;  //  unused
}

auto RfnConfigNotificationCommand::getCommandHeader() -> Bytes 
{ 
    return { getCommandCode() }; 
}

auto RfnConfigNotificationCommand::getCommandData() -> Bytes
{ 
    return { }; 
}

}
}
}
