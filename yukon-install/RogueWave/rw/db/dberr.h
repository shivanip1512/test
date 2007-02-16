#ifndef __RWDB_DBERR_H__
#define __RWDB_DBERR_H__

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
 * DBtools.h++ message catalog. This file could be named more appropriately
 * msg.h or some such, but dberr.h is retained for historical reasons.
 *
 **************************************************************************/


#include <rw/db/defs.h>

#include <rw/message.h>

#ifdef RWDLL
#  define RWDB_OK               RWDB_OK()
#  define RWDB_NOMEM            RWDB_NOMEM()
#  define RWDB_TYPECONVERSION   RWDB_TYPECONVERSION()
#  define RWDB_DBNOTFOUND       RWDB_DBNOTFOUND()
#  define RWDB_NOTINITIALIZED   RWDB_NOTINITIALIZED()
#  define RWDB_SERVERERROR      RWDB_SERVERERROR()
#  define RWDB_SERVERMESSAGE    RWDB_SERVERMESSAGE()
#  define RWDB_VENDORLIB        RWDB_VENDORLIB()
#  define RWDB_NOTCONNECTED     RWDB_NOTCONNECTED()
#  define RWDB_ENDOFFETCH       RWDB_ENDOFFETCH()
#  define RWDB_INVALIDUSAGE     RWDB_INVALIDUSAGE()
#  define RWDB_COLUMNNOTFOUND   RWDB_COLUMNNOTFOUND()
#  define RWDB_INVALIDPOSITION  RWDB_INVALIDPOSITION()
#  define RWDB_NOTSUPPORTED     RWDB_NOTSUPPORTED()
#  define RWDB_NULLREFERENCE    RWDB_NULLREFERENCE()
#  define RWDB_NOTFOUND         RWDB_NOTFOUND()
#  define RWDB_MISSING          RWDB_MISSING()
#  define RWDB_NOMULTIREADERS   RWDB_NOMULTIREADERS()
#  define RWDB_NODELETER        RWDB_NODELETER()
#  define RWDB_NOINSERTER       RWDB_NOINSERTER()
#  define RWDB_NOUPDATER        RWDB_NOUPDATER()
#  define RWDB_NOREADER         RWDB_NOREADER()
#  define RWDB_NOINDEX          RWDB_NOINDEX()
#  define RWDB_NODROP           RWDB_NODROP()
#  define RWDB_WRONGCONN        RWDB_WRONGCONN()
#  define RWDB_NOPRIVILEGE      RWDB_NOPRIVILEGE()
#  define RWDB_NOCURSOR         RWDB_NOCURSOR()
#  define RWDB_CANTOPEN         RWDB_CANTOPEN()
#  define RWDB_NOTREADY         RWDB_NOTREADY()
#endif

extern const RWMsgId rwdbexport RWDB_OK;
extern const RWMsgId rwdbexport RWDB_NOMEM;
extern const RWMsgId rwdbexport RWDB_TYPECONVERSION;
extern const RWMsgId rwdbexport RWDB_DBNOTFOUND;
extern const RWMsgId rwdbexport RWDB_NOTINITIALIZED;
extern const RWMsgId rwdbexport RWDB_SERVERERROR;
extern const RWMsgId rwdbexport RWDB_SERVERMESSAGE;
extern const RWMsgId rwdbexport RWDB_VENDORLIB;
extern const RWMsgId rwdbexport RWDB_NOTCONNECTED;
extern const RWMsgId rwdbexport RWDB_ENDOFFETCH;
extern const RWMsgId rwdbexport RWDB_INVALIDUSAGE;
extern const RWMsgId rwdbexport RWDB_COLUMNNOTFOUND;
extern const RWMsgId rwdbexport RWDB_INVALIDPOSITION;
extern const RWMsgId rwdbexport RWDB_NOTSUPPORTED;
extern const RWMsgId rwdbexport RWDB_NULLREFERENCE;
extern const RWMsgId rwdbexport RWDB_NOTFOUND;
extern const RWMsgId rwdbexport RWDB_MISSING;
extern const RWMsgId rwdbexport RWDB_NOMULTIREADERS;
extern const RWMsgId rwdbexport RWDB_NODELETER;
extern const RWMsgId rwdbexport RWDB_NOINSERTER;
extern const RWMsgId rwdbexport RWDB_NOUPDATER;
extern const RWMsgId rwdbexport RWDB_NOREADER;
extern const RWMsgId rwdbexport RWDB_NOINDEX;
extern const RWMsgId rwdbexport RWDB_NODROP;
extern const RWMsgId rwdbexport RWDB_WRONGCONN;
extern const RWMsgId rwdbexport RWDB_NOPRIVILEGE;
extern const RWMsgId rwdbexport RWDB_NOCURSOR;
extern const RWMsgId rwdbexport RWDB_CANTOPEN;
extern const RWMsgId rwdbexport RWDB_NOTREADY;

#endif
