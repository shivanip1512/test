#pragma warning( disable : 4786 )  // No truncated debug name warnings please....

#include <iostream>
using namespace std;

#include <rw/thr/mutex.h>

#include "calccomponent.h"
#include "logger.h"

RWDEFINE_NAMED_COLLECTABLE( CtiCalcComponent, "CtiCalcComponent" );


CtiCalcComponent::CtiCalcComponent( const RWCString &componentType, long componentPointId, 
                                    const RWCString &operationType, CtiPointStoreElement *pointPtr, 
                                    double constantValue, const RWCString &functionName )
{
   _valid = TRUE;
      
   if( pointPtr == NULL && !componentType.compareTo("operation", RWCString::ignoreCase) )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << "ERROR creating CtiCalcComponent - operation with ComponentPointID of 0 - setting invalid flag" << endl;
       _valid = FALSE;
   } 
   else if( !componentType.compareTo("operation", RWCString::ignoreCase) )
   {
      _componentType = operation;
      _componentPointId = componentPointId;
      _pointPtr = pointPtr;
      _lastUseUpdateNum = -1;
      
      if( operationType == "+" ) 
      {
            _operationType = addition;
      }
      else if( operationType == "-" )  
      {
          _operationType = subtraction;
      }
      else if( operationType == "*" )
      {
            _operationType = multiplication;
      }
      else if( operationType == "/" )  
      {
            _operationType = division;
      }
      else
      {
          _valid = FALSE;
      }

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
         _valid = FALSE;

      if( operationType == "+" ) 
      {
            _operationType = addition;
      }
      else if( operationType == "-" )  
      {
          _operationType = subtraction;
      }
      else if( operationType == "*" )
      {
            _operationType = multiplication;
      }
      else if( operationType == "/" )  
      {
            _operationType = division;
      }
      else
      {
          _valid = FALSE;
      }

      {
          CtiLockGuard<CtiLogger> doubt_guard(dout);
          dout << "Adding CtiCalcComponent - Constant ComponentPointID = " << componentPointId << " Const: " << _constantValue << endl;
      }


   }
   else if( !componentType.compareTo("function", RWCString::ignoreCase)  )
   {
      _componentType = function;
      _functionName = functionName;
   }
   else
   {
      _valid = FALSE;   

      {
          CtiLockGuard<CtiLogger> doubt_guard(dout);
          dout << "Invalid CtiCalcComponent - ComponentPointID = " << componentPointId << endl;
      }

   }
}
   
CtiCalcComponent &CtiCalcComponent::operator=( const CtiCalcComponent &copyFrom )
{
   _componentType = copyFrom._componentType;
   _pointId = copyFrom._pointId;
   _componentPointId = copyFrom._componentPointId;
   _operationType = copyFrom._operationType;
   _pointPtr = copyFrom._pointPtr;
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
     return ( (_lastUseUpdateNum == _pointPtr->getNumUpdates()) || 
              (_pointPtr->getPointQuality() == UnintializedQuality) )?(FALSE):(TRUE);
  else
     return TRUE;
}

double CtiCalcComponent::calculate( double input )
{
    double tempValue = 0;

    if ( _componentType == operation)
    {
        // using a point value

        // handle operations on points
        _lastUseUpdateNum = _pointPtr->getNumUpdates( );

        tempValue = _pointPtr->getPointValue( );
    }
    else if ( _componentType == constant)
    {
        // using a constant
        tempValue = _constantValue;
    }
    else
    {
        //  Must be a function something something _functionName
        {
            CtiLockGuard<CtiLogger> doubt_guard(dout);
            dout << __FILE__ << " (" << __LINE__ << ")  \"function\" code will be implemented someday...  for now, returning \'input\'" << endl;
        }
        
        return input;
    }


    switch ( _operationType )
    {
        case addition:
            input += tempValue;
            break;
    
        case subtraction:
            input -= tempValue;
            break;
    
        case multiplication: 
            input *= tempValue;
            break;
    
        case division:
            if (tempValue != 0)
                input /= tempValue;

            break;
    }

    return input;
}
