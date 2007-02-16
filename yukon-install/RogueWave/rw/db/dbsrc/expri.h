#ifndef __RWDB_EXPRI_H__
#define __RWDB_EXPRI_H__

/***************************************************************************
 *
 * $Id$
 *
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

#include <rw/db/dbref.h>
#include <rw/db/expr.h>
#include <rw/db/schema.h>
#include <rw/db/select.h>
#include <rw/db/table.h>

#include <rw/db/dbsrc/stmt.h>


// RWDBExprImp::isA():
#define __RWDBEXPRIMP                   0xDB10
#define __RWDBDYADICEXPRIMP             0xDB11
#define __RWDBASSIGNMENTEXPRIMP         0xDB12
#define __RWDBMONADICEXPRIMP            0xDB13
#define __RWDBCOLUMNEXPRIMP             0xDB14
#define __RWDBFORMSUBSTITUTIONEXPRIMP   0xDB15
#define __RWDBFUNCTIONEXPRIMP           0xDB16
#define __RWDBPHRASEKEYEXPRIMP          0xDB17
#define __RWDBVALUEEXPRIMP              0xDB18
#define __RWDBCOLLECTIONEXPRIMP         0xDB19
#define __RWDBSELECTOREXPRIMP           0xDB1A
#define __RWDBSCHEMAEXPRIMP             0xDB1B
#define __RWDBBOUNDEXPRIMP              0xDB1C
#define __RWDBTABLEEXPRIMP              0xDB1D
#define __RWDBJOINEXPRIMP               0xDB1E

//extern const RWDBPrecedence rwdbDefaultOperatorPrecedences;

//////////////////////////////////////////////////////////////////////////
//  
//  RWDBExprImp
//
//  This class is the base class for all expression implementations.  It
//  contains reference counting facilities and provids the
//  standard interface for all implementations.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBExprImp : public RWDBReference {

  public:
    RWDBExprImp ();
    virtual ~RWDBExprImp ();

    virtual void asString (const RWDBPhraseBook&,
                           RWDBExpr::AsStringControlFlag,
                           RWCString& retString,
                           RWDBStatementImp *stmt = 0) const = 0;
        virtual RWDBValue::ValueType type();
    virtual int                 nativeType() const;
    virtual void                setNativeType(int nativeType);

        virtual RWDBPhraseBook::RWDBPhraseKey getOperator() const;
    virtual void tableTagExpr (RWSet&) const;

    virtual RWClassID isA() const;
    virtual RWBoolean isEquivalent(const RWDBExprImp*) const;

  protected:

    friend class RWDBExpr;

private:
// not implemented
    RWDBExprImp( const RWDBExprImp& );
    RWDBExprImp& operator=( const RWDBExprImp& );
};

/*
 * The purpose of this class is to provide the ExprImp class with access
 * to the private and protected functions of the Expr which are not in
 * the public interface.
 *
 * This class uses the singleton pattern.
 */

class RWDBExport RWDBExprHelper {

  public:

    virtual ~RWDBExprHelper();
    
    static RWDBExprHelper *instance();
    static RWDBExprImp *getImp(const RWDBExpr&);

  protected:

    RWDBExprHelper();
    
  private:

    static RWDBExprHelper *instance_;
    
};


//////////////////////////////////////////////////////////////////////////
//  
//  RWDBDyadicExprImp
//
//  This class implements an expression constisting of a left expression,
//  an operator and a right expression.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBDyadicExprImp : public RWDBExprImp {
public:
    RWDBDyadicExprImp ();
    RWDBDyadicExprImp (const RWDBExpr&, /*enum*/ RWDBPhraseBook::RWDBPhraseKey,
                       const RWDBExpr&);
    virtual ~RWDBDyadicExprImp ();

    virtual void asString (const RWDBPhraseBook&,
                           RWDBExpr::AsStringControlFlag,
                           RWCString& retString,
                           RWDBStatementImp *stmt = 0) const;
    
    virtual RWDBPhraseBook::RWDBPhraseKey getOperator() const;
    virtual void tableTagExpr (RWSet&) const;
    virtual RWClassID isA() const;
    // virtual RWBoolean isA(RWClassID) const;  // defined by base class
    virtual RWBoolean isEquivalent(const RWDBExprImp*) const;

