#include "precompiled.h"

#include "cctwowaycbcpoints.h"
#include "ccid.h"
#include "database_util.h"
#include "ccutil.h"

#include <boost/assign/list_of.hpp>

using std::string;
using std::endl;

extern unsigned long _CC_DEBUG;
using Cti::CapControl::MissingPointAttribute;

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCTwoWayPoints::CtiCCTwoWayPoints(long paoid, string paotype)
    :   _paoid( paoid ),
        _paotype( paotype ),
        _insertDynamicDataFlag( true ),
        _dirty( true )
{
    _attributes
        = boost::assign::map_list_of
            ( PointAttribute::CbcVoltage,                   LitePoint() )
            ( PointAttribute::HighVoltage,                  LitePoint() )
            ( PointAttribute::LowVoltage,                   LitePoint() )
            ( PointAttribute::DeltaVoltage,                 LitePoint() )
            ( PointAttribute::AnalogInput1,                 LitePoint() )
            ( PointAttribute::Temperature,                  LitePoint() )
            ( PointAttribute::RSSI,                         LitePoint() )
            ( PointAttribute::IgnoredReason,                LitePoint() )
            ( PointAttribute::VoltageControl,               LitePoint() )
            ( PointAttribute::UvThreshold,                  LitePoint() )
            ( PointAttribute::OvThreshold,                  LitePoint() )
            ( PointAttribute::OVUVTrackTime,                LitePoint() )
            ( PointAttribute::NeutralCurrentSensor,         LitePoint() )
            ( PointAttribute::NeutralCurrentAlarmThreshold, LitePoint() )
            ( PointAttribute::TimeTempSeasonOne,            LitePoint() )
            ( PointAttribute::TimeTempSeasonTwo,            LitePoint() )
            ( PointAttribute::VarControl,                   LitePoint() )
            ( PointAttribute::UDPIpAddress,                 LitePoint() )
            ( PointAttribute::UDPPortNumber,                LitePoint() )
            ( PointAttribute::CapacitorBankState,           LitePoint() )
            ( PointAttribute::ReCloseBlocked,               LitePoint() )
            ( PointAttribute::ControlMode,                  LitePoint() )
            ( PointAttribute::AutoVoltControl,              LitePoint() )
            ( PointAttribute::LastControlLocal,             LitePoint() )
            ( PointAttribute::LastControlRemote,            LitePoint() )
            ( PointAttribute::LastControlOvUv,              LitePoint() )
            ( PointAttribute::LastControlNeutralFault,      LitePoint() )
            ( PointAttribute::LastControlScheduled,         LitePoint() )
            ( PointAttribute::LastControlDigital,           LitePoint() )
            ( PointAttribute::LastControlAnalog,            LitePoint() )
            ( PointAttribute::LastControlTemperature,       LitePoint() )
            ( PointAttribute::OvCondition,                  LitePoint() )
            ( PointAttribute::UvCondition,                  LitePoint() )
            ( PointAttribute::OpFailedNeutralCurrent,       LitePoint() )
            ( PointAttribute::NeutralCurrentFault,          LitePoint() )
            ( PointAttribute::BadRelay,                     LitePoint() )
            ( PointAttribute::DailyMaxOps,                  LitePoint() )
            ( PointAttribute::VoltageDeltaAbnormal,         LitePoint() )
            ( PointAttribute::TempAlarm,                    LitePoint() )
            ( PointAttribute::DSTActive,                    LitePoint() )
            ( PointAttribute::NeutralLockout,               LitePoint() )
            ( PointAttribute::IgnoredIndicator,             LitePoint() )
            ( PointAttribute::TotalOpCount,                 LitePoint() )
            ( PointAttribute::UvCount,                      LitePoint() )
            ( PointAttribute::OvCount,                      LitePoint() )
            ( PointAttribute::CloseOpCount,                 LitePoint() )
            ( PointAttribute::OpenOpCount,                  LitePoint() )
            ( PointAttribute::LastControlReason,            LitePoint() )
                ;
}


long CtiCCTwoWayPoints::getPAOId() const
{
    return _paoid;
}



