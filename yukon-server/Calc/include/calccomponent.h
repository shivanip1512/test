#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __CALCCOMPONENT_H__
#define __CALCCOMPONENT_H__

#include <rw/rwtime.h>
#include <rw/rwdate.h>
#include <rw/cstring.h>
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
        leq
    }                    _operationType;
    double               _constantValue;
    RWCString            _functionName;
    CtiCalc              *_parent;
    int                  _pointUpdated;
    BOOL                 _valid;
    long                 _lastUseUpdateNum;
    int                  _updatesInCurrentAvg;

    double               _doFunction( RWCString & );

    double               _figureDemandAvg( long );

protected:
    long                 _componentPointId;

public:
    RWDECLARE_COLLECTABLE( CtiCalcComponent );

    CtiCalcComponent( ) :
    _componentType(constant), _pointId(-1), _componentPointId(-1), _operationType(multiplication),
    _constantValue(0.0), _functionName(""), _valid(0), _lastUseUpdateNum(0), _parent(NULL), _updatesInCurrentAvg(0)
    {};

    CtiCalcComponent( const RWCString &componentType, long componentPointId, const RWCString &operationType,
                      double constantValue, const RWCString &functionName );

    CtiCalcComponent( CtiCalcComponent const &copyFrom )  {  *this = copyFrom;};

    ~CtiCalcComponent( )  {};
    void passParent( CtiCalc *parent )    //  this is only for the stack functionality, push( ) and pop( )
    {
        _parent = parent;
    };

    const RWCString& getFunctionName() {return _functionName;};
    long getComponentPointId() {return _componentPointId;};

    BOOL isValid( void )  {  return _valid;};

    BOOL isUpdated( int calcsUpdateType = 0, const RWTime &calcsLastUpdateTime = RWTime() );

    CtiCalcComponent  &operator=( const CtiCalcComponent &componentToCopy );
    double            calculate( double input, int &component_quality, RWTime &component_time );

//  as soon as the FIXME in calccomponent.cpp is done, these can be uncommented
//    or, if they're never used, delete the whole shebang...
//   void              saveGuts( RWvostream &aStream ) const;
//   void              restoreGuts( RWvistream &aStream );
};

#endif   // #ifndef __CALCCOMPONENT_H__
