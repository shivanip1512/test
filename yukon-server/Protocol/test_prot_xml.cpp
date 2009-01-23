
#include "yukon.h"
#include <boost/test/unit_test.hpp>

#include <string>
#include <iostream>
#include <sstream>

#include "prot_xml.h"

using std::string;
using std::stringstream;

#define BOOST_AUTO_TEST_MAIN "Test XML Protocol"
#include <boost/test/auto_unit_test.hpp>
using boost::unit_test_framework::test_suite;

using namespace Cti::Protocol;

BOOST_AUTO_UNIT_TEST(test_prot_xml_recvComm)
{
    int _groupId = 42;
    string _commandString = "control shed 60m";
    XmlObject::XmlObjectSPtr xml;

    OUTMESS * OutMessage = new OUTMESS();
    memcpy(OutMessage->Request.CommandStr,_commandString.c_str(),_commandString.size());
    OutMessage->DeviceIDofLMGroup = _groupId;

    XmlProtocolSPtr _xmlProtocol = XmlProtocolSPtr(new XmlProtocol()); 

    _xmlProtocol->recvCommRequest(OutMessage);

    string result = _xmlProtocol->getCommandString();
    BOOST_CHECK_EQUAL(_commandString,result);

    int gid = _xmlProtocol->getGroupId();
    BOOST_CHECK_EQUAL(_groupId,gid);

    delete OutMessage;
}

BOOST_AUTO_UNIT_TEST(test_prot_xml_genereate_xmloutput)
{
    string _commandString = "control shed 60m";
    XmlObject::XmlObjectSPtr xml;

    OUTMESS * OutMessage = new OUTMESS();
    memcpy(OutMessage->Request.CommandStr,_commandString.c_str(),_commandString.size());

    //from test cmd parse
    //"command=8:flags=1024::sa_dlc_mode=(none),1,1.000:sa_f0bit=(none),0,0.000:sa_f1bit=(none),0,0.000:shed=(none),3600,3600.000:type=versacom,0,0.000"
    string expected = "<XML_COMMAND command=\"8\" flags=\"1024\"><XML_DATA sa_dlc_mode=\"(none)\"><INT>1</INT><REAL>1.000</REAL></XML_DATA><XML_DATA sa_f0bit=\"(none)\"><INT>0</INT><REAL>0.000</REAL></XML_DATA><XML_DATA sa_f1bit=\"(none)\"><INT>0</INT><REAL>0.000</REAL></XML_DATA><XML_DATA shed=\"(none)\"><INT>3600</INT><REAL>3600.000</REAL></XML_DATA><XML_DATA type=\"versacom\"><INT>0</INT><REAL>0.000</REAL></XML_DATA></XML_COMMAND>";
    XmlProtocolSPtr _xmlProtocol = XmlProtocolSPtr(new XmlProtocol()); 

    //disable database hits for unit testing.
    _xmlProtocol->setCheckDatabase(false);

    //Do protocal stuff to generate xml
    _xmlProtocol->recvCommRequest(OutMessage);

    CtiXfer xfer;//Blank this is not being tested in this test case. 

    int ok = _xmlProtocol->generate(xfer);
    BOOST_REQUIRE( ok == NoError);//NoError from yukon.h 

    //Get XML from protocol.
    xml = _xmlProtocol->getXmlObject();

    stringstream ss(stringstream::out);
    //Use String stream and compare to expected.
    xml->outputXml(ss);

    string output = ss.str();
    BOOST_CHECK_EQUAL(expected,output);
}

BOOST_AUTO_UNIT_TEST(test_prot_xml_generate_xfer)
{
    string _commandString = "control shed 60m";
    string expected = "<XML_COMMAND command=\"8\" flags=\"1024\"><XML_DATA sa_dlc_mode=\"(none)\"><INT>1</INT><REAL>1.000</REAL></XML_DATA><XML_DATA sa_f0bit=\"(none)\"><INT>0</INT><REAL>0.000</REAL></XML_DATA><XML_DATA sa_f1bit=\"(none)\"><INT>0</INT><REAL>0.000</REAL></XML_DATA><XML_DATA shed=\"(none)\"><INT>3600</INT><REAL>3600.000</REAL></XML_DATA><XML_DATA type=\"versacom\"><INT>0</INT><REAL>0.000</REAL></XML_DATA></XML_COMMAND>";
    XmlObject::XmlObjectSPtr xml;

    OUTMESS * OutMessage = new OUTMESS();
    memcpy(OutMessage->Request.CommandStr,_commandString.c_str(),_commandString.size());

    XmlProtocolSPtr _xmlProtocol = XmlProtocolSPtr(new XmlProtocol()); 

    //disable database hits for unit testing.
    _xmlProtocol->setCheckDatabase(false);

    //Setup xmlProtocol with command
    _xmlProtocol->recvCommRequest(OutMessage);

    CtiXfer xfer;

    int ok = _xmlProtocol->generate(xfer);
    BOOST_REQUIRE( ok == NoError);//NoError from yukon.h 

    string output((char*)xfer.getOutBuffer(),xfer.getOutCount());

    //Test to see if xfer is setup correctly
    BOOST_CHECK_EQUAL(expected,output);
}
