#include "precompiled.h"

#include "dev_710.h"
#include "dev_base.h"
#include "dev_macro.h"
#include "dev_cbc6510.h"
#include "dev_cbc.h"
#include "dev_cbc7020.h"
#include "dev_cbc8020.h"
#include "dev_ccu.h"
#include "dev_ccu721.h"
#include "dev_welco.h"
#include "dev_ilex.h"
#include "dev_seriesv.h"
#include "dev_lmi.h"
#include "dev_tcu.h"
#include "dev_meter.h"
#include "dev_gridadvisor.h"
#include "dev_modbus.h"
#include "dev_schlum.h"
#include "dev_fulcrum.h"
#include "dev_ion.h"
#include "dev_lcu.h"
#include "dev_quantum.h"
#include "dev_vectron.h"
#include "dev_carrier.h"
#include "dev_rtm.h"
#include "dev_tap.h"
#include "dev_snpp.h"
#include "dev_pagingreceiver.h"
#include "dev_tnpp.h"
#include "dev_wctp.h"
#include "dev_grp_emetcon.h"
#include "dev_grp_expresscom.h"
#include "dev_grp_rfn_expresscom.h"
#include "dev_grp_golay.h"
#include "dev_grp_mct.h"
#include "dev_grp_point.h"
#include "dev_grp_ripple.h"
#include "dev_grp_sa105.h"
#include "dev_grp_sa205.h"
#include "dev_grp_sa305.h"
#include "dev_grp_sadigital.h"
#include "dev_grp_versacom.h"
#include "dev_davis.h"
#include "dev_dlcbase.h"
#include "dev_system.h"
#include "dev_aplus.h"
#include "dev_a1.h"
#include "dev_lgs4.h"
#include "dev_lcr3102.h"
#include "dev_dr87.h"
#include "dev_dct501.h"
#include "dev_mct.h"
#include "dev_mct210.h"
#include "dev_mct22X.h"
#include "dev_mct24X.h"
#include "dev_mct310.h"
#include "dev_mct31X.h"
#include "dev_mct410.h"
#include "dev_mct420.h"
#include "dev_mct470.h"
#include "dev_mct440_2131b.h"
#include "dev_mct440_2132b.h"
#include "dev_mct440_2133b.h"
#include "dev_mct_lmt2.h"
#include "dev_mct_broadcast.h"
#include "dev_kv2.h"
#include "dev_sentinel.h"
#include "dev_focus.h"
#include "dev_ipc410al.h"
#include "dev_ipc420ad.h"
#include "dev_mark_v.h"
#include "dev_rds.h"
#include "dev_repeater800.h"
#include "dev_repeater.h"
#include "dev_repeater850.h"
#include "dev_rfn.h"
#include "dev_rfn420centron.h"
#include "dev_rfn420focus.h"
#include "dev_rtc.h"
#include "dev_sixnet.h"
#include "rte_macro.h"
#include "rte_ccu.h"
#include "rte_xcu.h"
#include "rte_versacom.h"
#include "rte_expresscom.h"
#include "dev_fmu.h"

#include "devicetypes.h"
#include "rtdb.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "slctdev.h"
#include "precompiled.h"
#include "row_reader.h"

#include "std_helper.h"

#include <boost/assign/list_of.hpp>
#include <boost/function.hpp>

using namespace Cti::Devices;
using std::string;
using std::endl;


DLLEXPORT CtiDeviceBase* DeviceFactory(Cti::RowReader &rdr)
{
    string rwsType;
    string rwsPseudo;

    INT      DevType;

    CtiDeviceBase *NewDevice = NULL;

    rdr["type"]  >> rwsType;

    if(getDebugLevel() & DEBUGLEVEL_FACTORY)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Creating a Device of type " << rwsType << endl;
    }

    NewDevice = createDeviceType(resolveDeviceType(rwsType));

    if( !NewDevice )
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);

        dout << "Device Factory has failed to produce for type " << rwsType << "!" << endl;
    }

    return NewDevice;
}

template <class Dev>
CtiDeviceBase *makeDevice()
{
    return new Dev;
}

template <int LcuType>
CtiDeviceBase *makeLcu()
{
    return new CtiDeviceLCU(LcuType);
}

typedef std::map<int, boost::function<CtiDeviceBase *()>> DeviceLookup;

