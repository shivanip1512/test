
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   telegyrcontrolcenter
*
* Date:   5/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/07/12 18:30:55 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <vector>

using namespace std;

#include "telegyrcontrolcenter.h"
#include "telegyrgroup.h"

CtiTelegyrControlCenter::CtiTelegyrControlCenter()
{
}

CtiTelegyrControlCenter::~CtiTelegyrControlCenter()
{
}

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

RWCString CtiTelegyrControlCenter::getOperator( void )
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

CtiTelegyrControlCenter& CtiTelegyrControlCenter::setOperator( RWCString opIn )
{
   _operatorName = opIn;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

RWCString CtiTelegyrControlCenter::getPassword( void ) const
{
   return _password;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter& CtiTelegyrControlCenter::setPassword( RWCString passIn )
{
   _password = passIn;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

RWCString CtiTelegyrControlCenter::getSysName( void ) const
{
   return _systemName;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter& CtiTelegyrControlCenter::setSysName( RWCString sysIn )
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

RWCString CtiTelegyrControlCenter::getGroupValid( void ) const
{
   return _groupValid;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter& CtiTelegyrControlCenter::setGroupValid( RWCString validIn )
{
   _groupValid = validIn;
   return *this;
}

//=================================================================================================================================
//=================================================================================================================================

RWCString CtiTelegyrControlCenter::getSysType( void ) const
{
   return _systemType;
}

//=================================================================================================================================
//=================================================================================================================================

CtiTelegyrControlCenter& CtiTelegyrControlCenter::setSysType( RWCString typeIn )
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



/*
      if( _telegyrGroupList.getPointList() )
         _telegyrGroupList.getPointList().erase();

      _telegyrGroupList.erase();
*/
   }
}
