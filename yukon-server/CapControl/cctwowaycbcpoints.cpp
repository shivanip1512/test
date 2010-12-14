/*---------------------------------------------------------------------------
        Filename:  cctwowaycbcpoints.cpp

        Programmer:  Julie Richter

        Description:    Source file for CtiCCTwoWayPoints.
                        CtiCCTwoWayPoints maintains the state and handles
                        the persistence of cap banks for Cap Control.

        Initial Date:  8/30/2001

        COPYRIGHT:  Copyright (C) Cannon Technologies, Inc., 2001
---------------------------------------------------------------------------*/
#include "yukon.h"

#include "dbaccess.h"
#include "cctwowaycbcpoints.h"
#include "cccapbank.h"
#include "ccid.h"
#include "pointdefs.h"
#include "pointtypes.h"
#include "logger.h"
#include "resolvers.h"
#include "msg_ptreg.h"
#include "database_writer.h"
#include "ccutil.h"

extern ULONG _CC_DEBUG;
using Cti::CapControl::MissingPointAttribute;

/*---------------------------------------------------------------------------
    Constructors
---------------------------------------------------------------------------*/
CtiCCTwoWayPoints::CtiCCTwoWayPoints(LONG paoid, string paotype)
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
        PointAttribute::UvSetPoint,                                      
        PointAttribute::OvSetPoint,                                      
        PointAttribute::OVUVTrackTime,                                   
        PointAttribute::NeutralCurrentSensor,                            
        PointAttribute::NeutralCurrentAlarmSetPoint,                     
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
    _analogOffsetAttribute.insert( std::make_pair( 5,  PointAttribute::CbcVoltage) );
    _analogOffsetAttribute.insert( std::make_pair( 6,  PointAttribute::HighVoltage) );
    _analogOffsetAttribute.insert( std::make_pair( 7,  PointAttribute::LowVoltage) );
    _analogOffsetAttribute.insert( std::make_pair( 8,  PointAttribute::DeltaVoltage) );
    _analogOffsetAttribute.insert( std::make_pair( 9,  PointAttribute::AnalogInput1) );
    _analogOffsetAttribute.insert( std::make_pair( 10, PointAttribute::Temperature) );
    _analogOffsetAttribute.insert( std::make_pair( 13, PointAttribute::RSSI) );
    _analogOffsetAttribute.insert( std::make_pair( 14, PointAttribute::IgnoredReason) );

    //dnp analog output points have offsets starting with 10000
    _analogOffsetAttribute.insert( std::make_pair( 10001, PointAttribute::VoltageControl) );
    _analogOffsetAttribute.insert( std::make_pair( 10002, PointAttribute::UvSetPoint) );
    _analogOffsetAttribute.insert( std::make_pair( 10003, PointAttribute::OvSetPoint) );
    _analogOffsetAttribute.insert( std::make_pair( 10004, PointAttribute::OVUVTrackTime) );
    _analogOffsetAttribute.insert( std::make_pair( 10010, PointAttribute::NeutralCurrentSensor) );
    _analogOffsetAttribute.insert( std::make_pair( 10011, PointAttribute::NeutralCurrentAlarmSetPoint) );
    _analogOffsetAttribute.insert( std::make_pair( 10026, PointAttribute::TimeTempSeasonOne) );
    _analogOffsetAttribute.insert( std::make_pair( 10042, PointAttribute::TimeTempSeasonTwo) );
    _analogOffsetAttribute.insert( std::make_pair( 10068, PointAttribute::VarControl) );
    _analogOffsetAttribute.insert( std::make_pair( 20001, PointAttribute::UDPIpAddress) );
    _analogOffsetAttribute.insert( std::make_pair( 20002, PointAttribute::UDPPortNumber) );

    _statusOffsetAttribute.insert( std::make_pair( 1, PointAttribute::CapacitorBankState) );
    _statusOffsetAttribute.insert( std::make_pair( 2, PointAttribute::ReCloseBlocked) );
    _statusOffsetAttribute.insert( std::make_pair( 3, PointAttribute::ControlMode) );
    _statusOffsetAttribute.insert( std::make_pair( 4, PointAttribute::AutoVoltControl) );
    _statusOffsetAttribute.insert( std::make_pair( 5, PointAttribute::LastControlLocal) );
    _statusOffsetAttribute.insert( std::make_pair( 6, PointAttribute::LastControlRemote) );
    _statusOffsetAttribute.insert( std::make_pair( 7, PointAttribute::LastControlOvUv) );
    _statusOffsetAttribute.insert( std::make_pair( 8, PointAttribute::LastControlNeutralFault) );
    _statusOffsetAttribute.insert( std::make_pair( 9, PointAttribute::LastControlScheduled) );
    _statusOffsetAttribute.insert( std::make_pair( 10, PointAttribute::LastControlDigital) );
    _statusOffsetAttribute.insert( std::make_pair( 11, PointAttribute::LastControlAnalog) );
    _statusOffsetAttribute.insert( std::make_pair( 12, PointAttribute::LastControlTemperature) );
    _statusOffsetAttribute.insert( std::make_pair( 13, PointAttribute::OvCondition) );
    _statusOffsetAttribute.insert( std::make_pair( 14, PointAttribute::UvCondition) );
    _statusOffsetAttribute.insert( std::make_pair( 15, PointAttribute::OpFailedNeutralCurrent) );
    _statusOffsetAttribute.insert( std::make_pair( 16, PointAttribute::NeutralCurrentFault) );
    _statusOffsetAttribute.insert( std::make_pair( 24, PointAttribute::BadRelay) );
    _statusOffsetAttribute.insert( std::make_pair( 25, PointAttribute::DailyMaxOps) );
    _statusOffsetAttribute.insert( std::make_pair( 26, PointAttribute::VoltageDeltaAbnormal) );
    _statusOffsetAttribute.insert( std::make_pair( 27, PointAttribute::TempAlarm) );
    _statusOffsetAttribute.insert( std::make_pair( 28, PointAttribute::DSTActive) );
    _statusOffsetAttribute.insert( std::make_pair( 29, PointAttribute::NeutralLockout) );
    _statusOffsetAttribute.insert( std::make_pair( 34, PointAttribute::IgnoredIndicator) );

    _accumulatorOffsetAttribute.insert( std::make_pair( 1, PointAttribute::TotalOpCount) );
    _accumulatorOffsetAttribute.insert( std::make_pair( 2, PointAttribute::UvCount) );
    _accumulatorOffsetAttribute.insert( std::make_pair( 3, PointAttribute::OvCount) );


    _pointidPointtypeMap.clear();

    _paoid = paoid;
    _paotype = paotype;
    
    _lastControlReason = 0;

    _insertDynamicDataFlag = TRUE;
    _dirty = TRUE;

    return;
}