private:
    RWDBExpr leftExpr_;
    RWDBExpr rightExpr_;
    RWDBPhraseBook::RWDBPhraseKey op_;

};

//////////////////////////////////////////////////////////////////////////
//  
//  RWDBAssignmentExprImp
//
//  This class implements an expression constisting of a left expression,
//  operator "=", and a right expression.
//
//  Components:
//    leftExpr_ - an RWDBExpr representing the left expression
//    rightExpr_ - an RWDBExpr representing the right expression
//    op_ - an enum that represents the operator in the expression
//    column_ - the column to which an assignment is being made
//    rightExpr_ - the expression being assigned to the column, or an
//             empty expression if a value, rather than an expression is
//             being assigned
//    value_ - the value being assigned to the column, or a NULL value
//             if an expression, rather than a simple value is being assigned
//    valueSize_ - an estimate ( > 0 ) of the size of the rhs of the assignment,
//             if the rhs is a value, or 0 if the rhs is an expression
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBAssignmentExprImp : public RWDBExprImp {
  public:
    RWDBAssignmentExprImp ();
    RWDBAssignmentExprImp (const RWDBColumn&, const RWDBExpr&);
    RWDBAssignmentExprImp (const RWDBColumn&, const RWDBValue&);
    virtual ~RWDBAssignmentExprImp ();

    virtual void asString (const RWDBPhraseBook&,
                           RWDBExpr::AsStringControlFlag,
                           RWCString& retString,
                           RWDBStatementImp *stmt = 0) const;
    
    virtual RWDBPhraseBook::RWDBPhraseKey getOperator() const;
    virtual RWDBColumn column() const;
    virtual RWDBValue value() const;
    virtual RWDBExpr expression() const;
    virtual unsigned long updateSize() const;
    virtual void tableTagExpr (RWSet&) const;
    virtual RWClassID isA() const;
    // virtual RWBoolean isA(RWClassID) const;  // defined by base class
    virtual RWBoolean isEquivalent(const RWDBExprImp*) const;

private:
    RWDBColumn                    column_;
    RWDBExpr                      rightExpr_;
    unsigned long                 valueSize_;
    RWDBPhraseBook::RWDBPhraseKey op_;
};

//////////////////////////////////////////////////////////////////////////
//  
//  RWDBMonadicExprImp
//  
//  This class implements an expression constisting of a single expression and
//  an operator.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBMonadicExprImp : public RWDBExprImp {
public:
    RWDBMonadicExprImp ();
    RWDBMonadicExprImp (RWDBPhraseBook::RWDBPhraseKey, const RWDBExpr&);
    virtual ~RWDBMonadicExprImp ();

    virtual void asString (const RWDBPhraseBook&,
                           RWDBExpr::AsStringControlFlag,
                           RWCString& retString,
                           RWDBStatementImp *stmt = 0) const;
    
    virtual RWDBPhraseBook::RWDBPhraseKey getOperator() const;
    virtual void tableTagExpr (RWSet&) const;
    virtual RWClassID isA() const;
    // virtual RWBoolean isA(RWClassID) const;  // defined by base class
    virtual RWBoolean isEquivalent(const RWDBExprImp*) const;

private:
    RWDBExpr theExpr_;
    RWDBPhraseBook::RWDBPhraseKey op_;

};

//////////////////////////////////////////////////////////////////////////
//  
//  RWDBTableExprImp
//
//  This class implements a column encapsulated in an expression.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBTableExprImp : public RWDBExprImp {
public:
    RWDBTableExprImp (const RWDBTable&);
    virtual ~RWDBTableExprImp ();

    virtual void asString (const RWDBPhraseBook&,
                           RWDBExpr::AsStringControlFlag,
                           RWCString& retString,
                           RWDBStatementImp *stmt = 0) const;
    
    virtual RWClassID isA() const;
    virtual RWBoolean isEquivalent(const RWDBExprImp*) const;

