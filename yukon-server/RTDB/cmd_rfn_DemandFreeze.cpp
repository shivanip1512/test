
#include "precompiled.h"

#include "cmd_rfn_DemandFreeze.h"
#include "numstr.h"
#include "std_helper.h"
#include "cmd_rfn_helper.h"

#include <boost/assign/list_of.hpp>
#include <boost/optional.hpp>
#include <numeric>
#include <iterator>


namespace Cti        {
namespace Devices    {
namespace Commands   {


namespace   {

const std::map<unsigned char, std::string>  demandFreezeStatusResolver = boost::assign::map_list_of
    ( 0x00, "Success" )
    ( 0x01, "Not Ready" )
    ( 0x02, "Busy" )
    ( 0x03, "Protocol Error" )
    ( 0x04, "Meter Error" )
    ( 0x05, "Illegal Request" )
    ( 0x06, "Aborted Command" )
    ( 0x07, "Timeout" );

}


RfnDemandFreezeCommand::RfnDemandFreezeCommand( const Operation operation )
    : _operation( operation )
{

}


unsigned char RfnDemandFreezeCommand::getCommandCode() const
{
    return CommandCode_Request;
}


unsigned char RfnDemandFreezeCommand::getOperation() const
{
    return _operation;
}


RfnCommand::Bytes RfnDemandFreezeCommand::getCommandData()
{
    RfnCommand::Bytes   data;

    return data;
}


RfnCommandResult RfnDemandFreezeCommand::decodeResponseHeader( const CtiTime now,
                                                                    const RfnResponsePayload & response )
{
    RfnCommandResult  result;

    // We need at least 4 bytes

    validate( Condition( response.size() >= 4, ClientErrors::InvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    // Validate the first 4 bytes

    validate( Condition( response[0] == CommandCode_Response, ClientErrors::InvalidData )
            << "Invalid Response Command Code (" << CtiNumStr(response[0]).xhex(2) << ")" );

    // validate status

    boost::optional<std::string> status = mapFind( demandFreezeStatusResolver, response[1] );

    // if not found in map, then status == Reserved

    result.description += "Status: " + ( status ? *status : "Reserved" ) + " (" + CtiNumStr(response[1]).xhex(2) + ")";

    // validate additional status

    boost::optional<std::string> additionalStatus = findDescriptionForAscAsq( response[2], response[3] );

    validate( Condition( !! additionalStatus, ClientErrors::InvalidData )
            << "Invalid Additional Status (ASC: " << CtiNumStr(response[2]).xhex(2) << ", ASCQ: " << CtiNumStr(response[3]).xhex(2) << ")" );

    result.description += "\nAdditional Status: " + *additionalStatus  + " (ASC: " + CtiNumStr(response[2]).xhex(2) + ", ASCQ: " +  CtiNumStr(response[3]).xhex(2) + ")";

    validateStatus( response[1], response[2], response[3], result.description );

    return result;
}


void RfnDemandFreezeCommand::validateStatus(const unsigned char status, const unsigned char asc, const unsigned char ascq, const std::string &description )
{
    validate( Condition( status == 0x00 && asc == 0x00 && ascq == 0x00, ClientErrors::InvalidData )
            << description );
}


////


RfnDemandFreezeConfigurationCommand::RfnDemandFreezeConfigurationCommand( const unsigned char day_of_freeze )
    :   RfnDemandFreezeCommand( Operation_SetDayOfDemandFreeze ),
        freezeDay( day_of_freeze )
    /*
        0       : freeze disabled
        1 - 31  : day number of freeze
        32 +    : last day of month
    */
{
}


void RfnDemandFreezeConfigurationCommand::invokeResultHandler(RfnCommand::ResultHandler &rh) const
{
    rh.handleCommandResult(*this);
}


RfnCommand::Bytes RfnDemandFreezeConfigurationCommand::getCommandData()
{
    RfnCommand::Bytes   data;

    data.push_back( freezeDay );

    return data;
}


RfnCommandResult RfnDemandFreezeConfigurationCommand::decodeCommand( const CtiTime now,
                                                                   const RfnCommand::RfnResponsePayload & response )
{
    RfnCommandResult  result = decodeResponseHeader( now, response );

    validate( Condition( response.size() == 5, ClientErrors::InvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    validate( Condition( response[4] == 0, ClientErrors::InvalidData )
            << "Invalid TLV count (" << response[4] << ")" );

    return result;
}


////


RfnImmediateDemandFreezeCommand::RfnImmediateDemandFreezeCommand()
    :   RfnDemandFreezeCommand( Operation_ImmediateDemandFreeze )
{

}


void RfnImmediateDemandFreezeCommand::validateStatus( const unsigned char status, const unsigned char asc, const unsigned char ascq, const std::string &description )
{
    validate( Condition( status == 0x00 && asc == 0x04 && ascq == 0x00, ClientErrors::InvalidData )
            << description );
}


RfnCommandResult RfnImmediateDemandFreezeCommand::decodeCommand( const CtiTime now,
                                                               const RfnCommand::RfnResponsePayload & response )
{
    RfnCommandResult  result = decodeResponseHeader( now, response );

    validate( Condition( response.size() == 5, ClientErrors::InvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    validate( Condition( response[4] == 0, ClientErrors::InvalidData )
            << "Invalid TLV count (" << response[4] << ")" );

    return result;
}


////


RfnGetDemandFreezeInfoCommand::RfnGetDemandFreezeInfoCommand()
    :   RfnDemandFreezeCommand( Operation_GetDemandFreezeInfo )
{
}


void RfnGetDemandFreezeInfoCommand::invokeResultHandler(RfnCommand::ResultHandler &rh) const
{
    rh.handleCommandResult(*this);
}


RfnGetDemandFreezeInfoCommand::DemandFreezeData RfnGetDemandFreezeInfoCommand::getDemandFreezeData() const
{
    return _freezeData;
}


namespace {

typedef RfnGetDemandFreezeInfoCommand Cmd;
typedef Cmd::DemandFreezeData Dfd;

const std::map<RfnGetDemandFreezeInfoCommand::DemandFreezeData::DemandRates, std::string> RateNames = boost::assign::map_list_of
    (Dfd::DemandRates_Base,   "Base rate")
    (Dfd::DemandRates_Rate_A, "Rate A")
    (Dfd::DemandRates_Rate_B, "Rate B")
    (Dfd::DemandRates_Rate_C, "Rate C")
    (Dfd::DemandRates_Rate_D, "Rate D")
    (Dfd::DemandRates_Rate_E, "Rate E");

const std::map<RfnGetDemandFreezeInfoCommand::DemandFreezeData::MetricTypes, std::string> MetricNames = boost::assign::map_list_of
    (Dfd::Metric_FrozenPeak_Demand_Delivered, "Peak Delivered Demand")
    (Dfd::Metric_FrozenPeak_Demand_Received,  "Peak Received Demand")
    (Dfd::Metric_FrozenPeak_Vars_Delivered,   "Peak Delivered Vars")
    (Dfd::Metric_FrozenPeak_Vars_Received,    "Peak Received Vars");

std::string describeFreezeData(const RfnGetDemandFreezeInfoCommand::DemandFreezeData &freezeData)
{
    std::ostringstream str;

    if( freezeData.dayOfFreeze )
    {
        str << std::endl << "Day of freeze   : " << static_cast<unsigned>(*freezeData.dayOfFreeze);
    }
    if( freezeData.lastFreezeTime )
    {
        str << std::endl << "Last freeze time: " << CtiTime(*freezeData.lastFreezeTime);
    }
    for each( const Dfd::PeaksPerRate::value_type &ratePeak in freezeData.peakValues )
    {
        const boost::optional<std::string> rateName = mapFind(RateNames, ratePeak.first);

        for each( const Dfd::QuadrantPeakValues::value_type &qpv in ratePeak.second )
        {
            const boost::optional<std::string> metricName = mapFind(MetricNames, qpv.first);

            const Dfd::PeakRecord &peak = qpv.second;

            str << std::endl;

            str << (rateName   ? *rateName : "[Unknown rate " + CtiNumStr(ratePeak.first) + "]");
            str << " " << (metricName ? *metricName : "[Unknown rate " + CtiNumStr(qpv.first) + "]") << " ";

            str << " " << (peak.value ? CtiNumStr(*peak.value).toString() : "(value missing)");
            str << " @ " << (peak.timestamp ? CtiTime(*peak.timestamp).asString() : "(timestamp missing)");
        }
    }

    return str.str();
}

struct RateMetric
{
    Dfd::DemandRates rate;
    Dfd::MetricTypes metric;

    static RateMetric make( Dfd::DemandRates rate, Dfd::MetricTypes metric )
    {
        RateMetric rm = { rate, metric };

        return rm;
    }
};


const std::map<unsigned char, RateMetric> ValueTlvToPeakMetric = boost::assign::map_list_of
    (Cmd::TlvType_FrozenDeliveredPeakDemandTotal, RateMetric::make(Dfd::DemandRates_Base,   Dfd::Metric_FrozenPeak_Demand_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakDemandRateA, RateMetric::make(Dfd::DemandRates_Rate_A, Dfd::Metric_FrozenPeak_Demand_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakDemandRateB, RateMetric::make(Dfd::DemandRates_Rate_B, Dfd::Metric_FrozenPeak_Demand_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakDemandRateC, RateMetric::make(Dfd::DemandRates_Rate_C, Dfd::Metric_FrozenPeak_Demand_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakDemandRateD, RateMetric::make(Dfd::DemandRates_Rate_D, Dfd::Metric_FrozenPeak_Demand_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakDemandRateE, RateMetric::make(Dfd::DemandRates_Rate_E, Dfd::Metric_FrozenPeak_Demand_Delivered))

    (Cmd::TlvType_FrozenReceivedPeakDemandTotal,  RateMetric::make(Dfd::DemandRates_Base,   Dfd::Metric_FrozenPeak_Demand_Received))
    (Cmd::TlvType_FrozenReceivedPeakDemandRateA,  RateMetric::make(Dfd::DemandRates_Rate_A, Dfd::Metric_FrozenPeak_Demand_Received))
    (Cmd::TlvType_FrozenReceivedPeakDemandRateB,  RateMetric::make(Dfd::DemandRates_Rate_B, Dfd::Metric_FrozenPeak_Demand_Received))
    (Cmd::TlvType_FrozenReceivedPeakDemandRateC,  RateMetric::make(Dfd::DemandRates_Rate_C, Dfd::Metric_FrozenPeak_Demand_Received))
    (Cmd::TlvType_FrozenReceivedPeakDemandRateD,  RateMetric::make(Dfd::DemandRates_Rate_D, Dfd::Metric_FrozenPeak_Demand_Received))
    (Cmd::TlvType_FrozenReceivedPeakDemandRateE,  RateMetric::make(Dfd::DemandRates_Rate_E, Dfd::Metric_FrozenPeak_Demand_Received))

    (Cmd::TlvType_FrozenDeliveredPeakVarTotal,    RateMetric::make(Dfd::DemandRates_Base,   Dfd::Metric_FrozenPeak_Vars_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakVarRateA,    RateMetric::make(Dfd::DemandRates_Rate_A, Dfd::Metric_FrozenPeak_Vars_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakVarRateB,    RateMetric::make(Dfd::DemandRates_Rate_B, Dfd::Metric_FrozenPeak_Vars_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakVarRateC,    RateMetric::make(Dfd::DemandRates_Rate_C, Dfd::Metric_FrozenPeak_Vars_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakVarRateD,    RateMetric::make(Dfd::DemandRates_Rate_D, Dfd::Metric_FrozenPeak_Vars_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakVarRateE,    RateMetric::make(Dfd::DemandRates_Rate_E, Dfd::Metric_FrozenPeak_Vars_Delivered))

    (Cmd::TlvType_FrozenReceivedPeakVarTotal,     RateMetric::make(Dfd::DemandRates_Base,   Dfd::Metric_FrozenPeak_Vars_Received))
    (Cmd::TlvType_FrozenReceivedPeakVarRateA,     RateMetric::make(Dfd::DemandRates_Rate_A, Dfd::Metric_FrozenPeak_Vars_Received))
    (Cmd::TlvType_FrozenReceivedPeakVarRateB,     RateMetric::make(Dfd::DemandRates_Rate_B, Dfd::Metric_FrozenPeak_Vars_Received))
    (Cmd::TlvType_FrozenReceivedPeakVarRateC,     RateMetric::make(Dfd::DemandRates_Rate_C, Dfd::Metric_FrozenPeak_Vars_Received))
    (Cmd::TlvType_FrozenReceivedPeakVarRateD,     RateMetric::make(Dfd::DemandRates_Rate_D, Dfd::Metric_FrozenPeak_Vars_Received))
    (Cmd::TlvType_FrozenReceivedPeakVarRateE,     RateMetric::make(Dfd::DemandRates_Rate_E, Dfd::Metric_FrozenPeak_Vars_Received));

const std::map<unsigned char, RateMetric> TimestampTlvToPeakMetric = boost::assign::map_list_of
    (Cmd::TlvType_FrozenDeliveredPeakDemandTime,      RateMetric::make(Dfd::DemandRates_Base,   Dfd::Metric_FrozenPeak_Demand_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakDemandRateATime, RateMetric::make(Dfd::DemandRates_Rate_A, Dfd::Metric_FrozenPeak_Demand_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakDemandRateBTime, RateMetric::make(Dfd::DemandRates_Rate_B, Dfd::Metric_FrozenPeak_Demand_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakDemandRateCTime, RateMetric::make(Dfd::DemandRates_Rate_C, Dfd::Metric_FrozenPeak_Demand_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakDemandRateDTime, RateMetric::make(Dfd::DemandRates_Rate_D, Dfd::Metric_FrozenPeak_Demand_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakDemandRateETime, RateMetric::make(Dfd::DemandRates_Rate_E, Dfd::Metric_FrozenPeak_Demand_Delivered))

    (Cmd::TlvType_FrozenReceivedPeakDemandTime,       RateMetric::make(Dfd::DemandRates_Base,   Dfd::Metric_FrozenPeak_Demand_Received))
    (Cmd::TlvType_FrozenReceivedPeakDemandRateATime,  RateMetric::make(Dfd::DemandRates_Rate_A, Dfd::Metric_FrozenPeak_Demand_Received))
    (Cmd::TlvType_FrozenReceivedPeakDemandRateBTime,  RateMetric::make(Dfd::DemandRates_Rate_B, Dfd::Metric_FrozenPeak_Demand_Received))
    (Cmd::TlvType_FrozenReceivedPeakDemandRateCTime,  RateMetric::make(Dfd::DemandRates_Rate_C, Dfd::Metric_FrozenPeak_Demand_Received))
    (Cmd::TlvType_FrozenReceivedPeakDemandRateDTime,  RateMetric::make(Dfd::DemandRates_Rate_D, Dfd::Metric_FrozenPeak_Demand_Received))
    (Cmd::TlvType_FrozenReceivedPeakDemandRateETime,  RateMetric::make(Dfd::DemandRates_Rate_E, Dfd::Metric_FrozenPeak_Demand_Received))

    (Cmd::TlvType_FrozenDeliveredPeakVarTime,         RateMetric::make(Dfd::DemandRates_Base,   Dfd::Metric_FrozenPeak_Vars_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakVarRateATime,    RateMetric::make(Dfd::DemandRates_Rate_A, Dfd::Metric_FrozenPeak_Vars_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakVarRateBTime,    RateMetric::make(Dfd::DemandRates_Rate_B, Dfd::Metric_FrozenPeak_Vars_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakVarRateCTime,    RateMetric::make(Dfd::DemandRates_Rate_C, Dfd::Metric_FrozenPeak_Vars_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakVarRateDTime,    RateMetric::make(Dfd::DemandRates_Rate_D, Dfd::Metric_FrozenPeak_Vars_Delivered))
    (Cmd::TlvType_FrozenDeliveredPeakVarRateETime,    RateMetric::make(Dfd::DemandRates_Rate_E, Dfd::Metric_FrozenPeak_Vars_Delivered))

    (Cmd::TlvType_FrozenReceivedPeakVarTime,          RateMetric::make(Dfd::DemandRates_Base,   Dfd::Metric_FrozenPeak_Vars_Received))
    (Cmd::TlvType_FrozenReceivedPeakVarRateATime,     RateMetric::make(Dfd::DemandRates_Rate_A, Dfd::Metric_FrozenPeak_Vars_Received))
    (Cmd::TlvType_FrozenReceivedPeakVarRateBTime,     RateMetric::make(Dfd::DemandRates_Rate_B, Dfd::Metric_FrozenPeak_Vars_Received))
    (Cmd::TlvType_FrozenReceivedPeakVarRateCTime,     RateMetric::make(Dfd::DemandRates_Rate_C, Dfd::Metric_FrozenPeak_Vars_Received))
    (Cmd::TlvType_FrozenReceivedPeakVarRateDTime,     RateMetric::make(Dfd::DemandRates_Rate_D, Dfd::Metric_FrozenPeak_Vars_Received))
    (Cmd::TlvType_FrozenReceivedPeakVarRateETime,     RateMetric::make(Dfd::DemandRates_Rate_E, Dfd::Metric_FrozenPeak_Vars_Received));
}


RfnCommandResult RfnGetDemandFreezeInfoCommand::decodeCommand( const CtiTime now,
                                                                    const RfnCommand::RfnResponsePayload & response )
{
    struct Accumulator
    {
        unsigned int operator()( unsigned int acc, unsigned char val )
        {
            return ( acc << 8 ) + val;
        }
    };

    RfnCommandResult result = decodeResponseHeader( now, response );

    validate( Condition( response.size() >= 5, ClientErrors::InvalidData )
            << "Invalid Response length (" << response.size() << ")" );

    unsigned tlvCount   = 0;
    unsigned totalBytes = 5;

    for ( RfnCommand::RfnResponsePayload::const_iterator current = response.begin() + 5;
          current <= response.end() - 2;
          ++tlvCount )
    {
        unsigned char tlvType   = *current++;
        unsigned char tlvLength = *current++;

        totalBytes += 2;

        validate( Condition( totalBytes + tlvLength <= response.size(), ClientErrors::InvalidData )
                << "Invalid TLV length (" << std::distance(current, response.end()) << ") expected " << tlvLength );

        unsigned int  tlvValue  = std::accumulate( current, current + tlvLength, 0u, Accumulator() );

        std::advance( current, tlvLength );
        totalBytes += tlvLength;

        switch ( tlvType )
        {
            case TlvType_ScheduledDayOfDemandFreeze:
            {
                _freezeData.dayOfFreeze = tlvValue;
                break;
            }
            case TlvType_TimeLastDemandFreezeOccured:
            {
                _freezeData.lastFreezeTime = tlvValue;
                break;
            }
            default:
            {
                if( const boost::optional<RateMetric> rateMetric = mapFind(ValueTlvToPeakMetric, tlvType) )
                {
                    _freezeData.peakValues[rateMetric->rate][rateMetric->metric].value = tlvValue;

                    break;
                }
                if( const boost::optional<RateMetric> rateMetric = mapFind(TimestampTlvToPeakMetric, tlvType) )
                {
                    _freezeData.peakValues[rateMetric->rate][rateMetric->metric].timestamp = tlvValue;

                    break;
                }

                throw RfnCommand::CommandException( ClientErrors::InvalidData,
                                                    "Missing decode for TLV type (" + CtiNumStr(tlvType).xhex(2) + ")" );
            }
        }
    }

    validate( Condition( response[4] == tlvCount, ClientErrors::InvalidData )
            << "Invalid TLV count (" << tlvCount << ") expected " << response[4] );

    result.description += describeFreezeData(_freezeData);

    return result;
}


}
}
}

