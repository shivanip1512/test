#pragma once

#include <windows.h>

#include "dlldefs.h"
#include "queues.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"
#include "fdrsinglesocket.h"

// global defines
#define RDEX_PORTNUMBER       1670

// RDEX SCADA/EMS quality codes
// Quality member of the message structure

#define RDEX_NORMAL           0x0001   // Good data
#define RDEX_MANUAL           0x0002   // Manual  entry
#define RDEX_QUESTIONABLE     0x0004   // Anything else

#define RDEX_NULL                       0
#define RDEX_REGISTRATION               1
#define RDEX_ACKNOWLEDGMENT             2
#define RDEX_VALUE                      101
#define RDEX_STATUS                     102
#define RDEX_CONTROL                    201

/*

NOTE:  All data limit violations will be handled by the receiving system
*/

/*
NOTE:  Decision was made to use function 201 as a way of starting and
      stopping control by strategy and also by group.

      RDEX_OPEN   =     stop strategy  =  restore load group
      RDEX_CLOSED =  start strategy =  Shed load group
*/

#pragma pack(push, rdex_packing, 4)
// R-DEX Null Message Function: 0
typedef struct
{
   ULONG function;      // Function 0 - Null Message
   CHAR timestamp[16];
   CHAR spare1[80];
   ULONG spare2;
   ULONG spare3;
} Rdex_Null;

// R-DEX Registration Function: 1
typedef struct
{
   ULONG function;      // Function 1 - Registration Message
   CHAR  timestamp[16];
   CHAR  clientName[40];    // unique name of client
   CHAR  spare1[40];
   ULONG spare2;
   ULONG spare3;
} Rdex_Registration;

// R-DEX Acknowledge Function: 2

typedef struct
{
   ULONG function;      // Function 2 - Acknowledge Message
   CHAR  timestamp[16];
   CHAR  serverName[40];    // unique name of server
   CHAR  spare1[40];
   ULONG spare2;
   ULONG spare3;
} Rdex_Acknowledgement;

// R-DEX Value Message Function: 101

typedef struct
{
   ULONG function;      // Function 101 - Value Message
   CHAR  timestamp[16];
   CHAR  translation[80];
   ULONG quality;
   union {
         FLOAT floatValue;
         ULONG longValue;
         };
} Rdex_Value;

// R-DEX Status Message Function: 102

typedef  struct
{
   ULONG function;      // Function 102 - Status Message
   CHAR  timestamp[16];
   CHAR  translation[80];
   ULONG quality;
   ULONG value;
} Rdex_Status;

// RDEX Control Message Function: 201

typedef struct
{
   ULONG function;      // Function 201 - Control Message
   CHAR  timestamp[16];
   CHAR  translation[80];
   ULONG spare1;
   ULONG value;
} Rdex_Control;


// Structure for messages to and from RDEX
typedef struct {
   ULONG function;
   CHAR timestamp[16];
   union {
        // Null Message     Function = 0
      // registration       function = 1
      struct
      {
         CHAR  clientName[40];    // unique name of client
         CHAR  spare1[40];
         ULONG spare2;
         ULONG spare3;
      } registration;

      struct               // function 2
      {
         CHAR  serverName[40];    // unique name of server
         CHAR  spare1[40];
         ULONG spare2;
         ULONG spare3;
      } acknowledgement;

      struct               // function = 101
      {
         CHAR translation[80];
         ULONG quality;
         union {
               FLOAT floatValue;
               ULONG longValue;
               };
      } value;

      struct               // function 102
      {
         CHAR  translation[80];
         ULONG quality;
         ULONG value;
      } status;

      struct               // function = 201
      {
         CHAR  translation[80];
         ULONG spare1;
         ULONG value;
      } control;
    };
} RdexInterface_t;

#pragma pack(pop, rdex_packing)     // Restore the prior packing alignment..

class CtiTime;

class IM_EX_FDRRDEX CtiFDR_Rdex : public CtiFDRSingleSocket
{
    typedef CtiFDRSingleSocket Inherited;

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDR_Rdex();

        virtual ~CtiFDR_Rdex();

        virtual int processMessageFromForeignSystem (CHAR *data);
        virtual CHAR *buildForeignSystemHeartbeatMsg (void);
        virtual CHAR *buildForeignSystemMsg (CtiFDRPoint &aPoint);
        virtual int getMessageSize(CHAR *data);
        virtual std::string decodeClientName(CHAR *data);

        bool readConfig() override;

        virtual int processValueMessage(CHAR *data);
        virtual int processStatusMessage(CHAR *data);
        virtual int processRegistrationMessage(CHAR *data);
        virtual int processControlMessage(CHAR *data);

        ULONG       ForeignToYukonQuality (ULONG aQuality);
        ULONG       ForeignToYukonStatus (ULONG aStatus);
        CtiTime      ForeignToYukonTime (PCHAR aTime);

        // end getters and setters
        static const CHAR * KEY_LISTEN_PORT_NUMBER;
        static const CHAR * KEY_TIMESTAMP_WINDOW;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_QUEUE_FLUSH_RATE;
        static const CHAR * KEY_OUTBOUND_SEND_RATE;
        static const CHAR * KEY_OUTBOUND_SEND_INTERVAL;
        static const CHAR * KEY_LINK_TIMEOUT;

        std::string   YukonToForeignTime (CtiTime aTimeStamp);
        ULONG         YukonToForeignQuality (ULONG aQuality);
        ULONG         YukonToForeignStatus (ULONG aStatus);

        enum {Rdex_Open = 0, Rdex_Closed = 1, Rdex_Invalid=99};
        virtual bool translateAndUpdatePoint(CtiFDRPointSPtr & translationPoint, int aDestinationIndex);
        virtual bool isRegistrationNeeded(void);
};
