
#pragma warning( disable : 4786)
#ifndef __VERIFICATION_WORK_H__
#define __VERIFICATION_WORK_H__

/*---------------------------------------------------------------------------------*
*
* File:   verification_work
*
* Class:  
* Date:   4/9/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/04/12 17:14:35 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#include <rw/rwtime.h>

#include "verification.h"

class CtiVerficationWork : public CtiVerification
{

public:
   CtiVerficationWork();
   CtiVerficationWork( const CtiVerficationWork& aRef );
   ~CtiVerficationWork();

   int getWorkPriority( void );
   void setWorkPriority( int aPri );
   RWTime getTransmitTime( void );
   void setTransmitTime( RWTime );
   RWTime getExpriationTime( void );
   void setExpirationTime( RWTime );

private:

   int      _workPriority;    //we'll override the Outmess's priority
   RWTime   _txTime;
   RWTime   _expTime;         //at this time, the message won't be valid anymore and we won't send it
   OUTMESS  *_OutMessage;

protected:

};

#endif // #ifndef __VERIFICATION_WORK_H__
