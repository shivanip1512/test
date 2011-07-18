#include "precompiled.h"


#include <vector>
using namespace std;

#include "telegyrgroup.h"


//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrGroup::CtiTelegyrGroup() :
    _interval(0),
    _groupID(0)
{
}


//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrGroup::~CtiTelegyrGroup()
{
   //delete point list in here start to finish
   _pointList.erase( _pointList.begin(), _pointList.end() );
}


//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrGroup& CtiTelegyrGroup::operator=( const CtiTelegyrGroup &other )
{
   if( this != &other )
   {
      _groupName = other.getGroupName();
      _groupID = getGroupID();
      _groupType = other.getGroupName();
      _interval = getInterval();

      _pointList = other._pointList;
   }
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

string CtiTelegyrGroup::getGroupName( void ) const
{
   return _groupName;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrGroup& CtiTelegyrGroup::setGroupName( string grpName )
{
   _groupName = grpName;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

string CtiTelegyrGroup::getGroupType( void ) const
{
   return _groupType;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrGroup& CtiTelegyrGroup::setGroupType( string grpType )
{
   _groupType = grpType;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

int CtiTelegyrGroup::getGroupID( void )
{
   return _groupID;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrGroup& CtiTelegyrGroup::setGroupID( int grpID )
{
   _groupID = grpID;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

int CtiTelegyrGroup::getInterval( void )
{
   return _interval;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrGroup& CtiTelegyrGroup::setInterval( int intervalIn )
{
   _interval = intervalIn;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

vector< CtiFDRPoint > CtiTelegyrGroup::getPointList( void ) const
{
   return _pointList;
}

//=================================================================================================================================
//=================================================================================================================================

vector< CtiFDRPoint > & CtiTelegyrGroup::getPointList( void )
{
   return _pointList;
}

