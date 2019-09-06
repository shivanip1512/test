#pragma once

#include "dlldefs.h"
#include "queues.h"
#include "fdrpointlist.h"
#include "fdrscadaserver.h"
#include "dnp_object_analoginput.h"
#include "prot_dnpSlave.h"
#include "string_util.h"
#include "random_generator.h"
#include "AttributeService.h"

#include <boost/tuple/tuple_comparison.hpp>

#include <map>

class AttributeService;
class CtiRequestMsg;
class CtiReturnMsg;

namespace Cti {
namespace Fdr {

struct DnpId : public Cti::Loggable
{
    DEBUG_INSTRUMENTATION;

    USHORT MasterId;
    USHORT SlaveId;
    CtiPointType_t PointType;
    USHORT Offset;
    double Multiplier;
    BOOL valid;

    CtiFDRClientServerConnection::Destination MasterServerName;

    bool operator<(const DnpId &other) const
    {
        return boost::tie(      MasterServerName,       MasterId,       SlaveId,       PointType,       Offset)
             < boost::tie(other.MasterServerName, other.MasterId, other.SlaveId, other.PointType, other.Offset);
    }

    std::string toString() const
    {
        return Cti::StreamBuffer()
            << "[DNP: Master= "<< MasterServerName
            << ", M=" << MasterId
            << ", S=" << SlaveId
            << ", P=" << PointType
            << ", O=" << Offset << "]";
    }
};

class IM_EX_FDRDNPSLAVE DnpSlave : public CtiFDRSocketServer
{
    public:
        DEBUG_INSTRUMENTATION;
        // constructors and destructors
        DnpSlave();
        ~DnpSlave();  //  delay definition for incomplete AttributeService type

        unsigned int getMessageSize(const char* data) override;

        bool readConfig() override;

        void startup();

        std::unique_ptr<CtiPointRegistrationMsg> buildRegistrationPointList() override;

    protected:
        CtiFDRClientServerConnectionSPtr createNewConnection(SOCKET newConnection) override;

        bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList) override;
        void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList) override;

        bool buildForeignSystemMessage(const CtiFDRDestination& destination,
                                       char** buffer,
                                       unsigned int& bufferSize) override;

        void logCommand(const std::string &description, const char *data, const unsigned size);

        int processMessageFromForeignSystem(Cti::Fdr::ServerConnection& connection,
                                           const char* data, unsigned int size) override;

        virtual bool isDnpDirectDeviceId(const long deviceId) const;  //  virtual for unit test override

        unsigned int getHeaderLength() override;

        virtual YukonError_t  writePorterConnection(CtiRequestMsg *msg, const Timing::Chrono duration);
        virtual std::unique_ptr<CtiReturnMsg> readPorterConnection(const Timing::Chrono duration);

        virtual LitePoint lookupPointById(long pointid);

        bool shouldIgnoreOldData() const override;

    private:
        DnpId ForeignToYukonId(const CtiFDRDestination &pointDestination);
        bool  YukonToForeignQuality(const int aQuality, const CtiTime lastTimeStamp, const CtiTime Now);

        struct ConnectionProtocol
        {
            ServerConnection& connection;
            Protocols::DnpSlaveProtocol& dnpSlave;
        };

        ConnectionProtocol getProtocolForConnection(ServerConnection& connection);

        int processScanSlaveRequest           (ConnectionProtocol cp);
        int processControlRequest             (ConnectionProtocol cp, const Protocols::DNP::ObjectBlock &control, const Protocols::DnpSlave::ControlAction);
        int processAnalogOutputRequest        (ConnectionProtocol cp, const Protocols::DNP::ObjectBlock &control, const Protocols::DnpSlave::ControlAction);
        int processDataLinkConfirmationRequest(ConnectionProtocol cp);
        int processDataLinkReset              (ConnectionProtocol cp);
        int processUnsupportedRequest         (ConnectionProtocol cp);
        int processUnsolicitedDisableRequest  (ConnectionProtocol cp);
        int processUnsolicitedEnableRequest   (ConnectionProtocol cp);
        int processWriteTimeRequest           (ConnectionProtocol cp);
        int processDelayMeasurementRequest    (ConnectionProtocol cp);

        int doComms(ConnectionProtocol cp, const std::string& messageType);

        auto tryPorterControl  (const Protocols::DnpSlave::control_request &control, const long pointId) -> Protocols::DNP::ControlStatus;
        bool tryDispatchControl(const Protocols::DnpSlave::control_request &control, const long pointId);

        auto tryPorterAnalogOutput  (const Protocols::DnpSlave::analog_output_request &analog, const long pointId, const double multiplier) -> Protocols::DNP::ControlStatus;
        bool tryDispatchAnalogOutput(const Protocols::DnpSlave::analog_output_request &analog, const long pointId, const double multiplier);

        auto waitForResponse(const long userMessageId) -> Protocols::DNP::ControlStatus;

        typedef std::map<CtiFDRDestination, DnpId> DnpDestinationMap;
        /** Map of DNP Send and Receive Translations */
        DnpDestinationMap _sendMap, _receiveMap;
        /** Locks to protect map of DNP Send and Receive Translations */
        CtiMutex          _sendMux, _receiveMux;

        std::mutex _connectionMux;
        std::map<int, Protocols::DnpSlaveProtocol> _dnpSlaves;

        CtiClientConnection _porterConnection;

        // maps ip address -> server name
        typedef std::map<std::string, std::string> ServerNameMap;
        ServerNameMap _serverNameLookup;
        int _staleDataTimeout;
        unsigned _porterTimeout;
        unsigned _porterPriority;
        RandomGenerator<long> _porterUserMsgIdGenerator;

        AttributeService _attributeService;
};

}
}

