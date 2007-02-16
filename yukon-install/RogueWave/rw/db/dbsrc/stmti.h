#ifndef __RWDB_STMTI_H__
#define __RWDB_STMTI_H__

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
 * Definition of RWDBStatementImp
 *
 **************************************************************************/

#ifdef _MSC_VER
#pragma warning( disable : 4251)
#endif

#include <rw/gslist.h>
#ifndef RW_NO_WSTR
#include <rw/wstring.h>
#endif

#include <rw/db/dbref.h>
#include <rw/db/status.h>

#include <rw/db/dbsrc/stmt.h>

declare(RWGSlist, RWDBBoundObject)
typedef RWGSlist(RWDBBoundObject) RWDBBoundObjectList;


//////////////////////////////////////////////////////////////////////////
//
// class RWDBStatementImp
//
//////////////////////////////////////////////////////////////////////////

class RWDBExport RWDBStatementImp : public RWDBStatus,
                                    public RWDBReference {

public:

    RWDBStatementImp();
    virtual ~RWDBStatementImp();

      // Statement stuff
    virtual void             cancel( const RWDBConnection&, RWDBStatus&,
                                     RWBoolean changed = 0 );
    virtual RWDBResultImp *  parse( const RWCString&, const RWDBConnection&, 
                                    RWDBStatus& );
    virtual RWDBResultImp *  bind( const RWDBConnection&, RWDBStatus& );
    virtual RWDBResultImp *  perform( const RWDBConnection&, RWDBStatus& );
    virtual void             clear( const RWDBConnection&, RWDBStatus& );

      // Binding stuff
    virtual RWCString   addBoundObject(const RWCString&,
                                       void *,
                                       RWDBBoundObject::PtrType,
                                       size_t len, int nativeType=-1);
    virtual RWCString   addBoundObject(const RWDBBoundObject&);
    
    virtual void        doFinalBind( const RWDBConnection&, RWDBStatus& );

    virtual void        clearBoundObjects(void);
    virtual size_t      entries() const;
    virtual RWBoolean   isNull(size_t pos) const;

      // generates a new placeholder using the currentPlaceHolder_ as
      // a unique number.
    virtual RWCString   newPlaceHolder(void);

    virtual RWBoolean   needsPlaceHolder(const RWDBValue&) const;
    virtual RWBoolean   needsPlaceHolder(RWDBValue::ValueType,
                                         void *data) const;
    RWDBBoundObjectList&         boundObjects();
protected:

      // implementations are required to override this behavior
    virtual void doBind( RWDBStatus&, const RWCString&,
                         char *, size_t pos);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         short *, size_t pos);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         int *, size_t pos);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         long *, size_t pos);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         float *, size_t pos);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         double *, size_t pos);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWCString *, size_t pos);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDBMBString *, size_t pos);
#ifndef RW_NO_WSTR
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWWString *, size_t pos);
#endif
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDecimalPortable *, size_t pos);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDate *, size_t pos);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDBDateTime *, size_t pos);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDBDuration *, size_t pos);
    virtual void doBind( RWDBStatus&, const RWCString&,
                         RWDBBlob *, size_t pos);

      // maintains a list of Bound objects
//    RWTPtrSlist<RWDBBoundObject>   boundObjects_;
    RWDBBoundObjectList         boundObjects_;


    unsigned long  currentPlaceHolder_;
    
private:
// not implemented
    RWDBStatementImp( const RWDBStatementImp& );
    RWDBStatementImp& operator=( const RWDBStatementImp& );
};


#endif
