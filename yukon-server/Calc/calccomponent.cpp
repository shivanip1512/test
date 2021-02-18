#include "precompiled.h"

#include <math.h>
#include <iostream>
#include <sstream>
using namespace std;

#include "calccomponent.h"
#include "logger.h"
#include "calc.h"
#include "utility.h"
#include "database_connection.h"
#include "database_reader.h"

extern ULONG _CALC_DEBUG;
extern bool _ignoreTimeValidTag;

// square root of 3 for power factor calculations
#define SQRT3               1.7320508075688772935274463415059

using Cti::Database::DatabaseConnection;
using Cti::Database::DatabaseReader;

extern DatabaseConnection::QueryTimeout getCalcQueryTimeout();

CtiCalcComponent::CtiCalcComponent( const string &componentType, long componentPointId,
                                    const string &operationType,
                                    double constantValue, const string &functionName ) :
_componentType(constant), _operationType(multiplication), _constantValue(0.0), _functionName(""),
_calcpoint(NULL), _valid(0), _lastUseUpdateNum(0), _componentPointId(0)
{
    _valid = TRUE;

    if( componentPointId == 0 && ciStringEqual(componentType,"operation" ) )
    {
        CTILOG_ERROR(dout, "Failed to create CtiCalcComponent - operation with ComponentPointID of 0 - setting invalid flag");
        _valid = FALSE;
    }
    else if( ciStringEqual(componentType,"operation") )
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
        else if( ciStringEqual(operationType,"push" ) )  _operationType = push;
        else
        {
            CTILOG_ERROR(dout, "Invalid Operation Type: "<< operationType <<" ComponentPointID = "<< componentPointId);
            _valid = FALSE;
        }

        if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
        {
            CTILOG_DEBUG(dout, "Adding CtiCalcComponent - Normal Operation ComponentPointID = "<< componentPointId);
        }

    }
    else if( ciStringEqual(componentType,"constant" ) )
    {
        _componentType = constant;
        _constantValue = constantValue;

        if( operationType == "+" )          _operationType = addition;
        else if( operationType == "-" )     _operationType = subtraction;
        else if( operationType == "*" )     _operationType = multiplication;
        else if( operationType == "/" )     _operationType = division;
        else if( operationType == "%" )     _operationType = modulo;
        else if( ciStringEqual(operationType,"push" ) )  _operationType = push;
        else
        {
            CTILOG_ERROR(dout, "Invalid Operation Type: "<< operationType <<" ComponentPointID = "<< componentPointId);
            _valid = FALSE;
        }

        if( _CALC_DEBUG & CALC_DEBUG_CALC_INIT )
        {
            CTILOG_DEBUG(dout, "Adding CtiCalcComponent - Constant ComponentPointID = "<< componentPointId <<" Const: "<< _constantValue);
        }
    }
    else if( ciStringEqual(componentType,"function" ) )
    {
        _componentType = function;
        _functionName = functionName;
        _componentPointId = componentPointId;
        _lastUseUpdateNum = 0;
    }
    else
    {
        CTILOG_ERROR(dout, "Invalid CtiCalcComponent - ComponentPointID = "<< componentPointId <<" ComponentType: "<< componentType);
        _valid = FALSE;
    }
}

CtiCalcComponent &CtiCalcComponent::operator=( const CtiCalcComponent &copyFrom )
{
    _componentType = copyFrom._componentType;
    _operationType = copyFrom._operationType;
    _constantValue = copyFrom._constantValue;
    _functionName = copyFrom._functionName;
    _valid = copyFrom._valid;
    _lastUseUpdateNum = copyFrom._lastUseUpdateNum;
    _componentPointId = copyFrom._componentPointId;

    return *this;
}