private:
    RWDBTable theTable_;
};

//////////////////////////////////////////////////////////////////////////
//  
//  RWDBColumnExprImp
//
//  This class implements a column encapsulated in an expression.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBColumnExprImp : public RWDBExprImp {
public:
    RWDBColumnExprImp (const RWDBColumn&);
    virtual ~RWDBColumnExprImp ();

    virtual void asString (const RWDBPhraseBook&,
                           RWDBExpr::AsStringControlFlag,
                           RWCString& retString,
                           RWDBStatementImp *stmt = 0) const;
    
    //virtual RWDBPhraseBook::RWDBPhraseKey getOperator() const;
                   //defined by base class
    virtual void tableTagExpr (RWSet&) const;
    virtual RWClassID isA() const;
    // virtual RWBoolean isA(RWClassID) const;  // defined by base class
    virtual RWBoolean isEquivalent(const RWDBExprImp*) const;

private:
    RWDBColumn theColumn_;
};

//////////////////////////////////////////////////////////////////////////
//  
//  RWDBFormSubstitutionExprImp 
//
//  This class implements a special type of expression that can use
//  several expressions substituted into a string.  For example: if
//  there are two expressions:
//    (COL1 + 14)
//    (COL2 * 3.1415)
//  and the format string is of the form:
//    MAX(%0, %1)
//  an instance of this class will produce:
//    MAX(COL1 + 14, COL2 * 3.1415)
//  when the asString member function is invoked.
//
//  Components:
//    exprs_ - an array of RWDBExpr instances that will be used in the
//             substitution.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBFormSubstitutionExprImp : public RWDBExprImp {
public:
    RWDBFormSubstitutionExprImp (
                  const RWDBExpr* = 0, const RWDBExpr* = 0,
                  const RWDBExpr* = 0, const RWDBExpr* = 0);
    virtual ~RWDBFormSubstitutionExprImp();

    virtual void asString (const RWDBPhraseBook&,
                           RWDBExpr::AsStringControlFlag,
                           RWCString& retString,
                           RWDBStatementImp *stmt = 0) const;
    
    //virtual RWDBPhraseBook::RWDBPhraseKey getOperator() const;
                                     //defined by base class
    virtual void tableTagExpr (RWSet&) const;
    virtual RWClassID isA() const;
    // virtual RWBoolean isA(RWClassID) const;  // defined by base class
    virtual RWBoolean isEquivalent(const RWDBExprImp*) const;

protected:
    virtual RWCString getForm (const RWDBPhraseBook&) const = 0;

private:
    RWDBExpr exprs_[4];

};

/////////////////////////////////////////////////////////////////////////////
//
// RWDBJoinExprImp 
// Used for constructing outer join expression
//
/////////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBJoinExprImp : public RWDBExprImp {
public :
    RWDBJoinExprImp( RWDBPhraseBook::RWDBPhraseKey pk, 
                     const RWDBExpr *x1, const RWDBExpr *x2,
                     RWDBPhraseBook::RWDBPhraseKey precedenceBegin =
                                     RWDBPhraseBook::singleSpace,
                     RWDBPhraseBook::RWDBPhraseKey precedenceEnd = 
                                     RWDBPhraseBook::singleSpace );
    ~RWDBJoinExprImp();

    virtual void asString (const RWDBPhraseBook&,
                           RWDBExpr::AsStringControlFlag,
                           RWCString& retString,
                           RWDBStatementImp *stmt = 0) const;

    virtual RWClassID isA() const;

protected:
    RWCString getForm (const RWDBPhraseBook&) const;
                     
private :
    RWDBPhraseBook::RWDBPhraseKey joinOperator_;
    RWDBPhraseBook::RWDBPhraseKey precedenceBegin_;
    RWDBPhraseBook::RWDBPhraseKey precedenceEnd_;
    
    RWDBExpr exprs_[2];

};


