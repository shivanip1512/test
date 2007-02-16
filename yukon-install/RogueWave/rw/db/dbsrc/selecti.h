#ifndef __RWDB_SELECTI_H__
#define __RWDB_SELECTI_H__

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

#include <rw/ordcltn.h>

#include <rw/db/dbref.h>
#include <rw/db/expr.h>
#include <rw/db/select.h>
#include <rw/db/cursor.h>
#include <rw/db/connect.h>
#include <rw/db/dbase.h>
#include <rw/db/schema.h>

#include <rw/db/dbsrc/stmti.h>

class RWDBBulkReaderImp;

//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDBSelectorBaseImp : public RWDBReference, public RWDBStatus
{
  public:
    RWDBSelectorBaseImp ( const RWDBStatus& status );
    RWDBSelectorBaseImp ( const RWDBStatus& status, const RWDBDatabase& db );
    virtual ~RWDBSelectorBaseImp();

    virtual RWDBCompoundSelectorImp * compoundSelectorImp(
                                           const RWDBSelectorBase&,
                                           RWDBPhraseBook::RWDBPhraseKey,
                                           const RWDBSelectorBase&);

    virtual RWCString  asString (const RWDBPhraseBook&,
                                 RWDBStatementImp *stmt = 0) const;

      // accessors
    virtual RWDBDatabase            database() const;
    virtual const RWDBPhraseBook&   phraseBook() const;
    virtual RWDBSchema              schema() const;

    virtual RWDBColumn              column(size_t position) const;
    virtual RWDBColumn              column(const RWCString& name) const;
    virtual RWDBColumn              column(const RWCString& name,
                                      RWCString::caseCompare caseCompare) const;

    virtual RWDBResult          execute (const RWDBConnection&);
    virtual void                cancel(const RWDBConnection&, RWDBStatus&);
    virtual RWDBResult          parse(const RWDBConnection&, RWDBStatus&);
    virtual RWDBResult          bind(const RWDBConnection&, RWDBStatus&);
    virtual RWDBResult          perform(const RWDBConnection&, RWDBStatus&);
    virtual RWDBStatus          clear();
    virtual RWDBStatementImp *  statement() const;
            RWDBBoundObjectList&         boundObjects();
    virtual RWBoolean  fetchSchema(const RWDBConnection& connection);

    virtual void orderBy (const RWDBColumn& column,
                              RWDBSelectorBase::OrderByDirection direction);
    virtual void orderBy (int columnNumber,
                              RWDBSelectorBase::OrderByDirection direction);
    virtual void orderByClear ();

//    virtual RWDBReaderImp * readerImp(const RWDBTable&, const RWDBConnection&);
        virtual RWDBBulkReaderImp* bulkReaderImp( const RWDBConnection& conn);

    virtual RWDBReaderImp * readerImp(const RWDBConnection&);

    virtual RWDBCursorImp * cursorImp(const RWDBConnection&    connection,
                              RWDBCursor::CursorType   type,
                              RWDBCursor::CursorAccess access);
    virtual RWDBCursorImp * cursorImp(const RWDBSchema&        updateCols,
                              const RWDBConnection&    connection,
                              RWDBCursor::CursorType   type,
                              RWDBCursor::CursorAccess access);

  protected:
    RWOrdered       orderBy_;
    RWCString       sql_;
    RWBoolean       changed_;
    RWDBSchema      schema_;
    
    RWDBStatement   statement_;
    RWDBDatabase    database_;

  private:
    //not implemented
    RWDBSelectorBaseImp(const RWDBSelectorBaseImp&);
    RWDBSelectorBaseImp& operator=(const RWDBSelectorBaseImp&);
  protected:
          RWDBConnection    conn_;

};

//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
class RWDBSelectorHelper {

  public:

    static RWDBSelectorBaseImp *getImp(const RWDBSelectorBase&);
    
};

//////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBSelectorImp : public RWDBSelectorBaseImp
{
  public:
    RWDBSelectorImp ( const RWDBStatus& status );
    RWDBSelectorImp ( const RWDBStatus& status, const RWDBDatabase& db,
                      const RWDBCriterion& crit );
    virtual ~RWDBSelectorImp();

    virtual RWCString asString (const RWDBPhraseBook&,
                          RWDBStatementImp *stmt = 0) const;
    virtual RWDBCriterion where() const;

    virtual RWDBStatus clear();

    virtual void select (const RWDBExpr& expr);
    virtual void selectClear ();
    virtual void where (const RWDBCriterion& criterion);
    virtual void into (const RWCString& name);
    virtual void having (const RWDBCriterion& criterion);
    virtual void groupBy (const RWDBColumn& column);
    virtual void groupBy (int columnNumber);
    virtual void groupByClear ();
    virtual void distinct (RWBoolean = TRUE);
//    virtual void from (const RWDBExpr&);
    virtual void from (const RWCString&);
    virtual void from (const RWDBJoinExpr&);
    virtual void fromClear ();

    virtual void on( const RWDBColumn& joinColumn );
    virtual void on( const RWDBCriterion& joinCriterion );

  protected:
    RWOrdered       selectList_;
    RWOrdered       fromList_;

    // Controls outer join syntax
    RWBoolean       outerJoin_;
    RWOrdered       joinColumnList_;
    RWDBCriterion   joinCriterion_;

    RWDBCriterion   where_;

    RWCString       into_;
    RWOrdered       groupBy_;
    RWDBCriterion   having_;
    RWBoolean       distinct_;
    
  private:
    // Not Implemented
    RWDBSelectorImp(const RWDBSelectorImp&);
    RWDBSelectorImp& operator=(const RWDBSelectorImp&);

};

#endif


