#ifndef __RWDECPORT_H__
#define __RWDECPORT_H__

/***************************************************************************
 *
 * RWDecimalPortable: portable decimal representation, used for I/O and
 *                    as an intermediate in conversions
 *
 * Copyright (c) 1993, 1996-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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
 ***************************************************************************
 *
 * This class represents an arbitrary precision decimal fraction.  The
 * representation is not exposed via member functions for accessing the
 * strings representing the pieces before and after the decimal.  This
 * is done because it might make sense to one day replace this implementation
 * with an arbitrary precision integer based implementation.
 *
 * The concept of trailing zeros after the decimal place is supported. If
 * you construct an RWDecimalPortable using the string constructor and have
 * trailing zeros (eg 1.200), then you are guaranteed that the string conversion
 * operator will return a string with these trailing zeros.  This is necessary
 * for correct conversions between fixed decimal types in Money.h++.  Also,
 * rounding supports the concept of trailing zeros, so round("1.2",2) yields
 * 1.20.  The math functions (+-*) truncate trailing zeros.  Use trimZeros()
 * to strip off the excess zeros.  Note that because trailing zeroes are 
 * allowed the relation x==y does not guarantee that RWCString(x)==RWCString(y)
 * since "1.200"=="1.2".
 *
 * Input note: for details on allowable input syntax, see RWDecimalParser.
 *
 **************************************************************************/

#include <rw/cstring.h>
#include <rw/db/decbase.h>

// Forward declaration of locale classe
class RWLocaleSnapshot;

// If using standard library, include <utility> for possible
// relational operator definitions.
#ifndef RW_NO_STL
# include <utility>
#endif


class RWDCMLExport RWDecimalPortable : public RWDecimalBase {
private:
  // Private constructor
    RWDecimalPortable( State s ) { state_ = s; }
    
public:

  static const RWDecimalPortable rwfar null;        // prototype values, use with op= and op==
  static const RWDecimalPortable rwfar missing;
  static const RWDecimalPortable rwfar NaN;
  
  RWDecimalPortable();                     // initializes to zero
  // RWDecimalPortable(const RWDecimalPortable&);  default is ok.
  // RWDecomalPortable& operator=(const RWDecimalPortable&); default is ok.
  RWDecimalPortable(const char *);         // parses input or sets to NaN if unparseable
  
  RWDecimalPortable(long);
  RWDecimalPortable(int);

  operator  RWCString() const;          // format is, eg, -23.21 or 0.32 or 432
  RWCString asString( const RWLocaleSnapshot& ) const;

  size_t   numSignificantDigits() const; // Number of digits. Does not count leading
                                         // 0's (for numbers < 1), minus signs or
                                         // decimal points.

  // precision is to total number of digits in the number
  // scale is the number of digits to the right of the
  // decimal point.
  size_t precision() const { return( before_.length() + after_.length() ); }
  size_t scale()     const { return after_.length(); }
    
  RWBoolean         isNumber() const   {return state_ == normal;}
  State             state() const      {return state_;}
  void              setState(State);
  void              trimZeros();       // Remove leading zeros before decimal point and trailers after

  friend RWDecimalPortable rwdcmlexport
                    round(const RWDecimalPortable&, int digits, 
        RoundingMethod=RWDecimalBase::PLAIN);
  friend double rwdcmlexport toDouble(const RWDecimalPortable&);

// RWCollectable compliant member functions

  RWspace   binaryStoreSize() const;
  unsigned  hash() const;
  void      restoreFrom(RWvistream&);
  void      restoreFrom(RWFile&);
  void      saveOn(RWvostream&) const;
  void      saveOn(RWFile&) const;


// Rudimentary math functions.  These are not very efficient.
// For efficiency, use one of the decimal classes of Money.h++
// These are very useful because they provide a
// second implementation of arithmetic which can be used for testing
// purposes with the RWDecimal classes.  They could also be used as a
// poor man's arbitrary precision decimal class.

friend RWDecimalPortable rwdcmlexport operator-(const RWDecimalPortable&);
friend RWDecimalPortable rwdcmlexport operator+(const RWDecimalPortable&, const RWDecimalPortable&);
friend RWDecimalPortable rwdcmlexport operator-(const RWDecimalPortable&, const RWDecimalPortable&);
friend RWDecimalPortable rwdcmlexport operator*(const RWDecimalPortable&, const RWDecimalPortable&);

// Relational operators.
//
// == and < are implemented the hard way.  All the rest are inline functions
// which refer back to these two.
friend RWBoolean rwdcmlexport operator==(const RWDecimalPortable& x, const RWDecimalPortable& y) ;
friend RWBoolean rwdcmlexport operator<(const RWDecimalPortable& x, const RWDecimalPortable& y) ;


private:
  RWBoolean negative_;      // TRUE indicates number less than zero
  RWCString before_;        // digits before the decimal
  RWCString after_;         // digits after the decimal

