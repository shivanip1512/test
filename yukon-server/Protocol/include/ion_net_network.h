/*-----------------------------------------------------------------------------*
 *
 * File:    ion_net_network.h
 *
 * Classes: CtiIONNetworkLayer
 * Date:    2002-oct-03
 *
 * Author:  Matthew Fisher
 *
 *          ION pseudo-ISO network layer classes for the ION serial protocol
 *
 * Copyright (c) 2001 Cannon Technologies Inc. All rights reserved.
 *-----------------------------------------------------------------------------*/
#ifndef __ION_NET_NETWORK_H__
#define __ION_NET_NETWORK_H__
#pragma warning( disable : 4786 )


#include <vector>

#include "dllbase.h"
#include "dlldefs.h"
#include "xfer.h"

#include "ion_net_datalink.h"


//  necessary to preserve byte alignment;  makes for easy memcpy initialization and serialization
#pragma pack(push, ion_packing, 1)

class CtiIONNetworkLayer : public CtiIONSerializable
{
private:

    void initOutPacketReserved( void );
    void initInPacketReserved ( void );

    void freeOutPacketMemory( void );
    void freeInPacketMemory ( void );

    struct _net_layer_struct
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
    };

    _net_layer_struct _netOut, _netIn;

    CtiIONDatalinkLayer _datalinkLayer;

    enum IOState
    {
        Uninitialized,
        Output,
        Input,
        Complete,
        Failed
    };

    int _ioState;

    unsigned int   _valid, _masterAddress, _slaveAddress;
    unsigned short _msgCount;

    enum
    {
        NetworkLayerLengthBytes = 2
    };

    enum MessageType
    {
        MessageType_IONMessage = 0,
        MessageType_TimeSync   = 17
    };

protected:

public:
    CtiIONNetworkLayer( );
    ~CtiIONNetworkLayer( );

    void setAddresses( unsigned short masterAddress, unsigned short slaveAddress );

    void setToOutput( CtiIONSerializable &payload, bool timeSync=false );
    void setToInput( void );

    void putPayload( unsigned char *buf );
    int  getPayloadLength( void ) const;

    int generate( CtiXfer &xfer );
    int decode  ( CtiXfer &xfer, int status );

    bool isTransactionComplete( void );
    bool errorCondition( void );

    void putSerialized( unsigned char *buf ) const;
    unsigned int getSerializedLength( void ) const;

    int isValid( void ) { return _valid; };
};

#pragma pack(pop, ion_packing)

#endif  //  #ifndef __ION_NET_NETWORK_H__

