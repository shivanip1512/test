#include "yukon.h"

#include <windows.h>
#include <iostream>
#include <eh.h>

using namespace std;

#include "stdexcepthdlr.h"

void trans_func( unsigned int u, EXCEPTION_POINTERS* pExp )
{
    // printf( "In trans_func. %u\n", u );
    throw SE_Exception( u );
}

