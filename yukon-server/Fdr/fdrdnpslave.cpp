#include "precompiled.h"

#include "fdrdnpslave.h"

#include "prot_dnp.h"
#include "dnp_object_analogoutput.h"

#include "amq_constants.h"

#include "AttributeService.h"

#include "resolvers.h"
#include "desolvers.h"
#include "slctdev.h"
#include "msg_pcrequest.h"
#include "msg_pcreturn.h"

#include "millisecond_timer.h"

#include "std_helper.h"

#include <boost/scoped_ptr.hpp>
#include <boost/algorithm/string.hpp>

#include <regex>
#include <memory>

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
    _staleDataTimeout(0),
    _porterTimeout(30),
    _porterPriority(14),
    _porterConnection(Cti::Messaging::ActiveMQ::Queue::porter)
{
    _porterConnection.setName("FDR DNP Slave to Porter");
}

DnpSlave::~DnpSlave() = default;

void DnpSlave::startup()
{
    init();

    _porterConnection.start();
    _porterConnection.WriteConnQue(
        new CtiRegistrationMsg("FDR (DNP Slave)", 0, true), CALLSITE);
}

std::unique_ptr<CtiPointRegistrationMsg> DnpSlave::buildRegistrationPointList()
{
    auto ptRegMsg = std::make_unique<CtiPointRegistrationMsg>(REG_TAG_UPLOAD);

    // do outbounds
    {
        auto & sendToMap = getSendToList().getPointList()->getMap();

        for( auto kv : sendToMap )
        {
            // add this point ID to register
            ptRegMsg->insert( kv.first );
        }
    }

    // do inbounds
    {
        auto & receiveFromMap = getReceiveFromList().getPointList()->getMap();

        for ( auto kv : receiveFromMap)
        {
            // add this point ID to register
            ptRegMsg->insert( kv.first );
        }
    }

    return std::move( ptRegMsg );
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
    const char *KEY_FDR_DNPSLAVE_SERVER_NAMES   = "FDR_DNPSLAVE_SERVER_NAMES";
    const char *KEY_LINK_TIMEOUT                = "FDR_DNPSLAVE_LINK_TIMEOUT_SECONDS";
    const char *KEY_STALEDATA_TIMEOUT           = "FDR_DNPSLAVE_STALEDATA_TIMEOUT";
    const char *KEY_PORTER_TIMEOUT              = "FDR_DNPSLAVE_PORTER_TIMEOUT";
    const char *KEY_PORTER_PRIORITY             = "FDR_DNPSLAVE_PORTER_PRIORITY";

    const int DNPSLAVE_PORTNUMBER = 2085;

    // load up the base class
    CtiFDRSocketServer::readConfig();

    setPortNumber(
            gConfigParms.getValueAsInt(KEY_LISTEN_PORT_NUMBER, DNPSLAVE_PORTNUMBER));

    setReloadRate(
            gConfigParms.getValueAsInt(KEY_DB_RELOAD_RATE, 86400));

    setLinkTimeout(
            gConfigParms.getValueAsInt(KEY_LINK_TIMEOUT, 60));

    _staleDataTimeout =
            gConfigParms.getValueAsInt(KEY_STALEDATA_TIMEOUT, 3600);

    _porterTimeout =
            gConfigParms.getValueAsInt(KEY_PORTER_TIMEOUT, 30);

    _porterPriority =
            gConfigParms.getValueAsInt(KEY_PORTER_PRIORITY, 14);

    const std::string serverNames =
            gConfigParms.getValueAsString(KEY_FDR_DNPSLAVE_SERVER_NAMES);

    using substring_range = boost::iterator_range<std::string::const_iterator>;
    std::vector<substring_range> mappings;
    boost::algorithm::split(mappings, serverNames, is_char{','});

    for( const auto &mapping : mappings)
    {
        std::vector<std::string> name_address;
        boost::algorithm::split(name_address, mapping, is_chars{'=',' '});

        if (name_address.size() >= 2)
        {
            const std::string &serverAddress = name_address[0],
                              &serverName    = name_address[1];

            _serverNameLookup[serverAddress] = serverName;

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

    const std::string ipString = peerAddr.getIpAddress();

    const std::string connName = Cti::mapFindOrDefault(_serverNameLookup, ipString, ipString);

    CtiFDRClientServerConnectionSPtr newConnection(new CtiFDRClientServerConnection(connName.c_str(),newSocket,this));
    newConnection->setRegistered(true); //DNPSLAVE doesn't have a separate registration message

    return newConnection;
}


bool DnpSlave::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    // Protect pointMap while we use it.
    CTILOCKGUARD( CtiMutex, guard, sendList ? _sendMux : _receiveMux );
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
            CTILOG_DEBUG(dout, "Added " << (sendList ? "send" : "receive") << " mapping "<< pointDestination <<" to " << dnpId);
        }
    }
    return true;
}

void DnpSlave::cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)
{
    // Protect pointMap while we use it.
    CTILOCKGUARD( CtiMutex, guard, recvList ? _receiveMux : _sendMux );
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


void DnpSlave::logCommand(const std::string &description, const char *data, const unsigned size)
{
    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<" received " << description << std::endl <<
                     arrayToRange(reinterpret_cast<const unsigned char*>(data), size));
    }
}