//////////////////////////////////////////////////////////////////////////
//  
//  RWDBFunctionExprImp 
//
//  This class implements the special case of a RWDBFormSubstitutionExprImp
//  where the substitution string is stored locally in the instance of this
//  class.
//
//  Components:
//    format_ - an RWCString storing the template used in the substitution
//              process.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBFunctionExprImp : public RWDBFormSubstitutionExprImp {
public:
    RWDBFunctionExprImp (const RWCString&, const RWDBExpr* = 0,
                         const RWDBExpr* = 0, const RWDBExpr* = 0,
                         const RWDBExpr* = 0);
    virtual ~RWDBFunctionExprImp();

    //virtual RWDBPhraseBook::RWDBPhraseKey getOperator() const;
                      //defined by base class
    //virtual void tableTagExpr (RWSet&) const; //defined by base class
    virtual RWClassID isA() const;
    // virtual RWBoolean isA(RWClassID) const;  // defined by base class
    // virtual RWBoolean isEquivalent(const RWDBExprImp*) const;
                  // defined by base class
private:
    RWCString format_;

protected:
    virtual RWCString getForm (const RWDBPhraseBook&) const;

};

//////////////////////////////////////////////////////////////////////////
//  
//  RWDBPhraseKeyExprImp 
//  
//  This class implements the special case of a RWDBFormSubstitutionExprImp
//  where the substitution string comes from an instance of RWDBPhraseBook.
//
//  Components:
//    phraseKey - an enum storing the key to the template used in the
//                substitution process.  The actual template string is then
//                fetched from an instance of RWDBPhraseBook.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBPhraseKeyExprImp : public RWDBFormSubstitutionExprImp {
public:
    RWDBPhraseKeyExprImp (RWDBPhraseBook::RWDBPhraseKey, 
                          const RWDBExpr* = 0,
                          const RWDBExpr* = 0, const RWDBExpr* = 0,
                          const RWDBExpr* = 0);
    virtual ~RWDBPhraseKeyExprImp();

    virtual RWDBPhraseBook::RWDBPhraseKey getOperator() const;
    //virtual void tableTagExpr (RWSet&) const;   //defined by base class
    virtual RWClassID isA() const;
    // virtual RWBoolean isA(RWClassID) const;  // defined by base class
    // virtual RWBoolean isEquivalent(const RWDBExprImp*) const;
                 // defined by base class
protected:
    RWCString getForm (const RWDBPhraseBook& aPhraseBook) const;

private:
    RWDBPhraseBook::RWDBPhraseKey phraseKey_;
};


//////////////////////////////////////////////////////////////////////////
//  
//  RWDBValueExprImp 
//  
//  This class implements an RWDBValue encapsulated in an expression. Ie,
//  a literal.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBValueExprImp : public RWDBExprImp, public RWDBValue {
public:
    RWDBValueExprImp (const RWDBValue& v, RWBoolean b = TRUE, RWDBExpr::AsStringControlFlag c = RWDBExpr::normal);
    RWDBValueExprImp (char v);
    RWDBValueExprImp (unsigned char v);
    RWDBValueExprImp (short v);
    RWDBValueExprImp (unsigned short v);
    RWDBValueExprImp (int v);
    RWDBValueExprImp (unsigned int v);
    RWDBValueExprImp (long int v);
    RWDBValueExprImp (unsigned long int v);
    RWDBValueExprImp (float v);
    RWDBValueExprImp (double v);
    virtual ~RWDBValueExprImp();

    virtual int                 nativeType() const;
    virtual void                setNativeType(int nativeType);

    virtual RWDBValue::ValueType type();
    virtual void asString (const RWDBPhraseBook&,
                           RWDBExpr::AsStringControlFlag,
                           RWCString&,
                           RWDBStatementImp *stmt = 0) const;
 
    //virtual RWDBPhraseBook::RWDBPhraseKey getOperator() const;
                  //defined by base class
    //virtual void tableTagExpr (RWSet&) const;   //defined by base class
    virtual RWClassID isA() const;
    // virtual RWBoolean isA(RWClassID) const;  // defined by base class
    virtual RWBoolean isEquivalent(const RWDBExprImp*) const;