const DeviceLookup deviceFactory = boost::assign::map_list_of
    //  RTUs
    (TYPE_WELCORTU,     makeDevice<CtiDeviceWelco>)
    (TYPE_ILEXRTU,      makeDevice<CtiDeviceILEX>)
    (TYPE_DARTRTU,      makeDevice<DnpDevice>)
    (TYPE_DNPRTU,       makeDevice<DnpDevice>)
    (TYPE_SERIESVRTU,   makeDevice<CtiDeviceSeriesV>)
    (TYPE_SERIESVLMIRTU,makeDevice<CtiDeviceLMI>)
    (TYPE_ION7330,      makeDevice<CtiDeviceION>)
    (TYPE_ION7700,      makeDevice<CtiDeviceION>)
    (TYPE_ION8300,      makeDevice<CtiDeviceION>)
    (TYPE_DAVIS,        makeDevice<CtiDeviceDavis>)
    (TYPE_SES92RTU,     makeDevice<CtiDeviceRemote>)
    (TYPE_MODBUS,       makeDevice<ModbusDevice>)
    (TYPE_RTC,          makeDevice<CtiDeviceRTC>)
    (TYPE_RTM,          makeDevice<CtiDeviceRTM>)
    (TYPE_FMU,          makeDevice<CtiDeviceFMU>)
    (TYPE_PAGING_RECEIVER,  makeDevice<CtiDevicePagingReceiver>)
    //  Transmitters
    (TYPE_TCU5000,      makeDevice<CtiDeviceTCU>)
    (TYPE_TCU5500,      makeDevice<CtiDeviceTCU>)
    (TYPE_CCU711,       makeDevice<CtiDeviceCCU>)
    (TYPE_CCU721,       makeDevice<Ccu721Device>)
    (TYPE_CCU710,       makeDevice<CtiDeviceCCU710>)
    (TYPE_CCU700,       makeDevice<CtiDeviceCCU710>)
    (TYPE_REPEATER800,  makeDevice<Repeater800Device>)
    (TYPE_REPEATER850,  makeDevice<Repeater850Device>)
    (TYPE_REPEATER900,  makeDevice<Repeater900Device>)
    (TYPE_TAPTERM,      makeDevice<CtiDeviceTapPagingTerminal>)
    (TYPE_SNPP,         makeDevice<CtiDeviceSnppPagingTerminal>)
    (TYPE_RDS,          makeDevice<RDSTransmitter>)
    (TYPE_TNPP,         makeDevice<CtiDeviceTnppPagingTerminal>)
    (TYPE_WCTP,         makeDevice<CtiDeviceWctpTerminal>)
    (TYPE_LCU415,       makeLcu<TYPE_LCU415>)
    (TYPE_LCU415LG,     makeLcu<TYPE_LCU415LG>)
    (TYPE_LCU415ER,     makeLcu<TYPE_LCU415ER>)
    (TYPE_LCUT3026,     makeLcu<TYPE_LCUT3026>)
    //  PLC meters
    (TYPEDCT501,        makeDevice<Dct501Device>)
    (TYPELMT2,          makeDevice<Lmt2Device>)
    (TYPEMCT210,        makeDevice<Mct210Device>)
    (TYPEMCT213,        makeDevice<Mct210Device>)
    (TYPEMCT212,        makeDevice<Mct22xDevice>)
    (TYPEMCT224,        makeDevice<Mct22xDevice>)
    (TYPEMCT226,        makeDevice<Mct22xDevice>)
    (TYPEMCT240,        makeDevice<Mct24xDevice>)
    (TYPEMCT242,        makeDevice<Mct24xDevice>)
    (TYPEMCT248,        makeDevice<Mct24xDevice>)
    (TYPEMCT250,        makeDevice<Mct24xDevice>)
    (TYPEMCT310,        makeDevice<Mct310Device>)
    (TYPEMCT310ID,      makeDevice<Mct310Device>)
    (TYPEMCT310IDL,     makeDevice<Mct310Device>)
    (TYPEMCT310IL,      makeDevice<Mct310Device>)
    (TYPEMCT318,        makeDevice<Mct31xDevice>)
    (TYPEMCT318L,       makeDevice<Mct31xDevice>)
    (TYPEMCT360,        makeDevice<Mct31xDevice>)
    (TYPEMCT370,        makeDevice<Mct31xDevice>)
    (TYPEMCT410CL,      makeDevice<Mct410Device>)
    (TYPEMCT410FL,      makeDevice<Mct410Device>)
    (TYPEMCT410GL,      makeDevice<Mct410Device>)
    (TYPEMCT410IL,      makeDevice<Mct410Device>)
    (TYPEMCT420CL,      makeDevice<Mct420Device>)
    (TYPEMCT420CD,      makeDevice<Mct420Device>)
    (TYPEMCT420FL,      makeDevice<Mct420Device>)
    (TYPEMCT420FD,      makeDevice<Mct420Device>)
    (TYPEMCT430A,       makeDevice<Mct470Device>)
    (TYPEMCT430A3,      makeDevice<Mct470Device>)
    (TYPEMCT430S4,      makeDevice<Mct470Device>)
    (TYPEMCT430SL,      makeDevice<Mct470Device>)
    (TYPEMCT470,        makeDevice<Mct470Device>)
    (TYPEMCT440_2131B,  makeDevice<Mct440_2131BDevice>)
    (TYPEMCT440_2132B,  makeDevice<Mct440_2132BDevice>)
    (TYPEMCT440_2133B,  makeDevice<Mct440_2133BDevice>)
    //  Other PLC devices
    (TYPELCR3102,       makeDevice<Lcr3102Device>)
    (TYPEMCTBCAST,      makeDevice<MctBroadcastDevice>)
    //  RFN meters
    (TYPE_RFN410FL,     makeDevice<Rfn410flDevice>)
    (TYPE_RFN410FX,     makeDevice<Rfn410fxDevice>)
    (TYPE_RFN410FD,     makeDevice<Rfn410fdDevice>)
    (TYPE_RFN420FL,     makeDevice<Rfn420flDevice>)
    (TYPE_RFN420FX,     makeDevice<Rfn420fxDevice>)
    (TYPE_RFN420FD,     makeDevice<Rfn420fdDevice>)
    (TYPE_RFN420FRX,    makeDevice<Rfn420frxDevice>)
    (TYPE_RFN420FRD,    makeDevice<Rfn420frdDevice>)
    (TYPE_RFN410CL,     makeDevice<Rfn410clDevice>)
    (TYPE_RFN420CL,     makeDevice<Rfn420clDevice>)
    (TYPE_RFN420CD,     makeDevice<Rfn420cdDevice>)
    (TYPE_RFN430A3D,    makeDevice<Rfn430a3dDevice>)
    (TYPE_RFN430A3T,    makeDevice<Rfn430a3tDevice>)
    (TYPE_RFN430A3K,    makeDevice<Rfn430a3kDevice>)
    (TYPE_RFN430A3R,    makeDevice<Rfn430a3rDevice>)
    (TYPE_RFN430KV,     makeDevice<Rfn430kvDevice>)
    //  Electronic meters
    (TYPE_FULCRUM,      makeDevice<CtiDeviceFulcrum>)
    (TYPE_QUANTUM,      makeDevice<CtiDeviceQuantum>)
    (TYPE_VECTRON,      makeDevice<CtiDeviceVectron>)
    (TYPE_ALPHA_PPLUS,  makeDevice<CtiDeviceAlphaPPlus>)
    (TYPE_TDMARKV,      makeDevice<CtiDeviceMarkV>)
    (TYPE_ALPHA_A1,     makeDevice<CtiDeviceAlphaA1>)
    (TYPE_IPC_430S4E,   makeDevice<CtiDeviceLandisGyrS4>)
    (TYPE_LGS4,         makeDevice<CtiDeviceLandisGyrS4>)
    (TYPE_DR87,         makeDevice<CtiDeviceDR87>)
    (TYPE_KV2,          makeDevice<CtiDeviceKV2>)
    (TYPE_ALPHA_A3,     makeDevice<CtiDeviceKV2>)
    (TYPE_IPC_430SL,    makeDevice<CtiDeviceSentinel>)
    (TYPE_SENTINEL,     makeDevice<CtiDeviceSentinel>)
    (TYPE_IPC_410FL,    makeDevice<Ipc410ALDevice>)
    (TYPE_IPC_420FD,    makeDevice<Ipc420ADDevice>)
    (TYPE_FOCUS,        makeDevice<CtiDeviceFocus>)
    (TYPE_SIXNET,       makeDevice<CtiDeviceSixnet>)
    //  Load Management load groups
    (TYPE_LMGROUP_POINT,            makeDevice<CtiDeviceGroupPoint>)
    (TYPE_LMGROUP_EMETCON,          makeDevice<CtiDeviceGroupEmetcon>)
    (TYPE_LMGROUP_RIPPLE,           makeDevice<CtiDeviceGroupRipple>)
    (TYPE_LMGROUP_VERSACOM,         makeDevice<CtiDeviceGroupVersacom>)
    (TYPE_LMGROUP_EXPRESSCOM,       makeDevice<CtiDeviceGroupExpresscom>)
    (TYPE_LMGROUP_RFN_EXPRESSCOM,   makeDevice<CtiDeviceGroupRfnExpresscom>)
    (TYPE_LMGROUP_MCT,              makeDevice<CtiDeviceGroupMCT>)
    (TYPE_LMGROUP_GOLAY,            makeDevice<CtiDeviceGroupGolay>)
    (TYPE_LMGROUP_SADIGITAL,        makeDevice<CtiDeviceGroupSADigital>)
    (TYPE_LMGROUP_SA105,            makeDevice<CtiDeviceGroupSA105>)
    (TYPE_LMGROUP_SA205,            makeDevice<CtiDeviceGroupSA205>)
    (TYPE_LMGROUP_SA305,            makeDevice<CtiDeviceGroupSA305>)
    //  Capacitor bank controllers
    (TYPECBCDNP,        makeDevice<DnpDevice>)
    (TYPECBC6510,       makeDevice<Cbc6510Device>)
    (TYPECBC7020,       makeDevice<Cbc7020Device>)
    (TYPECBC8020,       makeDevice<Cbc8020Device>)
    (TYPECBC7010,       makeDevice<CtiDeviceCBC>)
    (TYPEFISHERPCBC,    makeDevice<CtiDeviceCBC>)
    (TYPEVERSACOMCBC,   makeDevice<CtiDeviceCBC>)
    (TYPEEXPRESSCOMCBC, makeDevice<CtiDeviceCBC>)
    //  Smart sensors
    (TYPE_FCI,          makeDevice<CtiDeviceGridAdvisor>)
    (TYPE_NEUTRAL_MONITOR,  makeDevice<CtiDeviceGridAdvisor>)
    //  System devices
    (TYPE_MACRO,        makeDevice<CtiDeviceMacro>)
    (TYPE_SYSTEM,       makeDevice<CtiDeviceSystem>)
    //  (TYPE_VIRTUAL_SYSTEM, ) // These are created by the porter thread which manages them.
    ;

