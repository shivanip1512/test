#ifndef __FDRVALMET_H__
#define __FDRVALMET_H__


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "dlldefs.h"
#include "queues.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"
#include "fdrsinglesocket.h"

// global defines
#define VALMET_PORTNUMBER       1666


/* Definitions and structures used to share data with VALMET */

/* Valmet link quality definition masks */
#define VALMET_NORMAL           0x0000
#define VALMET_PLUGGED          0x0001
#define VALMET_DATAINVALID      0x0002
#define VALMET_UNREASONABLE     0x0004
#define VALMET_MANUALENTRY      0x0008
#define VALMET_OUTOFSCAN        0x0010

/*
NOTE:  All data limit violations will be handled by the receiving system
*/
#pragma pack(push, valmet_packing, 1)

typedef struct
{
    USHORT Function;
    CHAR TimeStamp[18];
    union {
        /* Null Message     Function = 0 */

        /* Value Message   Function = 101 */
        struct {
            CHAR Name[16];
            union {
                FLOAT Value;
                ULONG LongValue;
            };
            USHORT Quality;
        } Value;

        /* Status Message   Function = 102 */
        struct {
            CHAR Name[16];
            union {
                USHORT Value;
                ULONG LongValue;
            };
            USHORT Quality;
        } Status;

        /* Control Message   Function = 103 */
        struct {
            CHAR Name[16];
            USHORT Value;
        } Control;

        /* Force Scan Message  Function = 110 */
        struct {
            CHAR Name[16];
        } ForceScan;

        /* TimeSync         Function = 401 */
    };

    /* Force long alignment */
    USHORT Spare;

} ValmetInterface_t;

#pragma pack(pop, valmet_packing)     // Restore the prior packing alignment..

class CtiTime;

class IM_EX_FDRVALMET CtiFDR_Valmet : public CtiFDRSingleSocket
{
    typedef CtiFDRSingleSocket Inherited;

    public:
        // constructors and destructors
        CtiFDR_Valmet();

        virtual ~CtiFDR_Valmet();

        virtual CHAR *buildForeignSystemHeartbeatMsg (void);
        virtual CHAR *buildForeignSystemMsg (CtiFDRPoint &aPoint);
        virtual int getMessageSize(CHAR *data);
        virtual std::string decodeClientName(CHAR *data);

        virtual int readConfig( void );

        // end getters and setters
        static const CHAR * KEY_LISTEN_PORT_NUMBER;
        static const CHAR * KEY_TIMESTAMP_WINDOW;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_QUEUE_FLUSH_RATE;
        static const CHAR * KEY_DEBUG_MODE;
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

        USHORT      ForeignToYukonQuality (USHORT aQuality);
        int         ForeignToYukonStatus (USHORT aStatus);
        CtiTime      ForeignToYukonTime (PCHAR aTime, bool aTimeSyncFlag = false);
        string CtiFDR_Valmet::ForeignQualityToString(USHORT quality);

        std::string   YukonToForeignTime (CtiTime aTimeStamp);
        USHORT      YukonToForeignQuality (USHORT aQuality);
        USHORT      YukonToForeignStatus (int aStatus);

        virtual void signalReloadList();
        virtual void signalPointRemoved(std::string &pointName);

        bool translateAndUpdatePoint(CtiFDRPointSPtr & translationPoint, int aIndex);

        enum {  Valmet_Invalid = 0,
                Valmet_Open = 1,
                Valmet_Closed=2,
                Valmet_Indeterminate=3};

        private:
            std::map<std::string,int> nameToPointId;
            string scanDevicePointName;
            string sendAllPointsPointName;

};



#endif  //  #ifndef __FDRVALMET_H__


