#pragma once
#pragma warning( disable : 4786)

#include "dev_remote.h"

namespace Cti       {
namespace Devices    {

//template<class T>
//class std::list<T>;

class IM_EX_DEVDB RDSTransmitter : public CtiDeviceRemote
{
public:

    RDSTransmitter();
    virtual ~RDSTransmitter();

    int recvCommRequest( OUTMESS *OutMessage );

    virtual int  generate(CtiXfer &xfer);
    virtual int  decode(CtiXfer &xfer, int status);
    virtual bool isTransactionComplete();

    virtual std::string getSQLCoreStatement() const;
    virtual void DecodeDatabaseReader(Cti::RowReader &rdr);
    virtual INT  ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    virtual LONG getAddress() const;

    static bool isPacketValid(const unsigned char *buf, const size_t len);

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
        StateCheckResponse
    };

    enum MessageElementCodes
    {
        CommunicationModeCode = 0x2C,
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

    void resetStates();
    unsigned char  getEncoderAddress() const;
    unsigned short getSiteAddress() const;
    unsigned char  getSequenceCount() const;
    unsigned char  getGroupTypeCode() const;
    float          getGroupsPerSecond() const;
    unsigned char  getMessageCountFromBufSize(unsigned char size) const;

    // Message to ask unit to give us immediate responses
    void createBiDirectionRequest   (MessageStore &message);
    void createCompletePackedMessage(MessageStore &message);

    void addMessageSize             (MessageStore &message);
    void addSequenceCounter         (MessageStore &message);
    void addMessageAddressing       (MessageStore &message);
    void addMessageCRC              (MessageStore &message);
    void replaceReservedBytes       (MessageStore &message);
    void addStartStopBytes          (MessageStore &message);

    void copyMessageToXfer          (CtiXfer &xfer, MessageStore &message);

    void printAcknowledgmentError   (unsigned char error);

    virtual int sendCommResult      (INMESS *InMessage);

    bool isTwoWay();
    void delay();

    unsigned int crc16_ccitt (const MessageStore &message);
};

}
}
