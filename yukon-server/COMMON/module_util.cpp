#include "precompiled.h"

#include "module_util.h"
#include "win_helper.h"
#include "logger.h"
#include "dllbase.h"
#include "guard.h"

using std::endl;

namespace Cti {

/**
 *  Used by the logging to identify each module at startup
 */
void identifyProject(const compileinfo_t &info)
{
    if(isDebugLudicrous() && info.date)      // DEBUGLEVEL added 012903 CGP
    {
        CTILOG_INFO(dout, info.project <<" [Version "<< info.version <<"]"<<" [Version Details: "<< info.details <<", "<< info.date <<"]");
    }
    else
    {
        CTILOG_INFO(dout, info.project <<" [Version "<< info.version <<"]"<<" [Version Details: "<< info.details <<"]");
    }
}

/**
 *  Attempt to set the title of the console window
 *  If it returns false, the program is not running in a window
 */
bool setConsoleTitle(const compileinfo_t &info)
{
    std::ostringstream s;

    s << info.project << " - YUKON " << info.version;

    return SetConsoleTitle( s.str().c_str() );
}

/**
 * Create an exclusive event with default arguments.
 *
 * @return the event handle,
 *         NULL if another event with the same name already exist
 *
 * @throw  std::runtime_error if CreateEvent fails
 */
HANDLE createExclusiveEvent(const char *eventName)
{
    return createExclusiveEvent(true,  // manual reset by default
                                false, // initial state is false
                                eventName);
}

/**
 * Create an exclusive event.
 *
 * @return the event handle,
 *         NULL if another event with the same name already exist
 *
 * @throw  std::runtime_error if CreateEvent fails
 */
HANDLE createExclusiveEvent(bool manualReset,
                            bool initialState,
                            const char *eventName)
{
    const HANDLE hExclusion = CreateEvent(NULL, manualReset, initialState, eventName);

    if( hExclusion == NULL )
    {
        const DWORD error = GetLastError();
        std::ostringstream msg;
        msg <<"failed to create event \""<< eventName <<"\" due to error "<< error <<" / "<< getSystemErrorMessage(error);
        throw std::runtime_error(msg.str());
    }

    if( GetLastError() == ERROR_ALREADY_EXISTS )
    {
        CloseHandle(hExclusion);
        return NULL;
    }

    return hExclusion;
}

/** Report System Metrics when the timer expires */
void reportSystemMetrics( const compileinfo_t &info )
{
    static CtiTime nextMemoryReportTime;

    CtiTime now = CtiTime::now();
    if(nextMemoryReportTime < now)
    {
        nextMemoryReportTime = nextScheduledTimeAlignedOnRate( now, gMemoryReportIntervalSeconds );

        StreamBuffer buf;

        long long privateBytes = getPrivateBytes();
        buf << info.project << " memory use: " << CtiNumStr( privateBytes / 1024.0 / 1024, 1 ) << " MB (" 
            << commaFormatted( privateBytes ) << ")";
        CTILOG_INFO( dout, buf.extractToString() );

        const auto times = getProcessTimes();
        unsigned long long elapsed = times.currentTime - times.creationTime;
        buf << info.project << " process times: "
            << CtiNumStr( (double)elapsed / 10000000, 3 ) << "(e) "
            << CtiNumStr( (double)times.kernelTime / 10000000, 3 ) << "(k) "
            << CtiNumStr( (double)times.userTime / 10000000, 3 ) << "(u) ";
        CTILOG_INFO( dout, buf.extractToString() );

        double cpuTotal = pdhGetCpuTotal();
        buf << "Total processor time: " << CtiNumStr( cpuTotal, 3 ) << "%";
        CTILOG_INFO( dout, buf.extractToString());

        EAS_DBG_CHECK
    }
}

/** Calculate CPU Load based on processTimes().  Result is in percent. */
double getCPULoad()
{
    /** Keep old accumulations around to calculate deltas */
    static ULONGLONG oldCreationTime, oldKernelTime, oldUserTime, oldCurrentTime;
    static CtiCriticalSection cpuTimeLock;
    static int processorCount;

    ULONGLONG elapsedUserTime;
    ULONGLONG elapsedKernelTime;
    ULONGLONG elapsedCPUTime;
    ULONGLONG elapsedTime;
    double cpuLoad = 0.0;

    CtiLockGuard<CtiCriticalSection> lock(cpuTimeLock);

    const auto newTimes=getProcessTimes();

    if(oldCurrentTime == 0) // First time through?
    {
        std::string processorCountString(getenv("NUMBER_OF_PROCESSORS"));
        processorCount = std::stoi(processorCountString);
    }
    else
    {
        // Times reported are in 100 ns increments.  We convert to uS
        elapsedUserTime = (newTimes.userTime - oldUserTime)/10;
        elapsedKernelTime = (newTimes.kernelTime - oldKernelTime)/10;
        elapsedCPUTime = elapsedKernelTime + elapsedUserTime;

        elapsedTime = (newTimes.currentTime - oldCurrentTime)/10;
        cpuLoad = (double)elapsedCPUTime / (double)elapsedTime;
    }

    oldCreationTime = newTimes.creationTime;
    oldKernelTime = newTimes.kernelTime;
    oldUserTime = newTimes.userTime;
    oldCurrentTime = newTimes.currentTime;

    return cpuLoad/processorCount*100;  // Handle multiple cores & Dont forget this is in percent.
}

//std::ostream &operator<<(std::ostream &o, const ::Cti::CallSite &cs)
//{
//    return o << "( called from " << cs.file << " : " << cs.line << " )";
//}

//StreamBufferSink &operator<<(StreamBufferSink  &o, const ::Cti::CallSite &cs)
//{
//    return o << "( called from " << cs.file << " : " << cs.line << " )";
//}


} // namespace Cti
