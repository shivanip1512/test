#pragma once

#include "fdrpointlist.h"
#include "fdrscadaserver.h"
#include "fdrscadahelper.h"

#pragma pack(push, valmetmulti_packing, 1)

struct IM_EX_FDRBASE CtiValmetPortId
{
    std::string PointName;
    int PortNumber;

    bool operator<(const CtiValmetPortId& other) const
    {
        if (PointName == other.PointName)
        {
            return PortNumber < other.PortNumber;
        }
        else
        {
            return PointName < other.PointName;
        }
    }

};

inline std::ostream& operator<< (std::ostream& os, const CtiValmetPortId& id)
{
    return os << "[VALMET: N=" << id.PointName << ", P=" << id.PortNumber << "]";
};


#pragma pack(pop, valmetmulti_packing)
#define VALMET_MULTI_HEADER_SIZE        32
class CtiTime;

class IM_EX_FDRVALMETMULTI CtiFDR_ValmetMulti : public CtiFDRScadaServer
{
    typedef CtiFDRScadaServer Inherited;

    public:
        // constructors and destructors
        CtiFDR_ValmetMulti();

        virtual ~CtiFDR_ValmetMulti();

        virtual bool alwaysSendRegistrationPoints();

        virtual std::string decodeClientName(CHAR *data);
        virtual int readConfig();
        virtual unsigned int getMessageSize(const char* data);
        virtual unsigned int getMagicInitialMsgSize(){return VALMET_MULTI_HEADER_SIZE;};

        virtual BOOL run( void );
        virtual BOOL stop( void );

        virtual int processScanMessage(CHAR *data);
        virtual CtiFDRClientServerConnection* createNewConnection(SOCKET newConnection);

        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList);
        virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList);

        virtual bool buildForeignSystemHeartbeatMsg(char** buffer,
                                                    unsigned int& bufferSize);
        virtual bool buildForeignSystemMessage(const CtiFDRDestination& destination,
                                               char** buffer,
                                               unsigned int& bufferSize);


        virtual bool processValueMessage(Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size);
        virtual bool processStatusMessage(Cti::Fdr::ServerConnection& connection,
                                          const char* data, unsigned int size);
        virtual bool processControlMessage(Cti::Fdr::ServerConnection& connection,
                                           const char* data, unsigned int size);
        virtual bool processTimeSyncMessage(Cti::Fdr::ServerConnection& connection,
                                            const char* data, unsigned int size);

        virtual void signalReloadList();
        virtual void signalPointRemoved(std::string &pointName);

        void updatePointQualitiesOnDevice(PointQuality_t quality, long paoId);

        CtiFDRPointSPtr findFdrPointInPointList(const std::string &translationName);

        virtual CtiFDRClientServerConnection* findConnectionForDestination(const CtiFDRDestination destination) const;

    private:

        void startMultiListeners();
        void stopMultiListeners();

        std::set<int> _listeningPortNumbers;

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
        static const CHAR * KEY_SCAN_DEVICE_POINTNAME;
        static const CHAR * KEY_SEND_ALL_POINTS_POINTNAME;

        CtiFDRScadaHelper<CtiValmetPortId>* _helper;

        std::vector<RWThreadFunction> _listenerThreads;

        // maps ip address -> server name
        typedef std::map<std::string, std::string> ServerNameMap;
        ServerNameMap _serverNameLookup;

        typedef std::map<std::string,int> NameToPointIdMap;
        NameToPointIdMap _nameToPointId;
        std::string _scanDevicePointName;
        std::string _sendAllPointsPointName;
};







