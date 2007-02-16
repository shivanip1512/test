#ifndef __RWHTTPREPLY_H__
#define __RWHTTPREPLY_H__
/***************************************************************************
 *
 * httprepl.h
 *
 * $Id: httprepl.h@#/main/11  02/11/98 09:32:49  jc (TPR0100_WIN32_19980305)
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

#include <rw/tvordvec.h>

#include <rw/toolpro/sockport.h>

#include <rw/toolpro/inetdefs.h>
#include <rw/toolpro/nreply.h>
#include <rw/toolpro/httphdr.h>

/*
 * RWIHttpReply
 *
 * RWIHttpReply is a specialization class of RWINumReply. It
 * provides additional methods for retrieving returned HTTP
 * header information.
 *
 */

class RWINETExport RWIHttpReply : public RWINumReply {

  public:

    RWIHttpReply(void);
    // default constructor

    RWIHttpReply(const RWIHttpReply& r);
    // copy constructor
 
    RWIHttpReply& operator=(const RWIHttpReply& rs);
    // assignment operator

    RWIHttpReply(const RWSocketPortal& portal, int majorVerNum);
    // constructs an RWIHttpReply with data from an portal,
    // the reply data will be read until a complete reply
    // is obtained

    RWSocketPortal portal(void) const
    { return portal_; }
    // returns a copy of the internal portal

    RWBoolean containsHeader(const RWCString& label) const;
    // looks up label in header collection and returns
    // whether it exists or not

    RWCString headerValue(const RWCString& label) const;
    // looks up label in header collection and if found
    // returns its corresponding value

    size_t headerEntries(void) const
    { return headerMap_.entries(); }
    // returns the total number of headers in the
    // returned reply

    RWIHttpGenericHeader header(size_t i) const;
    // returns the header found at the passed in index
    // if the index is invalid an exception of type
    // RWBoundsError will be thrown

    RWCString versionAsString(void) const
    { return sVersion_; }
    // returns the HTTP version number passed back
    // by the HTTP server

    void clearAndDestroy(void);
    // clears all headers by calling headerMap_.clear() and the base class 
    // clearAndDestroy

  private:

    void setPortal(const RWSocketPortal& portal, int majorVerNum);
    // assigns the internal portal and, if dependent upon the
    // major version number passed in, will call readFromPortal
    // Used by RWIHttpFsm

    virtual void readFromPortal(const RWSocketPortal& portal);
    // reads return code and header info from the portal
    // and morphs it into a more readable form

    RWCString getline(const RWSocketPortal& portal);
    // reads a line of data from the portal

    RWTValOrderedVector<RWIHttpGenericHeader>   headerMap_;
    RWCString                                   sVersion_;
    RWSocketPortal                              portal_;

    // friends
    friend class RWIHttpFSM;

    friend RWINETExportFunc(RW_SL_IO_STD(ostream)&) 
    operator<<(RW_SL_IO_STD(ostream)& str, const RWIHttpReply& r);
    // outputs an RWIHttpReply to an ostream
};

#endif
