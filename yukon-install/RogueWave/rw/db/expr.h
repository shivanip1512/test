#ifndef __RWDB_EXPR_H__
#define __RWDB_EXPR_H__

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
 **************************************************************************/


#include <rw/db/defs.h>
#include <rw/db/value.h>
#include <rw/db/phrase.h>

#include <rw/colclass.h>


//////////////////////////////////////////////////////////////////////////
//
//  RWDBExpr 
//
//  This class represents expressions used in constructing SQL
//  statements.  It allows the clauses to be constructed using C++ syntax and
//  then translated into SQL.  Because there are several types of expressions,
//  this class is simply an interface class to a family of implementations.
//  These implemetations can represent numeric and string constants, columns
//  from tables, or other compound expressions.  By its nature, an RWDBExpr
//  instance can actually represent a complete complex expression in the form
//  of a parse tree.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBExpr {
  public:
  
    enum AsStringControlFlag { normal, suppressTagsOnColumns, noPlaceHolder };

    RWDBExpr (const RWDBValue& value, RWBoolean usePhraseBook = TRUE, AsStringControlFlag = normal);
    RWDBExpr ();
    RWDBExpr (const RWDBExpr& expr);

    RWDBExpr (char value);
    RWDBExpr (unsigned char value);
    RWDBExpr (short value);
    RWDBExpr (unsigned short value);
    RWDBExpr (int value);
    RWDBExpr (unsigned int value);
    RWDBExpr (long int value);
    RWDBExpr (unsigned long int value);
    RWDBExpr (float value);
    RWDBExpr (double value);
    RWDBExpr (const char* value);
#ifndef RW_NO_WSTR
    RWDBExpr (const wchar_t* value);
#endif

    RWDBExpr (const RWDBColumn& col);
    RWDBExpr (const RWCString& value);
    RWDBExpr (const RWDecimalPortable& value);
    RWDBExpr (const RWDBDateTime& value);
    RWDBExpr (const RWDBDuration& value);
    RWDBExpr (const RWDate& value);
    RWDBExpr (const RWTime& value);
    RWDBExpr (const RWDBBlob& value);
    RWDBExpr (const RWDBMBString& value);
#ifndef RW_NO_WSTR
    RWDBExpr (const RWWString& value);
#endif
    RWDBExpr (const RWDBSelectorBase& subSelect);
    RWDBExpr (RWDBValueManip manip);
    RWDBExpr (const RWCollection& collection);
    ~RWDBExpr();

    RWDBExpr& operator= (const RWDBExpr&);

    RWDBPhraseBook::RWDBPhraseKey getOperator(void) const;

        RWDBValue::ValueType type() const;

    RWCString asString (const RWDBPhraseBook&,
                        AsStringControlFlag = normal) const;
    
    RWDBCriterion between (const RWDBExpr&, const RWDBExpr&) const;
    RWDBCriterion in (const RWDBExpr&) const;
    RWDBCriterion isNull (void) const;
    RWDBCriterion leftOuterJoin (const RWDBExpr&) const;
    RWDBCriterion like (const RWDBExpr&) const;
    RWDBCriterion rightOuterJoin (const RWDBExpr&) const;
    RWDBCriterion fullOuterJoin (const RWDBExpr&) const;

    RWDBExpr (const RWCString&, const RWDBExpr*, const RWDBExpr*,
              const RWDBExpr* = 0, const RWDBExpr* = 0);
    RWDBExpr ( const RWDBExpr* , const RWDBExpr* ,
              RWDBPhraseBook::RWDBPhraseKey, 
              RWDBPhraseBook::RWDBPhraseKey pk = RWDBPhraseBook::singleSpace, 
              RWDBPhraseBook::RWDBPhraseKey pk2 = RWDBPhraseBook::singleSpace );

    RWDBExpr (RWDBPhraseBook::RWDBPhraseKey, const RWDBExpr* = 0,
              const RWDBExpr* = 0, const RWDBExpr* = 0, const RWDBExpr* = 0);
    RWDBExpr (RWDBPhraseBook::RWDBPhraseKey, const RWDBExpr&);
    RWDBExpr (const RWDBSchema&);

    RWBoolean isValid (void) const;
    RWBoolean isEquivalent(const RWDBExpr&) const;
    RWDBCriterion matchUnique (const RWDBExpr&) const;

    RWDBExpr (const RWDBTable&);
    RWDBExpr (const RWDBExpr&, RWDBPhraseBook::RWDBPhraseKey, const RWDBExpr&);
    RWDBExpr (const RWDBColumn&, const RWDBExpr&);
    RWDBExpr (const RWDBColumn&, const RWDBValue&);

  protected:
    RWDBExpr (RWDBExprImp*);
    RWDBExprImp* impl_;

  private:

    friend class RWDBExport RWDBExprHelper;
        // Candidates for removal:
      // void tableTagExpr(RWSet&) const;
    
};

