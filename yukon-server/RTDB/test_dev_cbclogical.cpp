#include <boost/test/unit_test.hpp>

#include "dev_cbclogical.h"
#include "config_data_dnp.h"
#include "ctidate.h"

#include "rtdb_test_helpers.h"
#include "boost_test_helpers.h"

#include <boost/make_shared.hpp>

BOOST_AUTO_TEST_SUITE( test_dev_cbclogical )

using Cti::Test::byte_str;

struct TestCbcLogicalDevice : Cti::Devices::CbcLogicalDevice
{
    using CtiTblPAOLite::setID;
    using CtiDeviceBase::setName;

    CtiPointSPtr logicalPoint;
    std::string requestedPointName;

    CtiPointSPtr getDevicePointByName(const std::string& pointName) override
    {
        requestedPointName = pointName;

        return logicalPoint;
    }
};

struct beginExecuteRequest_helper
{
    CtiRequestMsg           request;
    std::list<CtiMessage*>  vgList, retList;
    std::list<OUTMESS*>     outList;

    boost::shared_ptr<Cti::Test::test_DeviceConfig> fixtureConfig;
    Cti::Test::Override_ConfigManager overrideConfigManager;

    beginExecuteRequest_helper() :
        fixtureConfig(new Cti::Test::test_DeviceConfig),
        overrideConfigManager(fixtureConfig)
    {
    }

    ~beginExecuteRequest_helper()
    {
        delete_container(vgList);
        delete_container(retList);
        delete_container(outList);
    }

    YukonError_t execute(CtiDeviceBase &dev, const std::string& command)
    {
        request.setCommandString(command);

        return dev.beginExecuteRequest(&request, CtiCommandParser{ command }, vgList, retList, outList);
    }
};

BOOST_FIXTURE_TEST_SUITE(test_control_commands, beginExecuteRequest_helper)

