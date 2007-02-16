#ifndef __RWPORTAL_H
#define __RWPORTAL_H
/***************************************************************************
 *
 * portal.h
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
 * RWPortal: An access point to stream oriented communication channel
 *
 * A portal is an access point of a reliable byte stream communication
 * channel.  It is possible for more than one portal to access the
 * communications channel.  This happens, for example, when using the
 * copy constructor and assignment operator.  Most portal classes are 
 * designed so that when the last portal disappears, the communications
 * channel is closed.
 * 
 * Portals are lightweight objects.  Since copying and assignment copy
 * only the portal, and not the underlying communications channel, these
 * operations are inexpensive.  As a result, portals are suitable for 
 * returning from functions and using by value in objects.
 *
 * Portals are implemented using the interface-implementation design pattern.
 * The portal itself is really a handle to an implementation that represents
 * the communication channel.
 */

#include <rw/toolpro/handle.h>
#include <rw/toolpro/netbuf.h>

/*
 * RWPortalImp: implementation class for portals
 *
 * This needs to be specified in the header file and before the
 * declaration of RWPortal because some of the inline members in RWPortal
 * use the member functions of this class.  Also putting it here makes life
 * easier when defining specific types of portals.
 */

class RWNETExport RWPortalImp : public RWReference {
public:
  virtual ~RWPortalImp();
  virtual RWNetBuf recv() =0;
  virtual int recv(char * buf, int len, RWNetBuf::State* state) =0;
  virtual int send(const char *buf, int len) =0;
};

class RWNETExport RWPortal {
public:
  RWPortal();
  // The default constructor creates a portal which can not send or recv.
  // Trying to send or recv will throw an exception.  The copy constructor
  // is lightweight.

  ~RWPortal();
  // Destroy this portal.  If there are other portals around that were created
  // from this one by assignment or copying, they are unaffected.

  RWNetBuf recv() const;
  int recv(char* buf, int bufLen, RWNetBuf::State *state=0) const;
  // Receive a buffer of data.
  // The version returning an int returns the number of characters
  // actually received.
  // [[recv()]] makes only one call to the communications channel.

  RWNetBuf recvAtLeast(int n) const;
  int recvAtLeast(char *buf, int bufLen, int n, RWNetBuf::State *state=0) const;
  // Receive at least [[n]] characters
  // into the buffer or throw an exception.
  // The implementation loops over [[recv]] until either all the data
  // is received or, for some reason, no data is returned.
  // If no data is returned by [[recv]] an exception is thrown.
  // Calling [[recvAtLeast]] on a non-blocking socket is therefore
  // very likely to cause an exception.

  int send(const RWCString& s) const  {return impl_->send(s.data(),s.length());}
  int send(const char* buf, int buflen) const {return impl_->send(buf,buflen);}
  // Send a buffer of data.
  // Returns the number of bytes actually sent.
  // send() makes only one call to the communications channel.

  int  sendAtLeast(const char* buf, int bufLen, int n) const;
  void sendAtLeast(const char* buf, int bufLen) const;
  int  sendAtLeast(const RWCString& s, int n) const;
  void sendAtLeast(const RWCString& s) const;
  // Send at least [[n]] characters into 
  // the communications channel or throw an exception.
  // If [[n]] is omitted, all of the data will be sent.
  // The implementation loops over [[send]] to send the data.
  // If any of the calls to [[send]] cannot send any data, an
  // exception is thrown.
  // Calling [[sendAtLeast]] on a non-blocking channel is therefore
  // likely cause an exception.

protected:
  RWPortal(RWPortalImp *impl);
  // Specify the portal implementation.  This is used by derived classes which
  // create specific types of portals by passing in specific types of portal
  // implementations.  The argument, like all PortalImps, must live on the 
  // heap.

  const RWPortalImp* implementation() const {return impl_;}
  // Derived classes may need to do things to the implementation, but
  // they are only allowed a const pointer so they do not go and do something
  // silly like to try change what impl_ points to.

private:
  RWNetHandle<RWPortalImp> impl_;

public:
  friend RWNETExportFunc(RWBoolean) operator==(const RWPortal& p, const RWPortal& q);

};

#endif

