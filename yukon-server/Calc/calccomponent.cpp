#include "yukon.h"

#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <math.h>
#include <iostream>
using namespace std;

#include <rw/thr/mutex.h>

#include "calccomponent.h"
#include "logger.h"
#include "calc.h"
#include "utility.h"

extern ULONG _CALC_DEBUG;

RWDEFINE_NAMED_COLLECTABLE( CtiCalcComponent, "CtiCalcComponent" );

CtiCalcComponent::CtiCalcComponent( const RWCString &componentType, long componentPointId,
                                    const RWCString &operationType,
                                    double constantValue, const RWCString &functionName )
{
    _valid = TRUE;

    if( componentPointId <= 0 && !componentType.compareTo("operation", RWCString::ignoreCase) )
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
        else if( operationType == "%" )     _operationType = modulo;
        else if( operationType == ">" )     _operationType = greater;
        else if( operationType == ">=" )    _operationType = geq;
        else if( operationType == "<" )     _operationType = less;
        else if( operationType == "<=" )    _operationType = leq;
        else if( !operationType.compareTo("push", RWCString::ignoreCase) )  _operationType = push;
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Invalid Operation Type: " << operationType << " ComponentPointID = " << componentPointId << endl;
            }
            _valid = FALSE;
        }

        if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "Adding CtiCalcComponent - Normal Operation ComponentPointID = " << componentPointId << endl;
        }

    }
    else if( !componentType.compareTo("constant", RWCString::ignoreCase) )
    {
        _componentType = constant;
        _constantValue = constantValue;

        if( operationType == "+" )          _operationType = addition;
        else if( operationType == "-" )     _operationType = subtraction;
        else if( operationType == "*" )     _operationType = multiplication;
        else if( operationType == "/" )     _operationType = division;
        else if( operationType == "%" )     _operationType = modulo;
        else if( !operationType.compareTo("push", RWCString::ignoreCase) )  _operationType = push;
        else
        {
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Invalid Operation Type: " << operationType << " ComponentPointID = " << componentPointId << endl;
            }
            _valid = FALSE;
        }

        if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
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
BOOL CtiCalcComponent::isUpdated( int calcsUpdateType, const RWTime &calcsLastUpdateTime )
{
    //  you can only be updated (or non-) if you're a point...
    if( _componentType == operation )
    {
        CtiHashKey hashKey(_componentPointId);
        CtiPointStore* pointStore = CtiPointStore::getInstance();
        CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&hashKey]);

        if( componentPointPtr->getPointQuality() == NonUpdatedQuality ||
            componentPointPtr->getPointQuality() == ConstantQuality  )
        {
            if( _CALC_DEBUG & CALC_DEBUG_POINTDATA_QUALITY )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " - Point quality is constant or non-updated, Point ID: " << componentPointPtr->getPointNum() << endl;
            }
            return TRUE;
        }
        else if( componentPointPtr->getPointTags() & (TAG_DISABLE_DEVICE_BY_DEVICE | TAG_DISABLE_POINT_BY_POINT) )
        {
            if( _CALC_DEBUG & CALC_DEBUG_POINTDATA_QUALITY )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " - Point tags mark component disabled, Point ID: " << componentPointPtr->getPointNum() << endl;
            }
            return TRUE;
        }
        else if( (calcsUpdateType == periodicPlusUpdate) )
        {
            if( componentPointPtr->getPointTime() >= calcsLastUpdateTime)
                return TRUE;
            else
                return FALSE;
        }
        else if( _lastUseUpdateNum != componentPointPtr->getNumUpdates() )
        {
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

double CtiCalcComponent::calculate( double input, int &component_quality, RWTime &component_time, bool &calcValid )
{
    double orignal = input;

    if(_componentPointId > 0)
    {
        // If this component HAS a component ID it means it is associated with a point.  We discover and push the point's value onto the stack
        // so it may be operated upon.  The quality and time are returned to allow the calculation to be graded based upon ALL such returns.
        // input is passed in by value and returned by the function....

        CtiPointStore* pointStore = CtiPointStore::getInstance();
        CtiHashKey componentHashKey(_componentPointId);
        CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&componentHashKey]);

        if(componentPointPtr != NULL)
        {
            _lastUseUpdateNum = componentPointPtr->getNumUpdates( );
            if( _calcpoint->push( componentPointPtr->getPointValue() ) )
                input = componentPointPtr->getPointValue();

            component_quality = componentPointPtr->getPointQuality();
            component_time = componentPointPtr->getPointTime();
        }
    }
    else
    {
        component_quality = NormalQuality;
        component_time = component_time.now();
    }

    if( _componentType == operation )
    {
        switch( _operationType )
        {
        case addition:       input = _doFunction(RWCString("addition"), calcValid);  break;
        case subtraction:    input = _doFunction(RWCString("subtraction"), calcValid);  break;
        case multiplication: input = _doFunction(RWCString("multiplication"), calcValid);  break;
        case division:       input = _doFunction(RWCString("division"), calcValid);  break;
        case modulo:         input = _doFunction(RWCString("modulo divide"), calcValid);  break;
        case push:
            {
                // Handled above with the push based on _componentPointId.
                break;
            }
        }
    }
    else if( _componentType == constant )
    {
        // Push the constant and then perform the action against the stack.
        if( _calcpoint != NULL )
        {
            if( _calcpoint->push( _constantValue ) )    // The final result of a sequence of push operations is always equal to the last push.
                input = _constantValue;
        }
        else
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " (" << __LINE__ << ")  attempt to \'push\' with no parent pointer - returning \'input\'" << endl;
        }

        switch( _operationType )
        {
        case addition:       input = _doFunction(RWCString("addition"), calcValid);  break;
        case subtraction:    input = _doFunction(RWCString("subtraction"), calcValid);  break;
        case multiplication: input = _doFunction(RWCString("multiplication"), calcValid);  break;
        case division:       input = _doFunction(RWCString("division"), calcValid);  break;
        case modulo:         input = _doFunction(RWCString("modulo divide"), calcValid);  break;
        case greater:        input = _doFunction(RWCString(">"), calcValid);  break;
        case geq:            input = _doFunction(RWCString(">="), calcValid);  break;
        case less:           input = _doFunction(RWCString("<"), calcValid);  break;
        case leq:            input = _doFunction(RWCString("<="), calcValid);  break;
        case push:           break; // This was completed with the above push!
        }
        if( _CALC_DEBUG & CALC_DEBUG_COMPONENT_POSTCALC_VALUE )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " CtiCalcComponent::calculate(); constant operation; input:" << orignal << ",   constant:" << _constantValue << ",   return: " << input << endl;
        }
    }
    else if( _componentType == function )
    {
        //  this, to keep this function small, and the functions elsewhere for maintenence
        input = _doFunction( _functionName, calcValid );
        if( _CALC_DEBUG & CALC_DEBUG_COMPONENT_POSTCALC_VALUE )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " CtiCalcComponent::calculate(); function return: " << input << endl;
        }
    }


    return input;
}


