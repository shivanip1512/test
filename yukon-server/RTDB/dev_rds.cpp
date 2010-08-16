#include "yukon.h"
#include "dev_rds.h"

namespace Cti       {
namespace Devices    {

static const unsigned char UECPStart  = 0xFE;
static const unsigned char UECPEnd    = 0xFF;
static const unsigned char UECPEscape = 0xFD;

static const unsigned char UECPMinLen = 8; // Start/stop(2), crc(2), addressing(2), Sequence(1), length(1)
static const unsigned char UECPResponseLen = 10;

RDSTransmitter::RDSTransmitter() :
_inCountActual(0),
_isBiDirectionSet(false),
_messageToggleFlag(false),
_command(Complete)
{
    resetStates();
}

RDSTransmitter::~RDSTransmitter()
{
}

int RDSTransmitter::recvCommRequest( OUTMESS *OutMessage )
{
    int retVal = NoError;

    if( OutMessage )
    {
        _outMessage = *OutMessage;
        resetStates();
        _command = Normal;

    }
    else
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - invalid OutMessage in RDSTransmitter::recvCommResult() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
        }

        retVal = MemoryError;
    }

    return retVal;
}

bool RDSTransmitter::isTwoWay()
{
    // Not expected to be set, not supported by UDP. Here for emergency serial use only.
    return gConfigParms.isTrue("RDS_USE_TWO_WAY_MESSAGING");
}

int RDSTransmitter::generate(CtiXfer &xfer)
{
    int status = Normal;

    MessageStore newMessage;

    if(isTwoWay() && !_isBiDirectionSet)
    {
        _currentState = StateSendBiDirectionalRequest;
    }

    switch(_currentState)
    {
        case StateSendBiDirectionalRequest:
        {
            createBiDirectionRequest(newMessage);
            break;
        }
        case StateSendRequestedMessage:
        {
            createCompletePackedMessage(newMessage);
            break;
        }
    }

    _previousState = _currentState;
    _currentState  = StateCheckResponse;

    if(status == Normal)
    {
        addMessageSize(newMessage);
        addSequenceCounter(newMessage);
        addMessageAddressing(newMessage);
        addMessageCRC(newMessage);
        replaceReservedBytes(newMessage);
        addStartStopBytes(newMessage);

        copyMessageToXfer(xfer, newMessage);
    }

    if(!isTwoWay())
    {
        // UDP ports do not do decode.
        _command = Complete;

        // If not 2 way, this is our best place to sleep. Yes its silly.
        // 2 way messages sleep in the decode.
        delay();
    }

    return status;
}

int RDSTransmitter::decode(CtiXfer &xfer, int status)
{
    status = Normal;

    if(isTwoWay())
    {
        if(xfer.getInCountActual() < UECPResponseLen)
        {
            _command = Complete; //Transaction Complete
            status = FinalError;
            _isBiDirectionSet = false;
        }
        else 
        {
            _isBiDirectionSet = true;

            // Check if we are ok, then continue? somehow.
            if(xfer.getInBuffer()[6] == 0)
            {
                //OK!
                status = Normal;
                if(_previousState == StateSendBiDirectionalRequest)
                {
                    _currentState = StateSendRequestedMessage;
                }
                else 
                {
                    _command = Complete; //Transaction Complete
                    delay();
                }
            }
            else
            {
                // print error
                printAcknowledgmentError(xfer.getInBuffer()[6]);
                _command = Complete; //Transaction Complete
                status = FinalError;
            }
        }
    }
    else
    {
        _command = Complete;
        status = Normal;
    }

    return status;
}

void RDSTransmitter::delay()
{
    const int totalGroups = getMessageCountFromBufSize(_outMessage.OutLength);
    Sleep((float)1000*totalGroups/getGroupsPerSecond());
}

void RDSTransmitter::createBiDirectionRequest(MessageStore &message)
{
    message.push_back(CommunicationModeCode);
    message.push_back(0x02); // means bi-directional mode with spontaneous response (see Section 1.3.3); the sequence counter is set to 0.
}

