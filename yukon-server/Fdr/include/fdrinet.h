#pragma once

#include <windows.h>
#include <vector>

#include "dlldefs.h"
#include "queues.h"
#include "fdrinterface.h"
#include "fdrpointlist.h"

// global defines
#define INET_PORTNUMBER         1000

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

class CtiTime;


// forward class declarations

class IM_EX_FDRINET CtiFDR_Inet : public CtiFDRSocketInterface
{
    typedef CtiFDRSocketInterface Inherited;

    public:
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDR_Inet(std::string aName=std::string ("INET"));

        virtual ~CtiFDR_Inet();

        virtual int processMessageFromForeignSystem (CHAR *data);
        virtual CHAR *buildForeignSystemHeartbeatMsg (void);
        virtual int getMessageSize(CHAR *data);
        virtual std::string decodeClientName(CHAR *data);
        virtual bool CtiFDR_Inet::buildAndWriteToForeignSystem (CtiFDRPoint &aPoint );

        virtual BOOL    init( void );
        virtual BOOL    run( void );
        virtual BOOL    stop( void );

        std::string & getSourceName();
        std::string  getSourceName() const;
        CtiFDR_Inet &setSourceName (std::string &aName);

        enum
        {
            Inet_Invalid = 0,
            Inet_Open,
            Inet_Closed,
            Inet_Indeterminate,
            Inet_State_Four,
            Inet_State_Five,
            Inet_State_Six
        };

    protected:

        Cti::WorkerThread   iThreadMonitor;
        void threadFunctionMonitor( void );

        Cti::WorkerThread   iThreadServer;
        void threadFunctionServerConnection( void );

        Cti::WorkerThread   iThreadClient;
        void threadFunctionClientConnection( void );

        virtual bool loadTranslationLists(void);
        virtual bool loadClientList(void);
        bool loadList(std::string &aDirection, CtiFDRPointList &aList);

        bool readConfig() override;
        virtual void setCurrentClientLinkStates();
        int   findConnectionByNameInList(const std::string &aName);
        int   findClientInList(const Cti::SocketAddress& aAddr);
        virtual bool  findAndInitializeClients( void );

        CHAR *buildForeignSystemMsg ( CtiMessage *aMessage );


        USHORT      ForeignToYukonQuality (USHORT aQuality);
        USHORT      YukonToForeignQuality (USHORT aQuality);
        int         processValueMessage(InetInterface_t *data);

        std::vector< CtiFDRSocketLayer *>& getConnectionList ();
        std::vector< CtiFDRSocketLayer *> getConnectionList () const;
        CtiMutex & getConnectionMux ();

        std::vector< std::string >& getClientList ();
        std::vector< std::string > getClientList () const;
        CtiMutex & getClientListMux ();

        std::string getServerList() const;
        void setServerList( const std::string & serverList );

    private:
        //translateSingle Point
        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList = false);

        std::string                   iSourceName;

        HEV                         iClientConnectionSemaphore;

        // need getters and setters for these guys
        std::vector< CtiFDRSocketLayer * > iConnectionList;
        CtiMutex                    iConnectionListMux;

        std::vector< std::string > iClientList;
        CtiMutex                    iClientListMux;

        std::string                   iServerList;
};
