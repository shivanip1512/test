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
#include "rwutil.h"

extern ULONG _CALC_DEBUG;

RWDEFINE_NAMED_COLLECTABLE( CtiCalcComponent, "CtiCalcComponent" );

CtiCalcComponent::CtiCalcComponent( const string &componentType, long componentPointId,
                                    const string &operationType,
                                    double constantValue, const string &functionName )
{
    _valid = TRUE;

    if( componentPointId <= 0 && !stringCompareIgnoreCase(componentType,"operation" ) )
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << "ERROR creating CtiCalcComponent - operation with ComponentPointID of 0 - setting invalid flag" << endl;
        }
        _valid = FALSE;
    }
    else if( !stringCompareIgnoreCase(componentType,"operation") )
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
        else if( !stringCompareIgnoreCase(operationType,"push" ) )  _operationType = push;
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
    else if( !stringCompareIgnoreCase(componentType,"constant" ) )
    {
        _componentType = constant;
        _constantValue = constantValue;

        if( operationType == "+" )          _operationType = addition;
        else if( operationType == "-" )     _operationType = subtraction;
        else if( operationType == "*" )     _operationType = multiplication;
        else if( operationType == "/" )     _operationType = division;
        else if( operationType == "%" )     _operationType = modulo;
        else if( !stringCompareIgnoreCase(operationType,"push" ) )  _operationType = push;
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
    else if( !stringCompareIgnoreCase(componentType,"function" ) )
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
BOOL CtiCalcComponent::isUpdated( int calcsUpdateType, const CtiTime &calcsLastUpdateTime )
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
                dout << CtiTime() << " - Point quality is constant or non-updated, Point ID: " << componentPointPtr->getPointNum() << endl;
            }
            return TRUE;
        }
        else if( componentPointPtr->getPointTags() & (TAG_DISABLE_DEVICE_BY_DEVICE | TAG_DISABLE_POINT_BY_POINT) )
        {
            if( _CALC_DEBUG & CALC_DEBUG_POINTDATA_QUALITY )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " - Point tags mark component disabled, Point ID: " << componentPointPtr->getPointNum() << endl;
            }
            return TRUE;
        }
        else if( !stringCompareIgnoreCase(_functionName,"Get Interval Minutes") || !stringCompareIgnoreCase(_functionName,"Get Point Limit") )
        {
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

double CtiCalcComponent::calculate( double input, int &component_quality, CtiTime &component_time, bool &calcValid )
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
            component_time = componentPointPtr->getPointTime();
            component_quality = componentPointPtr->getPointQuality();
            if(componentPointPtr->getPointTags() & (TAG_DISABLE_DEVICE_BY_DEVICE | TAG_DISABLE_POINT_BY_POINT))
            {
                component_quality = QuestionableQuality;
            }

            if( _calcpoint->getUpdateType() == historical )
            {
                if( _calcpoint->push( componentPointPtr->getHistoricValue() ) )
                    input = componentPointPtr->getHistoricValue();
            }
            else
            {
                if( _calcpoint->push( componentPointPtr->getPointValue() ) )
                    input = componentPointPtr->getPointValue();
            }
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
        case addition:       input = _doFunction(string("addition"), calcValid);  break;
        case subtraction:    input = _doFunction(string("subtraction"), calcValid);  break;
        case multiplication: input = _doFunction(string("multiplication"), calcValid);  break;
        case division:       input = _doFunction(string("division"), calcValid);  break;
        case modulo:         input = _doFunction(string("modulo divide"), calcValid);  break;
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
        case addition:       input = _doFunction(string("addition"), calcValid);  break;
        case subtraction:    input = _doFunction(string("subtraction"), calcValid);  break;
        case multiplication: input = _doFunction(string("multiplication"), calcValid);  break;
        case division:       input = _doFunction(string("division"), calcValid);  break;
        case modulo:         input = _doFunction(string("modulo divide"), calcValid);  break;
        case greater:        input = _doFunction(string(">"), calcValid);  break;
        case geq:            input = _doFunction(string(">="), calcValid);  break;
        case less:           input = _doFunction(string("<"), calcValid);  break;
        case leq:            input = _doFunction(string("<="), calcValid);  break;
        case push:           break; // This was completed with the above push!
        }
        if( _CALC_DEBUG & CALC_DEBUG_COMPONENT_POSTCALC_VALUE )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " CtiCalcComponent::calculate(); constant operation; input:" << orignal << ",   constant:" << _constantValue << ",   return: " << input << endl;
        }
    }
    else if( _componentType == function )
    {
        //  this, to keep this function small, and the functions elsewhere for maintenence
        input = _doFunction( _functionName, calcValid );
        if( _CALC_DEBUG & CALC_DEBUG_COMPONENT_POSTCALC_VALUE )
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " CtiCalcComponent::calculate(); function return: " << input << endl;
        }
    }


    return input;
}


