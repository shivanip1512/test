
#pragma warning( disable : 4786)
#ifndef __VERIFIER_THREAD_H__
#define __VERIFIER_THREAD_H__

/*---------------------------------------------------------------------------------*
*
* File:   verifier_thread
*
* Class:  CtiVerifierThread
* Date:   4/14/2004
*
* Author: Eric Schmit
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive$
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2004/04/14 18:10:29 $
*
* Copyright (c) 1999, 2000, 2001, 2002 Cannon Technologies Inc. All rights reserved.
*----------------------------------------------------------------------------------*/

#include <vector>
#include <rw/rwtime.h>
#include <rw\thr\mutex.h>

#include "cticalls.h"
#include "ctitypes.h"
#include "cparms.h"
#include "dsm2.h"
#include "connection.h"
#include "mgr_device.h"
#include "mgr_port.h"
#include "msg_cmd.h"
#include "os2_2w32.h"
#include "pilserver.h"
#include "port_base.h"
#include "portglob.h"
#include "verification_work.h"
#include "verification_report.h"

class CtiVerifierThread //: public CtiDBMemObject
{

protected:

private:
   
   vector<CtiVerficationWork>    _workVector;
   vector<CtiVerificationReport> _reportVector;
   RWThreadFunction              _verifierThread;

   int compareMessages( CtiVerificationReport in, CtiVerficationWork out );
   void verifierThread( void );

public:

   CtiVerifierThread();
   CtiVerifierThread( const CtiVerifierThread& aRef );
   virtual ~CtiVerifierThread();
//   CtiVerifierThread& operator=( const CtiVerifierThread& aRef );

   vector<CtiVerficationWork> getWorkVector( void );
   vector<CtiVerificationReport> getReportVector( void );

};
#endif // #ifndef __VERIFIER_THREAD_H__
