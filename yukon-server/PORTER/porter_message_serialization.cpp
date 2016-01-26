#include "precompiled.h"

#include "porter_message_serialization.h"

#include "PorterDynamicPaoInfoMsg.h"

#include "std_helper.h"
#include "amq_connection.h"

#include <boost/bimap.hpp>
#include <boost/assign.hpp>

using namespace std;

namespace Cti {
namespace Messaging {
namespace Serialization {

namespace {

using ThriftDurationKeys = Thrift::Porter::DynamicPaoInfoDurationKeys::type;
using YukonDurationKeys = Messaging::Porter::DynamicPaoInfoDurationKeys;

using DurationKeyMapping = boost::bimap<ThriftDurationKeys, YukonDurationKeys>;

static const DurationKeyMapping durationKeyLookup = boost::assign::list_of<DurationKeyMapping::relation>
    (ThriftDurationKeys::RFN_VOLTAGE_PROFILE_INTERVAL, YukonDurationKeys::RfnVoltageProfileInterval)
    (ThriftDurationKeys::MCT_IED_LOAD_PROFILE_INTERVAL, YukonDurationKeys::MctIedLoadProfileInterval);

boost::optional<const YukonDurationKeys> mapping(ThriftDurationKeys k)
{
    return mapFind(durationKeyLookup.left, k);
}

boost::optional<const ThriftDurationKeys> mapping(YukonDurationKeys k)
{
    return mapFind(durationKeyLookup.right, k);
}

using ThriftTimestampKeys = Thrift::Porter::DynamicPaoInfoTimestampKeys::type;
using YukonTimestampKeys = Messaging::Porter::DynamicPaoInfoTimestampKeys;

using TimestampKeyMapping = boost::bimap<ThriftTimestampKeys, YukonTimestampKeys>;

static const TimestampKeyMapping timestampKeyLookup = boost::assign::list_of<TimestampKeyMapping::relation>
    (ThriftTimestampKeys::RFN_VOLTAGE_PROFILE_ENABLED_UNTIL, YukonTimestampKeys::RfnVoltageProfileEnabledUntil);

boost::optional<const YukonTimestampKeys> mapping(ThriftTimestampKeys k)
{
    return mapFind(timestampKeyLookup.left, k);
}

boost::optional<const ThriftTimestampKeys> mapping(YukonTimestampKeys k)
{
    return mapFind(timestampKeyLookup.right, k);
}

}

using ReqMsg = ::Cti::Messaging::Porter::DynamicPaoInfoRequestMsg;
using RspMsg = ::Cti::Messaging::Porter::DynamicPaoInfoResponseMsg;

using std::chrono::milliseconds;
using std::chrono::duration_cast;
using std::chrono::system_clock;

MessagePtr<Thrift::Porter::DynamicPaoInfoResponse>::type populateThrift( const RspMsg & imsg )
{
    MessagePtr<Thrift::Porter::DynamicPaoInfoResponse>::type omsg( new Thrift::Porter::DynamicPaoInfoResponse );

    omsg->__set__deviceId( imsg.deviceId );

    for( const auto &kv : imsg.durationValues )
    {
        if( auto key = mapping(kv.first) )
        {
            omsg->_durationValues.emplace(*key, duration_cast<milliseconds>(kv.second).count());
        }
        else
        {
            CTILOG_ERROR(dout, "Unmapped duration key " << static_cast<int>(kv.first));
        }
    }

    for( const auto &kv : imsg.timestampValues )
    {
        if( auto key = mapping(kv.first) )
        {
            omsg->_timestampValues.emplace(*key, duration_cast<milliseconds>(kv.second.time_since_epoch()).count());
        }
        else
        {
            CTILOG_ERROR(dout, "Unmapped timestamp key " << static_cast<int>(kv.first));
        }
    }

    return omsg;
}

MessagePtr<ReqMsg>::type populateMessage(const Thrift::Porter::DynamicPaoInfoRequest& imsg)
{
    MessagePtr<ReqMsg>::type omsg(new ReqMsg);

    omsg->deviceId = imsg._deviceId;

    for( const auto &key : imsg._durationKeys )
    {
        if( auto mappedKey = mapping(key) )
        {
            omsg->durationKeys.insert(*mappedKey);
        }
        else
        {
            CTILOG_ERROR(dout, "Unmapped duration key " << static_cast<int>(key));
        }
    }

    for( const auto &key : imsg._timestampKeys )
    {
        if( auto mappedKey = mapping(key) )
        {
            omsg->timestampKeys.insert(*mappedKey);
        }
        else
        {
            CTILOG_ERROR(dout, "Unmapped timestamp key " << static_cast<int>(key));
        }
    }

    return omsg;
}


template<>
boost::optional<ReqMsg> MessageSerializer<ReqMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage &msg)
{
    try
    {
        auto tmsg = DeserializeThriftBytes<Thrift::Porter::DynamicPaoInfoRequest>(msg);

        auto msg = populateMessage(tmsg);

        return *msg;
    }
    catch( apache::thrift::TException &e )
    {
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to deserialize a \"" << typeid(ReqMsg).name() << "\"");
    }

    return boost::none;
}

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<RspMsg>::serialize(const RspMsg &msg)
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
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to serialize a \"" << typeid(RspMsg).name() << "\"");
    }

    return{};
}

}
}
}
