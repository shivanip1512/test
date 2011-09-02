#pragma once

#include "logger.h"
#include "dllbase.h"
#include "message.h"
#include "yukon.h"

class IM_EX_MSG CtiNotifEmailAttachmentMsg : public CtiMessage
{
private:

   std::string   _fileName;

public:

   RWDECLARE_COLLECTABLE( CtiNotifEmailAttachmentMsg );

   CtiNotifEmailAttachmentMsg( std::string fileName );
   CtiNotifEmailAttachmentMsg();
   virtual ~CtiNotifEmailAttachmentMsg();
   virtual void dump() const;
   void saveGuts( RWvostream &aStream ) const;
   void restoreGuts( RWvistream& aStream );
   const std::string &getFileName( void ) const;
   void setFileName( std::string name );
   int getLength( void );
   void setLength( int len );
   CtiMessage* replicateMessage() const;
};
