/*-----------------------------------------------------------------------------*
*
* File:   desolvers
*
* Date:   8/16/2001
*
* Author : Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/desolvers.cpp-arc  $
* REVISION     :  $Revision: 1.30 $
* DATE         :  $Date: 2005/02/10 23:23:44 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include <rw/db/db.h>
#include <rw\cstring.h>

#include "desolvers.h"
#include "dsm2.h"
#include "resolvers.h"
#include "devicetypes.h"
#include "pointtypes.h"
#include "logger.h"

RWCString desolveScanType( LONG scanType )
{
/*
   ScanRateGeneral = 0,
   ScanRateAccum,
   ScanRateStatus,
   ScanRateIntegrity,
   ScanRateInvalid
*/

   RWCString Ret;

   if(scanType == ScanRateGeneral)
   {
      Ret = SCANRATE_GENERAL;
   }
   else if(scanType == ScanRateAccum)
   {
      Ret = SCANRATE_ACCUM;
   }
   else if(scanType == ScanRateIntegrity)
   {
      Ret = SCANRATE_INTEGRITY;
   }
//   else if(scanType == ScanRateGeneral)
   else if(scanType == ScanRateStatus)
   {
      Ret = SCANRATE_STATUS;
   }
   else if(scanType == ScanRateGeneral)
   {
      Ret = SCANRATE_EXCEPTION;
   }
   else
   {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "Unsupported scan rate type " << endl;
      Ret = SCANRATE_INVALID;
   }

   return Ret;
}

RWCString desolveDeviceWindowType( LONG aType )
{
   RWCString Ret;

   if(aType == DeviceWindowScan)
   {
      Ret = DEVICE_WINDOW_TYPE_SCAN;
   }
   else if(aType == DeviceWindowPeak)
   {
      Ret = DEVICE_WINDOW_TYPE_PEAK;
   }
   else if(aType == DeviceWindowAlternateRate)
   {
      Ret = DEVICE_WINDOW_TYPE_ALTERNATE_RATE;
   }
   else
   {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "Unsupported device window type " << endl;
        Ret = DEVICE_WINDOW_TYPE_INVALID;
   }

   return Ret;
}


RWCString desolveStatisticsType( INT statType )
{
   RWCString Ret;

   if(statType == StatTypeMonthly)
   {
      Ret = STATTYPE_MONTHLY;
   }
   else if(statType == StatTypeHourly)
   {
      Ret = STATTYPE_HOURLY;
   }
   else if(statType == StatType24Hour)
   {
      Ret = STATTYPE_DAILY;
   }
   else
   {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
      dout << "Unsupported statistics collection type " << endl;
      Ret = STATTYPE_INVALID;
   }

   return Ret;

}

RWCString desolveAmpUseType( INT useType )
{
   RWCString autype;

   if( useType == RouteAmpAlternating )
   {
      autype = AMPUSE_ATLTERNATE;
   }
   else if(useType == RouteAmpAltFail)
   {
      autype = AMPUSE_WITHFAIL;
   }
   else if(useType == RouteAmpDefault1Fail2)
   {
      autype = AMPUSE_DEFAULTONE;
   }
   else if(useType == RouteAmpDefault2Fail1)
   {
      autype = AMPUSE_DEFAULTTWO;
   }
   else if(useType == RouteAmp1)
   {
      autype = AMPUSE_AMPONE;
   }
   else if(useType == RouteAmp2)
   {
      autype = AMPUSE_AMPTWO;
   }
   else
   {
      autype = AMPUSE_DEFAULTTWO;
   }

   return autype;

}