CtiCCTwoWayPoints& CtiCCTwoWayPoints::setPAOId(long paoId)
{
    _paoid = paoId;
    return *this;
}

void CtiCCTwoWayPoints::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    if( ! _dirty )
    {
        return;
    }

    int lastControlReason = encodeLastControlReasonForDB();

    int condition = 0;
    condition |= 0x01 * !!getPointValueByAttribute(PointAttribute::UvCondition);
    condition |= 0x02 * !!getPointValueByAttribute(PointAttribute::OvCondition);

    if( !_insertDynamicDataFlag )
    {
        static const string updateSql = "update dynamiccctwowaycbc set "
                                        "recloseblocked = ?, "
                                        "controlmode = ?, "
                                        "autovoltcontrol = ?, "
                                        "lastcontrol = ?, "
                                        "condition = ?, "
                                        "opfailedneutralcurrent = ?, "
                                        "neutralcurrentfault = ?, "
                                        "badrelay = ?, "
                                        "dailymaxops = ?, "
                                        "voltagedeltaabnormal = ?, "
                                        "tempalarm = ?, "
                                        "dstactive = ?, "
                                        "neutrallockout = ?, "
                                        "ignoredindicator = ?, "
                                        "voltage = ?, "
                                        "highvoltage = ?, "
                                        "lowvoltage = ?, "
                                        "deltavoltage = ?, "
                                        "analoginputone = ?, "
                                        "temp = ?, "
                                        "rssi = ?, "
                                        "ignoredreason = ?, "
                                        "totalopcount = ?, "
                                        "uvopcount = ?, "
                                        "ovopcount = ?, "
                                        "ovuvcountresetdate = ?, "
                                        "uvsetpoint = ?, "
                                        "ovsetpoint = ?, "
                                        "ovuvtracktime = ?, "
                                        "lastovuvdatetime = ?, "
                                        "neutralcurrentsensor = ?, "
                                        "neutralcurrentalarmsetpoint = ?, "
                                        "ipaddress = ?, "
                                        "udpport = ?"
                                        " where deviceid = ?";

        Cti::Database::DatabaseWriter updater(conn, updateSql);

        updater
            << (string)(getPointValueByAttribute(PointAttribute::ReCloseBlocked)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::ControlMode)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::AutoVoltControl)?"Y":"N")
            << lastControlReason
            << condition
            << (string)(getPointValueByAttribute(PointAttribute::OpFailedNeutralCurrent)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::NeutralCurrentFault)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::BadRelay)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::DailyMaxOps)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::VoltageDeltaAbnormal)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::TempAlarm)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::DSTActive)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::NeutralLockout)?"Y":"N")
            << (string)(getPointValueByAttribute(PointAttribute::IgnoredIndicator)?"Y":"N")
            << getPointValueByAttribute(PointAttribute::CbcVoltage)
            << getPointValueByAttribute(PointAttribute::HighVoltage)
            << getPointValueByAttribute(PointAttribute::LowVoltage)
            << getPointValueByAttribute(PointAttribute::DeltaVoltage)
            << getPointValueByAttribute(PointAttribute::AnalogInput1)
            << getPointValueByAttribute(PointAttribute::Temperature)
            << getPointValueByAttribute(PointAttribute::RSSI)
            << getPointValueByAttribute(PointAttribute::IgnoredReason)
            << getPointValueByAttribute(PointAttribute::TotalOpCount)
            << getPointValueByAttribute(PointAttribute::UvCount)
            << getPointValueByAttribute(PointAttribute::OvCount)
            << _ovuvCountResetDate
            << getPointValueByAttribute(PointAttribute::UvThreshold)
            << getPointValueByAttribute(PointAttribute::OvThreshold)
            << getPointValueByAttribute(PointAttribute::OVUVTrackTime)
            << _lastOvUvDateTime
            << getPointValueByAttribute(PointAttribute::NeutralCurrentSensor)
            << getPointValueByAttribute(PointAttribute::NeutralCurrentAlarmThreshold)
            << getPointValueByAttribute(PointAttribute::UDPIpAddress)
            << getPointValueByAttribute(PointAttribute::UDPPortNumber)
            << _paoid;

        if( Cti::Database::executeCommand( updater, __FILE__, __LINE__ ))
        {
            _dirty = false; // No error occured!
        }
    }
    else
    {
        {
            CtiLockGuard<CtiLogger> logger_guard(dout);
            dout << CtiTime() << " - Inserted TwoWay CBC data into DynamicCCTwoWayCBC: " << getPAOId() << endl;
        }
        static const string insertSql = "insert into dynamiccctwowaycbc values ( "
                                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, "
                                        "?, ?, ?, ?, ?)";

        Cti::Database::DatabaseWriter dbInserter(conn, insertSql);

        dbInserter << _paoid
                 << (string)(getPointValueByAttribute(PointAttribute::ReCloseBlocked)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::ControlMode)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::AutoVoltControl)?"Y":"N")
                 << lastControlReason
                 << condition
                 << (string)(getPointValueByAttribute(PointAttribute::OpFailedNeutralCurrent)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::NeutralCurrentFault)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::BadRelay)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::DailyMaxOps)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::VoltageDeltaAbnormal)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::TempAlarm)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::DSTActive)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::NeutralLockout)?"Y":"N")
                 << (string)(getPointValueByAttribute(PointAttribute::IgnoredIndicator)?"Y":"N")
                 << getPointValueByAttribute(PointAttribute::CbcVoltage)
                 << getPointValueByAttribute(PointAttribute::HighVoltage)
                 << getPointValueByAttribute(PointAttribute::LowVoltage)
                 << getPointValueByAttribute(PointAttribute::DeltaVoltage)
                 << getPointValueByAttribute(PointAttribute::AnalogInput1)
                 << getPointValueByAttribute(PointAttribute::Temperature)
                 << getPointValueByAttribute(PointAttribute::RSSI)
                 << getPointValueByAttribute(PointAttribute::IgnoredReason)
                 << getPointValueByAttribute(PointAttribute::TotalOpCount)
                 << getPointValueByAttribute(PointAttribute::UvCount)
                 << getPointValueByAttribute(PointAttribute::OvCount)
                 << _ovuvCountResetDate
                 << getPointValueByAttribute(PointAttribute::UvThreshold)
                 << getPointValueByAttribute(PointAttribute::OvThreshold)
                 << getPointValueByAttribute(PointAttribute::OVUVTrackTime)
                 << _lastOvUvDateTime
                 << getPointValueByAttribute(PointAttribute::NeutralCurrentSensor)
                 << getPointValueByAttribute(PointAttribute::NeutralCurrentAlarmThreshold)
                 << getPointValueByAttribute(PointAttribute::UDPIpAddress)
                 << getPointValueByAttribute(PointAttribute::UDPPortNumber);

        if( Cti::Database::executeCommand( dbInserter, __FILE__, __LINE__, Cti::Database::LogDebug(_CC_DEBUG & CC_DEBUG_DATABASE) ))
        {
            _insertDynamicDataFlag = false;
            _dirty = false; // No error occured!
        }
    }
}

