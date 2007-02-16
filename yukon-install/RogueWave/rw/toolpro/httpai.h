#ifndef _RWIHTTPAGENTIMPL_
#define _RWIHTTPAGENTIMPL_
/***************************************************************************
 *
 * httpai.h
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
#include <rw/toolpro/http.h>
#include <rw/toolpro/agenti.h>

#include <rw/thr/iouescro.h>

// Keys used by the http implementation
#define HTTPAI_HTTPPATH_KEY    "HttpPath"
#define HTTPAI_HTTPDATE_KEY    "HttpDate"
#define HTTPAI_POSTDATA_KEY    "PostData"
#define HTTPAI_VERSION_KEY     "Version"

/*
 * RWIHttpAgentImpl
 *
 * RWIHttpAgentImpl is an implementation class of its interface counterpart
 * RWIHttpAgent. The main functionality of RWIHttpAgentImpl is to handle
 * all HTTP agent commands dispatched from RWIHttpAgent.
 *
 */

class RWINETExport RWIHttpAgentImpl : public RWIAgentImpl {

  public:

    RWIHttpAgentImpl(void);
    // Default constructor

    virtual ~RWIHttpAgentImpl(void);
    // Destructor

    void
    get(RWIOUEscrow<RWSocketPortal> iouEscrow);
    // performs a get transaction that connects to
    // the server, sends GET information, and opens
    // a data connection to the resulting http object.

    void
    getIfNewer(RWIOUEscrow<RWBoolean> iouEscrow);
    // performs a get transaction that connects to the
    // server, sends GET information, along with an
    // If-Modified-Since header.  If a "not modified"
    // message is return then the escrow is set to FALSE.
    // Otherwise, it is set to TRUE.

    void
    post(RWIOUEscrow<RWSocketPortal> iouEscrow);
    // performs a post transaction that connects to the
    // server, sends POST information, and opens a data
    // connection to the resulting http object.

    virtual void
    setProperty(const RWCString& p, const RWCString& v);
    // Sets property

    void
    setHost(const RWCString& host)
    { host_ = host; }

  private: // member functions

    void
    connect(void);
    // connects to HTTP server.

  private: // data members

    RWIHttpClient httpClient_;
    // http client class.

    RWCString host_;
    // ip address of http server

    RWMutex threadMutex_;
    // exclusive thread access control.
};

#endif
