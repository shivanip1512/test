
#ifndef _DUPSCHL_H_
#define _DUPSCHL_H_

#pragma pack(1)

#include "dev_base.h"

#define SCHL_SHMEMLP  "SCHL\\LPROFILE.DAT"
#define SCHL_SHMEMMM  "SCHL\\MASSMEM.DAT"

#define SCHLUMBERGER_RETRIES 7

/* Schlumberger Protocol Field Definition */
#define  ENQ                  0x05
#define  ACK                  0x06
#define  NAK                  0x15
#define  CAN                  0x18

/* Command defines for Schlumberger meters */
#define  SCHL_CMD_UPLOAD_ALL           'A'
#define  SCHL_CMD_DOWNLOAD_DATA        'D'
#define  SCHL_CMD_UPLOAD_SCAN          'S'     // This is the scanner request...
#define  SCHL_CMD_UPLOAD_LOADPROFILE   'L'     // This is the scanner request...
#define  SCHL_CMD_UPLOAD_DATA          'U'

#define  SCHL_GET_LOADPROFILE          0x01     // Command byte [1]

/*
 * (FUNC) Function Codes
 */

#define SCHL_FUNC_SIMPLE_REQUEST       0x0001
#define SCHL_FUNC_CONFIGURATION_DATA   0x0002
#define SCHL_FUNC_COMPLETE             0x0004


/*
 *  DEBUG bit fields.
 *
 */
#define  DBG_PRINT_RECEIPTS      0x8000
#define  DBG_PRINT_SENDS         0x4000
#define  DBG_PRINT_CHARBUF       0x2000
#define  DBG_PRINT_DESCRIPTIONS  0x1000

/*
 *  State machine states
 */

#define SCHL_STATE_START               0x0001
#define SCHL_STATE_ATTN                0x0002
#define SCHL_STATE_IDENTIFY            0x0004
#define SCHL_STATE_SECURITY            0x0008
#define SCHL_STATE_CONNECTED           0x0010
#define SCHL_STATE_ERRABORT            0x4000
#define SCHL_STATE_COMPLETE            0x8000



typedef struct {
    char    Command;
    char    *CmdName;
    int     OutBytes;
    int     InBytes;
} CTI_FULCRUM_FUNC;


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

typedef struct {
   CHAR     UnitType[4];
   CHAR     UnitId[9];
   ULONG    Start;
   ULONG    Stop;
   ULONG    Length;
   BYTE     Direction;
} SchlMeterStruct;

#define MMFLAG_CONFIG_VALID   0x00000001

typedef struct {
   time_t                     LastFileTime;
   time_t                     RecordTime;
   time_t                     NextRecordTime;
   ULONG                      porterLPTime;
   ULONG                      Flags;               // Misc Flags
   FulcrumMMConfig_t          MMConfig;
   BYTE                       MMBuffer[1600];
}  FulcrumLoadProfileMessage_t;                    // To fit in a DIALREPLY we cannot be > 2kB


typedef struct {
   time_t      RecordTime;
   ULONG       RecordAddress;
}  SchlLoadProfile_t;

// Schlumberger basepage data types:
#define SCHL_DTYPE_INT           0x01     // Holds up to a 4 byte value integer type....
#define SCHL_DTYPE_REALTIME      0x02
#define SCHL_DTYPE_ASCII         0x03
#define SCHL_DTYPE_1YTE          0x04
#define SCHL_DTYPE_2YTE          0x05
#define SCHL_DTYPE_3YTE          0x06
#define SCHL_DTYPE_4YTE          0x07

typedef struct
{
   INT      Address;
   INT      Bytes;
   CHAR     *Name;

   BYTE     Type;
} FulcrumBase;

typedef struct {
   BYTE                 *MMBuffer;
   SchlLoadProfile_t    *MMLPBuffer;
   INT                  MMRecordCount;
} FulcrumMMStruct_t;

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
	BYTE     Year;       // Mod-100 year anyway....
	BYTE     Month;
	BYTE     DayOfMonth;
	BYTE     Hours;
	BYTE     Minutes;
	BYTE     Seconds;
	BYTE     DayOfWeek;
} VectronRealTimeRegister_t;


typedef struct {

	DOUBLE						pulseWeight;	//   4	
	USHORT						registerAddress;
	USHORT						energyRegister;
	USHORT						pulseCount;
} VectronProgram_t;

typedef struct {

	struct 
	{
		BYTE						energyRegisterChannel1;
		BYTE						energyRegisterChannel2;
		BYTE						pulseWeightChannel1[4];	//   4	
		BYTE                        intervalLength;         //   1
		BYTE                        logicalStart[2];        //   3
		BYTE                        logicalEnd[2];          //   3
		BYTE						outageLength[2];		//   2
		BYTE						unused01;
		BYTE						loadResearchID[14];
		BYTE                     	currentRecord[2];       //   2
		BYTE                       	currentInterval;        //   1
		BYTE						reserved01[2];
		BYTE						leftoverOutageTime[3];	
		BYTE						currentRecordPtr[2];
		BYTE						reserved02[5];
		BYTE						reserved03[3];
		BYTE						coldStartTime[7];
		BYTE						rserved04[2];
		BYTE						numberOfChannels;
		BYTE						registerAddressChannel1;
		BYTE						registerAddressChannel2;
		BYTE						pulseWeightChannel2[4];
		BYTE						intervalTimer;
		BYTE						pulseCountChannel1[2];
		BYTE						pulseCountChannel2[2];

	}	Byte;

	struct 
	{
		VectronProgram_t			program[2];
		USHORT                      intervalLength;         //   1
		USHORT                      logicalStart;        //   3
		USHORT                      logicalEnd;          //   3
		USHORT                      logicalCurrent;          //   3
		BYTE						loadResearchID[14];

		USHORT                     	currentRecord;       //   2
		USHORT                      recordLength;          //   3
		USHORT                     	currentInterval;        //   1

		DOUBLE						leftoverOutageTime;	
		USHORT						currentRecordPtr;
		DOUBLE						coldStartTime;

		USHORT						numberOfChannels;
		USHORT						intervalTimer;

	}	Real;

	BOOL     dataIsReal;

} VectronMMConfig_t;

