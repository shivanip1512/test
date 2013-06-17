#pragma once

#include "MsgCapControlCommand.h"

#include "ctitime.h"

#include "message.h"
#include "ccsubstation.h"
#include "ccarea.h"
#include "ccsparea.h"
#include "ccstate.h"



#include "MsgItemCommand.h"

#include "EventTypes.h"


#include "MsgBankMove.h"
#include "MsgObjectMove.h"
#include "MsgSubstationBus.h"
#include "MsgCapControlEventLog.h"
#include "MsgCapBankStates.h"
#include "MsgAreas.h"
#include "MsgSpecialAreas.h"
#include "MsgSubstations.h"
#include "MsgVoltageRegulator.h"
#include "MsgDeleteItem.h"
#include "MsgSystemStatus.h"
#include "MsgCapControlServerResponse.h"
#include "MsgCapControlShutdown.h"

namespace Cti
{
    namespace CapControl
    {
        class VoltageRegulator;
    }
}
