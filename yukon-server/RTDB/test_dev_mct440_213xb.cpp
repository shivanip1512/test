#include <boost/test/unit_test.hpp>

#include "dev_mct440_213xb.h"
#include "devicetypes.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "utility.h"  //  for delete_container
#include "dev_ccu.h"
#include "rte_ccu.h"
#include "connection_client.h"

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

#include <boost/assign/list_of.hpp>
#include <boost/range/algorithm/transform.hpp>

using namespace Cti::Protocols;
using namespace Cti::Config;

using std::string;
using std::vector;
typedef CtiTableDynamicPaoInfo Dpi;


struct test_CtiDeviceCCU : CtiDeviceCCU
{
    test_CtiDeviceCCU()
    {
        _paObjectID = 12345;
    }
};

struct test_CtiRouteCCU : CtiRouteCCU
{
    CtiDeviceSPtr ccu;

    test_CtiRouteCCU() : ccu(new test_CtiDeviceCCU)
    {
        _tblPAO.setID(1234, test_tag);
        setDevicePointer(ccu);
    }
};

struct test_Mct440_213xBDevice : Cti::Devices::Mct440_213xBDevice
{
    test_Mct440_213xBDevice(/*DeviceTypes type,*/ const string &name)
    {
        // setDeviceType(type);
        _name = name;
        _paObjectID = 123456;
    }

    using CtiTblPAOLite::_type;

    using MctDevice::getOperation;
    using MctDevice::ReadDescriptor;
    using MctDevice::value_locator;
    using MctDevice::getDescriptorForRead;
    using MctDevice::updateFreezeInfo;
    using MctDevice::ResultDecode;

    using Mct4xxDevice::getUsageReportDelay;

    using Mct440_213xBDevice::executeGetValue;
    using Mct440_213xBDevice::decodeGetValueDailyRead;
    using Mct440_213xBDevice::decodeGetValueOutage;
    using Mct440_213xBDevice::decodeDisconnectConfig;
    using Mct440_213xBDevice::decodeDisconnectStatus;
    using Mct440_213xBDevice::isProfileTablePointerCurrent;
    using Mct440_213xBDevice::decodeGetValueKWH;
    using Mct440_213xBDevice::decodeGetValueTOUkWh;
    using Mct440_213xBDevice::decodeGetValueDailyReadRecent;
    using Mct440_213xBDevice::executePutConfigTOU;
    using Mct440_213xBDevice::executePutConfig;
    using Mct440_213xBDevice::executeGetConfig;
    using Mct440_213xBDevice::decodeGetConfigTOU;
    using Mct440_213xBDevice::executePutStatus;
    using Mct440_213xBDevice::decodeGetStatusDisconnect;
    using Mct440_213xBDevice::executeGetStatus;
    using Mct440_213xBDevice::decodeGetStatusEventLog;

    bool test_isSupported_Mct410Feature_OutageUnits()
    {  return isSupported(Feature_OutageUnits);  }

    bool test_isSupported_Mct410Feature_DisconnectCollar()
    {  return isSupported(Feature_DisconnectCollar);  }

    bool test_isSupported_Mct410Feature_HourlyKwh()
    {  return isSupported(Feature_HourlyKwh);  }

    Cti::Test::DevicePointHelper pointHelper;

    CtiPointSPtr getDevicePointOffsetTypeEqual(int offset, CtiPointType_t type) override
    {
        return pointHelper.getCachedPoint(offset, type);
    }

    virtual int getPhaseCount()
    {
        return 0;
    }

    std::string resolveStateName(long groupId, long rawValue) const override
    {
        static const std::array<const char *, 10> stateNames{
            "False", "True", "State Two", "State Three", "State Four", "State Five", "State Six", "State Seven", "State Eight", "State Nine"
        };

        if( rawValue >= 0 && rawValue < stateNames.size() )
        {
            return stateNames[rawValue];
        }

        return "State " + std::to_string(rawValue);
    }
};

struct test_Mct440_213xB : test_Mct440_213xBDevice
{
    test_Mct440_213xB() : test_Mct440_213xBDevice("Test MCT-440-213xB")  {}
};

//
// Test device with a route
//
struct test_Mct440_213xB_route : test_Mct440_213xBDevice
{
    CtiRouteSPtr rte;

    test_Mct440_213xB_route() : test_Mct440_213xBDevice("Test MCT-440-213xB"), rte(new test_CtiRouteCCU)
    {}

    virtual CtiRouteSPtr getRoute(LONG rteId) const
    {
        return rte;
    }
};

namespace std {
    //  defined in rtdb/test_main.cpp
    ostream& operator<<(ostream& out, const unsigned char &uc);
    ostream& operator<<(ostream& out, const test_Mct440_213xBDevice::ReadDescriptor &rd);
    ostream& operator<<(ostream& out, const vector<boost::tuples::tuple<unsigned, unsigned, int>> &rd);
    bool operator==(const test_Mct440_213xBDevice::value_locator &lhs, const boost::tuples::tuple<unsigned, unsigned, int> &rhs);
    bool operator==(const boost::tuples::tuple<unsigned, unsigned, int> &lhs, const test_Mct440_213xBDevice::value_locator &rhs);
}

namespace boost {
namespace test_tools {
    //  defined in rtdb/test_main.cpp
    bool operator!=(const test_Mct440_213xBDevice::ReadDescriptor &lhs, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rhs);
    bool operator!=(const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rhs, const test_Mct440_213xBDevice::ReadDescriptor &lhs);
}
}

struct resetGlobals_helper
{
    Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;
};

BOOST_FIXTURE_TEST_SUITE( test_dev_mct440_213xb, resetGlobals_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE


BOOST_AUTO_TEST_CASE(test_isSupported_DisconnectCollar)
{
    BOOST_CHECK_EQUAL(false, test_Mct440_213xB().test_isSupported_Mct410Feature_DisconnectCollar());
}

BOOST_AUTO_TEST_CASE(test_isSupported_OutageUnits)
{
    BOOST_CHECK_EQUAL(true,  test_Mct440_213xB().test_isSupported_Mct410Feature_OutageUnits());
}

BOOST_AUTO_TEST_CASE(test_isSupported_HourlyKwh)
{
    BOOST_CHECK_EQUAL(false, test_Mct440_213xB().test_isSupported_Mct410Feature_HourlyKwh());
}

BOOST_AUTO_TEST_CASE(test_decodeDisconnectConfig)
{
    struct test_case
    {
        const int config_byte;
        const string expected;
    }
    test_cases[] =
    {
        {0x00, "Disconnect load limit connect delay: 34 minutes\n"
               "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
               "Disconnect demand threshold disabled\n"
               "Disconnect cycling mode disabled\n"},
        {0x04, "Disconnect load limit connect delay: 34 minutes\n"
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

            test_Mct440_213xBDevice test_dev("No name");

            test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 40);
            test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, tc.config_byte);

            results.push_back(test_dev.decodeDisconnectConfig(DSt));
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(),  results.end());
}


BOOST_AUTO_TEST_CASE(test_dev_mct440_213xb_decodeDisconnectStatus)
{
    struct test_case
    {
        const unsigned char dst_message_0;
        const string expected;
    }
    test_cases[] =
    {
        {0x23,  "Disconnect state uncertain\n"
                "Control output status open\n"
                "Disconnect sensor open\n"},

        {0x43,  "Load side voltage detected\n"
                "Control output status open\n"
                "Disconnect sensor open\n"
                "Disconnect locked open\n"},

        {0x0C,  "Control output status closed\n"
                "Disconnect sensor closed\n"
                "Disconnect not locked\n"},
    };

    std::vector<std::string> expected, results;

    for each( const test_case &tc in test_cases )
    {
        expected.push_back(tc.expected);

        {
            test_Mct440_213xB test_dev;

            DSTRUCT DSt;

            DSt.Message[0] = tc.dst_message_0;

            results.push_back(test_dev.decodeDisconnectStatus(DSt));
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(),  results.end());
}


BOOST_AUTO_TEST_CASE(test_decodePulseAccumulator)
{
    unsigned char kwh_read[3] = { 0x00, 0x02, 0x00 };

    Cti::Devices::Mct4xxDevice::frozen_point_info pi;

    pi = Cti::Devices::Mct440_213xBDevice::decodePulseAccumulator(kwh_read, 3, 0);

    BOOST_CHECK_EQUAL( pi.value,      512 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );

    kwh_read[2] = 0x01;

    pi = Cti::Devices::Mct440_213xBDevice::decodePulseAccumulator(kwh_read, 3, 0);

    BOOST_CHECK_EQUAL( pi.value,      513 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );
}


BOOST_AUTO_TEST_CASE(test_isProfileTablePointerCurrent)
{
    test_Mct440_213xB test_dev;

    {
        CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(254, t, 3600));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(255, t, 3600));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(  0, t, 3600));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(254, t, 1800));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(255, t, 1800));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(  0, t, 1800));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(254, t,  900));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(255, t,  900));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(  0, t,  900));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(255, t,  600));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(  0, t,  600));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(  1, t,  600));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(  0, t,  300));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(  1, t,  300));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(  2, t,  300));
    }

    {
        CtiTime t(CtiDate(1, 1, 2011), 1, 16, 0);  //  1293866160 seconds (0x4D1ED4B0)

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(251, t, 3600));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(252, t, 3600));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(253, t, 3600));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(248, t, 1800));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(249, t, 1800));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(250, t, 1800));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(242, t,  900));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(243, t,  900));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(244, t,  900));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(237, t,  600));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(238, t,  600));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(249, t,  600));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(220, t,  300));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(221, t,  300));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(222, t,  300));
    }

    {
        CtiTime t(CtiDate(1, 1, 2011), 9, 16, 0);  //  1293894960 seconds (0x4D1F4530)

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(252, t, 3600));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(253, t, 3600));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(254, t, 3600));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(251, t, 1800));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(252, t, 1800));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(253, t, 1800));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(248, t,  900));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(249, t,  900));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(250, t,  900));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(245, t,  600));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(246, t,  600));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(247, t,  600));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(236, t,  300));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(237, t,  300));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(238, t,  300));
    }

    {
        CtiTime t(CtiDate(1, 1, 2011), 12, 26, 0);  //  1293906360 seconds (0x4D1F71B8)

        long tz;
        _get_timezone(&tz);
        t.addSeconds(21600 - tz);

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(253, t, 3600));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(254, t, 3600));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(255, t, 3600));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(252, t, 1800));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(253, t, 1800));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(254, t, 1800));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(250, t,  900));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(251, t,  900));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(252, t,  900));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(248, t,  600));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(249, t,  600));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(250, t,  600));

        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(242, t,  300));
        BOOST_CHECK_EQUAL(true,  test_dev.isProfileTablePointerCurrent(243, t,  300));
        BOOST_CHECK_EQUAL(false, test_dev.isProfileTablePointerCurrent(244, t,  300));
    }
}


struct beginExecuteRequest_helper
{
    Cti::Test::Override_DynamicPaoInfoManager overrideDpi;
    CtiRequestMsg           request;
    std::list<CtiMessage*>  vgList, retList;
    std::list<OUTMESS*>     outList;

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

