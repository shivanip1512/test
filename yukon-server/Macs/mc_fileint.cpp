/*-----------------------------------------------------------------------------*
*
* File:   mc_fileint
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_fileint.cpp-arc  $
* REVISION     :  $Revision: 1.10.10.1 $
* DATE         :  $Date: 2008/11/21 20:56:59 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

/*-----------------------------------------------------------------------------
    Filename:  mc_fileint.cpp

    Programmer:  Aaron Lauinger

    Description:    Source file for CtiMCFileInterface.


    Initial Date:  6/29/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "mc_fileint.h"
#include "mc_msg.h"
#include "ctibase.h"
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
    if( mkdir(_consumed_dir.c_str()) < 0 ) {
        if( errno != EEXIST ) {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " File Interface: An error occured creating directory:  " << _consumed_dir << endl;
            dout << CtiTime() << " File Interface: Processed/consumed files will be deleted " << endl;
        }
    }

    // make sure to let our super start
    CtiFileInterface::start();
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
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << CtiTime() << " File Interface: An error occured opening file:  " << filename << endl;
        }

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
            {
                CtiLockGuard< CtiLogger > guard(dout);
                dout << CtiTime() << " File Interface: An error occured at line: " << line << " in file: " << filename << endl;
            }
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

    if( CopyFile(filename.c_str(), consume_file.str().c_str(), FALSE) == 0 ) {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime() << " File Interface:  failed copying processed file " << filename << endl;
        dout << CtiTime() << " to " << consume_file.str() << endl;
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
            CtiLockGuard< CtiLogger > logGuard(dout);
            dout
            << CtiTime()
            << " File Interface: unable to locate a schedule id for schedule named:"
            << name
            << endl;

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
        CtiLockGuard< CtiLogger > guard(dout);
        dout << CtiTime()
             << " File Interface - Unknown function: "
             << function
             << "  for schedule name: "
             << name
             << endl;
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
                CtiLockGuard< CtiLogger > guard(dout);
                dout << CtiTime()
                     << " File Interface: Placed a command on the main queue: "
                     << endl
                     << CtiTime()
                     << " Function: " << lower_function
                     << " Schedule: " << name
                     << endl;
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

