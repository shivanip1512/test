/*  PFACTOR.H
    Include file for Load Management Automation Btrieve routines
*/

#ifndef PFACTOR_H
#define PFACTOR_H

//// #include "btrieve.h"

#define POWERFACTORFILENAME "DATA\\PFACTTAB.BDB"
#define BDB_POWERFACTORFILENAME "PFACTTAB.BDB"
#define POWERFACTORNUMKEYS 2

typedef struct {
    USHORT RecordLength;
    USHORT PageSize;
    USHORT IndexCount;
    ULONG Records;
    USHORT FileFlags;
    CHAR Reserved [2];
    USHORT PreAlloc;
    KEYSPECTYPE KeySpecBuf[POWERFACTORNUMKEYS];
} POWERFACTORSPECBUF;




typedef struct _POWERFACTOR {
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
    CHAR KWDeviceName[STANDNAMLEN];
    CHAR KWPointName[STANDNAMLEN];
    CHAR KVARDeviceName[STANDNAMLEN];
    CHAR KVARPointName[STANDNAMLEN];
    CHAR KQDeviceName[STANDNAMLEN];
    CHAR KQPointName[STANDNAMLEN];
    CHAR KVADeviceName[STANDNAMLEN];
    CHAR KVAPointName[STANDNAMLEN];
    USHORT Status;
    USHORT ReportOrder;
} POWERFACTOR;

#define KEYPOWERFACTORNAME  0
typedef struct _POWERFACTORNAME {
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
} POWERFACTORNAME;


/* Structure for operands */
typedef struct _PFOPERAND {
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
    ULONG TimeStamp;
    FLOAT Value;
    USHORT Quality;
    PVOID NextPFOperand;
} PFOPERAND;

/* Structure for the results */
typedef struct _PFRESULT {
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
    ULONG TimeStamp;
    FLOAT Value;
    USHORT Quality;
    PVOID NextPFResult;
} PFRESULT;

/* Structure for operations */
typedef struct _PFOPERATION {
    PFRESULT *PF_Result;
    PFRESULT *KVAR_Result;
    PFOPERAND *KVAR_Operand;
    PFRESULT *KVA_Result;
    PFOPERAND *KVA_Operand;
    PFRESULT *KW_Result;
    PFOPERAND *KW_Operand;
    PFRESULT *KQ_Result;
    PFOPERAND *KQ_Operand;
    PVOID NextPFOperation;
} PFOPERATION;


/* Prototypes from FUPDATE.C */
IM_EX_CTIBASE INT InitPowerFactorDB (VOID);
IM_EX_CTIBASE INT CreatePowerFactorDB (PCHAR);
IM_EX_CTIBASE INT ClosePowerFactorDB(VOID);
IM_EX_CTIBASE INT PowerFactorAdd (POWERFACTOR *);
IM_EX_CTIBASE INT PowerFactorUpdate (POWERFACTOR *);
IM_EX_CTIBASE INT PowerFactorDelete (POWERFACTOR *);
IM_EX_CTIBASE INT PowerFactorGetEqual (POWERFACTOR *);
IM_EX_CTIBASE INT PowerFactorGetFirst (POWERFACTOR *);
IM_EX_CTIBASE INT PowerFactorGetNext (POWERFACTOR *);
IM_EX_CTIBASE INT PowerFactorGetPrev (POWERFACTOR *);
IM_EX_CTIBASE INT PowerFactorRecords (PULONG);
IM_EX_CTIBASE INT Check4PFactorDBChange (ULONG, PCHAR);
IM_EX_CTIBASE INT FO_CopyPowerFactorDB (PCHAR, PCHAR);
IM_EX_CTIBASE INT OpenPowerFactorDB (PCHAR, PBYTE);
IM_EX_CTIBASE INT BDB_CopyPowerFactorDB (PCHAR, PCHAR);


/* Prototypes from pfactor.c */
IM_EX_CTIBASE INT PFInit (VOID);
IM_EX_CTIBASE INT UpdatePFactorPoint (DRPVALUE *);
PFOPERATION IM_EX_CTIBASE *FindPFOperandInOperation (PFOPERAND *, PFOPERATION *);
IM_EX_CTIBASE INT PFCalc (PFOPERATION *);
PFOPERATION IM_EX_CTIBASE *AddPFOperation (POWERFACTOR *, POINT *, PFOPERATION *, PFOPERATION *);
PFOPERAND IM_EX_CTIBASE *AddPFOperand (POINT *, PFOPERAND *, PFOPERAND *);
PFOPERAND IM_EX_CTIBASE *FindPFOperand (PCHAR, PCHAR, PFOPERAND *);
PFRESULT IM_EX_CTIBASE *AddPFResult (POINT *, PFRESULT *, PFRESULT *);
PFRESULT IM_EX_CTIBASE *FindPFResult (PCHAR, PCHAR, PFRESULT *);
PFRESULT IM_EX_CTIBASE *CopyPFOperandToResult (PFOPERAND *);
IM_EX_CTIBASE INT RemovePFOperand (PFOPERAND *);
IM_EX_CTIBASE INT PFDeAllocate (VOID);

#endif




