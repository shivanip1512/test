#ifndef _RWIHTTPAGENT_
#define _RWIHTTPAGENT_
/***************************************************************************
 *
 * httpa.h
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
#include <rw/rwtime.h>

#include <rw/toolpro/sockport.h>

#include <rw/toolpro/inetdefs.h>
#include <rw/toolpro/agent.h>
#include <rw/toolpro/httpai.h>

#include <rw/thr/ioureslt.h>

/*
 * RWIHttpAgent
 *
 * RWIHttpAgent is an easy-to-use Inter.Net.h++ class that does
 * basic HTTP access and posting. It handles more of the details
 * of the HTTP protocol than the RWIHttpClient class. However,
 * it doesn't provide the flexibility of the RWIHttpClient class.
 *
 * The constructor stores connection information. The methods
 * perform interaction with the server by connecting, perform-
 * ing the requested action, and disconnecting with the server.
 * Multiple transactions may be performed before an RWIHttpAgent
 * object gets destroyed.
 *
 * An RWIHttpAgent object is lightweight. It is implemented
 * using interface-implementation pattern. The RWIHttpAgent
 * itself is really a handle to an implementation that performs
 * the protocol interaction.
 *
 */

class RWINETExport RWIHttpAgent : public RWIAgent {

  public:

    RWIHttpAgent(void);
    // Default constructor, builds an invalid object.  Use the
    // assignment operator to initialize.

    RWIHttpAgent(const RWCString& host, const RWIHttpVersion& ver);
    // Constructs a valid object ready use.  Host is domain name of
    // the http server of interest. Ver specifies which HTTP
    // version to use.

    RWIOUResult<RWSocketPortal>
    get(const RWCString& httpPath = "/");
    // sends a GET request to the server requesting the object
    // specified in httpPath.  No header information will be
    // sent to the server.

    RWIOUResult<RWBoolean>
    isNewer(const RWCString& httpPath, const RWTime& time);
    // sends a GET request to the server requesting the object
    // specified in httpPath.  An If-Modified-Since header will
    // be attached to the request.  If the object has been modified 
    // more recently than the passed in time t then the IOU is populated
    // with a value of TRUE. Otherwise it will be FALSE.

    RWIOUResult<RWSocketPortal>
    post(const RWCString& httpPath, const RWCString& postData = "");
    // sends a POST request to the server.  The passed-in postData will
    // be attached to the request along with a Content-Length header
    // specifying the size of the body, if postData is not null. The
    // POST request will be given to the object specified in the
    // httpPath.

  private:

    RWIHttpAgentImpl* httpImpl(void) const;
};

#endif
