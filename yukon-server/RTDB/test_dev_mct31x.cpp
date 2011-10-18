#include "dev_mct31x.h"
#include "devicetypes.h"

#define BOOST_TEST_MAIN
#include <boost/test/unit_test.hpp>

using Cti::Devices::Mct31xDevice;
using Cti::Protocols::EmetconProtocol;

struct test_Mct31xDevice : Mct31xDevice
{
    using MctDevice::getOperation;
    using CtiTblPAOLite::_type;
};


struct getOperation_helper
{
    test_Mct31xDevice mct;
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
    BOOST_AUTO_TEST_CASE(test_getOperation_13)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x49);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    //  MCT-310 commands
    BOOST_AUTO_TEST_CASE(test_getOperation_14)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Time, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x7a);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_15)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_DemandInterval, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1b);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_16)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_LoadProfileInterval, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x76);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_17)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Multiplier, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x26);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_18)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Options, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x02);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_19)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_GroupAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_20)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_UniqueAddress, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x0a);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_21)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_GroupAddress_GoldSilver, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x14);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_22)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_GroupAddress_Bronze, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x10);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_23)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_GroupAddress_Lead, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x11);
        BOOST_CHECK_EQUAL(BSt.Length,   3);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_24)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_DemandInterval, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x1b);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_25)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_LoadProfileInterval, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x70);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_26)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Multiplier, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x26);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_27)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_TSync, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x49);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_28)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_OnOffPeak, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x03);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_29)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_MinMax, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x03);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_30)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_Accum, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x90);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_31)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_KWH, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x90);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_32)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Internal, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x05);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_33)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_Reset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x06);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_34)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_PFCount, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x07);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_35)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_ResetPFCount, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x07);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_36)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_PeakOn, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x5a);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_37)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_PeakOff, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x59);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_38)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeOne, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x40);  //  Q_ARMS
        BOOST_CHECK_EQUAL(BSt.Function, 0x51);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_39)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutStatus_FreezeTwo, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x40);  //  Q_ARMS
        BOOST_CHECK_EQUAL(BSt.Function, 0x52);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    //  Overridden by MCT-31x
    /*
    BOOST_AUTO_TEST_CASE(test_getOperation_40)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_Integrity, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x2c);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_41)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Demand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x2c);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    */
    BOOST_AUTO_TEST_CASE(test_getOperation_42)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_KYZ, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x20);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    //  Overridden by MCT-31x
    /*
    BOOST_AUTO_TEST_CASE(test_getOperation_43)
    {
        mct._type = TYPEMCT310;
        BOOST_REQUIRE( ! mct.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));

        mct._type = TYPEMCT310ID;
        BOOST_REQUIRE( ! mct.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));

        //  only valid for 310IL

        mct._type = TYPEMCT310IL;
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));

        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);

        mct._type = TYPEMCT310IDL;
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));

        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    */
    BOOST_AUTO_TEST_CASE(test_getOperation_44)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_Disconnect, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x05);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_45)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x70);
        BOOST_CHECK_EQUAL(BSt.Length,   9);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_46)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Control_Latch, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write | 0x20);
        BOOST_CHECK_EQUAL(BSt.Function, 0x48);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    //  MCT-31x functions
    BOOST_AUTO_TEST_CASE(test_getOperation_47)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_General, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_48)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_Integrity, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   7);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_49)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_Demand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   7);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_50)
    {
        mct._type = TYPEMCT318;
        BOOST_REQUIRE( ! mct.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));

        mct._type = TYPEMCT360;
        BOOST_REQUIRE( ! mct.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));

        mct._type = TYPEMCT370;
        BOOST_REQUIRE( ! mct.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));

        mct._type = TYPEMCT318L;
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::Scan_LoadProfile, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_51)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_PeakDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x93);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_52)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_FrozenPeakDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x94);
        BOOST_CHECK_EQUAL(BSt.Length,   12);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_53)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_KYZ2, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x32);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_54)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_KYZ3, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x51);
        BOOST_CHECK_EQUAL(BSt.Length,   6);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_55)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_External, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x92);
        BOOST_CHECK_EQUAL(BSt.Length,   1);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_56)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Multiplier2, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x38);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_57)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_Multiplier3, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x57);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_58)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Multiplier2, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x38);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_59)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_Multiplier3, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x57);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_60)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_IEDScan, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0x74);
        BOOST_CHECK_EQUAL(BSt.Length,   8);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_61)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_IEDScan, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x74);
        BOOST_CHECK_EQUAL(BSt.Length,   2);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_62)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutConfig_IEDClass, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x76);
        BOOST_CHECK_EQUAL(BSt.Length,   4);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_63)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetConfig_IEDTime, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xaa);
        BOOST_CHECK_EQUAL(BSt.Length,   11);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_64)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_IEDDemand, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xa1);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_65)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_IEDKwh, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xa2);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_66)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_IEDKvarh, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xa7);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_67)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetValue_IEDKvah, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xa7);
        BOOST_CHECK_EQUAL(BSt.Length,   13);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_68)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::PutValue_IEDReset, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Write);
        BOOST_CHECK_EQUAL(BSt.Function, 0x00);
        BOOST_CHECK_EQUAL(BSt.Length,   0);
    }
    BOOST_AUTO_TEST_CASE(test_getOperation_69)
    {
        BOOST_REQUIRE(mct.getOperation(EmetconProtocol::GetStatus_IEDLink, BSt));
        BOOST_CHECK_EQUAL(BSt.IO, EmetconProtocol::IO_Function_Read);
        BOOST_CHECK_EQUAL(BSt.Function, 0xa0);
        BOOST_CHECK_EQUAL(BSt.Length,   5);
    }

//}  Brace matching for BOOST_FIXTURE_TEST_SUITE
BOOST_AUTO_TEST_SUITE_END()
