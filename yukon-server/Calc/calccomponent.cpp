#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <math.h>
#include <iostream>
using namespace std;

#include <rw/thr/mutex.h>

#include "calccomponent.h"
#include "logger.h"
#include "calc.h"

extern BOOL _CALC_DEBUG;

RWDEFINE_NAMED_COLLECTABLE( CtiCalcComponent, "CtiCalcComponent" );

CtiCalcComponent::CtiCalcComponent( const RWCString &componentType, long componentPointId, 
                                    const RWCString &operationType,
                                    double constantValue, const RWCString &functionName )
{
    _updatesInCurrentAvg = 0;
    _valid = TRUE;

    if( componentPointId == 0 && !componentType.compareTo("operation", RWCString::ignoreCase) )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "ERROR creating CtiCalcComponent - operation with ComponentPointID of 0 - setting invalid flag" << endl;
        }
        _valid = FALSE;
    }
    else if( !componentType.compareTo("operation", RWCString::ignoreCase) )
    {
        _componentType = operation;
        _componentPointId = componentPointId;
        _lastUseUpdateNum = 0;

        if( operationType == "+" )          _operationType = addition;
        else if( operationType == "-" )     _operationType = subtraction;
        else if( operationType == "*" )     _operationType = multiplication;
        else if( operationType == "/" )     _operationType = division;
        else if( !operationType.compareTo("push", RWCString::ignoreCase) )  _operationType = push;
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Invalid Operation Type: " << operationType << " ComponentPointID = " << componentPointId << endl;
            }
            _valid = FALSE;
        }

        if( _CALC_DEBUG )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Adding CtiCalcComponent - Normal Operation ComponentPointID = " << componentPointId << endl;
        }

    }
    else if( !componentType.compareTo("constant", RWCString::ignoreCase) )
    {
        _componentType = constant;
        if( constantValue != 0.0 )
            _constantValue = constantValue;
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Why is there a constant with value 0.0 in the equation, ComponentPointID = " << componentPointId << endl;
            }
            _valid = FALSE;
        }

        if( operationType == "+" )          _operationType = addition;
        else if( operationType == "-" )     _operationType = subtraction;
        else if( operationType == "*" )     _operationType = multiplication;
        else if( operationType == "/" )     _operationType = division;
        else if( !operationType.compareTo("push", RWCString::ignoreCase) )  _operationType = push;
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Invalid Operation Type: " << operationType << " ComponentPointID = " << componentPointId << endl;
            }
            _valid = FALSE;
        }

        if( _CALC_DEBUG )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Adding CtiCalcComponent - Constant ComponentPointID = " << componentPointId << " Const: " << _constantValue << endl;
        }
    }
    else if( !componentType.compareTo("function", RWCString::ignoreCase) )
    {
        _componentType = function;
        _functionName = functionName;
        _componentPointId = componentPointId;
        _lastUseUpdateNum = 0;
    }
    else
    {
        _valid = FALSE;   

        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Invalid CtiCalcComponent - ComponentPointID = " << componentPointId << " ComponentType: " << componentType << endl;
        }
    }
}

CtiCalcComponent &CtiCalcComponent::operator=( const CtiCalcComponent &copyFrom )
{
    _componentType = copyFrom._componentType;
    _pointId = copyFrom._pointId;
    _componentPointId = copyFrom._componentPointId;
    _operationType = copyFrom._operationType;
    _constantValue = copyFrom._constantValue;
    _functionName = copyFrom._functionName;
    _pointUpdated = copyFrom._pointUpdated;
    _valid = copyFrom._valid;
    return *this;
}