  // Implementation helper functions
  static RWDecimalPortable plus(const RWDecimalPortable& x, const RWDecimalPortable& y);
  static RWDecimalPortable minus(const RWDecimalPortable& x, const RWDecimalPortable& y);
  static RWCString decimalSeparator( const RWLocaleSnapshot& );
  static RWCString thousandsSeparator( const RWLocaleSnapshot& );
friend class RWExport RWDecimalParser;
friend class RWDCMLExport RWDecimalPortableInit;

//#if defined(__GNUG__)
    // These are a workaround for a g++ bug DON'T USE THEM in user code
//    static RWDecimal64    nullValue();
//    static RWDecimal64    missingValue();
//    static RWDecimal64    NaNValue();
//#endif

};

/*
 * I/O.  All RWDecimal I/O is done via RWDecimalPortable objects.
 */

#ifndef RW_TRAILING_RWEXPORT
#  if defined (__OS2__)
RW_SL_IO_STD(ostream&) operator<<(RW_SL_IO_STD(ostream&),const RWDecimalPortable&);
RW_SL_IO_STD(istream&) operator>>(RW_SL_IO_STD(istream&),RWDecimalPortable&);
#  else
rwdcmlexport RW_SL_IO_STD(ostream&) operator<<(RW_SL_IO_STD(ostream&),const RWDecimalPortable&);
rwdcmlexport RW_SL_IO_STD(istream&) operator>>(RW_SL_IO_STD(istream&),RWDecimalPortable&);
#  endif
#else
RW_SL_IO_STD(ostream&) rwdcmlexport operator<<(RW_SL_IO_STD(ostream&),const RWDecimalPortable&);
RW_SL_IO_STD(istream&) rwdcmlexport operator>>(RW_SL_IO_STD(istream&),RWDecimalPortable&);
#endif


// If using the standard library with broken namespace, then these functions
// are in <utility>.  Otherwise they are here.
#if defined(RW_NO_STL) || ( !defined(RW_NO_STL) && !defined(RWSTD_NO_NAMESPACE) )
RWBoolean rwdcmlexport operator!=(const RWDecimalPortable& x, const RWDecimalPortable& y);

RWBoolean rwdcmlexport operator>=(const RWDecimalPortable& x, const RWDecimalPortable& y);

RWBoolean rwdcmlexport operator>(const RWDecimalPortable& x, const RWDecimalPortable& y);

RWBoolean rwdcmlexport operator<=(const RWDecimalPortable& x, const RWDecimalPortable& y);
#endif


/*
 * The RWDecimalPortableInit class initializes static variables before user code
 * using the iostream trick of putting a static init object in every
 * translation unit and initializing the first time one is constructed.
 * The implementation data structures and static vars NaN, null, missing
 * are initialized.
 */

class RWDCMLExport RWDecimalPortableInit
{
  static int count;
public:
  RWDecimalPortableInit();
};

static RWDecimalPortableInit RWDecimalPortableInitVar;
/* Global Declarations for Friend functions */

RWDecimalPortable rwdcmlexport operator-(const RWDecimalPortable&);
RWDecimalPortable rwdcmlexport operator+(const RWDecimalPortable&, const RWDecimalPortable&);
RWDecimalPortable rwdcmlexport operator-(const RWDecimalPortable&, const RWDecimalPortable&);
RWDecimalPortable rwdcmlexport operator*(const RWDecimalPortable&, const RWDecimalPortable&);

RWBoolean rwdcmlexport operator==(const RWDecimalPortable& x, const RWDecimalPortable& y) ;
RWBoolean rwdcmlexport operator<(const RWDecimalPortable& x, const RWDecimalPortable& y) ;

#endif







