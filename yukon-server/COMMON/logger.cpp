/*-----------------------------------------------------------------------------*
*
* File:   logger
*
* Date:   2/19/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/COMMON/logger.cpp-arc  $
* REVISION     :  $Revision: 1.22 $
* DATE         :  $Date: 2008/10/13 21:23:23 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "utility.h"
#include "cparms.h"
#include "dllbase.h"
#include "logger.h"
#include "numstr.h"

using namespace std;

IM_EX_CTIBASE CtiLogger   dout;           // Global log
IM_EX_CTIBASE CtiLogger   slog;           // Global instance. Simulator log
IM_EX_CTIBASE CtiLogger   blog;           // Global instance. Statistics


CtiLogger::CtiLogger(const string& file, bool to_stdout) :
    _first_output(true),
    _base_filename(file),
    _std_out(to_stdout),
    _path("..\\log"),
    _fill(' '),
    _write_interval(5000)
{
    _current_stream = new strstream;
    _current_stream->fill(_fill);
}

CtiLogger::~CtiLogger()
{
    delete _current_stream;
}

CtiLogger& CtiLogger::setOutputPath(const string& path)
{
    CtiLockGuard<CtiMutex> guard(_log_mux);

    _path = path;

    return *this;
}

CtiLogger& CtiLogger::setOutputFile(const string& file)
{
    CtiLockGuard<CtiMutex> guard(_log_mux);

    _base_filename = scrub(file);

    return *this;
}

CtiLogger& CtiLogger::setOwnerInfo(const compileinfo_t &ownerinfo)
{
    CtiLockGuard<CtiMutex> guard(_log_mux);

    _project = ownerinfo.project;
    _version = ownerinfo.version;

    return *this;
}

CtiLogger& CtiLogger::setWriteInterval(long millis)
{
    CtiLockGuard<CtiMutex> guard(_log_mux);

    _write_interval = millis;

    return *this;
}

CtiLogger& CtiLogger::setToStdOut(bool to_stdout)
{
    CtiLockGuard<CtiMutex> guard(_log_mux);

    _std_out = to_stdout;

    return *this;
}

CtiLogger& CtiLogger::flush()
{
    if( _current_stream != NULL && (_current_stream->pcount()) > 0 )           // Only do this if there is data there. 081001 CGP
    {
        _queue.write(_current_stream);
        _current_stream = NULL;

        if( _flush_mux.acquire(0) )
        {
            interrupt();
            _flush_mux.release();
        }
    }
    else if( _current_stream != NULL )
    {
        _current_stream->rdbuf()->freeze(false);     // Unfreeze it, for further input...
    }

    return *this;
}
CtiLogger& CtiLogger::write()
{
    interrupt();
    return *this;
}

void CtiLogger::run()
{
    SetThreadName(-1, "Logger   ");

    while( !isSet(SHUTDOWN) )
    {
        // consider anything less than 1000 millis to
        // be immediate
        if( _write_interval < 1000 )
        {
            sleep( numeric_limits<int>::max() );
        }
        else
        {
            // Acquire _flush_mux...
            // when it is acquired, calls to flush()
            // will NOT cause a call to interrupt

            CtiLockGuard<CtiMutex> guard(_flush_mux);
            sleep(_write_interval);
        }

        if( !_queue.empty() )
        {
            doOutput();
        }
    }

    if( !_queue.empty() )
    {
        doOutput();
    }
}

CtiLogger& CtiLogger::acquire()
{
#ifdef _DEBUG

    bool isacq = false;
    do
    {
        isacq = _log_mux.acquire(300000);       // Five minutes seems like infinite anyway!

        if(!isacq)
        {
            cerr << CtiTime() << " logger mutex is unable to be locked down for thread id: " << GetCurrentThreadId() << endl;
            cerr << "  Thread TID=" << _log_mux.lastAcquiredByTID() << " was the last to acquire " << endl;
        }

    } while (!isacq);
#else
    _log_mux.acquire();
#endif
    return *this;
}

CtiLogger& CtiLogger::release()
{
    flush();
    _log_mux.release();
    return *this;
}

bool CtiLogger::acquire(unsigned long millis)
{
    return _log_mux.acquire(millis);
}


void CtiLogger::doOutput()
{
    ofstream outfile;

    try
    {
        if( !_base_filename.empty() )
        {
            bool append = isExistingLogFileCurrent();

            if( !tryOpenOutputFile(outfile, append) )
            {
                static bool nag = false;

                if(!nag)
                {
                    nag = true;
                    RWMutexLock::LockGuard  guard(coutMux);
                    cout << endl << "*********************" << endl;
                    cout << "ERROR!  Unable to open output file " << _today_filename << endl;
                    cout << "   Use cparm LOG_DIRECTORY to alter the logfile path" << endl;
                    cout << "*********************" << endl << endl;
                }
            }

            //  first time we've written to this file - write the headers
            if( outfile && (_first_output || !append) )
            {
                strstream s;
                CtiTime now;

                if( !_project.empty() && !_version.empty() )
                {
                    s << now << " --------  " << "(" << _project << " [Version " << _version << "])  --------" << endl;
                }

                if( _first_output )
                {
                    _first_output = false;

                    s << now << " --------  LOG BEGINS  --------" << endl;
                }
                else
                {
                    s << now << " --------  LOG CONTINUES (Running since " << _running_since << ")  --------" << endl;
                }

                outfile.write(s.str(), s.pcount());
            }

            strstream* to_write;
            while( _queue.tryRead(to_write) )
            {
                int n = to_write->pcount();

                if( n > 0 )
                {
                    if( _std_out )
                    {
                        int acquireloops = 0;

                        RWMutexLock::TryLockGuard guard(coutMux);

                        while( !guard.isAcquired() && acquireloops++ < 60)
                        {
                            Sleep(500L);

                            guard.tryAcquire();
                        }

                        cout.write( to_write->str(), n );
                    }

                    if( outfile )
                    {
                        outfile.write( to_write->str(), n );
                    }
                }

                // unfreeze to_write's buffer
                to_write->rdbuf()->freeze(false);

                delete to_write;
            }
            outfile.close();
        }
    }
    catch(...)                          // 081001 CGP I dunno.
    {
        if( outfile.is_open() )
        {
            outfile.close();
            {
                cerr << "*********************" << endl;
                cerr << "EXCEPTION in LOGGER " << _base_filename << endl;
                cerr << "*********************" << endl;
            }
        }
    }
}


string CtiLogger::scrub(string filename)
{
    //  this only gets called once per file per day, so it's not too expensive
    for(int i = 0; i < filename.length(); i++)
    {
        //  if the character is not a-z, A-Z, 0-9, '-', or '_', scrub it to an underscore
        if( !(isalnum(filename[i]) || filename[i] == '-' || filename[i] == '_') )
        {
            filename[i] = '_';
        }
    }

    return filename;
}


void CtiLogger::setTodayFilename(unsigned day_of_month)
{
    ostringstream stream;

    stream << _path << "\\" << _base_filename;

    ((day_of_month < 10)?(stream << "0"):(stream)) << day_of_month;

    stream << ".log";

    _today_filename = stream.str();
}


bool CtiLogger::isExistingLogFileCurrent()
{
    time_t now = ::time(0);

    if( _next_filetime_check.seconds() <= now )
    {
        tm tm_now = *(CtiTime::localtime_r(&now));

        unsigned seconds_to_midnight = 0;
        seconds_to_midnight += 24 - tm_now.tm_hour;
        seconds_to_midnight *= 60;
        seconds_to_midnight -= tm_now.tm_min;
        seconds_to_midnight *= 60;
        seconds_to_midnight -= tm_now.tm_sec;

        //  check at midnight or after 12 hours, whichever is closer...  this accomodates DST
        _next_filetime_check = now + min(seconds_to_midnight, 12U * 60U * 60U);

        setTodayFilename(tm_now.tm_mday);

        struct _stat file_stat;

        //  if the file doesn't exist or the month doesn't match, overwrite it
        if( _stat(_today_filename.c_str(), &file_stat) == -1 ||
            CtiTime::localtime_r(&file_stat.st_mtime)->tm_mon != tm_now.tm_mon )
        {
            return false;
        }
    }

    return true;
}


bool CtiLogger::tryOpenOutputFile(ofstream& stream, bool append)
{
    if( append )
    {
        stream.open( _today_filename.c_str(), ios::app );
    }
    else
    {
        //file either doesn't exist or hasn't been modified this month
        //open with default to truncate
        stream.open( _today_filename.c_str() );
    }

    return stream;
}

void CtiLogger::initStream()
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fill);
    }
}

ostream& CtiLogger::operator<<(ostream& (*pf)(ostream&))  {  initStream();  *_current_stream << pf;  return *_current_stream;  }
ostream& CtiLogger::operator<<(ios_base& (*pf)(ios_base&)){  initStream();  *_current_stream << pf;  return *_current_stream;  }

ostream& CtiLogger::operator<<(const char *s)   {  initStream();  *_current_stream << s;  return *_current_stream;  }
ostream& CtiLogger::operator<<(char c)          {  initStream();  *_current_stream << c;  return *_current_stream;  }
ostream& CtiLogger::operator<<(bool n)          {  initStream();  *_current_stream << n;  return *_current_stream;  }
ostream& CtiLogger::operator<<(short n)         {  initStream();  *_current_stream << n;  return *_current_stream;  }
ostream& CtiLogger::operator<<(unsigned short n){  initStream();  *_current_stream << n;  return *_current_stream;  }
ostream& CtiLogger::operator<<(int n)           {  initStream();  *_current_stream << n;  return *_current_stream;  }
ostream& CtiLogger::operator<<(unsigned int n)  {  initStream();  *_current_stream << n;  return *_current_stream;  }
ostream& CtiLogger::operator<<(long n)          {  initStream();  *_current_stream << n;  return *_current_stream;  }
ostream& CtiLogger::operator<<(unsigned long n) {  initStream();  *_current_stream << n;  return *_current_stream;  }
ostream& CtiLogger::operator<<(float n)         {  initStream();  *_current_stream << n;  return *_current_stream;  }
ostream& CtiLogger::operator<<(double n)        {  initStream();  *_current_stream << n;  return *_current_stream;  }
ostream& CtiLogger::operator<<(long double n)   {  initStream();  *_current_stream << n;  return *_current_stream;  }
ostream& CtiLogger::operator<<(void * n)        {  initStream();  *_current_stream << n;  return *_current_stream;  }
ostream& CtiLogger::operator<<(const string& s) {  initStream();  *_current_stream << s;  return *_current_stream;  }

ostream& CtiLogger::operator<<(const CtiTime &r){  return operator<<(r.asString());  }

char CtiLogger::fill(char cfill)
{
    _fill = cfill;
    initStream();
    return _current_stream->fill(_fill);
}
char CtiLogger::fill() const
{
    if( _current_stream != NULL )
    {
        return _current_stream->fill();
    }
    else
    {
        return _fill;
    }
}

#ifdef _DEBUG
DWORD CtiLogger::lastAcquiredByTID() const
{
    return _log_mux.lastAcquiredByTID();
}
#endif

