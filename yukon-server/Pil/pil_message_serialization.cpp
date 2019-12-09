#include "precompiled.h"

#include "pil_message_serialization.h"

namespace Cti::Messaging::Serialization {

namespace {

long long as_milliseconds(std::chrono::system_clock::time_point tp)
{
    return std::chrono::duration_cast<std::chrono::milliseconds>(tp.time_since_epoch()).count();
}

}

using MpsMsg = Pil::MeterProgramStatusArchiveRequestMsg;

MessagePtr<Thrift::MeterProgramming::MeterProgramStatusArchiveRequest>::type populateThrift(const MpsMsg & imsg)
{
    MessagePtr<Thrift::MeterProgramming::MeterProgramStatusArchiveRequest>::type omsg(new Thrift::MeterProgramming::MeterProgramStatusArchiveRequest);

    omsg->__set_configurationId(imsg.configurationId);
    omsg->__set_error(imsg.error);

    Thrift::Rfn::RfnIdentifier rfnId;

    rfnId.__set_sensorManufacturer(imsg.rfnIdentifier.manufacturer);
    rfnId.__set_sensorModel(imsg.rfnIdentifier.model);
    rfnId.__set_sensorSerialNumber(imsg.rfnIdentifier.serialNumber);

    omsg->__set_rfnIdentifier(rfnId);

    //  Hardcoded to Porter
    omsg->__set_source(Thrift::MeterProgramming::Source::PORTER);

    using PPS = Pil::ProgrammingStatus;
    using TPS = Thrift::MeterProgramming::ProgrammingStatus::type;

    static const std::map<PPS, TPS> sourceMapping{
        { PPS::Canceled,   TPS::CANCELED },
        { PPS::Confirming, TPS::CONFIRMING },
        { PPS::Failed,     TPS::FAILED },
        { PPS::Idle,       TPS::IDLE },
        { PPS::Initiating, TPS::INITIATING },
        { PPS::Mismatched, TPS::MISMATCHED },
        { PPS::Uploading,  TPS::UPLOADING }
    };

    if( auto thriftStatus = mapFind(sourceMapping, imsg.status) )
    {
        omsg->__set_status(*thriftStatus);
    }
    else
    {
        CTILOG_WARN(dout, "No Thrift mapping found for ProgrammingStatus " << static_cast<int>(imsg.status));
    }

    omsg->__set_timeStamp(as_milliseconds(imsg.timeStamp));

    return omsg;
}

template<>
ActiveMQConnectionManager::SerializedMessage MessageSerializer<MpsMsg>::serialize(const MpsMsg &msg)
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
        CTILOG_EXCEPTION_ERROR(dout, e, "Failed to serialize a \"" << typeid(MpsMsg).name() << "\"");
    }

    return{};
}

}