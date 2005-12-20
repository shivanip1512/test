
#pragma warning( disable : 4786)
#ifndef __TELEGYRCONTROLCENTER_H__
#define __TELEGYRCONTROLCENTER_H__

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

      string getOperator( void );
      CtiTelegyrControlCenter &setOperator( string opIn );

      string getPassword( void ) const;
      CtiTelegyrControlCenter &setPassword( string passIn );

      string getSysName( void ) const;
      CtiTelegyrControlCenter &setSysName( string sysIn );

      int getAccess();
      CtiTelegyrControlCenter &setAccess( int accessIn );

      string getGroupValid( void ) const;
      CtiTelegyrControlCenter &setGroupValid( string validIn );

      string getSysType( void ) const;
      CtiTelegyrControlCenter &setSysType( string typeIn );

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

      string                           _operatorName;
      string                           _password;
      string                           _systemName;
      string                           _groupValid;
      string                           _systemType;

      CtiMutex                            _mux;
};
#endif // #ifndef __TELEGYRCONTROLCENTERS_H__
