#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __CALCCOMPONENT_H__
#define __CALCCOMPONENT_H__

#include <rw/rwtime.h>
#include <rw/cstring.h>
#include <rw/collect.h>

#include "pointstore.h"

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
      multiplication
   }                    _operationType;
   double               _constantValue;
   RWCString            _functionName;
   CtiPointStoreElement *_pointPtr;
   int                  _pointUpdated;
   BOOL                 _valid;
   long                 _lastUseUpdateNum;
   
protected:
   long                 _componentPointId;       

public:
   RWDECLARE_COLLECTABLE( CtiCalcComponent );

   CtiCalcComponent( ) : 
      _componentType(constant), _pointId(-1), _componentPointId(-1), _operationType(multiplication), 
      _pointPtr(NULL), _constantValue(0.0), _functionName(""), _valid(0), _lastUseUpdateNum(-1)
   {    };
                     
   CtiCalcComponent( const RWCString &componentType, long componentPointId, const RWCString &operationType, 
                     CtiPointStoreElement *pointPtr, double constantValue, const RWCString &functionName );
   
   CtiCalcComponent( CtiCalcComponent const &copyFrom )  {  *this = copyFrom;  };

   ~CtiCalcComponent( )  {  };

   BOOL isValid( void )  {  return _valid;  };

   BOOL isUpdated( void );

   CtiCalcComponent  &operator=( const CtiCalcComponent &componentToCopy );
   double            calculate( double input );

//  as soon as the FIXME in calccomponent.cpp is done, these can be uncommented
//    or, if they're never used, delete the whole shebang...   
//   void              saveGuts( RWvostream &aStream ) const;
//   void              restoreGuts( RWvistream &aStream );
};

#endif   // #ifndef __CALCCOMPONENT_H__
