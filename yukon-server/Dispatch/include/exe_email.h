
#pragma warning( disable : 4786)
#ifndef __EXE_EMAIL_H__
#define __EXE_EMAIL_H__

/*-----------------------------------------------------------------------------*
*
* File:   exe_email
*
* Class:  CtiEmailExecutor
* Date:   4/9/2001
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/exe_email.h-arc  $
* REVISION     :  $Revision: 1.3 $
* DATE         :  $Date: 2002/04/16 15:58:28 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#include "executor.h"

// Forward Declarations
class CtiConnectionManager;
class CtiServer;


class IM_EX_CTIVANGOGH CtiEmailExecutor : public CtiExecutor
{
protected:

private:

public:
   CtiEmailExecutor(CtiMessage *p = NULL);// :CtiExecutor(p);
   virtual ~CtiEmailExecutor();

   INT  ServerExecute(CtiServer *Svr);

};

#endif // #ifndef __EXE_EMAIL_H__
