#include "yukon.h"


#pragma title ( "DST Time Routines" )
#pragma subtitle ( "CTI Copyright (c) 1990-1994" )
/*---------------------------------------------------------------------
    Copyright (c) 1990-1993 Cannon Technologies, Inc. All rights reserved.

    Programmer:
        Don Neidviki

    FileName:
        UCTTIME.C

    Purpose:
        Routines to support time on the system

    The following procedures are contained in this module:
        UCTSetFTime                 UCTFTime
        UCTLocalTime                UCTMakeTime
        Holiday                     LongTime
        DSTFlag                     UCTLocoTime
        UCTAsciiTime                UCTAscTime
        setNextInterval             FindMidNight
        MidNight                    MidNightWas
        MakeDailyTime               MakeControlHours


    Initial Date:
        7-92

    Revision History:
        Unknown prior to 8-93
        09-06-93  Converted to 32 bit                             WRO
        11-02-93  Changed timezone conversion to accept decimals  DLS
        12-28-93  Changed to use TZ environment var.              BDW
        12-28-93  Changed holiday schedule to support a year val. BDW
        12-28-93  Changed name on dst file and shared mem name.   BDW
        03-30-99  Convert to Win32 API for YUKON                  CGP

   -------------------------------------------------------------------- */

#include <windows.h>
#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <sys\timeb.h>
#include <string.h>
#include "dllbase.h"
#include "os2_2w32.h"
#include "cticalls.h"
// #include "btrieve.h"
#include "dsm2.h"
#include "elogger.h"
#include "logger.h"

#define HOLIDAY_SEG      "HOLIDAY.DAT"
#define HOLIDAY_FILE     "DATA\\HOLIDAY.DAT"
#define HOLIDAY_ERROR    FALSE
#define DST_MEMORY_NAME  "DSM2_DST.DAT"
#define DST_FILE_NAME    "DATA\\DSM2_DST.DAT"
#define DST_TIME_NAME    "DATA\\DSM2_TIM.DAT"

typedef struct
{
    ULONG MyDSTFlag;
    LONG DSTFixOffset;
} _dstFlags_t;

_dstFlags_t *Flags = NULL;



int IM_EX_CTIBASE setUCTMemoryFlags (struct timeb *);
int IM_EX_CTIBASE getUCTMemoryFlags (void);

CHAR *HolidayPtr = NULL;
CHAR *EndPtr = NULL;

void IM_EX_CTIBASE freeUCTMemory(void)
{
    if(Flags)
    {
        free(Flags);
        Flags = NULL;
    }
}

/* this function loads and sets some global flags for our
   DSM/2 time system */
int IM_EX_CTIBASE setUCTMemoryFlags(struct timeb *timebuffer)
{

    FILE *fptr;
    int rc;

    /* get our memory block from the system  and set
       is values for a time change or adjustment */
    if((rc = getUCTMemoryFlags ()) != NORMAL)
    {
        /* could not get the memeory */
        return(rc);
    }

    /* set the new dst flag and store it */
    Flags->MyDSTFlag = timebuffer->dstflag;

    /* Write a file that will remember dst for us  */
    if((fptr = fopen(DST_FILE_NAME,"w+")) == NULL)
    {
        return(BADFILE);
    }

    /* write the dst status value */
    fprintf (fptr,"%ld", timebuffer->dstflag);
    fclose (fptr);

    return(NORMAL);
}

