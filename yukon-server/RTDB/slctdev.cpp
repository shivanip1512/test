#include "precompiled.h"

#include "dev_710.h"
#include "dev_base.h"
#include "dev_macro.h"
#include "dev_cbc.h"
#include "dev_cbc7020.h"
#include "dev_cbc8020.h"
#include "dev_cbcdnp.h"
#include "dev_cbclogical.h"
#include "dev_ccu.h"
#include "dev_ccu721.h"
#include "dev_dnprtu.h"
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
#include "dev_rf_da.h"
#include "dev_rfn410centron.h"
#include "dev_rfn420centron.h"
#include "dev_rfn_LgyrFocus_al.h"
#include "dev_rfnCommercial.h"
#include "dev_rtc.h"
#include "dev_sixnet.h"
#include "rte_macro.h"
#include "rte_ccu.h"
#include "rte_xcu.h"
#include "rte_versacom.h"
#include "rte_expresscom.h"
#include "dev_rf_BatteryNode.h"

#include "devicetypes.h"
#include "rtdb.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "slctdev.h"
#include "precompiled.h"
#include "row_reader.h"

#include "std_helper.h"

using namespace Cti::Devices;
using std::string;
using std::endl;


DLLEXPORT CtiDeviceBase* DeviceFactory(Cti::RowReader &rdr)
{
    string typeStr;

    INT      DevType;

    CtiDeviceBase *NewDevice = NULL;

    rdr["type"]  >> typeStr;

    if(getDebugLevel() & DEBUGLEVEL_FACTORY)
    {
        CTILOG_DEBUG(dout, "Creating a Device of type "<< typeStr );
    }

    NewDevice = createDeviceType(resolveDeviceType(typeStr));

    if( ! NewDevice )
    {
        if( ! isKnownUnsupportedDevice(typeStr) )
        {
            CTILOG_ERROR(dout, "Device Factory has failed to produce for type "<< typeStr <<"!");
        }
        else if( getDebugLevel() & DEBUGLEVEL_FACTORY )
        {
            CTILOG_DEBUG(dout, "Device Factory cannot produce for type "<< typeStr <<"! : the device is known but unsupported");
        }
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

typedef std::function<CtiDeviceBase *()> MakeDeviceFunc;
typedef std::map<int, MakeDeviceFunc> DeviceLookup;

const DeviceLookup deviceFactory {
    //  RTUs
    { TYPE_WELCORTU,     MakeDeviceFunc(makeDevice<CtiDeviceWelco>) },
    { TYPE_ILEXRTU,      MakeDeviceFunc(makeDevice<CtiDeviceILEX>) },
    { TYPE_DARTRTU,      MakeDeviceFunc(makeDevice<DnpRtuDevice>) },
    { TYPE_DNPRTU,       MakeDeviceFunc(makeDevice<DnpRtuDevice>) },
    { TYPE_SERIESVRTU,   MakeDeviceFunc(makeDevice<CtiDeviceSeriesV>) },
    { TYPE_SERIESVLMIRTU,MakeDeviceFunc(makeDevice<CtiDeviceLMI>) },
    { TYPE_ION7330,      MakeDeviceFunc(makeDevice<CtiDeviceION>) },
    { TYPE_ION7700,      MakeDeviceFunc(makeDevice<CtiDeviceION>) },
    { TYPE_ION8300,      MakeDeviceFunc(makeDevice<CtiDeviceION>) },
    { TYPE_DAVIS,        MakeDeviceFunc(makeDevice<CtiDeviceDavis>) },
    { TYPE_SES92RTU,     MakeDeviceFunc(makeDevice<CtiDeviceRemote>) },
    { TYPE_MODBUS,       MakeDeviceFunc(makeDevice<ModbusDevice>) },
    { TYPE_RTC,          MakeDeviceFunc(makeDevice<CtiDeviceRTC>) },
    { TYPE_RTM,          MakeDeviceFunc(makeDevice<CtiDeviceRTM>) },
    { TYPE_PAGING_RECEIVER,  MakeDeviceFunc(makeDevice<CtiDevicePagingReceiver>) },
    //  Transmitters
    { TYPE_TCU5000,      MakeDeviceFunc(makeDevice<CtiDeviceTCU>) },
    { TYPE_TCU5500,      MakeDeviceFunc(makeDevice<CtiDeviceTCU>) },
    { TYPE_CCU711,       MakeDeviceFunc(makeDevice<CtiDeviceCCU>) },
    { TYPE_CCU721,       MakeDeviceFunc(makeDevice<Ccu721Device>) },
    { TYPE_CCU710,       MakeDeviceFunc(makeDevice<CtiDeviceCCU710>) },
    { TYPE_CCU700,       MakeDeviceFunc(makeDevice<CtiDeviceCCU710>) },
    { TYPE_REPEATER800,  MakeDeviceFunc(makeDevice<Repeater800Device>) },
    { TYPE_REPEATER850,  MakeDeviceFunc(makeDevice<Repeater850Device>) },
    { TYPE_REPEATER900,  MakeDeviceFunc(makeDevice<Repeater900Device>) },
    { TYPE_TAPTERM,      MakeDeviceFunc(makeDevice<TapPagingTerminal>) },
    { TYPE_SNPP,         MakeDeviceFunc(makeDevice<CtiDeviceSnppPagingTerminal>) },
    { TYPE_RDS,          MakeDeviceFunc(makeDevice<RDSTransmitter>) },
    { TYPE_TNPP,         MakeDeviceFunc(makeDevice<CtiDeviceTnppPagingTerminal>) },
    { TYPE_WCTP,         MakeDeviceFunc(makeDevice<CtiDeviceWctpTerminal>) },
    { TYPE_LCU415,       MakeDeviceFunc(makeLcu<TYPE_LCU415>) },
    { TYPE_LCU415LG,     MakeDeviceFunc(makeLcu<TYPE_LCU415LG>) },
    { TYPE_LCU415ER,     MakeDeviceFunc(makeLcu<TYPE_LCU415ER>) },
    { TYPE_LCUT3026,     MakeDeviceFunc(makeLcu<TYPE_LCUT3026>) },
    //  PLC meters
    { TYPEDCT501,        MakeDeviceFunc(makeDevice<Dct501Device>) },
    { TYPELMT2,          MakeDeviceFunc(makeDevice<Lmt2Device>) },
    { TYPEMCT210,        MakeDeviceFunc(makeDevice<Mct210Device>) },
    { TYPEMCT213,        MakeDeviceFunc(makeDevice<Mct210Device>) },
    { TYPEMCT212,        MakeDeviceFunc(makeDevice<Mct22xDevice>) },
    { TYPEMCT224,        MakeDeviceFunc(makeDevice<Mct22xDevice>) },
    { TYPEMCT226,        MakeDeviceFunc(makeDevice<Mct22xDevice>) },
    { TYPEMCT240,        MakeDeviceFunc(makeDevice<Mct24xDevice>) },
    { TYPEMCT242,        MakeDeviceFunc(makeDevice<Mct24xDevice>) },
    { TYPEMCT248,        MakeDeviceFunc(makeDevice<Mct24xDevice>) },
    { TYPEMCT250,        MakeDeviceFunc(makeDevice<Mct24xDevice>) },
    { TYPEMCT310,        MakeDeviceFunc(makeDevice<Mct310Device>) },
    { TYPEMCT310ID,      MakeDeviceFunc(makeDevice<Mct310Device>) },
    { TYPEMCT310IDL,     MakeDeviceFunc(makeDevice<Mct310Device>) },
    { TYPEMCT310IL,      MakeDeviceFunc(makeDevice<Mct310Device>) },
    { TYPEMCT318,        MakeDeviceFunc(makeDevice<Mct31xDevice>) },
    { TYPEMCT318L,       MakeDeviceFunc(makeDevice<Mct31xDevice>) },
    { TYPEMCT360,        MakeDeviceFunc(makeDevice<Mct31xDevice>) },
    { TYPEMCT370,        MakeDeviceFunc(makeDevice<Mct31xDevice>) },
    { TYPEMCT410CL,      MakeDeviceFunc(makeDevice<Mct410Device>) },
    { TYPEMCT410FL,      MakeDeviceFunc(makeDevice<Mct410Device>) },
    { TYPEMCT410GL,      MakeDeviceFunc(makeDevice<Mct410Device>) },
    { TYPEMCT410IL,      MakeDeviceFunc(makeDevice<Mct410Device>) },
    { TYPEMCT420CL,      MakeDeviceFunc(makeDevice<Mct420Device>) },
    { TYPEMCT420CD,      MakeDeviceFunc(makeDevice<Mct420Device>) },
    { TYPEMCT420FL,      MakeDeviceFunc(makeDevice<Mct420Device>) },
    { TYPEMCT420FD,      MakeDeviceFunc(makeDevice<Mct420Device>) },
    { TYPEMCT430A,       MakeDeviceFunc(makeDevice<Mct470Device>) },
    { TYPEMCT430A3,      MakeDeviceFunc(makeDevice<Mct470Device>) },
    { TYPEMCT430S4,      MakeDeviceFunc(makeDevice<Mct470Device>) },
    { TYPEMCT430SL,      MakeDeviceFunc(makeDevice<Mct470Device>) },
    { TYPEMCT470,        MakeDeviceFunc(makeDevice<Mct470Device>) },
    { TYPEMCT440_2131B,  MakeDeviceFunc(makeDevice<Mct440_2131BDevice>) },
    { TYPEMCT440_2132B,  MakeDeviceFunc(makeDevice<Mct440_2132BDevice>) },
    { TYPEMCT440_2133B,  MakeDeviceFunc(makeDevice<Mct440_2133BDevice>) },
    //  Other PLC devices
    { TYPELCR3102,       MakeDeviceFunc(makeDevice<Lcr3102Device>) },
    { TYPEMCTBCAST,      MakeDeviceFunc(makeDevice<MctBroadcastDevice>) },
    //  RFN meters
    { TYPE_RFN410FL,     MakeDeviceFunc(makeDevice<Rfn410flDevice>) },
    { TYPE_RFN410FX,     MakeDeviceFunc(makeDevice<Rfn410fxDevice>) },
    { TYPE_RFN410FD,     MakeDeviceFunc(makeDevice<Rfn410fdDevice>) },
    { TYPE_RFN420FL,     MakeDeviceFunc(makeDevice<Rfn420flDevice>) },
    { TYPE_RFN420FX,     MakeDeviceFunc(makeDevice<Rfn420fxDevice>) },
    { TYPE_RFN420FD,     MakeDeviceFunc(makeDevice<Rfn420fdDevice>) },
    { TYPE_RFN420FRX,    MakeDeviceFunc(makeDevice<Rfn420frxDevice>) },
    { TYPE_RFN420FRD,    MakeDeviceFunc(makeDevice<Rfn420frdDevice>) },
    { TYPE_RFN410CL,     MakeDeviceFunc(makeDevice<Rfn410clDevice>) },
    { TYPE_RFN420CL,     MakeDeviceFunc(makeDevice<Rfn420clDevice>) },
    { TYPE_RFN420CD,     MakeDeviceFunc(makeDevice<Rfn420cdDevice>) },
    { TYPE_RFN430A3D,    MakeDeviceFunc(makeDevice<Rfn430a3dDevice>) },
    { TYPE_RFN430A3T,    MakeDeviceFunc(makeDevice<Rfn430a3tDevice>) },
    { TYPE_RFN430A3K,    MakeDeviceFunc(makeDevice<Rfn430a3kDevice>) },
    { TYPE_RFN430A3R,    MakeDeviceFunc(makeDevice<Rfn430a3rDevice>) },
    { TYPE_RFN430KV,     MakeDeviceFunc(makeDevice<Rfn430kvDevice>) },
    { TYPE_RFN430SL0,    MakeDeviceFunc(makeDevice<Rfn430sl0Device>) },
    { TYPE_RFN430SL1,    MakeDeviceFunc(makeDevice<Rfn430sl1Device>) },
    { TYPE_RFN430SL2,    MakeDeviceFunc(makeDevice<Rfn430sl2Device>) },
    { TYPE_RFN430SL3,    MakeDeviceFunc(makeDevice<Rfn430sl3Device>) },
    { TYPE_RFN430SL4,    MakeDeviceFunc(makeDevice<Rfn430sl4Device>) },
    { TYPE_RFN510FL,     MakeDeviceFunc(makeDevice<Rfn510flDevice>) },
    { TYPE_RFN520FAX,    MakeDeviceFunc(makeDevice<Rfn520faxDevice>) },
    { TYPE_RFN520FRX,    MakeDeviceFunc(makeDevice<Rfn520frxDevice>) },
    { TYPE_RFN520FAXD,   MakeDeviceFunc(makeDevice<Rfn520faxdDevice>) },
    { TYPE_RFN520FRXD,   MakeDeviceFunc(makeDevice<Rfn520frxdDevice>) },
    { TYPE_RFN530FAX,    MakeDeviceFunc(makeDevice<Rfn530faxDevice>) },
    { TYPE_RFN530FRX,    MakeDeviceFunc(makeDevice<Rfn530frxDevice>) },
    { TYPE_RFN530S4X,    MakeDeviceFunc(makeDevice<Rfn530s4xDevice>) },
    { TYPE_RFN530S4EAX,  MakeDeviceFunc(makeDevice<Rfn530s4eaxDevice>) },
    { TYPE_RFN530S4EAXR, MakeDeviceFunc(makeDevice<Rfn530s4eaxrDevice>) },
    { TYPE_RFN530S4ERX,  MakeDeviceFunc(makeDevice<Rfn530s4erxDevice>) },
    { TYPE_RFN530S4ERXR, MakeDeviceFunc(makeDevice<Rfn530s4erxrDevice>) },
    //  RF DA devices
    { TYPE_RFN1200,      MakeDeviceFunc(makeDevice<RfDaDevice>) },
    //  RF water meters
    { TYPE_RFW201,       MakeDeviceFunc(makeDevice<Rfw201Device>) },
    //  RF gas meters
    { TYPE_RFG201,       MakeDeviceFunc(makeDevice<Rfg201Device>) },
    { TYPE_RFG301,       MakeDeviceFunc(makeDevice<Rfg301Device>) },
    //  Electronic meters
    { TYPE_FULCRUM,      MakeDeviceFunc(makeDevice<CtiDeviceFulcrum>) },
    { TYPE_QUANTUM,      MakeDeviceFunc(makeDevice<CtiDeviceQuantum>) },
    { TYPE_VECTRON,      MakeDeviceFunc(makeDevice<CtiDeviceVectron>) },
    { TYPE_ALPHA_PPLUS,  MakeDeviceFunc(makeDevice<CtiDeviceAlphaPPlus>) },
    { TYPE_TDMARKV,      MakeDeviceFunc(makeDevice<CtiDeviceMarkV>) },
    { TYPE_ALPHA_A1,     MakeDeviceFunc(makeDevice<CtiDeviceAlphaA1>) },
    { TYPE_IPC_430S4E,   MakeDeviceFunc(makeDevice<CtiDeviceLandisGyrS4>) },
    { TYPE_LGS4,         MakeDeviceFunc(makeDevice<CtiDeviceLandisGyrS4>) },
    { TYPE_DR87,         MakeDeviceFunc(makeDevice<CtiDeviceDR87>) },
    { TYPE_KV2,          MakeDeviceFunc(makeDevice<CtiDeviceKV2>) },
    { TYPE_ALPHA_A3,     MakeDeviceFunc(makeDevice<CtiDeviceKV2>) },
    { TYPE_IPC_430SL,    MakeDeviceFunc(makeDevice<CtiDeviceSentinel>) },
    { TYPE_SENTINEL,     MakeDeviceFunc(makeDevice<CtiDeviceSentinel>) },
    { TYPE_IPC_410FL,    MakeDeviceFunc(makeDevice<Ipc410ALDevice>) },
    { TYPE_IPC_420FD,    MakeDeviceFunc(makeDevice<Ipc420ADDevice>) },
    { TYPE_FOCUS,        MakeDeviceFunc(makeDevice<CtiDeviceFocus>) },
    { TYPE_SIXNET,       MakeDeviceFunc(makeDevice<CtiDeviceSixnet>) },
    //  Load Management load groups
    { TYPE_LMGROUP_POINT,            MakeDeviceFunc(makeDevice<CtiDeviceGroupPoint>) },
    { TYPE_LMGROUP_EMETCON,          MakeDeviceFunc(makeDevice<CtiDeviceGroupEmetcon>) },
    { TYPE_LMGROUP_RIPPLE,           MakeDeviceFunc(makeDevice<CtiDeviceGroupRipple>) },
    { TYPE_LMGROUP_VERSACOM,         MakeDeviceFunc(makeDevice<CtiDeviceGroupVersacom>) },
    { TYPE_LMGROUP_EXPRESSCOM,       MakeDeviceFunc(makeDevice<CtiDeviceGroupExpresscom>) },
    { TYPE_LMGROUP_RFN_EXPRESSCOM,   MakeDeviceFunc(makeDevice<CtiDeviceGroupRfnExpresscom>) },
    { TYPE_LMGROUP_MCT,              MakeDeviceFunc(makeDevice<CtiDeviceGroupMCT>) },
    { TYPE_LMGROUP_GOLAY,            MakeDeviceFunc(makeDevice<CtiDeviceGroupGolay>) },
    { TYPE_LMGROUP_SADIGITAL,        MakeDeviceFunc(makeDevice<CtiDeviceGroupSADigital>) },
    { TYPE_LMGROUP_SA105,            MakeDeviceFunc(makeDevice<CtiDeviceGroupSA105>) },
    { TYPE_LMGROUP_SA205,            MakeDeviceFunc(makeDevice<CtiDeviceGroupSA205>) },
    { TYPE_LMGROUP_SA305,            MakeDeviceFunc(makeDevice<CtiDeviceGroupSA305>) },
    //  Capacitor bank controllers
    { TYPE_CBCDNP,        MakeDeviceFunc(makeDevice<CbcDnpDevice>) },
    { TYPE_CBCLOGICAL,    MakeDeviceFunc(makeDevice<CbcLogicalDevice>) },
    { TYPE_CBC7020,       MakeDeviceFunc(makeDevice<Cbc7020Device>) },
    { TYPE_CBC8020,       MakeDeviceFunc(makeDevice<Cbc8020Device>) },
    { TYPE_CBC7010,       MakeDeviceFunc(makeDevice<CtiDeviceCBC>) },
    { TYPE_FISHERPCBC,    MakeDeviceFunc(makeDevice<CtiDeviceCBC>) },
    { TYPE_VERSACOMCBC,   MakeDeviceFunc(makeDevice<CtiDeviceCBC>) },
    { TYPE_EXPRESSCOMCBC, MakeDeviceFunc(makeDevice<CtiDeviceCBC>) },
    //  Smart sensors
    { TYPE_FCI,              MakeDeviceFunc(makeDevice<CtiDeviceGridAdvisor>) },
    { TYPE_NEUTRAL_MONITOR,  MakeDeviceFunc(makeDevice<CtiDeviceGridAdvisor>) },
    //  System devices
    { TYPE_MACRO,        MakeDeviceFunc(makeDevice<CtiDeviceMacro>) },
    { TYPE_SYSTEM,       MakeDeviceFunc(makeDevice<CtiDeviceSystem>) },
    //  { TYPE_VIRTUAL_SYSTEM, ) }, // These are created by the porter thread which manages them.
    };

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
    string type;
    string category;

    rdr["category"]  >> category;
    rdr["type"]  >> type;

    boost::algorithm::to_lower(category);

    if( category == "route" )
    {
        if(getDebugLevel() & DEBUGLEVEL_FACTORY)
        {
            CTILOG_DEBUG(dout, "Creating a Route of type "<< type );
        }

        const int RteType = resolveRouteType(type);

        switch(RteType)
        {
            case RouteTypeCCU:
            {
                return new CtiRouteCCU;
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
                return new CtiRouteXCU;
            }
            case RouteTypeMacro:
            {
                return new Cti::Routes::MacroRoute;
            }
            case RouteTypeVersacom:
            {
                return new CtiRouteVersacom;
            }
            case RouteTypeExpresscom:
            {
                return new CtiRouteExpresscom;
            }
            case RouteTypeInvalid:
            default:
            {
                CTILOG_ERROR(dout, "Route Factory has failed to produce for type "<< type <<"!");
            }
        }
    }

    return 0;
}

namespace {
const std::set<int> carrierLpDeviceTypes = {
        TYPELMT2,
        TYPEDCT501,
        TYPEMCT240,
        TYPEMCT242,
        TYPEMCT248,
        TYPEMCT250,
        TYPEMCT310IL,
        TYPEMCT318L,
        TYPEMCT410CL,
        TYPEMCT410FL,
        TYPEMCT410GL,
        TYPEMCT410IL,
        TYPEMCT420CL,
        TYPEMCT420CD,
        TYPEMCT420FL,
        TYPEMCT420FD,
        TYPEMCT430A,
        TYPEMCT430A3,
        TYPEMCT430S4,
        TYPEMCT430SL,
        TYPEMCT470 };

const std::set<int> dnpDeviceTypes = {
        TYPE_DNPRTU,
        TYPE_CBC7020,
        TYPE_CBC8020,
        TYPE_CBCDNP };
}

DLLEXPORT bool isCarrierLPDeviceType(const int type)
{
    return carrierLpDeviceTypes.count(type);
}

DLLEXPORT bool isDnpDeviceType(const int type)
{
    return dnpDeviceTypes.count(type);
}
