
#pragma warning( disable : 4786)
#ifndef __TELEGYRCONTROLCENTER_H__
#define __TELEGYRCONTROLCENTER_H__

#include <rw/cstring.h>
#include <vector>

#include "dlldefs.h"
#include "fdr.h"
#include "telegyrgroup.h"
#include "mutex.h"

class IM_EX_FDRTELEGYRAPI CtiTelegyrControlCenter
{

   public:

      CtiTelegyrControlCenter( void );
      virtual ~CtiTelegyrControlCenter();
      CtiTelegyrControlCenter& operator=( const CtiTelegyrControlCenter &other );

      int getChannelID( void );
      CtiTelegyrControlCenter &setChannelID( int idIn );

      RWCString getOperator( void );
      CtiTelegyrControlCenter &setOperator( RWCString opIn );

      RWCString getPassword( void ) const;
      CtiTelegyrControlCenter &setPassword( RWCString passIn );

      RWCString getSysName( void ) const;
      CtiTelegyrControlCenter &setSysName( RWCString sysIn );

      int getAccess();
      CtiTelegyrControlCenter &setAccess( int accessIn );

      RWCString getGroupValid( void ) const;
      CtiTelegyrControlCenter &setGroupValid( RWCString validIn );

      RWCString getSysType( void ) const;
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
      int                                 _access;

      RWCString                           _operatorName;
      RWCString                           _password;
      RWCString                           _systemName;
      RWCString                           _groupValid;
      RWCString                           _systemType;

      CtiMutex                            _mux;
};
#endif // #ifndef __TELEGYRCONTROLCENTERS_H__
