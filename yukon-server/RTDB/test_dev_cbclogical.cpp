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
    CtiPointSPtr logicalPoint;
    std::string requestedPointName;

    CtiPointSPtr getLogicalPoint(const std::string& pointName) override
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
        return dev.beginExecuteRequest(&request, CtiCommandParser{ command }, vgList, retList, outList);
    }
};

BOOST_FIXTURE_TEST_SUITE(test_control_commands, beginExecuteRequest_helper)

BOOST_AUTO_TEST_CASE(test_control_success)
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
}

BOOST_AUTO_TEST_SUITE_END()

BOOST_AUTO_TEST_SUITE_END()