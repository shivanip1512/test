
#pragma title ( "Log Database Update Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        Ben Wallace

    FileName:
        ALARMLOG.C

    Purpose:
        Routines to maintain the log database

    The following procedures are contained in this module:
        INITALLALARMLOG     InitAllAlarmLog
        AlarmLogRecords


    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        9-6-93  Converted to 32 bit                            WRO
        6-17-94 Moved drp send to end                          WRO
        09-24-94  Added routines for Failover                  BDW
        03-30-99  Convert for YUKON                            CGP

   -------------------------------------------------------------------- */
#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <string.h>
#include <malloc.h>

#include "os2_2w32.h"
#include "cticalls.h"

#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "elogger.h"
#include "alarmlog.h"
#include "drp.h"



BYTE   AlarmLogPosBlk[128];
BYTE   AlarmSum0PosBlk[128];
BYTE   AlarmSum1PosBlk[128];
BYTE   ComErrorPosBlk[128];
BYTE   ProgErrorPosBlk[128];
ULONG  ComRecordMax;
USHORT UnackTimeInterval = {0};
USHORT SaveClass;


/* Routine to initialize Btrieve databases */
IM_EX_CTIBASE INT InitAllAlarmLog (VOID)
{
   ULONG i;

   if((i= InitAlarmDB()) != NORMAL)
   {
      return(i);
   }

   return(InitComErrorLog());

}


/* Routine to initialize Btrieve databases */
IM_EX_CTIBASE INT InitAlarmDB ()
{
   ULONG i;
   PSZ Environment;

   if((i = InitAlarmLog()) != NORMAL)
      return(i);

   /* unack needs to be init */
   if(CTIScanEnv ("MAX_UNACK_MINUTES",
                  &Environment) ||
      !(UnackTimeInterval = atoi (Environment)))
   {
      UnackTimeInterval = 5;        // default to 5 minutes
   }

   if((i = InitAlarmSum(CRITICALCLASS)) != NORMAL)
      return(i);


   return(InitAlarmSum(NONCRITICALCLASS));

}

/* Routine to initialize Btrieve databases */
IM_EX_CTIBASE INT CloseAllAlarmLog ()
{
   CloseAlarmDB();
   return(CloseComError());
}


/* Routine to initialize Btrieve databases */
IM_EX_CTIBASE INT CloseAlarmDB ()
{
   CloseAlarmLog();
   return(CloseAlarmSum());
}


/* Routine to initialize Btrieve databases */
IM_EX_CTIBASE INT InitAlarmLog ()
{
   extern BYTE AlarmLogPosBlk[];
   ALARM_LOG_STRUCT AlarmLogRecord;
   USHORT BufLen;
   ULONG i;
   BYTE KeyBuf[64];
   ALARMLOGSPECBUF AlarmLogSpecBuf;

   BufLen = sizeof (AlarmLogRecord);
   if((i = BTRV (B_OPEN,
                 AlarmLogPosBlk,
                 &AlarmLogRecord,
                 &BufLen,
                 ALARMLOGFILENAME,
                 B_OPEN_NORMAL)) != NORMAL)
   {

      if(i != B_ERR_FNF)
      {
         return(ALARMLOGOPEN);
      }
      else
      {
         /* File does not exist so go ahead and create it */
         if((i = CreateAlarmLogDB(ALARMLOGFILENAME)) != NORMAL)
         {
            return(i);
         }

         /* lets try to open it again */
         BufLen = sizeof (AlarmLogRecord);
         if(BTRV (B_OPEN,
                  AlarmLogPosBlk,
                  &AlarmLogRecord,
                  &BufLen,
                  ALARMLOGFILENAME,
                  B_OPEN_NORMAL))
         {

            return(ALARMLOGOPEN);
         }
      }
   }
   else
   {
      /* Check that the record size is still what it should be */
      /* Note that this does NOT warrenty against changes in indexing
         or ordering of the record */
      BufLen = sizeof (AlarmLogSpecBuf);
      if(BTRV (B_STAT,
               AlarmLogPosBlk,
               &AlarmLogSpecBuf,
               &BufLen,
               KeyBuf,
               0) || AlarmLogSpecBuf.RecordLength != sizeof (AlarmLogRecord))
      {
         return(RECORDCHANGED);
      }
   }

   return(NORMAL);

}


IM_EX_CTIBASE INT CreateAlarmLogDB (PCHAR FileName)
{
   BYTE TempPosBlk[128];
   ALARM_LOG_STRUCT AlarmLogRecord;
   USHORT BufLen;
   ALARMLOGSPECBUF AlarmLogSpecBuf;


   /* File does not exist so go ahead and create it */
   memset (&AlarmLogSpecBuf, 0, sizeof (AlarmLogSpecBuf));
   AlarmLogSpecBuf.RecordLength = sizeof (AlarmLogRecord);
   AlarmLogSpecBuf.PageSize = 1024;
   AlarmLogSpecBuf.FileFlags = B_PREALLOC;
   AlarmLogSpecBuf.IndexCount = 5;
   AlarmLogSpecBuf.PreAlloc = 1;
   AlarmLogSpecBuf.KeySpecBuf[0].KeyPos = (USHORT) ((ULONG) &AlarmLogRecord.TimeStamp - (ULONG) &AlarmLogRecord) + 1;
   AlarmLogSpecBuf.KeySpecBuf[0].KeyLen = sizeof (AlarmLogRecord.TimeStamp);
   AlarmLogSpecBuf.KeySpecBuf[0].KeyFlag = B_MOD | B_DUP | B_EXT | B_DES;
   AlarmLogSpecBuf.KeySpecBuf[0].KeyType = B_UNSIGNED;
   AlarmLogSpecBuf.KeySpecBuf[1].KeyPos = (USHORT) ((ULONG) &AlarmLogRecord.AlarmClass - (ULONG) &AlarmLogRecord) + 1;
   AlarmLogSpecBuf.KeySpecBuf[1].KeyLen = sizeof (AlarmLogRecord.AlarmClass);
   AlarmLogSpecBuf.KeySpecBuf[1].KeyFlag = B_MOD | B_DUP | B_EXT | B_SEG;
   AlarmLogSpecBuf.KeySpecBuf[1].KeyType = B_UNSIGNED;
   AlarmLogSpecBuf.KeySpecBuf[2].KeyPos = (USHORT) ((ULONG) &AlarmLogRecord.DeviceName - (ULONG) &AlarmLogRecord) + 1;
   AlarmLogSpecBuf.KeySpecBuf[2].KeyLen = sizeof (AlarmLogRecord.DeviceName);
   AlarmLogSpecBuf.KeySpecBuf[2].KeyFlag = B_MOD | B_DUP | B_EXT;
   AlarmLogSpecBuf.KeySpecBuf[2].KeyType = B_STRING;
   AlarmLogSpecBuf.KeySpecBuf[3].KeyPos = (USHORT) ((ULONG) &AlarmLogRecord.AlarmClass - (ULONG) &AlarmLogRecord) + 1;
   AlarmLogSpecBuf.KeySpecBuf[3].KeyLen = sizeof (AlarmLogRecord.AlarmClass);
   AlarmLogSpecBuf.KeySpecBuf[3].KeyFlag = B_MOD | B_DUP | B_EXT | B_SEG;
   AlarmLogSpecBuf.KeySpecBuf[3].KeyType = B_UNSIGNED;
   AlarmLogSpecBuf.KeySpecBuf[4].KeyPos = (USHORT) ((ULONG) &AlarmLogRecord.DeviceName - (ULONG) &AlarmLogRecord) + 1;
   AlarmLogSpecBuf.KeySpecBuf[4].KeyLen = sizeof (AlarmLogRecord.DeviceName);
   AlarmLogSpecBuf.KeySpecBuf[4].KeyFlag = B_MOD | B_DUP | B_EXT | B_SEG;
   AlarmLogSpecBuf.KeySpecBuf[4].KeyType = B_STRING;
   AlarmLogSpecBuf.KeySpecBuf[5].KeyPos = (USHORT) ((ULONG) &AlarmLogRecord.PointName - (ULONG) &AlarmLogRecord) + 1;
   AlarmLogSpecBuf.KeySpecBuf[5].KeyLen = sizeof (AlarmLogRecord.PointName);
   AlarmLogSpecBuf.KeySpecBuf[5].KeyFlag = B_MOD | B_DUP | B_EXT;
   AlarmLogSpecBuf.KeySpecBuf[5].KeyType = B_STRING;
   AlarmLogSpecBuf.KeySpecBuf[6].KeyPos = (USHORT) ((ULONG) &AlarmLogRecord.AlarmClass - (ULONG) &AlarmLogRecord) + 1;
   AlarmLogSpecBuf.KeySpecBuf[6].KeyLen = sizeof (AlarmLogRecord.AlarmClass);
   AlarmLogSpecBuf.KeySpecBuf[6].KeyFlag = B_MOD | B_DUP | B_EXT | B_SEG;
   AlarmLogSpecBuf.KeySpecBuf[6].KeyType = B_UNSIGNED;
   AlarmLogSpecBuf.KeySpecBuf[7].KeyPos = (USHORT) ((ULONG) &AlarmLogRecord.TimeStamp - (ULONG) &AlarmLogRecord) + 1;
   AlarmLogSpecBuf.KeySpecBuf[7].KeyLen = sizeof (AlarmLogRecord.TimeStamp);
   AlarmLogSpecBuf.KeySpecBuf[7].KeyFlag = B_MOD | B_DUP | B_EXT | B_DES;
   AlarmLogSpecBuf.KeySpecBuf[7].KeyType = B_UNSIGNED;
   AlarmLogSpecBuf.KeySpecBuf[8].KeyPos = (USHORT) ((ULONG) &AlarmLogRecord.AlarmClass - (ULONG) &AlarmLogRecord) + 1;
   AlarmLogSpecBuf.KeySpecBuf[8].KeyLen = sizeof (AlarmLogRecord.AlarmClass);
   AlarmLogSpecBuf.KeySpecBuf[8].KeyFlag = B_MOD | B_DUP | B_EXT | B_SEG;
   AlarmLogSpecBuf.KeySpecBuf[8].KeyType = B_UNSIGNED;
   AlarmLogSpecBuf.KeySpecBuf[9].KeyPos = (USHORT) ((ULONG) &AlarmLogRecord.TimeStamp - (ULONG) &AlarmLogRecord) + 1;
   AlarmLogSpecBuf.KeySpecBuf[9].KeyLen = sizeof (AlarmLogRecord.TimeStamp);
   AlarmLogSpecBuf.KeySpecBuf[9].KeyFlag = B_MOD | B_DUP | B_EXT | B_DES | B_SEG;
   AlarmLogSpecBuf.KeySpecBuf[9].KeyType = B_UNSIGNED;
   AlarmLogSpecBuf.KeySpecBuf[10].KeyPos = (USHORT) ((ULONG) &AlarmLogRecord.DeviceName - (ULONG) &AlarmLogRecord) + 1;
   AlarmLogSpecBuf.KeySpecBuf[10].KeyLen = sizeof (AlarmLogRecord.DeviceName);
   AlarmLogSpecBuf.KeySpecBuf[10].KeyFlag = B_MOD | B_DUP | B_EXT | B_SEG;
   AlarmLogSpecBuf.KeySpecBuf[10].KeyType = B_STRING;
   AlarmLogSpecBuf.KeySpecBuf[11].KeyPos = (USHORT) ((ULONG) &AlarmLogRecord.PointName - (ULONG) &AlarmLogRecord) + 1;
   AlarmLogSpecBuf.KeySpecBuf[11].KeyLen = sizeof (AlarmLogRecord.PointName);
   AlarmLogSpecBuf.KeySpecBuf[11].KeyFlag = B_MOD | B_DUP | B_EXT;
   AlarmLogSpecBuf.KeySpecBuf[11].KeyType = B_STRING;
   BufLen = sizeof (AlarmLogSpecBuf);

   if(BTRV (B_CREATE,
            TempPosBlk,
            &AlarmLogSpecBuf,
            &BufLen,
            FileName,
            0))
   {
      return(ALARMLOGCREATE);
   }

   return(NORMAL);

}

