#pragma once

#include <boost/accumulators/accumulators.hpp>
#include <boost/accumulators/statistics/stats.hpp>
#include <boost/accumulators/statistics/mean.hpp>

using namespace boost::accumulators;


class CCStatsObject
{
public:

    long getOpCount() const;
    long getFailCount() const;

    double getAverage() const;

    void addSuccessSample( const double sample );

private:

    accumulator_set<double, stats<tag::mean>>   accumulator;
};

