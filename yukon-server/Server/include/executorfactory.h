#ifndef __EXECUTORFACTORY_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   executorfactory
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/INCLUDE/executorfactory.h-arc  $
* REVISION     :  $Revision: 1.2 $
* DATE         :  $Date: 2002/04/15 15:20:04 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __EXECUTORFACTORY_H__

#include "collectable.h"
#include "message.h"

#include "executor.h"
#include "exe_cmd.h"
#include "exe_reg.h"
// #include "exe_ptchg.h"

class CtiConnectionManager;

class IM_EX_CTISVR CtiExecutorFactory
{
private:
   // Currently no data members in this class.

public:
   CtiExecutorFactory();
   ~CtiExecutorFactory();
   virtual CtiExecutor *getExecutor(CtiMessage*);
};
#endif // #ifndef __EXECUTORFACTORY_H__





