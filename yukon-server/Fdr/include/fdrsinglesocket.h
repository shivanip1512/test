#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrsinglesocket.h
*
*    DATE: 06/12/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/INCLUDE/fdrsinglesocket.h-arc  $
*    REVISION     :  $Revision: 1.3 $
*    DATE         :  $Date: 2002/04/16 15:58:46 $
*
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Base class for single socket interfaces, acs,ilex,valmet
*
*    DESCRIPTION: This class implements the base functionality of a socket interface
*                   that uses a single socket to exchange data with a foreign system
*
*    ---------------------------------------------------
*    History: 
      $Log: fdrsinglesocket.h,v $
      Revision 1.3  2002/04/16 15:58:46  softwarebuild
      20020416_1031_2_16

      Revision 1.2  2002/04/15 15:19:01  cplender

      This is an update due to the freezing of PVCS on 4/13/2002

 * 
 *    Rev 2.5   01 Mar 2002 13:10:22   dsutton
 * function proto's for timesync processing and client link state processing
 * 
 *    Rev 2.4   20 Dec 2001 14:50:02   dsutton
 * added a isregistrationneeded function to check if the initial data dump is dependant on a registration message.  Base function in this class returns false and it can be overridden for any child classes.  Aslo a call to see if the client connection is valid to keep from getting stuck in the initial upload loop
 * 
 *    Rev 2.3   14 Dec 2001 17:12:18   dsutton
 * identical functions from child classes were moved here
 * 
 *    Rev 2.2   15 Nov 2001 16:15:54   dsutton
 * code for multipliers and an queue for the messages to dispatch
 * 
 *    Rev 2.1   26 Oct 2001 15:21:46   dsutton
 * moving revision 1 to 2.x
 * 
 *    Rev 1.0   19 Jun 2001 10:43:36   dsutton
 * Initial revision.
*
*
*
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
*-----------------------------------------------------------------------------*
*/
#ifndef __FDRSINGLESOCKET_H__
#define __FDRSINGLESOCKET_H__

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this
#include <rw/cstring.h>
#include <rw/tpslist.h>

#include "dlldefs.h"
#include "queues.h"
#include "fdrsocketlayer.h"
#include "fdrinterface.h"

#define SINGLE_SOCKET_NULL               0
#define SINGLE_SOCKET_REGISTRATION       1
#define SINGLE_SOCKET_ACKNOWLEDGEMENT    2
#define SINGLE_SOCKET_VALUE            101
#define SINGLE_SOCKET_STATUS           102
#define SINGLE_SOCKET_VALMET_CONTROL   103 // arrgh, backward compatibility
#define SINGLE_SOCKET_CONTROL          201
#define SINGLE_SOCKET_FORCESCAN        110 
#define SINGLE_SOCKET_TIMESYNC         401
#define SINGLE_SOCKET_STRATEGY         501
#define SINGLE_SOCKET_STRATEGYSTOP     503


class RWTime;

class IM_EX_FDRBASE CtiFDRSingleSocket : public CtiFDRSocketInterface
{                                    
    typedef CtiFDRSocketInterface Inherited;

    public:
        // constructors and destructors
        CtiFDRSingleSocket(RWCString &); 

        virtual ~CtiFDRSingleSocket();

        CtiFDRSocketLayer * getLayer (void);
        CtiFDRSingleSocket& setLayer (CtiFDRSocketLayer * aLayer);

        // coming from the base class fdrsocketinterface
        virtual int processMessageFromForeignSystem (CHAR *data);
        virtual CHAR *buildForeignSystemHeartbeatMsg (void);
        virtual int getMessageSize(CHAR *data);
        virtual RWCString decodeClientName(CHAR *data);

        virtual bool loadList(RWCString &aDirection,  CtiFDRPointList &aList);

        virtual BOOL    init( void );   
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

        // effective for all classes inheriting from here
        virtual CHAR *buildForeignSystemMsg (CtiFDRPoint &aPoint)=0;
        virtual bool buildAndWriteToForeignSystem (CtiFDRPoint &aPoint );
        virtual int readConfig( void )=0;
        virtual bool translateAndUpdatePoint(CtiFDRPoint *translationPoint, int aIndex)=0;

        virtual int processValueMessage(CHAR *data);
        virtual int processStatusMessage(CHAR *data);
        virtual int processControlMessage(CHAR *data);
        virtual int processRegistrationMessage(CHAR *data);
        virtual int processTimeSyncMessage(CHAR *data);

        virtual bool isRegistrationNeeded(void);
        virtual bool isClientConnectionValid (void);
        virtual void setCurrentClientLinkStates();
        
    protected:

        RWThreadFunction    iThreadSendDebugData;
        void threadFunctionSendDebugData( void );

        RWThreadFunction    iThreadConnection;
        void threadFunctionConnection( void );

        FLOAT   ntohieeef (LONG NetLong);
        LONG    htonieeef (FLOAT  HostFloat); 

    private:
        CtiFDRSocketLayer           *iLayer;
};


#endif  //  #ifndef __FDRSingleSocket_H__