protected:
      // These functions inherited from RWDBValue, but must be
      // overridden to avoid warnings
    RWCString asString() const;
    RWCString asString(const RWDBPhraseBook& phrasebook) const;
    RWDBExpr::AsStringControlFlag control() const;

private:
    RWBoolean usePhraseBook_;
    int nativeType_;
    RWDBExpr::AsStringControlFlag control_;
};

//////////////////////////////////////////////////////////////////////////
//
//  RWDBCollectionExprImp 
//
//  This class implements an RWCollection encapsulated in an expression.
//  The collection is assumed to contain RWDBCollectableExpr instances.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBCollectionExprImp : public RWDBExprImp {
public:
    RWDBCollectionExprImp (const RWCollection& c);
    virtual ~RWDBCollectionExprImp();

    virtual void asString (const RWDBPhraseBook&,
                           RWDBExpr::AsStringControlFlag,
                           RWCString&,
                           RWDBStatementImp *stmt = 0) const;
    virtual RWDBPhraseBook::RWDBPhraseKey getOperator() const;
    virtual void tableTagExpr (RWSet&) const;
    virtual RWClassID isA() const;
    // virtual RWBoolean isA(RWClassID) const;  // defined by base class
    virtual RWBoolean isEquivalent(const RWDBExprImp*) const;

private:
    const RWCollection& theCollection_;
};

//////////////////////////////////////////////////////////////////////////
//
//  RWDBSelectorExprImp 
//
//  This class implements an RWDBSelctor encapsulated in an expression.
//  It's main purpose is to support the notion of the sub-select.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBSelectorExprImp : public RWDBExprImp {
public:
    RWDBSelectorExprImp (const RWDBSelectorBase&);
    virtual ~RWDBSelectorExprImp();

    virtual void asString (const RWDBPhraseBook&,
                           RWDBExpr::AsStringControlFlag,
                           RWCString &,
                           RWDBStatementImp *stmt = 0) const;
    
    virtual RWDBPhraseBook::RWDBPhraseKey getOperator() const;
    //virtual void tableTagExpr (RWSet&) const;   //defined by base class
    virtual RWClassID isA() const;
    // virtual RWBoolean isA(RWClassID) const;  // defined by base class
    virtual RWBoolean isEquivalent(const RWDBExprImp*) const;

private:
    RWDBSelectorBase theSelector_;

};

//////////////////////////////////////////////////////////////////////////
//
//  RWDBSchemaExprImp 
//
//  This class implements an RWDBSchema encapsulated in an expression.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBSchemaExprImp : public RWDBExprImp {
public:
    RWDBSchemaExprImp (const RWDBSchema& s);
    virtual ~RWDBSchemaExprImp();

    virtual void asString (const RWDBPhraseBook&,
                           RWDBExpr::AsStringControlFlag,
                           RWCString&,
                           RWDBStatementImp *stmt = 0) const;
    
    //virtual RWDBPhraseBook::RWDBPhraseKey getOperator() const;
                 //defined by base class
    //virtual void tableTagExpr (RWSet&) const;
    virtual RWClassID isA() const;
    //virtual RWBoolean isA(RWClassID) const;  // defined by base class
    virtual RWBoolean isEquivalent(const RWDBExprImp*) const;

private:
    const RWDBSchema theSchema_;
};

/***************************************************
 *
 * class RWDBBoundExprImp
 *
 * This class provides users with the ability to the binding ability of
 * the underlying Database.
 *
 ***************************************************/

class RWDBBoundExprImp : public RWDBExprImp {

  public:

   
    RWDBBoundExprImp(const RWDBBoundObject&, RWBoolean* nullIndicator);
    RWDBBoundExprImp(const RWDBBoundObject&);

    virtual ~RWDBBoundExprImp();

    virtual void asString(const RWDBPhraseBook&,
                          RWDBExpr::AsStringControlFlag,
                          RWCString& retString,
                          RWDBStatementImp *stmt = 0) const;

    virtual RWClassID isA() const;
    virtual RWBoolean isEquivalent(const RWDBExprImp *) const;

    RWDBBoundObject boundObject() const;
  protected:

    RWDBBoundObject      boundExpr_;

  private:

};

#endif

