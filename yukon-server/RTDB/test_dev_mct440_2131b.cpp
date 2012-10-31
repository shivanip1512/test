#include <boost/test/unit_test.hpp>

#include "dev_mct440_2131b.h"
#include "devicetypes.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "utility.h"  //  for delete_container
#include "config_device.h"
#include "boost_test_helpers.h"

#include <boost/assign/list_of.hpp>

using Cti::Protocols::EmetconProtocol;

using std::string;
using std::vector;

struct test_Mct440_2131BDevice : Cti::Devices::Mct440_2131BDevice
{
    test_Mct440_2131BDevice(int type, const string &name)
    {
        setType(type);
        _name = name;
    }

    using CtiTblPAOLite::_type;

    using MctDevice::getOperation;
    using MctDevice::ReadDescriptor;
    using MctDevice::value_locator;
    using MctDevice::getDescriptorForRead;

    using Mct4xxDevice::getUsageReportDelay;

    using Mct440_2131BDevice::executeGetValue;

    using Mct440_2131BDevice::decodeGetValueDailyRead;

    using Mct440_2131BDevice::decodeDisconnectConfig;
    using Mct440_2131BDevice::decodeDisconnectStatus;
    using Mct440_2131BDevice::isProfileTablePointerCurrent;

    using Mct440_2131BDevice::decodeGetValueInstantLineData;
    using Mct440_2131BDevice::decodeGetValueTOUkWh;
    using Mct440_2131BDevice::decodeGetValueDailyReadRecent;
    using Mct440_2131BDevice::executePutConfigTOU;
    using Mct440_2131BDevice::executePutConfig;
    using Mct440_2131BDevice::executeGetConfig;
    using MctDevice::updateFreezeInfo;

    enum test_Features
    {
        test_Feature_DisconnectCollar,
        test_Feature_HourlyKwh
    };

    bool test_isSupported(test_Features f)
    {
        switch(f)
        {
            case test_Feature_DisconnectCollar: return isSupported(Feature_DisconnectCollar);
            case test_Feature_HourlyKwh:        return isSupported(Feature_HourlyKwh);
        }

        return false;
    }

    typedef std::map<int, CtiPointSPtr>              PointOffsetMap;
    typedef std::map<CtiPointType_t, PointOffsetMap> PointTypeOffsetMap;

    PointTypeOffsetMap points;

    virtual CtiPointSPtr getDevicePointOffsetTypeEqual(int offset, CtiPointType_t type)
    {
        CtiPointSPtr point = points[type][offset];

        if( point )
        {
            return point;
        }

        unsigned point_count = 0;

        for each( const std::pair<CtiPointType_t, PointOffsetMap> &p in points )
        {
            point_count += p.second.size();
        }

        switch( type )
        {
            case AnalogPointType:
            {
                Test_CtiPointAnalog *analog = new Test_CtiPointAnalog();
                analog->setPointOffset(offset);
                analog->setDeviceID(reinterpret_cast<long>(&points));
                analog->setID(point_count);
                point.reset(analog);
            }
            break;

            case PulseAccumulatorPointType:
            case DemandAccumulatorPointType:
            {
                Test_CtiPointAccumulator *accumulator = new Test_CtiPointAccumulator();
                accumulator->setPointOffset(offset);
                accumulator->setDeviceID(reinterpret_cast<long>(&points));
                accumulator->setID(point_count);
                point.reset(accumulator);
            }
            break;

            case StatusPointType:
            {
                Test_CtiPointStatus *status = new Test_CtiPointStatus();
                status->setPointOffset(offset);
                status->setDeviceID(reinterpret_cast<long>(&points));
                status->setID(point_count);
                point.reset(status);
            }
            break;
        }

        return point;
    }
};

struct test_Mct440_2131B : test_Mct440_2131BDevice
{
    test_Mct440_2131B() : test_Mct440_2131BDevice(TYPEMCT440_2131B, "Test MCT-440-2131B")  {}
};

struct test_DeviceConfig : public Cti::Config::DeviceConfig
{
    test_DeviceConfig() :
        DeviceConfig(-1, string(), string())
    {
    }

    using DeviceConfig::insertValue;
};

namespace std {
    //  defined in rtdb/test_main.cpp
    std::ostream& operator<<(std::ostream& out, const test_Mct440_2131BDevice::ReadDescriptor &rd);
    std::ostream& operator<<(std::ostream& out, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rd);
    bool operator==(const test_Mct440_2131BDevice::value_locator &lhs, const boost::tuples::tuple<unsigned, unsigned, int> &rhs);
}

namespace boost {
namespace test_tools {
    //  defined in rtdb/test_main.cpp
    bool operator!=(const test_Mct440_2131BDevice::ReadDescriptor &lhs, const std::vector<boost::tuples::tuple<unsigned, unsigned, int>> &rhs);
}
}

BOOST_AUTO_TEST_SUITE( test_dev_mct440_2131b )

BOOST_AUTO_TEST_CASE(test_isSupported_DisconnectCollar)
{
    BOOST_CHECK_EQUAL(false,  test_Mct440_2131B().test_isSupported(test_Mct440_2131BDevice::test_Feature_DisconnectCollar));
}

BOOST_AUTO_TEST_CASE(test_decodeDisconnectConfig)
{
    struct test_case
    {
        const int mct_type;
        const int config_byte;
        const string expected;
    }
    test_cases[] =
    {
        {TYPEMCT440_2131B,  0x00, "Disconnect load limit connect delay: 34 minutes\n"
                                  "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                                  "Disconnect demand threshold disabled\n"
                                  "Disconnect cycling mode disabled\n"},
        {TYPEMCT440_2131B,  0x04, "Disconnect load limit connect delay: 34 minutes\n"
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

            test_Mct440_2131BDevice test_dev(tc.mct_type, "No name");

            test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 40);
            test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, tc.config_byte);

            results.push_back(test_dev.decodeDisconnectConfig(DSt));
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(expected.begin(), expected.end(),
                                  results.begin(),  results.end());
}


BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_decodeDisconnectStatus)
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
            test_Mct440_2131B test_dev;

            DSTRUCT DSt;

            DSt.Message[0] = tc.dst_message_0;
            DSt.Message[1] = tc.dst_message_1;
            DSt.Message[8] = tc.dst_message_8;

            results.push_back(test_dev.decodeDisconnectStatus(DSt));
        }
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(expected.begin(), expected.end(),
                                  results.begin(),  results.end());
}


BOOST_AUTO_TEST_CASE(test_decodePulseAccumulator)
{
    unsigned char kwh_read[3] = { 0x00, 0x02, 0x00 };

    CtiDeviceSingle::point_info pi;

    pi = Cti::Devices::Mct440_2131BDevice::decodePulseAccumulator(kwh_read, 3, 0);

    BOOST_CHECK_EQUAL( pi.value,      512 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );

    kwh_read[2] = 0x01;

    pi = Cti::Devices::Mct440_2131BDevice::decodePulseAccumulator(kwh_read, 3, 0);

    BOOST_CHECK_EQUAL( pi.value,      513 );
    BOOST_CHECK_EQUAL( pi.freeze_bit, false );
    BOOST_CHECK_EQUAL( pi.quality,    NormalQuality );
}


BOOST_AUTO_TEST_CASE(test_isProfileTablePointerCurrent)
{
    test_Mct440_2131B test_dev;

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

        BOOST_CHECK_EQUAL( NoMethod, test_Mct440_2131B().beginExecuteRequest(&request, parse, vgList, retList, outList) );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_centron_ratio)
    {
        CtiCommandParser parse("getconfig centron ratio");

        BOOST_CHECK_EQUAL( NoMethod, test_Mct440_2131B().beginExecuteRequest(&request, parse, vgList, retList, outList) );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_meter_parameters)
    {
        CtiCommandParser parse("getconfig meter parameters");

        BOOST_CHECK_EQUAL( NoError, test_Mct440_2131B().beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf3 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   2 );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_meter_ratio)
    {
        CtiCommandParser parse("getconfig meter ratio");

        BOOST_CHECK_EQUAL( NoError, test_Mct440_2131B().beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf3 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   2 );
    }
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


struct getvalueDailyReads_helper : beginExecuteRequest_helper
{
    test_Mct440_2131B test_dev;
    OUTMESS *om;

    getvalueDailyReads_helper()
    {
        om = new OUTMESS;
    }

    ~getvalueDailyReads_helper()
    {
        delete om;
    }
};

BOOST_FIXTURE_TEST_SUITE(getvalue_daily_reads, getvalueDailyReads_helper)
//{  Brace matching for BOOST_FIXTURE_TEST_SUITE
    BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_getvalue_daily_reads_0kwh)
    {
        CtiTime timenow;

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , test_dev.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , test_dev.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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

    BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_getvalue_daily_reads_underflow)
    {
        CtiTime timenow;

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , test_dev.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , test_dev.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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

    BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_getvalue_daily_reads_normal_deltas)
    {
        CtiTime timenow;

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , test_dev.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , test_dev.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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
    BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_getvalue_daily_reads_bad_first_kwh)
    {
        CtiTime timenow;

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , test_dev.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , test_dev.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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
    BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_getvalue_daily_reads_bad_first_delta)
    {
        CtiTime timenow;

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , test_dev.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , test_dev.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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
    BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_getvalue_daily_reads_large_deltas)
    {
        CtiTime timenow;

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , test_dev.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , test_dev.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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
    BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_getvalue_daily_reads_all_deltas_bad)
    {
        CtiTime timenow;

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , test_dev.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , test_dev.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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
    BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_getvalue_daily_reads_all_bad)
    {
        CtiTime timenow;

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , test_dev.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , test_dev.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

            CtiMultiMsg_vec points = retMsg->PointData();

            BOOST_CHECK_EQUAL( points.size(), 0 );
        }
    }
    BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_getvalue_daily_reads_all_bad_but_last)
    {
        CtiTime timenow;

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , test_dev.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , test_dev.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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



    BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_getvalue_daily_read_recent)
    {

        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        test_dev.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);



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
                    str_cmd += " " + date.asStringUSFormat();
                }

                CtiCommandParser parse(str_cmd);

                BOOST_CHECK_EQUAL( NoError , test_dev.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

                unsigned char buf[10] = { 0x98, 0x96, 0x7e, 0x98, 0x96, 0x7e, 0x0, 0x3, 0x0, 0x0};

                buf[8] = date.dayOfMonth();
                buf[9] = date.month() - 1;

                std::copy(buf,  buf + 10, im.Buffer.DSt.Message );

                im.Buffer.DSt.Length = 10;
                im.Buffer.DSt.Address = 0x1ffff;  //  CarrierAddress is -1 by default, so the lower 13 bits are all set

                BOOST_CHECK_EQUAL( NoError , test_dev.decodeGetValueDailyReadRecent(&im, timenow, vgList, retList, outList) );

                BOOST_REQUIRE_EQUAL(retList.size(), 1);

                const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

                BOOST_REQUIRE(retMsg);

                CtiMultiMsg_vec points = retMsg->PointData();

                BOOST_REQUIRE_EQUAL(points.size(), 3);

                {
                    const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

                    BOOST_REQUIRE( pdata );

                    CtiTime t_exp((date + 1), 0, 0, 0); // add 1 day

                    BOOST_CHECK_EQUAL( pdata->getValue(), 3);
                    BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
                    BOOST_CHECK_EQUAL( pdata->getTime().seconds(), t_exp.seconds());
                }
            }
        }
    }

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()


BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_getUsageReportDelay)
{
    const test_Mct440_2131B test_dev;

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


BOOST_AUTO_TEST_CASE(test_dev_mct440_2131b_extractDynamicPaoInfo)
{
    test_Mct440_2131B test_dev;

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
    test_Mct440_2131B test_dev;

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
    BOOST_AUTO_TEST_CASE(test_getOperation_31)
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
    BOOST_AUTO_TEST_CASE(test_getOperation_35)
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
        BOOST_CHECK_EQUAL(BSt.Length,   10);
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
    BOOST_AUTO_TEST_CASE(test_getOperation_42)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(BSt.Length,   11);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_43)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0xfe);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
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
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_50)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_Intervals, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1a);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_51)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_OutageThreshold, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x22);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_52)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_Thresholds, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1e);
        BOOST_CHECK_EQUAL(BSt.Length,   3);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_53)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_DailyReadInterest, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf3);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
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
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_LongLoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x04);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_67)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::GetConfig_LongLoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x9d);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_68)
    {
        BOOST_REQUIRE(test_dev.getOperation(EmetconProtocol::PutConfig_Thresholds, BSt));
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
        BOOST_CHECK_EQUAL(BSt.Length,   5);
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

//vector<vector<boost::tuple<unsigned, unsigned, int>>> gen_expected_io_read_mapping_val (vector<vector<boost::tuple<unsigned, unsigned, int>>> ref_vect, int size)
//{
//    vector<vector<boost::tuple<unsigned, unsigned, int>>> new_vect;
//    vector<vector<boost::tuple<unsigned, unsigned, int>>>::iterator it;
//    vector<boost::tuple<unsigned, unsigned, int>>::iterator it2;
//
//    for (it = ref_vect.begin(); it < ref_vect.end(); it++)
//    {
//        vector<boost::tuple<unsigned, unsigned, int>> new_vect_2d;
//
//        for (it2 = it->begin(); it2 < it->end(); it2++)
//        {
//            unsigned pos = it2->get<0>();
//            unsigned len = it2->get<1>();
//            int      off = it2->get<2>();
//
//            if((pos + len) <= (size))
//            {
//                boost::tuple<unsigned, unsigned, int> tup(pos, len, off);
//                new_vect_2d.push_back(tup);
//            }
//        }
//        new_vect.push_back(new_vect_2d);
//    }
//
//    return new_vect;
//}
//
//
//BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_AllDwords)
//{
//    using namespace boost::assign;
//    using namespace boost::tuples;
//
//    vector<test_Mct440_2131BDevice::ReadDescriptor> results;
//
//    const vector<tuple<unsigned, unsigned, int>> empty;
//
//    const vector<vector<tuple<unsigned, unsigned, int>>> ref_expected = list_of<vector<tuple<unsigned, unsigned, int>>>
//        //  memory read 0
//        (tuple_list_of(0,1,101)(1,2,100))
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (tuple_list_of(5,1,128)(6,1,129)(7,2,130))
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        // memory read 10
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (tuple_list_of(0,1,141))
//        (empty)
//        (empty)
//        (empty)
//        (tuple_list_of(0,1,123)(1,2,126)(3,2,124)(5,1,125))
//        // memory read 20
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (tuple_list_of(0,1,142))
//        (tuple_list_of(0,1,112)(1,1,103))
//        (empty)
//        (empty)
//        (empty)
//        // memory read 30
//        (tuple_list_of(0,2,111)(2,2,110)(4,1,132))
//        (empty)
//        (empty)
//        (empty)
//        (tuple_list_of(0,1,132))
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        // memory read 40
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        // memory read 50
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (tuple_list_of(0,1,106))
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        // memory read 60
//        (empty)
//        (empty)
//        (empty)
//        (tuple_list_of(0,1,109))
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        // memory read 70
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (tuple_list_of(0,1,115))
//        // memory read 80
//        .repeat(100, empty)
//        // memory read 180
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        // memory read 190
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        // memory read 200
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (empty)
//        (tuple_list_of(0,4,143)(4,4,144)(8,4,145))
//        (empty)
//        // memory read 210
//        (empty)
//        (empty)
//        (tuple_list_of(0,4,144)(4,4,145))
//        (empty)
//        (empty)
//        (empty)
//        (tuple_list_of(0,4,145))
//        (empty)
//        (empty)
//        (empty)
//        // memory read 220
//        .repeat(36, empty);
//
//
//    const test_Mct440_2131B test_dev;
//
//    for (int i = 1 ; i <= 13 ; i++)
//    {
//        const vector<vector<tuple<unsigned, unsigned, int>>> expected = gen_expected_io_read_mapping_val(ref_expected, i);
//
//        results.clear();
//        for( unsigned function = 0; function < 256; ++function )
//        {
//            results.push_back( test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, i));
//        }
//
//        BOOST_CHECK_EQUAL_COLLECTIONS(results.begin(),
//                                      results.end(),
//                                      expected.begin(),
//                                      expected.end());
//
//
//    }
//}


BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_1Dword)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct440_2131BDevice::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  memory read 0
        (tuple_list_of(0,1,101)(1,2,100))
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
        (tuple_list_of(0,1,141))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,123)(1,2,126))
        // memory read 20
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,142))
        (tuple_list_of(0,1,112)(1,1,103))
        (empty)
        (empty)
        (empty)
        // memory read 30
        (tuple_list_of(0,1,264)(1,2,265))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,132))
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
        (tuple_list_of(0,1,106))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 60
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,109))
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
        (tuple_list_of(0,1,115))
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
        (tuple_list_of(0,4,143))
        (tuple_list_of(0,4,243)) // holiday 4
        // memory read 210
        (tuple_list_of(0,4,246)) // holiday 7
        (tuple_list_of(0,4,249)) // holiday 10
        (tuple_list_of(0,4,252)) // holiday 13
        (tuple_list_of(0,4,255)) // holiday 16
        (tuple_list_of(0,4,258)) // holiday 19
        (tuple_list_of(0,4,261)) // holiday 22
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 220
        .repeat(36, empty);

    const test_Mct440_2131B test_dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back( test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 3));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_2Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct440_2131BDevice::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  memory read 0
        (tuple_list_of(0,1,101)(1,2,100))
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(5,1,128)(6,1,129)(7,2,130))
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
        (tuple_list_of(0,1,141))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,123)(1,2,126)(3,2,124)(5,1,125))
        // memory read 20
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,142))
        (tuple_list_of(0,1,112)(1,1,103))
        (empty)
        (empty)
        (empty)
        // memory read 30
        (tuple_list_of(0,1,264)(1,2,265))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,132))
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
        (tuple_list_of(0,1,106))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 60
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,109))
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
        (tuple_list_of(0,1,115))
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
        (tuple_list_of(0,4,143)(4,4,144))
        (tuple_list_of(0,4,243)(4,4,244)) // holiday 4, 5
        // memory read 210
        (tuple_list_of(0,4,246)(4,4,247)) // holiday 7, 8
        (tuple_list_of(0,4,249)(4,4,250)) // holiday 10, 11
        (tuple_list_of(0,4,252)(4,4,253)) // holiday 13, 14
        (tuple_list_of(0,4,255)(4,4,256)) // holiday 16, 17
        (tuple_list_of(0,4,258)(4,4,259)) // holiday 19, 20
        (tuple_list_of(0,4,261)(4,4,262)) // holiday 22, 23
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 220
        .repeat(36, empty);

    const test_Mct440_2131B test_dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back(test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 8));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Read_3Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct440_2131BDevice::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  memory read 0
        (tuple_list_of(0,1,101)(1,2,100))
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(5,1,128)(6,1,129)(7,2,130))
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
        (tuple_list_of(0,1,141))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,123)(1,2,126)(3,2,124)(5,1,125))
        // memory read 20
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,142))
        (tuple_list_of(0,1,112)(1,1,103))
        (empty)
        (empty)
        (empty)
        // memory read 30
        (tuple_list_of(0,1,264)(1,2,265))
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,132))
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
        (tuple_list_of(0,1,106))
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 60
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,1,109))
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
        (tuple_list_of(0,1,115))
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
        (tuple_list_of(0,4,143)(4,4,144)(8,4,145))
        (tuple_list_of(0,4,243)(4,4,244)(8,4,245)) // holiday 4, 5, 6
        // memory read 210
        (tuple_list_of(0,4,246)(4,4,247)(8,4,248)) // holiday 7, 8, 9
        (tuple_list_of(0,4,249)(4,4,250)(8,4,251)) // holiday 10, 11, 12
        (tuple_list_of(0,4,252)(4,4,253)(8,4,254)) // holiday 13, 14, 15
        (tuple_list_of(0,4,255)(4,4,256)(8,4,257)) // holiday 16, 17, 18
        (tuple_list_of(0,4,258)(4,4,259)(8,4,260)) // holiday 19, 20, 21
        (tuple_list_of(0,4,261)(4,4,262)(8,4,263)) // holiday 22, 23, 24
        (empty)
        (empty)
        (empty)
        (empty)
        // memory read 220
        .repeat(36, empty);

    const test_Mct440_2131B test_dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back( test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Read, function, 13));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_1Dword)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct440_2131BDevice::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty)
        .repeat(170-1, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,117)(2,1,122))
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
        (tuple_list_of(0,1,176)(1,1,177)(2,1,178))
        (tuple_list_of(0,1,189)(1,1,190)(2,1,191))
        (empty)
        (empty)
        //  function read 240
        .repeat(6, empty);

    const test_Mct440_2131B test_dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back( test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 3));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_2Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct440_2131BDevice::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty)
        .repeat(150-1, empty)
        //  function read 150
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(4,1,137)(5,1,138)(6,1,139)(7,1,140))
        (empty)
        (empty)
        //  function read 160
        .repeat(10, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,117)(2,1,122))
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
        (tuple_list_of(0,1,176)(1,1,177)(2,1,178)(3,1,179)(4,1,180)(5,1,181)(6,1,182)(7,1,183))
        (tuple_list_of(0,1,189)(1,1,190)(2,1,191)(3,1,192)(4,1,193)(5,1,194)(6,1,195)(7,1,196))
        (empty)
        (empty)
        //  function read 250
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(5,2,133)(7,1,134))
        (empty);

    const test_Mct440_2131B test_dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back( test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 8));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_CASE(test_getValueMappingForRead_IO_Function_Read_3Dwords)
{
    using namespace boost::assign;
    using namespace boost::tuples;

    vector<test_Mct440_2131BDevice::ReadDescriptor> results;

    const vector<tuple<unsigned, unsigned, int>> empty;

    const vector<vector<tuple<unsigned, unsigned, int>>> expected = list_of<vector<tuple<unsigned, unsigned, int>>>
        //  function read 0
        (empty)
        .repeat(150-1, empty)
        //  function read 150
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(4,1,137)(5,1,138)(6,1,139)(7,1,140))
        (empty)
        (empty)
        //  function read 160
        .repeat(10, empty)
        //  function read 170
        (empty)
        (empty)
        (empty)
        (tuple_list_of(0,2,117)(2,1,122)(10,1,109))
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
        (tuple_list_of(0,1,176)(1,1,177)(2,1,178)(3,1,179)(4,1,180)(5,1,181)(6,1,182)(7,1,183)(8,1,184)(9,1,185)(10,1,186)(11,1,187)(12,1,188))
        (tuple_list_of(0,1,189)(1,1,190)(2,1,191)(3,1,192)(4,1,193)(5,1,194)(6,1,195)(7,1,196)(8,1,197)(9,1,198)(10,1,199)(11,1,200)(12,1,201))
        (empty)
        (empty)
        //  function read 250
        (empty)
        (empty)
        (empty)
        (empty)
        (tuple_list_of(5,2,133)(7,1,134)(9,1,135)(10,1,136)(11,1,127))
        (empty);

    const test_Mct440_2131B test_dev;

    for( unsigned function = 0; function < 256; ++function )
    {
        results.push_back( test_dev.getDescriptorForRead(Cti::Protocols::EmetconProtocol::IO_Function_Read, function, 13));
    }

    BOOST_CHECK_EQUAL_COLLECTIONS(
       results.begin(),  results.end(),
       expected.begin(), expected.end());
}

BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_CASE(test_decodeGetValueInstantLineData)
{
    INMESS                         InMessage;
    CtiTime                        t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)
    CtiDeviceBase::CtiMessageList  vgList;
    CtiDeviceBase::CtiMessageList  retList;
    CtiDeviceBase::OutMessageList  outList; // not use


    unsigned char test_data[12] = {0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9,0xA,0xB,0xC};

    memcpy(InMessage.Buffer.DSt.Message, test_data, 12);

    InMessage.Return.UserID = 0;
    InMessage.Sequence      = EmetconProtocol::GetValue_InstantLineData;

    test_Mct440_2131B test_dev;

    BOOST_CHECK_EQUAL(NoError, test_dev.decodeGetValueInstantLineData(&InMessage, t, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    CtiMultiMsg_vec points = retMsg->PointData();

    BOOST_REQUIRE_EQUAL(points.size(), 3);

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // phase A Line Volage

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 16);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // phase A Line Current

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 515);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // phase A Power Factor

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 4);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

//    {
//        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);  // phase B Line Volage
//
//        BOOST_REQUIRE( pdata );
//
//        BOOST_CHECK_EQUAL( pdata->getValue(), 80);
//        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
//        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
//    }
//
//    {
//        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[4]);  // phase B Line Current
//
//        BOOST_REQUIRE( pdata );
//
//        BOOST_CHECK_EQUAL( pdata->getValue(), 1543);
//        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
//        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
//    }
//
//    {
//        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[5]);  // phase B Power Factor
//
//        BOOST_REQUIRE( pdata );
//
//        BOOST_CHECK_EQUAL( pdata->getValue(), 8);
//        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
//        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
//    }
//
//    {
//        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[6]);  // phase C Line Volage
//
//        BOOST_REQUIRE( pdata );
//
//        BOOST_CHECK_EQUAL( pdata->getValue(), 144);
//        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
//        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
//    }
//
//    {
//        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[7]);  // phase C Line Current
//
//        BOOST_REQUIRE( pdata );
//
//        BOOST_CHECK_EQUAL( pdata->getValue(), 2571);
//        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
//        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
//    }
//
//    {
//        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[8]);  // phase C Power Factor
//
//        BOOST_REQUIRE( pdata );
//
//        BOOST_CHECK_EQUAL( pdata->getValue(), 12);
//        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
//        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
//    }

}

