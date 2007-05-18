#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrTriStateSub.cpp
*
*    DATE: 5/15/2007
*
*    AUTHOR: Thain Spar
*
*    PURPOSE: Read in load data
*
*    DESCRIPTION: FTP interface to bring in load data from San Migul's Subs.
*    
*    "20070516101500","Nucla 115/69 Xfmr.","MW",4.326
*    "20070516101500","Happy Canyon 661Idarado","MW",2.11
*    "20070516101500","Cascade 115/69 (T2)","MW",5.978
*    "20070516101500","Ames Generation","MW",3.721
*    "20070516101500","Dallas Creek MW","MW",4.079
*    "20070516101500","Dallas Creek MV","MW",-2.671
*****************************************************************************/
#ifndef __FDRTriStateSub_H__
#define __FDRTriStateSub_H__

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

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

using std::string;
using std::istream;
using boost::shared_ptr;

class StringMessageContainer;

class __declspec(dllexport) FDRTriStateSub : public CtiFDRFtpInterface
{
    typedef CtiFDRFtpInterface Inherited;

    public:
        // constructors and destructors
        FDRTriStateSub(); 
        virtual ~FDRTriStateSub();

        //overloads
        /* gets called from fdr anytime a point data msg comes in. */
        bool sendMessageToForeignSys( CtiMessage *aMessage );
        int processMessageFromForeignSystem( char* );//should never be called.

		virtual int fail();
        virtual int decodeFile ();
        int readConfig( void );

        virtual BOOL init( void );   
        virtual BOOL run( void );
        virtual BOOL stop( void );

        StringMessageContainer generateMessage( Boost_char_tokenizer& tokens );
        std::list<std::string> readInFile( istream io );
        list<StringMessageContainer> processData( list<string>& stringList );

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
        void setName( string n );
        string getName();
        shared_ptr<CtiMessage> getMessage();
    
    private:
        shared_ptr<CtiMessage> msg;
        string      name;
};
#endif


