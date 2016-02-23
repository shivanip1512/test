#pragma once

#include <boost/accumulators/accumulators.hpp>
#include <boost/accumulators/statistics/stats.hpp>
#include <boost/accumulators/statistics/mean.hpp>



class CCStatsObject
{
public:

    long getOpCount() const;
    long getFailCount() const;

    double getAverage() const;

    void addSuccessSample( const double sample );

private:

    boost::accumulators::accumulator_set
    <
        double,
        boost::accumulators::stats
        <
            boost::accumulators::tag::mean
        >
    >
    accumulator;
};