BOOST_AUTO_TEST_CASE(test_decodeGetValueTOUkWh)
{
    INMESS                          InMessage;
    CtiTime                         t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)
    CtiDeviceBase::CtiMessageList   vgList;
    CtiDeviceBase::CtiMessageList   retList;
    CtiDeviceBase::OutMessageList   outList; // not use


    unsigned char test_data[13] = {0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9,0xA,0xB,0xC,0xD};

    memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

    InMessage.Return.UserID = 0;
    InMessage.Sequence      = EmetconProtocol::GetValue_TOUkWh;

    test_Mct440_2131B test_dev;

    BOOST_CHECK_EQUAL(NoError, test_dev.decodeGetValueTOUkWh(&InMessage, t, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    CtiMultiMsg_vec points = retMsg->PointData();

    BOOST_REQUIRE_EQUAL(points.size(), 4);

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0x010203);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // rate b

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0x040506);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // rate c

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0x070809);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);  // rate d

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0x0A0B0C);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }
}


BOOST_AUTO_TEST_CASE(test_decodeGetValueTOUkWh_reverse)
{
    INMESS                          InMessage;
    CtiTime                         t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)
    CtiDeviceBase::CtiMessageList   vgList;
    CtiDeviceBase::CtiMessageList   retList;
    CtiDeviceBase::OutMessageList   outList; // not use


    unsigned char test_data[13] = {0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9,0xA,0xB,0xC,0xD};

    memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

    InMessage.Return.UserID = 0;
    InMessage.Sequence      = EmetconProtocol::GetValue_TOUkWhReverse;

    test_Mct440_2131B test_dev;

    BOOST_CHECK_EQUAL(NoError, test_dev.decodeGetValueTOUkWh(&InMessage, t, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    CtiMultiMsg_vec points = retMsg->PointData();

    BOOST_REQUIRE_EQUAL(points.size(), 4);

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 66051);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // rate b

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 263430);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // rate c

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 460809);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);  // rate d

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 658188);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }
}


