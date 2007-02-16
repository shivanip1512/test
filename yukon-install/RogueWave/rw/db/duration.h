#ifndef __RWDB_DURATION_H__
#define __RWDB_DURATION_H__

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
 * Definition of class RWDBDuration
 *
 * This class provides interfaces to manipulate a duration of time,
 * i.e. one week, three hours, five years, etc.
 *
 **************************************************************************/


#include <rw/db/defs.h>

#include <rw/cstring.h>


#define RWDB_MILLISECONDS_PER_SEC    ((double)1000.0)
#define RWDB_SECONDS_PER_MIN         ((double)60.0)
#define RWDB_SECONDS_PER_HR          (RWDB_SECONDS_PER_MIN*60.0)
#define RWDB_SECONDS_PER_DAY         (RWDB_SECONDS_PER_HR*24.0)
#define RWDB_SECONDS_PER_WEEK        (RWDB_SECONDS_PER_DAY*7.0)
#define RWDB_SECONDS_PER_RWMTH       (RWDB_SECONDS_PER_WEEK*4.0)
#define RWDB_SECONDS_PER_RWYR        (RWDB_SECONDS_PER_RWMTH*12.0)

class RWDBExport RWDBDuration {
public:
    enum DurationType {
        milliseconds,
        seconds,
        minutes,
        hours,
        days,
        weeks,
        months,
        years
    };

// constructors
    RWDBDuration();
    RWDBDuration(int yr, int mth = 0, int day = 0, int hr = 0,
                 int min = 0, int sec = 0, int msec = 0);
    RWDBDuration(const RWDBDuration& d);

// public member functions
    double          asRWYears() const;
    double          asRWMonths() const;
    double          asWeeks() const;
    double          asDays() const;
    double          asHours() const;
    double          asMinutes() const;
    double          asSeconds() const;
    double          asMilliseconds() const;

    void            addRWYears(int years);
    void            addRWMonths(int months);
    void            addWeeks(int weeks);
    void            addDays(int days); 
    void            addHours(int hours);
    void            addMinutes(int minutes);
    void            addSeconds(int seconds);
    void            addMilliseconds(int milliseconds);
 
    RWCString       asString() const;

// RWCollectable support
    RWspace         binaryStoreSize() const;
    int             compareTo(const RWDBDuration* d) const;
    RWBoolean       isEqual(const RWDBDuration* d) const;
    unsigned        hash() const;
    void            saveOn(RWFile& s) const;
    void            saveOn(RWvostream& s) const;
    void            restoreFrom(RWFile& s);
    void            restoreFrom(RWvistream& s);

// public member operators
    RWDBDuration    operator-();
    RWDBDuration&   operator=(const RWDBDuration& dur);
    RWDBDuration&   operator+=(const RWDBDuration& dur);
    RWDBDuration&   operator-=(const RWDBDuration& dur);
    RWDBDuration&   operator+=(double seconds);
    RWDBDuration&   operator-=(double seconds);
    RWDBDuration&   operator*=(double factor);
    RWDBDuration&   operator/=(double divisor);

// related operators
friend  RWBoolean rwdbexport     operator==(const RWDBDuration& a,
                                            const RWDBDuration& b);
friend  RWBoolean rwdbexport     operator!=(const RWDBDuration& a,
                                            const RWDBDuration& b);
friend  RWBoolean rwdbexport     operator>(const RWDBDuration& a,
                                            const RWDBDuration& b);
friend  RWBoolean rwdbexport     operator>=(const RWDBDuration& a,
                                            const RWDBDuration& b);
friend  RWBoolean rwdbexport     operator<(const RWDBDuration& a,
                                            const RWDBDuration& b);
friend  RWBoolean rwdbexport     operator<=(const RWDBDuration& a,
                                            const RWDBDuration& b);
friend  RWDBDuration rwdbexport  operator+(RWDBDuration& a,
                                            const RWDBDuration& b);
friend  RWDBDuration rwdbexport  operator-(RWDBDuration& a,
                                            const RWDBDuration& b);
friend  RWDBDuration rwdbexport  operator+(RWDBDuration& dur, double seconds);
friend  RWDBDuration rwdbexport  operator-(RWDBDuration& dur, double seconds);
friend  RWDBDuration rwdbexport  operator*(RWDBDuration& dur, double factor);
friend  RWDBDuration rwdbexport  operator/(RWDBDuration& dur, double divisor);
friend  RWDBDuration rwdbexport  operator-(const RWDBDateTime& a,
                                           const RWDBDateTime& b );

private:
    double                       seconds_;   // duration represented in seconds
    RWDBDuration                 (double seconds);
};

/* Global declarations for Related Global operators  */

RWBoolean rwdbexport     operator==(const RWDBDuration& a,
                                            const RWDBDuration& b);
RWBoolean rwdbexport     operator!=(const RWDBDuration& a,
                                            const RWDBDuration& b);
RWBoolean rwdbexport     operator>(const RWDBDuration& a,
                                            const RWDBDuration& b);
RWBoolean rwdbexport     operator>=(const RWDBDuration& a,
                                            const RWDBDuration& b);
RWBoolean rwdbexport     operator<(const RWDBDuration& a,
                                            const RWDBDuration& b);
RWBoolean rwdbexport     operator<=(const RWDBDuration& a,
                                            const RWDBDuration& b);
RWDBDuration rwdbexport  operator+(RWDBDuration& a,
                                            const RWDBDuration& b);
RWDBDuration rwdbexport  operator-(RWDBDuration& a,
                                            const RWDBDuration& b);
RWDBDuration rwdbexport  operator+(RWDBDuration& dur, double seconds);
RWDBDuration rwdbexport  operator-(RWDBDuration& dur, double seconds);
RWDBDuration rwdbexport  operator*(RWDBDuration& dur, double factor);
RWDBDuration rwdbexport  operator/(RWDBDuration& dur, double divisor);
RWDBDuration rwdbexport  operator-(const RWDBDateTime& a,
                                           const RWDBDateTime& b );

#endif
