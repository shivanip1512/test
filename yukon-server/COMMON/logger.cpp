#include "precompiled.h"

#include "utility.h"
#include "cparms.h"
#include "dllbase.h"
#include "logger.h"
#include "numstr.h"
#include "ctidate.h"
#include "ctistring.h"

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
    _write_interval(5000),
    _days_to_keep(0)
{
    _current_stream = new strstream;
    _current_stream->fill(_fill);
}

void cleanupStream( strstream *stream )
{
    if( stream )
    {
        stream->freeze(false);

        delete stream;
    }
}

CtiLogger::~CtiLogger()
{
    cleanupStream(_current_stream);

    strstream *tmpStream;

    while( _queue.tryRead(tmpStream) )
    {
        cleanupStream(tmpStream);
    }
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

//  retain logs for 'days_to_keep' days:  0 == keep forever
CtiLogger& CtiLogger::setRetentionLength(const unsigned long days_to_keep)
{
    CtiLockGuard<CtiMutex> guard(_log_mux);

    _days_to_keep = days_to_keep;

    return *this;
}

void CtiLogger::flush()
{
    if( _current_stream )
    {
        const std::streamsize pcount = _current_stream->pcount();

        if( pcount > 0 )
        {
            if( pcount > 256 * 1024 )
            {
                std::auto_ptr<strstream> tmp(new strstream);

                *tmp << std::string(_current_stream->str(), 1024);
                *tmp << endl;
                *tmp << "Log entry trimmed due to excessive size(" << pcount << ")" << endl;
                *tmp << "Stack trace follows:" << endl;
                *tmp << autopsy_as_string(__FILE__,  __LINE__);

                delete _current_stream;
                _current_stream = 0;

                _queue.write(tmp.release());
            }
            else
            {
                _queue.write(_current_stream);
                _current_stream = 0;
            }

            if( _flush_mux.acquire(0) )
            {
                interrupt();
                _flush_mux.release();
            }
        }
        else if( pcount < 0 )
        {
            //  This is not legal in any of the 50 states, Puerto Rico, Guam, or anywhere else in the universe.
            delete _current_stream;
            _current_stream = 0;
        }
        else
        {
            _current_stream->rdbuf()->freeze(false);     // Unfreeze it, for further input...
        }
    }
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
            bool logfile_current = true;

            const CtiTime   now;

            if( now >= _next_logfile_check )
            {
                //  check at midnight or after 14 hours, whichever is closer...  this accomodates
                //    DST, since if we go ahead or behind by an hour, we'll still only check twice a day
                _next_logfile_check = now + min(secondsUntilMidnight(now), 14U * 60U * 60U);

                std::string new_log_filename = logFilename( CtiDate(now) );

                logfile_current = _today_filename == new_log_filename;

                if ( ! logfile_current )
                {
                    _today_filename = new_log_filename;
                }
            }

            if( !tryOpenOutputFile(outfile) )
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
            if( outfile && (_first_output || !logfile_current) )
            {
                strstream s;
                const CtiTime headerTime;

                if( !_project.empty() && !_version.empty() )
                {
                    s << headerTime << " --------  " << "(" << _project << " [Version " << _version << "])  --------" << endl;
                }

                if( _first_output )
                {
                    _first_output = false;

                    s << headerTime << " --------  LOG BEGINS  --------" << endl;
                }
                else
                {
                    s << headerTime << " --------  LOG CONTINUES (Running since " << _running_since << ")  --------" << endl;
                }

                outfile.write(s.str(), s.pcount());

                cleanupOldFiles();
            }

            int outputCount = 0;
            strstream* to_write;
            bool truncated_output_printed = false;
            while( _queue.tryRead(to_write) )
            {
                const int pcount = to_write->pcount();
                const bool oversize = pcount > 1024 * 4;

                const int n = std::min(pcount, 1024 * 4);

                if( n > 0 )
                {
                    // print to screen rate limiting. This always prints the first 100 lines then
                    // only prints the last 100 or so. This only applies to the console and helps
                    // console mode keep up.
                    if( _std_out )
                    {
                        if( ++outputCount < 100 || _queue.entries() < 100 )
                        {
                            int acquireloops = 0;

                            RWMutexLock::TryLockGuard guard(coutMux);

                            while( !guard.isAcquired() && acquireloops++ < 60)
                            {
                                Sleep(500L);

                                guard.tryAcquire();
                            }

                            cout.write( to_write->str(), n );
                            if( oversize )
                            {
                                cout << endl << " ******** Console Output Limited ******** " << endl;
                            }
                        }
                        else if( !truncated_output_printed )
                        {
                            truncated_output_printed = true;
                            cout << " ******** Console Output Truncated ******** " << endl;
                        }
                    }

                    if( outfile )
                    {
                        outfile.write( to_write->str(), n );
                        if( oversize )
                        {
                            outfile << endl << "********  Log entry limited to " << 1024 * 4 << " chars, was " << pcount << " chars ********" << endl;
                        }
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

std::string CtiLogger::logFilename(const CtiDate & date)
{
    ostringstream stream;

    stream << _path << "\\" << _base_filename;

    stream
        << date.year()
        << setfill('0') << setw(2) << date.month()
        << setfill('0') << setw(2) << date.dayOfMonth();

    stream << ".log";

    return stream.str();

}

unsigned CtiLogger::secondsUntilMidnight(const CtiTime & tm_now)
{
    unsigned seconds_until_midnight = 0;

    seconds_until_midnight += 24 - tm_now.hour();
    seconds_until_midnight *= 60;
    seconds_until_midnight -= tm_now.minute();
    seconds_until_midnight *= 60;
    seconds_until_midnight -= tm_now.second();

    return seconds_until_midnight;
}

bool CtiLogger::tryOpenOutputFile(ofstream& stream)
{
    stream.open( _today_filename.c_str(), ios::app );   // only append - never truncate

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

ostream& CtiLogger::operator<<(ostream& (*pf)(ostream&))       {  initStream();  *_current_stream << pf;  return *_current_stream;  }
ostream& CtiLogger::operator<<(ios_base& (*pf)(ios_base&))     {  initStream();  *_current_stream << pf;  return *_current_stream;  }
ostream& CtiLogger::operator<<(_Smanip<ios_base::fmtflags> &s) {  initStream();  *_current_stream << s;   return *_current_stream;  }

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

ostream& CtiLogger::operator<<(const CtiTime &r){  return operator<<(r.asString(CtiTime::Local, CtiTime::IncludeTimezone));  }

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

/// #ifdef _DEBUG
DWORD CtiLogger::lastAcquiredByTID() const
{
    return _log_mux.lastAcquiredByTID();
}
/// #endif


void CtiLogger::cleanupOldFiles()
{
    if ( _days_to_keep )
    {
        WIN32_FIND_DATA     found_file_info;
        const std::string   file_spec = _path + "\\" + _base_filename + "????????.log";

        CtiDate cutOffDate;
        cutOffDate -= _days_to_keep;

        HANDLE finder_handle = FindFirstFile( file_spec.c_str(), &found_file_info );

        if ( finder_handle != INVALID_HANDLE_VALUE )
        {
            std::string found_filename = _path + "\\" + found_file_info.cFileName;

            if ( shouldDeleteFile( found_filename, cutOffDate ) )
            {
                deleteOldFile( found_filename );
            }

            while ( FindNextFile( finder_handle, &found_file_info ) )
            {
                found_filename = _path + "\\" + found_file_info.cFileName;

                if ( shouldDeleteFile( found_filename, cutOffDate ) )
                {
                    deleteOldFile( found_filename );
                }
            }

            FindClose( finder_handle );
        }
    }
}


void CtiLogger::deleteOldFile( const std::string &file_to_delete )
{
    if ( DeleteFile( file_to_delete.c_str() ) )
    {
        std::cerr << "Error deleting old log file: " << file_to_delete << std::endl;
    }
}


bool CtiLogger::shouldDeleteFile( const std::string &file_to_delete, const CtiDate &cut_off )
{
    std::string base_filename( _base_filename );

    CtiToLower( base_filename );

    const std::string   date_regex_spec = "2\\d{7}";
    const std::string   file_regex_spec = "\\\\" + base_filename + date_regex_spec + "\\.log$";

    CtiString   input( file_to_delete );
    input.toLower();

    // Check if the filename portion is in the proper format -- '\filenameYYYYMMDD.log'

    CtiString   output = input.match( file_regex_spec );

    if ( output.empty() )
    {
        return false;
    }

    // Check the date format
    //  Previous 'file_regex_spec' match ensures a match here too.

    CtiString   date = output.match( date_regex_spec );

    unsigned long   date_num = std::strtoul( date.c_str(), NULL, 10 );

    unsigned    day = date_num % 100;
    date_num /= 100;
    unsigned    month = date_num % 100;
    unsigned    year = date_num / 100;

    CtiDate     fileDate( day, month, year );   // Any invalid params set us to Jan 01, 1970

    if ( fileDate < CtiDate( 1, 1, 2000 ) || fileDate > CtiDate( 31, 12, 2035 ) )
    {
        return false;
    }

    return fileDate < cut_off;
}

