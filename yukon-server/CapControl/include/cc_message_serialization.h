#pragma once

#include "CapControlPao.h"
#include "ccarea.h"
#include "ccAreaBase.h"
#include "cccapbank.h"
#include "ccfeeder.h"
#include "ccmonitorpoint.h"
#include "ccsparea.h"
#include "ccstate.h"
#include "ccsubstation.h"
#include "ccsubstationbus.h"
#include "DynamicCommand.h"
#include "GangOperatedVoltageRegulator.h"
#include "MsgAreas.h"
#include "MsgBankMove.h"
#include "MsgCapBankStates.h"
#include "MsgCapControlCommand.h"
#include "MsgCapControlMessage.h"
#include "MsgCapControlServerResponse.h"
#include "MsgCapControlShutdown.h"
#include "MsgChangeOpState.h"
#include "MsgDeleteItem.h"
#include "MsgItemCommand.h"
#include "MsgObjectMove.h"
#include "MsgSpecialAreas.h"
#include "MsgSubStationBus.h"
#include "MsgSubStations.h"
#include "MsgSystemStatus.h"
#include "MsgVerifyBanks.h"
#include "MsgVerifyInactiveBanks.h"
#include "MsgVerifySelectedBank.h"
#include "MsgVoltageRegulator.h"
#include "PhaseOperatedVoltageRegulator.h"
#include "VoltageRegulator.h"

#include "Thrift/CCCapBankMove_types.h"
#include "Thrift/CCCapBankStates_types.h"
#include "Thrift/CCChangeOpState_types.h"
#include "Thrift/CCCommand_types.h"
#include "Thrift/CCDeleteItem_types.h"
#include "Thrift/CCDynamicCommand_types.h"
#include "Thrift/CCGeoAreas_types.h"
#include "Thrift/CCItemCommand_types.h"
#include "Thrift/CCMessage_types.h"
#include "Thrift/CCObjectMove_types.h"
#include "Thrift/CCServerResponse_types.h"
#include "Thrift/CCShutdown_types.h"
#include "Thrift/CCSpecialAreas_types.h"
#include "Thrift/CCSubstationBus_types.h"
#include "Thrift/CCSubstations_types.h"
#include "Thrift/CCSystemStatus_types.h"
#include "Thrift/CCVerifyBanks_types.h"
#include "Thrift/CCVerifyInactiveBanks_types.h"
#include "Thrift/CCVerifySelectedBank_types.h"
#include "Thrift/CCVoltageRegulator_types.h"

