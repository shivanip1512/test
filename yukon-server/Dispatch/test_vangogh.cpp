#include <boost/test/unit_test.hpp>

#include "ctivangogh.h"
#include "ctidate.h"

#include <bitset>

BOOST_AUTO_TEST_SUITE( test_vangogh )

struct test_CtiVanGogh : CtiVanGogh
{
    test_CtiVanGogh(CtiPointClientManager& pcm)
        : CtiVanGogh(pcm)
    {}
};

struct test_CtiPointClientManager : CtiPointClientManager
{
    std::map<long, boost::shared_ptr<CtiPointBase>> points;

    ptr_type getPoint(long point, long pao) override
    {
        return points.emplace(point, boost::make_shared<CtiPointBase>(point)).first->second;
    }
};

struct pointdata_test_helper
{
    struct pointdata_test_case
    {
        CtiTime timestamp;
        unsigned millis;
        PointQuality_t quality;
        double value;
    };

    std::vector<pointdata_test_case> pointdata_test_cases;

    pointdata_test_helper()
    {
        CtiDate timestamp_cases[] = {
            CtiDate(1, 1, 2001),
            CtiDate(1, 1, 2002) };
        unsigned millis_cases[] = {
            0,
            500 };
        PointQuality_t quality_cases[] = {
            UnintializedQuality,
            NonUpdatedQuality,
            NormalQuality };
        double value_cases[] = {
            0,
            3.14159 };

        //  2 * 2 * 3 * 2 cases = 24 cases
        for each( const CtiDate &timestamp in timestamp_cases )
        {
            for each( const unsigned millis in millis_cases )
            {
                for each( const PointQuality_t quality in quality_cases )
                {
                    for each( const double value in value_cases )
                    {
                        pointdata_test_case ptc;

                        ptc.timestamp = timestamp;
                        ptc.millis = millis;
                        ptc.quality = quality;
                        ptc.value = value;

                        pointdata_test_cases.push_back(ptc);
                    }
                }
            }
        }
    }
};


BOOST_FIXTURE_TEST_CASE(test_isDuplicatePointData, pointdata_test_helper)
{
    std::string expected =
        "100000000000000000000000"
        "010000000000000000000000"
        "001000000000000000000000"
        "000100000000000000000000"
        "000010000000000000000000"
        "000001000000000000000000"
        "000000100000000000000000"
        "000000010000000000000000"
        "000000001000000000000000"
        "000000000100000000000000"
        "000000000010000000000000"
        "000000000001000000000000"
        "000000000000100000000000"
        "000000000000010000000000"
        "000000000000001000000000"
        "000000000000000100000000"
        "000000000000000010000000"
        "000000000000000001000000"
        "000000000000000000100000"
        "000000000000000000010000"
        "000000000000000000001000"
        "000000000000000000000100"
        "000000000000000000000010"
        "000000000000000000000001";

    std::bitset<576> results;

    CtiPointDataMsg pdm(17);
    CtiDynamicPointDispatch dpd(17);

    unsigned i = 0;

    //  24 cases
    for each( const pointdata_test_case &ptc in pointdata_test_cases )
    {
        pdm.setTime(ptc.timestamp);
        pdm.setMillis(ptc.millis);
        pdm.setQuality(ptc.quality);
        pdm.setValue(ptc.value);

        //  24 cases
        for each( const pointdata_test_case &ptc2 in pointdata_test_cases )
        {
            //  This will be run 24 * 24 = 576 times

            dpd.getDispatch().setTimeStamp(ptc2.timestamp);
            dpd.getDispatch().setTimeStampMillis(ptc2.millis);
            dpd.getDispatch().setQuality(ptc2.quality);
            dpd.getDispatch().setValue(ptc2.value);

            results[i++] = CtiVanGogh::isDuplicatePointData(pdm, dpd);

            pdm.setId(18);

            BOOST_CHECK_EQUAL(false, CtiVanGogh::isDuplicatePointData(pdm, dpd));

            pdm.setId(17);
        }
    }

    std::string results_string = results.to_string();

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results_string.begin(), results_string.end());
}