BOOST_AUTO_TEST_CASE(test_command_success)
{
    using Cti::Config::DNPStrings;

    fixtureConfig->addCategory(
        Cti::Config::Category::ConstructCategory(
            "cbcAttributeMapping",
            std::map<std::string, std::string> {
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix, "5" },
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix + ".0."
                    + DNPStrings::AttributeMappingConfiguration::AttributeMappings::Attribute, "CONTROL_POINT" },
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix + ".0."
                    + DNPStrings::AttributeMappingConfiguration::AttributeMappings::PointName, "Banana" },
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix + ".1."
                    + DNPStrings::AttributeMappingConfiguration::AttributeMappings::Attribute, "ENABLE_OVUV_CONTROL" },
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix + ".1."
                    + DNPStrings::AttributeMappingConfiguration::AttributeMappings::PointName, "Coconut" },
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix + ".2."
                    + DNPStrings::AttributeMappingConfiguration::AttributeMappings::Attribute, "ENABLE_TEMPERATURE_CONTROL" },
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix + ".2."
                    + DNPStrings::AttributeMappingConfiguration::AttributeMappings::PointName, "Durian" },
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix + ".3."
                    + DNPStrings::AttributeMappingConfiguration::AttributeMappings::Attribute, "ENABLE_TIME_CONTROL" },
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix + ".3."
                    + DNPStrings::AttributeMappingConfiguration::AttributeMappings::PointName, "Elderberry" },
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix + ".4."
                    + DNPStrings::AttributeMappingConfiguration::AttributeMappings::Attribute, "ENABLE_VAR_CONTROL" },
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix + ".4."
                    + DNPStrings::AttributeMappingConfiguration::AttributeMappings::PointName, "Fig" }}));

    TestCbcLogicalDevice dev;

    dev.logicalPoint.reset(Cti::Test::makeControlPoint(1729, 17, 172, 3458, ControlType_Normal));

    using P = CtiCommandParser;

    //  control open
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, execute(dev, "control open"));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Banana");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto req = dynamic_cast<const CtiRequestMsg*>(msg);

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->DeviceId(), 1729);
        BOOST_CHECK_EQUAL(req->CommandString(), "control open offset 3458");
    }
    delete_container(retList);
    retList.clear();
    //  control close
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, execute(dev, "control close"));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Banana");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto req = dynamic_cast<const CtiRequestMsg*>(msg);

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->DeviceId(), 1729);
        BOOST_CHECK_EQUAL(req->CommandString(), "control close offset 3458");
    }
    delete_container(retList);
    retList.clear();


    //  putconfig ovuv enable
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, execute(dev, "putconfig ovuv enable"));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Coconut");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto req = dynamic_cast<const CtiRequestMsg*>(msg);

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->DeviceId(), 1729);
        BOOST_CHECK_EQUAL(req->CommandString(), "control close offset 3458");
    }
    delete_container(retList);
    retList.clear();
    //  putconfig ovuv disable
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, execute(dev, "putconfig ovuv disable"));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Coconut");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto req = dynamic_cast<const CtiRequestMsg*>(msg);

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->DeviceId(), 1729);
        BOOST_CHECK_EQUAL(req->CommandString(), "control open offset 3458");
    }
    delete_container(retList);
    retList.clear();


    //  putconfig temp enable
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, execute(dev, "putconfig temp enable"));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Durian");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto req = dynamic_cast<const CtiRequestMsg*>(msg);

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->DeviceId(), 1729);
        BOOST_CHECK_EQUAL(req->CommandString(), "control close offset 3458");
    }
    delete_container(retList);
    retList.clear();
    //  putconfig temp disable
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, execute(dev, "putconfig temp disable"));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Durian");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto req = dynamic_cast<const CtiRequestMsg*>(msg);

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->DeviceId(), 1729);
        BOOST_CHECK_EQUAL(req->CommandString(), "control open offset 3458");
    }
    delete_container(retList);
    retList.clear();


    //  putconfig time enable
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, execute(dev, "putconfig time enable"));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Elderberry");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto req = dynamic_cast<const CtiRequestMsg*>(msg);

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->DeviceId(), 1729);
        BOOST_CHECK_EQUAL(req->CommandString(), "control close offset 3458");
    }
    delete_container(retList);
    retList.clear();
    //  putconfig time disable
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, execute(dev, "putconfig time disable"));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Elderberry");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto req = dynamic_cast<const CtiRequestMsg*>(msg);

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->DeviceId(), 1729);
        BOOST_CHECK_EQUAL(req->CommandString(), "control open offset 3458");
    }
    delete_container(retList);
    retList.clear();


    //  putconfig var enable
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, execute(dev, "putconfig var enable"));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Fig");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto req = dynamic_cast<const CtiRequestMsg*>(msg);

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->DeviceId(), 1729);
        BOOST_CHECK_EQUAL(req->CommandString(), "control close offset 3458");
    }
    delete_container(retList);
    retList.clear();
    //  putconfig var disable
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, execute(dev, "putconfig var disable"));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Fig");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto req = dynamic_cast<const CtiRequestMsg*>(msg);

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->DeviceId(), 1729);
        BOOST_CHECK_EQUAL(req->CommandString(), "control open offset 3458");
    }
    delete_container(retList);
    retList.clear();

    //  ping
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, execute(dev, "ping"));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Banana");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto req = dynamic_cast<const CtiRequestMsg*>(msg);

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->DeviceId(), 1729);
        BOOST_CHECK_EQUAL(req->CommandString(), "ping");
    }
    delete_container(retList);
    retList.clear();

    //  scan integrity
    {
        BOOST_CHECK_EQUAL(ClientErrors::None, execute(dev, "scan integrity"));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Banana");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto req = dynamic_cast<const CtiRequestMsg*>(msg);

        BOOST_REQUIRE(req);

        BOOST_CHECK_EQUAL(req->DeviceId(), 1729);
        BOOST_CHECK_EQUAL(req->CommandString(), "scan integrity");
    }
    delete_container(retList);
    retList.clear();
}

