/*---------------------------------------------------------------------------
        Filename:  lmprogramcontrolwindow.cpp
        
        Programmer:  Josh Wolberg
        
        Description:    Source file for CtiLMProgramControlWindow.
                        CtiLMProgramControlWindow maintains the state and handles
                        the persistence of programs for Load Management.

        Initial Date:  2/13/2001
         
        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "lmid.h"
#include "lmprogramcontrolwindow.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"

extern ULONG _LM_DEBUG;

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
LONG CtiLMProgramControlWindow::getPAOId() const
{

    return _paoid;
}


/*---------------------------------------------------------------------------
    getWindowNumber
    
    Returns the window number of the program control window
---------------------------------------------------------------------------*/
LONG CtiLMProgramControlWindow::getWindowNumber() const
{

    return _windownumber;
}

/*---------------------------------------------------------------------------
    getAvailableStartTime
    
    Returns the available start time as the number of seconds from midnight
    for the program control window.
---------------------------------------------------------------------------*/
LONG CtiLMProgramControlWindow::getAvailableStartTime() const
{

    return _availablestarttime;
}

/*---------------------------------------------------------------------------
    getAvailableStopTime
    
    Returns the available stop time as the number of seconds from midnight
    for the program control window.
---------------------------------------------------------------------------*/
LONG CtiLMProgramControlWindow::getAvailableStopTime() const
{

    return _availablestoptime;
}

/*---------------------------------------------------------------------------
    setPAOId
    
    Sets the id of the program control window - use with caution
---------------------------------------------------------------------------*/
CtiLMProgramControlWindow& CtiLMProgramControlWindow::setPAOId(LONG devid)
{

    _paoid = devid;
    //do not notify observers of this !
    return *this;
}

/*---------------------------------------------------------------------------
    setWindowNumber
    
    Sets the window number of the program control window
---------------------------------------------------------------------------*/    
CtiLMProgramControlWindow& CtiLMProgramControlWindow::setWindowNumber(LONG winnum)
{

    _windownumber = winnum;

    return *this;
}

/*---------------------------------------------------------------------------
    setAvailableStartTime

    Sets the available start time as the number of seconds from midnight
    for the program control window.
---------------------------------------------------------------------------*/    
CtiLMProgramControlWindow& CtiLMProgramControlWindow::setAvailableStartTime(LONG availstarttime)
{

    _availablestarttime = availstarttime;

    return *this;
}

/*---------------------------------------------------------------------------
    setAvailableStopTime

    Sets the available stop time as the number of seconds from midnight
    for the program control window.
---------------------------------------------------------------------------*/    
CtiLMProgramControlWindow& CtiLMProgramControlWindow::setAvailableStopTime(LONG availstoptime)
{

    _availablestoptime = availstoptime;

    return *this;
}

/*-------------------------------------------------------------------------
    restoreGuts
    
    Restore self's state from the given stream
--------------------------------------------------------------------------*/
void CtiLMProgramControlWindow::restoreGuts(RWvistream& istrm)
{



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

    return (getPAOId() == right.getPAOId() && getWindowNumber() == right.getWindowNumber());
}

/*---------------------------------------------------------------------------
    operator!=
---------------------------------------------------------------------------*/
int CtiLMProgramControlWindow::operator!=(const CtiLMProgramControlWindow& right) const
{

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


    rdr["deviceid"] >> _paoid;//will be paobjectid
    rdr["windownumber"] >> _windownumber;
    rdr["availablestarttime"] >> _availablestarttime;
    rdr["availablestoptime"] >> _availablestoptime;
}
