#include "yukon.h"
#include <windows.h>

#include "os2_2w32.h"
#include "cticalls.h"

// #include <btrapi.h>

#include <stdlib.h>
// #include "btrieve.h"
#include <stdio.h>
#include <string.h>
#include "dsm2.h"
#include "dsm2err.h"
#include "elogger.h"
#include "device.h"
#include "alarmlog.h"

main ()
{
    ALARM_SUM_STRUCT AlarmSumRecord;
    CTIPOINT PointRecord;

    /* Open up the program error log */
    InitAllAlarmLog ();


    strncpy (PointRecord.DeviceName, "ALARM TEST SUB     ", 20);
    strncpy (AlarmSumRecord.AlarmText, "TRIPPED            ", 18);
    AlarmSumRecord.StatusFlag = 0x8000;
    AlarmSumRecord.AcknowlegeFlag = 1;
    AlarmSumRecord.StateChangeCount = 1;
    AlarmSumRecord.LogCode = STATEALARM;
    PointRecord.AlarmDRPMask = 0;
    PointRecord.AlarmMask = 0L;

    fprintf(stderr,"*** DEBUG *** %s (%d): \n",__FILE__, __LINE__);

    strncpy (PointRecord.PointName, "BREAKER #1          ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #2          ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #3          ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #4          ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #5          ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #6          ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #7          ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #8          ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #9          ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #10         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #11         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #12         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #13         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #14         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #15         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #16         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #17         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #18         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #19         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #20         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #21         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #22         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    strncpy (PointRecord.PointName, "BREAKER #23         ", 20);
    AlarmSumRecord.TimeStamp = LongTime();
    LogAlarm (&AlarmSumRecord, CRITICALCLASS, FALSE, &PointRecord, 0x0001);

    fprintf(stderr,"*** DEBUG *** %s (%d): \n",__FILE__, __LINE__);

    /* Done so close the program error  */
    CloseAllAlarmLog ();

}
