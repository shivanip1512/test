#ifndef __RWNET_INETADDR_H__
#define __RWNET_INETADDR_H__
/***************************************************************************
 *
 * inetaddr.h
 *
 * $Id: inetaddr.h@#/main/19  02/11/98 09:32:53  jc (TPR0100_WIN32_19980305)
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
 * Internet addresses: Encapsulate internet naming
 *
 * These classes encapsulate internet ports and hosts, including converting
 * between symbolic and integer representations for these.  The classes
 * include:
 * <DL>
 * <DT>RWInetType
 * <DD>The Internet address type. 
 * This is like an envelope with no address on it.
 * <DT>RWInetPort
 * <DD>Encapsulates an internet port and its service names.  You can
 * construct an RWInetPort from either an explicit port number or a
 * symbolic service name.
 * <DT>RWInetHost
 * <DD>Encapsulates an internet host IP address and its names.
 * You can
 * construct an RWInetHost from either an explicit IP address or a
 * symbolic name.
 * <DT>RWInetAddr
 * <DD>A complete internet address:  type information, a host, and a port
 * </DL>
 *
 * In many implementations, the gethostbyname(), getservbyport(), etc library 
 * functions, which are used by the non-inlined member functions, are not
 * multithread safe.  If you only access these functions only
 * indirectly via the Net.h++
 * classes, then you are safe provided that 
 * the macro RWNET_GETXBYY_NOT_REENTRANT is
 * set in toolpro/netdefs.h.
 */

#include <rw/toolpro/netdefs.h>

#if !defined(RWNET_WINSOCK)
#include <sys/types.h>
#include <netinet/in.h>   /* sockaddr_in structure */
#include <sys/socket.h>   /* SOCK_STREAM */
#endif

#include <rw/toolpro/sockaddr.h>

#include <rw/tvvector.h>

struct hostent;

class RWNETExport RWInetType : public RWSockType {
public:
  RWInetType(int type=SOCK_STREAM, int protocol=0);
  // The family name is set to "inet" and the family id
  // is set to AF_INET.
};

class RWNETExport RWInetPort {
public:
  RWInetPort(int port=0);
  RWInetPort(const RWCString& service);
  RWInetPort(const char *);
  // Build a port from the port number itself, or from a string representing
  // the name of the port.
  // Both the string and char* constructors are necessary.
  // This way you can use either a char* or a string where a port is
  // needed.
  // None of the constructors will block.

  void prepare() const;
  // Set up so no future operations will block.
  // This calls
  // the service database routines to learn all that can be
  // learned about this port name.
  // <P>
  // It is not necessary to explicitly call prepare().
  // It will be called by other member functions if necessary.

  int                     port()     const;
  RWCString               name()     const;
  RWTValVector<RWCString> aliases()  const;
  // Return the port number, the official service name,
  // or the list of aliases for this service name.
  // The protocol will be set to null if that is what was specified
  // in the constructor.

  RWCString id(unsigned level=0) const;
  // Return a string which identifies the port.
  // The level can be either 0,1, or 2, and controls the formatting
  // of the output.
  // If level is zero, only the port number is output (or the port name,
  // if we don't know the number).
  // If level is zero, it is guaranteed that the database lookup routines
  // will not be called, and this the call will not block.
  // Level one includes both the port name and number in a format 
  // like "9(discard)".
  // Level two output is a sequence of name=value pairs giving the port
  // number, name, and all known aliases (if any are known).

private:
  int                     port_;  // in host, not network, byte order
  RWCString               name_;  
  RWTValVector<RWCString> aliases_;
  // These items of data are only valid if the corresponding flags are
  // set to true.

  RWBoolean knowPort_;
  RWBoolean knowName_;
  // These flags indicate what we know about the port.  Once we know
  // both the port and the name we know everything.

#ifdef RWNET_GETXBYY_NOT_REENTRANT
  static RWMutex mutex_;
#endif
  // This mutex is used to protect the non-reentrant database calls.
};

