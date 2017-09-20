#include <boost/test/unit_test.hpp>

#include "pt_analog.h"

#include "test_reader.h"

#include "string_util.h"

#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_pt_analog )

struct PointAttributes
{
    unsigned flags;
    CtiPointType_t pointType;
    bool outOfService, alarmDisabled, pseudoPoint;
    struct ControlParameters
    {
        long controlOffset;
        bool controlInhibited;
        bool operator==(const ControlParameters& other) const
        {
            return controlOffset == other.controlOffset 
                && controlInhibited == other.controlInhibited;
        }
    };
    boost::optional<ControlParameters> cp;

    bool operator!=(const PointAttributes& other) const
    {
        return ! ( flags == other.flags 
            && pointType == other.pointType
            && outOfService == other.outOfService
            && alarmDisabled == other.alarmDisabled
            && pseudoPoint   == other.pseudoPoint
            && cp == other.cp );
    }
};

std::ostream& operator<<(std::ostream& os, const PointAttributes &pa)
{
    Cti::FormattedList l;
    l.add("PointAttribute");
    l.add("Flags") << std::hex << pa.flags;
    l.add("Point type") << pa.pointType;
    l.add("Out of service") << pa.outOfService;
    l.add("Alarm disabled") << pa.alarmDisabled;
    l.add("Pseudo point")   << pa.pseudoPoint;
    l.add("Control parameters set?") << pa.cp.is_initialized();
    if( pa.cp )
    {
        l.add("Control offset")      << pa.cp->controlOffset;
        l.add("Control inhibited") << pa.cp->controlInhibited;
    }

    return os << l.toString();
}