/* check if the uct environment is initalized */
int IM_EX_CTIBASE getUCTMemoryFlags (void)
{
    static int InitTZFlag = {FALSE};
    PSZ HourOffset;
    FILE *fptr;
    struct timeb buffer;

    if(Flags == NULL)
    {
        /* no memory exists we will get some */
        if(NULL == (Flags = (_dstFlags_t*)malloc(sizeof(*Flags))))
        {
            /* we got no memory from the system */
            return(MEMORY);
        }

        /* because we had to create the memory we need to set it */
        /* lets see if the TZ env is set up */
        if((CTIScanEnv ("TZ", &HourOffset)))
        {
            /* no var, we will default it to Central time */
            // _putenv ("TZ=CST6CDT,4,1,0,7200,10,-1,0,7200,3600");

            // Control Panel settings will be used.
        }

        /* initalize the time zone and DST information */
        _tzset();
        InitTZFlag = TRUE;  /* the time zone must be initialized for each
                               program that uses this DLL.  This flag says so */

        /* check which side of greenwich were are */
        if(_timezone < 0)
        {
            /* We are East of UCT so make our DST ajust the other way */
//            Flags->DSTFixOffset = 3600;
            Flags->DSTFixOffset = 0;
        }
        else
        {
            /* We default to west of UCT so make our DST ajust if positive */
            Flags->DSTFixOffset = 3600;
        }

        if((fptr = fopen (DST_FILE_NAME,"r")) == NULL)
        {
            /* Neither present, make best guess by present DST flag. */
            ftime (&buffer);
            Flags->MyDSTFlag = buffer.dstflag;
            /* write to the file what we think DST is */
            if((fptr = fopen (DST_FILE_NAME, "w+")) != NULL)
            {
                fprintf (fptr,"%ld", Flags->MyDSTFlag);
                fclose (fptr);
            }
        }
        else
        {
            fscanf (fptr,"%ld",&Flags->MyDSTFlag);
            fclose (fptr);
        }
    }
    else if(InitTZFlag == FALSE)
    {
        /* this flag indicates that this program has not initialized the
           TZ environment and needs to do so */

        /* lets see if the TZ env is set up */
        if((CTIScanEnv ("TZ", &HourOffset)))
        {
            /* no var, we will default it to Central time */
            // _putenv ("TZ=CST6CDT,4,1,0,7200,10,-1,0,7200,3600");

            // Control Panel settings will be used.
        }

        /* initalize the time zone and DST information */
        _tzset();
        InitTZFlag = TRUE;  /* the time zone must be initialized for each program that uses this DLL.  This flag says so */
    }

    return(NORMAL);
}


/* Routine to set the local time by being passed the UCT time */
IM_EX_CTIBASE INT UCTSetFTime (struct timeb *TimeBuffer)
{
    DATETIME Date;
    int rc;
    FILE *fptr;
    struct tm *Temp;

    /* Need to set memory for dst flag */
    if(Flags == NULL)
    {
        if((rc = setUCTMemoryFlags(TimeBuffer)) != NORMAL)
        {
            /* we got some kind of an error */
            return(rc);
        }
    }
    else if(((!(Flags->MyDSTFlag) && TimeBuffer->dstflag) || Flags->MyDSTFlag && !(TimeBuffer->dstflag)))
    {
        /* Write file if not present or old data is incorrect */
        Flags->MyDSTFlag = TimeBuffer->dstflag;
        if((fptr = fopen (DST_FILE_NAME, "w+")) != NULL)
        {
            fprintf (fptr,"%ld", TimeBuffer->dstflag);
            fclose (fptr);
        }
    }


    /* Save the milliseconds offset */
    Date.hundredths = TimeBuffer->millitm / 10;

    /* Use the second that we are into */
    TimeBuffer->millitm = 0;

    /* see how we fair with the system */
    if(TimeBuffer->dstflag && !(Flags->MyDSTFlag))
    {
        TimeBuffer->time += Flags->DSTFixOffset;
    }
    else if(!(TimeBuffer->dstflag) && Flags->MyDSTFlag)
    {
        TimeBuffer->time -= Flags->DSTFixOffset;
    }

    Temp = localtime (&TimeBuffer->time);
    CTIGetDateTime(&Date);
    Date.year     = Temp->tm_year + 1900;
    Date.month    = Temp->tm_mon + 1;
    Date.day      = Temp->tm_mday;
    Date.hours    = Temp->tm_hour;
    Date.minutes  = Temp->tm_min;
    Date.seconds  = Temp->tm_sec;
    Date.timezone = _timezone / 60;
    CTISetDateTime (&Date);

    return(NORMAL);
}