/* Routine to initialize Btrieve databases */
IM_EX_CTIBASE INT InitAlarmSum (USHORT AlarmClass)
{
   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   ALARM_SUM_STRUCT AlarmSumRecord;
   USHORT BufLen;
   ULONG i;
   BYTE KeyBuf[64];
   ALARMSUMSPECBUF AlarmSumSpecBuf;

   BufLen = sizeof (AlarmSumRecord);
   if(AlarmClass == CRITICALCLASS)
      i = BTRV (B_OPEN,
                AlarmSum1PosBlk,
                &AlarmSumRecord,
                &BufLen,
                ALARMSUM1FILENAME,
                B_OPEN_NORMAL);
   else
      i = BTRV (B_OPEN,
                AlarmSum0PosBlk,
                &AlarmSumRecord,
                &BufLen,
                ALARMSUM0FILENAME,
                B_OPEN_NORMAL);

   if(i == B_ERR_FNF)
   {
      /* File does not exist so go ahead and create it */
      if(AlarmClass == CRITICALCLASS)
      {
         if((i = CreateAlarmSum (ALARMSUM1FILENAME)) != NORMAL)
            return(i);

         BufLen = sizeof (AlarmSumRecord);
         if(BTRV (B_OPEN,
                  AlarmSum1PosBlk,
                  &AlarmSumRecord,
                  &BufLen,
                  ALARMSUM1FILENAME,
                  B_OPEN_NORMAL))
            return(ALARMSUMOPEN);
      }
      else
      {
         if((i = CreateAlarmSum (ALARMSUM0FILENAME)) != NORMAL)
            return(i);

         BufLen = sizeof (AlarmSumRecord);
         if(BTRV (B_OPEN,
                  AlarmSum0PosBlk,
                  &AlarmSumRecord,
                  &BufLen,
                  ALARMSUM0FILENAME,
                  B_OPEN_NORMAL))
            return(ALARMSUMOPEN);
      }
   }
   else if(i != NORMAL)
      return(ALARMSUMOPEN);
   else
   {
      /* Check that the record size is still what it should be */
      /* Note that this does NOT warrenty against changes in indexing or ordering of the record */
      BufLen = sizeof (AlarmSumSpecBuf);
      if(AlarmClass == CRITICALCLASS)
      {
         if(BTRV (B_STAT,
                  AlarmSum1PosBlk,
                  &AlarmSumSpecBuf,
                  &BufLen,
                  KeyBuf,
                  0) || AlarmSumSpecBuf.RecordLength != sizeof (AlarmSumRecord))
         {
            printf("AL2\n");
            return(RECORDCHANGED);
         }
      }
      else
      {
         if(BTRV (B_STAT,
                  AlarmSum0PosBlk,
                  &AlarmSumSpecBuf,
                  &BufLen,
                  KeyBuf,
                  0) || AlarmSumSpecBuf.RecordLength != sizeof (AlarmSumRecord))
         {
            printf("AL3\n");
            return(RECORDCHANGED);
         }
      }
   }

   return(NORMAL);

}

IM_EX_CTIBASE INT CreateAlarmSum (PCHAR FileName)
{
   BYTE TempPosBlk[128];
   ALARM_SUM_STRUCT AlarmSumRecord;
   USHORT BufLen;
   ALARMSUMSPECBUF AlarmSumSpecBuf;

   /* setup all the indexs and thing like that */
   memset (&AlarmSumSpecBuf, 0, sizeof (AlarmSumSpecBuf));
   AlarmSumSpecBuf.RecordLength = sizeof (AlarmSumRecord);
   AlarmSumSpecBuf.PageSize = 512;
   AlarmSumSpecBuf.FileFlags = B_PREALLOC;
   AlarmSumSpecBuf.IndexCount = 4;
   AlarmSumSpecBuf.PreAlloc = 1;
   AlarmSumSpecBuf.KeySpecBuf[0].KeyPos = (USHORT) ((ULONG) &AlarmSumRecord.TimeStamp - (ULONG) &AlarmSumRecord) + 1;
   AlarmSumSpecBuf.KeySpecBuf[0].KeyLen = sizeof (AlarmSumRecord.TimeStamp);
   AlarmSumSpecBuf.KeySpecBuf[0].KeyFlag = B_MOD | B_DUP | B_EXT | B_DES;
   AlarmSumSpecBuf.KeySpecBuf[0].KeyType = B_UNSIGNED;
   AlarmSumSpecBuf.KeySpecBuf[1].KeyPos = (USHORT) ((ULONG) &AlarmSumRecord.DeviceName - (ULONG) &AlarmSumRecord) + 1;
   AlarmSumSpecBuf.KeySpecBuf[1].KeyLen = sizeof (AlarmSumRecord.DeviceName);
   AlarmSumSpecBuf.KeySpecBuf[1].KeyFlag = B_MOD | B_DUP | B_EXT| B_SEG;
   AlarmSumSpecBuf.KeySpecBuf[1].KeyType = B_STRING;
   AlarmSumSpecBuf.KeySpecBuf[2].KeyPos = (USHORT) ((ULONG) &AlarmSumRecord.PointName - (ULONG) &AlarmSumRecord) + 1;
   AlarmSumSpecBuf.KeySpecBuf[2].KeyLen = sizeof (AlarmSumRecord.PointName);
   AlarmSumSpecBuf.KeySpecBuf[2].KeyFlag = B_MOD | B_DUP | B_EXT;
   AlarmSumSpecBuf.KeySpecBuf[2].KeyType = B_STRING;
   AlarmSumSpecBuf.KeySpecBuf[3].KeyPos = (USHORT) ((ULONG) &AlarmSumRecord.AcknowlegeFlag - (ULONG) &AlarmSumRecord) + 1;
   AlarmSumSpecBuf.KeySpecBuf[3].KeyLen = sizeof (AlarmSumRecord.AcknowlegeFlag);
   AlarmSumSpecBuf.KeySpecBuf[3].KeyFlag = B_MOD | B_DUP | B_EXT | B_DES;
   AlarmSumSpecBuf.KeySpecBuf[3].KeyType = B_UNSIGNED;
   AlarmSumSpecBuf.KeySpecBuf[4].KeyPos = (USHORT) ((ULONG) &AlarmSumRecord.UnAckTimeOutTime - (ULONG) &AlarmSumRecord) + 1;
   AlarmSumSpecBuf.KeySpecBuf[4].KeyLen = sizeof (AlarmSumRecord.UnAckTimeOutTime);
   AlarmSumSpecBuf.KeySpecBuf[4].KeyFlag = B_MOD | B_DUP | B_EXT;
   AlarmSumSpecBuf.KeySpecBuf[4].KeyType = B_UNSIGNED;
   BufLen = sizeof (AlarmSumSpecBuf);

   if(BTRV (B_CREATE,
            TempPosBlk,
            &AlarmSumSpecBuf,
            &BufLen,
            FileName,
            0))
      return(ALARMSUMCREATE);


   return(NORMAL);
}


/* Open the alarm critical class DB with an optional path name */
IM_EX_CTIBASE INT OpenAlarmCSumDB (PCHAR PathName, PBYTE MyPosBlk)
{
   ALARM_SUM_STRUCT AlarmSumRecord;
   USHORT BufLen;
   CHAR FileName[128];

   /* Make the file name first */
   if(PathName[0] == '\0')
   {
      strcpy (FileName, ALARMSUM1FILENAME);
   }
   else
   {
      strcpy (FileName, PathName);
      strcat (FileName, ALARMSUM1FILENAME);
   }

   BufLen = sizeof (AlarmSumRecord);
   return(BTRV (B_OPEN,
                MyPosBlk,
                &AlarmSumRecord,
                &BufLen,
                FileName,
                B_OPEN_NORMAL));
}

/* Open the alarm non critical class DB with an optional path name */
IM_EX_CTIBASE INT OpenAlarmNCSumDB (PCHAR PathName, PBYTE MyPosBlk)
{
   ALARM_SUM_STRUCT AlarmSumRecord;
   USHORT BufLen;
   CHAR FileName[128];

   /* Make the file name first */
   if(PathName[0] == '\0')
   {
      strcpy (FileName, ALARMSUM0FILENAME);
   }
   else
   {
      strcpy (FileName, PathName);
      strcat (FileName, ALARMSUM0FILENAME);
   }

   BufLen = sizeof (AlarmSumRecord);
   return(BTRV (B_OPEN,
                MyPosBlk,
                &AlarmSumRecord,
                &BufLen,
                FileName,
                B_OPEN_NORMAL));
}

/* Routine to initialize Btrieve databases */
IM_EX_CTIBASE INT InitComErrorLog ()
{
   extern BYTE ComErrorPosBlk[];
   COMM_ERROR_LOG_STRUCT ComErrorRecord;
   USHORT BufLen;
   ULONG i;
   BYTE KeyBuf[64];
   PSZ ComMaxName;
   COMERRORSPECBUF ComErrorSpecBuf;

   /* init the max number of records for com error logs  this limits the file size */
   if(!CTIScanEnv ("COMLOGRECORDMAX",
                   &ComMaxName))
   {
      ComRecordMax = atol(ComMaxName);
      if(ComRecordMax < 1)
         ComRecordMax = 50000L;
   }
   else
      ComRecordMax = 50000L;    // default to number of records

   BufLen = sizeof (ComErrorRecord);
   if((i = BTRV (B_OPEN,
                 ComErrorPosBlk,
                 &ComErrorRecord,
                 &BufLen,
                 COMMLOGFILENAME,
                 B_OPEN_NORMAL)) != NORMAL)
   {
      if(i != B_ERR_FNF)
         return(COMERROROPEN);
      else
      {
         /* File does not exist so go ahead and create it */
         if(CreateComErrorLog (COMMLOGFILENAME))
         {
            return(COMERRORCREATE);
         }

         /* not we can open the file */
         BufLen = sizeof (ComErrorRecord);
         if(BTRV (B_OPEN,
                  ComErrorPosBlk,
                  &ComErrorRecord,
                  &BufLen,
                  COMMLOGFILENAME,
                  B_OPEN_NORMAL))
         {
            return(COMERROROPEN);
         }
      }
   }

   else
   {
      /* Check that the record size is still what it should be */
      /* Note that this does NOT warrenty against changes in indexing
         or ordering of the record */
      BufLen = sizeof (ComErrorSpecBuf);
      if(BTRV (B_STAT,
               ComErrorPosBlk,
               &ComErrorSpecBuf,
               &BufLen,
               KeyBuf,
               0) || ComErrorSpecBuf.RecordLength != sizeof (ComErrorRecord))
      {
         printf("AL4\n");
         return(RECORDCHANGED);
      }
   }

   return(NORMAL);

}

IM_EX_CTIBASE INT CreateComErrorLog (PCHAR FileName)
{
   BYTE TempPosBlk[128];
   COMM_ERROR_LOG_STRUCT ComErrorRecord;
   USHORT BufLen;
   COMERRORSPECBUF ComErrorSpecBuf;

   /* File does not exist so go ahead and create it */
   memset (&ComErrorSpecBuf, 0, sizeof (ComErrorSpecBuf));
   ComErrorSpecBuf.RecordLength = sizeof (ComErrorRecord);
   ComErrorSpecBuf.PageSize = 512;
   ComErrorSpecBuf.FileFlags = B_PREALLOC;
   ComErrorSpecBuf.IndexCount = 1;
   ComErrorSpecBuf.PreAlloc = 40;
   ComErrorSpecBuf.KeySpecBuf[0].KeyPos = (USHORT) ((ULONG) &ComErrorRecord.TimeStamp - (ULONG) &ComErrorRecord) + 1;
   ComErrorSpecBuf.KeySpecBuf[0].KeyLen = sizeof (ComErrorRecord.TimeStamp);
   ComErrorSpecBuf.KeySpecBuf[0].KeyFlag = B_MOD | B_DUP | B_EXT;
   ComErrorSpecBuf.KeySpecBuf[0].KeyType = B_UNSIGNED;

   BufLen = sizeof (ComErrorSpecBuf);
   if(BTRV (B_CREATE,
            TempPosBlk,
            &ComErrorSpecBuf,
            &BufLen,
            FileName,
            0))
   {
      return(COMERRORCREATE);
   }

   return(NORMAL);
}

/* Routine to initialize Btrieve databases */
IM_EX_CTIBASE INT InitProgErrorLog ()
{
   extern BYTE ProgErrorPosBlk[];
   PROG_ERROR_LOG_STRUCT ProgErrorRecord;
   USHORT BufLen;
   ULONG i;
   BYTE KeyBuf[64];
   PROGERRORSPECBUF ProgErrorSpecBuf;

   BufLen = sizeof (ProgErrorRecord);
   if((i = BTRV (B_OPEN,
                 ProgErrorPosBlk,
                 &ProgErrorRecord,
                 &BufLen,
                 PROGLOGFILENAME,
                 B_OPEN_NORMAL)) != NORMAL)
   {
      if(i != B_ERR_FNF)
         return(PROGERROROPEN);
      else
      {
         /* File does not exist so go ahead and create it */
         memset (&ProgErrorSpecBuf, 0, sizeof (ProgErrorSpecBuf));
         ProgErrorSpecBuf.RecordLength = sizeof (ProgErrorRecord);
         ProgErrorSpecBuf.PageSize = 512;
         ProgErrorSpecBuf.FileFlags = B_PREALLOC;
         ProgErrorSpecBuf.IndexCount = 1;
         ProgErrorSpecBuf.PreAlloc = 1;
         ProgErrorSpecBuf.KeySpecBuf[0].KeyPos = (USHORT) ((ULONG) &ProgErrorRecord.ProgramName - (ULONG) &ProgErrorRecord) + 1;
         ProgErrorSpecBuf.KeySpecBuf[0].KeyLen = sizeof (ProgErrorRecord.ProgramName);
         ProgErrorSpecBuf.KeySpecBuf[0].KeyFlag = B_MOD | B_DUP | B_EXT | B_SEG;
         ProgErrorSpecBuf.KeySpecBuf[0].KeyType = B_STRING;
         ProgErrorSpecBuf.KeySpecBuf[1].KeyPos = (USHORT) ((ULONG) &ProgErrorRecord.TimeStamp - (ULONG) &ProgErrorRecord) + 1;
         ProgErrorSpecBuf.KeySpecBuf[1].KeyLen = sizeof (ProgErrorRecord.TimeStamp);
         ProgErrorSpecBuf.KeySpecBuf[1].KeyFlag = B_MOD | B_DUP | B_EXT;
         ProgErrorSpecBuf.KeySpecBuf[1].KeyType = B_UNSIGNED;
         BufLen = sizeof (ProgErrorSpecBuf);

         if(BTRV (B_CREATE,
                  ProgErrorPosBlk,
                  &ProgErrorSpecBuf,
                  &BufLen,
                  PROGLOGFILENAME,
                  0))
         {
            return(PROGERRORCREATE);
         }

         BufLen = sizeof (ProgErrorRecord);
         if(BTRV (B_OPEN,
                  ProgErrorPosBlk,
                  &ProgErrorRecord,
                  &BufLen,
                  PROGLOGFILENAME,
                  B_OPEN_NORMAL))
         {
            return(PROGERROROPEN);
         }
      }
   }

   else
   {
      /* Check that the record size is still what it should be */
      /* Note that this does NOT warrenty against changes in indexing
         or ordering of the record */
      BufLen = sizeof (ProgErrorSpecBuf);
      if(BTRV (B_STAT,
               ProgErrorPosBlk,
               &ProgErrorSpecBuf,
               &BufLen,
               KeyBuf,
               0) || ProgErrorSpecBuf.RecordLength != sizeof (ProgErrorRecord))
      {
         printf("AL5\n");
         return(RECORDCHANGED);
      }
   }

   return(NORMAL);

}

