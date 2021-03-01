#pragma once

#include "connectionHandle.h"
#include "streamConnection.h"
#include "mutex.h"
#include "guard.h"
#include "dlldefs.h"
#include "error.h"
#include "words.h"
#include "macro_offset.h"
#include "constants.h"

#include <time.h>
#include <set>

namespace Cti {
class StreamSocketConnection;
}

IM_EX_CTIBASE void incrementCount();
IM_EX_CTIBASE void decrementCount();
IM_EX_CTIBASE LONG OutMessageCount();


#pragma pack(push, message_packing, 1)

/* Structure definitions */
enum  CtiSyncType_t
{
  CtiEventType = 0,
  CtiMutexType
};

struct CtiSyncDefStruct
{
   CtiSyncType_t  type;
   BOOL           manualReset;
   BOOL           initState;
   CHAR *         syncObjName;
};

struct FPPCCST
{
   BYTE PRE[1];         /* * or - depending on recipient */
   BYTE UID[3];
   BYTE VID[3];
   BYTE D[2];
   BYTE F[2];
   BYTE ADDRS[7];
   BYTE GRP[4];
   BYTE VALUE[6];
   BYTE POST[1];        /* * or - depending on recipient */
};

struct FPSTRUCT
{
   USHORT   Function;
   USHORT   Length;

   union
   {
      BYTE     Message[36];
      FPPCCST  PCC;
   } u;
};

/* Structure used for ripple */
struct RSTRUCT
{
   BYTE Message[7];
};


/* Structures used for VERSACOM */
struct VRELAY
{
   USHORT ControlType;    /* 0 = Cycling, 1 = Discrete */
   USHORT Relay[3];       /* 0 = Relay Not Affected, !0 = Relay Affected */
   USHORT TimeCode;       /* Versacom time code */
};

struct VERELAY
{
   USHORT ControlType;
   USHORT CycleType;
   USHORT Relay;
   SHORT  Percent;
   ULONG  ControlTime;
   ULONG  RandomTime;
   USHORT Window;
   USHORT Count;
   ULONG  DelayUntil;
};

struct VDATA
{
   INT      DataLength;
   BYTE     Data[30];
   USHORT   DataType;
};

struct VCONFIG
{
   USHORT ConfigType;
   BYTE Data[6];
};

struct VESERVICE
{
   BOOL     Cancel;     // If set, this command cancels a prior TOOS command
   BOOL     LED_Off;    // If set, the LEDs are off during the TOOS.
   USHORT   TOOS_Time;  // TOOS time in half seconds.

};

struct VSTRUCT
{
   INT      Retry;

   USHORT   CommandType;

   USHORT   UtilityID;
   USHORT   Section;
   USHORT   Class;
   USHORT   Division;
   ULONG    Address;             // This is the serial number of the switch!

   INT      RelayMask;           // 022200 CGP bitmask for relays in control! indicates relay, or relays used in a control operation

   union {
      USHORT  Arg;               // This is an alias for all USHORT members!
      USHORT  Initiator;
      USHORT  Service;
      USHORT  PropDIT;
      USHORT  CountReset;
      VDATA   VData;
      VRELAY  Load[3];
      VERELAY ELoad;
      VCONFIG VConfig;
      VESERVICE ExService;
   };

   USHORT  NumW;
   USHORT  Nibbles;

   BYTE   Message[MAX_VERSACOM_MESSAGE];      // Added 013100 CGP

   CHAR   Action[30];                         // Added 041400 CGP for logging purposes

};

struct TAPSTRUCT
{
   // CHAR CapCode[STANDNAMLEN];
   CHAR Message[256];
   ULONG Length;
};

/*
 * The Dialup structures
 */

struct DIALUPREQUEST
{
   /* Elements from BSTRUCT */
   // LONG        PortID;                       // 083199 CGP
   // LONG        DeviceID;                     // 083199 CGP
   // INT         Priority;
   // INT         Sequence;

   // USHORT      Retry;
   ULONG       Address;
   /* END - Elements from BSTRUCT */

