/*-----------------------------------------------------------------------------*
*
* File:   stdexcepthdlr
*
* Class:
* Date:   9/20/2000
*
* Author: Corey G. Plender
*
* PVCS KEYWORDS:
* ARCHIVE      :  $Archive:   Z:/SOFTWAREARCHIVES/YUKON/common/INCLUDE/stdexcepthdlr.h-arc  $
* REVISION     :  $Revision: 1.4 $
* DATE         :  $Date: 2003/03/13 19:35:28 $
*
* Copyright (c) 1999, 2000 Cannon Technologies Inc. All rights reserved.
*-----------------------------------------------------------------------------*/
#ifndef __STDEXCEPTHDLR_H__
#define __STDEXCEPTHDLR_H__
#pragma warning( disable : 4786)


#include <stdio.h>
#include <windows.h>
#include <eh.h>

#include "dlldefs.h"

DLLEXPORT void trans_func( unsigned int, EXCEPTION_POINTERS* );

class SE_Exception
{
private:
    unsigned int nSE;
public:
    SE_Exception() {}
    SE_Exception( unsigned int n ) : nSE( n ) {}
    ~SE_Exception() {}
    unsigned int getException() { return nSE; }
};

#endif // #ifndef __STDEXCEPTHDLR_H__
