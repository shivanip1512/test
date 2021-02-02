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
    
    std::string getCommandName() const override = 0;

protected:

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