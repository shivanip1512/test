#ifndef __RWDB_MSQENVH_H__
#define __RWDB_MSQENVH_H__

/**************************************************************************
 *
 * $Id: 
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
 * DB-Library definition of RWDBEnvironmentHandle
 *
 **************************************************************************/

/*
 * Need full path here, since applications may need to include
 * this file.
 */
#include  <rw/db/msqlibsrc/msqdefs.h> 

#include  <rw/db/envhandl.h>
#include  <rw/ordcltn.h>

class RWDBAccessExport 
RWDBMsDbLibEnvironmentHandle : public RWDBEnvironmentHandle {

friend class RWDBMsDbLibSystemHandle;

public:

    RWDBMsDbLibEnvironmentHandle  (); 

    ~RWDBMsDbLibEnvironmentHandle ();

    int                            loginTimeout() const;
    RWDBStatus                     loginTimeout( int seconds );

    int                            timeout() const;
    RWDBStatus                     timeout(int seconds);

    RWCString                      defaultLanguage() const;
    RWCString                      interfacesFile() const;

    int                            maximumProcs() const;
    RWDBStatus                     maximumProcs(int value);

    // These are set after a loginrec is allocated
    short                          packetSize() const;
    RWDBStatus                     packetSize(short value);

    RWCString                      clientCharacterSet() const;
    RWDBStatus                     clientCharacterSet(const RWCString& set);

    RWBoolean                      encrypt() const;
    RWDBStatus                     encrypt(RWBoolean enable);

    RWCString                      nationalLanguage() const;
    RWDBStatus                     nationalLanguage(const RWCString& lang);

    RWCString                      applicationName() const;
    RWDBStatus                     applicationName(const RWCString& name);

    RWCString                      hostName() const;
    RWDBStatus                     hostName(const RWCString& name);

    RWBoolean                      enableSecurity() const;
    RWDBStatus                     enableSecurity(RWBoolean enable);

    RWDBStatus                     securityLabels(const RWCString& name,
                                                  const RWCString& value );

    RWBoolean                      secureLogin() const;
    RWDBStatus                     secureLogin(RWBoolean enable);    

protected:

private:
    int                            loginTimeout_;
    RWCString                      defaultLanguage_;
    RWCString                      interfacesFile_;

    short                          packetSize_;
    RWCString                      characterSet_;
    RWCString                      appName_;
    RWCString                      nationalLanguage_;
    RWCString                      hostName_;
    RWBoolean                      encrypt_;
    RWBoolean                      secureLogin_;

    RWBoolean                      enableSecurity_;
    RWOrdered                      labelNameList_;
    RWOrdered                      labelValueList_;

    // Not Implemented
    RWDBMsDbLibEnvironmentHandle (const RWDBMsDbLibEnvironmentHandle&);
    RWDBMsDbLibEnvironmentHandle& 
                         operator=(const RWDBMsDbLibEnvironmentHandle&);
};


#endif







