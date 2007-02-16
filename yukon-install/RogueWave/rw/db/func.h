#ifndef __RWDB_FUNC_H__
#define __RWDB_FUNC_H__

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
#include <rw/db/expr.h>


//////////////////////////////////////////////////////////////////////////
//  
//  RWDBExprFormDefinition 
//
//  This class allows a programmer to create an object that will translate
//  into an RWDBExpr and then into an SQL string.  Each instance will allow
//  up to four expressions to be embedded into the SQL statement.
//
//  The operator() variants are used to convert instances of
//  RWDBExprFormDefinition into RWDBExpr instances.
//
//  Components:
//    format_ - an RWCString to be used as a template for the final
//              substitution of expressions.
//
//              The format_ string may contain markers of the form '%n'
//              (n can be 0..3, inclusive).  Each instance of '%0' is
//              eventually replaced by the first positional parameter,
//              %1, by the second, and so on.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBExprFormDefinition {
public:
    RWDBExprFormDefinition();
    RWDBExprFormDefinition (const RWCString& s);
    virtual ~RWDBExprFormDefinition();

    virtual RWDBExpr operator()();
    virtual RWDBExpr operator()(const RWDBExpr& x1);
    virtual RWDBExpr operator()(const RWDBExpr& x1, const RWDBExpr& x2);
    virtual RWDBExpr operator()(const RWDBExpr& x1, const RWDBExpr& x2,
                                const RWDBExpr& x3);
    virtual RWDBExpr operator()(const RWDBExpr& x1, const RWDBExpr& x2,
                                const RWDBExpr& x3, const RWDBExpr& x4);

protected:
    RWCString format_;

    RWDBExpr buildExpression(const RWDBExpr* = 0, const RWDBExpr* = 0,
                             const RWDBExpr* = 0, const RWDBExpr* = 0);
};

//////////////////////////////////////////////////////////////////////////
//  
//  RWDBExprFuncDef<n> 
//
//  These classes allow the programmer to make an objects which represent any
//  arbitrary aggregate or other database specific-function to be used in an
//  expression.  The expressions these objects make represent a functions
//  of arity <n>.  (n may be 0..4, inclusive).
//
//  operator() is used to convert to RWDBExpr.
//  
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBExprFuncDef0 : private RWDBExprFormDefinition {
public:
    RWDBExprFuncDef0(const RWCString& s);
    virtual ~RWDBExprFuncDef0();
    virtual RWDBExpr operator()();

private:
    virtual RWDBExpr operator()(const RWDBExpr&);
    virtual RWDBExpr operator()(const RWDBExpr&, const RWDBExpr&);
    virtual RWDBExpr operator()(const RWDBExpr&, const RWDBExpr&,
                                const RWDBExpr&);
    virtual RWDBExpr operator()(const RWDBExpr&, const RWDBExpr&,
                                const RWDBExpr&, const RWDBExpr&);
};

class RWDBExport RWDBExprFuncDef1 : private RWDBExprFormDefinition {
public:
    RWDBExprFuncDef1(const RWCString& s);
    virtual ~RWDBExprFuncDef1();
    virtual RWDBExpr operator()(const RWDBExpr& x1);

private:
    virtual RWDBExpr operator()();
    virtual RWDBExpr operator()(const RWDBExpr&, const RWDBExpr&);
    virtual RWDBExpr operator()(const RWDBExpr&, const RWDBExpr&,
                                const RWDBExpr&);
    virtual RWDBExpr operator()(const RWDBExpr&, const RWDBExpr&,
                                const RWDBExpr&, const RWDBExpr&);
};

