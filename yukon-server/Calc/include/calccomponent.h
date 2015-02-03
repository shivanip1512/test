#pragma once

#include "ctitime.h"
#include "ctidate.h"

#include "pointstore.h"

class CtiCalc;

class CtiCalcComponent
{
private:
    enum ptType
    {
        operation,
        constant,
        function
    };
    enum opType
    {
        addition,
        subtraction,
        division,
        multiplication,
        push,
        greater,
        geq,
        less,
        leq,
        modulo
    };
    enum limittype
    {
        HighLimit1 = 1,
        HighLimit2,
        LowLimit1,
        LowLimit2
    };
    ptType               _componentType;
    opType               _operationType;
    double               _constantValue;
    std::string          _functionName;
    CtiCalc              *_calcpoint;
    BOOL                 _valid;
    long                 _lastUseUpdateNum;

    double               _doFunction( std::string &, bool &validCalc );
    void                 primeHistoricalRegression(CtiCalc *calcPoint, CtiTime &pointTime, int number);

protected:
    long                 _componentPointId;

public:

    CtiCalcComponent( ) :
    _componentType(constant), _operationType(multiplication), _constantValue(0.0), _functionName(""),
    _calcpoint(NULL), _valid(0), _lastUseUpdateNum(0), _componentPointId(0)
    {};

    CtiCalcComponent( const std::string &componentType, long componentPointId, const std::string &operationType,
                      double constantValue, const std::string &functionName );

    CtiCalcComponent( CtiCalcComponent const &copyFrom ) :
    _componentType(constant), _operationType(multiplication), _constantValue(0.0), _functionName(""),
    _calcpoint(NULL), _valid(0), _lastUseUpdateNum(0), _componentPointId(0)
    {  
        *this = copyFrom; 
    }

    ~CtiCalcComponent( )  {};
    void passParent( CtiCalc *parent )    //  this is only for the stack functionality, push( ) and pop( )
    {
        _calcpoint = parent;
    };

    const std::string& getFunctionName() {return _functionName;};
    long getComponentPointId() {return _componentPointId;};
    
    BOOL isValid( void )  {  return _valid;};

    BOOL isUpdated( int calcsUpdateType = 0, const CtiTime &calcsLastUpdateTime = CtiTime() );

    CtiCalcComponent  &operator=( const CtiCalcComponent &componentToCopy );
    double            calculate( double input, int &component_quality, CtiTime &component_time, bool &calcValid );
};
