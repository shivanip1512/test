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

string XmlProtocol::createMessage(const CtiCommandParser &parse, const string &rawAscii, const vector<pair<string, string> > &params)
{
    const int relayCount = 15;
    string ret = "";
    unsigned int ctlReq = CMD_FLAG_CTL_ALIASMASK & parse.getFlags();

    ControlType controlType;
    //make sure this is a supported type
    if ((controlType = checkSupportedControlType(ctlReq)) == Unknown)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " XML Protocol error, Unsupported Control Type" << endl;
        }
        return ret;
    }

    // Initilize Xerces, must terminate once per initialize
    XMLPlatformUtils::Initialize();

    try
    {
        DOMImplementation * pDOMImplementation = NULL;

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

        int relaymask = parse.getiValue("relaymask", 0);

        DOMElement * commandNode = pDOMDocument->createElement(L"command");
        DOMElement * raw = NULL;
        raw = pDOMDocument->createElement(L"raw");
        raw->setAttribute(L"commandeType",L"EXPRESSCOM");

        DOMText* rawBytesText = pDOMDocument->createTextNode(XMLString::transcode(rawAscii.c_str()));
        raw->appendChild(rawBytesText);
        commandNode->appendChild(raw);

        wchar_t wcharBuffer[128];

        //This is causing an error when the command is not timed or cycle.
        DOMElement * controlNode = NULL;

        if (controlType == Timed)
        {//Timed
            int shed_seconds = parse.getiValue("shed");
            controlNode = pDOMDocument->createElement(L"timedControl");

            DOMElement * timeNode = pDOMDocument->createElement(L"time");
            _itow_s(shed_seconds, wcharBuffer, 128, 10);
            DOMText * timeText = pDOMDocument->createTextNode(wcharBuffer);
            timeNode->appendChild(timeText);
            controlNode->appendChild(timeNode);
        }
        else if (controlType == Cycle && !parse.isKeyValid("xctcycle"))//xctcycle is thermostat
        {//Cycle
            int delay = parse.getiValue("delaytime_sec", 0) / 60;

            bool truecycle = (parse.getiValue("xctruecycle", 0) ? true : false);
            bool targetcycle = (parse.getiValue("xctargetcycle", 0) ? true : false);

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

            //Cycle Type
            DOMElement * typeNode = pDOMDocument->createElement(L"type");
            DOMText * typeText = NULL;
            //SmartCycle and MagnitudeCylce need to be added here when
            //LoadManagement decides how to indicate those message types in the parse.
            if (truecycle)
            {
                typeText = pDOMDocument->createTextNode(L"truecycle");
            }
            else if (targetcycle)
            {
                typeText = pDOMDocument->createTextNode(L"targetcycle");
            }
            else
            {
                typeText = pDOMDocument->createTextNode(L"standardcycle");
            }
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
        else if (controlType == Restore || controlType == Terminate)
        { //restore
            int delayTime = parse.getiValue("delaytime_sec", 0) / 60;
            int radomizeTime = parse.getiValue("xcrandstart", 0);
            bool stopCycle = (controlType == Terminate);

            controlNode = pDOMDocument->createElement(L"restoreControl");

            DOMElement * delayTimeNode = pDOMDocument->createElement(L"delayTime");
            _itow_s(delayTime, wcharBuffer, 128, 10);
            DOMText * delayTimeText = pDOMDocument->createTextNode(wcharBuffer);
            delayTimeNode->appendChild(delayTimeText);
            controlNode->appendChild(delayTimeNode);

            DOMElement * randomNode = pDOMDocument->createElement(L"randomizeTime");
            _itow_s(radomizeTime, wcharBuffer, 128, 10);
            DOMText * randomText = pDOMDocument->createTextNode(wcharBuffer);
            randomNode->appendChild(randomText);
            controlNode->appendChild(randomNode);

            DOMElement * stopCycleNode = pDOMDocument->createElement(L"stopCycle");
            DOMText * stopCycleText = pDOMDocument->createTextNode((stopCycle)?L"true":L"false");
            stopCycleNode->appendChild(stopCycleText);
            controlNode->appendChild(stopCycleNode);
        }

        DOMElement * relaysNode = pDOMDocument->createElement(L"relays");
        int mask = 1;
        for (int i = 0; i < relayCount; i++)
        {
            if ((mask & relaymask) != 0)
            {
                DOMElement * relayNode = pDOMDocument->createElement(L"relay");

                //i+1 is the relay in question. Convert to wchar string
                _itow_s(i+1, wcharBuffer, 128, 10);
                DOMText * relayText = pDOMDocument->createTextNode(wcharBuffer);

                relayNode->appendChild(relayText);
                relaysNode->appendChild(relayNode);
                mask <<= 1;
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

        const XMLByte* utf8str = pTarget->getRawBuffer();
        int size = pTarget->getLen();

        ret = string((char*)utf8str,size);

        delete pTarget;
        pDOMDocument->release();
        theOutput->release();
        pSerializer->release();

    }
    catch (...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " XML Protocol error, Exception generating the XML." << endl;
        }
    }
    XMLPlatformUtils::Terminate();

    return ret;
}

ControlType XmlProtocol::checkSupportedControlType(unsigned int controlRequest)
{
    switch (controlRequest)
    {
        case CMD_FLAG_CTL_CYCLE:
            return Cycle;
        case CMD_FLAG_CTL_SHED:
            return Timed;
        case CMD_FLAG_CTL_RESTORE:
            return Restore;
        case CMD_FLAG_CTL_TERMINATE:
            return Terminate;
        default:
            return Unknown;
    }
}

}
}
