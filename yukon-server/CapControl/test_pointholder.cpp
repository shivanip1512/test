#define BOOST_AUTO_TEST_MAIN "Test Point Holders"

#include <boost/test/unit_test.hpp>

#include "yukon.h"

#include "PointValueHolder.h"

BOOST_AUTO_TEST_CASE(test_PointValueHolder_addPointValue)
{
    PointValueHolder pvh;

    CtiTime time;

    pvh.addPointValue(1,1.0,time);
    pvh.addPointValue(2,2.0,time);
    pvh.addPointValue(3,3.0,time);

    bool ret = false;
    double tester = 0.0;

    ret = pvh.getPointValue(1,tester);
    BOOST_CHECK(ret == true);
    BOOST_CHECK(tester == 1.0);

    ret = false;
    tester = 0.0;
    ret = pvh.getPointValue(2,tester);
    BOOST_CHECK(ret == true);
    BOOST_CHECK(tester == 2.0);

    ret = false;
    tester = 0.0;
    ret = pvh.getPointValue(3,tester);
    BOOST_CHECK(ret == true);
    BOOST_CHECK(tester == 3.0);

    ret = false;
    tester = 0.0;
    ret = pvh.getPointValue(4,tester);
    BOOST_CHECK(ret == false);
    BOOST_CHECK(tester == 0.0);

    //Copy Constructor.
    PointValueHolder pvhNew;
    pvhNew = pvh;

    ret = false;
    tester = 0.0;
    ret = pvhNew.getPointValue(1,tester);
    BOOST_CHECK(ret == true);
    BOOST_CHECK(tester == 1.0);

    ret = false;
    tester = 0.0;
    ret = pvhNew.getPointValue(2,tester);
    BOOST_CHECK(ret == true);
    BOOST_CHECK(tester == 2.0);

    ret = false;
    tester = 0.0;
    ret = pvhNew.getPointValue(3,tester);
    BOOST_CHECK(ret == true);
    BOOST_CHECK(tester == 3.0);

    ret = false;
    tester = 0.0;
    ret = pvhNew.getPointValue(4,tester);
    BOOST_CHECK(ret == false);
    BOOST_CHECK(tester == 0.0);
}
