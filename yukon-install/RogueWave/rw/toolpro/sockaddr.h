#ifndef __RWNET_SOCKET__
#define __RWNET_SOCKET__
/***************************************************************************
 *
 * sockaddr.h
 *
 * $Id: sockaddr.h@#/main/24  02/11/98 09:34:09  jc (TPR0100_WIN32_19980305)
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
 * Socket address: Socket type and address classes
 *
 * RWSockType represents a type of socket communications channel.  It
 * has three components:  the family, the type, and the protocol.  These
 * correspond to the three components used to classify sockets in the C
 * API.  Derived classes RWInetSockType and RWUnixSockType (parallel to
 * RWInetSockAddr and RWUnixSockAddr) provide simplified construction for
 * those domains.
 *
 * The RWSockAddrBase class is an interface class that represents a socket
 * address.  All member functions are pure virtual.  Specific derived
 * classes include RWInetSockAddr and RWUnixSockAddr and are defined in
 * their own header files.
 * 
 * The RWSockAddr class is a concrete socket address reference class.
 * It is a proxy for the real socket address, which is stored on the free
 * store and reference counted.
 *
 */

#include <rw/toolpro/netdefs.h>
#include <rw/toolpro/handle.h>

#include <rw/tvordvec.h>
#include <rw/cstring.h>

/*
 * Include the socket headers.  We need these so the client has access
 * to SOCK_STREAM, etc.  We could just ask the client to include the
 * header files, but this way the client code is portable and free from
 * ifdefs.
 */

#if defined(RWNET_WINSOCK)
#  if defined(__TURBOC__)
#    if __TURBOC__ >= 0x530
#       include <windows.h>
#    endif
#  endif
#include <winsock.h>
#else
#include <sys/types.h>
#include <sys/socket.h>
#endif

#include <rw/cstring.h>
struct sockaddr;

const int RWNET_MAX_SOCKADDR_SIZE = 257;
// Size to use when you are passing a buffer for a C API to fill in
// with an address.
// Usually, the next step is to take the buffer and pass it to a
// [[SockAddr]] constructor, so you can use the Net.h++ types.
// This is used mainly in the implementation of
// [[RWSocket]], it shouldn't concern Net.h++ clients.
// The value chosen is long enough for any of the Net.h++ supported
// address families.

/*
 * RWSockType: a type of socket communications channel
 *
 * An RWSockType represents a type of socket communications, but without
 * an address attached to it.  It is made up of a family (sometimes called
 * a domain), a socket type, and a protocol.  An example of a family is
 * the Internet TCP/IP family, a type is stream or datagram, and a protocol
 * could be TCP or UDP.
 */

class RWNETExport RWSockType {
public:
  RWSockType();
  // The default constructor builds an invalid socket type.  You must
  // use the assignment operator to set it to a valid type.

  RWSockType(const RWCString& familyName, int domain,
	     int type=SOCK_STREAM, int protocol=0);
  // Generally, you don't need to call this constructor.  Instead, use
  // one of the derived classes like RWInetType.
  // <P>
  // Build a socket communication type descriptor.  The domain specifies
  // the type of communications channel.  For the Internet domain, this 
  // is SOCK_INET; for unix domain, SOCK_UNIX.  The type specifies the 
  // type of channel, usually SOCK_STREAM or SOCK_DGRAM.  The protocol
  // is a specific prototol to use.  It defaults to zero, which means to
  // use the most sensible protocol; this is usually the correct thing to do.

  RWCString familyName() const {return familyName_;}
  int family()   const {return family_;}
  int domain()   const {return family_;}
  int type()     const {return type_;}
  int protocol() const {return protocol_;}
  // Get at the pieces which describe the socket type.  family() and domain()
  // are synonyms.

  RWCString id(unsigned level) const;
  // Provides an identifier for this socket type.  The 
  // higher the level specifier, the more information is provided.

  RWBoolean operator==(const RWSockType&) const;
  // To be equal, all components must be the same (name,family,type,protocol)

  RWBoolean operator!=(const RWSockType&) const;
  // not equal if all components are not the same (name,family,type,protocol)
private:
  RWCString familyName_;
  int family_;
  int type_;
  int protocol_;
};


