
#pragma warning( disable : 4786)
#ifndef __VERIFICATION_H__
#define __VERIFICATION_H__

/*---------------------------------------------------------------------------------*
*
* File:   verification
*
* Class:  
* Date:   4/9/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/04/14 18:12:05 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#include <rw/cstring.h>

#include "dsm2.h"

class CtiVerification
{
public:

   enum
   {
      Report   = 0,
      Work
   } type;

   CtiVerification();
   CtiVerification( const CtiVerification& aRef );
   virtual ~CtiVerification();

   int getType( void );
   void setType( int aType );

private:

protected:

   int   _type;

};
#endif // #ifndef __VERIFICATION_H__
