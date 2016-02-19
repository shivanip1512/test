#include <boost/test/unit_test.hpp>

#include <boost/range/algorithm/for_each.hpp>

#include "CCStatsObject.h"



BOOST_AUTO_TEST_SUITE( test_ccStatistics )

BOOST_AUTO_TEST_CASE( test_ccStatistics_stats_object )
{
/* 
    The CCStatsObject maintains a running average of the values inserted with incrementTotal().  These
        values are the 'success percentages' of other objects in the heirarchy. Eg, a stats object for a
        feeders daily op count will take as input the daily op success percentage of each capbank attached
        to the feeder.
 
        They are always written to in the following sequence:
            1. incrementTotal( x )
                'x' is a success percentage in the range [0.0, 100.0]
 
            2. incrementOpCount( x )
                'x' is always 1
 
        code
        ----
        CCStatsObject   feederUserDef;
 
        feederUserDef.incrementTotal(currentCapBank->getConfirmationStats().calculateSuccessPercent(capcontrol::USER_DEF_CCSTATS));
        feederUserDef.incrementOpCount(1);
 
        The CCStatsObject is always polled for its values in a certain way as well. The values are used to
            initialize the statistics of a different object.
 
        The calling sequence is as follows:
            1. getAverage()
            2. getOpCount()
            3. getFailCount()
 
            The reason for this is that the call to getAverage() actually updates the value for the failCount,
                so it must occur before the call to getFailCount() or data will be inconsistent.  The call to
                getOpCount() is unaffected and can happen anywhere.
 
        code
        ----
        object.getOperationStats().setUserDefOpSuccessPercent( userDef.getAverage() );
        object.getOperationStats().setUserDefOpCount( userDef.getOpCount() );
        object.getOperationStats().setUserDefConfFail( userDef.getFailCount() ); 
*/

    CCStatsObject   statistic;

    // default state before any operations have taken place on the object

    BOOST_CHECK_CLOSE( 100.000, statistic.getAverage(),  1e-3 );
    BOOST_CHECK_EQUAL(       0, statistic.getOpCount()        );
    BOOST_CHECK_EQUAL(       0, statistic.getFailCount()      );


    std::vector<double> inputValues
    {
        51.604, 74.842, 59.986, 51.121, 28.401,
        89.261, 12.590, 31.522, 82.983, 94.567,
        89.947, 83.436, 11.247,  8.615,  6.316,
        72.546, 95.305, 25.254, 77.137, 97.758
    };

    struct ResultData
    {
        double  average;
        long    opCount,
                failCount;
    };

    std::vector<ResultData> testResults,
        expectedResults
        {
            {  51.604,   1,  0  },
            {  63.223,   2,  0  },
            {  62.144,   3,  1  },
            {  59.388,   4,  1  },
            {  53.191,   5,  2  },
            {  59.203,   6,  2  },
            {  52.544,   7,  3  },
            {  49.916,   8,  4  },
            {  53.590,   9,  4  },
            {  57.688,  10,  4  },
            {  60.620,  11,  4  },
            {  62.522,  12,  4  },
            {  58.577,  13,  5  },
            {  55.009,  14,  6  },
            {  51.763,  15,  7  },
            {  53.062,  16,  7  },
            {  55.546,  17,  7  },
            {  53.864,  18,  8  },
            {  55.088,  19,  8  },
            {  57.222,  20,  8  }
        };

    boost::for_each( inputValues,
                     [ & ]( double input )
                     {
                         statistic.incrementTotal( input );
                         statistic.incrementOpCount( 1 );

                         testResults.push_back( { statistic.getAverage(),
                                                  statistic.getOpCount(),
                                                  statistic.getFailCount()
                                                } );
                     } );

    BOOST_REQUIRE_EQUAL( testResults.size(), expectedResults.size() );

    for ( int i = 0; i < testResults.size(); ++i )
    {
        BOOST_CHECK_CLOSE( testResults[i].average,   expectedResults[i].average,  1e-3 );
        BOOST_CHECK_EQUAL( testResults[i].opCount,   expectedResults[i].opCount        );
        BOOST_CHECK_EQUAL( testResults[i].failCount, expectedResults[i].failCount      );
    }

    // Test copy operations.

    CCStatsObject   statistic2( statistic );

    BOOST_CHECK_CLOSE(  57.222, statistic2.getAverage(),  1e-3 );
    BOOST_CHECK_EQUAL(      20, statistic2.getOpCount()        );
    BOOST_CHECK_EQUAL(       8, statistic2.getFailCount()      );

    statistic.incrementTotal( 80.0 );
    statistic.incrementOpCount( 1 );

    statistic.incrementTotal( 60.0 );
    statistic.incrementOpCount( 1 );

    BOOST_CHECK_CLOSE(  58.384, statistic.getAverage(),  1e-3 );
    BOOST_CHECK_EQUAL(      22, statistic.getOpCount()        );
    BOOST_CHECK_EQUAL(       9, statistic.getFailCount()      );

    BOOST_CHECK_CLOSE(  57.222, statistic2.getAverage(),  1e-3 );
    BOOST_CHECK_EQUAL(      20, statistic2.getOpCount()        );
    BOOST_CHECK_EQUAL(       8, statistic2.getFailCount()      );

    statistic2 = statistic;

    BOOST_CHECK_CLOSE(  58.384, statistic2.getAverage(),  1e-3 );
    BOOST_CHECK_EQUAL(      22, statistic2.getOpCount()        );
    BOOST_CHECK_EQUAL(       9, statistic2.getFailCount()      );

    // And a reset...

    statistic = CCStatsObject();

    BOOST_CHECK_CLOSE( 100.000, statistic.getAverage(),  1e-3 );
    BOOST_CHECK_EQUAL(       0, statistic.getOpCount()        );
    BOOST_CHECK_EQUAL(       0, statistic.getFailCount()      );

    BOOST_CHECK_CLOSE(  58.384, statistic2.getAverage(),  1e-3 );
    BOOST_CHECK_EQUAL(      22, statistic2.getOpCount()        );
    BOOST_CHECK_EQUAL(       9, statistic2.getFailCount()      );
}

BOOST_AUTO_TEST_SUITE_END()

