/*-----------------------------------------------------------------------------*
*
* File:   prot_idlc
*
* Namespace: Cti::Protocol
* Class:     IDLC
* Date:   7/26/2006
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2006/10/05 16:44:51 $
*
* Copyright (c) 2006 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __PROT_IDLC_H__
#define __PROT_IDLC_H__
#pragma warning( disable : 4786)


#include "dsm2.h"
#include "xfer.h"

#include "prot_base.h"

namespace Cti       {
namespace Protocol  {

class IM_EX_PROT IDLC : public Interface
{
private:

    unsigned char _address;

#pragma pack(push, 1)

    union control_code
    {
        unsigned char code;
        struct
        {
            unsigned char unsequenced       : 1;
            unsigned char request_sequence  : 3;
            unsigned char final_frame       : 1;
            unsigned char response_sequence : 3;
        };
    };

    struct frame_header
    {
        unsigned char flag;
        unsigned char direction : 1;
        unsigned char address   : 7;
        control_code  control;
    };

    union frame
    {
        struct
        {
            frame_header  header;
            unsigned char data[258];
        };
        unsigned char raw[261];
    };

#pragma pack(pop)

    enum IOState
    {
        State_IO_Output,      //  normal output
        State_IO_Reset,       //  reset request from master to CCU
        State_IO_Retransmit,  //  retransmit request from master to CCU
        State_IO_Input,       //  input
        State_IO_Complete,    //  transaction complete
        State_IO_Failed,      //  transaction failed
        State_IO_Invalid,     //  catch-all error condition for initialization errors
    } _io_state;

    enum ControlCodes
    {
        Control_ResetRequest      = 0x1f,
        Control_ResetAcknowlegde  = 0x73,
        Control_RejectWithRestart = 0x19,  //  upper 3 bits are the sequence number of the next frame expected
        Control_RetransmitRequest = 0x1d,  //  upper 3 bits are the sequence number of the frame to retransmit
        Control_Unsequenced       = 0x13,
    };

    enum Misc
    {
        FramingFlag = 0x7e,
    };

    //  outbound-specific
    unsigned char _out_payload_length;
    unsigned char _out_payload[255];
    unsigned long _out_sent;
    unsigned char _master_sequence;
    frame _out_frame;

    //  inbound-specific
    unsigned char _in_payload_length;
    unsigned char _in_payload[255];
    unsigned long _in_expected;
    unsigned long _in_actual;
    unsigned long _in_recv;
    unsigned char _slave_sequence;
    unsigned int  _framing_seek_length;
    frame _in_frame;

    int _comm_errors;
    int _protocol_errors;
    int _sanity_counter;

    void sendFrame     (CtiXfer &xfer);
    void sendRetransmit(CtiXfer &xfer);
    void sendReset     (CtiXfer &xfer);
    void recvFrame     (CtiXfer &xfer, unsigned bytes_received);

    unsigned calcFrameLength(const frame &f);

    unsigned alignFrame(CtiXfer &xfer, frame &f, unsigned long &aligned_inbound_count);

    bool isCRCValid     (const frame &f);
    bool isControlFrame (const frame &f);
    bool isCompleteFrame(const frame &f, unsigned bytes_received);

    enum IDLCFrameEnum
    {
        Frame_MaximumPayloadLength      = 255,
        Frame_MinimumLength             =   5,
        Frame_ControlPacketLength       =   5,
        Frame_DataPacket_OverheadLength =   6,
    };

    enum MiscNumeric
    {
        Insanity                 =  10,

        MaximumCommErrors        =   3,
        MaximumProtocolErrors    =   3,
        MaximumFramingSeekLength = 260,
    };

protected:

public:

    IDLC();
    IDLC(const IDLC &aRef);

    virtual ~IDLC();

    IDLC &operator=(const IDLC &aRef);

    void setAddress( unsigned short address );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool isTransactionComplete( void );
    bool errorCondition( void );

    bool setOutPayload( const unsigned char *payload, unsigned len );

    void getInPayload( unsigned char *buf );
    int  getInPayloadLength( void );

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

#endif // #ifndef __PROT_IDLC_H__
