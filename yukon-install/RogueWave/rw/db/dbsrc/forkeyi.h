#ifndef __RWDB_ForeignKeyI_H__
#define __RWDB_ForeignKeyI_H__

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
 * Definition of class RWDBForeignKeyImp, the reference-counted
 * implementation of class RWDBForeignKey.
 *
 * Note: this is NOT a base class for database-specific variants.
 *
 **************************************************************************/

#include <rw/db/forkey.h>

#include <rw/db/dbsrc/schemai.h>


//////////////////////////////////////////////////////////////////////////
//                                                                      
// RWDBForeignKeyImp
//                                                                     
//////////////////////////////////////////////////////////////////////////
class RWDBExport RWDBForeignKeyImp : public RWDBSchemaImp 
{
  public:
    RWDBForeignKeyImp( const RWDBStatus& );
    RWDBForeignKeyImp( const RWDBStatus&,
                       const RWCString& refName, 
                             RWDBForeignKey::Constraint updateConstraint,
                             RWDBForeignKey::Constraint deleteConstraint ); 

    virtual ~RWDBForeignKeyImp();

    RWCString asString(const RWDBPhraseBook& pb) const;
    RWCString asString(const RWCString& delimiter,
                       const RWCString& whiteSpace) const;

    RWCString                deleteConstraintAsString(const RWDBPhraseBook& pb) const;
    RWCString                updateConstraintAsString(const RWDBPhraseBook& pb) const;

    RWCString referenceName() const;
    RWDBForeignKey::Constraint updateConstraint() const;
    RWDBForeignKey::Constraint deleteConstraint() const;


  private:
    RWCString                  referenceName_;
    RWDBForeignKey::Constraint updateConstraint_;
    RWDBForeignKey::Constraint deleteConstraint_;

// not implemented:
    RWDBForeignKeyImp( const RWDBForeignKeyImp& impl );
    RWDBForeignKeyImp& operator=( const RWDBForeignKeyImp& impl );
};

#endif