class RWDBExport RWDBExprFuncDef2 : private RWDBExprFormDefinition {
public:
    RWDBExprFuncDef2(const RWCString& s);
    virtual ~RWDBExprFuncDef2();
    virtual RWDBExpr operator()(const RWDBExpr& x1, const RWDBExpr& x2);

private:
    virtual RWDBExpr operator()();
    virtual RWDBExpr operator()(const RWDBExpr&);
    virtual RWDBExpr operator()(const RWDBExpr&, const RWDBExpr&,
                                const RWDBExpr&);
    virtual RWDBExpr operator()(const RWDBExpr&, const RWDBExpr&,
                                const RWDBExpr&, const RWDBExpr&);
};

class RWDBExport RWDBExprFuncDef3 : private RWDBExprFormDefinition {
public:
    RWDBExprFuncDef3(const RWCString& s);
    virtual ~RWDBExprFuncDef3();
    virtual RWDBExpr operator()(const RWDBExpr& x1, const RWDBExpr& x2,
                                const RWDBExpr& x3);

private:
    virtual RWDBExpr operator()();
    virtual RWDBExpr operator()(const RWDBExpr&);
    virtual RWDBExpr operator()(const RWDBExpr&, const RWDBExpr&);
    virtual RWDBExpr operator()(const RWDBExpr&, const RWDBExpr&,
                                const RWDBExpr&, const RWDBExpr&);
};

class RWDBExport RWDBExprFuncDef4 : private RWDBExprFormDefinition {
public:
    RWDBExprFuncDef4(const RWCString& s);
    virtual ~RWDBExprFuncDef4();
    virtual RWDBExpr operator()(const RWDBExpr& x1, const RWDBExpr& x2,
                                const RWDBExpr& x3, const RWDBExpr& x4);

private:
    virtual RWDBExpr operator()();
    virtual RWDBExpr operator()(const RWDBExpr&);
    virtual RWDBExpr operator()(const RWDBExpr&, const RWDBExpr&);
    virtual RWDBExpr operator()(const RWDBExpr&, const RWDBExpr&,
                                const RWDBExpr&);
};


//////////////////////////////////////////////////////////////////////////
//
//  RWDBPhraseExprDefinition 
//
//  This class allows a programmer to create an object that will translate
//  into an RWDBExpr and then into an SQL string.  Each instance will allow
//  up to four expressions to be embedded into the SQL statement. It is
//  similar to RWDBExprFormDefinition, however, an index into an
//  RWDBPhraseBook is stored, rather than a format string.  The format
//  string is eventually fetched from the phraseBook.
//
//  The operator() variants are used to convert instances of
//  RWDBPhraseExprDefinition into RWDBExpr instances.
//
//  Components:
//    aPhraseKey_ - an RWDBPhraseKey to be used to get a template for the
//                  final substitution of expressions. The template may
//                  contain markers of the form '%n' (n can be 0..3,
//                  inclusive).  Each instance of '%0' is eventually replaced
//                  by the first positional parameter, %1, by the second,
//                  and so on.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBPhraseExprDefinition {
public:
    RWDBPhraseExprDefinition(RWDBPhraseBook::RWDBPhraseKey pk);
    virtual ~RWDBPhraseExprDefinition();
    virtual RWDBExpr operator()();
    virtual RWDBExpr operator()(const RWDBExpr& x0);
    virtual RWDBExpr operator()(const RWDBExpr& x0, const RWDBExpr& x1);
    virtual RWDBExpr operator()(const RWDBExpr& x0, const RWDBExpr& x1,
                                const RWDBExpr& x2);
    virtual RWDBExpr operator()(const RWDBExpr& x0, const RWDBExpr& x1,
                                const RWDBExpr& x2, const RWDBExpr& x3);

protected:
    RWDBPhraseBook::RWDBPhraseKey aPhraseKey_;

    RWDBExpr buildExpression (const RWDBExpr* x0 = 0, const RWDBExpr* x1 = 0,
                              const RWDBExpr* x2 = 0, const RWDBExpr* x3 = 0);
};

