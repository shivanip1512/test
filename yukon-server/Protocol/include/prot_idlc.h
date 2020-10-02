#pragma once

#include "dsm2.h"
#include "xfer.h"

#include "prot_wrap.h"

namespace Cti {
namespace Protocols {

class IM_EX_PROT IDLC : public Wrap
{
    unsigned char _address;

#pragma pack(push, 1)

    union control_code
    {
        unsigned char code;
        struct
        {
            unsigned char unsequenced       : 1;  //  byte 0 bit 0
            unsigned char request_sequence  : 3;  //  byte 0 bits 1-3
            unsigned char final_frame       : 1;  //  byte 0 bit 4
            unsigned char response_sequence : 3;  //  byte 0 bits 5-7
        };
    };

    //  3 bytes
    struct frame_header
    {
        unsigned char flag;             //  byte 0
        unsigned char direction : 1;    //  byte 1 bit 0
        unsigned char address   : 7;    //  byte 1 bits 1-7
        control_code  control;          //  byte 2
    };

    union frame
    {
        struct
        {
            frame_header  header;     //  bytes 0-2
            unsigned char data[258];  //  bytes 3-260
        };
        unsigned char raw[261];
    };

#pragma pack(pop)

    enum IOOperations
    {
        IO_Operation_Output,      //  normal output
        IO_Operation_Input,       //  input
        IO_Operation_Complete,    //  transaction complete
        IO_Operation_Failed,      //  transaction failed
        IO_Operation_Invalid,     //  catch-all error condition for initialization errors
    } _io_operation;

    enum ControlStates
    {
        Control_State_OK,
        Control_State_ResetSend,  //  reset request from master to CCU
        Control_State_ResetRecv,  //  reset request from master to CCU
        Control_State_Retransmit, //  retransmit request from master to CCU
    } _control_state;

    enum ControlCodes
    {
        ControlCode_ResetRequest      = 0x1f,
        ControlCode_ResetAcknowledge  = 0x73,
        ControlCode_RejectWithRestart = 0x19,  //  upper 3 bits are the sequence number of the next frame expected
        ControlCode_RetransmitRequest = 0x1d,  //  upper 3 bits are the sequence number of the frame to retransmit
        ControlCode_Unsequenced       = 0x13,
    };

    enum Misc
    {
        FramingFlag = 0x7e,
    };

    //  outbound-specific
    std::vector<unsigned char> _out_data;
    unsigned long _out_sent;
    unsigned char _master_sequence;
    frame _out_frame;

    //  inbound-specific
    unsigned char _in_data_length;
    std::vector<unsigned char> _in_data;
    unsigned long _in_expected;
    unsigned long _in_actual;
    unsigned long _in_recv;
    unsigned char _slave_sequence;
    unsigned int  _framing_seek_length;
    frame _in_frame;

    int _comm_errors;
    int _protocol_errors;
    int _input_loops;

    enum ControlResult
    {
        ControlResult_Retransmit
    };

    bool process_inbound( CtiXfer &xfer, YukonError_t status );  //  returns completeFrame & crcValid & direction
    YukonError_t process_control( frame in_frame );

    YukonError_t generate_control( CtiXfer &xfer );
    YukonError_t decode_control( CtiXfer &xfer, YukonError_t status );

    bool control_pending( void );

    void sendFrame     (CtiXfer &xfer);
    void sendRetransmit(CtiXfer &xfer);
    void sendReset     (CtiXfer &xfer);
    void recvFrame     (CtiXfer &xfer, unsigned bytes_received);

    unsigned calcFrameLength(const frame &f);

    unsigned alignFrame(CtiXfer &xfer, frame &f, unsigned long &aligned_inbound_count);

    bool isCRCValid     (const frame &f);
    bool isControlFrame (const frame &f);
    bool isCompleteFrame(const frame &f, unsigned bytes_received);

    bool setOutData(const std::vector<unsigned char> &buf);

    enum IDLCFrameEnum
    {
        Frame_MaximumDataLength         = 255,
        Frame_MinimumLength             =   5,
        Frame_ControlPacketLength       =   5,
        Frame_DataPacket_OverheadLength =   6,  //  header is 3, CRC is 2
    };

    enum MiscNumeric
    {
        //  these errors are all set to 1 so we immediately return control to the Klondike protocol on any error
        //    this was mainly done so the timesyncs will be refreshed on any error - even an IDLC reset.
        MaximumCommErrors        =   1,
        MaximumInputLoops        =   1,
        MaximumProtocolErrors    =   1,
        MaximumFramingSeekLength = 260,
    };

protected:

    void clearSequenceNumbers( Test::use_in_unit_tests_only & );

public:

    IDLC();
    virtual ~IDLC();

    void setAddress( unsigned short address );

    YukonError_t generate( CtiXfer &xfer );
    YukonError_t decode  ( CtiXfer &xfer, YukonError_t status );

    virtual bool isTransactionComplete( void ) const;
    virtual bool errorCondition( void ) const;

    bool send( const std::vector<unsigned char> &buf );
    bool recv( void );

    bool init( void );

    void getInboundData( std::vector<unsigned char> &buf );

    unsigned getMaximumPayload( void ) const;

    enum IDLCError
    {
        Error_NoError        = 0,
        Error_BadFraming,
        Error_BadCRC,
        Error_BadAddress,
        Error_BadLength,
        Error_UnknownMessage
    };
};

}
}

