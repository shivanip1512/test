#ifndef __EXE_REG_H__
#pragma warning( disable : 4786)

/*-----------------------------------------------------------------------------*
*
* File:   exe_reg
*
* Date:   7/18/2001
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/SERVER/INCLUDE/exe_reg.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2002/06/10 22:29:24 $
*
* Copyright (c) 1999, 2000, 2001 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/

#define __EXE_REG_H__

// Forward Declarations
class CtiConnectionManager;

#include "executor.h"

class IM_EX_CTISVR CtiRegistrationExecutor : public CtiExecutor
{
private:

public:

   CtiRegistrationExecutor(CtiMessage *p = NULL);
   ~CtiRegistrationExecutor();
   INT  ServerExecute(CtiServer *);
};

#endif //#ifndef __EXE_REG_H__


