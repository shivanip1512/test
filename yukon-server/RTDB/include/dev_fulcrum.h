#pragma once

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
    // WORKAROUND:
    // Declare copy ctor and assignment operator private with no implementation
    // MSVC2008 and 2010 do not prevent copying if a class is DLLEXPORT
    // http://stackoverflow.com/questions/7482891/inheriting-noncopyable-has-no-effect-in-dllexport-classes
    CtiDeviceFulcrum(const CtiDeviceFulcrum&);
    CtiDeviceFulcrum& operator=(const CtiDeviceFulcrum&);

    typedef CtiDeviceSchlumberger Inherited;

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

   /*
    *  These guys initiate a scan based upon the type requested.
    */

   YukonError_t GeneralScan(CtiRequestMsg *pReq, CtiCommandParser &parse, OUTMESS *&OutMessage, CtiMessageList &vgList, CtiMessageList &retList, OutMessageList &outList,
                                 INT ScanPriority = MAXPRIORITY - 4) override;

   // interrogation routines
   YukonError_t generateCommandHandshake  (CtiXfer  &Transfer, CtiMessageList &traceList) override;
   YukonError_t generateCommand           (CtiXfer  &Transfer, CtiMessageList &traceList) override;
   YukonError_t generateCommandScan       (CtiXfer  &Transfer, CtiMessageList &traceList) override;
   YukonError_t generateCommandLoadProfile(CtiXfer  &Transfer, CtiMessageList &traceList) override;
   YukonError_t generateCommandSelectMeter(CtiXfer  &Transfer, CtiMessageList &traceList) override;

   YukonError_t decodeResponse            (CtiXfer  &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;
   YukonError_t decodeResponseHandshake   (CtiXfer  &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;
   YukonError_t decodeResponseScan        (CtiXfer  &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;
   YukonError_t decodeResponseSelectMeter (CtiXfer  &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;
   YukonError_t decodeResponseLoadProfile (CtiXfer  &Transfer, YukonError_t commReturnValue, CtiMessageList &traceList) override;

   INT reformatDataBuffer (BYTE *aInMessBuffer, ULONG &aBytesReceived) override;
   INT copyLoadProfileData(BYTE *aInMessBuffer, ULONG &aTotalBytes) override;

   INT allocateDataBins (OUTMESS *outMess) override;

   virtual INT decodeResultScan ( const INMESS &InMessage,
                          const CtiTime TimeNow,
                          CtiMessageList   &vgList,
                          CtiMessageList &retList,
                          OutMessageList &outList);

   virtual INT decodeResultLoadProfile ( const INMESS &InMessage,
                                 const CtiTime TimeNow,
                                 CtiMessageList   &vgList,
                                 CtiMessageList &retList,
                                 OutMessageList &outList);

   BOOL getMeterDataFromScanStruct (int aOffset, DOUBLE &aValue, CtiTime &peak, const FulcrumScanData_t *aScanData);
   INT ResultDisplay (const INMESS &InMessage);
};