LitePoint CtiCCTwoWayPoints::getPointByAttribute(const PointAttribute & attribute) const
{
    AttributePoint::const_iterator iter = _attributes.find(attribute);

    if ( iter == _attributes.end() )
    {
        throw MissingPointAttribute( _paoid, attribute, _paotype );
    }

    return iter->second;
}

int CtiCCTwoWayPoints::getPointIdByAttribute(const PointAttribute & attribute) const
{
    try
    {
        return getPointByAttribute(attribute).getPointId();
    }
    catch ( const MissingPointAttribute & missingAttribute )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);

        dout << CtiTime() << " - ** " << missingAttribute.what() << std::endl;
        return 0;
    }

}

PointAttribute CtiCCTwoWayPoints::getAttribute(int pointtype, int offset)
{
    switch (pointtype)
    {
        case StatusPointType:
        {
            return getStatusAttribute(offset);
            break;
        }
        case AnalogPointType:
        {
            return getAnalogAttribute(offset);
            break;
        }
        case PulseAccumulatorPointType:
        {
            return getAccumulatorAttribute(offset);
            break;
    }
        default:
        {
            return PointAttribute::Unknown;
            break;
        }
    }
}
PointAttribute CtiCCTwoWayPoints::getAnalogAttribute(int offset)
{
    return (_analogOffsetAttribute.find(offset) != _analogOffsetAttribute.end() ? _analogOffsetAttribute.find(offset)->second : PointAttribute::Unknown);
}
PointAttribute CtiCCTwoWayPoints::getStatusAttribute(int offset)
{
    return (_statusOffsetAttribute.find(offset) != _statusOffsetAttribute.end() ? _statusOffsetAttribute.find(offset)->second : PointAttribute::Unknown);
}
PointAttribute CtiCCTwoWayPoints::getAccumulatorAttribute(int offset)
{
    return (_accumulatorOffsetAttribute.find(offset) != _accumulatorOffsetAttribute.end() ? _accumulatorOffsetAttribute.find(offset)->second : PointAttribute::Unknown);
}

