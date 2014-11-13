#include "precompiled.h"

#include "fdrdnpslave.h"

#include "prot_dnp.h"

#include "std_helper.h"

using namespace std;
using namespace Cti::Protocols;

namespace Cti {
namespace Fdr {

enum MiscDefines
{
    DNPSLAVE_PORTNUMBER       = 2085,
    FDR_DNP_HEADER_SIZE       = 10,
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

/** global used to start the interface by c functions **/
DnpSlave * dnpSlaveInterface;



const string DNPInMessageString  = "DNP InMessage";
const string DNPOutMessageString = "DNP OutMessage";


// Constructors, Destructor, and Operators
DnpSlave::DnpSlave() :
    CtiFDRSocketServer("DNPSLAVE"),
    _staleDataTimeOut(0)
{
}

void DnpSlave::startup()
{
    init();
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
    string   tempStr;

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


    tempStr = gConfigParms.getValueAsString(KEY_DEBUG_MODE);
    if (tempStr.length() > 0)
    {
        setInterfaceDebugMode (true);
    }
    else
    {
        setInterfaceDebugMode (false);
    }

    tempStr = gConfigParms.getValueAsString(KEY_FDR_DNPSLAVE_SERVER_NAMES);
    std::string serverNames = tempStr;
    typedef boost::tokenizer<boost::char_separator<char> > tokenizer;
    boost::char_separator<char> sep(",");
    tokenizer pairTok(serverNames, sep);
    for (tokenizer::iterator pairIter = pairTok.begin();
         pairIter != pairTok.end();
         ++pairIter)
    {
        boost::char_separator<char> innerSep("= ");
        tokenizer innerTok(*pairIter, innerSep);
        tokenizer::iterator first = innerTok.begin();
        tokenizer::iterator end = innerTok.end();
        if (first != end)
        {
            tokenizer::iterator second = first;
            ++second;
            if (second != end)
            {
                std::string serverName = *first;
                std::string serverAddress = *second;
                _serverNameLookup[serverAddress] = serverName;
                if (getDebugLevel () & STARTUP_FDR_DEBUGLEVEL)
                {
                    CTILOG_DEBUG(dout, logNow() << "Added server mapping: "<< serverAddress <<" -> "<< serverName);
                }
            }
        }
    }


    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        Cti::FormattedList loglist;

        loglist.add(KEY_LISTEN_PORT_NUMBER) << getPortNumber();
        loglist.add(KEY_DB_RELOAD_RATE)     << getReloadRate();
        loglist.add(KEY_LINK_TIMEOUT)       << getLinkTimeout();
        loglist.add(KEY_DEBUG_MODE)         << (bool)isInterfaceInDebugMode();

        CTILOG_INFO(dout, "FDRDnpSlave Configs"
                << loglist);
    }

