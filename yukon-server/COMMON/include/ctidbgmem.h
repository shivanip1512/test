#pragma once

#define _CRTDBG_MAP_ALLOC

#include <windows.h>
#include <crtdbg.h>

#ifdef BOUNDSCHECKER

     #define SET_CRT_OUTPUT_MODES
     #define ENABLE_CRT_SHUTDOWN_CHECK
     #define SET_CRT_DEBUG_FIELD(a)   ((void) 0)
     #define CLEAR_CRT_DEBUG_FIELD(a) ((void) 0)
     #define CTIDBG_new new

#else

#ifdef   _DEBUG
 #define SET_CRT_OUTPUT_MODES \
           _CrtSetReportMode( _CRT_WARN, _CRTDBG_MODE_FILE ); \
           _CrtSetReportFile( _CRT_WARN, _CRTDBG_FILE_STDOUT ); \
           _CrtSetReportMode( _CRT_ERROR, _CRTDBG_MODE_FILE ); \
           _CrtSetReportFile( _CRT_ERROR, _CRTDBG_FILE_STDOUT ); \
           _CrtSetReportMode( _CRT_ASSERT, _CRTDBG_MODE_FILE ); \
           _CrtSetReportFile( _CRT_ASSERT, _CRTDBG_FILE_STDOUT );
 #define ENABLE_CRT_SHUTDOWN_CHECK \
           _CrtSetDbgFlag((_CRTDBG_LEAK_CHECK_DF) | _CrtSetDbgFlag(_CRTDBG_REPORT_FLAG));
 #define SET_CRT_DEBUG_FIELD(a) \
            _CrtSetDbgFlag((a) | _CrtSetDbgFlag(_CRTDBG_REPORT_FLAG))
 #define CLEAR_CRT_DEBUG_FIELD(a) \
            _CrtSetDbgFlag(~(a) & _CrtSetDbgFlag(_CRTDBG_REPORT_FLAG))

 #define CTIDBG_new new(_NORMAL_BLOCK, __FILE__, __LINE__)

#else

 #define SET_CRT_OUTPUT_MODES
 #define ENABLE_CRT_SHUTDOWN_CHECK
 #define SET_CRT_DEBUG_FIELD(a)   ((void) 0)
 #define CLEAR_CRT_DEBUG_FIELD(a) ((void) 0)
 #define CTIDBG_new new

#endif
#endif

/*
* This set of macros replaces the default C++ new and new[] methods with
* a version that calls the debug malloc function.  This allows tools like
* _CrtDumpMemoryLeaks() to report the source of a memory block.
*
* To use this, you need to put the line:
*   EAS_DBG_NEW
* in the public area of a class/structure.
*/
#if defined(_DEBUG)
#define EAS_DBG_NEW \
static void* operator new( std::size_t sz )                       \
{                                                                 \
    return _malloc_dbg( sz, _NORMAL_BLOCK, __FILE__, __LINE__ );  \
}                                                                 \
static void* operator new[]( std::size_t sz )                     \
{                                                                 \
    return _malloc_dbg( sz, _NORMAL_BLOCK, __FILE__, __LINE__ );  \
}                                                                  
#else
#define EAS_DBG_NEW 
#endif

/*
 *  Macro to add code that reports heap size.  
 */
#if defined(_DEBUG)
#define EAS_DBG_CHECK \
    static _CrtMemState s1; \
    _CrtMemCheckpoint( &s1 ); \
    CTILOG_INFO( dout, "Heap: size " << s1.lSizes[1] << ", count " << s1.lCounts[1] ); 
#endif
