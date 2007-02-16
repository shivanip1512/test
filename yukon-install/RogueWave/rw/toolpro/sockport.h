#ifndef __RWSOCKPORTAL_H
#define __RWSOCKPORTAL_H
/***************************************************************************
 *
 * sockport.h
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
 ***************************************************************************/
/*
 * The RWSocketPortal class provides a socket implementation of Portal built
 * on the RWSocket class.  There is no state added in this class.  The
 * main thing you get are convenient constructors and access to the 
 * underlying RWSocket.
 *
 * A common use of this class will be to use it as a named constructor.
 * For example, 
 * <br>
 * <code>RWPortal socketPortal = RWSocketPortal(RWInetAddr(port,host));</code>
 */

#include <rw/toolpro/portal.h>
#include <rw/toolpro/socket.h>  /* we return an RWSocket from a member fn */

class RWNETExport RWSocketPortalBase {
public:

  virtual ~RWSocketPortalBase() {};
  // does nothing, but solves a Microsoft bug.

  enum WhoShouldClose { Portal, Application };
  // Indicates who is responsible for closing the socket.

  RWSocketPortalBase() {};
  RWSocketPortalBase(const RWSocketPortalBase &) {};
  // Explicitly null constructors. This is necessary because at least 
  // one compiler, that we know of, generates improper copy constructors for 
  // classes which only contain an enum.
};

class RWNETExport RWSocketPortal : public RWPortal, public RWSocketPortalBase {
public:
  RWSocketPortal();
  // Create a socket portal.  You need to do establish a
  // connection using [[connect()]], or [[op=]],
  // before the portal can be used.

  RWSocketPortal(const RWSockAddrBase&);
  // Create a socket portal connected to the address provided.

  RWSocketPortal(const RWSocket& socket, WhoShouldClose=Portal);
  // Create a portal to the communications channel represented by socket.
  // Using this constructor is the only way to create a socket which will
  // not be automatically closed when no more portals are using it.

  void connect(const RWSockAddrBase&);
  // Connect this portal to the address provided.

  RWSocket socket() const;
  // Obtain a reference to the underlying socket.
};

class RWNETExport RWSocketListener : public RWSocketPortalBase {
public:
  RWSocketListener();
  // Self needs to be initialized before it can be used.

  RWSocketListener(const RWSockAddrBase&);
  // Set up a listener on the address indicated, and set up a queue to
  // wait for connections.

  RWSocketListener(const RWSocket& socket, WhoShouldClose who=Portal);
  // Create a listening portal to the communications channel, where 
  // socket is the socket doing the listening.
  // Using this constructor is the only way to create a socket which will
  // not be automatically closed when no more portals are using it.

  ~RWSocketListener();
  // Closes the socket if no one else needs it

  void listen(const RWSockAddrBase&);
  // Bind the listener to the address indicated, and set up a queue to
  // wait for connections.

  RWSocketPortal operator()() const;
  // Receive the next waiting connection.

  RWSocket socket() const;
  // Obtain a reference to the underyling socket.

private:
  WhoShouldClose whoShouldClose_;
  // Indicates whether or not we should close the socket.

  RWNetHandle<RWSocket> socket_;
  // The socket which is doing the work
};

#endif