    return true;
}

CtiFDRClientServerConnectionSPtr DnpSlave::createNewConnection(SOCKET newSocket)
{
    Cti::SocketAddress peerAddr( Cti::SocketAddress::STORAGE_SIZE );

    if( getpeername(newSocket, &peerAddr._addr.sa, &peerAddr._addrlen) == SOCKET_ERROR )
    {
        const DWORD error = WSAGetLastError();
        CTILOG_ERROR(dout, "getpeername() failed with error code: "<< error <<" / "<< Cti::getSystemErrorMessage(error));

        return CtiFDRClientServerConnectionSPtr();
    }

    const string ipString = peerAddr.toString();

    ServerNameMap::const_iterator iter = _serverNameLookup.find(ipString);

    const string connName = (iter == _serverNameLookup.end())? ipString : iter->second;

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

/********************************************************************************************************
  not used intentionally
*********************************************************************************************************/
bool DnpSlave::buildForeignSystemMessage(const CtiFDRDestination& destination,
                                               char** buffer,
                                               unsigned int& bufferSize)
{
    return false;
}

int DnpSlave::processMessageFromForeignSystem (Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size)
{
    BOOL timeFlag = false;
    unsigned long function = getHeaderBytes(data, size);

    switch (function)
    {
        case SINGLE_SOCKET_DNP_DATALINK_REQ:
        {
            processDataLinkConfirmationRequest (connection, data);
            break;
        }
        case SINGLE_SOCKET_DNP_READ:
        {
            /********************************************************************************************************
              here should add something to _dnpData.slaveDecode()  However, for now, this will process any read command
              as a scan1230 request.
              NOTE:  scan integrity dnp message
                05 64 17 c4 1e 00 02 00 78 b5 c0 ca 01 32 01 06 3c 02 06 3c 03 06 3c 04 06 3c 9d f5 01 06 75 e1

                SCAN INTEGRITY:
                05 64 17 c4 1e 00 01 00 d3 05
                c0 c3
                01 - read
                32 01 - data object 50 variation 1 - TIME AND DATE
                06 - qualifier - no index, packed.  no range field
                3c 02 - data object 60 variation 2 - class 1 data
                06 - qualifier - no index, packed.  no range field
                3c 03 - data object 60 variation 3 - class 2 data
                06 - qualifier - no index, packed.  no range field
                3c 04 - data object 60 variation 4 - class 3 data
                06 - qualifier - no index, packed.  no range field
                3c <ce 3c - 16byte CRC> 01 - data object 60 variation 1 - class 0 data
                06 - qualifier - no index, packed.  no range field
                75 e1 - CRC

                SCAN GENERAL
                05 64 14 c4 1e 00 01 00 83 96
                c0 c9
                01
                32 01 - data object 50 variation 1 - TIME AND DATE
                06 - qualifier - no index, packed.  no range field
                3c 02 - data object 60 variation 2 - class 1 data
                06 - qualifier - no index, packed.  no range field
                3c 03 - data object 60 variation 3 - class 2 data
                06 - qualifier - no index, packed.  no range field
                3c 04 - data object 60 variation 4 - class 3 data
                06 - qualifier - no index, packed.  no range field
                1b b5 - CRC
             ********************************************************************************************************/
            if (size > FDR_DNP_HEADER_SIZE)
            {
                /*if (isScanIntegrityRequest(data, size))
                {
                    timeFlag = false;
                }*/
                processScanSlaveRequest (connection, data, size, timeFlag);
            }
            break;
        }
        case SINGLE_SOCKET_DNP_CONFIRM:
        case SINGLE_SOCKET_DNP_WRITE:
        case SINGLE_SOCKET_DNP_DIRECT_OP:
        default:
        {
            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CTILOG_DEBUG(dout, logNow() << getInterfaceName() <<" received an unsupported DNP message, response not generated.");
            }
            break;
        }
    }
    return 0;
}


int DnpSlave::processDataLinkConfirmationRequest(Cti::Fdr::ServerConnection& connection, const char* data)
{
    int retVal = 0;
    unsigned char* buffer = NULL;
    unsigned int bufferSize = getMessageSize(data);
    if (bufferSize == 10)
    {
        bool linkStatusReq = ( ((data[3] & 0x09) == 0x09 ) ? true : false);
        string linkMessage = ( linkStatusReq ? "data link status request" : "reset link" );

        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() <<" "<< getInterfaceName() <<" received DNP " << linkMessage <<" request message."<<
                    dumpDNPMessage(DNPInMessageString, data, bufferSize));
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
        crc.sh = DNP::DatalinkLayer::crc((const unsigned char*) buffer, 8);
        buffer[8] = crc.ch[0];
        buffer[9] = crc.ch[1];

        connection.queueMessage((CHAR *)buffer, bufferSize, MAXPRIORITY - 1);
        //error processing data link confirmation Request
        linkMessage = ( linkStatusReq ? "data link acknowledgement" : "ack" );
        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() << getInterfaceName() <<" sending DNP "<< linkMessage <<" message."<<
                    dumpDNPMessage(DNPOutMessageString, (CHAR *)buffer, bufferSize));
        }
    }
    else
    {
        //error processing data link confirmation Request
        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
        {
            CTILOG_DEBUG(dout, logNow() << getInterfaceName() <<" received an DNP data link confirmation request message, response not generated.");
        }
        retVal = -1;
    }
    return retVal;


}

