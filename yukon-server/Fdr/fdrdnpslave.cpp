#include "precompiled.h"

#include "fdrdnpslave.h"

#include "prot_dnp.h"

#include "amq_constants.h"

#include "std_helper.h"

#include <boost/scoped_ptr.hpp>
#include <boost/algorithm/string/split.hpp>
#include <boost/algorithm/string/classification.hpp>

namespace {
boost::scoped_ptr<Cti::Fdr::DnpSlave> dnpSlaveInterface;
}

extern "C" {

DLLEXPORT int RunInterface(void)
{
    // make a point to the interface
    dnpSlaveInterface.reset(new Cti::Fdr::DnpSlave);
    dnpSlaveInterface->startup();
    // now start it up
    return dnpSlaveInterface->run();
}

DLLEXPORT int StopInterface( void )
{
    dnpSlaveInterface->stop();

    dnpSlaveInterface.reset();

    return 0;
}

}

namespace Cti {
namespace Fdr {

enum MiscDefines
{
    DNPSLAVE_PORTNUMBER       = 2085,
    FDR_DNP_REQ_FUNC_LOCATION = 12,
    FDR_DNP_HEADER_BYTE1 = 0x05,
    FDR_DNP_HEADER_BYTE2 = 0x64,
};

enum DnpApplicationFunctionCodes
{
    SINGLE_SOCKET_DNP_CONFIRM      = 0,
    SINGLE_SOCKET_DNP_READ         = 1,
    SINGLE_SOCKET_DNP_WRITE        = 2,
    SINGLE_SOCKET_DNP_DIRECT_OP    = 5,
    SINGLE_SOCKET_DNP_DATALINK_REQ = 100,
};


const std::string DNPInMessageString  = "DNP InMessage";
const std::string DNPOutMessageString = "DNP OutMessage";

using Protocols::DnpSlaveProtocol;
using namespace Protocols::DNP;

// Constructors, Destructor, and Operators
DnpSlave::DnpSlave() :
    CtiFDRSocketServer("DNPSLAVE"),
    _staleDataTimeOut(0),
    _porterConnection(Cti::Messaging::ActiveMQ::Queue::porter)
{
    _porterConnection.setName("FDR DNP Slave to Porter");
}

void DnpSlave::startup()
{
    init();

    _porterConnection.start();
    _porterConnection.WriteConnQue(
            new CtiRegistrationMsg("FDR (DNP Slave)", 0, true));
}

/*************************************************
* Function Name: DnpSlave::config()
*
* Description: loads cparm config values
*
**************************************************
*/
bool DnpSlave::readConfig()
{
    const char *KEY_LISTEN_PORT_NUMBER          = "FDR_DNPSLAVE_PORT_NUMBER";
    const char *KEY_DB_RELOAD_RATE              = "FDR_DNPSLAVE_DB_RELOAD_RATE";
    const char *KEY_DEBUG_MODE                  = "FDR_DNPSLAVE_DEBUG_MODE";
    const char *KEY_FDR_DNPSLAVE_SERVER_NAMES   = "FDR_DNPSLAVE_SERVER_NAMES";
    const char *KEY_LINK_TIMEOUT                = "FDR_DNPSLAVE_LINK_TIMEOUT_SECONDS";
    const char *KEY_STALEDATA_TIMEOUT           = "FDR_DNPSLAVE_STALEDATA_TIMEOUT";

    setPortNumber(gConfigParms.getValueAsInt( KEY_LISTEN_PORT_NUMBER, DNPSLAVE_PORTNUMBER));

    setReloadRate(gConfigParms.getValueAsInt(KEY_DB_RELOAD_RATE, 86400));

    setLinkTimeout(gConfigParms.getValueAsInt(KEY_LINK_TIMEOUT, 60));

    _staleDataTimeOut = gConfigParms.getValueAsInt(KEY_STALEDATA_TIMEOUT, 3600);

    setInterfaceDebugMode (gConfigParms.getValueAsString(KEY_DEBUG_MODE).length() > 0);

    const std::string serverNames = gConfigParms.getValueAsString(KEY_FDR_DNPSLAVE_SERVER_NAMES);

    typedef boost::iterator_range<std::string::const_iterator> substring_range;
    std::vector<substring_range> mappings;
    boost::algorithm::split(mappings, serverNames, boost::is_any_of(","));

    for each(const substring_range &mapping in mappings)
    {
        std::vector<std::string> name_address;
        boost::algorithm::split(name_address, mapping, boost::is_any_of("= "));

        if (name_address.size() >= 2)
        {
            const std::string &serverAddress = name_address[0],
                              &serverName    = name_address[1];

            _serverNameLookup[serverName] = serverAddress;

            if (getDebugLevel () & STARTUP_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, logNow() << "Added server mapping: "<< serverAddress <<" -> "<< serverName);
            }
        }
    }

    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        FormattedList loglist;