/*
 * RWSockAddrBase: a socket address
 *
 * RWSockAddrBase is an abstract base class for representing socket addresses.
 * It corresponds to the C API struct sockaddr.
 *
 * Derived class constructors will not block.
 * Blocking may occur, however, when you call
 * one of the member functions, such as asSockaddr().
 * A routine like this can also be called indirectly, for example when you
 * connect an RWSocket to an address.
 * To prevent blocking, call prepare() before using the address.
 * This will do any preparation necessary to make the address completely 
 * ready for use.
 * For example, in the Internet family, prepare() will obtain the host
 * address from the name, and the port number from the service name.
 */
  
class RWNETExport RWSockAddrBase {
public:
  virtual ~RWSockAddrBase();
  // Destroy this address object.  We need a virtual destructor so that the
  // correct destructor will be called when destroying an RWSockAddr.

  virtual void prepare() const;
  // Set up all internal data so that no subsequent calls to interfaces on
  // this address will block.
  // This is declared const because conceptually, nothing in the address
  // is changed.
  // Also, this may be called from const functions like asSockaddr(), so
  // it needs to be const.
  // In practice, implementations of this class will often have to either
  // use mutable, or cast away the constness.

  virtual RWSockType addressType() const =0;
  RWCString familyName() const;
  int family()   const;
  int domain()   const;
  int type()     const;
  int protocol() const;
  // Return the type of address, and components of the address.
  // The component access functions provide more convenient access
  // syntax than addressType().xxxxx(), but the same semantics.
  
  virtual sockaddr* asSockaddr()   const =0;
  virtual size_t    sockaddrSize() const =0;
  // Returns a pointer to an internal representation of the address cast to
  // a sockaddr structure, as defined by the sockets and TLI APIs.  The
  // pointer is to an internal data structure, so it will become invalid if
  // this object is changed in any way or goes out of scope.  If the address
  // has no representation as a sockaddr, a null pointer is returned.
  // These operations may block if prepare() has not been called.

  virtual operator RWCString () const;
  // converts a particular address to a RWCString. The returned value of id(),
  // which is a RWCString, is used as default. Specialization classes of
  // this RWSockAddrBase may overwrite this type conversion operator, if
  // necessary.

  virtual RWCString id(unsigned level=0) const =0;
  // Return a string which identifies this address.
  // The higher the setting on level, the more information is provided.
  // Using level=0 (the default) is guaranteed not to block, higher
  // levels may hit the network to determine information.

  virtual const RWSockAddrBase *myAddress() const;
  // Used by derived classes to implement narrowing conversions from
  // an RWSockAddr or an RWSockAddrBase to a derived address type.
  // It returns this, unless this address is a proxy, in which case it
  // returns the pointer to the base class of the object being proxied.
  // From a derived class, access this functionality via itsAddress().
  // I'd like to restrict use of this to derived classes, but can find
  // no way to do this, since protected controls member, not name, access.

  friend RWNETExportFunc(RW_SL_IO_STD(ostream)&)
     operator<<(RW_SL_IO_STD(ostream)&, const RWSockAddrBase&);
  // Print a representation of this address using id() with level=0.

  static RWCString cannonicalizeInputString(const RWCString& in);
  // Do cannonical transformations on a string from which we are
  // going to extract a socket address.
  // Removes comments (in parentheses), converts to lower case, replaces
  // colons with spaces.

  static RWTValOrderedVector<RWCString> tokenizeInputString(const RWCString& s);
  // Break the string up into tokens at whitespace.  Before breaking up,
  // the string is put into cannonical form using cannonicalizeInputString.

  static short stringToSocketType(const RWCString& s);
  // Convert the string s to a socket type, or else throw an exception.
  // The socket types are the ones that can be output by RWSockAddrBase::id().
};

/*
 * RWSockAddr: a socket address proxy
 *
 * An RWSockAddr is a proxy to a socket address of some unknown type.  The
 * RWSockAddr keeps a handle to a reference counted RWSockAddrBase object
 * which is used to do the actual work; the RWSockAddr passes requests on
 * to this real address.
 *
 * RWSockAddr is used by the rwSockAddrFactory to return addresses whose
 * precise types are unknown.  It is used indirectly by RWSocket functions
 * such as getpeername() which return an address of unknown type.  A common
 * use in applications will be to construct an RWSockAddr from an istream.
 * This allows you to specify different address families at run time, for
 * example by storing the address in a configuration file.
 */

