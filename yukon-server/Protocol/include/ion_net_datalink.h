/*-----------------------------------------------------------------------------*
 *
 * File:    ion_net_datalink.h
 *
 * Classes: CtiIONDatalinkLayer, CtiIONFrame
 * Date:    07/06/2001
 *
 * Author:  Matthew Fisher
 *
 *          ION pseudo-ISO network layer classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#ifndef __ION_NET_DATALINK_H__
#define __ION_NET_DATALINK_H__
#pragma warning( disable : 4786 )


#include <vector>

#include "dllbase.h"
#include "dlldefs.h"
#include "xfer.h"

#include "ion_serializable.h"


//  necessary to preserve byte alignment;  makes for easy memcpy initialization and serialization
#pragma pack(push, ion_packing, 1)

class CtiIONDatalinkLayer
{
private:

    //  frame definitions

    struct ion_frame_header
    {
        unsigned char sync;
        unsigned char fmt;

        unsigned char cntlreserved  : 5;
        unsigned char cntlframetype : 2;
        unsigned char cntldirection : 1;

        unsigned char len;

        unsigned short srcid        : 15;
        unsigned short srcreserved  : 1;

        unsigned short dstid;

        unsigned char trancounter    : 6;
        unsigned char tranfirstframe : 1;
        unsigned char tranreserved   : 1;

        unsigned char reserved;
    };

    struct ion_input_frame
    {
        ion_frame_header header;

        unsigned char data[260];  //  ideally 238 + 2 byte CRC, but we have to allow for len = 255 (the device doesn't obey <= 0xF5)
    };

    //  necessary because of the padding bytes
    struct ion_output_frame
    {
        //unsigned short padding;

        ion_frame_header header;

        unsigned char data[260];  //  238 + 2 byte CRC...  and a little more
    };

    enum
    {
        MaxFrameLength       = 252,
        MaxPayloadLength     = 238,
        MinimumPacketSize    =  12,
        PrePayloadCRCOffset  =   8,
        EmptyPacketLength    =   7,
        UncountedHeaderBytes =   5,
        OutputPadding        =   0  //  2
    };

    enum FrameType
    {
        DataAcknakDsbl = 0,  //  00b
        DataAcknakEnbl = 1,  //  01b
        AcknakNAK = 2,       //  10b
        AcknakACK = 3        //  11b
    };

    void generateOutputData( ion_output_frame *targetFrame );
    void generateOutputAck ( ion_output_frame *out_frame, const ion_input_frame *in_frame );
    void generateOutputNack( ion_output_frame *out_frame, const ion_input_frame *in_frame );

    bool crcIsValid( const ion_input_frame *frame );
    void setCRC( ion_output_frame *frame );

    unsigned int crc16( const unsigned char *data, int length );

    void freeMemory( void );

    int _currentInputFrame, _currentOutputFrame;
    int _masterAddress, _slaveAddress;

    ion_input_frame  _inFrame;
    ion_output_frame _outFrame;

    unsigned char _inBuffer[250];

    vector< ion_input_frame > _inputFrameVector;

    unsigned char  *_data;
    int             _dataLength, _dataSent;
    int             _bytesInLastFrame;
    unsigned long   _inActual, _inTotal;
    int             _commErrorCount, _packetErrorCount, _framingErrorCount;

    enum IOState
    {
        Uninitialized,
        InputHeader,
        InputPacket,
        InputSendAck,
        InputSendNack,
        Output,
        OutputRecvAckNack,
        Complete,
        Failed
    } _ioState;

    enum
    {
        PacketRetries  =  3,
        FramingRetries = 30
    };

protected:

public:

    CtiIONDatalinkLayer( );
    virtual ~CtiIONDatalinkLayer( );

    void setAddresses( unsigned short masterAddress, unsigned short slaveAddress );

    void resetConnection( void );

    void setToOutput( CtiIONSerializable &payload );
    void setToInput( void );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    void putPayload( unsigned char *buf );
    int  getPayloadLength( void );

    bool isTransactionComplete( void );
    bool errorCondition( void );

    bool isValid( void );
};


#pragma pack(pop, ion_packing)

#endif  //  #ifndef __ION_NET_DATALINK_H__