        BOOST_CHECK_EQUAL( ClientErrors::NoMethod, test_Mct440_213xB().beginExecuteRequest(&request, parse, vgList, retList, outList) );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_centron_ratio)
    {
        CtiCommandParser parse("getconfig centron ratio");

        BOOST_CHECK_EQUAL( ClientErrors::NoMethod, test_Mct440_213xB().beginExecuteRequest(&request, parse, vgList, retList, outList) );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_meter_parameters)
    {
        CtiCommandParser parse("getconfig meter parameters");

        BOOST_CHECK_EQUAL( ClientErrors::None, test_Mct440_213xB().beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf3 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   2 );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_meter_ratio)
    {
        CtiCommandParser parse("getconfig meter ratio");

        BOOST_CHECK_EQUAL( ClientErrors::None, test_Mct440_213xB().beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf3 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   2 );
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


struct commandExecution_helper : beginExecuteRequest_helper
{
    test_Mct440_213xB mct440;
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

    BOOST_AUTO_TEST_CASE(test_dev_mct440_213xb_getvalue_daily_read_recent)
    {

        mct440.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct440.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        for(int i = 0; i < 9; i++)
        {
            CtiTime timenow;
            CtiDate today = timenow.date();

            CtiDate date  = today;

            /* iteration 0 -> test yesterday (no date specified)
             *           1 -> test yesterday
             *           2 -> test 2 days ago
             *           3 -> test 3 days ago
             *           4 -> test 4 days ago
             *           5 -> test 5 days ago
             *           6 -> test 6 days ago
             *           7 -> test 7 days ago
             *           8 -> test 8 days ago
             */

            date -= (i == 0) ? 1 : i;

            {
                string str_cmd( string("getvalue daily read") );

                if (i != 0)
                {
                    str_cmd += " " + date.asStringMDY();
                }

                CtiCommandParser parse(str_cmd);

                BOOST_CHECK_EQUAL( ClientErrors::None , mct440.executeGetValue(&request, parse, om, vgList, retList, outList) );

                BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read);

                /* iteration 0 -> func 0x30
                 *           1 -> func 0x30
                 *           2 -> func 0x31
                 *           3 -> func 0x32
                 *           4 -> func 0x33
                 *           5 -> func 0x34
                 *           6 -> func 0x35
                 *           7 -> func 0x36
                 *           8 -> func 0x37
                 */

                int exp_func = (i == 0) ? 0x30 : (0x30 + i - 1);

                BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, exp_func);
                BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   10);

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

                unsigned char buf[10] = { 0x98, 0x96, 0x7e, 0x88, 0x86, 0x8e, 0x0, 0x3, 0x0, 0x0};

                buf[8] = date.dayOfMonth();
                buf[9] = date.month() - 1;

                std::copy(buf,  buf + 10, im.Buffer.DSt.Message );

                im.Buffer.DSt.Length = 10;
                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

                BOOST_CHECK_EQUAL( ClientErrors::None , mct440.decodeGetValueDailyReadRecent(im, timenow, vgList, retList, outList) );

                BOOST_REQUIRE_EQUAL(retList.size(), 1);

                const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

                BOOST_REQUIRE(retMsg);

                CtiMultiMsg_vec points = retMsg->PointData();

                BOOST_REQUIRE_EQUAL(points.size(), 5);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // number of outage

                    BOOST_REQUIRE( pdata );

                    CtiTime t_exp((date + 1), 0, 0, 0); // add 1 day

                    BOOST_CHECK_EQUAL( pdata->getValue(), 3);
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime().seconds(), t_exp.seconds());

                    CtiPointSPtr point = mct440.getDevicePointOffsetTypeEqual(20, PulseAccumulatorPointType);

                    BOOST_CHECK_EQUAL( pdata->getId(), point->getID() );
                }

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // forward active energy

                    BOOST_REQUIRE( pdata );

                    CtiTime t_exp((date + 1), 0, 0, 0); // add 1 day

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0x98967e);
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime().seconds(), t_exp.seconds());

                    CtiPointSPtr point = mct440.getDevicePointOffsetTypeEqual(1, PulseAccumulatorPointType);

                    BOOST_CHECK_EQUAL( pdata->getId(), point->getID() );
                }

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // reverse active energy

                    BOOST_REQUIRE( pdata );

                    CtiTime t_exp((date + 1), 0, 0, 0); // add 1 day

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0x88868e);
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime().seconds(), t_exp.seconds());

                    CtiPointSPtr point = mct440.getDevicePointOffsetTypeEqual(2, PulseAccumulatorPointType);

                    BOOST_CHECK_EQUAL( pdata->getId(), point->getID() );
                }

                // verify points are echoed to 181 and 281

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);  // forward active energy echoed

                    BOOST_REQUIRE( pdata );

                    CtiTime t_exp((date + 1), 0, 0, 0); // add 1 day

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0x98967e);
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime().seconds(), t_exp.seconds());

                    CtiPointSPtr point = mct440.getDevicePointOffsetTypeEqual(181, PulseAccumulatorPointType);

                    BOOST_CHECK_EQUAL( pdata->getId(), point->getID() );
                }

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);  // reverse active energy echoed

                    BOOST_REQUIRE( pdata );

                    CtiTime t_exp((date + 1), 0, 0, 0); // add 1 day

                    BOOST_CHECK_EQUAL( pdata->getValue(), 0x88868e);
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime().seconds(), t_exp.seconds());

                    CtiPointSPtr point = mct440.getDevicePointOffsetTypeEqual(281, PulseAccumulatorPointType);

                    BOOST_CHECK_EQUAL( pdata->getId(), point->getID() );
                }
            }
        }
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct440_getvalue_outage)
    {
        const auto tz_override = Cti::Test::set_to_central_timezone();

        mct440.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec,         10300);
        //  SSPEC revision does not matter for the MCT-440 outage decode
        //mct440.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 17);  //  set the device to SSPEC revision 1.7

        {
            CtiCommandParser parse( "getvalue outage 1" );

            BOOST_CHECK_EQUAL( ClientErrors::None , mct440.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            //  4 bytes of time, 2 bytes duration in seconds.  Last byte is duration types.
            char input[13] = {0x50, 1, 2, 3, 4, 5, 0x50, 7, 8, 9, 10, 11, 0x11};

            std::copy(input, input + 13, im.Buffer.DSt.Message);
            im.Buffer.DSt.Length = 13;
            im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set
            strcpy(im.Return.CommandStr, "getvalue outage 1");

            BOOST_CHECK_EQUAL( ClientErrors::None , mct440.decodeGetValueOutage(im, timeNow, vgList, retList, outList) );
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

                    BOOST_CHECK_CLOSE( pdata->getValue(), 1029, 0.001 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(14,  7, 2012), 0, 22, 11) );
                    BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-440-213xB / Outage 1 : 07/14/2012 00:22:11 for 00:17:09");
                }
                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);

                    BOOST_REQUIRE( pdata );

                    BOOST_CHECK_CLOSE( pdata->getValue(), 2571, 0.001 );
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime(), CtiTime(CtiDate(18,  7, 2012), 14, 01, 29) );
                    BOOST_CHECK_EQUAL( pdata->getString(), "Test MCT-440-213xB / Outage 2 : 07/18/2012 14:01:29 for 00:42:51");
                }
            }
        }
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


BOOST_FIXTURE_TEST_SUITE(requests, beginExecuteRequest_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE

    BOOST_AUTO_TEST_CASE(test_dev_mct_control_connect_execute)
    {
        CtiRequestMsg    req( -1, "control connect" );
        CtiCommandParser parse( req.CommandString() );

        BOOST_CHECK_EQUAL( ClientErrors::None , test_Mct440_213xB().beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );
        BOOST_REQUIRE_EQUAL( 1, outList.size() );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE    ( om );
        BOOST_CHECK_EQUAL( 76, om->Buffer.BSt.Function );
        BOOST_CHECK_EQUAL(  0, om->Buffer.BSt.IO );
        BOOST_CHECK_EQUAL(  0, om->Buffer.BSt.Length );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct_control_connect_decode)
    {
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        INMESS im;

        im.Sequence = EmetconProtocol::Control_Connect;
        im.Buffer.DSt.Length = 0;
        im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

        strcpy(im.Return.CommandStr, "control connect");

        BOOST_CHECK_EQUAL( ClientErrors::None , test_Mct440_213xB().ResultDecode(im, timeNow, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( 2, retList.size() );
        BOOST_CHECK( outList.empty() );

        const std::vector<const CtiMessage *> retMsgs(retList.begin(), retList.end());

        const CtiRequestMsg *req = dynamic_cast<const CtiRequestMsg *>(retMsgs[0]);

        BOOST_REQUIRE( req );
        BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( req->CommandString(), "getstatus disconnect" );
        BOOST_CHECK_EQUAL( req->getMessageTime(), CtiTime(CtiDate(1, 1, 2010), 1, 2, 18) );  //  the MCT-440 scans the disconnect status 10 seconds after the decode

        const CtiReturnMsg *ret = dynamic_cast<const CtiReturnMsg *>(retMsgs[1]);

        BOOST_REQUIRE( ret );
        BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( ret->Status(),   0 );
        BOOST_CHECK_EQUAL( ret->CommandString(), "control connect" );
        BOOST_CHECK_EQUAL( ret->ResultString(),  "Test MCT-440-213xB / control sent\nWaiting 15 seconds to read status (until 01/01/2010 01:02:18)" );
    }

    BOOST_AUTO_TEST_CASE(test_dev_mct_control_disconnect_execute)
    {
        CtiRequestMsg    req( -1, "control disconnect" );
        CtiCommandParser parse( req.CommandString() );

        BOOST_CHECK_EQUAL( ClientErrors::None , test_Mct440_213xB().beginExecuteRequest(&req, parse, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_CHECK( retList.empty() );
        BOOST_REQUIRE_EQUAL( 1, outList.size() );

        const OUTMESS *om = outList.front();

        BOOST_REQUIRE( om );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 77 );
        BOOST_CHECK_EQUAL(  0, om->Buffer.BSt.IO );
        BOOST_CHECK_EQUAL(  0, om->Buffer.BSt.Length );
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct_control_disconnect_decode)
    {
        CtiTime timeNow(CtiDate(1, 1, 2010), 1, 2, 3);

        INMESS im;

        im.Sequence = EmetconProtocol::Control_Disconnect;
        im.Buffer.DSt.Length = 0;
        im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

        strcpy(im.Return.CommandStr, "control disconnect");

        BOOST_CHECK_EQUAL( ClientErrors::None , test_Mct440_213xB().ResultDecode(im, timeNow, vgList, retList, outList) );

        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( 2, retList.size() );
        BOOST_CHECK( outList.empty() );

        const std::vector<const CtiMessage *> retMsgs(retList.begin(), retList.end());

        const CtiRequestMsg *req = dynamic_cast<const CtiRequestMsg *>(retMsgs[0]);

        BOOST_REQUIRE( req );
        BOOST_CHECK_EQUAL( req->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( req->CommandString(), "getstatus disconnect" );
        BOOST_CHECK_EQUAL( req->getMessageTime(), CtiTime(CtiDate(1, 1, 2010), 1, 2, 18) );  //  the MCT-440 scans the disconnect status 10 seconds after the decode

        const CtiReturnMsg *ret = dynamic_cast<const CtiReturnMsg *>(retMsgs[1]);

        BOOST_REQUIRE( ret );
        BOOST_CHECK_EQUAL( ret->DeviceId(), 123456 );
        BOOST_CHECK_EQUAL( ret->Status(),   0 );
        BOOST_CHECK_EQUAL( ret->CommandString(), "control disconnect" );
        BOOST_CHECK_EQUAL( ret->ResultString(),  "Test MCT-440-213xB / control sent\nWaiting 15 seconds to read status (until 01/01/2010 01:02:18)" );
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


BOOST_AUTO_TEST_CASE(test_dev_mct440_213xb_getUsageReportDelay)
{
    const test_Mct440_213xB test_dev;

    //  Calculation is 10s + max(36, days) * intervals/day * 10ms, rounded up to the next second

    //  36 * 24 * 12 * 0.01 = 103.68s
    BOOST_CHECK_EQUAL(test_dev.getUsageReportDelay(300,   1), 114);

    //  36 * 24 * 12 * 0.01 = 103.68s
    BOOST_CHECK_EQUAL(test_dev.getUsageReportDelay(300,  30), 114);

    //  60 * 24 * 12 * 0.01 = 172.80s
    BOOST_CHECK_EQUAL(test_dev.getUsageReportDelay(300,  60), 183);

    //  36 * 24 *  6 * 0.01 = 51.84s
    BOOST_CHECK_EQUAL(test_dev.getUsageReportDelay(600,   1),  62);

    //  36 * 24 *  6 * 0.01 = 51.84s
    BOOST_CHECK_EQUAL(test_dev.getUsageReportDelay(600,  30),  62);

    //  60 * 24 *  6 * 0.01 = 86.40s
    BOOST_CHECK_EQUAL(test_dev.getUsageReportDelay(600,  60),  97);

    //  36 * 24 *  1 * 0.01 = 8.64s
    BOOST_CHECK_EQUAL(test_dev.getUsageReportDelay(3600,  1),  19);

    //  36 * 24 *  1 * 0.01 = 8.64s
    BOOST_CHECK_EQUAL(test_dev.getUsageReportDelay(3600, 30),  19);

    //  60 * 24 *  1 * 0.01 = 14.40s
    BOOST_CHECK_EQUAL(test_dev.getUsageReportDelay(3600, 60),  25);
}


BOOST_AUTO_TEST_CASE(test_dev_mct440_213xb_extractDynamicPaoInfo)
{
    test_Mct440_213xB test_dev;

    INMESS im;

    im.Buffer.DSt.Message[ 0] = 0xff;
    im.Buffer.DSt.Message[ 1] = 0x28;
    im.Buffer.DSt.Message[ 2] = 0x3c;
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

    test_dev.extractDynamicPaoInfo(im);

    BOOST_CHECK(test_dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec));
    BOOST_CHECK(test_dev.hasDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision));

    BOOST_CHECK_EQUAL(test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpec),           0x283c);
    BOOST_CHECK_EQUAL(test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision),   0xff);
}


struct getOperation_helper
{
    test_Mct440_213xB test_dev;

    BSTRUCT BSt;
};

BOOST_FIXTURE_TEST_SUITE(test_getOperation, getOperation_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_getOperation_01)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::Command_Loop, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_02)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_Model, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_03)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_Install, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_04)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_GroupAddressEnable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x54);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_05)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_GroupAddressInhibit, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x53);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_06)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_Raw, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_07)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::Control_Shed, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_08)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::Control_Restore, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    //  Overridden by MCT-410
    /*
    BOOST_AUTO_TEST_CASE(test_getOperation_09)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::Control_Connect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x20);  //  Q_ARML
        BOOST_CHECK_EQUAL(BSt.Function, 0x42);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_10)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::Control_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x20);  //  Q_ARML
        BOOST_CHECK_EQUAL(BSt.Function, 0x41);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    */
    BOOST_AUTO_TEST_CASE(test_getOperation_11)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_ARMC, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x62);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_12)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_ARML, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x60);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    //  Overridden by MCT-410
    /*
    BOOST_AUTO_TEST_CASE(test_getOperation_13)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x49);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    */
    //  MCT-4xx commands
    BOOST_AUTO_TEST_CASE(test_getOperation_14)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_TOUPeak, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xb0);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_15)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutValue_TOUReset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x5f);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_16)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutValue_ResetPFCount, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x89);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_17)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf0);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_18)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_TOUEnable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x56);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_19)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_TOUDisable, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x55);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_20)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_LoadProfileInterest, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x05);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    //  MCT-410 commands
    BOOST_AUTO_TEST_CASE(test_getOperation_21)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::Scan_Accum, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x90);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_22)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_KWH, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x90);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_23)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_FrozenKWH, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x91);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_24)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_TOUkWh, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe0);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_25)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_FrozenTOUkWh, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe1);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_26)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::Scan_Integrity, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_27)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_28)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_29)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_LoadProfilePeakReport, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_30)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_Demand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