/* Routine to calculate the current UCT time */
/* get our unix style time and dst flag */
IM_EX_CTIBASE INT UCTFTime (struct timeb *TimeBuffer)
{
    int rc;

    /* First check if shared memory has been opened */
    if(Flags == NULL)
    {
        /* get our share memory */
        if((rc = getUCTMemoryFlags()) != NORMAL)
        {
            /* we got some kind of an error */
            return(rc);
        }
    }

    /* get the time from the system */
    ftime (TimeBuffer);

    /* make a dst ajustment if needed */
    if(Flags->MyDSTFlag && !(TimeBuffer->dstflag))
    {
        TimeBuffer->time -= Flags->DSTFixOffset;
    }
    else if(!(Flags->MyDSTFlag) && TimeBuffer->dstflag)
    {
        TimeBuffer->time += Flags->DSTFixOffset;
    }

    /* flags is the dst ruler set return value */
    TimeBuffer->dstflag = (SHORT)Flags->MyDSTFlag;

    return(NORMAL);
}


/* Routine to convert a UCT time into a ::localtime structure  */
IM_EX_CTIBASE struct tm * UCTLocalTime (time_t Time, USHORT MyDSTFlag)
{
    struct tm *TheLocalTime;

    /* First check if shared memory has been opened */
    if(Flags == NULL)
    {
        /* get our share memory */
        if(getUCTMemoryFlags())
        {
            /* we got some kind of an error */
            return(NULL);
        }
    }

    /* get the local time value */
    if((TheLocalTime = localtime (&Time)) != NULL)
    {
        /* check and see if our dst flags agree */
        if(MyDSTFlag && !(TheLocalTime->tm_isdst))
        {
            /* move back an hour */
            Time += Flags->DSTFixOffset;
            TheLocalTime = localtime (&Time);
        }
        else if(!(MyDSTFlag) && TheLocalTime->tm_isdst)
        {
            /* move ahead an hour */
            Time -= Flags->DSTFixOffset;
            TheLocalTime = localtime (&Time);
        }

        /* restore the dst flag */
        TheLocalTime->tm_isdst = MyDSTFlag;

        return(TheLocalTime);
    }

    return(NULL);
}


/* Routine to complete a tm structure and return UCT time */
time_t IM_EX_CTIBASE UCTMakeTime (struct tm *TStruct)
{
    time_t MyTime;
    struct tm *MyTStruct;
    USHORT DST;
    int AjustFlag = {FALSE};

    /* First check if shared memory has been opened */
    if(Flags == NULL)
    {
        /* get our share memory */
        if(getUCTMemoryFlags())
        {
            /* we got some kind of an error */
            return(0L);
        }
    }

    /* Save DST flag before it is trashed by mktime */
    DST = TStruct->tm_isdst;

    /* now get the long time */
    MyTime = mktime (TStruct);

    if(DST && !(TStruct->tm_isdst))
    {
        /* move back an hour */
        MyTime += Flags->DSTFixOffset;
        AjustFlag = TRUE;
    }
    else if(!(DST) && TStruct->tm_isdst)
    {
        /* move ahead an hour */
        MyTime -= Flags->DSTFixOffset;
        AjustFlag = TRUE;
    }

    if(AjustFlag == TRUE)
    {
        /* need a recalc because of an adjustment */
        if((MyTStruct = UCTLocalTime (MyTime, (USHORT)TStruct->tm_isdst)) == NULL)
        {
            /* failed for some reason */
            return(0L);
        }
        /* copy in the adjust structure */
        memcpy (TStruct, MyTStruct, sizeof (*TStruct));
    }

    /* restore the dst flag (in case it changed) */
    TStruct->tm_isdst = DST;

    return(MyTime);
}

