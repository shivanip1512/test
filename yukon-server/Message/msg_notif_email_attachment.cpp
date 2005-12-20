#include "yukon.h"


/*-----------------------------------------------------------------------------*
*
* File:   msg_notif_email_attachment
*
* Date:   1/22/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/12/20 17:18:53 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <fstream>
#include "msg_notif_email_attachment.h"

using namespace std;

RWDEFINE_COLLECTABLE( CtiNotifEmailAttachmentMsg, NOTIF_EMAIL_ATTCH_MSG_ID );

//=====================================================================================================================
//=====================================================================================================================

CtiNotifEmailAttachmentMsg::CtiNotifEmailAttachmentMsg( string fileName )
{
   setFileName( fileName );
}

//=====================================================================================================================
//=====================================================================================================================

CtiNotifEmailAttachmentMsg::CtiNotifEmailAttachmentMsg()
{
}

//=====================================================================================================================
//=====================================================================================================================

CtiNotifEmailAttachmentMsg::~CtiNotifEmailAttachmentMsg()
{ 

}

//=====================================================================================================================
//=====================================================================================================================

void CtiNotifEmailAttachmentMsg::saveGuts( RWvostream &aStream ) const
{
   int         len = 0;
   char        data[1024];
   const int   dataLen = sizeof( data );

   CtiMessage::saveGuts( aStream );

   aStream << getFileName()
            << len;

   std::ifstream fileAttachment( getFileName().c_str() );

   fileAttachment.seekg( 0, ios::end );
   len = fileAttachment.tellg();
   fileAttachment.seekg( 0, ios::beg );

   for( int index = 0; index < len; index += dataLen )
   {
      int inCount = ( ( len - index ) > dataLen ) ? dataLen : ( len - index );

      fileAttachment.read( data, inCount );

      for( int outCount = 0; outCount < inCount; outCount++ )
      {
         aStream << data[outCount];
      }
   }

   fileAttachment.close();
}
   
//=====================================================================================================================
//=====================================================================================================================

void CtiNotifEmailAttachmentMsg::restoreGuts( RWvistream& aStream )
{
   int         len;

   CtiMessage::restoreGuts( aStream );

   aStream >> _fileName
            >> len;

   char  *data = new char[len];

   for( int index = 0; index < len; index++ )
   {
      aStream >> data[index];
   }
      
   std::ofstream fileAttachment( _fileName.c_str() );

   fileAttachment.write( data, len );
   
   delete [] data;
   
   fileAttachment.close();
}

//=====================================================================================================================
//=====================================================================================================================

void CtiNotifEmailAttachmentMsg::dump() const
{
   CtiLockGuard<CtiLogger> doubt_guard(dout);

   dout << " ------- Message --------- " << isA() << endl;
   dout << " Attachment name:          " << getFileName() << endl;
}

//=====================================================================================================================
//=====================================================================================================================

const string &CtiNotifEmailAttachmentMsg::getFileName( void ) const
{
   return( _fileName );
}

//=====================================================================================================================
//=====================================================================================================================

void CtiNotifEmailAttachmentMsg::setFileName( string name )
{
   _fileName = name;
}

//=====================================================================================================================
// Return a new'ed copy of this message!
//=====================================================================================================================

CtiMessage* CtiNotifEmailAttachmentMsg::replicateMessage() const
{
   CtiNotifEmailAttachmentMsg *ret = CTIDBG_new CtiNotifEmailAttachmentMsg(*this);

   return( (CtiMessage*)ret );
}
   

