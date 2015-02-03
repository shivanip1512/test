#include <boost/test/unit_test.hpp>

#include "ccfeeder.h"

#include <boost/assign.hpp>



BOOST_AUTO_TEST_SUITE( test_ccFeeder_BusOptimizedSort )

BOOST_AUTO_TEST_CASE( test_ccFeeder_BusOptimizedSort_document_sort_order )
{
    //  name --> [ category, offset ]
    typedef std::map<std::string, std::pair<long, double> > FeederInitializerData;

    FeederInitializerData   initData
        = boost::assign::map_list_of
            ( "A", std::make_pair( 0, 0.00 ) )
            ( "B", std::make_pair( 1, 0.10 ) )
            ( "C", std::make_pair( 2, 0.20 ) )
            ( "D", std::make_pair( 0, 0.30 ) )
            ( "E", std::make_pair( 1, 0.40 ) )
            ( "F", std::make_pair( 2, 0.50 ) )
            ( "G", std::make_pair( 0, 0.60 ) )
            ( "H", std::make_pair( 1, 0.70 ) )
            ( "I", std::make_pair( 2, 0.80 ) )
            ( "J", std::make_pair( 0, 0.90 ) )
            ( "K", std::make_pair( 1, 1.00 ) )
            ( "L", std::make_pair( 2, 0.90 ) )
            ( "M", std::make_pair( 0, 0.80 ) )
            ( "N", std::make_pair( 1, 0.70 ) )
            ( "O", std::make_pair( 2, 0.60 ) )
            ( "P", std::make_pair( 0, 0.50 ) )
            ( "Q", std::make_pair( 1, 0.40 ) )
            ( "R", std::make_pair( 2, 0.30 ) )
            ( "S", std::make_pair( 0, 0.20 ) )
            ( "T", std::make_pair( 1, 0.10 ) )
            ( "U", std::make_pair( 2, 0.00 ) )
            ( "V", std::make_pair( 0, 0.10 ) )
            ( "W", std::make_pair( 1, 0.20 ) )
            ( "X", std::make_pair( 2, 0.30 ) )
            ( "Y", std::make_pair( 0, 0.40 ) )
            ( "Z", std::make_pair( 1, 0.50 ) )
                ;

    typedef std::vector<CtiCCFeederPtr> Container;

    Container   varSortedFeeders;

    for each ( FeederInitializerData::value_type data in initData )
    {
        CtiCCFeederPtr feeder = new CtiCCFeeder();

        feeder->setPaoName( data.first );
        feeder->setBusOptimizedVarCategory( data.second.first );
        feeder->setBusOptimizedVarOffset( data.second.second );

        varSortedFeeders.push_back( feeder );
    }

    std::sort( varSortedFeeders.begin(), varSortedFeeders.end(), FeederVARComparison() );

    std::string sortResult;

    for each ( Container::value_type feeder in varSortedFeeders )
    {
        sortResult += feeder->getPaoName();
    }

    BOOST_CHECK_EQUAL( "LIOFRXCUKHNZEQWBTJMGPYDSVA", sortResult );
    //                      -=   -= -= -=
    // when inserting an equal valued object in the collection,
    //  later objects come after the equal object in the sequence
    //      R == X : R inserted before X
    //      H == N : H inserted before N
    //      E == Q : E inserted before Q
    //      B == T : B inserted before T

    delete_container( varSortedFeeders );
}

BOOST_AUTO_TEST_SUITE_END()

