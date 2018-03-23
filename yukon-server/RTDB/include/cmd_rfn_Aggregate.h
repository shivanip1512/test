#pragma once

#include "cmd_rfn.h"

namespace Cti {
namespace Devices {
namespace Commands {

class IM_EX_DEVDB RfnAggregateCommand : public RfnCommand
{
public:

    RfnAggregateCommand(RfnCommandList commands);

    void prepareCommandData(const CtiTime now) override;

    ASID getApplicationServiceId() const override;

    RfnCommandResultList handleResponse(const CtiTime now, const RfnResponsePayload &response) override;
    RfnCommandResultList handleError(const CtiTime now, const YukonError_t error) override;

    RfnCommandResult decodeCommand(const CtiTime now, const RfnResponsePayload &response) override;
    RfnCommandResult error(const CtiTime now, const YukonError_t error) override;

    void invokeResultHandler(ResultHandler &rh) const override final;

    static void setGlobalContextId(uint16_t id, Test::use_in_unit_tests_only&);

private:

    enum
    {
        Command_AggregateMessage = 0x01,

        HeaderLength = 4,
        SubMessageHeaderLength = 4
    };

    unsigned char getCommandCode() const override;
    unsigned char getOperation()   const override;

    Bytes getCommandHeader() override;
    Bytes getCommandData()   override;

    static std::atomic_uint16_t _globalContextId;

    using CommandMap = std::map<uint16_t, RfnCommandPtr>;
    CommandMap _commands;

    using MessageMap = std::map<uint16_t, Bytes>;
    MessageMap _messages;

    using StatusMap = std::map<uint16_t, YukonError_t>;
    StatusMap _statuses;

    size_t getPayloadLength() const;
};

}
}
}