int DnpSlave::processMessageFromForeignSystem (ServerConnection& connection,
                                         const char* data, unsigned int size)
{
    auto cp = getProtocolForConnection(connection);

    const auto requestType = cp.dnpSlave.identifyRequest(data, size);

    using Cmd = DnpSlaveProtocol::Commands;
    using Act = Protocols::DnpSlave::ControlAction;

    const std::map<Cmd, std::pair<std::string, std::function<int ()>>> commandFunctions
    {
        { Cmd::Unsupported,
            { "an unsupported DNP request",
                [&] { return processUnsupportedRequest(cp); }}},

        { Cmd::DelayMeasurement,
            { "a DNP delay measurement request",
                [&] { return processDelayMeasurementRequest(cp); }}},

        { Cmd::WriteTime,
            { "a DNP write time request",
                [&] { return processWriteTimeRequest(cp); } } },

        { Cmd::LinkStatus,
            { "a DNP data link status request",
                [&] { return processDataLinkConfirmationRequest(cp); }}},

        { Cmd::UnsolicitedEnable,
            { "a DNP unsolicited enable request",
                [&] { return processUnsolicitedEnableRequest(cp); }}},

        { Cmd::UnsolicitedDisable,
            { "a DNP unsolicited disable request",
                [&] { return processUnsolicitedDisableRequest(cp); }}},

        { Cmd::ResetLink,
            { "a DNP data link reset",
                [&] { return processDataLinkReset(cp); }}},

        { Cmd::Class1230Read,
            { "a DNP scan request",
                [&] { return processScanSlaveRequest(cp); }}},

        { Cmd::SetDigitalOut_Select,
            { "a DNP control select request",
                [&] { return processControlRequest(cp, *requestType.second, Act::Select); }}},

        { Cmd::SetDigitalOut_Operate,
            { "a DNP control operate request",
                [&] { return processControlRequest(cp, *requestType.second, Act::Operate); }}},

        { Cmd::SetDigitalOut_Direct,
            { "a DNP direct control request",
                [&] { return processControlRequest(cp, *requestType.second, Act::Direct); }}},

        { Cmd::SetAnalogOut_Select,
            { "a DNP analog output select request",
                [&] { return processAnalogOutputRequest(cp, *requestType.second, Act::Select); }}},

        { Cmd::SetAnalogOut_Operate,
            { "a DNP analog output operate request",
                [&] { return processAnalogOutputRequest(cp, *requestType.second, Act::Operate); }}},

        { Cmd::SetAnalogOut_Direct,
            { "a DNP direct analog output request",
                [&] { return processAnalogOutputRequest(cp, *requestType.second, Act::Direct); }}}};

    if( const auto &descFunc = mapFind(commandFunctions, requestType.first) )
    {
        const auto &description      = descFunc->first;
        const auto &commandProcessor = descFunc->second;

        logCommand(description, data, size);

        return commandProcessor();
    }

    logCommand("an unsupported DNP message, response not generated.", data, size);

    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() << " request received: " << std::endl <<
                arrayToRange(reinterpret_cast<const unsigned char*>(data), size));
    }

    return -1;
}


int DnpSlave::processDataLinkConfirmationRequest(ConnectionProtocol cp)
{
    auto buf = cp.dnpSlave.createDatalinkConfirmation();

    char *bufForConnection = new char[buf.size()];

    std::copy(buf.begin(), buf.end(), 
            stdext::make_checked_array_iterator(bufForConnection, buf.size()));

    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<" sending DNP data link acknowledgement message." << buf);
    }

    cp.connection.queueMessage(bufForConnection, buf.size(), MAXPRIORITY - 1);

    return 0;
}

int DnpSlave::processDataLinkReset(ConnectionProtocol cp)
{
    auto buf = cp.dnpSlave.createDatalinkAck();

    char *bufForConnection = new char[buf.size()];

    std::copy(buf.begin(), buf.end(), 
            stdext::make_checked_array_iterator(bufForConnection, buf.size()));

    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<" sending DNP ack message." << buf);
    }

    cp.connection.queueMessage(bufForConnection, buf.size(), MAXPRIORITY - 1);

    return 0;
}

int DnpSlave::processUnsupportedRequest(ConnectionProtocol cp)
{
    cp.dnpSlave.setUnsupportedCommand();

    return doComms(cp, "unsupported");
}

int DnpSlave::processUnsolicitedDisableRequest(ConnectionProtocol cp)
{
    cp.dnpSlave.setUnsolicitedDisableCommand();

    return doComms(cp, "unsolicited disable");
}

int DnpSlave::processUnsolicitedEnableRequest(ConnectionProtocol cp)
{
    cp.dnpSlave.setUnsolicitedEnableCommand();

    return doComms(cp, "unsolicited enable");
}


int DnpSlave::processDelayMeasurementRequest(ConnectionProtocol cp)
{
    cp.dnpSlave.setDelayMeasurementCommand(std::chrono::milliseconds::zero());

    return doComms(cp, "delay measurement");
}


int DnpSlave::processWriteTimeRequest(ConnectionProtocol cp)
{
    cp.dnpSlave.setWriteTimeCommand();

    return doComms(cp, "write time");
}


