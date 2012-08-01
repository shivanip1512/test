#include "precompiled.h"

#include <stdlib.h>
#include <stdio.h>
#include <time.h>
#include <sys\timeb.h>
#include <string.h>
#include "dllbase.h"
#include "os2_2w32.h"
#include "cticalls.h"
#include "dsm2.h"
#include "elogger.h"
#include "logger.h"

#define DST_FILE_NAME    "DATA\\DSM2_DST.DAT"

typedef struct
{
    ULONG MyDSTFlag;
    LONG DSTFixOffset;
} _dstFlags_t;

_dstFlags_t *Flags = NULL;



int IM_EX_CTIBASE getUCTMemoryFlags (void);

void IM_EX_CTIBASE freeUCTMemory(void)
{
    if(Flags)
    {
        free(Flags);
        Flags = NULL;
    }
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
IM_EX_CTIBASE struct tm* UCTLocalTime (time_t Time, USHORT MyDSTFlag)
{
    struct tm* TheLocalTime;
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
            TheLocalTime = CtiTime::localtime_r (&Time);
        }
        else if(!(MyDSTFlag) && TheLocalTime->tm_isdst)
        {
            /* move ahead an hour */
            Time -= Flags->DSTFixOffset;
            TheLocalTime = CtiTime::localtime_r (&Time);
        }

        /* restore the dst flag */
        TheLocalTime->tm_isdst = MyDSTFlag;

        return(TheLocalTime);
    }

    return (NULL);


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
        memcpy (TStruct, MyTStruct, sizeof (TStruct));
    }

    /* restore the dst flag (in case it changed) */
    TStruct->tm_isdst = DST;

    return(MyTime);
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