        loglist.add(KEY_LISTEN_PORT_NUMBER) << getPortNumber();
        loglist.add(KEY_DB_RELOAD_RATE)     << getReloadRate();
        loglist.add(KEY_LINK_TIMEOUT)       << getLinkTimeout();
        loglist.add(KEY_DEBUG_MODE)         << isInterfaceInDebugMode();

        CTILOG_INFO(dout, "FDRDnpSlave Configs"
                << loglist);
    }

    return true;
}

CtiFDRClientServerConnectionSPtr DnpSlave::createNewConnection(SOCKET newSocket)
{
    SocketAddress peerAddr( SocketAddress::STORAGE_SIZE );

    if( getpeername(newSocket, &peerAddr._addr.sa, &peerAddr._addrlen) == SOCKET_ERROR )
    {
        const DWORD error = WSAGetLastError();
        CTILOG_ERROR(dout, "getpeername() failed with error code: "<< error <<" / "<< getSystemErrorMessage(error));

        return CtiFDRClientServerConnectionSPtr();
    }

    const std::string ipString = peerAddr.toString();

    ServerNameMap::const_iterator iter = _serverNameLookup.find(ipString);

    const std::string connName = (iter == _serverNameLookup.end())? ipString : iter->second;

    CtiFDRClientServerConnectionSPtr newConnection(new CtiFDRClientServerConnection(connName.c_str(),newSocket,this));
    newConnection->setRegistered(true); //DNPSLAVE doesn't have a separate registration message

    // I'm not sure this is the best location for this
    sendAllPoints(newConnection);

    return newConnection;
}



bool DnpSlave::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool foundPoint = false;

    for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
    {
        foundPoint = true;
        CtiFDRDestination pointDestination = translationPoint->getDestinationList()[x];
        // translate and put the point id the list

        DnpId dnpId = ForeignToYukonId(pointDestination);
        if (!dnpId.valid)
        {
            return foundPoint;
        }

        if (sendList)
        {
            _sendMap[pointDestination] = dnpId;

            if( getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL )
            {
                CTILOG_DEBUG(dout, "Added send mapping "<< pointDestination <<" to " << dnpId);
            }
        }
    }
    return foundPoint;
}

void DnpSlave::cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)
{
    if (recvList)
    {
        return;
    }
    for each( const CtiFDRDestination &dest in translationPoint->getDestinationList() )
    {
        SendMap::iterator itr = _sendMap.find(dest);

        if ( itr != _sendMap.end() )
        {
            if( getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL )
            {
                CTILOG_DEBUG(dout, "Removing send mapping "<< itr->first <<" to "<< itr->second);
            }

            _sendMap.erase(itr);
        }
        else
        {
            CTILOG_WARN(dout, "No mapping found for " << dest);
        }
    }
}

bool DnpSlave::buildForeignSystemMessage(const CtiFDRDestination& destination,
                                               char** buffer,
                                               unsigned int& bufferSize)
{
    //  could be used for unsolicited reporting in future
    return false;
}

int DnpSlave::processMessageFromForeignSystem (ServerConnection& connection,
                                         const char* data, unsigned int size)
{
    unsigned long function = determineRequestFunction(data, size);

    switch (function)
    {
        case SINGLE_SOCKET_DNP_DATALINK_REQ:
        {
            processDataLinkConfirmationRequest (connection, data);
            break;
        }
        case SINGLE_SOCKET_DNP_READ:
        {
            processScanSlaveRequest (connection, data, size);
            break;
        }
        case SINGLE_SOCKET_DNP_DIRECT_OP:
        {
            //processControlRequest(connection, data, size);
            //break;
        }
        case SINGLE_SOCKET_DNP_CONFIRM:
        case SINGLE_SOCKET_DNP_WRITE:
        default:
        {
            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, logNow() << " received an unsupported DNP message, response not generated.");
            }
            break;
        }
    }
    return 0;
}


