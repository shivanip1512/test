
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   verifier
*
* Date:   4/12/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/04/14 18:10:29 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include <windows.h>
#include <process.h>
#include <iostream>
#include <iomanip>
#include <set>
using namespace std;

#include <rw/regexp.h>
#include <rw\ctoken.h>
#include <rw\thr\mutex.h>

#include "verifier_thread.h"

//============================================================================================================
//============================================================================================================

CtiVerifierThread::CtiVerifierThread()
{
   _verifierThread = rwMakeThreadFunction( *this, &CtiVerifierThread::verifierThread );
}

//============================================================================================================
//============================================================================================================

CtiVerifierThread::CtiVerifierThread( const CtiVerifierThread& aRef )
{
   *this = aRef;
}

//============================================================================================================
//============================================================================================================

CtiVerifierThread::~CtiVerifierThread()
{
}

//============================================================================================================
//we want to check to see if there are messages in our work and report vectors that we need to compare
//the work vector will be filled as messages are sent out
//the report vector will be filled by the protocol for the reporting device (RTM for GRE)
//if the message(s) match in some way, it's removed from the vectors
//if the message exists in the work, but not the report vector, we'll resend
//if the message exists in the report, but not the work vector, we'll log an error etc...
//============================================================================================================

void CtiVerifierThread::verifierThread( void )
{
   RWRunnableSelf          pSelf = rwRunnable();
   CtiVerficationWork      work;
   CtiVerificationReport   report;
   
   try
   {
      for( ;; )
      {
         if(( !_reportVector.empty() ) && ( !_workVector.empty() ))
         {
            for( int outer = 0; outer < _reportVector.size(); outer++ )
            {
               report = _reportVector[outer];

               for( int inner = 0; inner < _workVector.size(); inner++ )
               {
                  work = _workVector[inner];

                  compareMessages( report, work );
               }
            }
         }
         else
         {
            pSelf.sleep( 1000 );
         }
      }
   }
   catch( ... )
   {
       CtiLockGuard<CtiLogger> doubt_guard(dout);
       dout << RWTime() << " Verifier Thread Error!" << endl;
   }
}

//============================================================================================================
//here, we'll pull the data out of the out.OutMessage and compare it with the data that we got back from
//the dev_verifier that's stored in the in
//============================================================================================================

int CtiVerifierThread::compareMessages( CtiVerificationReport in, CtiVerficationWork out )
{
   int status = 0;     //this should be an enum or defined types of returns

//...................... add code ......................
// do the actual comparison
//...................... add code ......................
   
   return status;
}

//============================================================================================================
//============================================================================================================

vector<CtiVerficationWork> CtiVerifierThread::getWorkVector( void )
{
   return _workVector;
}
   
//============================================================================================================
//============================================================================================================

vector<CtiVerificationReport> CtiVerifierThread::getReportVector( void )
{
   return _reportVector;
}

