/*---------------------------------------------------------------------------
        Filename:  lmprogramcontrolwindow.cpp
        
        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMProgramControlWindow.
                        CtiLMProgramControlWindow maintains the state and handles
                        the persistence of programs for Load Management.

        Initial Date:  2/13/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include "dbaccess.h"
#include "lmid.h"
#include "lmprogramcontrolwindow.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"

extern BOOL _LM_DEBUG;

RWDEFINE_COLLECTABLE( CtiLMProgramControlWindow, CTILMPROGRAMCONTROLWINDOW_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramControlWindow::CtiLMProgramControlWindow()
{   
}

CtiLMProgramControlWindow::CtiLMProgramControlWindow(RWDBReader& rdr)
{
    restore(rdr);   
}

CtiLMProgramControlWindow::CtiLMProgramControlWindow(const CtiLMProgramControlWindow& lmprogcontwindow)
{
    operator=(lmprogcontwindow);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiLMProgramControlWindow::~CtiLMProgramControlWindow()
{
}

/*---------------------------------------------------------------------------
    getPAOId
    
    Returns the unique id of the program control window
---------------------------------------------------------------------------*/
ULONG CtiLMProgramControlWindow::getPAOId() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _paoid;
}


/*---------------------------------------------------------------------------
    getWindowNumber
    
    Returns the window number of the program control window
---------------------------------------------------------------------------*/
ULONG CtiLMProgramControlWindow::getWindowNumber() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _windownumber;
}

/*---------------------------------------------------------------------------
    getAvailableStartTime
    
    Returns the available start time as the number of seconds from midnight
    for the program control window.
---------------------------------------------------------------------------*/
ULONG CtiLMProgramControlWindow::getAvailableStartTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _availablestarttime;
}

/*---------------------------------------------------------------------------
    getAvailableStopTime
    
    Returns the available stop time as the number of seconds from midnight
    for the program control window.
---------------------------------------------------------------------------*/
ULONG CtiLMProgramControlWindow::getAvailableStopTime() const
{
    RWRecursiveLock<RWMutexLock>::LockGuard guard( _mutex);
    return _availablestoptime;
}

/*---------------------------------------------------------------------------
    setPAOId
    
    Sets the id of the program control window - use with caution
---------------------------------------------------------------------------*/
CtiLMProgramControlWindow& CtiLMProgramControlWindow::setPAOId(ULONG devid)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _paoid = devid;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setWindowNumber
    
    Sets the window number of the program control window
---------------------------------------------------------------------------*/    
CtiLMProgramControlWindow& CtiLMProgramControlWindow::setWindowNumber(ULONG winnum)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _windownumber = winnum;

    return *this;
}

/*---------------------------------------------------------------------------
    setAvailableStartTime

    Sets the available start time as the number of seconds from midnight
    for the program control window.
---------------------------------------------------------------------------*/    
CtiLMProgramControlWindow& CtiLMProgramControlWindow::setAvailableStartTime(ULONG availstarttime)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _availablestarttime = availstarttime;

    return *this;
}

/*---------------------------------------------------------------------------
    setAvailableStopTime

    Sets the available stop time as the number of seconds from midnight
    for the program control window.
---------------------------------------------------------------------------*/    
CtiLMProgramControlWindow& CtiLMProgramControlWindow::setAvailableStopTime(ULONG availstoptime)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    _availablestoptime = availstoptime;

    return *this;
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMProgramControlWindow::restoreGuts(RWvistream& istrm)
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    RWCollectable::restoreGuts( istrm );

    istrm >> _paoid
          >> _windownumber
          >> _availablestarttime
          >> _availablestoptime;

}

/*---------------------------------------------------------------------------
    saveGuts
    
    Save self's state onto the given stream
---------------------------------------------------------------------------*/
void CtiLMProgramControlWindow::saveGuts(RWvostream& ostrm ) const  
{

    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
        
    RWCollectable::saveGuts( ostrm );

    ostrm << _paoid
          << _windownumber
          << _availablestarttime
          << _availablestoptime;

    return;
}

/*---------------------------------------------------------------------------
    operator=
---------------------------------------------------------------------------*/
CtiLMProgramControlWindow& CtiLMProgramControlWindow::operator=(const CtiLMProgramControlWindow& right)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    if( this != &right )
    {
        _paoid = right._paoid;
        _windownumber = right._windownumber;
        _availablestarttime = right._availablestarttime;
        _availablestoptime = right._availablestoptime;
    }

    return *this;
}

/*---------------------------------------------------------------------------
    operator==
---------------------------------------------------------------------------*/
int CtiLMProgramControlWindow::operator==(const CtiLMProgramControlWindow& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return (getPAOId() == right.getPAOId() && getWindowNumber() == right.getWindowNumber());
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMProgramControlWindow::operator!=(const CtiLMProgramControlWindow& right) const
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);
    return getPAOId() != right.getPAOId();
}

/*---------------------------------------------------------------------------
    replicate
    
    Restores self's operation fields
---------------------------------------------------------------------------*/
CtiLMProgramControlWindow* CtiLMProgramControlWindow::replicate() const
{
    return (new CtiLMProgramControlWindow(*this));
}

/*---------------------------------------------------------------------------
    restore
    
    Restores given a RWDBReader
---------------------------------------------------------------------------*/
void CtiLMProgramControlWindow::restore(RWDBReader& rdr)
{
    RWRecursiveLock<RWMutexLock>::LockGuard  guard(_mutex);

    rdr["deviceid"] >> _paoid;//will be paobjectid
    rdr["windownumber"] >> _windownumber;
    rdr["availablestarttime"] >> _availablestarttime;
    rdr["availablestoptime"] >> _availablestoptime;
}
