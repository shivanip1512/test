#pragma once

#include <windows.h>    

#include "dlldefs.h"
#include "fdrftpinterface.h"

class IM_EX_FDRSTEC CtiFDR_STEC : public CtiFDRFtpInterface
{
    typedef CtiFDRFtpInterface Inherited;

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDR_STEC();

        virtual ~CtiFDR_STEC();

        void sendMessageToForeignSys ( CtiMessage *aMessage ) override;
        virtual int processMessageFromForeignSystem (CHAR *data);

        virtual BOOL    init( void );
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

        virtual int fail();
        virtual int decodeFile ();
        bool readConfig() override;
        int sendToDispatch(CtiTime aTime, FLOAT aSystemLoad, FLOAT aStecLoad);

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
            static const CHAR * KEY_STEC_TOTAL_LABEL;
};
