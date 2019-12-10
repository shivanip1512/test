#pragma once

#include "cmd_rfn.h"

namespace Cti::Devices::Commands {

class IM_EX_DEVDB RfnIndividualCommand : public RfnCommand
{
public:
    RfnCommandResultList handleResponse(const CtiTime now, const RfnResponsePayload &response) override = 0;
    RfnCommandResultList handleError(const CtiTime now, const YukonError_t errorCode) override final;

    //  Single-command (non-aggregate) decode methods
    virtual RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response) = 0;
    virtual RfnCommandResult error(const CtiTime now, const YukonError_t errorCode);
    
    virtual std::string getCommandName() = 0;

protected:
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

        bool getExtensionBit() const
        {
            return _uomModifier2 & 0x8000;
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

    double adjustByScalingFactor(double value, double scalingFactor) const;

    //
    // Type Length Values
    //
    struct TypeLengthValue
    {
        unsigned char type;
        bool          isLongTlv;
        Bytes         value;

        TypeLengthValue(unsigned char type_)
            :   type(type_),
                isLongTlv(false)
        {}

        TypeLengthValue(unsigned char type_, const Bytes &value_)
            :   type(type_),
                isLongTlv(false),
                value(value_)
        {}

        static TypeLengthValue makeLongTlv(unsigned char type_ )
        {
            TypeLengthValue tlv(type_);
            tlv.isLongTlv = true;
            return tlv;
        }

        static TypeLengthValue makeLongTlv(unsigned char type_, const Bytes &value_)
        {
            TypeLengthValue tlv(type_, value_);
            tlv.isLongTlv = true;
            return tlv;
        }
    };

    static Bytes getBytesFromTlvs( const std::vector<TypeLengthValue> &tlvs );

    typedef std::set<unsigned char> LongTlvList;

    static std::vector<TypeLengthValue> getTlvsFromBytes( const Bytes &bytes );
    static std::vector<TypeLengthValue> getTlvsFromBytes( const Bytes &bytes, const LongTlvList &longTlvs );
};

using RfnIndividualCommandPtr = std::unique_ptr<RfnIndividualCommand>;
using RfnIndividualCommandList = std::vector<RfnIndividualCommandPtr>;

struct IM_EX_DEVDB RfnOneWayCommand : public RfnIndividualCommand, NoResultHandler
{
    RfnCommandResultList handleResponse(const CtiTime now, const RfnResponsePayload &response) override final;
    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response) override final;

    bool isOneWay() const override final;
};
    
struct IM_EX_DEVDB RfnTwoWayCommand : public RfnIndividualCommand
{
    RfnCommandResultList handleResponse(const CtiTime now, const RfnResponsePayload &response) override final;

    bool isOneWay() const override final;
};


}