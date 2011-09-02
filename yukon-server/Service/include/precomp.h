#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>
#include <process.h>

#include <iostream>

#ifdef UNICODE
    #define _tcout wcout
    #define _tostream wostream
#else
    #define _tcout std::cout
    #define _tostream std::ostream
#endif

#include <TCHAR.h>
#include "Monitor.h"
