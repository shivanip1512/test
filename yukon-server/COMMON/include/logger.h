#pragma once

#ifdef _WINDOWS
    
    #if !defined (NOMINMAX)
    #define NOMINMAX
    #endif

    #include <windows.h>
#endif

#include <rw/thr/prodcons.h>

#include <sys/types.h>
#include <sys/stat.h>
#include <time.h>

#include <limits>
#include <sstream>
#include <strstream>
#include <iostream>
#include <iomanip>
#include <fstream>
#include <string>


#include "dlldefs.h"
#include "thread.h"
#include "mutex.h"
#include "ctitime.h"
#include "CtiPCPtrQueue.h"
#include "utility.h"

class CtiDate;

class IM_EX_CTIBASE CtiLogger : public CtiThread
{
public:

    CtiLogger(const std::string& file = "", bool to_stdout = true);
    virtual ~CtiLogger();

    CtiLogger& setOutputPath(const std::string& path);
    CtiLogger& setOutputFile(const std::string& file);
    CtiLogger& setOwnerInfo(const compileinfo_t &ownerinfo);
    CtiLogger& setWriteInterval(long millis);
    CtiLogger& setToStdOut(bool to_stdout);
    CtiLogger& setRetentionLength(const unsigned long days_to_keep);

    //Immediately write out everything in the queue waiting
    // to be written
    CtiLogger& write();

    //Immediately move everything to the queue for writing
    CtiLogger& flush();

    bool acquire(unsigned long millis);
    CtiLogger& acquire();
    CtiLogger& release();

    std::ostream& operator<<(std::ios_base& (*pf)(std::ios_base&));
    std::ostream& operator<<(std::ostream& (*pf)(std::ostream&));
    std::ostream& operator<<(std::_Smanip<std::ios_base::fmtflags> &s);
    std::ostream& operator<<(const char *s);
    std::ostream& operator<<(char c);
    std::ostream& operator<<(bool n);
    std::ostream& operator<<(short n);
    std::ostream& operator<<(unsigned short n);
    std::ostream& operator<<(int n);
    std::ostream& operator<<(unsigned int n);
    std::ostream& operator<<(long n);
    std::ostream& operator<<(unsigned long n);
    std::ostream& operator<<(float n);
    std::ostream& operator<<(double n);
    std::ostream& operator<<(long double n);
    std::ostream& operator<<(void * n);
    std::ostream& operator<<(const std::string& s);
    std::ostream& operator<<(const CtiTime &r);

    char fill(char cfill);
    char fill() const;

/// #ifdef _DEBUG
    DWORD lastAcquiredByTID() const;
/// #endif

protected:
    void run();

    static std::string   scrub(std::string filename);
    static unsigned secondsUntilMidnight(const CtiTime & tm_now);

    std::string logFilename(const CtiDate & date);

private:
    char    _fill;

    std::string  _path, _base_filename, _today_filename;
    CtiTime  _next_logfile_check;

    bool    _first_output;

    CtiTime _running_since;

    std::string  _project, _version;

    volatile long _write_interval;
    volatile bool _std_out;

    CtiPCPtrQueue<std::strstream> _queue;
    CtiMutex _log_mux;
    CtiMutex _flush_mux;

    std::strstream* _current_stream;

    unsigned long _days_to_keep;

    void doOutput();
    void initStream();
    bool tryOpenOutputFile(std::ofstream& stream);

    void cleanupOldFiles();
    void deleteOldFile( const std::string &file_to_delete );

protected:
    bool shouldDeleteFile( const std::string &file_to_delete, const std::string &cut_off );
};


IM_EX_CTIBASE extern CtiLogger   dout;       // Global instance
IM_EX_CTIBASE extern CtiLogger   slog;       // Global instance. Simulator log
IM_EX_CTIBASE extern CtiLogger   blog;       // Global instance. Simulator log


