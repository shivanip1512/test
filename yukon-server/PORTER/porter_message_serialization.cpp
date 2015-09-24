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

using KeyMapping = boost::bimap<Thrift::DynamicPaoInfoKeys::type, Messaging::Porter::DynamicPaoInfoKeys>;

static const KeyMapping PaoInfoKeyMapping = boost::assign::list_of<KeyMapping::relation>
    (Thrift::DynamicPaoInfoKeys::RFN_VOLTAGE_PROFILE_ENABLED_UNTIL, Messaging::Porter::DynamicPaoInfoKeys::RfnVoltageProfileEnabledUntil)
    (Thrift::DynamicPaoInfoKeys::RFN_VOLTAGE_PROFILE_INTERVAL,      Messaging::Porter::DynamicPaoInfoKeys::RfnVoltageProfileInterval);

boost::optional<const Messaging::Porter::DynamicPaoInfoKeys> mapping(Thrift::DynamicPaoInfoKeys::type k)
{
    return mapFind(PaoInfoKeyMapping.left, k);
}

boost::optional<const Thrift::DynamicPaoInfoKeys::type> mapping(Messaging::Porter::DynamicPaoInfoKeys k)
{
    return mapFind(PaoInfoKeyMapping.right, k);
}

}

using ReqMsg = ::Cti::Messaging::Porter::PorterDynamicPaoInfoRequestMsg;
using RspMsg = ::Cti::Messaging::Porter::PorterDynamicPaoInfoResponseMsg;


MessagePtr<Thrift::PorterDynamicPaoInfoResponse>::type populateThrift( const RspMsg & imsg )
{
    MessagePtr<Thrift::PorterDynamicPaoInfoResponse>::type omsg( new Thrift::PorterDynamicPaoInfoResponse );

    omsg->__set__deviceId( imsg.deviceId );

    struct convert : boost::static_visitor<Thrift::DynamicPaoInfoTypes>
    {
        using Dpit = Thrift::DynamicPaoInfoTypes;

        Dpit operator()(long long ll) const
        {
            Dpit d;

            d._integer = ll;
            d.__isset._integer = true;  //  have to manually do this until the C++ Thrift library does it for us via __set__integer

            return d;
        }

        Dpit operator()(CtiTime t) const
        {
            Dpit d;

            d._time = t.seconds() * 1000;  //  milliseconds
            d.__isset._time = true;  //  have to manually do this until the C++ Thrift library does it for us via __set__integer

            return d;
        }

        Dpit operator()(std::string s) const
        {
            Dpit d;

            d._string = s;
            d.__isset._string = true;  //  have to manually do this until the C++ Thrift library does it for us via __set__integer

            return d;
        }
    }
    const converter;

    for( const auto &kv : imsg.result )
    {
        if( auto mappedKey = mapping(kv.first) )
        {
            omsg->_values.emplace(*mappedKey, kv.second.apply_visitor(converter));
        }
        else
        {
            CTILOG_ERROR(dout, "No conversion for Thrift DynamicPaoInfoKeys value " << static_cast<int>(kv.first));
        }
    }

    return omsg;
}

MessagePtr<ReqMsg>::type populateMessage(const Thrift::PorterDynamicPaoInfoRequest& imsg)
{
    MessagePtr<ReqMsg>::type omsg(new ReqMsg);

    omsg->deviceId = imsg._deviceId;

    for( const auto &key : imsg._keys )
    {
        if( auto mappedKey = mapping(key) )
        {
            omsg->paoInfoKeys.insert(*mappedKey);
        }
        else
        {
            CTILOG_ERROR(dout, "No conversion for Thrift DynamicPaoInfoKeys value " << static_cast<int>(key));
        }
    }

    return omsg;
}


template<>
boost::optional<ReqMsg> MessageSerializer<ReqMsg>::deserialize(const ActiveMQConnectionManager::SerializedMessage &msg)
{
    try
    {
        auto tmsg = DeserializeThriftBytes<Thrift::PorterDynamicPaoInfoRequest>(msg);

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
