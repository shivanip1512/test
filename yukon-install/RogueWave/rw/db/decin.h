#ifndef __RWDECIN_H__
#define __RWDECIN_H__

/***************************************************************************
 *
 * decin.h -- RWDecimalParser: parse decimal fractions from an input stream
 *
 * $Id$
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
 **************************************************************************/


#include <rw/db/decport.h>


class RWDCMLExport RWDecimalParser {
public:
  RWDecimalParser();
  RWDecimalPortable operator()(RW_SL_IO_STD(istream&));    // parse a number from a stream
private:
  RWDecimalPortable number_;

  /*
   * Lexical analysis.
   * Interface is via the reset(), token(), consume(), and digit() functions.
   * The RWCStrings allow adjustments of precicely what matches a token.
   */
  RW_SL_IO_STD(istream *input_);
  RWCString leadingString_, trailingString_, digitSepString_, decString_;
public:
  // this enum must be public for Borland 3.2 to compile definition of token()
  enum Token { DIGIT, DIGITSEP, DEC, LEADING, TRAILING, PERCENT,
               PLUS, MINUS, LPAREN, RPAREN, NULLSTATE, MISSING, NAN, ERR, NONE };
private:
  Token     token_;       // current token: access via token() only!
  char      digit_;       // access via digit() only!
  int       unmatchedLparens_;  // RPAREN only found if this is > 0
  void      reset(RW_SL_IO_STD(istream&));    // prepare to start lexical analysis
  Token     token();      // returns the current token
  RWBoolean consume(Token);//consumes the indicated token, or returns FALSE if wrong token
  char      digit();      // if token() is DIGIT, this is the digit read in
  void      nextToken();  // called by token() when necessary
  RWBoolean stripFromInput(const char *);

  /*
   * Parsing.  Each parse functions reads in the associated non-terminal.
   * If a syntax error is encountered, FALSE is returned, otherwise TRUE
   * is returned.  The number is filled in as it is parsed.
   */
  RWBoolean start();
  RWBoolean anonum();
  RWBoolean led();
  RWBoolean sined();      // Can't use signed, it's a keyword
  RWBoolean frac();
  RWBoolean digits(RWCString *num);
};

#endif

