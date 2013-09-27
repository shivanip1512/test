#pragma once

#include "cmd_device.h"

#include <boost/shared_ptr.hpp>
#include <boost/cstdint.hpp>

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnCommand : public DeviceCommand
{
public:

    typedef Bytes RfnRequest;
    typedef Bytes RfnResponse;

    struct RfnResult
    {
        std::string description;
        std::vector<point_data> points;
    };

    RfnRequest executeCommand(const CtiTime now);
    virtual RfnResult decodeCommand(const CtiTime now, const RfnResponse &response) = 0;
    virtual RfnResult error  (const CtiTime now, const YukonError_t error_code) = 0;

protected:

    //
    // Functions called by execute() to create a request command
    //
    // Request command format :
    // 1-byte - Command Code
    // 1-byte - Operation
    // N-byte - Data
    //
    virtual unsigned char getCommandCode() const = 0;
    virtual unsigned char getOperation() const = 0;
    virtual Bytes         getCommandData() = 0;

    virtual void    prepareCommandData(const CtiTime & now) { }

    enum UnitOfMeasure
    {
        Uom_Reserved          = 0x00,
        Uom_WattHour          = 0x01,
        Uom_VarHour           = 0x02,
        Uom_QHour             = 0x03,
        Uom_VaHour            = 0x04,
        Uom_Time              = 0x05,
        Uom_Volts             = 0x10,
        Uom_Current           = 0x11,
        Uom_VoltageAngle      = 0x12,
        Uom_CurrentAngle      = 0x13,
        Uom_PowerFactorAngle  = 0x16,
        Uom_PowerFactor       = 0x18,
        Uom_PoundHour         = 0x20,
        Uom_Gallon            = 0x21,
        Uom_Pulse             = 0x3f,
        Uom_NoUnits           = 0x40,
        Uom_Watt              = 0x41,
        Uom_Var               = 0x42,
        Uom_Q                 = 0x43,
        Uom_Va                = 0x44,
        Uom_OutageCount       = 0x50,
        Uom_RestoreCount      = 0x51,
        Uom_OutageBlinkCount  = 0x52,
        Uom_RestoreBlinkCount = 0x53,
        Uom_Unknown           = 0x7f
    };

    class UomModifier1
    {
        unsigned short _uomModifier1;

    public:

        UomModifier1(unsigned short uomm1) :
            _uomModifier1(uomm1)
        {
        }

        bool getExtensionBit() const
        {
            return _uomModifier1 & 0x8000;
        }

        // unsigned short primary_secondary    : 2;
        // unsigned short phase                : 2;
        // unsigned short quadrant             : 4;
        // unsigned short fundamental_harmonic : 2;
        // unsigned short additional_rates     : 2;
        // unsigned short range                : 3;
        // unsigned short extension_bit        : 1;
    };

    class UomModifier2
    {
        unsigned short _uomModifier2;

    public:

        UomModifier2(unsigned short uomm2) :
            _uomModifier2(uomm2)
        {
        }

        double getScalingFactor() const
        {
            switch( (_uomModifier2 & 0x1c0) >> 6 )
            {
                default:
                case 0:  return 1;    //  10^0
                case 1:  return 1e3;  //  10^3
                case 2:  return 1e6;  //  mega, 10^6
                case 3:  return 1e9;  //  giga, 10^9
                case 4:  return 0;    //  Overflow
                case 5:  return 1e-1; //  0.1, no longer nano (10^-9)
                case 6:  return 1e-6; //  10^-6
                case 7:  return 1e-3; //  10^-3
            }
        }

        // unsigned short segmentation          : 3;
        // unsigned short rate                  : 3;
        // unsigned short scaling_factor        : 3;
        // unsigned short coincident_value      : 3;
        // unsigned short cumulative            : 1;
        // unsigned short continuous_cumulative : 1;
        // unsigned short frozen                : 1;
        // unsigned short extension_bit         : 1;
    };

    //
    // Type Length Values
    //
    struct TypeLengthValue
    {
        unsigned char type;
        Bytes value;

        TypeLengthValue(unsigned char type_) : type(type_)  {}
        TypeLengthValue(unsigned char type_, Bytes value_) : type(type_), value(value_)  {}
    };

    static Bytes getBytesFromTlvs( const std::vector<TypeLengthValue> &tlvs );

    static std::vector<TypeLengthValue> getTlvsFromBytes( const Bytes &bytes );
    static std::vector<TypeLengthValue> getLongTlvsFromBytes( const Bytes &bytes );

private:

    static std::vector<TypeLengthValue> getTlvsFromBytesWithLength( const unsigned length, const Bytes &bytes );
};

typedef boost::shared_ptr<RfnCommand> RfnCommandSPtr;

}
}
}