/*  FIX_ME:  This class has some wacky persistence issues.  I'm not sure how to fix this, and I 
               don't know if save/restoreGuts will ever be used...
void CtiCalcComponent::restoreGuts( RWvistream& aStream )
{
   aStream >> (int &)_componentType;
   aStream >> _pointId;
   aStream >> _componentPointId;
   aStream >> (int &)_operationType;
   _pointPtr = NULL;
   aStream >> _constantValue;
   aStream >> _functionName;
   aStream >> _pointUpdated;
   aStream >> _valid;
}


void CtiCalcComponent::saveGuts(RWvostream &aStream) const
{
   aStream << _componentType;
   aStream << _pointId;
   aStream << _componentPointId;
   aStream << _operationType;
//   aStream << _pointPtr;
   aStream << _constantValue;
   aStream << _functionName;
   aStream << _pointUpdated;
   aStream << _valid;
}
*/
BOOL CtiCalcComponent::isUpdated( void )
{
    //  you can only be updated (or non-) if you're a point...
    if( _componentType == operation )
    {
        CtiHashKey hashKey(_componentPointId);
        CtiPointStore* pointStore = CtiPointStore::getInstance();
        CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&hashKey]);
        if( _lastUseUpdateNum != componentPointPtr->getNumUpdates() )
        {
            return TRUE;
        }
        else if( componentPointPtr->getPointQuality() == NonUpdatedQuality )
        {
            if( _CALC_DEBUG )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " - Point quality is non updated, Point ID: " << componentPointPtr->getPointNum() << endl;
            }
            return TRUE;
        }
        else if( componentPointPtr->getPointTags() & (TAG_DISABLE_DEVICE_BY_DEVICE | TAG_DISABLE_POINT_BY_POINT) )
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " - Point tags mark component disabled, Point ID: " << componentPointPtr->getPointNum() << endl;
            }
            return TRUE;
        }
        else
        {
            return FALSE;
        }
    }
    else
        return TRUE;
}

double CtiCalcComponent::calculate( double input )
{
    double orignal = input;
    if( _componentType == operation )
    {
        CtiHashKey hashKey(_componentPointId);
        CtiPointStore* pointStore = CtiPointStore::getInstance();
        CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&hashKey]);

        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << " component point id: " << _componentPointId << " lastUseUpdateNum: " << _lastUseUpdateNum << " componentPointPtr->getNumUpdates()" << componentPointPtr->getNumUpdates() << endl;
        }*/
        _lastUseUpdateNum = componentPointPtr->getNumUpdates( );

        switch( _operationType )
        {
            case addition:       input += componentPointPtr->getPointValue( );  break;
            case subtraction:    input -= componentPointPtr->getPointValue( );  break;
            case multiplication: input *= componentPointPtr->getPointValue( );  break;
            case division:       input /= componentPointPtr->getPointValue( );  break;
            case push:           
                if( _parent != NULL )
                {
                    _parent->push( componentPointPtr->getPointValue( ) );
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << __FILE__ << " (" << __LINE__ << ")  attempt to \'push\' with no parent pointer - returning \'input\'" << endl;
                }
                break;
        }
        if( _CALC_DEBUG )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "CtiCalcComponent::calculate(); point operation; input:" << orignal << ",   value:" << componentPointPtr->getPointValue() << ",   return:" << input << endl;
        }
    }
    else if( _componentType == constant )
    {
        switch( _operationType )
        {
            case addition:       input += _constantValue;  break;
            case subtraction:    input -= _constantValue;  break;
            case multiplication: input *= _constantValue;  break;
            case division:       input /= _constantValue;  break;
            case push:           
                if( _parent != NULL )
                {
                    _parent->push( _constantValue );
                }
                else
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << __FILE__ << " (" << __LINE__ << ")  attempt to \'push\' with no parent pointer - returning \'input\'" << endl;
                }
                break;
        }
        if( _CALC_DEBUG )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "CtiCalcComponent::calculate(); constant operation; input:" << orignal << ",   constant:" << _constantValue << ",   return:" << input << endl;
        }
    }
    else if( _componentType == function )
    {
        //  this, to keep this function small, and the functions elsewhere for maintenence
        input = _doFunction( _functionName );
        if( _CALC_DEBUG )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "CtiCalcComponent::calculate(); function return:" << input << endl;
        }
    }

    return input;
}


