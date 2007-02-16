#ifndef __RWIHTTPVERSION_H__
#define __RWIHTTPVERSION_H__
/***************************************************************************
 *
 * httpver.h
 *
 * $Id$
 *
 * Copyright (c) 1998-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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

#include <rw/toolpro/inetdefs.h>

// forward declarations
class RWIHttpMethod;

/*
 * RWIHttpVersion
 *
 * RWIHttpVersion is an abstract class that defines the interface
 * for HTTP versions. The version classes are used by RWIHttpClient,
 * as helper classes, to filter request information based on
 * version.
 *
 */

class RWINETExport RWIHttpVersion {

  public:
    RWIHttpVersion(void)
    {}
    // default constructor

    virtual RWBoolean canPersist(void) const = 0;
    // returns whether this version can persist connections

    virtual RWBoolean methodIsValid(const RWCString& methodName) const = 0;
    // returns whether the passed in method name is valid for
    // this version

    virtual RWCString makeMethodAsString(RWIHttpMethod& pMethod) = 0;
    // Uses method to create a valid HTTP method
    // string suitable for this version

    virtual int majorVersionNumber(void) const = 0;
    // returns major HTTP version number

    virtual int minorVersionNumber(void) const = 0;
    // returns minor HTTP version number

    virtual RWIHttpVersion* clone(void) const = 0;
    // returns a pointer to a copy of a specific RWIHttpVersion object
};

/*
 * RWIHttpVersion_0_9
 *
 * RWIHttpVersion_0_9 is a specialization class of RWIHttpVersion. It
 * provides all version 0.9 related information.
 *
 */

class RWINETExport RWIHttpVersion_0_9 : public RWIHttpVersion {

  public:
    RWIHttpVersion_0_9(void)
    {}
    // default constructor

    RWBoolean canPersist(void) const
    { return FALSE; }

    RWBoolean methodIsValid(const RWCString& methodName) const;

    RWCString makeMethodAsString(RWIHttpMethod& pMethod);

    int majorVersionNumber(void) const
    { return 0; }

    int minorVersionNumber(void) const
    { return 9; }

    RWIHttpVersion* clone(void) const
    { return new RWIHttpVersion_0_9(*this); }
};

/*
 * RWIHttpVersion_1_0
 *
 * RWIHttpVersion_1_0 is a specialization class of RWIHttpVersion. It
 * provides all version 1.0 related information.
 *
 */

class RWINETExport RWIHttpVersion_1_0 : public RWIHttpVersion {

  public:

    RWIHttpVersion_1_0(void)
    {}
    // default constructor

    RWBoolean canPersist(void) const
    { return FALSE; }

    RWBoolean methodIsValid(const RWCString& methodName) const;

    RWCString makeMethodAsString(RWIHttpMethod& pMethod);

    int majorVersionNumber(void) const
    { return 1; }

    int minorVersionNumber(void) const
    { return 0; }

    RWIHttpVersion* clone(void) const
    { return new RWIHttpVersion_1_0(*this); }
};

#endif