int DnpSlave::doComms(ConnectionProtocol cp, const std::string& messageType)
{
    CtiXfer xfer;

    //  reply with success
    while( ! cp.dnpSlave.isTransactionComplete() )
    {
        if( cp.dnpSlave.generate(xfer) == ClientErrors::None )
        {
            if (xfer.getOutBuffer() != NULL && xfer.getOutCount() > 0)
            {
                int bufferSize = xfer.getOutCount();
                char* buffer = new CHAR[bufferSize];
                std::memcpy(buffer, xfer.getOutBuffer(), bufferSize);
                if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, logNow() << " sending DNP " << messageType << " response." << std::endl <<
                        arrayToRange(reinterpret_cast<const unsigned char*>(buffer), bufferSize));
                }
                cp.connection.queueMessage(buffer,bufferSize, MAXPRIORITY - 1);
            }

            cp.dnpSlave.decode(xfer);
        }
        else if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<" was not able to generate " << messageType << " response.");
        }
    }

    return 0;
}

int DnpSlave::processScanSlaveRequest (ConnectionProtocol cp)
{
    const CtiTime Now;

    std::vector<Protocols::DnpSlave::output_point> outputPoints;

    using Protocols::DnpSlave::PointType;

    auto emplacePoint = 
        [&](const DnpId dnpId, const CtiFDRPoint fdrPoint, const PointType dnpType)
        {
            const auto online = YukonToForeignQuality(fdrPoint.getQuality(), fdrPoint.getLastTimeStamp(), Now);
            const auto offset = dnpId.Offset - 1;  //  convert to DNP's 0-based indexing
            const auto value  = dnpId.PointType == StatusPointType 
                ? fdrPoint.getValue() 
                : fdrPoint.getValue() * dnpId.Multiplier;

            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, logNow() << " sending " << desolvePointType(dnpId.PointType) << " for " << dnpId.toString() << ", Value=" << value);
            }
            
            outputPoints.emplace_back(offset, online, dnpType, value);
        };

    {
        // This guard must happen before the _sendMux or a deadlock can occur.
        CTILOCKGUARD( CtiMutex, sendGuard, getSendToList().getMutex() );
        // Protect _sendMap while we use it.
        CTILOCKGUARD( CtiMutex, guard, _sendMux );

        for( const auto &kv : _sendMap )
        {
            const DnpId &dnpId = kv.second;
            if( dnpId.SlaveId == cp.dnpSlave.getSrcAddr() &&
                dnpId.MasterId == cp.dnpSlave.getDstAddr() )
            {
                const CtiFDRDestination &fdrdest = kv.first;
                long fdrPointId = fdrdest.getParentPointId();
                CtiFDRPoint fdrPoint;
                if( findPointIdInList( fdrPointId, getSendToList(), fdrPoint ) )
                {
                    switch( dnpId.PointType )
                    {
                    case StatusPointType:             emplacePoint(dnpId, fdrPoint, PointType::BinaryInput);  break;
                    case AnalogPointType:             emplacePoint(dnpId, fdrPoint, PointType::AnalogInput);  break;
                    case PulseAccumulatorPointType:   emplacePoint(dnpId, fdrPoint, PointType::Accumulator);  break;
                    case DemandAccumulatorPointType:  emplacePoint(dnpId, fdrPoint, PointType::DemandAccumulator);  break;
                    default:
                        CTILOG_WARN(dout, logNow() << " Unsupported point type " << dnpId.PointType);
                    }
                }
            }
        }
    }

    {
        // This guard must happen before the _receiveMux or a deadlock can occur.
        CTILOCKGUARD(CtiMutex, recvGuard, getReceiveFromList().getMutex());
        // Protect _receiveMap while we use it.
        CTILOCKGUARD(CtiMutex, guard, _receiveMux);

        for( const auto &kv : _receiveMap )
        {
            const DnpId &dnpId = kv.second;
            if( dnpId.SlaveId == cp.dnpSlave.getSrcAddr() &&
                dnpId.MasterId == cp.dnpSlave.getDstAddr() )
            {
                const CtiFDRDestination &fdrdest = kv.first;
                long fdrPointId = fdrdest.getParentPointId();
                CtiFDRPoint fdrPoint;
                if( findPointIdInList( fdrPointId, getReceiveFromList(), fdrPoint ) )
                {
                    switch( dnpId.PointType )
                    {
                    case StatusPointType:  emplacePoint(dnpId, fdrPoint, PointType::BinaryOutput);  break;
                    case AnalogPointType:  emplacePoint(dnpId, fdrPoint, PointType::AnalogOutput);  break;
                    default:
                        CTILOG_WARN(dout, logNow() << " Unsupported point type " << dnpId.PointType);
                    }
                }
            }
        }
    }

    cp.dnpSlave.setScanCommand(std::move(outputPoints));

    return doComms(cp, "scan");
}


bool DnpSlave::isDnpDirectDeviceId(const long deviceId) const
{
    const std::string sql = "select type from yukonpaobject where paobjectid=?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    rdr << deviceId;

    rdr.execute();

    if( rdr() )
    {
        std::string paoTypeStr;

        rdr[0] >> paoTypeStr;

        auto deviceType = resolveDeviceType(paoTypeStr);

        return isDnpDeviceType(deviceType) || deviceType == TYPE_CBCLOGICAL;
    }

    return false;
}


