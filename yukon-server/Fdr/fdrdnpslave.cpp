#include "precompiled.h"

#include "fdrdnpslave.h"

#include "prot_dnp.h"

#include "amq_constants.h"

#include "std_helper.h"

#include <boost/scoped_ptr.hpp>
#include <boost/algorithm/string.hpp>

using Cti::Fdr::DnpSlave;
using Cti::Logging::Vector::Hex::operator<<;
using Cti::Logging::Range::Hex::operator<<;

namespace {
std::unique_ptr<DnpSlave> dnpSlaveInterface;
}

extern "C" {

DLLEXPORT int RunInterface(void)
{
    // make a point to the interface
    dnpSlaveInterface = std::make_unique<DnpSlave>();
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

const std::string DNPInMessageString  = "DNP InMessage";
const std::string DNPOutMessageString = "DNP OutMessage";

using Cti::Protocols::DnpSlaveProtocol;
using namespace Cti::Protocols::DNP;

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

    const int DNPSLAVE_PORTNUMBER = 2085;

    setPortNumber(
            gConfigParms.getValueAsInt(KEY_LISTEN_PORT_NUMBER, DNPSLAVE_PORTNUMBER));

    setReloadRate(
            gConfigParms.getValueAsInt(KEY_DB_RELOAD_RATE, 86400));

    setLinkTimeout(
            gConfigParms.getValueAsInt(KEY_LINK_TIMEOUT, 60));

    _staleDataTimeOut =
            gConfigParms.getValueAsInt(KEY_STALEDATA_TIMEOUT, 3600);

    setInterfaceDebugMode(
            gConfigParms.getValueAsString(KEY_DEBUG_MODE).length() > 0);

    const std::string serverNames =
            gConfigParms.getValueAsString(KEY_FDR_DNPSLAVE_SERVER_NAMES);

    using substring_range = boost::iterator_range<std::string::const_iterator>;
    std::vector<substring_range> mappings;
    boost::algorithm::split(mappings, serverNames, boost::is_any_of(","));

    for( const auto &mapping : mappings)
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
    DnpDestinationMap &pointMap = sendList ? _sendMap : _receiveMap;

    CtiFDRPoint::DestinationList &destinations = translationPoint->getDestinationList();

    if ( destinations.empty() )
    {
        return false;
    }

    for( const auto &pointDestination : destinations )
    {
        // translate and put the point id the list

        DnpId dnpId = ForeignToYukonId(pointDestination);
        if (!dnpId.valid)
        {
            return true;
        }

        pointMap[pointDestination] = dnpId;

        if( getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL )
        {
            CTILOG_DEBUG(dout, "Added " << (sendList ? "send" : "receieve") << " mapping "<< pointDestination <<" to " << dnpId);
        }
    }
    return true;
}

void DnpSlave::cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)
{
    DnpDestinationMap &pointMap = recvList ? _receiveMap : _sendMap;

    for( const auto &dest : translationPoint->getDestinationList() )
    {
        DnpDestinationMap::iterator itr = pointMap.find(dest);

        if ( itr != pointMap.end() )
        {
            if( getDebugLevel () & MIN_DETAIL_FDR_DEBUGLEVEL )
            {
                CTILOG_DEBUG(dout, "Removing " << (recvList ? "receive" : "send") << " mapping "<< itr->first <<" to "<< itr->second);
            }

            pointMap.erase(itr);
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
    const auto requestType = _dnpSlave.identifyRequest(data, size);

    switch( requestType.first )
    {
        case DnpSlaveProtocol::Commands::LinkStatus:
        {
            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, logNow() <<" received DNP data link status request message"<<
                        arrayToRange(reinterpret_cast<const unsigned char*>(data), size));
            }
            return processDataLinkConfirmationRequest(connection);
        }
        case DnpSlaveProtocol::Commands::ResetLink:
        {
            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, logNow() <<" received DNP data link reset message"<<
                        arrayToRange(reinterpret_cast<const unsigned char*>(data), size));
            }
            return processDataLinkReset(connection);
        }
        case DnpSlaveProtocol::Commands::Class1230Read:
        {
            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, logNow() <<" received DNP scan request message"<<
                        arrayToRange(reinterpret_cast<const unsigned char*>(data), size));
            }
            return processScanSlaveRequest(connection);
        }
        case DnpSlaveProtocol::Commands::SetDigitalOut_Direct:
        {
            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, logNow() <<" received DNP control request message"<<
                        arrayToRange(reinterpret_cast<const unsigned char*>(data), size));
            }
            return processControlRequest(connection, *requestType.second);
        }
    }

    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() << " received an unsupported DNP message, response not generated."<<
                arrayToRange(reinterpret_cast<const unsigned char*>(data), size));
    }

    return -1;
}


