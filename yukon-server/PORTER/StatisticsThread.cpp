#include "yukon.h"

#include "StatisticsManager.h"
#include "debug_timer.h"
#include "millisecond_timer.h"
#include "portglob.h"

namespace Cti {
namespace Porter {

StatisticsManager PorterStatisticsManager;

void StatisticsThread( void * )
{
    ThreadStatusKeeper threadKeeper("Statistics Thread");

    do
    {
        unsigned long StatisticsUpdateRate = gConfigParms.getValueAsULong("PORTER_DEVICESTATUPDATERATE", 3600L);

        CtiTime nextTime = nextScheduledTimeAlignedOnRate(CtiTime::now(), StatisticsUpdateRate);

        Timing::MillisecondTimer t;

        while( ! PorterQuit && CtiTime::now() < nextTime )
        {
            threadKeeper.monitorCheck(CtiThreadRegData::None);

            PorterStatisticsManager.processEvents(threadKeeper);

            const long elapsed = t.elapsed();

            if( elapsed < 1000 )
            {
                Sleep(1000 - elapsed);
            }

            t.reset();
        }

        if( PorterQuit )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " StatisticsThread: TID " << CurrentTID() << " recieved shutdown request." << endl;
        }

        {
            Timing::DebugTimer timer("Updating statistics");

            PorterStatisticsManager.processEvents(threadKeeper);

            PorterStatisticsManager.writeRecords(threadKeeper);
        }

    } while( ! PorterQuit );

    {
        CtiLockGuard<CtiLogger> doubt_guard(dout);
        dout << CtiTime() << " StatisticsThread: TID " << CurrentTID() << " exiting" << endl;
    }
}

}
}
