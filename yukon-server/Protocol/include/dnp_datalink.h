#pragma warning( disable : 4786)
#ifndef __DNP_DATALINK_H__
#define __DNP_DATALINK_H__

/*-----------------------------------------------------------------------------*
*
* File:   dnp_datalink
*
* Class:  CtiDNPDatalink
* Date:   5/6/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2003/02/12 01:16:10 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "dsm2.h"
#include "xfer.h"

#include "dnp_datalink_packet.h"

class CtiDNPDatalink
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
    _dnp_datalink_packet _packet;

    int _comm_errors;
    int _protocol_errors;

    enum DatalinkIOState
    {
        State_Uninitialized = 0,
        State_Output,
        State_Input,
        State_Complete,
        State_Failed
    } _io_state;

    //  outbound-specific
    unsigned char _out_data[250];
    unsigned long _out_data_len;
    unsigned long _out_sent;

    //  inbound-specific
    unsigned long _in_recv, _in_expected, _in_actual;

protected:
    unsigned short computeCRC(const unsigned char *buf, int len);

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
    CtiDNPDatalink();
    CtiDNPDatalink(const CtiDNPDatalink &aRef);

    virtual ~CtiDNPDatalink();

    CtiDNPDatalink &operator=(const CtiDNPDatalink &aRef);

    void setAddresses( unsigned short dst, unsigned short src);
    void setOptions  ( int options );

    void resetLink( void );

    void setToOutput( unsigned char *buf, unsigned int len );
    void setToInput ( void );

    int calcPacketLength( int headerLen );

    int getPayload( unsigned char *buf );
    int getPayloadLength( void );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    void constructPacket( _dnp_datalink_packet *packet, unsigned char *buf, unsigned long len );

    void sendPacket( _dnp_datalink_packet *packet, CtiXfer &xfer );
    void recvPacket( _dnp_datalink_packet *packet, CtiXfer &xfer );

    bool isDatalinkControlActionPending( void );
    void doDatalinkControlAction( CtiXfer &xfer );

    bool isTransactionComplete( void );
    bool errorCondition( void );

    bool areFramingBytesValid( const _dnp_datalink_packet &packet );
    bool areCRCsValid        ( const _dnp_datalink_packet &packet );

    enum ControlFunction
    {
        Control_PrimaryResetLink           = 0x0,
        Control_PrimaryResetProcess        = 0x1,
        Control_PrimaryTestLink            = 0x2,
        Control_PrimaryUserDataConfirmed   = 0x3,
        Control_PrimaryUserDataUnconfirmed = 0x4,
        Control_PrimaryLinkStatus          = 0x9,

        Control_SecondaryACK               = 0x0,
        Control_SecondaryNACK              = 0x1,
        Control_SecondaryLinkStatus        = 0xb
    };

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

#endif // #ifndef __DNP_DATALINK_H__
