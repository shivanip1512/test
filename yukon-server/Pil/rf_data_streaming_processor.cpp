#include "precompiled.h"

#include "rf_data_streaming_processor.h"
#include "amq_connection.h"
#include "dev_rfn.h"
#include "rfn_statistics.h"
#include "std_helper.h"

#include <boost/range/adaptor/map.hpp>
#include <boost/range/adaptor/transformed.hpp>
#include <boost/range/algorithm/count_if.hpp>
#include <boost/range/algorithm/transform.hpp>
#include <boost/range/numeric.hpp>

#include <chrono>

using Cti::Logging::Vector::Hex::operator<<;
using Cti::Messaging::Rfn::E2eMessenger;

static const auto RF_DATA_STREAMING_STATS_REPORTING_INTERVAL = 86400;

namespace Cti {
namespace Pil {

//  Jan 1 2016 0:00 GMT
static const auto DataStreamingEpoch = std::chrono::system_clock::from_time_t(1451606400);
    
void RfDataStreamingProcessor::start()
{
    E2eMessenger::registerDataStreamingHandler(
            [&](const E2eMessenger::Indication &msg)
            {
                LockGuard guard(_packetMux);

                _packets.push_back(msg);
            });
}


auto RfDataStreamingProcessor::tick() -> ResultVector
{
    PacketQueue newPackets;

    {
        LockGuard g{ _packetMux };

        newPackets.swap(_packets);
    }

    ResultVector results;

    boost::range::transform(
        newPackets, 
        std::back_inserter(results),
        [this](const PacketQueue::value_type &packet) {
            return processPacket(packet);
        });

    handleStatistics();

    return results;
}


std::unique_ptr<CtiMultiMsg> RfDataStreamingProcessor::processPacket(const Packet &p)
{
    //  TODO:  do actual processing
    return nullptr;
}

/*
unsigned statsReportFrequency = gConfigParms.getValueAsInt("RF_DATA_STREAMING_STATS_REPORTING_INTERVAL", RF_DATA_STREAMING_STATS_REPORTING_INTERVAL);
CtiTime nextStatisticsReport = nextScheduledTimeAlignedOnRate(CtiTime::now(), statsReportFrequency);
*/

void RfDataStreamingProcessor::handleStatistics()
{/*
    if( CtiTime::now() > nextStatisticsReport )
    {
        nextStatisticsReport = nextScheduledTimeAlignedOnRate(nextStatisticsReport, statsReportFrequency);

        StreamBuffer report;

        report << "RFN data streaming statistics report:" << std::endl;

        CTILOG_INFO(dout, report);
    }*/
}

}
} //namespace Cti::Pil
