#pragma once

#include "dlldefs.h"
#include "queues.h"
#include "fdrpointlist.h"
#include "fdrscadaserver.h"
#include "dnp_object_analoginput.h"
#include "prot_dnpSlave.h"
#include "string_util.h"

#include <boost/tuple/tuple_comparison.hpp>

#include <map>

namespace Cti {
namespace Fdr {

struct DnpId : public Cti::Loggable
{
    USHORT MasterId;
    USHORT SlaveId;
    UINT   PointType;
    USHORT Offset;
    FLOAT  Multiplier;
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
        // constructors and destructors
        DnpSlave();

        unsigned int getMessageSize(const char* data) override;

        bool readConfig() override;

        void startup();

    protected:
        CtiFDRClientServerConnectionSPtr createNewConnection(SOCKET newConnection) override;

        bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList) override;
        void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList) override;

        bool buildForeignSystemMessage(const CtiFDRDestination& destination,
                                       char** buffer,
                                       unsigned int& bufferSize) override;

        int processMessageFromForeignSystem(Cti::Fdr::ServerConnection& connection,
                                           const char* data, unsigned int size) override;

        virtual bool isDnpDeviceId(const long deviceId) const;

        unsigned int getHeaderLength() override;

    private:
        DnpId ForeignToYukonId(const CtiFDRDestination &pointDestination);
        bool  YukonToForeignQuality(const int aQuality, const CtiTime lastTimeStamp, const CtiTime Now);
        int processScanSlaveRequest           (ServerConnection &connection);
        int processControlRequest             (ServerConnection &connection, const Protocols::DNP::ObjectBlock &control);
        int processAnalogOutputRequest        (ServerConnection &connection, const Protocols::DNP::ObjectBlock &control);
        int processDataLinkConfirmationRequest(ServerConnection &connection);
        int processDataLinkReset              (ServerConnection &connection);

        auto tryPorterControl  (const Protocols::DnpSlave::control_request &control) -> Protocols::DNP::ControlStatus;
        bool tryDispatchControl(const Protocols::DnpSlave::control_request &control, const long pointId);

        auto tryPorterAnalogOutput  (const Protocols::DnpSlave::analog_output_request &analog) -> Protocols::DNP::ControlStatus;
        bool tryDispatchAnalogOutput(const Protocols::DnpSlave::analog_output_request &analog, const long pointId);

        std::string dumpDNPMessage(const char* data, unsigned int size);

        typedef std::map<CtiFDRDestination, DnpId> DnpDestinationMap;
        DnpDestinationMap _sendMap, _receiveMap;

        Protocols::DnpSlaveProtocol  _dnpSlave;

        CtiClientConnection _porterConnection;

        // maps ip address -> server name
        typedef std::map<std::string, std::string> ServerNameMap;
        ServerNameMap _serverNameLookup;
        int _staleDataTimeOut;
};

}
}

