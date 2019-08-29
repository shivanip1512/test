#pragma once

#include "fdrpointlist.h"
#include "fdrscadaserver.h"
#include "fdrscadahelper.h"
#include "loggable.h"

#pragma pack(push, valmetmulti_packing, 1)

// forward declarations
struct CtiValmetPortId;
std::ostream& operator<< (std::ostream&, const CtiValmetPortId&);

using boost::thread;

struct CtiValmetPortId : public Cti::Loggable
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

    std::string toString() const
    {
        std::ostringstream oss;
        oss << *this;
        return oss.str();
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
        DEBUG_INSTRUMENTATION;

        // constructors and destructors
        CtiFDR_ValmetMulti();

        virtual ~CtiFDR_ValmetMulti();

        virtual bool alwaysSendRegistrationPoints();

        virtual std::string decodeClientName(CHAR *data);
        bool readConfig() override;
        virtual unsigned int getMessageSize(const char* data);
        virtual unsigned int getHeaderLength() override  {  return VALMET_MULTI_HEADER_SIZE;  }

        virtual BOOL run( void );
        virtual BOOL stop( void );

        virtual int processScanMessage(CtiFDRClientServerConnection* connection, const char* data);
        virtual CtiFDRClientServerConnectionSPtr createNewConnection(SOCKET newConnection);

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

        virtual void begineNewPoints();
        virtual void signalReloadList();

        void updatePointQualitiesOnDevice(PointQuality_t quality, long paoId);

        CtiFDRPointSPtr findFdrPointInPointList(const std::string &translationName);

         virtual CtiFDRClientServerConnectionSPtr findConnectionForDestination(const CtiFDRDestination destination) const;

    protected:
        void threadListenerStartupMonitor();

    private:

        void startMultiListeners();
        void stopMultiListeners();

        Cti::WorkerThread _listenerStarterThread;
        CtiMutex _listeningThreadManagementMutex;

        CtiValueQueue<int>  _listeningPortNumbers;

        static const CHAR * KEY_LISTEN_PORT_NUMBER;
        static const CHAR * KEY_TIMESTAMP_WINDOW;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_QUEUE_FLUSH_RATE;
        static const CHAR * KEY_OUTBOUND_SEND_RATE;
        static const CHAR * KEY_OUTBOUND_SEND_INTERVAL;
        static const CHAR * KEY_TIMESYNC_UPDATE;
        static const CHAR * KEY_TIMESYNC_VARIATION;
        static const CHAR * KEY_LINK_TIMEOUT;
        static const CHAR * KEY_SCAN_DEVICE_POINTNAME;
        static const CHAR * KEY_SEND_ALL_POINTS_POINTNAME;
        static const CHAR * KEY_STARTUP_DELAY_SECONDS;
        static const CHAR * KEY_PORTS_TO_LOG;

        CtiFDRScadaHelper<CtiValmetPortId>* _helper;

        typedef std::map<int,thread*> PortNumToListenerThreadMap;
        PortNumToListenerThreadMap _listenerThreadMap;
        boost::thread_group _listenerThreads;

        typedef std::map<std::string,int> NameToPointIdMap;
        NameToPointIdMap _receiveNameToPointId;
        std::string _scanDevicePointName;
        std::string _sendAllPointsPointName;
        int _listenerThreadStartupDelay;

        std::set<int> _portsToLog;
        bool _specificPortLoggingEnabled;
        bool isPortLoggingNotRestricted(Cti::Fdr::ServerConnection& connection);
        bool isPortLoggingNotRestricted(const CtiFDRDestination& destination);
        bool isPortLoggingNotRestricted(int portNumber);
};