BOOST_AUTO_TEST_CASE(test_command_fail)
{
    using Cti::Config::DNPStrings;

    fixtureConfig->addCategory(
        Cti::Config::Category::ConstructCategory(
            "cbcAttributeMapping",
            std::map<std::string, std::string> {
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix, "1" },
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix + ".0."
                    + DNPStrings::AttributeMappingConfiguration::AttributeMappings::Attribute, "CONTROL_POINT" },
                { DNPStrings::AttributeMappingConfiguration::AttributeMappings_Prefix + ".0."
                    + DNPStrings::AttributeMappingConfiguration::AttributeMappings::PointName, "Banana" }}));

    TestCbcLogicalDevice dev;
    
    dev.setID(1776, test_tag);
    dev.setName("George Washington", test_tag);

    //  Missing control point mapping
    {
        CtiCommandParser parse("putconfig ovuv enable");

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

        BOOST_REQUIRE(ret);

        BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
        BOOST_CHECK_EQUAL(ret->DeviceId(), 1776);
        BOOST_CHECK_EQUAL(ret->ResultString(), 
            "George Washington / No control offset name"
            "\nAttribute : ENABLE_OVUV_CONTROL");
    }
    delete_container(retList);
    retList.clear();
    
    //  Missing control point
    {
        CtiCommandParser parse("control open");

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

        BOOST_REQUIRE(ret);

        BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
        BOOST_CHECK_EQUAL(ret->DeviceId(), 1776);
        BOOST_CHECK_EQUAL(ret->ResultString(),
            "George Washington / Override point not found"
            "\nOverride point name : Banana"
            "\nAttribute           : CONTROL_POINT");
    }
    delete_container(retList);
    retList.clear();
    {
        CtiCommandParser parse("ping");

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

        BOOST_REQUIRE(ret);

        BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
        BOOST_CHECK_EQUAL(ret->DeviceId(), 1776);
        BOOST_CHECK_EQUAL(ret->ResultString(),
            "George Washington / Override point not found"
            "\nOverride point name : Banana"
            "\nAttribute           : CONTROL_POINT");
    }
    delete_container(retList);
    retList.clear();
    {
        CtiCommandParser parse("scan integrity");

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

        BOOST_REQUIRE(ret);

        BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
        BOOST_CHECK_EQUAL(ret->DeviceId(), 1776);
        BOOST_CHECK_EQUAL(ret->ResultString(),
            "George Washington / Override point not found"
            "\nOverride point name : Banana"
            "\nAttribute           : CONTROL_POINT");
    }
    delete_container(retList);
    retList.clear();

    //  Not a status point
    {
        dev.logicalPoint.reset(Cti::Test::makeAnalogPoint(2, 22, 221));

        CtiCommandParser parse("control open");

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Banana");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

        BOOST_REQUIRE(ret);

        BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
        BOOST_CHECK_EQUAL(ret->DeviceId(), 1776);
        BOOST_CHECK_EQUAL(ret->ResultString(), 
            "George Washington / Control offset override point not Status type"
            "\nOverride point name : Banana"
            "\nOverride point ID   : 22"
            "\nOverride device ID  : 2"
            "\nOverride point type : Analog"
            "\nAttribute           : CONTROL_POINT");
    }
    delete_container(retList);
    retList.clear();

    //  No control information
    {
        dev.logicalPoint.reset(Cti::Test::makeStatusPoint(2, 22, 221));

        CtiCommandParser parse("control open");

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Banana");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

        BOOST_REQUIRE(ret);

        BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
        BOOST_CHECK_EQUAL(ret->DeviceId(), 1776);
        BOOST_CHECK_EQUAL(ret->ResultString(), 
            "George Washington / Control offset override point does not have control parameters"
            "\nOverride point name : Banana"
            "\nOverride point ID   : 22"
            "\nOverride device ID  : 2"
            "\nAttribute           : CONTROL_POINT");
    }
    delete_container(retList);
    retList.clear();

    //  Invalid control offset
    {
        dev.logicalPoint.reset(Cti::Test::makeControlPoint(2, 22, 221, -17, ControlType_Normal));

        CtiCommandParser parse("control open");

        BOOST_CHECK_EQUAL(ClientErrors::None, dev.beginExecuteRequest(&request, parse, vgList, retList, outList));

        BOOST_CHECK_EQUAL(dev.requestedPointName, "Banana");

        BOOST_CHECK(outList.empty());
        BOOST_CHECK(vgList.empty());
        BOOST_REQUIRE_EQUAL(retList.size(), 1);

        const auto msg = retList.front();

        BOOST_REQUIRE(msg);

        const auto ret = dynamic_cast<const CtiReturnMsg*>(msg);

        BOOST_REQUIRE(ret);

        BOOST_CHECK_EQUAL(ret->ExpectMore(), false);
        BOOST_CHECK_EQUAL(ret->DeviceId(), 1776);
        BOOST_CHECK_EQUAL(ret->ResultString(),
            "George Washington / Control offset override not valid"
            "\nOverride point name     : Banana"
            "\nOverride point ID       : 22"
            "\nOverride device ID      : 2"
            "\nOverride control offset : -17"
            "\nAttribute               : CONTROL_POINT");
    }
    delete_container(retList);
    retList.clear();
}

BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()