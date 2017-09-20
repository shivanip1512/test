#include <boost/test/unit_test.hpp>

#include "pt_base.h"

#include "test_reader.h"

#include "string_util.h"

#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_pt_base )

struct test_CtiPointBase : CtiPointBase
{
    using CtiPointBase::setType;
};

BOOST_AUTO_TEST_CASE(test_isNumeric)
{
    test_CtiPointBase p;

    p.setType(AnalogPointType);
    BOOST_CHECK(p.isNumeric());

    p.setType(PulseAccumulatorPointType);
    BOOST_CHECK(p.isNumeric());

    p.setType(DemandAccumulatorPointType);
    BOOST_CHECK(p.isNumeric());

    p.setType(CalculatedPointType);
    BOOST_CHECK(p.isNumeric());

    p.setType(AnalogOutputPointType);
    BOOST_CHECK(p.isNumeric());

    p.setType(SystemPointType);
    BOOST_CHECK( ! p.isNumeric());

    p.setType(StatusOutputPointType);
    BOOST_CHECK( ! p.isNumeric());

    p.setType(StatusPointType);
    BOOST_CHECK( ! p.isNumeric());

    p.setType(CalculatedStatusPointType);
    BOOST_CHECK( ! p.isNumeric());

    p.setType(InvalidPointType);
    BOOST_CHECK( ! p.isNumeric());
}

BOOST_AUTO_TEST_CASE(test_isStatus)
{
    test_CtiPointBase p;

    p.setType(AnalogPointType);
    BOOST_CHECK( ! p.isStatus());

    p.setType(PulseAccumulatorPointType);
    BOOST_CHECK( ! p.isStatus());

    p.setType(DemandAccumulatorPointType);
    BOOST_CHECK( ! p.isStatus());

    p.setType(CalculatedPointType);
    BOOST_CHECK( ! p.isStatus());

    p.setType(AnalogOutputPointType);
    BOOST_CHECK( ! p.isStatus());

    p.setType(SystemPointType);
    BOOST_CHECK( ! p.isStatus());

    p.setType(StatusOutputPointType);
    BOOST_CHECK( p.isStatus());

    p.setType(StatusPointType);
    BOOST_CHECK( p.isStatus());

    p.setType(CalculatedStatusPointType);
    BOOST_CHECK( p.isStatus());

    p.setType(InvalidPointType);
    BOOST_CHECK( ! p.isStatus());
}

struct PointAttributes
{
    unsigned flags;
    bool outOfService, alarmDisabled, pseudoPoint;

    bool operator!=(const PointAttributes& other) const
    {
        return !(flags == other.flags
            && outOfService  == other.outOfService
            && pseudoPoint   == other.pseudoPoint
            && alarmDisabled == other.alarmDisabled);
    }
};

std::ostream& operator<<(std::ostream& os, const PointAttributes &pa)
{
    Cti::FormattedList l;
    l.add("PointAttribute");
    l.add("Flags") << std::hex << pa.flags;
    l.add("Out of service") << pa.outOfService;
    l.add("Alarm disabled") << pa.alarmDisabled;
    l.add("Pseudo point") << pa.pseudoPoint;

    return os << l.toString();
}

BOOST_AUTO_TEST_CASE(test_adjustStaticTags)
{
    typedef Cti::Test::StringRow<11> PointRow;
    typedef Cti::Test::TestReader<PointRow> PointReader;

    PointRow columnNames = 
        { "pointid", "pointname", "pointtype", "paobjectid", "stategroupid", "pointoffset", "serviceflag", "alarminhibit", "pseudoflag", "archivetype", "archiveinterval" };
    std::vector<PointRow> rows {
        { "1234",    "AA",        "system",    "99",         "31",           "7",           "N",           "N",            "R",          "None",        "8"               },
        { "1234",    "AA",        "system",    "99",         "31",           "7",           "N",           "N",            "P",          "None",        "8"               },
        { "1234",    "AA",        "system",    "99",         "31",           "7",           "N",           "Y",            "R",          "None",        "8"               },
        { "1234",    "AA",        "system",    "99",         "31",           "7",           "N",           "Y",            "P",          "None",        "8"               },
        { "1234",    "AA",        "system",    "99",         "31",           "7",           "Y",           "N",            "R",          "None",        "8"               },
        { "1234",    "AA",        "system",    "99",         "31",           "7",           "Y",           "N",            "P",          "None",        "8"               },
        { "1234",    "AA",        "system",    "99",         "31",           "7",           "Y",           "Y",            "R",          "None",        "8"               },
        { "1234",    "AA",        "system",    "99",         "31",           "7",           "Y",           "Y",            "P",          "None",        "8"               }};

    PointReader rdr { columnNames, rows };
    
    CtiPointBase p;

    std::vector<PointAttributes> pa_results;

    const bool X = true, _ = false;

    const std::vector<PointAttributes> pa_expected{
        { 0xdffffffc, _, _, _ },  //  no flags
        { 0xfffffffc, _, _, X },  //  pseudo
        { 0xdffffffe, _, X, _ },  //  alarm
        { 0xfffffffe, _, X, X },  //  alarm + pseudo
        { 0xdffffffd, X, _, _ },  //  disable
        { 0xfffffffd, X, _, X },  //  disable + pseudo
        { 0xdfffffff, X, X, _ },  //  disable + alarm
        { 0xffffffff, X, X, X }}; //  disable + alarm + pseudo
    std::vector<unsigned> tags_results;

    for( int i = 0; i < rows.size(); i++ )
    {
        rdr();

        p.DecodeDatabaseReader(rdr);

        BOOST_CHECK_EQUAL(p.getID(),   1234);
        BOOST_CHECK_EQUAL(p.getName(), "AA");
        BOOST_CHECK_EQUAL(p.getType(), SystemPointType);
        BOOST_CHECK_EQUAL(p.getDeviceID(), 99);
        BOOST_CHECK_EQUAL(p.getStateGroupID(), 31);
        BOOST_CHECK_EQUAL(p.getPointOffset(), 7);
        BOOST_CHECK_EQUAL(p.getArchiveType(),     ArchiveTypeNone);
        BOOST_CHECK_EQUAL(p.getArchiveInterval(), 8);

        unsigned tags = -1;
        p.adjustStaticTags(tags);

        PointAttributes pa{ tags, p.isOutOfService(), p.isAlarmDisabled(), p.isPseudoPoint() };

        pa_results.push_back(pa);
    }

    BOOST_CHECK_EQUAL_RANGES(pa_results, pa_expected);
}

BOOST_AUTO_TEST_CASE(test_makeStaticTags)
{
    const bool X = true, _ = false;

    BOOST_CHECK_EQUAL(CtiPointBase::makeStaticTags(_, _, _), 0x00000000);
    BOOST_CHECK_EQUAL(CtiPointBase::makeStaticTags(_, _, X), 0x00000002);
    BOOST_CHECK_EQUAL(CtiPointBase::makeStaticTags(_, X, _), 0x00000001);
    BOOST_CHECK_EQUAL(CtiPointBase::makeStaticTags(_, X, X), 0x00000003);
    BOOST_CHECK_EQUAL(CtiPointBase::makeStaticTags(X, _, _), 0x20000000);
    BOOST_CHECK_EQUAL(CtiPointBase::makeStaticTags(X, _, X), 0x20000002);
    BOOST_CHECK_EQUAL(CtiPointBase::makeStaticTags(X, X, _), 0x20000001);
    BOOST_CHECK_EQUAL(CtiPointBase::makeStaticTags(X, X, X), 0x20000003);
}

BOOST_AUTO_TEST_SUITE_END()