// Moves OutMessage to the UECP formatted message.
// Will copy up to 3 random bytes from the buffer at the end of the message.
void RDSTransmitter::createCompletePackedMessage(MessageStore &message)
{
    _messageToggleFlag = !_messageToggleFlag;
    int msgCount = 0;
    int byteCount = 0;
    message.push_back(ODAFreeFormat);
    message.push_back(getGroupTypeCode());
    message.push_back(0);   //Priority Normal, Normal Mode, No Re-Sending
    message.push_back(_messageToggleFlag); // Format = ToggleFlag is LSB then 4 bits for sequence number in 5 bit field
    message.push_back(_outMessage.OutLength);
    message.push_back(_outMessage.Buffer.OutMessage[byteCount++]);
    message.push_back(_outMessage.Buffer.OutMessage[byteCount++]);
    message.push_back(_outMessage.Buffer.OutMessage[byteCount++]);
    msgCount++;
     
    const int totalMessages = getMessageCountFromBufSize(_outMessage.OutLength);

    while( msgCount < totalMessages)
    {
        message.push_back(ODAFreeFormat);
        message.push_back(getGroupTypeCode());
        message.push_back(0);   //Priority Normal, Normal Mode, No Re-Sending
        message.push_back((msgCount << 1) + _messageToggleFlag); // Format = ToggleFlag is LSB then 4 bits for sequence number in 5 bit field
        message.push_back(_outMessage.Buffer.OutMessage[byteCount++]);
        message.push_back(_outMessage.Buffer.OutMessage[byteCount++]);
        message.push_back(_outMessage.Buffer.OutMessage[byteCount++]);
        message.push_back(_outMessage.Buffer.OutMessage[byteCount++]);

        msgCount++;
    }
}

unsigned char RDSTransmitter::getMessageCountFromBufSize(unsigned char size)
{
    // 4 bytes to a message, but only 3 in the first
    return size/4 + 1;
}

void RDSTransmitter::addMessageSize(MessageStore &message)
{
    message.push_front(message.size());
}

void RDSTransmitter::addSequenceCounter(MessageStore &message)
{
    message.push_front(getSequenceCount());
}

void RDSTransmitter::addMessageAddressing(MessageStore &message)
{
    // remember, built up backwords
    message.push_front(getEncoderAddress() | (getSiteAddress() << 6));
    message.push_front(getSiteAddress() >> 2);
}

void RDSTransmitter::addMessageCRC(MessageStore &message)
{


    unsigned int crc = crc16_ccitt(message);
    message.push_back(crc >> 8);
    message.push_back(crc);
}

void RDSTransmitter::replaceReservedBytes(MessageStore &message)
{
    //From Doc:
    //Byte = Resultant byte pair
    //0xFD = 0xFD 00
    //0xFE = 0xFD 01
    //0xFF = 0xFD 02

    for(MessageStore::iterator iter = message.begin(); iter != message.end(); ++iter)
    {
        if(*iter == UECPStart)
        {
            iter = message.erase(iter);
            message.insert(iter, UECPEscape);
            iter = message.insert(iter, 0x01);
        }
        else if(*iter == UECPEnd)
        {
            iter = message.erase(iter);
            message.insert(iter, UECPEscape);
            iter = message.insert(iter, 0x02);
        }
        else if(*iter == UECPEscape)
        {
            iter = message.insert(++iter, 0x00);
        }
    }
}

void RDSTransmitter::addStartStopBytes(MessageStore &message)
{
    message.push_front(UECPStart);
    message.push_back(UECPEnd);
}

unsigned short RDSTransmitter::getSiteAddress()
{
    return _rdsTable.getSiteAddress();
}

unsigned char RDSTransmitter::getEncoderAddress()
{
    return _rdsTable.getEncoderAddress();
}

unsigned char RDSTransmitter::getSequenceCount()
{
    // 0 is allowed if we have no intention of repeating our messages to the encoder
    // If we need this for reliability reasons, it can be implemented here.
    return 0;
}

