#pragma once

#include "dlldefs.h"
#include "queues.h"
#include "fdrpointlist.h"
#include "fdrscadaserver.h"
#include "dnp_object_analoginput.h"
#include "prot_dnp.h"
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

        virtual unsigned int getMessageSize(const char* data);

        virtual int readConfig( void );

        void startup();

    protected:
        virtual CtiFDRClientServerConnectionSPtr createNewConnection(SOCKET newConnection);

        virtual bool translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList);
        virtual void cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList);

        virtual bool buildForeignSystemMessage(const CtiFDRDestination& destination,
                                               char** buffer,
                                               unsigned int& bufferSize);

        virtual int processMessageFromForeignSystem(Cti::Fdr::ServerConnection& connection,
                                           const char* data, unsigned int size);
        virtual unsigned long getHeaderBytes(const char* data, unsigned int size);

        virtual unsigned int getMagicInitialMsgSize();


    private:
        DnpId   ForeignToYukonId(CtiFDRDestination pointDestination);
        bool    YukonToForeignQuality (USHORT aQuality, CtiTime lastTimeStamp);
        int processScanSlaveRequest (Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size, bool includeTime);
        int processDataLinkConfirmationRequest(Cti::Fdr::ServerConnection& connection, const char* data);

        bool isScanIntegrityRequest(const char* data, unsigned int size);
        std::string dumpDNPMessage(const std::string direction, const char* data, unsigned int size);

        typedef std::map<CtiFDRDestination, DnpId> SendMap;
        SendMap _sendMap;

        Cti::Protocol::DNPSlaveInterface  _dnpData;

        // maps ip address -> server name
        typedef std::map<std::string, std::string> ServerNameMap;
        ServerNameMap _serverNameLookup;
        int _staleDataTimeOut;

        static const CHAR * KEY_LISTEN_PORT_NUMBER;
        static const CHAR * KEY_DB_RELOAD_RATE;
        static const CHAR * KEY_DEBUG_MODE;
        static const CHAR * KEY_FDR_DNPSLAVE_SERVER_NAMES;
        static const CHAR * KEY_LINK_TIMEOUT;
        static const CHAR * KEY_STALEDATA_TIMEOUT;

        static const std::string dnpMasterId;
        static const std::string dnpSlaveId;
        static const std::string dnpPointType;
        static const std::string dnpPointOffset;
        static const std::string dnpPointStatusString;
        static const std::string dnpPointAnalogString;
        static const std::string dnpPointCalcAnalogString;
        static const std::string dnpPointCounterString;
        static const std::string dnpPointMultiplier;

        static const std::string CtiFdrDNPInMessageString;
        static const std::string CtiFdrDNPOutMessageString;
};

}
}

