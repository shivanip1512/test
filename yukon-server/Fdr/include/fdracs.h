#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdracs.h
*
*    DATE: 03/07/2001
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Interface to the ACS scada systemm (class header)
*
*    DESCRIPTION: This class implements an interface that exchanges point data
*                 from an ACS scada system.  The data is both status and Analog data.
*				  Information is exchanged using sockets opened on a predefined socket 
*				  number and also pre-defined messages between the systems.  See the 
*				  design document for more information
*    History: 
      $Log: fdracs.h,v $
      Revision 1.4  2004/02/13 20:37:04  dsutton
      Added a new cparm for ACS interface that allows the user to filter points
      being routed to ACS by timestamp.  The filter is the number of seconds
      since the last update.   If set to zero, system behaves as it always has,
      routing everything that comes from dispatch.  If the cparm is > 0, FDR will
      first check the value and route the point if it has changed.  If the value has
      not changed, FDR will check the timestamp to see if it is greater than or equal
      to the previous timestamp plus the cparm.  If so, route the data, if not, throw
      the point update away.

      Revision 1.3  2002/04/16 15:58:43  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:19:00  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

 * 
 *    Rev 2.6   08 Apr 2002 14:41:20   dsutton
 * updated foreigntoyukontime function to contain a flag that says whether we're processing a time sync.  If we are, we don't want to do the validity window since the timesync has a configurable window of its own
 * 
 *    Rev 2.5   01 Mar 2002 13:03:44   dsutton
 * added new cparms to handle timesync functions
 * 
 *    Rev 2.4   15 Feb 2002 11:11:18   dsutton
 * added two new cparms to control data flow to ACS that limit the number of entries sent per so many seconds
 * 
 *    Rev 2.3   14 Dec 2001 17:08:06   dsutton
 * changed prototypes for new fdrpointclass and moved a few functions to singlesocket class
 * 
 *    Rev 2.2   15 Nov 2001 16:15:50   dsutton
 * code for multipliers and an queue for the messages to dispatch
 * 
 *    Rev 2.1   26 Oct 2001 15:20:52   dsutton
 * moving revision 1 to 2.x
 * 
 *    Rev 1.4.1.0   26 Oct 2001 14:07:34   dsutton
 * prototype for is point sendable 
 * 
 *    Rev 1.4   23 Aug 2001 14:01:00   dsutton
 * new function prototype for function checking send status of point
 * 
 *    Rev 1.3   19 Jun 2001 10:30:28   dsutton
 * now inherits from fdrsinglesocket to make building of the ilex, valmet
 * interfaces easier


*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRACS_H__
#define __FDRACS_H__

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
#define ACS_PORTNUMBER     	1668


/* Definitions and structures used to share data with ACS */

// ACS quality codes
#define ACS_NORMAL            	0x0001		// channel up good reading
#define ACS_MANUALENTRY         0x0002		// operator entered
#define ACS_PLUGGED		        0x0004		// channel down
/*

NOTE:  All data limit violations will be handled by the receiving system
*/

/*
NOTE:  Decision was made to use function 201 (ACS_CONTROL) as a way of starting and
		stopping control by strategy and also by group (as opposed to functions 503/501).  
		Differentation between these and regular control are to be implemented on the 
		DSM2 side by using the naming convention @C_ in the device name
		
		ACS_OPEN  	=  	stop strategy	= 	restore load group
		ACS_CLOSED	=	start strategy	=	Shed load group
*/
		
/*
Naming conventions for points are as follows:

	RrrrrCcPxxxx		where
							rrrr = remote number
							c	  = category
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
#define ACS_ERRROR_NOREPLY				31
#define ACS_ERRROR_SEQUENCE			32
#define ACS_ERRROR_FRAME				33
#define ACS_ERRROR_BADCRC				34
#define ACS_ERRROR_BADLENGTH			35
#define ACS_ERRROR_UNKNOWN				37
#define ACS_ERRROR_DSM2_DATABASE		53
#define ACS_ERRROR_RTU_DISABLED		78
#define ACS_ERRROR_PORT_DISABLED		83

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

class RWTime;

class IM_EX_FDRACS CtiFDR_ACS : public CtiFDRSingleSocket
{                                    
    typedef CtiFDRSingleSocket Inherited;

    public:
        // constructors and destructors
        CtiFDR_ACS(); 

        virtual ~CtiFDR_ACS();

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
        static const CHAR * KEY_POINT_TIME_VARIATION;

        virtual int processValueMessage(CHAR *data);
        virtual int processStatusMessage(CHAR *data);
        virtual int processControlMessage(CHAR *data);
        virtual int processTimeSyncMessage(CHAR *data);

        RWCString   ForeignToYukonId (USHORT remote, CHAR category, USHORT point);
        USHORT      ForeignToYukonQuality (USHORT aQuality);
        int         ForeignToYukonStatus (USHORT aStatus);
        RWTime      ForeignToYukonTime (PCHAR aTime, bool aTimeSyncFlag = false);

        RWCString   YukonToForeignTime (RWTime aTimeStamp);
        int         YukonToForeignId (RWCString aPointName, USHORT &remoteNumber, CHAR &category, USHORT &pointNumber);
        USHORT      YukonToForeignQuality (USHORT aQuality);
        USHORT      YukonToForeignStatus (int aStatus);

        enum {ACS_Open = 0, ACS_Closed = 1, ACS_Invalid=99};

        virtual bool translateAndUpdatePoint(CtiFDRPoint *translationPoint, int aDestinationIndex);

};


#endif  //  #ifndef __FDRACS_H__

