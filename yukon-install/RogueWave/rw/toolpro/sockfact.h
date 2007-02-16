#ifndef __RWNET_SOCKFACT_H__
#define __RWNET_SOCKFACT_H__
/***************************************************************************
 *
 * sockfact.h
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
 * rwSockAddrFactory:  Construct an address whose type varies at run time
 *
 * The object rwSockAddrFactory is a factory for constructing socket addresses
 * whose types are not known until runtime.
 * Usually, the use of this factory is transparent to the library client.
 * It is used by the istream shift operator, and by the constructor from
 * the C struct sockaddr.
 *
 * The types are indicated to the factory by either the address family integer
 * identifier (as used defined by the C sockets API) or a string.
 * From there, the factory turns around and invokes a factory specific to
 * the address family.
 * If finds the specific factory using a dictionary kept with the factory.
 * 
 * Specific address factory objects need to be registered with the factory via
 * the general factory's register() method.
 * Check out [[RWInetAddrFactory]] for an example of a specific 
 * address family factory.
 * In an initial prototype code line, we experimented with using a template
 * class for specific address families, but found that subtle differences
 * between the families and subtle template bugs in some compilers made
 * this approach more trouble that it was worth.
 *
 * All singleton objects are constructed explicitly rather than relying
 * on static initialization.  This way we have no dependence on static
 * initialization here.
 */

#include <rw/toolpro/netdefs.h>
#include <rw/toolpro/sockaddr.h>

#include <rw/tvsldict.h>
class RWCString;

/*
 * RWSockAddrFactoryBase: something that can build addresses
 * 
 * This class represents the concept of something that can build socket
 * addresses from either the C API representation, or an input stream.
 */

class RWNETExport RWSockAddrFactoryBase {
public:
  virtual ~RWSockAddrFactoryBase();
  // Need a virtual destructor because we keep collections typed to this.

  virtual RWSockAddrBase* operator()(struct sockaddr *addr, 
				     RWSockCallIntType addrLen)const=0;
  virtual RWSockAddrBase* operator()(struct sockaddr *addr, 
	RWSockCallIntType addrLen, int type) const=0;
  virtual RWSockAddrBase* operator()(const RWCString& str) const=0;
  // Construct an address from the C representation or read it in
  // from the string.
};

/*
 * RWSockAddrFactory: Build addresses of any (registered) type
 *
 * Building an address using this factory happens in two stages.  First,
 * the type of address is determined.  Next, the type of address is used
 * to look up an RWSockAddrFactoryBase object, and that is used to actually
 * construct the address.  Note that since an RWSockAddrFactory is itself
 * an RWSockAddrFactoryBase, this scheme can be used in a hierarchical fashion.
 */

class RWNETExport RWSockAddrFactory : public RWSockAddrFactoryBase {
public:
  RWSockAddrFactory();
  // Create a factory with no create methods attached.

  virtual ~RWSockAddrFactory();
  //

  void registerWithFactory(const RWSockType& family, RWSockAddrFactoryBase *maker);
  void registerWithFactory(int family, RWSockAddrFactoryBase *maker);
  void registerWithFactory(const RWCString& family, RWSockAddrFactoryBase *maker);
  // Register maker objects.  Duplicate registration is a no-op.
  // If you attempt to register two different maker instances
  // with the same family, an exception will be thrown.
  // The variant taking a socket type argument registers both the standard
  // family name for that socket type and the family number.
  
  void unRegister(const RWSockType&);
  void unRegister(int family);
  void unRegister(const RWCString& family);
  // Remove all knowledge of how to build addresses of the indicated family
  // from the factory.
  // This is useful if you want to explicitly replace one factory with another
  // for the same family.
  // Unless you first unRegister, you'll get an exception.
  // It is not an error to unregister a type which has never been registered.

  virtual RWSockAddrBase* operator()(struct sockaddr *addr, 
	RWSockCallIntType  addrLen) const;
  virtual RWSockAddrBase* operator()(struct sockaddr *addr, 
	RWSockCallIntType addrLen, int type) const;
  virtual RWSockAddrBase* operator()(const RWCString& str) const;
  // Construct a socket address.
  // First extract the
  // family name (from the strm/string)
  // or identifier (from the sockaddr), then look up the appropriate maker
  // object and use it.  If no maker has been registered for the family, an
  // exception is thrown.
  // The string is first cannonicalized using 
  // RWSockAddrBase::cannonicalizeInputString().

  static RWSockAddrFactory& theGlobalRWSockAddrFactory();
  // Returns the global factory that is used by Net.h++.  This is more
  // conveniently accessed via the rwSockAddrFactory macro.  If the
  // factory has not yet been constructed, it will be constructed,
  // and the well known families will be registered.

private:
  static RWSockAddrFactory *rwSockAddrFactory_;
  // The global singleton used by Net.h++ to create addresses.
  // Access via the public member function rwSockAddrFactory(), which
  // will create it if necessary.

  RWTValSlistDictionary<int,RWSockAddrFactoryBase*> factoryKeyedByInt_;
  RWTValSlistDictionary<RWCString,RWSockAddrFactoryBase*> factoryKeyedByString_;
  // The factories called indirectly to build the address.
};

#define rwSockAddrFactory (RWSockAddrFactory::theGlobalRWSockAddrFactory())
// Using the macro here also gives us some flexibility in case we decide
// to be more sophisticated in the face of DLLs.

#endif