double CtiCalcComponent::_doFunction( RWCString &functionName, bool &validCalc )
{
    double retVal = 0.0;
    validCalc = true;

    try
    {
        if( !functionName.compareTo("addition",RWCString::ignoreCase) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = operand1 + operand2;
        }
        else if( !functionName.compareTo("subtraction",RWCString::ignoreCase) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = operand1 - operand2;
        }
        else if( !functionName.compareTo("multiplication",RWCString::ignoreCase) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = operand1 * operand2;
        }
        else if( !functionName.compareTo("division",RWCString::ignoreCase) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            if(operand2 != 0.0)
            {
                retVal = operand1 / operand2;
            }
            else
                validCalc = false;
        }
        else if( !functionName.compareTo("modulo divide",RWCString::ignoreCase) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            if(operand2 != 0.0)
            {
                retVal = (double)((int)operand1 % (int)operand2);
            }
            else
                validCalc = false;
        }
        else if( !functionName.compareTo(">") || !functionName.compareTo("greater than",RWCString::ignoreCase) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 > operand2) ? 1.0 : 0.0;
        }
        else if( !functionName.compareTo(">=",RWCString::ignoreCase) || !functionName.compareTo("geq than",RWCString::ignoreCase) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 >= operand2) ? 1.0 : 0.0;
        }
        else if( !functionName.compareTo("<",RWCString::ignoreCase) || !functionName.compareTo("less than",RWCString::ignoreCase) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 < operand2) ? 1.0 : 0.0;
        }
        else if( !functionName.compareTo("<=",RWCString::ignoreCase) || !functionName.compareTo("leq than",RWCString::ignoreCase) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 <= operand2) ? 1.0 : 0.0;
        }
        else if( !functionName.compareTo("logical and",RWCString::ignoreCase) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 != 0.0 && operand2 != 0.0) ? 1.0 : 0.0;
        }
        else if( !functionName.compareTo("logical or",RWCString::ignoreCase) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 != 0.0 || operand2 != 0.0) ? 1.0 : 0.0;
        }
        else if( !functionName.compareTo("logical not",RWCString::ignoreCase) )
        {
            double operand = _calcpoint->pop( );

            retVal = (operand == 0.0) ? 1.0 : 0.0;
        }
        else if( !functionName.compareTo("logical xor",RWCString::ignoreCase) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = ((operand1 != 0.0) ^ (operand2 != 0.0)) ? 1.0 : 0.0;
        }
        //  Transformer Thermal Age Calculation
        //  params:  Thermal Age Hours - the current thermal age of the transformer
        //           HotSpotTemp - the hot spot temperature of the transformer, calculated elsewhere
        //           UpdateFreq - the minutes between updates of the thermal age
        else if( !functionName.compareTo("XfrmThermAge",RWCString::ignoreCase) )
        {
            double ThermalAgeHours, HotSpotTemp, UpdateFreq, tmp;
            ThermalAgeHours = _calcpoint->pop( );
            HotSpotTemp = _calcpoint->pop( );
            UpdateFreq = _calcpoint->pop( );

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
            OilTemp = _calcpoint->pop( );
            TempRise = _calcpoint->pop( );
            LoadWatts = _calcpoint->pop( );
            LoadVARs = _calcpoint->pop( );
            Rating = _calcpoint->pop( );
            Mfactor = _calcpoint->pop( );

            if( Rating == 0.0 )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << __FILE__ << " (" << __LINE__ << ")  in \'HotSpot\' function - Rating parameter equal to zero, setting to 1" << endl;
                Rating = 1.0;
            }

            double NaNDefenseDouble = (LoadWatts*LoadWatts)+(LoadVARs*LoadVARs);
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
        else if( !functionName.compareTo("DemandAvg1",RWCString::ignoreCase) )
        {
            retVal = _calcpoint->figureDemandAvg(60);// seconds in avg
        }
        else if( !functionName.compareTo("DemandAvg5",RWCString::ignoreCase) )
        {
            retVal = _calcpoint->figureDemandAvg(300);// seconds in avg
        }
        else if( !functionName.compareTo("DemandAvg15",RWCString::ignoreCase) )
        {
            retVal = _calcpoint->figureDemandAvg(900);// seconds in avg
        }
        else if( !functionName.compareTo("DemandAvg30",RWCString::ignoreCase) )
        {
            retVal = _calcpoint->figureDemandAvg(1800);// seconds in avg
        }
        else if( !functionName.compareTo("DemandAvg60",RWCString::ignoreCase) )
        {
            retVal = _calcpoint->figureDemandAvg(3600);// seconds in avg
        }
        else if( !functionName.compareTo("P-Factor KW/KVar",RWCString::ignoreCase) )
        {
            double kvar = _calcpoint->pop();
            double kw = _calcpoint->pop();
            double newPowerFactorValue = 1.0;
            double kva = 0.0;

            double NaNDefenseDouble = (kw*kw)+(kvar*kvar);
            if( NaNDefenseDouble <= 0.0 )
            {
                validCalc = false;
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
            double kq = _calcpoint->pop();
            double kw = _calcpoint->pop();
            double kvar = ((2.0*kq)-kw)/SQRT3;
            double newPowerFactorValue = 1.0;
            double kva = 0.0;

            double NaNDefenseDouble = (kw*kw)+(kvar*kvar);
            if( NaNDefenseDouble <= 0.0 )
            {
                validCalc = false;
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
            double kva = _calcpoint->pop();
            double kw = _calcpoint->pop();
            double newPowerFactorValue = 1.0;

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
            double kq = _calcpoint->pop();
            double kw = _calcpoint->pop();
            double kvar = ((2.0*kq)-kw)/SQRT3;

            retVal = kvar;
        }
        else if( !functionName.compareTo("KVa from KW/KVar",RWCString::ignoreCase) )
        {
            double kvar = _calcpoint->pop();
            double kw = _calcpoint->pop();

            double NaNDefenseDouble = (kw*kw)+(kvar*kvar);
            double kva = 0.0;
            if( NaNDefenseDouble <= 0.0 )
            {
                validCalc = false;
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
            double kq = _calcpoint->pop();
            double kw = _calcpoint->pop();
            double kvar = ((2.0*kq)-kw)/SQRT3;

            double kva = 0.0;
            double NaNDefenseDouble = (kw*kw)+(kvar*kvar);
            if( NaNDefenseDouble <= 0.0 )
            {
                validCalc = false;
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
            double kvar = _calcpoint->pop();
            double kva = _calcpoint->pop();

            double kw = 0.0;
            double NaNDefenseDouble = (kva*kva)-(kvar*kvar);
            if( NaNDefenseDouble <= 0.0 )
            {
                validCalc = false;
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
            double kva = _calcpoint->pop();
            double kw = _calcpoint->pop();

            double kvar = 0.0;
            double NaNDefenseDouble = (kva*kva)-(kw*kw);
            if( NaNDefenseDouble <= 0.0 )
            {
                validCalc = false;
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
            double componentPointValue = _calcpoint->pop( );;

            retVal = componentPointValue*componentPointValue;
        }
        else if( !functionName.compareTo("Square Root",RWCString::ignoreCase) )
        {
            double componentPointValue = _calcpoint->pop( );;

            if( componentPointValue <= 0.0 )
            {
                validCalc = false;
                retVal = 0.0;
            }
            else
            {
                retVal = sqrt(componentPointValue);
            }
        }
        else if( !functionName.compareTo("COS from P/Q",RWCString::ignoreCase) )
        {
            double q = _calcpoint->pop();
            double p = _calcpoint->pop();
            if(q != 0)
            {
                #if 0
                double temp = p/q;
                retVal = cos(temp);
                #else
                retVal = p/q;               // Why anyone would ask for this function as a function is beyond me!  Maybe because the original code couldn't mix functions with operations at all.
                #endif
            }
            else
            {
                validCalc = false;
                {
                    CtiLockGuard<CtiLogger> doubt_guard(dout);
                    dout << RWTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
        else if( !functionName.compareTo("sin",RWCString::ignoreCase) )
        {
            double temp = _calcpoint->pop();
            retVal = sin(temp);
        }
        else if( !functionName.compareTo("cos",RWCString::ignoreCase) )
        {
            double temp = _calcpoint->pop();
            retVal = cos(temp);
        }
        else if( !functionName.compareTo("tan",RWCString::ignoreCase) )
        {
            double temp = _calcpoint->pop();
            retVal = tan(temp);
        }
        else if( !functionName.compareTo("arcsin",RWCString::ignoreCase) )
        {
            double componentPointValue = _calcpoint->pop( );;
            retVal = asin(componentPointValue);
        }
        else if( !functionName.compareTo("arccos",RWCString::ignoreCase) )
        {
            double componentPointValue = _calcpoint->pop( );;
            retVal = acos(componentPointValue);
        }
        else if( !functionName.compareTo("arctan",RWCString::ignoreCase) )
        {
            double componentPointValue = _calcpoint->pop( );;
            retVal = atan(componentPointValue);
        }
        else if( !functionName.compareTo("X^Y",RWCString::ignoreCase) )
        {
            double y = _calcpoint->pop();
            double x = _calcpoint->pop();
            retVal = pow(x,y);
        }
        else if( !functionName.compareTo("Max",RWCString::ignoreCase) )
        {
            DOUBLE a = _calcpoint->pop();
            DOUBLE b = _calcpoint->pop();

            retVal = ((a)>(b))?(a):(b);
        }
        else if( !functionName.compareTo("Min",RWCString::ignoreCase) )
        {
            DOUBLE a = _calcpoint->pop();
            DOUBLE b = _calcpoint->pop();

            retVal = ((a)<(b))?(a):(b);
        }
        else if( !functionName.compareTo("Absolute Value",RWCString::ignoreCase) )
        {
            double a = _calcpoint->pop();
            retVal = fabs(a);
        }
        else if( !functionName.compareTo("Max Difference",RWCString::ignoreCase) )
        {
            double a = _calcpoint->pop();
            double b = _calcpoint->pop();
            double c = _calcpoint->pop();

            retVal = (fabs(a-b) + fabs(b-c) + fabs(c-a)) / 2.0;

        }
        else if( !functionName.compareTo("lohi accumulator",RWCString::ignoreCase) )
        {
            CtiPointStore* pointStore = CtiPointStore::getInstance();
            CtiHashKey componentHashKey(_componentPointId);
            CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&componentHashKey]);

            double val = _calcpoint->pop();
            double inc = 0.0;

            if(componentPointPtr != NULL)
            {
                int tags = componentPointPtr->getPointTags();
                if(tags & TAG_POINT_DATA_TIMESTAMP_VALID)
                {
                    inc = 1.0;
                }
            }

            retVal = val + inc;
        }
        else if( !functionName.compareTo("State Timer",RWCString::ignoreCase) )
        {
            /*
             *  This function pops a number representing the trigger state of a status and returns the
             *  number of seconds since the point entered that state
             */
            double pt_val = _calcpoint->pop();       // This should be the point's value.
            double state  = _calcpoint->pop();   // This is the state value it must be in.  Return zero if the state is not correct.

            retVal = 0;
            if(_componentPointId > 0)
            {

                CtiPointStore* pointStore = CtiPointStore::getInstance();
                CtiHashKey componentHashKey(_componentPointId);
                CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&componentHashKey]);

                if(componentPointPtr != NULL)
                {
                    RWTime now;
                    RWTime component_time = componentPointPtr->getLastValueChangedTime();

                    if(pt_val == state)
                        retVal = now.seconds() - component_time.seconds();
                }
            }
        }
        else if( !functionName.compareTo("True,False,Condition",RWCString::ignoreCase) )
        {
            double c = _calcpoint->pop();
            double b = _calcpoint->pop();
            double a = _calcpoint->pop();

            retVal = ( c != 0.0 ? a : b );
        }
        else
        {
            // We do not have a function.
            validCalc = false;
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << RWTime() << " Function " << functionName << " not implemented " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            retVal = _calcpoint->pop();     // Do a pop() to allow a retVal push() below to keep the stack sane.
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << RWTime() << " **** EXCEPTION performing component calculate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
            dout << "  Failed point: " << _calcpoint->getPointId() << endl;
        }
    }

    //added 7/31/03 JW
    if( _isnan(retVal) || !_finite(retVal) )        // !_finite should be thought of as isInfinite(retVal)  We cannot have that!
    {
        retVal = 0.0;
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " (" << __LINE__ << ")  _doFunction tried to return a NaN/INF for Calc Point Id: " << _calcpoint->getPointId() << endl;
        }
    }

    _calcpoint->push( retVal );       // Return result to the stack so it can be used properly by another callee/component.

    return retVal;
}


