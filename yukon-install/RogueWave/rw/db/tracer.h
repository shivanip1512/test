#ifndef __RWDB_TRACER_H__
#define __RWDB_TRACER_H__

/**************************************************************************
 *
 * $Id$
 *
 ***************************************************************************
 *
 * Copyright (c) 1994-1999 Rogue Wave Software, Inc.  All Rights Reserved.
 *
 * This computer software is owned by Rogue Wave Software, Inc. and is
 * protected by U.S. copyright laws and other laws and by international
 * treaties.  This computer software is furnished by Rogue Wave Software,
 * Inc. pursuant to a written license agreement and may be used, copied,
 * transmitted, and stored only in accordance with the terms of such
 * license and with the inclusion of the above copyright notice.  This
 * computer software or any other copies thereof may not be provided or
 * otherwise made available to any other person.
 *
 * U.S. Government Restricted Rights.  This computer software is provided
 * with Restricted Rights.  Use, duplication, or disclosure by the
 * Government is subject to restrictions as set forth in subparagraph (c)
 * (1) (ii) of The Rights in Technical Data and Computer Software clause
 * at DFARS 252.227-7013 or subparagraphs (c) (1) and (2) of the
 * Commercial Computer Software – Restricted Rights at 48 CFR 52.227-19,
 * as applicable.  Manufacturer is Rogue Wave Software, Inc., 5500
 * Flatiron Parkway, Boulder, Colorado 80301 USA.
 *
 **************************************************************************
 *
 * Declaration  of class RWDBTracer
 *
 **************************************************************************/

#include <rw/rstream.h>
#include <rw/db/defs.h>
#include <rw/db/dbmutex.h>

class RWDBExport RWDBTracer
{
public:
    enum TraceFlag { ZERO = 0, SQL = 1, XXX = 2, YYY = 4, ZZZ = 8 /*, ... */ };

    RWDBTracer();
    RWDBTracer(const RWDBTracer& aTrace);
    ~RWDBTracer();
    RWDBTracer&  operator=(const RWDBTracer& aTrace);

    RWDBTracer&  stream(RW_SL_IO_STD(ostream&) str);
    RW_SL_IO_STD(ostream&)   stream();

    unsigned     setOn(TraceFlag aFlag);
    RWBoolean    isOn(TraceFlag aFlag);
    unsigned     flag();
    unsigned     setOff( TraceFlag aFlag );

      // multi-threading support
    void         acquire() const;
    void         release() const;

    RWDBTracer&  operator<< (         char arg);
#ifndef RW_NO_SCHAR
# ifndef RW_NO_OVERLOAD_SCHAR
    RWDBTracer&  operator<< (  signed char arg);
# endif
#endif
    RWDBTracer&  operator<< (unsigned char arg);
    RWDBTracer&  operator<< (short arg);
    RWDBTracer&  operator<< (unsigned short arg);
    RWDBTracer&  operator<< (int arg);
    RWDBTracer&  operator<< (unsigned int arg);
    RWDBTracer&  operator<< (long arg);
    RWDBTracer&  operator<< (unsigned long arg);
    RWDBTracer&  operator<< (float arg);
    RWDBTracer&  operator<< (double arg);
    RWDBTracer&  operator<< (const          char *str);

//      The following two aren't supported by all iostream.h:
#ifndef RW_NO_SCHAR
# ifndef RW_NO_OVERLOAD_SCHAR
    RWDBTracer&  operator<< (const   signed char *arg);
# endif
#endif
    RWDBTracer&  operator<< (const unsigned char * arg);

    RWDBTracer&  operator<< (void  * arg);
    RWDBTracer&  operator<< (RW_SL_IO_STD(ostream&) ( *_f)(RW_SL_IO_STD(ostream&) ));
    RWDBTracer&  operator<< (RW_SL_IO_STD(ios&) ( *_f)(RW_SL_IO_STD(ios&)));
    RWBoolean    isValid();

private:
    RW_SL_IO_STD(ostream*)     impl_;
    unsigned     traceFlag_;
    RWDBMutex    mutex_;

};

#endif
