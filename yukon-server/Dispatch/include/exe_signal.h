/*-----------------------------------------------------------------------------*
*
* File:   exe_signal
*
* Date:   3/24/2000
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/DISPATCH/INCLUDE/exe_signal.h-arc  $
* REVISION     :  $Revision: 1.1.1.1 $
* DATE         :  $Date: 2002/04/12 13:59:39 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#pragma warning( disable : 4786)
#ifndef __EXE_SIGNAL_H__
#define __EXE_SIGNAL_H__

#include "executor.h"

// Forward Declarations
class CtiServer;


class IM_EX_CTIVANGOGH CtiSignalExecutor : public CtiExecutor
{
protected:

private:

public:
   CtiSignalExecutor(CtiMessage *p = NULL) :
      CtiExecutor(p);

   CtiSignalExecutor(const CtiSignalExecutor& aRef);
   virtual ~CtiSignalExecutor();


   INT  ServerExecute(CtiServer *Svr);
};
#endif // #ifndef __EXE_SIGNAL_H__