BOOL CtiCalcComponent::isUpdated( CalcUpdateType calcsUpdateType, const CtiTime &calcsLastUpdateTime )
{
    //  you can only be updated (or non-) if you're a point...
    if( _componentType == operation )
    {
        CtiPointStoreElement* componentPointPtr = CtiPointStore::find(_componentPointId);

        if( componentPointPtr->getPointQuality() == NonUpdatedQuality ||
            componentPointPtr->getPointQuality() == ConstantQuality  )
        {
            if( _CALC_DEBUG & CALC_DEBUG_POINTDATA_QUALITY )
            {
                CTILOG_DEBUG(dout, "Point quality is constant or non-updated, Point ID: "<< componentPointPtr->getPointNum());
            }
            return TRUE;
        }
        else if( componentPointPtr->getPointTags() & (TAG_DISABLE_DEVICE_BY_DEVICE | TAG_DISABLE_POINT_BY_POINT) )
        {
            if( _CALC_DEBUG & CALC_DEBUG_POINTDATA_QUALITY )
            {
                CTILOG_DEBUG(dout, "Point tags mark component disabled, Point ID: "<< componentPointPtr->getPointNum());
            }
            return TRUE;
        }
        else if( ciStringEqual(_functionName,"Get Interval Minutes") || ciStringEqual(_functionName,"Get Point Limit") )
        {
            return TRUE;
        }
        else if( (calcsUpdateType == CalcUpdateType::PeriodicPlusUpdate) )
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

    if(_componentPointId != 0)
    {
        // If this component HAS a component ID it means it is associated with a point.  We discover and push the point's value onto the stack
        // so it may be operated upon.  The quality and time are returned to allow the calculation to be graded based upon ALL such returns.
        // input is passed in by value and returned by the function....

        if( CtiPointStoreElement* componentPointPtr = CtiPointStore::find(_componentPointId) )
        {
            _lastUseUpdateNum = componentPointPtr->getNumUpdates( );
            component_time = componentPointPtr->getPointTime();
            component_quality = componentPointPtr->getPointQuality();
            if(componentPointPtr->getPointTags() & (TAG_DISABLE_DEVICE_BY_DEVICE | TAG_DISABLE_POINT_BY_POINT))
            {
                component_quality = QuestionableQuality;
            }

            if( _calcpoint->getUpdateType() == CalcUpdateType::Historical ||
                _calcpoint->getUpdateType() == CalcUpdateType::BackfillingHistorical )
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
           CTILOG_WARN(dout, "attempt to \'push\' with no parent pointer - returning \'input\'");
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
            CTILOG_DEBUG(dout, "constant operation; input:"<< orignal <<",   constant:"<< _constantValue <<",   return: "<< input);
        }
    }
    else if( _componentType == function )
    {
        //  this, to keep this function small, and the functions elsewhere for maintenence
        input = _doFunction( _functionName, calcValid );
        if( _CALC_DEBUG & CALC_DEBUG_COMPONENT_POSTCALC_VALUE )
        {
            CTILOG_DEBUG(dout, "calculate returns: "<< input);
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
        if( ciStringEqual(functionName,"addition" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = operand1 + operand2;
        }
        else if( ciStringEqual(functionName,"subtraction" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = operand1 - operand2;
        }
        else if( ciStringEqual(functionName,"multiplication" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = operand1 * operand2;
        }
        else if( ciStringEqual(functionName,"division" ) )
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
        else if( ciStringEqual(functionName,"modulo divide" ) )
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
        else if( ciStringEqual(functionName,">") || ciStringEqual(functionName,"greater than" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 > operand2) ? 1.0 : 0.0;
        }
        else if( ciStringEqual(functionName,">=" ) || ciStringEqual(functionName,"geq than" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 >= operand2) ? 1.0 : 0.0;
        }
        else if( ciStringEqual(functionName,"<" ) || ciStringEqual(functionName,"less than" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 < operand2) ? 1.0 : 0.0;
        }
        else if( ciStringEqual(functionName,"<=" ) || ciStringEqual(functionName,"leq than" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 <= operand2) ? 1.0 : 0.0;
        }
        else if( ciStringEqual(functionName,"logical and" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 != 0.0 && operand2 != 0.0) ? 1.0 : 0.0;
        }
        else if( ciStringEqual(functionName,"logical or" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = (operand1 != 0.0 || operand2 != 0.0) ? 1.0 : 0.0;
        }
        else if( ciStringEqual(functionName,"logical not" ) )
        {
            double operand = _calcpoint->pop( );

            retVal = (operand == 0.0) ? 1.0 : 0.0;
        }
        else if( ciStringEqual(functionName,"logical xor" ) )
        {
            double operand2 = _calcpoint->pop( );
            double operand1 = _calcpoint->pop( );

            retVal = ((operand1 != 0.0) ^ (operand2 != 0.0)) ? 1.0 : 0.0;
        }
        //  Transformer Thermal Age Calculation
        //  params:  Thermal Age Hours - the current thermal age of the transformer
        //           HotSpotTemp - the hot spot temperature of the transformer, calculated elsewhere
        //           UpdateFreq - the minutes between updates of the thermal age
        else if( ciStringEqual(functionName,"XfrmThermAge" ) )
        {
            double ThermalAgeHours, HotSpotTemp, UpdateFreq, tmp;
            ThermalAgeHours = _calcpoint->pop( );
            HotSpotTemp = _calcpoint->pop( );
            UpdateFreq = _calcpoint->pop( );

            if( UpdateFreq < 0.001 )
            {
                CTILOG_WARN(dout, "in \'XfrmThermAge\' function -  parameter UpdateFreq < 0.001, setting to 1.0e10 to prevent runaway transformer aging");
                UpdateFreq = 1.0e10;
            }

            tmp = ((15000.0 / 383.0) - 15000.0 / (HotSpotTemp + 273.0));
            ThermalAgeHours += pow( 2.78, tmp ) / (60.0 / UpdateFreq);

            retVal = ThermalAgeHours;
        }
        //  Hot Spot Calculation
        //  params:
        else if( ciStringEqual(functionName,"HotSpot" ) )
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
                CTILOG_WARN(dout, "in \'HotSpot\' function - Rating parameter equal to zero, setting to 1");
                Rating = 1.0;
            }

            double NaNDefenseDouble = (LoadWatts*LoadWatts)+(LoadVARs*LoadVARs);
            if( NaNDefenseDouble < 0.0 )
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
        else if( ciStringEqual(functionName,"DemandAvg1" ) )
        {
            retVal = _calcpoint->figureDemandAvg(60);// seconds in avg
        }
        else if( ciStringEqual(functionName,"DemandAvg5" ) )
        {
            retVal = _calcpoint->figureDemandAvg(300);// seconds in avg
        }
        else if( ciStringEqual(functionName,"DemandAvg15" ) )
        {
            retVal = _calcpoint->figureDemandAvg(900);// seconds in avg
        }
        else if( ciStringEqual(functionName,"DemandAvg30" ) )
        {
            retVal = _calcpoint->figureDemandAvg(1800);// seconds in avg
        }
        else if( ciStringEqual(functionName,"DemandAvg60" ) )
        {
            retVal = _calcpoint->figureDemandAvg(3600);// seconds in avg
        }
        else if( ciStringEqual(functionName,"P-Factor KW/KVar" ) )
        {
            double kvar = _calcpoint->pop();
            double kw = _calcpoint->pop();
            double newPowerFactorValue = 1.0;
            double kva = 0.0;

            double NaNDefenseDouble = (kw*kw)+(kvar*kvar);
            if( NaNDefenseDouble < 0.0 )
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
        else if( ciStringEqual(functionName,"P-Factor KW/KQ" ) )
        {
            double kq = _calcpoint->pop();
            double kw = _calcpoint->pop();
            double kvar = ((2.0*kq)-kw)/SQRT3;
            double newPowerFactorValue = 1.0;
            double kva = 0.0;

            double NaNDefenseDouble = (kw*kw)+(kvar*kvar);
            if( NaNDefenseDouble < 0.0 )
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
        else if( ciStringEqual(functionName,"P-Factor KW/KVa" ) )
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
        else if( ciStringEqual(functionName,"KVar from KW/KQ" ) )
        {
            double kq = _calcpoint->pop();
            double kw = _calcpoint->pop();
            double kvar = ((2.0*kq)-kw)/SQRT3;

            retVal = kvar;
        }
        else if( ciStringEqual(functionName,"KVa from KW/KVar" ) )
        {
            double kvar = _calcpoint->pop();
            double kw = _calcpoint->pop();

            double NaNDefenseDouble = (kw*kw)+(kvar*kvar);
            double kva = 0.0;
            if( NaNDefenseDouble < 0.0 )
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
        else if( ciStringEqual(functionName,"KVa from KW/KQ" ) )
        {
            double kq = _calcpoint->pop();
            double kw = _calcpoint->pop();
            double kvar = ((2.0*kq)-kw)/SQRT3;

            double kva = 0.0;
            double NaNDefenseDouble = (kw*kw)+(kvar*kvar);
            if( NaNDefenseDouble < 0.0 )
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
        else if( ciStringEqual(functionName,"KW from KVa/KVAR" ) )
        {
            double kvar = _calcpoint->pop();
            double kva = _calcpoint->pop();

            double kw = 0.0;
            double NaNDefenseDouble = (kva*kva)-(kvar*kvar);
            if( NaNDefenseDouble < 0.0 )
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
        else if( ciStringEqual(functionName,"KVAR from KW/KVa" ) )
        {
            double kva = _calcpoint->pop();
            double kw = _calcpoint->pop();

            double kvar = 0.0;
            double NaNDefenseDouble = (kva*kva)-(kw*kw);
            if( NaNDefenseDouble < 0.0 )
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
        else if( ciStringEqual(functionName,"Squared" ) )
        {
            double componentPointValue = _calcpoint->pop( );;

            retVal = componentPointValue*componentPointValue;
        }
        else if( ciStringEqual(functionName,"Square Root" ) )
        {
            double componentPointValue = _calcpoint->pop( );;

            if( componentPointValue < 0.0 )
            {
                validCalc = false;
                retVal = 0.0;
            }
            else
            {
                retVal = sqrt(componentPointValue);
            }
        }
        else if( ciStringEqual(functionName,"COS from P/Q" ) )
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

                CTILOG_ERROR(dout, "Cannot divide by zero - COS from P/Q => Q is zero");
            }
        }
        else if( ciStringEqual(functionName,"sin") )
        {
            double temp = _calcpoint->pop();
            retVal = sin(temp);
        }
        else if( ciStringEqual(functionName,"cos") )
        {
            double temp = _calcpoint->pop();
            retVal = cos(temp);
        }
        else if( ciStringEqual(functionName,"tan") )
        {
            double temp = _calcpoint->pop();
            retVal = tan(temp);
        }
        else if( ciStringEqual(functionName,"arcsin") )
        {
            double componentPointValue = _calcpoint->pop( );;
            retVal = asin(componentPointValue);
        }
        else if( ciStringEqual(functionName,"arccos") )
        {
            double componentPointValue = _calcpoint->pop( );;
            retVal = acos(componentPointValue);
        }
        else if( ciStringEqual(functionName,"arctan") )
        {
            double componentPointValue = _calcpoint->pop( );;
            retVal = atan(componentPointValue);
        }
        else if( ciStringEqual(functionName,"X^Y" ) )
        {
            double y = _calcpoint->pop();
            double x = _calcpoint->pop();
            retVal = pow(x,y);
        }
        else if( ciStringEqual(functionName,"Max" ) )
        {
            DOUBLE a = _calcpoint->pop();
            DOUBLE b = _calcpoint->pop();

            retVal = ((a)>(b))?(a):(b);
        }
        else if( ciStringEqual(functionName,"Min" ) )
        {
            DOUBLE a = _calcpoint->pop();
            DOUBLE b = _calcpoint->pop();

            retVal = ((a)<(b))?(a):(b);
        }
        else if( ciStringEqual(functionName,"Absolute Value" ) )
        {
            double a = _calcpoint->pop();
            retVal = fabs(a);
        }
        else if( ciStringEqual(functionName,"Max Difference" ) )
        {
            double a = _calcpoint->pop();
            double b = _calcpoint->pop();
            double c = _calcpoint->pop();

            retVal = (fabs(a-b) + fabs(b-c) + fabs(c-a)) / 2.0;

        }

        else if( ciStringEqual(functionName,"lohi accumulator") )
        {
            double val = _calcpoint->pop();
            double inc = 0.0;

            if( CtiPointStoreElement* componentPointPtr = CtiPointStore::find(_componentPointId) )
            {
                int tags = componentPointPtr->getPointTags();
                if(tags & TAG_POINT_DATA_TIMESTAMP_VALID)
                {
                    inc = 1.0;
                }
            }

            retVal = val + inc;
        }
        else if( ciStringEqual(functionName,"State Timer") )
        {
            /*
             *  This function pops a number representing the trigger state of a status and returns the
             *  number of seconds since the point entered that state
             */
            double pt_val = _calcpoint->pop();       // This should be the point's value.
            double state  = _calcpoint->pop();   // This is the state value it must be in.  Return zero if the state is not correct.

            retVal = 0;
            if(_componentPointId != 0)
            {
                if( CtiPointStoreElement* componentPointPtr = CtiPointStore::find(_componentPointId) )
                {
                    CtiTime now;
                    CtiTime component_time = componentPointPtr->getLastValueChangedTime();

                    if(pt_val == state)
                        retVal = now.seconds() - component_time.seconds();
                }
            }
        }
        else if( ciStringEqual(functionName,"True,False,Condition") )
        {
            double c = _calcpoint->pop();
            double b = _calcpoint->pop();
            double a = _calcpoint->pop();

            retVal = ( c != 0.0 ? a : b );
        }
        else if( ciStringEqual(functionName,"Regression") )      // Stack has Depth,Minutes,Value
        {
            /*
             *  This function pops a number representing the trigger state of a status and returns the
             *  number of seconds since the point entered that state
             */
            double value  = _calcpoint->pop();  // This should be the point's most recent value.  It is not used by the regression computation.
            ULONG  min    = _calcpoint->pop();  // This is the number of minutes forward we want to compute.
            int    depth  = _calcpoint->pop();  // This is the storage depth of our regression.

            retVal = 0;
            if(_componentPointId != 0)
            {
                if( CtiPointStoreElement* componentPointPtr = CtiPointStore::find(_componentPointId) )
                {
                    CtiTime now;
                    componentPointPtr->resize_regession( depth );
                    retVal = componentPointPtr->regression( now.seconds() + (min * 60) );
                }
            }
        }
        else if( ciStringEqual(functionName,"Float From 16bit") )      // Stack has two 16 bit unsigned.
        {
            unsigned short umsw = _calcpoint->pop();
            unsigned short ulsw = _calcpoint->pop();

            unsigned fpu = (unsigned short)umsw;
            fpu = (fpu << 16) + ulsw;

            float *floatPtr = (float *)&fpu;

            retVal = *floatPtr;
        }
        else if( ciStringEqual(functionName,"Binary Encode") )
        {
            double newValue = _calcpoint->pop( );
            double oldResult  = _calcpoint->pop( );

            oldResult = oldResult*2;
            retVal = oldResult + newValue;
        }
        else if( ciStringEqual(functionName,"Mid Level Latch") )
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
                retVal = CtiPointStore::find(_calcpoint->getPointId())->getPointValue();
            }
        }
        else if( ciStringEqual(functionName,"Get Interval Minutes") )
        {
            retVal = 0;
            if( _calcpoint != NULL )
            {
                retVal = _calcpoint->getUpdateInterval();
            }

            retVal = retVal/60; //Convert to minutes
        }
        else if( ciStringEqual(functionName,"Get Point Limit") )
        {
            _calcpoint->pop(); //throwaway value
            int limitFunc = _calcpoint->pop( );
            retVal = 0;

            if( _componentPointId != 0 )
            {
                CtiPointStoreElement* componentPtr = CtiPointStore::find(_componentPointId);

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
        else if( ciStringEqual(functionName,"Intervals To Value") )      // Stack has Depth,Minutes,Value
        {
            // This function calculates a regression and estimates the number of intervals until a limit is reached.

            double value  = _calcpoint->pop();  // This should be the point's most recent value.  It is not used by the regression computation.
            double limit  = _calcpoint->pop();  // The limit value we are trying to reach
            int    mindepth  = _calcpoint->pop();  // This is the storage depth of our regression.
            int    depth  = _calcpoint->pop();  // This is the storage depth of our regression.
            int    isMax  = _calcpoint->pop();  // is the value a max value (or a min)?
            int    needsHistory  = _calcpoint->pop();  // Are we supposed to look at historical values?

            retVal = 9999;
            if(_componentPointId != 0)
            {
                if( CtiPointStoreElement* calcPointPtr = CtiPointStore::find(_calcpoint->getPointId()) )
                {
                    CtiTime pointTime;
                    CtiPointStoreElement* componentPointPtr = CtiPointStore::find(_componentPointId);
                    if( _calcpoint->getUpdateType() != CalcUpdateType::Periodic && componentPointPtr != NULL )
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

                    if(componentPointPtr != NULL && _calcpoint != NULL && _calcpoint->getUpdateType() == CalcUpdateType::AnyUpdate)
                    {
                        if( componentPointPtr->getPointTags() & TAG_POINT_DATA_TIMESTAMP_VALID || _ignoreTimeValidTag )
                        {
                            calcPointPtr->addRegressionVal( pointTime, value );
                        }
                        //else do not add regression value.
                    }
                    else
                    {
                        calcPointPtr->addRegressionVal( pointTime, value );
                    }

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
            if( retVal > 9999 )
            {
                retVal = 9999;
            }
        }
        else if( ciStringEqual(functionName,"Linear Slope") )      // Stack has Depth,Minutes,Value
        {
            // This function calculates a regression and estimates the number of intervals until a limit is reached.

            double value  = _calcpoint->pop();  // This should be the point's most recent value.  It is not used by the regression computation.
            int    mindepth  = _calcpoint->pop();  // This is the storage depth of our regression.
            int    depth  = _calcpoint->pop();  // This is the storage depth of our regression.
            int    needsHistory  = _calcpoint->pop();  // Are we supposed to look at historical values?

            retVal = 0;
            if(_componentPointId != 0)
            {
                if( CtiPointStoreElement* calcPointPtr = CtiPointStore::find(_calcpoint->getPointId()) )
                {
                    CtiTime pointTime;
                    CtiPointStoreElement* componentPointPtr = CtiPointStore::find(_componentPointId);

                    calcPointPtr->setRegressionMinDepth(mindepth);
                    calcPointPtr->setRegressionDepth(depth);

                    if( _calcpoint->getUpdateType() != CalcUpdateType::Periodic && componentPointPtr != NULL )
                    {
                        pointTime = componentPointPtr->getPointTime();
                    }

                    if( needsHistory && !calcPointPtr->isPrimed() )
                    {
                        primeHistoricalRegression(_calcpoint, pointTime,  depth);
                    }

                    double slope, intercept, xAtLimit, yNow;

                    if(componentPointPtr != NULL && _calcpoint != NULL && _calcpoint->getUpdateType() == CalcUpdateType::AnyUpdate)
                    {
                        if( componentPointPtr->getPointTags() & TAG_POINT_DATA_TIMESTAMP_VALID || _ignoreTimeValidTag )
                        {
                            calcPointPtr->addRegressionVal( pointTime, value );
                        }
                        //else do not add regression value.
                    }
                    else
                    {
                        calcPointPtr->addRegressionVal( pointTime, value );
                    }

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

            CTILOG_ERROR(dout, "Function "<< functionName <<" not implemented");

            retVal = _calcpoint->pop();     // Do a pop() to allow a retVal push() below to keep the stack sane.
        }
    }
    catch(...)
    {
        CTILOG_UNKNOWN_EXCEPTION_ERROR(dout, "Failed to perform component calculate(). Failed point: "<< _calcpoint->getPointId());
    }

    //added 7/31/03 JW
    if( _isnan(retVal) || !_finite(retVal) )        // !_finite should be thought of as isInfinite(retVal)  We cannot have that!
    {
        retVal = 0.0;

        CTILOG_ERROR(dout, "_doFunction tried to return a NaN/INF for Calc Point Id: "<< _calcpoint->getPointId());
    }

    _calcpoint->push( retVal );       // Return result to the stack so it can be used properly by another callee/component.

    return retVal;
}

void CtiCalcComponent::primeHistoricalRegression(CtiCalc *calcPoint, CtiTime &pointTime, int number)
{
    if( ! calcPoint )
    {
        return;
    }

    typedef pair<long, double> PointValuePair;
    typedef map<CtiTime, PointValuePair> DynamicTableSinglePointData;
    typedef map<CtiTime, PointValuePair >::iterator DynamicTableSinglePointDataIter;

    DynamicTableSinglePointData dataMap;
    long regressionPt = calcPoint->getRegressionComponentId();

    if( ! regressionPt )
    {
        return;
    }

    if( CtiPointStoreElement* calcPointPtr = CtiPointStore::find(calcPoint->getPointId()) )
    {
        try
        {
            static const string sqlCore = "SELECT RPH.POINTID, RPH.TIMESTAMP, RPH.VALUE "
                                          "FROM RAWPOINTHISTORY RPH "
                                          "WHERE RPH.POINTID = ? ORDER BY TIMESTAMP DESC";

            DatabaseConnection connection { getCalcQueryTimeout() };
            DatabaseReader rdr { connection, sqlCore };

            rdr << regressionPt;

            rdr.execute();

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
                    CTILOG_ERROR(dout, "Timestamp are equal timeStamp != pointTime ("<< timeStamp.asString() <<" = "<< pointTime.asString() <<")");
                }
            }

            DynamicTableSinglePointDataIter iter;

            for( iter = dataMap.begin(); iter != dataMap.end(); iter++ )
            {
                calcPointPtr->addRegressionVal(iter->first,  iter->second.second);
            }

        }
        catch(...)
        {
            CTILOG_UNKNOWN_EXCEPTION_ERROR(dout);
        }
    }
}
