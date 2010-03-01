#define BOOST_AUTO_TEST_MAIN "Test consumption multiplier"

#include "yukon.h"
#include "boostutil.h"
#include "types.h"

#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;
using namespace Cti::Simulator;

double getConsumptionMultiplier(int address)
{
    unsigned address_range = address % 1000;

    if( address_range < 400 )               
    {                                       
        return 1.0;                         
    }                                       
    else if( address_range < 600 )          
    {                                       
        return 2.0;                         
    }                                       
    else if( address_range < 800 )          
    {                                      
        return 3.0;                         
    }                                       
    else if( address_range < 950 )          
    {                                       
        return 5.0;                         
    }                                       
    else if( address_range < 995 )          
    {                                       
        return 10.0;                        
    }                                       
    else                                    
    {
        return 20.0;
    }
}

BOOST_AUTO_TEST_CASE( mct_consumption_multiplier )
{
    int address = 0;
    for (; address < 400; address++)
    {
        BOOST_CHECK_EQUAL(getConsumptionMultiplier(address), 1.0);
    }
    for (; address < 600; address++)
    {
        BOOST_CHECK_EQUAL(getConsumptionMultiplier(address), 2.0);
    }
    for (; address < 800; address++)
    {
        BOOST_CHECK_EQUAL(getConsumptionMultiplier(address), 3.0);
    }
    for (; address < 950; address++)
    {
        BOOST_CHECK_EQUAL(getConsumptionMultiplier(address), 5.0);
    }
    for (; address < 995; address++)
    {
        BOOST_CHECK_EQUAL(getConsumptionMultiplier(address), 10.0);
    }
    for (; address < 1000; address++)
    {
        BOOST_CHECK_EQUAL(getConsumptionMultiplier(address), 20.0);
    }
}