int DnpSlave::processScanSlaveRequest (Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size, bool includeTime)
{

    int retVal = 0;

    CtiXfer xfer = CtiXfer(NULL, 0, (BYTE*)data, getMessageSize(data));
    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CTILOG_DEBUG(dout, logNow() << getInterfaceName() <<" received DNP scan request message"<<
                dumpDNPMessage(DNPInMessageString, data, size));
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

            Protocols::DNPSlaveInterface::input_point iPoint;

            iPoint.online = YukonToForeignQuality(fdrPoint->getQuality(), fdrPoint->getLastTimeStamp());
            iPoint.control_offset = dnpId.Offset;
            iPoint.includeTime = includeTime;
            iPoint.timestamp = fdrPoint->getLastTimeStamp();

            if (dnpId.PointType == StatusPointType )
            {
                iPoint.din.trip_close = (fdrPoint->getValue() == 0)?(DNP::BinaryOutputControl::Trip):(DNP::BinaryOutputControl::Close);
                iPoint.type = Protocols::DNPSlaveInterface::DigitalInput;
            }
            else if (dnpId.PointType == AnalogPointType )
            {
                iPoint.ain.value =  (fdrPoint->getValue() * dnpId.Multiplier);
                iPoint.type = Protocols::DNPSlaveInterface::AnalogInputType;
            }
            else if (dnpId.PointType == PulseAccumulatorPointType )
            {
                iPoint.counterin.value =  (fdrPoint->getValue() * dnpId.Multiplier);
                iPoint.type = Protocols::DNPSlaveInterface::Counters;
            }
            else
            {
                continue;

            }
            _dnpData.addInputPoint(iPoint);
        }
    }

    _dnpData.setAddresses(src.sh, dest.sh);
    _dnpData.setSlaveCommand(DNPSlaveInterface::Command_Class1230Read);
    _dnpData.setOptions(DNPSlaveInterface::Options_SlaveResponse, seqnumber);

     while( !_dnpData.isTransactionComplete() )
     {
         if( _dnpData.slaveGenerate(xfer) == 0 )
         {
             if (xfer.getOutBuffer() != NULL && xfer.getOutCount() > 0)
             {
                 int bufferSize = xfer.getOutCount();
                 char* buffer = new CHAR[bufferSize];
                 std::memcpy(buffer, xfer.getOutBuffer(), bufferSize);
                 if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
                 {
                     CTILOG_DEBUG(dout, logNow() << getInterfaceName() <<" sending DNP scan response message."<<
                             dumpDNPMessage(DNPOutMessageString, buffer, bufferSize));
                 }
                 connection.queueMessage(buffer,bufferSize, MAXPRIORITY - 1);
             }

             _dnpData.slaveDecode(xfer);
         }
         else
         {
             if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
             {
                 CTILOG_DEBUG(dout, logNow() << getInterfaceName() <<" was not able to generate scan response.");
             }
         }
     }




    return retVal;

}





DnpId DnpSlave::ForeignToYukonId(CtiFDRDestination pointDestination)
{
    DnpId dnpId;

    static const string dnpMasterId              = "MasterId";
    static const string dnpSlaveId               = "SlaveId";
    static const string dnpPointType             = "POINTTYPE";
    static const string dnpPointOffset           = "Offset";
    static const string dnpPointStatusString     = "Status";
    static const string dnpPointAnalogString     = "Analog";
    static const string dnpPointCalcAnalogString = "CalcAnalog";
    static const string dnpPointCounterString    = "PulseAccumulator";
    static const string dnpPointMultiplier       = "Multiplier";

    string masterId  = pointDestination.getTranslationValue(dnpMasterId);
    string slaveId   = pointDestination.getTranslationValue(dnpSlaveId);
    string pointType = pointDestination.getTranslationValue(dnpPointType);
    string dnpOffset = pointDestination.getTranslationValue(dnpPointOffset);
    string dnpMultiplier = pointDestination.getTranslationValue(dnpPointMultiplier);

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



unsigned long DnpSlave::getHeaderBytes(const char* data, unsigned int size)
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
    BYTE msgSize = 0;
    BYTE msgLength = 0;
    if (data != NULL)
    {
        msgLength =  (BYTE)*(data + 2);

        msgSize += msgLength;
        msgSize += 5; //x05 x64 Len(1) headerCRC(2)

        if (msgLength > 5)
        {
            msgSize += ( (msgLength - 5) / 16) * 2;

            msgSize += 2; //finalCRC(2)
        }
    }
    return msgSize;
}

