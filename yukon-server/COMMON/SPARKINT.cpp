#include <windows.h>       // These next few are required for Win32
#include "os2_2w32.h"
#include "cticalls.h"

#include <process.h>

#include <stdlib.h>
#include <stdio.h>


#include "dsm2.h"

#define SUICIDE_THREAD_STACK_SIZE 8192

HEV SuicideSem = (HEV) NULL;
HEV OkaySem = (HEV) NULL;

IM_EX_CTIBASE INT SparkRegister ()
{
    CHAR Name[20];
    ULONG Count;
    VOID SuicideThread (PVOID Arg);

    /* Attempt to open the process start thread */
    if (CTIOpenEventSem ("PSTRTSEM", &OkaySem, MUTEX_ALL_ACCESS)) {
        return (NORMAL);
    }

    CTICloseEventSem (&OkaySem);

    OkaySem = (HEV) NULL;

    /* Create the name of the Okay semaphore */
    sprintf (Name, "OKS%d", CurrentPID ());

    /* Attempt to open the OK semaphore */
    for (Count = 0; Count < 300; Count++) {
        if (!(CTIOpenEventSem (Name, &OkaySem, MUTEX_ALL_ACCESS))) {
            break;
        }
        CTISleep (100);
    }

    /* Check if we timed out */
    if (Count >= 300) {
        CTIExit (EXIT_PROCESS, -1);
    }

    /* Create the name of the Okay semaphore */
    sprintf (Name, "SUS%d", CurrentPID ());

    /* Attempt to open the Suicide Semaphore */
    if (CTIOpenEventSem (Name, &SuicideSem, MUTEX_ALL_ACCESS)) {
        printf ("Error Opening Suicide Semaphore\n");
        CTIExit (EXIT_PROCESS, -1);
    }

    /* Start the suicide thread */
    if (_beginthread (SuicideThread,
                      SUICIDE_THREAD_STACK_SIZE,
                      NULL) == -1) {
        CTIExit (EXIT_PROCESS, -1);
    }

    return (!NORMAL);
}


VOID SuicideThread (PVOID Arg)
{
    CTIWaitEventSem (SuicideSem,
                     SEM_INDEFINITE_WAIT);

    CTIExit (EXIT_PROCESS, -1);
}

IM_EX_CTIBASE INT ImOkay (VOID)
{
    CTIPostEventSem (OkaySem);

    return (NORMAL);
}


IM_EX_CTIBASE INT StartSparkThread (VOID)
{
    VOID SparkThread (PVOID);

    if (_beginthread (SparkThread,
                      8192,
                      NULL) == -1) {
        return (!NORMAL);
    }

    return (NORMAL);
}

VOID SparkThread (PVOID Arg)
{
    /* Up the priority of this one */
    CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, 10, 0);

    for (;;) {
        ImOkay ();
        CTISleep (5000L);
    }
}