DLLEXPORT CtiDeviceBase *createDeviceType(int type)
{
    boost::optional<DeviceLookup::mapped_type> deviceCreator = Cti::mapFind(deviceFactory, type);

    if( deviceCreator )
    {
        return (*deviceCreator)();
    }

    return NULL;
}


DLLEXPORT CtiRouteBase* RouteFactory(Cti::RowReader &rdr)
{
    string rwsType;
    string category;

    INT      RteType;

    CtiRouteBase *Route = NULL;


    rdr["category"]  >> category;
    rdr["type"]  >> rwsType;

    std::transform(category.begin(), category.end(), category.begin(), ::tolower);


    if(category == string("route"))
    {
        if(getDebugLevel() & DEBUGLEVEL_FACTORY)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Creating a Route of type " << rwsType << endl;
        }

        RteType = resolveRouteType(rwsType);

        switch(RteType)
        {
            case RouteTypeCCU:
            {
                Route = (CtiRouteBase*) CTIDBG_new CtiRouteCCU;
                break;
            }
            case RouteTypeLCU:
            case RouteTypeTCU:
            case RouteTypeTap:
            case RouteTypeRDS:
            case RouteTypeWCTP:
            case RouteTypeTNPP:
            case RouteTypeSNPP:
            case RouteTypeRTC:
            case RouteTypeSeriesVLMI:
            {
                Route = (CtiRouteBase*) CTIDBG_new CtiRouteXCU;
                break;
            }
            case RouteTypeMacro:
            {
                Route = (CtiRouteBase*) CTIDBG_new CtiRouteMacro;
                break;
            }
            case RouteTypeVersacom:
            {
                Route = (CtiRouteBase*) CTIDBG_new CtiRouteVersacom;
                break;
            }
            case RouteTypeExpresscom:
            {
                Route = (CtiRouteBase*) CTIDBG_new CtiRouteExpresscom;
                break;
            }
            case RouteTypeInvalid:
            default:
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Route Factory has failed to produce for type " << rwsType << "!" << endl;
                break;
            }
        }
    }

    return Route;
}

namespace {
const std::set<int> carrierLpDeviceTypes = boost::assign::list_of
        (TYPELMT2)
        (TYPEDCT501)
        (TYPEMCT240)
        (TYPEMCT242)
        (TYPEMCT248)
        (TYPEMCT250)
        (TYPEMCT310IL)
        (TYPEMCT318L)
        (TYPEMCT410CL)
        (TYPEMCT410FL)
        (TYPEMCT410GL)
        (TYPEMCT410IL)
        (TYPEMCT420CL)
        (TYPEMCT420CD)
        (TYPEMCT420FL)
        (TYPEMCT420FD)
        (TYPEMCT430A)
        (TYPEMCT430A3)
        (TYPEMCT430S4)
        (TYPEMCT430SL)
        (TYPEMCT470);
}

DLLEXPORT bool isCarrierLPDeviceType(const int type)
{
    return carrierLpDeviceTypes.count(type);
}

