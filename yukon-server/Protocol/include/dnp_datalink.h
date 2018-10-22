#pragma once

#include "xfer.h"

#include "dnp_datalink_packet.h"

namespace Cti::Protocols::DNP {

class DatalinkLayer
{
private:

    enum PacketDetails
    {
        Packet_MaxPayloadLen      = DatalinkPacket::DataLengthMax - (DatalinkPacket::BlockCountMax * DatalinkPacket::CRCLength),
        Packet_HeaderLen          = DatalinkPacket::HeaderLength,
        Packet_HeaderLenUncounted =   5,  //  the number of bytes in the header that are counted (i.e. only those after the len octet)
        Packet_FramingLen         = DatalinkPacket::FramingLength,
        Packet_CRCLen             = DatalinkPacket::CRCLength,
    };

    enum IOState
    {
        State_IO_Uninitialized,
        State_IO_Output,
        State_IO_OutputRecvAck,
        State_IO_Input,
        State_IO_InputSendAck,
        State_IO_Complete,
        State_IO_Failed
    } _io_state;

    YukonError_t _errorCondition;

    enum ControlState
    {
        State_Control_Ready,
        State_Control_Request_DLReset_Out,
        State_Control_Request_DLReset_In,
        State_Control_Request_LinkStatus_Out,
        State_Control_Request_LinkStatus_In,
        State_Control_Reply_Ack,
        State_Control_Reply_Nack,
        State_Control_Reply_NonFCBAck,
        State_Control_Reply_LinkStatus,
        State_Control_Reply_ResetLink
    } _control_state;

    //  initial config data
    unsigned short _src, _dst;

    bool _dl_confirm;
    bool _slave_response;

    //  datalink control statuses
    bool _send_confirm;
    bool _reset_sent;
    bool _fcb_out, _fcb_in;

    int _comm_errors;
    int _protocol_errors;

    //  outbound-specific
    unsigned long _out_sent;
    unsigned char _out_data[Packet_MaxPayloadLen];
    unsigned long _out_data_len;

    //  inbound-specific
    unsigned long _in_recv;
    unsigned long _in_expected;
    unsigned long _in_actual;
    unsigned char _in_data[Packet_MaxPayloadLen];
    int           _in_data_len;

    typedef DatalinkPacket::dl_packet packet_t;

    packet_t _packet, _control_packet;

    enum PrimaryControlFunction;
    void constructDataPacket( packet_t &p, unsigned char *buf, unsigned long len );

    bool isControlPending( void ) const;
    bool processControl( const packet_t &p );
    YukonError_t generateControl( CtiXfer &xfer );
    YukonError_t decodeControl  ( CtiXfer &xfer, YukonError_t status );
    int  decodePacket   ( CtiXfer &xfer, packet_t &p, unsigned long received );

    packet_t constructPrimaryControlPacket( PrimaryControlFunction function, bool fcv, bool fcb );

    void sendPacket( packet_t &packet, CtiXfer &xfer );
    void recvPacket( packet_t &packet, CtiXfer &xfer );

    static bool isValidAckPacket ( const packet_t &p );

    static bool isHeaderCRCValid( const DatalinkPacket::dlp_header &header );
    static bool isDataBlockCRCValid( const unsigned char *block, unsigned length );
    static bool arePacketCRCsValid( const packet_t &packet );

    enum MiscNumeric
    {
        CommRetryCount     =   3,
        ProtocolRetryCount =   3
    };

public:

    struct Slave {};

    DatalinkLayer();
    DatalinkLayer(const Slave);

    DatalinkLayer(const DatalinkLayer &) = delete;
    DatalinkLayer &operator=(const DatalinkLayer &) = delete;

    void setAddresses( unsigned short dst, unsigned short src);
    unsigned short getSrcAddress() const;
    unsigned short getDstAddress() const;
    void setDatalinkConfirm();

    void setToLoopback();

    void setToOutput( unsigned char *buf, unsigned int len );
    void setToInput ( void );

    YukonError_t generate( CtiXfer &xfer );
    YukonError_t decode  ( CtiXfer &xfer, YukonError_t status );

    bool isTransactionComplete( void );
    YukonError_t errorCondition( void );

    std::vector<unsigned char> getInPayload() const;

    static void putPacketPayload( const packet_t &p, unsigned char *buf, int *len );

    IM_EX_PROT static bool isPacketValid(const unsigned char *buf, const size_t len);

    enum PrimaryControlFunction
    {
        Control_PrimaryResetLink           = 0x0,
        Control_PrimaryResetProcess        = 0x1,
        Control_PrimaryTestLink            = 0x2,
        Control_PrimaryUserDataConfirmed   = 0x3,  //  these two really should only go along with data packets,
        Control_PrimaryUserDataUnconfirmed = 0x4,  //    but it's overkill to seperate them.
        Control_PrimaryLinkStatus          = 0x9
    };

    enum SecondaryControlFunction
    {
        Control_SecondaryACK               = 0x0,
        Control_SecondaryNACK              = 0x1,
        Control_SecondaryLinkStatus        = 0xb,
        Control_SecondaryNotSupported      = 0xf,
    };

    packet_t constructSecondaryControlPacket( SecondaryControlFunction function, bool dfc );

    IM_EX_PROT static unsigned short crc(const unsigned char *buf, const int len);

    enum DatalinkError
    {
        Error_NoError        = 0,
        Error_BadFraming,
        Error_BadCRC,
        Error_BadAddress,
        Error_BadLength,
        Error_UnknownMessage
    };

    void setIoStateComplete();
};


}