int DnpSlave::processControlRequest (ConnectionProtocol cp, const ObjectBlock &ob, const Protocols::DnpSlave::ControlAction action)
{
    if( ob.getGroup()     != BinaryOutputControl::Group ||
        ob.getVariation() != BinaryOutputControl::BOC_ControlRelayOutputBlock ||
        ob.empty() )
    {
        return -1;
    }

    const auto &objectDescriptor = ob[0];

    const auto boc = dynamic_cast<const BinaryOutputControl *>(objectDescriptor.object);

    if( ! boc )
    {
        return -1;
    }

    Protocols::DnpSlave::control_request control;

    control.offset     = objectDescriptor.index;
    control.control    = boc->getControlCode();
    control.queue      = boc->getQueue();
    control.clear      = boc->getClear();
    control.trip_close = boc->getTripClose();
    control.count      = boc->getCount();
    control.on_time    = boc->getOnTime();
    control.off_time   = boc->getOffTime();
    control.status     = ControlStatus::NotSupported;  //  actually need to parse and preserve the passed status...  confirm it's Normal before sending control
    control.action     = action;
    control.isLongIndexed =
        (ob.getIndexLength()    == 2 &&
         ob.getQuantityLength() == 2);

    //  look for the point with the correct control offset
    {
        // This guard must happen before the _receiveMux or a deadlock can occur.
        CTILOCKGUARD( CtiMutex, recvGuard, getReceiveFromList().getMutex() );
        // Protect pointMap while we use it.
        CTILOCKGUARD( CtiMutex, guard, _receiveMux );

        for( const auto &kv : _receiveMap )
        {
            const DnpId &dnpId = kv.second;
            if( dnpId.PointType == StatusPointType
                && dnpId.SlaveId == cp.dnpSlave.getSrcAddr()
                && dnpId.MasterId == cp.dnpSlave.getDstAddr()
                && dnpId.Offset == ( control.offset + 1 ) )  //  DnpId offsets are 1-based (Yukon is 1-based, DNP is 0-based)
            {
                const CtiFDRDestination &fdrdest = kv.first;
                long fdrPointId = fdrdest.getParentPointId();
                CtiFDRPoint fdrPoint;
                if( !findPointIdInList( fdrPointId, getReceiveFromList(), fdrPoint ) )
                {
                    continue;
                }

                if( fdrPoint.isControllable() )
                {
                    if( isDnpDirectDeviceId( fdrPoint.getPaoID() ) )
                    {
                        control.status = tryPorterControl( control, fdrPoint.getPointID() );
                    }
                    else if( tryDispatchControl( control, fdrPoint.getPointID() ) )
                    {
                        control.status = ControlStatus::Success;
                    }
                    else
                    {
                        control.status = ControlStatus::FormatError;
                    }

                    //  only send the control to the first point found
                    break;
                }
                else
                {
                    auto statusValue = [=]{
                        switch( control.trip_close )
                        {
                            case BinaryOutputControl::TripClose::Trip:  return 0;
                            case BinaryOutputControl::TripClose::Close: return 1;
                        }
                        //  If it's TripClose::NUL, rely on latch/pulse ON to send a 1 or 0
                        switch( control.control )
                        {
                            case BinaryOutputControl::ControlCode::LatchOn:
                            case BinaryOutputControl::ControlCode::PulseOn:
                                return 1;
                            default:
                                return 0;
                        }
                    }();

                    auto pData = 
                        std::make_unique<CtiPointDataMsg>(
                            fdrPoint.getPointID(),
                            statusValue,
                            NormalQuality,
                            fdrPoint.getPointType());

                    // consumes a delete memory
                    queueMessageToDispatch(pData.release());

                    control.status = ControlStatus::Success;

                    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                    {
                        FormattedList l;

                        l.add("Point ID") << fdrPoint.getPointID();
                        l.add("Pao ID") << fdrPoint.getPaoID();
                        l.add("Value") << statusValue;

                        CTILOG_DEBUG(dout, "Sending status point update to Dispatch:" << l);
                    }
                }
            }
        }
    }

    cp.dnpSlave.setControlCommand(control);

    return doComms(cp, "control");
}


const char *log(Protocols::DnpSlave::ControlAction c)
{
    using ControlAction = Protocols::DnpSlave::ControlAction;

    switch( c )
    {
        case ControlAction::Direct:   return "Direct";
        case ControlAction::Operate:  return "Operate";
        case ControlAction::Select:   return "Select";
        default:                      return "Invalid";
    }
}