// The transmitted group type code is not the "friendly" group type code.
// This should return a number in the range of 0x07 to 0x1B
//3B 0 0 1 1 1 Open Data Applications
//4A 0 1 0 0 0 Clock Time and Date only
//4B 0 1 0 0 1 Open Data Applications
//5A 0 1 0 1 0 Transparent Data Channels (32 channels) or ODA
//5B 0 1 0 1 1 Transparent Data Channels (32
//6A 0 1 1 0 0 In-House Applications or ODA
//6B 0 1 1 0 1 In-House Applications or ODA
//7A 0 1 1 1 0 Y Radio Paging or ODA
//7B 0 1 1 1 1 Open Data Applications

unsigned char RDSTransmitter::getGroupTypeCode()
{
    return _rdsTable.getGroupID();
}

float RDSTransmitter::getGroupsPerSecond()
{
    return _rdsTable.getGroupRate();
}

void RDSTransmitter::copyMessageToXfer(CtiXfer &xfer, MessageStore &message)
{
    xfer.setOutBuffer(_outBuffer);
    xfer.setInBuffer(_inBuffer);
    xfer.setInCountActual(&_inCountActual);

    xfer.setOutCount(message.size());
    xfer.setInCountExpected(isTwoWay() ? UECPResponseLen : 0);
    
    int i = 0;
    for each( const unsigned char element in message )
    {
        _outBuffer[i] = element;
        i++;
    }
}

INT RDSTransmitter::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, list< CtiMessage* > &vgList, list< CtiMessage* > &retList, list< OUTMESS* > &outList)
{
    INT nRet = NORMAL;
    /*
     *  This method should only be called by the dev_base method
     *   ExecuteRequest(CtiReturnMsg*, INT ScanPriority)
     *   (NOTE THE DIFFERENCE IN ARGS)
     *   That method prepares an outmessage for submission to the internals..
     */

    string resultString;
    CtiReturnMsg *pReturnMsg = CTIDBG_new CtiReturnMsg(getID(),
                                            string(OutMessage->Request.CommandStr),
                                            string(),
                                            nRet,
                                            OutMessage->Request.RouteID,
                                            OutMessage->Request.MacroOffset,
                                            OutMessage->Request.Attempt,
                                            OutMessage->Request.GrpMsgID,
                                            OutMessage->Request.UserID,
                                            OutMessage->Request.SOE,
                                            CtiMultiMsg_vec());

    switch(parse.getCommand())
    {
    case PutValueRequest:
    {
        if( parse.isKeyValid("asciiraw") && gConfigParms.isTrue("ALLOW_RAW_PAGE_MESSAGES") )
        {
            string outputValue = parse.getsValue("asciiraw");
            strcpy_s((char *)OutMessage->Buffer.OutMessage, 300, outputValue.c_str());
            OutMessage->OutLength = outputValue.size();
            OutMessage->DeviceID    = getID();
            OutMessage->TargetID    = getID();
            OutMessage->Port        = getPortID();
            OutMessage->InLength    = 0;
            OutMessage->Source      = 0;
            OutMessage->Retry       = 2;

            resultString = "Device: " + getName() + " -- Raw ASCII Command sent \n\"" + outputValue + "\"";

            outList.push_back(OutMessage);
            OutMessage = NULL;
            break;
        }
        //else fall through!
    }
    case ControlRequest:
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "**** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
        }
    case GetStatusRequest:
    case LoopbackRequest:
    case GetValueRequest:
    case PutStatusRequest:
    case GetConfigRequest:
    case PutConfigRequest:
    default:
        {
            nRet = NoExecuteRequestMethod;
            /* Set the error value in the base class. */
            // FIX FIX FIX 092999
            resultString = "RDS Devices do not support this command (yet?)";

            if(OutMessage)                // And get rid of our memory....
            {
                delete OutMessage;
                OutMessage = NULL;
            }
            break;
        }
    }

    if(pReturnMsg != NULL)
    {
        pReturnMsg->setResultString(resultString);
        pReturnMsg->setStatus(nRet);
        retList.push_back(pReturnMsg);
    }

    return nRet;
}

