/* Include file for LS database routines */

#ifndef LSURVEY_H
#define LSURVEY_H


//// #include "btrieve.h"
//#include "btrerrs.h"
//#include "btrvhist.h"

#ifndef FLOAT
  #define FLOAT float
#endif

#define LSFILENAME "DATA\\LSURVEY.BDB"
#define BDB_LSFILENAME "LSURVEY.BDB"

#define DAY_NOT_FOUND_QUIT       0
#define GO_BACKWARD_TO_TARGET    1
#define GO_FORWARD_TO_ANYDAY     2
#define GO_BACKWARD_TO_ANYDAY    3
#define GO_BACKWARD_SMALLFILE    4
#define BIG_BUFFER_RECORD_COUNT  1000
#define DAY_BUFFER_RECORD_COUNT  288

/* Define the structures used for searching */

#define LSNUMKEYS 6

typedef struct {
    USHORT RecordLength;
    USHORT PageSize;
    USHORT IndexCount;
    ULONG Records;
    USHORT FileFlags;
    CHAR Reserved [2];
    USHORT PreAlloc;
    KEYSPECTYPE KeySpecBuf[LSNUMKEYS];
} LSSPECBUF;


#define KEYLSDEVICE 0
typedef struct _LSDEVICENAME {
    CHAR DeviceName[STANDNAMLEN];
} LSDEVICENAME;

#define KEYLSDEVICEPOINT 1
typedef struct _LSDEVICEPOINT {
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
} LSDEVICEPOINT;

#define KEYLSNEXTSCAN 2
typedef struct _LSNEXTSCAN {
    ULONG NextScan;
} LSNEXTSCAN;

#define KEYSEQUENCENUM 4
typedef struct _SEQUENCENUM {
    ULONG SequenceNumber;
} SEQUENCENUM;

/* define structure used to store data */
typedef struct _LSTABLE {
    CHAR DeviceName[STANDNAMLEN];
    CHAR PointName[STANDNAMLEN];
    ULONG NextScan;
    ULONG FileNumber;
    USHORT SequenceNumber;
    USHORT LSInterval;
    USHORT DeviceType;
    USHORT CurrentStatus;
    USHORT CatchupFlag;
    USHORT Enable;
    USHORT NextBlock;
    USHORT DevQStat;
    USHORT PowerFail;
    USHORT StatusFlag;
    ULONG PutonQTime;
    ULONG LastVerifyTime;
} LSTABLE;


typedef struct _LSURVEY_EXT {
    USHORT RecordLen;                   /* length of record image */
    ULONG RecPosition;                  /* record position number */
    LSTABLE LSRec;                      /* 1 record from DB */
} LSURVEY_EXT;

typedef struct _LSURVEY_EXTTYPE {
    USHORT NumRecords;                              /* number of records returned */
    LSURVEY_EXT LSRecs[EXT_RECORD_COUNT_MAX];      /* space for 200 records */
} LSURVEY_EXTTYPE;

typedef struct _LS_E_BUFFER {
    union {
        LSURVEY_EXTTYPE RecordsBuf;
        EXTENDEDGETDESCTYPE Filter;
    } Buf;
} LS_E_BUFFER;


/* Prototypes from LUPDATE.C */
int InitLSDB (VOID);
int CreateLSDB (PCHAR);
int CloseLSDB(VOID);
int LSAdd (LSTABLE *);
int LSUpdate (LSTABLE *);
int LSFastUpdate (LSTABLE *);
int LSDelete (LSTABLE *);
int LSGetEqual (LSTABLE *);
int LSGetNextEqual (LSTABLE *);
int LSGetFirst (LSTABLE *);
int LSGetNext (LSTABLE *);
int LSGetPrev (LSTABLE *);
int LSGetGT (LSTABLE *);
int LSGetSequence (LSTABLE *);
int LSGetFirstScan (LSTABLE *);
int LSRecords (PULONG);
int OpenLSurveyDB(PCHAR, PBYTE);
int FO_CopyLSurveyDB (PCHAR, PCHAR);
int BDB_CopyLSurveyDB (PCHAR, PCHAR);
int Check4LSurveyDBChange (ULONG, PCHAR);
USHORT LSCreateLogIn (void);
LONG CTIFindDataInFile (PCHAR, BOOL, int, int, int);
LONG CTIFindDataInFileLong (PCHAR, BOOL, ULONG, SHORT);
int CTIFindOnOffPeak (ULONG, ULONG, USHORT, ULONG, LSFILETYPE *);
int CTIGlobalDataFileClose (void);
int CTIGlobalDataFileOpen (PCHAR, PULONG);
int CTIFindFirstLastRecord (ULONG, USHORT, ULONG, USHORT, PULONG, PULONG);
int CTIDeleteDataRange (PCHAR, PCHAR, ULONG, USHORT, ULONG, USHORT);
int CTIFixOffDst95 (PCHAR MyFileName);
int CTIFixOffDst96 (PCHAR MyFileName);
int OutputLSToWLT40Format (POINT *Point, time_t StartDate, USHORT StartDST,
                           time_t StopDate, USHORT StopDST, USHORT DemandInterval);
int MV90FindFirstLastRecord (ULONG MyStartTime,
                            USHORT MyStartDstFlag,
                            ULONG MyStopTime,
                            USHORT MyStopDstFlag,
                            PULONG StartRecord,
                            PULONG StopRecord);


#endif
