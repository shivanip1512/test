/*  FOMIRROR.H

    Include file for failover file copy routines containing
    prototypes, constants, and other definitions

    by:  BDW    08/30/94

*/

#ifndef FOMIRROR_H      /* make sure it has not been included yet */

#define FOMIRROR_H

#define CHILD_TERM_QNAME    "\\QUEUES\\FOCHILD.QUE"
#define RECORD_COUNT_THRESH 202

#define COPY_THREAD_STK_SIZE 16384

#define GENERAL_FILE_MAX  25




/* message definitions for display routine */
#define FO_CRITSUMALARMMESS     1
#define FO_NONCRITSUMALARMMESS  2
#define FO_LOADGROUPMESS        3
#define FO_LOADGROUPMACROMESS   4
#define FO_LOADCTLDAILYHISTMESS 5
#define FO_VERSACOMCONFIGMESS   6
#define FO_CONTROLMAPMESS       7
#define FO_COMMPORTMESS         8
#define FO_REMOTEMESS           9
#define FO_ROUTEMESS            10
#define FO_ROUTEMACROMESS       11
#define FO_SCANPOINTMESS        12
#define FO_STATUSPOINTMESS      13
#define FO_STATENAMETABLEMESS   14
#define FO_LOADSURVEYTABLEMESS  15
#define FO_MONITORPOINTMESS     16
#define FO_STRATEGYMESS         17
#define FO_TIMESCHEDULEMESS     18
#define FO_PARAMETERMESS        19
#define FO_LOADTYPEMESS         20
#define FO_CALCPOINTMESS        21
#define FO_POWERFACTORPOINTMESS 22
#define FO_DRPPOINTMESS         23
#define FO_PEAKDAYMESS          24
#define FO_FULLEVENTLOGMESS     25
#define FO_FULLCOMERRORMESS     26
#define FO_FULLALARMLOGMESS     27
#define FO_FULLCONTROLHISTMESS  28
#define FO_FULLDEVICEMESS       29
#define FO_FULLPOINTMESS        30
#define FO_UPDATEEVENTLOGMESS   31
#define FO_UPDATEALARMLOGMESS   32
#define FO_UPDATECOMERRORMESS   33
#define FO_UPDATECONTROLHISTMESS 34
#define FO_UPDATEDEVICEMESS     35
#define FO_UPDATEPOINTMESS      36
#define FO_GENERALUPDATEMESS    37
#define FO_FILESEQUALMESS       38
#define FO_UPDATEBINARYMESS     39
#define FO_DEVICEINTEGRITYMESS   40
#define FO_POINTINTEGRITYMESS   41
#define FO_REMOTEINTEGRITYMESS   42
#define FO_STRATEGYINTEGRITYMESS   43
#define FO_MONITORINTEGRITYMESS   44

/* error or return codes */
#define INVALID_DEST_DRIVE      2
#define INVALID_RECORD_COUNT    100
#define ZERO_RECORD_COUNT       101
#define INVALID_APPEND_RECORD   102

#define FO_TARGET_RECORD_NOT_FOUND 103
#define FO_RECORDS_EQUAL           104
#define FO_UPDATE_SOURCE           105
#define FO_UPDATE_TARGET           106

#define FO_FINISH_SEARCH        1000
#define DEST_DRIVE_VALID_AGAIN  10

    // size of bytes to compare in integrity check
#define FO_DEVICEBYTES          112
#define FO_POINTBYTES           112
#define FO_REMOTEBYTES           36

    // strings used in the definition of the reports
#define FO_INTEGRITYREPDEVICESTRING "%0.20s  %0.6s         %0.3s\n"
#define FO_INTEGRITYREPPOINTSTRING "%0.20s %0.20s %0.6s         %0.3s\n"
#define FO_INTEGRITYREPREMOTESTRING "%0.20s  %0.6s         %0.3s\n"
#define FO_INTEGRITYREPSTRATEGYSTRING "%0.8s              %0.6s         %0.3s\n"
#define FO_INTEGRITYREPMONITORSTRING "%0.8s              %0.6s         %0.3s\n"

/* function prototypes */
VOID FO_FileCopyThread (PVOID);


int FO_DupComErrorDB (void);
int FO_DupEventLogDB (void);
int FO_DupAlarmLogDB (void);
int FO_DupCtlHistDB (void);
int FO_DupDeviceDB (void);
int FO_DupPointDB (void);

int FO_FastCopyBigFile (PCHAR, PCHAR);
int FO_OpenAllBTRFiles (void);
int FO_CopyAllBTRFiles (void);
int FO_UpdateAllBTRFiles (void);
BOOL FO_BTRFileCheck (PCHAR, PCHAR);
int FO_CopyAllOtherBTRFiles (void);
int ValidDrives (void);
VOID APIENTRY FO_MirrorCleanUp (ULONG);
void DisplayUpdateMessage (int, PCHAR);
int CheckAllNonBtrieveFiles (void);
int FO_CopySubDirFiles (PCHAR SrcPath, PCHAR TargetPath, PCHAR FileSpec, int FileType);
void FO_CloseAllBTRFiles (void);
int AppendNewRecords (PCHAR SrcFile, PCHAR DestFile);

int CompareFiles (FDATE *, FTIME *, FDATE *, FTIME *);
int FO_IntegrityCheckBTRFiles (PCHAR SrcPath, PCHAR DestPath);

#endif
