#define BOOST_AUTO_TEST_MAIN "Test Point Holders"

#include <boost/test/unit_test.hpp>

#include "yukon.h"

#include "PointOffsetValueHolder.h"
#include "LtcPointHolder.h"

BOOST_AUTO_TEST_CASE(test_point_offset_holder)
{
    //Add some points and get them out.
    PointOffestValueHolder valueHolder;

    valueHolder.addPointOffset(1,AnalogPointType,50,72.0);
    valueHolder.addPointOffset(2,AnalogPointType,51,73.0);
    valueHolder.addPointOffset(3,AnalogPointType,52,74.0);
    valueHolder.addPointOffset(1,StatusPointType,53,1.0);

    double result = -1.0;
    int intResult = 0;
    bool found = false;

    found = valueHolder.getPointValueByOffsetAndType(1,AnalogPointType,result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,72.0);

    found = valueHolder.getPointIdByOffsetAndType(1,AnalogPointType,intResult);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(intResult,50);

    found = valueHolder.getPointValueByOffsetAndType(2,AnalogPointType,result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,73.0);

    found = valueHolder.getPointIdByOffsetAndType(2,AnalogPointType,intResult);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(intResult,51);

    found = valueHolder.getPointValueByOffsetAndType(3,AnalogPointType,result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,74.0);

    found = valueHolder.getPointIdByOffsetAndType(3,AnalogPointType,intResult);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(intResult,52);

    found = valueHolder.getPointValueByOffsetAndType(1,StatusPointType,result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,1.0);

    found = valueHolder.getPointIdByOffsetAndType(1,StatusPointType,intResult);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(intResult,53);

    //Add duplicates and make sure we get the latest value.

    valueHolder.addPointOffset(1,StatusPointType,54,0.0);

    found = valueHolder.getPointValueByOffsetAndType(1,StatusPointType,result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,0.0);

    found = valueHolder.getPointIdByOffsetAndType(1,StatusPointType,intResult);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(intResult,54);

}

BOOST_AUTO_TEST_CASE(test_ltc_point_offset_holder)
{
    //Add some points and get them out.
    LtcPointHolder valueHolder;

    valueHolder.addPointOffset("P1",1,AnalogPointType,50,72.0);
    valueHolder.addPointOffset("P2",2,AnalogPointType,51,73.0);
    valueHolder.addPointOffset("P3",3,AnalogPointType,52,74.0);
    valueHolder.addPointOffset("P4",1,StatusPointType,53,1.0);

    double result = -1;
    int intResult = 0;
    bool found = false;

    found = valueHolder.getPointValueByOffsetAndType(1,AnalogPointType,result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,72.0);

    found = valueHolder.getPointIdByOffsetAndType(1,AnalogPointType,intResult);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(intResult,50);

    found = valueHolder.getPointValueByName("P1",result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,72.0);

    found = valueHolder.getPointValueByOffsetAndType(2,AnalogPointType,result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,73.0);

    found = valueHolder.getPointIdByOffsetAndType(2,AnalogPointType,intResult);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(intResult,51);

    found = valueHolder.getPointValueByName("P2",result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,73.0);

    found = valueHolder.getPointValueByOffsetAndType(3,AnalogPointType,result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,74.0);

    found = valueHolder.getPointIdByOffsetAndType(3,AnalogPointType,intResult);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(intResult,52);

    found = valueHolder.getPointValueByName("P3",result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,74.0);

    found = valueHolder.getPointValueByOffsetAndType(1,StatusPointType,result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,1.0);

    found = valueHolder.getPointIdByOffsetAndType(1,StatusPointType,intResult);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(intResult,53);

    found = valueHolder.getPointValueByName("P4",result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,1.0);


    //Add duplicates and make sure we get the latest value.

    valueHolder.addPointOffset("P4",1,StatusPointType,54,0.0);

    found = valueHolder.getPointValueByOffsetAndType(1,StatusPointType,result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,0.0);

    found = valueHolder.getPointIdByOffsetAndType(1,StatusPointType,intResult);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(intResult,54);

        found = valueHolder.getPointValueByName("P4",result);
    BOOST_REQUIRE_EQUAL(found,true);
    BOOST_CHECK_EQUAL(result,0.0);
}
