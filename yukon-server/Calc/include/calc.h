#pragma warning( disable : 4786 )  // No truncated debug name warnings please....
#ifndef __CALC_H__
#define __CALC_H__

#include <rw/slistcol.h>

#include "calccomponent.h"

class CtiCalc : public RWCollectable
{
   RWDECLARE_COLLECTABLE( CtiCalc );

private:
   RWSlistCollectables  _components;
   PointUpdateType      _updateType;
   ULONG                _nextInterval;
   int                  _updateInterval;
//   int                  _updateInterval, _countdown;
   long                 _pointId;
   BOOL                 _valid;

   // text from the database
   static const CHAR * UpdateType_Periodic;
   static const CHAR * UpdateType_AllChange;
   static const CHAR * UpdateType_OneChange;
   static const CHAR * UpdateType_Historical;

public:

   CtiCalc( ) : 
      _updateType(undefined), _updateInterval(-1), _pointId(-1), _valid(FALSE), _nextInterval( 1 )
   {  };

   CtiCalc( long pointId, const RWCString &updateType, int updateInterval );

   ~CtiCalc( )  {  cleanup( );  };

   ULONG     getNextInterval() const;
   CtiCalc&  setNextInterval (int interval);
	int      getUpdateInterval( ) const;

   long getPointId( void )  {  return _pointId;  };

   CtiCalc &operator=( CtiCalc &toCopy );
   BOOL operator==( CtiCalc &equalTest )  {  return _pointId == equalTest.getPointId( );  }
   
   void appendComponent( CtiCalcComponent *componentToAdd );
   void cleanup( void );
   PointUpdateType getUpdateType( void );
   double calculate( void );
   BOOL ready( void );
   

   //  see FIX_ME in calc.cpp
   //void saveGuts( RWvostream &aStream ) const;
   //void restoreGuts( RWvistream &aStream );
};

#endif   // #ifndef __CALC_H__


