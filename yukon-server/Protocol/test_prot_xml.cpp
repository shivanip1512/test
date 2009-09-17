
#include "yukon.h"

#define BOOST_AUTO_TEST_MAIN "Test prot_xml"

#include <boost/test/unit_test.hpp>

#include <string>
#include <iostream>
#include <sstream>

#include "prot_xml.h"
#include "cmdparse.h"
#include <fstream>

using namespace std;

using boost::unit_test_framework::test_suite;

using namespace Cti::Protocols;

BOOST_AUTO_TEST_CASE(test_Shed_Control)
{
    XmlProtocol xmlProtocol;

    string expected = "<loadGroupAction xmlns=\"http://yukon.cannontech.com/api\"><groupParametersList><groupParameter name=\"Param1\">Value1</groupParameter><groupParameter name=\"Param2\">Value2</groupParameter></groupParametersList><command><raw commandeType=\"EXPRESSCOM\">abcdefg</raw><timedControl><time>300</time><relays><relay>1</relay><relay>3</relay></relays></timedControl></command></loadGroupAction>";
    string result;

    CtiCommandParser parse("control shed 5m");
    parse.setValue("relaymask", 0x05);

    string rawAscii = "abcdefg";

    std::vector<std::pair<string,string> > params;

    params.push_back(make_pair("Param1","Value1"));
    params.push_back(make_pair("Param2","Value2"));

    result = xmlProtocol.createMessage(parse,rawAscii,params);

    BOOST_CHECK_EQUAL(expected,result);
}

BOOST_AUTO_TEST_CASE(test_Restore_Control)
{
    XmlProtocol xmlProtocol;

    string expected = "<loadGroupAction xmlns=\"http://yukon.cannontech.com/api\"><groupParametersList><groupParameter name=\"Param1\">Value1</groupParameter><groupParameter name=\"Param2\">Value2</groupParameter></groupParametersList><command><raw commandeType=\"EXPRESSCOM\">abcdefg</raw><restoreControl><delayTime>0</delayTime><randomizeTime>0</randomizeTime><stopCycle>false</stopCycle><relays><relay>15</relay></relays></restoreControl></command></loadGroupAction>";
    string result;

    CtiCommandParser parse("control restore");
    parse.setValue("relaymask", 0x4000);

    string rawAscii = "abcdefg";
    std::vector<std::pair<string,string> > params;

    params.push_back(make_pair("Param1","Value1"));
    params.push_back(make_pair("Param2","Value2"));

    result = xmlProtocol.createMessage(parse,rawAscii, params);

    BOOST_CHECK_EQUAL(expected,result);
}

BOOST_AUTO_TEST_CASE(test_Standard_Cycle_Control)
{
    XmlProtocol xmlProtocol;

    string expected = "<loadGroupAction xmlns=\"http://yukon.cannontech.com/api\"><groupParametersList/><command><raw commandeType=\"EXPRESSCOM\">abcdefg</raw><cycleControl><startDelay>0</startDelay><type>standardcycle</type><percent>5</percent><period>30</period><count>8</count><ramp>true</ramp><relays><relay>15</relay></relays></cycleControl></command></loadGroupAction>";
    string result;

    CtiCommandParser parse("control cycle 5m");
    parse.setValue("relaymask", 0x4000);

    string rawAscii = "abcdefg";
    std::vector<std::pair<string,string> > params;

    result = xmlProtocol.createMessage(parse,rawAscii, params);

    BOOST_CHECK_EQUAL(expected,result);
}

BOOST_AUTO_TEST_CASE(test_True_Cycle_Control)
{
    XmlProtocol xmlProtocol;

    string expected = "<loadGroupAction xmlns=\"http://yukon.cannontech.com/api\"><groupParametersList/><command><raw commandeType=\"EXPRESSCOM\">abcdefg</raw><cycleControl><startDelay>0</startDelay><type>truecycle</type><percent>5</percent><period>30</period><count>8</count><ramp>true</ramp><relays><relay>15</relay></relays></cycleControl></command></loadGroupAction>";
    string result;

    CtiCommandParser parse("control cycle 5m");
    parse.setValue("relaymask", 0x4000);
    parse.setValue("xctruecycle", 1);

    string rawAscii = "abcdefg";
    std::vector<std::pair<string,string> > params;

    result = xmlProtocol.createMessage(parse,rawAscii, params);

    BOOST_CHECK_EQUAL(expected,result);
}

BOOST_AUTO_TEST_CASE(test_Target_Cycle_Control)
{
    XmlProtocol xmlProtocol;

    string expected = "<loadGroupAction xmlns=\"http://yukon.cannontech.com/api\"><groupParametersList/><command><raw commandeType=\"EXPRESSCOM\">abcdefg</raw><cycleControl><startDelay>0</startDelay><type>targetcycle</type><percent>5</percent><period>30</period><count>8</count><ramp>true</ramp><relays><relay>15</relay></relays></cycleControl></command></loadGroupAction>";
    string result;

    CtiCommandParser parse("control cycle 5m");
    parse.setValue("relaymask", 0x4000);
    parse.setValue("xctargetcycle", 1);

    string rawAscii = "abcdefg";
    std::vector<std::pair<string,string> > params;

    result = xmlProtocol.createMessage(parse,rawAscii, params);

    BOOST_CHECK_EQUAL(expected,result);
}

BOOST_AUTO_TEST_CASE(test_Unknown_Control)
{
    XmlProtocol xmlProtocol;

    string expected = "";
    string result;

    CtiCommandParser parse("putconfig emetcon install all");

    string rawAscii = "";
    std::vector<std::pair<string,string> > params;

    result = xmlProtocol.createMessage(parse,rawAscii, params);

    BOOST_CHECK_EQUAL(expected,result);
}

