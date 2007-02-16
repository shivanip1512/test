#ifndef __RWHTTPMETHOD_H__
#define __RWHTTPMETHOD_H__
/***************************************************************************
 *
 * httpmeth.h
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

#include <rw/tvslist.h>

#include <rw/toolpro/inetdefs.h>
#include <rw/toolpro/httphdr.h>

/*
 * RWIHttpMethod
 *
 * RWIHttpMethod is an abstract base class used to define HTTP request
 * methods, which are defined by the HTTP protocol specifications. For
 * HTTP 0.9, GET is supported. For HTTP 1.0, GET, HEAD, and POST are
 * supported.
 *
 * RWIHttpMethod accomodates adding header information to the request,
 * and provides methods to construct a full request.
 *
 */

#  if defined(RW_INET_INLINE_FUNCTIONS_NEED_BE_EXPANDED)
// Borland C++ compiler generates warnings that state some inline
// functions are not expanded inline
// This pragma turns these warnings off...
#      pragma warn -inl
#  endif

class RWINETExport RWIHttpMethod {

  public:

    RWIHttpMethod(const RWCString& uri) : uri_(uri)
    {}
    // constructor

    void addHeader(const RWIHttpHeaderBase& hdr);
    void addHeader(const RWCString& label, const RWCString& value);
    // adds header information

    void persistConnection(const RWBoolean bVal)
    { persistConn_ = bVal; }
    // may be used by future RWIHttpVersion classes
    // to keep a connection open

    virtual RWCString requestString(void) const;
    // returns request line in the form of
    // "request request-URI"

    virtual RWCString headerString(void);
    // iterates through header list concatenating
    // each header + newline to return string

    virtual RWCString bodyString(void) const
    { return RWCString(); }
    // returns contents of body, which is null in the
    // superclass. Subclasses with bodies should
    // override this function

    virtual RWCString name(void) const = 0;
    // returns the request type (e.g. "GET") by
    // a concrete subclass

  private:

    RWTValSlist<RWCString>  headerList_;
    RWBoolean               persistConn_;
    RWCString               uri_;
};

/*
 * RWIHttpGet
 *
 * RWIHttpGet provides basic formatting for HTTP GET requests.
 *
 */

class RWINETExport RWIHttpGet : public RWIHttpMethod {

  public:

    RWIHttpGet(const RWCString& uri = "/") : RWIHttpMethod(uri)
    {}
    // constructor

    RWCString name(void) const
    { return HTTP_GET; }
    // returns the request type (i.e. "GET")

  public:

    static const RWCString HTTP_GET;
};

/*
 * RWIHttpHead
 *
 * RWIHttpHead provides basic formatting for HTTP HEAD requests.
 *
 */

class RWINETExport RWIHttpHead : public RWIHttpMethod {

  public:

    RWIHttpHead(const RWCString& uri = "/") : RWIHttpMethod(uri)
    {}
    // constructor

    RWCString name(void) const
    { return HTTP_HEAD; }
    // returns the request type (i.e. "HEAD")

  public:

    static const RWCString HTTP_HEAD;
};

/*
 * RWIHttpPost
 *
 * RWIHttpPost provides basic formatting for HTTP POST requests.
 *
 */

class RWINETExport RWIHttpPost : public RWIHttpMethod {

  public:

    RWIHttpPost(void) : RWIHttpMethod(""), body_()
    {}
    // default constructor. It constructs an invalid object,
    // use assignment operator to initialize.

    RWIHttpPost(const RWCString& uri) : RWIHttpMethod(uri), body_()
    {}
    // constructor

    void append(const RWCString& str)
    { body_.append(str); }
    // adds body of post 

    void appendLine(const RWCString& str = "")
    { body_.append(str + "\xD\xA"); }
    // appends CR/LF

    RWCString headerString(void);
    // adds content-length header and
    // calls RWIHttpMethod::headerString

    RWCString bodyString(void) const
    { return body_; }
    // returns contents of body

    RWCString name(void) const
    { return HTTP_POST; }
    // returns the request type (i.e. "POST")

  public:

    static const RWCString HTTP_POST;

  private:

    RWCString body_;
};

#endif
