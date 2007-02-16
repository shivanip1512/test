#ifndef __RWDB_ODBSEL_H__
#define __RWDB_ODBSEL_H__

/***************************************************************************
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
 ***************************************************************************
 *
 * ODBC access library implementation of RWDBSelectorImp and
 *                                       RWDBCompoundSelectorImp
 *
 **************************************************************************/

#include <rw/db/odbcsrc/odbdefs.h>

#include <rw/db/dbsrc/comseli.h>
#include <rw/db/dbsrc/selecti.h>
class RWDBBulkReaderImp;

class RWDBAccessExport RWDBODBCLibSelectorImp : public RWDBSelectorImp {
public:
    RWDBODBCLibSelectorImp     ( const RWDBStatus& status,
                                 const RWDBDatabase&     dbase,
                                 const RWDBCriterion&    criterion);
    ~RWDBODBCLibSelectorImp    ();
    virtual RWCString          asString (const RWDBPhraseBook&,
                                         RWDBStatementImp *stmt = 0) const;
    const RWDBPhraseBook&      phraseBook() const;
    RWDBBulkReaderImp*         bulkReaderImp( const RWDBConnection& conn);
    RWDBCursorImp *            cursorImp( const RWDBConnection&    connection,
                                       RWDBCursor::CursorType   type,
                                       RWDBCursor::CursorAccess access );
    RWDBCursorImp *            cursorImp( const RWDBSchema&,
                                       const RWDBConnection&    connection,
                                       RWDBCursor::CursorType   type,
                                       RWDBCursor::CursorAccess access );
    RWDBCompoundSelectorImp * compoundSelectorImp( const RWDBSelectorBase& left,
                                              RWDBPhraseBook::RWDBPhraseKey op,
                                              const RWDBSelectorBase& right );

private:
// Not Implemented:
    RWDBODBCLibSelectorImp     ( const RWDBODBCLibSelectorImp& );
    RWDBODBCLibSelectorImp&    operator=( const RWDBODBCLibSelectorImp& );

};

class RWDBAccessExport RWDBODBCLibCompoundSelectorImp : public RWDBCompoundSelectorImp
{
public:
    RWDBODBCLibCompoundSelectorImp     ( const RWDBStatus& status,
                                         const RWDBDatabase& db,
                                         const RWDBSelectorBase& left,
                                         RWDBPhraseBook::RWDBPhraseKey op,
                                         const RWDBSelectorBase& right);

    ~RWDBODBCLibCompoundSelectorImp    ();
    

    RWDBCursorImp *            cursorImp( const RWDBConnection&    connection,
                                       RWDBCursor::CursorType   type,
                                       RWDBCursor::CursorAccess access);
    RWDBCursorImp *            cursorImp( const RWDBSchema&,
                                       const RWDBConnection&    connection,
                                       RWDBCursor::CursorType   type,
                                       RWDBCursor::CursorAccess access);

    const RWDBPhraseBook&      phraseBook() const;

//    RWDBResult                 execute( const RWDBConnection& conn);

    RWDBCompoundSelectorImp * compoundSelectorImp( const RWDBSelectorBase& left,
                                              RWDBPhraseBook::RWDBPhraseKey op,
                                              const RWDBSelectorBase& right );

private:

      // Not Implemented:
    RWDBODBCLibCompoundSelectorImp(const RWDBODBCLibCompoundSelectorImp&);
    RWDBODBCLibCompoundSelectorImp&
      operator=(const RWDBODBCLibCompoundSelectorImp& );

};


#endif