namespace Cti {
namespace Messaging {
namespace Serialization {

MessagePtr<Thrift::CCCapBankMove>::type           serialize   ( const ::CtiCCCapBankMoveMsg& imsg );
MessagePtr<::CtiCCCapBankMoveMsg>::type           deserialize ( const Thrift::CCCapBankMove& imsg );

MessagePtr<Thrift::CCCapBankStates>::type         serialize   ( const ::CtiCCCapBankStatesMsg& imsg );
MessagePtr<::CtiCCCapBankStatesMsg>::type         deserialize ( const Thrift::CCCapBankStates& imsg );

MessagePtr<Thrift::CCChangeOpState>::type         serialize   ( const ::ChangeOpState& imsg );
MessagePtr<::ChangeOpState>::type                 deserialize ( const Thrift::CCChangeOpState& imsg );

MessagePtr<Thrift::CCCommand>::type               serialize   ( const ::CapControlCommand& imsg );
MessagePtr<::CapControlCommand>::type             deserialize ( const Thrift::CCCommand& imsg );

MessagePtr<Thrift::CCDeleteItem>::type            serialize   ( const ::DeleteItem& imsg );
MessagePtr<::DeleteItem>::type                    deserialize ( const Thrift::CCDeleteItem& imsg );

MessagePtr<Thrift::CCDynamicCommand>::type        serialize   ( const ::DynamicCommand& imsg );
MessagePtr<::DynamicCommand>::type                deserialize ( const Thrift::CCDynamicCommand& imsg );

MessagePtr<Thrift::CCGeoAreas>::type              serialize   ( const ::CtiCCGeoAreasMsg& imsg );
MessagePtr<::CtiCCGeoAreasMsg>::type              deserialize ( const Thrift::CCGeoAreas& imsg );

MessagePtr<Thrift::CCItemCommand>::type           serialize   ( const ::ItemCommand& imsg );
MessagePtr<::ItemCommand>::type                   deserialize ( const Thrift::CCItemCommand& imsg );

MessagePtr<Thrift::CCMessage>::type               serialize   ( const ::CapControlMessage& imsg );
MessagePtr<::CapControlMessage>::type             deserialize ( const Thrift::CCMessage& imsg );

MessagePtr<Thrift::CCObjectMove>::type            serialize   ( const ::CtiCCObjectMoveMsg& imsg );
MessagePtr<::CtiCCObjectMoveMsg>::type            deserialize ( const Thrift::CCObjectMove& imsg );

MessagePtr<Thrift::CCServerResponse>::type        serialize   ( const ::CtiCCServerResponse& imsg );
MessagePtr<::CtiCCServerResponse>::type           deserialize ( const Thrift::CCServerResponse& imsg );

MessagePtr<Thrift::CCShutdown>::type              serialize   ( const ::CtiCCShutdown& imsg );
MessagePtr<::CtiCCShutdown>::type                 deserialize ( const Thrift::CCShutdown& imsg );

MessagePtr<Thrift::CCSpecialAreas>::type          serialize   ( const ::CtiCCSpecialAreasMsg& imsg );
MessagePtr<::CtiCCSpecialAreasMsg>::type          deserialize ( const Thrift::CCSpecialAreas& imsg );

MessagePtr<Thrift::CCSubstationBus>::type         serialize   ( const ::CtiCCSubstationBusMsg& imsg );
MessagePtr<::CtiCCSubstationBusMsg>::type         deserialize ( const Thrift::CCSubstationBus& imsg );

MessagePtr<Thrift::CCSubstations>::type           serialize   ( const ::CtiCCSubstationsMsg& imsg );
MessagePtr<::CtiCCSubstationsMsg>::type           deserialize ( const Thrift::CCSubstations& imsg );

MessagePtr<Thrift::CCSystemStatus>::type          serialize   ( const ::SystemStatus& imsg );
MessagePtr<::SystemStatus>::type                  deserialize ( const Thrift::CCSystemStatus& imsg );

MessagePtr<Thrift::CCVerifyBanks>::type           serialize   ( const ::VerifyBanks& imsg );
MessagePtr<::VerifyBanks>::type                   deserialize ( const Thrift::CCVerifyBanks& imsg );

MessagePtr<Thrift::CCVerifyInactiveBanks>::type   serialize   ( const ::VerifyInactiveBanks& imsg );
MessagePtr<::VerifyInactiveBanks>::type           deserialize ( const Thrift::CCVerifyInactiveBanks& imsg );

MessagePtr<Thrift::CCVerifySelectedBank>::type    serialize   ( const ::VerifySelectedBank& imsg );
MessagePtr<::VerifySelectedBank>::type            deserialize ( const Thrift::CCVerifySelectedBank& imsg );

MessagePtr<Thrift::CCVoltageRegulator>::type      serialize   ( const ::VoltageRegulatorMessage& imsg );
MessagePtr<::VoltageRegulatorMessage>::type       deserialize ( const Thrift::CCVoltageRegulator& imsg );

MessagePtr<Thrift::CCVoltageRegulatorItem>::type  serialize   ( const ::Cti::CapControl::VoltageRegulator& imsg );

MessagePtr<Thrift::CCPao>::type                   serialize   ( const ::CapControlPao& imsg );
MessagePtr<::CapControlPao>::type                 deserialize ( const Thrift::CCPao& imsg );

MessagePtr<Thrift::CCArea>::type                  serialize   ( const ::CtiCCArea& imsg );
MessagePtr<::CtiCCArea>::type                     deserialize ( const Thrift::CCArea& imsg );

MessagePtr<Thrift::CCSpecial>::type               serialize   ( const ::CtiCCSpecial& imsg );
MessagePtr<::CtiCCSpecial>::type                  deserialize ( const Thrift::CCSpecial& imsg );

MessagePtr<Thrift::CCSubstationItem>::type        serialize   ( const ::CtiCCSubstation& imsg );
MessagePtr<::CtiCCSubstation>::type               deserialize ( const Thrift::CCSubstationItem& imsg );

MessagePtr<Thrift::CCFeeder>::type                serialize   ( const ::CtiCCFeeder& imsg );
MessagePtr<::CtiCCFeeder>::type                   deserialize ( const Thrift::CCFeeder& imsg );

MessagePtr<Thrift::CCCapBank>::type               serialize   ( const ::CtiCCCapBank& imsg );
MessagePtr<::CtiCCCapBank>::type                  deserialize ( const Thrift::CCCapBank& imsg );

MessagePtr<Thrift::CCState>::type                 serialize   ( const ::CtiCCState& imsg );
MessagePtr<::CtiCCState>::type                    deserialize ( const Thrift::CCState& imsg );

MessagePtr<Thrift::CCSubstationBusItem>::type     serialize   ( const ::CtiCCSubstationBus& imsg );
MessagePtr<::CtiCCSubstationBus>::type            deserialize ( const Thrift::CCSubstationBusItem& imsg );

}
}
}
