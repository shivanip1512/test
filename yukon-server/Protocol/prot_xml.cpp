#include "yukon.h"

#include "logger.h"
#include "utility.h"
#include "porter.h"
#include "prot_xml.h"
#include "cmdparse.h"
#include "numstr.h"
#include <sstream>

#include <xercesc/util/PlatformUtils.hpp>
#include <xercesc/framework/MemBufFormatTarget.hpp>
#include <xercesc/dom/DOM.hpp>

XERCES_CPP_NAMESPACE_USE

namespace Cti {
namespace Protocols {

using namespace std;
/*
string XmlProtocol::createMessage(const CtiCommandParser &parse, const string &rawAscii, const vector<pair<string, string> > &params)
{
    stringstream ss;

    ss << "<commandstring>" << parse.getCommandStr() << "</commandstring>\n";
    ss << "<rawascii>" << rawAscii << "</rawascii>\n";

    vector<pair<string, string> >::const_iterator itr = params.begin();

    for( ; itr != params.end(); ++itr )
    {
        ss << "<" << itr->first << ">" << itr->second << "</" << itr->first << ">\n";
    }

    return ss.str();
}
*/

string XmlProtocol::createMessage(const CtiCommandParser &parse, const string &rawAscii, const vector<pair<string, string> > &params)
{
    // Initilize Xerces.
    XMLPlatformUtils::Initialize();

    DOMImplementation * pDOMImplementation = NULL;

    //leak?
    pDOMImplementation = DOMImplementationRegistry::getDOMImplementation(XMLString::transcode("core"));

    DOMDocument * pDOMDocument = NULL;

    /*Create an empty DOMDocument.*/
    pDOMDocument = pDOMImplementation->createDocument(0,L"loadGroupAction", 0);
    DOMElement * rootElement = NULL;
    rootElement = pDOMDocument->getDocumentElement();

    rootElement->setAttribute(L"xmlns",L"http://yukon.cannontech.com/api");
    DOMElement * groupParametersListNode = NULL;
    groupParametersListNode = pDOMDocument->createElement(L"groupParametersList");

    for (vector<pair<string, string> >::const_iterator itr = params.begin(); itr != params.end();itr++) {
        string paramName = (*itr).first;
        string paramValue = (*itr).second;

        DOMElement * groupParameterNode = pDOMDocument->createElement(L"groupParameter");
        groupParameterNode->setAttribute(L"name",XMLString::transcode(paramName.c_str()));
        DOMText* groupParameterText = NULL;
        groupParameterText = pDOMDocument->createTextNode(XMLString::transcode(paramValue.c_str()));
        groupParameterNode->appendChild(groupParameterText);
        groupParametersListNode->appendChild(groupParameterNode);
    }

    rootElement->appendChild(groupParametersListNode);

    unsigned int ctlReq = CMD_FLAG_CTL_ALIASMASK & parse.getFlags();
    int relaymask = parse.getiValue("relaymask", 0);

    DOMElement * commandNode = pDOMDocument->createElement(L"command");
    DOMElement * raw = NULL;
    raw = pDOMDocument->createElement(L"raw");
    raw->setAttribute(L"commandeType",L"EXPRESSCOM");

    DOMText* rawBytesText = pDOMDocument->createTextNode(XMLString::transcode(rawAscii.c_str()));
    raw->appendChild(rawBytesText);
    commandNode->appendChild(raw);

    wchar_t wcharBuffer[128];

    DOMElement * controlNode;

    if (ctlReq == CMD_FLAG_CTL_SHED)
    {//Timed
        int shed_seconds = parse.getiValue("shed");
        controlNode = pDOMDocument->createElement(L"timedControl");

        DOMElement * timeNode = pDOMDocument->createElement(L"time");
        _itow_s(shed_seconds, wcharBuffer, 128, 10);
        DOMText * timeText = pDOMDocument->createTextNode(wcharBuffer);
        timeNode->appendChild(timeText);
        controlNode->appendChild(timeNode);
    }
    else if (ctlReq == CMD_FLAG_CTL_CYCLE && !parse.isKeyValid("xctcycle"))//xctcycle is thermostat
    {//Cycle
        int delay = parse.getiValue("delaytime_sec", 0) / 60;

        //TODO: Determing Cycle type
        bool tc = (parse.getiValue("xctruecycle", 0) ? true : false);

        int cycle = parse.getiValue("cycle", 30);
        int period = parse.getiValue("cycle_period", 30);
        int cycle_count = parse.getiValue("cycle_count", 8);

        bool ramp = (parse.getiValue("xcnoramp", 0) ? false : true);

        controlNode = pDOMDocument->createElement(L"cycleControl");

        DOMElement * startDelayNode = pDOMDocument->createElement(L"startDelay");
        _itow_s(delay, wcharBuffer, 128, 10);
        DOMText * startDelayText = pDOMDocument->createTextNode(wcharBuffer);
        startDelayNode->appendChild(startDelayText);
        controlNode->appendChild(startDelayNode);

        DOMElement * typeNode = pDOMDocument->createElement(L"type");
        //TODO: Change to use cycletype
        DOMText * typeText = pDOMDocument->createTextNode(L"truecycle");
        typeNode->appendChild(typeText);
        controlNode->appendChild(typeNode);

        DOMElement * percentNode = pDOMDocument->createElement(L"percent");
        _itow_s(cycle, wcharBuffer, 128, 10);
        DOMText * percentText = pDOMDocument->createTextNode(wcharBuffer);
        percentNode->appendChild(percentText);
        controlNode->appendChild(percentNode);

        DOMElement * periodNode = pDOMDocument->createElement(L"period");
        _itow_s(period, wcharBuffer, 128, 10);
        DOMText * periodText = pDOMDocument->createTextNode(wcharBuffer);
        periodNode->appendChild(periodText);
        controlNode->appendChild(periodNode);

        DOMElement * countNode = pDOMDocument->createElement(L"count");
        _itow_s(cycle_count, wcharBuffer, 128, 10);
        DOMText * countText = pDOMDocument->createTextNode(wcharBuffer);
        countNode->appendChild(countText);
        controlNode->appendChild(countNode);

        DOMElement * rampNode = pDOMDocument->createElement(L"ramp");

        DOMText * rampText = pDOMDocument->createTextNode((ramp)?L"true":L"false");
        rampNode->appendChild(rampText);
        controlNode->appendChild(rampNode);
    }

    DOMElement * relaysNode = pDOMDocument->createElement(L"relays");
    int mask = 1;
    for (int i = 0; i < 15; i++)
    {
        if ((mask & relaymask) != 0)
        {
            DOMElement * relayNode = pDOMDocument->createElement(L"relay");

            //i+1 is the relay in question. Convert to wchar string
            _itow_s(i+1, wcharBuffer, 128, 10);
            DOMText * relayText = pDOMDocument->createTextNode(wcharBuffer);

            relayNode->appendChild(relayText);
            relaysNode->appendChild(relayNode);
        }
    }

    controlNode->appendChild(relaysNode);
    commandNode->appendChild(controlNode);
    rootElement->appendChild(commandNode);

    /*----------------------*/
    DOMImplementation *pImplement = NULL;
    DOMLSSerializer *pSerializer = NULL;
    MemBufFormatTarget *pTarget = NULL;

    DOMLSOutput *theOutput = NULL;

    pImplement = DOMImplementationRegistry::getDOMImplementation(L"LS");

    pSerializer = ((DOMImplementationLS*)pImplement)->createLSSerializer();
    theOutput = ((DOMImplementationLS*)pImplement)->createLSOutput();

    pTarget = new MemBufFormatTarget();
    theOutput->setByteStream(pTarget);

    pSerializer->write(rootElement,theOutput);
    //leak?
    const XMLByte* utf8str = pTarget->getRawBuffer();
    int size = pTarget->getLen();

    //char* temp1 = //XMLString::transcode(str);

    string ret((char*)utf8str,size);// = toNative(utf8str);

    //std::cout << ret;

    theOutput->release();
    pSerializer->release();

    XMLPlatformUtils::Terminate();

    return ret;
}

}
}
