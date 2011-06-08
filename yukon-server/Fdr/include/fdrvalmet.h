#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrvalmet.h
*
*    DATE: 03/07/2001
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Interface to the VALMET scada systemm (class header)
*
*    DESCRIPTION: This class implements an interface that exchanges point data
*                 from an VALMET scada system.  The data is both status and Analog data.
*                 Information is exchanged using sockets opened on a predefined socket
*                 number and also pre-defined messages between the systems.  See the
*                 design document for more information
*    History:
      $Log: fdrvalmet.h,v $
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

      Revision 1.9  2008/10/02 23:57:16  tspar
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
      Revision 1.3.58.3  2005/08/12 19:53:48  jliu
      Date Time Replaced

      Revision 1.3.58.2  2005/07/14 22:26:57  jliu
      RWCStringRemoved

      Revision 1.3.58.1  2005/07/12 21:08:39  jliu
      rpStringWithoutCmpParser

      Revision 1.3  2002/04/16 15:58:47  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:19:01  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

 *
 *    Rev 2.6   08 Apr 2002 14:41:28   dsutton
 * updated foreigntoyukontime function to contain a flag that says whether we're processing a time sync.  If we are, we don't want to do the validity window since the timesync has a configurable window of its own
 *
 *    Rev 2.5   01 Mar 2002 13:04:14   dsutton
 * added new cparms to handle timesync functions
 *
 *    Rev 2.4   15 Feb 2002 11:11:44   dsutton
 * added two new cparms to control data flow to VALMET that limit the number of entries sent per so many seconds
 *
 *    Rev 2.3   14 Dec 2001 17:08:12   dsutton
 * changed prototypes for new fdrpointclass and moved a few functions to singlesocket class
 *
 *    Rev 2.2   15 Nov 2001 16:15:54   dsutton
 * code for multipliers and an queue for the messages to dispatch
 *
 *    Rev 2.1   26 Oct 2001 15:21:56   dsutton
 * moving revision 1 to 2.x
 *
 *    Rev 1.1   23 Aug 2001 14:03:24   dsutton
 * add function to check send state of a point to intercept control points
 *
 *    Rev 1.0   19 Jun 2001 10:43:52   dsutton
 * Initial revision.


*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

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
            USHORT Value;
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

        virtual int processValueMessage(CHAR *data);
        virtual int processStatusMessage(CHAR *data);
        virtual int processControlMessage(CHAR *data);
        virtual int processTimeSyncMessage(CHAR *data);

        USHORT      ForeignToYukonQuality (USHORT aQuality);
        int         ForeignToYukonStatus (USHORT aStatus);
        CtiTime      ForeignToYukonTime (PCHAR aTime, bool aTimeSyncFlag = false);

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

};



#endif  //  #ifndef __FDRVALMET_H__