/* Routine to close Alarm log database */
IM_EX_CTIBASE INT CloseAlarmLog ()
{
   extern BYTE AlarmLogPosBlk[];
   USHORT BufLen;


   BTRV (B_CLOSE,
         AlarmLogPosBlk,
         NULL,
         &BufLen,
         NULL,
         0);

   return(NORMAL);

}

/* Routine to close Alarm summary database */
IM_EX_CTIBASE INT CloseAlarmSum ()
{
   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;

   /* close noncritical alarm summary */
   BTRV (B_CLOSE,
         AlarmSum0PosBlk,
         NULL,
         &BufLen,
         NULL,
         0);

   /* close critical alarm summary */
   BTRV (B_CLOSE,
         AlarmSum1PosBlk,
         NULL,
         &BufLen,
         NULL,
         0);

   return(NORMAL);

}

/* Routine to close Com Error database */
IM_EX_CTIBASE INT CloseComError ()
{
   extern BYTE ComErrorPosBlk[];
   USHORT BufLen;

   BTRV (B_CLOSE,
         ComErrorPosBlk,
         NULL,
         &BufLen,
         NULL,
         0);

   return(NORMAL);

}


/* Routine to close Program Error database */
IM_EX_CTIBASE INT CloseProgError ()
{
   extern BYTE ProgErrorPosBlk[];
   USHORT BufLen;

   BTRV (B_CLOSE,
         ProgErrorPosBlk,
         NULL,
         &BufLen,
         NULL,
         0);

   return(NORMAL);

}

/* Routine to add Alarm log and add or update summary */
IM_EX_CTIBASE INT LogAlarm (ALARM_SUM_STRUCT *AlarmSumRecord,
                         USHORT AlarmClass,
                         USHORT LogFlag,
                         CTIPOINT *PointRecord,
                         USHORT CurrentAlarmMask)

{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   extern BYTE AlarmLogPosBlk[];

   USHORT BufLen;
   ULONG i;
   ALARM_LOG_STRUCT AlarmLogRecord;
   SYSTEMLOGMESS LogMessage;
   CLASSDEVICENAME KeyClassDevice;
   ALARMDEVICEPOINTNAME KeyDevicePoint;
   ALARM_SUM_STRUCT DummyAlarmSum;
   DRPVALUE DrpValue;

   /* first add it to the alarm log */
   memcpy (AlarmSumRecord->DeviceName, PointRecord->DeviceName, sizeof (AlarmSumRecord->DeviceName));
   memcpy (AlarmLogRecord.DeviceName, AlarmSumRecord->DeviceName, sizeof (AlarmSumRecord->DeviceName));
   memcpy (AlarmSumRecord->PointName, PointRecord->PointName, sizeof (AlarmSumRecord->PointName));
   memcpy (AlarmLogRecord.PointName, AlarmSumRecord->PointName, sizeof (AlarmSumRecord->PointName));
   memcpy (AlarmLogRecord.AlarmText, AlarmSumRecord->AlarmText, sizeof (AlarmSumRecord->AlarmText));
   AlarmLogRecord.TimeStamp = AlarmSumRecord->TimeStamp;
   AlarmLogRecord.StatusFlag = AlarmSumRecord->StatusFlag;
   AlarmLogRecord.LogCode = AlarmSumRecord->LogCode;
   AlarmLogRecord.AlarmClass = AlarmClass;

   BufLen = sizeof (AlarmLogRecord);
   if(!(i = BTRV (B_INSERT,
                  AlarmLogPosBlk,
                  &AlarmLogRecord,
                  &BufLen,
                  &KeyClassDevice,
                  KEYCLASSDEVICE)))
   {
      /* update the history DB */
      BtrvHistUpdate (ALARMLOGFILENAME);
   }


   /* send to the event logger */
   LogMessage.TimeStamp = AlarmSumRecord->TimeStamp;
   LogMessage.StatusFlag = AlarmSumRecord->StatusFlag;
   if(LogFlag == TRUE)
      LogMessage.StatusFlag |= EVENTLOG;          // this flags to log on the printer

   LogMessage.Originator = ALARMSYSTEM;            // no originator needed for alarms
   memcpy (LogMessage.DeviceName, AlarmSumRecord->DeviceName, sizeof (AlarmSumRecord->DeviceName));
   memcpy (LogMessage.PointName, AlarmSumRecord->PointName, sizeof (AlarmSumRecord->PointName));
   LogMessage.EventType = ALARMEVENT;

   /* fill in the log label */
   switch(AlarmSumRecord->LogCode)
   {

   case INVALIDDATA:
      memcpy (LogMessage.EventLable, INVALIDDATA_ALARMLABEL, 3);     // event label
      break;

   case PLUGGED:
      memcpy (LogMessage.EventLable, PLUGDATA_ALARMLABEL, 3);        // event label
      break;

   case HLDATA:
      memcpy (LogMessage.EventLable, HLDATA_ALARMLABEL, 3);      // event label
      break;

   case LLDATA:
      memcpy (LogMessage.EventLable, LLDATA_ALARMLABEL, 3);      // event label
      break;

   case HWDATA:
      memcpy (LogMessage.EventLable, HWDATA_ALARMLABEL, 3);      // event label
      break;

   case LWDATA:
      memcpy (LogMessage.EventLable, LWDATA_ALARMLABEL, 3);      // event label
      break;

   case DATALOST:
      memcpy (LogMessage.EventLable, DATALOST_ALARMLABEL, 3);        // event label
      break;

   case STATECHANGE:
      memcpy (LogMessage.EventLable, STATECHANGE_ALARMLABEL, 3);     // event label
      break;

   case STATEALARM:
      memcpy (LogMessage.EventLable, STATEALARM_ALARMLABEL, 3);      // event label
      break;

   case SYSTEMERROR:
      memcpy (LogMessage.EventLable, SYSTEMERROR_ALARMLABEL, 3);     // event label
      break;

   case DBSETUPERROR:
      memcpy (LogMessage.EventLable, DBSETUPERROR_ALARMLABEL, 3);        // event label
      break;

   case ABNORMALSTATEALARM:
      memcpy (LogMessage.EventLable, ABNORMALSTATEALARM_ALARMLABEL, 3);        // event label
      break;

   case CONTROLEVENT:
      memcpy (LogMessage.EventLable, CONTROLEVENT_ALARMLABEL, 3);        // event label
      break;

   case DEVICEPERCENTLOW:
      memcpy (LogMessage.EventLable, DEVICEPERCENTLOW_LABEL, 3);        // event label
      break;

   case DEVICEPERCENT24LOW:
      memcpy (LogMessage.EventLable, DEVICEPERCENT24LOW_LABEL, 3);        // event label
      break;

   case DEVICECOMMFAILURE:
      memcpy (LogMessage.EventLable, DEVICECOMMFAILURE_LABEL, 3);        // event label
      break;

   case DEVICECOMMSUCCESS:
      memcpy (LogMessage.EventLable, DEVICECOMMSUCCESS_LABEL, 3);        // event label
      break;

   case REMOTEPERCENTLOW:
      memcpy (LogMessage.EventLable, REMOTEPERCENTLOW_LABEL, 3);        // event label
      break;

   case REMOTEPERCENT24LOW:
      memcpy (LogMessage.EventLable, REMOTEPERCENT24LOW_LABEL, 3);        // event label
      break;

   case REMOTECOMMFAILURE:
      memcpy (LogMessage.EventLable, REMOTECOMMFAILURE_LABEL, 3);        // event label
      break;

   case REMOTECOMMSUCCESS:
      memcpy (LogMessage.EventLable, REMOTECOMMSUCCESS_LABEL, 3);        // event label
      break;

   case PORTPERCENTLOW:
      memcpy (LogMessage.EventLable, PORTPERCENTLOW_LABEL, 3);        // event label
      break;

   case PORTPERCENT24LOW:
      memcpy (LogMessage.EventLable, PORTPERCENT24LOW_LABEL, 3);        // event label
      break;

   case PORTCOMMFAILURE:
      memcpy (LogMessage.EventLable, PORTCOMMFAILURE_LABEL, 3);        // event label
      break;

   case PORTCOMMSUCCESS:
      memcpy (LogMessage.EventLable, PORTCOMMSUCCESS_LABEL, 3);        // event label
      break;

   case ROUTEPERCENTLOW:
      memcpy (LogMessage.EventLable, ROUTEPERCENTLOW_LABEL, 3);        // event label
      break;

   case ROUTEPERCENT24LOW:
      memcpy (LogMessage.EventLable, ROUTEPERCENT24LOW_LABEL, 3);        // event label
      break;

   case ROUTECOMMFAILURE:
      memcpy (LogMessage.EventLable, ROUTECOMMFAILURE_LABEL, 3);        // event label
      break;

   case ROUTECOMMSUCCESS:
      memcpy (LogMessage.EventLable, ROUTECOMMSUCCESS_LABEL, 3);        // event label
      break;

   case NETCOMMFAILURE:
      memcpy (LogMessage.EventLable, NETCOMMFAILURE_LABEL, 3);        // event label
      break;

   case NETCOMMSUCCESS:
      memcpy (LogMessage.EventLable, NETCOMMSUCCESS_LABEL, 3);        // event label
      break;

   case ACOUNTVIOLATION:
      memcpy (LogMessage.EventLable, ACOUNTVIOLATION_LABEL, 3);        // event label
      break;

   default:
      memcpy (LogMessage.EventLable, DEFAULT_ALARMLABEL, 3);     // event label
   }

   LogMessage.EventLable[3] = ' ';
   memcpy (LogMessage.LogMessage1,
           AlarmSumRecord->AlarmText,
           sizeof (AlarmSumRecord->AlarmText));     // alarm description
   LogMessage.LogMessage2[0] = ' ';        // this causes second line not to be logged

   /* send to event logger */
   LogEvent (&LogMessage);

   if(AlarmSumRecord->AcknowlegeFlag != FALSE)
   {
      if(AlarmClass == CRITICALCLASS)
         /* generate continuous audilble tone  */
         LogMessage.StatusFlag = AUDIBLEALARM;
      else
         /* getnerate a single warning beep */
         LogMessage.StatusFlag = NONCRITAUDIBLE;

      /* set message to event log to make some noise */
      LogEvent (&LogMessage);
   }

   /* Check and see if this device/point already exist in the summary */
   memcpy (DummyAlarmSum.DeviceName, AlarmSumRecord->DeviceName, sizeof (AlarmSumRecord->DeviceName));
   memcpy (DummyAlarmSum.PointName, AlarmSumRecord->PointName, sizeof (AlarmSumRecord->PointName));

   if((PointRecord->AlarmMask & CRITUNACK) || (PointRecord->AlarmMask & NONCRITUNACK))
      /* we want an alarm on unack time-out */
      AlarmSumRecord->UnAckTimeOutTime = AlarmSumRecord->TimeStamp + (ULONG) (UnackTimeInterval * 60);  // set unack time limit
   else
      /* set time-out way ahead not used */
      AlarmSumRecord->UnAckTimeOutTime = 0xFFFFFFFF;


   if(AlarmSumgetEqual(&DummyAlarmSum, AlarmClass))
   {
      AlarmSumRecord->AcknowlegeFlag = ALARMEDUNACK;
      AlarmSumRecord->StateChangeCount = 1;
      BufLen = sizeof (*AlarmSumRecord);
      if(AlarmClass == CRITICALCLASS)
      {
         if(BTRV (B_INSERT,        // put it in the critical summary
                  AlarmSum1PosBlk,
                  AlarmSumRecord,
                  &BufLen,
                  &KeyDevicePoint,
                  KEYALARDEVICEPOINT))
         {
            return(INSERTERROR);
         }

         /* update the history DB */
         BtrvHistUpdate (ALARMSUM1FILENAME);
      }
      else
      {
         if(BTRV (B_INSERT,        // put it in the non-critical summary
                  AlarmSum0PosBlk,
                  AlarmSumRecord,
                  &BufLen,
                  &KeyDevicePoint,
                  KEYALARDEVICEPOINT))
         {
            return(INSERTERROR);
         }
         /* update the history DB */
         BtrvHistUpdate (ALARMSUM0FILENAME);
      }
   }
   else
   {
      if(DummyAlarmSum.AcknowlegeFlag == ALARMEDACK)
         /* alarm is acknowleged state is reset */
         DummyAlarmSum.StateChangeCount = 1;
      else
         DummyAlarmSum.StateChangeCount++;    // add to state change count

      DummyAlarmSum.AcknowlegeFlag = ALARMEDUNACK;
      DummyAlarmSum.TimeStamp = AlarmSumRecord->TimeStamp;     // set new time stamp
      DummyAlarmSum.StatusFlag = AlarmSumRecord->StatusFlag;
      DummyAlarmSum.LogCode = AlarmSumRecord->LogCode;
      memcpy (DummyAlarmSum.AlarmText, AlarmSumRecord->AlarmText, sizeof (AlarmSumRecord->AlarmText));
      DummyAlarmSum.UnAckTimeOutTime = AlarmSumRecord->UnAckTimeOutTime;

      /* update the summary record */
      BufLen = sizeof (DummyAlarmSum);
      if(AlarmClass == CRITICALCLASS)
      {
         if(BTRV (B_UPDATE,         // update the critical summary
                  AlarmSum1PosBlk,
                  &DummyAlarmSum,
                  &BufLen,
                  &KeyDevicePoint,
                  KEYALARDEVICEPOINT))
         {
            return(UPDATEERROR);
         }
         /* update the history DB */
         BtrvHistUpdate (ALARMSUM1FILENAME);
      }
      else
      {
         if(BTRV (B_UPDATE,         // update the non-critical summary
                  AlarmSum0PosBlk,
                  &DummyAlarmSum,
                  &BufLen,
                  &KeyDevicePoint,
                  KEYALARDEVICEPOINT))
         {
            return(UPDATEERROR);
         }

         /* update the history DB */
         BtrvHistUpdate (ALARMSUM0FILENAME);
      }
   }

   /* check to see if we need to send him to drp */
   if(PointRecord->AlarmDRPMask & CurrentAlarmMask)
   {
      /* yep we need to send the alarm to drp so fill in the data */
      memcpy (DrpValue.DeviceName, AlarmSumRecord->DeviceName, sizeof (AlarmSumRecord->DeviceName));
      memcpy (DrpValue.PointName, AlarmSumRecord->PointName, sizeof (AlarmSumRecord->PointName));
      DrpValue.Type = DRPTYPEALARM;
      DrpValue.AlarmState = ALARMEDUNACK;
      DrpValue.TimeStamp = AlarmSumRecord->TimeStamp;
      DrpValue.Quality = PointRecord->CurrentQuality;

      switch(PointRecord->PointType)
      {

      case TWOSTATEPOINT:
      case THREESTATEPOINT:
      case PSEUDOSTATUSPOINT:
      case PSEUDOALARMPOINT:
         /* value is the state for status points */
         DrpValue.Value = PointRecord->CurrentValue;
         break;

      default:
         /* value is -1 if its not a status point and
             quality must be used to figure out the alarm state */
         DrpValue.Value = -1.0;
      }

      /* send on it on it merry way to drp */
      SendDRPPoint (&DrpValue);
   }

   return(NORMAL);

}

