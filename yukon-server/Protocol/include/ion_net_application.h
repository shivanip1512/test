#pragma warning( disable : 4786 )

#ifndef __ION_NET_APPLICATION_H__
#define __ION_NET_APPLICATION_H__

/*-----------------------------------------------------------------------------*
 *
 * File:    ion_net_application.h
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
#include "ctitypes.h"
#include "dlldefs.h"

#include "ion_rootclasses.h"
#include "ion_valuebasictypes.h"

//  predefining for all following classes
class CtiIONNetworkLayer;
class CtiIONDataLinkLayer;
class CtiIONFrame;


//  necessary to preserve byte alignment;  makes for easy memcpy initialization and serialization
#pragma pack(push, ion_packing, 1)


class IM_EX_PROT CtiIONApplicationLayer : public CtiIONSerializable
{
private:

    enum IONStates
    {
        IONStateUninitialized,
        IONStateInit,
        IONStateRequestFeatureManagerInfo,
        IONStateReceiveFeatureManagerInfo,
        IONStateRequestManagerInfo,
        IONStateReceiveManagerInfo,
        IONStateRequestModuleInfo,
        IONStateReceiveModuleInfo,
        IONStateRequestRegisterInfo,
        IONStateReceiveRegisterInfo,
        IONStateRequestData,
        IONStateReceiveData,
        IONStateComplete,
        IONStateAbort
    } _IONState;

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

            unsigned short pid;  //  <--  this may break, MSB vs. Intel ordering.

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

    void initReserved( void );

    bool _valid;


public:

    CtiIONApplicationLayer( );
    ~CtiIONApplicationLayer( );

    void init( CtiIONDataStream &dataStream );
    void init( CtiIONNetworkLayer &netLayer );

    void putPayload( unsigned char *buf );
    int  getPayloadLength( void );

    void putSerialized( unsigned char *buf );
    unsigned int getSerializedLength( void );

    bool isValid( void );
    bool isRequest( void );
};

#pragma pack(pop, ion_packing)

#endif  //  #ifndef __ION_NET_APPLICATION_H__

