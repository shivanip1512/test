#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   mc_fileint
*
* Date:   7/19/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/MACS/mc_fileint.cpp-arc  $
* REVISION     :  $Revision: 1.5 $
* DATE         :  $Date: 2005/02/10 23:23:52 $
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

#include "mc_fileint.h"
#include "mc_msg.h"
#include "ctibase.h"

#include <rw/re.h>


/*
    Directory where processed files go
*/    
const RWCString CtiMCFileInterface::getConsumedDirectory() const
{
    return _consumed_dir;
}
    
CtiMCFileInterface& CtiMCFileInterface::setConsumedDirectory(const RWCString& dir)
{
    _consumed_dir = dir;
    return *this;
}

/*
*/        
void CtiMCFileInterface::start()
{   
    if( mkdir(_consumed_dir.data()) < 0 ) {
        if( errno != EEXIST ) {            
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " File Interface: An error occured creating directory:  " << _consumed_dir << endl;    
            dout << RWTime() << " File Interface: Processed/consumed files will be deleted " << endl;
        }
    }

    // make sure to let our super start
    CtiFileInterface::start();
}
/*---------------------------------------------------------------------------
    handleFile

    Handles a newly found file.  Called from our super class.
---------------------------------------------------------------------------*/
void CtiMCFileInterface::handleFile(const RWCString& filename )
{
    FILE* fptr;
    char buf[MC_FILE_BUF_SIZE];
    char* sep_ptr;
    int line = 0;

    if( (fptr = fopen( filename, "r" ) ) == NULL )
    {
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout << RWTime() << " File Interface: An error occured opening file:  " << filename << endl;
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
                dout << RWTime() << " File Interface: An error occured at line: " << line << " in file: " << filename << endl;
            }
            continue;
        }
         

        *sep_ptr = NULL;

        RWCString function(buf);
        RWCString name(sep_ptr+1);
               
        name = name.strip(RWCString::both);

        execute( function, name );

        //sometimes fgets returns without affecting buf
        //this causes some lines to be interpreted twice -
        //zeroing out buf will avoid this 
        memset( buf, 0, MC_FILE_BUF_SIZE );
    }
                   
    fclose( fptr );

    // Attempt to copy the file into the consumed directory
    RWCString consume_file(_consumed_dir);
    consume_file += "\\";
    consume_file += filename;
    consume_file += " ";
   
    RWTime now_time;
    RWDate now_date(now_time);
  
    consume_file += RWLocale::global().asString((long) now_date.month());    
    consume_file += RWLocale::global().asString((long) now_date.dayOfMonth());
    consume_file += " ";
    consume_file += RWLocale::global().asString((long) now_time.hour());    
    consume_file += RWLocale::global().asString((long) now_time.minute());
    consume_file += RWLocale::global().asString((long) now_time.second());

    if( CopyFile(filename.data(), consume_file.data(), FALSE) == 0 ) {
        CtiLockGuard< CtiLogger > guard(dout);
        dout << RWTime() << " File Interface:  failed copying processed file " << filename << endl;
        dout << RWTime() << " to " << consume_file << endl;        
    }
    
    return;
}
/*----------------------------------------------------------------------------
  setQueue

  Messages will be put on this queue for procssing when they are generated
----------------------------------------------------------------------------*/
void CtiMCFileInterface::setQueue(CtiQueue< CtiMessage, less<CtiMessage> >* queue )
{
    _message_queue = queue;
}

/*---------------------------------------------------------------------------
    execute

    Creates the appropriate executor and executes it.
---------------------------------------------------------------------------*/
void CtiMCFileInterface::execute(const RWCString& function, const RWCString& name )
{

    //attempt to find the id of the schedule
    long id = -1;
    {
        RWRecursiveLock<RWMutexLock>::LockGuard guard( _schedule_manager.getMux() );

        id = _schedule_manager.getID( string( name.data() ));

        if( id == -1 )
        {
            CtiLockGuard< CtiLogger > guard(dout);
            dout
            << RWTime()
            << " File Interface: unable to locate a schedule id for schedule named:"
            << name
            << endl;

            return;
        }
    }

    //Determine which function
    CtiMCOverrideRequest* msg = NULL;
    RWCString lower_function = function;
    lower_function.toLower();

    if( lower_function == "start" ||
        lower_function == "$start" )
    {
        msg = new CtiMCOverrideRequest();
        msg->setAction( CtiMCOverrideRequest::Start );
        msg->setStartTime( RWTime::now() );
    }
    else
    if( lower_function == "stop" ||
        lower_function == "$stop" )
    {
        msg = new CtiMCOverrideRequest();
        msg->setAction( CtiMCOverrideRequest::Stop );
        msg->setStopTime( RWTime::now() );
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
        dout << RWTime()
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
                dout << RWTime()
                     << " File Interface: Placed a command on the main queue: "
                     << endl
                     << RWTime()
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