/* Routine to check if date is a holiday (TRUE) or not (FALSE) */
IM_EX_CTIBASE INT Holiday (struct timeb *TimeB)
{
    ULONG BytesRead, Action;
    USHORT Month, Day, Year;
    HANDLE MyHandle;
    FILESTATUS FileStatus;
    CHAR *MyPtr;
    struct tm Time;
    USHORT RCount;


#ifdef THIS_SUCKS

    if(HolidayPtr == NULL)
    {
        if(CTIGetNamedSharedMem ((PPVOID) &HolidayPtr,
                                 HOLIDAY_SEG,
                                 PAG_READ | PAG_WRITE))
        {
            /* We are first so allocate memory an load holiday file from disk */
            if(CTIOpen (HOLIDAY_FILE,
                        &MyHandle,
                        &Action,
                        0L,
                        FILE_NORMAL,
                        FILE_OPEN,
                        OPEN_ACCESS_READONLY | OPEN_SHARE_DENYNONE,
                        0L))
            {
                /* File does not exist so can't be a holiday */
                return(FALSE);
            }

            /* Querry the File Length */
            FileStatus.cbFile = GetFileSize(MyHandle, NULL);

            /* Allocate Shared Memory Segment */
            if(CTIAllocSharedMem ((PPVOID) &HolidayPtr,
                                  HOLIDAY_SEG,
                                  FileStatus.cbFile + 1,
                                  OBJ_TILE | PAG_COMMIT | PAG_READ | PAG_WRITE))
            {
                /* I give up */
                CTIClose (MyHandle);
                return(FALSE);
            }

            /* Read the List of Holidays into shared memory */
            if(CTIRead (MyHandle,
                        HolidayPtr,
                        (USHORT) FileStatus.cbFile,
                        &BytesRead) || BytesRead != FileStatus.cbFile)
            {
                CTIClose (MyHandle);
                SendTextToLogger ("Inf",
                                  "Holiday File Error");
                return(FALSE);
            }

            HolidayPtr[BytesRead] = '\0';
            EndPtr = &HolidayPtr[strlen (HolidayPtr)];

            CTIClose (MyHandle);
        }
        else
        {
            EndPtr = &HolidayPtr[strlen (HolidayPtr)];
        }
    }

    /* Scan for today in the List of Holidays */
    UCTLocoTime (TimeB->time, TimeB->dstflag, &Time);
    MyPtr = HolidayPtr;
    while(MyPtr < EndPtr)
    {

        /* read a date from the file in mm/dd or mm/dd/yy format */
        if((RCount = sscanf (MyPtr,
                             "%hd/%hd/%hd",
                             &Month,
                             &Day,
                             &Year)) > 1)
        {
            if(Month == (Time.tm_mon + 1) && Day == Time.tm_mday)
            {
                /* check if the year was there and make sure its not zero */
                if(RCount == 3 && Year)
                {
                    /* check the year also */
                    if(Time.tm_year == Year)
                    {
                        /* its a holiday this year */
                        return(TRUE);
                    }
                }
                else
                {
                    return(TRUE);
                }
            }
        }
        else
        {
            /* Check if something should have been here */
            if((MyPtr + 1) < EndPtr)
            {
                SendTextToLogger ("Inf",
                                  "Holiday File Error");
            }

            return(FALSE);
        }

        if((MyPtr = strchr (MyPtr, '\n')) == NULL)
        {
            return(FALSE);
        }

        MyPtr++;
    }

#endif

    return(FALSE);
}


/* routine to get unix style time */
IM_EX_CTIBASE ULONG LongTime ()
{
    struct timeb TimeB;
    UCTFTime (&TimeB);
    return(TimeB.time);
}

/* Routine to get the DST flag */
IM_EX_CTIBASE ULONG DSTFlag ()
{
    struct timeb TimeB;
    UCTFTime (&TimeB);
    return(TimeB.dstflag);
}


/* routine to convert unix style to structure */
void IM_EX_CTIBASE UCTLocoTime (time_t LTime,
                                USHORT MyDSTFlag,
                                struct tm *TStruct)
{
    struct tm *Temp;