/* Routine to delete an entry in the summary */
IM_EX_CTIBASE INT AlarmSumDelete (ALARM_SUM_STRUCT *AlarmSumRecord, USHORT AlarmClass)
{
   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;
   // ALARMDEVICEPOINTNAME KeyDevicePoint;
   ALARM_SUM_STRUCT DummyAlarmSum;


   /* Check and see if this device/point already exist in the summary */
   memcpy (DummyAlarmSum.DeviceName, AlarmSumRecord->DeviceName, sizeof (AlarmSumRecord->DeviceName));
   memcpy (DummyAlarmSum.PointName, AlarmSumRecord->PointName, sizeof (AlarmSumRecord->PointName));

   if(AlarmSumgetEqual(&DummyAlarmSum, AlarmClass))
      return(POINTUNKNOWN);


   /* delete it */
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_DELETE,
               AlarmSum1PosBlk,      // delete from critical summary
               NULL,
               &BufLen,
               NULL,
               0))
         return(DELETEERROR);

      /* update the history DB */
      BtrvHistUpdate (ALARMSUM1FILENAME);
   }
   else
   {
      if(BTRV (B_DELETE,
               AlarmSum0PosBlk,      // delete from non-critical summary
               NULL,
               &BufLen,
               NULL,
               0))
         return(DELETEERROR);

      /* update the history DB */
      BtrvHistUpdate (ALARMSUM0FILENAME);
   }

   return(NORMAL);

}

IM_EX_CTIBASE INT AlarmSumgetEqual (ALARM_SUM_STRUCT *AlarmSumRecord, USHORT AlarmClass)
{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;
   ALARMDEVICEPOINTNAME KeyDevicePoint;

   /* get the record from the database */
   memcpy (KeyDevicePoint.DeviceName, AlarmSumRecord->DeviceName, sizeof (AlarmSumRecord->DeviceName));
   memcpy (KeyDevicePoint.PointName, AlarmSumRecord->PointName, sizeof (AlarmSumRecord->PointName));
   BufLen = sizeof (*AlarmSumRecord);
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_GETEQ,
               AlarmSum1PosBlk,            // look in critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyDevicePoint,
               KEYALARDEVICEPOINT))
         return(POINTUNKNOWN);
   }
   else
   {
      if(BTRV (B_GETEQ,
               AlarmSum0PosBlk,            // look in non-critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyDevicePoint,
               KEYALARDEVICEPOINT))
         return(POINTUNKNOWN);
   }

   return(NORMAL);

}

IM_EX_CTIBASE INT AlarmSumGetFirst (ALARM_SUM_STRUCT *AlarmSumRecord, USHORT AlarmClass)
{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;
   ALARMDEVICEPOINTNAME KeyDevicePoint;


   /* get the record from the database */
   BufLen = sizeof (*AlarmSumRecord);
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_GETFIRST,
               AlarmSum1PosBlk,     // from critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyDevicePoint,
               KEYALARDEVICEPOINT))
         return(GETFIRSTERROR);
   }
   else
   {
      if(BTRV (B_GETFIRST,
               AlarmSum0PosBlk,     // from non-critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyDevicePoint,
               KEYALARDEVICEPOINT))
         return(GETFIRSTERROR);
   }

   return(NORMAL);

}


IM_EX_CTIBASE INT AlarmSumgetNext (ALARM_SUM_STRUCT *AlarmSumRecord, USHORT AlarmClass)
{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;
   ALARMDEVICEPOINTNAME KeyDevicePoint;

   /* get the record from the database */
   BufLen = sizeof (*AlarmSumRecord);
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_GETNEXT,
               AlarmSum1PosBlk,     // from critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyDevicePoint,
               KEYALARDEVICEPOINT))
         return(GETNEXTERROR);
   }
   else
   {
      if(BTRV (B_GETNEXT,
               AlarmSum0PosBlk,     // from non-critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyDevicePoint,
               KEYALARDEVICEPOINT))
         return(GETNEXTERROR);
   }

   return(NORMAL);

}


IM_EX_CTIBASE INT AlarmSumgetPrev (ALARM_SUM_STRUCT *AlarmSumRecord, USHORT AlarmClass)
{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;
   ALARMDEVICEPOINTNAME KeyDevicePoint;

   /* get the record from the database */
   BufLen = sizeof (*AlarmSumRecord);
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_GETPREV,
               AlarmSum1PosBlk,     // from critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyDevicePoint,
               KEYALARDEVICEPOINT))
         return(GETPREVERROR);
   }
   else
   {
      if(BTRV (B_GETPREV,
               AlarmSum0PosBlk,     // from non-critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyDevicePoint,
               KEYALARDEVICEPOINT))
         return(GETPREVERROR);
   }

   return(NORMAL);

}

IM_EX_CTIBASE INT AlarmSumGetFirstTime(ALARM_SUM_STRUCT *AlarmSumRecord, USHORT AlarmClass)
{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;
   TIMESTAMP KeyTime;


   /* get the record from the database */
   BufLen = sizeof (*AlarmSumRecord);
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_GETFIRST,
               AlarmSum1PosBlk,     // from critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYTIMESTAMP))
         return(GETFIRSTERROR);
   }
   else
   {
      if(BTRV (B_GETFIRST,
               AlarmSum0PosBlk,     // from non-critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYTIMESTAMP))
         return(GETFIRSTERROR);
   }

   return(NORMAL);

}

IM_EX_CTIBASE INT AlarmSumgetNextTime (ALARM_SUM_STRUCT *AlarmSumRecord, USHORT AlarmClass)
{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;
   TIMESTAMP KeyTime;

   /* get the record from the database */
   BufLen = sizeof (*AlarmSumRecord);
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_GETNEXT,
               AlarmSum1PosBlk,     // from critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYTIMESTAMP))
         return(GETNEXTERROR);
   }
   else
   {
      if(BTRV (B_GETNEXT,
               AlarmSum0PosBlk,     // from non-critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYTIMESTAMP))
         return(GETNEXTERROR);
   }

   return(NORMAL);

}

IM_EX_CTIBASE INT AlarmSumgetPrevTime (ALARM_SUM_STRUCT *AlarmSumRecord, USHORT AlarmClass)
{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;
   TIMESTAMP KeyTime;

   /* get the record from the database */
   BufLen = sizeof (*AlarmSumRecord);
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_GETPREV,
               AlarmSum1PosBlk,     // from critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYTIMESTAMP))
         return(GETPREVERROR);
   }
   else
   {
      if(BTRV (B_GETPREV,
               AlarmSum0PosBlk,     // from non-critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYTIMESTAMP))
         return(GETPREVERROR);
   }

   return(NORMAL);

}


IM_EX_CTIBASE INT AlarmLogGetFirst (ALARM_LOG_STRUCT *AlarmLogRecord)
{

   extern BYTE AlarmLogPosBlk[];
   USHORT BufLen;
   CLASSTIME KeyClassTime;

   /* alarm class must be checked */
   SaveClass = AlarmLogRecord->AlarmClass;   //Save class for
   if(AlarmLogRecord->AlarmClass == CRITICALCLASS)
   {
      KeyClassTime.AlarmClass = NONCRITICALCLASS;
      KeyClassTime.TimeStamp = 0L;
      BufLen = sizeof (*AlarmLogRecord);
      if(BTRV (B_GETGT,
               AlarmLogPosBlk,
               AlarmLogRecord,
               &BufLen,
               &KeyClassTime,
               KEYCLASSTIME))
      {
         return(GETFIRSTERROR);
      }
   }
   else
   {
      BufLen = sizeof (*AlarmLogRecord);
      if(BTRV (B_GETFIRST,
               AlarmLogPosBlk,
               AlarmLogRecord,
               &BufLen,
               &KeyClassTime,
               KEYCLASSTIME))
      {
         return(GETFIRSTERROR);
      }
   }

   return(NORMAL);

}


IM_EX_CTIBASE INT AlarmLoggetNext (ALARM_LOG_STRUCT *AlarmLogRecord)
{

   extern BYTE AlarmLogPosBlk[];
   USHORT BufLen;
   CLASSTIME KeyClassTime;

   BufLen = sizeof (*AlarmLogRecord);
   if(BTRV (B_GETNEXT,
            AlarmLogPosBlk,
            AlarmLogRecord,
            &BufLen,
            &KeyClassTime,
            KEYCLASSTIME) || SaveClass != AlarmLogRecord->AlarmClass)
   {
      return(GETNEXTERROR);
   }

   return(NORMAL);

}

IM_EX_CTIBASE INT AlarmLoggetPrev (ALARM_LOG_STRUCT *AlarmLogRecord)
{

   extern BYTE AlarmLogPosBlk[];
   USHORT BufLen;
   CLASSTIME KeyClassTime;

   BufLen = sizeof (*AlarmLogRecord);
   if(BTRV (B_GETPREV,
            AlarmLogPosBlk,
            AlarmLogRecord,
            &BufLen,
            &KeyClassTime,
            KEYCLASSTIME) || SaveClass != AlarmLogRecord->AlarmClass)
   {
      return(GETPREVERROR);
   }

   return(NORMAL);

}

IM_EX_CTIBASE INT AlarmLoggetNewest (ALARM_LOG_STRUCT *AlarmLogRecord, PBYTE MyPosBlk)
{

   USHORT BufLen;
   TIMESTAMP KeyTimeStamp;

   BufLen = sizeof (*AlarmLogRecord);
   /* Key is DESENDING Order */
   if(BTRV (B_GETFIRST,
            MyPosBlk,
            AlarmLogRecord,
            &BufLen,
            &KeyTimeStamp,
            KEYTIMESTAMP))
   {
      return(GETFIRSTERROR);
   }

   return(NORMAL);
}

IM_EX_CTIBASE INT LogSumRecords (PULONG Records, USHORT AlarmClass)
{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   BYTE KeyBuf[64];
   USHORT BufLen;
   ALARMSUMSPECBUF AlarmSumSpecBuf;


   /* get the file information */
   BufLen = sizeof (AlarmSumSpecBuf);
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_STAT,
               AlarmSum1PosBlk,     //from the critical alarm summary
               &AlarmSumSpecBuf,
               &BufLen,
               KeyBuf,
               0))
         return(ERRUNKNOWN);
   }
   else
   {
      if(BTRV (B_STAT,
               AlarmSum0PosBlk,     //from the non-critical alarm summary
               &AlarmSumSpecBuf,
               &BufLen,
               KeyBuf,
               0))
         return(ERRUNKNOWN);
   }

   /* ok so return # records */
   *Records = AlarmSumSpecBuf.RecordCount;

   return(NORMAL);

}

