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
* REVISION     :  $Revision: 1.20 $
* DATE         :  $Date: 2007/07/10 20:53:35 $
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
    _new_day(true),
    _first_output(true),
    _base_filename(file),
    _day_of_month(-1),
    _std_out(to_stdout),
    _path("..\\log"),
    _fillChar(' '),
    _write_interval(5000)
{
    _current_stream = new strstream;
    _current_stream->fill(_fillChar);
}

CtiLogger::~CtiLogger()
{
    if( _current_stream != NULL )
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
    _write_interval = millis;
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
    if( gConfigParms.isOpt("LOG_MAXPRIORITY") && !stricmp(gConfigParms.getValueAsString("LOG_MAXPRIORITY").data(), "TRUE") )
    {
        {
            CtiLockGuard<CtiMutex> guard(_log_mux);
            *this << "*********************" << endl;
            *this << "Logging is occuring at max priority.  May affect port operations."  << endl;
            *this << "   Use cparm LOG_MAXPRIORITY : TRUE/FALSE" << endl;
            *this << "*********************" << endl << endl;
        }

        CTISetPriority (PRTYS_THREAD, PRTYC_TIMECRITICAL, 31, 0);
    }

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

        if( _queue.entries() > 0 )
            doOutput();
    }

    if( _queue.entries() > 0 )
        doOutput();
}

CtiLogger& CtiLogger::setToStdOut(bool to_stdout)
{
    _std_out = to_stdout;
    return *this;
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

ostream& CtiLogger::operator<<(ostream& (*pf)(ostream&))
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << pf;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(ios_base& (*pf)(ios_base&))
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << pf;
    return *_current_stream;
}


void CtiLogger::doOutput()
{
    static bool nag = false;
    ofstream outfile;
    string todays_file_name;

    try
    {
        if( !_base_filename.empty() )
        {
            {
                CtiLockGuard<CtiMutex> guard(_log_mux);

                _new_day = verifyTodayFileName();

                if( _today_filename.length() > 0 && !tryOpenOutputFile( outfile, _path + "\\" + _today_filename) )
                {
                    if(!nag)
                    {
                        nag = true;
                        RWMutexLock::LockGuard  guard(coutMux);
                        cout << endl << "*********************" << endl;
                        cout << "ERROR!  Unable to open output file " << _path + "\\" + _today_filename << endl;
                        cout << "   Use cparm LOG_DIRECTORY to alter the logfile path" << endl;
                        cout << "*********************" << endl << endl;
                    }
                }
            }
            strstream* to_write;
            while( _queue.tryRead(to_write) )
            {
                int n;
                if( (n=to_write->pcount()) > 0 )
                {
                    if( _std_out )
                    {
                        int acquireloops = 0;

                        RWMutexLock::TryLockGuard guard(coutMux);
                        while( !guard.isAcquired() && acquireloops++ < 60)
                        {
                            if(guard.tryAcquire()) break;
                            Sleep(500L);
                        }

                        cout.write( to_write->str(), n );
                    }

                    if( outfile )
                    {
                        if( _new_day )
                        {
                            _new_day = false;

                            strstream s;
                            CtiTime now;

                            if( !_project.empty() && !_version.empty() )
                            {
                                s << now << " --------  " << "(" << _project << " [Version " << _version << "])  --------" << endl;
                            }

                            if( _first_output )
                            {
                                _first_output = false;

                                s << now << " --------  LOG BEGINS  --------\n";
                            }
                            else
                            {
                                s << now << " --------  LOG CONTINUES (Running since " << _running_since << ")  --------\n";
                            }

                            outfile.write(s.str(), s.pcount());
                        }

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

bool CtiLogger::verifyTodayFileName()
{
    bool new_day = false;

    time_t ltime;
    // convert the day of the month into a string
    ::time(&ltime);

    ostringstream itos_buffer;

    int daynum = CtiTime::localtime_r(&ltime)->tm_mday;

    if( _day_of_month != daynum )
    {
        new_day = true;

        _day_of_month = daynum;

        if( daynum < 10 )  itos_buffer << "0";

        itos_buffer << daynum;

        _today_filename = _base_filename + itos_buffer.str() + ".log";
    }

    return new_day;
}


string CtiLogger::scrub(const string& filename)
{
    string tmp = filename;

    //  this only gets called once per file per day, so it's not too expensive
    for(int i = 0; i < tmp.length(); i++)
    {
        //  if the character is not a-z, A-Z, 0-9, '-', or '_', scrub it to an underscore
        if( !(tmp[i] >= 'a' && tmp[i] <= 'z') && !(tmp[i] >= 'A' && tmp[i] <= 'Z') &&
            !(tmp[i] >= '0' && tmp[i] <= '9') &&
            !(tmp[i] == '-') && !(tmp[i] == '_') && !(tmp[i] == ' ') )
        {
            tmp[i] = '_';
        }
    }

    return tmp;
}


bool CtiLogger::tryOpenOutputFile(ofstream& strm, const string& file)
{
    int cur_month;
    time_t ltime;
    struct _stat file_stat;

    ::time( &ltime );
    cur_month = CtiTime::localtime_r(&ltime)->tm_mon;

    if( _stat(file.c_str(), &file_stat) != -1 &&
        CtiTime::localtime_r(&file_stat.st_mtime)->tm_mon == cur_month )
    {
        //the file exists and was last modified this month
        //open to append
        strm.open( file.c_str(), ios::app );
    }
    else
    {
        //file either doesn't exist or hasn't been modified this month
        //open with default to truncate
        strm.open( file.c_str() );
    }

    return(bool) strm;
}

ostream& CtiLogger::operator<<(const char *s)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << s;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(char c)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << c;
    return *_current_stream;
}
ostream& CtiLogger::operator<<(bool n)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << n;
    return *_current_stream;
}
ostream& CtiLogger::operator<<(short n)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << n;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(unsigned short n)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << n;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(int n)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << n;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(unsigned int n)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << n;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(long n)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << n;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(unsigned long n)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << n;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(float n)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << n;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(double n)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << n;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(long double n)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << n;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(void * n)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << n;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(const string& s)
{
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    *_current_stream << s;
    return *_current_stream;
}

ostream& CtiLogger::operator<<(const CtiTime &r)
{
    return operator<<(r.asString());
}

char CtiLogger::fill(char cfill)
{
    _fillChar = cfill;
    if( _current_stream == NULL )
    {
        _current_stream = new strstream;
        _current_stream->fill(_fillChar);
    }
    return _current_stream->fill(cfill);
}
char CtiLogger::fill() const
{
    if( _current_stream != NULL )
    {
        return _current_stream->fill();
    }
    else
    {
        return _fillChar;
    }
}

#ifdef _DEBUG
DWORD CtiLogger::lastAcquiredByTID() const
{
    return _log_mux.lastAcquiredByTID();
}
#endif