BOOST_AUTO_TEST_CASE(test_decodeGetValueTOUkWhFrozen)
{
    INMESS                          InMessage;
    CtiTime                         t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)
    CtiDeviceBase::CtiMessageList   vgList;
    CtiDeviceBase::CtiMessageList   retList;
    CtiDeviceBase::OutMessageList   outList; // not use


    unsigned char test_data[13] = {0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9,0xA,0xB,0xC,0xD};

    memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

    InMessage.Return.UserID = 0;
    InMessage.Sequence      = EmetconProtocol::GetValue_FrozenTOUkWh;

    test_Mct440_2131B test_dev;

    test_dev.updateFreezeInfo(12,0);

    BOOST_CHECK_EQUAL(NoError, test_dev.decodeGetValueTOUkWh(&InMessage, t, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    CtiMultiMsg_vec points = retMsg->PointData();

    BOOST_REQUIRE_EQUAL(points.size(), 4);

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0x010203);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // rate b

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0x040506);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // rate c

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0x070809);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);  // rate d

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0x0A0B0C);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }
}

BOOST_AUTO_TEST_CASE(test_decodeGetValueTOUkWhReverseFrozen)
{
    INMESS                          InMessage;
    CtiTime                         t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)
    CtiDeviceBase::CtiMessageList   vgList;
    CtiDeviceBase::CtiMessageList   retList;
    CtiDeviceBase::OutMessageList   outList; // not use


    unsigned char test_data[13] = {0x1,0x2,0x3,0x4,0x5,0x6,0x7,0x8,0x9,0xA,0xB,0xC,0xD};

    memcpy(InMessage.Buffer.DSt.Message, test_data, 13);

    InMessage.Return.UserID = 0;
    InMessage.Sequence      = EmetconProtocol::GetValue_FrozenTOUkWhReverse;

    test_Mct440_2131B test_dev;

    test_dev.updateFreezeInfo(12,0);

    BOOST_CHECK_EQUAL(NoError, test_dev.decodeGetValueTOUkWh(&InMessage, t, vgList, retList, outList));

    BOOST_REQUIRE_EQUAL(retList.size(), 1);

    const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

    BOOST_REQUIRE(retMsg);

    CtiMultiMsg_vec points = retMsg->PointData();

    BOOST_REQUIRE_EQUAL(points.size(), 4);

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[0]);  // rate A

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0x010203);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[1]);  // rate b

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0x040506);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[2]);  // rate c

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0x070809);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }

    {
        const CtiPointDataMsg *pdata = dynamic_cast<CtiPointDataMsg *>(points[3]);  // rate d

        BOOST_REQUIRE( pdata );

        BOOST_CHECK_EQUAL( pdata->getValue(), 0x0A0B0C);
        BOOST_CHECK_EQUAL( pdata->getQuality(), NormalQuality );
        //BOOST_CHECK_EQUAL( pdata->getTime(), t);
    }
}

struct executePutConfig_helper
{
    CtiRequestMsg           request;
    std::list<CtiMessage*>  vgList, retList;
    std::list<OUTMESS*>     outList;

    ~executePutConfig_helper()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
    }
};

