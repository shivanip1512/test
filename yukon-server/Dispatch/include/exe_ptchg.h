#ifndef __EXE_PTCHG_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   exe_ptchg
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/exe_ptchg.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:18:52 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __EXE_PTCHG_H__

#include "executor.h"

// Forward Declarations
class CtiConnectionManager;
class CtiServer;


class IM_EX_CTIVANGOGH CtiPointChangeExecutor : public CtiExecutor
{
private:

public:

   CtiPointChangeExecutor(CtiMessage *p = NULL);// :CtiExecutor(p);
   virtual ~CtiPointChangeExecutor();
   INT  ServerExecute(CtiServer *Svr);
};

#endif //#ifndef __EXE_PTCHG_H__