   USHORT      Command[5];
   ULONG       Request[5];
   USHORT      ReadCode;
   LONG        LastFileTime;                // Load Profile Time
   LONG        LP_Time;                     // Load Profile Time
   BYTE        Message[256];

   ULONG       Time;                        /* Porter fills */
   USHORT      DSTFlag;
};

struct DIALUPSTRUCT
{
   DIALUPREQUEST     Request;

   FLOAT             FResponse[100];
   USHORT            IResponse[100];
   BYTE              SResponse[36];
};

struct DIALUPREPLY
{
   DIALUPREQUEST ReqSt;                    /* The original Request made somewhere */
   BYTE          Status;                   /* Device Status Byte */
   BYTE          AckNak;                   /* Last device response to comms */
   USHORT        CompFlag;                 /* Indicates progress through the various commands */
   BYTE          Message[2600];            /* Data is in here */
};

/*----------------------------------------------------------------------------*
 * Any and all byte sensitive boundaries should be protected within the
 * confines of these pack pragmas....  Please use your head in regards to
 * making things require this though....
 *----------------------------------------------------------------------------*/

struct PIL_ECHO               // Data echo'ed through porter fro the PIL.
{
   char     BuildIt;          // 022801 CGP If !FALSE porter will analyze the CommandStr and make his own assumptions.

   Cti::ConnectionHandle Connection;
   CHAR     CommandStr[COMMAND_STR_SIZE + 1];   // First COMMAND_STR_SIZE characters of the request string.
   INT      RouteID;          // The ID of the route which is currently being addressed
   char     Attempt;          // On request, the number of attempts to make for this route specification. On reply, the number of attempts remaining.
   char     SOE;              // SOE id added by client, or by pil on receipt.
   LONG     GrpMsgID;         // Group Message ID, tracks groups of messages for canceling and reporting
   LONG     UserID;           // User ID
   ULONG    CheckSum;         // 071300 CGP CheckSum generated by getUniqueIdentifier() used to match "like-connected" devices in the porter queues
   long     OptionsField;     // 2010-09-22 mskf - Added to allow commands to track whether they are active

   Cti::MacroOffset RetryMacroOffset;

   union _protocol_info_t
   {
       struct _emetcon_t
       {
           unsigned char Function;
           unsigned char IO;
       } Emetcon;
   } ProtocolInfo;
};

struct CtiSAData
{
    BYTE _buffer[MAX_SA_MSG_SIZE];      // This MUST be first in the struct.
    int _bufferLen;                     // This is the valid size of the prepared _buffer

    INT  _commandType;
    BYTE _lbt;
    BYTE _delayToTx;                    // Time in seconds before transmitting codes.
    BYTE _maxTxTime;                    // Maximum Time in seconds to transmit codes.

    BYTE _mark;                         // SA Digital mark
    BYTE _space;                        // SA Digital space

    int _transmitterAddress;            // The address of the RTC, or RTU.
    int _groupType;                     // This must be one of the supported DCU types in the lib.. ie. SA205, GOLAY...

    BOOL _retransmit;                   // DCU205 groups use this bool to determine that this is a retransmission.
    BOOL _shed;                         // NON DCU205 groups use this bool to determine shed or restore.
    int _function;                      // This is the function to execute on the DCU....  Should be directly applied in the switch.

    int _code205;                       // This is the code to transmit iff this is an SA205 DCU type group.
    CHAR _codeSimple[7];                // This is the code to transmit iff this is NOT an SA205 DCU type group.
    CHAR _serial[33];                   // This is a 205/305 serial number.
    CHAR _code305[128];                 // This is a 305 message.

    // The parameters below are assigned typically by the parse object
    int _swTimeout;                     // Switch OFF time in seconds.
    int _cycleTime;                     // Switch on + off time in seconds.
    int _repeats;                       // Number of _cycleTimes to repeat the operation (DCU205)
    int _sTime;                         // swTimeout as needed by the protocol
    int _cTime;                         // cycleTime needed by the protocol
};