int DnpSlave::processDataLinkConfirmationRequest(ServerConnection& connection, const char* data)
{
    int retVal = 0;
    unsigned char* buffer = NULL;
    unsigned int bufferSize = getMessageSize(data);
    if (bufferSize == 10)
    {
        bool linkStatusReq = ( ((data[3] & 0x09) == 0x09 ) ? true : false);
        std::string linkMessage = ( linkStatusReq ? "data link status request" : "reset link" );

        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<" received DNP " << linkMessage <<" request message."<<
                    arrayToRange(reinterpret_cast<const unsigned char*>(data), bufferSize));
        }

        buffer = new UCHAR[bufferSize];

        std::memcpy(buffer, data, bufferSize);

        buffer[2] = 5;
        if (linkStatusReq)
        {
            buffer[3] = 0x0B;
        }
        else
        {
            buffer[3] = 0x00;
        }

        buffer[4] = data[6]; //swap source to destination
        buffer[5] = data[7];
        buffer[6] = data[4]; //swap destination to source
        buffer[7] = data[5];

        BYTEUSHORT crc;
        crc.sh = DatalinkLayer::crc((const unsigned char*) buffer, 8);
        buffer[8] = crc.ch[0];
        buffer[9] = crc.ch[1];

        connection.queueMessage((CHAR *)buffer, bufferSize, MAXPRIORITY - 1);
        //error processing data link confirmation Request
        linkMessage = ( linkStatusReq ? "data link acknowledgement" : "ack" );
        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<" sending DNP "<< linkMessage <<" message."<<
                    arrayToRange(reinterpret_cast<const unsigned char*>(buffer), bufferSize));
        }
    }
    else
    {
        //error processing data link confirmation Request
        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<" received an DNP data link confirmation request message, response not generated.");
        }
        retVal = -1;
    }
    return retVal;


}

int DnpSlave::processScanSlaveRequest (ServerConnection& connection, const char* data, unsigned int size)
{
    CtiXfer xfer = CtiXfer(NULL, 0, (BYTE*)data, getMessageSize(data));
    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<" received DNP scan request message"<<
                arrayToRange(reinterpret_cast<const unsigned char*>(data), size));
    }

    BYTEUSHORT dest, src;
    dest.ch[0] = data[4];
    dest.ch[1] = data[5];
    src.ch[0] = data[6];
    src.ch[1] = data[7];
    int seqnumber = (data[11] & 0x0F);

    for each( SendMap::value_type mapping in _sendMap )
    {
        const DnpId &dnpId = mapping.second;
        if (dnpId.SlaveId == dest.sh && dnpId.MasterId == src.sh )
        {
            const CtiFDRDestination &fdrdest = mapping.first;
            CtiFDRPoint* fdrPoint = fdrdest.getParentPoint();
            CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
            if (!findPointIdInList(fdrPoint->getPointID(),getSendToList(),*fdrPoint) )
                continue;

            DnpSlaveProtocol::input_point iPoint;

            iPoint.online = YukonToForeignQuality(fdrPoint->getQuality(), fdrPoint->getLastTimeStamp());
            iPoint.control_offset = dnpId.Offset;

            switch (dnpId.PointType)
            {
                case StatusPointType:
                {
                    iPoint.din.trip_close = (fdrPoint->getValue() == 0)?(BinaryOutputControl::Trip):(BinaryOutputControl::Close);
                    iPoint.type = DnpSlaveProtocol::DigitalInput;
                    break;
                }
                case AnalogPointType:
                {
                    iPoint.ain.value =  (fdrPoint->getValue() * dnpId.Multiplier);
                    iPoint.type = DnpSlaveProtocol::AnalogInputType;
                    break;
                }
                case PulseAccumulatorPointType:
                {
                    iPoint.counterin.value =  (fdrPoint->getValue() * dnpId.Multiplier);
                    iPoint.type = DnpSlaveProtocol::Counters;
                    break;
                }
                default:
                {
                    continue;
                }
            }

            _dnpSlave.addInputPoint(iPoint);
        }
    }

    _dnpSlave.setAddresses(src.sh, dest.sh);
    _dnpSlave.setSlaveCommand(DnpSlaveProtocol::Command_Class1230Read);
    _dnpSlave.setOptions(DnpSlaveProtocol::Options_SlaveResponse, seqnumber);

     while( !_dnpSlave.isTransactionComplete() )
     {
         if( _dnpSlave.slaveGenerate(xfer) == 0 )
         {
             if (xfer.getOutBuffer() != NULL && xfer.getOutCount() > 0)
             {
                 int bufferSize = xfer.getOutCount();
                 char* buffer = new CHAR[bufferSize];
                 std::memcpy(buffer, xfer.getOutBuffer(), bufferSize);
                 if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                 {
                     CTILOG_DEBUG(dout, logNow() <<" sending DNP scan response message."<<
                             arrayToRange(reinterpret_cast<const unsigned char*>(buffer), bufferSize));
                 }
                 connection.queueMessage(buffer,bufferSize, MAXPRIORITY - 1);
             }

             _dnpSlave.slaveDecode(xfer);
         }
         else
         {
             if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
             {
                 CTILOG_DEBUG(dout, logNow() <<" was not able to generate scan response.");
             }
         }
     }

    return 0;
}





DnpId DnpSlave::ForeignToYukonId(CtiFDRDestination pointDestination)
{
    DnpId dnpId;

    static const std::string dnpMasterId              = "MasterId";
    static const std::string dnpSlaveId               = "SlaveId";
    static const std::string dnpPointType             = "POINTTYPE";
    static const std::string dnpPointOffset           = "Offset";
    static const std::string dnpPointStatusString     = "Status";
    static const std::string dnpPointAnalogString     = "Analog";
    static const std::string dnpPointCalcAnalogString = "CalcAnalog";
    static const std::string dnpPointCounterString    = "PulseAccumulator";
    static const std::string dnpPointMultiplier       = "Multiplier";

    std::string masterId  = pointDestination.getTranslationValue(dnpMasterId);
    std::string slaveId   = pointDestination.getTranslationValue(dnpSlaveId);
    std::string pointType = pointDestination.getTranslationValue(dnpPointType);
    std::string dnpOffset = pointDestination.getTranslationValue(dnpPointOffset);
    std::string dnpMultiplier = pointDestination.getTranslationValue(dnpPointMultiplier);

    if (masterId.empty() || slaveId.empty() || pointType.empty() || dnpOffset.empty())
    {
        CTILOG_ERROR(dout, logNow() <<"Unable to add destination "<< pointDestination <<" because one of the fields was blank");

        dnpId.valid = false;
        return dnpId;
    }

    dnpId.MasterId = atoi(masterId.c_str());
    dnpId.SlaveId = atoi(slaveId.c_str());

    if (ciStringEqual(pointType, dnpPointStatusString))
    {
        dnpId.PointType = StatusPointType;
    }
    else if (ciStringEqual(pointType, dnpPointAnalogString) ||
             ciStringEqual(pointType, dnpPointCalcAnalogString))
    {
        dnpId.PointType = AnalogPointType;
    }
    else if (ciStringEqual(pointType, dnpPointCounterString))
    {
        dnpId.PointType = PulseAccumulatorPointType;
    }
    else
    {
        dnpId.PointType = InvalidPointType;
    }

    dnpId.Offset = atoi(dnpOffset.c_str());
    dnpId.MasterServerName = pointDestination.getDestination();
    if (dnpMultiplier.empty())
    {
        dnpId.Multiplier = 1;
    }
    else
    {
        dnpId.Multiplier = atof(dnpMultiplier.c_str());
    }
    dnpId.valid = true;

    return dnpId;
}

bool DnpSlave::YukonToForeignQuality(USHORT aQuality, CtiTime lastTimeStamp)
{

    bool goodQuality = false;
    CtiTime staleTime = CtiTime(CtiTime().seconds() - _staleDataTimeOut);

    if (aQuality == ManualQuality ||
        aQuality == NormalQuality)
    {
        if (lastTimeStamp >= staleTime)
        {
            goodQuality = true;
        }
    }
    return goodQuality;
}



unsigned long DnpSlave::determineRequestFunction(const char* data, unsigned int size)
{
    unsigned long retVal = -1;
    if (size >= FDR_DNP_REQ_FUNC_LOCATION)
    {
        if (data[0] == FDR_DNP_HEADER_BYTE1 &&
            data[1] == FDR_DNP_HEADER_BYTE2 )
        {
            long function = (BYTE)* (data + FDR_DNP_REQ_FUNC_LOCATION);
            retVal = function;
        }
    }
    else //size == FDR_DNP_HEADER_SIZE
    {

        //data link confirmation request
        retVal = SINGLE_SOCKET_DNP_DATALINK_REQ;
    }

    return retVal;

}

unsigned int DnpSlave::getMessageSize(const char* data)
{
    using namespace Protocols::DNP::DatalinkPacket;

    if ( ! data )
    {
        return 0;
    }

    const dlp_header &header = *reinterpret_cast<const dlp_header *>(data);

    return calcPacketLength(header.fmt.len);
}


unsigned int DnpSlave::getHeaderLength()
{
    return Protocols::DNP::DatalinkPacket::HeaderLength;
}


}
}


