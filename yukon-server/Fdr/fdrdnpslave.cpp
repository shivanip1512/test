#include "precompiled.h"

#include "fdrdnpslave.h"

#include "prot_dnp.h"
#include "dnp_object_analogoutput.h"

#include "amq_constants.h"

#include "AttributeService.h"

#include "resolvers.h"
#include "slctdev.h"

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
    _staleDataTimeOut(0),
    _porterConnection(Cti::Messaging::ActiveMQ::Queue::porter),
    _attributeService(std::make_unique<AttributeService>())
{
    _porterConnection.setName("FDR DNP Slave to Porter");
}

DnpSlave::~DnpSlave() = default;

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


void DnpSlave::logCommand(const std::string &description, const char *data, const unsigned size)
{
    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() <<" received " << description <<
                     arrayToRange(reinterpret_cast<const unsigned char*>(data), size));
    }
}



int DnpSlave::processMessageFromForeignSystem (ServerConnection& connection,
                                         const char* data, unsigned int size)
{
    const auto requestType = _dnpSlave.identifyRequest(data, size);

    using Cmd = DnpSlaveProtocol::Commands;
    using Act = Protocols::DnpSlave::ControlAction;

    static const std::map<Cmd, std::pair<std::string, std::function<int ()>>> commandFunctions {
        { Cmd::LinkStatus,              { "a DNP data link status request",
            [&] { return processDataLinkConfirmationRequest(connection); }}},

        { Cmd::ResetLink,               { "a DNP data link reset",
            [&] { return processDataLinkReset(connection); }}},

        { Cmd::Class1230Read,           { "a DNP scan request",
            [&] { return processScanSlaveRequest(connection); }}},

        { Cmd::SetDigitalOut_Select,    { "a DNP control select request",
            [&] { return processControlRequest(connection, *requestType.second, Act::Select); }}},

        { Cmd::SetDigitalOut_Operate,   { "a DNP control operate request",
            [&] { return processControlRequest(connection, *requestType.second, Act::Operate); }}},

        { Cmd::SetDigitalOut_Direct,    { "a DNP direct control request",
            [&] { return processControlRequest(connection, *requestType.second, Act::Direct); }}},

        { Cmd::SetAnalogOut_Select,     { "a DNP analog output select request",
            [&] { return processAnalogOutputRequest(connection, *requestType.second, Act::Select); }}},

        { Cmd::SetAnalogOut_Operate,    { "a DNP analog output operate request",
            [&] { return processAnalogOutputRequest(connection, *requestType.second, Act::Operate); }}},

        { Cmd::SetAnalogOut_Direct,     { "a DNP direct analog output request",
            [&] { return processAnalogOutputRequest(connection, *requestType.second, Act::Direct); }}}
        };

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
        CTILOG_DEBUG(dout, logNow() << " received an "<<
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

    std::vector<std::unique_ptr<Protocols::DnpSlave::output_point>> outputPoints;

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

            std::unique_ptr<Protocols::DnpSlave::output_point> point;

            switch (dnpId.PointType)
            {
                case StatusPointType:
                {
                    auto s = std::make_unique<Protocols::DnpSlave::output_digital>();
                    s->status = fdrPoint->getValue();
                    point = std::move(s);
                    break;
                }
                case AnalogPointType:
                {
                    auto a = std::make_unique<Protocols::DnpSlave::output_analog>();
                    a->value = fdrPoint->getValue() * dnpId.Multiplier;
                    point = std::move(a);
                    break;
                }
                case PulseAccumulatorPointType:
                {
                    auto c = std::make_unique<Protocols::DnpSlave::output_counter>();
                    c->value = fdrPoint->getValue() * dnpId.Multiplier;
                    point = std::move(c);
                    break;
                }
                default:
                {
                    continue;
                }
            }

            point->online = YukonToForeignQuality(fdrPoint->getQuality(), fdrPoint->getLastTimeStamp(), Now);
            point->offset = dnpId.Offset - 1;  //  convert to DNP's 0-based indexing

            outputPoints.emplace_back(std::move(point));
        }
    }

    _dnpSlave.setScanCommand(std::move(outputPoints));

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


bool DnpSlave::isDnpDeviceId(const long deviceId) const
{
    const std::string sql = "select paotype from yukonpaobject where paobjectid=?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader rdr(connection, sql);

    rdr << deviceId;

    rdr.execute();

    if( rdr() )
    {
        std::string paoTypeStr;

        rdr[0] >> paoTypeStr;

        return isDnpDeviceType(resolveDeviceType(paoTypeStr));
    }

    return false;
}


