
#pragma title ( "Scan, Status and State Name Database Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1993" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        Ben Wallace

    FileName:
        scupdate.c

    Purpose:
        Routines to maintain the Scan Table, Status Table and
        State Name Table database


    The following procedures are contained in this module:


    Initial Date:
        Unknown

    Revision History:
        Unknown prior to 8-93
        09-06-93 Converted to 32 bit                             WRO
        09-27-94 Added routines for Failover                     BDW
        12-4-97 Added routines for BDBCOPY                      DLS


   -------------------------------------------------------------------- */
#include <windows.h>       // These next few are required for Win32
#include "os2_2w32.h"
#include "cticalls.h"
// #include <btrapi.h>


#include <stdlib.h>
// #include "btrieve.h"
#include <stdio.h>
#include <string.h>
#include <malloc.h>

#include "dsm2.h"
#include "dsm2err.h"
#include "device.h"
#include "lm_auto.h"


POSBLK      *ScanPointPosBlk = NULL;
BYTE        StateNamePosBlk[128];
POSBLK      *StatPointPosBlk = NULL;


/* Routine to intialize scan point database */
INT InitScanPointDB (VOID )
{
   USHORT BufLen;
   ULONG i;
   BYTE KeyBuf[64];
   SCANPOINTSPECBUF ScanPointSpecBuf;

   if(ScanPointPosBlk == NULL)
   {
      if((i = OpenScanPtDB (USE_NO_PATH_NAME,
                            PositionBlock (&ScanPointPosBlk, InitScanPointDB))) != NORMAL)
      {
         if(i != B_ERR_FNF)
         {
            return(SCANPOINTOPEN);
         }

         /* File does not exist so go ahead and create it */
         if((i = CreateScanPointDB (SCANPOINTFILENAME)) != NORMAL)
         {
            return(i);
         }

         if(OpenScanPtDB (USE_NO_PATH_NAME, PositionBlock (&ScanPointPosBlk, InitScanPointDB)))
         {
            return(SCANPOINTOPEN);
         }
      }
      else
      {
         /* Check that the record size is still what it should be
            Note that this does NOT warrenty against changes in indexing
            or ordering of the record */
         BufLen = sizeof (ScanPointSpecBuf);
         if(BTRV (B_STAT,
                  PositionBlock (&ScanPointPosBlk, InitScanPointDB),
                  &ScanPointSpecBuf,
                  &BufLen,
                  KeyBuf,
                  0) || ScanPointSpecBuf.RecordLength != sizeof (SCANPOINT))
         {
            printf("SCUP\n");
            return(RECORDCHANGED);
         }
      }
   }
   else
   {
      return(OpenScanPtDB (USE_NO_PATH_NAME, PositionBlock (&ScanPointPosBlk, InitScanPointDB)));
   }

   return(NORMAL);
}

IM_EX_CTIBASE INT CreateScanPointDB (PCHAR FileName)
{
   BYTE TempPosBlk[128];
   SCANPOINT ScanPointRecord;
   USHORT BufLen;
   SCANPOINTSPECBUF ScanPointSpecBuf;

   /* File does not exist so go ahead and create it */
   memset (&ScanPointSpecBuf, 0, sizeof (ScanPointSpecBuf));
   ScanPointSpecBuf.RecordLength = sizeof (ScanPointRecord);
   ScanPointSpecBuf.PageSize = 512;
   ScanPointSpecBuf.FileFlags = B_PREALLOC;
   ScanPointSpecBuf.IndexCount = 3;
   ScanPointSpecBuf.PreAlloc = 1;
   ScanPointSpecBuf.KeySpecBuf[0].KeyPos = (USHORT) ((ULONG) &ScanPointRecord.DeviceName - (ULONG) &ScanPointRecord) + 1;
   ScanPointSpecBuf.KeySpecBuf[0].KeyLen = sizeof (ScanPointRecord.DeviceName);
   ScanPointSpecBuf.KeySpecBuf[0].KeyFlag = B_MOD | B_EXT | B_SEG;
   ScanPointSpecBuf.KeySpecBuf[0].KeyType = B_STRING;
   ScanPointSpecBuf.KeySpecBuf[1].KeyPos = (USHORT) ((ULONG) &ScanPointRecord.PointName - (ULONG) &ScanPointRecord) + 1;
   ScanPointSpecBuf.KeySpecBuf[1].KeyLen = sizeof (ScanPointRecord.PointName);
   ScanPointSpecBuf.KeySpecBuf[1].KeyFlag = B_MOD | B_EXT;
   ScanPointSpecBuf.KeySpecBuf[1].KeyType = B_STRING;
   ScanPointSpecBuf.KeySpecBuf[2].KeyPos = (USHORT) ((ULONG) &ScanPointRecord.NextScan - (ULONG) &ScanPointRecord) + 1;
   ScanPointSpecBuf.KeySpecBuf[2].KeyLen = sizeof (ScanPointRecord.NextScan);
   ScanPointSpecBuf.KeySpecBuf[2].KeyFlag = B_MOD | B_EXT | B_DUP;
   ScanPointSpecBuf.KeySpecBuf[2].KeyType = B_UNSIGNED;
   ScanPointSpecBuf.KeySpecBuf[3].KeyPos = (USHORT) ((ULONG) &ScanPointRecord.ReportOrder - (ULONG) &ScanPointRecord) + 1;
   ScanPointSpecBuf.KeySpecBuf[3].KeyLen = sizeof (ScanPointRecord.ReportOrder);
   ScanPointSpecBuf.KeySpecBuf[3].KeyFlag = B_MOD | B_EXT | B_DUP | B_SEG;
   ScanPointSpecBuf.KeySpecBuf[3].KeyType = B_UNSIGNED;
   ScanPointSpecBuf.KeySpecBuf[4].KeyPos = (USHORT) ((ULONG) &ScanPointRecord.DeviceName - (ULONG) &ScanPointRecord) + 1;
   ScanPointSpecBuf.KeySpecBuf[4].KeyLen = sizeof (ScanPointRecord.DeviceName);
   ScanPointSpecBuf.KeySpecBuf[4].KeyFlag = B_MOD | B_EXT | B_DUP | B_SEG;
   ScanPointSpecBuf.KeySpecBuf[4].KeyType = B_STRING;
   ScanPointSpecBuf.KeySpecBuf[5].KeyPos = (USHORT) ((ULONG) &ScanPointRecord.PointName - (ULONG) &ScanPointRecord) + 1;
   ScanPointSpecBuf.KeySpecBuf[5].KeyLen = sizeof (ScanPointRecord.PointName);
   ScanPointSpecBuf.KeySpecBuf[5].KeyFlag = B_MOD | B_EXT | B_DUP;
   ScanPointSpecBuf.KeySpecBuf[5].KeyType = B_STRING;

   BufLen = sizeof (ScanPointSpecBuf);
   if(BTRV (B_CREATE,
            TempPosBlk,
            &ScanPointSpecBuf,
            &BufLen,
            FileName,
            0))
   {
      return(SCANPOINTCREATE);
   }

   return(NORMAL);
}

/* Routine to open Scan Point database */
IM_EX_CTIBASE INT OpenScanPtDB(PCHAR PathName, PBYTE MyPosBlk)
{
   SCANPOINT ScanRecord;
   USHORT BufLen;
   CHAR FileName[128];

   /* Make the file name first */
   if(PathName[0] == '\0')
   {
      strcpy (FileName, SCANPOINTFILENAME);
   }
   else
   {
      strcpy (FileName, PathName);
      strcat (FileName, SCANPOINTFILENAME);
   }

   BufLen = sizeof (ScanRecord);
   return(BTRV (B_OPEN,
                MyPosBlk,
                &ScanRecord,
                &BufLen,
                FileName,
                B_OPEN_NORMAL));
}

/* Routine to close ScanPoint  database */
INT CloseScanPointDB ()
{
   USHORT BufLen;

   BTRV (B_CLOSE,
         PositionBlock (&ScanPointPosBlk, InitScanPointDB),
         NULL,
         &BufLen,
         NULL,
         0);

   return(NORMAL);

}


/* Routine to add a point to scan point table */
IM_EX_CTIBASE INT ScanPointAdd (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINT Dummy;
   SCANPOINTNAME KeyScanPointName;

   /* Check and see if this ScanPoint exists */
   memcpy (Dummy.DeviceName, ScanPointRecord->DeviceName, sizeof (ScanPointRecord->DeviceName));
   memcpy (Dummy.PointName, ScanPointRecord->PointName, sizeof (ScanPointRecord->PointName));
   if(!ScanPointgetEqual (&Dummy))
      return(SCANPOINTEXISTS);

   ScanPointRecord->NextScan = 0L;     // this flags LC that this is new

   /* Everything is ok so go ahead and add ScanPoint */
   BufLen = sizeof (*ScanPointRecord);
   if(BTRV (B_INSERT,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            ScanPointRecord,
            &BufLen,
            &KeyScanPointName,
            KEYSCANPOINTNAME))
   {
      return(INSERTERROR);
   }

   /* Now that we have added record do a get equal to repositon */
   BtrvHistUpdate (SCANPOINTFILENAME);
   return(ScanPointgetEqual (ScanPointRecord));
}

