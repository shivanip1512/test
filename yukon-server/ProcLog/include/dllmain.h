#ifndef __DLLMAIN_HPP__
#define __DLLMAIN_HPP__

#include "ctinexus.h"
#include "errclient.h"
#include "logger.h"

extern int        Initialized;   // Shared proclogger.
extern CErrClient *ErrClient;    // Global Client Member
extern INT   ErrorThreadCount;

#endif                           // #ifndef __DLLMAIN_HPP__

