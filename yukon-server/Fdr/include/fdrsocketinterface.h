#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrsocketinterface.h
*
*    DATE: 04/27/2001
*
*    AUTHOR: David Sutton
*
*    PURPOSE: base class for the socket interfaces such as ACS,VALMET, INET
*
*    DESCRIPTION:  base class for the socket type interfaces
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRSOCKETINTERFACE_H__
#define __FDRSOCKETINTERFACE_H__

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this
#include <rw/cstring.h>
#include <rw/tpslist.h>

#include "dlldefs.h"
#include "queues.h"
#include "fdrinterface.h"

class CtiFDRSocketConnection;
class RWTime;

class IM_EX_FDRBASE CtiFDRSocketInterface : public CtiFDRInterface
{                                    
    typedef CtiFDRInterface Inherited;

    public:
        // constructors and destructors
        CtiFDRSocketInterface(RWCString & interfaceType, int aPort=0, int aWindow = 120); 

        virtual ~CtiFDRSocketInterface();

        virtual BOOL    init( void );   
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

		bool loadTranslationLists(void);
                     
        int  getPortNumber () const;
        CtiFDRSocketInterface& setPortNumber(int aPort);

        int  getTimestampReasonabilityWindow () const;
        CtiFDRSocketInterface& setTimestampReasonabilityWindow(int aWindow);
                       
        int  getPointTimeVariation () const;
        CtiFDRSocketInterface& setPointTimeVariation(int aTime);

		virtual bool loadList(RWCString &aDirection, CtiFDRPointList &aList) = 0;
        virtual CHAR *buildForeignSystemHeartbeatMsg (void) = 0;
        virtual INT getMessageSize(CHAR *data)=0;
        virtual RWCString decodeClientName(CHAR *data)=0;
        virtual int  sendAllPoints(void);
        virtual bool sendMessageToForeignSys ( CtiMessage *aMessage );
        virtual bool buildAndWriteToForeignSystem (CtiFDRPoint &aPoint )=0;

        bool isRegistered();
        CtiFDRSocketInterface &setRegistered (bool aFlag=true);
        virtual bool CtiFDRSocketInterface::isClientConnectionValid (void);

        CtiMutex & getListenerMux ();
        void shutdownListener();
        CtiFDRSocketConnection * getListener ();
        CtiFDRSocketInterface & setListener (CtiFDRSocketConnection *aListener);


    private:

		int     iPortNumber;
		int     iTimestampReasonabilityWindow;
        bool    iRegistered;
        int     iPointTimeVariation;

        CtiMutex                    iListenerMux;
        CtiFDRSocketConnection      *iListener;



};                              

#endif

