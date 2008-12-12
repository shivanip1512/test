#include "yukon.h"


#include "logger.h"
#include "utility.h"
#include "porter.h"
#include "prot_xml.h"
#include "cmdparse.h"
#include "numstr.h"
#include <sstream>

namespace Cti       {
namespace Protocol  {

using std::stringstream;


XmlProtocol::XmlProtocol()
{
	complete = false;
	checkDatabase = true;
}

/**
 * Generate the xfer message from the command string. 
 * 
 * @param xfer 
 * 
 * @return int 
 */
int XmlProtocol::generate(CtiXfer &xfer)
{
	int retVal = NoError;
	char tempArray[(sizeof(int)*8+1)];//makes sure this is big enough for any number
	CtiCommandParser parser(commandString); 

	//grab by reference to save a copy.
	CtiCommandParser::map_type parseMap = parser.Map();
	XmlObject::XmlObjectSPtr xmlBaseObject(new XmlObject());

	xmlBaseObject->setTagName("XML_COMMAND");
	xmlBaseObject->insertParameter("command",string(itoa(parser.getCommand(),tempArray,10)));
	xmlBaseObject->insertParameter("flags",string(itoa(parser.getFlags(),tempArray,10)));

	if(getCheckDatabase())
	{
		generateDataFromDatabase(xmlBaseObject);
	}

	/* Add fields from the CMD Parse */
	for (CtiCommandParser::map_itr_type itr = parseMap.begin(); itr != parseMap.end(); ++itr)
	{
		XmlObject::XmlObjectSPtr xmlChild(new XmlObject());
		XmlObject::XmlObjectSPtr xmlData1(new XmlObject());
		XmlObject::XmlObjectSPtr xmlData2(new XmlObject());

		string paramName = itr->first;
		string paramValue = (itr->second.getString().empty())?"(none)":itr->second.getString();
		string data1 = CtiNumStr(itr->second.getInt());
		string data2 = CtiNumStr(itr->second.getReal());

		xmlChild->insertParameter(paramName,paramValue);
		xmlChild->setTagName("XML_DATA");
		xmlData1->setTagName("INT");
		xmlData2->setTagName("REAL");

		xmlData1->insertData(data1);
		xmlData2->insertData(data2);

		xmlChild->insertXmlNode(xmlData1);
		xmlChild->insertXmlNode(xmlData2);
		xmlBaseObject->insertXmlNode(xmlChild);
	}

	xmlObject = xmlBaseObject;
	complete = true;

	int size = 0;
	BYTE * buf = generateByteBuffer(size);

	xfer.setOutBuffer(buf);
	xfer.setOutCount(size);

	/* needed? */
	xfer.setCRCFlag(0);

	return retVal;
}

void XmlProtocol::generateDataFromDatabase(XmlObject::XmlObjectSPtr xmlBaseObject)
{
	//Database Call here:
	std::vector< std::vector<string> > params = getLmXmlParametersByGroupId(getGroupId());
	std::vector< std::vector<string> >::iterator itr;

	/* Add fields from the database */
	for( itr = params.begin(); itr != params.end(); ++itr)
	{
		if (itr->size() != 2)
		{
			//incorrect number of fields in parameter. Skipping
			CtiLockGuard<CtiLogger> doubt_guard(dout);
			dout << CtiTime() << " Skipping due to incorrect number of fields in parameter. Expecting 2, got " << itr->size() << endl;
		}
		else
		{
			XmlObject::XmlObjectSPtr xmlChild(new XmlObject());
			string paramName = (*itr)[0];
			string paramValue = (*itr)[1];
			xmlChild->insertParameter(paramName,paramValue);
			xmlChild->setTagName("XML_DATA");
			xmlBaseObject->insertXmlNode(xmlChild);
		}
	}
}

BYTE* XmlProtocol::generateByteBuffer(int & size)
{	
	BYTE * buffer = NULL;

	if (complete == true)
	{
		//generate buffer
		stringstream ss(stringstream::out);
		//Use String stream and compare to expected.
		xmlObject->outputXml(ss);

		string output = ss.str();
		size = output.size();

		buffer = new BYTE [size];
		memcpy(buffer,output.begin(),output.size());
	}

	return buffer;
}

bool XmlProtocol::isTransactionComplete( void ) const 
{
	return getComplete();
}

/* Not sure what happens in here.*/
int XmlProtocol::decode( CtiXfer &xfer, int status )
{
	int retVal = NoError;

	return retVal;
}

void XmlProtocol::setComplete(bool comp)
{
	complete = comp;
}

bool XmlProtocol::getComplete() const
{
	return complete;
}

void XmlProtocol::setXmlObject(XmlObject::XmlObjectSPtr & xmlPtr)
{
	xmlObject = xmlPtr;
}

XmlObject::XmlObjectSPtr & XmlProtocol::getXmlObject()
{
	return xmlObject;
}

/**
 * Take the OutMessage and form XML objects for generate to 
 * process. 
 * 
 * @param OutMessage 
 * 
 * @return int 
 */
int XmlProtocol::recvCommRequest( OUTMESS *OutMessage )
{
	int retVal = NoError;

	commandString = string(OutMessage->Request.CommandStr);
	groupId = OutMessage->DeviceIDofLMGroup;

	//Test success of on Strlen?

	return retVal;
}

string& XmlProtocol::getCommandString()
{
	return commandString;
}

void XmlProtocol::setCheckDatabase(bool flag)
{
	checkDatabase = flag;
}

bool XmlProtocol::getCheckDatabase()
{
	return checkDatabase;
}

int XmlProtocol::getGroupId()
{
	return groupId;
}

void XmlProtocol::setGroupId(int gid)
{
	groupId = gid;
}

}//end Protocol namespace
}//end Cti namespace
