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
*				  Information is exchanged using sockets opened on a predefined socket 
*				  number and also pre-defined messages between the systems.  See the 
*				  design document for more information
*    History: 
      $Log: fdrvalmet.h,v $
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

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this
#include <rw/cstring.h>
#include <rw/tpslist.h>

#include "dlldefs.h"
#include "queues.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"
#include "device.h"             // get the raw states
#include "fdrsinglesocket.h"

// global defines
#define VALMET_PORTNUMBER     	1666


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

class RWTime;

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
        virtual RWCString decodeClientName(CHAR *data);

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

        virtual int processValueMessage(CHAR *data);
        virtual int processStatusMessage(CHAR *data);
        virtual int processControlMessage(CHAR *data);
        virtual int processTimeSyncMessage(CHAR *data);

        USHORT      ForeignToYukonQuality (USHORT aQuality);
        int         ForeignToYukonStatus (USHORT aStatus);
        RWTime      ForeignToYukonTime (PCHAR aTime, bool aTimeSyncFlag = false);

        RWCString   YukonToForeignTime (RWTime aTimeStamp);
        USHORT      YukonToForeignQuality (USHORT aQuality);
        USHORT      YukonToForeignStatus (int aStatus);

        bool translateAndUpdatePoint(CtiFDRPoint *translationPoint, int aIndex);


        enum {  Valmet_Invalid = 0, 
                Valmet_Open = 1, 
                Valmet_Closed=2, 
                Valmet_Indeterminate=3};

};



#endif  //  #ifndef __FDRVALMET_H__