    if((Temp = UCTLocalTime (LTime, MyDSTFlag)) != NULL)
    {
        /* its safe to copy the contents */
        memcpy (TStruct, Temp, sizeof (*TStruct));
    }

}


/* routine to convert unix style to string */
void IM_EX_CTIBASE UCTAsciiTime (time_t LTime,
                                 USHORT MyDSTFlag,
                                 PCHAR STime)
{
    strcpy (STime, asctime (UCTLocalTime (LTime, MyDSTFlag)));
}


/* routine to convert unix style to string */
PCHAR IM_EX_CTIBASE UCTAscTime (time_t LTime, USHORT MyDSTFlag)
{
    return(asctime (UCTLocalTime (LTime, MyDSTFlag)));
}


/* Return to find the next scan time */
ULONG IM_EX_CTIBASE setNextInterval(time_t TimeNow, ULONG ScanRate)
{
    ULONG SecsPastHour;

    SecsPastHour = TimeNow % 3600L;

    if((SecsPastHour % ScanRate) == 0)
    {
        return(TimeNow);
    }

    return(TimeNow - (SecsPastHour % ScanRate) + ScanRate);
}

int IM_EX_CTIBASE FindTodayStart (time_t *LTime, USHORT MyDSTFlag, time_t *LMidNight)
{
    struct tm TStruct, *Temp;
    time_t LocalLTime;

    LocalLTime = *LTime;
    LocalLTime -= 86400L;

    /* get time 24 hours before now */
    if((Temp = UCTLocalTime (LocalLTime, MyDSTFlag)) == NULL)
    {
        return(!NORMAL);
    }

    /* it was safe to copy this guy */
    memcpy (&TStruct, Temp, sizeof (TStruct));

    /* set time to midnight */
    TStruct.tm_sec = 0;
    TStruct.tm_min = 0;
    TStruct.tm_hour = 0;

    /*  get midnight time  */
    *LMidNight = UCTMakeTime (&TStruct);

    /* fix if dst doesn't jive */
    if(TStruct.tm_isdst && !(MyDSTFlag))
    {
        *LMidNight+= Flags->DSTFixOffset;
    }
    else if(!(TStruct.tm_isdst) && MyDSTFlag)
    {
        *LMidNight-= Flags->DSTFixOffset;
    }

    return(NORMAL);
}

int IM_EX_CTIBASE FindMidNight (time_t *LTime, USHORT MyDSTFlag, time_t *LMidNight)
{
    struct tm TStruct, *Temp;
    time_t LocalLTime;

    LocalLTime = *LTime;
    LocalLTime += 86400L;

    /* get time 24 hours from now */
    if((Temp = UCTLocalTime (LocalLTime, MyDSTFlag)) == NULL)
    {
        return(!NORMAL);
    }

    /* it was safe to copy this guy */
    memcpy (&TStruct, Temp, sizeof (TStruct));

    /* set time to midnight */
    TStruct.tm_sec = 0;
    TStruct.tm_min = 0;
    TStruct.tm_hour = 0;

    /*  get midnight time  */
    *LMidNight = UCTMakeTime (&TStruct);

    /* fix if dst doesn't jive */
    if(TStruct.tm_isdst && !(MyDSTFlag))
    {
        *LMidNight+= Flags->DSTFixOffset;
    }
    else if(!(TStruct.tm_isdst) && MyDSTFlag)
    {
        *LMidNight-= Flags->DSTFixOffset;
    }

    return(NORMAL);
}


/* this routine call findmidnight and is here to be compatable with BASIC */
time_t IM_EX_CTIBASE MidNight (time_t LTime, USHORT MyDSTFlag)
{
    time_t TempLTime;

    if(FindMidNight (&LTime, MyDSTFlag, &TempLTime))
    {
        /* had an error */
        return(0L);
    }

    return(TempLTime);
}

