#pragma warning( disable : 4786 )

#ifndef __ION_NET_DATALINK_H__
#define __ION_NET_DATALINK_H__

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

#include <vector>

#include "dlldefs.h"
#include "xfer.h"

#include "ion_serializable.h"


//  necessary to preserve byte alignment;  makes for easy memcpy initialization and serialization
#pragma pack(push, ion_packing, 1)

class CtiIONDatalinkLayer
{
private:

    //  frame definitions

    struct ion_frame
    {
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
        } header;
        unsigned char data[240];  //  238 + 2 byte CRC
    };

    enum
    {
        MaxFrameLength       = 252,
        MaxPayloadLength     = 238,
        MinimumPacketSize    =  12,
        PrePayloadCRCOffset  =   8,
        EmptyPacketLength    =   7,
        UncountedHeaderBytes =   5
    };

    enum FrameType
    {
        DataAcknakDsbl = 0,  //  00b
        DataAcknakEnbl = 1,  //  01b
        AcknakNAK = 2,       //  10b
        AcknakACK = 3        //  11b
    };

    void initFrameReserved( ion_frame *frame );

    void initInputFrame( unsigned char *rawFrame, int rawFrameLength );
    void generateOutputFrame( ion_frame *targetFrame );
    void generateAck( ion_frame *out_frame, const ion_frame *in_frame );
    void generateNack( ion_frame *out_frame, const ion_frame *in_frame );

    bool crcIsValid( ion_frame *frame );
    void setCRC( ion_frame *frame );

    unsigned int crc16( unsigned char *data, int length );

    void freeMemory( void );

    int _currentInputFrame, _currentOutputFrame;
    int _src, _dst;

    ion_frame _outFrame, _inFrame;
    unsigned char _inBuffer[250];

    vector< ion_frame > _inputFrameVector;

    unsigned char  *_data;
    int             _dataLength, _dataSent;
    int             _bytesInLastFrame;
    unsigned long   _inActual, _inTotal;
    int             _commErrorCount, _protocolErrorCount;

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
        CommRetries     = 3,
        ProtocolRetries = 3
    };

protected:

public:

    CtiIONDatalinkLayer( );
    ~CtiIONDatalinkLayer( );

    void setAddresses( unsigned short srcID, unsigned short dstID );

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

