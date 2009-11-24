#include "yukon.h"


#include <vector>

using namespace std;

#include "telegyrcontrolcenter.h"
#include "telegyrgroup.h"
#include "logger.h"                    //temp include

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter::CtiTelegyrControlCenter() :
    _channelID(0),
    _access(0)
{
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter::~CtiTelegyrControlCenter()
{
   deleteTelegyrGroupList();
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter& CtiTelegyrControlCenter::operator=( const CtiTelegyrControlCenter &other )
{
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

int CtiTelegyrControlCenter::getChannelID( void )
{
   return _channelID;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter& CtiTelegyrControlCenter::setChannelID( int idIn )
{
   _channelID = idIn;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

string CtiTelegyrControlCenter::getOperator( void )
{
   return _operatorName;
}

/*
//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter & CtiTelegyrControlCenter::getOperator( void ) const
{
   return *this;
}
*/

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter& CtiTelegyrControlCenter::setOperator( string opIn )
{
   _operatorName = opIn;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

string CtiTelegyrControlCenter::getPassword( void ) const
{
   return _password;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter& CtiTelegyrControlCenter::setPassword( string passIn )
{
   _password = passIn;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

string CtiTelegyrControlCenter::getSysName( void ) const
{
   return _systemName;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter& CtiTelegyrControlCenter::setSysName( string sysIn )
{
   _systemName = sysIn;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

int CtiTelegyrControlCenter::getAccess()
{
   return _access;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter& CtiTelegyrControlCenter::setAccess( int accessIn )
{
   _access = accessIn;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

string CtiTelegyrControlCenter::getGroupValid( void ) const
{
   return _groupValid;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter& CtiTelegyrControlCenter::setGroupValid( string validIn )
{
   _groupValid = validIn;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

string CtiTelegyrControlCenter::getSysType( void ) const
{
   return _systemType;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter& CtiTelegyrControlCenter::setSysType( string typeIn )
{
   _systemType = typeIn;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

vector< CtiTelegyrGroup > CtiTelegyrControlCenter::getTelegyrGroupList( void ) const
{
   return _telegyrGroupList;
}

//=================================================================================================================================
//=================================================================================================================================

vector< CtiTelegyrGroup > & CtiTelegyrControlCenter::getTelegyrGroupList( void )
{
   return _telegyrGroupList;
}

//=================================================================================================================================
//=================================================================================================================================

void CtiTelegyrControlCenter::addToGroupList( CtiTelegyrGroup aGroup )
{
   _telegyrGroupList.push_back( aGroup );
}

//=================================================================================================================================
//here we need to delete our vector of fdrpoints, then delete the vector of groups that they were encased in
//=================================================================================================================================

void CtiTelegyrControlCenter::deleteTelegyrGroupList( void )
{
   if( !_telegyrGroupList.empty() )
   {
       _telegyrGroupList.erase( _telegyrGroupList.begin(), _telegyrGroupList.end() );
   }
}

//=================================================================================================================================
//=================================================================================================================================

CtiMutex& CtiTelegyrControlCenter::getMutex ()
{
    return _mux;
}