std::string logPoints(const Protocols::DnpSlave::control_request &control, const LitePoint &point)
{
    FormattedList l;

    l.add("DNP request");
    l.add("Action")         << log(control.action);
    l.add("Clear")          << control.clear;
    l.add("Control")        << control.control;
    l.add("Count")          << control.count;
    l.add("Long index")     << control.isLongIndexed;
    l.add("Off time (ms)")  << control.off_time;
    l.add("Offset")         << control.offset;
    l.add("On time (ms)")   << control.on_time;
    l.add("Queue")          << control.queue;
    l.add("Status")         << Protocols::DnpProtocol::getControlResultString(static_cast<unsigned char>(control.status));
    l.add("Trip/close")     << control.trip_close;

    l.add("Yukon point");
    l.add("Close time 1")   << point.getCloseTime1();
    l.add("Close time 2")   << point.getCloseTime2();
    l.add("Control offset") << point.getControlOffset();
    l.add("Control type")   << desolveControlType(point.getControlType());
    l.add("Pao id")         << point.getPaoId();
    l.add("Point id")       << point.getPointId();
    l.add("Point name")     << point.getPointName();
    l.add("Point offset")   << point.getPointOffset();
    l.add("Point type")     << point.getPointType();
    l.add("State one control")  << point.getStateOneControl();
    l.add("State zero control") << point.getStateZeroControl();

    return l.toString();
}


ControlStatus DnpSlave::tryPorterControl(const Protocols::DnpSlave::control_request &control, const long pointId)
{
    auto point = lookupPointById(pointId);

    if( point.getPointId() != pointId )
    {
        CTILOG_WARN(dout, logNow() <<" could not load DNP pointid "<< pointId);

        return ControlStatus::NotSupported;
    }

    //  Confirm SBO vs direct
    switch( control.action )
    {
        case Protocols::DnpSlave::ControlAction::Select:
        case Protocols::DnpSlave::ControlAction::Operate:
        {
            switch( point.getControlType() )
            {
                case ControlType_SBOPulse:
                case ControlType_SBOLatch:
                    break;

                default:
                {
                    CTILOG_WARN(dout, logNow() <<" control type/action mismatch" << logPoints(control, point));
                    return ControlStatus::NotSupported;
                }
            }
            break;
        }
        case Protocols::DnpSlave::ControlAction::Direct:
        {
            switch( point.getControlType() )
            {
                case ControlType_Normal:
                case ControlType_Latch:
                    break;

                default:
                {
                    CTILOG_WARN(dout, logNow() <<" control type/action mismatch" << logPoints(control, point));
                    return ControlStatus::NotSupported;
                }
            }
            break;
        }
        default:
        {
            CTILOG_WARN(dout, logNow() <<" unsupported action" << logPoints(control, point));
            return ControlStatus::NotSupported;
        }
    }

    //  Confirm queue, clear, count all match the hardcoded values in dev_dnp
    if( control.queue || control.clear || control.count != 1 )
    {
        CTILOG_WARN(dout, logNow() <<" unsupported queue/clear/count parameters" << logPoints(control, point));
        return ControlStatus::NotSupported;
    }

    std::string commandString;

    switch( point.getControlType() )
    {
        case ControlType_Normal:
        case ControlType_SBOPulse:
        {
            if( control.control != BinaryOutputControl::PulseOn )
            {
                CTILOG_WARN(dout, logNow() <<" Incorrect control type" << logPoints(control, point));
                return ControlStatus::FormatError;
            }

            switch( control.trip_close )
            {
                case BinaryOutputControl::Trip:
                {
                    commandString = point.getStateZeroControl();

                    if( ! icontainsString(commandString, " open") )
                    {
                        CTILOG_WARN(dout, logNow() <<" State zero control string is not an OPEN" << logPoints(control, point));
                        return ControlStatus::FormatError;
                    }
                    if( point.getCloseTime1() != control.on_time )
                    {
                        CTILOG_WARN(dout, logNow() <<" On time mismatch" << logPoints(control, point));
                        return ControlStatus::FormatError;
                    }
                    if( control.off_time )
                    {
                        CTILOG_WARN(dout, logNow() <<" Off time not supported, must be zero" << logPoints(control, point));
                        return ControlStatus::FormatError;
                    }
                    break;
                }
                case BinaryOutputControl::Close:
                {
                    commandString = point.getStateOneControl();

                    if( ! icontainsString(commandString, " close") )
                    {
                        CTILOG_WARN(dout, logNow() <<" State one control string is not a CLOSE" << logPoints(control, point));
                        return ControlStatus::FormatError;
                    }
                    if( point.getCloseTime2() != control.on_time )
                    {
                        CTILOG_WARN(dout, logNow() <<" On time mismatch" << logPoints(control, point));
                        return ControlStatus::FormatError;
                    }
                    if( control.off_time )
                    {
                        CTILOG_WARN(dout, logNow() <<" Off time not supported, must be zero" << logPoints(control, point));
                        return ControlStatus::FormatError;
                    }
                    break;
                }
                case BinaryOutputControl::NUL:
                {
                    commandString = point.getStateOneControl();  //  always send state one control

                    if( ! icontainsString(commandString, " close") )
                    {
                        CTILOG_WARN(dout, logNow() <<" State one control string is not a CLOSE" << logPoints(control, point));
                        return ControlStatus::FormatError;
                    }
                    if( ! icontainsString(commandString, " direct") )
                    {
                        CTILOG_WARN(dout, logNow() <<" State one control string does not contain DIRECT" << logPoints(control, point));
                        return ControlStatus::FormatError;
                    }
                    if( point.getCloseTime2() != control.on_time )
                    {
                        CTILOG_WARN(dout, logNow() <<" On time mismatch" << logPoints(control, point));
                        return ControlStatus::FormatError;
                    }
                    if( control.off_time )
                    {
                        CTILOG_WARN(dout, logNow() <<" Off time not supported, must be zero" << logPoints(control, point));
                        return ControlStatus::FormatError;
                    }
                    break;
                }
            }
            break;
        }
        case ControlType_Latch:
        case ControlType_SBOLatch:
        {
            switch( control.control )
            {
                case BinaryOutputControl::LatchOff:
                {
                    commandString = point.getStateZeroControl();

                    if( ! icontainsString(commandString, " open") )
                    {
                        CTILOG_WARN(dout, logNow() <<" State zero control string is not an OPEN" << logPoints(control, point));
                        return ControlStatus::FormatError;
                    }
                    break;
                }
                case BinaryOutputControl::LatchOn:
                {
                    commandString = point.getStateOneControl();

                    if( ! icontainsString(commandString, " close") )
                    {
                        CTILOG_WARN(dout, logNow() <<" State one control string is not a CLOSE" << logPoints(control, point));
                        return ControlStatus::FormatError;
                    }
                    break;
                }
                default:
                {
                    CTILOG_WARN(dout, logNow() <<" incorrect control type" << logPoints(control, point));
                    return ControlStatus::FormatError;
                }
            }
            break;
        }
        default:
        {
            CTILOG_WARN(dout, logNow() <<" unknown control type " << logPoints(control, point));
            return ControlStatus::FormatError;
        }
    }

    commandString += " offset " + std::to_string(point.getControlOffset());

    switch( control.action )
    {
        case Protocols::DnpSlave::ControlAction::Select:
        {
            commandString += " sbo_selectonly";
            break;
        }
        case Protocols::DnpSlave::ControlAction::Operate:
        {
            commandString += " sbo_operate";
            break;
        }
    }

    const long userMessageId = _porterUserMsgIdGenerator();

    auto requestMsg =
        std::make_unique<CtiRequestMsg>(
                point.getPaoId(),
                commandString,
                userMessageId);

    requestMsg->setMessagePriority(_porterPriority);

    if( const auto error = writePorterConnection(requestMsg.release(), Timing::Chrono::seconds(5)) )
    {
        CTILOG_ERROR(dout, logNow() << " failed to send control request to Porter" << logPoints(control, point));

        return ControlStatus::Undefined;
    }

    return waitForResponse(userMessageId);
}


