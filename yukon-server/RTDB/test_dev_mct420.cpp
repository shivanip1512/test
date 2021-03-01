#include <boost/test/unit_test.hpp>

#include "dev_mct420.h"
#include "devicetypes.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "utility.h"  //  for delete_container

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

#include <boost/algorithm/cxx11/all_of.hpp>
#include <boost/assign/list_of.hpp>

using namespace Cti::Protocols;
using Cti::Test::isSentOnRouteMsg;
using Cti::Test::makeInmessReply;

using std::string;
using std::vector;
typedef CtiTableDynamicPaoInfo Dpi;

struct test_Mct420Device : Cti::Devices::Mct420Device
{
    test_Mct420Device(DeviceTypes type, const string &name) 
        : rte(boost::make_shared<Cti::Test::test_CtiRouteCCU>())
    {
        setDeviceType(type);
        _name = name;
        _paObjectID = 123456;
    }

    using CtiTblPAOLite::_type;

    using MctDevice::getOperation;
    using MctDevice::ReadDescriptor;
    using MctDevice::value_locator;
    using MctDevice::getDescriptorForRead;
    using MctDevice::ResultDecode;
    using MctDevice::decodeGetStatusDisconnect;

    using Mct4xxDevice::getUsageReportDelay;

    using Mct420Device::getDemandData;
    using Mct420Device::executeGetValue;
    using Mct420Device::executeGetStatus;

    using Mct420Device::decodeGetValueDailyRead;
    using Mct420Device::decodeGetValueOutage;
    using Mct420Device::decodeDisconnectConfig;
    using Mct420Device::decodeDisconnectStatus;

    using Mct420Device::isProfileTablePointerCurrent;

    bool test_isSupported_Mct410Feature_HourlyKwh() const
            {  return isSupported(Feature_HourlyKwh);  }

    bool test_isSupported_Mct410Feature_DisconnectCollar() const
            {  return isSupported(Feature_DisconnectCollar);  }

    bool test_isSupported_Mct4xxFeature_LoadProfilePeakReport() const
            {  return Mct410Device::isSupported(Feature_LoadProfilePeakReport);  }

    bool test_isSupported_Mct4xxFeature_TouPeaks() const
            {  return Mct410Device::isSupported(Feature_TouPeaks);  }

    CtiRouteSPtr rte;
    Cti::Test::DevicePointHelper pointHelper;

    CtiPointSPtr getDevicePointOffsetTypeEqual(int offset, CtiPointType_t type) override
    {
        return pointHelper.getCachedPoint(offset, type);
    }

    CtiRouteSPtr getRoute(long routeId) const override
    {
        return rte;
    }

    std::string resolveStateName(long groupId, long rawValue) const override
    {
        static const std::array<const char*, 10> stateNames{
            "False", "True", "State Two", "State Three", "State Four", "State Five", "State Six", "State Seven", "State Eight", "State Nine"
        };

        if( rawValue >= 0 && rawValue < stateNames.size() )
        {
            return stateNames[rawValue];
        }

        return "State " + std::to_string(rawValue);
    }
};

struct test_Mct420CL : test_Mct420Device
{
    test_Mct420CL() : test_Mct420Device(TYPEMCT420CL, "Test MCT-420CL")  {}
};

struct test_Mct420CD : test_Mct420Device
{
    test_Mct420CD() : test_Mct420Device(TYPEMCT420CD, "Test MCT-420CD")  {}
};

struct test_Mct420FL : test_Mct420Device
{
    test_Mct420FL() : test_Mct420Device(TYPEMCT420FL, "Test MCT-420FL")  {}
};

struct test_Mct420FD : test_Mct420Device
{
    test_Mct420FD() : test_Mct420Device(TYPEMCT420FD, "Test MCT-420FD")  {}
};

namespace Cti {
    //  defined in rtdb/test_main.cpp
    std::ostream& operator<<(std::ostream& o, const ConnectionHandle& h);
}

namespace std {
    //  defined in rtdb/test_main.cpp
    std::ostream& operator<<(std::ostream& out, const test_Mct420Device::ReadDescriptor &rd);
    std::ostream& operator<<(std::ostream& out, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rd);
    bool operator==(const test_Mct420Device::value_locator &lhs, const boost::tuples::tuple<unsigned, unsigned, int> &rhs);
}

namespace boost {
namespace test_tools {
    //  defined in rtdb/test_main.cpp
    bool operator!=(const test_Mct420Device::ReadDescriptor &lhs, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rhs);
}
}

struct overrideGlobals
{
    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
};

BOOST_FIXTURE_TEST_SUITE( test_dev_mct420, overrideGlobals )

BOOST_AUTO_TEST_CASE(test_isSupported_Mct410Feature_DisconnectCollar)
{
    BOOST_CHECK_EQUAL(false, test_Mct420CL().test_isSupported_Mct410Feature_DisconnectCollar());

    BOOST_CHECK_EQUAL(true,  test_Mct420FL().test_isSupported_Mct410Feature_DisconnectCollar());

    BOOST_CHECK_EQUAL(false, test_Mct420CD().test_isSupported_Mct410Feature_DisconnectCollar());

    BOOST_CHECK_EQUAL(false, test_Mct420FD().test_isSupported_Mct410Feature_DisconnectCollar());
}

BOOST_AUTO_TEST_CASE(test_isSupported_Mct410Feature_HourlyKwh)
{
    BOOST_CHECK_EQUAL(true, test_Mct420CL().test_isSupported_Mct410Feature_HourlyKwh());

    BOOST_CHECK_EQUAL(true, test_Mct420FL().test_isSupported_Mct410Feature_HourlyKwh());

    BOOST_CHECK_EQUAL(true, test_Mct420CD().test_isSupported_Mct410Feature_HourlyKwh());

    BOOST_CHECK_EQUAL(true, test_Mct420FD().test_isSupported_Mct410Feature_HourlyKwh());
}

BOOST_AUTO_TEST_CASE(test_isSupported_Mct4xxFeature_LoadProfilePeakReport)
{
    test_Mct420CL mct;

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 9);

    BOOST_CHECK_EQUAL(false, mct.test_isSupported_Mct4xxFeature_LoadProfilePeakReport());

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 10291);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 1);

    BOOST_CHECK_EQUAL(false, mct.test_isSupported_Mct4xxFeature_LoadProfilePeakReport());

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 10291);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 9);

    BOOST_CHECK_EQUAL(true, mct.test_isSupported_Mct4xxFeature_LoadProfilePeakReport());
}

BOOST_AUTO_TEST_CASE(test_isSupported_Mct4xxFeature_TouPeaks)
{
    test_Mct420CL mct;

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 1029);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 13);

    BOOST_CHECK_EQUAL(false, mct.test_isSupported_Mct4xxFeature_TouPeaks());

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 10291);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 12);

    BOOST_CHECK_EQUAL(false, mct.test_isSupported_Mct4xxFeature_TouPeaks());

    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 10291);
    mct.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 13);

    BOOST_CHECK_EQUAL(true, mct.test_isSupported_Mct4xxFeature_TouPeaks());
}

BOOST_AUTO_TEST_CASE( test_dev_mct420_getDemandData )
{
    test_Mct420CL dev;

    const unsigned char *none = 0;

    const unsigned char even = 12;
    const unsigned char odd = 13;

    struct demand_checks
    {
        const unsigned char raw_value[2];
        const unsigned char *freeze_counter;
        const double value;
        const bool freeze_bit;
    };

    demand_checks dc[10] = {
        { { 0x30, 0x05 }, none, 0.005, 1 },     // freeze bit is the lowest bit of the raw data when no counter
        { { 0x30, 0x05 }, &odd, 0.005, 0 },     // freeze bit is the inverse of lowest bit of the counter
        { { 0x30, 0x04 }, none, 0.004, 0 },     // freeze bit is the lowest bit of the raw data when no counter
        { { 0x30, 0x04 }, &even, 0.004, 1 },    // freeze bit is the inverse of lowest bit of the counter
        { { 0x2f, 0x0f }, none, 38.55, 1 },
        { { 0x2f, 0x0f }, &odd, 38.55, 0 },
        { { 0x2f, 0x0e }, none, 38.54, 0 },
        { { 0x2f, 0x0e }, &even, 38.54, 1 },
        { { 0x01, 0x11 }, none, 273, 1 },
        { { 0x01, 0x11 }, &odd, 273, 0 }
    };

    test_Mct420Device::frozen_point_info pi;

    pi = dev.getDemandData( dc[0].raw_value, 2, dc[0].freeze_counter );
    BOOST_CHECK_EQUAL( pi.value, dc[0].value );
    BOOST_CHECK_EQUAL( pi.freeze_bit, dc[0].freeze_bit );

    pi = dev.getDemandData( dc[1].raw_value, 2, dc[1].freeze_counter );
    BOOST_CHECK_EQUAL( pi.value, dc[1].value );
    BOOST_CHECK_EQUAL( pi.freeze_bit, dc[1].freeze_bit );

    pi = dev.getDemandData( dc[2].raw_value, 2, dc[2].freeze_counter );
    BOOST_CHECK_EQUAL( pi.value, dc[2].value );
    BOOST_CHECK_EQUAL( pi.freeze_bit, dc[2].freeze_bit );

    pi = dev.getDemandData( dc[3].raw_value, 2, dc[3].freeze_counter );
    BOOST_CHECK_EQUAL( pi.value, dc[3].value );
    BOOST_CHECK_EQUAL( pi.freeze_bit, dc[3].freeze_bit );

    pi = dev.getDemandData( dc[4].raw_value, 2, dc[4].freeze_counter );
    BOOST_CHECK_EQUAL( pi.value, dc[4].value );
    BOOST_CHECK_EQUAL( pi.freeze_bit, dc[4].freeze_bit );

    pi = dev.getDemandData( dc[5].raw_value, 2, dc[5].freeze_counter );
    BOOST_CHECK_EQUAL( pi.value, dc[5].value );
    BOOST_CHECK_EQUAL( pi.freeze_bit, dc[5].freeze_bit );

    pi = dev.getDemandData( dc[6].raw_value, 2, dc[6].freeze_counter );
    BOOST_CHECK_EQUAL( pi.value, dc[6].value );
    BOOST_CHECK_EQUAL( pi.freeze_bit, dc[6].freeze_bit );

    pi = dev.getDemandData( dc[7].raw_value, 2, dc[7].freeze_counter );
    BOOST_CHECK_EQUAL( pi.value, dc[7].value );
    BOOST_CHECK_EQUAL( pi.freeze_bit, dc[7].freeze_bit );

    pi = dev.getDemandData( dc[8].raw_value, 2, dc[8].freeze_counter );
    BOOST_CHECK_EQUAL( pi.value, dc[8].value );
    BOOST_CHECK_EQUAL( pi.freeze_bit, dc[8].freeze_bit );

    pi = dev.getDemandData( dc[9].raw_value, 2, dc[9].freeze_counter );
    BOOST_CHECK_EQUAL( pi.value, dc[9].value );
    BOOST_CHECK_EQUAL( pi.freeze_bit, dc[9].freeze_bit );
}

