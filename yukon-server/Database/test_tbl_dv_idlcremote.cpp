#include <boost/test/unit_test.hpp>

#include "tbl_dv_idlcremote.h"

BOOST_AUTO_TEST_SUITE( test_dbl_dv_idlcremote )


class test_CtiTableDeviceIDLC : public CtiTableDeviceIDLC
{
public:
    typedef CtiTableDeviceIDLC Inherited;

    void setCcuAmpUseType(const int type)
    {
        Inherited::setCCUAmpUseType(type);
    }
};

BOOST_AUTO_TEST_CASE(test_tbl_dv_idlcremote_getamp)
{
    test_CtiTableDeviceIDLC tbl;

    tbl.setCcuAmpUseType(-1);

    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);

    tbl.setCcuAmpUseType(9999);

    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);

    tbl.setCcuAmpUseType(RouteAmpUndefined);

    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);

    tbl.setCcuAmpUseType(RouteAmpAlternating);

    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);

    tbl.setCcuAmpUseType(RouteAmpAltFail);

    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);

    tbl.setCcuAmpUseType(RouteAmp1);

    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);

    tbl.setCcuAmpUseType(RouteAmp2);

    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);

    tbl.setCcuAmpUseType(RouteAmpDefault1Fail2);

    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);

    tbl.setCcuAmpUseType(RouteAmpDefault2Fail1);

    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 1);

    tbl.setCcuAmpUseType(RouteAmpLastChoice);

    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
    BOOST_CHECK_EQUAL(tbl.getAmp(), 0);
}


BOOST_AUTO_TEST_SUITE_END()
