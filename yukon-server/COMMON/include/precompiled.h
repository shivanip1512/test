/*
 * Every source file should include this file.
 *
 * This header file exists to precompile large header files that never change.
 * Add any headers here that you want precompiled.
 */
#ifndef __PRECOMPILED_H__
#define __PRECOMPILED_H__

#pragma warning( disable : 4786)

#if !defined(_WIN32_WINNT)
#define _WIN32_WINNT 0x0500
#endif 

#define WIN32_LEAN_AND_MEAN 

#if !defined (NOMINMAX)
#define NOMINMAX
#endif


#if !defined (NOMINMAX)
#define NOMINMAX
#endif

#include <windows.h>

#include <stdlib.h>
#include <stdio.h>

#include <algorithm>
#include <iomanip>
#include <functional>

#include <iostream>
#include <string>
#include <vector>
#include <map>
#include <set>

#include <rw/collect.h>
#include <rw/ctoken.h>
#include <rw/vstream.h>
#include <rw/tvhdict.h>
#include <rw/tpslist.h>
#include <rw/tvslist.h>

#include <rw/thr/monitor.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/mutex.h>

#include <rw/db/db.h>

#include "boost/tokenizer.hpp"
#include "boost/shared_ptr.hpp"

#endif
