/* Data structures for the calc routines from hell */

#ifndef CALC_H
#define CALC_H

//// #include "btrieve.h"

#define CALCPOINTFILENAME "DATA\\CALCPT.BDB"
#define BDB_CALCPOINTFILENAME "CALCPT.BDB"
#define CALCPOINTNUMKEYS 2

typedef struct {
    USHORT RecordLength;
    USHORT PageSize;
    USHORT IndexCount;
    ULONG Records;
    USHORT FileFlags;
    CHAR Reserved [2];
    USHORT PreAlloc;
    KEYSPECTYPE KeySpecBuf[CALCPOINTNUMKEYS];
} CALCPOINTSPECBUF;



typedef struct _CALCPOINTS {
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
    CHAR Operation[5];
} CALCPOINTS;

typedef struct _CALCPOINT {
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
    CALCPOINTS CalcPoints[15];
} CALCPOINT;

#define KEYCALCPOINTNAME  0
typedef struct _CALCPOINTNAME {
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
} CALCPOINTNAME;


/* Structure for operands */
typedef struct _OPERAND {
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
    ULONG TimeStamp;
    FLOAT Value;
    USHORT Quality;
    BOOL NoTimeCheck;
    struct _OPERAND *NextOperand;
    struct _OPERATION *FirstOperation;
} OPERAND;

/* Structure for operations */
typedef struct _OPERATION {
    CHAR Operation[5];
    PVOID Result;
    struct _OPERATION *NextResultOperation;
    struct _OPERAND *Operand;
    struct _OPERATION *NextOperandOperation;
} OPERATION;

/* Structure for the results */
typedef struct _RESULTANT {
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
    ULONG TimeStamp;
    FLOAT Value;
    USHORT Quality;
    struct _RESULTANT *NextResult;
    struct _OPERATION *FirstOperation;
} RESULTANT;

/* Some numbers */
#define PI      3.14159265
#define E       2.71828183
#define SQRT2   1.41421356
#define SQRT3   1.73205081
#define ISQRT2  0.70710678
#define ISQRT3  0.57735027

/* Prototypes from fupdate.c */
int      IM_EX_CTIBASE InitCalcPointDB (VOID);
int      IM_EX_CTIBASE CreateCalcPointDB (PCHAR);
int      IM_EX_CTIBASE CloseCalcPointDB(VOID);
int      IM_EX_CTIBASE CalcPointAdd (CALCPOINT *);
int      IM_EX_CTIBASE CalcPointUpdate (CALCPOINT *);
int      IM_EX_CTIBASE CalcPointDelete (CALCPOINT *);
int      IM_EX_CTIBASE CalcPointGetEqual (CALCPOINT *);
int      IM_EX_CTIBASE CalcPointGetFirst (CALCPOINT *);
int      IM_EX_CTIBASE CalcPointGetNext (CALCPOINT *);
int      IM_EX_CTIBASE CalcPointGetPrev (CALCPOINT *);
int      IM_EX_CTIBASE CalcPointRecords (PULONG);
int      IM_EX_CTIBASE Check4CalcDBChange (ULONG, PCHAR);
DOUBLE   IM_EX_CTIBASE xConstant (PCHAR);
int      IM_EX_CTIBASE OperationCheck (PCHAR);
int      IM_EX_CTIBASE FO_CopyCalcPointDB (PCHAR, PCHAR);
int      IM_EX_CTIBASE OpenCalcPointDB(PCHAR, PBYTE);
int      IM_EX_CTIBASE BDB_CopyCalcPointDB (PCHAR, PCHAR);


/* Prototypes from calc.c */
int         IM_EX_CTIBASE CalcInit (VOID);
int         IM_EX_CTIBASE UpdateCalcPoint (DRPVALUE *);
int         IM_EX_CTIBASE ResultCalc (RESULTANT *);
OPERATION   IM_EX_CTIBASE *OperationCalc (OPERATION *);
OPERATION   IM_EX_CTIBASE *ResultTimeCheck (ULONG, OPERATION *);
int         IM_EX_CTIBASE CalcDeAllocate (VOID);
RESULTANT   IM_EX_CTIBASE *AddResult (POINT *, RESULTANT *, RESULTANT *);
RESULTANT   IM_EX_CTIBASE *FindResult (PCHAR, PCHAR, RESULTANT *);
OPERAND     IM_EX_CTIBASE *AddOperand (POINT *, OPERAND *, OPERAND *);
OPERAND     IM_EX_CTIBASE *FindOperand (PCHAR, PCHAR, OPERAND *);
OPERATION   IM_EX_CTIBASE *AddOperation (PCHAR, OPERAND *, RESULTANT *);
DOUBLE      IM_EX_CTIBASE Constant (PCHAR);

#endif




