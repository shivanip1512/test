#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __CALCCOMPONENT_H__
#define __CALCCOMPONENT_H__

#include "ctitime.h"
#include "ctidate.h"
#include <rw/collect.h>

#include "pointstore.h"

class CtiCalc;

class CtiCalcComponent : public RWCollectable
{
private:
    enum ptType
    {
        operation,
        constant,
        function
    }                    _componentType;
    long                 _pointId;
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
    }                    _operationType;
    double               _constantValue;
    string            _functionName;
    CtiCalc              *_calcpoint;
    int                  _pointUpdated;
    BOOL                 _valid;
    long                 _lastUseUpdateNum;

    double               _doFunction( string &, bool &validCalc );

protected:
    long                 _componentPointId;

public:
    RWDECLARE_COLLECTABLE( CtiCalcComponent );

    CtiCalcComponent( ) :
    _componentType(constant), _pointId(-1), _componentPointId(-1), _operationType(multiplication),
    _constantValue(0.0), _functionName(""), _valid(0), _lastUseUpdateNum(0), _calcpoint(NULL)
    {}

    CtiCalcComponent( const string &componentType, long componentPointId, const string &operationType,
                      double constantValue, const string &functionName );

    CtiCalcComponent( CtiCalcComponent const &copyFrom )  
    {  
        *this = copyFrom; 
    }

    ~CtiCalcComponent( )  {};
    void passParent( CtiCalc *parent )    //  this is only for the stack functionality, push( ) and pop( )
    {
        _calcpoint = parent;
    };

    const string& getFunctionName() {return _functionName;};
    long getComponentPointId() {return _componentPointId;};

    BOOL isValid( void )  {  return _valid;};

    BOOL isUpdated( int calcsUpdateType = 0, const CtiTime &calcsLastUpdateTime = CtiTime() );

    CtiCalcComponent  &operator=( const CtiCalcComponent &componentToCopy );
    double            calculate( double input, int &component_quality, CtiTime &component_time, bool &calcValid );

//  as soon as the FIXME in calccomponent.cpp is done, these can be uncommented
//    or, if they're never used, delete the whole shebang...
//   void              saveGuts( RWvostream &aStream ) const;
//   void              restoreGuts( RWvistream &aStream );
};

#endif   // #ifndef __CALCCOMPONENT_H__