bool CtiCCTwoWayPoints::setTwoWayPointId(CtiPointType_t pointtype, int offset, long pointId)
{
    PointAttribute pa = getAttribute(pointtype, offset);
    if (pa == PointAttribute::Unknown)
    {
        return false;
    }
    try
    {
        LitePoint p = getPointByAttribute(pa);
        p.setPointId(pointId);
        p.setPointOffset(offset);
        p.setPointType(pointtype);
        _attributes[pa] = p;
        _pointidPointtypeMap.insert(std::make_pair(pointId, pointtype));
    }
    catch ( const MissingPointAttribute & missingAttribute )
    {
        CtiLockGuard<CtiLogger> logger_guard(dout);
        dout << CtiTime() << " - ** " << missingAttribute.what() << std::endl;
        return false;
    }
    return true;

}
double CtiCCTwoWayPoints::getPointValueByAttribute(PointAttribute attribute, const double sentinel)
{
    double value = sentinel;
    _pointValues.getPointValue(getPointIdByAttribute(attribute), value);
    return value;
}

bool CtiCCTwoWayPoints::isTimestampNew(long pointID, CtiTime timestamp)
{
    bool retVal = true;
    CtiTime prevTime = gInvalidCtiTime;
    if (_pointValues.getPointTime(pointID, prevTime) && timestamp <= prevTime)
    {
        retVal = false;
    }
    return retVal;
}

bool CtiCCTwoWayPoints::setTwoWayStatusPointValue(long pointID, long value, CtiTime timestamp)
{
    bool retVal = false;
    if (_pointidPointtypeMap.find(pointID) == _pointidPointtypeMap.end())
        return retVal;
    if ( _pointidPointtypeMap.find(pointID)->second == StatusPointType &&
         (retVal = isTimestampNew(pointID, timestamp)) )
    {
        _pointValues.addPointValue(pointID, value, timestamp);
        _dirty = true;
    }
    return retVal;
}
bool CtiCCTwoWayPoints::setTwoWayAnalogPointValue(long pointID, double value, CtiTime timestamp)
{
    bool retVal = false;
    if (_pointidPointtypeMap.find(pointID) == _pointidPointtypeMap.end())
        return retVal;
    if ( _pointidPointtypeMap.find(pointID)->second == AnalogPointType &&
         (retVal = isTimestampNew(pointID, timestamp)) )
    {
        _pointValues.addPointValue(pointID, value, timestamp);
        _dirty = true;
    }
    return retVal;
}
bool CtiCCTwoWayPoints::setTwoWayPulseAccumulatorPointValue(long pointID, double value, CtiTime timestamp)
{
    bool retVal = false;
    if (_pointidPointtypeMap.find(pointID) == _pointidPointtypeMap.end())
        return retVal;
    if ( _pointidPointtypeMap.find(pointID)->second == PulseAccumulatorPointType &&
         (retVal = isTimestampNew(pointID, timestamp)) )
    {
        _pointValues.addPointValue(pointID, value, timestamp);
        _dirty = true;
    }
    return retVal;
}



CtiCCTwoWayPoints& CtiCCTwoWayPoints::addAllCBCPointsToRegMsg(std::set<long>& pointList)
{
    AttributePoint::const_iterator iter = _attributes.begin();
    while ( iter != _attributes.end() )
    {
        if ( (iter->second).getPointId() > 0)
        {
            pointList.insert((iter->second).getPointId());
        }
        iter++;
    }
    return *this;
}