/*    BOOST_AUTO_TEST_CASE(test_getOperation_31)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_PeakDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x93);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_32)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_FrozenPeakDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x94);
        BOOST_CHECK_EQUAL(BSt.Length,   10);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_33)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_Voltage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x95);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_34)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_FrozenVoltage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x96);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
 */ BOOST_AUTO_TEST_CASE(test_getOperation_35)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_Outage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_36)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetStatus_Internal, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x05);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_37)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetStatus_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x97);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_38)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetStatus_Freeze, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x26);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_39)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::Control_Connect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4c);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_40)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::Control_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4d);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_41)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetStatus_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
//    BOOST_AUTO_TEST_CASE(test_getOperation_42)
//    {
//        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_Disconnect, BSt));
//        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
//        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
//        BOOST_CHECK_EQUAL(BSt.Length,   11);
//    }
//    BOOST_AUTO_TEST_CASE(test_getOperation_43)
//    {
//        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_Disconnect, BSt));
//        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
//        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
//        BOOST_CHECK_EQUAL(BSt.Length,   8);
//    }
    BOOST_AUTO_TEST_CASE(test_getOperation_44)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_Raw, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_45)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x44);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_46)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_Time, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x3f);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_47)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_FreezeDay, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_48)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_TimeZoneOffset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x3f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_49)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_Intervals, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x03);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_50)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_Intervals, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1a);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_51)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_OutageThreshold, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x22);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
//    BOOST_AUTO_TEST_CASE(test_getOperation_52)
//    {
//        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_Thresholds, BSt));
//        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
//        BOOST_CHECK_EQUAL(BSt.Function, 0x1e);
//        BOOST_CHECK_EQUAL(BSt.Length,   3);
//    }
    BOOST_AUTO_TEST_CASE(test_getOperation_53)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_DailyReadInterest, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL(BSt.Length,   11);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_54)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_PFCount, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x23);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_55)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutStatus_Reset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x8a);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_56)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutStatus_ResetAlarms, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x08);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_57)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutStatus_FreezeOne, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x51);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_58)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutStatus_FreezeTwo, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x52);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_59)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutStatus_FreezeVoltageOne, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x59);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_60)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutStatus_FreezeVoltageTwo, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x5a);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_61)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_UniqueAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf1);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_62)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_UniqueAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   3);
    }
    //  Overridden by MCT-420
    /*
    BOOST_AUTO_TEST_CASE(test_getOperation_63)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_Multiplier, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x19);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_64)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_MeterParameters, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x0f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    */
    BOOST_AUTO_TEST_CASE(test_getOperation_65)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_Freeze, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4f);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_66)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_LongLoadProfileStorage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x04);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_67)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_LongLoadProfileStorage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x9d);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_68)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_PhaseLossThreshold, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL(BSt.Length,   3);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_69)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_Holiday, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd0);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_70)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_Holiday, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xd0);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_71)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_Options, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x01);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_72)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_AutoReconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x01);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_73)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_Outage, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x22);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_74)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_TimeAdjustTolerance, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x36);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_75)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_PhaseDetect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x0f);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_76)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_PhaseDetect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_77)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_PhaseDetectClear, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    //  MCT-420 commands
    BOOST_AUTO_TEST_CASE(test_getOperation_78)
    {
        BOOST_REQUIRE(test_dev.getOperation(Cti::Protocols::EmetconProtocol::GetConfig_Multiplier, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf3);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_79)
    {
        BOOST_REQUIRE(test_dev.getOperation(Cti::Protocols::EmetconProtocol::GetConfig_MeterParameters, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf3);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_80)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_InstantLineData, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x9f);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_81)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_TOUkWhReverse, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe2);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_82)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetValue_FrozenTOUkWhReverse, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xe3);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_1Dword)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct440_213xBDevice::ReadDescriptor> results;

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
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DemandInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval))
        (empty)
        (empty)
        (empty)
        // memory read 30
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PhaseLossPercent)(1,2,(int)Dpi::Key_MCT_PhaseLossSeconds))
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
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_Holiday1)(2,2,(int)Dpi::Key_MCT_Holiday2)) // holiday 1 - 2
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_Holiday7)(2,2,(int)Dpi::Key_MCT_Holiday8)) // holiday 7 - 9
        // memory read 210
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_Holiday13)(2,2,(int)Dpi::Key_MCT_Holiday14)) // holiday 13 - 14
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 220
        .repeat(36, empty);

    const test_Mct440_213xB test_dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back( test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 3));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_2Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct440_213xBDevice::ReadDescriptor> results;

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
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DemandInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval))
        (empty)
        (empty)
        (empty)
        // memory read 30
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PhaseLossPercent)(1,2,(int)Dpi::Key_MCT_PhaseLossSeconds))
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
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_Holiday1)(2,2,(int)Dpi::Key_MCT_Holiday2)(4,2,(int)Dpi::Key_MCT_Holiday3)(6,2,(int)Dpi::Key_MCT_Holiday4)) // holiday 1 - 4
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_Holiday7)(2,2,(int)Dpi::Key_MCT_Holiday8)(4,2,(int)Dpi::Key_MCT_Holiday9)(6,2,(int)Dpi::Key_MCT_Holiday10)) // holiday 7 - 11
        // memory read 210
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_Holiday13)(2,2,(int)Dpi::Key_MCT_Holiday14)(4,2,(int)Dpi::Key_MCT_Holiday15))          // holiday 13 - 15
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 220
        .repeat(36, empty);

    const test_Mct440_213xB test_dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 8));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_3Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct440_213xBDevice::ReadDescriptor> results;

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
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_DemandInterval)(1,1,(int)Dpi::Key_MCT_LoadProfileInterval))
        (empty)
        (empty)
        (empty)
        // memory read 30
        (tuple_list_of(0,1,(int)Dpi::Key_MCT_PhaseLossPercent)(1,2,(int)Dpi::Key_MCT_PhaseLossSeconds))
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
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_Holiday1)(2,2,(int)Dpi::Key_MCT_Holiday2)(4,2,(int)Dpi::Key_MCT_Holiday3)(6,2,(int)Dpi::Key_MCT_Holiday4)(8,2,(int)Dpi::Key_MCT_Holiday5)(10,2,(int)Dpi::Key_MCT_Holiday6)) // holiday 1 - 6
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_Holiday7)(2,2,(int)Dpi::Key_MCT_Holiday8)(4,2,(int)Dpi::Key_MCT_Holiday9)(6,2,(int)Dpi::Key_MCT_Holiday10)(8,2,(int)Dpi::Key_MCT_Holiday11)(10,2,(int)Dpi::Key_MCT_Holiday12)) // holiday 7 - 12
        // memory read 210
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_Holiday13)(2,2,(int)Dpi::Key_MCT_Holiday14)(4,2,(int)Dpi::Key_MCT_Holiday15))                             // holiday 13 - 15
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 220
        .repeat(36, empty);

    const test_Mct440_213xB test_dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back( test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 13));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_1Dword)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct440_213xBDevice::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty)
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_EventFlagsMask1)(2,1,(int)Dpi::Key_MCT_EventFlagsMask2))
        .repeat(170-2, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable))
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
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 240
        .repeat(6, empty);

    const test_Mct440_213xB test_dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back( test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 3));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_2Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct440_213xBDevice::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty)
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_EventFlagsMask1)(2,1,(int)Dpi::Key_MCT_EventFlagsMask2)(3,2,(int)Dpi::Key_MCT_MeterAlarmMask)(5,2,(int)Dpi::Key_MCT_Configuration))
        .repeat(150-2, empty)
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
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable))
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
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 250
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty);

    const test_Mct440_213xB test_dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back( test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 8));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_3Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct440_213xBDevice::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty)
        (tuple_list_of(1,1,(int)Dpi::Key_MCT_EventFlagsMask1)(2,1,(int)Dpi::Key_MCT_EventFlagsMask2)(3,2,(int)Dpi::Key_MCT_MeterAlarmMask)(5,2,(int)Dpi::Key_MCT_Configuration))
        .repeat(150-2, empty)
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
        (tuple_list_of(0,2,(int)Dpi::Key_MCT_DayTable)(10,1,(int)Dpi::Key_MCT_TimeZoneOffset))
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
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        //  function read 250
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty);

    const test_Mct440_213xB test_dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back( test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 13));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
        expected.begin(), expected.end(),
        results.begin(),  results.end());
}

