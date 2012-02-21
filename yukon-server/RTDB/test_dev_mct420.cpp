#include <boost/test/unit_test.hpp>

#include "dev_mct420.h"
#include "devicetypes.h"
#include "pt_analog.h"
#include "pt_accum.h"
#include "pt_status.h"
#include "utility.h"  //  for delete_container

#include "boost_test_helpers.h"

using Cti::Protocols::EmetconProtocol;

using std::string;

BOOST_AUTO_TEST_SUITE( test_dev_mct420 )

struct test_Mct420Device : Cti::Devices::Mct420Device
{
    test_Mct420Device(int type, const string &name)
    {
        setType(type);
        _name = name;
    }

    using CtiTblPAOLite::_type;

    using MctDevice::getOperation;

    using Mct4xxDevice::getUsageReportDelay;

    using Mct420Device::executeGetValue;

    using Mct420Device::decodeGetValueDailyRead;

    using Mct420Device::decodeDisconnectConfig;
    using Mct420Device::decodeDisconnectStatus;
    using Mct420Device::isProfileTablePointerCurrent;

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
                analog->setMultiplier(1);
                analog->setDataOffset(0);
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
                accumulator->setMultiplier(1);
                accumulator->setDataOffset(0);
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

struct test_Mct420CL : test_Mct420Device
{
    test_Mct420CL() : test_Mct420Device(TYPEMCT420CL, "Test MCT-420CL")  {}
};

struct test_Mct420CLD : test_Mct420Device
{
    test_Mct420CLD() : test_Mct420Device(TYPEMCT420CLD, "Test MCT-420CLD")  {}
};

struct test_Mct420FL : test_Mct420Device
{
    test_Mct420FL() : test_Mct420Device(TYPEMCT420FL, "Test MCT-420FL")  {}
};

struct test_Mct420FLD : test_Mct420Device
{
    test_Mct420FLD() : test_Mct420Device(TYPEMCT420FLD, "Test MCT-420FLD")  {}
};


BOOST_AUTO_TEST_CASE(test_isSupported_DisconnectCollar)
{
    BOOST_CHECK_EQUAL(false, test_Mct420CL().test_isSupported(test_Mct420Device::test_Feature_DisconnectCollar));

    BOOST_CHECK_EQUAL(true,  test_Mct420FL().test_isSupported(test_Mct420Device::test_Feature_DisconnectCollar));

    BOOST_CHECK_EQUAL(false, test_Mct420CLD().test_isSupported(test_Mct420Device::test_Feature_DisconnectCollar));

    BOOST_CHECK_EQUAL(false, test_Mct420FLD().test_isSupported(test_Mct420Device::test_Feature_DisconnectCollar));
}

BOOST_AUTO_TEST_CASE(test_decodeDisconnectConfig)
{
    //  Test case permutations:
    //    MCT type:  MCT420CL, MCT420CLD, MCT420FL, MCT420FLD
    //    Config byte: autoreconnect disabled, autoreconnect enabled
    //  Config byte cannot be missing when the SSPEC is > ConfigReadEnhanced, since it returns the config byte

    struct test_case
    {
        const int mct_type;
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

        //  MCT-420CLD
        {TYPEMCT420CLD, 0x00, "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {TYPEMCT420CLD, 0x04, "Disconnect load limit connect delay: 34 minutes\n"
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

        //  MCT-420FLD
        {TYPEMCT420FLD, 0x00, "Disconnect load limit connect delay: 34 minutes\n"
                              "Disconnect verification threshold: 12.300 kW (205 Wh/minute)\n"
                              "Disconnect demand threshold disabled\n"
                              "Disconnect cycling mode disabled\n"},
        {TYPEMCT420FLD, 0x04, "Disconnect load limit connect delay: 34 minutes\n"
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

    CtiDeviceSingle::point_info pi;

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
        const CtiTime t(CtiDate(1, 1, 2011), 19, 16, 0);  //  1293930960 seconds (0x4D1FD1D0)

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
        const CtiTime t(CtiDate(1, 1, 2011), 1, 16, 0);  //  1293866160 seconds (0x4D1ED4B0)

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
        const CtiTime t(CtiDate(1, 1, 2011), 9, 16, 0);  //  1293894960 seconds (0x4D1F4530)

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
        const CtiTime t(CtiDate(1, 1, 2011), 12, 26, 0);  //  1293906360 seconds (0x4D1F71B8)

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

        BOOST_CHECK_EQUAL( NoMethod, test_Mct420CL().beginExecuteRequest(&request, parse, vgList, retList, outList) );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_centron_ratio)
    {
        CtiCommandParser parse("getconfig centron ratio");

        BOOST_CHECK_EQUAL( NoMethod, test_Mct420CL().beginExecuteRequest(&request, parse, vgList, retList, outList) );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_meter_parameters)
    {
        CtiCommandParser parse("getconfig meter parameters");

        BOOST_CHECK_EQUAL( NoError, test_Mct420CL().beginExecuteRequest(&request, parse, vgList, retList, outList) );

        BOOST_REQUIRE_EQUAL( outList.size(), 1 );

        const OUTMESS *om = outList.front();

        BOOST_CHECK_EQUAL( om->Buffer.BSt.IO,       Cti::Protocols::EmetconProtocol::IO_Function_Read );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Function, 0xf3 );
        BOOST_CHECK_EQUAL( om->Buffer.BSt.Length,   2 );
    }

    BOOST_AUTO_TEST_CASE(test_getconfig_meter_ratio)
    {
        CtiCommandParser parse("getconfig meter ratio");

        BOOST_CHECK_EQUAL( NoError, test_Mct420CL().beginExecuteRequest(&request, parse, vgList, retList, outList) );

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
    test_Mct420CL mct420;
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
    BOOST_AUTO_TEST_CASE(test_dev_mct420_getvalue_daily_reads_0kwh)
    {
        CtiTime timenow;

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , mct420.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( NoError , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , mct420.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( NoError , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , mct420.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( NoError , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , mct420.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( NoError , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , mct420.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( NoError , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , mct420.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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

            BOOST_CHECK_EQUAL( NoError , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , mct420.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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
        CtiTime timenow;

        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 21);  //  set the device to SSPEC revision 2.1
        mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_DailyReadInterestChannel, 1);

        {
            CtiCommandParser parse( "getvalue daily reads" );  //  most recent 6 daily reads

            BOOST_CHECK_EQUAL( NoError , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , mct420.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
        }

        {
            BOOST_REQUIRE_EQUAL( retList.size(),  1 );

            const CtiReturnMsg *retMsg = dynamic_cast<CtiReturnMsg *>(retList.front());

            BOOST_REQUIRE(retMsg);

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

            BOOST_CHECK_EQUAL( NoError , mct420.executeGetValue(&request, parse, om, vgList, retList, outList) );

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

            BOOST_CHECK_EQUAL( NoError , mct420.decodeGetValueDailyRead(&im, timenow, vgList, retList, outList) );
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
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Connect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4c);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_40)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x4d);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
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
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xf3);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
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
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_LongLoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x04);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_67)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_LongLoadProfile, BSt));
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
//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()
