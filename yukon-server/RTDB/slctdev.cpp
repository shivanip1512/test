/*-----------------------------------------------------------------------------*
*
* File:   slctdev
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/slctdev.cpp-arc  $
* REVISION     :  $Revision: 1.46 $
* DATE         :  $Date: 2005/07/11 18:30:28 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"


#include <rw/db/db.h>

#include "dev_710.h"
#include "dev_base.h"
#include "dev_macro.h"
#include "dev_cbc6510.h"
#include "dev_cbc.h"
#include "dev_ccu.h"
#include "dev_welco.h"
#include "dev_ilex.h"
#include "dev_seriesv.h"
#include "dev_lmi.h"
#include "dev_tcu.h"
#include "dev_meter.h"
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
#include "dev_grp_energypro.h"
#include "dev_grp_golay.h"
#include "dev_grp_mct.h"
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
#include "dev_dr87.h"
#include "dev_dct501.h"
#include "dev_mct.h"
#include "dev_mct210.h"
#include "dev_mct22X.h"
#include "dev_mct24X.h"
#include "dev_mct310.h"
#include "dev_mct31X.h"
#include "dev_mct410.h"
#include "dev_mct470.h"
#include "dev_mct_lmt2.h"
#include "dev_mct_broadcast.h"
#include "dev_kv2.h"
#include "dev_sentinel.h"
#include "dev_mark_v.h"
#include "dev_repeater800.h"
#include "dev_repeater.h"
#include "dev_rtc.h"
#include "dev_sixnet.h"
#include "rte_macro.h"
#include "rte_ccu.h"
#include "rte_xcu.h"
#include "rte_versacom.h"

#include "devicetypes.h"
#include "rtdb.h"
#include "dbaccess.h"
#include "resolvers.h"
#include "slctdev.h"
#include "yukon.h"


using namespace Cti;  //  in preparation for moving devices to their own namespace


DLLEXPORT CtiDeviceBase* DeviceFactory(RWDBReader &rdr)
{
    RWCString rwsType;
    RWCString rwsPseudo;

    INT      DevType;

    CtiDeviceBase *NewDevice = NULL;

    rdr["type"]  >> rwsType;

    if(getDebugLevel() & DEBUGLEVEL_FACTORY)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Creating a Device of type " << rwsType << endl;
    }

    DevType = resolveDeviceType(rwsType);

    switch(DevType)
    {
    case TYPE_WELCORTU:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceWelco;
            break;
        }
    case TYPE_ILEXRTU:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceILEX;
            break;
        }
    case TYPE_DARTRTU:
    case TYPE_DNPRTU:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new Device::DNP;
            break;
        }
    case TYPE_SERIESVRTU:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceSeriesV;
            break;
        }
    case TYPE_SERIESVLMIRTU:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceLMI;
            break;
        }
    case TYPE_ION7330:
    case TYPE_ION7700:
    case TYPE_ION8300:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceION;
            break;
        }
    case TYPE_TCU5000:
    case TYPE_TCU5500:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceTCU;
            break;
        }
    case TYPE_CCU711:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceCCU;
            break;
        }
    case TYPE_CCU710:
    case TYPE_CCU700:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceCCU710;
            break;
        }
    case TYPE_DAVIS:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceDavis;
            break;
        }
    case TYPE_SES92RTU:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceRemote;
            break;
        }
    case TYPEDCT501:
        {
            NewDevice = (CtiDeviceBase *) CTIDBG_new CtiDeviceDCT501;
            break;
        }
    case TYPELMT2:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT_LMT2;
            break;
        }
    case TYPEMCT210:     // S00095C
    case TYPEMCT213:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT210;
            break;
        }
    case TYPEMCT212:     // S0074E (sspec indicates a 213 too, but I know nothing)
    case TYPEMCT224:
    case TYPEMCT226:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT22X;
            break;
        }
    case TYPEMCT240:     // S00121B (240, 242, 248)
    case TYPEMCT242:
    case TYPEMCT248:
    case TYPEMCT250:     // S00111B (250)
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT24X;
            break;
        }
    case TYPEMCT310:
    case TYPEMCT310ID:
    case TYPEMCT310IDL:
    case TYPEMCT310IL:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT310;
            break;
        }
    case TYPEMCT318:
    case TYPEMCT318L:
    case TYPEMCT360:
    case TYPEMCT370:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT31X;
            break;
        }
    case TYPEMCT410:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT410;
            break;
        }
    case TYPEMCT470:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT470;
            break;
        }
    case TYPE_REPEATER800:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceRepeater800;
            break;
        }
    case TYPE_REPEATER900:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceRepeater900;
            break;
        }
    case TYPE_FULCRUM:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceFulcrum;
            break;
        }
    case TYPE_QUANTUM:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceQuantum;
            break;
        }
    case TYPE_VECTRON:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceVectron;
            break;
        }
    case TYPE_ALPHA_PPLUS:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceAlphaPPlus;
            break;
        }
    case TYPE_TDMARKV:
       {
           NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceMarkV;
           break;
       }
    case TYPE_ALPHA_A1:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceAlphaA1;
            break;
        }
    case TYPE_LGS4:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceLandisGyrS4;
            break;
        }
    case TYPE_DR87:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceDR87;
            break;
        }
    case TYPE_KV2:
    case TYPE_ALPHA_A3:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceKV2;
            break;
        }
    case TYPE_SENTINEL:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceSentinel;
            break;
        }

    case TYPE_SIXNET:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceSixnet;
            break;
        }
    case TYPE_TAPTERM:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceTapPagingTerminal;
            break;
        }
    case TYPE_SNPP:
    {
        NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceSnppPagingTerminal;
        break;
    }
    case TYPE_PAGING_RECEIVER:
    {
        NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDevicePagingReceiver;
        break;
    }
    case TYPE_TNPP:
    {
        NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceTnppPagingTerminal;
        break;
    }
    case TYPE_WCTP:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceWctpTerminal;
            break;
        }
    case TYPE_LMGROUP_EMETCON:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupEmetcon;
            break;
        }
    case TYPE_LMGROUP_RIPPLE:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupRipple;
            break;
        }
    case TYPE_LMGROUP_VERSACOM:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupVersacom;
            break;
        }
    case TYPE_LMGROUP_EXPRESSCOM:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupExpresscom;
            break;
        }
    case TYPE_LMGROUP_ENERGYPRO:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupEnergyPro;
            break;
        }
    case TYPE_LMGROUP_MCT:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupMCT;
            break;
        }
    case TYPE_MACRO:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceMacro;
            break;
        }
    case TYPE_SYSTEM:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceSystem;
            break;
        }
    case TYPECBC6510:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceCBC6510;
            break;
        }
    case TYPECBC7020:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new Device::CBC7020;
            break;
        }
    case TYPECBC7010:
    case TYPEFISHERPCBC:
    case TYPEVERSACOMCBC:
    case TYPEEXPRESSCOMCBC:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceCBC;
            break;
        }
    case TYPE_LCU415:
    case TYPE_LCU415LG:
    case TYPE_LCU415ER:
    case TYPE_LCUT3026:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceLCU(DevType);
            break;
        }
    case TYPEMCTBCAST:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCTBroadcast;
            break;
        }
    case TYPE_RTC:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceRTC;
            break;
        }
    case TYPE_RTM:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceRTM;
            break;
        }
    case TYPE_LMGROUP_GOLAY:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupGolay;
            break;
        }
    case TYPE_LMGROUP_SADIGITAL:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupSADigital;
            break;
        }
    case TYPE_LMGROUP_SA105:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupSA105;
            break;
        }
    case TYPE_LMGROUP_SA205:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupSA205;
            break;
        }
    case TYPE_LMGROUP_SA305:
        {
            NewDevice = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupSA305;
            break;
        }
    case TYPE_ENERGYPRO:
        {
            break;          // Nothing in here!     // These are created by the porter thread which manages them!
        }
    default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Device Factory has failed to produce for type " << rwsType << "!" << endl;
            break;
        }
    }

    return NewDevice;
}

DLLEXPORT CtiRouteBase* RouteFactory(RWDBReader &rdr)
{
    RWCString rwsType;
    RWCString category;

    INT      RteType;

    CtiRouteBase *Route = NULL;


    rdr["category"]  >> category;
    rdr["type"]  >> rwsType;

    category.toLower();

    if(category == RWCString("route"))
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
        case RouteTypeWCTP:
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
        case RouteTypeRepeater:
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

DLLEXPORT bool isADevice(CtiDeviceSPtr& pSp, void *arg)
{
    bool bRet = true;
    return bRet;
}

DLLEXPORT bool isNotADevice(CtiDeviceSPtr& pSp, void *arg)
{
    return !isADevice(pSp, arg);
}

DLLEXPORT bool isNotAScannableDevice(CtiDeviceSPtr& pDevice, void* d)
{
    return !isAScannableDevice(pDevice, d);
}

DLLEXPORT bool isAScannableDevice(CtiDeviceSPtr& pDevice, void* d)
{
    bool bRet = false;

    if(pDevice->isSingle())
    {
        CtiDeviceSingle* pUnique = (CtiDeviceSingle*)pDevice.get();

        // Return TRUE if it is NOT SET
        for(INT i = 0; i  < ScanRateInvalid; i++ )
        {
            if(pUnique->getScanRate(i) != -1)
            {
                bRet = true;              // I found a scan rate...
                break;
            }
        }

        if(!bRet && isCarrierLPDevice(pDevice))
        {
            for(int i = 0; i < CtiTableDeviceLoadProfile::MaxCollectedChannel; i++)
            {
                if(((CtiDeviceCarrier *)pUnique)->getLoadProfile().isChannelValid(i))
                {
                    bRet = true;
                    break;
                }
            }
        }
    }

    return(bRet);
}

DLLEXPORT BOOL isARoute(CtiRouteBase* pSp, void *arg)
{
    BOOL bRet = TRUE;

    return bRet;
}


DLLEXPORT RWBoolean isCarrierLPDevice(CtiDeviceSPtr &pDevice)
{
    BOOL result;

    switch(pDevice->getType())
    {
    case TYPELMT2:
    case TYPEDCT501:
    case TYPEMCT240:
    case TYPEMCT242:
    case TYPEMCT248:
    case TYPEMCT250:
    case TYPEMCT260:
    case TYPEMCT310IL:
    case TYPEMCT318L:
    case TYPEMCT410:
    case TYPEMCT470:
        result = TRUE;
        break;

    default:
        result = FALSE;
        break;
    }

    return result;
}

/*
 *Function Name:isNotScannable
 *
 *Description:
 *
 *  This function is for use in a find() call with a device manager object
 *  it will return TRUE for any device which is not a scannable device,
 *  or for which there is not currently a valid scan rate.
 *
 */
//  2001-dec-05 addition:  added special case for carrier load profile devices

DLLEXPORT RWBoolean isNotScannable( CtiDeviceSPtr& pDevice, void* d)
{
    RWBoolean bRet = TRUE;

    if(pDevice->isSingle())
    {
        CtiDeviceSingle* pUnique = (CtiDeviceSingle*)pDevice.get();

        // Return TRUE if it is NOT SET
        for(INT i = 0; i  < ScanRateInvalid; i++ )
        {
            if(pUnique->getScanRate(i) != -1)
            {
                bRet = FALSE;              // I found a scan rate...
                break;
            }
        }

        if(bRet && isCarrierLPDevice(pDevice))
        {
            for(int i = 0; i < CtiTableDeviceLoadProfile::MaxCollectedChannel; i++)
            {
                if(((CtiDeviceCarrier *)pUnique)->getLoadProfile().isChannelValid(i))
                {
                    bRet = FALSE;
                    break;
                }
            }
        }
    }

    return(bRet);
}

