#ifndef __RWDB_SELECT_H__
#define __RWDB_SELECT_H__

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
 * RWDBSelector provides an encapsulation of the SQL "SELECT" statement.
 * The result of a select is a table. This is an awkward concept to
 * represent: the "is a" relationship does not exist for the entire life
 * of the object. Nevertheless, RWDBSelector is derived from RWDBTable
 * in order to provide convenient syntax for getting results. Apps can
 * use a reader to get rows from a selector, without having to manipulate
 * an intermediate table object.
 *
 * RWDBCompoundSelector instances result from applying set operations to
 * selectors or other compound selectors.  To implement this, both
 * selector and compoundSelector are derived from a common base class,
 * RWDBSelectorBase. This is important because not all selector methods
 * can be applied to compound selectors.
 *
 **************************************************************************/

                                                                 
#include <rw/db/defs.h>
#include <rw/db/expr.h>
#include <rw/db/phrase.h>
#include <rw/db/status.h>
#include <rw/db/cursor.h>

class RWDBBulkReader;

class RWDBExport RWDBSelectorBase
{
public:
    enum OrderByDirection { Ascending, Descending };

    // constructors, destructor, assignment
    RWDBSelectorBase            ();
    RWDBSelectorBase            (RWDBSelectorBaseImp* anImp);
    RWDBSelectorBase            (const RWDBDatabase& dbase);
    RWDBSelectorBase            (const RWDBSelectorBase& base);
    virtual ~RWDBSelectorBase   ();

    RWDBSelectorBase& operator= (const RWDBSelectorBase& base);

    // database operations
    RWDBResult execute          ();
    RWDBResult execute          (const RWDBConnection& conn);
    RWDBStatus clear();
    RWBoolean  fetchSchema      ();
    RWBoolean  fetchSchema      (const RWDBConnection& conn);

    // accessors
    RWDBDatabase      database() const;
    RWDBSchema        schema() const;
    RWDBColumn        column(size_t position) const;
    RWDBColumn        column(const RWCString& name ) const;
    RWDBColumn        column(const RWCString& name,
                             RWCString::caseCompare caseCompare) const;
    RWDBColumn        operator[](const RWCString& name) const;
    RWDBColumn        operator[](size_t           idx) const;

    // mutators
    RWDBSelectorBase& orderBy           (const RWDBColumn& column);
    RWDBSelectorBase& orderBy           (int columnNumber);
    RWDBSelectorBase& orderByDescending (const RWDBColumn& column);
    RWDBSelectorBase& orderByDescending (int columnNumber);
    RWDBSelectorBase& orderByClear      ();

    // error handling
    void                     setErrorHandler(RWDBStatus::ErrorHandler handler);
    RWDBStatus::ErrorHandler errorHandler() const;
    RWBoolean                isValid() const;
    RWBoolean                isReady() const;
    RWDBStatus               status() const;

    // producers
    RWDBCursor   cursor(RWDBCursor::CursorType type = RWDBCursor::Sequential,
                        RWDBCursor::CursorAccess access =
                                         RWDBCursor::Read) const;
    RWDBCursor   cursor(const RWDBConnection& conn,
                        RWDBCursor::CursorType type = RWDBCursor::Sequential,
                        RWDBCursor::CursorAccess access =
                                         RWDBCursor::Read) const;
    RWDBCursor   cursor(const RWDBSchema& updateCols,
                        RWDBCursor::CursorType type = RWDBCursor::Sequential,
                        RWDBCursor::CursorAccess access =
                                         RWDBCursor::Read) const;
    RWDBCursor   cursor(const RWDBSchema& updateCols,
                        const RWDBConnection& conn,
                        RWDBCursor::CursorType type = RWDBCursor::Sequential,
                        RWDBCursor::CursorAccess access =
                                         RWDBCursor::Read) const;
    RWDBReader   reader() const;
    RWDBReader   reader(const RWDBConnection& conn) const;
    RWDBBulkReader   bulkReader(const RWDBConnection& conn) const;
    // set operations
    virtual RWDBCompoundSelector union_
                                     (const RWDBSelectorBase& base) const;
    virtual RWDBCompoundSelector unionAll
                                     (const RWDBSelectorBase& base) const;
    virtual RWDBCompoundSelector intersection
                                     (const RWDBSelectorBase& base) const;
    virtual RWDBCompoundSelector difference
                                     (const RWDBSelectorBase& base) const;

    // format self as SQL string
    virtual RWCString asString       () const;

    // utilities
    RWBoolean isEquivalent(const RWDBSelectorBase& sel) const;

protected:

    RWDBSelectorBaseImp     *impl_;

private:
    virtual RWDBCompoundSelector compoundSelector
                                     (RWDBPhraseBook::RWDBPhraseKey,
                                               const RWDBSelectorBase&) const;

    friend class RWDBExport RWDBSelectorHelper;


};

// set operators 
RWDBCompoundSelector rwdbexport operator+ (const RWDBSelectorBase& base1,
                                           const RWDBSelectorBase& base2);
RWDBCompoundSelector rwdbexport operator- (const RWDBSelectorBase& base1,
                                           const RWDBSelectorBase& base2);
RWDBCompoundSelector rwdbexport operator* (const RWDBSelectorBase& base1,
                                           const RWDBSelectorBase& base2);


class RWDBExport RWDBSelector : public RWDBSelectorBase
{
public:
    // constructors, destructor, assignment
    RWDBSelector                  ();
    RWDBSelector                  (const RWDBSelector& selector);
    RWDBSelector                  (RWDBSelectorImp* imp);
    RWDBSelector& operator=       (const RWDBSelector& selector);
    virtual ~RWDBSelector         ();

    // inherited from RWDBSelectorBase
    //virtual RWCString  asString () const;

    // accessor for embedded "where clause"
    RWDBCriterion where           () const;

    // projection.   operator<< and select() are synonyms
    RWDBSelector& operator<<      (RWDBValueManip manip);
    RWDBSelector& operator<<      (const RWDBExpr& expr);
    RWDBSelector& operator<<      (const RWDBTable& aTable);
    RWDBSelector& select          (const RWDBExpr& expr);
    RWDBSelector& select          (const RWDBTable& aTable);

    // undo projection
    RWDBSelector& selectClear     ();

    // restriction
    RWDBSelector& where           (const RWDBCriterion& criterion);

    // assorted SQL concepts
    RWDBSelector& into            (const RWCString& name);
    RWDBSelector& orderBy         (const RWDBColumn& column);
    RWDBSelector& orderBy         (int columnNumber);
    RWDBSelector& orderByDescending (const RWDBColumn& column);
    RWDBSelector& orderByDescending (int columnNumber);
    RWDBSelector& orderByClear    ();
    RWDBSelector& having          (const RWDBCriterion& criterion);
    RWDBSelector& groupBy         (const RWDBColumn& column);
    RWDBSelector& groupBy         (int columnNumber);
    RWDBSelector& groupByClear    ();
    RWDBSelector& distinct        (RWBoolean logic = TRUE);

    // the "from" clause is usually implicit. use these to
    // manipulate it directly or when using outer joins
    RWDBSelector& from            (const RWCString& tableName);
    RWDBSelector& from            (const RWDBTable& table);
    RWDBSelector& from            (const RWDBJoinExpr& joinExpr);
    RWDBSelector& fromClear       ();

    // the "on" clause is used for specifying join conditions and 
    // join columns for some outer join constructs
    RWDBSelector& on              (const RWDBCriterion& criterion);
    RWDBSelector& on              (const RWDBColumn& column);
};

#endif