double CtiCalcComponent::_doFunction( string &functionName, bool &validCalc )
{
    double retVal = 0.0;
    validCalc = true;

    try
    {
        if( !stringCompareIgnoreCase(functionName,"addition" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = operand1 + operand2;
        }
        else if( !stringCompareIgnoreCase(functionName,"subtraction" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = operand1 - operand2;
        }
        else if( !stringCompareIgnoreCase(functionName,"multiplication" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = operand1 * operand2;
        }
        else if( !stringCompareIgnoreCase(functionName,"division" ) )
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
        else if( !stringCompareIgnoreCase(functionName,"modulo divide" ) )
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
        else if( !stringCompareIgnoreCase(functionName,">") || !stringCompareIgnoreCase(functionName,"greater than" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 > operand2) ? 1.0 : 0.0;
        }
        else if( !stringCompareIgnoreCase(functionName,">=" ) || !stringCompareIgnoreCase(functionName,"geq than" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 >= operand2) ? 1.0 : 0.0;
        }
        else if( !stringCompareIgnoreCase(functionName,"<" ) || !stringCompareIgnoreCase(functionName,"less than" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 < operand2) ? 1.0 : 0.0;
        }
        else if( !stringCompareIgnoreCase(functionName,"<=" ) || !stringCompareIgnoreCase(functionName,"leq than" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 <= operand2) ? 1.0 : 0.0;
        }
        else if( !stringCompareIgnoreCase(functionName,"logical and" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 != 0.0 && operand2 != 0.0) ? 1.0 : 0.0;
        }
        else if( !stringCompareIgnoreCase(functionName,"logical or" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 != 0.0 || operand2 != 0.0) ? 1.0 : 0.0;
        }
        else if( !stringCompareIgnoreCase(functionName,"logical not" ) )
        {
            double operand = _calcpoint->pop( );

            retVal = (operand == 0.0) ? 1.0 : 0.0;
        }
        else if( !stringCompareIgnoreCase(functionName,"logical xor" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = ((operand1 != 0.0) ^ (operand2 != 0.0)) ? 1.0 : 0.0;
        }
        //  Transformer Thermal Age Calculation
        //  params:  Thermal Age Hours - the current thermal age of the transformer
        //           HotSpotTemp - the hot spot temperature of the transformer, calculated elsewhere
        //           UpdateFreq - the minutes between updates of the thermal age
        else if( !stringCompareIgnoreCase(functionName,"XfrmThermAge" ) )
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
        else if( !stringCompareIgnoreCase(functionName,"HotSpot" ) )
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
        else if( !stringCompareIgnoreCase(functionName,"DemandAvg1" ) )
        {
            retVal = _calcpoint->figureDemandAvg(60);// seconds in avg
        }
        else if( !stringCompareIgnoreCase(functionName,"DemandAvg5" ) )
        {
            retVal = _calcpoint->figureDemandAvg(300);// seconds in avg
        }
        else if( !stringCompareIgnoreCase(functionName,"DemandAvg15" ) )
        {
            retVal = _calcpoint->figureDemandAvg(900);// seconds in avg
        }
        else if( !stringCompareIgnoreCase(functionName,"DemandAvg30" ) )
        {
            retVal = _calcpoint->figureDemandAvg(1800);// seconds in avg
        }
        else if( !stringCompareIgnoreCase(functionName,"DemandAvg60" ) )
        {
            retVal = _calcpoint->figureDemandAvg(3600);// seconds in avg
        }
        else if( !stringCompareIgnoreCase(functionName,"P-Factor KW/KVar" ) )
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
        else if( !stringCompareIgnoreCase(functionName,"P-Factor KW/KQ" ) )
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
        else if( !stringCompareIgnoreCase(functionName,"P-Factor KW/KVa" ) )
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
        else if( !stringCompareIgnoreCase(functionName,"KVar from KW/KQ" ) )
        {
            double kq = _calcpoint->pop();
            double kw = _calcpoint->pop();
            double kvar = ((2.0*kq)-kw)/SQRT3;

            retVal = kvar;
        }
        else if( !stringCompareIgnoreCase(functionName,"KVa from KW/KVar" ) )
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
        else if( !stringCompareIgnoreCase(functionName,"KVa from KW/KQ" ) )
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
        else if( !stringCompareIgnoreCase(functionName,"KW from KVa/KVAR" ) )
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
        else if( !stringCompareIgnoreCase(functionName,"KVAR from KW/KVa" ) )
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
        else if( !stringCompareIgnoreCase(functionName,"Squared" ) )
        {
            double componentPointValue = _calcpoint->pop( );;

            retVal = componentPointValue*componentPointValue;
        }
        else if( !stringCompareIgnoreCase(functionName,"Square Root" ) )
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
        else if( !stringCompareIgnoreCase(functionName,"COS from P/Q" ) )
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
                    dout << CtiTime() << " **** Checkpoint **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
                }
            }
        }
        else if( !stringCompareIgnoreCase(functionName,"sin") )
        {
            double temp = _calcpoint->pop();
            retVal = sin(temp);
        }
        else if( !stringCompareIgnoreCase(functionName,"cos") )
        {
            double temp = _calcpoint->pop();
            retVal = cos(temp);
        }
        else if( !stringCompareIgnoreCase(functionName,"tan") )
        {
            double temp = _calcpoint->pop();
            retVal = tan(temp);
        }
        else if( !stringCompareIgnoreCase(functionName,"arcsin") )
        {
            double componentPointValue = _calcpoint->pop( );;
            retVal = asin(componentPointValue);
        }
        else if( !stringCompareIgnoreCase(functionName,"arccos") )
        {
            double componentPointValue = _calcpoint->pop( );;
            retVal = acos(componentPointValue);
        }
        else if( !stringCompareIgnoreCase(functionName,"arctan") )
        {
            double componentPointValue = _calcpoint->pop( );;
            retVal = atan(componentPointValue);
        }
        else if( !stringCompareIgnoreCase(functionName,"X^Y" ) )
        {
            double y = _calcpoint->pop();
            double x = _calcpoint->pop();
            retVal = pow(x,y);
        }
        else if( !stringCompareIgnoreCase(functionName,"Max" ) )
        {
            DOUBLE a = _calcpoint->pop();
            DOUBLE b = _calcpoint->pop();

            retVal = ((a)>(b))?(a):(b);
        }
        else if( !stringCompareIgnoreCase(functionName,"Min" ) )
        {
            DOUBLE a = _calcpoint->pop();
            DOUBLE b = _calcpoint->pop();

            retVal = ((a)<(b))?(a):(b);
        }
        else if( !stringCompareIgnoreCase(functionName,"Absolute Value" ) )
        {
            double a = _calcpoint->pop();
            retVal = fabs(a);
        }
        else if( !stringCompareIgnoreCase(functionName,"Max Difference" ) )
        {
            double a = _calcpoint->pop();
            double b = _calcpoint->pop();
            double c = _calcpoint->pop();

            retVal = (fabs(a-b) + fabs(b-c) + fabs(c-a)) / 2.0;

        }

        else if( !stringCompareIgnoreCase(functionName,"lohi accumulator") )
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
        else if( !stringCompareIgnoreCase(functionName,"State Timer") )
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
                    CtiTime now;
                    CtiTime component_time = componentPointPtr->getLastValueChangedTime();

                    if(pt_val == state)
                        retVal = now.seconds() - component_time.seconds();
                }
            }
        }
        else if( !stringCompareIgnoreCase(functionName,"True,False,Condition") )
        {
            double c = _calcpoint->pop();
            double b = _calcpoint->pop();
            double a = _calcpoint->pop();

            retVal = ( c != 0.0 ? a : b );
        }
        else if( !stringCompareIgnoreCase(functionName,"Regression") )      // Stack has Depth,Minutes,Value
        {
            /*
             *  This function pops a number representing the trigger state of a status and returns the
             *  number of seconds since the point entered that state
             */
            double value  = _calcpoint->pop();  // This should be the point's most recent value.  It is not used by the regression computation.
            ULONG  min    = _calcpoint->pop();  // This is the number of minutes forward we want to compute.
            int    depth  = _calcpoint->pop();  // This is the storage depth of our regression.

            retVal = 0;
            if(_componentPointId > 0)
            {
                CtiPointStore* pointStore = CtiPointStore::getInstance();
                CtiHashKey componentHashKey(_componentPointId);
                CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore)[&componentHashKey]);

                if(componentPointPtr != NULL)
                {
                    CtiTime now;
                    componentPointPtr->resize_regession( depth );
                    retVal = componentPointPtr->regression( now.seconds() + (min * 60) );
                }
            }
        }
        else if( !stringCompareIgnoreCase(functionName,"Float From 16bit") )      // Stack has two 16 bit unsigned.
        {
            unsigned short umsw = _calcpoint->pop();
            unsigned short ulsw = _calcpoint->pop();
        
            unsigned fpu = (unsigned short)umsw;
            fpu = (fpu << 16) + ulsw;

            float *floatPtr = (float *)&fpu;

            retVal = *floatPtr;
        }
        else if( !stringCompareIgnoreCase(functionName,"Binary Encode") )
        {
            double newValue = _calcpoint->pop( );
            double oldResult  = _calcpoint->pop( );

            oldResult = oldResult*2;
            retVal = oldResult + newValue;
        }
        else if( !stringCompareIgnoreCase(functionName,"Mid Level Latch") )
        {
            double point1 = _calcpoint->pop( );
            double point2  = _calcpoint->pop( );

            if( point1 > 0 && point2 > 0 ) //if both true
            {
                retVal = 1;
            }
            else if( point1 <= 0 && point2 <= 0 )//if both false
            {
                retVal = 0;
            }
            else //If only 1 true and 1 false, keep last value
            {
                CtiPointStore* pointStore = CtiPointStore::getInstance();
                CtiHashKey parentHashKey(_calcpoint->getPointId());
                CtiPointStoreElement* parentPointPtr = (CtiPointStoreElement*)((*pointStore)[&parentHashKey]);
                retVal = parentPointPtr->getPointValue();
            }
        }
        else if( !stringCompareIgnoreCase(functionName,"Get Interval Minutes") )
        {
            retVal = 0;
            if( _calcpoint != NULL )
            {
                retVal = _calcpoint->getUpdateInterval();
            }

            retVal = retVal/60; //Convert to minutes
        }
        else if( !stringCompareIgnoreCase(functionName,"Get Point Limit") )
        {
            _calcpoint->pop(); //throwaway value
            int limitFunc = _calcpoint->pop( );
            retVal = 0;

            if( _componentPointId > 0 )
            {
                CtiPointStore* pointStore = CtiPointStore::getInstance();
                CtiHashKey hashKey(_componentPointId);
                CtiPointStoreElement* componentPtr = (CtiPointStoreElement*)((*pointStore)[&hashKey]);

                // Find the limit table we want
                CtiTablePointLimit *limitPtr = NULL;
                if( limitFunc == HighLimit1 || limitFunc == LowLimit1 )
                {
                     limitPtr = componentPtr->getLimit(1);
                }
                else if( limitFunc == HighLimit2 || limitFunc == LowLimit2 )
                {
                     limitPtr = componentPtr->getLimit(2);
                }

                //Get the correct limit out of the limit table
                if( limitPtr != NULL && (limitFunc == HighLimit2 || limitFunc == HighLimit1) )
                {
                    retVal = limitPtr->getHighLimit();
                }
                else if( limitPtr != NULL && (limitFunc == LowLimit2 || limitFunc == LowLimit1) )
                {
                    retVal = limitPtr->getLowLimit();
                }
                int interval = 0;
            }
        }
        else if( !stringCompareIgnoreCase(functionName,"Intervals To Value") )      // Stack has Depth,Minutes,Value
        {
            // This function calculates a regression and estimates the number of intervals until a limit is reached.

            double value  = _calcpoint->pop();  // This should be the point's most recent value.  It is not used by the regression computation.
            double limit  = _calcpoint->pop();  // The limit value we are trying to reach
            int    mindepth  = _calcpoint->pop();  // This is the storage depth of our regression.
            int    depth  = _calcpoint->pop();  // This is the storage depth of our regression.
            int    isMax  = _calcpoint->pop();  // is the value a max value (or a min)?
            int    needsHistory  = _calcpoint->pop();  // Are we supposed to look at historical values?

            retVal = 999;
            if(_componentPointId > 0)
            {
                CtiPointStore* pointStore = CtiPointStore::getInstance();
                
                CtiHashKey calcHashKey(_calcpoint->getPointId());
                CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore)[&calcHashKey]);

                if(calcPointPtr != NULL)
                {
                    CtiHashKey componentHashKey(_componentPointId);
                    CtiTime pointTime;
                    CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore).findValue(&componentHashKey));
                    if( _calcpoint->getUpdateType() != periodic && componentPointPtr != NULL )
                    {
                        pointTime = componentPointPtr->getPointTime();
                    }

                    calcPointPtr->setRegressionMinDepth(mindepth);
                    calcPointPtr->setRegressionDepth(depth);

                    if( needsHistory && !calcPointPtr->isPrimed() )
                    {
                        primeHistoricalRegression(_calcpoint, pointTime, depth);
                    }

                    double slope, intercept, xAtLimit, yNow;
                    calcPointPtr->addRegressionVal( pointTime, value );
                    if( calcPointPtr->linearRegression(slope, intercept) )
                    {
                        depth = calcPointPtr->getRegressCurrentDepth();
                        //x= (y-b)/m
                        xAtLimit = (limit-intercept)/slope;
                        yNow = (depth-1)*slope + intercept; //Y at this very moment according to the regression

                        if( isMax > 0 ) //We are looking for a max limit
                        {
                            if( yNow >= limit )
                            {
                                retVal = 0;
                            }
                            else if( xAtLimit > (depth-1) && slope > 0 )
                            {
                                retVal = xAtLimit - (depth-1);
                            }
                        }
                        else //We are looking for a min limit
                        {
                            if( yNow <= limit )
                            {
                                retVal = 0;
                            }
                            else if( xAtLimit > (depth-1) && slope < 0 )
                            {
                                retVal = xAtLimit - (depth-1);
                            }
                        }
                    }
                }
            }
        }
        else if( !stringCompareIgnoreCase(functionName,"Linear Slope") )      // Stack has Depth,Minutes,Value
        {
            // This function calculates a regression and estimates the number of intervals until a limit is reached.

            double value  = _calcpoint->pop();  // This should be the point's most recent value.  It is not used by the regression computation.
            int    mindepth  = _calcpoint->pop();  // This is the storage depth of our regression.
            int    depth  = _calcpoint->pop();  // This is the storage depth of our regression.
            int    needsHistory  = _calcpoint->pop();  // Are we supposed to look at historical values?

            retVal = 0;
            if(_componentPointId > 0)
            {
                CtiPointStore* pointStore = CtiPointStore::getInstance();
                
                CtiHashKey calcHashKey(_calcpoint->getPointId());
                CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore)[&calcHashKey]);

                if(calcPointPtr != NULL)
                {
                    CtiHashKey componentHashKey(_componentPointId);
                    CtiTime pointTime;
                    CtiPointStoreElement* componentPointPtr = (CtiPointStoreElement*)((*pointStore).findValue(&componentHashKey));

                    calcPointPtr->setRegressionMinDepth(mindepth);
                    calcPointPtr->setRegressionDepth(depth);

                    if( _calcpoint->getUpdateType() != periodic && componentPointPtr != NULL )
                    {
                        pointTime = componentPointPtr->getPointTime();
                    }

                    if( needsHistory && !calcPointPtr->isPrimed() )
                    {
                        primeHistoricalRegression(_calcpoint, pointTime,  depth);
                    }

                    double slope, intercept, xAtLimit, yNow;
                    calcPointPtr->addRegressionVal( pointTime, value );
                    if( calcPointPtr->linearRegression(slope, intercept) )
                    {
                        retVal = slope;
                    }
                }
            }
        }
        else
        {
            // We do not have a function.
            validCalc = false;
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << CtiTime() << " Function " << functionName << " not implemented " << __FILE__ << " (" << __LINE__ << ")" << endl;
            }
            retVal = _calcpoint->pop();     // Do a pop() to allow a retVal push() below to keep the stack sane.
        }
    }
    catch(...)
    {
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << CtiTime() << " **** EXCEPTION performing component calculate() **** " << __FILE__ << " (" << __LINE__ << ")" << endl;
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

void CtiCalcComponent::primeHistoricalRegression(CtiCalc *calcPoint, CtiTime &pointTime, int number)
{
    if( calcPoint != NULL )
    {
        typedef pair<long, double> PointValuePair;
        typedef map<CtiTime, PointValuePair> DynamicTableSinglePointData;
        typedef map<CtiTime, PointValuePair >::iterator DynamicTableSinglePointDataIter;

        DynamicTableSinglePointData dataMap;
        long regressionPt = calcPoint->getRegressionComponentId();

        CtiPointStore* pointStore = CtiPointStore::getInstance();
        CtiHashKey pointHashKey(calcPoint->getPointId());
        CtiPointStoreElement* calcPointPtr = (CtiPointStoreElement*)((*pointStore).findValue(&pointHashKey));

        if( calcPointPtr != NULL && regressionPt != 0 )
        {
            try
            {
                //  connect to the database
                CtiLockGuard<CtiSemaphore> cg(gDBAccessSema);
                RWDBConnection conn = getConnection( );
    
                RWDBDatabase db             = conn.database();
                RWDBTable    table     = db.table("RAWPOINTHISTORY");
                RWDBSelector selector  = db.selector();
    
                selector << table["POINTID"]
                << table["TIMESTAMP"]
                << table["VALUE"];
    
                selector.from( table );
    
                selector.where( selector["POINTID"] == regressionPt );
                selector.orderByDescending( selector["TIMESTAMP"] );
    
                RWDBReader  rdr = selector.reader( conn );
    
                int i = 0;
                long pointid;
                double value;
                CtiTime timeStamp;
                //  iterate through the components
                while( rdr() && i < number )
                {
                    //  read 'em in, and append to the data structure
                    rdr["POINTID"] >> pointid;
                    rdr["TIMESTAMP"] >> timeStamp;
                    rdr["VALUE"] >> value;
    
                    PointValuePair insertPair(pointid, value);
                    if( timeStamp != pointTime )
                    {
                        dataMap.insert(DynamicTableSinglePointData::value_type(timeStamp, insertPair));
                        i++;
                    }
                    else
                    {
                        CtiLockGuard<CtiLogger> logger_guard(dout);
                        dout << CtiTime() << " - TIMESTAMP ERROR: " << timeStamp.asString() << " = " << pointTime.asString() << endl;
                    }
                }
    
                DynamicTableSinglePointDataIter iter;
                
                for( iter = dataMap.begin(); iter != dataMap.end(); iter++ )
                {
                    calcPointPtr->addRegressionVal(iter->first,  iter->second.second); 
                }
    
            }
            catch( RWxmsg &msg )
            {
                CtiLockGuard<CtiLogger> doubt_guard(dout);
                dout << "Exception while reading calc last updated time from database: " << msg.why( ) << endl;
                exit( -1 );
            }
            catch(...)
            {
                CtiLockGuard<CtiLogger> logger_guard(dout);
                dout << CtiTime() << " - Caught '...' in: " << __FILE__ << " at:" << __LINE__ << endl;
            }
        }
    }
}
