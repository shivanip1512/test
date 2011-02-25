/*-----------------------------------------------------------------------------*
*
* File:   dev_fulcrum
*
* Class:  CtiDeviceFulcrum
* Date:   09/07/2000
*
* Author: David Sutton
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/RTDB/INCLUDE/dev_fulcrum.h-arc  $
* REVISION     :  $Revision: 1.11.10.2 $
* DATE         :  $Date: 2008/11/17 23:06:32 $
*
* Copyright (c) 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __DEV_FULCRUM_H__
#define __DEV_FULCRUM_H__
#pragma warning( disable : 4786 )



#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include "types.h"
#include "os2_2w32.h"
#include "dsm2.h"
#include "dev_meter.h"
#include "dev_schlum.h"

#pragma pack(push, schlumberger_packing, 1)

typedef struct {
   FLOAT    PeakValue;
   BYTE     Minute;
   BYTE     Hour;
   BYTE     Day;
   BYTE     Month;
   BYTE     Year;
} FulcrumMaximumRegister_t;

typedef struct {
   FLOAT    RateE_Energy;
   FLOAT    RateA_Energy;
   FLOAT    RateB_Energy;
   FLOAT    RateC_Energy;
   FLOAT    RateD_Energy;
} FulcrumTotalEnergyRegister_t;

typedef struct {
   FulcrumTotalEnergyRegister_t     Energy;
   FLOAT                            RateE_IntEnergy;
   FLOAT                            Unused;
} FulcrumEnergyRegister_t;

typedef struct {
   FLOAT                      Instantaneous;
   FLOAT                      TotalPreviousInterval;
   FulcrumMaximumRegister_t   TotalMaximum;
   FulcrumMaximumRegister_t   A_Maximum;
   FulcrumMaximumRegister_t   B_Maximum;
   FulcrumMaximumRegister_t   C_Maximum;
   FulcrumMaximumRegister_t   D_Maximum;
   FulcrumMaximumRegister_t   Peak1;
   FulcrumMaximumRegister_t   Peak2;
   FulcrumMaximumRegister_t   Peak3;
   FulcrumMaximumRegister_t   Peak4;
   FulcrumMaximumRegister_t   Peak5;
   FLOAT                      Coincident;
   FLOAT                      TotalCumulative;
   FLOAT                      A_Cumulative;
   FLOAT                      B_Cumulative;
   FLOAT                      C_Cumulative;
   FLOAT                      D_Cumulative;
   FLOAT                      TotalContinuousCumulative;
   FLOAT                      A_ContinuousCumulative;
   FLOAT                      B_ContinuousCumulative;
   FLOAT                      C_ContinuousCumulative;
   FLOAT                      D_ContinuousCumulative;
} FulcrumDemandRegister_t;


typedef struct {
   FLOAT                      INSTPF;           // Instantaneous Power factor
   FLOAT                      AVGPF;            // Average Power factor
   FLOAT                      PREVPF;           // Previous Power factor
   FulcrumMaximumRegister_t   MinRate_E;
   FulcrumMaximumRegister_t   MinRate_A;
   FulcrumMaximumRegister_t   MinRate_B;
   FulcrumMaximumRegister_t   MinRate_C;
   FulcrumMaximumRegister_t   MinRate_D;
   FLOAT                      WHatDR;           // Watthour reading at demand reset
   FLOAT                      VAHatDR;          // VAhour reading at demand reset
   FLOAT                      PFCoincident;     // PF minimum coincident register value
} FulcrumPFRegister_t;

typedef struct {
   FLOAT    TotalVolts;
   FLOAT    A_Volts;
   FLOAT    B_Volts;
   FLOAT    C_Volts;
   FLOAT    TotalAmps;
   FLOAT    A_Amps;
   FLOAT    B_Amps;
   FLOAT    C_Amps;
} FulcrumInstantaneousRegister_t;

typedef struct {
   BYTE     DayOfWeek;
   BYTE     Seconds;
   BYTE     Minutes;
   BYTE     Hours;
   BYTE     DayOfMonth;
   BYTE     Month;
   BYTE     Year;       // Mod-100 year anyway....
} FulcrumRealTimeRegister_t;

typedef struct {
   USHORT                     EnergyRegisterNumber;
   FLOAT                      PulseWeight;
   BYTE                       NotUsed[10];
} FulcrumMMProgram_t;

typedef struct {
   FulcrumMMProgram_t         Program[8];             // 128
   USHORT                     RecordLength;           //   2
   BYTE                       LogicalStart[3];        //   3
   BYTE                       LogicalEnd[3];          //   3
   BYTE                       ActualEnd[3];           //   3
   BYTE                       CurrentLogical[3];      //   3
   BYTE                       Reserved01[3];          //   3
   USHORT                     CurrentRecord;          //   2
   BYTE                       CurrentInterval;        //   1
   BYTE                       NumberOfChannels;       //   1
   BYTE                       Reserved02;             //   1
   BYTE                       IntervalLength;         //   1
} FulcrumMMConfig_t;

typedef struct
{
   UCHAR                            ActivePhaseStatus;
   USHORT                           DemandResetCount;
   USHORT                           OutageCount;
   USHORT                           PhaseOutageCount;

   BYTE                             MeterId[9];
   BYTE                             SWRev[6];
   BYTE                             FWRev[6];
   BYTE                             ProgId[2];
   BYTE                             UnitType[3];
   BYTE                             UnitId[8];

   FLOAT                            RegisterMultiplier;

   FulcrumEnergyRegister_t          Watthour;
   FulcrumEnergyRegister_t          VARhourLag;
   FulcrumEnergyRegister_t          VAhour;
   FulcrumEnergyRegister_t          Qhour;
   FulcrumEnergyRegister_t          VARhourTotal;
   FulcrumEnergyRegister_t          V2hour;
   FulcrumEnergyRegister_t          Amphour;

   FulcrumDemandRegister_t          WattsDemand;
   FulcrumDemandRegister_t          VARLagDemand;
   FulcrumDemandRegister_t          VADemand;

   FulcrumPFRegister_t              PowerFactor;

   FulcrumInstantaneousRegister_t   InstReg;

   FulcrumRealTimeRegister_t        TimeDate;

} FulcrumScanData_t;

typedef struct {
   time_t                     LastFileTime;
   time_t                     RecordTime;
   time_t                     NextRecordTime;
   ULONG                      porterLPTime;
   ULONG                      Flags;               // Misc Flags
   FulcrumMMConfig_t          MMConfig;
   BYTE                       MMBuffer[1600];
}  FulcrumLoadProfileMessage_t;                    // To fit in a DIALREPLY we cannot be > 2kB


#pragma pack(pop, schlumberger_packing)     // Restore the prior packing alignment..

class IM_EX_DEVDB CtiDeviceFulcrum : public CtiDeviceSchlumberger
{
private:

    typedef CtiDeviceSchlumberger Inherited;

    CtiDeviceFulcrum & operator=(const CtiDeviceFulcrum & aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - operator=() is invalid for device \"" << getName() << "\" **** " << FO(__FILE__) << " (" << __LINE__ << ")" << endl;
        }

        return *this;
    }

    CtiDeviceFulcrum (const CtiDeviceFulcrum & aRef)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** Checkpoint - copy constructor is invalid for device \"" << getName() << "\" **** " << FO(__FILE__) << " (" << __LINE__ << ")" << endl;
        }
    }

protected:

public:

   // default constructor that takes 2 optional parameters
   CtiDeviceFulcrum ( BYTE         *dataPtr  = NULL,
                      BYTE         *mmPtr  = NULL,
                      BYTE         *timeDatePtr = NULL,
                      BYTE         *mmlProfilePtr = NULL,
                      BYTE         *mmlProfileInputPtr = NULL,
                      BYTE         *lProfilePtr = NULL,
                      ULONG        totalByteCount = 0 ) :
      CtiDeviceSchlumberger(dataPtr,
                     mmPtr,
                     timeDatePtr,
                     mmlProfilePtr,
                     mmlProfileInputPtr,
                     lProfilePtr,
                     totalByteCount)
   {
      setRetryAttempts(SCHLUMBERGER_RETRIES);
   }

   virtual ~CtiDeviceFulcrum()
   {
      // all the databuffers are destroyed in base if needed
   }

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   virtual INT GeneralScan(CtiRequestMsg *pReq,
                           CtiCommandParser &parse,
                           OUTMESS *&OutMessage,
                           list< CtiMessage* > &vgList,
                           list< CtiMessage* > &retList,
                           list< OUTMESS* > &outList,
                           INT ScanPriority = MAXPRIORITY - 4);

   // interrogation routines
   virtual INT generateCommandHandshake (CtiXfer  &Transfer, list< CtiMessage* > &traceList);
   virtual INT generateCommand    (CtiXfer  &Transfer, list< CtiMessage* > &traceList);
   virtual INT generateCommandScan (CtiXfer  &Transfer, list< CtiMessage* > &traceList);
   virtual INT generateCommandLoadProfile (CtiXfer  &Transfer, list< CtiMessage* > &traceList);
   virtual INT generateCommandSelectMeter( CtiXfer  &Transfer, list< CtiMessage* > &traceList);

   virtual INT decodeResponse (CtiXfer &Transfer,INT commReturnValue, list< CtiMessage* > &traceList);
   virtual INT decodeResponseHandshake (CtiXfer &Transfer, INT commReturnValue, list< CtiMessage* > &traceList);
   virtual INT decodeResponseScan (CtiXfer  &Transfer,INT       commReturnValue, list< CtiMessage* > &traceList);
   virtual INT decodeResponseSelectMeter ( CtiXfer  &Transfer, INT commReturnValue, list< CtiMessage* > &traceList);
   virtual INT decodeResponseLoadProfile (CtiXfer  &Transfer,INT commReturnValue, list< CtiMessage* > &traceList);

   virtual INT reformatDataBuffer (BYTE *aInMessBuffer, ULONG &aBytesReceived);
   virtual INT copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes);

   virtual INT allocateDataBins (OUTMESS *outMess);

   virtual INT decodeResultScan ( INMESS *InMessage,
                          CtiTime &TimeNow,
                          list< CtiMessage* >   &vgList,
                          list< CtiMessage* > &retList,
                          list< OUTMESS* > &outList);

   virtual INT decodeResultLoadProfile ( INMESS *InMessage,
                                 CtiTime &TimeNow,
                                 list< CtiMessage* >   &vgList,
                                 list< CtiMessage* > &retList,
                                 list< OUTMESS* > &outList);

   BOOL getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, CtiTime &peak,  FulcrumScanData_t *aScanData);
   INT ResultDisplay (INMESS *InMessage);
};

#endif // #ifndef __DEV_FULCRUM_H__