RWCString desolveDeviceType( INT aType )
{
    RWCString Ret;


   if(aType == TYPE_CCU711)
   {
      Ret = "ccu-711";
   }
   else if(aType == TYPE_CCU710)
   {
      Ret = "ccu-710a";
   }
   else if(aType == TYPE_CCU700)
   {
      Ret = "ccu-700";
   }
   else if(aType == TYPE_REPEATER900)
   {
      Ret = "repeater";
   }
   else if(aType == TYPE_REPEATER800)
   {
      Ret = "repeater 800";
   }
   else if(aType == TYPE_ILEXRTU)
   {
      Ret = "rtu-ilex";
   }
   else if(aType == TYPE_WELCORTU)
   {
      Ret = "rtu-welco";
   }
   else if(aType == TYPE_SES92RTU)
   {
      Ret = "rtu-ses92";
   }
   else if(aType == TYPE_DNPRTU)
   {
      Ret = "rtu-dnp";
   }
   else if(aType == TYPE_DARTRTU)
   {
      Ret = "rtu-dart";
   }
   else if(aType == TYPE_ION7330)
   {
       Ret = "ion-7330";
   }
   else if(aType == TYPE_ION7700)
   {
       Ret = "ion-7700";
   }
   else if(aType == TYPE_ION8300)
   {
       Ret = "ion-8300";
   }
   else if(aType == TYPE_LCU415)
   {
      Ret = "lcu-415";
   }
   else if(aType == TYPE_LCU415LG)
   {
      Ret = "lcu-lg";
   }
   else if(aType == TYPE_LCU415ER)
   {
      Ret = "lcu-eastriver";
   }
   else if(aType == TYPE_LCUT3026)
   {
      Ret = "lcu-t026";
   }
   else if(aType == TYPE_TCU5000)
   {
      Ret = "tcu-5000";
   }
   else if(aType == TYPE_TCU5500)
   {
      Ret = "tcu-5500";
   }
   else if(aType == TYPE_TDMARKV)
   {
      Ret = "transdata mark-v";
   }
   else if(aType == TYPE_DAVIS)
   {
      Ret = "davisweather";
   }
   else if(aType == TYPE_ALPHA_PPLUS)
   {
      Ret = "alpha power plus";
   }
   else if(aType == TYPE_ALPHA_A1)
   {
      Ret = "alpha a1";
   }
   else if(aType == TYPE_FULCRUM)
   {
      Ret = "fulcrum";
   }
   else if(aType == TYPE_QUANTUM)
   {
      Ret = "quantum";
   }
   else if(aType == TYPE_VECTRON)
   {
      Ret = "vectron";
   }
   else if(aType == TYPE_LGS4)
   {
      Ret = "landis-gyr s4";
   }
   else if(aType == TYPE_DR87)
   {
      Ret = "dr-87";
   }
   else if(aType == TYPE_KV2)
   {
      Ret = "kv2";
   }
   else if(aType == TYPE_ALPHA_A3)
   {
      Ret = "alpha a3";
   }
   else if(aType == TYPE_SENTINEL)
   {
      Ret = "sentinel";
   }
   else if(aType == TYPEDCT501)
   {
      Ret = "dct-501";
   }
   else if(aType == TYPEMCT210)
   {
      Ret = "mct-210";
   }
   else if(aType == TYPEMCT212)
   {
      Ret = "mct-212";
   }
   else if(aType == TYPEMCT213)
   {
      Ret = "mct-213";
   }
   else if(aType == TYPEMCT224)
   {
      Ret = "mct-224";
   }
   else if(aType == TYPEMCT226)
   {
      Ret = "mct-226";
   }
   else if(aType == TYPEMCT240)
   {
      Ret = "mct-240";
   }
   else if(aType == TYPEMCT242)
   {
      Ret = "mct-242";
   }
   else if(aType == TYPEMCT248)
   {
      Ret = "mct-248";
   }
   else if(aType == TYPEMCT250)
   {
      Ret = "mct-250";
   }
   else if(aType == TYPEMCT310)
   {
      Ret = "mct-310";
   }
   else if(aType == TYPEMCT310ID)
   {
      Ret = "mct-310id";
   }
   else if(aType == TYPEMCT310IL)
   {
      Ret = "mct-310il";
   }
   else if(aType == TYPEMCT310IDL)
   {
      Ret = "mct-310idl";
   }
   else if(aType == TYPEMCT318)
   {
      Ret = "mct-318";
   }
   else if(aType == TYPEMCT360)
   {
      Ret = "mct-360";
   }
   else if(aType == TYPEMCT370)
   {
      Ret = "mct-370";
   }
   else if(aType == TYPEMCT410)
   {
      Ret = "mct-410ile";
   }
   else if(aType == TYPELMT2)
   {
      Ret = "lmt-2";
   }
   else if(aType == TYPE_SIXNET)
   {
      Ret = "sixnet";
   }
   else if(aType == TYPE_LMGROUP_EMETCON)
   {
      Ret = "emetcon group";
   }
   else if(aType == TYPE_LMGROUP_POINT)
   {
      Ret = "point group";
   }
   else if(aType == TYPE_LMGROUP_RIPPLE)
   {
      Ret = "ripple group";
   }
   else if(aType == TYPE_LMGROUP_VERSACOM)
   {
      Ret = "versacom group";
   }
   else if(aType == TYPE_LMGROUP_EXPRESSCOM)
   {
      Ret = "expresscom group";
   }
   else if(aType == TYPE_LMGROUP_ENERGYPRO)
   {
      Ret = "energypro group";
   }
   else if(aType == TYPE_LMGROUP_MCT)
   {
      Ret = "mct group";
   }
   else if(aType == TYPE_MACRO)
   {
      Ret = "macro group";
   }
   else if(aType == TYPEVERSACOMCBC)
   {
      Ret = "cbc versacom";
   }
   else if(aType == TYPEEXPRESSCOMCBC)
   {
      Ret = "cbc expresscom";
   }
   else if(aType == TYPEFISHERPCBC)
   {
      Ret = "cbc fp-2800";
   }
   else if(aType == TYPECBC6510)
   {
      Ret = "cbc 6510";
   }
   else if(aType == TYPE_TAPTERM)
   {
      Ret = "tap terminal";
   }
   else if(aType == TYPE_WCTP)
   {
      Ret = "wctp terminal";
   }
   else if(aType == TYPE_LMPROGRAM_DIRECT)
   {
      Ret = "lm direct program";
   }
   else if(aType == TYPE_LMPROGRAM_CURTAILMENT)
   {
      Ret = "lm curtail program";
   }
   else if(aType == TYPE_LM_CONTROL_AREA)
   {
      Ret = "lm control area";
   }
   else if(aType == TYPE_CI_CUSTOMER)
   {
      Ret = "ci customer";
   }
   else if(aType == TYPE_LMPROGRAM_ENERGYEXCHANGE)
   {
      Ret = "lm energy exchange";
   }
   else if(aType == TYPE_DAVIS)
   {
      Ret = "davis weather";
   }
   else if(aType == TYPE_SYSTEM)
   {
      Ret = "system";
   }
   else if(aType == TYPE_LMGROUP_GOLAY)
   {
      Ret = "golay group";
   }
   else if(aType == TYPE_LMGROUP_SADIGITAL)
   {
      Ret = "sa-digital group";
   }
   else if(aType == TYPE_LMGROUP_SA105)
   {
      Ret = "sa-105 group";
   }
   else if(aType == TYPE_LMGROUP_SA205)
   {
      Ret = "sa-205 group";
   }
   else if(aType == TYPE_LMGROUP_SA305)
   {
      Ret = "sa-305 group";
   }
   else if(aType == TYPE_RTC)
   {
      Ret = "rtc";
   }
   else if(aType == TYPE_RTM)
   {
      Ret = "rtm";
   }
   else
   {
       {
          CtiLockGuard<CtiLogger> doubt_guard(dout);
          dout << "Unknown DEVICE type \"" << aType << "\" " << endl;
       }
   }

   Ret.toUpper();
   return Ret;
}