BOOST_AUTO_TEST_CASE( test_decodeDisconnectConfig )
{
    //  Test case permutations:
    //    MCT type:  MCT420CL, MCT420CD, MCT420FL, MCT420FD
    //    Config byte: autoreconnect disabled, autoreconnect enabled
    //  Config byte cannot be missing when the SSPEC is > ConfigReadEnhanced, since it returns the config byte

    struct test_case
    {
        const DeviceTypes mct_type;
        const int config_byte;
        const string expected;
    }
    test_cases[] =
    {
        //  MCT-420CL
        {TYPEMCT420CL,  0x00, "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {TYPEMCT420CL,  0x04, "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},

        //  MCT-420CD
        {TYPEMCT420CD,  0x00, "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {TYPEMCT420CD,  0x04, "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},

        //  MCT-420FL
        {TYPEMCT420FL,  0x00, "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {TYPEMCT420FL,  0x04, "Disconnect receiver address: 131844\n"
                              "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Autoreconnect enabled\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},

        //  MCT-420FD
        {TYPEMCT420FD,  0x00, "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {TYPEMCT420FD,  0x04, "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
    };

    std::vector<std::string> expected, results;

    for each( const test_case &tc in test_cases )
    {
        expected.push_back(tc.expected);

        {
            DSTRUCT DSt;

            DSt.Message[2] = 2;     //  Disconnect address
            DSt.Message[3] = 3;     //     :
            DSt.Message[4] = 4;     //     :
            DSt.Message[5] = 0x35;  //  Disconnect demand threshold
            DSt.Message[6] = 0x08;  //     :
            DSt.Message[7] = 34;    //  Disconnect load limit connect delay

            DSt.Message[9]  = 10;
            DSt.Message[10] = 11;

            DSt.Message[11] = tc.config_byte;
            DSt.Message[12] = 205;

            DSt.Length      = 13;

            test_Mct420Device mct420(tc.mct_type, "No name");

            mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 40);
            mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, tc.config_byte);

            results.push_back(mct420.decodeDisconnectConfig(DSt));
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(expected.begin(), expected.end(),
                                  results.begin(),  results.end());
}


BOOST_AUTO_TEST_CASE(test_dev_mct420_decodeDisconnectStatus)
{
    struct test_case
    {
        const unsigned char dst_message_0;
        const unsigned char dst_message_1;
        const unsigned char dst_message_8;
        const string expected;
    }
    test_cases[] =
    {
        {0x3c, 0x02, 123, "Load limiting mode active\n"
                          "Cycling mode active, currently connected\n"
                          "Disconnect state uncertain (powerfail during disconnect)\n"
                          "Disconnect error - demand detected after disconnect command sent to collar\n"
                          "Disconnect load limit count: 123\n"},
        {0x7c, 0x02, 124, "Load side voltage detected\n"
                          "Load limiting mode active\n"
                          "Cycling mode active, currently connected\n"
                          "Disconnect state uncertain (powerfail during disconnect)\n"
                          "Disconnect error - demand detected after disconnect command sent to collar\n"
                          "Disconnect load limit count: 124\n"},
        {0x10, 0x00, 125, "Disconnect load limit count: 125\n"},
        {0x50, 0x00, 126, "Load side voltage detected\n"
                          "Disconnect load limit count: 126\n"},
    };

    std::vector<std::string> expected, results;

    for each( const test_case &tc in test_cases )
    {
        expected.push_back(tc.expected);

        {
            test_Mct420CL mct420;

            DSTRUCT DSt;

            DSt.Message[0] = tc.dst_message_0;
            DSt.Message[1] = tc.dst_message_1;
            DSt.Message[8] = tc.dst_message_8;

            results.push_back(mct420.decodeDisconnectStatus(DSt));
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(expected.begin(), expected.end(),
                                  results.begin(),  results.end());
}


BOOST_AUTO_TEST_CASE(test_decodePulseAccumulator)
{
    unsigned char kwh_read[3] = { 0x00, 0x02, 0x00 };

    Cti::Devices::Mct4xxDevice::frozen_point_info pi;

    pi = Cti::Devices::Mct420Device::decodePulseAccumulator(kwh_read, 3, 0);

    BOOST_CHECK_EQUAL( pi.value,      512 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );

    kwh_read[2] = 0x01;

    pi = Cti::Devices::Mct420Device::decodePulseAccumulator(kwh_read, 3, 0);

    BOOST_CHECK_EQUAL( pi.value,      513 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );
}


BOOST_AUTO_TEST_CASE(test_isProfileTablePointerCurrent)
{
    test_Mct420CL dev;

    {
        CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(254, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(255, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(  0, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(254, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(255, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(  0, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(254, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(255, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(  0, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(255, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(  0, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(  1, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(  0, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(  1, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(  2, t,  300));
    }

    {
        CtiTime t(CtiDate(1, 1, 2011), 1, 16, 0);  //  1293866160 seconds (0x4D1ED4B0)

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(251, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(252, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(253, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(248, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(249, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(250, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(242, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(243, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(244, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(237, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(238, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(249, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(220, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(221, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(222, t,  300));
    }

    {
        CtiTime t(CtiDate(1, 1, 2011), 9, 16, 0);  //  1293894960 seconds (0x4D1F4530)

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(252, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(253, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(254, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(251, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(252, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(253, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(248, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(249, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(250, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(245, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(246, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(247, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(236, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(237, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(238, t,  300));
    }

    {
        CtiTime t(CtiDate(1, 1, 2011), 12, 26, 0);  //  1293906360 seconds (0x4D1F71B8)

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(253, t, 3600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(254, t, 3600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(255, t, 3600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(252, t, 1800));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(253, t, 1800));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(254, t, 1800));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(250, t,  900));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(251, t,  900));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(252, t,  900));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(248, t,  600));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(249, t,  600));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(250, t,  600));

        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(242, t,  300));
        BOOST_CHECK_EQUAL(true,  dev.isProfileTablePointerCurrent(243, t,  300));
        BOOST_CHECK_EQUAL(false, dev.isProfileTablePointerCurrent(244, t,  300));
    }
}


struct beginExecuteRequest_helper : overrideGlobals
{
    const Cti::ConnectionHandle connHandle{ 999 };
    CtiRequestMsg           request;
    std::list<CtiMessage*>  vgList, retList;
    std::list<OUTMESS*>     outList;

    beginExecuteRequest_helper()
    {
        request.setConnectionHandle(connHandle);
    }

    ~beginExecuteRequest_helper()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
    }
};

BOOST_FIXTURE_TEST_SUITE(command_executions, beginExecuteRequest_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_getconfig_centron_parameters)
    {
        CtiCommandParser parse("getconfig centron parameters");

        BOOST_CHECK_EQUAL( ClientErrors::NoMethod, test_Mct420CL().beginExecuteRequest(&request, parse, vgList, retList, outList) );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_centron_ratio)
    {
        CtiCommandParser parse("getconfig centron ratio");

        BOOST_CHECK_EQUAL( ClientErrors::NoMethod, test_Mct420CL().beginExecuteRequest(&request, parse, vgList, retList, outList) );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_meter_parameters)
    {
        CtiCommandParser parse("getconfig meter parameters");

        BOOST_CHECK_EQUAL( ClientErrors::None, test_Mct420CL().beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf3 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   2 );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_meter_ratio)
    {
        CtiCommandParser parse("getconfig meter ratio");

        BOOST_CHECK_EQUAL( ClientErrors::None, test_Mct420CL().beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf3 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   2 );
    }

    BOOST_AUTO_TEST_CASE(test_putconfig_centron)
    {
        //  This is the old MCT-410 command and should fail.
        CtiCommandParser parse("putconfig emetcon centron ratio 1 display 5x1 test 1 errors enable");

        BOOST_CHECK_EQUAL(ClientErrors::NoMethod, test_Mct420CL().beginExecuteRequest(&request, parse, vgList, retList, outList));
    }

    BOOST_AUTO_TEST_CASE(test_putconfig_meter_parameters_no_lcd_digits)
    {
        test_Mct420CL mct420;

        CtiCommandParser parse("putconfig emetcon meter parameters ratio 1 lcd cycle time 8 disconnect display enable");

        BOOST_CHECK_EQUAL(ClientErrors::None, mct420.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(outList.size(), 1);

        const OUTMESS* om = outList.front();

        BOOST_CHECK_EQUAL(om->Buffer.BSt.IO, Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(om->Buffer.BSt.Function, 0xf3);
        BOOST_REQUIRE_EQUAL(om->Buffer.BSt.Length, 3);

        const Cti::Test::byte_str expected = "ff 08 01";

        BOOST_CHECK_EQUAL_COLLECTIONS(
            expected.begin(),
            expected.end(),
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length);
    }

    BOOST_AUTO_TEST_CASE(test_putconfig_meter_parameters_lcd_digits_insufficient_sspec)
    {
        test_Mct420CL mct420;

        CtiCommandParser parse("putconfig emetcon meter parameters ratio 1 lcd cycle time 8 disconnect display enable lcd display digits 5x1");

        BOOST_CHECK_EQUAL(ClientErrors::None, mct420.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto retMsg = dynamic_cast<const CtiReturnMsg*>(retList.front());

        BOOST_REQUIRE(retMsg);

        BOOST_CHECK_EQUAL(retMsg->Status(), 269);
        BOOST_CHECK_EQUAL(retMsg->ResultString(),
            "Test MCT-420CL / LCD display digits not supported for this device's SSPEC");
    }

    BOOST_AUTO_TEST_CASE(test_putconfig_meter_parameters_lcd_digits)
    {
        test_Mct420CL mct420;

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec, 10290);
        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 44);  //  set the device to SSPEC revision 4.4

        CtiCommandParser parse("putconfig emetcon meter parameters ratio 1 lcd cycle time 8 disconnect display enable lcd display digits 4x1");

        BOOST_CHECK_EQUAL(ClientErrors::None, mct420.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(outList.size(), 1);

        const OUTMESS* om = outList.front();

        BOOST_CHECK_EQUAL(om->Buffer.BSt.IO, Cti::Protocols::EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(om->Buffer.BSt.Function, 0xf3);
        BOOST_REQUIRE_EQUAL(om->Buffer.BSt.Length, 3);

        const Cti::Test::byte_str expected = "ff 28 01";

        BOOST_CHECK_EQUAL_COLLECTIONS(
            expected.begin(),
            expected.end(),
            om->Buffer.BSt.Message,
            om->Buffer.BSt.Message + om->Buffer.BSt.Length);
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct420_getconfig_options_all_zeroes)
    {
        test_Mct420CL mct420;
        long sequence;

        {
            CtiCommandParser parse( "getconfig options" );

            BOOST_CHECK_EQUAL( ClientErrors::None, mct420.beginExecuteRequest(&request, parse, vgList, retList, outList) );

            BOOST_REQUIRE_EQUAL( outList.size(), 1 );

            const OUTMESS *om = outList.front();

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x01 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

            sequence = om->Sequence;
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

            INMESS im;

            const std::vector<unsigned char> input = boost::assign::list_of(0).repeat(5, 0);

            std::copy(input.begin(), input.end(), im.Buffer.DSt.Message);
            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            im.Sequence = sequence;

            BOOST_CHECK_EQUAL( ClientErrors::None, mct420.ResultDecode(im, timeNow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL(
                    retMsg->ResultString(),
                    "Test MCT-420CL / Configuration information:"
                    "\nDST disabled"
                    "\nLED test disabled"
                    "\nReconnect button required"
                    "\nDemand limit mode disabled"
                    "\nDisconnect cycling mode disabled"
                    "\nRepeater role disabled"
                    "\nDisconnect collar is not MCT-410d Rev E (or later)"
                    "\nDaily reporting disabled"
                    "\nTest MCT-420CL / Event mask information:"
                    "\nZero usage event mask disabled"
                    "\nDisconnect error event mask disabled"
                    "\nMeter reading corrupted event mask disabled"
                    "\nReverse power event mask disabled"
                    "\nPower fail event event mask disabled"
                    "\nUnder voltage event event mask disabled"
                    "\nOver voltage event event mask disabled"
                    "\nRTC lost event mask disabled"
                    "\nRTC adjusted event mask disabled"
                    "\nHoliday flag event mask disabled"
                    "\nDST change event mask disabled"
                    "\nTamper flag event mask disabled"
                    "\nTest MCT-420CL / Meter alarm mask information:"
                    "\nUnprogrammed meter alarm mask disabled"
                    "\nConfiguration error meter alarm mask disabled"
                    "\nSelf check error meter alarm mask disabled"
                    "\nRAM failure meter alarm mask disabled"
                    "\nNon-volatile memory failure meter alarm mask disabled"
                    "\nMeasurement error meter alarm mask disabled"
                    "\nPower failure meter alarm mask disabled"
                    "\nTest MCT-420CL / Channel configuration:"
                    "\nChannel 2: No meter attached"
                    "\nChannel 3: No meter attached"
                    "\n");
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420_getconfig_options_all_Fs)
    {
        test_Mct420CL mct420;
        long sequence;

        {
            CtiCommandParser parse( "getconfig options" );

            BOOST_CHECK_EQUAL( ClientErrors::None, mct420.beginExecuteRequest(&request, parse, vgList, retList, outList) );

            BOOST_REQUIRE_EQUAL( outList.size(), 1 );

            const OUTMESS *om = outList.front();

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x01 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   6 );

            sequence = om->Sequence;
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

            INMESS im;

            const std::vector<unsigned char> input = boost::assign::list_of(0xff).repeat(5, 0xff);

            std::copy(input.begin(), input.end(), im.Buffer.DSt.Message);
            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            im.Sequence = sequence;

            BOOST_CHECK_EQUAL( ClientErrors::None, mct420.ResultDecode(im, timeNow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL(
                    retMsg->ResultString(),
                    "Test MCT-420CL / Configuration information:"
                    "\nDST enabled"
                    "\nLED test enabled"
                    "\nReconnect button not required"
                    "\nDemand limit mode enabled"
                    "\nDisconnect cycling mode enabled"
                    "\nRepeater role enabled"
                    "\nDisconnect collar is MCT-410d Rev E (or later)"
                    "\nDaily reporting enabled"
                    "\nTest MCT-420CL / Event mask information:"
                    "\nZero usage event mask enabled"
                    "\nDisconnect error event mask enabled"
                    "\nMeter reading corrupted event mask enabled"
                    "\nReverse power event mask enabled"
                    "\nPower fail event event mask enabled"
                    "\nUnder voltage event event mask enabled"
                    "\nOver voltage event event mask enabled"
                    "\nRTC lost event mask enabled"
                    "\nRTC adjusted event mask enabled"
                    "\nHoliday flag event mask enabled"
                    "\nDST change event mask enabled"
                    "\nTamper flag event mask enabled"
                    "\nTest MCT-420CL / Meter alarm mask information:"
                    "\nUnprogrammed meter alarm mask enabled"
                    "\nConfiguration error meter alarm mask enabled"
                    "\nSelf check error meter alarm mask enabled"
                    "\nRAM failure meter alarm mask enabled"
                    "\nNon-volatile memory failure meter alarm mask enabled"
                    "\nMeasurement error meter alarm mask enabled"
                    "\nPower failure meter alarm mask enabled"
                    "\nTest MCT-420CL / Channel configuration:"
                    "\nChannel 2: Net metering mode enabled"
                    "\n");
        }
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

struct control_helper : beginExecuteRequest_helper
{
    void executeControlConnect_expectSuccess(test_Mct420Device &dev)
    {
        CtiRequestMsg    req( -1, "control connect" );
        CtiCommandParser parse( req.CommandString() );

        BOOST_CHECK_EQUAL( 0, dev.beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_CHECK( retList.empty() );
        BOOST_REQUIRE_EQUAL( 1, vgList .size() );
        BOOST_REQUIRE_EQUAL( 1, outList.size() );

        const auto signalMsg = dynamic_cast<const CtiSignalMsg*>(vgList.front());
        BOOST_REQUIRE( signalMsg );
        BOOST_CHECK_EQUAL( signalMsg->getLogType(), LoadMgmtLogType );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE( om );
        BOOST_CHECK_EQUAL( om->DeviceID, 12345 );
        //BOOST_CHECK_EQUAL( om->MessageFlags, 80 );  //  Must be checked separately for the integrated disconnect vs. collar disconnect meters
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 76 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   0 );
        BOOST_CHECK_EQUAL( om->Request.CommandStr,  "control connect" );
    }
    void decodeControlConnect_expectSuccess(test_Mct420Device &dev)
    {
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        INMESS im;

        im.Sequence = EmetconProtocol::Control_Disconnect;
        im.Buffer.DSt.Length = 0;
        im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

        strcpy(im.Return.CommandStr, "control disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None, dev.ResultDecode(im, timeNow, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( 2, retList.size() );
        BOOST_CHECK( outList.empty() );

        auto retList_itr = retList.cbegin();

        auto req = dynamic_cast<const CtiRequestMsg *>(*retList_itr++);

        BOOST_REQUIRE( req );
        BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( req->CommandString(), "getstatus disconnect" );

        auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

        BOOST_REQUIRE( ret );
        BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( ret->Status(),   0 );
        BOOST_CHECK_EQUAL( ret->CommandString(), "control disconnect" );
        //BOOST_CHECK_EQUAL( ret->ResultString(),  //  Must be checked per device, since the names differ.
    }
    void executeControlConnect_expectNoMethod(test_Mct420Device &dev)
    {
        CtiRequestMsg    req( -1, "control connect" );
        CtiCommandParser parse( req.CommandString() );

        BOOST_CHECK_EQUAL( 202, dev.beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList .empty() );
        BOOST_REQUIRE_EQUAL( 1, retList.size() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *ret = dynamic_cast<const CtiReturnMsg *>(retList.back());

        BOOST_REQUIRE( ret );
        BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( ret->Status(),   202 );
        BOOST_CHECK_EQUAL( ret->CommandString(), "control connect" );
        BOOST_CHECK_EQUAL( ret->ResultString(),  "NoMethod or invalid command." );
    }
    void executeControlDisconnect_expectSuccess(test_Mct420Device &dev)
    {
        CtiRequestMsg    req( -1, "control disconnect" );
        CtiCommandParser parse( req.CommandString() );

        BOOST_CHECK_EQUAL( 0, dev.beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( 1, vgList .size() );
        BOOST_CHECK( retList.empty() );
        BOOST_REQUIRE_EQUAL( 1, outList.size() );

        const auto signalMsg = dynamic_cast<const CtiSignalMsg*>(vgList.front());
        BOOST_REQUIRE( signalMsg );
        BOOST_CHECK_EQUAL( signalMsg->getLogType(), LoadMgmtLogType );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE( om );
        BOOST_CHECK_EQUAL( om->DeviceID, 12345 );
        //BOOST_CHECK_EQUAL( om->MessageFlags, 80 );  //  Must be checked separately for the integrated disconnect vs. collar disconnect meters
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 77 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       0 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   0 );
        BOOST_CHECK_EQUAL( om->Request.CommandStr, "control disconnect" );
    }
    void decodeControlDisconnect_expectSuccess(test_Mct420Device &dev)
    {
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        INMESS im;

        im.Sequence = EmetconProtocol::Control_Disconnect;
        im.Buffer.DSt.Length = 0;
        im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

        strcpy(im.Return.CommandStr, "control disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None, dev.ResultDecode(im, timeNow, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( 2, retList.size() );
        BOOST_CHECK( outList.empty() );

        auto retList_itr = retList.cbegin();

        auto req = dynamic_cast<const CtiRequestMsg *>(*retList_itr++);

        BOOST_REQUIRE( req );
        BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( req->CommandString(), "getstatus disconnect" );

        auto ret = dynamic_cast<const CtiReturnMsg *>(*retList_itr++);

        BOOST_REQUIRE( ret );
        BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( ret->Status(),   0 );
        BOOST_CHECK_EQUAL( ret->CommandString(), "control disconnect" );
        //BOOST_CHECK_EQUAL( ret->ResultString(),  //  Must be checked per device, since the names differ
    }
    void executeControlDisconnect_expectNoMethod(test_Mct420Device &dev)
    {
        CtiRequestMsg    req( -1, "control disconnect" );
        CtiCommandParser parse( req.CommandString() );

        BOOST_CHECK_EQUAL( 202, dev.beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList .empty() );
        BOOST_REQUIRE_EQUAL( 1, retList.size() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *ret = dynamic_cast<const CtiReturnMsg *>(retList.back());

        BOOST_REQUIRE( ret );
        BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( ret->Status(),   202 );
        BOOST_CHECK_EQUAL( ret->CommandString(), "control disconnect" );
        BOOST_CHECK_EQUAL( ret->ResultString(),  "NoMethod or invalid command." );
    }
};

BOOST_FIXTURE_TEST_SUITE(control_commands, control_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE

    //  MCT-420 CD
    BOOST_AUTO_TEST_CASE(test_dev_mct420cd_control_connect_execute)
    {
        executeControlConnect_expectSuccess(test_Mct420CD());

        //  confirm the message flags are not set for the MCT collar disconnect silence
        BOOST_CHECK_EQUAL( 0, outList.front()->MessageFlags & MessageFlag_AddMctDisconnectSilence );
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420cd_control_connect_decode)
    {
        decodeControlConnect_expectSuccess(test_Mct420CD());

        BOOST_CHECK_EQUAL( dynamic_cast<CtiReturnMsg *>(retList.back())->ResultString(),
                                 "Test MCT-420CD / control sent");
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420cd_control_disconnect_execute)
    {
        executeControlDisconnect_expectSuccess(test_Mct420CD());

        //  confirm the message flags are not set for the MCT collar disconnect silence
        BOOST_CHECK_EQUAL( 0, outList.front()->MessageFlags & MessageFlag_AddMctDisconnectSilence );
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420cd_control_disconnect_decode)
    {
        decodeControlDisconnect_expectSuccess(test_Mct420CD());

        BOOST_CHECK_EQUAL( dynamic_cast<CtiReturnMsg *>(retList.back())->ResultString(),
                                 "Test MCT-420CD / control sent");
    }

    //  MCT-420 CL
    BOOST_AUTO_TEST_CASE(test_dev_mct420cl_control_connect_execute)
    {
        executeControlConnect_expectNoMethod(test_Mct420CL());
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420cl_control_disconnect_execute)
    {
        executeControlDisconnect_expectNoMethod(test_Mct420CL());
    }

    //  MCT-420 FD
    BOOST_AUTO_TEST_CASE(test_dev_mct420fd_control_connect_execute)
    {
        executeControlConnect_expectSuccess(test_Mct420FD());

        //  confirm the message flags are not set for the MCT collar disconnect silence
        BOOST_CHECK_EQUAL( 0, outList.front()->MessageFlags & MessageFlag_AddMctDisconnectSilence );
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420fd_control_connect_decode)
    {
        decodeControlConnect_expectSuccess(test_Mct420FD());

        BOOST_CHECK_EQUAL( dynamic_cast<CtiReturnMsg *>(retList.back())->ResultString(),
                               "Test MCT-420FD / control sent");
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420fd_control_disconnect_execute)
    {
        executeControlDisconnect_expectSuccess(test_Mct420FD());

        //  confirm the message flags are not set for the MCT collar disconnect silence
        BOOST_CHECK_EQUAL( 0, outList.front()->MessageFlags & MessageFlag_AddMctDisconnectSilence );
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420fd_control_disconnect_decode)
    {
        decodeControlDisconnect_expectSuccess(test_Mct420FD());

        BOOST_CHECK_EQUAL( dynamic_cast<CtiReturnMsg *>(retList.back())->ResultString(),
                               "Test MCT-420FD / control sent");
    }

    //  MCT-420 FL
    BOOST_AUTO_TEST_CASE(test_dev_mct420fl_control_connect_execute)
    {
        //  This is an error - we should be failing, since we have no address.
        executeControlConnect_expectSuccess(test_Mct420FL());

        //  confirm the message flags are set for the MCT collar disconnect silence
        BOOST_CHECK_EQUAL( 64, outList.front()->MessageFlags & MessageFlag_AddMctDisconnectSilence );
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420fl_control_connect_decode)
    {
        decodeControlConnect_expectSuccess(test_Mct420FL());

        BOOST_CHECK_EQUAL( dynamic_cast<CtiReturnMsg *>(retList.back())->ResultString(),
                                 "Test MCT-420FL / control sent");
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420fl_control_disconnect_execute_no_address)
    {
        CtiRequestMsg    req( -1, "control disconnect" );
        CtiCommandParser parse( req.CommandString() );

        BOOST_CHECK_EQUAL( 0, test_Mct420FL().beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList .empty() );
        BOOST_REQUIRE_EQUAL(  1, retList.size() );
        BOOST_CHECK( outList.empty() );

        const CtiReturnMsg *ret = dynamic_cast<const CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( ret );

        BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( ret->Status(),   276 );
        BOOST_CHECK_EQUAL( ret->CommandString(), "control disconnect" );
        BOOST_CHECK_EQUAL( ret->ResultString(),  "Test MCT-420FL / Disconnect command cannot be sent to an empty (zero) address" );
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420fl_control_disconnect_execute_with_address)
    {
        test_Mct420FL mct420fl;

        mct420fl.setDisconnectAddress(123);

        executeControlDisconnect_expectSuccess(mct420fl);

        //  confirm the message flags are set for the MCT collar disconnect silence
        BOOST_CHECK_EQUAL( 64, outList.front()->MessageFlags & MessageFlag_AddMctDisconnectSilence );
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420fl_control_disconnect_decode)
    {
        decodeControlDisconnect_expectSuccess(test_Mct420FL());

        BOOST_CHECK_EQUAL( dynamic_cast<CtiReturnMsg *>(retList.back())->ResultString(),
                                 "Test MCT-420FL / control sent");
    }

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


struct commandExecution_helper : beginExecuteRequest_helper
{
    test_Mct420CL mct420;
    OUTMESS *om;

    commandExecution_helper()
    {
        om = new OUTMESS;
    }

    ~commandExecution_helper()
    {
        delete om;
    }
};

BOOST_FIXTURE_TEST_SUITE(commandExecutions, commandExecution_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_dev_mct420_getvalue_daily_reads_0kwh)
    {
        CtiTime timenow;

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            //  The rest are initialized to 0
            im.Buffer.DSt.Message[2] = 0x01;
            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 6 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 1.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 1.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 1.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 1.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 1.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[5]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 1.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct420_getvalue_daily_reads_underflow)
    {
        CtiTime timenow;

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x00, 0x00, 0x03, 0x00, 0x02, 0x00, 0x01, 0x00, 0x01, 0x00, 0x01, 0x00, 0x00 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 6 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), OverflowQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), OverflowQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), OverflowQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 1.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[5]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 3.0 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct420_getvalue_daily_reads_normal_deltas)
    {
        CtiTime timenow;

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x00, 0x01, 0x02, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04, 0x00, 0x05 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 6 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 243 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 248 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 252 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 255 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 257 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[5]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 258 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420_getvalue_daily_reads_bad_first_kwh)
    {
        CtiTime timenow;

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x3f, 0xfa, 0x00, 0x01, 0x02, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 5 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 248 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 252 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 255 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 257 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 258 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420_getvalue_daily_reads_bad_first_delta)
    {
        CtiTime timenow;

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x3f, 0xfa, 0x00, 0x01, 0x02, 0x00, 0x01, 0x00, 0x02, 0x00, 0x03, 0x00, 0x04 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 5 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 248 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 252 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 4 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 255 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 3 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 257 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 258 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420_getvalue_daily_reads_large_deltas)
    {
        CtiTime timenow;

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x01, 0x00, 0x00, 0x3f, 0x9f, 0x3f, 0xa0, 0x3f, 0xa1, 0x3f, 0xfa, 0x3f, 0xff };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 3 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 32961 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 2 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 49249 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 1 );
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 65536 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420_getvalue_daily_reads_all_deltas_bad)
    {
        CtiTime timenow;

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x00, 0x01, 0x02, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 1 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 258 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight );
                }
            }
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420_getvalue_daily_reads_all_bad)
    {
        const CtiDate datenow(4, 7, 2013);
        CtiTime timenow(datenow, 12, 34, 56);

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0xff, 0xff, 0xfa };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( ClientErrors::InvalidTimestamp , mct420.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<const CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::InvalidTimestamp );
            /*
            //  Doesn't work due to const CtiDate Today; in dev_mct410.cpp
            BOOST_CHECK_EQUAL(
                    retMsg->ResultString(),
                    "Test MCT-420CL / PulseAccumulator1 = (invalid data) @ 12/14/2013 00:00:00 [Requested interval outside of valid range]\n"
                    "Test MCT-420CL / PulseAccumulator1 = (invalid data) @ 12/15/2013 00:00:00 [Requested interval outside of valid range]\n"
                    "Test MCT-420CL / PulseAccumulator1 = (invalid data) @ 12/16/2013 00:00:00 [Requested interval outside of valid range]\n"
                    "Test MCT-420CL / PulseAccumulator1 = (invalid data) @ 12/17/2013 00:00:00 [Requested interval outside of valid range]\n"
                    "Test MCT-420CL / PulseAccumulator1 = (invalid data) @ 12/18/2013 00:00:00 [Requested interval outside of valid range]\n"
                    "Test MCT-420CL / PulseAccumulator1 = (invalid data) @ 12/19/2013 00:00:00 [Requested interval outside of valid range]\n"
                    "Multi-day daily read request complete\n" );
            */
            CtiMultiMsg_vec points = retMsg->PointData();

            BOOST_CHECK_EQUAL( points.size(), 0 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct420_getvalue_daily_reads_all_bad_but_last)
    {
        CtiTime timenow;

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_REQUIRE( om );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x20);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            INMESS im;

            unsigned char buf[13] = { 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x3f, 0xfa, 0x12, 0x34, 0x56 };

            std::copy(buf,  buf + 13, im.Buffer.DSt.Message );

            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.decodeGetValueDailyRead(im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_CHECK_EQUAL( points.size(), 1 );

                const CtiDate Midnight(timenow);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0x123456 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), Midnight - 5 );
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct420_getvalue_outage)
    {
        const auto tz_override = Cti::Test::set_to_central_timezone();

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec,         10291);
        //  SSPEC revision does not matter for the MCT-420 outage decode
        //mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 17);  //  set the device to SSPEC revision 1.7

        {
            CtiCommandParser parse( "getvalue outage 1" );

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x10);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   13);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

            INMESS im;

            //  4 bytes of time, 2 bytes duration in cycles.  Last byte is duration types.
            char input[13] = {0x50, 1, 2, 3, 4, 5, 0x50, 7, 8, 9, 10, 11, 0x01};

            std::copy(input, input + 13, im.Buffer.DSt.Message);
            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            strcpy(im.Return.CommandStr, "getvalue outage 1");

            BOOST_CHECK_EQUAL( ClientErrors::None , mct420.decodeGetValueOutage(im, timeNow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            {
                BOOST_REQUIRE_EQUAL( points.size(), 2 );

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_CLOSE( pdata->getValue(), 17.15, 0.001 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(14,  7, 2012), 0, 22, 11) );
                    BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-420CL / Outage 1 : 07/14/2012 00:22:11 for 00:00:17.150");
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_CLOSE( pdata->getValue(), 2571, 0.001 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(18,  7, 2012), 14, 01, 29) );
                    BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-420CL / Outage 2 : 07/18/2012 14:01:29 for 00:42:51");
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct420cd_getstatus_disconnect)
    {
        const auto tz_override = Cti::Test::set_to_central_timezone();

        {
            CtiCommandParser parse( "getstatus disconnect" );

            BOOST_CHECK_EQUAL( ClientErrors::None , test_Mct420CD().executeGetStatus(&request, parse, om, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xFE);
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   1);

            BOOST_CHECK( outList.empty() );
        }

        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);

        vgList.clear();
        retList.clear();
        outList.clear();

        {
            CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

            INMESS im;

            im.Buffer.DSt.Message[0] = 0xFC;
            im.Buffer.DSt.Length = 1;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            strcpy(im.Return.CommandStr, "getstatus disconnect");

            BOOST_CHECK_EQUAL( ClientErrors::None , test_Mct420CD().decodeGetStatusDisconnect(im, timeNow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            BOOST_REQUIRE_EQUAL( points.size(), 1 );

            {
                const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);

                BOOST_REQUIRE( pdata );

                BOOST_CHECK_CLOSE( pdata->getValue(), 1, 0.001 );
                BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                //BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(20, 10, 2011), 13, 37, 22) );
                BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-420CD / Status1:Connected");
            }
        }
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

struct putconfigInstall_helper : beginExecuteRequest_helper
{
    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    putconfigInstall_helper() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
        //  Disconnect category
        fixtureConfig->insertValue("disconnectMode", "DEMAND_THRESHOLD");
        fixtureConfig->insertValue("disconnectDemandThreshold", "4.71");
        fixtureConfig->insertValue("disconnectLoadLimitConnectDelay", "3");
        fixtureConfig->insertValue("disconnectMinutes", "6");
        fixtureConfig->insertValue("connectMinutes", "18");
        fixtureConfig->insertValue("reconnectParam", "IMMEDIATE");

        //  Demand freeze day category
        fixtureConfig->insertValue("demandFreezeDay", "21");

        //  Meter parameters category
        fixtureConfig->insertValue("lcdCycleTime", "3" );
        fixtureConfig->insertValue("disconnectDisplayDisabled", "true" );
        fixtureConfig->insertValue("displayDigits", "5" );

        //  Time zone category
        fixtureConfig->insertValue("timeZoneOffset", "CHICAGO");

        //  Display category
        fixtureConfig->insertValue("displayItem1", Cti::Config::DisplayItemValues::NO_SEGMENTS);    //1
        fixtureConfig->insertValue("displayItem2", Cti::Config::DisplayItemValues::ALL_SEGMENTS);   //2
        fixtureConfig->insertValue("displayItem3", Cti::Config::DisplayItemValues::CURRENT_LOCAL_DATE);
        fixtureConfig->insertValue("displayItem4", Cti::Config::DisplayItemValues::NET_KWH );
        fixtureConfig->insertValue("displayItem5", Cti::Config::DisplayItemValues::RECEIVED_KWH );
        fixtureConfig->insertValue("displayItem6", Cti::Config::DisplayItemValues::PEAK_KW);
        fixtureConfig->insertValue("displayItem7", Cti::Config::DisplayItemValues::PEAK_KW_TIME);
        fixtureConfig->insertValue("displayItem8", Cti::Config::DisplayItemValues::PEAK_VOLTAGE);
        fixtureConfig->insertValue("displayItem9", Cti::Config::DisplayItemValues::PEAK_VOLTAGE_TIME);
        fixtureConfig->insertValue("displayItem10", Cti::Config::DisplayItemValues::MINIMUM_VOLTAGE_DATE);
        fixtureConfig->insertValue("displayItem11", Cti::Config::DisplayItemValues::TOU_RATE_A_KWH);
        fixtureConfig->insertValue("displayItem12", Cti::Config::DisplayItemValues::TOU_RATE_A_DATE_OF_PEAK_KW);
        fixtureConfig->insertValue("displayItem13", Cti::Config::DisplayItemValues::TOU_RATE_B_KWH);
        fixtureConfig->insertValue("displayItem14", Cti::Config::DisplayItemValues::TOU_RATE_B_DATE_OF_PEAK_KW);
        fixtureConfig->insertValue("displayItem15", Cti::Config::DisplayItemValues::TOU_RATE_C_KWH);
        fixtureConfig->insertValue("displayItem16", Cti::Config::DisplayItemValues::TOU_RATE_C_DATE_OF_PEAK_KW);
        fixtureConfig->insertValue("displayItem17", Cti::Config::DisplayItemValues::TOU_RATE_D_KWH);
        fixtureConfig->insertValue("displayItem18", Cti::Config::DisplayItemValues::TOU_RATE_D_DATE_OF_PEAK_KW);
        fixtureConfig->insertValue("displayItem19", Cti::Config::DisplayItemValues::SLOT_DISABLED);
        fixtureConfig->insertValue("displayItem20", Cti::Config::DisplayItemValues::SLOT_DISABLED);
        fixtureConfig->insertValue("displayItem21", Cti::Config::DisplayItemValues::SLOT_DISABLED);
        fixtureConfig->insertValue("displayItem22", Cti::Config::DisplayItemValues::SLOT_DISABLED);
        fixtureConfig->insertValue("displayItem23", Cti::Config::DisplayItemValues::SLOT_DISABLED);
        fixtureConfig->insertValue("displayItem24", Cti::Config::DisplayItemValues::SLOT_DISABLED);
        fixtureConfig->insertValue("displayItem25", Cti::Config::DisplayItemValues::SLOT_DISABLED);
        fixtureConfig->insertValue("displayItem26", Cti::Config::DisplayItemValues::SLOT_DISABLED);
    }
};

BOOST_FIXTURE_TEST_SUITE(test_putconfig_install, putconfigInstall_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_mct420cl_putconfig_install_all)
    {
        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( ClientErrors::None, test_Mct420CL().beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 6 );
        BOOST_REQUIRE_EQUAL( outList.size(), 6 );

        BOOST_CHECK( boost::algorithm::all_of( retList, isSentOnRouteMsg ) );

        auto om_itr = outList.cbegin();

        int writeMsgPriority,
            readMsgPriority;        // Capture message priorities to validate ordering

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf6 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     13 );

            const std::vector<unsigned> expected = { 0x01, 0x02, 0x05, 0x07, 0x09, 0x0b, 0x0d, 0x0f, 0x11, 0x13, 0x15, 0x17, 0x19 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      2 );

            const std::vector<unsigned> expected = { 0xff, 0x83 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );
        }

        // This validates the read-after-write behavior... write message has higher priority

        // Freeze Day messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            const std::vector<unsigned> expected = { 0x15 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );

            writeMsgPriority = om->Priority;
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority
        BOOST_CHECK( writeMsgPriority > readMsgPriority );

        // Timezone message
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x3f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            const std::vector<unsigned> expected = {
                0xe8 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length);
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL(om->Buffer.BSt.IO,          1 );
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Function, 0x3f );
            BOOST_CHECK_EQUAL(om->Buffer.BSt.Length,      1 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK(writeMsgPriority > readMsgPriority);
    }
    BOOST_AUTO_TEST_CASE(test_mct420cd_putconfig_install_all)
    {
        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( ClientErrors::None, test_Mct420CD().beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 8 );
        BOOST_REQUIRE_EQUAL( outList.size(), 8 );

        BOOST_CHECK( boost::algorithm::all_of( retList, isSentOnRouteMsg ) );

        auto om_itr = outList.cbegin();

        int writeMsgPriority,
            readMsgPriority;        // Capture message priorities to validate ordering

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf6 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     13 );

            const std::vector<unsigned> expected = { 0x01, 0x02, 0x05, 0x07, 0x09, 0x0b, 0x0d, 0x0f, 0x11, 0x13, 0x15, 0x17, 0x19 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );
        }
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      2 );

            const std::vector<unsigned> expected = { 0xff, 0x83 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );
        }

        // This validates the read-after-write behavior... write message has higher priority

        // Disconnect messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      9 );

            const std::vector<unsigned> expected = { 0x00, 0x00, 0x00, 0x3f, 0x55, 0x03, 0x06, 0x12, 0x44 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );

            writeMsgPriority = om->Priority;
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     13 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority
        BOOST_CHECK( writeMsgPriority > readMsgPriority );

        // Freeze Day messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            const std::vector<unsigned> expected = { 0x15 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );

            writeMsgPriority = om->Priority;
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority
        BOOST_CHECK( writeMsgPriority > readMsgPriority );

        // Timezone message
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x3f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            const std::vector<unsigned> expected = {
                0xe8 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length);
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x3f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK(writeMsgPriority > readMsgPriority);
    }
    BOOST_AUTO_TEST_CASE(test_mct420fl_putconfig_install_all)
    {
        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( ClientErrors::None, test_Mct420FL().beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 7 );
        BOOST_REQUIRE_EQUAL( outList.size(), 7 );

        BOOST_CHECK( boost::algorithm::all_of( retList, isSentOnRouteMsg ) );

        auto om_itr = outList.cbegin();

        int writeMsgPriority,
            readMsgPriority;        // Capture message priorities to validate ordering

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      2 );

            const std::vector<unsigned> expected = { 0xff, 0x83 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );
        }

        // This validates the read-after-write behavior... write message has higher priority

        // Disconnect messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      9 );

            const std::vector<unsigned> expected = { 0x00, 0x00, 0x00, 0x3f, 0x55, 0x03, 0x06, 0x12, 0x44 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );

            writeMsgPriority = om->Priority;
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     13 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority
        BOOST_CHECK( writeMsgPriority > readMsgPriority );

        // Freeze Day messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            const std::vector<unsigned> expected = { 0x15 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );

            writeMsgPriority = om->Priority;
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority
        BOOST_CHECK( writeMsgPriority > readMsgPriority );

        // Timezone message
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x3f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            const std::vector<unsigned> expected = {
                0xe8 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length);
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x3f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK(writeMsgPriority > readMsgPriority);
    }
    BOOST_AUTO_TEST_CASE(test_mct420fd_putconfig_install_all)
    {
        CtiCommandParser parse("putconfig install all");

        BOOST_CHECK_EQUAL( ClientErrors::None, test_Mct420FD().beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 7 );
        BOOST_REQUIRE_EQUAL( outList.size(), 7 );

        BOOST_CHECK( boost::algorithm::all_of( retList, isSentOnRouteMsg ) );

        auto om_itr = outList.cbegin();

        int writeMsgPriority,
            readMsgPriority;        // Capture message priorities to validate ordering

        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      2 );

            const std::vector<unsigned> expected = { 0xff, 0x83 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );
        }

        // This validates the read-after-write behavior... write message has higher priority

        // Disconnect messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          2 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      9 );

            const std::vector<unsigned> expected = { 0x00, 0x00, 0x00, 0x3f, 0x55, 0x03, 0x06, 0x12, 0x44 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );

            writeMsgPriority = om->Priority;
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          3 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xfe );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,     13 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority
        BOOST_CHECK( writeMsgPriority > readMsgPriority );

        // Freeze Day messages - read-after-write.
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            const std::vector<unsigned> expected = { 0x15 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length );

            writeMsgPriority = om->Priority;
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x4f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority
        BOOST_CHECK( writeMsgPriority > readMsgPriority );

        // Timezone message
        // Write message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          0 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x3f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            const std::vector<unsigned> expected = {
                0xe8 };

            BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(),
                expected.end(),
                om->Buffer.BSt.Message,
                om->Buffer.BSt.Message + om->Buffer.BSt.Length);
        }
        // Read message
        {
            const OUTMESS *om = *om_itr++;

            BOOST_REQUIRE(om);

            BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,          1 );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0x3f );
            BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,      1 );

            readMsgPriority = om->Priority;
        }

        // This validates the read-after-write behavior... write message has higher priority

        BOOST_CHECK(writeMsgPriority > readMsgPriority);
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_install_all)
    {
        test_Mct420FD mct420;

        request.setCommandString("getconfig install all");

        CtiCommandParser parse(request.CommandString());

        BOOST_CHECK_EQUAL(ClientErrors::None, mct420.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 4);
        BOOST_REQUIRE_EQUAL(outList.size(), 4);

        auto retList_itr = retList.cbegin();

        BOOST_CHECK( boost::algorithm::all_of( retList, isSentOnRouteMsg ) );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_install_all_mct420cl_expectMore)
    {
        test_Mct420CL mct420;

        constexpr int UserMessageId = 11235;

        request.setCommandString("getconfig install all");
        request.setUserMessageId(UserMessageId);

        CtiCommandParser parse(request.CommandString());

        BOOST_CHECK_EQUAL(ClientErrors::None, mct420.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(4, retList.size());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(4, outList.size());

        BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 4);

        {
            BOOST_CHECK(boost::algorithm::all_of(retList, isSentOnRouteMsg));
            delete_container(retList);
            retList.clear();
        }

        auto outList_itr = outList.cbegin();

        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0xf6);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 13);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0xf6);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 3);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 4);
        }
        BOOST_REQUIRE_EQUAL(5, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0xf3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 2);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0xf3);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 3);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 3);
        }
        BOOST_REQUIRE_EQUAL(5, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0x4f);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 1);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 1);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0x4f);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 1);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 2);
        }
        BOOST_REQUIRE_EQUAL(5, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0x3f);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 1);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 1);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0x3f);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 1);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 1);
        }
        BOOST_REQUIRE_EQUAL(5, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0xf7);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 13);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0xf7);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 3);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 0);
        }

        BOOST_REQUIRE_EQUAL(retList.size(), 5);

        {
            auto retList_itr = retList.cbegin();

            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), "Emetcon DLC command sent on route ");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), 
                    "Test MCT-420CL / Display metric 1: Slot Disabled"
                    "\nDisplay metric 2: Slot Disabled"
                    "\nDisplay metric 3: Slot Disabled"
                    "\nDisplay metric 4: Slot Disabled"
                    "\nDisplay metric 5: Slot Disabled"
                    "\nDisplay metric 6: Slot Disabled"
                    "\nDisplay metric 7: Slot Disabled"
                    "\nDisplay metric 8: Slot Disabled"
                    "\nDisplay metric 9: Slot Disabled"
                    "\nDisplay metric 10: Slot Disabled"
                    "\nDisplay metric 11: Slot Disabled"
                    "\nDisplay metric 12: Slot Disabled"
                    "\nDisplay metric 13: Slot Disabled"
                    "\n");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), "Test MCT-420CL / Scheduled day of freeze: (disabled)\n");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), "Config data received: 00");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), 
                    "Test MCT-420CL / Display metric 14: Slot Disabled"
                    "\nDisplay metric 15: Slot Disabled"
                    "\nDisplay metric 16: Slot Disabled"
                    "\nDisplay metric 17: Slot Disabled"
                    "\nDisplay metric 18: Slot Disabled"
                    "\nDisplay metric 19: Slot Disabled"
                    "\nDisplay metric 20: Slot Disabled"
                    "\nDisplay metric 21: Slot Disabled"
                    "\nDisplay metric 22: Slot Disabled"
                    "\nDisplay metric 23: Slot Disabled"
                    "\nDisplay metric 24: Slot Disabled"
                    "\nDisplay metric 25: Slot Disabled"
                    "\nDisplay metric 26: Slot Disabled"
                    "\n");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), false);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_install_all_mct420cd_expectMore)
    {
        test_Mct420CD mct420;

        constexpr int UserMessageId = 11235;

        request.setCommandString("getconfig install all");
        request.setUserMessageId(UserMessageId);

        CtiCommandParser parse(request.CommandString());

        BOOST_CHECK_EQUAL(ClientErrors::None, mct420.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(5, retList.size());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(5, outList.size());

        BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 5);

        {
            BOOST_CHECK(boost::algorithm::all_of(retList, isSentOnRouteMsg));
            delete_container(retList);
            retList.clear();
        }

        auto outList_itr = outList.cbegin();
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0xf6);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 13);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0xf6);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 3);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 5);
        }
        BOOST_REQUIRE_EQUAL(6, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0xf3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 2);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0xf3);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 3);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 4);
        }
        BOOST_REQUIRE_EQUAL(6, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0xfe);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 13);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0xfe);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 3);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 3);
        }
        BOOST_REQUIRE_EQUAL(6, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0x4f);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 1);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 1);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0x4f);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 1);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 2);
        }
        BOOST_REQUIRE_EQUAL(6, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0x3f);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 1);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 1);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0x3f);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 1);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 1);
        }
        BOOST_REQUIRE_EQUAL(6, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0xf7);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 13);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0xf7);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 3);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 0);
        }

        BOOST_REQUIRE_EQUAL(retList.size(), 6);

        {
            auto retList_itr = retList.cbegin();

            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), "Emetcon DLC command sent on route ");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(),
                    "Test MCT-420CD / Display metric 1: Slot Disabled"
                    "\nDisplay metric 2: Slot Disabled"
                    "\nDisplay metric 3: Slot Disabled"
                    "\nDisplay metric 4: Slot Disabled"
                    "\nDisplay metric 5: Slot Disabled"
                    "\nDisplay metric 6: Slot Disabled"
                    "\nDisplay metric 7: Slot Disabled"
                    "\nDisplay metric 8: Slot Disabled"
                    "\nDisplay metric 9: Slot Disabled"
                    "\nDisplay metric 10: Slot Disabled"
                    "\nDisplay metric 11: Slot Disabled"
                    "\nDisplay metric 12: Slot Disabled"
                    "\nDisplay metric 13: Slot Disabled"
                    "\n");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), "Test MCT-420CD / "
                    "\nConfig data received: 00 00 00 00 00 00 00 00 00 00 00 00 00");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->Status(), 0);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), "Test MCT-420CD / Scheduled day of freeze: (disabled)\n");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), "Config data received: 00");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(),
                    "Test MCT-420CD / Display metric 14: Slot Disabled"
                    "\nDisplay metric 15: Slot Disabled"
                    "\nDisplay metric 16: Slot Disabled"
                    "\nDisplay metric 17: Slot Disabled"
                    "\nDisplay metric 18: Slot Disabled"
                    "\nDisplay metric 19: Slot Disabled"
                    "\nDisplay metric 20: Slot Disabled"
                    "\nDisplay metric 21: Slot Disabled"
                    "\nDisplay metric 22: Slot Disabled"
                    "\nDisplay metric 23: Slot Disabled"
                    "\nDisplay metric 24: Slot Disabled"
                    "\nDisplay metric 25: Slot Disabled"
                    "\nDisplay metric 26: Slot Disabled"
                    "\n");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), false);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_install_all_mct420fl_expectMore)
    {
        test_Mct420FL mct420;

        constexpr int UserMessageId = 11235;

        request.setCommandString("getconfig install all");
        request.setUserMessageId(UserMessageId);

        CtiCommandParser parse(request.CommandString());

        BOOST_CHECK_EQUAL(ClientErrors::None, mct420.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(4, retList.size());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(4, outList.size());

        BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 4);

        {
            BOOST_CHECK(boost::algorithm::all_of(retList, isSentOnRouteMsg));
            delete_container(retList);
            retList.clear();
        }

        auto outList_itr = outList.cbegin();
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0xf3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 2);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0xf3);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 3);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 3);
        }
        BOOST_CHECK_EQUAL(4, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0xfe);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 13);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0xfe);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 3);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 2);
        }
        BOOST_CHECK_EQUAL(4, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0x4f);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 1);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 1);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0x4f);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 1);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 1);
        }
        BOOST_CHECK_EQUAL(4, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0x3f);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 1);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 1);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0x3f);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 1);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 0);
        }
        BOOST_REQUIRE_EQUAL(4, outList.size());

        BOOST_REQUIRE_EQUAL(retList.size(), 3);
        {
            auto retList_itr = retList.cbegin();

            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), 
                    "Test MCT-420FL / "
                    "\nConfig data received: 00 00 00 00 00 00 00 00 00 00 00 00 00");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), "Test MCT-420FL / Scheduled day of freeze: (disabled)\n");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), "Config data received: 00");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), false);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_install_all_mct420fd_expectMore)
    {
        test_Mct420FD mct420;

        constexpr int UserMessageId = 11235;

        request.setCommandString("getconfig install all");
        request.setUserMessageId(UserMessageId);

        CtiCommandParser parse(request.CommandString());

        BOOST_CHECK_EQUAL(ClientErrors::None, mct420.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(4, retList.size());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(4, outList.size());

        BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 4);

        {
            BOOST_CHECK(boost::algorithm::all_of(retList, isSentOnRouteMsg));
            delete_container(retList);
            retList.clear();
        }

        auto outList_itr = outList.cbegin();

        BOOST_CHECK_EQUAL(4, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0xf3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 2);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0xf3);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 3);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 3);
        }
        BOOST_CHECK_EQUAL(4, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0xfe);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 3);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 13);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0xfe);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 3);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 2);
        }
        BOOST_CHECK_EQUAL(4, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0x4f);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 1);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 1);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0x4f);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 1);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 1);
        }
        BOOST_CHECK_EQUAL(4, outList.size());
        {
            auto outmess = *outList_itr++;

            BOOST_REQUIRE(outmess);

            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Function, 0x3f);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.IO, 1);
            BOOST_CHECK_EQUAL(outmess->Buffer.BSt.Length, 1);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.Function, 0x3f);
            BOOST_CHECK_EQUAL(outmess->Request.ProtocolInfo.Emetcon.IO, 1);

            INMESS im = makeInmessReply(*outmess);

            mct420.ProcessInMessageResult(im, CtiTime::now(), vgList, retList, outList);

            BOOST_CHECK_EQUAL(mct420.getGroupMessageCount(UserMessageId, connHandle), 0);
        }
        BOOST_REQUIRE_EQUAL(4, outList.size());

        BOOST_REQUIRE_EQUAL(retList.size(), 3);
        {
            auto retList_itr = retList.cbegin();

            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), "Test MCT-420FD / "
                    "\nConfig data received: 00 00 00 00 00 00 00 00 00 00 00 00 00");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), "Test MCT-420FD / Scheduled day of freeze: (disabled)\n");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), true);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
            {
                auto retMsg = dynamic_cast<CtiReturnMsg*>(*retList_itr++);

                BOOST_REQUIRE(retMsg);

                BOOST_CHECK_EQUAL(retMsg->ResultString(), "Config data received: 00");
                BOOST_CHECK_EQUAL(retMsg->ExpectMore(), false);
                BOOST_CHECK_EQUAL(retMsg->UserMessageId(), UserMessageId);
            }
        }
    }
    
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