BOOST_AUTO_TEST_CASE( test_hasPointDataChanged )
{
    //  without TAG_POINT_DATA_TIMESTAMP_VALID
    {
        CtiPointDataMsg msg;
        CtiDynamicPointDispatch dpd(1729);

        msg.setValue(0.0);
        msg.setTimeWithMillis(CtiTime(1403560784), 123);
        msg.setQuality(NormalQuality);

        dpd.setPoint(msg.getTime(), msg.getMillis(), msg.getValue(), msg.getQuality(), msg.getTags());

        //  equal - not new
        BOOST_CHECK_EQUAL( false, CtiVanGogh::hasPointDataChanged(msg, dpd) );

        //  value changed
        msg.setValue(1.0);
        BOOST_CHECK_EQUAL( true, CtiVanGogh::hasPointDataChanged(msg, dpd) );
        msg.setValue(0.0);

        //  quality changed
        msg.setQuality(OverflowQuality);
        BOOST_CHECK_EQUAL( true, CtiVanGogh::hasPointDataChanged(msg, dpd) );

        //  DPD set to NonUpdatedQuality
        dpd.setPoint(msg.getTime(), msg.getMillis(), msg.getValue(), NonUpdatedQuality, msg.getTags());
        BOOST_CHECK_EQUAL( false, CtiVanGogh::hasPointDataChanged(msg, dpd) );
        msg.setQuality(NonUpdatedQuality);
        BOOST_CHECK_EQUAL( false, CtiVanGogh::hasPointDataChanged(msg, dpd) );
    }

    //  with TAG_POINT_DATA_TIMESTAMP_VALID
    {
        CtiPointDataMsg msg;
        CtiDynamicPointDispatch dpd(1729);

        msg.setValue(0.0);
        msg.setTimeWithMillis(CtiTime(1403560784), 123);
        msg.setQuality(NormalQuality);
        msg.setTags(TAG_POINT_DATA_TIMESTAMP_VALID);

        dpd.setPoint(msg.getTime(), msg.getMillis(), msg.getValue(), msg.getQuality(), msg.getTags());

        //  equal - not new
        BOOST_CHECK_EQUAL( false, CtiVanGogh::hasPointDataChanged(msg, dpd) );

        msg.setTime(msg.getTime().seconds() + 1);
        //  newer time, but no value change
        BOOST_CHECK_EQUAL( false, CtiVanGogh::hasPointDataChanged(msg, dpd) );
        msg.setValue(1.0);
        //  newer time with value change
        BOOST_CHECK_EQUAL( true, CtiVanGogh::hasPointDataChanged(msg, dpd) );
        msg.setValue(0.0);
        msg.setTime(msg.getTime().seconds() - 1);

        msg.setMillis(msg.getMillis() - 1);
        //  different millis, no value change
        BOOST_CHECK_EQUAL( false, CtiVanGogh::hasPointDataChanged(msg, dpd) );
        msg.setValue(1.0);
        //  different millis with value change
        BOOST_CHECK_EQUAL( true, CtiVanGogh::hasPointDataChanged(msg, dpd) );
        msg.setValue(0.0);
        msg.setMillis(msg.getMillis() + 1);

        //  DPD set to Uninitialized
        dpd.setPoint(msg.getTime(), msg.getMillis(), msg.getValue(), UnintializedQuality, msg.getTags());
        BOOST_CHECK_EQUAL( true, CtiVanGogh::hasPointDataChanged(msg, dpd) );
    }
}

BOOST_AUTO_TEST_CASE( test_registration_add_remove_points )
{
    test_CtiPointClientManager pcm;
    test_CtiVanGogh vg{pcm};

    CtiListenerConnection lc( "test1" );

    auto testQ = boost::make_shared<CtiConnection::Que_t>();
    auto  vgcm = boost::make_shared<CtiVanGoghConnectionManager>(lc, testQ.get());
    auto    cm = boost::static_pointer_cast<CtiConnectionManager>(vgcm);

    //  Check ADD_POINTS
    {
        CtiPointRegistrationMsg aReg{ REG_ADD_POINTS };
        aReg.insert( 1001 );
        aReg.insert( 1003 );
        aReg.insert( 1004 );

        vg.registration(cm, aReg);

        BOOST_CHECK( ! vgcm->getAllPoints() );

        auto regMap = pcm.getRegistrationMap(cm->hash(*cm));

        BOOST_REQUIRE_EQUAL(regMap.size(), 3);

        auto regItr = regMap.begin();

        BOOST_CHECK_EQUAL(regItr++->first, 1001);
        BOOST_CHECK_EQUAL(regItr++->first, 1003);
        BOOST_CHECK_EQUAL(regItr++->first, 1004);
    }

    //  Check REMOVE_POINTS
    {
        CtiPointRegistrationMsg aReg{ REG_REMOVE_POINTS };
        aReg.insert( 1001 );
        aReg.insert( 1004 );
        aReg.insert( 1099 );

        vg.registration(cm, aReg);

        BOOST_CHECK( ! vgcm->getAllPoints() );

        auto regMap = pcm.getRegistrationMap(cm->hash(*cm));

        BOOST_REQUIRE_EQUAL(regMap.size(), 1);

        auto regItr = regMap.begin();

        BOOST_CHECK_EQUAL(regItr++->first, 1003);
    }
}

BOOST_AUTO_TEST_CASE( test_registration_all_points )
{
    test_CtiPointClientManager pcm;
    test_CtiVanGogh vg{pcm};

    CtiListenerConnection lc( "test1" );

    auto testQ = boost::make_shared<CtiConnection::Que_t>();
    auto  vgcm = boost::make_shared<CtiVanGoghConnectionManager>(lc, testQ.get());
    auto    cm = boost::static_pointer_cast<CtiConnectionManager>(vgcm);

    CtiPointRegistrationMsg aReg{ REG_ALL_POINTS };

    vg.registration(cm, aReg);

    BOOST_CHECK( vgcm->getAllPoints() );

    auto regMap = pcm.getRegistrationMap(cm->hash(*cm));

    BOOST_CHECK_EQUAL(regMap.size(), 0);
}

BOOST_AUTO_TEST_SUITE_END()