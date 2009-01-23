#include "yukon.h"


#include "logger.h"
#include "utility.h"
#include "porter.h"
#include "prot_xml.h"
#include "cmdparse.h"
#include "numstr.h"
#include <sstream>

namespace Cti
{
    namespace Protocol
    {

        using std::stringstream;


        XmlProtocol::XmlProtocol()
        {
            _complete = false;
            _generateParameters = true;
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
            CtiCommandParser parser(_commandString);

            //grab by reference to save a copy.
            CtiCommandParser::map_type parseMap = parser.Map();
            XmlObject::XmlObjectSPtr xmlBaseObject(new XmlObject());

            xmlBaseObject->setTagName("XML_COMMAND");
            xmlBaseObject->insertParameter("command",string(itoa(parser.getCommand(),tempArray,10)));
            xmlBaseObject->insertParameter("flags",string(itoa(parser.getFlags(),tempArray,10)));

            if (getGenerateParameters())
            {
                generateParameters(xmlBaseObject);
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

            _xmlObject = xmlBaseObject;
            _complete = true;

            int size = 0;
            BYTE * buf = generateByteBuffer(size);

            xfer.setOutBuffer(buf);
            xfer.setOutCount(size);

            /* needed? */
            xfer.setCRCFlag(0);

            return retVal;
        }

        void XmlProtocol::generateParameters(XmlObject::XmlObjectSPtr xmlBaseObject)
        {

            std::vector<std::pair<string,string> >::iterator itr;

            /* Add fields from the database */
            for ( itr = _params.begin(); itr != _params.end(); ++itr)
            {
                XmlObject::XmlObjectSPtr xmlChild(new XmlObject());
                string paramName = (*itr).first;
                string paramValue = (*itr).second;
                xmlChild->insertParameter(paramName,paramValue);
                xmlChild->setTagName("XML_DATA");
                xmlBaseObject->insertXmlNode(xmlChild);
            }
            _params.clear();
        }

        BYTE* XmlProtocol::generateByteBuffer(int & size)
        {
            if (_complete == true)
            {
                //generate buffer
                stringstream ss(stringstream::out);
                //Use String stream and compare to expected.
                _xmlObject->outputXml(ss);

                string output = ss.str();
                size = output.size();

                _buffer.reset(new BYTE [size]);
                std::copy(output.begin(),output.end(),_buffer.get());
            }

            return _buffer.get();
        }

        int XmlProtocol::sendCommResult(INMESS *InMessage)
        {
            return NORMAL;
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
            _complete = comp;
        }

        bool XmlProtocol::getComplete() const
        {
            return _complete;
        }

        void XmlProtocol::setXmlObject(XmlObject::XmlObjectSPtr & xmlPtr)
        {
            _xmlObject = xmlPtr;
        }

        XmlObject::XmlObjectSPtr & XmlProtocol::getXmlObject()
        {
            return _xmlObject;
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

            _commandString = string(OutMessage->Request.CommandStr);
            _groupId = OutMessage->DeviceIDofLMGroup;

            //Test success of on Strlen?

            return retVal;
        }

        string& XmlProtocol::getCommandString()
        {
            return _commandString;
        }

        void XmlProtocol::setGenerateParameters(bool flag)
        {
            _generateParameters = flag;
        }

        bool XmlProtocol::getGenerateParameters()
        {
            return _generateParameters;
        }

        int XmlProtocol::getGroupId()
        {
            return _groupId;
        }

        void XmlProtocol::setGroupId(int gid)
        {
            _groupId = gid;
        }

        void XmlProtocol::reset()
        {
            _groupId = 0;
            _commandString = "";
            _complete = false;
        }

        void XmlProtocol::setParameters( std::vector<std::pair<string,string> >& params)
        {
            _params = std::vector<std::pair<string,string> >(params);
        }

    }//end Protocol namespace
}//end Cti namespace
