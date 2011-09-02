#pragma once

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

      std::string getOperator( void );
      CtiTelegyrControlCenter &setOperator( std::string opIn );

      std::string getPassword( void ) const;
      CtiTelegyrControlCenter &setPassword( std::string passIn );

      std::string getSysName( void ) const;
      CtiTelegyrControlCenter &setSysName( std::string sysIn );

      int getAccess();
      CtiTelegyrControlCenter &setAccess( int accessIn );

      std::string getGroupValid( void ) const;
      CtiTelegyrControlCenter &setGroupValid( std::string validIn );

      std::string getSysType( void ) const;
      CtiTelegyrControlCenter &setSysType( std::string typeIn );

      std::vector< CtiTelegyrGroup > getTelegyrGroupList( void ) const;
      std::vector< CtiTelegyrGroup > &getTelegyrGroupList( void );

      void deleteTelegyrGroupList( void );

      void addToGroupList( CtiTelegyrGroup newGroup );

      CtiMutex& getMutex( void );

   protected:


   private:

      //this is a vector of groups with a vector of pointnames for each
      std::vector< CtiTelegyrGroup >           _telegyrGroupList;

      int                                 _channelID;
      int                                 _access;

      std::string                           _operatorName;
      std::string                           _password;
      std::string                           _systemName;
      std::string                           _groupValid;
      std::string                           _systemType;

      CtiMutex                            _mux;
};
