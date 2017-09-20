#include <boost/test/unit_test.hpp>

#include "pt_status.h"

#include "test_reader.h"

#include "string_util.h"

#include "boost_test_helpers.h"

BOOST_AUTO_TEST_SUITE( test_pt_status )

struct PointAttributes
{
    unsigned flags;
    bool outOfService, alarmDisabled, pseudoPoint;
    struct ControlParameters
    {
        CtiControlType_t controlType;
        bool controlInhibited;
        bool operator==(const ControlParameters& other) const
        {
            return controlType == other.controlType 
                && controlInhibited == other.controlInhibited;
        }
    };
    boost::optional<ControlParameters> cp;

    bool operator!=(const PointAttributes& other) const
    {
        return ! ( flags == other.flags 
            && outOfService  == other.outOfService
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
    l.add("Out of service") << pa.outOfService;
    l.add("Alarm disabled") << pa.alarmDisabled;
    l.add("Pseudo point")   << pa.pseudoPoint;
    l.add("Control parameters set?") << pa.cp.is_initialized();
    if( pa.cp )
    {
        l.add("Control type")      << pa.cp->controlType;
        l.add("Control inhibited") << pa.cp->controlInhibited;
    }

    return os << l.toString();
}

BOOST_AUTO_TEST_CASE(test_adjustStaticTags)
{
    using StatusPointRow = Cti::Test::StringRow<20>;
    using StatusPointReader = Cti::Test::TestReader<StatusPointRow> ;
    using SPR = StatusPointReader;

    StatusPointRow columnNames = 
        { "pointid", "pointname", "pointtype", "paobjectid", "stategroupid", "pointoffset", "serviceflag", "alarminhibit", "pseudoflag", "archivetype", "archiveinterval", "initialstate", "controltype", "closetime1", "closetime2", "statezerocontrol", "stateonecontrol", "commandtimeout", "controlinhibit", "controloffset" };
    std::vector<StatusPointRow> rows {
        //  control missing, all base flags off and on
        { "1234",    "AA",        "status",    "99",         "31",           "7",           "N",           "N",            "R",          "None",        "8",               "17",           SPR::getNullString(), SPR::getNullString(), SPR::getNullString(), SPR::getNullString(), SPR::getNullString(), SPR::getNullString(), SPR::getNullString(), SPR::getNullString() },
        { "1234",    "AA",        "status",    "99",         "31",           "7",           "Y",           "Y",            "P",          "None",        "8",               "17",           SPR::getNullString(), SPR::getNullString(), SPR::getNullString(), SPR::getNullString(), SPR::getNullString(), SPR::getNullString(), SPR::getNullString(), SPR::getNullString() },
        //  control invalid        
        { "1234",    "AA",        "status",    "99",         "31",           "7",           "N",           "N",            "R",          "None",        "8",               "17",           "jimmy",       "47",         "58",         "apple",            "banana",          "17",             "N",              "2" },
        { "1234",    "AA",        "status",    "99",         "31",           "7",           "Y",           "Y",            "P",          "None",        "8",               "17",           "jimmy",       "47",         "58",         "apple",            "banana",          "17",             "N",              "2" },
            //  control inhibited                                                                                                                                                                                                                                                                                 
        { "1234",    "AA",        "status",    "99",         "31",           "7",           "N",           "N",            "R",          "None",        "8",               "17",           "jimmy",       "47",         "58",         "apple",            "banana",          "17",             "Y",              "2" },
        { "1234",    "AA",        "status",    "99",         "31",           "7",           "Y",           "Y",            "P",          "None",        "8",               "17",           "jimmy",       "47",         "58",         "apple",            "banana",          "17",             "Y",              "2" },
        //  control valid                                                                                                                                                                                                                                                                                         
        { "1234",    "AA",        "status",    "99",         "31",           "7",           "N",           "N",            "R",          "None",        "8",               "17",           "normal",      "47",         "58",         "apple",            "banana",          "17",             "N",              "2" },
        { "1234",    "AA",        "status",    "99",         "31",           "7",           "Y",           "Y",            "P",          "None",        "8",               "17",           "normal",      "47",         "58",         "apple",            "banana",          "17",             "N",              "2" },
            //  control inhibited                                                                                                                                                                                                                                                                           
        { "1234",    "AA",        "status",    "99",         "31",           "7",           "N",           "N",            "R",          "None",        "8",               "17",           "normal",      "47",         "58",         "apple",            "banana",          "17",             "Y",              "2" },
        { "1234",    "AA",        "status",    "99",         "31",           "7",           "Y",           "Y",            "P",          "None",        "8",               "17",           "normal",      "47",         "58",         "apple",            "banana",          "17",             "Y",              "2" }};

    StatusPointReader rdr { columnNames, rows };
    
    using CP = PointAttributes::ControlParameters;
    const bool X = true, _ = false;

    const std::vector<PointAttributes> pa_expected {
        { 0xcffffff8, _, _, _, boost::none },                    //  no flags
        { 0xeffffffb, X, X, X, boost::none },                    //  base flags
        { 0xcffffff8, _, _, _, CP { ControlType_Invalid, _ } },  //  no flags
        { 0xeffffffb, X, X, X, CP { ControlType_Invalid, _ } },  //  base flags
        { 0xcffffffc, _, _, _, CP { ControlType_Invalid, X } },  //  control inhibited
        { 0xefffffff, X, X, X, CP { ControlType_Invalid, X } },  //  control inhibited + base flags
        { 0xdffffff8, _, _, _, CP { ControlType_Normal,  _ } },  //  control available
        { 0xfffffffb, X, X, X, CP { ControlType_Normal,  _ } },  //  control available + base flags
        { 0xdffffffc, _, _, _, CP { ControlType_Normal,  X } },  //  control available + control inhibited
        { 0xffffffff, X, X, X, CP { ControlType_Normal,  X } },  //  control available + control inhibited + base flags
    };

    std::vector<PointAttributes> pa_results;

    CtiPointStatus p;

    for( int i = 0; i < rows.size(); i++ )
    {
        rdr();

        p.DecodeDatabaseReader(rdr);

        BOOST_CHECK_EQUAL(p.getID(),   1234);
        BOOST_CHECK_EQUAL(p.getName(), "AA");
        BOOST_CHECK_EQUAL(p.getType(), StatusPointType);
        BOOST_CHECK_EQUAL(p.getDeviceID(), 99);
        BOOST_CHECK_EQUAL(p.getStateGroupID(), 31);
        BOOST_CHECK_EQUAL(p.getPointOffset(), 7);
        BOOST_CHECK_EQUAL(p.getArchiveType(), ArchiveTypeNone);
        BOOST_CHECK_EQUAL(p.getArchiveInterval(), 8);
        BOOST_CHECK_EQUAL(p.getDefaultValue(), 17);
        
        unsigned tags = -1;
        p.adjustStaticTags(tags);

        PointAttributes pa { tags, p.isOutOfService(), p.isAlarmDisabled(), p.isPseudoPoint(), boost::none };
        
        if( auto cp = p.getControlParameters() )
        {
            pa.cp = PointAttributes::ControlParameters { cp->getControlType(), cp->isControlInhibited() };
        }

        pa_results.push_back(pa);
    }

    BOOST_CHECK_EQUAL_RANGES(pa_results, pa_expected);
}

BOOST_AUTO_TEST_SUITE_END()