RWNETExportFunc(RW_SL_IO_STD(ostream)&)
   operator<<(RW_SL_IO_STD(ostream)& strm, const RWInetPort& port);
// Output using id with level=0


class RWNETExport RWInetHost {
public:
  RWInetHost();
  RWInetHost(unsigned long IPAddress);
  RWInetHost(const RWCString& hostname);
  RWInetHost(const char *);
  // An internet host can be specified as an actual address or using
  // a string.  The string can be either the symbolic name of the host,
  // or an IP address in dotted decimal notation.  If an address is passed
  // in as a long, it must be in network byte order.  The default constructor
  // sets the host address as INADDR_ANY to mean any IP address on the current
  // host.  This can not be done as a default arg to the (long) constructor 
  // without including system header files.
  // <P>
  // Both the string and char* constructors are necessary.
  // This way you can use either a char* or a string where a port is
  // needed.
  // <P>
  // None of these constructors will block.

  void prepare() const;
  // Set up so no future operations will block.
  // This calls
  // the host database routines to learn all that can be
  // learned about this host.
  // <P>
  // It is not necessary to explicitly call prepare().
  // It will be called by other member functions if necessary.

  RWBoolean anyHost() const;
  // Returns TRUE if this host represents any available host interface
  // on the machine.

  RWCString                    name()      const;
  unsigned long                address()   const;
  RWTValVector<RWCString>      aliases()   const;
  RWTValVector<unsigned long>  addresses() const;
  // Return the official name, primary address, list of aliases, or
  // list of addresses for this host entry.
  // Addresses are returned
  // in network byte order.
  // If you would like to print out an address,
  // consider using the addressAsString() static member function.
  // The official name is not included in the list of aliases.
  // The primary address is the first entry in the addresses() array.
  // These may block if prepare() has not been called.

  RWCString id(unsigned level=0) const;
  // Return a string which identifies this host.
  // The level can be either 0,1,2, and controls the formatting
  // of the output.
  // If the level is zero, only the name of the host (or the IP address,
  // if the host is not known) is output.
  // If level is zero, it is guaranteed that the database lookup routines
  // will not be called, and this the call will not block.
  // Level one includes both the host name and IP address in a format 
  // like "machine(42.98.21.129)".
  // Level two output is a sequence of name=value pairs giving the host
  // name, primary IP address, aliases, and alternate addresses.

  static RWInetHost me();
  static RWCString  addressAsString(unsigned long IPAddress);
  // Auxiallary functions related to internet hosts and addresses.
  // me() returns the host object for the host on which we are currently
  // running.  addressAsString() takes an address in network byte order 
  // and returns the dotted decimal string representation of the address.
  // To go from a dotted decimal string to a address, build an RWInetHost
  // object from the dotted decimal string.

private:
  RWCString               name_;
// Fixed (second time, with better macro) per Scopus # 10131
# if defined(RW_BYTES_PER_LONG) && RW_BYTES_PER_LONG > 4
  unsigned int            addr_;
# else
  unsigned long           addr_;  // in network byte order
#endif
  RWTValVector<RWCString> aliases_;
  RWTValVector<unsigned long>      addresses_;
  // These pieces of information are only valid as long as the corresponding
  // know flags are true.
  // If name_ is set to null, this means that we couldn't manage to
  // look up the host name.

  RWBoolean knowName_;
  RWBoolean knowAddr_;
  RWBoolean problem_;
  // These flags indicate which data we currently know about the host.
  // If we know both the name and address then we must have everything.
  // Calling prepare_ teaches us all.
  // The problem_ flag is set if there was a problem looking up the host.
  // If name_ is null, then we couldn't find the name given the IP, if
  // name_ is non-null, then we couldn't find the IP given the name.

#ifdef RWNET_GETXBYY_NOT_REENTRANT
  static RWMutex mutex_;
#endif
  // This mutex is used to protect the non-reentrant database calls.
};

