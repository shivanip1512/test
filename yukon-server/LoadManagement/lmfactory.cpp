#include "lmfactory.h"

#include <rw/db/db.h>

#include "guard.h"
#include "logger.h"
#include "rwutil.h"
#include "resolvers.h"
#include "devicetypes.h"

#include "lmgroupversacom.h"
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

CtiLMGroupBase* CtiLMGroupFactory::createLMGroup(RWDBReader& rdr)
{
    CtiLMGroupBase* lm_group = 0;
    string category;
    string paotype;

    RWDBNullIndicator cat_null, type_null;

    rdr["category"] >> cat_null;
    rdr["type"] >> type_null;
    if(cat_null || type_null)
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " - " << "No paotype available in the given RWDBReader:" << endl;
	return 0;
    }

    rdr["category"] >> category;
    rdr["type"] >> paotype;

    switch(resolvePAOType(category.data(), paotype.data()))
    {
    case TYPE_LMGROUP_VERSACOM:
	lm_group = new CtiLMGroupVersacom(rdr);
	break;
    case TYPE_LMGROUP_EMETCON:
	lm_group = new CtiLMGroupEmetcon(rdr);
	break;
    case TYPE_LMGROUP_RIPPLE:
	lm_group = new CtiLMGroupRipple(rdr);
	break;
    case TYPE_LMGROUP_POINT:
	lm_group = new CtiLMGroupPoint(rdr);
	break;
    case TYPE_LMGROUP_EXPRESSCOM:
	lm_group = new CtiLMGroupExpresscom(rdr);
	break;
    case TYPE_LMGROUP_MCT:
	lm_group = new CtiLMGroupMCT(rdr);
	break;
    case TYPE_LMGROUP_SA105:
	lm_group = new CtiLMGroupSA105(rdr);
	break;
    case TYPE_LMGROUP_SA205:
	lm_group = new CtiLMGroupSA205(rdr);
	break;
    case TYPE_LMGROUP_SA305:
	lm_group = new CtiLMGroupSA305(rdr);
	break;
    case TYPE_LMGROUP_SADIGITAL:
	lm_group = new CtiLMGroupSADigital(rdr);
	break;
    case TYPE_LMGROUP_GOLAY:
	lm_group = new CtiLMGroupGolay(rdr);
	break;
    case TYPE_MACRO:
	lm_group = new CtiLMGroupMacro(rdr);
	break;
    default:
    {
        CtiLockGuard<CtiLogger> dout_guard(dout);
        dout << RWTime() << " **Checkpoint** " <<  "Invalid paotype: " << paotype << __FILE__ << "(" << __LINE__ << ")" << endl;
    }
    break;
    }
    return lm_group;
}