bool DnpSlave::tryDispatchControl(const Protocols::DnpSlave::control_request &control, const long pointId)
{
    boost::optional<unsigned> controlState;

    switch( control.trip_close )
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
            switch( control.control )
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

    if( ! controlState)
    {
        return false;
    }

    // build the command message and send the control
    auto cmdMsg = std::make_unique<CtiCommandMsg>(CtiCommandMsg::ControlRequest);

    cmdMsg->insert(-1);     // This is the dispatch token and is unimplemented at this time
    cmdMsg->insert(0);      // device id, unknown at this point, dispatch will find it
    cmdMsg->insert(pointId);  // point for control
    cmdMsg->insert(*controlState);

    sendMessageToDispatch(cmdMsg.release());

    if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() << " Control " << (*controlState ? "close" : "open") << " sent to pointid " << pointId);
    }

    return true;
}


int DnpSlave::processAnalogOutputRequest (ConnectionProtocol cp, const ObjectBlock &ob, const Protocols::DnpSlave::ControlAction action)
{
    if( ob.getGroup() != AnalogOutput::Group ||
        ob.empty() )
    {
        return -1;
    }

    const auto &objectDescriptor = ob[0];

    if( ! objectDescriptor.object )
    {
        return -1;
    }

    const auto aoc = dynamic_cast<const AnalogOutput *>(objectDescriptor.object);

    if( ! aoc )
    {
        return -1;
    }

    using Protocols::DnpSlave::analog_output_request;

    analog_output_request analog;

    static const std::map<int, AnalogOutput::Variation> Variation {
        { AnalogOutput::AO_16Bit, AnalogOutput::AO_16Bit },
        { AnalogOutput::AO_32Bit, AnalogOutput::AO_32Bit },
        { AnalogOutput::AO_SingleFloat, AnalogOutput::AO_SingleFloat },
        { AnalogOutput::AO_DoubleFloat, AnalogOutput::AO_DoubleFloat },
    };

    //  create the point so we can echo it back in the DNP response
    analog.offset = objectDescriptor.index;
    analog.value  = aoc->getValue();
    analog.type   = mapFindOrDefault(Variation, aoc->getVariation(), AnalogOutput::AO_32Bit);
    analog.action = action;
    analog.isLongIndexed =
        (ob.getIndexLength()    == 2 &&
         ob.getQuantityLength() == 2);

    analog.status = ControlStatus::NotSupported;

    // This guard must happen before the _receiveMux or a deadlock can occur.
    CTILOCKGUARD( CtiMutex, recvGuard, getReceiveFromList().getMutex() );
    // Protect _receiveMap while we use it.
    CTILOCKGUARD( CtiMutex, guard, _receiveMux );

    //  look for the point with the correct control offset
    for( const auto &kv : _receiveMap )
    {
        const DnpId &dnpId = kv.second;

        if( dnpId.SlaveId      == cp.dnpSlave.getSrcAddr()
            && dnpId.MasterId  == cp.dnpSlave.getDstAddr()
            && dnpId.PointType == AnalogPointType
            && dnpId.Offset    == (analog.offset + 1) )
        {
            const CtiFDRDestination &fdrdest = kv.first;
            long fdrPointId = fdrdest.getParentPointId();
            CtiFDRPoint fdrPoint;
            if( !findPointIdInList( fdrPointId, getReceiveFromList(), fdrPoint ) )
            {
                continue;
            }

            if( fdrPoint.isControllable() )
            {
                if( isDnpDirectDeviceId( fdrPoint.getPaoID() ) )
                {
                    analog.status = tryPorterAnalogOutput(analog, fdrPoint.getPointID(), dnpId.Multiplier);
                }
                else if( tryDispatchAnalogOutput(analog, fdrPoint.getPointID(), dnpId.Multiplier) )
                {
                    analog.status = ControlStatus::Success;
                }
            }
            else
            {
                auto pData = 
                    std::make_unique<CtiPointDataMsg>(
                        fdrPoint.getPointID(),
                        analog.value * dnpId.Multiplier,
                        NormalQuality,
                        fdrPoint.getPointType());

                // consumes a delete memory
                queueMessageToDispatch(pData.release());

                analog.status = ControlStatus::Success;

                if (getDebugLevel () & DETAIL_FDR_DEBUGLEVEL)
                {
                    FormattedList l;

                    l.add("Point ID") << fdrPoint.getPointID();
                    l.add("Pao ID") << fdrPoint.getPaoID();
                    l.add("Incoming value") << analog.value;
                    l.add("FDR multiplier") << dnpId.Multiplier;
                    l.add("Resulting value") << analog.value * dnpId.Multiplier;

                    CTILOG_DEBUG(dout, "Sending analog point update to Dispatch:" << l);
                }
            }
        }
    }

    cp.dnpSlave.setAnalogOutputCommand(analog);

    return doComms(cp, "analog output");
}