RWCString desolvePAOCategory( INT aCategory )
{
RWCString Ret;

    if(aCategory == PAO_CATEGORY_DEVICE)
    {
        Ret = "device";
    }
    else if(aCategory == PAO_CATEGORY_PORT)
    {
        Ret = "port";
    }
    else if(aCategory == PAO_CATEGORY_ROUTE)
    {
        Ret = "route";
    }
    else if(aCategory == PAO_CATEGORY_LOAD_MANAGEMENT)
    {
        Ret = "loadmanagement";
    }
    else if(aCategory == PAO_CATEGORY_CAP_CONTROL)
    {
        Ret = "capcontrol";
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unsupported pao category in: " << __FILE__ << " at: " << __LINE__ << endl;
        Ret = "invalid";
    }

return Ret;
}


RWCString desolvePortType( INT aType )
{
RWCString Ret;

    if(aType == PortTypeLocalDirect)
    {
        Ret = "local serial port";
    }
    else if(aType == PortTypeLocalDialup)
    {
        Ret = "local dialup";
    }
    else if(aType == PortTypeLocalDialBack)
    {
        Ret = "local dialback";
    }
    else if(aType == PortTypeTServerDirect)
    {
        Ret = "terminal server";
    }
    else if(aType == PortTypeTServerDialup)
    {
        Ret = "terminal server dialup";
    }
    else if(aType == PortTypeTServerDialBack)
    {
        Ret = "terminal server dialback";
    }
    else if(aType == PortTypePoolDialout)
    {
        Ret = "dialout pool";
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unsupported port type in: " << __FILE__ << " at: " << __LINE__ << endl;
        Ret = "invalid";
    }

return Ret;
}


