
#pragma warning (disable : 4786)
/*-----------------------------------------------------------------------------*
*
* File:   slctdev
*
* Date:   7/23/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/slctdev.cpp-arc  $
* REVISION     :  $Revision: 1.11 $
* DATE         :  $Date: 2002/09/04 13:12:54 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/


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
#include "dev_lcu.h"
#include "dev_quantum.h"
#include "dev_vectron.h"
#include "dev_carrier.h"
#include "dev_tap.h"
#include "dev_wctp.h"
#include "dev_grp_emetcon.h"
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
#include "dev_mct_lmt2.h"
#include "dev_kv2.h"
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
            Device = (CtiDeviceBase*) new CtiDeviceWelco;
            break;
        }
    case TYPE_ILEXRTU:
        {
            Device = (CtiDeviceBase*) new CtiDeviceILEX;
            break;
        }
    case TYPE_DNPRTU:
        {
            Device = (CtiDeviceBase*) new CtiDeviceDNP;
            break;
        }
    case TYPE_TCU5000:
    case TYPE_TCU5500:
        {
            Device = (CtiDeviceBase*) new CtiDeviceTCU;
            break;
        }
    case TYPE_CCU711:
        {
            Device = (CtiDeviceBase*) new CtiDeviceCCU;
            break;
        }
    case TYPE_CCU710:
    case TYPE_CCU700:
        {
            Device = (CtiDeviceBase*) new CtiDeviceCCU710;
            break;
        }
    case TYPE_DAVIS:
        {
            Device = (CtiDeviceBase*) new CtiDeviceDavis;
            break;
        }
    case TYPE_SES92RTU:
        {
            Device = (CtiDeviceBase*) new CtiDeviceRemote;
            break;
        }
    case TYPEDCT501:
        {
            Device = (CtiDeviceBase *) new CtiDeviceDCT501;
            break;
        }
    case TYPELMT2:
        {
            Device = (CtiDeviceBase*) new CtiDeviceMCT_LMT2;
            break;
        }
    case TYPEMCT210:     // S00095C
    case TYPEMCT213:
        {
            Device = (CtiDeviceBase*) new CtiDeviceMCT210;
            break;
        }
    case TYPEMCT212:     // S0074E (sspec indicates a 213 too, but I know nothing)
    case TYPEMCT224:
    case TYPEMCT226:
        {
            Device = (CtiDeviceBase*) new CtiDeviceMCT22X;
            break;
        }
    case TYPEMCT240:     // S00121B (240, 242, 248)
    case TYPEMCT242:
    case TYPEMCT248:
    case TYPEMCT250:     // S00111B (250)
        {
            Device = (CtiDeviceBase*) new CtiDeviceMCT24X;
            break;
        }
    case TYPEMCT310:
    case TYPEMCT310ID:
    case TYPEMCT310IL:
        {
            Device = (CtiDeviceBase*) new CtiDeviceMCT310;
            break;
        }
    case TYPEMCT318:
    case TYPEMCT318L:
    case TYPEMCT360:
    case TYPEMCT370:
        {
            Device = (CtiDeviceBase*) new CtiDeviceMCT31X;
            break;
        }
    case TYPE_REPEATER800:
        {
            Device = (CtiDeviceBase*) new CtiDeviceRepeater800;
            break;
        }
    case TYPE_REPEATER900:
        {
            Device = (CtiDeviceBase*) new CtiDeviceRepeater900;
            break;
        }
    case TYPE_FULCRUM:
        {
            Device = (CtiDeviceBase*) new CtiDeviceFulcrum;
            break;
        }
    case TYPE_QUANTUM:
        {
            Device = (CtiDeviceBase*) new CtiDeviceQuantum;
            break;
        }
    case TYPE_VECTRON:
        {
            Device = (CtiDeviceBase*) new CtiDeviceVectron;
            break;
        }
    case TYPE_ALPHA_PPLUS:
        {
            Device = (CtiDeviceBase*) new CtiDeviceAlphaPPlus;
            break;
        }
    case TYPE_ALPHA_A1:
        {
            Device = (CtiDeviceBase*) new CtiDeviceAlphaA1;
            break;
        }
    case TYPE_LGS4:
        {
            Device = (CtiDeviceBase*) new CtiDeviceLandisGyrS4;
            break;
        }
    case TYPE_DR87:
        {
            Device = (CtiDeviceBase*) new CtiDeviceDR87;
            break;
        }
    case TYPE_KV2:
        {
            Device = (CtiDeviceBase*) new CtiDeviceKV2;
            break;
        }

    case TYPE_SIXNET:
        {
            Device = (CtiDeviceBase*) new CtiDeviceSixnet;
            break;
        }
    case TYPE_TAPTERM:
        {
            Device = (CtiDeviceBase*) new CtiDeviceTapPagingTerminal;
            break;
        }
    case TYPE_WCTP:
        {
            Device = (CtiDeviceBase*) new CtiDeviceWctpTerminal;
            break;
        }
    case TYPE_LMGROUP_EMETCON:
        {
            Device = (CtiDeviceBase*) new CtiDeviceGroupEmetcon;
            break;
        }
    case TYPE_LMGROUP_RIPPLE:
        {
            Device = (CtiDeviceBase*) new CtiDeviceGroupRipple;
            break;
        }
    case TYPE_LMGROUP_VERSACOM:
        {
            Device = (CtiDeviceBase*) new CtiDeviceGroupVersacom;
            break;
        }
    case TYPE_MACRO:
        {
            Device = (CtiDeviceBase*) new CtiDeviceMacro;
            break;
        }
    case TYPE_SYSTEM:
        {
            Device = (CtiDeviceBase*) new CtiDeviceSystem;
            break;
        }
    case TYPECBC6510:
        {
            Device = (CtiDeviceBase*) new CtiDeviceCBC6510;
            break;
        }
    case TYPEFISHERPCBC:
    case TYPEVERSACOMCBC:
        {
            Device = (CtiDeviceBase*) new CtiDeviceCBC;
            break;
        }
    case TYPE_LCU415:
    case TYPE_LCU415LG:
    case TYPE_LCU415ER:
    case TYPE_LCUT3026:
        {
            Device = (CtiDeviceBase*) new CtiDeviceLCU(DevType);
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
                Route = (CtiRouteBase*) new CtiRouteCCU;
                break;
            }
        case LCURouteType:
        case TCURouteType:
        case TapRouteType:
        case WCTPRouteType:
            {
                Route = (CtiRouteBase*) new CtiRouteXCU;
                break;
            }
        case MacroRouteType:
            {
                Route = (CtiRouteBase*) new CtiRouteMacro;
                break;
            }
        case VersacomRouteType:
            {
                Route = (CtiRouteBase*) new CtiRouteVersacom;
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

DLLEXPORT BOOL isADevice(CtiDeviceBase* pSp, void *arg)
{
    BOOL bRet = TRUE;

    return bRet;
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

        if(isCarrierLPDevice(pUnique))
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

DLLEXPORT RWBoolean isScannable(CtiDeviceBase *pDevice, void* d)
{
    RWBoolean bRet = FALSE;

    if(pDevice->isSingle())
    {
        CtiDeviceSingle* pUnique = (CtiDeviceSingle*)pDevice;

        // Return TRUE if it is NOT SET
        for(INT i = 0; i  < ScanRateInvalid; i++ )
        {
            if(pUnique->getScanRate(i) != -1)
            {
                bRet = TRUE;              // I found a scan rate...
                break;
            }
        }

        if(isCarrierLPDevice(pUnique))
        {
            for(int i = 0; i < MAX_COLLECTED_CHANNEL; i++)
            {
                if(((CtiDeviceMCT *)pUnique)->getLoadProfile().isChannelValid(i))
                {
                    bRet = TRUE;
                    break;
                }
            }
        }
    }

    return(bRet);
}

