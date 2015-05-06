#include "precompiled.h"

#include "mc_fileint.h"
#include "mc_msg.h"
#include "dllbase.h"
#include "ctidate.h"
#include "utility.h"
#include "errno.h"

using namespace std;

/*
    Directory where processed files go
*/
const string CtiMCFileInterface::getConsumedDirectory() const
{
    return _consumed_dir;
}

CtiMCFileInterface& CtiMCFileInterface::setConsumedDirectory(const string& dir)
{
    _consumed_dir = dir;
    return *this;
}

/*
*/
void CtiMCFileInterface::start()
{
    if( mkdir(_consumed_dir.c_str()) < 0 )
    {
        if( errno != EEXIST )
        {
            CTILOG_ERROR(dout, "Could not create directory: "<< _consumed_dir <<" - Processed/consumed files will be deleted.");
        }
    }

    // make sure to let our super start
    Cti::FileInterface::start();
}
/*---------------------------------------------------------------------------
    handleFile

    Handles a newly found file.  Called from our super class.
---------------------------------------------------------------------------*/
void CtiMCFileInterface::handleFile(const string& filename )
{
    FILE* fptr;
    char buf[MC_FILE_BUF_SIZE] = {0};
    char* sep_ptr;
    int line = 0;

    if( (fptr = fopen( filename.c_str(), "r" ) ) == NULL )
    {
        CTILOG_ERROR(dout, "Could not open file: "<< filename);

        return;
    }

    while( !feof(fptr) )
    {
        fgets( buf, MC_FILE_BUF_SIZE, fptr);
        line++;

        if( *buf == 0 )
            break;

        //Eliminate non-printables they cause trouble - assuming non-alphas are at the end
        for( int i = 0; i < MC_FILE_BUF_SIZE; i++ )
        {
            if( !isprint( buf[i] ) )
                buf[i] = 0;
        }

        //Locate the comma and set it to zero - then we have two strings
        sep_ptr = strchr(buf, ',');


        if( sep_ptr == NULL )
        {
            //Didn't find a comma?  Look for an =
            sep_ptr = strchr(buf, '=');
        }

        // Could be a bogus line
        if( sep_ptr == NULL )
        {
            CTILOG_ERROR(dout, "An error occurred at line: "<< line <<" in file: "<< filename);
            continue;
        }


        *sep_ptr = NULL;

        string function(buf);
        string name(sep_ptr+1);

        name = trim(name);

        execute( function, name );

        //sometimes fgets returns without affecting buf
        //this causes some lines to be interpreted twice -
        //zeroing out buf will avoid this
        memset( buf, 0, MC_FILE_BUF_SIZE );
    }

    fclose( fptr );

    CtiTime now_time;
    CtiDate now_date(now_time);

    // Attempt to copy the file into the consumed directory
    ostringstream consume_file;

    consume_file << _consumed_dir << "\\" << filename;

    consume_file << setfill('0');
    consume_file << " ";
    consume_file << setw(2) << now_date.year();
    consume_file << setw(2) << now_date.month();
    consume_file << setw(2) << now_date.dayOfMonth();
    consume_file << " ";
    consume_file << setw(2) << now_time.hour();
    consume_file << setw(2) << now_time.minute();
    consume_file << setw(2) << now_time.second();

    if( CopyFile(filename.c_str(), consume_file.str().c_str(), FALSE) == 0 )
    {
        CTILOG_ERROR(dout, "Failed copying processed file "<< filename <<" to "<< consume_file);
    }

    return;
}
/*----------------------------------------------------------------------------
  setQueue

  Messages will be put on this queue for procssing when they are generated
----------------------------------------------------------------------------*/
void CtiMCFileInterface::setQueue( CtiQueue< CtiMessage, std::greater<CtiMessage> >* q )
{
    _message_queue = q;
}

/*---------------------------------------------------------------------------
    execute

    Creates the appropriate executor and executes it.
---------------------------------------------------------------------------*/
void CtiMCFileInterface::execute(const string& function, const string& name )
{

    //attempt to find the id of the schedule
    long id = -1;
    {
        CtiLockGuard<CtiMutex> guard( _schedule_manager.getMux() );

        id = _schedule_manager.getID( string( name.c_str() ));

        if( id == -1 )
        {
            CTILOG_ERROR(dout, "Unable to locate a schedule id for schedule named: "<< name);

            return;
        }
    }

    //Determine which function
    CtiMCOverrideRequest* msg = NULL;
    string lower_function = function;
    std::transform(lower_function.begin(), lower_function.end(), lower_function.begin(), tolower);


    if( lower_function == "start" ||
        lower_function == "$start" )
    {
        msg = new CtiMCOverrideRequest();
        msg->setAction( CtiMCOverrideRequest::Start );
        msg->setStartTime( CtiTime::now() );
    }
    else
    if( lower_function == "stop" ||
        lower_function == "$stop" )
    {
        msg = new CtiMCOverrideRequest();
        msg->setAction( CtiMCOverrideRequest::Stop );
        msg->setStopTime( CtiTime::now() );
    }
    else
    if( lower_function == "enable" ||
        lower_function == "$enable" )
    {
        msg = new CtiMCOverrideRequest();
        msg->setAction( CtiMCOverrideRequest::Enable );
    }
    else
    if( lower_function == "disable" ||
        lower_function == "$disable" )
    {
        msg = new CtiMCOverrideRequest();
        msg->setAction( CtiMCOverrideRequest::Disable );
    }
    else
    {
        CTILOG_ERROR(dout, "Unknown function: "<< function <<" for schedule name: "<< name);
    }

    // Write out the command/message if there is one
    if( msg != NULL )
    {
        if( _message_queue != NULL )
        {
            msg->setID(id);
            msg->setUser(MC_FTP_INTERFACE_USER);

            _message_queue->putQueue(msg);

            if( gMacsDebugLevel & MC_DEBUG_FILEINT )
            {
                Cti::FormattedList loglist;
                loglist.add("Function") << lower_function;
                loglist.add("Schedule") << name;

                CTILOG_DEBUG(dout, "Placed a command on the main queue: "<<
                        loglist);
            }
        }
        else
        {
            //clean up the msg, nowhere to send it
            delete msg;
        }
    }

    return;
}

