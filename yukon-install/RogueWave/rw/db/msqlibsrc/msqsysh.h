#ifndef __RWDB_MSQSYSH_H__
#define __RWDB_MSQSYSH_H__

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
 **************************************************************************
 *
 * DB-Library definition of RWDBSystemHandle
 *
 **************************************************************************/

/*
** Need full path here, since applications may need to include
** this file.
*/
#include  <rw/db/msqlibsrc/msqdefs.h> 
#include  <rw/db/msqlibsrc/msqcfg.h> 

#include  <rw/db/syshandl.h>

#include  <rw/cstring.h>


#ifndef RWDB_TEXT_THRESHOLD
#define RWDB_TEXT_THRESHOLD    512
#endif


class RWDBAccessExport RWDBMsDbLibSystemHandle : public RWDBSystemHandle {

friend class RWDBAccessExport RWDBMsDbLibConnectionImp;
friend class RWDBAccessExport RWDBMsDbLibStoredProcImp;

public:

    enum ServerType { Unknown, SQL42, SQL60 };
    enum CursorType { RWDBCURFORWARD=CUR_FORWARD, RWDBCURKEYSET=CUR_KEYSET, 
                      RWDBCURDYNAMIC=CUR_DYNAMIC };

    RWDBMsDbLibSystemHandle     ( const RWCString& userName,
                                   const RWCString& passWord,
                                   const RWCString& serverName,
                                   const RWCString& dbName,
                                   RWDBEnvironmentHandle *environment );
    ~RWDBMsDbLibSystemHandle    ();

    RWDBDbProcessP               dbproc() const;
    RWDBLoginRecP                loginrec() const;

    CursorType                   cursorType() const;
    RWDBStatus                   cursorType(CursorType ctype);

    RWBoolean                    logTextUpdates() const;
    RWDBStatus                   logTextUpdates(RWBoolean val);

    long                         textThreshold() const;
    RWDBStatus                   textThreshold(long size);
    
    unsigned long                textSize() const;
    RWDBStatus                   textSize(unsigned long val);


protected:
    RWBoolean                    open();
    void                         close();
    RWCString                    serverName() const;
    ServerType                   serverType() const;
    RWCString                    userName() const;
    unsigned long                textLimit() const;
    RWDBStatus                   textLimit(unsigned long val);

    int                          packetSize() const;

    RWCString                    clientCharacterSet() const;
    RWCString                    serverCharacterSet() const;
    RWBoolean                    isConvertsCharacterSet() const;

    unsigned long                maxStoredProcParameterSize(unsigned long newSize);
    unsigned long                maxStoredProcParameterSize() const;

private:
    unsigned long                maxParamSize_; 
    RWDBDbProcessP               dbproc_;
    RWDBLoginRecP                loginrec_;
    RWCString                    serverName_;
    RWCString                    dbName_;
    RWCString                    userName_;
    unsigned long                textThreshold_;
    RWBoolean                    logTextUpdates_;
    CursorType                   cursorType_;
    unsigned long                textSize_;
    unsigned long                textLimit_;
    RWDBMsDbLibSystemHandle::ServerType                 
                                 serverType_;

    // Not Implemented
    RWDBMsDbLibSystemHandle     (const RWDBMsDbLibSystemHandle&);
    RWDBMsDbLibSystemHandle&    operator=(const RWDBMsDbLibSystemHandle&);
};


#endif