//////////////////////////////////////////////////////////////////////////
//  
//  RWDBCritFormDefinition 
//  
//  This class allows a programmer to create an object that will translate
//  into an RWDBExpr and then into an SQL string.  Each instance will allow
//  up to four expressions to be embedded into the SQL statement. It is
//  similar to RWDBExprFormDefinition. However, the instances of this
//  class are converted into RWDBCriterion instances, rather than
//  RWDBExpr instances.
//
//  The operator() variants are used to convert instances of
//  RWDBCritFormDefinition into RWDBCriterion instances.
//
//  Components:
//    format_ - an RWCString to be used as a template for the final
//              substitution of expressions.  The template may
//              contain markers of the form '%n' (n can be 0..3,
//              inclusive).  Each instance of '%0' is eventually replaced
//              by the first positional parameter, %1, by the second,
//              and so on.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBCritFormDefinition {
public:
    RWDBCritFormDefinition();
    RWDBCritFormDefinition(const RWCString& s);
    virtual ~RWDBCritFormDefinition();

    virtual RWDBCriterion operator()();
    virtual RWDBCriterion operator()(const RWDBExpr& x1);
    virtual RWDBCriterion operator()(const RWDBExpr& x1, const RWDBExpr& x2);
    virtual RWDBCriterion operator()(const RWDBExpr& x1, const RWDBExpr& x2,
                                     const RWDBExpr& x3);
    virtual RWDBCriterion operator()(const RWDBExpr& x1, const RWDBExpr& x2,
                                     const RWDBExpr& x3, const RWDBExpr& x4);

protected:
    RWCString format_;

    RWDBCriterion buildExpression (const RWDBExpr* = 0, const RWDBExpr* = 0,
                                   const RWDBExpr* = 0, const RWDBExpr* = 0);
};

//////////////////////////////////////////////////////////////////////////
//  
//  RWDBCritFuncDef<n> 
//
//  These classes allow the programmer to make an objects which represent any
//  arbitrary aggregate or other database specific-function to be used in an
//  expression.  The expressions these objects make represent a functions
//  of arity <n>.  (n may be 0..4, inclusive). These classes are the same
//  as the RWDBExprFuncDef<n> classes, except that they are eventually
//  converted into RWDBCriterion instances, rather than RWDBExpr instances.
//
//  operator() is used to convert to RWDBCriterion.
//  
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBCritFuncDef0 : private RWDBCritFormDefinition {
public:
    RWDBCritFuncDef0(const RWCString& s);
    virtual ~RWDBCritFuncDef0();
    virtual RWDBCriterion operator()();

private:
    virtual RWDBCriterion operator()(const RWDBExpr&);
    virtual RWDBCriterion operator()(const RWDBExpr&, const RWDBExpr&);
    virtual RWDBCriterion operator()(const RWDBExpr&, const RWDBExpr&,
                                     const RWDBExpr&);
    virtual RWDBCriterion operator()(const RWDBExpr&, const RWDBExpr&,
                                     const RWDBExpr&, const RWDBExpr&);
};

class RWDBExport RWDBCritFuncDef1 : private RWDBCritFormDefinition {
public:
    RWDBCritFuncDef1(const RWCString& s);
    virtual ~RWDBCritFuncDef1();
    virtual RWDBCriterion operator()(const RWDBExpr& x1);

private:
    virtual RWDBCriterion operator()();
    virtual RWDBCriterion operator()(const RWDBExpr&, const RWDBExpr&);
    virtual RWDBCriterion operator()(const RWDBExpr&, const RWDBExpr&,
                                     const RWDBExpr&);
    virtual RWDBCriterion operator()(const RWDBExpr&, const RWDBExpr&,
                                     const RWDBExpr&, const RWDBExpr&);
};

