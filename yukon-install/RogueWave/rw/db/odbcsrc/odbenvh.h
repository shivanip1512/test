#ifndef __RWDB_ODBENVH_H__
#define __RWDB_ODBENVH_H__

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
 * RWDBEnvironmentHandle specialization for the ODBC Access library
 *
 **************************************************************************/

#include  <rw/db/odbcsrc/odbdefs.h> 

#include  <rw/cstring.h>
#include  <rw/db/envhandl.h>



class RWDBAccessExport 
RWDBODBCLibEnvironmentHandle : public RWDBEnvironmentHandle {

//friend class RWDBODBCLibSystemHandle;

public:

     RWDBODBCLibEnvironmentHandle();

    ~RWDBODBCLibEnvironmentHandle();


    int                          loginTimeout() const;
    RWDBStatus                   loginTimeout(int value);

    int                          cursorOption() const;
    RWDBStatus                   cursorOption(int value);

    int                          traceOption() const;
    RWDBStatus                   traceOption(int val);

    RWCString                    traceFileName() const;
    RWDBStatus                   traceFileName(const RWCString& name);

    int                          accessMode() const; 
    RWDBStatus                   accessMode(int mode); 

    RWCString                    currentQualifier() const;
    RWDBStatus                   currentQualifier(const RWCString& qualifier);

    size_t                       maxBlobSize() const;
    RWDBStatus                   maxBlobSize(size_t rVal) ;

    size_t                       maxStringSize() const;
    RWDBStatus                   maxStringSize(size_t rVal) ;

private:
    int                         loginTimeout_;
    int                         cursorOption_;
    int                         traceOption_;
    RWCString                   traceFileName_;
    int                         accessMode_;
    RWCString                   currentQualifier_; 
    //size_t                      maxStringSize_;   // should use rwdbMaxStringSize_
    //size_t                      maxBlobSize_;         // should use rwdbMaxBlobSize_

    // Not Implemented
    RWDBODBCLibEnvironmentHandle (const RWDBODBCLibEnvironmentHandle&);
    RWDBODBCLibEnvironmentHandle& 
                         operator=(const RWDBODBCLibEnvironmentHandle&);
};
#endif

