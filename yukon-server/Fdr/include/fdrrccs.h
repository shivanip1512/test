#pragma once

#include <windows.h>    

#include "dlldefs.h"
#include "queues.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"
#include "fdrinet.h"

#define RCCSDEVICEPRIMARY "RCCS PRIMARY        "
#define RCCSDEVICESTANDBY "RCCS STANDBY        "
#define RCCSPOINTMASTER   "MASTER              "
#define RCCSPOINTSBACKUP  "BACKUP              "

// I hate to hard code things but this is a one of a kind (not so much DLS 7 Nov 02)
#define RCCS_PAIR_ONE "RCCS PAIR ONE"
#define RCCS_PAIR_TWO "RCCS PAIR TWO"
#define RCCS_PAIR_THREE "RCCS PAIR THREE"
#define RCCS_PAIR_FOUR "RCCS PAIR FOUR"
#define RCCS_STANDALONE "STANDALONE"

class CtiTime;

class IM_EX_FDRRCCS CtiFDR_Rccs : public CtiFDR_Inet
{
    typedef CtiFDR_Inet Inherited;

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDR_Rccs();

        virtual ~CtiFDR_Rccs();

        virtual int  processMessageFromForeignSystem (CHAR *data);
        virtual bool buildAndWriteToForeignSystem (CtiFDRPoint &aPoint );

        // end getters and setters
        static const CHAR * KEY_LISTEN_PORT_NUMBER;
        static const CHAR * KEY_TIMESTAMP_WINDOW;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_SOURCE_NAME;
        static const CHAR * KEY_BATCH_MARKER_NAME;
        static const CHAR * KEY_STANDALONE;

        bool isAMaster(int aID);
        CtiFDR_Rccs& setAuthorizationFlag(int aID,bool aFlag);
        int processValueMessage(InetInterface_t *data);
        int resolvePairNumber(std::string &);

        static const CHAR * KEY_CONNECT_PORT_NUMBER;


    protected:

        bool readConfig() override;
        virtual bool  findAndInitializeClients( void );
        virtual void setCurrentClientLinkStates();


    private:
        int                         iAuthorizationFlags;
        std::string                 iBatchMarkerName;
        bool                        iStandalone;
};
