#ifndef __VGEXECUTORFACTORY_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   vgexe_factory
*
* Date:   7/16/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/vgexe_factory.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2005/07/19 22:48:53 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __VGEXECUTORFACTORY_H__

#include "collectable.h"
#include "message.h"

#include "executor.h"
#include "exe_ptchg.h"
#include "executorfactory.h"

class CtiConnectionManager;

class IM_EX_CTIVANGOGH CtiVanGoghExecutorFactory : public CtiExecutorFactory
{
private:
   // Currently no data members in this class.

public:
   typedef CtiExecutorFactory Inherited;

   CtiVanGoghExecutorFactory();
   ~CtiVanGoghExecutorFactory();
   virtual CtiExecutor* getExecutor(CtiMessage*);
};
#endif // #ifndef __VGEXECUTORFACTORY_H__