int DnpSlave::processControlRequest (ServerConnection& connection, const ObjectBlock &ob, const Protocols::DnpSlave::ControlAction action)
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
    for( const auto &kv : _receiveMap )
    {
        const DnpId &dnpId = kv.second;
        if( dnpId.PointType   == StatusPointType
            && dnpId.SlaveId  == _dnpSlave.getSrcAddr()
            && dnpId.MasterId == _dnpSlave.getDstAddr()
            && dnpId.Offset   == control.offset )
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
                if( isDnpDeviceId(fdrPoint->getPaoID()) )
                {
                    control.status = tryPorterControl(control, fdrPoint->getPointID());
                }
                else if( tryDispatchControl(control, fdrPoint->getPointID()) )
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
        }
    }

    _dnpSlave.setControlCommand(control);

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


ControlStatus DnpSlave::tryPorterControl(const Protocols::DnpSlave::control_request &control, const long pointId)
{
    auto point = _attributeService->getLitePointById(pointId);

    //point.getControlType();
    bool match = false;
/*
    if( controlParameters->getControlType() == ControlType_Latch ||
        controlParameters->getControlType() == ControlType_SBOLatch )
    {
        if( findStringIgnoreCase(parse.getCommandStr().c_str(), controlParameters->getStateZeroControl()) )
        {
            hist->setRawState(STATEZERO);
        }
        else if( findStringIgnoreCase(parse.getCommandStr().c_str(), controlParameters->getStateOneControl()) )
        {
            hist->setRawState(STATEONE);
        }

        if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
        {
            controltype = BinaryOutputControl::LatchOff;
        }
        else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
        {
            controltype = BinaryOutputControl::LatchOn;
        }

        offset = controlParameters->getControlOffset();
    }
    else  //  assume pulsed
    {
        if( findStringIgnoreCase(parse.getCommandStr().c_str(), controlParameters->getStateZeroControl()) )      //  CMD_FLAG_CTL_OPEN
        {
            on_time = controlParameters->getCloseTime1();

            hist->setRawState(STATEZERO);
        }
        else if( findStringIgnoreCase(parse.getCommandStr().c_str(), controlParameters->getStateOneControl()) )  //  CMD_FLAG_CTL_CLOSE
        {
            on_time = controlParameters->getCloseTime2();

            hist->setRawState(STATEONE);
        }

        if( findStringIgnoreCase(parse.getCommandStr().c_str(), " direct") )
        {
            trip_close = BinaryOutputControl::NUL;
        }
        else
        {
            if( parse.getFlags() & CMD_FLAG_CTL_OPEN )
            {
                trip_close = BinaryOutputControl::Trip;
            }
            else if( parse.getFlags() & CMD_FLAG_CTL_CLOSE )
            {
                trip_close = BinaryOutputControl::Close;
            }
        }

        controltype = BinaryOutputControl::PulseOn;
        offset      = controlParameters->getControlOffset();
    }
*/
    if( ! match )
    {
        return ControlStatus::NotSupported;
    }

    //PorterConnection->WriteConnQue(request, 1000);

    //  DNP passthrough control via Porter...  but set timeouts, I guess?

    std::string responseString;

    std::regex re { "Control result \\(([0-9]+)\\)" };

    std::smatch results;

    if( std::regex_search(responseString, results, re) )
    {
        try
        {
            int error = std::stoi(results[0].str());
        }
        catch( std::invalid_argument & )
        {
        }
        catch( std::out_of_range & )
        {
        }
    }

    return Protocols::DNP::ControlStatus::NotSupported;
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


int DnpSlave::processAnalogOutputRequest (ServerConnection& connection, const ObjectBlock &ob, const Protocols::DnpSlave::ControlAction action)
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

    //  look for the point with the correct control offset
    for( const auto &kv : _receiveMap )
    {
        const DnpId &dnpId = kv.second;
        if( dnpId.SlaveId      == _dnpSlave.getSrcAddr()
            && dnpId.MasterId  == _dnpSlave.getDstAddr()
            && dnpId.Offset    == analog.offset
            && dnpId.PointType == AnalogPointType )
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
                if( isDnpDeviceId(fdrPoint->getPaoID()) )
                {
                    analog.status = tryPorterAnalogOutput(analog);
                }
                else if( tryDispatchAnalogOutput(analog, fdrPoint->getPointID()) )
                {
                    analog.status = ControlStatus::Success;
                }
            }
        }
    }

    _dnpSlave.setAnalogOutputCommand(analog);

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


ControlStatus DnpSlave::tryPorterAnalogOutput(const Protocols::DnpSlave::analog_output_request &analog)
{
    return ControlStatus::Success;
}


bool DnpSlave::tryDispatchAnalogOutput(const Protocols::DnpSlave::analog_output_request &analog, long pointid)
{
    std::string translationName = "DNP offset " + std::to_string(analog.offset);

    CtiCommandMsg *aoMsg = createAnalogOutputMessage(pointid, translationName, analog.value);

    return sendMessageToDispatch(aoMsg);
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
        { to_lower_copy(dnpPointStatusString),       StatusPointType },
        { to_lower_copy(dnpPointAnalogString),       AnalogPointType },
        { to_lower_copy(dnpPointCalcAnalogString),   AnalogPointType },
        { to_lower_copy(dnpPointCounterString),      PulseAccumulatorPointType }
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


