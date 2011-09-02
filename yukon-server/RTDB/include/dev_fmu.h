#pragma once

#include <queue>
#include "dev_ied.h"
#include "queue.h"
#include "verification_objects.h"

#define NAK_ACTION_RESEND_LAST_MSG 0x80
#define NAK_ACTION_RESET_SEQUENCE  0x40
#define NAK_INVALID_REQUEST        0x10
#define NAK_INVALID_DATA           0x08
#define NAK_INVALID_DATA_LENGTH    0x04
#define NAK_INVALID_SEQUENCE       0x02
#define NAK_INVALID_CMD            0x01

class IM_EX_DEVDB CtiDeviceFMU : public CtiDeviceIED
{
private:

    typedef CtiDeviceIED Inherited;
    typedef std::vector< std::string * > stringlist_t;

    CtiOutMessage _outbound;
    unsigned char _inbound[256];

    //ULONG _fmuAddress;
    USHORT _sequence;
    USHORT _cmd;
   
    enum States
    {
        State_Uninit,
        State_Ack_Continue,
        State_Ack_Complete,
        State_Do_Nothing,
        State_Request_Data,
        State_Read_Data,
        State_Read_More,
        State_Read_Time,
        State_Send_LoCom_Command,
        State_Send_Direct_Command,
        State_Send_Reset_Log,
        State_Send_Time_Sync,
        State_Complete
    } _state;

    States _prevState;

    enum
    {
        SequenceStart = 0,
        SequenceEnd = 0x3F,
        SequenceField = 0x3F,
        SequenceFlagStart = 0x80,
        SequenceFlagEnd = 0x40,
        SequenceUnknown = 255
    };

    enum MessageTypes
    {
        AckResponse         = 0x00,
        NakResponse         = 0x01,
        CommSync            = 0x02,
        Unsolicited         = 0x03,
        ResetLog            = 0x04,
        TimeSend            = 0x05,
        TimeRead            = 0x06,
        TimeResponse        = 0x07,
        DataRequest         = 0x08,
        DataResponse        = 0x09,
        LoComCommand       = 0x0C,
        LoCommResponse      = 0x0D,
        ExternalDevCommand  = 0x0E,
        ExternalDevResponse = 0x0F
    };

    enum
    {
        DawnOfTime = 0x386d4380,  //  jan 1, 2000, in UTC seconds
    };

    unsigned short _error_count;
    unsigned long  _in_expected, _in_actual, _in_remaining;
    int _code_len, _codes_received;
    bool _endOfTransactionFlag;
    std::string _rawData;
    stringlist_t _stringList;

    bool checkMessageCRC();
    void decodeDataRead();
    void decodeGenericResponse();
    void nakDecode();
    void setupHeader(CtiXfer &xfer, UCHAR command, int &length, UCHAR flags);
    void setupLoComCommand(CtiXfer &xfer);
    void setupExternalDevCommand(CtiXfer &xfer);
    void setupReadMore(CtiXfer &xfer);
    void setupResetSequenceMessage(CtiXfer &xfer);
    void setupRequestDataMessage(CtiXfer &xfer);
    void setupResetLogMessage(CtiXfer &xfer);
    void setupTimeSyncMessage(CtiXfer &xfer);
    void setupTimeReadMessage(CtiXfer &xfer);

    unsigned short crc16(const unsigned char *data, int dataLen);
    static const unsigned short crc16table[256];

    enum
    {
        MaxErrors = 3
    };

    std::queue< CtiVerificationBase * > _verification_objects;

protected:

public:
    INT _cmd_len;

    CtiDeviceFMU();
    ~CtiDeviceFMU();
    virtual LONG getAddress() const;

    INT ExecuteRequest(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList, INT ScanPriority = MAXPRIORITY - 4);

    INT ResultDecode(INMESS *InMessage, CtiTime &TimeNow, std::list< CtiMessage* > &vgList, std::list< CtiMessage* > &retList, std::list< OUTMESS* > &outList);
    INT ErrorDecode (const INMESS &InMessage, const CtiTime TimeNow, std::list< CtiMessage* > &retList);

    int recvCommRequest(OUTMESS *OutMessage);
    int sendCommResult(INMESS *InMessage);

    int generate(CtiXfer &xfer);
    int decode(CtiXfer &xfer, int status);

    void getVerificationObjects(std::queue<CtiVerificationBase *> &work_queue);

    bool isTransactionComplete();

};