/* update routine for the static part of the database */
IM_EX_CTIBASE INT ScanPointUpdate (SCANPOINT *ScanPointRecord)
{
   SCANPOINT       Dummy;
   SCANPOINTNAME   KeyScanPointName;

   USHORT  BufLen;
   int     rc;

   /* Check and see if this ScanPoint exists */
   memcpy (Dummy.DeviceName, ScanPointRecord->DeviceName, sizeof (ScanPointRecord->DeviceName));
   memcpy (Dummy.PointName, ScanPointRecord->PointName, sizeof (ScanPointRecord->PointName));

   /* lock the record so we can update static data fields */
   rc = ScanPointLock (&Dummy);

   if(rc == RECORD_IN_USE)
   {
      /* Try to get a lock one more time */
      rc = ScanPointLock (&Dummy);
   }

   /* Record was found and locked */
   if(rc == NORMAL)
   {
      /* ONLY Change the static part of the db */
      Dummy.ReportOrder = ScanPointRecord->ReportOrder;
      Dummy.StatusFlag = ScanPointRecord->StatusFlag;

      /* Everything is ok so go ahead and update ScanPoint */
      BufLen = sizeof (*ScanPointRecord);
      if(BTRV (B_UPDATE,
               PositionBlock (&ScanPointPosBlk, InitScanPointDB),
               &Dummy,
               &BufLen,
               &KeyScanPointName,
               KEYSCANPOINTNAME))
      {
         rc = UPDATEERROR;
      }

      /* Now that we have updated record do a get equal to reposition */
      BtrvHistUpdate (SCANPOINTFILENAME);
      ScanPointgetEqual (ScanPointRecord);
   }

   return(rc);
}

/* fast update to change the dynamic part of the record */
IM_EX_CTIBASE INT ScanPointFastUpdate (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINTNAME KeyScanPointName;

   BufLen = sizeof (*ScanPointRecord);
   if(BTRV (B_UPDATE,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            ScanPointRecord,
            &BufLen,
            &KeyScanPointName,
            KEYSCANPOINTNAME))
   {
      return(UPDATEERROR);
   }

   /* Now that we have update record do a get equal to reposition */
   BtrvHistUpdate (SCANPOINTFILENAME);
   return(ScanPointgetEqual (ScanPointRecord));
}

/* routine to delete a record from scan point database */
IM_EX_CTIBASE INT ScanPointDelete (SCANPOINT *ScanPointRecord)
{
   SCANPOINT Dummy;
   USHORT BufLen;
   int rc;

   /* check and see if this ScanPoint  exists */
   memcpy (Dummy.DeviceName, ScanPointRecord->DeviceName, sizeof (ScanPointRecord->DeviceName));
   memcpy (Dummy.PointName, ScanPointRecord->PointName, sizeof (ScanPointRecord->PointName));

   /* lock the record so we can update static data fields */
   rc = ScanPointLock (&Dummy);

   if(rc == RECORD_IN_USE)
   {
      /* Try to get a lock one more time */
      rc = ScanPointLock (&Dummy);
   }

   /* Record was found and locked */
   if(rc == NORMAL)
   {
      /* If we made it to this  ScanPoint  exists and is unused */
      if(BTRV (B_DELETE,
               PositionBlock (&ScanPointPosBlk, InitScanPointDB),
               NULL,
               &BufLen,
               NULL,
               0))
      {
         rc = DELETEERROR;
      }
   }

   BtrvHistUpdate (SCANPOINTFILENAME);
   return(rc);
}


/* Routine to get equal from scan point database */
IM_EX_CTIBASE INT ScanPointgetEqual (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINTNAME KeyScanPointName;
   SCANPOINTNAME Dummy;

   /* get the record from the database */
   memcpy (KeyScanPointName.DeviceName, ScanPointRecord->DeviceName, sizeof (ScanPointRecord->DeviceName));
   memcpy (KeyScanPointName.PointName, ScanPointRecord->PointName, sizeof (ScanPointRecord->PointName));
   BufLen = sizeof (*ScanPointRecord);
   if(ScanPointRecord->PointName[0] != ' ')
   {
      if(BTRV (B_GETEQ,
               PositionBlock (&ScanPointPosBlk, InitScanPointDB),
               ScanPointRecord,
               &BufLen,
               &KeyScanPointName,
               KEYSCANPOINTNAME))
      {
         return(SCANPOINTUNKNOWN);
      }
   }
   else
   {
      /* get the first point on this device */
      memcpy (Dummy.DeviceName, ScanPointRecord->DeviceName, sizeof (ScanPointRecord->DeviceName));
      if(BTRV (B_GETGE,
               PositionBlock (&ScanPointPosBlk, InitScanPointDB),
               ScanPointRecord,
               &BufLen,
               &KeyScanPointName,
               KEYSCANPOINTNAME) || strnicmp(Dummy.DeviceName, KeyScanPointName.DeviceName, sizeof (KeyScanPointName.DeviceName)))
      {
         return(SCANPOINTUNKNOWN);
      }
   }

   return(NORMAL);
}


/* Routine to get and lock a record from Scan Point database */
IM_EX_CTIBASE INT ScanPointLock (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINTNAME KeyScanPointName;
   int rc;

   /* get the record from the database */
   BufLen = sizeof (*ScanPointRecord);
   memcpy (KeyScanPointName.DeviceName, ScanPointRecord->DeviceName, sizeof (ScanPointRecord->DeviceName));
   memcpy (KeyScanPointName.PointName, ScanPointRecord->PointName, sizeof (ScanPointRecord->PointName));

   /* try to lock the record */
   rc = BTRV (B_GETEQ + B_SINGLEWAITLOCK,
              PositionBlock (&ScanPointPosBlk, InitScanPointDB),
              ScanPointRecord,
              &BufLen,
              &KeyScanPointName,
              KEYSCANPOINTNAME);

   if(rc == B_KEY_VALUE_NOT_FOUND)
   {
      /* record did not exist in database */
      rc = SCANPOINTUNKNOWN;
   }
   else if(rc == B_RECORD_INUSE || rc == B_FILE_INUSE)
   {
      rc = RECORD_IN_USE;
   }
   else if(rc != B_NO_ERROR)
   {
      /* return the raw error with offset */
      rc += BTR_RAW_ERR_OFFSET;
   }
   else
   {
      /* no error */
      rc = NORMAL;
   }

   return(rc);
}


/* Routine to implicitly unlock a record in Device database */
IM_EX_CTIBASE INT ScanPointUnLock (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;

   BufLen = sizeof (*ScanPointRecord);
   if(BTRV (B_UNLOCK,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            ScanPointRecord,
            &BufLen,
            NULL,
            KEYSCANPOINTNAME))
   {
      return(UNLOCKERROR);
   }

   return(NORMAL);
}


/* Routine to get first record from scan point database */
IM_EX_CTIBASE INT ScanPointGetFirst (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINTNAME KeyScanPointName;

   /* get the record from the database */
   BufLen = sizeof (*ScanPointRecord);
   if(BTRV (B_GETFIRST,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            ScanPointRecord,
            &BufLen,
            &KeyScanPointName,
            KEYSCANPOINTNAME))
   {
      return(GETFIRSTERROR);
   }

   return(NORMAL);
}


/* Routine to get next record from scan point database */
IM_EX_CTIBASE INT ScanPointgetNext (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINTNAME KeyScanPointName;

   /* get the record from the database */
   BufLen = sizeof (*ScanPointRecord);
   if(BTRV (B_GETNEXT,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            ScanPointRecord,
            &BufLen,
            &KeyScanPointName,
            KEYSCANPOINTNAME))
   {
      return(GETNEXTERROR);
   }

   return(NORMAL);
}


/* this get the next greater record */
IM_EX_CTIBASE INT ScanPointgetNextGt (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINTNAME KeyScanPointName;

   /* get the record from the database */
   memcpy (KeyScanPointName.DeviceName, ScanPointRecord->DeviceName, sizeof (ScanPointRecord->DeviceName));
   memcpy (KeyScanPointName.PointName, ScanPointRecord->PointName, sizeof (ScanPointRecord->PointName));
   BufLen = sizeof (*ScanPointRecord);
   if(BTRV (B_GETGT,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            ScanPointRecord,
            &BufLen,
            &KeyScanPointName,
            KEYSCANPOINTNAME))
   {
      return(GETNEXTERROR);
   }

   return(NORMAL);
}


/* Routine to get previous record from scan point database */
IM_EX_CTIBASE INT ScanPointgetPrev (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINTNAME KeyScanPointName;

   /* get the record from the database */
   BufLen = sizeof (*ScanPointRecord);
   if(BTRV (B_GETPREV,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            ScanPointRecord,
            &BufLen,
            &KeyScanPointName,
            KEYSCANPOINTNAME))
   {
      return(GETPREVERROR);
   }

   return(NORMAL);
}


/* Routine to get number of records in load control ScanPoint database */
IM_EX_CTIBASE INT ScanPointRecords (PULONG Records)
{
   USHORT BufLen;
   BYTE KeyBuf[64];
   SCANPOINTSPECBUF ScanPointSpecBuf;

   /* get the file information */
   BufLen = sizeof (ScanPointSpecBuf);
   if(BTRV (B_STAT,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            &ScanPointSpecBuf,
            &BufLen,
            KeyBuf,
            0))
      return(ERRUNKNOWN);

   /* ok so return # records */
   *Records = ScanPointSpecBuf.Records;

   return(NORMAL);
}


/* get first record to be scanned */
IM_EX_CTIBASE INT ScanPointGetFirstScan (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINTNEXT KeyScanPointTime;

   /* get the record from the database */
   BufLen = sizeof (*ScanPointRecord);
   if(BTRV (B_GETFIRST,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            ScanPointRecord,
            &BufLen,
            &KeyScanPointTime,
            KEYSCANPOINTNEXTSCAN))
   {
      return(GETFIRSTERROR);
   }

   return(NORMAL);
}

