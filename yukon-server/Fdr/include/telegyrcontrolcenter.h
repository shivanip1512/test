
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   telegyrcontrolcenter
*
* Class:  head
* Date:   5/20/2002
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/08/07 17:11:10 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#ifndef __TELEGYRCONTROLCENTER_H__
#define __TELEGYRCONTROLCENTER_H__

#include <rw/cstring.h>
#include <vector>

#include "dlldefs.h"
#include "fdr.h"
#include "telegyrgroup.h"
#include "mutex.h"

class IM_EX_FDRBASE  CtiTelegyrControlCenter
{

   public:

      CtiTelegyrControlCenter( void );
      virtual ~CtiTelegyrControlCenter();
      CtiTelegyrControlCenter& operator=( const CtiTelegyrControlCenter &other );

      int getChannelID( void );
      CtiTelegyrControlCenter &setChannelID( int idIn );

      RWCString getOperator( void );
//      CtiTelegyrControlCenter &getOperator( void );
      CtiTelegyrControlCenter &setOperator( RWCString opIn );

      RWCString getPassword( void ) const;
      //RWCString &getPassword( void );
      CtiTelegyrControlCenter &setPassword( RWCString passIn );

      RWCString getSysName( void ) const;
      //RWCString &getSysName( void );
      CtiTelegyrControlCenter &setSysName( RWCString sysIn );

      int getAccess();
      CtiTelegyrControlCenter &setAccess( int accessIn );

      RWCString getGroupValid( void ) const;
      //RWCString &getGroupValid( void );
      CtiTelegyrControlCenter &setGroupValid( RWCString validIn );

      RWCString getSysType( void ) const;
      //RWCString &getSysType( void );
      CtiTelegyrControlCenter &setSysType( RWCString typeIn );

      vector< CtiTelegyrGroup > getTelegyrGroupList( void ) const;
      vector< CtiTelegyrGroup > &getTelegyrGroupList( void );

      void deleteTelegyrGroupList( void );

      void addToGroupList( CtiTelegyrGroup newGroup );

      CtiMutex& getMutex( void );

   protected:


   private:

      //this is a vector of groups with a vector of pointnames for each
      vector< CtiTelegyrGroup >           _telegyrGroupList;
      int                                 _channelID;
      RWCString                           _operatorName;
      RWCString                           _password;
      RWCString                           _systemName;
      int                                 _access;
      RWCString                           _groupValid;
      RWCString                           _systemType;

      CtiMutex                            _mux;
};
#endif // #ifndef __TELEGYRCONTROLCENTERS_H__
