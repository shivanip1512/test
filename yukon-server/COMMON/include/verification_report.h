
#pragma warning( disable : 4786)
#ifndef __VERIFICATION_REPORT_H__
#define __VERIFICATION_REPORT_H__

/*---------------------------------------------------------------------------------*
*
* File:   verification_report
*
* Class:  
* Date:   4/9/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2004/04/14 18:12:05 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/
#include <rw/rwtime.h>

#include "verification.h"

class IM_EX_CTIBASE CtiVerificationReport : public CtiVerification
{
public:

   CtiVerificationReport();
   CtiVerificationReport( const CtiVerificationReport& aRef );
   ~CtiVerificationReport();

   RWCString getData( void );
   void setData( RWCString input );
   RWTime getTransmitTime( void );        //will this..
   void setTransmitTime( RWTime aTime );  //.. and this confilict with verification_work ???

private:

   RWCString   _receivedData;
   RWTime      _txTime;                   //if the device knows, fine, if not, we'll use now()

protected:

};

#endif // #ifndef __VERIFICATION_REPORT_H__
