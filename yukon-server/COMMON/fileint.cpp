/*-----------------------------------------------------------------------------
    Filename:  fileint.cpp

    Programmer:  Aaron Lauinger

    Description:    Source file for CtiFileInterface

    Initial Date:  6/29/99

    COPYRIGHT: Copyright (C) Cannon Technologies, Inc., 1999
-----------------------------------------------------------------------------*/
#include "yukon.h"

#include "fileint.h"
#include "dllbase.h"

#include <rw\rwtime.h>

/*---------------------------------------------------------------------------
    Constructor

    Takes the name of the directory to watch.
---------------------------------------------------------------------------*/
CtiFileInterface::CtiFileInterface(const RWCString& dirtowatch, const RWCString& extension) :
_extension(extension), _dir(dirtowatch), _delete_on_start(false)
{
    _valid = false;
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiFileInterface::~CtiFileInterface()
{
}

const RWCString& CtiFileInterface::getDirectory() const
{
    return _dir;
}

const RWCString& CtiFileInterface::getExtension() const
{
    return _extension;
}

bool CtiFileInterface::getDeleteOnStart() const
{
    return _delete_on_start;
}

CtiFileInterface& CtiFileInterface::setDirectory(const RWCString& dir)
{

   _dir = dir;

   return *this;
}

CtiFileInterface& CtiFileInterface::setExtension(const RWCString& ext)
{
    _extension = ext;
    return *this;
}

CtiFileInterface& CtiFileInterface::setDeleteOnStart(bool del)
{
    _delete_on_start = del;
    return *this;
}

/*---------------------------------------------------------------------------
    start

    Starts the thread that watches the directory for new files.
    Does nothing if the thread is alread running.
---------------------------------------------------------------------------*/
void CtiFileInterface::start()
{
    RWMutexLock::LockGuard guard(_mutex);

    //Make sure that the watch thread isn't already running
    if ( !(_watchthr.isValid() && _watchthr.getExecutionState() == RW_THR_ACTIVE) )
    {
        RWThreadFunction watch_func = rwMakeThreadFunction(*this, &CtiFileInterface::_watch );
        _watchthr = watch_func;
        watch_func.start();

        //Wait until the watch thread started successfully
        //while ( !(_watchthr.getExecutionState() & RW_THR_ACTIVE) )
        //    rwYield();
    }

}

/*---------------------------------------------------------------------------
    stop

    Stops the thread that watches the directory for new files.
    Does nothing if the thread isn't already running.
---------------------------------------------------------------------------*/
void CtiFileInterface::stop()
{
    RWMutexLock::LockGuard guard(_mutex);

    //Make sure that the watch thread is running
    if ( _watchthr.isValid() && _watchthr.getExecutionState() & RW_THR_ACTIVE )
    {
        if ( _watchthr.requestCancellation() == RW_THR_ABORTED )
        {
            //Cancellation started but didn't complete - kick it over
            _watchthr.terminate();

            {
                RWMutexLock::LockGuard guard(coutMux);
                cerr << RWTime() << " - CtiFileInterface::stop() - Watch thread aborted cancellation and has been terminated" << endl;
            }
        }
    }
}

/*---------------------------------------------------------------------------
    isValid

    Returns true if this file interface is watching the given directory.
---------------------------------------------------------------------------*/
bool CtiFileInterface::isValid() const
{
    return _valid;
}

/*---------------------------------------------------------------------------
    _watch

    This is where the watch thread loops waiting for new files to appear.
---------------------------------------------------------------------------*/
void CtiFileInterface::_watch()
{
    DWORD dwWaitStatus;
    HANDLE dwChangeHandle;

    try
    {
        //The find functions work in the current directory
        //Lets hope there are no conflicts with others!

        {
            RWMutexLock::LockGuard guard(coutMux);
            cout << RWTime() << " - Setting Current Directory to:  " << _dir << endl;
        }
        if ( !SetCurrentDirectory( _dir ) )
        {
            {
                RWMutexLock::LockGuard guard(coutMux);
                cerr << RWTime() << " - CtiFileInterface::_watch() - An error occured changing the current directory" << endl;
                //where should this be going FIX FIX - maybe that'll get someones attention
            }
            return;
        }

        // flag indicates we need to delete
        // all matching files on startup, don't allow these to be handled
        // if this is set true
        bool do_delete = _delete_on_start;

        while (TRUE)
        {
            WIN32_FIND_DATA FileData;
            HANDLE hSearch;

            RWCString search_string = "*" + _extension;
            hSearch = FindFirstFile( search_string, &FileData );

            if ( hSearch == INVALID_HANDLE_VALUE )
            {
                rwRunnable().serviceCancellation();
                rwRunnable().sleep(1000);

                if( do_delete )
                {
                    do_delete = false;
                    _valid = true;
                }
                continue;
            }

            do
            {
                if( !do_delete)
                {
                    //Actually handle the file in some child class
                    {
                        RWMutexLock::LockGuard guard(coutMux);
                        cout << RWTime()  << " File Interface: handling file " << FileData.cFileName << endl;
                    }

                    handleFile( FileData.cFileName );
                }
                else
                {
                    {
                        RWMutexLock::LockGuard guard(coutMux);
                        cout << RWTime()  << " File Interface: deleting file " << FileData.cFileName << endl;
                    }
                }

                //Delete it
                unlink( FileData.cFileName );
            }
            while ( FindNextFile( hSearch, &FileData ) );

            FindClose(hSearch);

        }
    } catch (RWCancellation&)
    {
        _valid = false;
        {
            RWMutexLock::LockGuard guard(coutMux);
            cout << RWTime()  << " file interface exiting" << endl;
        }
        throw;
    }

}