time_t IM_EX_CTIBASE MidNightWas (time_t LTime, USHORT MyDSTFlag)
{
    struct tm TStruct, *Temp;
    ULONG TempLTime;

    if((Temp = UCTLocalTime (LTime, MyDSTFlag)) == NULL)
    {
        return(0L);
    }

    memcpy (&TStruct, Temp, sizeof (TStruct));

    /* set time to midnight */
    TStruct.tm_sec = 0;
    TStruct.tm_min = 0;
    TStruct.tm_hour = 0;

    /*  get midnight time  */
    TempLTime = UCTMakeTime (&TStruct);

    /*  get what the system thinks dst should be  */
    mktime (&TStruct);

    /* fix if dst doesn't jive */
    if(TStruct.tm_isdst && !(MyDSTFlag))
    {
        TempLTime += Flags->DSTFixOffset;
    }
    else if(!(TStruct.tm_isdst) && MyDSTFlag)
    {
        TempLTime -= Flags->DSTFixOffset;
    }

    return(TempLTime);
}

int IM_EX_CTIBASE MakeDailyTime (ULONG LTime, PCHAR MyTime)
/* this makes a time total from midnight */
{
    CHAR WorkString[30];
    ULONG WorkInteger;
    ULONG MyMinutes;

    if(LTime < 1)
    {
        /* no time yet */
        strcpy (MyTime, "00:00:00");
        return(NORMAL);
    }

    WorkInteger = (LTime / 3600L);
    if(WorkInteger < 10)
    {
        WorkString[0] = '0';
        ultoa (WorkInteger, WorkString + 1, 10);
    }
    else
    {
        ultoa (WorkInteger, WorkString, 10);
    }

    WorkInteger = (LTime % 3600L);      /* get the let over seconds */
    MyMinutes = WorkInteger / 60L;
    WorkString[2] = ':';
    if(MyMinutes < 10)
    {
        WorkString[3] = '0';
        ultoa (MyMinutes, WorkString + 4, 10);
    }
    else
    {
        ultoa (MyMinutes, WorkString + 3, 10);
    }

    WorkString[5] = ':';
    /* get the seconds */
    if((WorkInteger - (MyMinutes * 60)) < 10)
    {
        WorkString[6] = '0';
        ultoa ((WorkInteger - (MyMinutes * 60)), WorkString + 7, 10);
    }
    else
    {
        ultoa ((WorkInteger - (MyMinutes * 60)), WorkString + 6, 10);
    }

    /* terminate it just to make sure */
    WorkString[8] = '\0';
    strcpy (MyTime, WorkString);

    return(NORMAL);
}

int IM_EX_CTIBASE MakeCtrlHours (ULONG LTime, PCHAR CtrlHours)
/* this makes a total control hours */
{
    CHAR WorkString1[34];
    CHAR WorkString2[18];
    USHORT count;

    if(LTime < 1)
    {
        /* no time yet */
        strcpy (CtrlHours, "   0.0");
        return(NORMAL);
    }

    /* find midnight for a reference and add to daily time */
    ltoa ((LTime / 3600L), WorkString1, 10);
    itoa ((USHORT)((LTime % 3600L) / 360), WorkString2, 10);
    strcat (WorkString1, ".");               /* decimal point   */
    strcat (WorkString1, WorkString2);       /* decimal it self */

    /* now fix the length */
    count = strlen(WorkString1);
    if(count < 6)
    {
        /* right justify the string */
        memset (WorkString2, ' ', 10);
        WorkString2[6 - count] = '\0';
        strcat (WorkString2, WorkString1);
        strcpy (CtrlHours, WorkString2);
    }
    else
    {
        strcpy (CtrlHours, WorkString1);
    }

    return(NORMAL);
}

/* This routine makes an ascii date from a long time.
   The format is: mm-dd-yy and is null terminated */