int RDSTransmitter::sendCommResult(INMESS *InMessage)
{
    // We are not interested in changing this return value here!
    // Must override base as we have no protocol.
    return NoError;
}

bool RDSTransmitter::isTransactionComplete()
{
    return _command == Complete;
}

void RDSTransmitter::resetStates()
{
    _currentState = StateSendRequestedMessage;
    _previousState = StateSendRequestedMessage;
}

void RDSTransmitter::printAcknowledgmentError(unsigned char error)
{
    string errorText = "RDS reported error: ";
    switch(error)
    {
        case 1: 
        {
            errorText += "CRC error has occurred: Message is wrong";
            break;
        }
        case 2: 
        {
            errorText += "Message was not received (derived from the sequence counter)";
            break;
        }
        case 3: 
        {
            errorText += "Message unknown";
            break;
        }
        case 4: 
        {
            errorText += "DSN error";
            break;
        }
        case 5: 
        {
            errorText += "PSN error";
            break;
        }
        case 6: 
        {
            errorText += "Parameter out of range";
            break;
        }
        case 7: 
        {
            errorText += "Message element length error";
            break;
        }
        case 8: 
        {
            errorText += "Message field length error";
            break;
        }
        case 9: 
        {
            errorText += "Message not acceptable";
            break;
        }
        case 10: 
        {
            errorText += "End message (0xFF) missing";
            break;
        }
        case 11: 
        {
            errorText += "Buffer overflow";
            break;
        }
        case 12: 
        {
            errorText += "Bad stuffing, after 0xFD a number outside the range 00 to 02 has been received";
            break;
        }
        case 13: 
        {
            errorText += "Unexpected end of message (0xFF) received";
            break;
        }
        case 14: 
        {
            errorText += "Message received OK, but not interpreted";
            break;
        }
        default:
        {
            errorText += "Undefined";
            break;
        }
    }

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " " << errorText << endl;
    }
}

// Copied directly from UECP spec. This is a CCITT CRC initialized with 0xFFFF and inverted at the end.
unsigned int RDSTransmitter::crc16_ccitt (const MessageStore &message)
{
    unsigned crc=0xFFFF;

    for each( unsigned char element in message )
    {
        crc = (unsigned char)(crc >> 8) | (crc << 8);
        crc ^= element;
        crc ^= (unsigned char)(crc & 0xff) >> 4;
        crc ^= (crc << 8) << 4;
        crc ^= ((crc & 0xff) << 4) << 1;
    }
    return ((crc ^= 0xFFFF) & 0xFFFF);
}

//Database Functions
string RDSTransmitter::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                     "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.portid, "
                                     "DUS.phonenumber, DUS.minconnecttime, DUS.maxconnecttime, DUS.linesettings, "
                                     "DUS.baudrate, RDS.siteaddress, RDS.encoderaddress, RDS.groupid, RDS.transmitspeed "
                                   "FROM Device DV, RDSTransmitter RDS, DeviceDirectCommSettings CS, "
                                     "YukonPAObject YP LEFT OUTER JOIN DeviceDialupSettings DUS ON "
                                     "YP.paobjectid = DUS.deviceid "
                                   "WHERE YP.paobjectid = RDS.paobjectid AND YP.paobjectid = DV.deviceid AND "
                                     "YP.paobjectid = CS.deviceid";

    return sqlCore;
}

void RDSTransmitter::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Decoding " << __FILE__ << " (" << __LINE__ << ")" << endl;
    }

    _rdsTable.DecodeDatabaseReader(rdr);
}

bool RDSTransmitter::isPacketValid(const unsigned char *buf, const size_t len)
{
    if( len < UECPMinLen )
    {
        return false;
    }

    if( buf[0]     != UECPStart ||
        buf[len-1] != UECPEnd )
    {
        return false;
    }

    // Possible ToDo, check CRC. Others do this. I am not convinced it is necessary.

    return true;
}

LONG RDSTransmitter::getAddress() const
{
    return ((long)_rdsTable.getSiteAddress() << 6) | _rdsTable.getEncoderAddress();
}


}
}