BOOST_AUTO_TEST_CASE(test_dev_mct420_getUsageReportDelay)
{
    const test_Mct420CL mct;

    //  Calculation is 10s + max(36, days) * intervals/day * 10ms, rounded up to the next second

    //  36 * 24 * 12 * 0.01 = 103.68s
    BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,   1), 114);

    //  36 * 24 * 12 * 0.01 = 103.68s
    BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,  30), 114);

    //  60 * 24 * 12 * 0.01 = 172.80s
    BOOST_CHECK_EQUAL(mct.getUsageReportDelay(300,  60), 183);

    //  36 * 24 *  6 * 0.01 = 51.84s
    BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,   1),  62);

    //  36 * 24 *  6 * 0.01 = 51.84s
    BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,  30),  62);

    //  60 * 24 *  6 * 0.01 = 86.40s
    BOOST_CHECK_EQUAL(mct.getUsageReportDelay(600,  60),  97);

    //  36 * 24 *  1 * 0.01 = 8.64s
    BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600,  1),  19);

    //  36 * 24 *  1 * 0.01 = 8.64s
    BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600, 30),  19);

    //  60 * 24 *  1 * 0.01 = 14.40s
    BOOST_CHECK_EQUAL(mct.getUsageReportDelay(3600, 60),  25);
}


