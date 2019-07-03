#pragma once

#include <windows.h>    

#include "dlldefs.h"
#include "fdrasciiimportbase.h"

class IM_EX_FDRDSM2IMPORT CtiFDR_Dsm2Import : public CtiFDRAsciiImportBase
{                                    
    typedef CtiFDRAsciiImportBase Inherited;

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDR_Dsm2Import(); 

        virtual ~CtiFDR_Dsm2Import();

        void sendMessageToForeignSys ( CtiMessage *aMessage ) override;
        virtual int processMessageFromForeignSystem (CHAR *data);
        virtual bool validateAndDecodeLine (std::string &aLine, CtiMessage **retMsg);
                                                                
        CtiTime Dsm2ToYukonTime (std::string aTime);
        USHORT Dsm2ToYukonQuality (CHAR aQuality);

        virtual BOOL    init( void );   
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

        bool readConfig() override;

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
