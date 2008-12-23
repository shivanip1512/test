#ifndef __PROT_XML_H__
#define __PROT_XML_H__

#include <string>
#include <list>

#include "DSM2.h"
#include "xfer.h"
#include "xml_object.h"
#include "prot_base.h"

namespace Cti
{
    namespace Protocol
    {

        class IM_EX_PROT XmlProtocol : public Cti::Protocol::Interface
        {
            public:
                XmlProtocol();
    
                /**
                 * Takes the command string out of the message and stores it.
                 * 
                 * @param OutMessage 
                 * 
                 * @return int 
                 */
                virtual int recvCommRequest(OUTMESS *OutMessage);
    
				virtual int sendCommResult(INMESS *InMessage);

                virtual bool isTransactionComplete(void) const;
    
                /**
                 * Generates the XML and sets the outbuffer and outcount  of the 
                 * CtiXfer message. 
                 * 
                 * @param xfer 
                 * 
                 * @return int 
                 */
                virtual int generate( CtiXfer &xfer );
                /**
                 * Determines the success of the generate call?
                 * 
                 * @param xfer 
                 * @param status 
                 * 
                 * @return int 
                 */
                virtual int decode( CtiXfer &xfer, int status );
    
                /**
                 * Group id is set when recvCommRequest is called.
                 * 
                 * @return int 
                 */
                int getGroupId();
                void setGroupId(int gid);
    
                /**
                 * Here for unit testing, normally would not get the xml object 
                 * from the protocol. generate is the method to get data. 
                 * 
                 * @param xmlPtr 
                 */
                void setXmlObject(XmlObject::XmlObjectSPtr & xmlPtr);
                XmlObject::XmlObjectSPtr & getXmlObject();
    
                /**
                 * Here for unit testing, this will be flase in unit tests to 
                 * revent a database hit. 
                 *  
                 * @param flag 
                 */
                void setCheckDatabase(bool flag);
                bool getCheckDatabase();
    
                /**
                 * These are here for unit testing, need the ability to set a 
                 * fail case. 
                 * 
                 * @param comp 
                 */
                void setComplete(bool comp);
                bool getComplete() const;
    
                string& getCommandString();
    
				/**
				 * Clear data to get ready for a new command.
				 * 
				 */
				void reset();

				void setParameters( std::vector< std::vector<string> >& params);

			private:
                BYTE * generateByteBuffer(int & size);
                void generateDataFromDatabase(XmlObject::XmlObjectSPtr xmlBaseObject);
    
				std::vector< std::vector<string> > _params;

                XmlObject::XmlObjectSPtr _xmlObject;
                bool _complete;
                bool _checkDatabase;
                string _commandString;
                int _groupId;

        };

        typedef boost::shared_ptr<XmlProtocol> XmlProtocolSPtr;

    }
}
#endif