BOOST_AUTO_TEST_CASE(test_adjustStaticTags)
{
    using AnalogPointRow = Cti::Test::StringRow<20>;
    using AnalogPointReader = Cti::Test::TestReader<AnalogPointRow> ;
    using APR = AnalogPointReader;

    AnalogPointRow columnNames = 
        { "pointid", "pointname", "pointtype",    "paobjectid", "stategroupid", "pointoffset", "serviceflag", "alarminhibit", "pseudoflag", "archivetype", "archiveinterval", "uomid", "decimalplaces", "decimaldigits", "calctype", "multiplier", "dataoffset", "deadband", "controloffset",      "controlinhibit" };
    std::vector<AnalogPointRow> rows {
        //  analog, no control offset, no control available, all base flags off and on
        { "1234",    "AA",        "analog",       "99",         "31",           "7",           "N",           "N",            "R",          "None",        "8",               "9",     "10",            "11",            "12",       "13",         "14",         "15",       APR::getNullString(), APR::getNullString() },
        { "1234",    "AA",        "analog",       "99",         "31",           "7",           "Y",           "Y",            "P",          "None",        "8",               "9",     "10",            "11",            "12",       "13",         "14",         "15",       APR::getNullString(), APR::getNullString() },
        //  analog output type, so control is available
        { "1234",    "AA",        "analogoutput", "99",         "31",           "7",           "N",           "N",            "R",          "None",        "8",               "9",     "10",            "11",            "12",       "13",         "14",         "15",       APR::getNullString(), APR::getNullString() },
        { "1234",    "AA",        "analogoutput", "99",         "31",           "7",           "Y",           "Y",            "P",          "None",        "8",               "9",     "10",            "11",            "12",       "13",         "14",         "15",       APR::getNullString(), APR::getNullString() },
        //  control offset, control is available
        { "1234",    "AA",        "analog",       "99",         "31",           "7",           "N",           "N",            "R",          "None",        "8",               "9",     "10",            "11",            "12",       "13",         "14",         "15",       "16",                 "N" },
        { "1234",    "AA",        "analog",       "99",         "31",           "7",           "Y",           "Y",            "P",          "None",        "8",               "9",     "10",            "11",            "12",       "13",         "14",         "15",       "16",                 "N" },
        { "1234",    "AA",        "analogoutput", "99",         "31",           "7",           "N",           "N",            "R",          "None",        "8",               "9",     "10",            "11",            "12",       "13",         "14",         "15",       "16",                 "N" },
        { "1234",    "AA",        "analogoutput", "99",         "31",           "7",           "Y",           "Y",            "P",          "None",        "8",               "9",     "10",            "11",            "12",       "13",         "14",         "15",       "16",                 "N" },
        //  control offset, control is available, but disabled
        { "1234",    "AA",        "analog",       "99",         "31",           "7",           "N",           "N",            "R",          "None",        "8",               "9",     "10",            "11",            "12",       "13",         "14",         "15",       "16",                 "Y" },
        { "1234",    "AA",        "analog",       "99",         "31",           "7",           "Y",           "Y",            "P",          "None",        "8",               "9",     "10",            "11",            "12",       "13",         "14",         "15",       "16",                 "Y" },
        { "1234",    "AA",        "analogoutput", "99",         "31",           "7",           "N",           "N",            "R",          "None",        "8",               "9",     "10",            "11",            "12",       "13",         "14",         "15",       "16",                 "Y" },
        { "1234",    "AA",        "analogoutput", "99",         "31",           "7",           "Y",           "Y",            "P",          "None",        "8",               "9",     "10",            "11",            "12",       "13",         "14",         "15",       "16",                 "Y" }};

    AnalogPointReader rdr { columnNames, rows };

    using CP = PointAttributes::ControlParameters;
    const bool X = true, _ = false;

    const std::vector<PointAttributes> pa_expected {
        { 0xcffffff8, AnalogPointType,       _, _, _, boost::none },   //  no flags
        { 0xeffffffb, AnalogPointType,       X, X, X, boost::none },   //  base flags
        { 0xdffffff8, AnalogOutputPointType, _, _, _, boost::none },   //  control available
        { 0xfffffffb, AnalogOutputPointType, X, X, X, boost::none },   //  control available + base flags
        { 0xdffffff8, AnalogPointType,       _, _, _, CP { 16, _ } },  //  control available
        { 0xfffffffb, AnalogPointType,       X, X, X, CP { 16, _ } },  //  control available + base flags
        { 0xdffffff8, AnalogOutputPointType, _, _, _, CP { 16, _ } },  //  control available
        { 0xfffffffb, AnalogOutputPointType, X, X, X, CP { 16, _ } },  //  control available + base flags
        { 0xdffffffc, AnalogPointType,       _, _, _, CP { 16, X } },  //  control inhibited + control available
        { 0xffffffff, AnalogPointType,       X, X, X, CP { 16, X } },  //  control inhibited + control available + base flags
        { 0xdffffffc, AnalogOutputPointType, _, _, _, CP { 16, X } },  //  control inhibited + control available
        { 0xffffffff, AnalogOutputPointType, X, X, X, CP { 16, X } },  //  control inhibited + control available + base flags
    };

    std::vector<PointAttributes> pa_results;

    CtiPointAnalog p;

    for( int i = 0; i < rows.size(); i++ )
    {
        rdr();

        p.DecodeDatabaseReader(rdr);

        BOOST_CHECK_EQUAL(p.getID(),   1234);
        BOOST_CHECK_EQUAL(p.getName(), "AA");
        BOOST_CHECK_EQUAL(p.getDeviceID(), 99);
        BOOST_CHECK_EQUAL(p.getStateGroupID(), 31);
        BOOST_CHECK_EQUAL(p.getPointOffset(), 7);
        BOOST_CHECK_EQUAL(p.getArchiveType(), ArchiveTypeNone);
        BOOST_CHECK_EQUAL(p.getArchiveInterval(), 8);
        BOOST_CHECK_EQUAL(p.getPointUnits().getUnitID(), 9);
        BOOST_CHECK_EQUAL(p.getPointUnits().getDecimalPlaces(), 10);
        BOOST_CHECK_EQUAL(p.getPointUnits().getDecimalDigits(), 11);
        BOOST_CHECK_EQUAL(p.getPointUnits().getUnitMeasure().getCalcType(), 12);
        BOOST_CHECK_EQUAL(p.getMultiplier(), 13);
        BOOST_CHECK_EQUAL(p.getDataOffset(), 14);
        BOOST_CHECK_EQUAL(p.getDeadband(), 15);

        unsigned tags = -1;
        p.adjustStaticTags(tags);

        PointAttributes pa { tags, p.getType(), p.isOutOfService(), p.isAlarmDisabled(), p.isPseudoPoint(), boost::none };

        if( auto c = p.getControl() )
        {
            pa.cp = PointAttributes::ControlParameters { c->getControlOffset(), c->isControlInhibited() };
        }

        pa_results.push_back(pa);
    }

    BOOST_CHECK_EQUAL_RANGES(pa_results, pa_expected);
}

BOOST_AUTO_TEST_SUITE_END()