/* queing  structures used by protected mode */
typedef class CtiOutMessage
{
public:

   BYTE               HeadFrame[2];                 // 082702 CGP    // Hey, it should have been in there for a long time!
   LONG               DeviceID;                     // 083199 CGP    // The device id of the transmitter device.
   LONG               TargetID;                     // 022701 CGP    // The device id of the end-of-line device. May be the same as DeviceID depending on protocol
   USHORT             Sequence;                     // 083199 CGP    // Used by CCU711 to id a CCU queue entry.
   LONG               VerificationSequence;         // 072104 mskf   // Used to track multiple OutMessage requests for the same code
                                                                     //   transmission, esp. for the purpose of logging retry success

   INT                Retry;                        // Instructions to porter!
   INT                Priority;
   INT                TimeOut;                      //  Timeout for inbound data, in seconds

   INT                Port;
   INT                Remote;

   ULONG              OutLength;     // We have 300 bytes available
   ULONG              InLength;      // We have about 4kB available.

   UINT               Source;
   UINT               Destination;
   UINT               Command;
   UINT               Function;
   UINT               EventCode;
   UINT               MessageFlags;

   LONG               DeviceIDofLMGroup;           // 091300 CGP Helps us track lm command's success
   UINT               TrxID;                       // 091300 CGP Helps us track lm command's success

   time_t             ExpirationTime;               // The PortThread will discard this message if it tries to execute after this time (and it is non-zero).

   PIL_ECHO           Request;

   Cti::StreamConnection  *ReturnNexus;   // Connection back to requestor.
   union _outmess_buf
   {
      BYTE            OutMessage[300];
      ASTRUCT         ASt;
      BSTRUCT         BSt;
      FPSTRUCT        FPSt;
      RSTRUCT         RSt;
      VSTRUCT         VSt;
      TAPSTRUCT       TAPSt;
      DIALUPREQUEST   DUPReq;
      CtiSAData       SASt;
   } Buffer;
   BYTE               TailFrame[2];               // 082702 CGP    // Hey, it should have been in there for a long time!

public:

   CtiOutMessage()
   {
       ::memset(this, 0, sizeof(CtiOutMessage));
       ReturnNexus = NULL;

       HeadFrame[0] = 0x02;      // STX
       HeadFrame[1] = 0xe0;
       TailFrame[0] = 0xea;
       TailFrame[1] = 0x03;      // ETX

       incrementCount();
   }

   CtiOutMessage(const CtiOutMessage &aRef)
   {
       *this = aRef;
       incrementCount();
   }

   CtiOutMessage(const CtiOutMessage *aRef)
   {
       //  this is unsafe - should be fixed to check for nulls, and should also be
       //    *this = *aRef;
       *this = CtiOutMessage( *aRef );
       incrementCount();
   }
   ~CtiOutMessage()
   {
       decrementCount();
   }

   CtiOutMessage& operator=(const CtiOutMessage &aRef)
   {
      if(this != &aRef)
      {
         HeadFrame[0]           = aRef.HeadFrame[0];
         HeadFrame[1]           = aRef.HeadFrame[1];
         DeviceID               = aRef.DeviceID;
         TargetID               = aRef.TargetID;
         Sequence               = aRef.Sequence;
         VerificationSequence   = aRef.VerificationSequence;

         Retry                  = aRef.Retry;
         Priority               = aRef.Priority;
         TimeOut                = aRef.TimeOut;

         Port                   = aRef.Port;
         Remote                 = aRef.Remote;

         OutLength              = aRef.OutLength;
         InLength               = aRef.InLength;

         Source                 = aRef.Source;
         Destination            = aRef.Destination;
         Command                = aRef.Command;
         Function               = aRef.Function;
         EventCode              = aRef.EventCode;
         MessageFlags           = aRef.MessageFlags & ~MessageFlag_StatisticsRequested;

         DeviceIDofLMGroup      = aRef.DeviceIDofLMGroup;
         TrxID                  = aRef.TrxID;

         ExpirationTime         = aRef.ExpirationTime;

         Request                = aRef.Request;

         ReturnNexus            = aRef.ReturnNexus;

         Buffer                 = aRef.Buffer;

         TailFrame[0]           = aRef.TailFrame[0];
         TailFrame[1]           = aRef.TailFrame[1];
      }

      return *this;
   }

   bool operator<( const CtiOutMessage &rhs ) const
   {
       return Priority > rhs.Priority; // Bigger is sorted first in terms of priority.
   }

   bool operator>( const CtiOutMessage &rhs ) const
   {
       return Priority < rhs.Priority; // Bigger is sorted first in terms of priority.
   }

} OUTMESS;

