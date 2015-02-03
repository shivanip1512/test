#include "precompiled.h"

#include "logger.h"
#include "tbl_dv_scandata.h"
#include "database_reader.h"
#include "database_writer.h"
#include "database_util.h"

using std::string;
using std::endl;

CtiTableDeviceScanData::CtiTableDeviceScanData(LONG did) :
lastFreezeNumber(0),
prevFreezeNumber(0),
lastLPTime(CtiTime( CtiTime() - (30 * 24 * 3600) )),       // Thirty days ago.
lastFreezeTime((unsigned)1985, (unsigned)1, (unsigned)1),
prevFreezeTime((unsigned)1985, (unsigned)1, (unsigned)1),
_deviceID(did)
{
    for(int i=0; i < ScanRateInvalid; i++)
    {
        _nextScan[i] = (CtiTime)CtiTime(YUKONEOT);
        _lastCommunicationTime[i] = CtiTime(YUKONEOT);
    }

}

CtiTableDeviceScanData::~CtiTableDeviceScanData()
{
    if( isDirty() )
    {
        if( ! Update() )
        {
            Insert();
        }
    }
}


LONG CtiTableDeviceScanData::getDeviceID() const
{
    return _deviceID;
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setDeviceID(LONG id)
{
    _deviceID = id;
    return *this;
}

CtiTime CtiTableDeviceScanData::getNextScan(INT a) const
{
    CtiTime atime = _nextScan[a];
    return atime;
}

CtiTableDeviceScanData& CtiTableDeviceScanData::setNextScan(INT a, const CtiTime &b)
{
    _nextScan[a] = CtiTime(b);
    return *this;
}

CtiTime CtiTableDeviceScanData::nextNearestTime(int maxrate) const
{
    CtiTime Win = CtiTime( CtiTime(YUKONEOT) );

    for(int i = 0; i < maxrate; i++)
    {
        if(_nextScan[i] < Win) Win = _nextScan[i];
    }

    return Win;
}

LONG  CtiTableDeviceScanData::getLastFreezeNumber() const
{
    return lastFreezeNumber;
}
LONG& CtiTableDeviceScanData::getLastFreezeNumber()
{
    return lastFreezeNumber;
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setLastFreezeNumber( const LONG aLastFreezeNumber )
{
    setDirty(true);
    lastFreezeNumber = aLastFreezeNumber;
    return *this;
}

LONG  CtiTableDeviceScanData::getPrevFreezeNumber() const
{
    return prevFreezeNumber;
}
LONG& CtiTableDeviceScanData::getPrevFreezeNumber()
{
    return prevFreezeNumber;
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setPrevFreezeNumber( const LONG aPrevFreezeNumber )
{
    setDirty(true);
    prevFreezeNumber = aPrevFreezeNumber;
    return *this;
}

CtiTime  CtiTableDeviceScanData::getLastFreezeTime() const
{
    return lastFreezeTime;
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setLastFreezeTime( const CtiTime& aLastFreezeTime )
{
    setDirty(true);
    lastFreezeTime = aLastFreezeTime;
    return *this;
}

CtiTime  CtiTableDeviceScanData::getLastLPTime() const
{
    return lastLPTime;
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setLastLPTime( const CtiTime& aLastTime )
{
    setDirty(true);
    lastLPTime = aLastTime;
    return *this;
}

CtiTime  CtiTableDeviceScanData::getPrevFreezeTime() const
{
    return prevFreezeTime;
}
CtiTableDeviceScanData& CtiTableDeviceScanData::setPrevFreezeTime( const CtiTime& aPrevFreezeTime )
{
    setDirty(true);
    prevFreezeTime = CtiTime(aPrevFreezeTime);
    return *this;
}



string CtiTableDeviceScanData::getTableName() const
{
    return "DynamicDeviceScanData";
}

bool CtiTableDeviceScanData::Restore()
{
    static const string sql =  "SELECT DDS.deviceid, DDS.lastfreezetime, DDS.prevfreezetime, DDS.lastlptime, "
                                   "DDS.lastfreezenumber, DDS.prevfreezenumber, DDS.nextscan0, DDS.nextscan1, DDS.nextscan2, "
                                   "DDS.nextscan3 "
                               "FROM DynamicDeviceScanData DDS "
                               "WHERE DDS.deviceid = ?";

    Cti::Database::DatabaseConnection connection;
    Cti::Database::DatabaseReader reader(connection, sql);

    reader << getDeviceID();

    reader.execute();

    if( reader() )
    {
        DecodeDatabaseReader( reader );
        setDirty( false );
        return true;
    }

    return false;
}

bool CtiTableDeviceScanData::Update(Cti::Database::DatabaseConnection &conn)
{
    static const std::string sql = "update " + getTableName() +
                                   " set "
                                        "lastfreezetime = ?, "
                                        "prevfreezetime = ?, "
                                        "lastlptime = ?, "
                                        "lastfreezenumber = ?, "
                                        "prevfreezenumber = ?, "
                                        "nextscan0 = ?, "
                                        "nextscan1 = ?, "
                                        "nextscan2 = ?, "
                                        "nextscan3 = ?"
                                   " where "
                                        "deviceid = ?";


    Cti::Database::DatabaseWriter   updater(conn, sql);

    updater
        << getLastFreezeTime()
        << getPrevFreezeTime()
        << getLastLPTime()
        << getLastFreezeNumber()
        << getPrevFreezeNumber()
        << getNextScan(0)
        << getNextScan(1)
        << getNextScan(2)
        << getNextScan(3)
        << getDeviceID();

    if ( ! Cti::Database::executeUpdater( updater, __FILE__, __LINE__ ))
    {
        return false;
    }

    setDirty(false);
    
    return true; // No error occured!
}

bool CtiTableDeviceScanData::Update()
{
    Cti::Database::DatabaseConnection   conn;

    return Update(conn);
}

bool CtiTableDeviceScanData::Insert()
{
    static const std::string sql = "insert into " + getTableName() +
                                   " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    Cti::Database::DatabaseConnection   conn;
    Cti::Database::DatabaseWriter       inserter(conn, sql);

    inserter
        << getDeviceID()
        << getLastFreezeTime()
        << getPrevFreezeTime()
        << getLastLPTime()
        << getLastFreezeNumber()
        << getPrevFreezeNumber()
        << getNextScan(0)
        << getNextScan(1)
        << getNextScan(2)
        << getNextScan(3);

    if( ! Cti::Database::executeCommand( inserter, __FILE__, __LINE__ ))
    {
        return false;
    }

    setDirty(false);
    
    return true; // No error occured!
}

void CtiTableDeviceScanData::DecodeDatabaseReader(Cti::RowReader& rdr )
{
    char temp[32];
    CtiTime adt;

    rdr["deviceid"] >> _deviceID;
    rdr["lastfreezetime"] >> lastFreezeTime;
    rdr["prevfreezetime"] >> prevFreezeTime;
    rdr["lastlptime"] >> lastLPTime;
    rdr["lastfreezenumber"] >> lastFreezeNumber;
    rdr["prevfreezenumber"] >> prevFreezeNumber;

#if 0   // 11/15/01 CGP Breakage??
    for(int i = 0; i <= ScanRateIntegrity; i++)
    {
        sprintf(temp, "nextscan%d", i);
        rdr[ temp ] >> adt;
        setNextScan(i, adt);
    }
#endif

}

CtiTableDeviceScanData::CtiTableDeviceScanData(const CtiTableDeviceScanData& aRef)
{
    *this = aRef;
}


CtiTableDeviceScanData& CtiTableDeviceScanData::operator=(const CtiTableDeviceScanData& aRef)
{
    if(this != &aRef)
    {
        setDeviceID( aRef.getDeviceID() );

        setLastFreezeTime( aRef.getLastFreezeTime() );
        setPrevFreezeTime( aRef.getPrevFreezeTime() );
        setLastLPTime( aRef.getLastLPTime() );
        setLastFreezeNumber( aRef.getLastFreezeNumber() );

        for(int i=0; i < ScanRateInvalid; i++)
        {
            _nextScan[i] = aRef.getNextScan(i);
            _lastCommunicationTime[i] = aRef.getLastCommunicationTime(i);
        }
    }

    return *this;
}

CtiTime CtiTableDeviceScanData::getLastCommunicationTime(int i) const
{
    return _lastCommunicationTime[i];
}

CtiTableDeviceScanData& CtiTableDeviceScanData::setLastCommunicationTime( int i, const CtiTime& tme )
{
    _lastCommunicationTime[i] = tme;
    return *this;
}


