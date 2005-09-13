/*-----------------------------------------------------------------------------*
*
*    FILE NAME: fdrsinglesocket.h
*
*    DATE: 06/12/2001
*
*    PVCS KEYWORDS:
*    ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/FDR/INCLUDE/fdrsinglesocket.h-arc  $
*    REVISION     :  $Revision: 1.4 $
*    DATE         :  $Date: 2005/09/13 20:45:53 $
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
*     $Log: fdrsinglesocket.h,v $
*     Revision 1.4  2005/09/13 20:45:53  tmack
*     In the process of working on the new ACS(MULTI) implementation, the following changes were made:
*
*     - removed the ntohieeef() and htonieeef() methods that were moved to a base class
*
*
*
*    Copyright (C) 2005 Cannon Technologies, Inc.  All rights reserved.
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

    private:
        CtiFDRSocketLayer           *iLayer;
};


#endif  //  #ifndef __FDRSingleSocket_H__