BOOST_AUTO_TEST_CASE(test_decodeGetConfigTOU)
{
    INMESS                         InMessage;
    CtiTime                        t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)
    CtiDeviceBase::CtiMessageList  vgList;
    CtiDeviceBase::CtiMessageList  retList;
    CtiDeviceBase::OutMessageList  outList; // not use

    string daySchedule1 = "10, 11, 12, 13, 14, 15, 16, 17, 18, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1",
           daySchedule2 = "20, 21, 22, 23, 24, 25, 26, 27, 28, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2",
           daySchedule3 = "30, 31, 32, 33, 34, 35, 36, 37, 38, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3",
           daySchedule4 = "40, 41, 42, 43, 44, 45, 46, 47, 48, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0";

    {
        test_Mct440_213xB test_dev;

        Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;  //  Reset the DynamicPao for this scope

        unsigned char test_data[13] = {50, 51, 52, 53, 54, 0xf0, 0x00, 60, 61, 62, 63, 64, 0xff};

        memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = EmetconProtocol::GetConfig_TOU;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0xAE;

        string cmd = "getconfig tou schedule 1";
        strcpy(InMessage.Return.CommandStr, cmd.c_str());

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetConfigTOU(InMessage, t, vgList, retList, outList));

        string result1, result2, result3, result4;

        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, result1);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, result2);

        string expSchedule1 = "50, 51, 52, 53, 54, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, -1, -1, -1, -1",
               expSchedule2 = "60, 61, 62, 63, 64, -1, -1, -1, -1, 3, 3, 3, 3, 3, 3, -1, -1, -1, -1",
               expSchedule3 = "",
               expSchedule4 = "";

        BOOST_CHECK_EQUAL( expSchedule1, result1);
        BOOST_CHECK_EQUAL( expSchedule2, result2);
        BOOST_CHECK_EQUAL( expSchedule3, result3);
        BOOST_CHECK_EQUAL( expSchedule4, result4);
    }

    {
        test_Mct440_213xB test_dev;

        Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;  //  Reset the DynamicPao for this scope

        unsigned char test_data[13] = {50, 51, 52, 53, 0x00, 60, 61, 62, 63, 0xff};

        memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = EmetconProtocol::GetConfig_TOU;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0xB8;

        string cmd = "getconfig tou schedule 1";
        strcpy(InMessage.Return.CommandStr, cmd.c_str());

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetConfigTOU(InMessage, t, vgList, retList, outList));

        string result1, result2, result3, result4;

        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, result1);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, result2);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, result3);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, result4);

        string expSchedule1 = "-1, -1, -1, -1, -1, 50, 51, 52, 53, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0",
               expSchedule2 = "-1, -1, -1, -1, -1, 60, 61, 62, 63, -1, -1, -1, -1, -1, -1, 3, 3, 3, 3",
               expSchedule3 = "",
               expSchedule4 = "";

        BOOST_CHECK_EQUAL( expSchedule1, result1);
        BOOST_CHECK_EQUAL( expSchedule2, result2);
        BOOST_CHECK_EQUAL( expSchedule3, result3);
        BOOST_CHECK_EQUAL( expSchedule4, result4);
    }

    {
        test_Mct440_213xB test_dev;

        Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;  //  Reset the DynamicPao for this scope

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, daySchedule1);
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, daySchedule2);
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, daySchedule3);
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, daySchedule4);

        unsigned char test_data[13] = {50, 51, 52, 53, 54, 0xf0, 0x00, 60, 61, 62, 63, 64, 0xff};

        memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = EmetconProtocol::GetConfig_TOU;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0xAE;

        string cmd = "getconfig tou schedule 1";
        strcpy(InMessage.Return.CommandStr, cmd.c_str());

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetConfigTOU(InMessage, t, vgList, retList, outList));

        string result1, result2, result3, result4;

        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, result1);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, result2);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, result3);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, result4);

        string expSchedule1 = "50, 51, 52, 53, 54, 15, 16, 17, 18, 0, 0, 0, 0, 0, 0, 2, 3, 0, 1",
               expSchedule2 = "60, 61, 62, 63, 64, 25, 26, 27, 28, 3, 3, 3, 3, 3, 3, 3, 0, 1, 2",
               expSchedule3 = "30, 31, 32, 33, 34, 35, 36, 37, 38, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3",
               expSchedule4 = "40, 41, 42, 43, 44, 45, 46, 47, 48, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0";

        BOOST_CHECK_EQUAL( expSchedule1, result1);
        BOOST_CHECK_EQUAL( expSchedule2, result2);
        BOOST_CHECK_EQUAL( expSchedule3, result3);
        BOOST_CHECK_EQUAL( expSchedule4, result4);
    }

    {
        test_Mct440_213xB test_dev;

        Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;  //  Reset the DynamicPao for this scope

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, daySchedule1);
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, daySchedule2);
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, daySchedule3);
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, daySchedule4);

        unsigned char test_data[13] = {50, 51, 52, 53, 0x00, 60, 61, 62, 63, 0xff};

        memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = EmetconProtocol::GetConfig_TOU;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0xB8;

        string cmd = "getconfig tou schedule 1";
        strcpy(InMessage.Return.CommandStr, cmd.c_str());

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetConfigTOU(InMessage, t, vgList, retList, outList));

        string result1, result2, result3, result4;

        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, result1);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, result2);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, result3);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, result4);

        string expSchedule1 = "10, 11, 12, 13, 14, 50, 51, 52, 53, 0, 1, 2, 3, 0, 1, 0, 0, 0, 0",
               expSchedule2 = "20, 21, 22, 23, 24, 60, 61, 62, 63, 1, 2, 3, 0, 1, 2, 3, 3, 3, 3",
               expSchedule3 = "30, 31, 32, 33, 34, 35, 36, 37, 38, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3",
               expSchedule4 = "40, 41, 42, 43, 44, 45, 46, 47, 48, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0";

        BOOST_CHECK_EQUAL( expSchedule1, result1);
        BOOST_CHECK_EQUAL( expSchedule2, result2);
        BOOST_CHECK_EQUAL( expSchedule3, result3);
        BOOST_CHECK_EQUAL( expSchedule4, result4);
    }

    {
        test_Mct440_213xB test_dev;

        Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;  //  Reset the DynamicPao for this scope

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, daySchedule1);
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, daySchedule2);
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, daySchedule3);
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, daySchedule4);

        unsigned char test_data[13] = {50, 51, 52, 53, 54, 0xf0, 0x00, 60, 61, 62, 63, 64, 0xff};

        memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = EmetconProtocol::GetConfig_TOU;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0xAF;

        string cmd = "getconfig tou schedule 3";
        strcpy(InMessage.Return.CommandStr, cmd.c_str());

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetConfigTOU(InMessage, t, vgList, retList, outList));

        string result1, result2, result3, result4;

        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, result1);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, result2);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, result3);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, result4);

        string expSchedule1 = "10, 11, 12, 13, 14, 15, 16, 17, 18, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1",
               expSchedule2 = "20, 21, 22, 23, 24, 25, 26, 27, 28, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2",
               expSchedule3 = "50, 51, 52, 53, 54, 35, 36, 37, 38, 0, 0, 0, 0, 0, 0, 0, 1, 2, 3",
               expSchedule4 = "60, 61, 62, 63, 64, 45, 46, 47, 48, 3, 3, 3, 3, 3, 3, 1, 2, 3, 0";

        BOOST_CHECK_EQUAL( expSchedule1, result1);
        BOOST_CHECK_EQUAL( expSchedule2, result2);
        BOOST_CHECK_EQUAL( expSchedule3, result3);
        BOOST_CHECK_EQUAL( expSchedule4, result4);
    }

    {
        test_Mct440_213xB test_dev;

        Cti::Test::Override_DynamicPaoInfoManager overrideDynamicPaoInfoManager;  //  Reset the DynamicPao for this scope

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, daySchedule1);
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, daySchedule2);
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, daySchedule3);
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, daySchedule4);

        unsigned char test_data[13] = {50, 51, 52, 53, 0x00, 60, 61, 62, 63, 0xff};

        memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = EmetconProtocol::GetConfig_TOU;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0xB9;

        string cmd = "getconfig tou schedule 3";
        strcpy(InMessage.Return.CommandStr, cmd.c_str());

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetConfigTOU(InMessage, t, vgList, retList, outList));

        string result1, result2, result3, result4;

        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule1, result1);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule2, result2);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule3, result3);
        test_dev.getDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DaySchedule4, result4);

        string expSchedule1 = "10, 11, 12, 13, 14, 15, 16, 17, 18, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1",
               expSchedule2 = "20, 21, 22, 23, 24, 25, 26, 27, 28, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2",
               expSchedule3 = "30, 31, 32, 33, 34, 50, 51, 52, 53, 2, 3, 0, 1, 2, 3, 0, 0, 0, 0",
               expSchedule4 = "40, 41, 42, 43, 44, 60, 61, 62, 63, 3, 0, 1, 2, 3, 0, 3, 3, 3, 3";

        BOOST_CHECK_EQUAL( expSchedule1, result1);
        BOOST_CHECK_EQUAL( expSchedule2, result2);
        BOOST_CHECK_EQUAL( expSchedule3, result3);
        BOOST_CHECK_EQUAL( expSchedule4, result4);
    }

    delete_container(vgList);
    delete_container(retList);
    delete_container(outList);
}


BOOST_AUTO_TEST_CASE(test_decodeGetValueKWH)
{
    INMESS                          InMessage;
    CtiDeviceBase::CtiMessageList   vgList;
    CtiDeviceBase::CtiMessageList   retList;
    CtiDeviceBase::OutMessageList   outList; // not use

    // ----------- GetValue_kWh (getvalue kwh) ----------- //

    {
        test_Mct440_213xB test_dev;
        CtiTime           timeNow     (CtiDate(1, 1, 2011), 19, 16, 23);
        CtiTime           timeExpected(CtiDate(1, 1, 2011), 19, 16, 23);
        string            cmd = "getvalue kwh";

        strcpy(InMessage.Return.CommandStr, cmd.c_str());

        const unsigned char test_data[3] = {0x1,0x2,0x3};

        memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
        InMessage.Return.UserID = 0;
        InMessage.Sequence      = EmetconProtocol::GetValue_KWH;

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetValueKWH(InMessage, timeNow, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        CtiMultiMsg_vec points = retMsg->PointData();

        BOOST_REQUIRE_EQUAL(points.size(), 1);

        {
            const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // Forward Active Energy

            BOOST_REQUIRE( pdata );

            BOOST_CHECK_EQUAL( pdata->getValue(),   0x010203      );
            BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
            BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
        }

        delete retMsg;

        retList.pop_front();
    }

    // ----------- GetValue_kWh (getvalue usage) ----------- //

    {
        test_Mct440_213xB test_dev;
        CtiTime           timeNow     (CtiDate(2, 2, 2011), 20, 15, 22);
        CtiTime           timeExpected(CtiDate(2, 2, 2011), 20, 15, 22);
        string            cmd = "getvalue usage";

        strcpy(InMessage.Return.CommandStr, cmd.c_str());

        const unsigned char test_data[9] = {0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9};

        memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
        InMessage.Return.UserID = 0;
        InMessage.Sequence      = EmetconProtocol::GetValue_KWH;

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetValueKWH(InMessage, timeNow, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        CtiMultiMsg_vec points = retMsg->PointData();

        BOOST_REQUIRE_EQUAL(points.size(), 3);

        {
            const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // Forward Active Energy

            BOOST_REQUIRE( pdata );

            BOOST_CHECK_EQUAL( pdata->getValue(),   0x010203      );
            BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
            BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
        }

        {
            const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // Reverse Active Energy

            BOOST_REQUIRE( pdata );

            BOOST_CHECK_EQUAL( pdata->getValue(),   0x040506      );
            BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
            BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
        }

        {
            const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // Inductive Reactive Energy

            BOOST_REQUIRE( pdata );

            BOOST_CHECK_EQUAL( pdata->getValue(),   0x070809      );
            BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
            BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
        }

        delete retMsg;

        retList.pop_front();
    }

    // ----------- GetValue_FrozenKWH (getvalue kwh frozen) ----------- //

    {
        test_Mct440_213xB test_dev;
        CtiTime           timeNow     (CtiDate(2, 2, 2011), 20, 15, 22);
        CtiTime           timeFrozen  (CtiDate(2, 1, 2011), 16, 12, 45);
        CtiTime           timeExpected(CtiDate(2, 1, 2011), 16, 12, 45);
        string            cmd = "getvalue kwh frozen";

        strcpy(InMessage.Return.CommandStr, cmd.c_str());

        const unsigned char freeze_counter = 16;
        const unsigned char test_data[4]   = {0x11,0x22,0x33, freeze_counter};

        memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
        InMessage.Return.UserID = 0;
        InMessage.Sequence      = EmetconProtocol::GetValue_FrozenKWH;

        test_dev.updateFreezeInfo(freeze_counter, timeFrozen.seconds());

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetValueKWH(InMessage, timeNow, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        CtiMultiMsg_vec points = retMsg->PointData();

        BOOST_REQUIRE_EQUAL(points.size(), 1);

        {
            const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);   // Frozen Forward Active Energy

            BOOST_REQUIRE( pdata );

            BOOST_CHECK_EQUAL( pdata->getValue(),   0x112233      );
            BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
            BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
        }

        delete retMsg;

        retList.pop_front();
    }

    // ----------- GetValue_FrozenKWH (getvalue usage frozen) ----------- //

    {
        test_Mct440_213xB test_dev;
        CtiTime           timeNow     (CtiDate(2, 2, 2011), 21, 38, 36);
        CtiTime           timeFrozen  (CtiDate(2, 2, 2011), 17, 41, 23);
        CtiTime           timeExpected(CtiDate(2, 2, 2011), 17, 41, 23);
        string            cmd = "getvalue usage frozen";

        strcpy(InMessage.Return.CommandStr, cmd.c_str());

        const unsigned char freeze_counter = 24;
        const unsigned char test_data[10]  = {0x11,0x22,0x33, freeze_counter, 0x44, 0x55, 0x66, 0x77, 0x88, 0x99};

        memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
        InMessage.Return.UserID = 0;
        InMessage.Sequence      = EmetconProtocol::GetValue_FrozenKWH;

        test_dev.updateFreezeInfo(freeze_counter, timeFrozen.seconds());

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetValueKWH(InMessage, timeNow, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        CtiMultiMsg_vec points = retMsg->PointData();

        BOOST_REQUIRE_EQUAL(points.size(), 3);

        {
            const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // Frozen Forward Active Energy

            BOOST_REQUIRE( pdata );

            BOOST_CHECK_EQUAL( pdata->getValue(),   0x112233      );
            BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
            BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
        }

        {
            const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // Frozen Reverse Active Energy

            BOOST_REQUIRE( pdata );

            BOOST_CHECK_EQUAL( pdata->getValue(),   0x445566      );
            BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
            BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
        }

        {
            const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // Frozen Inductive Reactive Energy

            BOOST_REQUIRE( pdata );

            BOOST_CHECK_EQUAL( pdata->getValue(),   0x778899      );
            BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
            BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
        }

        delete retMsg;

        retList.pop_front();
    }

    delete_container(vgList);
    delete_container(retList);
    delete_container(outList);
}


BOOST_AUTO_TEST_CASE(test_decodeGetValueTOUkWh)
{
    INMESS                          InMessage;
    CtiTime                         timeNow     (CtiDate(1, 1, 2011), 19, 16, 23);
    CtiTime                         timeExpected(CtiDate(1, 1, 2011), 19, 16, 23);
    CtiDeviceBase::CtiMessageList   vgList;
    CtiDeviceBase::CtiMessageList   retList;
    CtiDeviceBase::OutMessageList   outList; // not use


    unsigned char test_data[13] = {0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9,0xA,0xB,0xC,0xD};

    memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

    InMessage.Return.UserID = 0;
    InMessage.Sequence      = EmetconProtocol::GetValue_TOUkWh;

    test_Mct440_213xB test_dev;

    BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetValueTOUkWh(InMessage, timeNow, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    CtiMultiMsg_vec points = retMsg->PointData();

    BOOST_REQUIRE_EQUAL(points.size(), 4);

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   0x010203      );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // rate b

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   0x040506      );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // rate c

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   0x070809      );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);  // rate d

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   0x0A0B0C      );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    delete_container(vgList);
    delete_container(retList);
    delete_container(outList);
}


