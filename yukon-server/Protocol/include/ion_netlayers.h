#pragma warning( disable : 4786 )

#ifndef __ION_NETLAYERS_H__
#define __ION_NETLAYERS_H__

/*-----------------------------------------------------------------------------*
 *
 * File:    ion_netlayers.h
 *
 * Classes: CtiIONApplicationLayer, CtiIONNetworkLayer, CtiIONDataLinkLayer
 * Date:    07/06/2001
 *
 * Author:  Matthew Fisher
 *
 *          ION pseudo-ISO network layer classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/

#include <vector>
#include "ion_rootclasses.h"
#include "ion_valuebasictypes.h"
#include "ctitypes.h"
                    
//  predefining for all following classes
class CtiIONNetworkLayer;
class CtiIONDataLinkLayer;
class CtiIONFrame;


//  necessary to preserve byte alignment;  makes for easy memcpy initialization and serialization
#pragma pack(push, ion_packing, 1)
                         
class CtiIONApplicationLayer : public CtiIONSerializable
{
public:
    CtiIONApplicationLayer( ) : 
        _valid(FALSE)
        {  _alData.IONData = NULL;  };

    ~CtiIONApplicationLayer( ) { freeMemory( ); };

    void init( CtiIONDataStream &dataStream );
    void init( CtiIONNetworkLayer &netLayer );
    
    void putPayload( unsigned char *buf );
    int  getPayloadLength( void );

    void putSerialized( unsigned char *buf );
    unsigned int getSerializedLength( void );

    int isValid( void ) { return _valid; };
    int isRequest( void ) { return _alData.header.service & 0x01; };


private:
    void freeMemory( void );

    //  note that we have no provision for the timesync format...
    struct _appLayerStruct
    {
        struct _appLayerHeader
        {
            unsigned char servicereserved1 : 8;   //  MSB ordering, but bits are stored low-to-high, i.e.

            unsigned char service          : 4;   //  <-- low nibble
            unsigned char servicereserved0 : 4;   //  <-- high nibble

            unsigned char status;

            unsigned short pid;

            unsigned char freq;

            unsigned char priority;

            struct _lengthBytes
            {
                unsigned char byte1    :  7;  //  
                unsigned char reserved :  1;  //  <-- high bit of this char

                unsigned char byte0    :  8;  //
            } length;                        
        } header;
        unsigned char *IONData;
    } _alData;

    void initReserved( void )
    {
        _alData.header.servicereserved0 = 0;
        _alData.header.servicereserved1 = 0;
        _alData.header.length.reserved = 0;
        _alData.header.pid = 0;
        _alData.header.freq = 1;
        _alData.header.priority = 0;
    };

    int _valid;
};



class CtiIONNetworkLayer : public CtiIONSerializable
{
public:
    CtiIONNetworkLayer( );
    ~CtiIONNetworkLayer( )  {  freeMemory( );  };

    void init( CtiIONDataLinkLayer &dllLayer );
    void init( CtiIONApplicationLayer &appLayer, int msgID, int srcID, int dstID );

    int  getSrcID( void );
    int  getDstID( void );
    
    void putPayload( unsigned char *buf );
    int  getPayloadLength( void );

    void putSerialized( unsigned char *buf );
    unsigned int getSerializedLength( void );

    int isValid( void ) { return _valid; };

    enum MessageType
    {
        IONMessage = 0,
        TimeSync   = 17
    };

private:

    void freeMemory( void );
    
    struct _netLayerStruct
    {
        struct _netLayerHeader
        {
            struct _lengthBytes
            {
                unsigned short byte1    : 7;  //  slightly confusing because of memory ordering -
                unsigned short reserved : 1;  //    ordered here as least-significant bit to most-significant,
                unsigned short byte0    : 8;  //    but bytes are stored LSB first in memory, so to mimic
            } length;                         //    MSB storage, they must be swapped here.
            struct _msgidBytes
            {
                unsigned short byte1 : 8;
                unsigned short byte0 : 8;
            } msgid;
            struct _dscBits
            {
                unsigned char msgtype     :  1;
                unsigned char passwdsize  :  2;
                unsigned char timesetmsg  :  1;
                unsigned char reserved    :  4;
            } desc;
            struct _addrBytes
            {
                unsigned long reserved0_1 :  7; 
                unsigned long reserved1   :  1; 
                unsigned long reserved0_0 :  8;
                unsigned long byte1       :  8; 
                unsigned long byte0       :  8;
            } src, dst;
            //  unsigned char password[8];  //  not required for data retrieval
            unsigned char service;
            unsigned char msgType;
        } header;
        unsigned char *data;
    } _nlData;

    void initReserved( void )
    {
        _nlData.header.length.reserved = 0;
        _nlData.header.desc.reserved   = 0;
        _nlData.header.src.reserved0_0 = 0;
        _nlData.header.src.reserved0_1 = 0;
        _nlData.header.src.reserved1   = 1;
        _nlData.header.dst.reserved0_0 = 0;
        _nlData.header.dst.reserved0_1 = 0;
        _nlData.header.dst.reserved1   = 1;
    };
    
    int _valid, _srcID, _dstID;
};



class CtiIONDataLinkLayer
{
public:
    
    CtiIONDataLinkLayer( ) : 
        _valid(FALSE),
        _status(Uninitialized)
        {  _data = NULL;  };
    ~CtiIONDataLinkLayer( ) { freeMemory( ); };

    void setToOutput( CtiIONNetworkLayer &netLayer, int srcID, int dstID );
    void setToInput( void );
    
    int outFrame( unsigned char *data, unsigned long *len );
    int inFrame( unsigned char *data, unsigned long len );

    void putPayload( unsigned char *buf );
    int  getPayloadLength( void );

    enum DLLExternalStatus;

    DLLExternalStatus getStatus( void ) { return _status; };

    int isValid( void ) { return _valid; };
    
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
public:
    
    CtiIONFrame( void );
    CtiIONFrame( unsigned char *rawFrame, int rawFrameLength );
    ~CtiIONFrame( void ) { };

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
    int getPayloadLength( void ) { return _frame.header.len - 7; };  //  len = data length + 7

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

private:
    
    unsigned int crc16( unsigned char *data, int length );
    
    void initReserved( void ) 
    { 
        _frame.header.cntlreserved = 0;
        _frame.header.cntldirection = 0;  //  we're the master
        _frame.header.srcreserved = 0;
        _frame.header.tranreserved = 0;
        _frame.header.reserved = 0;
    };
    
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
};

#pragma pack(pop, ion_packing)

#endif  //  #ifndef __ION_NETLAYERS_H__

