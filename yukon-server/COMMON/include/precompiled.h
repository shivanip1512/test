/*
 * Every source file should include this file.
 *
 * This header file exists to precompile large header files that never change. 
 * Add any headers here that you want precompiled.
 */
#ifndef __PRECOMPILED_H__
#define __PRECOMPILED_H__

#pragma warning( disable : 4786)

#include <windows.h>
#include <winsock.h>

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
#include <rw/rwdate.h>
#include <rw/rwtime.h>
#include <rw/cstring.h>
#include <rw/ctoken.h>
#include <rw/vstream.h>
#include <rw/tvhdict.h>
#include <rw/tpslist.h>
#include <rw/tvslist.h>

#include <rw/thr/monitor.h>
#include <rw/thr/recursiv.h>
#include <rw/thr/mutex.h>

#include <rw/db/db.h>

#endif
