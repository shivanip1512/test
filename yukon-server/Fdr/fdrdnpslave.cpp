#include "yukon.h"

#include <iostream>

#include <stdio.h>

/** include files **/

#include "ctidate.h"
#include "ctitime.h"
#include "ctistring.h"
#include <boost/tokenizer.hpp>

#include "cparms.h"
#include "msg_multi.h"
#include "msg_ptreg.h"
#include "msg_cmd.h"
#include "message.h"
#include "msg_reg.h"
#include "msg_ptreg.h"
#include "msg_pdata.h"
#include "msg_signal.h"
#include "connection.h"
#include "pointtypes.h"
#include "dllbase.h"
#include "logger.h"
#include "guard.h"
#include "fdrpointlist.h"
#include "fdrsocketinterface.h"
#include "fdrscadaserver.h"
#include "fdrdnphelper.h"
#include "utility.h"
// this class header
#include "fdrdnpslave.h"
#include "prot_dnp.h"
#include "prot_base.h"
#include "dnp_object_binaryinput.h"
#include "dnp_object_counter.h"
#include "dnp_objects.h"

using namespace std;
using namespace Cti::Protocol;
/** local definitions **/

/** global used to start the interface by c functions **/
CtiFDRDnpSlave * dnpSlaveInterface;

const CHAR * CtiFDRDnpSlave::KEY_LISTEN_PORT_NUMBER = "FDR_DNPSLAVE_PORT_NUMBER";
const CHAR * CtiFDRDnpSlave::KEY_DB_RELOAD_RATE = "FDR_DNPSLAVE_DB_RELOAD_RATE";
const CHAR * CtiFDRDnpSlave::KEY_DEBUG_MODE = "FDR_DNPSLAVE_DEBUG_MODE";
const CHAR * CtiFDRDnpSlave::KEY_FDR_DNPSLAVE_SERVER_NAMES = "FDR_DNPSLAVE_SERVER_NAMES";
const CHAR * CtiFDRDnpSlave::KEY_LINK_TIMEOUT = "FDR_DNPSLAVE_LINK_TIMEOUT_SECONDS";
const CHAR * CtiFDRDnpSlave::KEY_STALEDATA_TIMEOUT = "FDR_DNPSLAVE_STALEDATA_TIMEOUT";

const string CtiFDRDnpSlave::dnpMasterId="MasterId";
const string CtiFDRDnpSlave::dnpSlaveId="SlaveId";
const string CtiFDRDnpSlave::dnpPointType="POINTTYPE";
const string CtiFDRDnpSlave::dnpPointOffset="Offset";
const string CtiFDRDnpSlave::dnpPointStatusString="Status";
const string CtiFDRDnpSlave::dnpPointAnalogString="Analog";
const string CtiFDRDnpSlave::dnpPointCalcAnalogString="CalcAnalog";
const string CtiFDRDnpSlave::dnpPointCounterString="PulseAccumulator";
const string CtiFDRDnpSlave::dnpPointMultiplier="Multiplier";


const string CtiFDRDnpSlave::CtiFdrDNPInMessageString="DNP InMessage";
const string CtiFDRDnpSlave::CtiFdrDNPOutMessageString="DNP OutMessage";


// Constructors, Destructor, and Operators
CtiFDRDnpSlave::CtiFDRDnpSlave() :
    CtiFDRSocketServer(string("DNPSLAVE")),
    _staleDataTimeOut(0)
{
    _helper = new CtiFDRDNPHelper<CtiDnpId>(this);
}

void CtiFDRDnpSlave::startup()
{
    init();
}

CtiFDRDnpSlave::~CtiFDRDnpSlave()
{
    delete _helper;
}

/*************************************************
* Function Name: CtiFDRDnpSlave::config()
*
* Description: loads cparm config values
*
**************************************************
*/
int CtiFDRDnpSlave::readConfig()
{
    int         successful = TRUE;
    string   tempStr;

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
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    logNow() << "Added server mapping: " << serverAddress
                        << " -> " << serverName << endl;
                }
            }
        }
    }


    if (getDebugLevel() & STARTUP_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "----------------FDR-DNPSLAVE Configs------------------------------" << endl;
        dout << "  " << KEY_LISTEN_PORT_NUMBER << ": "
            << getPortNumber() << endl;

        dout << "  " << KEY_DB_RELOAD_RATE << ": "
            << getReloadRate() << endl;

        dout << "  " << KEY_LINK_TIMEOUT << ": "
            << getLinkTimeout() << "       second(s)" << endl;

        dout << "  " << KEY_DEBUG_MODE << ": "
            << (isInterfaceInDebugMode() ? "TRUE" : "FALSE") << endl;

    }
    return successful;
}

