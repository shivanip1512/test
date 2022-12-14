#include "precompiled.h"

#include "dbaccess.h"
#include "lmid.h"
#include "lmprogramcontrolwindow.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "loadmanager.h"
#include "lmutility.h"

extern ULONG _LM_DEBUG;

DEFINE_COLLECTABLE( CtiLMProgramControlWindow, CTILMPROGRAMCONTROLWINDOW_ID )

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiLMProgramControlWindow::CtiLMProgramControlWindow() :
_paoid(0),
_windownumber(0),
_availablestarttime(0),
_availablestoptime(0)
{
}

CtiLMProgramControlWindow::CtiLMProgramControlWindow(Cti::RowReader &rdr)
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
CtiTime CtiLMProgramControlWindow::getAvailableStartTime(CtiDate &defaultDate) const
{
    return GetTimeFromOffsetAndDate(_availablestarttime, defaultDate);
}

LONG CtiLMProgramControlWindow::getAvailableStartTimeDaily() const
{
    return _availablestarttime;
}

/*---------------------------------------------------------------------------
    getAvailableStopTime

    Returns the available stop time as the number of seconds from midnight
    for the program control window.
---------------------------------------------------------------------------*/
CtiTime CtiLMProgramControlWindow::getAvailableStopTime(CtiDate &defaultDate) const
{
    return GetTimeFromOffsetAndDate(_availablestoptime, defaultDate);
}

LONG CtiLMProgramControlWindow::getAvailableStopTimeDaily() const
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
    return (CTIDBG_new CtiLMProgramControlWindow(*this));
}

/*---------------------------------------------------------------------------
    restore

    Restores given a Reader
---------------------------------------------------------------------------*/
void CtiLMProgramControlWindow::restore(Cti::RowReader &rdr)
{


    rdr["deviceid"] >> _paoid;//will be paobjectid
    rdr["windownumber"] >> _windownumber;
    rdr["availablestarttime"] >> _availablestarttime;
    rdr["availablestoptime"] >> _availablestoptime;
}