struct ColumnMapping
{
    enum
    {
        Boolean,
        Long,
        Double

    } type;
    const char *name;
    const PointAttribute &attribute;
}
static const TwoWayColumns[] =
{
    { ColumnMapping::Boolean, "recloseblocked",         PointAttribute::ReCloseBlocked         },
    { ColumnMapping::Boolean, "controlmode",            PointAttribute::ControlMode            },
    { ColumnMapping::Boolean, "autovoltcontrol",        PointAttribute::AutoVoltControl        },
    { ColumnMapping::Boolean, "opfailedneutralcurrent", PointAttribute::OpFailedNeutralCurrent },
    { ColumnMapping::Boolean, "neutralcurrentfault",    PointAttribute::NeutralCurrentFault    },
    { ColumnMapping::Boolean, "badrelay",               PointAttribute::BadRelay               },
    { ColumnMapping::Boolean, "dailymaxops",            PointAttribute::DailyMaxOps            },
    { ColumnMapping::Boolean, "voltagedeltaabnormal",   PointAttribute::VoltageDeltaAbnormal   },
    { ColumnMapping::Boolean, "tempalarm",              PointAttribute::TempAlarm              },
    { ColumnMapping::Boolean, "dstactive",              PointAttribute::DSTActive              },
    { ColumnMapping::Boolean, "neutrallockout",         PointAttribute::NeutralLockout         },
    { ColumnMapping::Boolean, "ignoredindicator",       PointAttribute::IgnoredIndicator       },
    { ColumnMapping::Double,  "voltage",                PointAttribute::CbcVoltage             },
    { ColumnMapping::Double,  "highvoltage",            PointAttribute::HighVoltage            },
    { ColumnMapping::Double,  "lowvoltage",             PointAttribute::LowVoltage             },
    { ColumnMapping::Double,  "deltavoltage",           PointAttribute::DeltaVoltage           },
    { ColumnMapping::Long,    "analoginputone",         PointAttribute::AnalogInput1           },
    { ColumnMapping::Double,  "temp",                   PointAttribute::Temperature            },
    { ColumnMapping::Long,    "rssi",                   PointAttribute::RSSI                   },
    { ColumnMapping::Long,    "ignoredreason",          PointAttribute::IgnoredReason          },
    { ColumnMapping::Long,    "totalopcount",           PointAttribute::TotalOpCount           },
    { ColumnMapping::Long,    "uvopcount",              PointAttribute::UvCount                },
    { ColumnMapping::Long,    "ovopcount",              PointAttribute::OvCount                },
    { ColumnMapping::Double,  "uvsetpoint",             PointAttribute::UvThreshold            },
    { ColumnMapping::Double,  "ovsetpoint",             PointAttribute::OvThreshold            },
    { ColumnMapping::Long,    "ovuvtracktime",          PointAttribute::OVUVTrackTime          },
    { ColumnMapping::Long,    "neutralcurrentsensor",        PointAttribute::NeutralCurrentSensor         },
    { ColumnMapping::Double,  "neutralcurrentalarmsetpoint", PointAttribute::NeutralCurrentAlarmThreshold },
    { ColumnMapping::Long,    "ipaddress",              PointAttribute::UDPIpAddress           },
    { ColumnMapping::Long,    "udpport",                PointAttribute::UDPPortNumber          },
};

void CtiCCTwoWayPoints::setDynamicData(Cti::RowReader& rdr, LONG cbcState, CtiTime timestamp)
{



    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::CapacitorBankState), cbcState, timestamp);
    for each( const ColumnMapping &cm in TwoWayColumns )
    {
        double value;



        if( cm.type == ColumnMapping::Boolean )
        {
            string tempBoolString;
            rdr[cm.name] >> tempBoolString;
            value = ciStringEqual(tempBoolString, "y");
        }
        else if( cm.type == ColumnMapping::Long )
        {
            long tempLong;
            rdr[cm.name] >> tempLong;
            value = tempLong;
        }
        else  //  cm.type == ColumnMapping::Double
        {
            rdr[cm.name] >> value;
        }

        _pointValues.addPointValue(getPointIdByAttribute(cm.attribute), value, timestamp);
    }

    rdr["ovuvcountresetdate"] >> _ovuvCountResetDate; //toADD
    rdr["lastovuvdatetime"] >> _lastOvUvDateTime; //toAdd

    int lastControlReason;
    rdr["lastcontrol"] >> lastControlReason;
    decodeLastControlReasonFromDB( lastControlReason, timestamp );

    int condition;
    rdr["condition"] >> condition;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::UvCondition), !!(condition & 0x01), timestamp);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::OvCondition), !!(condition & 0x02), timestamp);

    _insertDynamicDataFlag = false;
    _dirty = false;
}