BOOST_AUTO_TEST_CASE(test_dev_mct420_extractDynamicPaoInfo)
{
    test_Mct420CL dev;

    INMESS im;

    im.Buffer.DSt.Message[ 0] = 0xff;
    im.Buffer.DSt.Message[ 1] = 0xfe;
    im.Buffer.DSt.Message[ 2] = 0xfd;
    im.Buffer.DSt.Message[ 3] = 0xfc;
    im.Buffer.DSt.Message[ 4] = 0xfb;
    im.Buffer.DSt.Message[ 5] = 0xfa;
    im.Buffer.DSt.Message[ 5] = 0xf9;
    im.Buffer.DSt.Message[ 6] = 0xf8;
    im.Buffer.DSt.Message[ 7] = 0xf7;
    im.Buffer.DSt.Message[ 8] = 0xf6;
    im.Buffer.DSt.Message[ 9] = 0xf5;
    im.Buffer.DSt.Message[10] = 0xf4;
    im.Buffer.DSt.Message[11] = 0xf3;
    im.Buffer.DSt.Message[12] = 0xf2;

    im.Buffer.DSt.Length = 13;
    im.Return.ProtocolInfo.Emetcon.Function = 0;
    im.Return.ProtocolInfo.Emetcon.IO = EmetconProtocol::IO_Read;

    dev.extractDynamicPaoInfo(im);

    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec));
    BOOST_CHECK(dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision));

    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec),           0xfefd);
    BOOST_CHECK_EQUAL(dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision),   0xff);
}