/* get LE the present scan */
IM_EX_CTIBASE INT ScanPointgetScanLE (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINTNEXT KeyScanPointTime;

   /* get the record from the database */
   KeyScanPointTime.NextScan = ScanPointRecord->NextScan;
   BufLen = sizeof (*ScanPointRecord);
   if(BTRV (B_GETLE,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            ScanPointRecord,
            &BufLen,
            &KeyScanPointTime,
            KEYSCANPOINTNEXTSCAN))
   {
      return(GETPREVERROR);
   }

   return(NORMAL);
}



IM_EX_CTIBASE INT ScanPtOrdergetEqual (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINTNAMEREPORT KeyOrderScanPointName;

   /* get the record from the database */
   KeyOrderScanPointName.ReportOrder = ScanPointRecord->ReportOrder;
   memcpy (KeyOrderScanPointName.DeviceName, ScanPointRecord->DeviceName, sizeof (ScanPointRecord->DeviceName));
   memcpy (KeyOrderScanPointName.PointName, ScanPointRecord->PointName, sizeof (ScanPointRecord->PointName));
   BufLen = sizeof (*ScanPointRecord);
   if(BTRV (B_GETEQ,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            ScanPointRecord,
            &BufLen,
            &KeyOrderScanPointName,
            KEYSCANPOINTREPORT))
   {
      return(SCANPOINTUNKNOWN);
   }

   return(NORMAL);
}



IM_EX_CTIBASE INT ScanPtOrderGetFirst (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINTNAMEREPORT KeyOrderScanPointName;

   /* get the record from the database */
   BufLen = sizeof (*ScanPointRecord);
   if(BTRV (B_GETFIRST,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            ScanPointRecord,
            &BufLen,
            &KeyOrderScanPointName,
            KEYSCANPOINTREPORT))
   {
      return(GETFIRSTERROR);
   }

   return(NORMAL);
}



IM_EX_CTIBASE INT ScanPtOrdergetNext (SCANPOINT *ScanPointRecord)
{
   USHORT BufLen;
   SCANPOINTNAMEREPORT KeyOrderScanPointName;

   /* get the record from the database */
   BufLen = sizeof (*ScanPointRecord);
   if(BTRV (B_GETNEXT,
            PositionBlock (&ScanPointPosBlk, InitScanPointDB),
            ScanPointRecord,
            &BufLen,
            &KeyOrderScanPointName,
            KEYSCANPOINTREPORT))
   {
      return(GETNEXTERROR);
   }

   return(NORMAL);
}


/* Routine to intialize state name table database */
INT InitStateNameDB (VOID)
{
   extern BYTE StateNamePosBlk[];
   STATENAMETABLE StateNameRecord;
   USHORT BufLen;
   ULONG i;
   BYTE KeyBuf[64];
   STATENAMESPECBUF StateNameSpecBuf;

   BufLen = sizeof (StateNameRecord);
   if((i = BTRV (B_OPEN,
                 StateNamePosBlk,
                 &StateNameRecord,
                 &BufLen,
                 STATETABLEFILENAME,
                 B_OPEN_NORMAL)) != NORMAL)
   {
      if(i != B_ERR_FNF)
         return(STATENAMEOPEN);
      else
      {
         /* File does not exist so go ahead and create it */
         if((i = CreateStateNameDB (STATETABLEFILENAME)) != NORMAL)
         {
            return(i);
         }

         BufLen = sizeof (StateNameRecord);
         if(BTRV (B_OPEN,
                  StateNamePosBlk,
                  &StateNameRecord,
                  &BufLen,
                  STATETABLEFILENAME,
                  B_OPEN_NORMAL))
         {
            return(STATENAMEOPEN);
         }
      }
   }

   else
   {
      /* Check that the record size is still what it should be */
      /* Note that this does NOT warrenty against changes in indexing
         or ordering of the record */
      BufLen = sizeof (StateNameSpecBuf);
      if(BTRV (B_STAT,
               StateNamePosBlk,
               &StateNameSpecBuf,
               &BufLen,
               KeyBuf,
               0) || StateNameSpecBuf.RecordLength != sizeof (StateNameRecord))
      {
         printf("SCUP2\n");
         return(RECORDCHANGED);
      }
   }

   return(NORMAL);
}

IM_EX_CTIBASE INT CreateStateNameDB (PCHAR FileName)
{
   BYTE TempPosBlk[128];
   STATENAMETABLE StateNameRecord;
   USHORT BufLen;
   STATENAMESPECBUF StateNameSpecBuf;

   /* File does not exist so go ahead and create it */
   memset (&StateNameSpecBuf, 0, sizeof (StateNameSpecBuf));
   StateNameSpecBuf.RecordLength = sizeof (StateNameRecord);
   StateNameSpecBuf.PageSize = 512;
   StateNameSpecBuf.FileFlags = B_PREALLOC;
   StateNameSpecBuf.IndexCount = 1;
   StateNameSpecBuf.PreAlloc = 1;
   StateNameSpecBuf.KeySpecBuf[0].KeyPos = (USHORT)((ULONG) &StateNameRecord.StateNameNumber - (ULONG) &StateNameRecord + 1);
   StateNameSpecBuf.KeySpecBuf[0].KeyLen = sizeof (StateNameRecord.StateNameNumber);
   StateNameSpecBuf.KeySpecBuf[0].KeyFlag = B_MOD | B_EXT;
   StateNameSpecBuf.KeySpecBuf[0].KeyType = B_UNSIGNED;
   BufLen = sizeof (StateNameSpecBuf);

   if(BTRV (B_CREATE,
            TempPosBlk,
            &StateNameSpecBuf,
            &BufLen,
            FileName,
            0))
   {
      return(STATENAMECREATE);
   }

   return(NORMAL);
}

/* Open the State Table DB with an optional path name */
IM_EX_CTIBASE INT OpenStateNameDB(PCHAR PathName, PBYTE MyPosBlk)
{
   STATENAMETABLE StateNameRecord;
   USHORT BufLen;
   CHAR FileName[128];

   /* Make the file name first */
   if(PathName[0] == '\0')
   {
      strcpy (FileName, STATETABLEFILENAME);
   }
   else
   {
      strcpy (FileName, PathName);
      strcat (FileName, STATETABLEFILENAME);
   }

   BufLen = sizeof (StateNameRecord);
   return(BTRV (B_OPEN,
                MyPosBlk,
                &StateNameRecord,
                &BufLen,
                FileName,
                B_OPEN_NORMAL));
}


/* Routine to close StateName  database */
INT CloseStateNameDB(VOID)
{
   extern BYTE StateNamePosBlk[];
   USHORT BufLen;

   BTRV (B_CLOSE,
         StateNamePosBlk,
         NULL,
         &BufLen,
         NULL,
         0);

   return(NORMAL);

}



IM_EX_CTIBASE INT StateNameAdd (STATENAMETABLE *StateNameRecord)
{
   extern BYTE StateNamePosBlk[];
   USHORT BufLen;
   STATENAMETABLE Dummy;
   STATENUMBER KeyStateNumber;

   /* Check and see if this StateName exists already */
   Dummy.StateNameNumber = StateNameRecord->StateNameNumber;
   if(!(StateNameGetEqual (&Dummy)))
      return(STATENAMEEXISTS);

   /* Everything is ok so go ahead and add StateName */
   BufLen = sizeof (*StateNameRecord);
   if(BTRV (B_INSERT,
            StateNamePosBlk,
            StateNameRecord,
            &BufLen,
            &KeyStateNumber,
            KEYSTATENUMBER))
      return(INSERTERROR);

   /* Now that we have added record do a get equal to repositon */
   BtrvHistUpdate (STATETABLEFILENAME);
   return(StateNameGetEqual (StateNameRecord));
}



IM_EX_CTIBASE INT StateNameUpdate (STATENAMETABLE *StateNameRecord)
{
   extern BYTE StateNamePosBlk[];
   USHORT BufLen;
   STATENAMETABLE Dummy;
   STATENUMBER KeyStateNumber;

   /* Check and see if this StateName exists already */
   Dummy.StateNameNumber = StateNameRecord->StateNameNumber;
   if(StateNameGetEqual (&Dummy))
      return(STATENAMEUNKNOWN);

   /* Everything is ok so go ahead and update StateName  */
   BufLen = sizeof (*StateNameRecord);
   if(BTRV (B_UPDATE,
            StateNamePosBlk,
            StateNameRecord,
            &BufLen,
            &KeyStateNumber,
            KEYSTATENUMBER))
      return(UPDATEERROR);

   /* Now that we have update record do a get equal to reposition */
   BtrvHistUpdate (STATETABLEFILENAME);
   return(StateNameGetEqual (StateNameRecord));
}



IM_EX_CTIBASE INT StateNameDelete (STATENAMETABLE *StateNameRecord)
{
   extern BYTE StateNamePosBlk[];
   STATENAMETABLE Dummy;
   // STATENUMBER KeyStateNumber;
   USHORT BufLen;

   /* Check and see if this StateName exists already */
   Dummy.StateNameNumber = StateNameRecord->StateNameNumber;
   if(StateNameGetEqual (&Dummy))
      return(STATENAMEUNKNOWN);

   /* If we made it to this  StateName  exists and is unused */
   if(BTRV (B_DELETE,
            StateNamePosBlk,
            NULL,
            &BufLen,
            NULL,
            0))
      return(DELETEERROR);

   BtrvHistUpdate (STATETABLEFILENAME);
   return(NORMAL);
}