// ------------------------------


CtiCCTwoWayPointsCbcDnp::CtiCCTwoWayPointsCbcDnp( long paoid, std::string paotype  )
    :   CtiCCTwoWayPoints(paoid, paotype)
{
    _statusOffsetAttribute
        = boost::assign::map_list_of
            (     1, PointAttribute::CapacitorBankState )
                ;
}

std::string CtiCCTwoWayPointsCbcDnp::getLastControlText()
{
    return "Uninitialized";
}

int CtiCCTwoWayPointsCbcDnp::encodeLastControlReasonForDB()
{
    return 0;
}

void CtiCCTwoWayPointsCbcDnp::decodeLastControlReasonFromDB( const int lastControlReason, const CtiTime & timestamp )
{
    // empty
}


// ------------------------------


CtiCCTwoWayPointsCbc702x::CtiCCTwoWayPointsCbc702x( long paoid, std::string paotype  )
    :   CtiCCTwoWayPoints(paoid, paotype)
{
    _analogOffsetAttribute
        = boost::assign::map_list_of
            (     5, PointAttribute::CbcVoltage                     )
            (     6, PointAttribute::HighVoltage                    )
            (     7, PointAttribute::LowVoltage                     )
            (     8, PointAttribute::DeltaVoltage                   )
            (     9, PointAttribute::AnalogInput1                   )
            (    10, PointAttribute::Temperature                    )
            (    13, PointAttribute::RSSI                           )
            (    14, PointAttribute::IgnoredReason                  )
            ( 10001, PointAttribute::VoltageControl                 )
            ( 10002, PointAttribute::UvThreshold                    )
            ( 10003, PointAttribute::OvThreshold                    )
            ( 10004, PointAttribute::OVUVTrackTime                  )
            ( 10010, PointAttribute::NeutralCurrentSensor           )
            ( 10011, PointAttribute::NeutralCurrentAlarmThreshold   )
            ( 10026, PointAttribute::TimeTempSeasonOne              )
            ( 10042, PointAttribute::TimeTempSeasonTwo              )
            ( 10068, PointAttribute::VarControl                     )
            ( 20001, PointAttribute::UDPIpAddress                   )
            ( 20002, PointAttribute::UDPPortNumber                  )
                ;

    _statusOffsetAttribute
        = boost::assign::map_list_of
            (     1, PointAttribute::CapacitorBankState             )
            (     2, PointAttribute::ReCloseBlocked                 )
            (     3, PointAttribute::ControlMode                    )
            (     4, PointAttribute::AutoVoltControl                )
            (     5, PointAttribute::LastControlLocal               )
            (     6, PointAttribute::LastControlRemote              )
            (     7, PointAttribute::LastControlOvUv                )
            (     8, PointAttribute::LastControlNeutralFault        )
            (     9, PointAttribute::LastControlScheduled           )
            (    10, PointAttribute::LastControlDigital             )
            (    11, PointAttribute::LastControlAnalog              )
            (    12, PointAttribute::LastControlTemperature         )
            (    13, PointAttribute::OvCondition                    )
            (    14, PointAttribute::UvCondition                    )
            (    15, PointAttribute::OpFailedNeutralCurrent         )
            (    16, PointAttribute::NeutralCurrentFault            )
            (    24, PointAttribute::BadRelay                       )
            (    25, PointAttribute::DailyMaxOps                    )
            (    26, PointAttribute::VoltageDeltaAbnormal           )
            (    27, PointAttribute::TempAlarm                      )
            (    28, PointAttribute::DSTActive                      )
            (    29, PointAttribute::NeutralLockout                 )
            (    34, PointAttribute::IgnoredIndicator               )
                ;

    _accumulatorOffsetAttribute
        = boost::assign::map_list_of
            (     1, PointAttribute::TotalOpCount                   )
            (     2, PointAttribute::UvCount                        )
            (     3, PointAttribute::OvCount                        )
                ;
}

