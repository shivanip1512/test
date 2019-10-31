#include "precompiled.h"

#include "prot_dnpSlave.h"
#include "rfn_identifier.h"
#include "RfDa.h"

#include "std_helper.h"
#include "logger.h"

using namespace std::chrono_literals;

namespace Cti::Simulator {

auto RfDa::doHubMeterRequest(const Bytes& request, const RfnIdentifier & rfnId) -> Bytes
{
    if( ! request.empty() )
    {
        switch( request[0] )
        {
            case 0x35:
            {
                auto serial = std::stoi(rfnId.serialNumber);

                return { 0x36, static_cast<unsigned char>(serial >> 8), static_cast<unsigned char>(serial) };
            }
        }
    }
    return {};
}

auto RfDa::buildDnp3Response(const Bytes& request) -> Bytes
{
    Protocols::DnpSlaveProtocol prot;

    const std::vector<char> requestAsChar{ request.begin(), request.end() };

    auto requestId = prot.identifyRequest(requestAsChar.data(), requestAsChar.size());

    std::vector<unsigned char> response;

    if( requestId.first == Protocols::DnpSlaveProtocol::Commands::Class1230Read )
    {
        std::vector<Protocols::DnpSlave::output_point> points;
        using PT = Protocols::DnpSlave::PointType;

        points.emplace_back(17, true, PT::AnalogInput, 331);
        points.emplace_back(170, true, PT::BinaryInput, true);
        points.emplace_back(1700, true, PT::Accumulator, 3310);
        points.emplace_back(34, true, PT::AnalogOutput, 662);
        points.emplace_back(35, false, PT::AnalogOutput, 662);
        points.emplace_back(340, true, PT::BinaryOutput, true);
        points.emplace_back(341, false, PT::BinaryOutput, true);

        prot.setScanCommand(std::move(points));
    }
    else if( requestId.first == Protocols::DnpSlaveProtocol::Commands::DelayMeasurement )
    {
        prot.setDelayMeasurementCommand(0ms);
    }
    else
    {
        return response;
    }

    CtiXfer xfer;

    while( !prot.isTransactionComplete() )
    {
        if( prot.generate(xfer) == ClientErrors::None )
        {
            if( xfer.getOutBuffer() != NULL && xfer.getOutCount() > 0 )
            {
                auto packet = arrayToRange(xfer.getOutBuffer(), xfer.getOutCount());

                CTILOG_INFO(dout, "Generated DNP3 packet:" << packet);

                response.insert(response.end(), packet.begin(), packet.end());
            }

            prot.decode(xfer);
        }
        else
        {
            CTILOG_WARN(dout, "Was not able to generate scan response.");
        }
    }

    return response;
}

}