IM_EX_CTIBASE INT StateNameGetEqual (STATENAMETABLE *StateNameRecord)
{
   extern BYTE StateNamePosBlk[];
   USHORT BufLen;
   STATENUMBER KeyStateNumber;

   /* get the record from the database */
   KeyStateNumber.StateNameNumber = StateNameRecord->StateNameNumber;
   BufLen = sizeof (*StateNameRecord);
   if(BTRV (B_GETEQ,
            StateNamePosBlk,
            StateNameRecord,
            &BufLen,
            &KeyStateNumber,
            KEYSTATENUMBER))
   {
      return(STATENAMEUNKNOWN);
   }

   return(NORMAL);
}



IM_EX_CTIBASE INT StateNameGetFirst (STATENAMETABLE *StateNameRecord)
{
   extern BYTE StateNamePosBlk[];
   USHORT BufLen;
   STATENUMBER KeyStateNumber;

   /* get the record from the database */
   BufLen = sizeof (*StateNameRecord);
   if(BTRV (B_GETFIRST,
            StateNamePosBlk,
            StateNameRecord,
            &BufLen,
            &KeyStateNumber,
            KEYSTATENUMBER))
   {
      return(GETFIRSTERROR);
   }

   return(NORMAL);
}



IM_EX_CTIBASE INT StateNamegetNext (STATENAMETABLE *StateNameRecord)
{
   extern BYTE StateNamePosBlk[];
   USHORT BufLen;
   STATENUMBER KeyStateNumber;

   /* get the record from the database */
   BufLen = sizeof (*StateNameRecord);
   if(BTRV (B_GETNEXT,
            StateNamePosBlk,
            StateNameRecord,
            &BufLen,
            &KeyStateNumber,
            KEYSTATENUMBER))
   {
      return(GETNEXTERROR);
   }

   return(NORMAL);
}



IM_EX_CTIBASE INT StateNamegetPrev (STATENAMETABLE *StateNameRecord)
{
   extern BYTE StateNamePosBlk[];
   USHORT BufLen;
   STATENUMBER KeyStateNumber;

   /* get the record from the database */
   BufLen = sizeof (*StateNameRecord);
   if(BTRV (B_GETPREV,
            StateNamePosBlk,
            StateNameRecord,
            &BufLen,
            &KeyStateNumber,
            KEYSTATENUMBER))
   {
      return(GETPREVERROR);
   }

   return(NORMAL);
}



/* Routine to get number of records in load control StateName database */
IM_EX_CTIBASE INT StateNameRecords (PULONG Records)
{
   extern BYTE StateNamePosBlk[];
   USHORT BufLen;
   BYTE KeyBuf[64];
   STATENAMESPECBUF StateNameSpecBuf;

   /* get the file information */
   BufLen = sizeof (StateNameSpecBuf);
   if(BTRV (B_STAT,
            StateNamePosBlk,
            &StateNameSpecBuf,
            &BufLen,
            KeyBuf,
            0))
      return(ERRUNKNOWN);

   /* ok so return # records */
   *Records = StateNameSpecBuf.Records;

   return(NORMAL);
}


/* Routine to intialize status point table database */
INT InitStatPointDB (VOID)
{
//    STATPOINT StatPointRecord;
   USHORT BufLen;
   ULONG i;
   BYTE KeyBuf[64];
   STATPOINTSPECBUF StatPointSpecBuf;

   if(StatPointPosBlk == NULL)
   {
      if((i = OpenStatPtDB (USE_NO_PATH_NAME,
                            PositionBlock (&StatPointPosBlk, InitStatPointDB))) != NORMAL)
      {
         if(i != B_ERR_FNF)
         {
            return(STATPOINTOPEN);
         }
         /* File does not exist so go ahead and create it */
         if((i = CreateStatPointDB (STATPOINTFILENAME)) != NORMAL)
         {
            return(i);
         }

         if(OpenStatPtDB (USE_NO_PATH_NAME,
                          PositionBlock (&StatPointPosBlk, InitStatPointDB)))
         {
            return(STATPOINTOPEN);
         }
      }
      else
      {
         /* Check that the record size is still what it should be */
         /* Note that this does NOT warrenty against changes in indexing
            or ordering of the record */
         BufLen = sizeof (StatPointSpecBuf);
         if(BTRV (B_STAT,
                  PositionBlock (&StatPointPosBlk, InitStatPointDB),
                  &StatPointSpecBuf,
                  &BufLen,
                  KeyBuf,
                  0) || StatPointSpecBuf.RecordLength != sizeof (STATPOINT))
         {
            printf("SCUP3\n");
            return(RECORDCHANGED);
         }
      }
   }
   else
   {
      return(OpenStatPtDB (USE_NO_PATH_NAME, PositionBlock (&StatPointPosBlk, InitStatPointDB)));
   }

   return(NORMAL);
}


/* Routine to open Status Point database */
IM_EX_CTIBASE INT OpenStatPtDB(PCHAR PathName, PBYTE MyPosBlk)
{
   STATPOINT ScanRecord;
   USHORT BufLen;
   CHAR FileName[128];

   /* Make the file name first */
   if(PathName[0] == '\0')
   {
      strcpy (FileName, STATPOINTFILENAME);
   }
   else
   {
      strcpy (FileName, PathName);
      strcat (FileName, STATPOINTFILENAME);
   }

   BufLen = sizeof (ScanRecord);
   return(BTRV (B_OPEN,
                MyPosBlk,
                &ScanRecord,
                &BufLen,
                FileName,
                B_OPEN_NORMAL));
}

IM_EX_CTIBASE INT CreateStatPointDB (PCHAR FileName)
{
   BYTE TempPosBlk[128];
   STATPOINT StatPointRecord;
   USHORT BufLen;
   STATPOINTSPECBUF StatPointSpecBuf;

   /* File does not exist so go ahead and create it */
   memset (&StatPointSpecBuf, 0, sizeof (StatPointSpecBuf));
   StatPointSpecBuf.RecordLength = sizeof (StatPointRecord);
   StatPointSpecBuf.PageSize = 512;
   StatPointSpecBuf.FileFlags = B_PREALLOC;
   StatPointSpecBuf.IndexCount = 3;
   StatPointSpecBuf.PreAlloc = 1;
   StatPointSpecBuf.KeySpecBuf[0].KeyPos = (USHORT) ((ULONG) &StatPointRecord.DeviceName - (ULONG) &StatPointRecord) + 1;
   StatPointSpecBuf.KeySpecBuf[0].KeyLen = sizeof (StatPointRecord.DeviceName);
   StatPointSpecBuf.KeySpecBuf[0].KeyFlag = B_MOD | B_EXT | B_SEG;
   StatPointSpecBuf.KeySpecBuf[0].KeyType = B_STRING;
   StatPointSpecBuf.KeySpecBuf[1].KeyPos = (USHORT) ((ULONG) &StatPointRecord.PointName - (ULONG) &StatPointRecord) + 1;
   StatPointSpecBuf.KeySpecBuf[1].KeyLen = sizeof (StatPointRecord.PointName);
   StatPointSpecBuf.KeySpecBuf[1].KeyFlag = B_MOD | B_EXT;
   StatPointSpecBuf.KeySpecBuf[1].KeyType = B_STRING;
   StatPointSpecBuf.KeySpecBuf[2].KeyPos = (USHORT) ((ULONG) &StatPointRecord.NextScan - (ULONG) &StatPointRecord) + 1;
   StatPointSpecBuf.KeySpecBuf[2].KeyLen = sizeof (StatPointRecord.NextScan);
   StatPointSpecBuf.KeySpecBuf[2].KeyFlag = B_MOD | B_EXT | B_DUP;
   StatPointSpecBuf.KeySpecBuf[2].KeyType = B_UNSIGNED;
   StatPointSpecBuf.KeySpecBuf[3].KeyPos = (USHORT) ((ULONG) &StatPointRecord.ReportOrder - (ULONG) &StatPointRecord) + 1;
   StatPointSpecBuf.KeySpecBuf[3].KeyLen = sizeof (StatPointRecord.ReportOrder);
   StatPointSpecBuf.KeySpecBuf[3].KeyFlag = B_MOD | B_EXT | B_DUP | B_SEG;
   StatPointSpecBuf.KeySpecBuf[3].KeyType = B_UNSIGNED;
   StatPointSpecBuf.KeySpecBuf[4].KeyPos = (USHORT) ((ULONG) &StatPointRecord.DeviceName - (ULONG) &StatPointRecord) + 1;
   StatPointSpecBuf.KeySpecBuf[4].KeyLen = sizeof (StatPointRecord.DeviceName);
   StatPointSpecBuf.KeySpecBuf[4].KeyFlag = B_MOD | B_EXT | B_DUP | B_SEG;
   StatPointSpecBuf.KeySpecBuf[4].KeyType = B_STRING;
   StatPointSpecBuf.KeySpecBuf[5].KeyPos = (USHORT) ((ULONG) &StatPointRecord.PointName - (ULONG) &StatPointRecord) + 1;
   StatPointSpecBuf.KeySpecBuf[5].KeyLen = sizeof (StatPointRecord.PointName);
   StatPointSpecBuf.KeySpecBuf[5].KeyFlag = B_MOD | B_EXT | B_DUP;
   StatPointSpecBuf.KeySpecBuf[5].KeyType = B_STRING;
   BufLen = sizeof (StatPointSpecBuf);

   if(BTRV (B_CREATE,
            TempPosBlk,
            &StatPointSpecBuf,
            &BufLen,
            FileName,
            0))
   {
      return(STATPOINTCREATE);
   }

   return(NORMAL);
}

