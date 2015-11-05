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

CtiTime nextMemoryReportTime;

bool isTimeToReportMemory(const CtiTime Now)
{
    if( nextMemoryReportTime < Now )
    {
        nextMemoryReportTime = nextScheduledTimeAlignedOnRate(Now, gMemoryReportIntervalSeconds);

        return true;
    }

    return false;
}

std::string reportPrivateBytes(const compileinfo_t &info)
{
    StreamBuffer buf;

    long long privateBytes = getPrivateBytes();

    buf << info.project << " memory use: " << CtiNumStr(privateBytes / 1048576.0, 1) << " MB (" << commaFormatted(privateBytes) << ")";

    return buf.extractToString();
}

/** Keep old accumulations around to calculate deltas */
static ULONGLONG oldCreationTime, oldKernelTime, oldUserTime, oldCurrentTime;
static CtiCriticalSection cpuTimeLock;
static int processorCount;

/** Calculate CPU Load based on processTimes().  Result is in percent. */
double getCPULoad()
{
    struct processTimes_t newTimes;
    ULONGLONG elapsedUserTime;
    ULONGLONG elapsedKernelTime;
    ULONGLONG elapsedCPUTime;
    ULONGLONG elapsedTime;
    double cpuLoad = 0.0;

    CtiLockGuard<CtiCriticalSection> lock(cpuTimeLock);

    newTimes=getProcessTimes();

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

/** Get processTimes as a string */
std::string reportProcessTimes(const compileinfo_t &info)
{
    processTimes_t times;
    StreamBuffer buf;

    times = getProcessTimes();

    unsigned long long elapsed = times.currentTime - times.creationTime;

    buf << info.project << " process times: "
        << CtiNumStr((double)elapsed / 10000000, 3) << "(e) "
        << CtiNumStr((double)times.kernelTime / 10000000, 3) << "(k) "
        << CtiNumStr((double)times.userTime / 10000000, 3) << "(u) ";

    return buf.extractToString();
}

/** Get Total processor time as a string */
std::string reportProcessorTimes()
{
    double cpuTotal=pdhGetCpuTotal();
    StreamBuffer buf;

    buf << "Total processor time: " << CtiNumStr(cpuTotal, 3) << "%";

    return buf.extractToString();
}


} // namespace Cti
