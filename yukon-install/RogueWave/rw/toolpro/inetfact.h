#ifndef __RWNET_INETFACT_H__
#define __RWNET_INETFACT_H__
/***************************************************************************
 *
 * inetfact.h
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
/*
 * RWInetAddrFactory: Construct Internet style socket addresses
 *
 * RWInetAddrFactory has a singleton instance which is registered with
 * the global socket address factory.
 * The registration and construction of the singleton
 * happen the first time an instance of RWInetAddrFactory is constructed.  Thus
 * you must make sure that at least one RWInetAddrFactory gets constructed
 * somewhere along the line.
 * One is constructed for you at the time the socket factory is created, so
 * you are covered.
 */

#include <rw/toolpro/netdefs.h>
#include <rw/toolpro/sockfact.h>

class RWNETExport RWInetAddrFactory : public RWSockAddrFactoryBase {
public:
  RWInetAddrFactory();
  // If it hasn't already happened, construct an instance of this
  // class and register it with rwSockAddrFactory.

  virtual RWSockAddrBase* operator()(struct sockaddr *addr, 
	RWSockCallIntType addrLen) const;
  virtual RWSockAddrBase* operator()(struct sockaddr *addr, 
	RWSockCallIntType addrLen, int type) const;
  virtual RWSockAddrBase* operator()(const RWCString&) const;
  // Construct an address from the C representation or read it in
  // from the stream.

private:
  static RWInetAddrFactory* rwInetAddrFactory_;
  // The address factory which is registered with rwSockAddrFactory.
};

#endif