/* Routine to close StatPoint  database */
INT CloseStatPointDB(VOID)
{
   USHORT BufLen;

   BTRV (B_CLOSE,
         PositionBlock (&StatPointPosBlk, InitStatPointDB),
         NULL,
         &BufLen,
         NULL,
         0);

   return(NORMAL);

}



IM_EX_CTIBASE INT StatPointAdd (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   STATPOINT Dummy;
   STATPOINTNAME KeyStatPointName;

   /* Check and see if this StatPoint exists */
   memcpy (Dummy.DeviceName, StatPointRecord->DeviceName, sizeof (StatPointRecord->DeviceName));
   memcpy (Dummy.PointName, StatPointRecord->PointName, sizeof (StatPointRecord->PointName));
   if(!StatPointGetEqual (&Dummy))
      return(STATPOINTEXISTS);

   StatPointRecord->NextScan = 0L;     // this flags LC that this is new

   /* Everything is ok so go ahead and add StatPoint */
   BufLen = sizeof (*StatPointRecord);
   if(BTRV (B_INSERT,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            StatPointRecord,
            &BufLen,
            &KeyStatPointName,
            KEYSTATPOINTNAME))
      return(INSERTERROR);

   /* Now that we have added record do a get equal to repositon */
   BtrvHistUpdate (STATPOINTFILENAME);
   return(StatPointGetEqual (StatPointRecord));
}


/* update routine for setup routine */
IM_EX_CTIBASE INT StatPointUpdate (STATPOINT *StatPointRecord)
{
   STATPOINT       Dummy;
   STATPOINTNAME   KeyStatPointName;

   USHORT  BufLen;
   int     rc;

   /* Check and see if this StatPoint exists */
   memcpy (Dummy.DeviceName, StatPointRecord->DeviceName, sizeof (StatPointRecord->DeviceName));
   memcpy (Dummy.PointName, StatPointRecord->PointName, sizeof (StatPointRecord->PointName));


   /* lock the record so we can update static data fields */
   rc = StatPointLock (&Dummy);

   if(rc == RECORD_IN_USE)
   {
      /* Try to get a lock one more time */
      rc = StatPointLock (&Dummy);
   }

   /* Record was found and locked */
   if(rc == NORMAL)
   {
      /* ONLY Change the static part of the db */
      Dummy.ReportOrder = StatPointRecord->ReportOrder;
      Dummy.StatusFlag = StatPointRecord->StatusFlag;

      /* Everything is ok so go ahead and update StatPoint */
      BufLen = sizeof (*StatPointRecord);
      if(BTRV (B_UPDATE,
               PositionBlock (&StatPointPosBlk, InitStatPointDB),
               &Dummy,
               &BufLen,
               &KeyStatPointName,
               KEYSTATPOINTNAME))
      {
         rc = UPDATEERROR;
      }

      /* Now that we have update record do a get equal to reposition */
      BtrvHistUpdate (STATPOINTFILENAME);
      StatPointGetEqual (StatPointRecord);
   }

   return(rc);
}



/* status point fast update */
IM_EX_CTIBASE INT StatPointFastUpdate (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   STATPOINTNAME KeyStatPointName;

   BufLen = sizeof (*StatPointRecord);
   if(BTRV (B_UPDATE,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            StatPointRecord,
            &BufLen,
            &KeyStatPointName,
            KEYSTATPOINTNAME))
   {
      return(UPDATEERROR);
   }

   /* Now that we have update record do a get equal to reposition */
   BtrvHistUpdate (STATPOINTFILENAME);
   return(StatPointGetEqual (StatPointRecord));
}



IM_EX_CTIBASE INT StatPointDelete (STATPOINT *StatPointRecord)
{
   STATPOINT Dummy;
   // STATPOINTNAME KeyStatPointName;
   USHORT BufLen;
   int rc;

   /* check and see if this StatPoint  exists */
   memcpy (Dummy.DeviceName, StatPointRecord->DeviceName, sizeof (StatPointRecord->DeviceName));
   memcpy (Dummy.PointName, StatPointRecord->PointName, sizeof (StatPointRecord->PointName));

   /* lock the record so we can delete it */
   rc = StatPointLock (&Dummy);

   if(rc == RECORD_IN_USE)
   {
      /* Try to get a lock one more time */
      rc = StatPointLock (&Dummy);
   }

   /* Record was found and locked */
   if(rc == NORMAL)
   {
      /* If we made it to this  StatPoint  exists and is unused */
      if(BTRV (B_DELETE,
               PositionBlock (&StatPointPosBlk, InitStatPointDB),
               NULL,
               &BufLen,
               NULL,
               0))
      {
         /* Record was deleted */
         rc = DELETEERROR;
      }

      BtrvHistUpdate (STATPOINTFILENAME);
   }

   return(rc);
}



IM_EX_CTIBASE INT StatPointGetEqual (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   STATPOINTNAME KeyStatPointName;
   STATPOINTNAME Dummy;

   /* get the record from the database */
   memcpy (KeyStatPointName.DeviceName, StatPointRecord->DeviceName, sizeof (StatPointRecord->DeviceName));
   memcpy (KeyStatPointName.PointName, StatPointRecord->PointName, sizeof (StatPointRecord->PointName));
   BufLen = sizeof (*StatPointRecord);
   if(StatPointRecord->PointName[0] != ' ')
   {
      if(BTRV (B_GETEQ,
               PositionBlock (&StatPointPosBlk, InitStatPointDB),
               StatPointRecord,
               &BufLen,
               &KeyStatPointName,
               KEYSTATPOINTNAME))
      {
         return(STATPOINTUNKNOWN);
      }
   }
   else
   {
      /* get the first point on this device */
      memcpy (Dummy.DeviceName, StatPointRecord->DeviceName, sizeof (StatPointRecord->DeviceName));
      if(BTRV (B_GETGE,
               PositionBlock (&StatPointPosBlk, InitStatPointDB),
               StatPointRecord,
               &BufLen,
               &KeyStatPointName,
               KEYSTATPOINTNAME) || strnicmp(Dummy.DeviceName, KeyStatPointName.DeviceName, sizeof (KeyStatPointName.DeviceName)))
      {
         return(STATPOINTUNKNOWN);
      }
   }

   return(NORMAL);
}

/* Routine to get and lock a record from Scan Point database */
IM_EX_CTIBASE INT StatPointLock (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   SCANPOINTNAME KeyStatPointName;
   int rc;

   /* get the record from the database */
   BufLen = sizeof (*StatPointRecord);
   memcpy (KeyStatPointName.DeviceName, StatPointRecord->DeviceName, sizeof (StatPointRecord->DeviceName));
   memcpy (KeyStatPointName.PointName, StatPointRecord->PointName, sizeof (StatPointRecord->PointName));
   rc = BTRV (B_GETEQ + B_SINGLEWAITLOCK,
              PositionBlock (&StatPointPosBlk, InitStatPointDB),
              StatPointRecord,
              &BufLen,
              &KeyStatPointName,
              KEYSTATPOINTNAME);

   if(rc == B_KEY_VALUE_NOT_FOUND)
   {
      /* record did not exist in database */
      rc = STATPOINTUNKNOWN;
   }
   else if(rc == B_RECORD_INUSE || rc == B_FILE_INUSE)
   {
      rc = RECORD_IN_USE;
   }
   else if(rc != B_NO_ERROR)
   {
      /* return the raw error with offset */
      rc += BTR_RAW_ERR_OFFSET;
   }
   else
   {
      /* no error */
      rc = NORMAL;
   }

   return(rc);
}


/* Routine to implicitly unlock a record in Device database */
IM_EX_CTIBASE INT StatPointUnLock (STATPOINT *StatPointRecord)
{
   USHORT BufLen;

   BufLen = sizeof (*StatPointRecord);
   if(BTRV (B_UNLOCK,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            StatPointRecord,
            &BufLen,
            NULL,
            KEYSTATPOINTNAME))
   {
      return(UNLOCKERROR);
   }

   return(NORMAL);
}



IM_EX_CTIBASE INT StatPointGetFirst (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   STATPOINTNAME KeyStatPointName;

   /* get the record from the database */
   BufLen = sizeof (*StatPointRecord);
   if(BTRV (B_GETFIRST,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            StatPointRecord,
            &BufLen,
            &KeyStatPointName,
            KEYSTATPOINTNAME))
   {
      return(GETFIRSTERROR);
   }

   return(NORMAL);
}



IM_EX_CTIBASE INT StatPointgetNext (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   STATPOINTNAME KeyStatPointName;

   /* get the record from the database */
   BufLen = sizeof (*StatPointRecord);
   if(BTRV (B_GETNEXT,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            StatPointRecord,
            &BufLen,
            &KeyStatPointName,
            KEYSTATPOINTNAME))
   {
      return(GETNEXTERROR);
   }

   return(NORMAL);
}


/* get the next greater record */
IM_EX_CTIBASE INT StatPointgetNextGt (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   STATPOINTNAME KeyStatPointName;

   /* get the record from the database */
   memcpy (KeyStatPointName.DeviceName, StatPointRecord->DeviceName, sizeof (StatPointRecord->DeviceName));
   memcpy (KeyStatPointName.PointName, StatPointRecord->PointName, sizeof (StatPointRecord->PointName));
   BufLen = sizeof (*StatPointRecord);
   if(BTRV (B_GETGT,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            StatPointRecord,
            &BufLen,
            &KeyStatPointName,
            KEYSTATPOINTNAME))
   {
      return(GETNEXTERROR);
   }

   return(NORMAL);
}



