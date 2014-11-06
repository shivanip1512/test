#include "precompiled.h"

#include "tbl_dv_wnd.h"
#include "database_connection.h"
#include "database_reader.h"
#include "desolvers.h"

using namespace std;

CtiTableDeviceWindow::CtiTableDeviceWindow() :
_ID(-1),
_type(DeviceWindowInvalid),
_open(-1L),
_duration(-1L),
_alternateOpen(-1L),
_alternateDuration(-1L),
_updated(FALSE)
{}

CtiTableDeviceWindow::~CtiTableDeviceWindow()
{
}

CtiTableDeviceWindow::CtiTableDeviceWindow(const CtiTableDeviceWindow &aRef)
{
    *this = aRef;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::operator=(const CtiTableDeviceWindow &aRef)
{
    if(this != &aRef)
    {
        _ID                 = aRef.getID();
        _type                     = aRef.getType();
        _open                     = aRef.getOpen();
        _duration                 = aRef.getDuration();
        _alternateOpen            = aRef.getAlternateOpen();
        _alternateDuration        = aRef.getAlternateDuration();

        _updated = FALSE;
    }

    return *this;
}

LONG CtiTableDeviceWindow::getType() const
{

    return _type;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setType( const LONG aType )
{

    _type = aType;
    return *this;
}

LONG CtiTableDeviceWindow::getOpen() const
{

    return _open;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setOpen( const LONG aWindow )
{

    _open = aWindow;
    return *this;
}

LONG CtiTableDeviceWindow::getDuration() const
{

    return _duration;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setDuration( const LONG aDuration )
{

    _duration = aDuration;
    return *this;
}

LONG CtiTableDeviceWindow::getAlternateOpen() const
{

    return _alternateOpen;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setAlternateOpen( const LONG aWindow )
{

    _alternateOpen = aWindow;
    return *this;
}

LONG CtiTableDeviceWindow::getAlternateDuration() const
{

    return _alternateDuration;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setAlternateDuration( const LONG aDuration )
{

    _alternateDuration = aDuration;
    return *this;
}

BOOL CtiTableDeviceWindow::getUpdated() const
{

    return _updated;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setUpdated( const BOOL aBool )
{

    _updated = aBool;
    return *this;
}

LONG CtiTableDeviceWindow::getID() const
{

    return _ID;
}

CtiTableDeviceWindow& CtiTableDeviceWindow::setID( const LONG aID )
{

    _ID = aID;
    return *this;
}


string CtiTableDeviceWindow::getTableName()
{
    return "DeviceWindow";
}


LONG CtiTableDeviceWindow::calculateClose(LONG aOpen, LONG aDuration) const
{
    LONG close;

    close = aOpen + aDuration;
    if(close > 86400)
    {
        close -= 86400;
    }
    return close;
}

string CtiTableDeviceWindow::getSQLCoreStatement()
{
    static const string sqlCore =  "SELECT DV.deviceid, DW.type, DW.winopen, DW.winclose, DW.alternateopen, "
                                     "DW.alternateclose "
                                   "FROM Device DV, DeviceWindow DW "
                                   "WHERE DV.deviceid = DW.deviceid";

    return sqlCore;
}

string CtiTableDeviceWindow::addIDSQLClause(const Cti::Database::id_set &paoids)
{
    string sqlIDs;

    if( !paoids.empty() )
    {
        std::ostringstream in_list;

        if( paoids.size() == 1 )
        {
            //  special single id case

            in_list << *(paoids.begin());

            sqlIDs += "AND DV.deviceid = " + in_list.str();

            return sqlIDs;
        }
        else
        {
            in_list << "(";

            copy(paoids.begin(), paoids.end(), csv_output_iterator<long, std::ostringstream>(in_list));

            in_list << ")";

            sqlIDs += "AND DV.deviceid IN " + in_list.str();

            return sqlIDs;
        }
    }

    return string();
}

void CtiTableDeviceWindow::DecodeDatabaseReader(Cti::RowReader &aRdr)
{
    LONG close,alternateClose;

    if(getDebugLevel() & DEBUGLEVEL_DATABASE)
    {
        CTILOG_DEBUG(dout, "Decoding DB read from "<< getTableName());
    }

    string rwstemp;

    aRdr["type"]  >> rwstemp;

    _type = resolveDeviceWindowType( rwstemp );
    aRdr["winopen"] >> _open;
    aRdr["winclose"] >> close;
    aRdr["alternateopen"] >> _alternateOpen;
    aRdr["alternateclose"] >> alternateClose;

    // see if we pass over midnight
    if(close < _open)
    {
        // we are seconds in a day minus open + seconds into the day
        _duration = 86400 - _open + close;
    }
    else
    {
        _duration = close - _open;
    }

    // see if we pass over midnight
    if(close < _open)
    {
        // we are seconds in a day minus open + seconds into the day
        _alternateDuration = 86400 - _alternateOpen + alternateClose;
    }
    else
    {
        _alternateDuration = alternateClose - _alternateOpen;
    }

    _updated = TRUE;                    // _ONLY_ _ONLY_ place this is set.
}

std::string CtiTableDeviceWindow::toString() const
{
    Cti::FormattedList itemList;

    itemList <<"CtiTableDeviceWindow";
    itemList.add("Window Type")        << desolveDeviceWindowType(_type);
    itemList.add("Open")               << _open;
    itemList.add("Duration")           << _duration;
    itemList.add("Alternate Open")     << _alternateOpen;
    itemList.add("Alternate Duration") << _alternateDuration;

    return itemList.toString();
}

bool CtiTableDeviceWindow::addSignaledRateActive(int rate) const
{
    pair< CtiWindowSet_t::iterator, bool> ipair = _signaledRateActive.insert( rate );
    return ipair.second;
}

bool CtiTableDeviceWindow::addSignaledRateSent(int rate) const
{
    pair< CtiWindowSet_t::iterator, bool> ipair = _signaledRateSent.insert( rate );
    return ipair.second;
}

bool CtiTableDeviceWindow::isSignaledRateScheduled(int rate) const
{
    return (_signaledRateSent.find( rate ) != _signaledRateSent.end()); // Found it in there!
}

bool CtiTableDeviceWindow::verifyWindowMatch() const
{
    bool bmatch = false;

    if(_signaledRateSent == _signaledRateActive)
    {
        bmatch = true;
    }

    return bmatch;
}