typedef struct
{
    FLOAT  voltage;
    FLOAT  current;
} VectronPhaseInfo_t;

typedef struct {
   FLOAT    PeakValue;
   FLOAT    Cumulative;
   BYTE     Minute;
   BYTE     Hour;
   BYTE     Day;
   BYTE     Month;
} VectronMaximumRegister_t;

typedef struct
{
	struct {
        BYTE                             phaseAVoltage[4];
        BYTE                             phaseBVoltage[4];
        BYTE                             phaseCVoltage[4];
        BYTE                             phaseACurrent[4];
        BYTE                             phaseBCurrent[4];
        BYTE                             phaseCCurrent[4];

		BYTE                             unitType[3];
		BYTE                             unitId[8];
		BYTE                             registerMultiplier[4];
		BYTE                             registerMapping[6];

        BYTE                             register2Info1[8]; // either rate E Max or E energy
        BYTE                             register1RateE[12]; // e E Max, Cum
		BYTE                             demandResetCount[2];
		BYTE                             outageCount[2];
        BYTE                             register3RateECum[4]; // rate E Cum
        BYTE                             register1RateA[8]; // e E Max, Cum
        BYTE                             register4RateECum[4]; // rate E Cum
        BYTE                             register1RateB[8]; //  B Max, Cum
        BYTE                             register1RateC[8]; //  C Max, Cum
        BYTE                             register1RateD[8]; //  D Max, Cum

        BYTE                             register2Info2[28];  // either rate A, B max or A-D energy
        BYTE                             registerConfiguration;
        BYTE                             register3Info1[8]; // either rate E Max or E energy
        BYTE                             register4Info1[8]; // either rate E Max or E energy


		BYTE                             sWRev[2];
		BYTE                             fWRev[2];
		BYTE                             progId[2];
		BYTE                             meterId[9];

	}	Byte;

	struct {

        VectronPhaseInfo_t               phaseA;
        VectronPhaseInfo_t               phaseB;
        VectronPhaseInfo_t               phaseC;

		BYTE                             unitType[3];
		BYTE                             unitId[8];
		FLOAT                            registerMultiplier;
		USHORT                           demandResetCount;
		USHORT                           outageCount;
		FLOAT                            sWRev;
		FLOAT                            fWRev;
		USHORT                           progId;
		BYTE                             meterId[9];

        struct 
        {
            struct 
            {
                struct 
                {
                    VectronMaximumRegister_t     rateE;   
                    VectronMaximumRegister_t     rateA;   
                    VectronMaximumRegister_t     rateB;   
                    VectronMaximumRegister_t     rateC;   
                    VectronMaximumRegister_t     rateD;   
                } demand;
            } data;

            USHORT configFlag;
            USHORT mapping;
        } register1;

        struct 
        {
            union
            {
                struct 
                {
                    VectronMaximumRegister_t     rateE;   
                    VectronMaximumRegister_t     rateA;   
                    VectronMaximumRegister_t     rateB;   
                } demand;

                struct
                {
                    DOUBLE                       rateE;              
                    DOUBLE                       rateA;              
                    DOUBLE                       rateB;              
                    DOUBLE                       rateC;              
                    DOUBLE                       rateD;              

                } energy;

            } data;

            USHORT configFlag;
            USHORT mapping;
        } register2;

        struct 
        {
            union
            {
                struct 
                {
                    VectronMaximumRegister_t     rateE;   
                } demand;

                struct
                {
                    DOUBLE                       rateE;              
                } energy;

            } data;
            USHORT configFlag;
            USHORT mapping;
        } register3;

        struct 
        {
            union
            {
                struct 
                {
                    VectronMaximumRegister_t     rateE;   
                } demand;

                struct
                {
                    DOUBLE                       rateE;              
                } energy;

            } data;
            USHORT configFlag;
            USHORT mapping;
        } register4;

	}	Real;

	BOOL     dataIsReal;

} VectronScanData_t;


typedef struct {
   time_t                     LastFileTime;
   time_t                     RecordTime;
   time_t                     NextRecordTime;
   ULONG                      porterLPTime;
   ULONG                      Flags;               // Misc Flags
   VectronRealTimeRegister_t  RealTime;  
   VectronMMConfig_t          MMConfig;
   BYTE                       MMBuffer[1500];
}  VectronLoadProfileMessage_t;                    // To fit in a DIALREPLY we cannot be > 2kB


// array of commands used by both meters
typedef struct
{
	INT      startAddress;
	INT      stopAddress;
    INT      startOffset;
    INT      bytesToRead;
    CHAR     *name;
} SchlumbergerCommandBlk_t;

// structure containing all the mass memory config params we need
typedef struct {
   INT      MMIndex;
   INT      MMVintage;        // This is the last index we need to retrieve to make scanner happy
   ULONG    MMCount;
   ULONG    MMPos;
   ULONG    MMBlockSize;
   ULONG    MMScanStartAddress;
} SchlumbergerLProfileInput_t;



#pragma pack()

#endif /* _DUPSCHL_H_ */
