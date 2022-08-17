/*
 * Every source (.cpp) file should include this file, with the exception of unit tests.
 *
 * This header file exists to precompile large header files that never change,
 *   which are usually third-party library includes such as Boost.
 *
 */
#pragma once

#define _WIN32_WINNT 0x0501  //  Windows XP, Server 2003.  No service packs.

#define WIN32_LEAN_AND_MEAN

#include <winsock2.h>
#include <ws2tcpip.h>
#include <windows.h>

#include <wtypes.h>

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

//  for boost/bimap.hpp
#define BOOST_MULTI_INDEX_DISABLE_SERIALIZATION

#include <boost/assert.hpp>
#include <boost/assign/ptr_map_inserter.hpp>
#include <boost/bimap.hpp>
#include <boost/bind.hpp>
#include <boost/config.hpp>
#include <boost/crc.hpp>
#include <boost/mem_fn.hpp>
#include <boost/noncopyable.hpp>
#include <boost/ptr_container/ptr_map.hpp>
#include <boost/ptr_container/ptr_vector.hpp>
#include <boost/scoped_array.hpp>
#include <boost/scoped_ptr.hpp>
#include <boost/shared_ptr.hpp>
#include <boost/tokenizer.hpp>
#include <boost/utility.hpp>
#include <boost/weak_ptr.hpp>

//  The Boost libraries listed below all attempt to auto-link with their libraries.
//    This does not work for the Service/ directory, and we probably don't want to try to link with them by default anyway.

//#include <boost/date_time/gregorian/greg_date.hpp>
//#include <boost/date_time/gregorian/gregorian.hpp>
//#include <boost/date_time/microsec_time_clock.hpp>
//#include <boost/date_time/posix_time/posix_time.hpp>
//#include <boost/date_time/posix_time/posix_time_types.hpp>

//#include <boost/regex.hpp>

//#include <boost/test/auto_unit_test.hpp>
//#include <boost/test/floating_point_comparison.hpp>
//#include <boost/test/unit_test.hpp>

//#include <boost/thread/condition.hpp>
//#include <boost/thread/mutex.hpp>
//#include <boost/thread/shared_mutex.hpp>
//#include <boost/thread/thread.hpp>
//#include <boost/thread/tss.hpp>
//#include <boost/thread/xtime.hpp>

