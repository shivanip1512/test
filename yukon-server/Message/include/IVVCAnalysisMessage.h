#pragma once

#include "dlldefs.h"
#include "StreamableMessage.h"

#include <vector>


class CtiTime;


namespace Cti           {
namespace Messaging     {
namespace CapControl    {


class IM_EX_MSG IVVCAnalysisMessage : public StreamableMessage
{
public:

    enum Scenarios
    {
        Scenario_CBCCommsStale = 1,
        Scenario_VoltageRegulatorCommsStale,
        Scenario_VoltageMonitorCommsStale,
        Scenario_RequiredPointCommsStale,
        Scenario_CBCCommsIncomplete,
        Scenario_VoltageRegulatorCommsIncomplete,
        Scenario_VoltageMonitorCommsIncomplete,
        Scenario_RequiredPointCommsIncomplete,
        Scenario_TapRaiseOperation,
        Scenario_TapLowerOperation,
        Scenario_NoTapOperationMinTapPeriod,
        Scenario_NoTapOperationNeeded,
        Scenario_CapbankCloseOperation,
        Scenario_CapbankOpenOperation,
        Scenario_ExceededMaxKVar,
        Scenario_CapbankExceedDailyOpCount      // unimplemented -- ivvc doesn't support this functionality (yet...)
    };

    static IVVCAnalysisMessage * createCommsRatioMessage( const int       subbusId,
                                                          const int       scenarioId,
                                                          const CtiTime & timestamp,
                                                          const float     actualReceivedPercent,
                                                          const float     minimumReceivedPercent );

    static IVVCAnalysisMessage * createTapOperationMessage( const int       subbusId,
                                                            const int       scenarioId,
                                                            const CtiTime & timestamp,
                                                            const int       regulatorId );

    static IVVCAnalysisMessage * createNoTapOpMinTapPeriodMessage( const int       subbusId,
                                                                   const CtiTime & timestamp,
                                                                   const int       minTapPeriodMinutes );

    static IVVCAnalysisMessage * createNoTapOpNeededMessage( const int       subbusId,
                                                             const CtiTime & timestamp );

    static IVVCAnalysisMessage * createCapbankOperationMessage( const int       subbusId,
                                                                const int       scenarioId,
                                                                const CtiTime & timestamp,
                                                                const int       capbankId );

    static IVVCAnalysisMessage * createExceedMaxKVarMessage( const int       subbusId,
                                                             const CtiTime & timestamp,
                                                             const int       capbankId,
                                                             const int       maxKVar );

    virtual void streamInto( cms::StreamMessage & message ) const;

protected:

    long                _subbusId;
    long                _timestamp;
    int                 _scenarioId;
    std::vector<int>    _intData;
    std::vector<float>  _floatData;

    IVVCAnalysisMessage( const long      subbusId,
                         const int       scenarioId,
                         const CtiTime & timestamp );
};

}
}
}

