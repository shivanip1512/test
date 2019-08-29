#pragma once

#include <windows.h>

#include "dlldefs.h"
#include "queues.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"
#include "fdrsinglesocket.h"
#include "fdrvalmetutil.h"

class IM_EX_FDRVALMET CtiFDR_Valmet : public CtiFDRSingleSocket
{
    typedef CtiFDRSingleSocket Inherited;

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDR_Valmet();

        virtual ~CtiFDR_Valmet();

        virtual CHAR *buildForeignSystemHeartbeatMsg (void);
        virtual CHAR *buildForeignSystemMsg (CtiFDRPoint &aPoint);
        virtual bool alwaysSendRegistrationPoints();
        virtual int getMessageSize(CHAR *data);
        virtual std::string decodeClientName(CHAR *data);

        bool readConfig() override;

        // end getters and setters
        static const CHAR * KEY_LISTEN_PORT_NUMBER;
        static const CHAR * KEY_TIMESTAMP_WINDOW;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_QUEUE_FLUSH_RATE;
        static const CHAR * KEY_OUTBOUND_SEND_RATE;
        static const CHAR * KEY_OUTBOUND_SEND_INTERVAL;
        static const CHAR * KEY_TIMESYNC_UPDATE;
        static const CHAR * KEY_TIMESYNC_VARIATION;
        static const CHAR * KEY_LINK_TIMEOUT;
        static const CHAR * KEY_SCAN_DEVICE_POINTNAME;
        static const CHAR * KEY_SEND_ALL_POINTS_POINTNAME;

        virtual int processValueMessage(CHAR *data);
        virtual int processStatusMessage(CHAR *data);
        virtual int processControlMessage(CHAR *data);
        virtual int processScanMessage(CHAR *data);
        virtual int processTimeSyncMessage(CHAR *data);

        virtual void signalReloadList();
        virtual void signalPointRemoved(std::string &pointName);

        bool translateAndUpdatePoint(CtiFDRPointSPtr & translationPoint, int aIndex);
        void updatePointQualitiesOnDevice(PointQuality_t quality, long paoId);

        private:
            std::map<std::string,int> nameToPointId;
            std::string scanDevicePointName;
            std::string sendAllPointsPointName;
};