int DnpSlave::processDataLinkConfirmationRequest(ServerConnection& connection)
{
    auto buf = _dnpSlave.createDatalinkConfirmation();

    char *bufForConnection = new char[buf.size()];

    std::copy(buf.begin(), buf.end(), bufForConnection);

    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<" sending DNP data link acknowledgement message." << buf);
    }

    connection.queueMessage(bufForConnection, buf.size(), MAXPRIORITY - 1);

    return 0;
}

int DnpSlave::processDataLinkReset(ServerConnection& connection)
{
    auto buf = _dnpSlave.createDatalinkAck();

    char *bufForConnection = new char[buf.size()];

    std::copy(buf.begin(), buf.end(), bufForConnection);

    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<" sending DNP ack message." << buf);
    }

    connection.queueMessage(bufForConnection, buf.size(), MAXPRIORITY - 1);


    return 0;
}

int DnpSlave::processScanSlaveRequest (ServerConnection& connection)
{
    const CtiTime Now;

    std::vector<DnpSlaveProtocol::input_point> inputPoints;

    for( const auto &kv : _sendMap )
    {
        const DnpId &dnpId = kv.second;
        if (dnpId.SlaveId  == _dnpSlave.getSrcAddr() &&
            dnpId.MasterId == _dnpSlave.getDstAddr() )
        {
            const CtiFDRDestination &fdrdest = kv.first;
            CtiFDRPoint* fdrPoint = fdrdest.getParentPoint();
            CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
            if (!findPointIdInList(fdrPoint->getPointID(),getSendToList(),*fdrPoint) )
                continue;

            DnpSlaveProtocol::input_point iPoint;

            iPoint.online = YukonToForeignQuality(fdrPoint->getQuality(), fdrPoint->getLastTimeStamp(), Now);
            iPoint.offset = dnpId.Offset - 1;  //  convert to DNP's 0-based indexing

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

            inputPoints.emplace_back(iPoint);
        }
    }

    _dnpSlave.setScanCommand(std::move(inputPoints));

    CtiXfer xfer;

    while( !_dnpSlave.isTransactionComplete() )
     {
         if( _dnpSlave.generate(xfer) == ClientErrors::None )
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

             _dnpSlave.decode(xfer);
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


int DnpSlave::processControlRequest (ServerConnection& connection, const ObjectBlock &control)
{
    if( control.getGroup()     != BinaryOutputControl::Group ||
        control.getVariation() != BinaryOutputControl::BOC_ControlRelayOutputBlock ||
        control.empty() )
    {
        return -1;
    }

    auto &ob = control[0];

    const auto boc = dynamic_cast<const BinaryOutputControl *>(ob.object);

    if( ! boc )
    {
        return -1;
    }

    auto status = BinaryOutputControl::Status_NotSupported;

    boost::optional<unsigned> controlState;

    switch( boc->getTripClose() )
    {
        case BinaryOutputControl::Trip:
        {
            controlState = 0;
            break;
        }

        case BinaryOutputControl::Close:
        {
            controlState = 1;
            break;
        }

        case BinaryOutputControl::NUL:
        {
            switch( boc->getControlCode() )
            {
                case BinaryOutputControl::PulseOn:
                case BinaryOutputControl::LatchOn:
                {
                    controlState = 1;
                    break;
                }
                case BinaryOutputControl::PulseOff:
                case BinaryOutputControl::LatchOff:
                {
                    controlState = 0;
                    break;
                }
            }
        }
    }

    if ( ! controlState)
    {
        status = BinaryOutputControl::Status_FormatError;
    }
    else
    {
        //  look for the point with the correct control offset
        for( const auto &kv : _receiveMap )
        {
            const DnpId &dnpId = kv.second;
            if( dnpId.PointType   == StatusPointType
                && dnpId.SlaveId  == _dnpSlave.getSrcAddr()
                && dnpId.MasterId == _dnpSlave.getDstAddr()
                && dnpId.Offset   == ob.index )
            {
                const CtiFDRDestination &fdrdest = kv.first;
                CtiFDRPoint* fdrPoint = fdrdest.getParentPoint();

                {
                    CtiLockGuard<CtiMutex> recvGuard(getReceiveFromList().getMutex());

                    if ( ! findPointIdInList(fdrPoint->getPointID(),getReceiveFromList(),*fdrPoint) )
                    {
                        continue;
                    }
                }

                if( fdrPoint->isControllable() )
                {
                    // build the command message and send the control
                    std::auto_ptr<CtiCommandMsg> cmdMsg(
                            new CtiCommandMsg(CtiCommandMsg::ControlRequest));

                    cmdMsg->insert(-1);     // This is the dispatch token and is unimplemented at this time
                    cmdMsg->insert(0);      // device id, unknown at this point, dispatch will find it
                    cmdMsg->insert(fdrPoint->getPointID());  // point for control
                    cmdMsg->insert(*controlState);

                    sendMessageToDispatch(cmdMsg.release());

                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        CTILOG_DEBUG(dout, logNow() << " Control " << (*controlState ? "close" : "open") << " sent to pointid " << fdrPoint->getPointID());
                    }

                    status = BinaryOutputControl::Status_Success;
                }
            }
        }
    }

    _dnpSlave.setControlCommand(control, status);

    CtiXfer xfer;

    //  reply with success
    while( !_dnpSlave.isTransactionComplete() )
    {
        if( _dnpSlave.generate(xfer) == ClientErrors::None )
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

            _dnpSlave.decode(xfer);
        }
        else if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<" was not able to generate control response.");
        }
    }

    return 0;
}