int IM_EX_CTIBASE MakeAsciiDate (ULONG TimeStamp, USHORT DstFlag, PCHAR MyDate)
{

    struct tm *TStruct;

    if(TimeStamp < 347184000L || (LONG)TimeStamp == -1L)
    {
        /* time is invalid return an error */
        strcpy (MyDate, "********");
        return(!NORMAL);
    }

    TStruct = UCTLocalTime (TimeStamp, DstFlag);

    /* fix month value to my format */
    TStruct->tm_mon += 1;
    if(TStruct->tm_mon < 10)
    {
        /* add a leading zero when month is 1 digit */
        itoa (TStruct->tm_mon, MyDate + 1, 10);
        MyDate[0] = '0';
    }
    else
    {
        itoa (TStruct->tm_mon, MyDate, 10);
    }

    MyDate[2] = '-';

    if(TStruct->tm_mday < 10)
    {
        /* add a leading zero when day is 1 digit */
        itoa (TStruct->tm_mday, MyDate + 4, 10);
        MyDate[3] = '0';
    }
    else
    {
        itoa (TStruct->tm_mday, MyDate + 3, 10);
    }

    MyDate[5] = '-';


    if(TStruct->tm_year < 10)
    {
        /* add a leading zero when year is 1 digit */
        itoa (TStruct->tm_year, MyDate + 7, 10);
        MyDate[6] = '0';
    }
    else
    {
        itoa (TStruct->tm_year, MyDate + 6, 10);
    }

    return(NORMAL);
}

/* This routine makes an ascii time from a long time.
   The format is: hh-mm-ss and is null terminated */

int IM_EX_CTIBASE MakeAsciiTime (ULONG TimeStamp, USHORT DstFlag, PCHAR MyTime)
{

    struct tm *TStruct;
    char *TempTime;

    if(TimeStamp < 347184000L || (LONG)TimeStamp == -1L)
    {
        /* time is invalid return an error */
        strcpy (MyTime, "********");
        return(!NORMAL);
    }

    TStruct = UCTLocalTime (TimeStamp, DstFlag);
    TempTime = asctime (TStruct);

    /* add the time and a null */
    strncpy (MyTime, TempTime + 11, 8);
    MyTime[8] = '\0';

    return(NORMAL);
}

/* this function returns the current season based on
   a set a dates in an ascii file */
