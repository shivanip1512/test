#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
/*****************************************************************************
*
*    FILE NAME: fdrinet.h
*
*    DATE: 04/27/2001
*
*    AUTHOR: David Sutton
*
*    PURPOSE: Interface to the Inet subsystem
*
*    DESCRIPTION: This class implements an interface that exchanges point data
*                 in a standard device/point format
*
*    Copyright (C) 2000 Cannon Technologies, Inc.  All rights reserved.
****************************************************************************
*/

#ifndef __FDRINET_H__
#define __FDRINET_H__

#include <windows.h>    //  NOTE:  if porting this to non-WIN32, make sure to replace this
#include <vector>
#include <rw/cstring.h>
#include <rw/tpslist.h>

#include "dlldefs.h"
#include "queues.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"
#include "device.h"             // get the raw states

// global defines
#define INET_PORTNUMBER     	1000

#define INETDESTSIZE            10

// Inet Type definitions 
#define INETTYPEDEFAULT              0
#define INETTYPEVALUE                1
#define INETTYPEALARM                2
#define INETTYPETIMESYNC             50
#define INETTYPESHUTDOWN             51
#define INETTYPESEASONCHANGE         52
#define INETTYPELOADSCRAM            53
#define INETTYPELOADSTRATEGY         54
#define INETTYPETEXTALARM            55
#define INETTYPENULL                 56
#define INETTYPETEXTMESSAGE          57
#define INETTYPEFBLCMODE             58
#define INETTYPERENAMEDDEFAULT       100
#define INETTYPERENAMEDVALUE         101
#define INETTYPERENAMEDALARM         102


#define INETDATAINVALID         0X0200
#define INETUNREASONABLE        0x0400
#define INETPLUGGED             0x0800
#define INETMANUAL              0x1000
#define INETOUTOFSCAN           0x2000

#pragma pack(push, inet_packing, 1)


typedef struct _InetNull_t {
    USHORT Type;
    CHAR SourceName[INETDESTSIZE];
} InetHeartbeat_t;

typedef struct _InetShutdown_t {
    USHORT Type;
    CHAR SourceName[INETDESTSIZE];
} InetShutdown_t;

typedef struct _InetValue_t {
    USHORT Type;
    CHAR SourceName[INETDESTSIZE];
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
    USHORT Quality;
    ULONG TimeStamp;
    USHORT AlarmState;
    FLOAT Value;
} InetValue_t;


// leaving a few choices in here, I don't know if they will actually be used
typedef struct _InetInterface_t {
    USHORT Type;
    CHAR SourceName[INETDESTSIZE];
    union {
        struct {
            CHAR DeviceName[STANDNAMLEN];
            CHAR PointName[STANDNAMLEN];
            USHORT Quality;
            ULONG TimeStamp;
            USHORT AlarmState;
            FLOAT Value;
        } value;
    } msgUnion;
} InetInterface_t;

#pragma pack(pop, inet_packing)     // Restore the prior packing alignment..

class RWTime;


// forward class declarations
//typedef RWTPtrSlist<CtiFDRSocketLayer>  InetConnectionList;
//typedef RWTPtrSlist<RWCString>  InetClientList;

class IM_EX_FDRINET CtiFDR_Inet : public CtiFDRSocketInterface
{                                    
    typedef CtiFDRSocketInterface Inherited;

    public:
        // constructors and destructors
        CtiFDR_Inet(RWCString aName=RWCString ("INET")); 

        virtual ~CtiFDR_Inet();

        virtual int processMessageFromForeignSystem (CHAR *data);
        virtual CHAR *buildForeignSystemHeartbeatMsg (void);
        virtual int getMessageSize(CHAR *data);
        virtual RWCString decodeClientName(CHAR *data);
        virtual bool CtiFDR_Inet::buildAndWriteToForeignSystem (CtiFDRPoint &aPoint );

        virtual BOOL    init( void );   
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

        RWCString & getSourceName();
        RWCString  getSourceName() const;
        CtiFDR_Inet &setSourceName (RWCString &aName);

        // end getters and setters
        static const CHAR * KEY_LISTEN_PORT_NUMBER;
        static const CHAR * KEY_TIMESTAMP_WINDOW;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_SOURCE_NAME;
        static const CHAR * KEY_SERVER_LIST;
        static const CHAR * KEY_DEBUG_MODE;
        static const CHAR * KEY_QUEUE_FLUSH_RATE;

        enum {Inet_Invalid=0,
              Inet_Open, 
              Inet_Closed,
              Inet_Indeterminate,
              Inet_State_Four,
              Inet_State_Five,
              Inet_State_Six};

    protected:

        RWThreadFunction    iThreadSendDebugData;
        void threadFunctionSendDebugData( void );

        RWThreadFunction    iThreadMonitor;
        void threadFunctionMonitor( void );

        RWThreadFunction    iThreadServer;
        void threadFunctionServerConnection( void );

        RWThreadFunction    iThreadClient;
        void threadFunctionClientConnection( void );

        virtual bool loadTranslationLists(void);
        virtual bool loadClientList(void);
		bool loadList(RWCString &aDirection, CtiFDRPointList &aList);

        virtual int   readConfig( void );
        virtual void setCurrentClientLinkStates();
        int   findConnectionByNameInList(RWCString aName);
        int   findClientInList(SOCKADDR_IN aAddr);
        virtual bool  findAndInitializeClients( void );

        CHAR *buildForeignSystemMsg ( CtiMessage *aMessage );


        USHORT      ForeignToYukonQuality (USHORT aQuality);
        USHORT      YukonToForeignQuality (USHORT aQuality);
        int         processValueMessage(InetInterface_t *data);

        vector< CtiFDRSocketLayer *>& getConnectionList ();
        vector< CtiFDRSocketLayer *> getConnectionList () const;
        CtiMutex & getConnectionMux ();

        vector< RWCString >& getClientList ();
        vector< RWCString > getClientList () const;
        CtiMutex & getClientListMux ();

    private:

        RWCString                   iSourceName;

        HEV                         iClientConnectionSemaphore;

        // need getters and setters for these guys
        vector< CtiFDRSocketLayer * > iConnectionList;
        CtiMutex                    iConnectionListMux;

        vector< RWCString > iClientList;
        CtiMutex                    iClientListMux;


};                              

#endif
