#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrtristate.h
*
*    DATE: 05/30/2001
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Class to retrieve the tristate file
*
*    DESCRIPTION: 
*
*    Copyright (C) 2001 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRTRISTATE_H__
#define __FDRTRISTATE_H__

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include "dlldefs.h"
#include "fdrftpinterface.h"

class CtiFDRProtectedIdMapList;

class IM_EX_FDRTRISTATE CtiFDR_Tristate : public CtiFDRFtpInterface
{                                    
    typedef CtiFDRFtpInterface Inherited;

    public:
        // constructors and destructors
        CtiFDR_Tristate(); 

        virtual ~CtiFDR_Tristate();

        virtual bool sendMessageToForeignSys ( CtiMessage *aMessage );
        virtual int processMessageFromForeignSystem (CHAR *data);

        virtual BOOL    init( void );   
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

		virtual int fail();
        virtual int decodeFile ();
        int readConfig( void );
        int sendToDispatch(RWTime aTime, FLOAT aSystemLoad, FLOAT aAvgLoad);

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

			static const CHAR * KEY_SYSTEM_TOTAL_LABEL;
			static const CHAR * KEY_30_MINUTE_AVG_LABEL;

};
#endif  //  #ifndef __FDRTRISTATE_H__

