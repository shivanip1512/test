#pragma once

#include "dev_remote.h"
#include "encryption_oneway_message.h"

namespace Cti       {
namespace Devices    {

//template<class T>
//class std::list<T>;

class IM_EX_DEVDB RDSTransmitter : public CtiDeviceRemote, protected OneWayMsgEncryption
{
public:

    RDSTransmitter();

    YukonError_t recvCommRequest( OUTMESS *OutMessage ) override;

    virtual YukonError_t generate(CtiXfer &xfer);
    virtual YukonError_t decode  (CtiXfer &xfer, YukonError_t status);
    virtual bool isTransactionComplete();

    virtual std::string getSQLCoreStatement() const;
    void DecodeDatabaseReader(Cti::RowReader &rdr) override;
    YukonError_t ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList) override;
    virtual LONG getAddress() const;

    static bool isPacketValid(const unsigned char *buf, const size_t len);

    virtual bool timeToPerformPeriodicAction(const CtiTime & currentTime);

protected:

    typedef CtiDeviceRemote Inherited;
    typedef std::list <unsigned char> MessageStore;

    enum CommandState
    {
        Normal=0,
        Complete
    };

    enum StateMachine
    {
        StateSendBiDirectionalRequest,
        StateSendRequestedMessage,
        StateSendODAConfig,
        StateCheckResponse,
        StateSendAIDMessage
    };

    enum MessageElementCodes
    {
        TransparentFreeFormat = 0x24,
        CommunicationModeCode = 0x2C,
        ODAConfiguration      = 0x40,
        ODAFreeFormat         = 0x42,
    };

    OUTMESS               _outMessage;
    CommandState          _command;
    StateMachine          _previousState;
    StateMachine          _currentState;
    BYTE                  _outBuffer[255];
    BYTE                  _inBuffer[255];
    ULONG                 _inCountActual;
    bool                  _isBiDirectionSet;
    bool                  _messageToggleFlag;
    bool                  _twoWay;
    unsigned int          _repeatCount;
    CtiTime               _lastPeriodicActionTime;
    unsigned              _remainingSleepDelay;

    void resetStates(const StateMachine &s);

    unsigned char  getEncoderAddress() const;
    unsigned short getSiteAddress() const;
    unsigned char  getSequenceCount() const;
    unsigned char  getGroupTypeCode() const;
    float          getGroupsPerSecond() const;
    unsigned char  getMessageCountFromBufSize(unsigned char size) const;

    // Message to ask unit to give us immediate responses
    void createBiDirectionRequest     (MessageStore &message);
    void createRequestedMessage       (MessageStore &message);
    void createPeriodicAIDMessage     (MessageStore &message);

    void addMessageSize             (MessageStore &message);
    void addSequenceCounter         (MessageStore &message);
    void addMessageAddressing       (MessageStore &message);
    void addUECPCRC                 (MessageStore &message);
    void addCooperCRC               (MessageStore &message);
    void replaceReservedBytes       (MessageStore &message);
    void addStartStopBytes          (MessageStore &message);

    void buildRDSFrameFromOutMessage(MessageStore &frame);
    void addFrameToUECPMessage(MessageStore &message, MessageStore &frame);

    void buildRDSMessage(const StateMachine &m, MessageStore &msg);

    void copyMessageToXfer          (CtiXfer &xfer, MessageStore &message);

    void printAcknowledgmentError   (unsigned char error);

    YukonError_t sendCommResult(INMESS &InMessage) override;

    bool isTwoWay();
    bool isOdaConfigSendNeeded();
    void delay();

    unsigned calculateSleepDelay();

    unsigned getAIDRepeatRate();

    unsigned int uecp_crc   (const MessageStore &message);
    unsigned int cooper_crc (const MessageStore &message);
};

}
}
