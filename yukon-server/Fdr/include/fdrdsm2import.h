#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrdsm2import.h
*
*    DATE: 03/06/2002
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Class to retrieve the DSM2 FILEOUT file
*
*    DESCRIPTION: 
*
*    Copyright (C) 2001 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRDSM2IMPORT_H__
#define __FDRDSM2IMPORT_H__


#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this

#include "dlldefs.h"
#include "fdrasciiimportbase.h"

class IM_EX_FDRDSM2IMPORT CtiFDR_Dsm2Import : public CtiFDRAsciiImportBase
{                                    
    typedef CtiFDRAsciiImportBase Inherited;

    public:
        // constructors and destructors
        CtiFDR_Dsm2Import(); 

        virtual ~CtiFDR_Dsm2Import();

        virtual bool sendMessageToForeignSys ( CtiMessage *aMessage );
        virtual int processMessageFromForeignSystem (CHAR *data);
        virtual bool validateAndDecodeLine (string &aLine, CtiMessage **retMsg);
                                                                
        CtiTime Dsm2ToYukonTime (string aTime);
        USHORT Dsm2ToYukonQuality (CHAR aQuality);

        virtual BOOL    init( void );   
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

        int readConfig( void );

        enum {Dsm2_Invalid=0,
              Dsm2_Open, 
              Dsm2_Closed,
              Dsm2_Indeterminate,
              Dsm2_State_Four,
              Dsm2_State_Five,
              Dsm2_State_Six};

        static const CHAR * KEY_INTERVAL;
        static const CHAR * KEY_FILENAME;
        static const CHAR * KEY_DRIVE_AND_PATH;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_QUEUE_FLUSH_RATE;
        static const CHAR * KEY_DELETE_FILE;
};



#endif  //  #ifndef __FDR_STEC_H__