BOOST_AUTO_TEST_CASE(test_decodeGetValueTOUkWh_reverse)
{
    INMESS                          InMessage;
    CtiTime                         timeNow     (CtiDate(1, 1, 2011), 19, 16, 34);
    CtiTime                         timeExpected(CtiDate(1, 1, 2011), 19, 16, 34);
    CtiDeviceBase::CtiMessageList   vgList;
    CtiDeviceBase::CtiMessageList   retList;
    CtiDeviceBase::OutMessageList   outList; // not use


    unsigned char test_data[13] = {0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9,0xA,0xB,0xC,0xD};

    memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

    InMessage.Return.UserID = 0;
    InMessage.Sequence      = EmetconProtocol::GetValue_TOUkWhReverse;

    test_Mct440_213xB test_dev;

    BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetValueTOUkWh(InMessage, timeNow, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    CtiMultiMsg_vec points = retMsg->PointData();

    BOOST_REQUIRE_EQUAL(points.size(), 4);

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   66051         );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // rate b

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   263430        );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // rate c

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   460809        );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);  // rate d

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   658188        );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    delete_container(vgList);
    delete_container(retList);
    delete_container(outList);
}


BOOST_AUTO_TEST_CASE(test_decodeGetValueTOUkWhFrozen)
{
    INMESS                          InMessage;
    CtiTime                         timeNow     (CtiDate(1, 1, 2011), 19, 19, 23);
    CtiTime                         timeFrozen  (CtiDate(1, 1, 2011), 19, 12, 45);
    CtiTime                         timeExpected(CtiDate(1, 1, 2011), 19, 12, 45);
    CtiDeviceBase::CtiMessageList   vgList;
    CtiDeviceBase::CtiMessageList   retList;
    CtiDeviceBase::OutMessageList   outList; // not use

    const unsigned char freeze_counter = 16;
    const unsigned char test_data[13]  = {0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9,0xA,0xB,0xC, freeze_counter};

    memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

    InMessage.Return.UserID = 0;
    InMessage.Sequence      = EmetconProtocol::GetValue_FrozenTOUkWh;

    test_Mct440_213xB test_dev;

    test_dev.updateFreezeInfo(freeze_counter, timeFrozen.seconds());

    BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetValueTOUkWh(InMessage, timeNow, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    CtiMultiMsg_vec points = retMsg->PointData();

    BOOST_REQUIRE_EQUAL(points.size(), 4);

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   0x010203      );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // rate b

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   0x040506      );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // rate c

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   0x070809      );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);  // rate d

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   0x0A0B0C      );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    delete_container(vgList);
    delete_container(retList);
    delete_container(outList);
}

BOOST_AUTO_TEST_CASE(test_decodeGetValueTOUkWhReverseFrozen)
{
    INMESS                          InMessage;
    CtiTime                         timeNow     (CtiDate(1, 1, 2011), 19, 19, 12);
    CtiTime                         timeFrozen  (CtiDate(1, 1, 2011), 18, 12, 34);
    CtiTime                         timeExpected(CtiDate(1, 1, 2011), 18, 12, 34);
    CtiDeviceBase::CtiMessageList   vgList;
    CtiDeviceBase::CtiMessageList   retList;
    CtiDeviceBase::OutMessageList   outList; // not use

    const unsigned char freeze_counter = 10;
    const unsigned char test_data[13]  = {0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9,0xA,0xB,0xC, freeze_counter};

    memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

    InMessage.Return.UserID = 0;
    InMessage.Sequence      = EmetconProtocol::GetValue_FrozenTOUkWhReverse;

    test_Mct440_213xB test_dev;

    test_dev.updateFreezeInfo(freeze_counter, timeFrozen.seconds());

    BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetValueTOUkWh(InMessage, timeNow, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    CtiMultiMsg_vec points = retMsg->PointData();

    BOOST_REQUIRE_EQUAL(points.size(), 4);

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   0x010203      );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // rate b

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   0x040506      );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // rate c

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   0x070809      );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);  // rate d

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(),   0x0A0B0C      );
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        BOOST_CHECK_EQUAL( pdata->getTime(),    timeExpected  );
    }

    delete_container(vgList);
    delete_container(retList);
    delete_container(outList);
}

BOOST_AUTO_TEST_CASE(test_decodeGetStatusDisconnect)
{
    INMESS                          InMessage;
    CtiTime                         t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)
    CtiDeviceBase::CtiMessageList   vgList;
    CtiDeviceBase::CtiMessageList   retList;
    CtiDeviceBase::OutMessageList   outList; // not use

    test_Mct440_213xB test_dev;

    {
        unsigned char test_data[] = {0x00,0x00,0x00};

        memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = EmetconProtocol::GetStatus_Disconnect;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0x1FE;

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetStatusDisconnect(InMessage, t, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        CtiMultiMsg_vec points = retMsg->PointData();

        BOOST_REQUIRE_EQUAL(points.size(), 1);

        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 1); // connected
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );

        delete retMsg;

        retList.pop_front();
    }

    {
        unsigned char test_data[] = {0x01,0x00,0x00};

        memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = EmetconProtocol::GetStatus_Disconnect;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0x1FE;

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetStatusDisconnect(InMessage, t, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        CtiMultiMsg_vec points = retMsg->PointData();

        BOOST_REQUIRE_EQUAL(points.size(), 1);

        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0); // disconnected
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );

        delete retMsg;

        retList.pop_front();
    }

    {
        unsigned char test_data[] = {0x6E,0x00,0x00};

        memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = EmetconProtocol::GetStatus_Disconnect;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0x1FE;

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetStatusDisconnect(InMessage, t, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        CtiMultiMsg_vec points = retMsg->PointData();

        BOOST_REQUIRE_EQUAL(points.size(), 1);

        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 1); // connected
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );

        delete retMsg;

        retList.pop_front();
    }

    {
        unsigned char test_data[] = {0x6F,0x00,0x00};

        memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = EmetconProtocol::GetStatus_Disconnect;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0x1FE;

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetStatusDisconnect(InMessage, t, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        CtiMultiMsg_vec points = retMsg->PointData();

        BOOST_REQUIRE_EQUAL(points.size(), 1);

        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0); // disconnected
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );

        delete retMsg;

        retList.pop_front();
    }

    delete_container(vgList);
    delete_container(retList);
    delete_container(outList);
}


struct executePutConfig_helper : resetGlobals_helper
{
    CtiRequestMsg           request;
    std::list<CtiMessage*>  vgList, retList;
    std::list<OUTMESS*>     outList;

    std::unique_ptr<CtiRequestMsg> pRequest;

    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    executePutConfig_helper() :
        pRequest( new CtiRequestMsg ),
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
    }

    void resetTestState()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
        vgList.clear();
        retList.clear();
        outList.clear();

        pRequest.reset( new CtiRequestMsg );
    }

    ~executePutConfig_helper()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
    }
};

