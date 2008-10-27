#define BOOST_AUTO_TEST_MAIN "Test utility.h"

#include "yukon.h"
#include <boost/test/auto_unit_test.hpp>
#include <boost/test/unit_test.hpp>
using boost::unit_test_framework::test_suite;

#include "xml_object.h"

#include <string>
#include <iostream>
#include <sstream>

using std::string;
using std::stringstream;

BOOST_AUTO_UNIT_TEST(test_generating_xml_2)
{
	/* Expected Result 
	<TestTag name="tag1" addr="here">
	<ChildTag>
	</ChildTag>
	<ChildTag name="tag1">
	</ChildTag>
	</TestTag>
	*/
	string expected = "<TestTag name=\"tag1\" addr=\"here\">\n<ChildTag>\n</ChildTag>\n<ChildTag name=\"tag1\">\n</ChildTag>\n</TestTag>\n";

	XmlObject::XmlObjectSPtr xml(new XmlObject());

	xml->setTagName("TestTag");
	xml->insertParameter("name","tag1");
	xml->insertParameter("addr","here");

	XmlObject::XmlObjectSPtr c1_xml(new XmlObject());
	XmlObject::XmlObjectSPtr c2_xml(new XmlObject());

	c1_xml->setTagName("ChildTag");
	c2_xml->setTagName("ChildTag");
	c2_xml->insertParameter("name","tag1");

	xml->insertXmlNode(c1_xml);
	xml->insertXmlNode(c2_xml);

	stringstream ss(stringstream::out);

	/*Use String stream and compare to expected.*/
	xml->outputXml(ss);

	string output = ss.str();

	BOOST_CHECK_EQUAL(expected,output);
}

BOOST_AUTO_UNIT_TEST(test_generating_xml_3)
{
	/* Expected Result 
	<TestTag name="tag1" addr="here">
	<ChildTag>
	<Child2Tag name="tag1">
	</Child2Tag>
	</ChildTag> 
    </TestTag>
	*/
	string expected = "<TestTag name=\"tag1\" addr=\"here\">\n<ChildTag>\n<Child2Tag name=\"tag1\">\n</Child2Tag>\n</ChildTag>\n</TestTag>\n";

	XmlObject::XmlObjectSPtr xml(new XmlObject());

	xml->setTagName("TestTag");
	xml->insertParameter("name","tag1");
	xml->insertParameter("addr","here");

	XmlObject::XmlObjectSPtr c1_xml(new XmlObject());
	XmlObject::XmlObjectSPtr c2_xml(new XmlObject());

	c1_xml->setTagName("ChildTag");
	c2_xml->setTagName("Child2Tag");
	c2_xml->insertParameter("name","tag1");

	c1_xml->insertXmlNode(c2_xml);

	xml->insertXmlNode(c1_xml);


	stringstream ss(stringstream::out);

	/*Use String stream and compare to expected.*/
	xml->outputXml(ss);

	string output = ss.str();

	BOOST_CHECK_EQUAL(expected,output);
}