double CtiCalcComponent::_doFunction( RWCString &functionName )
{
    double retVal = 0.0;

    //  Transformer Thermal Age Calculation
    //  params:  Thermal Age Hours - the current thermal age of the transformer
    //           HotSpotTemp - the hot spot temperature of the transformer, calculated elsewhere
    //           UpdateFreq - the minutes between updates of the thermal age
    if( !functionName.compareTo("XfrmThermAge",RWCString::ignoreCase) )
    {
        double ThermalAgeHours, HotSpotTemp, UpdateFreq, tmp;
        ThermalAgeHours = _parent->pop( );
        HotSpotTemp = _parent->pop( );
        UpdateFreq = _parent->pop( );

        if( UpdateFreq < 0.001 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " (" << __LINE__ << ")  in \'XfrmThermAge\' function -  parameter UpdateFreq < 0.001, setting to 1.0e10 to prevent runaway transformer aging" << endl;
            UpdateFreq = 1.0e10;
        }

        tmp = ((15000.0 / 383.0) - 15000.0 / (HotSpotTemp + 273.0));
        ThermalAgeHours += pow( 2.78, tmp ) / (60.0 / UpdateFreq);

        retVal = ThermalAgeHours;
    }

    //  Hot Spot Calculation
    //  params:  
    else if( !functionName.compareTo("HotSpot",RWCString::ignoreCase) )
    {
        double HotSpotTemp, OilTemp, TempRise, Load; 
        double Rating, Mfactor, LoadWatts, LoadVARs;
        OilTemp = _parent->pop( );
        TempRise = _parent->pop( );
        LoadWatts = _parent->pop( );
        LoadVARs = _parent->pop( );
        Rating = _parent->pop( );
        Mfactor = _parent->pop( );

        if( Rating == 0.0 )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " (" << __LINE__ << ")  in \'HotSpot\' function - Rating parameter equal to zero, setting to 1" << endl;
            Rating = 1.0;
        }

        DOUBLE NaNDefenseDouble = (LoadWatts*LoadWatts)+(LoadVARs*LoadVARs);
        if( NaNDefenseDouble <= 0.0 )
        {
            Load = 0.0;
        }
        else
        {
            Load = sqrt(NaNDefenseDouble);
        }
        HotSpotTemp = OilTemp + TempRise * pow( (Load / Rating), (2 * Mfactor) );

        retVal = HotSpotTemp;
    }
    else if( !functionName.compareTo("DemandAvg15",RWCString::ignoreCase) )
    {
        retVal = _figureDemandAvg(900);// seconds in avg
    }
    else if( !functionName.compareTo("DemandAvg30",RWCString::ignoreCase) )
    {
        retVal = _figureDemandAvg(1800);// seconds in avg
    }
    else if( !functionName.compareTo("DemandAvg60",RWCString::ignoreCase) )
    {
        retVal = _figureDemandAvg(3600);// seconds in avg
    }
    else if( !functionName.compareTo("P-Factor KW/KVar",RWCString::ignoreCase) )
    {
        DOUBLE kvar = _parent->pop();
        DOUBLE kw = _parent->pop();
        DOUBLE newPowerFactorValue = 1.0;
        DOUBLE kva = 0.0;

        DOUBLE NaNDefenseDouble = (kw*kw)+(kvar*kvar);
        if( NaNDefenseDouble <= 0.0 )
        {
            kva = 0.0;
        }
        else
        {
            kva = sqrt(NaNDefenseDouble);
        }

        if( kva != 0.0 )
        {
            if( kw < 0 )
            {
                kw = -kw;
            }
            newPowerFactorValue = kw / kva;
        }
        retVal = newPowerFactorValue;
    }
    else if( !functionName.compareTo("P-Factor KW/KQ",RWCString::ignoreCase) )
    {
        DOUBLE kq = _parent->pop();
        DOUBLE kw = _parent->pop();
        DOUBLE kvar = ((2.0*kq)-kw)/SQRT3;
        DOUBLE newPowerFactorValue = 1.0;
        DOUBLE kva = 0.0;

        DOUBLE NaNDefenseDouble = (kw*kw)+(kvar*kvar);
        if( NaNDefenseDouble <= 0.0 )
        {
            kva = 0.0;
        }
        else
        {
            kva = sqrt(NaNDefenseDouble);
        }

        if( kva != 0.0 )
        {
            if( kw < 0 )
            {
                kw = -kw;
            }
            newPowerFactorValue = kw / kva;
        }
        retVal = newPowerFactorValue;
    }
    else if( !functionName.compareTo("P-Factor KW/KVa",RWCString::ignoreCase) )
    {
        DOUBLE kva = _parent->pop();
        DOUBLE kw = _parent->pop();
        DOUBLE newPowerFactorValue = 1.0;

        if( kva != 0.0 )
        {
            if( kw < 0 )
            {
                kw = -kw;
            }
            newPowerFactorValue = kw / kva;
            //check if this is leading
            /*if( kvar < 0.0 && newPowerFactorValue != 1.0 )
            {
                newPowerFactorValue = 2.0-newPowerFactorValue;
            }*/
        }
        retVal = newPowerFactorValue;
    }
    //added 3/4/03 JW
    else if( !functionName.compareTo("KVar from KW/KQ",RWCString::ignoreCase) )
    {
        DOUBLE kq = _parent->pop();
        DOUBLE kw = _parent->pop();
        DOUBLE kvar = ((2.0*kq)-kw)/SQRT3;

        retVal = kvar;
    }
    else if( !functionName.compareTo("KVa from KW/KVar",RWCString::ignoreCase) )
    {
        DOUBLE kvar = _parent->pop();
        DOUBLE kw = _parent->pop();

        DOUBLE NaNDefenseDouble = (kw*kw)+(kvar*kvar);
        DOUBLE kva = 0.0;
        if( NaNDefenseDouble <= 0.0 )
        {
            kva = 0.0;
        }
        else
        {
            kva = sqrt(NaNDefenseDouble);
        }

        retVal = kva;
    }
    else if( !functionName.compareTo("KVa from KW/KQ",RWCString::ignoreCase) )
    {
        DOUBLE kq = _parent->pop();
        DOUBLE kw = _parent->pop();
        DOUBLE kvar = ((2.0*kq)-kw)/SQRT3;

        DOUBLE kva = 0.0;
        DOUBLE NaNDefenseDouble = (kw*kw)+(kvar*kvar);
        if( NaNDefenseDouble <= 0.0 )
        {
            kva = 0.0;
        }
        else
        {
            kva = sqrt(NaNDefenseDouble);
        }

        retVal = kva;
    }
    else if( !functionName.compareTo("KW from KVa/KVAR",RWCString::ignoreCase) )
    {
        DOUBLE kvar = _parent->pop();
        DOUBLE kva = _parent->pop();

        DOUBLE kw = 0.0;
        DOUBLE NaNDefenseDouble = (kva*kva)-(kvar*kvar);
        if( NaNDefenseDouble <= 0.0 )
        {
            kw = 0.0;
        }
        else
        {
            kw = sqrt(NaNDefenseDouble);
        }

        retVal = kw;
    }
    else if( !functionName.compareTo("KVAR from KW/KVa",RWCString::ignoreCase) )
    {
        DOUBLE kva = _parent->pop();
        DOUBLE kw = _parent->pop();

        DOUBLE kvar = 0.0;
        DOUBLE NaNDefenseDouble = (kva*kva)-(kw*kw);
        if( NaNDefenseDouble <= 0.0 )
        {
            kvar = 0.0;
        }
        else
        {
            kvar = sqrt(NaNDefenseDouble);
        }

        retVal = kvar;
    }
    else if( !functionName.compareTo("Squared",RWCString::ignoreCase) )
    {
        CtiPointStore* pointStore = CtiPointStore::getInstance();

        CtiHashKey componentHashKey(_componentPointId);
        CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&componentHashKey]);

        DOUBLE componentPointValue = componentPointPtr->getPointValue();

        retVal = componentPointValue*componentPointValue;
    }
    else if( !functionName.compareTo("Square Root",RWCString::ignoreCase) )
    {
        CtiPointStore* pointStore = CtiPointStore::getInstance();

        CtiHashKey componentHashKey(_componentPointId);
        CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&componentHashKey]);

        DOUBLE componentPointValue = componentPointPtr->getPointValue();

        if( componentPointValue <= 0.0 )
        {
            retVal = 0.0;
        }
        else
        {
            retVal = sqrt(componentPointValue);
        }
    }
    else if( !functionName.compareTo("COS from P/Q",RWCString::ignoreCase) )
    {
        DOUBLE q = _parent->pop();
        DOUBLE p = _parent->pop();
        DOUBLE temp = p/q;
        retVal = cos(temp);
    }
    else if( !functionName.compareTo("ArcTan",RWCString::ignoreCase) )
    {
        CtiPointStore* pointStore = CtiPointStore::getInstance();

        CtiHashKey componentHashKey(_componentPointId);
        CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&componentHashKey]);

        DOUBLE componentPointValue = componentPointPtr->getPointValue();

        retVal = atan(componentPointValue);
    }
    //added 3/4/03 JW
    //added 7/31/03 JW
    else if( !functionName.compareTo("X^Y",RWCString::ignoreCase) )
    {
        DOUBLE y = _parent->pop();
        DOUBLE x = _parent->pop();
        retVal = pow(x,y);
    }
    //added 7/31/03 JW

    if( _isnan(retVal) )
    {
        retVal = 0.0;
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " (" << __LINE__ << ")  _doFunction tried to return a NaN for Calc Point Id: " << _parent->getPointId() << endl;
        }
    }

    return retVal;
}