std::string CtiCCTwoWayPointsCbc702x::getLastControlText()
{
    string retVal = "Uninitialized";
    if ((LONG) getPointValueByAttribute(PointAttribute::LastControlRemote) > 0 )
        retVal = "Remote";
    else if (getPointValueByAttribute(PointAttribute::LastControlLocal) > 0 )
        retVal = "Local";
    else if (getPointValueByAttribute(PointAttribute::LastControlOvUv) > 0 )
        retVal = "OvUv";
    else if (getPointValueByAttribute(PointAttribute::LastControlNeutralFault) > 0 )
        retVal = "NeutralFault";
    else if (getPointValueByAttribute(PointAttribute::LastControlScheduled) > 0 )
        retVal = "Schedule";
    else if (getPointValueByAttribute(PointAttribute::LastControlDigital) > 0 )
        retVal = "Digital";
    else if (getPointValueByAttribute(PointAttribute::LastControlAnalog) > 0 )
        retVal = "Analog";
    else if (getPointValueByAttribute(PointAttribute::LastControlTemperature) > 0 )
        retVal = "Temp";
    return retVal;
}

int CtiCCTwoWayPointsCbc702x::encodeLastControlReasonForDB()
{
    int lastControlReason = 0;

    lastControlReason |= 0x01 * !!getPointValueByAttribute(PointAttribute::LastControlLocal);
    lastControlReason |= 0x02 * !!getPointValueByAttribute(PointAttribute::LastControlRemote);
    lastControlReason |= 0x04 * !!getPointValueByAttribute(PointAttribute::LastControlOvUv);
    lastControlReason |= 0x08 * !!getPointValueByAttribute(PointAttribute::LastControlNeutralFault);
    lastControlReason |= 0x10 * !!getPointValueByAttribute(PointAttribute::LastControlScheduled);
    lastControlReason |= 0x20 * !!getPointValueByAttribute(PointAttribute::LastControlDigital);
    lastControlReason |= 0x40 * !!getPointValueByAttribute(PointAttribute::LastControlAnalog);
    lastControlReason |= 0x80 * !!getPointValueByAttribute(PointAttribute::LastControlTemperature);

    return lastControlReason;
}

void CtiCCTwoWayPointsCbc702x::decodeLastControlReasonFromDB( const int lastControlReason, const CtiTime & timestamp )
{
    setTwoWayStatusPointValue(getPointIdByAttribute(PointAttribute::LastControlLocal),        !!(lastControlReason & 0x01), timestamp);
    setTwoWayStatusPointValue(getPointIdByAttribute(PointAttribute::LastControlRemote),       !!(lastControlReason & 0x02), timestamp);
    setTwoWayStatusPointValue(getPointIdByAttribute(PointAttribute::LastControlOvUv),         !!(lastControlReason & 0x04), timestamp);
    setTwoWayStatusPointValue(getPointIdByAttribute(PointAttribute::LastControlNeutralFault), !!(lastControlReason & 0x08), timestamp);
    setTwoWayStatusPointValue(getPointIdByAttribute(PointAttribute::LastControlScheduled),    !!(lastControlReason & 0x10), timestamp);
    setTwoWayStatusPointValue(getPointIdByAttribute(PointAttribute::LastControlDigital),      !!(lastControlReason & 0x20), timestamp);
    setTwoWayStatusPointValue(getPointIdByAttribute(PointAttribute::LastControlAnalog),       !!(lastControlReason & 0x40), timestamp);
    setTwoWayStatusPointValue(getPointIdByAttribute(PointAttribute::LastControlTemperature),  !!(lastControlReason & 0x80), timestamp);
}


// ------------------------------


