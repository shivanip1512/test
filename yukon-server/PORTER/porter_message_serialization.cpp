#include "precompiled.h"

#include "porter_message_serialization.h"

#include "std_helper.h"
#include "boost_helper.h"
#include "amq_connection.h"

#include <boost/bimap.hpp>

using namespace std;

namespace Cti::Messaging::Serialization {

namespace {

using ThriftDurationKeys = Thrift::Porter::DynamicPaoInfoDurationKeys::type;
using YukonDurationKeys = Porter::DynamicPaoInfoDurationKeys;

static const auto durationKeyLookup = make_bimap<ThriftDurationKeys, YukonDurationKeys>({
    { ThriftDurationKeys::RFN_VOLTAGE_PROFILE_INTERVAL, YukonDurationKeys::RfnVoltageProfileInterval },
    { ThriftDurationKeys::MCT_IED_LOAD_PROFILE_INTERVAL, YukonDurationKeys::MctIedLoadProfileInterval }});

using ThriftTimestampKeys = Thrift::Porter::DynamicPaoInfoTimestampKeys::type;
using YukonTimestampKeys = Porter::DynamicPaoInfoTimestampKeys;

static const auto timestampKeyLookup = make_bimap<ThriftTimestampKeys, YukonTimestampKeys>({
    { ThriftTimestampKeys::RFN_VOLTAGE_PROFILE_ENABLED_UNTIL, YukonTimestampKeys::RfnVoltageProfileEnabledUntil }});

using ThriftPercentageKeys = Thrift::Porter::DynamicPaoInfoPercentageKeys::type;
using YukonPercentageKeys = Porter::DynamicPaoInfoPercentageKeys;

static const auto percentageKeyLookup = make_bimap<ThriftPercentageKeys, YukonPercentageKeys>({
    { ThriftPercentageKeys::METER_PROGRAMMING_PROGRESS, YukonPercentageKeys::MeterProgrammingProgress }});

template<typename Key, typename Value, typename ThriftKey, typename ValueMapper, typename ThriftValue = std::invoke_result_t<ValueMapper, Value>>
std::map<ThriftKey, ThriftValue> translateMap(std::string type, const std::map<Key, Value>& nativeMap, const typename boost::bimaps::bimap<ThriftKey, Key> & keyMapping, ValueMapper valueMapper)
{
    std::map<ThriftKey, ThriftValue> output;

    for( const auto& [nativeKey, nativeValue] : nativeMap )
    {
        if( auto thriftKey = mapFind(keyMapping.right, nativeKey) )
        {
            output.emplace(*thriftKey, valueMapper(nativeValue));
        }
        else
        {
            CTILOG_ERROR(dout, "Unmapped " << type << " key " << static_cast<int>(nativeKey));
        }
    }

    return output;
}

template<typename Key, typename ThriftKey>
std::set<Key> translateKeys(std::string type, const std::set<ThriftKey>& thriftKeys, const typename boost::bimaps::bimap<ThriftKey, Key> & keyMapping)
{
    std::set<Key> output;

    for( const auto& thriftKey : thriftKeys )
    {
        if( auto nativeKey = mapFind(keyMapping.left, thriftKey) )
        {
            output.emplace(*nativeKey);
        }
        else
        {
            CTILOG_ERROR(dout, "Unmapped " << type << " key " << static_cast<int>(thriftKey));
        }
    }

    return output;
}

long long as_milliseconds(std::chrono::system_clock::time_point tp)
{
    return std::chrono::duration_cast<std::chrono::milliseconds>(tp.time_since_epoch()).count();
}

}

using DpiReqMsg = Porter::DynamicPaoInfoRequestMsg;
using DpiRspMsg = Porter::DynamicPaoInfoResponseMsg;

MessagePtr<Thrift::Porter::DynamicPaoInfoResponse>::type populateThrift( const DpiRspMsg & imsg )
{
    MessagePtr<Thrift::Porter::DynamicPaoInfoResponse>::type omsg( new Thrift::Porter::DynamicPaoInfoResponse );

    omsg->__set__deviceId( imsg.deviceId );

    omsg->_durationValues = translateMap("duration", imsg.durationValues, durationKeyLookup, 
        [](std::chrono::milliseconds ms) { 
            return ms.count(); });

    omsg->_timestampValues = translateMap("timestamp", imsg.timestampValues, timestampKeyLookup, as_milliseconds);

    omsg->_percentageValues = translateMap("percentage", imsg.percentageValues, percentageKeyLookup, 
        [](double d) { 
            return d; });

    return omsg;
}

MessagePtr<DpiReqMsg>::type populateMessage(const Thrift::Porter::DynamicPaoInfoRequest& imsg)
{
    MessagePtr<DpiReqMsg>::type omsg(new DpiReqMsg);

    omsg->deviceId = imsg._deviceId;

    omsg->durationKeys = translateKeys("duration", imsg._durationKeys, durationKeyLookup);
    omsg->timestampKeys = translateKeys("timestamp", imsg._timestampKeys, timestampKeyLookup);
    omsg->percentageKeys = translateKeys("percentage", imsg._percentageKeys, percentageKeyLookup);

    return omsg;
}


template<>
boost::optional<DpiReqMsg> MessageSerializer<DpiReqMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage &msg)
{
    try
    {
        auto tmsg = DeserializeThriftBytes<Thrift::Porter::DynamicPaoInfoRequest>(msg);

        auto msg = populateMessage(tmsg);

        return *msg;
    }
    catch( apache::thrift::TException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to deserialize a \"" << typeid(DpiReqMsg).name() << "\"");
    }

    return boost::none;
}

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<DpiRspMsg>::serialize(const DpiRspMsg &msg)
{
    try
    {
        auto tmsg = populateThrift(msg);

        if( tmsg.get() )
        {
            return SerializeThriftBytes(*tmsg);
        }
    }
    catch( apache::thrift::TException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to serialize a \"" << typeid(DpiRspMsg).name() << "\"");
    }

    return{};
}

}