std::string describeAnalogOutputRequest(const Protocols::DnpSlave::analog_output_request &analog, const LitePoint &point)
{
    FormattedList l;

    l.add("DNP request");
    l.add("Action") << log(analog.action);
    l.add("Long index") << analog.isLongIndexed;
    l.add("Offset") << analog.offset;
    l.add("Status") << Protocols::DnpProtocol::getControlResultString(static_cast<unsigned char>(analog.status));
    l.add("Type") << analog.type;
    l.add("Value") << analog.value;

    l.add("Yukon point");
    l.add("Control offset") << point.getControlOffset();
    l.add("Pao id") << point.getPaoId();
    l.add("Point id") << point.getPointId();
    l.add("Point name") << point.getPointName();
    l.add("Point offset") << point.getPointOffset();
    l.add("Point type") << point.getPointType();

    return l.toString();
}


ControlStatus DnpSlave::tryPorterAnalogOutput(const Protocols::DnpSlave::analog_output_request &analog, long pointId, double multiplier)
{
    auto point = lookupPointById(pointId);

    if( point.getPointId() != pointId )
    {
        CTILOG_WARN(dout, logNow() <<" could not load DNP pointid "<< pointId);

        return ControlStatus::NotSupported;
    }

    std::string commandString = "putvalue analog value ";

    switch( analog.type )
    {
        case AnalogOutput::AO_16Bit:
        case AnalogOutput::AO_32Bit:
        {
            commandString += std::to_string(static_cast<long>(analog.value * multiplier));
            break;
        }

        case AnalogOutput::AO_SingleFloat:
        case AnalogOutput::AO_DoubleFloat:
        {
            commandString += std::to_string(analog.value * multiplier);
            break;
        }
    }

    commandString += " select pointid " + std::to_string(pointId);

    const long userMessageId = _porterUserMsgIdGenerator();

    auto requestMsg =
        std::make_unique<CtiRequestMsg>(
                point.getPaoId(),
                commandString,
                userMessageId);

    requestMsg->setMessagePriority(_porterPriority);

    if( const auto error = writePorterConnection(requestMsg.release(), Timing::Chrono::seconds(5)) )
    {
        CTILOG_ERROR(dout, logNow() << " failed to send analog output request to Porter" << describeAnalogOutputRequest(analog, point));

        return ControlStatus::Undefined;
    }

    return waitForResponse(userMessageId);
}


bool DnpSlave::tryDispatchAnalogOutput(const Protocols::DnpSlave::analog_output_request &analog, long pointid, double multiplier)
{
    std::string translationName = "DNP offset " + std::to_string(analog.offset);

    CtiCommandMsg *aoMsg = createAnalogOutputMessage(pointid, translationName, analog.value * multiplier);

    return sendMessageToDispatch(aoMsg);
}


YukonError_t DnpSlave::writePorterConnection(CtiRequestMsg *msg, const Timing::Chrono duration)
{
    return _porterConnection.WriteConnQue(msg, CALLSITE, duration.milliseconds());
}


std::unique_ptr<CtiReturnMsg> DnpSlave::readPorterConnection(const Timing::Chrono duration)
{
    return std::unique_ptr<CtiReturnMsg>(dynamic_cast<CtiReturnMsg *>(_porterConnection.ReadConnQue(duration.milliseconds())));
}


LitePoint DnpSlave::lookupPointById(long pointId)
{
    return _attributeService.getLitePointById(pointId);
}

