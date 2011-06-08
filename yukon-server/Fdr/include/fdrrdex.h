#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrrdex.h
*
*    DATE: 12/10/2001
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Generic Interface to a scada systemm (class header)
*
*    DESCRIPTION: This class implements an interface that exchanges point data
*                 with a scada system.  The data is both status and Analog data.
*             Information is exchanged using sockets opened on a predefined socket
*             number and also pre-defined messages between the systems.  See the
*             design document for more information
*    History:
      $Log: fdrrdex.h,v $
      Revision 1.10.2.2  2008/11/18 20:11:30  jmarks
      [YUKRV-525] Comment: YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      * Responded to reviewer comments
      * Changed monitor's version to MUTEX version
      * Other changes for compilation

      Revision 1.10.2.1  2008/11/13 17:23:46  jmarks
      YUK-5273 Upgrade Yukon tool chain to Visual Studio 2005/2008

      Responded to reviewer comments again.

      I eliminated excess references to windows.h .

      This still left over 100 references to it where "yukon.h" or "precompiled.h" was not obviously included.  Some other chaining of references could still be going on, and of course it is potentially possible that not all the files in the project that include windows.h actually need it - I didn't check for that.

      None-the-less, I than added the NOMINMAX define right before each place where windows.h is still included.
      Special note:  std::min<LONG>(TimeOut, 500); is still required for compilation.

      In this process I occasionally deleted a few empty lines, and when creating the define, also added some.

      This may not have affected every file in the project, but while mega-editing it certainly seemed like it did.

      Revision 1.10  2008/10/29 18:16:47  mfisher
      YUK-6374 Remove unused DSM/2 remnants
      Removed many orphaned function headers and structure definitions
      Moved ILEX items closer to point of use in TimeSyncThread()

      Revision 1.9  2008/10/02 23:57:15  tspar
      YUK-5013 Full FDR reload should not happen with every point

      YUKRV-325  review changes

      Revision 1.8  2008/09/23 15:15:22  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Review changes. Most notable is mgr_fdrpoint.cpp now encapsulates CtiSmartMap instead of extending from rtdb.

      Revision 1.7  2008/09/15 21:09:16  tspar
      YUK-5013 Full FDR reload should not happen with every point db change

      Changed interfaces to handle points on an individual basis so they can be added
      and removed by point id.

      Changed the fdr point manager to use smart pointers to help make this transition possible.

      Revision 1.6  2007/11/12 16:46:55  mfisher
      Removed some Rogue Wave includes

      Revision 1.5  2005/12/20 17:17:16  tspar
      Commiting  RougeWave Replacement of:  RWCString RWTokenizer RWtime RWDate Regex

      Revision 1.4  2005/10/19 16:53:23  dsutton
      Added the ability to set the connection timeout using a cparm.  Interfaces will
      kill the connection if they haven't heard anything from the other system after
      this amount of time.  Defaults to 60 seconds.  Also changed the logging to
      the system log so we don't log every unknown point as it comes in from the
      foreign system.  It will no log these points only if a debug level is set.
      Revision 1.3.58.3  2005/08/12 19:53:47  jliu
      Date Time Replaced

      Revision 1.3.58.2  2005/07/14 22:26:57  jliu
      RWCStringRemoved

      Revision 1.3.58.1  2005/07/12 21:08:39  jliu
      rpStringWithoutCmpParser

      Revision 1.3  2002/04/16 15:58:46  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:19:01  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

 *
 *    Rev 2.1   15 Feb 2002 11:11:30   dsutton
 * added two new cparms to control data flow to RDEX that limit the number of entries sent per so many seconds
 *
 *    Rev 2.0   20 Dec 2001 14:51:34   dsutton
 * Overrode the isregistrationneeded to return true
 *
 *    Rev 1.0   14 Dec 2001 17:14:06   dsutton
 * Initial revision.
 *
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRRDEX_H__
#define __FDRRDEX_H__


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
        // constructors and destructors
        CtiFDR_Rdex();

        virtual ~CtiFDR_Rdex();

        virtual int processMessageFromForeignSystem (CHAR *data);
        virtual CHAR *buildForeignSystemHeartbeatMsg (void);
        virtual CHAR *buildForeignSystemMsg (CtiFDRPoint &aPoint);
        virtual int getMessageSize(CHAR *data);
        virtual std::string decodeClientName(CHAR *data);

        virtual int readConfig( void );

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
        static const CHAR * KEY_DEBUG_MODE;
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


#endif  //  #ifndef __FDRRDEX_H__