/* Routine to add comm error Btrieve databases */
IM_EX_CTIBASE INT ComErrorLogAdd (COMM_ERROR_LOG_STRUCT *ComErrorRecord,
                               ERRSTRUCT *ErrorRecord,
                               USHORT LogFlag)
{
   extern BYTE ComErrorPosBlk[];
   USHORT BufLen;
   TIMESTAMP KeyTimeStamp;
   SYSTEMLOGMESS LogMessage;
   ULONG Records;
   COMM_ERROR_LOG_STRUCT DummyRecord;

   /* check for a good time */
   if(ComErrorRecord->TimeStamp < 347184000L)
   {
      /* set the time its not set or is less than Jan 1 1981 */
      ComErrorRecord->TimeStamp = LongTime();
      ComErrorRecord->StatusFlag = DSTSET (ComErrorRecord->StatusFlag);
   }

   /* send to the event logger */
   LogMessage.TimeStamp = ComErrorRecord->TimeStamp;
   if(ComErrorRecord->StatusFlag & DSTACTIVE)
   {
      LogMessage.StatusFlag = DSTACTIVE;
   }
   else
   {
      LogMessage.StatusFlag = FALSE;
   }

   if(LogFlag == TRUE)
      LogMessage.StatusFlag |= EVENTLOG;          // this flags to log on the printer

   LogMessage.Originator = ALARMSYSTEM;            // no originator needed
   memcpy (LogMessage.DeviceName, ComErrorRecord->DeviceName, sizeof (ComErrorRecord->DeviceName));
   memcpy (LogMessage.PointName, ComErrorRecord->RouteName, sizeof (ComErrorRecord->RouteName));
   LogMessage.EventType = COMMUNICATIONERROR;
   memcpy (LogMessage.EventLable, "Ec ", 3);      // event label
   LogMessage.EventLable[3] = ' ';
   memset (LogMessage.LogMessage1,
           ' ',
           sizeof (LogMessage.LogMessage1));
   memcpy (LogMessage.LogMessage1, ErrorRecord->TypeDescription, sizeof (ErrorRecord->TypeDescription));       // error type description
   LogMessage.LogMessage2[0] = ' ';        // this causes second line not to be logged

   /* send to event logger */
   LogEvent(&LogMessage);

   ComErrorLogRecords (&Records);
   Records += 1;

   if(Records > ComRecordMax)
   {
      /* must delete the last record */
      BufLen = sizeof (DummyRecord);
      if(!(BTRV (B_GETFIRST,
                 ComErrorPosBlk,
                 &DummyRecord,
                 &BufLen,
                 &KeyTimeStamp,
                 KEYTIMESTAMP)))
         BTRV (B_DELETE,               // delete the record
               ComErrorPosBlk,
               NULL,
               &BufLen,
               NULL,
               0);

   }

   /* fil1 in error info */
   ComErrorRecord->Error = ErrorRecord->Error;

   BufLen = sizeof (*ComErrorRecord);
   if(BTRV (B_INSERT,
            ComErrorPosBlk,
            ComErrorRecord,
            &BufLen,
            &KeyTimeStamp,
            KEYTIMESTAMP))
      return(INSERTERROR);

   /* update the history DB */
   BtrvHistUpdate (COMMLOGFILENAME);
   return(NORMAL);
}

/* Routine to get comm error Btrieve databases */
IM_EX_CTIBASE INT ComErrorLogGetFirstTime (COMM_ERROR_LOG_STRUCT *ComErrorRecord)
{
   extern BYTE ComErrorPosBlk[];
   USHORT BufLen;
   TIMESTAMP KeyTimeStamp;

   BufLen = sizeof (*ComErrorRecord);
   if(BTRV (B_GETFIRST,
            ComErrorPosBlk,
            ComErrorRecord,
            &BufLen,
            &KeyTimeStamp,
            KEYTIMESTAMP))
      return(GETFIRSTERROR);

   return(NORMAL);

}

/* Routine to get comm error Btrieve databases */
IM_EX_CTIBASE INT ComErrorLoggetLastTime (COMM_ERROR_LOG_STRUCT *ComErrorRecord)
{

   extern BYTE ComErrorPosBlk[];

   return(ComErrorLogxgetLastTime (ComErrorRecord, ComErrorPosBlk));

}

IM_EX_CTIBASE INT ComErrorLogxgetLastTime (COMM_ERROR_LOG_STRUCT *ComErrorRecord,
                                        PBYTE MyPosBlk)
{
   USHORT BufLen;
   TIMESTAMP KeyTimeStamp;

   BufLen = sizeof (*ComErrorRecord);
   if(BTRV (B_GETLAST,
            MyPosBlk,
            ComErrorRecord,
            &BufLen,
            &KeyTimeStamp,
            KEYTIMESTAMP))
      return(GETLASTERROR);

   return(NORMAL);
}

/* Routine to get comm error from Btrieve database */
IM_EX_CTIBASE INT ComErrorLoggetNextTime (COMM_ERROR_LOG_STRUCT *ComErrorRecord)
{
   extern BYTE ComErrorPosBlk[];
   USHORT BufLen;
   TIMESTAMP KeyTimeStamp;

   BufLen = sizeof (*ComErrorRecord);
   if(BTRV (B_GETNEXT,
            ComErrorPosBlk,
            ComErrorRecord,
            &BufLen,
            &KeyTimeStamp,
            KEYTIMESTAMP))
      return(GETNEXTERROR);

   return(NORMAL);

}

/* Routine to get comm error from Btrieve database */
IM_EX_CTIBASE INT ComErrorLoggetPrevTime (COMM_ERROR_LOG_STRUCT *ComErrorRecord)
{
   extern BYTE ComErrorPosBlk[];
   USHORT BufLen;
   TIMESTAMP KeyTimeStamp;

   BufLen = sizeof (*ComErrorRecord);
   if(BTRV (B_GETPREV,
            ComErrorPosBlk,
            ComErrorRecord,
            &BufLen,
            &KeyTimeStamp,
            KEYTIMESTAMP))
      return(GETPREVERROR);

   return(NORMAL);

}

/* Routine to add comm error Btrieve databases */
IM_EX_CTIBASE INT ProgErrorLogAdd (PROG_ERROR_LOG_STRUCT *ProgErrorRecord)
{
   extern BYTE ProgErrorPosBlk[];
   USHORT BufLen;
   ULONG i = 0, Result;
   PROG_ERROR_LOG_STRUCT DummyProgError;
   PROGRAMNAMETIME KeyProgNameTime;

   /* only save 10 errors so we must count how many we have for this progam */
   memcpy (KeyProgNameTime.ProgramName, ProgErrorRecord->ProgramName, sizeof (ProgErrorRecord->ProgramName));
   KeyProgNameTime.TimeStamp = 0L;
   BufLen = sizeof (DummyProgError);

   /* see if there 10 logs for this progam */
   Result = BTRV (B_GETGE,
                  ProgErrorPosBlk,
                  &DummyProgError,
                  &BufLen,
                  &KeyProgNameTime,
                  KEYPROGRAMNAMETIME);

   if(!(Result))
      Result = strnicmp (KeyProgNameTime.ProgramName, ProgErrorRecord->ProgramName, sizeof (ProgErrorRecord->ProgramName));

   if(!(Result))
   {
      /*  search for 9 more entries */
      for(i = 1; i < 10; i++)
      {
         BufLen = sizeof (DummyProgError);
         if((Result = BTRV (B_GETNEXT,
                            ProgErrorPosBlk,
                            &DummyProgError,
                            &BufLen,
                            &KeyProgNameTime,
                            KEYPROGRAMNAMETIME)) ||
            (strnicmp (KeyProgNameTime.ProgramName, ProgErrorRecord->ProgramName, sizeof (ProgErrorRecord->ProgramName))))
         {
            break;  // no more
         }
      }
   }

   /* i is 10 then update */
   if(i == 10)
   {
      memcpy (KeyProgNameTime.ProgramName, ProgErrorRecord->ProgramName, sizeof (ProgErrorRecord->ProgramName));
      KeyProgNameTime.TimeStamp = 0L;
      BufLen = sizeof (DummyProgError);
      if(BTRV (B_GETGE,
               ProgErrorPosBlk,
               &DummyProgError,
               &BufLen,
               &KeyProgNameTime,
               KEYPROGRAMNAMETIME))
      {
         return(GETFIRSTERROR);
      }

      /*  update old record with new info  */
      memcpy (DummyProgError.ModuleName, ProgErrorRecord->ModuleName, sizeof (ProgErrorRecord->ModuleName));
      DummyProgError.Error = ProgErrorRecord->Error;
      DummyProgError.ProcId = ProgErrorRecord->ProcId;

      DummyProgError.TimeStamp = LongTime ();

      BufLen = sizeof (DummyProgError);
      if(BTRV (B_UPDATE,
               ProgErrorPosBlk,
               &DummyProgError,
               &BufLen,
               &KeyProgNameTime,
               KEYPROGRAMNAMETIME))
      {
         return(UPDATEERROR);
      }
   }
   else
   {
      ProgErrorRecord->TimeStamp = LongTime ();

      BufLen = sizeof (*ProgErrorRecord);
      if(BTRV (B_INSERT,
               ProgErrorPosBlk,
               ProgErrorRecord,
               &BufLen,
               &KeyProgNameTime,
               KEYPROGRAMNAMETIME))
      {
         return(INSERTERROR);
      }
   }

   return(NORMAL);
}

/* Routine to add Program error Btrieve databases */
IM_EX_CTIBASE INT ProgErrorGetFirst (PROG_ERROR_LOG_STRUCT *ProgErrorRecord)
{
   extern BYTE ProgErrorPosBlk[];
   USHORT BufLen;
   PROGRAMNAMETIME KeyProgNameTime;

   BufLen = sizeof (*ProgErrorRecord);
   if(BTRV (B_GETFIRST,
            ProgErrorPosBlk,
            ProgErrorRecord,
            &BufLen,
            &KeyProgNameTime,
            KEYPROGRAMNAMETIME))
   {
      return(GETFIRSTERROR);
   }

   return(NORMAL);

}

/* Routine to add Program error Btrieve databases */
IM_EX_CTIBASE INT ProgErrorgetNext (PROG_ERROR_LOG_STRUCT *ProgErrorRecord)
{
   extern BYTE ProgErrorPosBlk[];
   USHORT BufLen;
   PROGRAMNAMETIME KeyProgNameTime;

   BufLen = sizeof (*ProgErrorRecord);
   if(BTRV (B_GETNEXT,
            ProgErrorPosBlk,
            ProgErrorRecord,
            &BufLen,
            &KeyProgNameTime,
            KEYPROGRAMNAMETIME))
   {
      return(GETNEXTERROR);
   }

   return(NORMAL);

}

/* Routine to deletes all Program errors Btrieve databases */
IM_EX_CTIBASE INT ProgErrorDeleteAll ()
{
   extern BYTE ProgErrorPosBlk[];
   PROG_ERROR_LOG_STRUCT ProgErrorRecord;
   USHORT BufLen;

   /* Loop through the device file outputting stats */
   if(!(ProgErrorGetFirst(&ProgErrorRecord)))
   {
      do
      {

         BufLen = sizeof (ProgErrorRecord);
         BTRV (B_DELETE,
               ProgErrorPosBlk,
               NULL,
               &BufLen,
               NULL,
               0);

      } while(!(ProgErrorGetFirst(&ProgErrorRecord)));
   }

   return(NORMAL);

}

/* Routine to delete up to a date from the com alarm log */
IM_EX_CTIBASE INT ComAlarmDeleteRange  (ULONG TargetDate)
{

   extern BYTE ComErrorPosBlk[];
   COMM_ERROR_LOG_STRUCT ComErrorRecord;
   USHORT BufLen;
   TIMESTAMP KeyTimeStamp;


   /* get the target time to delete  */
   KeyTimeStamp.TimeStamp = TargetDate + 1L;

   BufLen = sizeof (ComErrorRecord);
   if(BTRV (B_GETLT,
            ComErrorPosBlk,
            &ComErrorRecord,
            &BufLen,
            &KeyTimeStamp,
            KEYTIMESTAMP))
      return(NORMAL);     // no data to delete yet

   for(;;)
   {


      /* delete the record */
      if(BTRV (B_DELETE,
               ComErrorPosBlk,
               NULL,
               &BufLen,
               NULL,
               0))
         return(DELETEERROR);

      /* get the next target time to delete  */
      KeyTimeStamp.TimeStamp = TargetDate + 1L;
      if(BTRV (B_GETLT,
               ComErrorPosBlk,
               &ComErrorRecord,
               &BufLen,
               &KeyTimeStamp,
               KEYTIMESTAMP))
         return(NORMAL);     // no more data to delete

   }

   /* update the history DB */
   BtrvHistUpdate (COMMLOGFILENAME);
   return(NORMAL);
}

/* Routine to delete up to a date from the alarm log */
IM_EX_CTIBASE INT AlarmLogDeleteRange (PULONG TargetDate)
{

   extern BYTE AlarmLogPosBlk[];
   ALARM_LOG_STRUCT AlarmLogRecord;
   USHORT BufLen;
   TIMESTAMP KeyTimeStamp;

   /* get the last record */
   BufLen = sizeof (AlarmLogRecord);
   if(BTRV (B_GETLAST,
            AlarmLogPosBlk,
            &AlarmLogRecord,
            &BufLen,
            &KeyTimeStamp,
            KEYTIMESTAMP))
      return(NORMAL);         // no data yet


   for(;;)
   {
      if(AlarmLogRecord.TimeStamp > *TargetDate)
         break;    //stop when date is reached

      /* delete the record */
      if(BTRV (B_DELETE,
               AlarmLogPosBlk,
               NULL,
               &BufLen,
               NULL,
               KEYTIMESTAMP))
         return(DELETEERROR);
      else
         if(BTRV (B_GETPREV,
                  AlarmLogPosBlk,
                  &AlarmLogRecord,
                  &BufLen,
                  &KeyTimeStamp,
                  KEYTIMESTAMP))
         return(NORMAL);         // no more data

      // CTISleep (100L);
   }

   /* update the history DB */
   BtrvHistUpdate (ALARMLOGFILENAME);
   return(NORMAL);
}