bool DnpSlave::shouldIgnoreOldData() const
{
    return true;  //  This should be removed when we implement event (class 123) data
}


ControlStatus DnpSlave::waitForResponse(const long userMessageId)
{
    static const std::map<unsigned char, ControlStatus> controlStatuses {
        { static_cast<unsigned char>(ControlStatus::Success),           ControlStatus::Success           },
        { static_cast<unsigned char>(ControlStatus::Timeout),           ControlStatus::Timeout           },
        { static_cast<unsigned char>(ControlStatus::NoSelect),          ControlStatus::NoSelect          },
        { static_cast<unsigned char>(ControlStatus::FormatError),       ControlStatus::FormatError       },
        { static_cast<unsigned char>(ControlStatus::NotSupported),      ControlStatus::NotSupported      },
        { static_cast<unsigned char>(ControlStatus::AlreadyActive),     ControlStatus::AlreadyActive     },
        { static_cast<unsigned char>(ControlStatus::HardwareError),     ControlStatus::HardwareError     },
        { static_cast<unsigned char>(ControlStatus::Local),             ControlStatus::Local             },
        { static_cast<unsigned char>(ControlStatus::TooManyObjs),       ControlStatus::TooManyObjs       },
        { static_cast<unsigned char>(ControlStatus::NotAuthorized),     ControlStatus::NotAuthorized     },
        { static_cast<unsigned char>(ControlStatus::AutomationInhibit), ControlStatus::AutomationInhibit },
        { static_cast<unsigned char>(ControlStatus::ProcessingLimited), ControlStatus::ProcessingLimited },
        { static_cast<unsigned char>(ControlStatus::OutOfRange),        ControlStatus::OutOfRange        },
        { static_cast<unsigned char>(ControlStatus::ReservedMin),       ControlStatus::ReservedMin       },
        { static_cast<unsigned char>(ControlStatus::ReservedMax),       ControlStatus::ReservedMax       },
        { static_cast<unsigned char>(ControlStatus::NonParticipating),  ControlStatus::NonParticipating  },
        { static_cast<unsigned char>(ControlStatus::Undefined),         ControlStatus::Undefined         }};

    Cti::Timing::MillisecondTimer t;

    do
    {
        if( auto msg = readPorterConnection(Timing::Chrono::seconds(1)) )
        {
            if( msg->UserMessageId() == userMessageId && ! msg->ExpectMore() )
            {
                std::regex re { "Control result \\(([0-9]+)\\)" };

                std::smatch results;

                if( std::regex_search(msg->ResultString(), results, re) )
                {
                    try
                    {
                        int error = std::stoi(results[1].str());

                        return mapFindOrDefault(controlStatuses, error, ControlStatus::Undefined);
                    }
                    catch( std::invalid_argument & )
                    {
                    }
                    catch( std::out_of_range & )
                    {
                    }
                }

                return Protocols::DNP::ControlStatus::Undefined;
            }
        }

    } while( t.elapsed() < (_porterTimeout * 1000) );

    return ControlStatus::Undefined;
}


DnpId DnpSlave::ForeignToYukonId(const CtiFDRDestination &pointDestination)
{
    DnpId dnpId;

    static const std::string dnpMasterId                     = "MasterId";
    static const std::string dnpSlaveId                      = "SlaveId";
    static const std::string dnpPointType                    = "POINTTYPE";
    static const std::string dnpPointOffset                  = "Offset";
    static const std::string dnpPointStatusString            = "Status";
    static const std::string dnpPointAnalogString            = "Analog";
    static const std::string dnpPointCalcStatusString        = "CalcStatus";
    static const std::string dnpPointCalcAnalogString        = "CalcAnalog";
    static const std::string dnpPointAccumulatorString       = "PulseAccumulator";
    static const std::string dnpPointDemandAccumulatorString = "DemandAccumulator";
    static const std::string dnpPointMultiplier              = "Multiplier";

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
        { to_lower_copy(dnpPointStatusString),            StatusPointType },
        { to_lower_copy(dnpPointCalcStatusString),        StatusPointType },
        { to_lower_copy(dnpPointAnalogString),            AnalogPointType },
        { to_lower_copy(dnpPointCalcAnalogString),        AnalogPointType },
        { to_lower_copy(dnpPointAccumulatorString),       PulseAccumulatorPointType },
        { to_lower_copy(dnpPointDemandAccumulatorString), DemandAccumulatorPointType }
    };

    dnpId.PointType = mapFindOrDefault(PointTypeNames, to_lower_copy(pointType), InvalidPointType);

    dnpId.Offset = std::stoi(dnpOffset);
    dnpId.MasterServerName = pointDestination.getDestination();

    if (dnpMultiplier.empty())
    {
        dnpId.Multiplier = 1;
    }
    else
    {
        dnpId.Multiplier = std::stod(dnpMultiplier);
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
            return lastTimeStamp >= (Now - _staleDataTimeout);
        }
    }

    return false;
}

auto DnpSlave::getProtocolForConnection(ServerConnection& connection) -> ConnectionProtocol
{
    std::lock_guard<std::mutex> guard(_connectionMux);

    return { connection, _dnpSlaves[connection.getConnectionNumber()] };
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


