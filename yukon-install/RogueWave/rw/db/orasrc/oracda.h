/*************************************************************************
*  $Id$
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
***************************************************************************/


#ifndef __RWDB__ORACDA_H__
#define __RWDB__ORACDA_H__

// to avoid compiler warning on declare(RWGOrderedVector, RWDBOracleCDA)
#if defined(__BORLANDC__)
# if __BORLANDC__ >= 0x500
#   ifndef RW_NO_INLINE_CLASS_BY_VALUE
#     define RW_NO_INLINE_CLASS_BY_VALUE
#   endif
# endif
#endif

#include <rw/db/orasrc/oradefs.h>
#include <rw/db/dbref.h>

//
// RWDBOracleCDAImp provides a reference counting wrapper for a Cda_Def object.
//
class RWDBOracleConnectionImp;

class RWDBOracleCDAImp :  public RWDBReference {

public:

    //
    //   Constructors & Destructors
    //

    // This effectively creates a "null cda".
//    RWDBOracleCDAImp::RWDBOracleCDAImp( RWBoolean forCursorVariable = FALSE);
    RWDBOracleCDAImp( RWBoolean forCursorVariable = FALSE);

        // connImpl is not a const& because open() which is called by this
        // ctor will want to change the state of the connection.
        // When dummy is TRUE, no Cda_Def object actually gets created.
        // Basically, you get a wrapper that wraps nothing.
    RWDBOracleCDAImp( RWDBStatus& status, 
                      RWDBOracleConnectionImp& connImpl, 
                      RWBoolean dummy = FALSE, 
                      RWBoolean forCursorVariable=FALSE );

    RWDBOracleCDAImp( const RWDBOracleCDAImp& cda );

   ~RWDBOracleCDAImp();

    //
    //   Operators
    //

    const RWDBOracleCDAImp& operator=( const RWDBOracleCDAImp& cda );

    //
    //   Accessors
    //

    Cda_Def*                 data()       const { return cda_; }
    RWDBOracleConnectionImp* connection() const { return connImpl_; }

    RWBoolean forCursorVariable(); 

private:

    void open();
    void nukeCda();

    RWBoolean forCursorVariable_;
    //
    //   Data
    //

    Cda_Def*                 cda_;
    RWDBOracleConnectionImp* connImpl_;
    RWDBConnection           conn_;
    RWDBStatus               callerStatus_;

        // conn_ is here only to make sure that ConnectionImp is
        // reference counted and consequently stays around throughout
        // the lifetime of this cda. Note therefore that it is not
        // necessary to delete connImpl_ in the dtor as it will
        // delete itself when it is no longer being referenced.

};


//
// RWDBOracleCDA provides a reference counting wrapper for a Cda_Def object.
//

class RWDBAccessExport RWDBOracleCDA {

public:

    //
    //   Constructors & Destructors
    //

        // This effectively creates a "null cda".
    RWDBOracleCDA( RWBoolean forCursorVariable=FALSE);

    RWDBOracleCDA( RWDBStatus& status, 
                   RWDBOracleConnectionImp& connImpl, 
                   RWBoolean dummy = FALSE, 
                   RWBoolean forCursorVariable=FALSE );
        // connImpl is not a const& because open() which is called by this
        // ctor will want to change the state of the connection.
        // When dummy is TRUE, no Cda_Def object actually gets created.
        // Basically, you get a wrapper that wraps nothing.

    RWDBOracleCDA( const RWDBOracleCDA& cda );

   ~RWDBOracleCDA();

    //
    //   Operators
    //

    const RWDBOracleCDA& operator=( const RWDBOracleCDA& cda );
    RWBoolean operator==( const RWDBOracleCDA& aCda ) const;

    // This was added to fix stdbuild compile problem on AIX.
    // This operator itself is not used though it is collected 
    RWBoolean operator<( const RWDBOracleCDA& aCda ) const { return TRUE; }

    //
    //   Accessors
    //

    Cda_Def*                 data()       const { return impl_->data(); }
    RWDBOracleConnectionImp* connection() const { return impl_->connection(); }

    RWBoolean forCursorVariable(); 

private:

    void open();

    RWDBOracleCDAImp* impl_;

};

# if defined(__DECCXX)
    #include <rw/gordvec.h>

    declare(RWGVector, RWDBOracleCDA)
    declare(RWGOrderedVector, RWDBOracleCDA)

    typedef RWGOrderedVector(RWDBOracleCDA) RWDBOracleCDAQueue;
#else
    #include <rw/tvordvec.h>
    typedef RWTValOrderedVector<RWDBOracleCDA> RWDBOracleCDAQueue;
#endif

#endif