CtiFDRClientServerConnection* CtiFDRDnpSlave::createNewConnection(SOCKET newSocket)
{
    sockaddr_in peerAddr;
    int peerAddrSize = sizeof(peerAddr);
    getpeername(newSocket, (SOCKADDR*) &peerAddr, &peerAddrSize);
    std::string ipString(inet_ntoa(peerAddr.sin_addr));
    std::string connName;
    ServerNameMap::const_iterator iter = _serverNameLookup.find(ipString);
    if (iter == _serverNameLookup.end())
    {
        connName = ipString;
    }
    else
    {
        connName = _serverNameLookup[ipString];
    }
    CtiFDRClientServerConnection* newConnection;
    newConnection = new CtiFDRClientServerConnection(connName.c_str(),
                                                 newSocket,
                                                 this);
    newConnection->setRegistered(true); //DNPSLAVE doesn't have a separate registration message

    // I'm not sure this is the best location for this
    sendAllPoints(newConnection);

    return newConnection;
}



bool CtiFDRDnpSlave::translateSinglePoint(CtiFDRPointSPtr & translationPoint, bool sendList)
{
    bool foundPoint = false;

    for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
    {
        foundPoint = true;
        CtiFDRDestination pointDestination = translationPoint->getDestinationList()[x];
        // translate and put the point id the list

        CtiDnpId dnpId = ForeignToYukonId(pointDestination);
        if (!dnpId.valid)
        {
            return foundPoint;
        }

        if (sendList)
        {
            _helper->removeSendMapping(dnpId, pointDestination);
            _helper->addSendMapping(dnpId, pointDestination);
        }
    }
    return foundPoint;
}

void CtiFDRDnpSlave::cleanupTranslationPoint(CtiFDRPointSPtr & translationPoint, bool recvList)
{
    if (recvList)
    {
        return;
    }
    for (int x = 0; x < translationPoint->getDestinationList().size(); x++)
    {
        CtiFDRDestination pointDestination = translationPoint->getDestinationList()[x];
        // translate and put the point id the list


        CtiDnpId dnpId = ForeignToYukonId(pointDestination);
        if (!dnpId.valid)
        {
            return;
        }

        if (!_helper->getIdForDestination(pointDestination, dnpId))
        {
            return;
        }

        if (!recvList)
        {
            _helper->removeSendMapping(dnpId, pointDestination);
        }
    }
}

/********************************************************************************************************
  not used intentionally
*********************************************************************************************************/
bool CtiFDRDnpSlave::buildForeignSystemMessage(const CtiFDRDestination& destination,
                                               char** buffer,
                                               unsigned int& bufferSize)
{
    return false;
}

int CtiFDRDnpSlave::processMessageFromForeignSystem (Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size)
{
    BYTEUSHORT dest, src;
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
                CtiLockGuard<CtiLogger> dout_guard(dout);
                logNow() << " "<< getInterfaceName() <<" received an unsupported DNP message, response not generated. "<< endl;
            }
            break;
        }
    }
    return 0;
}


int CtiFDRDnpSlave::processDataLinkConfirmationRequest(Cti::Fdr::ServerConnection& connection, const char* data)
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
            CtiLockGuard<CtiLogger> dout_guard(dout);
            logNow() << " "<< getInterfaceName() <<" received DNP " << linkMessage <<" request message."<< endl;
            dumpDNPMessage(CtiFdrDNPInMessageString, data, bufferSize);
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
            CtiLockGuard<CtiLogger> dout_guard(dout);
            logNow() << " "<< getInterfaceName() <<" sending DNP " << linkMessage << " message."<< endl;
            dumpDNPMessage(CtiFdrDNPOutMessageString, (CHAR *)buffer, bufferSize);
        }
    }
    else
    {
        //error processing data link confirmation Request
        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            logNow() << " "<< getInterfaceName() <<" received an DNP data link confirmation request message, response not generated. "<< endl;
        }
        retVal = -1;
    }
    return retVal;


}