bool DnpSlave::isScanIntegrityRequest(const char* data, unsigned int size)
{

    /*    05 64 17 c4 1e 00 01 00 d3 05
          c0 c3
          01 - read
          32 01 - data object 50 variation 1 - TIME AND DATE
          06 - qualifier - no index, packed.  no range field
          3c 02 - data object 60 variation 2 - class 1 data
          06 - qualifier - no index, packed.  no range field
          3c 03 - data object 60 variation 3 - class 2 data
          06 - qualifier - no index, packed.  no range field
          3c 04 - data object 60 variation 4 - class 3 data
          06 - qualifier - no index, packed.  no range field
          3c <ce 3c - 16byte CRC> 01 - data object 60 variation 1 - class 0 data
          06 - qualifier - no index, packed.  no range field
          75 e1 - CRC
    */
    bool retVal = false;
    if (size > FDR_DNP_HEADER_SIZE && size > FDR_DNP_REQ_FUNC_LOCATION + 17)
    {

        if (data[FDR_DNP_REQ_FUNC_LOCATION + 13] == 0x3c &&
            data[FDR_DNP_REQ_FUNC_LOCATION + 16] == 0x01 &&
            data[FDR_DNP_REQ_FUNC_LOCATION + 17] == 0x06 )
        {
            retVal = true;
        }
    }


    return retVal;
}

std::string DnpSlave::dumpDNPMessage(const string dnpDirection, const char* data, unsigned int size)
{
    Cti::StreamBuffer sb;

    sb << endl << dnpDirection <<" message:"
       << endl << Cti::arrayToRange(reinterpret_cast<const unsigned char*>(data), size);

    return sb;
}


unsigned int DnpSlave::getMagicInitialMsgSize()
{
    return FDR_DNP_HEADER_SIZE;
}


}
}


/****************************************************************************************
*
*      Here Starts some C functions that are used to Start the
*      Interface and Stop it from the Main() of FDR.EXE.
*
*/

#ifdef __cplusplus
extern "C" {
#endif

/************************************************************************
* Function Name: Extern C int RunInterface(void)
*
* Description: This is used to Start the Interface from the Main()
*              of FDR.EXE. Each interface it Dynamicly loaded and
*              this function creates a global FDRCygnet Object and then
*              calls its run method to cank it up.
*
*************************************************************************
*/

    DLLEXPORT int RunInterface(void)
    {

        // make a point to the interface
        Cti::Fdr::dnpSlaveInterface = new Cti::Fdr::DnpSlave();
        Cti::Fdr::dnpSlaveInterface->startup();
        // now start it up
        return Cti::Fdr::dnpSlaveInterface->run();
    }

/************************************************************************
* Function Name: Extern C int StopInterface(void)
*
* Description: This is used to Stop the Interface from the Main()
*              of FDR.EXE. Each interface it Dynamicly loaded and
*              this function stops a global FDRCygnet Object and then
*              deletes it.
*
*************************************************************************
*/
    DLLEXPORT int StopInterface( void )
    {

        Cti::Fdr::dnpSlaveInterface->stop();
        delete Cti::Fdr::dnpSlaveInterface;
        Cti::Fdr::dnpSlaveInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif


