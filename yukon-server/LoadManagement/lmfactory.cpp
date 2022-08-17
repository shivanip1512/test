#include "precompiled.h"

#include "lmfactory.h"

#include "guard.h"
#include "logger.h"
#include "resolvers.h"
#include "devicetypes.h"

#include "std_helper.h"

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


namespace
{

template< typename GROUP >
CtiLMGroupBase * makeGroup( Cti::RowReader & rdr )
{
    return new GROUP( rdr );
}

static const std::map< DeviceTypes, std::function< CtiLMGroupBase * ( Cti::RowReader & ) > >    groupCreator
{
    { TYPE_LMGROUP_VERSACOM,         makeGroup< CtiLMGroupVersacom >        },
    { TYPE_LMGROUP_EMETCON,          makeGroup< CtiLMGroupEmetcon >         },
    { TYPE_LMGROUP_RIPPLE,           makeGroup< CtiLMGroupRipple >          },
    { TYPE_LMGROUP_POINT,            makeGroup< CtiLMGroupPoint >           },
    { TYPE_LMGROUP_RFN_EXPRESSCOM,   makeGroup< CtiLMGroupExpresscom >      },
    { TYPE_LMGROUP_EXPRESSCOM,       makeGroup< CtiLMGroupExpresscom >      },
    { TYPE_LMGROUP_DIGI_SEP,         makeGroup< LMGroupDigiSEP >            },
    { TYPE_LMGROUP_ECOBEE,           makeGroup< LMGroupEcobee >             },
    { TYPE_LMGROUP_HONEYWELL,        makeGroup< LMGroupHoneywell >          },
    { TYPE_LMGROUP_NEST,             makeGroup< LMGroupNest >               },
    { TYPE_LMGROUP_ITRON,            makeGroup< LMGroupItron >              },
    { TYPE_LMGROUP_METER_DISCONNECT, makeGroup< LMGroupMeterDisconnect >    },
    { TYPE_LMGROUP_MCT,              makeGroup< CtiLMGroupMCT >             },
    { TYPE_LMGROUP_SA105,            makeGroup< CtiLMGroupSA105 >           },
    { TYPE_LMGROUP_SA205,            makeGroup< CtiLMGroupSA205 >           },
    { TYPE_LMGROUP_SA305,            makeGroup< CtiLMGroupSA305 >           },
    { TYPE_LMGROUP_SADIGITAL,        makeGroup< CtiLMGroupSADigital >       },
    { TYPE_LMGROUP_GOLAY,            makeGroup< CtiLMGroupGolay >           },
    { TYPE_MACRO,                    makeGroup< CtiLMGroupMacro >           }
};

}

CtiLMGroupPtr CtiLMGroupFactory::createLMGroup( Cti::RowReader & rdr )
{
    if ( rdr["Category"].isNull() || rdr["Type"].isNull() )
    {
        CTILOG_INFO( dout, "No paotype available in the given Reader." );

        return nullptr;
    }

    std::string category,
                paotype;

    rdr["Category"] >> category;
    rdr["Type"]     >> paotype;

    if ( auto x = Cti::mapFind( groupCreator, static_cast< DeviceTypes >( resolvePAOType( category, paotype ) ) ) )
    {
        return CtiLMGroupPtr( ( *x )( rdr ) );
    }

    CTILOG_ERROR( dout, "Invalid paotype: " << paotype );

    return nullptr;
}