//////////////////////////////////////////////////////////////////////////
//
//  RWDBCollectableExpr
//
//   An RWDBCollectableExpr instance may be stored in an RWCollection.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBCollectableExpr : public RWCollectable, public RWDBExpr {
  public:
    RWDBCollectableExpr (const RWDBExpr& expr);
    ~RWDBCollectableExpr ();
    unsigned hash () const;
    RWBoolean isEqual (const RWCollectable* expr) const;
};

//////////////////////////////////////////////////////////////////////////
//  
//  R W D B C r i t e r i o n
//
//  This class represents a specialized type of RWDBExpr that will be evaluated
//  as a Boolean value.  This class adds neither components
//  nor memeber functions to the base class.  Its provides a
//  layer of type safety: a non-Boolean expression cannot accidentally be
//  placed in a location requiring a boolean.
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBCriterion : public RWDBExpr {
  public:
    RWDBCriterion (void);
    RWDBCriterion (const RWDBExpr& xLeft,
                   RWDBPhraseBook::RWDBPhraseKey op,
                   const RWDBExpr& xRight);
    RWDBCriterion (RWDBPhraseBook::RWDBPhraseKey op, const RWDBExpr& x);
    RWDBCriterion (RWDBPhraseBook::RWDBPhraseKey, const RWDBExpr* x0 = 0,
                   const RWDBExpr* x1 = 0, const RWDBExpr* x2 = 0,
                   const RWDBExpr* x3 = 0);
    RWDBCriterion (const RWCString& s, const RWDBExpr* x0,
                   const RWDBExpr* x1, const RWDBExpr* x2 = 0,
                   const RWDBExpr* x3 = 0);
   ~RWDBCriterion(void);

    RWDBCriterion& operator=(const RWDBCriterion& crit);
};

//////////////////////////////////////////////////////////////////////////
//
//  RWDBAssignment
//
//  RWDBAssignment is a specialized type of RWDBExpr that represents
//  an assignment of a value to a column in a database table. It corresponds
//  to the SQL "set <column-name> = <expression>" phrase.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBAssignment : public RWDBExpr , public RWCollectable {
  public:
    RWDBAssignment (void);
    RWDBAssignment (const RWDBColumn& c, const RWDBExpr& e);
    RWDBAssignment (const RWDBColumn& c, const RWDBValue& v);
    ~RWDBAssignment(void);
    RWDBColumn column() const;
    RWDBValue value() const;
    RWDBExpr expression() const;
    unsigned long updateSize() const;
};

/*
 *
 * class RWDBBoundExpr
 *
 * Used to bind a variable to a database column.
 *
 */

class RWDBExport RWDBBoundExpr : public RWDBExpr {

  public:

    RWDBBoundExpr();
    RWDBBoundExpr(short* valuePtr, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(unsigned short* valuePtr, RWBoolean* nullIndicator=NULL);
          RWDBBoundExpr(int *i, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(unsigned int* valuePtr, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(long* valuePtr, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(unsigned long* valuePtr, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(float* valuePtr, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(double* valuePtr, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(RWCString* valuePtr, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(RWDecimalPortable* valuePtr, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(RWDate* valuePtr, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(RWDBDateTime* valuePtr, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(RWDBDuration* valuePtr, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(RWDBBlob* valuePtr, RWBoolean* nullIndicator=NULL);
    RWDBBoundExpr(RWDBMBString* valuePtr, RWBoolean* nullIndicator=NULL);
#ifndef RW_NO_WSTR
    RWDBBoundExpr(RWWString* valuePtr, RWBoolean* nullIndicator=NULL);
#endif

    ~RWDBBoundExpr();

  protected:

  private:
    RWDBBoundExpr(const RWDBBoundExpr&);
    RWDBBoundExpr& operator=(const RWDBBoundExpr&);

};

///////////////////////////////////////////////////////////////////////////
//
// RWDBJoinExpr - Used for Outer join constructs
// Produced by global methods that generate syntax for outer joins
//
///////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBJoinExpr : public RWDBExpr {
public :
    RWDBJoinExpr();
    RWDBJoinExpr( const RWDBExpr& expr1, const RWDBExpr& expr2,
                  RWDBPhraseBook::RWDBPhraseKey op,
                  RWDBPhraseBook::RWDBPhraseKey precdBegin = 
                      RWDBPhraseBook::singleSpace,
                  RWDBPhraseBook::RWDBPhraseKey precdEnd = 
                      RWDBPhraseBook::singleSpace
                );

    ~RWDBJoinExpr();

    RWDBJoinExpr( const RWDBJoinExpr& joinExpr );
    RWDBJoinExpr& operator=( const RWDBJoinExpr& joinExpr );

protected :
private :

};



//////////////////////////////////////////////////////////////////////////
//  
//  Related Global Functions 
//  
//////////////////////////////////////////////////////////////////////////

RWDBExpr rwdbexport operator+ (const RWDBExpr&, const RWDBExpr&);
RWDBExpr rwdbexport operator+ (const RWDBExpr&);
RWDBExpr rwdbexport operator- (const RWDBExpr&, const RWDBExpr&);
RWDBExpr rwdbexport operator- (const RWDBExpr&);
RWDBExpr rwdbexport operator* (const RWDBExpr&, const RWDBExpr&);
RWDBExpr rwdbexport operator/ (const RWDBExpr&, const RWDBExpr&);
RWDBExpr rwdbexport operator% (const RWDBExpr&, const RWDBExpr&);

RWDBCriterion rwdbexport operator== (const RWDBExpr&, const RWDBExpr&);
RWDBCriterion rwdbexport operator!= (const RWDBExpr&, const RWDBExpr&);
RWDBCriterion rwdbexport operator>  (const RWDBExpr&, const RWDBExpr&);
RWDBCriterion rwdbexport operator<  (const RWDBExpr&, const RWDBExpr&);
RWDBCriterion rwdbexport operator>= (const RWDBExpr&, const RWDBExpr&);
RWDBCriterion rwdbexport operator<= (const RWDBExpr&, const RWDBExpr&);

RWDBCriterion rwdbexport operator&& (const RWDBCriterion&,
                                     const RWDBCriterion&);
RWDBCriterion rwdbexport operator|| (const RWDBCriterion&,
                                     const RWDBCriterion&);
RWDBCriterion rwdbexport operator!  (const RWDBCriterion&);
RWDBCriterion rwdbexport rwdbExists (const RWDBSelectorBase&);

RWDBExpr rwdbexport rwdbAvg (const RWDBExpr&);
RWDBExpr rwdbexport rwdbCast (const RWDBExpr&, const RWDBValue&);
RWDBExpr rwdbexport rwdbCast (const RWDBExpr&, const RWDBValue&,
                              const RWDBExpr&);
RWDBExpr rwdbexport rwdbCast (const RWDBExpr&, const RWDBValue&,
                              const RWDBExpr&, const RWDBExpr&);
RWDBExpr rwdbexport rwdbCharLength (const RWDBExpr&);
RWDBExpr rwdbexport rwdbCount (const RWDBExpr&);
RWDBExpr rwdbexport rwdbCount ();
RWDBExpr rwdbexport rwdbCountDistinct (const RWDBExpr&);
RWDBExpr rwdbexport rwdbCurrentUser ();
RWDBExpr rwdbexport rwdbLower (const RWDBExpr&);
RWDBExpr rwdbexport rwdbMax (const RWDBExpr&);
RWDBExpr rwdbexport rwdbMin (const RWDBExpr&);
RWDBExpr rwdbexport rwdbName (const RWCString&, const RWDBExpr&);
RWDBExpr rwdbexport rwdbPosition (const RWDBExpr&, const RWDBExpr&);
RWDBExpr rwdbexport rwdbSessionUser ();
RWDBExpr rwdbexport rwdbSubString (const RWDBExpr&, const RWDBExpr&);
RWDBExpr rwdbexport rwdbSubString (const RWDBExpr&, const RWDBExpr&,
                                   const RWDBExpr&);
RWDBExpr rwdbexport rwdbSum (const RWDBExpr&);
RWDBExpr rwdbexport rwdbSystemDateTime ();
RWDBExpr rwdbexport rwdbSystemUser ();
RWDBExpr rwdbexport rwdbTrimLeading (const RWDBExpr&, const RWDBExpr&);
RWDBExpr rwdbexport rwdbTrimTrailing (const RWDBExpr&, const RWDBExpr&);
RWDBExpr rwdbexport rwdbTrimBoth (const RWDBExpr&, const RWDBExpr&);
RWDBExpr rwdbexport rwdbUpper (const RWDBExpr&);

//////////////////////////////////////////////////////////////////////////////
//
// Outer Join Constructs
//
//////////////////////////////////////////////////////////////////////////////
RWDBJoinExpr rwdbexport rwdbLeftOuter(const RWDBTable&, const RWDBTable&);
RWDBJoinExpr rwdbexport rwdbLeftOuter(const RWDBTable&, const RWDBJoinExpr&);
RWDBJoinExpr rwdbexport rwdbLeftOuter(const RWDBJoinExpr&, const RWDBTable&);
RWDBJoinExpr rwdbexport rwdbLeftOuter(const RWDBJoinExpr&, const RWDBJoinExpr&);

RWDBJoinExpr rwdbexport rwdbRightOuter(const RWDBTable&,const RWDBTable&);
RWDBJoinExpr rwdbexport rwdbRightOuter(const RWDBTable&,const RWDBJoinExpr&);
RWDBJoinExpr rwdbexport rwdbRightOuter(const RWDBJoinExpr&,const RWDBTable&);
RWDBJoinExpr rwdbexport rwdbRightOuter(const RWDBJoinExpr&,const RWDBJoinExpr&);

RWDBJoinExpr rwdbexport rwdbOuter( const RWDBTable&, const RWDBTable& );
RWDBJoinExpr rwdbexport rwdbOuter( const RWDBTable&, const RWDBJoinExpr& );
RWDBJoinExpr rwdbexport rwdbOuter( const RWDBJoinExpr&, const RWDBTable& );
RWDBJoinExpr rwdbexport rwdbOuter( const RWDBJoinExpr&, const RWDBJoinExpr& );

//////////////////////////////////////////////////////////////////////////////
//
// Inner Join Constructs
//
//////////////////////////////////////////////////////////////////////////////
RWDBJoinExpr rwdbexport rwdbInner(const RWDBTable&, const RWDBTable&);
RWDBJoinExpr rwdbexport rwdbInner(const RWDBTable&, const RWDBJoinExpr&);
RWDBJoinExpr rwdbexport rwdbInner(const RWDBJoinExpr&, const RWDBTable&);
RWDBJoinExpr rwdbexport rwdbInner(const RWDBJoinExpr&, const RWDBJoinExpr&);

RWDBJoinExpr rwdbexport rwdbNaturalOuter( const RWDBTable&, const RWDBTable& );
RWDBJoinExpr rwdbexport rwdbNaturalOuter( 
                           const RWDBTable&, const RWDBJoinExpr& );
RWDBJoinExpr rwdbexport rwdbNaturalOuter( 
                           const RWDBJoinExpr&, const RWDBTable& );
RWDBJoinExpr rwdbexport rwdbNaturalOuter( 
                           const RWDBJoinExpr&, const RWDBJoinExpr& );

RWDBJoinExpr rwdbexport rwdbNaturalLeftOuter(
                           const RWDBTable&, const RWDBTable&);
RWDBJoinExpr rwdbexport rwdbNaturalLeftOuter(
                           const RWDBTable&, const RWDBJoinExpr&);
RWDBJoinExpr rwdbexport rwdbNaturalLeftOuter(
                           const RWDBJoinExpr&, const RWDBTable&);
RWDBJoinExpr rwdbexport rwdbNaturalLeftOuter(
                           const RWDBJoinExpr&, const RWDBJoinExpr&);

RWDBJoinExpr rwdbexport rwdbNaturalRightOuter(
                           const RWDBTable&,const RWDBTable&);
RWDBJoinExpr rwdbexport rwdbNaturalRightOuter(
                           const RWDBTable&,const RWDBJoinExpr&);
RWDBJoinExpr rwdbexport rwdbNaturalRightOuter(
                           const RWDBJoinExpr&,const RWDBTable&);
RWDBJoinExpr rwdbexport rwdbNaturalRightOuter(
                           const RWDBJoinExpr&,const RWDBJoinExpr&);


#endif