const std::vector<std::string>   CtiCCTwoWayPointsCbc802x::lastControlDecoder
    = boost::assign::list_of
        ( "Manual" )
        ( "SCADA Override" )
        ( "Fault Current" )
        ( "Emergency Voltage" )
        ( "Time ONOFF" )
        ( "OVUV Control" )
        ( "VAR" )
        ( "Va" )
        ( "Vb" )
        ( "Vc" )
        ( "Ia" )
        ( "Ib" )
        ( "Ic" )
        ( "Temp" )
        ( "Remote" )
        ( "Time" )
        ( "Reclose Block" )
            ;

CtiCCTwoWayPointsCbc802x::CtiCCTwoWayPointsCbc802x( long paoid, std::string paotype  )
    :   CtiCCTwoWayPoints(paoid, paotype)
{
    _analogOffsetAttribute
        = boost::assign::map_list_of
            (     2, PointAttribute::LastControlReason      )
            (    12, PointAttribute::CbcVoltage             )
            ( 10001, PointAttribute::OvThreshold            )
            ( 10002, PointAttribute::UvThreshold            )
            ( 10318, PointAttribute::VoltageControl         )
                ;

    _statusOffsetAttribute
        = boost::assign::map_list_of
            (     1, PointAttribute::CapacitorBankState     )
            (     3, PointAttribute::NeutralLockout         )
            (    72, PointAttribute::VoltageDeltaAbnormal   )
            (    84, PointAttribute::ReCloseBlocked         )
            (    86, PointAttribute::ControlMode            )
            (    89, PointAttribute::AutoVoltControl        )
                ;

    _accumulatorOffsetAttribute
        = boost::assign::map_list_of
            (     1, PointAttribute::TotalOpCount           )
            (     2, PointAttribute::OvCount                )
            (     3, PointAttribute::UvCount                )
            (     4, PointAttribute::CloseOpCount           )
            (     5, PointAttribute::OpenOpCount            )
                ;
}

std::string CtiCCTwoWayPointsCbc802x::getLastControlText()
{
    std::string retVal = "Uninitialized";

    if ( getPointIdByAttribute( PointAttribute::LastControlReason ) > 0 )
    {
        const int value = getPointValueByAttribute( PointAttribute::LastControlReason,
                                                    lastControlDecoder.size() );

        if ( 0 <= value && value < lastControlDecoder.size() )
        {
            retVal = lastControlDecoder[ value ];
        }
    }

    return retVal;
}

int CtiCCTwoWayPointsCbc802x::encodeLastControlReasonForDB()
{
    int lastControlReason = -1;

    if ( getPointIdByAttribute( PointAttribute::LastControlReason ) > 0 )
    {
        lastControlReason = getPointValueByAttribute( PointAttribute::LastControlReason, lastControlReason );
    }

    return lastControlReason;
}


void CtiCCTwoWayPointsCbc802x::decodeLastControlReasonFromDB( const int lastControlReason, const CtiTime & timestamp )
{
    const long pointID = getPointIdByAttribute( PointAttribute::LastControlReason );

    if ( pointID > 0 && lastControlReason != -1 )
    {
        setTwoWayAnalogPointValue( pointID, lastControlReason, timestamp );
    }
}


// ------------------------------


CtiCCTwoWayPoints * CtiCCTwoWayPointsFactory::Create( const long paoID, const std::string & paoType )
{
    if ( stringContainsIgnoreCase( paoType, "CBC 702" ) )
    {
        return new CtiCCTwoWayPointsCbc702x( paoID, paoType );
    }
    if ( stringContainsIgnoreCase( paoType, "CBC 802" ) )
    {
        return new CtiCCTwoWayPointsCbc802x( paoID, paoType );
    }
    if ( stringContainsIgnoreCase( paoType, "CBC DNP" ) )
    {
        return new CtiCCTwoWayPointsCbcDnp( paoID, paoType );
    }

    // Apparently 1-way devices need one of these guys even though they don't use it for anything,
    // returning a NULL here gives null pointer exceptions in 1-way code. Original behavior gave a set
    //  of CBC8000 points to 1-way devices, so maintain that behavior.
    // return 0;
    return new CtiCCTwoWayPointsCbc802x( paoID, paoType );
}