/* send a normal event log message */
IM_EX_CTIBASE INT NormalLogEvent (SYSTEMLOGMESS *LogMessage, USHORT LogFlag)
{
   /* send to the event logger */
   if(LogFlag == TRUE)
      LogMessage->StatusFlag |= EVENTLOG;         // this flags to log on the printer

   /* fill in the log label */
   switch(LogMessage->EventType)
   {

   case INVALIDDATA:
      memcpy (LogMessage->EventLable, INVALIDDATA_LABEL, 3);     // event label
      break;

   case PLUGGED:
      memcpy (LogMessage->EventLable, PLUGDATA_LABEL, 3);        // event label
      break;

   case HLDATA:
      memcpy (LogMessage->EventLable, HLDATA_LABEL, 3);      // event label
      break;

   case LLDATA:
      memcpy (LogMessage->EventLable, LLDATA_LABEL, 3);      // event label
      break;

   case HWDATA:
      memcpy (LogMessage->EventLable, HWDATA_LABEL, 3);      // event label
      break;

   case LWDATA:
      memcpy (LogMessage->EventLable, LWDATA_LABEL, 3);      // event label
      break;

   case DATALOST:
      memcpy (LogMessage->EventLable, DATALOST_LABEL, 3);        // event label
      break;

   case STATECHANGE:
      memcpy (LogMessage->EventLable, STATECHANGE_LABEL, 3);     // event label
      break;

   case STATEALARM:
      memcpy (LogMessage->EventLable, STATEALARM_LABEL, 3);      // event label
      break;

   case ABNORMALSTATEALARM:
      memcpy (LogMessage->EventLable, ABNORMALSTATEALARM_LABEL, 3);      // event label
      break;

   case SYSTEMERROR:
      memcpy (LogMessage->EventLable, SYSTEMERROR_LABEL, 3);     // event label
      break;

   case DBSETUPERROR:
      memcpy (LogMessage->EventLable, DBSETUPERROR_LABEL, 3);        // event label
      break;

   case SWITCHCONTROLMANUAL:
      memcpy (LogMessage->EventLable, SWITCHCONTROLMANUAL_LABEL, 3);     // event label
      break;

   case VOLTCONTROLMANUAL:
      memcpy (LogMessage->EventLable, VOLTCONTROLMANUAL_LABEL, 3);       // event label
      break;

   case CONTROLEVENT:
      memcpy (LogMessage->EventLable, CONTROLEVENT_LABEL, 3);        // event label
      break;

   case ALARMACKNOWLEDGE:
      memcpy (LogMessage->EventLable, ALARMACKNOWLEDGE_LABEL, 3);    // event label
      break;

   default:
      memcpy (LogMessage->EventLable, DEFAULT_LABEL, 3);     // event label

   }

   LogMessage->EventLable[3] = ' ';

   /* send to event logger */
   return(LogEvent(LogMessage));

}

