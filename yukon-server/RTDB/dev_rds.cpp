#include "precompiled.h"
#include "dev_rds.h"
#include "tbl_static_paoinfo.h"
#include "cparms.h"

using std::string;
using std::endl;
using std::list;

namespace Cti       {
namespace Devices    {

static const unsigned char UECPStart  = 0xFE;
static const unsigned char UECPEnd    = 0xFF;
static const unsigned char UECPEscape = 0xFD;

static const unsigned short CooperAID = 0xC549;

static const unsigned char UECPMinLen = 8; // Start/stop(2), crc(2), addressing(2), Sequence(1), length(1)
static const unsigned char UECPResponseLen = 10;

RDSTransmitter::RDSTransmitter() :
_inCountActual(0),
_isBiDirectionSet(false),
_messageToggleFlag(false),
_command(Complete),
_repeatCount(0),
_remainingSleepDelay(0)
{
    resetStates(StateSendRequestedMessage);
}

YukonError_t RDSTransmitter::recvCommRequest( OUTMESS *OutMessage )
{
    YukonError_t retVal = ClientErrors::None;

    if( OutMessage )
    {
        _outMessage = *OutMessage;
        if ( OutMessage->MessageFlags & MessageFlag_EncryptionRequired )    // One-Way Encryption
        {
            _outMessage.MessageFlags &= ~MessageFlag_EncryptionRequired;
            _outMessage.Buffer.TAPSt.Length = encryptMessage( CtiTime::now(),
                                                              OutMessage->Buffer.TAPSt.Message,
                                                              OutMessage->Buffer.TAPSt.Length,
                                                              _outMessage.Buffer.TAPSt.Message,
                                                              OneWayMsgEncryption::Binary );
            _outMessage.OutLength = _outMessage.Buffer.TAPSt.Length;
        }

        // do we have a AppID outmessage?

        std::string command( _outMessage.Request.CommandStr );

        if( containsString(command, "putvalue application-id") )
        {
            _repeatCount = 0;
            _command = Complete;    // ignore this if we've sent an AppID message withing the last getAIDRepeatRate() time.
            if ( timeToPerformPeriodicAction( CtiTime::now() ) )
            {
                _command = Normal;
                resetStates(StateSendAIDMessage);
            }
        }
        else
        {
            _command = Normal;
            _repeatCount = gConfigParms.getValueAsInt("RDS_REPEAT_COUNT", 0); // Reset this on every new transmission
            resetStates(StateSendRequestedMessage);
        }
    }
    else
    {
        CTILOG_ERROR(dout, "NULL OutMessage");

        retVal = ClientErrors::Memory;
    }

    return retVal;
}

bool RDSTransmitter::isTwoWay()
{
    // Not expected to be set, not supported by UDP. Here for emergency serial use only.
    return gConfigParms.isTrue("RDS_USE_TWO_WAY_MESSAGING");
}

void RDSTransmitter::buildRDSMessage(const StateMachine &m, MessageStore &msg)
{
    switch(m)
    {
        case StateSendAIDMessage:
        {
            createPeriodicAIDMessage(msg);
            _lastPeriodicActionTime += getAIDRepeatRate();
            if ( ( _lastPeriodicActionTime < ( CtiTime::now() - getAIDRepeatRate() ) ) ||   // catch up if way behind...
                 ( _lastPeriodicActionTime > CtiTime::now() ) )                             // but don't go into the future...
            {
                _lastPeriodicActionTime = CtiTime::now();
            }
            break;
        }
        case StateSendBiDirectionalRequest:
        {
            createBiDirectionRequest(msg);
            break;
        }
        case StateSendRequestedMessage:
        {
            // Note that due to this we need to ensure we have set up the encryption or other before this so the repeat is identical!
            createRequestedMessage(msg);
            break;
        }
    }

    addMessageSize(msg);
    addSequenceCounter(msg);
    addMessageAddressing(msg);
    addUECPCRC(msg);
    replaceReservedBytes(msg);
    addStartStopBytes(msg);
}

YukonError_t RDSTransmitter::generate(CtiXfer &xfer)
{
    MessageStore newMessage;

    if(isTwoWay() && !_isBiDirectionSet)
    {
        _currentState = StateSendBiDirectionalRequest;
    }

    if ( _remainingSleepDelay == 0 ) // received an outMessage based message
    {
        buildRDSMessage(_currentState, newMessage);
        if ( _currentState != StateSendAIDMessage )     // no delay for AppID messages
        {
            _remainingSleepDelay = calculateSleepDelay();
        }
    }
    else    // send AppID from 'interrupted' sleep...
    {
        buildRDSMessage(StateSendAIDMessage, newMessage);
    }

    copyMessageToXfer(xfer, newMessage);

    return ClientErrors::None;
}

YukonError_t RDSTransmitter::decode(CtiXfer &xfer, YukonError_t status)
{
    status = ClientErrors::None;

    if(isTwoWay() && xfer.getInCountActual() < UECPResponseLen)
    {
        _command = Complete; //Transaction Complete
        status = ClientErrors::Abnormal;
        _isBiDirectionSet = false;
    }
    else
    {
        // Check if we are ok, then continue? somehow.
        if(!isTwoWay() || xfer.getInBuffer()[6] == 0)
        {
            //OK!
            status = ClientErrors::None;
            if(_previousState == StateSendBiDirectionalRequest)
            {
                _isBiDirectionSet = true;
                _currentState = StateSendRequestedMessage;
            }
            else if(_previousState == StateSendRequestedMessage ||
                    _previousState == StateSendAIDMessage)
            {
                unsigned sleepDelay = 0;

                if ( getAIDRepeatRate() == 0 )  // no periodic AppID - wait whole time
                {
                    sleepDelay = _remainingSleepDelay;
                    _remainingSleepDelay = 0;
                }
                else
                {
                    unsigned delta = 0;
                    if ( _lastPeriodicActionTime.seconds() + getAIDRepeatRate() > CtiTime::now().seconds() )
                    {
                        delta = _lastPeriodicActionTime.seconds() + getAIDRepeatRate() - CtiTime::now().seconds();
                    }
                    sleepDelay = std::min( _remainingSleepDelay, 1000 * std::min( delta, getAIDRepeatRate() ) );
                    _remainingSleepDelay -= sleepDelay;
                }

                Sleep(sleepDelay);

                if ( _remainingSleepDelay == 0 )
                {
                    if(_repeatCount > 0)
                    {
                        _repeatCount--;
                    }
                    if ( _repeatCount == 0 )
                    {
                        _command = Complete; //Transaction Complete
                    }
                }
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
            status = ClientErrors::Abnormal;
        }
    }

    return status;
}

unsigned RDSTransmitter::calculateSleepDelay()  // time in milliseconds!!
{
    unsigned delay = 0;

    const int totalGroups = getMessageCountFromBufSize(_outMessage.OutLength);
    if(getGroupsPerSecond() > 0)
    {
        delay = (1000.0 * totalGroups) / getGroupsPerSecond();
    }
    else
    {
        CTILOG_ERROR(dout, "Invalid groups per second value");
    }
    return delay;
}

void RDSTransmitter::delay()
{
    const int totalGroups = getMessageCountFromBufSize(_outMessage.OutLength);
    if(getGroupsPerSecond() > 0)
    {
        Sleep(1000*totalGroups/getGroupsPerSecond());
    }
    else
    {
        CTILOG_ERROR(dout, "Invalid groups per second value");
    }
}

void RDSTransmitter::createBiDirectionRequest(MessageStore &message)
{
    message.push_back(CommunicationModeCode);
    message.push_back(0x02); // means bi-directional mode with spontaneous response (see Section 1.3.3); the sequence counter is set to 0.
}

// Moves OutMessage to the UECP formatted message.
// Will copy up to 3 0's at the end of the message to fit 4 byte blocks.
void RDSTransmitter::createRequestedMessage(MessageStore &message)
{
    MessageStore frame; // These will hold the bytes that we want to send.

    buildRDSFrameFromOutMessage(frame);
    addFrameToUECPMessage(message, frame);
}

void RDSTransmitter::buildRDSFrameFromOutMessage(MessageStore &frame)
{
    frame.push_back(_outMessage.OutLength);

    for(int i = 0; i < _outMessage.OutLength; i++)
    {
        frame.push_back(_outMessage.Buffer.OutMessage[i]);
    }

    addCooperCRC(frame);
}

// There are assumptions being made here about the frames fitting into RDS's 4 byte blocks.
void RDSTransmitter::addFrameToUECPMessage(MessageStore &message, MessageStore &frame)
{
    // This is a bit odd, but it simplifies the loops. Pad the frames with 0's so they have even 4 byte blocks.
    while(frame.size() % 4)
    {
        frame.push_back(0);
    }

    unsigned int msgCount = 0;

    while( frame.size() > 0)
    {
        message.push_back(ODAFreeFormat);
        message.push_back(getGroupTypeCode());
        message.push_back(0);   //Priority Normal, Normal Mode, No Re-Sending
        message.push_back(msgCount++);// The Message Sequence Block tells the device where in the message the current frame segments are to be placed.
                                    //There are 32 Sequence blocks meaning there can be 127 bytes in a single frame.
        for(int i=0; i<4; i++) // lets be clear we are doing this 4 times per message.
        {
            message.push_back(frame.front());
            frame.pop_front();
        }
    }
}

unsigned char RDSTransmitter::getMessageCountFromBufSize(unsigned char size) const
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

void RDSTransmitter::addUECPCRC(MessageStore &message)
{
    unsigned int crc = uecp_crc(message);
    message.push_back(crc >> 8);
    message.push_back(crc);
}

void RDSTransmitter::addCooperCRC(MessageStore &message)
{
    unsigned int crc = cooper_crc(message);
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

unsigned short RDSTransmitter::getSiteAddress() const
{
    return getStaticInfo(CtiTableStaticPaoInfo::Key_RDS_Site_Address);
}

unsigned char RDSTransmitter::getEncoderAddress() const
{
    return getStaticInfo(CtiTableStaticPaoInfo::Key_RDS_Encoder_Address);
}

unsigned char RDSTransmitter::getSequenceCount() const
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

unsigned char RDSTransmitter::getGroupTypeCode() const
{
    // Convert string (11A) to integer ((Number(11) << 1) | A = 0, B =  1)
    std::string groupType;
    unsigned char retVal = 0;

    if( getStaticInfo(CtiTableStaticPaoInfo::Key_RDS_Group_Type, groupType) )
    {
        retVal = (atoi(groupType.c_str())) << 1;

        if (icontainsString(groupType, "b"))
        {
            retVal |= 1;
        }
    }

    return retVal;
}

float RDSTransmitter::getGroupsPerSecond() const
{
    double retVal = 0;
    getStaticInfo(CtiTableStaticPaoInfo::Key_RDS_Transmit_Speed, retVal);
    return retVal;
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

YukonError_t RDSTransmitter::ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList)
{
    YukonError_t nRet = ClientErrors::None;
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
                                            OutMessage->Request.RetryMacroOffset,
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
        else if( parse.isKeyValid("hexraw") && gConfigParms.isTrue("ALLOW_RAW_PAGE_MESSAGES") )
        {
            std::string outputValue = parse.getsValue("hexraw");
            if( (outputValue.size()%2) != 0 )
            {
                outputValue.append("0");
            }
            for(int i = 0; i < outputValue.size()/2; i++)
            {
                OutMessage->Buffer.OutMessage[i] = strtoul(outputValue.substr(i*2,2).c_str(), NULL, 16);
            }
            OutMessage->OutLength = outputValue.size()/2;
            OutMessage->DeviceID    = getID();
            OutMessage->TargetID    = getID();
            OutMessage->Port        = getPortID();
            OutMessage->InLength    = 0;
            OutMessage->Source      = 0;
            OutMessage->Retry       = 2;

            resultString = "Device: " + getName() + " -- Raw hex Command sent \n\"" + (string)outputValue + "\"";

            outList.push_back(OutMessage);
            OutMessage = NULL;
            break;
        }
        if( parse.isKeyValid("application-id") )
        {
            OutMessage->OutLength   = 0;
            OutMessage->DeviceID    = getID();
            OutMessage->TargetID    = getID();
            OutMessage->Port        = getPortID();
            OutMessage->InLength    = 0;
            OutMessage->Source      = 0;
            OutMessage->Retry       = 2;

            resultString = "Device: " + getName() + " -- Application ID sent";

            outList.push_back(OutMessage);
            OutMessage = NULL;
            break;
        }
        //else fall through!
    }
    case ControlRequest:
        {
            CTILOG_ERROR(dout, "Unexpected ControlRequest command");
        }
    case GetStatusRequest:
    case LoopbackRequest:
    case GetValueRequest:
    case PutStatusRequest:
    case GetConfigRequest:
    case PutConfigRequest:
    default:
        {
            nRet = ClientErrors::NoMethodForExecuteRequest;
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

YukonError_t RDSTransmitter::sendCommResult(INMESS &InMessage)
{
    // We are not interested in changing this return value here!
    // Must override base as we have no protocol.
    return ClientErrors::None;
}

bool RDSTransmitter::isTransactionComplete()
{
    return _command == Complete;
}

void RDSTransmitter::resetStates(const StateMachine &s)
{
    _currentState = _previousState = s;
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

    CTILOG_ERROR(dout, errorText);
}

// This is a CCITT CRC initialized with 0xFFFF and inverted at the end.
unsigned int RDSTransmitter::uecp_crc (const MessageStore &message)
{
    unsigned int crc = cooper_crc(message);
    return ((crc ^= 0xFFFF) & 0xFFFF);
}

// Copied mostly from UECP spec. This is a CCITT CRC initialized with 0xFFFF.
unsigned int RDSTransmitter::cooper_crc (const MessageStore &message)
{
    unsigned int crc=0xFFFF;

    for each( unsigned char element in message )
    {
        crc = (unsigned char)(crc >> 8) | (crc << 8);
        crc ^= element;
        crc ^= (unsigned char)(crc & 0xff) >> 4;
        crc ^= (crc << 8) << 4;
        crc ^= ((crc & 0xff) << 4) << 1;
    }
    return crc;
}

//Database Functions
string RDSTransmitter::getSQLCoreStatement() const
{
    static const string sqlCore =  "SELECT YP.paobjectid, YP.category, YP.paoclass, YP.paoname, YP.type, "
                                     "YP.disableflag, DV.deviceid, DV.alarminhibit, DV.controlinhibit, CS.portid, "
                                     "DUS.phonenumber, DUS.minconnecttime, DUS.maxconnecttime, DUS.linesettings, "
                                     "DUS.baudrate "
                                   "FROM Device DV, DeviceDirectCommSettings CS, "
                                     "YukonPAObject YP LEFT OUTER JOIN DeviceDialupSettings DUS ON "
                                     "YP.paobjectid = DUS.deviceid "
                                   "WHERE YP.paobjectid = DV.deviceid AND YP.paobjectid = CS.deviceid";

    return sqlCore;
}

void RDSTransmitter::DecodeDatabaseReader(Cti::RowReader &rdr)
{
    Inherited::DecodeDatabaseReader(rdr);       // get the base class handled

    if( getDebugLevel() & DEBUGLEVEL_DATABASE )
    {
        CTILOG_DEBUG(dout, "Decoding DB reader");
    }
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
    return ((long)getSiteAddress() << 6) | getEncoderAddress();
}

void RDSTransmitter::createPeriodicAIDMessage(MessageStore &message)
{
    message.push_back(TransparentFreeFormat);
    message.push_back(0x06);

    message.push_back(getGroupTypeCode());

    unsigned short spid = getStaticInfo(CtiTableStaticPaoInfo::Key_RDS_SPID);

    message.push_back(spid >> 8);
    message.push_back(spid);

    message.push_back(CooperAID >> 8);
    message.push_back(CooperAID);
}

bool RDSTransmitter::timeToPerformPeriodicAction(const CtiTime & currentTime)
{
    unsigned repeatRate = getAIDRepeatRate();

    return ( repeatRate > 0 && currentTime >= ( _lastPeriodicActionTime + repeatRate ) );
}

unsigned RDSTransmitter::getAIDRepeatRate()
{
    return getStaticInfo(CtiTableStaticPaoInfo::Key_RDS_AID_Repeat_Period);
}

}
}
