/*-----------------------------------------------------------------------------*
*
* File:   dnp_object_binaryoutput
*
* Class:  DNP Binary Output objects
* Date:   7/3/2002
*
* Author: Matt Fisher
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.7 $
* DATE         :  $Date: 2003/12/26 17:27:06 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DNP_OBJECT_BINARYOUTPUT_H__
#define __DNP_OBJECT_BINARYOUTPUT_H__
#pragma warning( disable : 4786)


#include "dnp_objects.h"

class CtiDNPBinaryOutput : public CtiDNPObject
{
private:
    union bofu  //  binary out flag union, named for Slick's parsing pleasure
    {
        struct boflags
        {
            unsigned char online       : 1;
            unsigned char restart      : 1;
            unsigned char commlost     : 1;
            unsigned char remoteforced : 1;
            unsigned char localforced  : 1;
            unsigned char reserved     : 2;
            unsigned char state        : 1;
        } flags;

        unsigned char raw;
    } _bo;

protected:

public:
    CtiDNPBinaryOutput(int variation);

    enum Variation
    {
        SingleBit  = 1,
        WithStatus = 2
    };

    enum
    {
        Group = 10
    };

    enum
    {
        //  Binary outputs are offset by this amount when they are returned as statuses
        BinaryOutputStatusOffset = 10000
    };

    int restore(unsigned char *buf, int len);
    int restoreBits(unsigned char *buf, int bitoffset, int len);
    int serialize(unsigned char *buf);
    int getSerializedLen(void);

    CtiPointDataMsg *getPoint( const CtiDNPTimeCTO *cto );
};


#pragma pack( push, 1 )

class CtiDNPBinaryOutputControl : public CtiDNPObject
{
private:
    union crobu
    {
        struct crob
        {
            struct control
            {
                unsigned char code       : 4;
                unsigned char queue      : 1;
                unsigned char clear      : 1;
                unsigned char trip_close : 2;
            } control_code;                     //  byte   1

            unsigned char count;                //  byte   2
            unsigned long on_time;              //  bytes  3-6
            unsigned long off_time;             //  bytes  7-10
            unsigned char status;               //  byte  11
        } block;

        unsigned char raw[11];
    } _crob_or_pcb;

    bool _patternMask;

protected:

public:

    CtiDNPBinaryOutputControl(int variation);

    enum Variation
    {
        ControlRelayOutputBlock = 1,
        PatternControlBlock     = 2,
        PatternMask             = 3
    };

    enum
    {
        Group = 12
    };

    enum ControlCode
    {
        Noop     = 0,
        PulseOn  = 1,
        PulseOff = 2,
        LatchOn  = 3,
        LatchOff = 4
    };

    enum TripClose
    {
        NUL   = 0,
        Close = 1,
        Trip  = 2
    };

    enum Status
    {
        Status_RequestAccepted      = 0,
        Status_ArmTimeout           = 1,
        Status_NoSelect             = 2,
        Status_FormattingError      = 3,
        Status_PointNotControllable = 4,
        Status_QueueFullPointActive = 5,
        Status_HardwareError        = 6,
        Status_InvalidStatus
    };

    void setControlBlock(unsigned long onTime, unsigned long offTime, unsigned char count, ControlCode code, bool queue, bool clear, TripClose tripclose);

    int restore(unsigned char *buf, int len);
    int restoreBits(unsigned char *buf, int bitoffset, int len);
    int serialize(unsigned char *buf);
    int getSerializedLen(void);

    int getStatus( void ) const;
};

#pragma pack( pop )

#endif  //  #ifndef __DNP_OBJECT_BINARYOUTPUT_H__
