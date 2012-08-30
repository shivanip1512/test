#pragma once

#include <map>

#include "dlldefs.h"
#include "queues.h"
#include "fdrpointlist.h"
#include "fdrscadaserver.h"
#include "fdrdnphelper.h"
#include "dnp_object_analoginput.h"
#include "prot_dnp.h"

// global defines
#define DNPSLAVE_PORTNUMBER      2085
#define FDR_DNP_HEADER_SIZE        10
#define FDR_DNP_REQ_FUNC_LOCATION  12
#define FDR_DNP_DATA_CRC_MARKER    16
#define FDR_DNP_HEADER_BYTE1     0x05
#define FDR_DNP_HEADER_BYTE2     0x64


#define SINGLE_SOCKET_DNP_CONFIRM      0
#define SINGLE_SOCKET_DNP_READ         1
#define SINGLE_SOCKET_DNP_WRITE        2
#define SINGLE_SOCKET_DNP_DIRECT_OP    5
#define SINGLE_SOCKET_DNP_DATALINK_REQ 100


struct IM_EX_FDRBASE CtiDnpId
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

};
inline std::ostream& operator<< (std::ostream& os, const CtiDnpId& id)
{
    return os << "[DNP: Master= "<< id.MasterServerName <<", M=" << id.MasterId << ", S="
        << id.SlaveId << ", P=" << id.PointType
        << ", O=" << id.Offset << "]";
}


class IM_EX_FDRDNPSLAVE CtiFDRDnpSlave : public CtiFDRSocketServer
{
    public:
        // helper structs
        CtiFDRDNPHelper<CtiDnpId>* _helper;


        // constructors and destructors
        CtiFDRDnpSlave();

        virtual ~CtiFDRDnpSlave();

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
        void dumpDNPMessage(const std::string direction, const char* data, unsigned int size);

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

    public:

};
