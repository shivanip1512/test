#ifndef __RWDB_DATETIME_H__
#define __RWDB_DATETIME_H__

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
 * Definition of class RWDBDateTime
 *
 * This class provides interfaces to manipulate a date coupled with time,
 * as found in most databases.
 *
 **************************************************************************/



#include <rw/db/defs.h>
#include <rw/db/dbref.h>

#include <rw/locale.h>
#include <rw/rwtime.h>
#include <rw/rwdate.h>
STARTWRAP
#include <time.h>                /* System time management. */
ENDWRAP


class RWDBExport RWDBDateTimeImp : public RWDBReference {
 
  public:
 
    RWDBDateTimeImp();
    RWDBDateTimeImp(const RWDate& date);
 
  protected:
 
  private:
 
  // define the date
    RWDate  date_;
  // define the time in milliseconds of the day
    long    milli_;         // millisecond within the day; 0-86,400,000
 
  // used to cache asString results
    RWCString  asString_;
    RWCString  format_;
    RWBoolean  changed_;
 
    friend class RWDBExport RWDBDateTime;
 
};


class RWDBExport RWDBDateTime
{

  public:

// constructors
    RWDBDateTime();
    RWDBDateTime(const RWDBDateTime& dt);
   
    RWDBDateTime(const struct tm* tmbuf,
                 const RWZone& zone = RWZone::local());
    RWDBDateTime(const RWTime& rwtime,
                 const RWZone& zone = RWZone::local());
    RWDBDateTime(const RWDate& rwdate,
                 unsigned hour = 0, unsigned minute = 0,
                 unsigned second = 0, unsigned millesecond = 0,
                 const RWZone& zone = RWZone::local());
    RWDBDateTime(const RWDate& rwdate,
                 const RWCString& str,
                 const RWZone& zone = RWZone::local(),
                 const RWLocale& = RWLocale::global());
    RWDBDateTime(unsigned year, unsigned month, unsigned day,
                 unsigned hour = 0, unsigned minute = 0,
                 unsigned second = 0, unsigned millesecond = 0,
                 const RWZone& zone = RWZone::local());
    RWDBDateTime(unsigned long jd,
                 unsigned hour = 0, unsigned minute = 0,
                 unsigned second = 0, unsigned millesecond = 0,
                 const RWZone& zone = RWZone::local());
    ~RWDBDateTime();

// public member functions
    RWspace         binaryStoreSize() const;
    void            now();
    void            extract(struct tm* tmbuf,
                            const RWZone& zone = RWZone::local()) const;
    unsigned        hash() const;
    RWBoolean       isValid() const;

    unsigned        year() const;
    unsigned        month() const;
    RWCString       monthName(const RWLocale& locale =
                                                RWLocale::global()) const;
    unsigned        weekDay() const;
    RWCString       weekDayName(const RWLocale& locale
                                              = RWLocale::global()) const;
    unsigned        day() const;
    unsigned        dayOfMonth() const;
    unsigned        firstDayOfMonth() const;
    unsigned        firstDayOfMonth(unsigned month) const;
    RWBoolean       leap() const;
    RWDBDateTime    previous(unsigned dayNum) const;
    RWDBDateTime    previous(const char* dayName,
                             const RWLocale& locale = RWLocale::global()) const;

    unsigned        hour(const RWZone& zone = RWZone::local()) const;
    unsigned        hourGMT() const;
    RWBoolean       isDST(const RWZone& zone = RWZone::local()) const;
    unsigned        minute(const RWZone& zone = RWZone::local()) const;
    unsigned        minuteGMT() const;
    unsigned        second() const;
    unsigned        millisecond() const;
    unsigned long   seconds() const;

    int             compareTo(const RWDBDateTime* dt) const;
    int             compareTo(const RWDate* rwdate) const;
    int             compareTo(const RWTime* rwtime) const;
    RWDBDateTime    max(const RWDBDateTime& dt) const;
    RWDBDateTime    min(const RWDBDateTime& dt) const;
    RWBoolean       between(const RWDBDateTime& dt1,
                                    const RWDBDateTime& dt2) const;
    RWBoolean       isEqual(const RWDBDateTime& dt) const;

    RWDBDateTime    addYears(int num);
    RWDBDateTime    addMonths(int num);
    RWDBDateTime    addDays(int num);
    RWDBDateTime    addHours(int num);
    RWDBDateTime    addMinutes(int num);
    RWDBDateTime    addSeconds(int num);
    RWDBDateTime    addMilliseconds(long num);

    RWDate          rwdate() const;
    RWTime          rwtime() const;

    RWCString       asString() const;
    RWCString       asString(char format, 
                             const RWZone& zone = RWZone::local(), 
                             const RWLocale& locale = RWLocale::global()) const; 
    RWCString       asString(const char *format) const;
    RWCString       asString(const char *format, 
                             const RWZone& zone, 
                             const RWLocale& locale = RWLocale::global()) const; 
    void            saveOn(RWFile& file) const;
    void            saveOn(RWvostream& stream) const;
    void            restoreFrom(RWFile& file); 
    void            restoreFrom(RWvistream& stream);

      // public member operators
    RWDBDateTime& operator=(const RWDBDateTime& dt);

      // related global operators
    friend RWBoolean rwdbexport     operator< (const RWDBDateTime& dt1,
                                               const RWDBDateTime& dt2);
    friend RWBoolean rwdbexport     operator<=(const RWDBDateTime& dt1,
                                               const RWDBDateTime& dt2);
    friend RWBoolean rwdbexport     operator> (const RWDBDateTime& dt1,
                                               const RWDBDateTime& dt2);
    friend RWBoolean rwdbexport     operator>=(const RWDBDateTime& dt1,
                                               const RWDBDateTime& dt2);
    friend RWBoolean rwdbexport     operator==(const RWDBDateTime& dt1,
                                               const RWDBDateTime& dt2);
    friend RWBoolean rwdbexport     operator!=(const RWDBDateTime& dt1,
                                               const RWDBDateTime& dt2);

  private:
    void    changeZone(const RWZone& = RWZone::local());
    void    clear();

    void    cow();

    RWDBDateTimeImp   *impl_;

};

/* Global declarations for Related Global operators */

RWBoolean rwdbexport     operator< (const RWDBDateTime& dt1,
                                               const RWDBDateTime& dt2);
RWBoolean rwdbexport     operator<=(const RWDBDateTime& dt1,
                                               const RWDBDateTime& dt2);
RWBoolean rwdbexport     operator> (const RWDBDateTime& dt1,
                                               const RWDBDateTime& dt2);
RWBoolean rwdbexport     operator>=(const RWDBDateTime& dt1,
                                               const RWDBDateTime& dt2);
RWBoolean rwdbexport     operator==(const RWDBDateTime& dt1,
                                               const RWDBDateTime& dt2);
RWBoolean rwdbexport     operator!=(const RWDBDateTime& dt1,
                                               const RWDBDateTime& dt2);

#endif
