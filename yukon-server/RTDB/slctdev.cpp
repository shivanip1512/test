/*-----------------------------------------------------------------------------*
*
* File:   slctdev
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/slctdev.cpp-arc  $
* REVISION     :  $Revision: 1.25 $
* DATE         :  $Date: 2003/12/18 02:08:20 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#pragma warning (disable : 4786)


#include <rw/db/db.h>

#include "dev_710.h"
#include "dev_base.h"
#include "dev_macro.h"
#include "dev_cbc6510.h"
#include "dev_cbc.h"
#include "dev_ccu.h"
#include "dev_welco.h"
#include "dev_ilex.h"
#include "dev_tcu.h"
#include "dev_meter.h"
#include "dev_schlum.h"
#include "dev_fulcrum.h"
#include "dev_ion.h"
#include "dev_lcu.h"
#include "dev_quantum.h"
#include "dev_vectron.h"
#include "dev_carrier.h"
#include "dev_tap.h"
#include "dev_wctp.h"
#include "dev_grp_emetcon.h"
#include "dev_grp_expresscom.h"
#include "dev_grp_energypro.h"
#include "dev_grp_mct.h"
#include "dev_grp_ripple.h"
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
#include "dev_mct_lmt2.h"
#include "dev_mct_broadcast.h"
#include "dev_kv2.h"
#include "dev_mark_v.h"
#include "dev_repeater800.h"
#include "dev_repeater.h"
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


DLLEXPORT CtiDeviceBase* DeviceFactory(RWDBReader &rdr)
{
    RWCString rwsType;
    RWCString rwsPseudo;

    INT      DevType;

    CtiDeviceBase *Device = NULL;

    rdr["type"]  >> rwsType;

    if(getDebugLevel() & 0x00000400)
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Creating a Device of type " << rwsType << endl;
    }

    DevType = resolveDeviceType(rwsType);

    switch(DevType)
    {
    case TYPE_WELCORTU:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceWelco;
            break;
        }
    case TYPE_ILEXRTU:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceILEX;
            break;
        }
    case TYPE_DARTRTU:
    case TYPE_DNPRTU:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceDNP;
            break;
        }
    case TYPE_ION7330:
    case TYPE_ION7700:
    case TYPE_ION8300:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceION;
            break;
        }
    case TYPE_TCU5000:
    case TYPE_TCU5500:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceTCU;
            break;
        }
    case TYPE_CCU711:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceCCU;
            break;
        }
    case TYPE_CCU710:
    case TYPE_CCU700:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceCCU710;
            break;
        }
    case TYPE_DAVIS:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceDavis;
            break;
        }
    case TYPE_SES92RTU:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceRemote;
            break;
        }
    case TYPEDCT501:
        {
            Device = (CtiDeviceBase *) CTIDBG_new CtiDeviceDCT501;
            break;
        }
    case TYPELMT2:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT_LMT2;
            break;
        }
    case TYPEMCT210:     // S00095C
    case TYPEMCT213:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT210;
            break;
        }
    case TYPEMCT212:     // S0074E (sspec indicates a 213 too, but I know nothing)
    case TYPEMCT224:
    case TYPEMCT226:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT22X;
            break;
        }
    case TYPEMCT240:     // S00121B (240, 242, 248)
    case TYPEMCT242:
    case TYPEMCT248:
    case TYPEMCT250:     // S00111B (250)
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT24X;
            break;
        }
    case TYPEMCT310:
    case TYPEMCT310ID:
    case TYPEMCT310IL:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT310;
            break;
        }
    case TYPEMCT318:
    case TYPEMCT318L:
    case TYPEMCT360:
    case TYPEMCT370:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT31X;
            break;
        }
    case TYPEMCT410:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCT410;
            break;
        }
    case TYPE_REPEATER800:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceRepeater800;
            break;
        }
    case TYPE_REPEATER900:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceRepeater900;
            break;
        }
    case TYPE_FULCRUM:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceFulcrum;
            break;
        }
    case TYPE_QUANTUM:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceQuantum;
            break;
        }
    case TYPE_VECTRON:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceVectron;
            break;
        }
    case TYPE_ALPHA_PPLUS:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceAlphaPPlus;
            break;
        }
    case TYPE_TDMARKV:
       {
           Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceMarkV;
           break;
       }
    case TYPE_ALPHA_A1:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceAlphaA1;
            break;
        }
    case TYPE_LGS4:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceLandisGyrS4;
            break;
        }
    case TYPE_DR87:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceDR87;
            break;
        }
    case TYPE_KV2:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceKV2;
            break;
        }

    case TYPE_SIXNET:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceSixnet;
            break;
        }
    case TYPE_TAPTERM:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceTapPagingTerminal;
            break;
        }
    case TYPE_WCTP:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceWctpTerminal;
            break;
        }
    case TYPE_LMGROUP_EMETCON:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupEmetcon;
            break;
        }
    case TYPE_LMGROUP_RIPPLE:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupRipple;
            break;
        }
    case TYPE_LMGROUP_VERSACOM:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupVersacom;
            break;
        }
    case TYPE_LMGROUP_EXPRESSCOM:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupExpresscom;
            break;
        }
    case TYPE_LMGROUP_ENERGYPRO:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupEnergyPro;
            break;
        }
    case TYPE_LMGROUP_MCT:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceGroupMCT;
            break;
        }
    case TYPE_MACRO:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceMacro;
            break;
        }
    case TYPE_SYSTEM:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceSystem;
            break;
        }
    case TYPECBC6510:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceCBC6510;
            break;
        }
    case TYPEFISHERPCBC:
    case TYPEVERSACOMCBC:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceCBC;
            break;
        }
    case TYPE_LCU415:
    case TYPE_LCU415LG:
    case TYPE_LCU415ER:
    case TYPE_LCUT3026:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceLCU(DevType);
            break;
        }
    case TYPEMCTBCAST:
        {
            Device = (CtiDeviceBase*) CTIDBG_new CtiDeviceMCTBroadcast;
            break;
        }
    default:
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Device Factory has failed to produce for type " << rwsType << "!" << endl;
            break;
        }
    }

    return Device;
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
        if(getDebugLevel() & 0x00000400)
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Creating a Route of type " << rwsType << endl;
        }

        RteType = resolveRouteType(rwsType);

        switch(RteType)
        {
        case CCURouteType:
            {
                Route = (CtiRouteBase*) CTIDBG_new CtiRouteCCU;
                break;
            }
        case LCURouteType:
        case TCURouteType:
        case TapRouteType:
        case WCTPRouteType:
            {
                Route = (CtiRouteBase*) CTIDBG_new CtiRouteXCU;
                break;
            }
        case MacroRouteType:
            {
                Route = (CtiRouteBase*) CTIDBG_new CtiRouteMacro;
                break;
            }
        case VersacomRouteType:
            {
                Route = (CtiRouteBase*) CTIDBG_new CtiRouteVersacom;
                break;
            }
        case RepeaterRouteType:
        case InvalidRouteType:
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

DLLEXPORT bool isADevice(CtiDeviceBase* pSp, void *arg)
{
    bool bRet = true;
    return bRet;
}

DLLEXPORT bool isNotADevice(CtiDeviceBase* pSp, void *arg)
{
    return !isADevice(pSp, arg);
}

DLLEXPORT bool isNotAScannableDevice(CtiDeviceBase *pDevice, void* d)
{
    return !isAScannableDevice(pDevice, d);
}

DLLEXPORT bool isAScannableDevice(CtiDeviceBase *pDevice, void* d)
{
    bool bRet = false;

    if(pDevice->isSingle())
    {
        CtiDeviceSingle* pUnique = (CtiDeviceSingle*)pDevice;

        // Return TRUE if it is NOT SET
        for(INT i = 0; i  < ScanRateInvalid; i++ )
        {
            if(pUnique->getScanRate(i) != -1)
            {
                bRet = true;              // I found a scan rate...
                break;
            }
        }

        if(!bRet && isCarrierLPDevice(pUnique))
        {
            for(int i = 0; i < MAX_COLLECTED_CHANNEL; i++)
            {
                if(((CtiDeviceMCT *)pUnique)->getLoadProfile().isChannelValid(i))
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


DLLEXPORT RWBoolean isCarrierLPDevice(CtiDeviceBase *pDevice)
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

DLLEXPORT RWBoolean isNotScannable( CtiRTDB< CtiDeviceBase >::val_pair vp, void* d)
{
    RWBoolean bRet = TRUE;

    CtiDevice *pDevice = vp.second;

    if(pDevice->isSingle())
    {
        CtiDeviceSingle* pUnique = (CtiDeviceSingle*)pDevice;

        // Return TRUE if it is NOT SET
        for(INT i = 0; i  < ScanRateInvalid; i++ )
        {
            if(pUnique->getScanRate(i) != -1)
            {
                bRet = FALSE;              // I found a scan rate...
                break;
            }
        }

        if(bRet && isCarrierLPDevice(pUnique))
        {
            for(int i = 0; i < MAX_COLLECTED_CHANNEL; i++)
            {
                if(((CtiDeviceMCT *)pUnique)->getLoadProfile().isChannelValid(i))
                {
                    bRet = FALSE;
                    break;
                }
            }
        }
    }

    return(bRet);
}

