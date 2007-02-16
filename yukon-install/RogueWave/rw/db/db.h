#ifndef __RWDB_DB_H__
#define __RWDB_DB_H__

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


//////////////////////////////////////////////////////////////////////////
//                                                                      
// DBtools.h++ applications can #include specific header files of interest,
// or, instead, they may simply #include this header.  The latter strategy
// has in impact on compile-time efficiency, but is considerably simpler.
//                                                                     
//////////////////////////////////////////////////////////////////////////

#include <rw/db/defs.h>
#include <rw/db/bkread.h>
#include <rw/db/blob.h>
#include <rw/db/collect.h>
#include <rw/db/column.h>
#include <rw/db/comsel.h>
#include <rw/db/connect.h>
#include <rw/db/cursor.h>
#include <rw/db/datetime.h>
#include <rw/db/dbase.h>
#include <rw/db/dberr.h>
#include <rw/db/dbmgr.h>
#include <rw/db/decport.h>
#include <rw/db/deleter.h>
#include <rw/db/duration.h>
#include <rw/db/envhandl.h>
#include <rw/db/expr.h>
#include <rw/db/forkey.h>
#include <rw/db/func.h>
#include <rw/db/future.h>
#include <rw/db/inserter.h>
#include <rw/db/memtable.h>
#include <rw/db/nullind.h>
#include <rw/db/phrase.h>
#include <rw/db/prece.h>
#include <rw/db/reader.h>
#include <rw/db/result.h>
#include <rw/db/row.h>
#include <rw/db/schema.h>
#include <rw/db/select.h>
#include <rw/db/status.h>
#include <rw/db/stored.h>
#include <rw/db/syshandl.h>
#include <rw/db/table.h>
#include <rw/db/tmtbase.h>
#include <rw/db/tracer.h>
#include <rw/db/updater.h>
#include <rw/db/value.h>

#endif
