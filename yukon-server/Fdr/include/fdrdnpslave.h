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
        unsigned long getHeaderBytes(const char* data, unsigned int size) override;

        unsigned int getMagicInitialMsgSize() override;


    private:
        DnpId   ForeignToYukonId(CtiFDRDestination pointDestination);
        bool    YukonToForeignQuality (USHORT aQuality, CtiTime lastTimeStamp);
        int processScanSlaveRequest (Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size, bool includeTime);
        int processDataLinkConfirmationRequest(Cti::Fdr::ServerConnection& connection, const char* data);

        std::string dumpDNPMessage(const char* data, unsigned int size);

        typedef std::map<CtiFDRDestination, DnpId> SendMap;
        SendMap _sendMap;

        Protocols::DnpSlaveProtocol  _dnpSlave;

        CtiClientConnection _porterConnection;

        // maps ip address -> server name
        typedef std::map<std::string, std::string> ServerNameMap;
        ServerNameMap _serverNameLookup;
        int _staleDataTimeOut;
};

}
}