// This will be used to sort these things in a CtiQueue.
namespace std // eventually this needs to be removed, but for now we'll keep this the way it is.
{
  template <> struct greater<CtiOutMessage*>
  {
    bool operator()(CtiOutMessage const* p1, CtiOutMessage const* p2)
    {
      if(!p1)
        return true;
      if(!p2)
        return false;
      return *p1 > *p2;
    }
  };
};


//  InMessage.Buffer is 4kB, so this is no sweat
struct repeater_info
{
    long repeater_id;
    char repeater_name[120];
    long route_id;
    char route_name[120];
    unsigned route_position;
    unsigned total_stages;
};


class INMESS
{
public:

   LONG        DeviceID;                     // 083199 CGP     // The device id of the transmitter device.
   LONG        TargetID;                     // 022701 CGP     // The device id of the end-of-line device. May be the same as DeviceID depending on protocol
   INT         Sequence;                     // 083199 CGP

   INT         Port;
   INT         Remote;

   ULONG       Time;
   UINT        MilliTime;

   YukonError_t ErrorCode;
   UINT        MessageFlags;
   UINT        Priority;
   ULONG       InLength;

   PIL_ECHO    Return;

   Cti::StreamConnection  *ReturnNexus;   // Connection back to requestor.

   LONG        DeviceIDofLMGroup;           // 091300 CGP Helps us track lm command's success
   UINT        TrxID;                       // 091300 CGP Helps us track lm command's success

   BYTE        IDLCStat[18];          /* do NOT put anything between (IDLCStat & Buffer.Message) these */
   union _inmess_buf
   {
      BYTE            InMessage[4096];    /* two variables !!!!!! */
      DSTRUCT         DSt;
      struct
      {
         Cti::Optional<ESTRUCT>       ESt;
         Cti::Optional<repeater_info> Details;

      } RepeaterError;
      struct
      {
         DSTRUCT      DSt;              // This looks odd, but it lies on top of the one above.. Don't pull it out!
         DIALUPREPLY  DUPRep;
      } DUPSt;
   } Buffer;

public:

    INMESS()
    {
       ::memset(this, 0, sizeof(INMESS));
    }

    bool operator>(const INMESS &rhs) const
    {
       return Priority > rhs.Priority;
    }

    bool operator<(const INMESS &rhs) const
    {
       return Priority < rhs.Priority;
    }
};

const auto get_inmess_target_device = [](const INMESS &im)
{
    return im.TargetID
            ? im.TargetID
            : im.DeviceID;
};

/* Prototypes from UCTTime.C */
IM_EX_CTIBASE int            UCTFTime (struct timeb *);
IM_EX_CTIBASE struct tm *    UCTLocalTime (time_t, USHORT);               //
IM_EX_CTIBASE time_t         UCTMakeTime (struct tm *);
IM_EX_CTIBASE ULONG          LongTime (void);
IM_EX_CTIBASE ULONG          DSTFlag (void);
IM_EX_CTIBASE void           UCTLocoTime (time_t, USHORT, struct tm *);
IM_EX_CTIBASE ULONG          setNextInterval(time_t, ULONG);
IM_EX_CTIBASE time_t         MidNightWas (time_t, USHORT);

#pragma pack(pop, message_packing)     // Restore the prior packing alignment..

/* DST active flag */
#define DSTACTIVE           0x8000
#define DSTSET(q)  ((DSTFlag ()) ? (q | DSTACTIVE) : (q & ~DSTACTIVE))