RWCString desolveRouteType( INT aType )
{
RWCString Ret;

    if(aType == CCURouteType)
    {
        Ret = "ccu";
    }
    else if(aType == TCURouteType)
    {
        Ret = "tcu";
    }
    else if(aType == MacroRouteType)
    {
        Ret = "macro";
    }
    else if(aType == LCURouteType)
    {
        Ret = "lcu";
    }
    else if(aType == RepeaterRouteType)
    {
        Ret = "repeater";
    }
    else if(aType == VersacomRouteType)
    {
        Ret = "versacom";
    }
    else if(aType == TapRouteType)
    {
        Ret = "tap paging";
    }
    else if(aType == WCTPRouteType)
    {
        Ret = "wctp paging";
    }
    else if(aType == RTCRouteType)
    {
        Ret = "rtc";
    }
    else if(aType == SeriesVLMIRouteType)
    {
        Ret = "series 5 lmi";
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unsupported route type in: " << __FILE__ << " at: " << __LINE__ << endl;
        Ret = "invalid";
    }

return Ret;
}


RWCString desolveLoadManagementType( INT aType )
{
RWCString Ret;

    if(aType == TYPE_LMPROGRAM_DIRECT)
    {
        Ret = "lm direct program";
    }
    else if(aType == TYPE_LMPROGRAM_CURTAILMENT)
    {
        Ret = "lm curtail program";
    }
    else if(aType == TYPE_LM_CONTROL_AREA)
    {
        Ret = "lm control area";
    }
    else if(aType == TYPE_CI_CUSTOMER)
    {
        Ret = "ci customer";
    }
    else if(aType == TYPE_LMPROGRAM_ENERGYEXCHANGE)
    {
        Ret = "lm energy exchange";
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unsupported load management type in: " << __FILE__ << " at: " << __LINE__ << endl;
        Ret = "invalid";
    }

return Ret;
}


RWCString desolveCapControlType( INT aType )
{
RWCString Ret;

    if(aType == TYPE_CC_SUBSTATION_BUS)
    {
        Ret = "ccsubbus";
    }
    else if(aType == TYPE_CC_FEEDER)
    {
        Ret = "ccfeeder";
    }
    else
    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << "Unsupported cap control type in: " << __FILE__ << " at: " << __LINE__ << endl;
        Ret = "invalid";
    }

return Ret;
}