class RWNETExport RWSockAddr : public RWSockAddrBase {
public:
  RWSockAddr();
  // Creates an invalid address.  The address must be set
  // to something useful before trying to do anything with this address.
  // Common methods to set the address are by shifting it in from an istream,
  // or by using the assignment operator.

  RWSockAddr(sockaddr *addr, RWSockCallIntType len);
  RWSockAddr(sockaddr *addr, RWSockCallIntType len, int type);
  // Creates an address object from the C API representation.
  // This uses the rwSockAddrFactory to look up a specific construction
  // routine to construct the adrress.
  // The optional type parameter indicates the type of the
  // socket (ie SOCK_DGRAM).
  // An exception is thrown if the address is not a recognized address type.

  RWSockAddr(const RWCString& str);
  // Construct an address from the address specification in the string.
  // The first token in the string indicates the type of address, the
  // remainder the specific address.
  // Examples of the format are "inet ftp.roguewave.com ftp",
  // "inet www.roguewave.com 80", and "unix /tmp/here".
  // The [[rwSockAddrFactory]] is used to actually construct the
  // address.

  RWSockAddr(RWSockAddrBase* addr);
  // Construct an RWSockAddr that represents the address addr.  addr must be
  // an object allocated on the heap.  It will be deleted when no more
  // RWSockAddr objects refer to it.  This constructor is available for 
  // general use, but it is most often used by the rwSockAddrFactory.

  virtual RWSockType addressType() const;
  // Return the type of address.
  
  virtual sockaddr* asSockaddr()   const;
  virtual size_t    sockaddrSize() const;
  // Returns a pointer to an internal representation of the address cast to
  // a sockaddr structure, as defined by the sockets and TLI APIs.  The
  // pointer is to an internal data structure, so it will become invalid if
  // this object is changed in any way or goes out of scope.  If the address
  // has no representation as a sockaddr, a null pointer is returned.

  virtual void prepare() const;
  // Set up all internal data so that no subsequent calls to interfaces on
  // this address will block.
  // This forwards the [[prepare()]] request on to the real address object.

  virtual RWCString id(unsigned level=0) const;
  // Return a string which identifies this address.
  // The higher the setting on level, the more information is provided.
  // Using level=0 (the default) is guaranteed not to block, higher
  // levels may hit the network to determine information.

  RWBoolean valid() const {return ((RWSockAddrBase*)addr_) ? TRUE : FALSE;}
  // Returns TRUE if the address has been set, FALSE if self was 
  // created with default constructor and not initialized.

private:
  virtual const RWSockAddrBase *myAddress() const;
  // Returns the address of the object being proxied.  If the
  // object being proxied is itself a proxy, the request is forwarded,
  // and so on.

  RWNetHandle<RWSockAddrBase> addr_;
  // A reference counted handle to the address that we are a proxy to.
};

RWNETExportFunc(RW_SL_IO_STD(istream)&)
   operator>>(RW_SL_IO_STD(istream)&,RWSockAddr&);
// Reads the input stream and constructs an address from the information
// on the stream.  Example allowed formats are "inet ftp.roguewave.com ftp",
// and "unix /tmp/here", for example.
// The rwSockAddrFactory is used to actually construct the address.

#if !defined(RW_NO_EXPLICIT_TEMPLATE_FUNCTIONS)
template <class RWXXXAddr> inline RWBoolean is(const RWSockAddr& sockaddr)
{
  return RWXXXAddr::is(sockaddr);
}
// Test if sockaddr is really of type RWXXXAddr.  Provides a convenient
// syntax for the static member functions RWXXXAddr::is which are 
// provided by all derived address classes.

template <class RWXXXAddr> inline RWXXXAddr as(const RWSockAddr& sockaddr)
{
  return RWXXXAddr::as(sockaddr);
}
// Conversion of sockaddr to a specific derived class.
// If sockaddr is not of type RWXXXAddr, an exception will be thrown.
// Provides a convenient
// syntax for the static member functions RWXXXAddr::is which are 
// provided by all derived address classes.
#endif

#endif