int IM_EX_CTIBASE getCurrentSeason (void)
{

    /* this var keeps track of summer season start & stop */
    static DSM2_SEASON SummerSeason = {-2,0,0,0};
    SHORT TempStartMonth;
    ULONG BytesRead, Action;
    HANDLE MyHandle;
    FILESTATUS FileStatus;
    CHAR *MyBuffer;
    struct tm Time;
    USHORT RCount;
    struct timeb TimeB;


    if(SummerSeason.StartMonth == -2)
    {
        /* has not been initialized yet */
        SummerSeason.StartMonth = -1;  /* do this in case of an error so we
                                          don't try to load the file over and over */
        if(CTIOpen (SEASON_FILE_NAME,
                    &MyHandle,
                    &Action,
                    0L,
                    FILE_NORMAL,
                    FILE_OPEN,
                    OPEN_ACCESS_READONLY | OPEN_SHARE_DENYNONE,
                    0L))
        {
            /* File does not exist so there are no season's */
            return(FALSE);
        }

        /* Querry the File Length */
        FileStatus.cbFile = GetFileSize(MyHandle, NULL);

        /* Allocate some temporary memeory */
        if((MyBuffer = (CHAR*)malloc (FileStatus.cbFile + 1)) == NULL)
        {
            /* I give up could not get any memory */
            CTIClose (MyHandle);
            return(FALSE);
        }

        /* Read the season dates into memory */
        if(CTIRead (MyHandle,
                    MyBuffer,
                    (USHORT) FileStatus.cbFile,
                    &BytesRead) || BytesRead != FileStatus.cbFile)
        {
            CTIClose (MyHandle);
            SendTextToLogger ("Inf", "Season File Error");
            free (MyBuffer);
            return(FALSE);
        }

        MyBuffer[BytesRead] = '\0';
        CTIClose (MyHandle);

        /* get the current information */
        UCTFTime (&TimeB);
        UCTLocoTime (TimeB.time, TimeB.dstflag, &Time);

        /* now we look for the dates */
        /* read a date from the file in mm/dd or mm/dd/yy format */
        if((RCount = sscanf (MyBuffer,
                             "%hd/%hd,%hd/%hd",
                             &TempStartMonth,
                             &SummerSeason.StartDay,
                             &SummerSeason.StopMonth,
                             &SummerSeason.StopDay)) == 4)
        {
            /* we go some values give them a check */
            --TempStartMonth;
            Time.tm_mon = TempStartMonth;
            Time.tm_mday = SummerSeason.StartDay;
            Time.tm_sec = 0;
            Time.tm_min = 0;
            Time.tm_hour = 0;

            if(mktime (&Time) != -1)
            {
                /* check further */
                if(Time.tm_mon == TempStartMonth &&
                   Time.tm_mday == SummerSeason.StartDay)
                {
                    /* good so far now check the stop date */
                    --SummerSeason.StopMonth;
                    Time.tm_mon = SummerSeason.StopMonth;
                    Time.tm_mday = SummerSeason.StopDay;
                    Time.tm_sec = 0;
                    Time.tm_min = 0;
                    Time.tm_hour = 0;

                    /* call mktime one more time to check stop date */
                    if(mktime (&Time) != -1)
                    {
                        /* check further */
                        if(Time.tm_mon == SummerSeason.StopMonth &&
                           Time.tm_mday == SummerSeason.StopDay)
                        {
                            /* all dates are valid so copy the temp start
                               to the real one to make it valid */
                            SummerSeason.StartMonth = TempStartMonth;
                        }
                    }
                }
            }
        }

        free (MyBuffer);
        /* print a message it not valid */
        if(SummerSeason.StartMonth == -1)
        {
            SendTextToLogger ("Inf", "Invalid Season Date(s)");
            return(FALSE);
        }
    }
    else if(SummerSeason.StartMonth == -1)
    {
        /* file was not there or dates were invalid */
        return(FALSE);
    }

    /* get the current month and day */
    UCTFTime (&TimeB);
    UCTLocoTime (TimeB.time, TimeB.dstflag, &Time);

    /* check if summer season has started yet */
    if(Time.tm_mon < SummerSeason.StartMonth)
    {
        return(SEASON_WINTER);
    }
    else if(Time.tm_mon == SummerSeason.StartMonth &&
            Time.tm_mday < SummerSeason.StartDay)
    {
        /* not to valid start day yet */
        return(SEASON_WINTER);
    }

    /* check if summer season has stopped yet */
    if(Time.tm_mon > SummerSeason.StopMonth)
    {
        /* past the summer month */
        return(SEASON_WINTER);
    }
    else if(Time.tm_mon == SummerSeason.StopMonth &&
            Time.tm_mday >= SummerSeason.StopDay)
    {
        /* day of month is past */
        return(SEASON_WINTER);
    }

    /* its summer season */
    return(SEASON_SUMMER);
}


/* Routine to save the time when DST changed */
IM_EX_CTIBASE INT PutDSTTime (ULONG TimeStamp)
{
    FILE *fptr;

    /* write to the file the time DST changed */
    if((fptr = fopen (DST_TIME_NAME, "w+")) != NULL)
    {
        fprintf (fptr,"%ld", TimeStamp);
        fclose (fptr);
        return(NORMAL);
    }

    return(SYSTEM);
}


/* Routine to get the time when DST changed */
IM_EX_CTIBASE INT getDSTTime (VOID)
{
    FILE *fptr;
    ULONG TimeStamp = 0L;

    if((fptr = fopen (DST_TIME_NAME,"r")) == NULL)
    {
        TimeStamp = 0;
    }
    else
    {
        fscanf (fptr,"%ld",TimeStamp);
        fclose (fptr);
    }

    return(TimeStamp);
}


