#pragma once

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
