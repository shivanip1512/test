#ifndef __RWIHTTPHEADER_H__
#define __RWIHTTPHEADER_H__
/***************************************************************************
 *
 * httphdr.h
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
#include <rw/toolpro/httpdate.h>

/*
 * RWIHttpHeaderBase
 *
 * RWIHttpHeaderBase is an abstract class for all RWIHttpHeader classes.
 * These classes are helpers that ease header attachment and formatting
 * to requests.
 *
 */

#  if defined(RW_INET_INLINE_FUNCTIONS_NEED_BE_EXPANDED)
// Borland C++ compiler generates warnings that state some inline
// functions are not expanded inline
// This pragma turns these warnings off...
#      pragma warn -inl
#  endif

class RWINETExport RWIHttpHeaderBase {

  public:

    RWIHttpHeaderBase(void) : label_()
    {}
    // default constructor

    RWIHttpHeaderBase(const RWCString& label) : label_(label)
    {}
    // constructs an RWIHttpHeaderBase, as part of a specific HTTP
    // header object.

    RWBoolean operator==(const RWIHttpHeaderBase& h) const
    { return label_ == h.label_; }
    // provides logical equality check

	// STL fix : added < operator
	RWBoolean operator<(const RWIHttpHeaderBase& h) const
	{ return label_ < h.label_; }

    RWCString label(void) const
    { return label_; }
    // returns the internal label

    virtual RWCString asString(void) const;
    // concatenates label and value into the format "label: value"
    // and returns the result

    virtual RWCString value(void) const = 0;
    // returns the value associated with a header's label
    // this is a pure virtual function, it must be overridden by
    // derived classes

    static unsigned hash(const RWIHttpHeaderBase& h)
    { return h.label_.hash(); }
    // returns the hash value of RWIHttpHeaderBase 

  private:

    RWCString   label_;
};

/*
 * RWIHttpGenericHeader
 *
 * RWIHttpGenericHeader is a specialization class of RWIHttpHeaderBase.
 * It is a concrete class that defines an HTTP header in a general
 * format.
 *
 */

class RWINETExport RWIHttpGenericHeader : public RWIHttpHeaderBase {

  public:

    RWIHttpGenericHeader(void) : RWIHttpHeaderBase(), value_()
    {}
    // default constructor

    RWIHttpGenericHeader(const RWCString& label) : RWIHttpHeaderBase(label), value_()
    {}
    // constructs an RWIHttpGenericHeader object, with
    // label cached for later use

    RWIHttpGenericHeader(const RWCString& label, const RWCString& value)
        : RWIHttpHeaderBase(label), value_(value)
    {}
    // constructs an RWIHttpGenericHeader object, with
    // label and its associated value cached for later use

    void setValue(const RWCString& value)
    { value_ = value; }
    // sets the internal value associated with the label

    virtual RWCString value(void) const
    { return value_; }
    // returns the internal value associated with the label

  private:

    RWCString   value_;
};

/*
 * RWIHttpFromHeader
 *
 * RWIHttpFromHeader is a specialization class of RWIHttpGenericHeader.
 * It defines the HTTP "From" header request.
 *
 */

class RWINETExport RWIHttpFromHeader : public RWIHttpGenericHeader {

  public:

    RWIHttpFromHeader(void) : RWIHttpGenericHeader("From")
    {}
    // default constructor

    RWIHttpFromHeader(const RWCString& value) : RWIHttpGenericHeader("From", value)
    {}
    // constructs an RWIHttpFromHeader object, with
    // label as "From" and its associated value cached for later use
};

/*
 * RWIHttpUserAgentHeader
 *
 * RWIHttpUserAgentHeader is a specialization class of RWIHttpGenericHeader.
 * It defines an HTTP "User-Agent" header request.
 *
 */

class RWINETExport RWIHttpUserAgentHeader : public RWIHttpGenericHeader {

  public:

    RWIHttpUserAgentHeader(void) : RWIHttpGenericHeader("User-Agent")
    {}
    // default constructor

    RWIHttpUserAgentHeader(const RWCString& value) : RWIHttpGenericHeader("User-Agent", value)
    {}
    // constructs an RWIHttpUserAgentHeader object, with
    // label as "User-Agent" and its associated value cached for later use
};

/*
 * RWIHttpContentLengthHeader
 *
 * RWIHttpContentLengthHeader is a specialization class of RWIHttpHeaderBase.
 * It defines the HTTP "Content-Length" header.
 *
 */

class RWINETExport RWIHttpContentLengthHeader : public RWIHttpHeaderBase {

  public:

    RWIHttpContentLengthHeader(void) : RWIHttpHeaderBase("Content-Length"), value_(0)
    {}
    // default constructor

    RWIHttpContentLengthHeader(int value) : RWIHttpHeaderBase("Content-Length"),
                                            value_(value)
    {}
    // constructs an RWIHttpContentLengthHeader object, with
    // label as "Content-Length" and its associated value

    void setValue(int value)
    { value_ = value; }
    // sets the internal value

    RWCString value(void) const;
    // returns the internal value as a string

  private:

    int   value_;
};

/*
 * RWIHttpDateHeader
 *
 * RWIHttpDateHeaderBase is a specialization class of RWIHttpHeaderBase.
 * It defines an HTTP "Date" header.
 *
 */

class RWINETExport RWIHttpDateHeader : public RWIHttpHeaderBase {

  public:

    RWIHttpDateHeader(void)
        : RWIHttpHeaderBase("Date"), date_()
    {}
    // default constructor

    RWIHttpDateHeader(const RWCString& label)
        : RWIHttpHeaderBase(label), date_()
    {}
    // constructs an RWIHttpDateHeader object, with
    // the label as label and its associated value
    // as the current date

    RWIHttpDateHeader(const RWIHttpDate& d)
        : RWIHttpHeaderBase("Date"), date_(d)
    {}
    // constructs an RWIHttpDateHeader object, with
    // label as "Date" and its associated value as
    // the date d

    RWIHttpDateHeader(const RWCString& label, const RWIHttpDate& d)
        : RWIHttpHeaderBase(label), date_(d)
    {}
    // constructs an RWIHttpDateHeader object, with
    // the label as label and its associated value as
    // the date d

    void setDate(const RWIHttpDate& d)
    { date_ = d; }

    RWIHttpDate date(void) const
    { return date_; }

    RWCString value(void) const
    { return date_.asString(); }

  private:

    RWIHttpDate   date_;
};

/*
 * RWIHttpIfModifiedSinceHeader
 *
 * RWIHttpIfModifiedSinceHeader is a specialization class of RWIHttpDateHeader.
 * It defines an HTTP "If-Modified-Since" header.
 *
 */

class RWINETExport RWIHttpIfModifiedSinceHeader : public RWIHttpDateHeader {

  public:

    RWIHttpIfModifiedSinceHeader(void) : RWIHttpDateHeader("If-Modified-Since")
    {}
    // default constructor

    RWIHttpIfModifiedSinceHeader(const RWIHttpDate d)
        : RWIHttpDateHeader("If-Modified-Since", d)
    {}
    // constructs an RWIHttpIfModifiedSinceHeader object, with
    // label as "If-Modified-Since" and its associated value
    // as the RWIHttpDate d
};

#endif
