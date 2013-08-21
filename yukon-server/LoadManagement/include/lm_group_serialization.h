#pragma once

#include "message_factory.h"

#include "lmgroupbase.h"
#include "lmgroupdigisep.h"
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

MessagePtr<Thrift::LMGroupBase>::type        serialize ( const ::CtiLMGroupBase& imsg );

MessagePtr<Thrift::LMGroupDigiSEP>::type     serialize ( const ::LMGroupDigiSEP& imsg );

MessagePtr<Thrift::LMGroupEmetcon>::type     serialize ( const ::CtiLMGroupEmetcon& imsg );

MessagePtr<Thrift::LMGroupExpresscom>::type  serialize ( const ::CtiLMGroupExpresscom& imsg );

MessagePtr<Thrift::LMGroupGolay>::type       serialize ( const ::CtiLMGroupGolay& imsg );

MessagePtr<Thrift::LMGroupMacro>::type       serialize ( const ::CtiLMGroupMacro& imsg );

MessagePtr<Thrift::LMGroupMCT>::type         serialize ( const ::CtiLMGroupMCT& imsg );

MessagePtr<Thrift::LMGroupPoint>::type       serialize ( const ::CtiLMGroupPoint& imsg );

MessagePtr<Thrift::LMGroupRipple>::type      serialize ( const ::CtiLMGroupRipple& imsg );

MessagePtr<Thrift::LMGroupSA105>::type       serialize ( const ::CtiLMGroupSA105& imsg );

MessagePtr<Thrift::LMGroupSA205>::type       serialize ( const ::CtiLMGroupSA205& imsg );

MessagePtr<Thrift::LMGroupSA305>::type       serialize ( const ::CtiLMGroupSA305& imsg );

MessagePtr<Thrift::LMGroupSADigital>::type   serialize ( const ::CtiLMGroupSADigital& imsg );

MessagePtr<Thrift::LMGroupVersacom>::type    serialize ( const ::CtiLMGroupVersacom& imsg );


extern MessageFactory<::CtiLMGroupBase> g_lmGroupFactory;

}
}
}
