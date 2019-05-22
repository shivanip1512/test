#include "precompiled.h"

#include "lmfactory.h"


#include "guard.h"
#include "logger.h"
#include "resolvers.h"
#include "devicetypes.h"

#include "lmgroupversacom.h"
#include "lmgroupdigisep.h"
#include "lmgroupemetcon.h"
#include "lmgroupexpresscom.h"
#include "lmgroupmct.h"
#include "lmgroupripple.h"
#include "lmgrouppoint.h"
#include "lmgroupsa105.h"
#include "lmgroupsa205.h"
#include "lmgroupsa305.h"
#include "lmgroupsadigital.h"
#include "lmgroupgolay.h"
#include "lmgroupmacro.h"
#include "lmgroupecobee.h"
#include "lmgrouphoneywell.h"
#include "lmgroupnest.h"
#include "lmgroupitron.h"
#include "lmgroupmeterdisconnect.h"

using std::string;
using std::endl;

CtiLMGroupPtr CtiLMGroupFactory::createLMGroup(Cti::RowReader &rdr)
{
    CtiLMGroupBase* lm_group = 0;
    string category;
    string paotype;

    if(rdr["category"].isNull() || rdr["type"].isNull())
    {
        CTILOG_INFO(dout, "No paotype available in the given Reader:");
        return CtiLMGroupPtr();
    }

    rdr["category"] >> category;
    rdr["type"] >> paotype;

    switch(resolvePAOType(category, paotype))
    {
    case TYPE_LMGROUP_VERSACOM:
        lm_group = CTIDBG_new CtiLMGroupVersacom(rdr);
        break;
    case TYPE_LMGROUP_EMETCON:
        lm_group = CTIDBG_new CtiLMGroupEmetcon(rdr);
        break;
    case TYPE_LMGROUP_RIPPLE:
        lm_group = CTIDBG_new CtiLMGroupRipple(rdr);
        break;
    case TYPE_LMGROUP_POINT:
        lm_group = CTIDBG_new CtiLMGroupPoint(rdr);
        break;
    case TYPE_LMGROUP_RFN_EXPRESSCOM:
    case TYPE_LMGROUP_EXPRESSCOM:
        lm_group = CTIDBG_new CtiLMGroupExpresscom(rdr);
        break;
    case TYPE_LMGROUP_DIGI_SEP:
        lm_group = CTIDBG_new LMGroupDigiSEP(rdr);
        break;
    case TYPE_LMGROUP_ECOBEE:
        lm_group = CTIDBG_new LMGroupEcobee(rdr);
        break;
    case TYPE_LMGROUP_HONEYWELL:
        lm_group = CTIDBG_new LMGroupHoneywell(rdr);
        break;
    case TYPE_LMGROUP_NEST:
        lm_group = CTIDBG_new LMGroupNest(rdr);
        break;
    case TYPE_LMGROUP_ITRON:
        lm_group = CTIDBG_new LMGroupItron(rdr);
        break;
    case TYPE_LMGROUP_METER_DISCONNECT:
        lm_group = CTIDBG_new LMGroupMeterDisconnect(rdr);
        break;
    case TYPE_LMGROUP_MCT:
        lm_group = CTIDBG_new CtiLMGroupMCT(rdr);
        break;
    case TYPE_LMGROUP_SA105:
        lm_group = CTIDBG_new CtiLMGroupSA105(rdr);
        break;
    case TYPE_LMGROUP_SA205:
        lm_group = CTIDBG_new CtiLMGroupSA205(rdr);
        break;
    case TYPE_LMGROUP_SA305:
        lm_group = CTIDBG_new CtiLMGroupSA305(rdr);
        break;
    case TYPE_LMGROUP_SADIGITAL:
        lm_group = CTIDBG_new CtiLMGroupSADigital(rdr);
        break;
    case TYPE_LMGROUP_GOLAY:
        lm_group = CTIDBG_new CtiLMGroupGolay(rdr);
        break;
    case TYPE_MACRO:
        lm_group = CTIDBG_new CtiLMGroupMacro(rdr);
        break;
    default:
    CTILOG_ERROR(dout, "Invalid paotype: " << paotype);
    break;
    }
    return CtiLMGroupPtr(lm_group);
}
