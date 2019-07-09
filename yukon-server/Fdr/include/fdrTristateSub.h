#pragma once

#include <windows.h>    

#include "dlldefs.h"


#include "string.h"
//#include <wininet.h>
#include <list>
#include <fstream>
#include <iostream>
#include "fdrftpinterface.h"
#include <boost/thread/thread.hpp>
#include <boost/thread/mutex.hpp>
#include <boost/bind.hpp>

class StringMessageContainer;

class __declspec(dllexport) FDRTriStateSub : public CtiFDRFtpInterface
{
    typedef CtiFDRFtpInterface Inherited;

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        FDRTriStateSub(); 
        virtual ~FDRTriStateSub();

        //overloads
        /* gets called from fdr anytime a point data msg comes in. */
        void sendMessageToForeignSys( CtiMessage *aMessage ) override;
        int processMessageFromForeignSystem( char* );//should never be called.

        virtual int fail();
        virtual int decodeFile ();
        bool readConfig() override;

        virtual BOOL init( void );   
        virtual BOOL run( void );
        virtual BOOL stop( void );

        StringMessageContainer generateMessage( Boost_char_tokenizer& tokens );
        std::list<std::string> readInFile( std::istream & io );
        std::list<StringMessageContainer> processData( std::list<std::string>& stringList );

        static const CHAR * KEY_PORT_NUMBER;
        static const CHAR * KEY_TRIES;
        static const CHAR * KEY_INTERVAL;
        static const CHAR * KEY_IP_ADDRESS;
        static const CHAR * KEY_PASSWORD;
        static const CHAR * KEY_LOGIN;
        static const CHAR * KEY_SERVER_FILENAME;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_QUEUE_FLUSH_RATE;
        static const CHAR * KEY_FTP_DIRECTORY;
};

class __declspec(dllexport) StringMessageContainer
{
    public:
        StringMessageContainer();
        ~StringMessageContainer();
        void setMessage( CtiMessage* m );
        void setName( std::string n );
        std::string getName();
        boost::shared_ptr<CtiMessage> getMessage();
    
    private:
        boost::shared_ptr<CtiMessage> msg;
        std::string      name;
};