CtiCCTwoWayPoints::CtiCCTwoWayPoints(const CtiCCTwoWayPoints& twoWayPt)
{
    operator=(twoWayPt);
}

/*---------------------------------------------------------------------------
    Destructor
---------------------------------------------------------------------------*/
CtiCCTwoWayPoints::~CtiCCTwoWayPoints()
{

    _attributes.clear();
}

LONG CtiCCTwoWayPoints::getPAOId() const
{
    return _paoid;
}



CtiCCTwoWayPoints& CtiCCTwoWayPoints::setPAOId(LONG paoId)
{
    _paoid = paoId;
    return *this;
}


BOOL CtiCCTwoWayPoints::isDirty()
{
    return _dirty;
}
void CtiCCTwoWayPoints::dumpDynamicData(Cti::Database::DatabaseConnection& conn, CtiTime& currentDateTime)
{
    {
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
            _lastControlReason = ( ( (LONG) getPointValueByAttribute(PointAttribute::LastControlLocal) & 0x01)            ||
                                ( ((LONG)getPointValueByAttribute(PointAttribute::LastControlRemote) & 0x01 )        << 1 ) ||
                                ( ((LONG)getPointValueByAttribute(PointAttribute::LastControlOvUv) & 0x01 )         << 2 ) ||
                                ( ((LONG)getPointValueByAttribute(PointAttribute::LastControlNeutralFault) & 0x01)  << 3 ) ||
                                ( ((LONG)getPointValueByAttribute(PointAttribute::LastControlScheduled) & 0x01)     << 4 ) ||
                                ( ((LONG)getPointValueByAttribute(PointAttribute::LastControlDigital) & 0x01)       << 5 ) ||
                                ( ((LONG)getPointValueByAttribute(PointAttribute::LastControlAnalog) & 0x01 )       << 6 ) ||
                                ( ((LONG)getPointValueByAttribute(PointAttribute::LastControlTemperature) & 0x01 )  << 7 ) );
            INT condition = 0;
            if (getPointValueByAttribute(PointAttribute::UvCondition))
                condition = 1;
            else if (getPointValueByAttribute(PointAttribute::OvCondition))
                condition = 2;
            else
                condition = 0;

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
                << getPointValueByAttribute(PointAttribute::UvSetPoint) 
                << getPointValueByAttribute(PointAttribute::OvSetPoint)
                << getPointValueByAttribute(PointAttribute::OVUVTrackTime) 
                << _lastOvUvDateTime
                << getPointValueByAttribute(PointAttribute::NeutralCurrentSensor) 
                << getPointValueByAttribute(PointAttribute::NeutralCurrentAlarmSetPoint) 
                << getPointValueByAttribute(PointAttribute::UDPIpAddress) 
                << getPointValueByAttribute(PointAttribute::UDPPortNumber) 
                << _paoid;

            if(updater.execute())    // No error occured!
            {
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
                {
                    string loggedSQLstring = updater.asString();
                    {
                        CtiLockGuard<CtiLogger> doubt_guard(dout);
                        dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                        dout << "  " << loggedSQLstring << endl;
                    }
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

            _lastControlReason = ( ( (LONG)getPointValueByAttribute(PointAttribute::LastControlLocal)       & 0x01)  ||
                                ((LONG)getPointValueByAttribute(PointAttribute::LastControlRemote)       & 0x02)  ||
                                ((LONG)getPointValueByAttribute(PointAttribute::LastControlOvUv)         & 0x04)  ||
                                ((LONG)getPointValueByAttribute(PointAttribute::LastControlNeutralFault) & 0x08)  ||
                                ((LONG)getPointValueByAttribute(PointAttribute::LastControlScheduled)    & 0x10)  ||
                                ((LONG)getPointValueByAttribute(PointAttribute::LastControlDigital)      & 0x20)  ||
                                ((LONG)getPointValueByAttribute(PointAttribute::LastControlAnalog)       & 0x40)  ||
                                ((LONG)getPointValueByAttribute(PointAttribute::LastControlTemperature)  & 0x80)  );
            INT condition = 0;
            if (getPointValueByAttribute(PointAttribute::UvCondition))
                condition = 1;
            else if (getPointValueByAttribute(PointAttribute::OvCondition))
                condition = 2;
            else
                condition = 0;


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
                     << getPointValueByAttribute(PointAttribute::UvSetPoint)                                  
                     << getPointValueByAttribute(PointAttribute::OvSetPoint)                                  
                     << getPointValueByAttribute(PointAttribute::OVUVTrackTime)                               
                     << _lastOvUvDateTime                                                                     
                     << getPointValueByAttribute(PointAttribute::NeutralCurrentSensor)                        
                     << getPointValueByAttribute(PointAttribute::NeutralCurrentAlarmSetPoint)                 
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
                _insertDynamicDataFlag = FALSE;
                _dirty = FALSE;
            }
            else
            {
                _dirty = TRUE;
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

BOOL CtiCCTwoWayPoints::setTwoWayPointId(CtiPointType_t pointtype, int offset, LONG pointId)
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

CtiTime CtiCCTwoWayPoints::getPointTimeStampByAttribute(PointAttribute attribute)
{
    CtiTime time = gInvalidCtiTime;
    _pointValues.getPointTime(getPointIdByAttribute(attribute), time);
    return time;
}

BOOL CtiCCTwoWayPoints::setTwoWayStatusPointValue(LONG pointID, LONG value, CtiTime timestamp)
{
    BOOL retVal = false;
    if ( _pointidPointtypeMap.find(pointID)->second == StatusPointType)
        retVal = true;
    _pointValues.addPointValue(pointID, value, timestamp);

    return retVal;

}
BOOL CtiCCTwoWayPoints::setTwoWayAnalogPointValue(LONG pointID, LONG value, CtiTime timestamp)
{
    BOOL retVal = FALSE;
    if ( _pointidPointtypeMap.find(pointID)->second == AnalogPointType)
        retVal = true;
    _pointValues.addPointValue(pointID, value, timestamp);

    
    return retVal;
}
BOOL CtiCCTwoWayPoints::setTwoWayPulseAccumulatorPointValue(LONG pointID, LONG value, CtiTime timestamp)
{
    BOOL retVal = FALSE;
    if ( _pointidPointtypeMap.find(pointID)->second == PulseAccumulatorPointType)
        retVal = true;
    _pointValues.addPointValue(pointID, value, timestamp);

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


void CtiCCTwoWayPoints::setDynamicData(Cti::RowReader& rdr)
{
    INT lastControl;
    INT condition = 0;
    string tempBoolString;
    CtiTime timeNow;
    LONG tempLong;


    rdr["recloseblocked"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::ReCloseBlocked), tempBoolString=="y"?TRUE:FALSE, timeNow);
    rdr["controlmode"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::ControlMode), tempBoolString=="y"?TRUE:FALSE, timeNow);
    rdr["autovoltcontrol"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::AutoVoltControl), tempBoolString=="y"?TRUE:FALSE, timeNow);
    rdr["lastcontrol"] >>_lastControlReason;
    rdr["condition"] >> condition;
    rdr["opfailedneutralcurrent"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::OpFailedNeutralCurrent), tempBoolString=="y"?TRUE:FALSE, timeNow);
    rdr["neutralcurrentfault"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::NeutralCurrentFault), tempBoolString=="y"?TRUE:FALSE, timeNow);
    rdr["badrelay"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::BadRelay), tempBoolString=="y"?TRUE:FALSE, timeNow);
    rdr["dailymaxops"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::DailyMaxOps), tempBoolString=="y"?TRUE:FALSE, timeNow);
    rdr["voltagedeltaabnormal"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::VoltageDeltaAbnormal), tempBoolString=="y"?TRUE:FALSE, timeNow);
    rdr["tempalarm"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::TempAlarm), tempBoolString=="y"?TRUE:FALSE, timeNow);
    rdr["dstactive"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::DSTActive), tempBoolString=="y"?TRUE:FALSE, timeNow);
    rdr["neutrallockout"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::NeutralLockout), tempBoolString=="y"?TRUE:FALSE, timeNow);
    rdr["ignoredindicator"] >> tempBoolString;
    std::transform(tempBoolString.begin(), tempBoolString.end(), tempBoolString.begin(), tolower);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::IgnoredIndicator), tempBoolString=="y"?TRUE:FALSE, timeNow);
    rdr["voltage"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::CbcVoltage), tempLong, timeNow);
    rdr["highvoltage"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::HighVoltage), tempLong, timeNow);
    rdr["lowvoltage"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LowVoltage), tempLong, timeNow);
    rdr["deltavoltage"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::DeltaVoltage), tempLong, timeNow);
    rdr["analoginputone"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::AnalogInput1), tempLong, timeNow);
    rdr["temp"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::Temperature), tempLong, timeNow);
    rdr["rssi"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::RSSI), tempLong, timeNow);
    rdr["ignoredreason"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::IgnoredReason), tempLong, timeNow);
    rdr["totalopcount"] >>tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::TotalOpCount), tempLong, timeNow);
    rdr["uvopcount"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::UvCount), tempLong, timeNow);
    rdr["ovopcount"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::OvCount), tempLong, timeNow);
    rdr["ovuvcountresetdate"] >> _ovuvCountResetDate; //toADD
    rdr["uvsetpoint"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::UvSetPoint), tempLong, timeNow);
    rdr["ovsetpoint"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::OvSetPoint), tempLong, timeNow);
    rdr["ovuvtracktime"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::OVUVTrackTime), tempLong, timeNow);
    rdr["lastovuvdatetime"] >> _lastOvUvDateTime; //toAdd
    rdr["neutralcurrentsensor"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::NeutralCurrentSensor), tempLong, timeNow);
    rdr["neutralcurrentalarmsetpoint"] >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::NeutralCurrentAlarmSetPoint), tempLong, timeNow);
    rdr["ipaddress"]  >> tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::UDPIpAddress), tempLong, timeNow);
    rdr["udpport"] >>  tempLong;
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::UDPPortNumber), tempLong, timeNow);


    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlLocal), lastControl & 0x01, timeNow);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlRemote), lastControl & 0x02, timeNow);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlOvUv), lastControl & 0x04, timeNow);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlNeutralFault), lastControl & 0x08, timeNow);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlScheduled), lastControl & 0x10, timeNow);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlDigital), lastControl & 0x20, timeNow);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlAnalog), lastControl & 0x40, timeNow);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::LastControlTemperature), lastControl & 0x80, timeNow);


    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::UvCondition), condition & 0x01, timeNow);
    _pointValues.addPointValue(getPointIdByAttribute(PointAttribute::OvCondition), condition & 0x02, timeNow);

    _insertDynamicDataFlag = FALSE;
    _dirty = false;


}
/*---------------------------------------------------------------------------
    replicate

    Restores self's operation fields.
---------------------------------------------------------------------------*/
CtiCCTwoWayPoints* CtiCCTwoWayPoints::replicate() const
{
    return(new CtiCCTwoWayPoints(*this));
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


LONG CtiCCTwoWayPoints::getLastControl() 
{
    LONG retVal = 0;

    if (getPointValueByAttribute(PointAttribute::LastControlRemote) > 0 )
        retVal = CC_Remote;
    else if (getPointValueByAttribute(PointAttribute::LastControlLocal) > 0 )
        retVal = CC_Local;
    else if (getPointValueByAttribute(PointAttribute::LastControlOvUv) > 0 )
        retVal = CC_OvUv;
    else if (getPointValueByAttribute(PointAttribute::LastControlNeutralFault) > 0 )
        retVal = CC_NeutralFault;
    else if (getPointValueByAttribute(PointAttribute::LastControlScheduled) > 0 )
        retVal = CC_Scheduled;
    else if (getPointValueByAttribute(PointAttribute::LastControlDigital) > 0 )
        retVal = CC_Digital;
    else if (getPointValueByAttribute(PointAttribute::LastControlAnalog) > 0 )
        retVal = CC_Analog;
    else if (getPointValueByAttribute(PointAttribute::LastControlTemperature) > 0 )
        retVal = CC_Temperature;

    return retVal;
}

INT CtiCCTwoWayPoints::getLastControlReason() const
{
    return _lastControlReason;
}

CtiCCTwoWayPoints& CtiCCTwoWayPoints::setLastControlReason()
{
    if (getPointValueByAttribute(PointAttribute::LastControlRemote) > 0 )
        _lastControlReason = CC_Remote;
    else if (getPointValueByAttribute(PointAttribute::LastControlLocal) > 0 )
        _lastControlReason = CC_Local;
    else if (getPointValueByAttribute(PointAttribute::LastControlOvUv) > 0 )
        _lastControlReason = CC_OvUv;
    else if (getPointValueByAttribute(PointAttribute::LastControlNeutralFault) > 0 )
        _lastControlReason = CC_NeutralFault;
    else if (getPointValueByAttribute(PointAttribute::LastControlScheduled) > 0 )
        _lastControlReason = CC_Scheduled;
    else if (getPointValueByAttribute(PointAttribute::LastControlDigital) > 0 )
        _lastControlReason = CC_Digital;
    else if (getPointValueByAttribute(PointAttribute::LastControlAnalog) > 0 )
        _lastControlReason = CC_Analog;
    else if (getPointValueByAttribute(PointAttribute::LastControlTemperature) > 0 )
        _lastControlReason = CC_Temperature;

    return *this;
}


BOOL CtiCCTwoWayPoints::isLastControlReasonUpdated(LONG pointID,LONG reason )
{
    BOOL retVal = FALSE;
    switch (reason)
    {
        case CC_Remote:
        {
            if (getPointIdByAttribute(PointAttribute::LastControlRemote) ==  pointID && 
                getPointValueByAttribute(PointAttribute::LastControlRemote) == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_Local:
        {
            if (getPointIdByAttribute(PointAttribute::LastControlLocal) ==  pointID && 
                getPointValueByAttribute(PointAttribute::LastControlLocal) == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_OvUv:
        {
            if (getPointIdByAttribute(PointAttribute::LastControlOvUv) ==  pointID && 
                getPointValueByAttribute(PointAttribute::LastControlOvUv) == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_NeutralFault:
        {
            if (getPointIdByAttribute(PointAttribute::LastControlNeutralFault) ==  pointID && 
                getPointValueByAttribute(PointAttribute::LastControlNeutralFault) == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_Scheduled:
        {
            if (getPointIdByAttribute(PointAttribute::LastControlScheduled) ==  pointID && 
                getPointValueByAttribute(PointAttribute::LastControlScheduled) == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_Digital:
        {
            if (getPointIdByAttribute(PointAttribute::LastControlDigital) ==  pointID && 
                getPointValueByAttribute(PointAttribute::LastControlDigital) == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_Analog:
        {
            if (getPointIdByAttribute(PointAttribute::LastControlAnalog) == pointID && 
                getPointValueByAttribute(PointAttribute::LastControlAnalog) == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        case CC_Temperature:
        {
            if (getPointIdByAttribute(PointAttribute::LastControlTemperature) == pointID && 
                getPointValueByAttribute(PointAttribute::LastControlTemperature) == 0)
            {
                retVal = TRUE;
            }
            break;
        }
        default:
            break;
    }

    return retVal;
}