class RWDBExport RWDBCritFuncDef2 : private RWDBCritFormDefinition {
public:
    RWDBCritFuncDef2 (const RWCString& s);
    virtual ~RWDBCritFuncDef2();
    virtual RWDBCriterion operator()(const RWDBExpr& x1, const RWDBExpr& x2);

private:
    virtual RWDBCriterion operator()();
    virtual RWDBCriterion operator()(const RWDBExpr&);
    virtual RWDBCriterion operator()(const RWDBExpr&, const RWDBExpr&,
                                     const RWDBExpr&);
    virtual RWDBCriterion operator()(const RWDBExpr&, const RWDBExpr&,
                                     const RWDBExpr&, const RWDBExpr&);
};

class RWDBExport RWDBCritFuncDef3 : private RWDBCritFormDefinition {
public:
    RWDBCritFuncDef3 (const RWCString& s);
    virtual ~RWDBCritFuncDef3();
    virtual RWDBCriterion operator()(const RWDBExpr& x1, const RWDBExpr& x2,
                                     const RWDBExpr& x3);

private:
    virtual RWDBCriterion operator()();
    virtual RWDBCriterion operator()(const RWDBExpr&);
    virtual RWDBCriterion operator()(const RWDBExpr&, const RWDBExpr&);
    virtual RWDBCriterion operator()(const RWDBExpr&, const RWDBExpr&,
                                     const RWDBExpr&, const RWDBExpr&);
};

class RWDBExport RWDBCritFuncDef4 : private RWDBCritFormDefinition {
public:
    RWDBCritFuncDef4(const RWCString& s);
    virtual ~RWDBCritFuncDef4();
    virtual RWDBCriterion operator()(const RWDBExpr&, const RWDBExpr&,
                                     const RWDBExpr&, const RWDBExpr&);

private:
    virtual RWDBCriterion operator()();
    virtual RWDBCriterion operator()(const RWDBExpr&);
    virtual RWDBCriterion operator()(const RWDBExpr&, const RWDBExpr&);
    virtual RWDBCriterion operator()(const RWDBExpr& x1, const RWDBExpr& x2,
                                     const RWDBExpr& x3);
};

//////////////////////////////////////////////////////////////////////////
//
//  RWDBPhraseCritDefinition 
//
//  This class allows a programmer to create an object that will translate
//  into an RWDBCriterion and then into an SQL string.  Each instance will allow
//  up to four expressions to be embedded into the SQL statement. It is
//  similar to RWDBCritFormDefinition, however, an index into an
//  RWDBPhraseBook is stored, rather than a format string.  The format
//  string is eventually fetched from the phraseBook.
//
//  The operator() variants are used to convert instances of
//  RWDBPhraseCritDefinition into RWDBCriterion instances.
//
//  Components:
//    aPhraseKey_ - an RWDBPhraseKey to be used to get a template for the
//                  final substitution of expressions. The template may
//                  contain markers of the form '%n' (n can be 0..3,
//                  inclusive).  Each instance of '%0' is eventually replaced
//                  by the first positional parameter, %1, by the second,
//                  and so on.
//
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBPhraseCritDefinition {
public:
    RWDBPhraseCritDefinition(RWDBPhraseBook::RWDBPhraseKey pk);
    virtual ~RWDBPhraseCritDefinition();
    virtual RWDBCriterion operator()();
    virtual RWDBCriterion operator()(const RWDBExpr& x0);
    virtual RWDBCriterion operator()(const RWDBExpr& x0, const RWDBExpr& x1);
    virtual RWDBCriterion operator()(const RWDBExpr& x0, const RWDBExpr& x1,
                                     const RWDBExpr& x2);
    virtual RWDBCriterion operator()(const RWDBExpr& x0, const RWDBExpr& x1,
                                     const RWDBExpr& x2, const RWDBExpr& x3);

protected:
    RWDBPhraseBook::RWDBPhraseKey aPhraseKey_;

    RWDBCriterion buildExpression (const RWDBExpr* x0 = 0,
                                   const RWDBExpr* x1 = 0,
                                   const RWDBExpr* x2 = 0,
                                   const RWDBExpr* x3 = 0);
};

#endif
