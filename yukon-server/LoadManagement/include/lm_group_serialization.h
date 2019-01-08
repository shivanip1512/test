#pragma once

#include "message_factory.h"

#include "lmgroupbase.h"
#include "lmgroupdigisep.h"
#include "lmgroupecobee.h"
#include "lmgrouphoneywell.h"
#include "lmgroupnest.h"
#include "lmgroupitron.h"
#include "lmgroupemetcon.h"
#include "lmgroupexpresscom.h"
#include "lmgroupgolay.h"
#include "lmgroupmacro.h"
#include "lmgroupmct.h"
#include "lmgrouppoint.h"
#include "lmgroupripple.h"
#include "lmgroupsa105.h"
#include "lmgroupsa205.h"
#include "lmgroupsa305.h"
#include "lmgroupsadigital.h"
#include "lmgroupversacom.h"

#include "Thrift/LMControlAreas_types.h"
#include "Thrift/LMMessage_types.h"

namespace Cti {
namespace Messaging {
namespace Serialization {

MessagePtr<Thrift::LMGroupBase>::type        populateThrift ( const ::CtiLMGroupBase& imsg );

MessagePtr<Thrift::LMGroupDigiSEP>::type     populateThrift ( const ::LMGroupDigiSEP& imsg );

MessagePtr<Thrift::LMGroupEcobee>::type      populateThrift ( const ::LMGroupEcobee& imsg );

MessagePtr<Thrift::LMGroupHoneywell>::type   populateThrift ( const ::LMGroupHoneywell& imsg );

MessagePtr<Thrift::LMGroupNest>::type        populateThrift ( const ::LMGroupNest& imsg );

MessagePtr<Thrift::LMGroupItron>::type       populateThrift ( const ::LMGroupItron& imsg );

MessagePtr<Thrift::LMGroupEmetcon>::type     populateThrift ( const ::CtiLMGroupEmetcon& imsg );

MessagePtr<Thrift::LMGroupExpresscom>::type  populateThrift ( const ::CtiLMGroupExpresscom& imsg );

MessagePtr<Thrift::LMGroupGolay>::type       populateThrift ( const ::CtiLMGroupGolay& imsg );

MessagePtr<Thrift::LMGroupMacro>::type       populateThrift ( const ::CtiLMGroupMacro& imsg );

MessagePtr<Thrift::LMGroupMCT>::type         populateThrift ( const ::CtiLMGroupMCT& imsg );

MessagePtr<Thrift::LMGroupPoint>::type       populateThrift ( const ::CtiLMGroupPoint& imsg );

MessagePtr<Thrift::LMGroupRipple>::type      populateThrift ( const ::CtiLMGroupRipple& imsg );

MessagePtr<Thrift::LMGroupSA105>::type       populateThrift ( const ::CtiLMGroupSA105& imsg );

MessagePtr<Thrift::LMGroupSA205>::type       populateThrift ( const ::CtiLMGroupSA205& imsg );

MessagePtr<Thrift::LMGroupSA305>::type       populateThrift ( const ::CtiLMGroupSA305& imsg );

MessagePtr<Thrift::LMGroupSADigital>::type   populateThrift ( const ::CtiLMGroupSADigital& imsg );

MessagePtr<Thrift::LMGroupVersacom>::type    populateThrift ( const ::CtiLMGroupVersacom& imsg );


extern MessageFactory<::CtiLMGroupBase> g_lmGroupFactory;

}
}
}