BOOST_FIXTURE_TEST_SUITE(command_executions, executePutConfig_helper)

    BOOST_AUTO_TEST_CASE(test_executePutConfigTOU)
    {
        test_DeviceConfig   test_cfg;

        test_cfg.insertValue("monday",   "schedule 1");
        test_cfg.insertValue("tuesday",  "schedule 2");
        test_cfg.insertValue("wenesday", "schedule 3");
        test_cfg.insertValue("thursday", "schedule 4");
        test_cfg.insertValue("friday",   "schedule 1");
        test_cfg.insertValue("saturday", "schedule 2");
        test_cfg.insertValue("sunday",   "schedule 3");
        test_cfg.insertValue("holiday",  "schedule 4");


        char cfg_key[20];
        char cfg_val[10];

        for (int i=1; i<=4; i++)
        {
            for (int j=1; j<=10; j++)
            {
                sprintf(cfg_key, "schedule%itime%i",i,j);
                sprintf(cfg_val, "%i:%02i",j,i*5);

                test_cfg.insertValue(cfg_key, cfg_val);
            }
        }

        for (int i=1; i<=4; i++)
        {
            for (int j=0; j<=10; j++)
            {
                sprintf(cfg_key, "schedule%irate%i",i,j);

                switch(j%4)
                {
                case 0:  test_cfg.insertValue(cfg_key, "A"); break;
                case 1:  test_cfg.insertValue(cfg_key, "B"); break;
                case 2:  test_cfg.insertValue(cfg_key, "C"); break;
                default: test_cfg.insertValue(cfg_key, "D"); break;
                }
            }
        }

        test_cfg.insertValue("default rate", "A");

        test_Mct440_2131B test_dev;

        test_dev.changeDeviceConfig(Cti::Config::DeviceConfigSPtr(&test_cfg, null_deleter())); // null_deleter prevents destruction of the stack object when the shared_ptr goes out of scope.

        CtiCommandParser parse("installvalue tou force");
        CtiOutMessage *outMessage = CTIDBG_new OUTMESS;

        BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfigTOU(&request, parse, outMessage, vgList, retList, outList, false));

        BOOST_CHECK_EQUAL(outList.size(), 8);

        OUTMESS* outmsg = outList.front();

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x30);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  ,   15);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , EmetconProtocol::IO_Function_Write);

        unsigned char exp_msg_0[15] = {0xd3,0xd2,0x41,0xc,0xc,0xc,0xc,0x44,0xe6,0x46,0xc,0xc,0xc,0xc,0xe6};

        BOOST_CHECK_EQUAL_COLLECTIONS(
               &outmsg->Buffer.BSt.Message[0],&outmsg->Buffer.BSt.Message[14],
               &exp_msg_0[0], &exp_msg_0[14]);

        outList.pop_front();
        outmsg = outList.front();

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x31);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  ,   15);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , EmetconProtocol::IO_Function_Write);

        unsigned char exp_msg_1[15] = {0x4b,0xc,0xc,0xc,0xc,0x4,0xe6,0x50,0xc,0xc,0xc,0xc,0x4,0xe6,0x0};

        BOOST_CHECK_EQUAL_COLLECTIONS(
               &outmsg->Buffer.BSt.Message[0],&outmsg->Buffer.BSt.Message[14],
               &exp_msg_1[0], &exp_msg_1[14]);

        outList.pop_front();
        outmsg = outList.front();

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x33);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  ,   15);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , EmetconProtocol::IO_Function_Write);

        unsigned char exp_msg_2[15] = {0xc,0xc,0xc,0xc,0xc,0x9,0x38,0xc,0xc,0xc,0xc,0xc,0x9,0x38,0x0};

        BOOST_CHECK_EQUAL_COLLECTIONS(
               &outmsg->Buffer.BSt.Message[0],&outmsg->Buffer.BSt.Message[14],
               &exp_msg_2[0], &exp_msg_2[14]);

        outList.pop_front();
        outmsg = outList.front();

        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x34);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  ,   15);
        BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.IO      , EmetconProtocol::IO_Function_Write);

        unsigned char exp_msg_3[15] = {0xc,0xc,0xc,0xc,0xc,0x9,0x38,0xc,0xc,0xc,0xc,0xc,0x9,0x38,0x0};

        BOOST_CHECK_EQUAL_COLLECTIONS(
               &outmsg->Buffer.BSt.Message[0],&outmsg->Buffer.BSt.Message[14],
               &exp_msg_3[0], &exp_msg_3[14]);

        outList.pop_front();
    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigHoliday)
    {
        test_Mct440_2131B test_dev;

        {
            CtiCommandParser parse("putconfig EMETCON holiday 1 01/20/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 4    );

            unsigned char expmsg[4] = {0x96,0x93,0xc8,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[3],
                           &expmsg[0], &expmsg[3]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 4 01/20/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d1);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 4    );

            unsigned char expmsg[4] = {0x96,0x93,0xc8,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[3],
                           &expmsg[0], &expmsg[3]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 7 01/20/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d2);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 4    );

            unsigned char expmsg[4] = {0x96,0x93,0xc8,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[3],
                           &expmsg[0], &expmsg[3]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 10 01/20/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d3);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 4    );

            unsigned char expmsg[4] = {0x96,0x93,0xc8,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[3],
                           &expmsg[0], &expmsg[3]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 13 01/20/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d4);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 4    );

            unsigned char expmsg[4] = {0x96,0x93,0xc8,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[3],
                           &expmsg[0], &expmsg[3]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 16 01/20/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d5);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 4    );

            unsigned char expmsg[4] = {0x96,0x93,0xc8,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[3],
                           &expmsg[0], &expmsg[3]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 19 01/20/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d6);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 4    );

            unsigned char expmsg[4] = {0x96,0x93,0xc8,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[3],
                           &expmsg[0], &expmsg[3]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 22 01/20/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d7);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 4    );

            unsigned char expmsg[4] = {0x96,0x93,0xc8,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[3],
                           &expmsg[0], &expmsg[3]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 1 01/20/2050 01/21/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 8    );

            unsigned char expmsg[8] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[7],
                           &expmsg[0], &expmsg[7]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 4 01/20/2050 01/21/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d1);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 8    );

            unsigned char expmsg[8] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[7],
                           &expmsg[0], &expmsg[7]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 7 01/20/2050 01/21/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d2);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 8    );

            unsigned char expmsg[8] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[7],
                           &expmsg[0], &expmsg[7]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 10 01/20/2050 01/21/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d3);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 8    );

            unsigned char expmsg[8] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[7],
                           &expmsg[0], &expmsg[7]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 13 01/20/2050 01/21/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d4);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 8    );

            unsigned char expmsg[8] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[7],
                           &expmsg[0], &expmsg[7]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 16 01/20/2050 01/21/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d5);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 8    );

            unsigned char expmsg[8] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[7],
                           &expmsg[0], &expmsg[7]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 19 01/20/2050 01/21/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d6);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 8    );

            unsigned char expmsg[8] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[3],
                           &expmsg[0], &expmsg[3]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 22 01/20/2050 01/21/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d7);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 8    );

            unsigned char expmsg[12] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50,0x96,0x96,0x6b,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[7],
                           &expmsg[0], &expmsg[7]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 1 01/20/2050 01/21/2050 01/22/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d0);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12    );

            unsigned char expmsg[12] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50,0x96,0x96,0x6b,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[11],
                           &expmsg[0], &expmsg[11]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 4 01/20/2050 01/21/2050 01/22/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d1);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12    );

            unsigned char expmsg[12] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50,0x96,0x96,0x6b,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[11],
                           &expmsg[0], &expmsg[11]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 7 01/20/2050 01/21/2050 01/22/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d2);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12    );

            unsigned char expmsg[12] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50,0x96,0x96,0x6b,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[11],
                           &expmsg[0], &expmsg[11]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 10 01/20/2050 01/21/2050 01/22/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d3);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12   );

            unsigned char expmsg[12] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50,0x96,0x96,0x6b,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[11],
                           &expmsg[0], &expmsg[11]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 13 01/20/2050 01/21/2050 01/22/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d4);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12    );

            unsigned char expmsg[12] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50,0x96,0x96,0x6b,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[11],
                           &expmsg[0], &expmsg[11]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 16 01/20/2050 01/21/2050 01/22/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d5);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12    );

            unsigned char expmsg[12] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50,0x96,0x96,0x6b,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[11],
                           &expmsg[0], &expmsg[11]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 19 01/20/2050 01/21/2050 01/22/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d6);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12    );

            unsigned char expmsg[12] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50,0x96,0x96,0x6b,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[11],
                           &expmsg[0], &expmsg[11]);
        }

        {
            CtiCommandParser parse("putconfig EMETCON holiday 22 01/20/2050 01/21/2050 01/22/2050");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x0d7);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 12    );

            unsigned char expmsg[12] = {0x96,0x93,0xc8,0xd0,0x96,0x95,0x1A,0x50,0x96,0x96,0x6b,0xd0};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                           &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[11],
                           &expmsg[0], &expmsg[11]);
        }


    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigTouDays)
    {
        test_Mct440_2131B test_dev;

        {
            CtiCommandParser parse("putconfig EMETCON tou 12341234 "
                                   "schedule 1 a/00:00 b/01:00 c/01:05 d/01:10 a/01:15 b/01:20 c/01:25 d/01:30 a/01:35 b/01:40 c/01:45 "
                                   "schedule 2 b/00:00 c/02:00 d/02:05 a/02:10 b/02:15 c/02:20 d/02:25 a/02:30 b/02:35 c/02:40 d/02:45 "
                                   "schedule 3 c/00:00 d/03:00 a/03:05 b/03:10 c/03:15 d/03:20 a/03:25 b/03:30 c/03:35 d/03:40 a/03:45 "
                                   "schedule 4 d/00:00 a/04:00 b/04:05 c/04:10 d/04:15 a/04:20 b/04:25 c/04:30 d/04:35 a/04:40 b/04:45 "
                                   "default a");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outList.size(), 4);
        }

        {
            OUTMESS* outmsg = outList.front();

            BOOST_CHECK_EQUAL(outmsg->Sequence, Cti::Protocols::EmetconProtocol::PutConfig_TOU);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x034);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 15   );

            unsigned char expmsg[15] = {0x01,0x01,0x01,0x01,0x01,0x03,0x90,0x01,0x01,0x01,0x01,0x01,0x04,0xe4,0x00};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                                       &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[15],
                                       &expmsg[0], &expmsg[15]);

            outList.pop_front();
        }

        {
            OUTMESS* outmsg = outList.front();

            BOOST_CHECK_EQUAL(outmsg->Sequence, Cti::Protocols::EmetconProtocol::PutConfig_TOU);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x033);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 15   );

            unsigned char expmsg[15] = {0x01,0x01,0x01,0x01,0x01,0x09,0x38,0x01,0x01,0x01,0x01,0x01,0x0e,0x4c,0x00};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                                       &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[15],
                                       &expmsg[0], &expmsg[15]);

            outList.pop_front();
        }

        {
            OUTMESS* outmsg = outList.front();

            BOOST_CHECK_EQUAL(outmsg->Sequence, Cti::Protocols::EmetconProtocol::PutConfig_TOU);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x031);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 15   );

            unsigned char expmsg[15] = {0x24,0x01,0x01,0x01,0x01,0x0e,0x4e,0x30,0x01,0x01,0x01,0x01,0x03,0x93,0x00};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                                       &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[15],
                                       &expmsg[0], &expmsg[15]);

            outList.pop_front();
        }

        {
            OUTMESS* outmsg = outList.front();

            BOOST_CHECK_EQUAL(outmsg->Sequence, Cti::Protocols::EmetconProtocol::PutConfig_TOU);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x030);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 15   );

            unsigned char expmsg[15] = {0xe4,0xe4,0x0c,0x01,0x01,0x01,0x01,0x94,0xe4,0x18,0x01,0x01,0x01,0x01,0x39};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                                       &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[15],
                                       &expmsg[0], &expmsg[15]);

            outList.pop_front();
        }

    }

    BOOST_AUTO_TEST_CASE(test_executePutConfigThresholds)
    {
        test_Mct440_2131B test_dev;

        {
            CtiCommandParser parse("putconfig EMETCON phaseloss thresholds 65 10:30:45");

            CtiOutMessage *outmsg = CTIDBG_new OUTMESS;

            BOOST_CHECK_EQUAL(NoError, test_dev.executePutConfig(&request, parse, outmsg, vgList, retList, outList));

            BOOST_CHECK_EQUAL(outmsg->Sequence, Cti::Protocols::EmetconProtocol::PutConfig_Thresholds);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Function, 0x01E);
            BOOST_CHECK_EQUAL(outmsg->Buffer.BSt.Length  , 3    );

            unsigned char expmsg[3] = {0x41,0x93,0xd5};

            BOOST_CHECK_EQUAL_COLLECTIONS(
                                       &outmsg->Buffer.BSt.Message[0], &outmsg->Buffer.BSt.Message[3],
                                       &expmsg[0], &expmsg[3]);
        }
    }
}
