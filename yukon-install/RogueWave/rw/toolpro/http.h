#ifndef _RWIHTTP_H_
#define _RWIHTTP_H_
/***************************************************************************
 *
 * http.h
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

#include <rw/cstring.h>

#include <rw/toolpro/inetdefs.h>
#include <rw/toolpro/httpimpl.h>
#include <rw/toolpro/httprepl.h>
#include <rw/toolpro/httpver.h>

#include <rw/thr/ioureslt.h>

class RWIHttpMethod;

/*
 * RWIHttpClient
 *
 * RWIHttpClient provides low-level access to the HTTP client-side
 * protocol.  The RWIHttpClient class maintains a finite state
 * machine to inforce correct HTTP protocol action ordering. In the
 * case of mis-ordered method invocation, an RWIProtocolClientErr
 * exception will be thrown.
 *
 * All client methods return IOUResult's redeemable for the
 * type of RWIHttpReply. RWIHttpReply contains an encapsulation of
 * the standard HTTP protocol reply messages.
 *
 * RWIHttpClient, along with helper RWIHttpMethod and RWIHttpHeader
 * classes, provides fine-grained control over HTTP communications.
 *
 * RWIFtpClient object are lightweight. RWIFtpClient is implemented
 * using the interface-implementation pattern. The RWIFtpClient is
 * really a handle to an implementation that performs the protocol
 * interaction.
 *
 */

class RWINETExport RWIHttpClient {

  public:

    RWIHttpClient(void);
    // Default Constructor

    RWIHttpClient(const RWIHttpVersion& ver);
    // Constructor that takes version number

    RWIHttpClient(const RWIHttpClient& client);
    // Copy constructor

    RWIHttpClient&
    operator=(const RWIHttpClient& other);
    // Assignment operator

    ~RWIHttpClient(void);
    // Destructor

    RWIOUResult<RWBoolean>
    connect(const RWCString& host, int port = 80);
    // provides a connect method, host is expected to
    // be an IP address or machine domain name

    RWIOUResult<RWIHttpReply>
    execute(RWIHttpMethod& method);
    // sends method to server and retrieve possible response
    // NOTE:
    // If using HTTP 0.9, be careful that the returned
    // value (an RWIHttpReply object) of the method is invalid,
    // except that you may retrieve data using portal()
    // provided by RWIHttpReply, because there is no response
    // specified in 0.9 version of HTTP.

  private:

    RWNetHandle<RWIHttpClientImpl> impl_;
    // handle to HTTP body

    RWIHttpVersion* version_;
    // http version
};

#endif
