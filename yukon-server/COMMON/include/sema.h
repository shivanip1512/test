
#pragma warning( disable : 4786)
#ifndef __SEMA_H__
#define __SEMA_H__

/*-----------------------------------------------------------------------------*
*
* File:   sema
*
* Class:  CtiSemaphore
* Date:   5/1/2002
*
* Author: Corey G. Plender
*
* CVS KEYWORDS:
* REVISION     :  $Revision: 1.1 $
* DATE         :  $Date: 2002/05/02 17:03:11 $
*
* Copyright (c) 2002 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#include <windows.h>
#include <assert.h>
#include "dlldefs.h"

class IM_EX_CTIBASE CtiSemaphore
{
protected:

    private:

    HANDLE hSema;
#ifdef DEBUG
    DWORD  _threadID;
#endif

public:

    CtiSemaphore(long init = 1, long max = 1);
    virtual ~CtiSemaphore();

    bool acquire();
    bool acquire(unsigned long millis);
    void release();
};
#endif // #ifndef __SEMA_H__
