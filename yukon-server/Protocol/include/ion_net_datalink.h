#pragma warning( disable : 4786 )

#ifndef __ION_NET_DATALINK_H__
#define __ION_NET_DATALINK_H__

/*-----------------------------------------------------------------------------*
 *
 * File:    ion_net_datalink.h
 *
 * Classes: CtiIONDataLinkLayer, CtiIONFrame
 * Date:    07/06/2001
 *
 * Author:  Matthew Fisher
 *
 *          ION pseudo-ISO network layer classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include <vector>
#include "ctitypes.h"
#include "dlldefs.h"
#include "xfer.h"

#include "ion_rootclasses.h"
#include "ion_valuebasictypes.h"

//  predefining for all following classes
class CtiIONFrame;

//  necessary to preserve byte alignment;  makes for easy memcpy initialization and serialization
#pragma pack(push, ion_packing, 1)

class IM_EX_PROT CtiIONDataLinkLayer
{
public:

    CtiIONDataLinkLayer( );
    ~CtiIONDataLinkLayer( );

    void setAddresses( unsigned short srcID, unsigned short dstID );

    void setToOutput( CtiIONSerializable &payload );
    void setToInput( void );

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    int outFrame( unsigned char *data, unsigned long *len );
    int inFrame( unsigned char *data, unsigned long len );

    void putPayload( unsigned char *buf );
    int  getPayloadLength( void );

    enum DLLExternalStatus;

    DLLExternalStatus getStatus( void );

    bool isValid( void );

    enum DLLExternalStatus
    {
        OutDataReady,
        OutDataRetry,
        OutAckReady,
        OutAckRetry,
        OutNakReady,
        OutDataComplete,
        OutStates,        //  to seperate the states
        InDataReady,
        InAckReady,
        InDataComplete,
        InStates,         //  ditto
        Abort,
        ErrorStates,      //  and again
        Uninitialized
    };

private:

    void freeMemory( void );

    int _currentFrame;
    int _src, _dst;

    vector<CtiIONFrame *> _frameVector;
    unsigned char *_data;
    int            _dataLength, _dataSent;
    int            _bytesInLastFrame;
    int            _retries;

    enum Direction
    {
       Input,
       Output
    } _direction;

    enum
    {
        IONRetries = 5
    };

    DLLExternalStatus _status;

    int _valid;
};


class CtiIONFrame : CtiIONSerializable
{
private:

    unsigned int crc16( unsigned char *data, int length );

    void initReserved( void );

    struct _ionFrame
    {
        struct _ionFrameHeader
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
    } _frame;

protected:

public:

    CtiIONFrame( void );
    CtiIONFrame( unsigned char *rawFrame, int rawFrameLength );
    ~CtiIONFrame( void );

    void putSerialized( unsigned char *buf );
    unsigned int getSerializedLength( void );

    int crcIsValid( void );
    void setCRC( void );

    enum FrameType;

    void setFrameType( FrameType frameType );
    FrameType getFrameType( void )
    {
        switch( _frame.header.cntlframetype )
        {
            default:  //  this should never happen...
            case 0: return DataAcknakEnbl;
            case 1: return DataAcknakDsbl;
            case 2: return AcknakNAK;
            case 3: return AcknakACK;
        }
    };

    void setSrcID( unsigned short srcID ) { _frame.header.srcid = srcID; };
    unsigned short getSrcID( void ) { return _frame.header.srcid; };

    void setPayload( unsigned char *buf, int count );
    void putPayload( unsigned char *buf );
    int getPayloadLength( void ) { return _frame.header.len - EmptyPacketLength; };  //  len = data length + 7

    void setDstID( unsigned short dstID ) { _frame.header.dstid = dstID; };
    unsigned short getDstID( void ) { return _frame.header.dstid; };

    void setFirstFrame( int firstFrame ) { _frame.header.tranfirstframe = firstFrame; };
    int isFirstFrame( void ) { return _frame.header.tranfirstframe; };

    int getCounter( void ) { return _frame.header.trancounter; };
    void setCounter( int counter ) { _frame.header.trancounter = counter; };

    enum FrameType
    {
        DataAcknakEnbl = 0,  //  00b
        DataAcknakDsbl = 1,  //  01b
        AcknakNAK = 2,       //  10b
        AcknakACK = 3        //  11b
    };

    enum
    {
        MaxFrameLength       = 252,
        MaxPayloadLength     = 238,
        EmptyPacketLength    =   7,
        PrePayloadCRCOffset  =   8,
        UncountedHeaderBytes =   5
    };
};

#pragma pack(pop, ion_packing)

#endif  //  #ifndef __ION_NET_DATALINK_H__

