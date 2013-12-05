#pragma once

#include "ctitime.h"

#include <vector>
#include <map>

namespace Cti {
namespace Pil {
namespace Rfn {

struct E2eNodeStatistics
{
    E2eNodeStatistics()
    {
        cumulativeSuccessfulDelay = 0;
        uniqueMessages = 0;
        totalFailures = 0;
        blockTransfers = 0;
        blocksReceived = 0;
        cumulativeBlockTransferDelay = 0;
        totalAcks = 0;
    }

    //  Average number of attempts per successful (Acknowledged) E2EDT message
    //  Number of (Acknowledged) messages successful after 1st attempt, 2nd attempt, ... nth attempt
    typedef std::vector<unsigned> SuccessesPerAttempt;
    SuccessesPerAttempt successes;

    //  Average time delay to successful (Acknowledged) E2EDT message
    time_t cumulativeSuccessfulDelay;

    //  Total number of unique E2EDT messages sent
    unsigned uniqueMessages;

    //  Average E2EDT message inter-transmission time
    boost::optional<CtiTime> firstRequest, lastRequest;

    //  Number of failures
    unsigned totalFailures;

    //  Number of block transfers
    unsigned blockTransfers;

    //  Average number of blocks per-transfer
    unsigned blocksReceived;

    // Average time for each block transfer (last block arrival time – first block request time)
    boost::optional<CtiTime> blockBegin;
    time_t cumulativeBlockTransferDelay;

    unsigned totalAcks;
};


class E2eStatistics
{
public:

    //  Number of unique E2EDT end points with which data exchanges occurred
    typedef std::map<long, E2eNodeStatistics> StatisticsPerNode;

    StatisticsPerNode nodeStatistics;

    void incrementRequests( const long deviceid, CtiTime requestTime )
    {
        E2eNodeStatistics &stats = nodeStatistics[deviceid];

        //  ensure this is empty - we don't know if this is a block transfer or not.
        stats.blockBegin.reset();

        if( ! stats.firstRequest )
        {
            stats.firstRequest = requestTime;
        }

        incrementTransmits(stats, requestTime);
    }

    void incrementAcks( const long deviceid, CtiTime ackTime )
    {
        E2eNodeStatistics &stats = nodeStatistics[deviceid];

        stats.totalAcks++;
    }

    void incrementBlockContinuation( const long deviceid, const unsigned retransmitCount, CtiTime blockSendTime, CtiTime previousBlockSendTime )
    {
        E2eNodeStatistics &stats = nodeStatistics[deviceid];

        if( ! stats.blockBegin )
        {
            stats.blockBegin = previousBlockSendTime;
            stats.blockTransfers++;
        }

        stats.blocksReceived++;

        incrementSuccesses(stats, retransmitCount, blockSendTime);

        incrementTransmits(stats, blockSendTime);
    }

    void incrementCompletions( const long deviceid, const unsigned retransmitCount, CtiTime endTime )
    {
        E2eNodeStatistics &stats = nodeStatistics[deviceid];

        if( stats.blockBegin )
        {
            stats.cumulativeBlockTransferDelay += endTime.seconds() - stats.blockBegin->seconds();
        }

        incrementSuccesses(stats, retransmitCount, endTime);
    }

    void incrementFailures( const long deviceid )
    {
        E2eNodeStatistics &stats = nodeStatistics[deviceid];

        stats.totalFailures++;
    }

private:

    void incrementTransmits( E2eNodeStatistics &stats, CtiTime transmitTime )
    {
        stats.lastRequest = transmitTime;

        stats.uniqueMessages++;
    }

    void incrementSuccesses( E2eNodeStatistics &stats, const unsigned retransmitCount, CtiTime endTime )
    {
        if( stats.successes.size() < retransmitCount + 1 )
        {
            stats.successes.resize(retransmitCount + 1);
        }

        stats.successes[retransmitCount]++;

        if( stats.lastRequest )
        {
            stats.cumulativeSuccessfulDelay += endTime.seconds() - stats.lastRequest->seconds();
        }
    }
};

}
}
}