int CtiFDRDnpSlave::processScanSlaveRequest (Cti::Fdr::ServerConnection& connection,
                                         const char* data, unsigned int size, bool includeTime)
{

    int retVal = 0;

    CtiXfer xfer = CtiXfer(NULL, 0, (BYTE*)data, getMessageSize(data));
    if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        logNow() << " "<< getInterfaceName() <<" received DNP scan request message."<< endl;
        dumpDNPMessage(CtiFdrDNPInMessageString, data, size);
    }

    BYTEUSHORT dest, src;
    dest.ch[0] = data[4];
    dest.ch[1] = data[5];
    src.ch[0] = data[6];
    src.ch[1] = data[7];
    int seqnumber = (data[11] & 0x0F);


    std::map<CtiFDRDestination, CtiDnpId> sendMap = _helper->getSendMappings();
    for (std::map<CtiFDRDestination, CtiDnpId>::iterator iter = sendMap.begin(); iter != sendMap.end(); iter++)
    {
        CtiDnpId dnpId = iter->second;
        if (dnpId.SlaveId == dest.sh && dnpId.MasterId == src.sh )
        {
            CtiFDRDestination fdrdest = iter->first;
            CtiFDRPoint* fdrPoint = fdrdest.getParentPoint();
            CtiLockGuard<CtiMutex> sendGuard(getSendToList().getMutex());
            if (!findPointIdInList(fdrPoint->getPointID(),getSendToList(),*fdrPoint) )
                continue;

            Cti::Protocol::DNPSlaveInterface::input_point iPoint;

            iPoint.online = YukonToForeignQuality(fdrPoint->getQuality(), fdrPoint->getLastTimeStamp());
            iPoint.control_offset = dnpId.Offset;
            iPoint.includeTime = includeTime;
            iPoint.timestamp = fdrPoint->getLastTimeStamp();

            if (dnpId.PointType == StatusPointType )
            {
                iPoint.din.trip_close = (fdrPoint->getValue() == 0)?(DNP::BinaryOutputControl::Trip):(DNP::BinaryOutputControl::Close);
                iPoint.type = Cti::Protocol::DNPSlaveInterface::DigitalInput;
            }
            else if (dnpId.PointType == AnalogPointType )
            {
                iPoint.ain.value =  (fdrPoint->getValue() * dnpId.Multiplier);
                iPoint.type = Cti::Protocol::DNPSlaveInterface::AnalogInputType;
            }
            else if (dnpId.PointType == PulseAccumulatorPointType )
            {
                iPoint.counterin.value =  (fdrPoint->getValue() * dnpId.Multiplier);
                iPoint.type = Cti::Protocol::DNPSlaveInterface::Counters;
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

    if (_dnpData.slaveGenerate(xfer) == 0)
    {
        bool status = true;

        if (xfer.getOutBuffer() != NULL && xfer.getOutCount() > 0)
        {
            char* buffer = NULL;
            unsigned int bufferSize = xfer.getOutCount();
            buffer = new CHAR[bufferSize];
            std::memcpy(buffer, xfer.getOutBuffer(), bufferSize);
            if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
            {
                CtiLockGuard<CtiLogger> dout_guard(dout);
                logNow() << " "<< getInterfaceName() <<" sending DNP scan response message."<< endl;
                dumpDNPMessage(CtiFdrDNPOutMessageString, buffer, bufferSize);
            }
            connection.queueMessage(buffer, xfer.getOutCount(), MAXPRIORITY - 1);
        }
    }
    else
    {
        if (getDebugLevel() & DETAIL_FDR_DEBUGLEVEL)
        {
            CtiLockGuard<CtiLogger> dout_guard(dout);
            logNow() << " "<< getInterfaceName() <<" was not able to generate scan response. "<< endl;
        }

    }

    _dnpData.slaveTransactionComplete();

    return retVal;

}





CtiDnpId CtiFDRDnpSlave::ForeignToYukonId(CtiFDRDestination pointDestination)
{
    CtiDnpId dnpId;

    string masterId  = pointDestination.getTranslationValue(dnpMasterId);
    string slaveId   = pointDestination.getTranslationValue(dnpSlaveId);
    string pointType = pointDestination.getTranslationValue(dnpPointType);
    string dnpOffset = pointDestination.getTranslationValue(dnpPointOffset);
    string dnpMultiplier = pointDestination.getTranslationValue(dnpPointMultiplier);

    if (masterId.empty() || slaveId.empty() || pointType.empty() || dnpOffset.empty())
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        logNow() << "Unable to add destination " << pointDestination
            << " because one of the fields was blank" << endl;
        dnpId.valid = false;
        return dnpId;
    }

    dnpId.MasterId = atoi(masterId.c_str());
    dnpId.SlaveId = atoi(slaveId.c_str());

    if (string_equal(pointType, dnpPointStatusString))
    {
        dnpId.PointType = StatusPointType;
    }
    else if (string_equal(pointType, dnpPointAnalogString) ||
             string_equal(pointType, dnpPointCalcAnalogString))
    {
        dnpId.PointType = AnalogPointType;
    }
    else if (string_equal(pointType, dnpPointCounterString))
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

bool CtiFDRDnpSlave::YukonToForeignQuality(USHORT aQuality, CtiTime lastTimeStamp)
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



unsigned long CtiFDRDnpSlave::getHeaderBytes(const char* data, unsigned int size)
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

unsigned int CtiFDRDnpSlave::getMessageSize(const char* data)
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

bool CtiFDRDnpSlave::isScanIntegrityRequest(const char* data, unsigned int size)
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

void CtiFDRDnpSlave::dumpDNPMessage(const string dnpDirection, const char* data, unsigned int size)
{
    CtiLockGuard<CtiLogger> dout_guard(dout);
    logNow() << " "<< getInterfaceName() <<" "<< dnpDirection <<" message:"<< endl;
    for (int x=0; x < size; x++ )
    {
        dout <<" " + CtiNumStr(data[x]).hex().zpad(2).toString();
    }
    dout << endl;
}


unsigned int CtiFDRDnpSlave::getMagicInitialMsgSize()
{
    return FDR_DNP_HEADER_SIZE;
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
        dnpSlaveInterface = new CtiFDRDnpSlave();
        dnpSlaveInterface->startup();
        // now start it up
        return dnpSlaveInterface->run();
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

        dnpSlaveInterface->stop();
        delete dnpSlaveInterface;
        dnpSlaveInterface = 0;

        return 0;
    }


#ifdef __cplusplus
}
#endif