DnpId DnpSlave::ForeignToYukonId(const CtiFDRDestination &pointDestination)
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

    dnpId.MasterId  = std::stoi(masterId);
    dnpId.SlaveId   = std::stoi(slaveId);

    using boost::algorithm::to_lower_copy;

    static const std::map<std::string, CtiPointType_t> PointTypeNames {
        { boost::to_lower_copy(dnpPointStatusString),       StatusPointType },
        { boost::to_lower_copy(dnpPointAnalogString),       AnalogPointType },
        { boost::to_lower_copy(dnpPointCalcAnalogString),   AnalogPointType },
        { boost::to_lower_copy(dnpPointCounterString),      PulseAccumulatorPointType }
    };

    if( const auto type = mapFind(PointTypeNames, boost::to_lower_copy(pointType)) )
    {
        dnpId.PointType = *type;
    }
    else
    {
        dnpId.PointType = InvalidPointType;
    }

    dnpId.Offset = std::stoi(dnpOffset);
    dnpId.MasterServerName = pointDestination.getDestination();
    if (dnpMultiplier.empty())
    {
        dnpId.Multiplier = 1;
    }
    else
    {
        dnpId.Multiplier = std::stof(dnpMultiplier);
    }
    dnpId.valid = true;

    return dnpId;
}

bool DnpSlave::YukonToForeignQuality(const int aQuality, const CtiTime lastTimeStamp, const CtiTime Now)
{
    switch (aQuality)
    {
        case ManualQuality:
        case NormalQuality:
        {
            return lastTimeStamp >= (Now - _staleDataTimeOut);
        }
    }

    return false;
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


