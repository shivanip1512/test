#pragma once

#include "cmd_rfn.h"
#include "cmd_rfn_Individual.h"

namespace Cti::Devices::Commands {

class IM_EX_DEVDB RfnAggregateCommand : public RfnCommand
{
public:

    RfnAggregateCommand(RfnIndividualCommandList commands);

    void prepareCommandData(const CtiTime now) override;

    ASID getApplicationServiceId() const override;

    RfnCommandResultList handleResponse(const CtiTime now, const RfnResponsePayload &response) override final;
    RfnCommandResultList handleError(const CtiTime now, const YukonError_t error) override final;

    void invokeResultHandler(ResultHandler &rh) const override final;

    static void setGlobalContextId(uint16_t id, Test::use_in_unit_tests_only&);

private:

    enum
    {
        Command_AggregateMessage = 0x01,

        HeaderLength = 4,
        SubMessageHeaderLength = 5
    };

    unsigned char getCommandCode() const override;
    unsigned char getOperation()   const override;

    Bytes getCommandHeader() override;
    Bytes getCommandData()   override;

    static std::atomic_uint16_t _globalContextId;

    using CommandMap = std::map<uint16_t, RfnIndividualCommandPtr>;
    CommandMap _commands;

	struct Request
	{
		uint16_t contextId;
		Messaging::Rfn::ApplicationServiceIdentifiers asid;
		Bytes message;
	};

    using Requests = std::vector<Request>;
    Requests _requests;

    using StatusMap = std::map<uint16_t, YukonError_t>;
    StatusMap _statuses;

    size_t getPayloadLength() const;
};

}