IM_EX_CTIBASE INT StatPointgetPrev (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   STATPOINTNAME KeyStatPointName;

   /* get the record from the database */
   BufLen = sizeof (*StatPointRecord);
   if(BTRV (B_GETPREV,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            StatPointRecord,
            &BufLen,
            &KeyStatPointName,
            KEYSTATPOINTNAME))
   {
      return(GETPREVERROR);
   }

   return(NORMAL);
}



/* Routine to get number of records in load control StatPoint database */
IM_EX_CTIBASE INT StatPointRecords (PULONG Records)
{
   USHORT BufLen;
   BYTE KeyBuf[64];
   STATPOINTSPECBUF StatPointSpecBuf;

   /* get the file information */
   BufLen = sizeof (StatPointSpecBuf);
   if(BTRV (B_STAT,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            &StatPointSpecBuf,
            &BufLen,
            KeyBuf,
            0))
      return(ERRUNKNOWN);

   /* ok so return # records */
   *Records = StatPointSpecBuf.Records;

   return(NORMAL);
}



IM_EX_CTIBASE INT StatPointGetFirstScan (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   STATPOINTNEXT KeyStatPointTime;

   /* get the record from the database */
   BufLen = sizeof (*StatPointRecord);
   if(BTRV (B_GETFIRST,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            StatPointRecord,
            &BufLen,
            &KeyStatPointTime,
            KEYSTATPOINTNEXTSTAT))
   {
      return(GETFIRSTERROR);
   }

   return(NORMAL);
}


IM_EX_CTIBASE INT StatPointgetScanLE (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   STATPOINTNEXT KeyStatPointTime;

   /* get the record from the database */
   KeyStatPointTime.NextScan = StatPointRecord->NextScan;
   BufLen = sizeof (*StatPointRecord);
   if(BTRV (B_GETLE,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            StatPointRecord,
            &BufLen,
            &KeyStatPointTime,
            KEYSTATPOINTNEXTSTAT))
   {
      return(GETPREVERROR);
   }

   return(NORMAL);
}



IM_EX_CTIBASE INT StatPtOrdergetEqual (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   STATPOINTNAMEREPORT KeyOrderStatPointName;

   /* get the record from the database */
   KeyOrderStatPointName.ReportOrder = StatPointRecord->ReportOrder;
   memcpy (KeyOrderStatPointName.DeviceName, StatPointRecord->DeviceName, sizeof (StatPointRecord->DeviceName));
   memcpy (KeyOrderStatPointName.PointName, StatPointRecord->PointName, sizeof (StatPointRecord->PointName));
   BufLen = sizeof (*StatPointRecord);
   if(BTRV (B_GETEQ,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            StatPointRecord,
            &BufLen,
            &KeyOrderStatPointName,
            KEYSTATPOINTREPORT))
   {
      return(STATPOINTUNKNOWN);
   }

   return(NORMAL);
}



IM_EX_CTIBASE INT StatPtOrderGetFirst (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   STATPOINTNAMEREPORT KeyOrderStatPointName;

   /* get the record from the database */
   BufLen = sizeof (*StatPointRecord);
   if(BTRV (B_GETFIRST,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            StatPointRecord,
            &BufLen,
            &KeyOrderStatPointName,
            KEYSTATPOINTREPORT))
   {
      return(GETFIRSTERROR);
   }

   return(NORMAL);
}



IM_EX_CTIBASE INT StatPtOrdergetNext (STATPOINT *StatPointRecord)
{
   USHORT BufLen;
   STATPOINTNAMEREPORT KeyOrderStatPointName;

   /* get the record from the database */
   BufLen = sizeof (*StatPointRecord);
   if(BTRV (B_GETNEXT,
            PositionBlock (&StatPointPosBlk, InitStatPointDB),
            StatPointRecord,
            &BufLen,
            &KeyOrderStatPointName,
            KEYSTATPOINTREPORT))
   {
      return(GETNEXTERROR);
   }

   return(NORMAL);
}


