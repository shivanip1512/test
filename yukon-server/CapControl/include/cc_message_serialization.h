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

MessagePtr<Thrift::CCCapBankMove>::type           populateThrift  ( const ::CtiCCCapBankMoveMsg& imsg );
MessagePtr<::CtiCCCapBankMoveMsg>::type           populateMessage ( const Thrift::CCCapBankMove& imsg );

MessagePtr<Thrift::CCCapBankStates>::type         populateThrift  ( const ::CtiCCCapBankStatesMsg& imsg );
MessagePtr<::CtiCCCapBankStatesMsg>::type         populateMessage ( const Thrift::CCCapBankStates& imsg );

MessagePtr<Thrift::CCChangeOpState>::type         populateThrift  ( const ::ChangeOpState& imsg );
MessagePtr<::ChangeOpState>::type                 populateMessage ( const Thrift::CCChangeOpState& imsg );

MessagePtr<Thrift::CCCommand>::type               populateThrift  ( const ::CapControlCommand& imsg );
MessagePtr<::CapControlCommand>::type             populateMessage ( const Thrift::CCCommand& imsg );

MessagePtr<Thrift::CCDeleteItem>::type            populateThrift  ( const ::DeleteItem& imsg );
MessagePtr<::DeleteItem>::type                    populateMessage ( const Thrift::CCDeleteItem& imsg );

MessagePtr<Thrift::CCDynamicCommand>::type        populateThrift  ( const ::DynamicCommand& imsg );
MessagePtr<::DynamicCommand>::type                populateMessage ( const Thrift::CCDynamicCommand& imsg );

MessagePtr<Thrift::CCGeoAreas>::type              populateThrift  ( const ::CtiCCGeoAreasMsg& imsg );
MessagePtr<::CtiCCGeoAreasMsg>::type              populateMessage ( const Thrift::CCGeoAreas& imsg );

MessagePtr<Thrift::CCItemCommand>::type           populateThrift  ( const ::ItemCommand& imsg );
MessagePtr<::ItemCommand>::type                   populateMessage ( const Thrift::CCItemCommand& imsg );

MessagePtr<Thrift::CCMessage>::type               populateThrift  ( const ::CapControlMessage& imsg );
MessagePtr<::CapControlMessage>::type             populateMessage ( const Thrift::CCMessage& imsg );

MessagePtr<Thrift::CCObjectMove>::type            populateThrift  ( const ::CtiCCObjectMoveMsg& imsg );
MessagePtr<::CtiCCObjectMoveMsg>::type            populateMessage ( const Thrift::CCObjectMove& imsg );

MessagePtr<Thrift::CCServerResponse>::type        populateThrift  ( const ::CtiCCServerResponse& imsg );
MessagePtr<::CtiCCServerResponse>::type           populateMessage ( const Thrift::CCServerResponse& imsg );

MessagePtr<Thrift::CCShutdown>::type              populateThrift  ( const ::CtiCCShutdown& imsg );
MessagePtr<::CtiCCShutdown>::type                 populateMessage ( const Thrift::CCShutdown& imsg );

MessagePtr<Thrift::CCSpecialAreas>::type          populateThrift  ( const ::CtiCCSpecialAreasMsg& imsg );
MessagePtr<::CtiCCSpecialAreasMsg>::type          populateMessage ( const Thrift::CCSpecialAreas& imsg );

MessagePtr<Thrift::CCSubstationBus>::type         populateThrift  ( const ::CtiCCSubstationBusMsg& imsg );
MessagePtr<::CtiCCSubstationBusMsg>::type         populateMessage ( const Thrift::CCSubstationBus& imsg );

MessagePtr<Thrift::CCSubstations>::type           populateThrift  ( const ::CtiCCSubstationsMsg& imsg );
MessagePtr<::CtiCCSubstationsMsg>::type           populateMessage ( const Thrift::CCSubstations& imsg );

MessagePtr<Thrift::CCSystemStatus>::type          populateThrift  ( const ::SystemStatus& imsg );
MessagePtr<::SystemStatus>::type                  populateMessage ( const Thrift::CCSystemStatus& imsg );

MessagePtr<Thrift::CCVerifyBanks>::type           populateThrift  ( const ::VerifyBanks& imsg );
MessagePtr<::VerifyBanks>::type                   populateMessage ( const Thrift::CCVerifyBanks& imsg );

MessagePtr<Thrift::CCVerifyInactiveBanks>::type   populateThrift  ( const ::VerifyInactiveBanks& imsg );
MessagePtr<::VerifyInactiveBanks>::type           populateMessage ( const Thrift::CCVerifyInactiveBanks& imsg );

MessagePtr<Thrift::CCVerifySelectedBank>::type    populateThrift  ( const ::VerifySelectedBank& imsg );
MessagePtr<::VerifySelectedBank>::type            populateMessage ( const Thrift::CCVerifySelectedBank& imsg );

MessagePtr<Thrift::CCVoltageRegulator>::type      populateThrift  ( const ::VoltageRegulatorMessage& imsg );
MessagePtr<::VoltageRegulatorMessage>::type       populateMessage ( const Thrift::CCVoltageRegulator& imsg );

MessagePtr<Thrift::CCVoltageRegulatorItem>::type  populateThrift  ( const ::Cti::CapControl::VoltageRegulator& imsg );

MessagePtr<Thrift::CCPao>::type                   populateThrift  ( const ::CapControlPao& imsg );
MessagePtr<::CapControlPao>::type                 populateMessage ( const Thrift::CCPao& imsg );

MessagePtr<Thrift::CCArea>::type                  populateThrift  ( const ::CtiCCArea& imsg );
MessagePtr<::CtiCCArea>::type                     populateMessage ( const Thrift::CCArea& imsg );

MessagePtr<Thrift::CCSpecial>::type               populateThrift  ( const ::CtiCCSpecial& imsg );
MessagePtr<::CtiCCSpecial>::type                  populateMessage ( const Thrift::CCSpecial& imsg );

MessagePtr<Thrift::CCSubstationItem>::type        populateThrift  ( const ::CtiCCSubstation& imsg );
MessagePtr<::CtiCCSubstation>::type               populateMessage ( const Thrift::CCSubstationItem& imsg );

MessagePtr<Thrift::CCFeeder>::type                populateThrift  ( const ::CtiCCFeeder& imsg );
MessagePtr<::CtiCCFeeder>::type                   populateMessage ( const Thrift::CCFeeder& imsg );

MessagePtr<Thrift::CCCapBank>::type               populateThrift  ( const ::CtiCCCapBank& imsg );
MessagePtr<::CtiCCCapBank>::type                  populateMessage ( const Thrift::CCCapBank& imsg );

MessagePtr<Thrift::CCState>::type                 populateThrift  ( const ::CtiCCState& imsg );
MessagePtr<::CtiCCState>::type                    populateMessage ( const Thrift::CCState& imsg );

MessagePtr<Thrift::CCSubstationBusItem>::type     populateThrift  ( const ::CtiCCSubstationBus& imsg );
MessagePtr<::CtiCCSubstationBus>::type            populateMessage ( const Thrift::CCSubstationBusItem& imsg );

}
}
}
