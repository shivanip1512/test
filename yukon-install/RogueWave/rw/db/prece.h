#ifndef __RWDB_PRECE_H__
#define __RWDB_PRECE_H__

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
#include <rw/db/phrase.h>

//////////////////////////////////////////////////////////////////////////
//                                                                     
//  RWPrecedence 
//
//  This class sets up a method of determining the relative precedences
//  of the standard expression operators.  The table is referenced using
//  operator[] using an int.  However, it is intended that a RWDBPhraseKey
//  be used instead.
//
//  Components:
//    precedenceTable_ - an array of relative values used for comparing
//        precedence of two expression operators.  The number of elements
//        in this array is determined by the number of entries between the
//        'endExprOperatorGroup' and 'beginExprOperatorGroup' enum values
//        in the enum RWDBPhraseKey.  The precedence table is in the public
//        section of this class so that an instance of the class may be
//        statically initialized using the {} format. The default table
//        is initialized by RWDBExpr in expr.cpp
//  
//////////////////////////////////////////////////////////////////////////
//  
//  RWDBCPPPrecedence
//
//  This enum is used to initialize a default precedence table using the
//  precedence outlined in "The C++ Programming Language" Second Edition
//  by Bjarne Stroustrup pp 89-90.
//  
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDBPrecedence {
public:
    enum RWDBCPPPrecedence {
        precedenceLowest, 
        precedenceAssignment, 
        precedenceOr,
        precedenceAnd, 
        precedenceEqualGroup,
        precedenceLessGroup, 
        precedencePlusGroup, 
        precedenceMultiplyGroup,
        precedenceLikeInGroup,
        precedenceNot, 
        precedenceUnaryGroup, 
        precedenceNoOp,
        precedenceHighest
    };

    int precedenceTable_[RWDBPhraseBook::endExprOperatorGroup -
                         RWDBPhraseBook::beginExprOperatorGroup];
    int operator[](int) const;
};

#endif