struct getOperation_helper
{
    test_Mct420CL mct;

    BSTRUCT BSt;
};

BOOST_FIXTURE_TEST_SUITE(test_getOperation, getOperation_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_getOperation_01)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Command_Loop, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_02)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Model, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_03)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Install, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_04)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_GroupAddressEnable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x54);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_05)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_GroupAddressInhibit, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x53);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_06)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Raw, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_07)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Shed, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_08)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Restore, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    //  Overridden by MCT-410
    /*
    BOOST_AUTO_TEST_CASE(test_getOperation_09)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Connect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x20);  //  Q_ARML
        BOOST_CHECK_EQUAL(BSt.Function, 0x42);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_10)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x20);  //  Q_ARML
        BOOST_CHECK_EQUAL(BSt.Function, 0x41);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    */
    BOOST_AUTO_TEST_CASE(test_getOperation_11)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_ARMC, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x62);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_12)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_ARML, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x60);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    //  Overridden by MCT-410
    /*
    BOOST_AUTO_TEST_CASE(test_getOperation_13)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x49);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    */
    //  MCT-4xx commands
    BOOST_AUTO_TEST_CASE(test_getOperation_14)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_TOUPeak, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xb0);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_15)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_TOUReset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x5f);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_16)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_ResetPFCount, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x89);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_17)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf0);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_18)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TOUEnable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x56);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_19)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TOUDisable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x55);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_20)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_LoadProfileInterest, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x05);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    //  MCT-410 commands
    BOOST_AUTO_TEST_CASE(test_getOperation_21)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_Accum, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x90);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_22)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_KWH, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x90);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_23)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenKWH, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x91);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_24)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_TOUkWh, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe0);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_25)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenTOUkWh, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe1);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_26)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_Integrity, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_27)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_28)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_29)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_LoadProfilePeakReport, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_30)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Demand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_31)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_PeakDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x93);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_32)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenPeakDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x94);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_33)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Voltage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x95);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_34)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenVoltage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x96);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_35)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Outage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_36)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Internal, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x05);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_37)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x97);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_38)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Freeze, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x26);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_39)
    {
        {
            BOOST_CHECK( ! test_Mct420CL().getOperation(EmetconProtocol::Control_Connect, BSt));
        }
        {
            BOOST_CHECK(test_Mct420CD().getOperation(EmetconProtocol::Control_Connect, BSt));
            BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
            BOOST_CHECK_EQUAL(BSt.Function, 0x4c);
            BOOST_CHECK_EQUAL(BSt.Length,   0);
        }
        {
            BOOST_CHECK(test_Mct420FL().getOperation(EmetconProtocol::Control_Connect, BSt));
            BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
            BOOST_CHECK_EQUAL(BSt.Function, 0x4c);
            BOOST_CHECK_EQUAL(BSt.Length,   0);
        }
        {
            BOOST_CHECK(test_Mct420FD().getOperation(EmetconProtocol::Control_Connect, BSt));
            BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
            BOOST_CHECK_EQUAL(BSt.Function, 0x4c);
            BOOST_CHECK_EQUAL(BSt.Length,   0);
        }
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_40)
    {
        {
            BOOST_CHECK( ! test_Mct420CL().getOperation(EmetconProtocol::Control_Disconnect, BSt));
        }
        {
            BOOST_CHECK(test_Mct420CD().getOperation(EmetconProtocol::Control_Disconnect, BSt));
            BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
            BOOST_CHECK_EQUAL(BSt.Function, 0x4d);
            BOOST_CHECK_EQUAL(BSt.Length,   0);
        }
        {
            BOOST_CHECK(test_Mct420FL().getOperation(EmetconProtocol::Control_Disconnect, BSt));
            BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
            BOOST_CHECK_EQUAL(BSt.Function, 0x4d);
            BOOST_CHECK_EQUAL(BSt.Length,   0);
        }
        {
            BOOST_CHECK(test_Mct420FD().getOperation(EmetconProtocol::Control_Disconnect, BSt));
            BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
            BOOST_CHECK_EQUAL(BSt.Function, 0x4d);
            BOOST_CHECK_EQUAL(BSt.Length,   0);
        }
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_41)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_42)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(BSt.Length,   11);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_43)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_44)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Raw, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_45)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x44);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_46)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Time, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x3f);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_47)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_FreezeDay, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_48)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TimeZoneOffset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x3f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_49)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Intervals, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x03);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_50)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Intervals, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1a);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_51)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_OutageThreshold, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x22);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_52)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Thresholds, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_53)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_DailyReadInterest, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL(BSt.Length,   11);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_54)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_PFCount, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x23);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_55)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_Reset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x8a);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_56)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_ResetAlarms, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x08);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_57)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeOne, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x51);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_58)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeTwo, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x52);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_59)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeVoltageOne, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x59);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_60)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeVoltageTwo, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x5a);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_61)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_UniqueAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf1);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_62)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_UniqueAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   3);
    }
    //  Overridden by MCT-420
    /*
    BOOST_AUTO_TEST_CASE(test_getOperation_63)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Multiplier, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x19);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_64)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_MeterParameters, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x0f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    */
    BOOST_AUTO_TEST_CASE(test_getOperation_65)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Freeze, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_66)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_LongLoadProfileStorage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x04);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_67)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_LongLoadProfileStorage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x9d);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_68)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_VThreshold, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_69)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Holiday, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd0);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_70)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Holiday, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd0);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_71)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Options, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x01);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_72)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_AutoReconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x01);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_73)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Outage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x22);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_74)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TimeAdjustTolerance, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x36);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_75)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_PhaseDetect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x0f);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_76)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_PhaseDetect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_77)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_PhaseDetectClear, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    //  MCT-420 commands
    BOOST_AUTO_TEST_CASE(test_getOperation_78)
    {
        BOOST_REQUIRE(mct.getOperation(Cti::Protocols::EmetconProtocol::GetConfig_Multiplier, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf3);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_79)
    {
        BOOST_REQUIRE(mct.getOperation(Cti::Protocols::EmetconProtocol::GetConfig_MeterParameters, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf3);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_80)
    {
        BOOST_REQUIRE(mct.getOperation(Cti::Protocols::EmetconProtocol::GetConfig_Options, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x01);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_81)
    {
        BOOST_REQUIRE(mct.getOperation(Cti::Protocols::EmetconProtocol::PutConfig_Channel2NetMetering, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x85);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_1Dword)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct420Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  memory read 0
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_SSpecRevision)(1,2,(int)Dpi::Key_MCT_SSpec))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 10
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DisplayParameters))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressBronze)(1,2,(int)Dpi::Key_MCT_AddressLead))
        // memory read 20
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TransformerRatio))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DemandInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval)(2,1,(int)Dpi::Key_MCT_VoltageDemandInterval))
        (empty)
        (empty)
        (empty)
        // memory read 30
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(2,2,(int)Dpi::Key_MCT_UnderVoltageThreshold))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_OutageCycles))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 40
        .repeat(10, empty)
        // memory read 50
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeAdjustTolerance))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 60
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 70
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_ScheduledFreezeDay))
        // memory read 80
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 90
        .repeat(110, empty)
        // memory read 200
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday1))
        (empty)
        // memory read 210
        (empty)
        (empty)
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday2))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday3))
        (empty)
        (empty)
        (empty)
        // memory read 220
        .repeat(36, empty);

    const test_Mct420CL dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 3));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_2Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct420Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  memory read 0
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_SSpecRevision)(1,2,(int)Dpi::Key_MCT_SSpec))
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_EventFlagsMask1)(6,1,(int)Dpi::Key_MCT_EventFlagsMask2)(7,2,(int)Dpi::Key_MCT_MeterAlarmMask))
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 10
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DisplayParameters))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressBronze)(1,2,(int)Dpi::Key_MCT_AddressLead)(3,2,(int)Dpi::Key_MCT_AddressCollection)(5,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        // memory read 20
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TransformerRatio))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DemandInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval)(2,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(3,1,(int)Dpi::Key_MCT_VoltageLPInterval))
        (empty)
        (empty)
        (empty)
        // memory read 30
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(2,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(4,1,(int)Dpi::Key_MCT_OutageCycles))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_OutageCycles))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 40
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 50
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeAdjustTolerance))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 60
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 70
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_ScheduledFreezeDay))
        // memory read 80
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 90
        .repeat(110, empty)
        // memory read 200
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday1)(4,4,(int)Dpi::Key_MCT_Holiday2))
        (empty)
        // memory read 210
        (empty)
        (empty)
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday2)(4,4,(int)Dpi::Key_MCT_Holiday3))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday3))
        (empty)
        (empty)
        (empty)
        // memory read 220
        .repeat(36, empty);

    const test_Mct420CL dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 8));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_3Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct420Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  memory read 0
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_SSpecRevision)(1,2,(int)Dpi::Key_MCT_SSpec))
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(5,1,(int)Dpi::Key_MCT_EventFlagsMask1)(6,1,(int)Dpi::Key_MCT_EventFlagsMask2)(7,2,(int)Dpi::Key_MCT_MeterAlarmMask))
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 10
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DisplayParameters))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_AddressBronze)(1,2,(int)Dpi::Key_MCT_AddressLead)(3,2,(int)Dpi::Key_MCT_AddressCollection)(5,1,(int)Dpi::Key_MCT_AddressServiceProviderID))
        // memory read 20
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TransformerRatio))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DemandInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval)(2,1,(int)Dpi::Key_MCT_VoltageDemandInterval)(3,1,(int)Dpi::Key_MCT_VoltageLPInterval))
        (empty)
        (empty)
        (empty)
        // memory read 30
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_OverVoltageThreshold)(2,2,(int)Dpi::Key_MCT_UnderVoltageThreshold)(4,1,(int)Dpi::Key_MCT_OutageCycles))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_OutageCycles))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 40
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 50
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeAdjustTolerance))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 60
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 70
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_ScheduledFreezeDay))
        // memory read 80
        .repeat(100, empty)
        // memory read 180
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 190
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 200
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday1)(4,4,(int)Dpi::Key_MCT_Holiday2)(8,4,(int)Dpi::Key_MCT_Holiday3))
        (empty)
        // memory read 210
        (empty)
        (empty)
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday2)(4,4,(int)Dpi::Key_MCT_Holiday3))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,4,(int)Dpi::Key_MCT_Holiday3))
        (empty)
        (empty)
        (empty)
        // memory read 220
        .repeat(36, empty);

    const test_Mct420CL dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 13));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_1Dword)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct420Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Configuration)(1,1,(int)Dpi::Key_MCT_EventFlagsMask1)(2,1,(int)Dpi::Key_MCT_EventFlagsMask2))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 10
        .repeat(160, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(2,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 180
        .repeat(60, empty)
        //  function read 240
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DisplayParameters)(1,1,(int)Dpi::Key_MCT_TransformerRatio))
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LcdMetric01)(1,1,(int)Dpi::Key_MCT_LcdMetric02)(2,1,(int)Dpi::Key_MCT_LcdMetric03))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LcdMetric14)(1,1,(int)Dpi::Key_MCT_LcdMetric15)(2,1,(int)Dpi::Key_MCT_LcdMetric16))
        (empty)
        (empty)
        //  function read 250
        .repeat(6, empty);

    const test_Mct420CL dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 3));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_2Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct420Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Configuration)(1,1,(int)Dpi::Key_MCT_EventFlagsMask1)(2,1,(int)Dpi::Key_MCT_EventFlagsMask2)(3,2,(int)Dpi::Key_MCT_MeterAlarmMask)(5,1,(int)Dpi::Key_MCT_Options))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 10
        .repeat(140, empty)
        //  function read 150
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_LLPChannel1Len)(5,1,(int)Dpi::Key_MCT_LLPChannel2Len)(6,1,(int)Dpi::Key_MCT_LLPChannel3Len)(7,1,(int)Dpi::Key_MCT_LLPChannel4Len))
        (empty)
        (empty)
        //  function read 160
        .repeat(10, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(2,1,(int)Dpi::Key_MCT_DefaultTOURate))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 180
        .repeat(60, empty)
        //  function read 240
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DisplayParameters)(1,1,(int)Dpi::Key_MCT_TransformerRatio))
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LcdMetric01)(1,1,(int)Dpi::Key_MCT_LcdMetric02)(2,1,(int)Dpi::Key_MCT_LcdMetric03)(3,1,(int)Dpi::Key_MCT_LcdMetric04)(4,1,(int)Dpi::Key_MCT_LcdMetric05)(5,1,(int)Dpi::Key_MCT_LcdMetric06)(6,1,(int)Dpi::Key_MCT_LcdMetric07)(7,1,(int)Dpi::Key_MCT_LcdMetric08))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LcdMetric14)(1,1,(int)Dpi::Key_MCT_LcdMetric15)(2,1,(int)Dpi::Key_MCT_LcdMetric16)(3,1,(int)Dpi::Key_MCT_LcdMetric17)(4,1,(int)Dpi::Key_MCT_LcdMetric18)(5,1,(int)Dpi::Key_MCT_LcdMetric19)(6,1,(int)Dpi::Key_MCT_LcdMetric20)(7,1,(int)Dpi::Key_MCT_LcdMetric21))
        (empty)
        (empty)
        //  function read 250
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_ConnectDelay))
        (empty);

    const test_Mct420CL dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 8));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_3Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct420Device::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_Configuration)(1,1,(int)Dpi::Key_MCT_EventFlagsMask1)(2,1,(int)Dpi::Key_MCT_EventFlagsMask2)(3,2,(int)Dpi::Key_MCT_MeterAlarmMask)(5,1,(int)Dpi::Key_MCT_Options))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 10
        .repeat(140, empty)
        //  function read 150
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(4,1,(int)Dpi::Key_MCT_LLPChannel1Len)(5,1,(int)Dpi::Key_MCT_LLPChannel2Len)(6,1,(int)Dpi::Key_MCT_LLPChannel3Len)(7,1,(int)Dpi::Key_MCT_LLPChannel4Len))
        (empty)
        (empty)
        //  function read 160
        .repeat(10, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(2,1,(int)Dpi::Key_MCT_DefaultTOURate)(10,1,(int)Dpi::Key_MCT_TimeZoneOffset))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 180
        .repeat(60, empty)
        //  function read 240
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DisplayParameters)(1,1,(int)Dpi::Key_MCT_TransformerRatio))
        (empty)
        (empty)
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LcdMetric01)(1,1,(int)Dpi::Key_MCT_LcdMetric02)(2,1,(int)Dpi::Key_MCT_LcdMetric03)(3,1,(int)Dpi::Key_MCT_LcdMetric04)(4,1,(int)Dpi::Key_MCT_LcdMetric05)(5,1,(int)Dpi::Key_MCT_LcdMetric06)(6,1,(int)Dpi::Key_MCT_LcdMetric07)(7,1,(int)Dpi::Key_MCT_LcdMetric08)(8,1,(int)Dpi::Key_MCT_LcdMetric09)(9,1,(int)Dpi::Key_MCT_LcdMetric10)(10,1,(int)Dpi::Key_MCT_LcdMetric11)(11,1,(int)Dpi::Key_MCT_LcdMetric12)(12,1,(int)Dpi::Key_MCT_LcdMetric13))
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_LcdMetric14)(1,1,(int)Dpi::Key_MCT_LcdMetric15)(2,1,(int)Dpi::Key_MCT_LcdMetric16)(3,1,(int)Dpi::Key_MCT_LcdMetric17)(4,1,(int)Dpi::Key_MCT_LcdMetric18)(5,1,(int)Dpi::Key_MCT_LcdMetric19)(6,1,(int)Dpi::Key_MCT_LcdMetric20)(7,1,(int)Dpi::Key_MCT_LcdMetric21)(8,1,(int)Dpi::Key_MCT_LcdMetric22)(9,1,(int)Dpi::Key_MCT_LcdMetric23)(10,1,(int)Dpi::Key_MCT_LcdMetric24)(11,1,(int)Dpi::Key_MCT_LcdMetric25)(12,1,(int)Dpi::Key_MCT_LcdMetric26))
        (empty)
        (empty)
        //  function read 250
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(7,1,(int)Dpi::Key_MCT_ConnectDelay)(9,1,(int)Dpi::Key_MCT_DisconnectMinutes)(10,1,(int)Dpi::Key_MCT_ConnectMinutes)(11,1,(int)Dpi::Key_MCT_Configuration))
        (empty);

    const test_Mct420CL dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 13));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_SUITE_END()
