#pragma once

#include <windows.h>

#include "dlldefs.h"
#include "queues.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"
#include "fdrsinglesocket.h"

// global defines
#define ACS_PORTNUMBER      1668


/* Definitions and structures used to share data with ACS */

// ACS quality codes
#define ACS_NORMAL              0x0001      // channel up good reading
#define ACS_MANUALENTRY         0x0002      // operator entered
#define ACS_PLUGGED             0x0004      // channel down
/*

NOTE:  All data limit violations will be handled by the receiving system
*/

/*
NOTE:  Decision was made to use function 201 (ACS_CONTROL) as a way of starting and
        stopping control by strategy and also by group (as opposed to functions 503/501).
        Differentation between these and regular control are to be implemented on the
        DSM2 side by using the naming convention @C_ in the device name

        ACS_OPEN    =   stop strategy   =   restore load group
        ACS_CLOSED  =   start strategy  =   Shed load group
*/

/*
Naming conventions for points are as follows:

    RrrrrCcPxxxx        where
                            rrrr = remote number
                            c     = category
                            pppp = point number

    Current valid categories are:

      R    remote
        P    pseudo
        C    calculated
        D    diagnostic

        A    accumulator
*/

// error codes for ACS
// NOTE:  Currently, they treat all errors as no replies DLS 8 Apr 99
#define ACS_ERRROR_NOREPLY              31
#define ACS_ERRROR_SEQUENCE         32
#define ACS_ERRROR_FRAME                33
#define ACS_ERRROR_BADCRC               34
#define ACS_ERRROR_BADLENGTH            35
#define ACS_ERRROR_UNKNOWN              37
#define ACS_ERRROR_DSM2_DATABASE        53
#define ACS_ERRROR_RTU_DISABLED     78
#define ACS_ERRROR_PORT_DISABLED        83

#pragma pack(push, acs_packing, 1)

// Structure for messages to and from ACS
typedef struct {
    USHORT Function;
    CHAR TimeStamp[16];
    union {
        /* Null Message     Function = 0 */

        /* Value Message   Function = 101 */
        struct {
            USHORT RemoteNumber;
            USHORT PointNumber;
            CHAR CategoryCode;
            CHAR Spare;
            union {
                FLOAT Value;
                ULONG LongValue;
            };
            USHORT Quality;
        } Value;

        /* Status Message   Function = 102 */
        struct {
            USHORT RemoteNumber;
            USHORT PointNumber;
            CHAR CategoryCode;
            CHAR Spare;
            USHORT Value;
            USHORT Quality;
        } Status;

        /* Control Message   Function = 103 */
        struct {
            USHORT RemoteNumber;
            USHORT PointNumber;
            CHAR CategoryCode;
            CHAR Spare;
            USHORT Value;
        } Control;
    };

    /* Force long alignment */
//    CHAR Spare;

} ACSInterface_t;

#pragma pack(pop, acs_packing)     // Restore the prior packing alignment..

class CtiTime;

class IM_EX_FDRACS CtiFDR_ACS : public CtiFDRSingleSocket
{
    typedef CtiFDRSingleSocket Inherited;

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDR_ACS();

        virtual ~CtiFDR_ACS();

        virtual CHAR *buildForeignSystemHeartbeatMsg (void);
        virtual CHAR *buildForeignSystemMsg (CtiFDRPoint &aPoint);
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
        static const CHAR * KEY_POINT_TIME_VARIATION;
        static const CHAR * KEY_LINK_TIMEOUT;

        virtual int processValueMessage(CHAR *data);
        virtual int processStatusMessage(CHAR *data);
        virtual int processControlMessage(CHAR *data);
        virtual int processTimeSyncMessage(CHAR *data);

        std::string   ForeignToYukonId (USHORT remote, CHAR category, USHORT point);
        USHORT      ForeignToYukonQuality (USHORT aQuality);
        int         ForeignToYukonStatus (USHORT aStatus);
        CtiTime      ForeignToYukonTime (PCHAR aTime, bool aTimeSyncFlag = false);

        std::string   YukonToForeignTime (CtiTime aTimeStamp);
        int         YukonToForeignId (std::string aPointName, USHORT &remoteNumber, CHAR &category, USHORT &pointNumber);
        USHORT      YukonToForeignQuality (USHORT aQuality);
        USHORT      YukonToForeignStatus (int aStatus);

        enum {ACS_Open = 0, ACS_Closed = 1, ACS_Invalid=99};

        virtual bool translateAndUpdatePoint(CtiFDRPointSPtr & translationPoint, int aDestinationIndex);

};