/* see this this database file has changed */
IM_EX_CTIBASE INT Check4ScanPtDBChange (ULONG LastTime, PCHAR DestPathName)
{
   ULONG SrcLastTime;
   CHAR FileName[128];

   if(LastTime == 0L)
   {
      /* we need to check if the target exists and get its time stamp */
      ADDPATHTOFILENAME(DestPathName, FileName);
      strcat (FileName, SCANPOINTFILENAME);
      if(BTRFileCheckTime (FileName, &LastTime))
      {
         return(!NORMAL);
      }
   }

   /* get file information from PRIMARY file for time/date stamps */
   if(!(BtrvHistgetTime (SCANPOINTFILENAME, &SrcLastTime)))
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
IM_EX_CTIBASE INT Check4StatPtDBChange (ULONG LastTime, PCHAR DestPathName)
{
   ULONG SrcLastTime;
   CHAR FileName[128];

   if(LastTime == 0L)
   {
      /* we need to check if the target exists and get its time stamp */
      ADDPATHTOFILENAME(DestPathName, FileName);
      strcat (FileName, STATPOINTFILENAME);
      if(BTRFileCheckTime (FileName, &LastTime))
      {
         return(!NORMAL);
      }
   }

   /* get file information from PRIMARY file for time/date stamps */
   if(!(BtrvHistgetTime (STATPOINTFILENAME, &SrcLastTime)))
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
IM_EX_CTIBASE INT Check4StateTblDBChange (ULONG LastTime, PCHAR DestPathName)
{
   ULONG SrcLastTime;
   CHAR FileName[128];

   if(LastTime == 0L)
   {
      /* we need to check if the target exists and get its time stamp */
      ADDPATHTOFILENAME(DestPathName, FileName);
      strcat (FileName, STATETABLEFILENAME);
      if(BTRFileCheckTime (FileName, &LastTime))
      {
         return(!NORMAL);
      }
   }

   /* get file information from PRIMARY file for time/date stamps */
   if(!(BtrvHistgetTime (STATETABLEFILENAME, &SrcLastTime)))
   {
      if(SrcLastTime > LastTime)
      {
         /* File has changed */
         return(!NORMAL);
      }
   }

   return(NORMAL);
}

/* Routine to copy the whole Scan Point DB from the primary machine */
IM_EX_CTIBASE INT FO_CopyScanPtDB (PCHAR SrcPathName, PCHAR DestPathName)
{

   BYTE SrcPosBlk[128];
   BYTE TargetPosBlk[128];
   CHAR TempFileName[128];
   CHAR RealFileName[128];
   SCANPOINT ScanRecord;
   SCANPOINTNAME KeyBuffer;
   USHORT BufLen;
   USHORT status, rc;
   int i;
   SCAN_E_BUFFER *Buffer;

   if((Buffer = (SCAN_E_BUFFER*)malloc (sizeof(SCAN_E_BUFFER))) == NULL)
   {
      /* GOT No memory get out of Dodge */
      return(!NORMAL);
   }

   if((i = OpenScanPtDB (SrcPathName,
                         SrcPosBlk)) != NORMAL)
   {
      /* could not open the source file */
      free (Buffer);
      return(i);
   }

   BufLen = sizeof (ScanRecord);
   if((status = BTRV (B_STEPFIRST,
                      SrcPosBlk,
                      &Buffer->Buf.RecordsBuf.ScanRecs[0].ScanRec,
                      &BufLen,
                      &KeyBuffer,
                      0)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      free (Buffer);
      return(status);
   }


   /* open the target file in accelerated mode with the path */
   if(DestPathName[0] == '\0')
   {
      strcpy (TempFileName, TEMP2_FILE_NAME);
   }
   else
   {
      strcpy (TempFileName, DestPathName);
      strcat (TempFileName, TEMP2_FILE_NAME);
   }

   BufLen = sizeof (ScanRecord);
   CTIDelete (TempFileName);
   CreateScanPointDB (TempFileName);
   if((rc = BTRV (B_OPEN,
                  TargetPosBlk,
                  &ScanRecord,
                  &BufLen,
                  TempFileName,
                  B_OPEN_ACCELERATED)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      free (Buffer);
      return(rc);
   }

   /* let it know that we have 1 record */
   Buffer->Buf.RecordsBuf.NumRecords = 1;
   while(Buffer->Buf.RecordsBuf.NumRecords > 0)
   {

      for(i=0; i < Buffer->Buf.RecordsBuf.NumRecords; i++)
      {

         BufLen = sizeof (ScanRecord);
         if((rc = BTRV (B_INSERT,
                        TargetPosBlk,
                        &Buffer->Buf.RecordsBuf.ScanRecs[i].ScanRec,
                        &BufLen,
                        &KeyBuffer,
                        0)) != NORMAL)
         {
            /* got on error lets end it */
            status = INSERTERROR;
            break;
         }
      }

      if(status == B_ERR_EOF || status == INSERTERROR)
         /* prev status was eof or insert error */
         break;

      /* fill in the step extended information */
      Buffer->Buf.Filter.DescrLen = sizeof(Buffer->Buf.Filter);
      Buffer->Buf.Filter.id[0] = 'E';
      Buffer->Buf.Filter.id[1] = 'G';
      Buffer->Buf.Filter.RejectCount = 1;      /* no filter so should be no rejects */
      Buffer->Buf.Filter.NumTerms = 0;
      Buffer->Buf.Filter.NumRecords = EXT_RECORD_COUNT_MAX;   /* get 200 records */
      Buffer->Buf.Filter.NumFields = 1;                        /* get 1 field the whole record */
      Buffer->Buf.Filter.Field1.FieldLen = sizeof(ScanRecord);
      Buffer->Buf.Filter.Field1.FieldOffset = 0;

      BufLen = sizeof (*Buffer);
      status = BTRV (B_STEPNEXT_E,
                     SrcPosBlk,
                     Buffer,
                     &BufLen,
                     &KeyBuffer,
                     0);

      if((status != 0) && (status != B_ERR_EOF))
         break;
   }

   free (Buffer);
   BTRV (B_CLOSE, SrcPosBlk, NULL, &BufLen, NULL, 0);
   BTRV (B_CLOSE, TargetPosBlk, NULL, &BufLen, NULL, 0);
   if(status == INSERTERROR)
      return(status);


   /* rename the file to the real one */
   if(DestPathName[0] == '\0')
   {
      strcpy (RealFileName, SCANPOINTFILENAME);
   }
   else
   {
      strcpy (RealFileName, DestPathName);
      strcat (RealFileName, SCANPOINTFILENAME);
   }

   CTIDelete (RealFileName);
   CTIMove (TempFileName, RealFileName);
   return(NORMAL);
}


/* Routine to copy the whole Status Point DB from the primary machine */
IM_EX_CTIBASE INT FO_CopyStatPtDB (PCHAR SrcPathName, PCHAR DestPathName)
{

   BYTE SrcPosBlk[128];
   BYTE TargetPosBlk[128];
   CHAR TempFileName[128];
   CHAR RealFileName[128];
   STATPOINT ScanRecord;
   SCANPOINTNAME KeyBuffer;
   USHORT BufLen;
   USHORT status, rc;
   int i;
   SCAN_E_BUFFER *Buffer;

   if((Buffer = (SCAN_E_BUFFER*)malloc (sizeof(SCAN_E_BUFFER))) == NULL)
   {
      /* GOT No memory get out of Dodge */
      return(!NORMAL);
   }

   if((i = OpenStatPtDB (SrcPathName,
                         SrcPosBlk)) != NORMAL)
   {
      /* could not open the source file */
      free (Buffer);
      return(i);
   }

   BufLen = sizeof (ScanRecord);
   if((status = BTRV (B_STEPFIRST,
                      SrcPosBlk,
                      &Buffer->Buf.RecordsBuf.ScanRecs[0].ScanRec,
                      &BufLen,
                      &KeyBuffer,
                      0)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      free (Buffer);
      return(status);
   }

   /* open the target file in accelerated mode with the path */
   if(DestPathName[0] == '\0')
   {
      strcpy (TempFileName, TEMP2_FILE_NAME);
   }
   else
   {
      strcpy (TempFileName, DestPathName);
      strcat (TempFileName, TEMP2_FILE_NAME);
   }

   BufLen = sizeof (ScanRecord);
   CTIDelete (TempFileName);
   CreateStatPointDB (TempFileName);
   if((rc = BTRV (B_OPEN,
                  TargetPosBlk,
                  &ScanRecord,
                  &BufLen,
                  TempFileName,
                  B_OPEN_ACCELERATED)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      free (Buffer);
      return(rc);
   }

   /* let it know that we have 1 record */
   Buffer->Buf.RecordsBuf.NumRecords = 1;
   while(Buffer->Buf.RecordsBuf.NumRecords > 0)
   {

      for(i=0; i < Buffer->Buf.RecordsBuf.NumRecords; i++)
      {

         BufLen = sizeof (ScanRecord);
         if((rc = BTRV (B_INSERT,
                        TargetPosBlk,
                        &Buffer->Buf.RecordsBuf.ScanRecs[i].ScanRec,
                        &BufLen,
                        &KeyBuffer,
                        0)) != NORMAL)
         {
            /* got on error lets end it */
            status = INSERTERROR;
            break;
         }
      }

      if(status == B_ERR_EOF || status == INSERTERROR)
         /* prev status was eof or insert error */
         break;

      /* fill in the step extended information */
      Buffer->Buf.Filter.DescrLen = sizeof(Buffer->Buf.Filter);
      Buffer->Buf.Filter.id[0] = 'E';
      Buffer->Buf.Filter.id[1] = 'G';
      Buffer->Buf.Filter.RejectCount = 1;      /* no filter so should be no rejects */
      Buffer->Buf.Filter.NumTerms = 0;
      Buffer->Buf.Filter.NumRecords = EXT_RECORD_COUNT_MAX;   /* get 200 records */
      Buffer->Buf.Filter.NumFields = 1;                        /* get 1 field the whole record */
      Buffer->Buf.Filter.Field1.FieldLen = sizeof(ScanRecord);
      Buffer->Buf.Filter.Field1.FieldOffset = 0;

      BufLen = sizeof (*Buffer);
      status = BTRV (B_STEPNEXT_E,
                     SrcPosBlk,
                     Buffer,
                     &BufLen,
                     &KeyBuffer,
                     0);

      if((status != 0) && (status != B_ERR_EOF))
         break;
   }

   free (Buffer);
   BTRV (B_CLOSE, SrcPosBlk, NULL, &BufLen, NULL, 0);
   BTRV (B_CLOSE, TargetPosBlk, NULL, &BufLen, NULL, 0);
   if(status == INSERTERROR)
      return(status);


   /* rename the file to the real one */
   if(DestPathName[0] == '\0')
   {
      strcpy (RealFileName, STATPOINTFILENAME);
   }
   else
   {
      strcpy (RealFileName, DestPathName);
      strcat (RealFileName, STATPOINTFILENAME);
   }

   CTIDelete (RealFileName);
   CTIMove (TempFileName, RealFileName);
   return(NORMAL);
}
/* Routine to copy the whole  state name DB from the primary machine */
IM_EX_CTIBASE INT FO_CopyStateNameDB (PCHAR SrcPathName,
                                   PCHAR DestPathName)
{

   BYTE SrcPosBlk[128];
   BYTE TargetPosBlk[128];
   CHAR TempFileName[128];
   CHAR RealFileName[128];
   STATENAMETABLE StateNameRecord;
   USHORT BufLen;
   USHORT status, rc;
   int i;
   STATENUMBER KeyStateNumber;


   if((i = OpenStateNameDB(SrcPathName,
                           SrcPosBlk)) != NORMAL)
   {
      /* could not open the source file */
      return(i);
   }

   BufLen = sizeof (StateNameRecord);
   if((status = BTRV (B_STEPFIRST,
                      SrcPosBlk,
                      &StateNameRecord,
                      &BufLen,
                      &KeyStateNumber,
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

   BufLen = sizeof (StateNameRecord);
   CTIDelete (TempFileName);
   CreateStateNameDB (TempFileName);
   if((rc = BTRV (B_OPEN,
                  TargetPosBlk,
                  &StateNameRecord,
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

      BufLen = sizeof (StateNameRecord);
      if((rc = BTRV (B_INSERT,
                     TargetPosBlk,
                     &StateNameRecord,
                     &BufLen,
                     &KeyStateNumber,
                     0)) != NORMAL)
      {
         /* Had an error lets quit */
         status = INSERTERROR;
         break;
      }

      BufLen = sizeof (StateNameRecord);
      status = BTRV (B_STEPNEXT,
                     SrcPosBlk,
                     &StateNameRecord,
                     &BufLen,
                     &KeyStateNumber,
                     0);

   }

   BTRV (B_CLOSE, SrcPosBlk, NULL, &BufLen, NULL, 0);
   BTRV (B_CLOSE, TargetPosBlk, NULL, &BufLen, NULL, 0);
   if(status == INSERTERROR)
      return(status);

   /* rename the file to the real one */
   if(DestPathName[0] == '\0')
   {
      strcpy (RealFileName, STATETABLEFILENAME);
   }
   else
   {
      strcpy (RealFileName, DestPathName);
      strcat (RealFileName, STATETABLEFILENAME);
   }

   CTIDelete (RealFileName);
   CTIMove (TempFileName, RealFileName);
   return(NORMAL);
}

/* Routine to copy the whole Scan Point DB from the primary machine */
IM_EX_CTIBASE INT BDB_CopyScanPtDB (PCHAR SrcPathName, PCHAR DestPathName)
{

   BYTE SrcPosBlk[128];
   BYTE TargetPosBlk[128];
   CHAR TempFileName[128];
   CHAR RealFileName[128];
   SCANPOINT ScanRecord;
   SCANPOINTNAME KeyBuffer;
   USHORT BufLen;
   USHORT status, rc;
   int i;
   SCAN_E_BUFFER *Buffer;

   if((Buffer = (SCAN_E_BUFFER*)malloc (sizeof(SCAN_E_BUFFER))) == NULL)
   {
      /* GOT No memory get out of Dodge */
      return(!NORMAL);
   }

   if((i = OpenScanPtDB (SrcPathName,
                         SrcPosBlk)) != NORMAL)
   {
      /* could not open the source file */
      free (Buffer);
      return(i);
   }

   BufLen = sizeof (ScanRecord);
   if((status = BTRV (B_STEPFIRST,
                      SrcPosBlk,
                      &Buffer->Buf.RecordsBuf.ScanRecs[0].ScanRec,
                      &BufLen,
                      &KeyBuffer,
                      0)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      free (Buffer);
      return(status);
   }

   /* dest path will never be empty */
   strcpy (TempFileName, DestPathName);
   strcat (TempFileName, BDBCOPY_TEMP_FILE_NAME);

   BufLen = sizeof (ScanRecord);
   CTIDelete (TempFileName);
   CreateScanPointDB (TempFileName);
   if((rc = BTRV (B_OPEN,
                  TargetPosBlk,
                  &ScanRecord,
                  &BufLen,
                  TempFileName,
                  B_OPEN_ACCELERATED)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      free (Buffer);
      return(rc);
   }

   /* let it know that we have 1 record */
   Buffer->Buf.RecordsBuf.NumRecords = 1;
   while(Buffer->Buf.RecordsBuf.NumRecords > 0)
   {

      for(i=0; i < Buffer->Buf.RecordsBuf.NumRecords; i++)
      {

         BufLen = sizeof (ScanRecord);
         if((rc = BTRV (B_INSERT,
                        TargetPosBlk,
                        &Buffer->Buf.RecordsBuf.ScanRecs[i].ScanRec,
                        &BufLen,
                        &KeyBuffer,
                        0)) != NORMAL)
         {
            /* got on error lets end it */
            status = INSERTERROR;
            break;
         }
      }

      if(status == B_ERR_EOF || status == INSERTERROR)
         /* prev status was eof or insert error */
         break;

      /* fill in the step extended information */
      Buffer->Buf.Filter.DescrLen = sizeof(Buffer->Buf.Filter);
      Buffer->Buf.Filter.id[0] = 'E';
      Buffer->Buf.Filter.id[1] = 'G';
      Buffer->Buf.Filter.RejectCount = 1;      /* no filter so should be no rejects */
      Buffer->Buf.Filter.NumTerms = 0;
      Buffer->Buf.Filter.NumRecords = EXT_RECORD_COUNT_MAX;   /* get 200 records */
      Buffer->Buf.Filter.NumFields = 1;                        /* get 1 field the whole record */
      Buffer->Buf.Filter.Field1.FieldLen = sizeof(ScanRecord);
      Buffer->Buf.Filter.Field1.FieldOffset = 0;

      BufLen = sizeof (*Buffer);
      status = BTRV (B_STEPNEXT_E,
                     SrcPosBlk,
                     Buffer,
                     &BufLen,
                     &KeyBuffer,
                     0);

      if((status != 0) && (status != B_ERR_EOF))
         break;
   }

   free (Buffer);
   BTRV (B_CLOSE, SrcPosBlk, NULL, &BufLen, NULL, 0);
   BTRV (B_CLOSE, TargetPosBlk, NULL, &BufLen, NULL, 0);
   if(status == INSERTERROR)
      return(status);

   /* build the file name without the path because its not in the DATA dir */
   strcpy (RealFileName, DestPathName);
   strcat (RealFileName, BDB_SCANPOINTFILENAME);

   CTIDelete (RealFileName);
   CTIMove (TempFileName, RealFileName);
   return(NORMAL);
}


/* Routine to copy the whole Status Point DB from the primary machine */
IM_EX_CTIBASE INT BDB_CopyStatPtDB (PCHAR SrcPathName, PCHAR DestPathName)
{

   BYTE SrcPosBlk[128];
   BYTE TargetPosBlk[128];
   CHAR TempFileName[128];
   CHAR RealFileName[128];
   STATPOINT ScanRecord;
   SCANPOINTNAME KeyBuffer;
   USHORT BufLen;
   USHORT status, rc;
   int i;
   SCAN_E_BUFFER *Buffer;

   if((Buffer = (SCAN_E_BUFFER*)malloc (sizeof(SCAN_E_BUFFER))) == NULL)
   {
      /* GOT No memory get out of Dodge */
      return(!NORMAL);
   }

   if((i = OpenStatPtDB (SrcPathName, SrcPosBlk)) != NORMAL)
   {
      /* could not open the source file */
      free (Buffer);
      return(i);
   }

   BufLen = sizeof (ScanRecord);
   if((status = BTRV (B_STEPFIRST,
                      SrcPosBlk,
                      &Buffer->Buf.RecordsBuf.ScanRecs[0].ScanRec,
                      &BufLen,
                      &KeyBuffer,
                      0)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      free (Buffer);
      return(status);
   }

   /* dest path will never be empty */
   strcpy (TempFileName, DestPathName);
   strcat (TempFileName, BDBCOPY_TEMP_FILE_NAME);

   BufLen = sizeof (ScanRecord);
   CTIDelete (TempFileName);
   CreateStatPointDB (TempFileName);
   if((rc = BTRV (B_OPEN,
                  TargetPosBlk,
                  &ScanRecord,
                  &BufLen,
                  TempFileName,
                  B_OPEN_ACCELERATED)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      free (Buffer);
      return(rc);
   }

   /* let it know that we have 1 record */
   Buffer->Buf.RecordsBuf.NumRecords = 1;
   while(Buffer->Buf.RecordsBuf.NumRecords > 0)
   {

      for(i=0; i < Buffer->Buf.RecordsBuf.NumRecords; i++)
      {

         BufLen = sizeof (ScanRecord);
         if((rc = BTRV (B_INSERT,
                        TargetPosBlk,
                        &Buffer->Buf.RecordsBuf.ScanRecs[i].ScanRec,
                        &BufLen,
                        &KeyBuffer,
                        0)) != NORMAL)
         {
            /* got on error lets end it */
            status = INSERTERROR;
            break;
         }
      }

      if(status == B_ERR_EOF || status == INSERTERROR)
         /* prev status was eof or insert error */
         break;

      /* fill in the step extended information */
      Buffer->Buf.Filter.DescrLen = sizeof(Buffer->Buf.Filter);
      Buffer->Buf.Filter.id[0] = 'E';
      Buffer->Buf.Filter.id[1] = 'G';
      Buffer->Buf.Filter.RejectCount = 1;      /* no filter so should be no rejects */
      Buffer->Buf.Filter.NumTerms = 0;
      Buffer->Buf.Filter.NumRecords = EXT_RECORD_COUNT_MAX;   /* get 200 records */
      Buffer->Buf.Filter.NumFields = 1;                        /* get 1 field the whole record */
      Buffer->Buf.Filter.Field1.FieldLen = sizeof(ScanRecord);
      Buffer->Buf.Filter.Field1.FieldOffset = 0;

      BufLen = sizeof (*Buffer);
      status = BTRV (B_STEPNEXT_E,
                     SrcPosBlk,
                     Buffer,
                     &BufLen,
                     &KeyBuffer,
                     0);

      if((status != 0) && (status != B_ERR_EOF))
         break;
   }

   free (Buffer);
   BTRV (B_CLOSE, SrcPosBlk, NULL, &BufLen, NULL, 0);
   BTRV (B_CLOSE, TargetPosBlk, NULL, &BufLen, NULL, 0);
   if(status == INSERTERROR)
      return(status);

   /* build the file name without the path because its not in the DATA dir */
   strcpy (RealFileName, DestPathName);
   strcat (RealFileName, BDB_STATPOINTFILENAME);

   CTIDelete (RealFileName);
   CTIMove (TempFileName, RealFileName);
   return(NORMAL);
}
/* Routine to copy the whole  state name DB from the primary machine */
IM_EX_CTIBASE INT BDB_CopyStateNameDB (PCHAR SrcPathName, PCHAR DestPathName)
{

   BYTE SrcPosBlk[128];
   BYTE TargetPosBlk[128];
   CHAR TempFileName[128];
   CHAR RealFileName[128];
   STATENAMETABLE StateNameRecord;
   USHORT BufLen;
   USHORT status, rc;
   int i;
   STATENUMBER KeyStateNumber;


   if((i = OpenStateNameDB(SrcPathName,
                           SrcPosBlk)) != NORMAL)
   {
      /* could not open the source file */
      return(i);
   }

   BufLen = sizeof (StateNameRecord);
   if((status = BTRV (B_STEPFIRST,
                      SrcPosBlk,
                      &StateNameRecord,
                      &BufLen,
                      &KeyStateNumber,
                      0)) != NORMAL)
   {
      CloseAnyBtrvDB (SrcPosBlk);
      return(status);
   }

   /* dest path will never be empty */
   strcpy (TempFileName, DestPathName);
   strcat (TempFileName, BDBCOPY_TEMP_FILE_NAME);

   BufLen = sizeof (StateNameRecord);
   CTIDelete (TempFileName);
   CreateStateNameDB (TempFileName);
   if((rc = BTRV (B_OPEN,
                  TargetPosBlk,
                  &StateNameRecord,
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

      BufLen = sizeof (StateNameRecord);
      if((rc = BTRV (B_INSERT,
                     TargetPosBlk,
                     &StateNameRecord,
                     &BufLen,
                     &KeyStateNumber,
                     0)) != NORMAL)
      {
         /* Had an error lets quit */
         status = INSERTERROR;
         break;
      }

      BufLen = sizeof (StateNameRecord);
      status = BTRV (B_STEPNEXT,
                     SrcPosBlk,
                     &StateNameRecord,
                     &BufLen,
                     &KeyStateNumber,
                     0);

   }

   BTRV (B_CLOSE, SrcPosBlk, NULL, &BufLen, NULL, 0);
   BTRV (B_CLOSE, TargetPosBlk, NULL, &BufLen, NULL, 0);
   if(status == INSERTERROR)
      return(status);

   /* build the file name without the path because its not in the DATA dir */
   strcpy (RealFileName, DestPathName);
   strcat (RealFileName, BDB_STATETABLEFILENAME);

   CTIDelete (RealFileName);
   CTIMove (TempFileName, RealFileName);
   return(NORMAL);
}