BOOST_FIXTURE_TEST_SUITE(test_executeConfigs, executePutConfig_helper)

    BOOST_AUTO_TEST_CASE(test_executePutConfigTOU)
    {
        Cti::Test::test_DeviceConfig &test_cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture

        test_cfg.insertValue("sunday",   "SCHEDULE_1");
        test_cfg.insertValue("weekday",  "SCHEDULE_2");
        test_cfg.insertValue("saturday", "SCHEDULE_3");
        test_cfg.insertValue("holiday",  "SCHEDULE_4");

        char cfg_key[20];
        char cfg_val[10];

        for (int i=1; i<=4; i++)
        {
            for (int j=1; j<=9; j++)
            {
                sprintf(cfg_key, "schedule%itime%i",i,j);
                sprintf(cfg_val, "%i:%02i",j,i*5);

                test_cfg.insertValue(cfg_key, cfg_val);
            }
        }

        for (int i=1; i<=4; i++)
        {
            for (int j=0; j<=9; j++)
            {
                sprintf(cfg_key, "schedule%irate%i",i,j);

                int offset = (i-1) % 2;

                switch((j+offset)%4)
                {
                case 0:  test_cfg.insertValue(cfg_key, "A"); break;
                case 1:  test_cfg.insertValue(cfg_key, "B"); break;
                case 2:  test_cfg.insertValue(cfg_key, "C"); break;
                default: test_cfg.insertValue(cfg_key, "D"); break;
                }
            }
        }

        test_cfg.insertValue("defaultRate", "A");

        test_Mct440_213xB test_dev;

        CtiCommandParser parse("installvalue tou force");
        CtiOutMessage *outMessage = CTIDBG_new OUTMESS;

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.executePutConfigTOU(&request, parse, outMessage, vgList, retList, outList, false));

        delete outMessage;

        BOOST_CHECK_EQUAL(outList.size(), 4);

        {
            OUTMESS* outmsg = outList.front();

            std::vector<unsigned char> expected = boost::assign::list_of
                    (0xe0)(0x04)(0x0d)(0x0c)(0x0c)(0x0c)(0x0c)(0x94)(0xe4)(0x0e)(0x0c)(0x0c)(0x0c)(0x0c)(0x39);

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x30);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , EmetconProtocol::IO_Function_Write);

            const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
            const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                                 outmsg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS(
                    expected.begin(), expected.end(),
                    results_begin, results_end);

            delete outmsg;

            outList.pop_front();
        }

        {
            OUTMESS* outmsg = outList.front();

            std::vector<unsigned char> expected = boost::assign::list_of
                    (0x0f)(0x0c)(0x0c)(0x0c)(0x0c)(0x04)(0xe4)(0x10)(0x0c)(0x0c)(0x0c)(0x0c)(0x09)(0x39);

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x31);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , EmetconProtocol::IO_Function_Write);

            const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
            const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                                 outmsg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS(
                    expected.begin(), expected.end(),
                    results_begin, results_end);

            delete outmsg;

            outList.pop_front();
        }

        {
            OUTMESS* outmsg = outList.front();

            std::vector<unsigned char> expected = boost::assign::list_of
                    (0x0c)(0x0c)(0x0c)(0x0c)(0x4e)(0x0c)(0x0c)(0x0c)(0x0c)(0x93);

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x33);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , EmetconProtocol::IO_Function_Write);

            const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
            const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                                 outmsg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS(
                    expected.begin(), expected.end(),
                    results_begin, results_end);

            delete outmsg;

            outList.pop_front();
        }

        {
            OUTMESS* outmsg = outList.front();

            std::vector<unsigned char> expected = boost::assign::list_of
                    (0x0c)(0x0c)(0x0c)(0x0c)(0x4e)(0x0c)(0x0c)(0x0c)(0x0c)(0x93);

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x34);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , EmetconProtocol::IO_Function_Write);

            const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
            const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                                 outmsg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS(
                    expected.begin(), expected.end(),
                    results_begin, results_end);

            delete outmsg;

            outList.pop_front();
        }
    }

    BOOST_AUTO_TEST_CASE(test_executeGetConfigHoliday)
    {
        test_Mct440_213xB test_dev;

        unsigned commandSequence;

        {
            CtiCommandParser parse("getconfig holiday");

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

            BOOST_CHECK( retList.empty() );
            BOOST_CHECK( vgList.empty() );
            BOOST_REQUIRE_EQUAL( outList.size(), 1 );

            CtiOutMessage *outmsg = outList.front();

            BOOST_REQUIRE( outmsg );

            commandSequence = outmsg->Sequence;  //  for command tracking - must be copied to inmessage

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x01);  //  read
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12);
        }

        delete_container(outList);
        outList.clear();

        {
            INMESS  InMessage;
            CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);

            unsigned char test_data[] = {0x1,0x1,0x1,0x2,0x1,0x3,0x1,0x4,0x1,0x5,0x1,0x6};  //  leave as 12 bytes, as if we're reading queued

            memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
            InMessage.Buffer.DSt.Length = sizeof(test_data);

            InMessage.Return.UserID                        = 0;
            InMessage.Sequence                             = commandSequence;
            InMessage.Return.ProtocolInfo.Emetcon.Function = 0xd0;
            InMessage.Return.ProtocolInfo.Emetcon.IO       = 1;

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.ResultDecode(InMessage, t, vgList, retList, outList));

            BOOST_REQUIRE_EQUAL(retList.size(), 1);

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            const std::string expected =
                "Test MCT-440-213xB / Holiday schedule:\n"
                "Holiday 1: 09/15/2010\n"
                "Holiday 2: 09/16/2010\n"
                "Holiday 3: 09/17/2010\n"
                "Holiday 4: 09/18/2010\n"
                "Holiday 5: 09/19/2010\n"
                "Holiday 6: 09/20/2010\n";

            BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

            BOOST_CHECK_EQUAL( outList.size(), 1 );

            CtiOutMessage *outmsg = outList.front();

            BOOST_REQUIRE( outmsg );

            commandSequence = outmsg->Sequence;  //  for command tracking - must be copied to inmessage

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d1);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x01);  //  read
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12);
        }

        delete_container(outList);
        delete_container(retList);
        outList.clear();
        retList.clear();

        {
            INMESS  InMessage;
            CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);

            unsigned char test_data[] = {0x1,0x1,0x1,0x2,0x1,0x3,0x1,0x4,0x1,0x5,0x1,0x6,0x01};  //  round out to 13 bytes (3 D words)

            memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
            InMessage.Buffer.DSt.Length = sizeof(test_data);

            InMessage.Return.UserID                        = 0;
            InMessage.Sequence                             = commandSequence;
            InMessage.Return.ProtocolInfo.Emetcon.Function = 0xd1;
            InMessage.Return.ProtocolInfo.Emetcon.IO       = 1;

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.ResultDecode(InMessage, t, vgList, retList, outList));

            BOOST_REQUIRE_EQUAL(retList.size(), 1);

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            const std::string expected =
                "Test MCT-440-213xB / Holiday schedule:\n"
                "Holiday 7: 09/15/2010\n"
                "Holiday 8: 09/16/2010\n"
                "Holiday 9: 09/17/2010\n"
                "Holiday 10: 09/18/2010\n"
                "Holiday 11: 09/19/2010\n"
                "Holiday 12: 09/20/2010\n";

            BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

            BOOST_REQUIRE_EQUAL( outList.size(), 1 );

            CtiOutMessage *outmsg = outList.front();

            BOOST_REQUIRE( outmsg );

            commandSequence = outmsg->Sequence;  //  for command tracking - must be copied to inmessage

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d2);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x01);  //  read
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 6);
        }

        delete_container(outList);
        delete_container(retList);
        outList.clear();
        retList.clear();

        {
            INMESS  InMessage;
            CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);

            unsigned char test_data[] = {0x1,0x1,0x1,0x2,0x1,0x3,0x01,0x04};  //  round out to 8 bytes (2 D words)

            memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
            InMessage.Buffer.DSt.Length = sizeof(test_data);

            InMessage.Return.UserID                        = 0;
            InMessage.Sequence                             = commandSequence;
            InMessage.Return.ProtocolInfo.Emetcon.Function = 0xd2;
            InMessage.Return.ProtocolInfo.Emetcon.IO       = 1;

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.ResultDecode(InMessage, t, vgList, retList, outList));

            BOOST_REQUIRE_EQUAL(retList.size(), 1);

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            const std::string expected =
                "Test MCT-440-213xB / Holiday schedule:\n"
                "Holiday 13: 09/15/2010\n"
                "Holiday 14: 09/16/2010\n"
                "Holiday 15: 09/17/2010\n";

            BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

            BOOST_CHECK( outList.empty() );
        }
    }

    BOOST_AUTO_TEST_CASE(test_executeGetConfigHoliday_missing_payload)
    {
        test_Mct440_213xB test_dev;

        unsigned commandSequence;

        {
            CtiCommandParser parse("getconfig holiday");

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

            BOOST_CHECK( retList.empty() );
            BOOST_CHECK( vgList.empty() );
            BOOST_REQUIRE_EQUAL( outList.size(), 1 );

            CtiOutMessage *outmsg = outList.front();

            BOOST_REQUIRE( outmsg );

            commandSequence = outmsg->Sequence;  //  for command tracking - must be copied to inmessage

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x01);  //  read
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12);
        }

        delete_container(outList);
        outList.clear();

        {
            INMESS  InMessage;
            CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);

            unsigned char test_data[] = {0x1,0x1,0x1};  //  3 bytes - we were expecting 12

            memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
            InMessage.Buffer.DSt.Length = sizeof(test_data);

            InMessage.Return.UserID                        = 0;
            InMessage.Sequence                             = commandSequence;
            InMessage.Return.ProtocolInfo.Emetcon.Function = 0xd0;
            InMessage.Return.ProtocolInfo.Emetcon.IO       = 1;

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.ResultDecode(InMessage, t, vgList, retList, outList));

            BOOST_CHECK( outList.empty() );

            BOOST_REQUIRE_EQUAL(retList.size(), 1);

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            const std::string expected =
                "Test MCT-440-213xB / Payload too small (3 received, 12 required)";

            BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::DataMissing );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );
        }
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigHoliday_empty)
    {
        //  Override the current time to be July 2, 2013, at 12:34:56
        Cti::Test::Override_CtiTime_Now guard(CtiTime(CtiDate(2, 7, 2013), 12, 34, 56));

        test_Mct440_213xB test_dev;

        CtiCommandParser parse("putconfig emetcon holiday");

        BOOST_CHECK_EQUAL(ClientErrors::NoMethod, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( outList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( retMsg );

        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::NoMethod );
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigHoliday_1_holiday_no_decode)
    {
        //  Override the current time to be July 2, 2013, at 12:34:56
        Cti::Test::Override_CtiTime_Now guard(CtiTime(CtiDate(2, 7, 2013), 12, 34, 56));

        test_Mct440_213xB test_dev;

        CtiCommandParser parse("putconfig emetcon holiday 07/05/2013");

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( retList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = boost::assign::list_of
                (0x07);

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x00);  //  write
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());

        const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
        const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                             outmsg->Buffer.BSt.Length;

        BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results_begin, results_end);
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigHoliday_2_holidays_no_decode)  //  also out of order
    {
        //  Override the current time to be July 2, 2013, at 12:34:56
        Cti::Test::Override_CtiTime_Now guard(CtiTime(CtiDate(2, 7, 2013), 12, 34, 56));

        test_Mct440_213xB test_dev;

        CtiCommandParser parse("putconfig emetcon holiday 07/10/2013 07/05/2013");

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( retList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = boost::assign::list_of
                (0x07)(0x04);

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x00);  //  write
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());

        const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
        const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                             outmsg->Buffer.BSt.Length;

        BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results_begin, results_end);
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigHoliday_15_holidays_no_decode)
    {
        //  Override the current time to be July 2, 2013, at 12:34:56
        Cti::Test::Override_CtiTime_Now guard(CtiTime(CtiDate(2, 7, 2013), 12, 34, 56));

        test_Mct440_213xB test_dev;

        CtiCommandParser parse("putconfig emetcon holiday "
                               "07/04/2013 08/08/2013 09/02/2013 10/14/2013 11/11/2013 "
                               "11/28/2013 11/29/2013 12/24/2013 12/25/2013 12/31/2013 "
                               "1/1/2014 1/20/2014 2/17/2014 4/20/2014 5/26/2014");

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( retList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = boost::assign::list_of
                (0x05)(0x22)(0x4c)(0x6a)(0x03)(0x01)(0x60)(0x00)(0x05)(0x80)(0x64)(0xd3)(0x1b)(0x01);

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x00);  //  write
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());

        const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
        const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                             outmsg->Buffer.BSt.Length;

        BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results_begin, results_end);
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigHoliday_15_holidays_large_gap_no_decode)
    {
        //  Override the current time to be July 2, 2013, at 12:34:56
        Cti::Test::Override_CtiTime_Now guard(CtiTime(CtiDate(2, 7, 2013), 12, 34, 56));

        test_Mct440_213xB test_dev;

        CtiCommandParser parse("putconfig emetcon holiday "
                               "07/04/2013 "
                               "07/05/2013 "
                               "07/06/2013 "
                               "07/07/2013 "
                               "07/08/2013 "
                               "07/09/2013 "
                               "07/10/2013 "
                               "07/11/2013 "
                               "07/12/2013 "
                               "07/13/2013 "
                               "07/14/2013 "
                               "07/15/2013 "
                               "07/16/2013 "
                               "07/17/2013 "
                               "06/30/2014");

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( retList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = boost::assign::list_of
            (0x05)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0xf8)(0x7f)(0x01);

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x00);  //  write
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());

        const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
        const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                             outmsg->Buffer.BSt.Length;

        BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results_begin, results_end);
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigHoliday_15_holidays_large_gaps_no_decode)
    {
        //  Override the current time to be July 2, 2013, at 12:34:56
        Cti::Test::Override_CtiTime_Now guard(CtiTime(CtiDate(2, 7, 2013), 12, 34, 56));

        test_Mct440_213xB test_dev;

        CtiCommandParser parse("putconfig emetcon holiday "
                               "07/04/2013 "
                               "12/05/2013 "
                               "12/06/2013 "
                               "12/07/2013 "
                               "12/08/2013 "
                               "12/09/2013 "
                               "12/10/2013 "
                               "12/11/2013 "
                               "12/12/2013 "
                               "12/13/2013 "
                               "12/14/2013 "
                               "12/15/2013 "
                               "12/16/2013 "
                               "12/17/2013 "
                               "06/30/2014");

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( retList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = boost::assign::list_of
            (0x05)(0xfe)(0x0d)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0xf8)(0x89);

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x00);  //  write
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());

        const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
        const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                             outmsg->Buffer.BSt.Length;

        BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results_begin, results_end);
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigHoliday_bunched_at_end_of_year_no_decode)
    {
        //  Override the current time to be July 2, 2013, at 12:34:56
        Cti::Test::Override_CtiTime_Now guard(CtiTime(CtiDate(2, 7, 2013), 12, 34, 56));

        test_Mct440_213xB test_dev;

        CtiCommandParser parse("putconfig emetcon holiday "
                               "06/16/2014 "
                               "06/17/2014 "
                               "06/18/2014 "
                               "06/19/2014 "
                               "06/20/2014 "
                               "06/21/2014 "
                               "06/22/2014 "
                               "06/23/2014 "
                               "06/24/2014 "
                               "06/25/2014 "
                               "06/26/2014 "
                               "06/27/2014 "
                               "06/28/2014 "
                               "06/29/2014 "
                               "06/30/2014");

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( retList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        CtiOutMessage *outmsg = outList.front();

        BOOST_REQUIRE( outmsg );

        std::vector<unsigned char> expected = boost::assign::list_of
            (0xff)(0x61)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00)(0x00);

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x00);  //  write
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());

        const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
        const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                             outmsg->Buffer.BSt.Length;

        BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results_begin, results_end);
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigHoliday_15_holidays_invalid_holiday)
    {
        //  Override the current time to be July 2, 2013, at 12:34:56
        Cti::Test::Override_CtiTime_Now guard(CtiTime(CtiDate(2, 7, 2013), 12, 34, 56));

        test_Mct440_213xB test_dev;

        CtiCommandParser parse("putconfig emetcon holiday "
                               "07/04/2013 08/08/2013 09/02/2013 10/14/2013 11/11/2013 "
                               "11/28/2013 11/29/2013 12/24/2013 12/25/2013 12/31/2013 "
                               "1/1/2014 1/20/2014 2/17/2014 4/20/2014 5/26/2013");

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK( outList.empty() );
        BOOST_CHECK( vgList.empty() );
        BOOST_REQUIRE_EQUAL( retList.size(), 1 );

        CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE( retMsg );

        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::BadParameter );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), "Test MCT-440-213xB / Invalid holiday (05/26/2013), must be after 07/02/2013 and before 07/02/2014");
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigHoliday_15_holidays_plus_decode)
    {
        //  Override the current time to be July 2, 2013, at 12:34:56
        Cti::Test::Override_CtiTime_Now guard(CtiTime(CtiDate(2, 7, 2013), 12, 34, 56));

        test_Mct440_213xB test_dev;

        unsigned commandSequence;

        {
            CtiCommandParser parse("putconfig emetcon holiday "
                                   "07/04/2013 08/08/2013 09/02/2013 10/14/2013 11/11/2013 "
                                   "11/28/2013 11/29/2013 12/24/2013 12/25/2013 12/31/2013 "
                                   "1/1/2014 1/20/2014 2/17/2014 4/20/2014 5/26/2014");

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

            BOOST_CHECK( retList.empty() );
            BOOST_CHECK( vgList.empty() );
            BOOST_REQUIRE_EQUAL( outList.size(), 1 );

            CtiOutMessage *outmsg = outList.front();

            BOOST_REQUIRE( outmsg );

            commandSequence = outmsg->Sequence;  //  for command tracking - must be copied to inmessage

            std::vector<unsigned char> expected = boost::assign::list_of
                    (0x05)(0x22)(0x4c)(0x6a)(0x03)(0x01)(0x60)(0x00)(0x05)(0x80)(0x64)(0xd3)(0x1b)(0x01);

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x00);  //  write
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size());

            const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
            const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                                 outmsg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS(
                    expected.begin(), expected.end(),
                    results_begin, results_end);
        }

        delete_container(outList);
        outList.clear();

        {
            INMESS  InMessage;
            CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);

            InMessage.Return.UserID                        = 0;
            InMessage.Sequence                             = commandSequence;
            InMessage.Return.ProtocolInfo.Emetcon.Function = 0xd0;
            InMessage.Return.ProtocolInfo.Emetcon.IO       = 0;

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.ResultDecode(InMessage, t, vgList, retList, outList));

            BOOST_CHECK( vgList.empty() );
            BOOST_CHECK( retList.empty() );

            BOOST_REQUIRE_EQUAL( outList.size(), 1 );

            CtiOutMessage *outmsg = outList.front();

            BOOST_REQUIRE( outmsg );

            commandSequence = outmsg->Sequence;  //  for command tracking - must be copied to inmessage

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x01);  //  read
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12);
        }

        delete_container(outList);
        delete_container(retList);
        outList.clear();
        retList.clear();

        {
            INMESS  InMessage;
            CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);

            unsigned char test_data[] = {0x1,0x1,0x1,0x2,0x1,0x3,0x1,0x4,0x1,0x5,0x1,0x6};

            memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
            InMessage.Buffer.DSt.Length = sizeof(test_data);

            InMessage.Return.UserID                        = 0;
            InMessage.Sequence                             = commandSequence;
            InMessage.Return.ProtocolInfo.Emetcon.Function = 0xd0;
            InMessage.Return.ProtocolInfo.Emetcon.IO       = 1;

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.ResultDecode(InMessage, t, vgList, retList, outList));

            BOOST_REQUIRE_EQUAL(retList.size(), 1);

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            const std::string expected =
                "Test MCT-440-213xB / Holiday schedule:\n"
                "Holiday 1: 09/15/2010\n"
                "Holiday 2: 09/16/2010\n"
                "Holiday 3: 09/17/2010\n"
                "Holiday 4: 09/18/2010\n"
                "Holiday 5: 09/19/2010\n"
                "Holiday 6: 09/20/2010\n";

            BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

            BOOST_CHECK_EQUAL( outList.size(), 1 );

            CtiOutMessage *outmsg = outList.front();

            BOOST_REQUIRE( outmsg );

            commandSequence = outmsg->Sequence;  //  for command tracking - must be copied to inmessage

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d1);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x01);  //  read
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12);
        }

        delete_container(outList);
        delete_container(retList);
        outList.clear();
        retList.clear();

        {
            INMESS  InMessage;
            CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);

            unsigned char test_data[] = {0x1,0x1,0x1,0x2,0x1,0x3,0x1,0x4,0x1,0x5,0x1,0x6};

            memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
            InMessage.Buffer.DSt.Length = sizeof(test_data);

            InMessage.Return.UserID                        = 0;
            InMessage.Sequence                             = commandSequence;
            InMessage.Return.ProtocolInfo.Emetcon.Function = 0xd1;
            InMessage.Return.ProtocolInfo.Emetcon.IO       = 1;

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.ResultDecode(InMessage, t, vgList, retList, outList));

            BOOST_REQUIRE_EQUAL(retList.size(), 1);

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            const std::string expected =
                "Test MCT-440-213xB / Holiday schedule:\n"
                "Holiday 7: 09/15/2010\n"
                "Holiday 8: 09/16/2010\n"
                "Holiday 9: 09/17/2010\n"
                "Holiday 10: 09/18/2010\n"
                "Holiday 11: 09/19/2010\n"
                "Holiday 12: 09/20/2010\n";

            BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

            BOOST_REQUIRE_EQUAL( outList.size(), 1 );

            CtiOutMessage *outmsg = outList.front();

            BOOST_REQUIRE( outmsg );

            commandSequence = outmsg->Sequence;  //  for command tracking - must be copied to inmessage

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d2);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , 0x01);  //  read
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 6);
        }

        delete_container(outList);
        delete_container(retList);
        outList.clear();
        retList.clear();

        {
            INMESS  InMessage;
            CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);

            unsigned char test_data[] = {0x1,0x1,0x1,0x2,0x1,0x3};

            memcpy(InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));
            InMessage.Buffer.DSt.Length = sizeof(test_data);

            InMessage.Return.UserID                        = 0;
            InMessage.Sequence                             = commandSequence;
            InMessage.Return.ProtocolInfo.Emetcon.Function = 0xd2;
            InMessage.Return.ProtocolInfo.Emetcon.IO       = 1;

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.ResultDecode(InMessage, t, vgList, retList, outList));

            BOOST_REQUIRE_EQUAL(retList.size(), 1);

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            const std::string expected =
                "Test MCT-440-213xB / Holiday schedule:\n"
                "Holiday 13: 09/15/2010\n"
                "Holiday 14: 09/16/2010\n"
                "Holiday 15: 09/17/2010\n";

            BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
            BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

            BOOST_CHECK( outList.empty() );
        }
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigTouDays)
    {
        test_Mct440_213xB test_dev;

        {
            CtiCommandParser parse("putconfig emetcon tou 1234 "
                                   "schedule 1 a/00:00 b/01:00 c/01:05 d/01:10 a/01:15 b/01:20 c/01:25 d/01:30 a/01:35 b/01:40 "
                                   "schedule 2 b/00:00 c/02:00 d/02:05 a/02:10 b/02:15 c/02:20 d/02:25 a/02:30 b/02:35 c/02:40 "
                                   "schedule 3 c/00:00 d/03:00 a/03:05 b/03:10 c/03:15 d/03:20 a/03:25 b/03:30 c/03:35 d/03:40 "
                                   "schedule 4 d/00:00 a/04:00 b/04:05 c/04:10 d/04:15 a/04:20 b/04:25 c/04:30 d/04:35 a/04:40");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outList.size(), 4);
        }

        {
            OUTMESS* outmsg = outList.front();

            BOOST_CHECK_EQUAL(outmsg->Sequence, Cti::Protocols::EmetconProtocol::PutConfig_TOU);

            std::vector<unsigned char> expected = boost::assign::list_of
                    (0xe0)(0x04)(0x0c)(0x01)(0x01)(0x01)(0x01)(0x94)(0xe4)(0x18)(0x01)(0x01)(0x01)(0x01)(0x39);

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x030           );
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size() );

            const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
            const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                                 outmsg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS(
                    expected.begin(), expected.end(),
                    results_begin, results_end);

            delete outmsg;

            outList.pop_front();
        }

        {
            OUTMESS* outmsg = outList.front();

            BOOST_CHECK_EQUAL(outmsg->Sequence, Cti::Protocols::EmetconProtocol::PutConfig_TOU);

            std::vector<unsigned char> expected = boost::assign::list_of
                    (0x24)(0x01)(0x01)(0x01)(0x01)(0x0e)(0x4e)(0x30)(0x01)(0x01)(0x01)(0x01)(0x03)(0x93);

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x031           );
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size() );

            const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
            const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                                 outmsg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS(
                    expected.begin(), expected.end(),
                    results_begin, results_end);

            delete outmsg;

            outList.pop_front();
        }

        {
            OUTMESS* outmsg = outList.front();

            BOOST_CHECK_EQUAL(outmsg->Sequence, Cti::Protocols::EmetconProtocol::PutConfig_TOU);

            std::vector<unsigned char> expected = boost::assign::list_of
                    (0x01)(0x01)(0x01)(0x01)(0x4e)(0x01)(0x01)(0x01)(0x01)(0x93);

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x033           );
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size() );

            const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
            const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                                 outmsg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS(
                    expected.begin(), expected.end(),
                    results_begin, results_end);

            delete outmsg;

            outList.pop_front();
        }

        {
            OUTMESS* outmsg = outList.front();

            BOOST_CHECK_EQUAL(outmsg->Sequence, Cti::Protocols::EmetconProtocol::PutConfig_TOU);

            std::vector<unsigned char> expected = boost::assign::list_of
                    (0x01)(0x01)(0x01)(0x01)(0xe4)(0x01)(0x01)(0x01)(0x01)(0x39);

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x034           );
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size() );

            const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
            const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                                 outmsg->Buffer.BSt.Length;

            BOOST_CHECK_EQUAL_COLLECTIONS(
                    expected.begin(), expected.end(),
                    results_begin, results_end);

            delete outmsg;

            outList.pop_front();
        }

    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigPhaseLossThreshold)
    {
        test_Mct440_213xB test_dev;

        CtiCommandParser parse("putconfig emetcon phaseloss threshold 65 duration 10:30:45");

        CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

        BOOST_CHECK_EQUAL(outmsg->Sequence, Cti::Protocols::EmetconProtocol::PutConfig_PhaseLossThreshold);

        std::vector<unsigned char> expected = boost::assign::list_of
                (0x41)(0x93)(0xd5);

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x01E           );
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , expected.size() );

        const unsigned char *results_begin = outmsg->Buffer.BSt.Message;
        const unsigned char *results_end   = outmsg->Buffer.BSt.Message +
                                             outmsg->Buffer.BSt.Length;

        BOOST_CHECK_EQUAL_COLLECTIONS(
                expected.begin(), expected.end(),
                results_begin, results_end);

        delete outmsg;
    }

    BOOST_AUTO_TEST_CASE(test_executeControlTouHolidayRateUpdate)
    {
        test_Mct440_213xB test_dev;

        CtiCommandParser parse("putstatus emetcon set tou holiday rate");

        CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.executePutStatus(&request, parse, outmsg, vgList, retList, outList));

        BOOST_CHECK_EQUAL(outmsg->Sequence, Cti::Protocols::EmetconProtocol::PutStatus_SetTOUHolidayRate);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0A4  );
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 0      );

        delete outmsg;
    }

    BOOST_AUTO_TEST_CASE(test_executeControlTouHolidayRateReset)
    {
        test_Mct440_213xB test_dev;

        CtiCommandParser parse("putstatus emetcon clear tou holiday rate");

        CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.executePutStatus(&request, parse, outmsg, vgList, retList, outList));

        BOOST_CHECK_EQUAL(outmsg->Sequence, Cti::Protocols::EmetconProtocol::PutStatus_ClearTOUHolidayRate);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0A5  );
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 0      );

        delete outmsg;
    }

    BOOST_AUTO_TEST_CASE(test_putconfig_install_all)
    {
        Cti::Test::test_DeviceConfig &test_cfg = *fixtureConfig;  //  get a reference to the shared_ptr in the fixture
        test_Mct440_213xB_route test_dev;

        Cti::ConnectionHandle connHandle{ 9999 };

        {
            ////// empty configuration (no valid configuration) //////

            CtiCommandParser parse("putconfig install all");

            pRequest->setConnectionHandle(connHandle);

            BOOST_CHECK_EQUAL( ClientErrors::None, test_dev.beginExecuteRequest(pRequest.get(), parse, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( vgList.size(),  0 );
            BOOST_CHECK_EQUAL( outList.size(), 0 );
            BOOST_CHECK_EQUAL( retList.size(), 7 );

            std::vector<bool> expectMoreRcv;
            boost::range::transform(retList, std::back_inserter(expectMoreRcv), [](const CtiMessage *msg) { return static_cast<const CtiReturnMsg *>(msg)->ExpectMore(); });

            const std::vector<bool> expectMoreExp = boost::assign::list_of
                    (true)(true)(true)(true)(true)(true)(false); // 7 error messages

            BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                           expectMoreExp.begin() , expectMoreExp.end() );
        }

        // add TOU config
        test_cfg.insertValue("sunday",   "SCHEDULE_1");
        test_cfg.insertValue("weekday",  "SCHEDULE_2");
        test_cfg.insertValue("saturday", "SCHEDULE_3");
        test_cfg.insertValue("holiday",  "SCHEDULE_4");

        char cfg_key[20];
        char cfg_val[10];

        for (int i=1; i<=4; i++)
        {
            for (int j=1; j<=9; j++)
            {
                sprintf(cfg_key, "schedule%itime%i",i,j);
                sprintf(cfg_val, "%i:%02i",j,i*5);

                test_cfg.insertValue(cfg_key, cfg_val);
            }
        }

        for (int i=1; i<=4; i++)
        {
            for (int j=0; j<=9; j++)
            {
                sprintf(cfg_key, "schedule%irate%i",i,j);

                int offset = (i-1) % 2;

                switch((j+offset)%4)
                {
                case 0:  test_cfg.insertValue(cfg_key, "A"); break;
                case 1:  test_cfg.insertValue(cfg_key, "B"); break;
                case 2:  test_cfg.insertValue(cfg_key, "C"); break;
                default: test_cfg.insertValue(cfg_key, "D"); break;
                }
            }
        }

        test_cfg.insertValue("defaultRate", "A");

        {
            ////// 1 valid configuration //////

            resetTestState();

            CtiCommandParser parse("putconfig install all");

            pRequest->setConnectionHandle(connHandle);

            BOOST_CHECK_EQUAL( ClientErrors::None, test_dev.beginExecuteRequest(pRequest.get(), parse, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( vgList.size(),  0 );
            BOOST_CHECK_EQUAL( outList.size(), 4 );
            BOOST_CHECK_EQUAL( retList.size(), 10 );

            std::vector<bool> expectMoreRcv;
            boost::range::transform(retList, std::back_inserter(expectMoreRcv), [](const CtiMessage *msg) { return static_cast<const CtiReturnMsg *>(msg)->ExpectMore(); });

            const std::vector<bool> expectMoreExp = boost::assign::list_of
                    (true)(true)(true)(true)(true)(true)(true)(true)(true)(true); // 5 error messages + 4 message sent on route

            BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                           expectMoreExp.begin() , expectMoreExp.end() );
        }

        // add DST config
        test_cfg.insertValue( MCTStrings::EnableDst, "true" );

        {
            ////// 2 valid configuration //////

            resetTestState();

            CtiCommandParser parse("putconfig install all");

            pRequest->setConnectionHandle(connHandle);

            BOOST_CHECK_EQUAL( ClientErrors::None, test_dev.beginExecuteRequest(pRequest.get(), parse, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( vgList.size(),  0 );
            BOOST_CHECK_EQUAL( outList.size(), 5 );
            BOOST_CHECK_EQUAL( retList.size(), 10 );

            std::vector<bool> expectMoreRcv;
            boost::range::transform(retList, std::back_inserter(expectMoreRcv), [](const CtiMessage *msg) { return static_cast<const CtiReturnMsg *>(msg)->ExpectMore(); });

            const std::vector<bool> expectMoreExp = boost::assign::list_of
                    (true)(true)(true)(true)(true)(true)(true)(true)(true)(true); // 4 error messages + 5 message sent on route

            BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                           expectMoreExp.begin() , expectMoreExp.end() );
        }

        // add Timezone config
        test_cfg.insertValue( MCTStrings::TimeZoneOffset, "NORONHA" );

        {
            ////// 3 valid configuration //////

            resetTestState();

            CtiCommandParser parse("putconfig install all");

            pRequest->setConnectionHandle(connHandle);

            BOOST_CHECK_EQUAL( ClientErrors::None, test_dev.beginExecuteRequest(pRequest.get(), parse, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( vgList.size(),  0 );
            BOOST_CHECK_EQUAL( outList.size(), 7 );
            BOOST_CHECK_EQUAL( retList.size(), 11 );

            std::vector<bool> expectMoreRcv;
            boost::range::transform(retList, std::back_inserter(expectMoreRcv), [](const CtiMessage *msg) { return static_cast<const CtiReturnMsg *>(msg)->ExpectMore(); });

            const std::vector<bool> expectMoreExp = boost::assign::list_of
                    (true)(true)(true)(true)(true)(true)(true)(true)(true)(true)(true); // 3 error messages + 7 message sent on route

            BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                           expectMoreExp.begin() , expectMoreExp.end() );
        }

        // add time adjust tolerance config
        test_cfg.insertValue( MCTStrings::TimeAdjustTolerance, "128" );

        {
            ////// 4 valid configuration //////

            resetTestState();

            CtiCommandParser parse("putconfig install all");

            pRequest->setConnectionHandle(connHandle);

            BOOST_CHECK_EQUAL( ClientErrors::None, test_dev.beginExecuteRequest(pRequest.get(), parse, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( vgList.size(),  0 );
            BOOST_CHECK_EQUAL( outList.size(), 8 );
            BOOST_CHECK_EQUAL( retList.size(), 11 );

            std::vector<bool> expectMoreRcv;
            boost::range::transform(retList, std::back_inserter(expectMoreRcv), [](const CtiMessage *msg) { return static_cast<const CtiReturnMsg *>(msg)->ExpectMore(); });

            const std::vector<bool> expectMoreExp = boost::assign::list_of
                    (true)(true)(true)(true)(true)(true)(true)(true)(true)(true)(true); // 2 error messages + 8 messages sent on route

            BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                           expectMoreExp.begin() , expectMoreExp.end() );
        }

        // add addressing config
        test_cfg.insertValue( MCTStrings::Bronze,            "1" );
        test_cfg.insertValue( MCTStrings::Lead,              "2" );
        test_cfg.insertValue( MCTStrings::Collection,        "3" );
        test_cfg.insertValue( MCTStrings::ServiceProviderID, "4" );

        {
            ////// 5 valid configuration //////

            resetTestState();

            CtiCommandParser parse("putconfig install all");

            pRequest->setConnectionHandle(connHandle);

            BOOST_CHECK_EQUAL( ClientErrors::None, test_dev.beginExecuteRequest(pRequest.get(), parse, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( vgList.size(),  0 );
            BOOST_CHECK_EQUAL( outList.size(), 9 );
            BOOST_CHECK_EQUAL( retList.size(), 11 );

            std::vector<bool> expectMoreRcv;
            boost::range::transform(retList, std::back_inserter(expectMoreRcv), [](const CtiMessage *msg) { return static_cast<const CtiReturnMsg *>(msg)->ExpectMore(); });

            const std::vector<bool> expectMoreExp = boost::assign::list_of
                    (true)(true)(true)(true)(true)(true)(true)(true)(true)(true)(true); // 1 error messages + 9 message sent on route

            BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                           expectMoreExp.begin() , expectMoreExp.end() );
        }

        // add phaseloss config
        test_cfg.insertValue( MCTStrings::PhaseLossThreshold, "50" );
        test_cfg.insertValue( MCTStrings::PhaseLossDuration,  "123" );

        {
            ////// 6 valid configuration //////

            resetTestState();

            CtiCommandParser parse("putconfig install all");

            pRequest->setConnectionHandle(connHandle);

            BOOST_CHECK_EQUAL( ClientErrors::None, test_dev.beginExecuteRequest(pRequest.get(), parse, vgList, retList, outList) );

            BOOST_CHECK_EQUAL( vgList.size(),  0 );
            BOOST_CHECK_EQUAL( outList.size(), 10 );
            BOOST_CHECK_EQUAL( retList.size(), 10 );

            std::vector<bool> expectMoreRcv;
            boost::range::transform(retList, std::back_inserter(expectMoreRcv), [](const CtiMessage *msg) { return static_cast<const CtiReturnMsg *>(msg)->ExpectMore(); });

            const std::vector<bool> expectMoreExp = boost::assign::list_of
                    (true)(true)(true)(true)(true)(true)(true)(true)(true)(true); // 10 message sent on route

            BOOST_CHECK_EQUAL_COLLECTIONS( expectMoreRcv.begin() , expectMoreRcv.end() ,
                                           expectMoreExp.begin() , expectMoreExp.end() );
        }
    }

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


BOOST_AUTO_TEST_CASE(test_executeGetStatusEventLog)
{
    CtiRequestMsg                   request;
    CtiDeviceBase::CtiMessageList   vgList;
    CtiDeviceBase::CtiMessageList   retList;
    CtiDeviceBase::OutMessageList   outList;

    test_Mct440_213xB test_dev;

    CtiCommandParser parse("getstatus eventlog");

    CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

    BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.executeGetStatus(&request, parse, outmsg, vgList, retList, outList));

    BOOST_CHECK_EQUAL(outList.size(), 10);

    size_t offset = 0;
    for( const auto outmsg : outList )
    {
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(outmsg->Sequence,            Cti::Protocols::EmetconProtocol::GetStatus_EventLog);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x50 + offset++);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length,   10);
    }

    delete_container(outList);
}


BOOST_AUTO_TEST_CASE(test_decodeGetStatusEventLog)
{
    INMESS                          InMessage;
    CtiTime                         t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)
    CtiDeviceBase::CtiMessageList   vgList;
    CtiDeviceBase::CtiMessageList   retList;
    CtiDeviceBase::OutMessageList   outList; // not use

    test_Mct440_213xB test_dev;

    {
        // -- test no event (event code 0x0) --
        unsigned char test_data[10] = {0x01, 0x02, 0x03, 0x04, 0x0A, 0x05, 0x00, 0x00, 0x00, 0x00};
        memcpy( InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = Cti::Protocols::EmetconProtocol::GetStatus_EventLog;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0x50; // between 0x50 and 0x59

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetStatusEventLog(InMessage, t, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        string expected =
        "Test MCT-440-213xB / Parameters Change:\n"
        "Time: " + CtiTime(0x01020304).asString() + "\n"
        "User ID: 2565\n"
        "Event: No Event\n";

        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

        delete retMsg;

        retList.pop_front();
    }

    {
        // -- test primary power up (event code 0x0) due to power failure --
        unsigned char test_data[10] = {0x10, 0x20, 0x30, 0x40, 0x00, 0x05, 0x00, 0x02, 0x00, 0x03};
        memcpy( InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = Cti::Protocols::EmetconProtocol::GetStatus_EventLog;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0x59; // between 0x50 and 0x59

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetStatusEventLog(InMessage, t, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        string expected =
        "Test MCT-440-213xB / Parameters Change:\n"
        "Time: " + CtiTime(0x10203040).asString() + "\n"
        "User ID: 5\n"
        "Event: Primary Power Up\n"
        "Reset Cause: Power failure\n";

        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

        delete retMsg;

        retList.pop_front();
    }

    {
        // -- test Disconnect Switch (event code 50 ) -- format 2
        unsigned char test_data[10] = {0x11, 0x22, 0x33, 0x44, 0x02, 0x03, 0x00, 0x32, 0xEC, 0x4D};
        memcpy( InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = Cti::Protocols::EmetconProtocol::GetStatus_EventLog;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0x55; // between 0x50 and 0x59

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetStatusEventLog(InMessage, t, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        string expected =
        "Test MCT-440-213xB / Parameters Change:\n"
        "Time: " + CtiTime(0x11223344).asString() + "\n"
        "User ID: 515\n"
        "Event: Disconnect Switch Error Detected\n"
        "Internal state: opened\n"
        "External state: closed\n"
        "Phase A RMS voltage: <= 195 V RMS\n"
        "Phase B RMS voltage: <= 235 V RMS\n"
        "Phase C RMS voltage: <= 245 V RMS\n";

        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

        delete retMsg;

        retList.pop_front();
    }

    {
        // -- test Disconnect Switch (event code 50 ) -- format 3
        unsigned char test_data[10] = {0x11, 0x22, 0x33, 0x44, 0x00, 0x03, 0x00, 0x32, 0xEC, 0x46};
        memcpy( InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = Cti::Protocols::EmetconProtocol::GetStatus_EventLog;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0x55; // between 0x50 and 0x59

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetStatusEventLog(InMessage, t, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        string expected =
        "Test MCT-440-213xB / Parameters Change:\n"
        "Time: " + CtiTime(0x11223344).asString() + "\n"
        "User ID: 3\n"
        "Event: Disconnect Switch Error Detected\n"
        "Frequency deviation: -2 Hz\n"
        "Meter temparature: 2 C\n"
        "Highest instantaneous power (fwd+rev) over last 5 seconds: 48..63 kW\n"
        "Highest phase DC voltage compensation: >= 56 V\n";

        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

        delete retMsg;

        retList.pop_front();
    }

    {
        // -- test Disconnect Switch (event code 50 ) -- format 4
        unsigned char test_data[10] = {0x11, 0x22, 0x33, 0x44, 0x00, 0x04, 0x00, 0x32, 0xEC, 0x4C};
        memcpy( InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = Cti::Protocols::EmetconProtocol::GetStatus_EventLog;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0x55; // between 0x50 and 0x59

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetStatusEventLog(InMessage, t, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        string expected =
        "Test MCT-440-213xB / Parameters Change:\n"
        "Time: " + CtiTime(0x11223344).asString() + "\n"
        "User ID: 4\n"
        "Event: Disconnect Switch Error Detected\n"
        "Internal state: closed\n"
        "Disconnect state: open\n"
        "Load side voltage: present\n"
        "Instantaneous power: 472 W\n";

        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

        delete retMsg;

        retList.pop_front();
    }

    {
        // -- test Control Output 1 Tripped (event code 66 )
        unsigned char test_data[10] = {0x33, 0x44, 0x55, 0x66, 0x00, 0x02, 0x00, 0x42, 0x06, 0x52};
        memcpy( InMessage.Buffer.DSt.Message, test_data, sizeof(test_data));

        InMessage.Return.UserID                        = 0;
        InMessage.Sequence                             = Cti::Protocols::EmetconProtocol::GetStatus_EventLog;
        InMessage.Return.ProtocolInfo.Emetcon.Function = 0x55; // between 0x50 and 0x59

        BOOST_CHECK_EQUAL(ClientErrors::None, test_dev.decodeGetStatusEventLog(InMessage, t, vgList, retList, outList));

        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

        BOOST_REQUIRE(retMsg);

        string expected =
        "Test MCT-440-213xB / Parameters Change:\n"
        "Time: " + CtiTime(0x33445566).asString() + "\n"
        "User ID: 2\n"
        "Event: Control Output 1 Tripped\n"
        "Changed state: opened\n"
        "Attempt: failed\n"
        "Reason of change: Manual operation\n";

        BOOST_CHECK_EQUAL( retMsg->Status(), ClientErrors::None );
        BOOST_CHECK_EQUAL( retMsg->ResultString(), expected );

        delete retMsg;

        retList.pop_front();
    }
}

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

