#pragma once

#include <windows.h>    

#include "dlldefs.h"
#include "fdrftpinterface.h"

class CtiFDRProtectedIdMapList;

class IM_EX_FDRTRISTATE CtiFDR_Tristate : public CtiFDRFtpInterface
{                                    
    typedef CtiFDRFtpInterface Inherited;

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDR_Tristate(); 

        virtual ~CtiFDR_Tristate();

        void sendMessageToForeignSys ( CtiMessage *aMessage ) override;
        virtual int processMessageFromForeignSystem (CHAR *data);

        virtual BOOL    init( void );   
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

        virtual int fail();
        virtual int decodeFile ();
        bool readConfig() override;
        int sendToDispatch(CtiTime aTime, FLOAT aSystemLoad, FLOAT aAvgLoad);

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
