
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
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2004/04/12 19:26:16 $
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
   void setTransmitTime( RWTime aTime );
   RWTime getExpriationTime( void );
   void setExpirationTime( RWTime aTime );
   void decrementRetries( void );
   OUTMESS getOutMessage( void );
   void setOutMessage( OUTMESS sent );

private:

   int      _workPriority;    //we'll override the Outmess's priority
   RWTime   _txTime;
   RWTime   _expTime;         //at this time, the message won't be valid anymore and we won't send it
   OUTMESS  _outMessage;

protected:

};

#endif // #ifndef __VERIFICATION_WORK_H__
