#include "precompiled.h"

#include "cctwowaycbcpoints.h"
#include "ccid.h"
#include "dev_cbc7020.h"
#include "dev_cbc8020.h"
#include "database_writer.h"
#include "ccutil.h"

using std::string;
using std::endl;

extern unsigned long _CC_DEBUG;
using Cti::CapControl::MissingPointAttribute;

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCTwoWayPoints::CtiCCTwoWayPoints(long paoid, string paotype)
{
    const PointAttribute attributes[] =
    {
        PointAttribute::CbcVoltage,
        PointAttribute::HighVoltage,
        PointAttribute::LowVoltage,
        PointAttribute::DeltaVoltage,
        PointAttribute::AnalogInput1,
        PointAttribute::Temperature,
        PointAttribute::RSSI,
        PointAttribute::IgnoredReason,
        PointAttribute::VoltageControl,
        PointAttribute::UvThreshold,
        PointAttribute::OvThreshold,
        PointAttribute::OVUVTrackTime,
        PointAttribute::NeutralCurrentSensor,
        PointAttribute::NeutralCurrentAlarmThreshold,
        PointAttribute::TimeTempSeasonOne,
        PointAttribute::TimeTempSeasonTwo,
        PointAttribute::VarControl,
        PointAttribute::UDPIpAddress,
        PointAttribute::UDPPortNumber,
        PointAttribute::CapacitorBankState,
        PointAttribute::ReCloseBlocked,
        PointAttribute::ControlMode,
        PointAttribute::AutoVoltControl,
        PointAttribute::LastControlLocal,
        PointAttribute::LastControlRemote,
        PointAttribute::LastControlOvUv,
        PointAttribute::LastControlNeutralFault,
        PointAttribute::LastControlScheduled,
        PointAttribute::LastControlDigital,
        PointAttribute::LastControlAnalog,
        PointAttribute::LastControlTemperature,
        PointAttribute::OvCondition,
        PointAttribute::UvCondition,
        PointAttribute::OpFailedNeutralCurrent,
        PointAttribute::NeutralCurrentFault,
        PointAttribute::BadRelay,
        PointAttribute::DailyMaxOps,
        PointAttribute::VoltageDeltaAbnormal,
        PointAttribute::TempAlarm,
        PointAttribute::DSTActive,
        PointAttribute::NeutralLockout,
        PointAttribute::IgnoredIndicator,
        PointAttribute::TotalOpCount,
        PointAttribute::UvCount,
        PointAttribute::OvCount
    };

    for each ( const PointAttribute attribute in attributes )
    {
        _attributes.insert( std::make_pair( attribute, LitePoint()) );
    }

    if (stringContainsIgnoreCase(paotype, "cbc 702"))
    {
        Cti::Devices::Cbc7020Device::initOffsetAttributeMaps(_analogOffsetAttribute, _statusOffsetAttribute, _accumulatorOffsetAttribute);
    }
    else
    {
        Cti::Devices::Cbc8020Device::initOffsetAttributeMaps(_analogOffsetAttribute, _statusOffsetAttribute, _accumulatorOffsetAttribute);
    }

    _pointidPointtypeMap.clear();

    _paoid = paoid;
    _paotype = paotype;

    _lastControlReason = 0;

    _insertDynamicDataFlag = true;
    _dirty = true;

    return;
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

    _lastControlReason = 0;
    _lastControlReason |= 0x01 * !!getPointValueByAttribute(PointAttribute::LastControlLocal);
    _lastControlReason |= 0x02 * !!getPointValueByAttribute(PointAttribute::LastControlRemote);
    _lastControlReason |= 0x04 * !!getPointValueByAttribute(PointAttribute::LastControlOvUv);
    _lastControlReason |= 0x08 * !!getPointValueByAttribute(PointAttribute::LastControlNeutralFault);
    _lastControlReason |= 0x10 * !!getPointValueByAttribute(PointAttribute::LastControlScheduled);
    _lastControlReason |= 0x20 * !!getPointValueByAttribute(PointAttribute::LastControlDigital);
    _lastControlReason |= 0x40 * !!getPointValueByAttribute(PointAttribute::LastControlAnalog);
    _lastControlReason |= 0x80 * !!getPointValueByAttribute(PointAttribute::LastControlTemperature);

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
            << _lastControlReason
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

        if(updater.execute())    // No error occured!
        {
            _dirty = false;
        }
        else
        {
            string loggedSQLstring = updater.asString();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  " << loggedSQLstring << endl;
            }
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
                 << _lastControlReason
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

        if( _CC_DEBUG & CC_DEBUG_DATABASE )
        {
            string loggedSQLstring = dbInserter.asString();
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - " << loggedSQLstring << endl;
            }
        }

        if(dbInserter.execute())    // No error occured!
        {
            _insertDynamicDataFlag = false;
            _dirty = false;
        }
        else
        {
            string loggedSQLstring = dbInserter.asString();
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                dout << "  " << loggedSQLstring << endl;
            }
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
double CtiCCTwoWayPoints::getPointValueByAttribute(PointAttribute attribute)
{
    double value = 0;
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
bool CtiCCTwoWayPoints::setTwoWayAnalogPointValue(long pointID, long value, CtiTime timestamp)
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
bool CtiCCTwoWayPoints::setTwoWayPulseAccumulatorPointValue(long pointID, long value, CtiTime timestamp)
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
    { ColumnMapping::Long,    "temp",                   PointAttribute::Temperature            },
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

    INT condition;
    rdr["lastcontrol"] >>_lastControlReason;
    rdr["condition"] >> condition;

    rdr["ovuvcountresetdate"] >> _ovuvCountResetDate; //toADD
    rdr["lastovuvdatetime"] >> _lastOvUvDateTime; //toAdd

    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlLocal),        !!(_lastControlReason & 0x01), timestamp);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlRemote),       !!(_lastControlReason & 0x02), timestamp);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlOvUv),         !!(_lastControlReason & 0x04), timestamp);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlNeutralFault), !!(_lastControlReason & 0x08), timestamp);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlScheduled),    !!(_lastControlReason & 0x10), timestamp);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlDigital),      !!(_lastControlReason & 0x20), timestamp);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlAnalog),       !!(_lastControlReason & 0x40), timestamp);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlTemperature),  !!(_lastControlReason & 0x80), timestamp);

    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::UvCondition), !!(condition & 0x01), timestamp);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::OvCondition), !!(condition & 0x02), timestamp);

    _insertDynamicDataFlag = false;
    _dirty = false;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::operator=(const CtiCCTwoWayPoints& right)
{
    if( this != &right )
    {
        _paoid = right._paoid;
        _paotype = right._paotype;

        _lastControlReason = right._lastControlReason;

        _attributes = right._attributes;
        _pointValues = right._pointValues;
        _pointidPointtypeMap = right._pointidPointtypeMap;
        _statusOffsetAttribute = right._statusOffsetAttribute;
        _analogOffsetAttribute = right._analogOffsetAttribute;
        _accumulatorOffsetAttribute = right._accumulatorOffsetAttribute;

        _insertDynamicDataFlag = right._insertDynamicDataFlag;
        _dirty = right._dirty;
    }
    return *this;
}

int CtiCCTwoWayPoints::operator==(const CtiCCTwoWayPoints& right) const
{
    return getPAOId() == right.getPAOId();
}
int CtiCCTwoWayPoints::operator!=(const CtiCCTwoWayPoints& right) const
{
    return getPAOId() != right.getPAOId();
}


string CtiCCTwoWayPoints::getLastControlText()
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



