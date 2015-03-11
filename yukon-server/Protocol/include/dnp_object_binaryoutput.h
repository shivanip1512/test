#pragma once

#include "dnp_objects.h"

namespace Cti {
namespace Protocols {
namespace DNP {

class IM_EX_PROT BinaryOutput : public Object
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
    BinaryOutput(int variation);

    enum Variation
    {
        BO_SingleBit  = 1,
        BO_WithStatus = 2
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

    int restore(const unsigned char *buf, int len);
    int restoreBits(const unsigned char *buf, int bitoffset, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;

    CtiPointDataMsg *getPoint( const TimeCTO *cto ) const;
};


#pragma pack( push, 1 )

class IM_EX_PROT BinaryOutputControl : public Object
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

        unsigned char raw[sizeof(struct crob)];
    } _crob_or_pcb;

    enum
    {
        ProtocolSize = 11
    };

    BOOST_STATIC_ASSERT( ProtocolSize == sizeof(union crobu) );

    bool _patternMask;

protected:

public:

    BinaryOutputControl(int variation);

    enum Variation
    {
        BOC_ControlRelayOutputBlock = 1,
        BOC_PatternControlBlock     = 2,
        BOC_PatternMask             = 3
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
        Status_Success           = 0,
        Status_Timeout           = 1,
        Status_NoSelect          = 2,
        Status_FormatError       = 3,
        Status_NotSupported      = 4,
        Status_AlreadyActive     = 5,
        Status_HardwareError     = 6,
        Status_Local             = 7,
        Status_TooManyObjs       = 8,
        Status_NotAuthorized     = 9,
        Status_AutomationInhibit = 10,
        Status_ProcessingLimited = 11,
        Status_OutOfRange        = 12,

        Status_ReservedMin       = 13,
        Status_ReservedMax       = 125,

        Status_NonParticipating  = 126,

        Status_Undefined         = 127,
    };

    void setControlBlock(unsigned long onTime, unsigned long offTime, unsigned char count, ControlCode code, bool queue, bool clear, TripClose tripclose);
    void setStatus(Status s);

    int restore(const unsigned char *buf, int len);
    int restoreBits(const unsigned char *buf, int bitoffset, int len);
    int serialize(unsigned char *buf) const;
    int getSerializedLen(void) const;

    int getStatus( void ) const;
    ControlCode   getControlCode() const;
    TripClose     getTripClose()   const;
    unsigned long getOnTime()      const;
    unsigned long getOffTime()     const;
    unsigned char getCount()       const;
    bool          getQueue()       const;
    bool          getClear()       const;
};

#pragma pack( pop )

}
}
}
