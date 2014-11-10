#pragma once

#include <map>

#include "dlldefs.h"
#include "queues.h"
#include "fdrpointlist.h"
#include "fdrscadaserver.h"
#include "fdrdnphelper.h"
#include "dnp_object_analoginput.h"
#include "prot_dnp.h"
#include "string_util.h"


struct CtiDnpId : public Cti::Loggable
{
    USHORT MasterId;
    USHORT SlaveId;
    UINT   PointType;
    USHORT Offset;
    FLOAT  Multiplier;
    BOOL valid;

    CtiFDRClientServerConnection::Destination MasterServerName;
    bool operator<(const CtiDnpId& other) const
    {
        if( MasterServerName < other.MasterServerName )  return true;
        if( MasterServerName > other.MasterServerName )  return false;

        if( MasterId < other.MasterId )  return true;
        if( MasterId > other.MasterId )  return false;

        if( SlaveId < other.SlaveId )  return true;
        if( SlaveId > other.SlaveId )  return false;

        if( PointType < other.PointType )  return true;
        if( PointType > other.PointType )  return false;

        return Offset < other.Offset;
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

namespace Cti {
namespace Fdr {

class IM_EX_FDRDNPSLAVE DnpSlave : public CtiFDRSocketServer
{
    public:
        // helper structs
        CtiFDRDNPHelper<CtiDnpId>* _helper;


        // constructors and destructors
        DnpSlave();

        virtual ~DnpSlave();

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
        CtiDnpId    ForeignToYukonId(CtiFDRDestination pointDestination);
        bool        YukonToForeignQuality (USHORT aQuality, CtiTime lastTimeStamp);
        int processScanSlaveRequest (Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size, bool includeTime);
        int processDataLinkConfirmationRequest(Cti::Fdr::ServerConnection& connection, const char* data);

        bool isScanIntegrityRequest(const char* data, unsigned int size);
        std::string dumpDNPMessage(const std::string direction, const char* data, unsigned int size);

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

