#include "ctivangogh.h"

#define BOOST_TEST_MAIN
#include <boost/test/unit_test.hpp>

#include "ctidate.h"

#include <bitset>


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


BOOST_FIXTURE_TEST_CASE(test_isPointDataNewInformation, pointdata_test_helper)
{
    unsigned i = 0;

    std::string expected =
        "000011111111111111111111" "000011111111111111111111" "000011111111111111111111" "000011111111111111111111" "000000111111111111111111" "000000111111111111111111"
        "111111000011111111111111" "111111000011111111111111" "111111000011111111111111" "111111000011111111111111" "111111000000111111111111" "111111000000111111111111"
        "000011000011000011111111" "000011000011000011111111" "000011000011000011111111" "000011000011000011111111" "000000000000000000111111" "000000000000000000111111"
        "000011000011111111000011" "000011000011111111000011" "000011000011111111000011" "000011000011111111000011" "000000000000111111000000" "000000000000111111000000"

        "010111010111010111010111" "101011101011101011101011" "110111110111110111110111" "111011111011111011111011" "110101110101110101110101" "111010111010111010111010"
        "010111010111010111010111" "101011101011101011101011" "110111110111110111110111" "111011111011111011111011" "110101110101110101110101" "111010111010111010111010"
        "010111010111010111010111" "101011101011101011101011" "110111110111110111110111" "111011111011111011111011" "110101110101110101110101" "111010111010111010111010"
        "010111010111010111010111" "101011101011101011101011" "110111110111110111110111" "111011111011111011111011" "110101110101110101110101" "111010111010111010111010";

    std::bitset<1152> results;

    unsigned tags_cases[] = {
        0,
        TAG_POINT_DATA_TIMESTAMP_VALID };

    CtiPointDataMsg pdm(17);
    CtiDynamicPointDispatch dpd(17);

    //  2 cases
    for each( unsigned tags in tags_cases )
    {
        //  24 cases
        for each( const pointdata_test_case &ptc in pointdata_test_cases )
        {
            pdm.setTime(ptc.timestamp);
            pdm.setMillis(ptc.millis);
            pdm.setQuality(ptc.quality);
            pdm.setValue(ptc.value);
            pdm.setTags(tags);

            //  24 cases
            for each( const pointdata_test_case &ptc2 in pointdata_test_cases )
            {
                //  This will be run 2 * 24 * 24 = 1152 times

                dpd.getDispatch().setTimeStamp(ptc2.timestamp);
                dpd.getDispatch().setTimeStampMillis(ptc2.millis);
                dpd.getDispatch().setQuality(ptc2.quality);
                dpd.getDispatch().setValue(ptc2.value);

                results[i++] = CtiVanGogh::isPointDataNewInformation(pdm, dpd);
            }
        }
    }

    std::string results_string = results.to_string();

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results_string.begin(), results_string.end());
}


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