RWNETExportFunc(RW_SL_IO_STD(ostream)&)
   operator<<(RW_SL_IO_STD(ostream)& strm, const RWInetHost& host);
// output using id() with level=0


/*
 * RWInetAddr: address for a TCP/IP connection
 *
 * An internet address has two parts: the port number and the host.
 * This class contains both of these parts, each represented using the
 * classes defined above.
 */

class RWNETExport RWInetAddr : public RWSockAddrBase {
public:
  RWInetAddr();
  ~RWInetAddr();
  RWInetAddr(const RWInetPort& port);
  RWInetAddr(int port);
  RWInetAddr(const RWInetPort& port, 
	     const RWInetHost& host,
	     short type = SOCK_STREAM,
	     short protocol = 0);
  RWInetAddr(const sockaddr_in& addr,
	     short type = SOCK_STREAM,
	     short protocol = 0);
  // Construct an address for a TCP/IP internet connection.
  // Note that the RWInetPort and RWInetHost classes have conversion
  // constructors that take either a string or an integer, so you can
  // specifify either a string or an integer for either the port or the host.
  // The default constructor needs to be set (probably using [[operator=]]
  // to another address before it can be used.
  // Use the constructor taking only a port to represent a special address
  // used to bind to any of the network interfaces on the machine on which
  // the process is running;
  // this is done, for example, when you are setting a server address.
  // None of the constructors block.

  RWInetAddr(const RWCString& s);
  // Construct an address from the string s.
  // First, the string is canonicalized using
  // RWSockAddrBase::cannonicalizeInputString.  The string must then
  // match [ [type] host ] port, where each token is seperated by spaces.

  virtual void prepare() const;
  // Ensure that no future calls on this address block.
  // This will look up the IP address and port number from the host name
  // and service name, if necessary.
  // It is not necessary to call prepare(), but if you don't future calls
  // may block.

  virtual RWSockType addressType() const;
  // Returns the correct address type, constructed from an RWInetType.

  RWInetPort port() const {return port_;}
  RWInetHost host() const {return host_;}
  // Return the two parts of the address.

  virtual sockaddr* asSockaddr() const;
  virtual size_t    sockaddrSize() const;
  // Returns a pointer to an internal representation of the address cast to
  // a sockaddr structure, as defined by the sockets and TLI APIs.  The
  // pointer is to an internal data structure, so it will become invalid if
  // this object is changed in any way or goes out of scope.  If the address
  // has no representation as a sockaddr, a null pointer is returned.

  virtual RWCString id(unsigned level=0) const;
  // Return a string which identifies this address.
  // The level can be either 0,1,2, and controls the formatting
  // of the output.
  // If the level is zero, the name of the host and the port number
  // are output in form "machine:42".
  // If level is zero, it is guaranteed that the database lookup routines
  // will not be called, and this the call will not block.
  // Level one includes the host name, IP address,
  // port name, and service name, in a format 
  // like "machine(42.98.21.129):42(life)".
  // Level two output is a sequence of name=value pairs giving the
  // socket type information (socket type, protocol, etc), the
  // host name, primary IP address, aliases, alternate addresses, 
  // port number, and all service names.


  static RWBoolean  is(const RWSockAddrBase&);
  static RWInetAddr as(const RWSockAddrBase&);
  // Narrowing conversion from a base class, or from an RWSockAddr.
  // These are also accessible as the global template functions
  // is<RWInetAddr>() and as<RWInetAddr>() (provided your compiler
  // supports explicit template functions).

private:
  RWInetPort   port_;
  RWInetHost   host_;
  short        type_;
  short        protocol_;
  sockaddr_in  addr_;
};

RWNETExportFunc(RW_SL_IO_STD(istream)&)
   operator>>(RW_SL_IO_STD(istream)&, RWInetAddr&);
// Read an address from an input stream.

#endif