double CtiCalcComponent::_figureDemandAvg(long secondsInAvg)
{
    double retVal = 0.0;

    if( _updatesInCurrentAvg <= 1 )
    {
        RWTime currenttime = RWTime();
        ULONG tempsum = (currenttime.seconds()-(currenttime.seconds()%secondsInAvg))+secondsInAvg;
        _parent->setPointCalcWindowEndTime(RWTime(tempsum));
        /*{
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " (" << __LINE__ << ")  setting PointCalcWindowEndTime: " << _parent->getPointCalcWindowEndTime().asString() << endl;
        }*/
    }

    CtiPointStore* pointStore = CtiPointStore::getInstance();

    CtiHashKey componentHashKey(_componentPointId);
    CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&componentHashKey]);
    CtiHashKey parentHashKey(_parent->getPointId());
    CtiPointStoreElement* parentPointPtr = (CtiPointStoreElement*)((*pointStore)[&parentHashKey]);

    if( componentPointPtr->getPointTime().seconds() >= (_parent->getPointCalcWindowEndTime().seconds() + componentPointPtr->getSecondsSincePreviousPointTime() - secondsInAvg) &&
        componentPointPtr->getPointTime().seconds() < (_parent->getPointCalcWindowEndTime().seconds() + componentPointPtr->getSecondsSincePreviousPointTime()) )
    {//is the last point data received in the average or not
        if( _CALC_DEBUG )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Current Component Point Time: " << componentPointPtr->getPointTime().asString() << endl;
            dout << "Current Point Calc Window End Time: " << _parent->getPointCalcWindowEndTime().asString() << endl;
            //dout << "Seconds Since Previous Point Time: " << componentPointPtr->getSecondsSincePreviousPointTime() << endl;
        }
        double componentPointValue = componentPointPtr->getPointValue();
        double currentCalcPointValue = parentPointPtr->getPointValue();

        double currentTotal = currentCalcPointValue * _updatesInCurrentAvg;
        _updatesInCurrentAvg++;
        retVal = (currentTotal + componentPointValue) / _updatesInCurrentAvg;

        if( _CALC_DEBUG )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Current Calc Point Value: " << currentCalcPointValue << endl;
            //dout << "Current Total: " << currentTotal << endl;
            dout << "Updates In Current Avg: " << _updatesInCurrentAvg << endl;
            dout << "Component Point Value: " << componentPointValue << endl;
            dout << "New Calc Point Value: " << retVal << endl;
            dout << "Will Send point change at: " << _parent->getPointCalcWindowEndTime() << endl;
        }
    }
    else
    {
        if( _updatesInCurrentAvg > 0 )
        {
            retVal = componentPointPtr->getPointValue();
            _updatesInCurrentAvg = 1;
            if( _CALC_DEBUG )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << "***********NEW DEMAND AVERAGE BEGUN**************: " << endl;
                //dout << "Current Component Point Time: " << componentPointPtr->getPointTime().asString() << endl;
                dout << "Current Point Calc Window End Time: " << _parent->getPointCalcWindowEndTime().asString() << endl;
                //dout << "Seconds Since Previous Point Time: " << componentPointPtr->getSecondsSincePreviousPointTime() << endl;
                dout << "New Initial Demand Avg: " << retVal << endl;
                dout << "Updates In Current Avg: " << _updatesInCurrentAvg << endl;
                dout << "Previous demand average has a timestamp of: " << _parent->getPointCalcWindowEndTime() << endl;
                dout << "Next demand average will have timestamp of: " << RWTime(_parent->getPointCalcWindowEndTime().seconds()+secondsInAvg) << endl;
            }
        }
        else
        {
            retVal = componentPointPtr->getPointValue();
            _updatesInCurrentAvg = 1;
            if( _CALC_DEBUG )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " - Calc Point Id: " << _parent->getPointId()
                     << " Demand Avg Reset!" << endl;
            }
        }
    }

    /*if( componentPointPtr->getPointTime() >= _parent->getPointCalcWindowEndTime() )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " (" << __LINE__ << ")  reseting updates to zero" << endl;
        }
        _updatesInCurrentAvg = 0;
    }*/

    return retVal;
}

