#include "dev_mct420.h"
#include "devicetypes.h"

#include "boost_test_helpers.h"

#define BOOST_TEST_MAIN "Test dev_mct420"
#include <boost/test/unit_test.hpp>

using boost::unit_test_framework::test_suite;

using Cti::Devices::Mct420Device;
using std::string;

struct test_Mct420Device : Mct420Device
{
    using CtiTblPAOLite::_type;
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
            default:  return false;  //  to eliminate compile warning C4715
        }
    }
};

BOOST_AUTO_TEST_CASE(test_dev_mct420_isSupported_DisconnectCollar)
{
    {
        test_Mct420Device mct420;

        mct420._type = TYPEMCT420CL;

        BOOST_CHECK_EQUAL(false, mct420.test_isSupported(test_Mct420Device::test_Feature_DisconnectCollar));
    }

    {
        test_Mct420Device mct420;

        mct420._type = TYPEMCT420FL;

        BOOST_CHECK_EQUAL(true,  mct420.test_isSupported(test_Mct420Device::test_Feature_DisconnectCollar));
    }

    {
        test_Mct420Device mct420;

        mct420._type = TYPEMCT420CLD;

        BOOST_CHECK_EQUAL(false, mct420.test_isSupported(test_Mct420Device::test_Feature_DisconnectCollar));
    }

    {
        test_Mct420Device mct420;

        mct420._type = TYPEMCT420FLD;

        BOOST_CHECK_EQUAL(false, mct420.test_isSupported(test_Mct420Device::test_Feature_DisconnectCollar));
    }
}

BOOST_AUTO_TEST_CASE(test_dev_mct420_decodeDisconnectConfig)
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

    const unsigned count = sizeof(test_cases) / sizeof(*test_cases);

    struct my_test
    {
        typedef test_case test_case_type;
        typedef string    result_type;

        result_type operator()(const test_case_type &tc)
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

            test_Mct420Device mct420;

            mct420._type = tc.mct_type;

            mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_SSpecRevision, 40);
            mct420.setDynamicInfo(CtiTableDynamicPaoInfo::Key_MCT_Configuration, tc.config_byte);

            return mct420.decodeDisconnectConfig(DSt);
        }
    };

    Cti::TestRunner<my_test> tr(test_cases, test_cases + count);

    BOOST_CHECK_EQUAL_COLLECTIONS(tr.expected_begin(), tr.expected_end(),
                                  tr.results_begin(),  tr.results_end());

}


BOOST_AUTO_TEST_CASE(test_dev_mct410_decodeDisconnectStatus)
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

    const unsigned count = sizeof(test_cases) / sizeof(*test_cases);

    struct my_test
    {
        typedef test_case test_case_type;
        typedef string    result_type;

        result_type operator()(const test_case_type &tc)
        {
            test_Mct420Device mct420;

            DSTRUCT DSt;

            DSt.Message[0] = tc.dst_message_0;
            DSt.Message[1] = tc.dst_message_1;
            DSt.Message[8] = tc.dst_message_8;

            return mct420.decodeDisconnectStatus(DSt);
        }
    };

    Cti::TestRunner<my_test> tr(test_cases, test_cases + count);

    BOOST_CHECK_EQUAL_COLLECTIONS(tr.expected_begin(), tr.expected_end(),
                                  tr.results_begin(),  tr.results_end());
}

BOOST_AUTO_TEST_CASE(test_dev_mct420_isProfileTablePointerCurrent)
{
    test_Mct420Device dev;

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
