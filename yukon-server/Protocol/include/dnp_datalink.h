/*-----------------------------------------------------------------------------*
*
* File:   dnp_datalink
*
* Namespace: CtiDNP
* Class:     Datalink
* Date:   5/6/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.10 $
* DATE         :  $Date: 2005/03/10 21:07:11 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_DATALINK_H__
#define __DNP_DATALINK_H__
#pragma warning( disable : 4786)


#include "dsm2.h"
#include "xfer.h"

#include "dnp_datalink_packet.h"

namespace Cti       {
namespace Protocol  {
namespace DNP       {

class Datalink
{
private:

    //  initial config data
    unsigned short _src, _dst;

    bool _dl_confirm;

    //  datalink control statuses
    bool _send_confirm;
    bool _reset_sent;
    bool _fcb_out, _fcb_in;

    //  used in all cases
    datalink_packet _packet;
    datalink_packet _control_packet;

    int _comm_errors;
    int _protocol_errors;

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

    //  outbound-specific
    unsigned long _out_data_len;
    unsigned long _out_sent;
    unsigned char _out_data[250];

    //  inbound-specific
    unsigned long _in_recv;
    unsigned long _in_expected;
    unsigned long _in_actual;
    unsigned char _in_data[250];
    int           _in_data_len;

protected:

    enum PrimaryControlFunction;
    enum SecondaryControlFunction;

    unsigned short computeCRC(const unsigned char *buf, int len);

    void constructDataPacket( datalink_packet &packet, unsigned char *buf, unsigned long len );

    bool isControlPending( void );
    bool processControl( const datalink_packet &packet );
    void generateControl( CtiXfer &xfer );
    void decodeControl  ( CtiXfer &xfer, int status );

    void constructPrimaryControlPacket  ( datalink_packet &packet, PrimaryControlFunction   function, bool fcv, bool fcb );
    void constructSecondaryControlPacket( datalink_packet &packet, SecondaryControlFunction function, bool dfc );

    void sendPacket( datalink_packet &packet, CtiXfer &xfer );
    void recvPacket( datalink_packet &packet, CtiXfer &xfer );

    bool isValidDataPacket( const datalink_packet &packet );
    bool isValidAckPacket ( const datalink_packet &packet );

    int  calcPacketLength( int headerLen );
    bool isEntirePacket( const datalink_packet &packet, unsigned long in_recv );

    bool areFramingBytesValid( const datalink_packet &packet );
    bool areCRCsValid        ( const datalink_packet &packet );

    void putPacketPayload( const datalink_packet &packet, unsigned char *buf, int *len );

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
        Control_SecondaryLinkStatus        = 0xb
    };

    enum DatalinkPacketEnum
    {
        Packet_MaxPayloadLen      = 250,
        Packet_HeaderLen          =  10,
        Packet_HeaderLenCounted   =   5,  //  the number of bytes in the header that are counted (i.e. only those after the len octet)
        Packet_HeaderLenUncounted =   5,  //  the number of bytes that aren't counted
        Packet_FramingLen         =   2,
    };

    enum MiscNumeric
    {
        CommRetryCount     =   3,
        ProtocolRetryCount =   3
    };

public:

    Datalink();
    Datalink(const Datalink &aRef);

    virtual ~Datalink();

    Datalink &operator=(const Datalink &aRef);

    void setAddresses( unsigned short dst, unsigned short src);
    void setOptions  ( int options );

    void resetLink( void );

    void setToOutput( unsigned char *buf, unsigned int len );
    void setToInput ( void );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool isTransactionComplete( void );
    bool errorCondition( void );

    void getInPayload( unsigned char *buf );
    int  getInPayloadLength( void );

    enum DatalinkError
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
}

#endif // #ifndef __DNP_DATALINK_H__