IM_EX_CTIBASE INT AlarmSumUpdate (ALARM_SUM_STRUCT *AlarmSumRecord, USHORT AlarmClass)
{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;
   TIMESTAMP KeyTime;

   /* get the record from the database */
   BufLen = sizeof (*AlarmSumRecord);
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_UPDATE,
               AlarmSum1PosBlk,     // from critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYTIMESTAMP))
         return(UPDATEERROR);

      /* update the history DB */
      BtrvHistUpdate (ALARMSUM1FILENAME);
   }
   else
   {
      if(BTRV (B_UPDATE,
               AlarmSum0PosBlk,     // from non-critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYTIMESTAMP))
         return(UPDATEERROR);

      /* update the history DB */
      BtrvHistUpdate (ALARMSUM0FILENAME);
   }

   return(NORMAL);

}

/* Routine to initialize Btrieve databases */
IM_EX_CTIBASE INT ComErrorLogStatus ()
{
   extern BYTE ComErrorPosBlk[];
   // COMM_ERROR_LOG_STRUCT ComErrorRecord;
   USHORT BufLen;
   BYTE KeyBuf[64];
   COMERRORSPECBUF ComErrorstatus;

   BufLen = sizeof (ComErrorstatus);
   BTRV (B_STAT,
         ComErrorPosBlk,
         &ComErrorstatus,
         &BufLen,
         KeyBuf,
         0);

   printf ("Communication Error Log File Status\n\n");
   printf ("Record Length: %hd \n", ComErrorstatus.RecordLength);
   printf ("Page Size    : %hd \n", ComErrorstatus.PageSize);
   printf ("Index Count  : %hd \n", ComErrorstatus.IndexCount);
   printf ("Record Count : %Lu \n", ComErrorstatus.RecordCount);
   printf ("Unused Pages : %hd \n", ComErrorstatus.PreAlloc);
   printf ("File Flags   : %hd \n\n\n", ComErrorstatus.FileFlags);

   return(NORMAL);
}

/* Routine to initialize Btrieve databases */
IM_EX_CTIBASE INT ComErrorLogRecords (PULONG Records)
{
   extern BYTE ComErrorPosBlk[];

   return(ComErrorLogxRecords (Records, ComErrorPosBlk));
}

IM_EX_CTIBASE INT ComErrorLogxRecords (PULONG Records, PBYTE MyPosBlk)
{
   USHORT BufLen;
   BYTE KeyBuf[64];
   COMERRORSPECBUF ComErrorstatus;

   BufLen = sizeof (ComErrorstatus);
   if(BTRV (B_STAT,
            MyPosBlk,
            &ComErrorstatus,
            &BufLen,
            KeyBuf,
            0))
      return(!NORMAL);

   *Records = ComErrorstatus.RecordCount;

   return(NORMAL);
}

IM_EX_CTIBASE INT AlarmSumGetFirstTimeOut (ALARM_SUM_STRUCT *AlarmSumRecord,
                                        USHORT AlarmClass)
{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;
   TIMESTAMP KeyTime;


   /* get the record from the database */
   BufLen = sizeof (*AlarmSumRecord);
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_GETFIRST,
               AlarmSum1PosBlk,     // from critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYUNACKNOWLEGETIMEOUT))
         return(GETFIRSTERROR);
   }
   else
   {
      if(BTRV (B_GETFIRST,
               AlarmSum0PosBlk,     // from non-critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYUNACKNOWLEGETIMEOUT))
         return(GETFIRSTERROR);
   }

   return(NORMAL);

}

IM_EX_CTIBASE INT AlarmSumgetPrevTimeOut (ALARM_SUM_STRUCT *AlarmSumRecord, USHORT AlarmClass)
{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;
   TIMESTAMP KeyTime;

   /* get the record from the database */
   BufLen = sizeof (*AlarmSumRecord);
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_GETPREV,
               AlarmSum1PosBlk,     // from critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYUNACKNOWLEGETIMEOUT))
         return(GETPREVERROR);
   }
   else
   {
      if(BTRV (B_GETPREV,
               AlarmSum0PosBlk,     // from non-critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYUNACKNOWLEGETIMEOUT))
         return(GETPREVERROR);
   }

   return(NORMAL);

}

IM_EX_CTIBASE INT AlarmSumgetNextTimeOut (ALARM_SUM_STRUCT *AlarmSumRecord, USHORT AlarmClass)
{

   extern BYTE AlarmSum0PosBlk[];
   extern BYTE AlarmSum1PosBlk[];
   USHORT BufLen;
   TIMESTAMP KeyTime;

   /* get the record from the database */
   BufLen = sizeof (*AlarmSumRecord);
   if(AlarmClass == CRITICALCLASS)
   {
      if(BTRV (B_GETNEXT,
               AlarmSum1PosBlk,     // from critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYUNACKNOWLEGETIMEOUT))
         return(GETNEXTERROR);
   }
   else
   {
      if(BTRV (B_GETNEXT,
               AlarmSum0PosBlk,     // from non-critical summary
               AlarmSumRecord,
               &BufLen,
               &KeyTime,
               KEYUNACKNOWLEGETIMEOUT))
         return(GETNEXTERROR);
   }

   return(NORMAL);
}

/* Routine to open alarm log database on the primary machine */
IM_EX_CTIBASE INT FO_OpenAlarmLogDB (PCHAR PathName, PBYTE FOPosBlk)
{

   USHORT BufLen;
   CHAR FileName[80];
   ULONG i;
   ALARM_LOG_STRUCT AlarmLogRecord;

   /* make the file name */
   strcpy (FileName, PathName);
   strcat (FileName, ALARMLOGFILENAME);

   BufLen = sizeof (AlarmLogRecord);
   if((i = BTRV (B_OPEN,
                 FOPosBlk,
                 &AlarmLogRecord,
                 &BufLen,
                 FileName,
                 B_OPEN_NORMAL)) != NORMAL)
   {
      return(ALARMLOGOPEN);
   }

   return(NORMAL);

}

/* Routine to copy the whole Alarm log DB from the primary machine */
IM_EX_CTIBASE INT FO_CopyAlarmLogDB (PBYTE SrcPosBlk, PCHAR TempName)
{

   BYTE TempPosBlk[128];
   USHORT BufLen;
   USHORT status, rc;
   int i;
   TIMESTAMP KeyTimeStamp;
   ALARM_LOG_STRUCT AlarmLogRecord;
   ALARMLOG_E_BUFFER *Buffer;


   if((Buffer = (ALARMLOG_E_BUFFER*)malloc (sizeof(ALARMLOG_E_BUFFER))) == NULL)
   {
      /* Could not get any memory */
      return(!NORMAL);
   }

   BufLen = sizeof (AlarmLogRecord);
   if((status = BTRV (B_STEPFIRST,
                      SrcPosBlk,
                      &Buffer->Buf.RecordsBuf.AlarmLogs[0].AlarmRec,
                      &BufLen,
                      &KeyTimeStamp,
                      KEYTIMESTAMP)) != NORMAL)
   {
      free (Buffer);
      return(status);
   }

   /* open the target file in accelerated mode */
   BufLen = sizeof (AlarmLogRecord);
   if(BTRV (B_OPEN,
            TempPosBlk,
            &AlarmLogRecord,
            &BufLen,
            TempName,
            B_OPEN_ACCELERATED))
   {
      free (Buffer);
      return(COMERROROPEN);
   }


   /* let it know that we have 1 record */
   Buffer->Buf.RecordsBuf.NumRecords = 1;

   while(Buffer->Buf.RecordsBuf.NumRecords > 0)
   {

      /* now we add each record */
      for(i=0; i < Buffer->Buf.RecordsBuf.NumRecords; i++)
      {

         BufLen = sizeof (AlarmLogRecord);
         if((rc = BTRV (B_INSERT,
                        TempPosBlk,
                        &Buffer->Buf.RecordsBuf.AlarmLogs[i].AlarmRec,
                        &BufLen,
                        &KeyTimeStamp,
                        KEYTIMESTAMP)) != NORMAL)
         {
            BTRV (B_CLOSE, TempPosBlk, NULL, &BufLen, NULL, 0);
            free (Buffer);
            return(INSERTERROR);
         }
      }

      if(status == B_ERR_EOF)
         /* prev status was eof */
         break;

      /* fill in the step extended information */
      Buffer->Buf.Filter.DescrLen = sizeof(Buffer->Buf.Filter);
      Buffer->Buf.Filter.id[0] = 'E';
      Buffer->Buf.Filter.id[1] = 'G';
      Buffer->Buf.Filter.RejectCount = 1;      /* no filter so should be no rejects */
      Buffer->Buf.Filter.NumTerms = 0;
      Buffer->Buf.Filter.NumRecords = EXT_RECORD_COUNT_MAX;    /* get 100 records */
      Buffer->Buf.Filter.NumFields = 1;                        /* get 1 field the whole record */
      Buffer->Buf.Filter.Field1.FieldLen = sizeof(AlarmLogRecord);
      Buffer->Buf.Filter.Field1.FieldOffset = 0;

      BufLen = sizeof (*Buffer);
      status = BTRV (B_STEPNEXT_E,
                     SrcPosBlk,
                     Buffer,
                     &BufLen,
                     &KeyTimeStamp,
                     KEYTIMESTAMP);

      if((status != 0) && (status != B_ERR_EOF))
         break;
   }

   /* close the temporary target file */
   BTRV (B_CLOSE, TempPosBlk, NULL, &BufLen, NULL, 0);
   free (Buffer);
   return(NORMAL);
}

IM_EX_CTIBASE INT AlarmLogRecords (PULONG Records)
{
   extern BYTE AlarmLogPosBlk[];

   return(AlarmLogxRecords (Records, AlarmLogPosBlk));
}

IM_EX_CTIBASE INT AlarmLogxRecords (PULONG Records, PBYTE MyPosBlk)
{
   USHORT BufLen;
   BYTE KeyBuf[64];
   ALARMLOGSPECBUF AlarmLogSpecBuf;

   BufLen = sizeof (AlarmLogSpecBuf);
   if(BTRV (B_STAT,
            MyPosBlk,
            &AlarmLogSpecBuf,
            &BufLen,
            KeyBuf,
            0))
      return(TRUE);

   *Records = AlarmLogSpecBuf.RecordCount;

   return(NORMAL);
}


/* Routine to get new records from the Source Directory */
IM_EX_CTIBASE INT FO_CopyNewAlarmLogRecs (PBYTE FOPosBlk, PULONG RecordCount)

{

   extern BYTE AlarmLogPosBlk[];
   USHORT BufLen;
   USHORT status, rc;
   int i;
   TIMESTAMP KeyTimeStamp;
   ULONG SrcRecords, TargetRecords;
   ALARMLOG_E_BUFFER *Buffer;

   *RecordCount = 0L;

   /* check record count first */
   if(!(AlarmLogxRecords (&SrcRecords, FOPosBlk)))
   {
      /* only care if it is sucessful */
      if(AlarmLogRecords (&TargetRecords))
         /* if this call fails we will start over */
         return(REPLICATE_FILE);

      if(TargetRecords > SrcRecords)
      {
         /* this indicates that records we deleted
            so lets start from scratch */
         return(REPLICATE_FILE);
      }
      else if(SrcRecords == TargetRecords)
      {
         /* no records have been added */
         return(NORMAL);
      }
   }

   if((Buffer = (ALARMLOG_E_BUFFER*)malloc (sizeof(ALARMLOG_E_BUFFER))) == NULL)
   {
      /* Could not get any memory */
      return(!NORMAL);
   }


   if(TargetRecords == 0L)
   {
      /* no records in the target */
      BufLen = sizeof (ALARM_LOG_STRUCT);
      if(BTRV (B_GETFIRST,
               FOPosBlk,
               &Buffer->Buf.RecordsBuf.AlarmLogs[0].AlarmRec,
               &BufLen,
               &KeyTimeStamp,
               KEYTIMESTAMP))
      {
         free (Buffer);
         return(NORMAL);
      }

      /* Now add the record to the file */
      BufLen = sizeof (ALARM_LOG_STRUCT);
      if((rc = BTRV (B_INSERT,
                     AlarmLogPosBlk,
                     &Buffer->Buf.RecordsBuf.AlarmLogs[0].AlarmRec,
                     &BufLen,
                     &KeyTimeStamp,
                     KEYTIMESTAMP)) != NORMAL)
      {
         free (Buffer);
         return(NORMAL);
      }
      *RecordCount = 1;
   }


   do
   {

      /* fill in the step extended information */
      Buffer->Buf.Filter.DescrLen = sizeof(Buffer->Buf.Filter);
      Buffer->Buf.Filter.id[0] = 'E';
      Buffer->Buf.Filter.id[1] = 'G';
      Buffer->Buf.Filter.RejectCount = 1;      /* no filter so should be no rejects */
      Buffer->Buf.Filter.NumTerms = 0;
      Buffer->Buf.Filter.NumRecords = EXT_RECORD_COUNT_MAX;     /* get 200 records */
      Buffer->Buf.Filter.NumFields = 1;     /* get 1 field the whole record */
      Buffer->Buf.Filter.Field1.FieldLen = sizeof(ALARM_LOG_STRUCT);
      Buffer->Buf.Filter.Field1.FieldOffset = 0;

      BufLen = sizeof (*Buffer);
      status = BTRV (B_GETPREV_E,
                     FOPosBlk,
                     Buffer,
                     &BufLen,
                     &KeyTimeStamp,
                     KEYTIMESTAMP);

      if((status != 0) && (status != B_ERR_EOF))
         break;


      /* now we add each record */
      *RecordCount += Buffer->Buf.RecordsBuf.NumRecords;
      for(i=0; i < Buffer->Buf.RecordsBuf.NumRecords; i++)
      {

         BufLen = sizeof (ALARM_LOG_STRUCT);
         if((rc = BTRV (B_INSERT,
                        AlarmLogPosBlk,
                        &Buffer->Buf.RecordsBuf.AlarmLogs[i].AlarmRec,
                        &BufLen,
                        &KeyTimeStamp,
                        KEYTIMESTAMP)) != NORMAL)
         {
            free (Buffer);
            return(rc);
         }
      }

   } while(Buffer->Buf.RecordsBuf.NumRecords > 0 || status != B_ERR_EOF);

   free (Buffer);

   if((TargetRecords + *RecordCount) < SrcRecords)
   {
      /* we are out of sync, these should be equal */
      return(REPLICATE_FILE);
   }

   return(NORMAL);
}

/* Routine to open Comm Error log database on the primary machine */
IM_EX_CTIBASE INT FO_OpenComErrorDB (PCHAR PathName, PBYTE FOPosBlk)
{

   USHORT BufLen;
   CHAR FileName[80];
   ULONG i;
   COMM_ERROR_LOG_STRUCT ComErrorRecord;

   /* make the file name */
   strcpy (FileName, PathName);
   strcat (FileName, COMMLOGFILENAME);

   BufLen = sizeof (ComErrorRecord);
   if((i = BTRV (B_OPEN,
                 FOPosBlk,
                 &ComErrorRecord,
                 &BufLen,
                 FileName,
                 B_OPEN_NORMAL)) != NORMAL)
   {
      return(i);
   }

   return(NORMAL);

}

/* Routine to copy the whole Comm Error log DB from the primary machine */
IM_EX_CTIBASE INT FO_CopyComErrorDB (PBYTE SrcPosBlk, PCHAR TempName)
{

   BYTE TempPosBlk[128];
   USHORT BufLen;
   USHORT status;
   int i;
   TIMESTAMP KeyTimeStamp;
   COMM_ERROR_LOG_STRUCT ComErrorRecord;
   COMERR_E_BUFFER *Buffer;

   if((Buffer = (COMERR_E_BUFFER*)malloc (sizeof(COMERR_E_BUFFER))) == NULL)
   {
      /* Could not get any memory */
      return(!NORMAL);
   }

   BufLen = sizeof (ComErrorRecord);
   if((status = BTRV (B_STEPFIRST,
                      SrcPosBlk,
                      &Buffer->Buf.RecordsBuf.ComELogs[0].ComERec,
                      &BufLen,
                      &KeyTimeStamp,
                      KEYTIMESTAMP)) != NORMAL)
   {
      free (Buffer);
      return(status);
   }

   /* open the target file in accelerated mode */
   BufLen = sizeof (ComErrorRecord);
   if(BTRV (B_OPEN,
            TempPosBlk,
            &ComErrorRecord,
            &BufLen,
            TempName,
            B_OPEN_ACCELERATED))
   {
      free (Buffer);
      return(COMERROROPEN);
   }


   /* let it know that we have 1 record */
   Buffer->Buf.RecordsBuf.NumRecords = 1;
   while(Buffer->Buf.RecordsBuf.NumRecords > 0)
   {

      for(i=0; i < Buffer->Buf.RecordsBuf.NumRecords; i++)
      {

         BufLen = sizeof (ComErrorRecord);
         if(BTRV (B_INSERT,
                  TempPosBlk,
                  &Buffer->Buf.RecordsBuf.ComELogs[i].ComERec,
                  &BufLen,
                  &KeyTimeStamp,
                  KEYTIMESTAMP))
         {
            /* close the temporary target file */
            BTRV (B_CLOSE, TempPosBlk, NULL, &BufLen, NULL, 0);
            free (Buffer);
            return(INSERTERROR);
         }
      }

      if(status == B_ERR_EOF)
         /* prev status was eof */
         break;

      /* fill in the step extended information */
      Buffer->Buf.Filter.DescrLen = sizeof(Buffer->Buf.Filter);
      Buffer->Buf.Filter.id[0] = 'E';
      Buffer->Buf.Filter.id[1] = 'G';
      Buffer->Buf.Filter.RejectCount = 1;      /* no filter so should be no rejects */
      Buffer->Buf.Filter.NumTerms = 0;
      Buffer->Buf.Filter.NumRecords = EXT_RECORD_COUNT_MAX;        /* number of records to get */
      Buffer->Buf.Filter.NumFields = 1;                            /* get 1 field the whole record */
      Buffer->Buf.Filter.Field1.FieldLen = sizeof(ComErrorRecord);
      Buffer->Buf.Filter.Field1.FieldOffset = 0;
      BufLen = sizeof (*Buffer);
      status = BTRV (B_STEPNEXT_E,
                     SrcPosBlk,
                     Buffer,
                     &BufLen,
                     &KeyTimeStamp,
                     KEYTIMESTAMP);

      if((status != 0) && (status != B_ERR_EOF))
         break;
   }

   /* close the temporary target file */
   BTRV (B_CLOSE, TempPosBlk, NULL, &BufLen, NULL, 0);
   free (Buffer);
   return(NORMAL);
}

/* Routine to get New Comm Error records from the Source Directory */
IM_EX_CTIBASE INT FO_CopyNewComErrorRecs (PBYTE FOPosBlk, PULONG RecordCount)

{

   extern BYTE ComErrorPosBlk[];
   BYTE TmpPosBlk[128];
   USHORT BufLen;
   USHORT status, rc;
   int i;
   TIMESTAMP KeyTimeStamp;
   ULONG TargetRecords, SrcRecords, CurPos;
   COMERR_E_BUFFER *Buffer;


   // COMM_ERROR_LOG_STRUCT ComRec;
   // CHAR debug[120];

   *RecordCount = 0L;
   TargetRecords = 0L;


   /* check record count first */
   if(!(ComErrorLogxRecords (&SrcRecords, FOPosBlk)))
   {
      /* only care if it is sucessful */
      if(ComErrorLogRecords (&TargetRecords))
      {
//printf ("DEBUG:  BTRIEVE RECORD COUNT FAILED REPLICATE -- FO_COPYNEWCOM\n");
         /* if this call fails we will start over */
         return(REPLICATE_FILE);
      }

      if(TargetRecords > SrcRecords)
      {
         /* this indicates that records we deleted
            so lets start from scratch */
//printf ("DEBUG:  TARGET RECORD COUNT > SOURCE RECORD COUNT REPLICATE -- FO_COPYNEWCOM\n");

         return(REPLICATE_FILE);
      }
      else if(TargetRecords == SrcRecords)
      {
         /* no records have been added */
         return(NORMAL);
      }
   }

   if((Buffer = (COMERR_E_BUFFER*)malloc (sizeof(COMERR_E_BUFFER))) == NULL)
   {
      /* Could not get any memory */
      return(!NORMAL);
   }

   if(TargetRecords == 0L)
   {

//printf ("DEBUG:  TARGET WAS EMPTY, ADDING ONE RECORD -- FO_COPYNEWCOM\n");

      /* no records in the target */
      BufLen = sizeof (COMM_ERROR_LOG_STRUCT);
      if(BTRV (B_GETFIRST,
               FOPosBlk,
               &Buffer->Buf.RecordsBuf.ComELogs[0].ComERec,
               &BufLen,
               &KeyTimeStamp,
               KEYTIMESTAMP))
      {
         free (Buffer);
         return(NORMAL);
      }

      /* Now add the record to the file */
      BufLen = sizeof (COMM_ERROR_LOG_STRUCT);
      if(BTRV (B_INSERT,
               ComErrorPosBlk,
               &Buffer->Buf.RecordsBuf.ComELogs[0].ComERec,
               &BufLen,
               &KeyTimeStamp,
               KEYTIMESTAMP))
      {
         free (Buffer);
         return(NORMAL);
      }
      *RecordCount = 1;
   }

//printf ("DEBUG:  UPDATING TARGET WITH JUST NEW RECORDS -- FO_COPYNEWCOM\n");
   BufLen = 4;
   rc = BTRV (B_GETPOS,
              FOPosBlk,
              &CurPos,
              &BufLen,
              NULL,
              0);

//sprintf (debug, "POSITION BEFORE GET NEXT %ld, RETURN CODE %d -- FO_COPYNEWCOM\n",CurPos,rc);
//printf (debug);


   CloseAnyBtrvDB (TmpPosBlk);
   do
   {

      /* fill in the step extended information */
      Buffer->Buf.Filter.DescrLen = sizeof(Buffer->Buf.Filter);
      Buffer->Buf.Filter.id[0] = 'E';
      Buffer->Buf.Filter.id[1] = 'G';
      Buffer->Buf.Filter.RejectCount = 1;      /* no filter so should be no rejects */
      Buffer->Buf.Filter.NumTerms = 0;
      Buffer->Buf.Filter.NumRecords = EXT_RECORD_COUNT_MAX;     /* get 250 records */
      Buffer->Buf.Filter.NumFields = 1;     /* get 1 field the whole record */
      Buffer->Buf.Filter.Field1.FieldLen = sizeof(COMM_ERROR_LOG_STRUCT);
      Buffer->Buf.Filter.Field1.FieldOffset = 0;

      BufLen = sizeof (*Buffer);
      status = BTRV (B_GETNEXT_E,
                     FOPosBlk,
                     Buffer,
                     &BufLen,
                     &KeyTimeStamp,
                     KEYTIMESTAMP);
//sprintf (debug, "STATUS OF GETNEXT %d, TIMESTAMP is %ld\n",status,KeyTimeStamp.TimeStamp);
//printf (debug);

      if((status != 0) && (status != B_ERR_EOF))
      {

//printf ("DEBUG:  BTRIEVE GET NEXT EXTENDED FAILED WITH UNKNOWN ERROR-- FO_COPYNEWCOM\n");
         break;
      }


      /* now we add each record */
      *RecordCount += Buffer->Buf.RecordsBuf.NumRecords;
      for(i=0; i < Buffer->Buf.RecordsBuf.NumRecords; i++)
      {

         BufLen = sizeof (COMM_ERROR_LOG_STRUCT);
         if(BTRV (B_INSERT,
                  ComErrorPosBlk,
                  &Buffer->Buf.RecordsBuf.ComELogs[i].ComERec,
                  &BufLen,
                  &KeyTimeStamp,
                  KEYTIMESTAMP))
         {
            free (Buffer);
//printf ("DEBUG:  INSERTING NEW RECORD FAILED, RETURNING -- FO_COPYNEWCOM\n");
            return(INSERTERROR);
         }
      }

   } while(Buffer->Buf.RecordsBuf.NumRecords > 0 || status != B_ERR_EOF);
   free (Buffer);

   if((TargetRecords + *RecordCount) < SrcRecords)
   {
      /* we are out of sync, these should be equal */
      return(REPLICATE_FILE);
   }

   return(NORMAL);
}

/* this is used to specify a path name for the comm error file,
   but it uses the Global Comm Err Position Block so all other comm
   error routines work with the file that was opened */

IM_EX_CTIBASE INT FO_InitComErrorLog (PCHAR PathName)
{
   extern BYTE ComErrorPosBlk[];
   PSZ ComMaxName;
   CHAR FileName[128];
   int rc;

   /* init the max number of records for com error logs  this limits the file size */
   if(!CTIScanEnv ("COMLOGRECORDMAX",
                   &ComMaxName))
   {
      ComRecordMax = atol(ComMaxName);
      if(ComRecordMax < 1)
      {
         ComRecordMax = 50000L;
      }
   }
   else
   {
      ComRecordMax = 50000L;    // default to number of records
   }

   if((rc = FO_OpenComErrorDB (PathName, ComErrorPosBlk)) != NORMAL)
   {
      /* open failed lets see if we need to create it */
      if(rc != B_ERR_FNF)
      {
         /* file was found so return an error */
         return(COMERROROPEN);
      }

      /* File does not exist so go ahead and create it */
      if(PathName[0] == '\0')
      {
         if(CreateComErrorLog (COMMLOGFILENAME))
         {
            return(COMERRORCREATE);
         }
      }
      else
      {
         /* add the path to the name */
         strcpy (FileName, PathName);
         strcat (FileName, COMMLOGFILENAME);
         if(CreateComErrorLog (FileName))
         {
            return(COMERRORCREATE);
         }
      }

      /* now we can try to open it */
      if(FO_OpenComErrorDB (PathName, ComErrorPosBlk))
      {
         /* open still failed */
         return(COMERROROPEN);
      }
   }

   return(NORMAL);
}

IM_EX_CTIBASE INT FO_InitAlarmLog (PCHAR PathName)
{
   extern BYTE AlarmLogPosBlk[];
   CHAR FileName[128];
   int rc;

   if((rc = FO_OpenAlarmLogDB (PathName, AlarmLogPosBlk)) != NORMAL)
   {
      /* open failed lets see if we need to create it */
      if(rc != B_ERR_FNF)
      {
         /* file was found so return an error */
         return(ALARMLOGOPEN);
      }

      /* File does not exist so go ahead and create it */
      if(PathName[0] == '\0')
      {
         if(CreateAlarmLogDB (ALARMLOGFILENAME))
         {
            return(ALARMLOGCREATE);
         }
      }
      else
      {
         /* add the path to the name */
         strcpy (FileName, PathName);
         strcat (FileName, ALARMLOGFILENAME);
         if(CreateAlarmLogDB (FileName))
         {
            return(ALARMLOGCREATE);
         }
      }

      /* now we can try to open it */
      if(FO_OpenAlarmLogDB (PathName, AlarmLogPosBlk))
      {
         /* open still failed */
         return(ALARMLOGOPEN);
      }
   }

   return(NORMAL);
}

/* com error cleanup when time stamps are uninitialized */
IM_EX_CTIBASE INT ComErrorCleanUp(COMM_ERROR_LOG_STRUCT *ComErrorRecord, PBYTE MyPosBlk)
{

   USHORT BufLen;

   /* delete bad records */
   for(;;)
   {

      /* detete the record */
      BufLen = sizeof (*ComErrorRecord);
      if(BTRV (B_DELETE,
               MyPosBlk,
               NULL,
               &BufLen,
               NULL,
               0))
      {
         return(DELETEERROR);
      }

      if(ComErrorLogxgetLastTime (ComErrorRecord, MyPosBlk) ||
         ComErrorRecord->TimeStamp < LongTime())
      {
         /* we found a good time or call failed */
         break;
      }
   }

   return(NORMAL);
}

/* see this this database file has changed */
IM_EX_CTIBASE INT Check4AlarmDBChange (ULONG LastTime, PCHAR DestPathName)
{
   ULONG SrcLastTime;
   CHAR FileName[128];

   if(LastTime == 0L)
   {
      /* we need to check if the target exists and get its time stamp */
      ADDPATHTOFILENAME(DestPathName, FileName);
      strcat (FileName, ALARMLOGFILENAME);
      if(BTRFileCheckTime (FileName, &LastTime))
      {
         return(!NORMAL);
      }
   }

   /* get file information from PRIMARY file for time/date stamps */
   if(!(BtrvHistgetTime (ALARMLOGFILENAME, &SrcLastTime)))
   {
      if(SrcLastTime > LastTime)
      {
         /* File has changed */
         return(!NORMAL);
      }
   }

   return(NORMAL);
}

/* see this this database file has changed */
IM_EX_CTIBASE INT Check4AlarmCSumDBChange (ULONG LastTime, PCHAR DestPathName)
{
   ULONG SrcLastTime;
   CHAR FileName[128];

   if(LastTime == 0L)
   {
      /* we need to check if the target exists and get its time stamp */
      ADDPATHTOFILENAME(DestPathName, FileName);
      strcat (FileName, ALARMSUM1FILENAME);
      if(BTRFileCheckTime (FileName, &LastTime))
      {
         return(!NORMAL);
      }
   }

   /* get file information from PRIMARY file for time/date stamps */
   if(!(BtrvHistgetTime (ALARMSUM1FILENAME, &SrcLastTime)))
   {
      if(SrcLastTime > LastTime)
      {
         /* File has changed */
         return(!NORMAL);
      }
   }

   return(NORMAL);
}

/* see this this database file has changed */
IM_EX_CTIBASE INT Check4AlarmNCSumDBChange (ULONG LastTime, PCHAR DestPathName)
{
   ULONG SrcLastTime;
   CHAR FileName[128];

   if(LastTime == 0L)
   {
      /* we need to check if the target exists and get its time stamp */
      ADDPATHTOFILENAME(DestPathName, FileName);
      strcat (FileName, ALARMSUM0FILENAME);
      if(BTRFileCheckTime (FileName, &LastTime))
      {
         return(!NORMAL);
      }
   }

   /* get file information from PRIMARY file for time/date stamps */
   if(!(BtrvHistgetTime (ALARMSUM0FILENAME, &SrcLastTime)))
   {
      if(SrcLastTime > LastTime)
      {
         /* File has changed */
         return(!NORMAL);
      }
   }

   return(NORMAL);
}

/* see this this database file has changed */
IM_EX_CTIBASE INT Check4ComErrDBChange (ULONG LastTime, PCHAR DestPathName)
{
   ULONG SrcLastTime;
   CHAR FileName[128];

   if(LastTime == 0L)
   {
      /* we need to check if the target exists and get its time stamp */
      ADDPATHTOFILENAME(DestPathName, FileName);
      strcat (FileName, COMMLOGFILENAME);
      if(BTRFileCheckTime (FileName, &LastTime))
      {
         return(!NORMAL);
      }
   }

   /* get file information from PRIMARY file for time/date stamps */
   if(!(BtrvHistgetTime (COMMLOGFILENAME, &SrcLastTime)))
   {
      if(SrcLastTime > LastTime)
      {
         /* File has changed */
         return(!NORMAL);
      }
   }

   return(NORMAL);
}

/* Routine to copy the critical alarm summary DB from the primary machine */
IM_EX_CTIBASE INT FO_CopyAlarmCSumDB (PCHAR SrcPathName,
                                   PCHAR DestPathName)
{

   BYTE SrcPosBlk[128];
   BYTE TargetPosBlk[128];
   CHAR TempFileName[128];
   CHAR RealFileName[128];
   ALARM_SUM_STRUCT AlarmSumRecord;
   USHORT BufLen;
   USHORT status, rc;
   int i;
   ALARMDEVICEPOINTNAME KeyDevicePoint;


   if((i = OpenAlarmCSumDB(SrcPathName,
                           SrcPosBlk)) != NORMAL)
   {
      /* could not open the source file */
      return(i);
   }

   BufLen = sizeof (AlarmSumRecord);
   if((status = BTRV (B_STEPFIRST,
                      SrcPosBlk,
                      &AlarmSumRecord,
                      &BufLen,
                      &KeyDevicePoint,
                      0)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      return(status);
   }

   /* open the target file in accelerated mode */
   if(DestPathName[0] == '\0')
   {
      strcpy (TempFileName, TEMP2_FILE_NAME);
   }
   else
   {
      strcpy (TempFileName, DestPathName);
      strcat (TempFileName, TEMP2_FILE_NAME);
   }

   BufLen = sizeof (AlarmSumRecord);
   CTIDelete (TempFileName);
   CreateAlarmSum (TempFileName);
   if((rc = BTRV (B_OPEN,
                  TargetPosBlk,
                  &AlarmSumRecord,
                  &BufLen,
                  TempFileName,
                  B_OPEN_ACCELERATED)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      return(rc);
   }


   /* Loop through and get all the Groups */

   while(status == NORMAL)
   {

      BufLen = sizeof (AlarmSumRecord);
      if((rc = BTRV (B_INSERT,
                     TargetPosBlk,
                     &AlarmSumRecord,
                     &BufLen,
                     &KeyDevicePoint,
                     0)) != NORMAL)
      {
         /* Had an error lets quit */
         status = INSERTERROR;
         break;
      }

      BufLen = sizeof (AlarmSumRecord);
      status = BTRV (B_STEPNEXT,
                     SrcPosBlk,
                     &AlarmSumRecord,
                     &BufLen,
                     &KeyDevicePoint,
                     0);

   }

   BTRV (B_CLOSE, SrcPosBlk, NULL, &BufLen, NULL, 0);
   BTRV (B_CLOSE, TargetPosBlk, NULL, &BufLen, NULL, 0);
   if(status == INSERTERROR)
      return(status);

   /* rename the file to the real one */
   if(DestPathName[0] == '\0')
   {
      strcpy (RealFileName, ALARMSUM1FILENAME);
   }
   else
   {
      strcpy (RealFileName, DestPathName);
      strcat (RealFileName, ALARMSUM1FILENAME);
   }

   CTIDelete (RealFileName);
   CTIMove (TempFileName, RealFileName);
   return(NORMAL);
}

/* Routine to copy the non critical alarm summary DB from the primary machine */
IM_EX_CTIBASE INT FO_CopyAlarmNCSumDB (PCHAR SrcPathName,
                                    PCHAR DestPathName)
{

   BYTE SrcPosBlk[128];
   BYTE TargetPosBlk[128];
   CHAR TempFileName[128];
   CHAR RealFileName[128];
   ALARM_SUM_STRUCT AlarmSumRecord;
   USHORT BufLen;
   USHORT status, rc;
   int i;
   ALARMDEVICEPOINTNAME KeyDevicePoint;


   if((i = OpenAlarmNCSumDB(SrcPathName,
                            SrcPosBlk)) != NORMAL)
   {
      /* could not open the source file */
      return(i);
   }

   BufLen = sizeof (AlarmSumRecord);
   if((status = BTRV (B_STEPFIRST,
                      SrcPosBlk,
                      &AlarmSumRecord,
                      &BufLen,
                      &KeyDevicePoint,
                      0)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      return(status);
   }

   /* open the target file in accelerated mode */
   if(DestPathName[0] == '\0')
   {
      strcpy (TempFileName, TEMP2_FILE_NAME);
   }
   else
   {
      strcpy (TempFileName, DestPathName);
      strcat (TempFileName, TEMP2_FILE_NAME);
   }

   BufLen = sizeof (AlarmSumRecord);
   CTIDelete (TempFileName);
   CreateAlarmSum (TempFileName);
   if((rc = BTRV (B_OPEN,
                  TargetPosBlk,
                  &AlarmSumRecord,
                  &BufLen,
                  TempFileName,
                  B_OPEN_ACCELERATED)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      return(rc);
   }


   /* Loop through and get all the Groups */

   while(status == NORMAL)
   {

      BufLen = sizeof (AlarmSumRecord);
      if((rc = BTRV (B_INSERT,
                     TargetPosBlk,
                     &AlarmSumRecord,
                     &BufLen,
                     &KeyDevicePoint,
                     0)) != NORMAL)
      {
         /* Had an error lets quit */
         status = INSERTERROR;
         break;
      }

      BufLen = sizeof (AlarmSumRecord);
      status = BTRV (B_STEPNEXT,
                     SrcPosBlk,
                     &AlarmSumRecord,
                     &BufLen,
                     &KeyDevicePoint,
                     0);

   }

   BTRV (B_CLOSE, SrcPosBlk, NULL, &BufLen, NULL, 0);
   BTRV (B_CLOSE, TargetPosBlk, NULL, &BufLen, NULL, 0);
   if(status == INSERTERROR)
      return(status);

   /* rename the file to the real one */
   if(DestPathName[0] == '\0')
   {
      strcpy (RealFileName, ALARMSUM0FILENAME);
   }
   else
   {
      strcpy (RealFileName, DestPathName);
      strcat (RealFileName, ALARMSUM0FILENAME);
   }

   CTIDelete (RealFileName);
   CTIMove (TempFileName, RealFileName);
   return(NORMAL);
}



