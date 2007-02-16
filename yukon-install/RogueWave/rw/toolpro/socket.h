#ifndef __RWNET_SOCKET_H__
#define __RWNET_SOCKET_H__
/***************************************************************************
 *
 * socket.h
 *
 * $Id: socket.h@#/main/29  02/11/98 09:34:11  jc (TPR0100_WIN32_19980305)
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
 * RWSocket: Wrapper for sockets
 *
 * RWSocket is a wrapper for the C socket descriptor.
 * It's member functions correspond exactly with the C functions
 * in the C sockets API.
 * Typically, the member functions have the same names as the corresponding
 * C API functions, but the arguments and return values may be different
 * to reflect the C++ environment.
 * For example, many member functions have default arguments to handle
 * the most common cases.
 * Some member functions take alternate parameter types which simplify the
 * interface (eg, using the RWSockAddrBase class instead of the struct
 * sockaddr).
 * Sometimes, multiple overloadings of a member function exist to provide
 * alternate APIs for different occasions.
 *
 * Almost all of the socket() calls that deal with a single socket are
 * encapsulated in this class.
 * There are a few calls left out:
 * <UL>
 * <LI> the data base calls of form getXbyY (eg gethostbyname), and
 * IP address conversion routines (eg inet_ntoa()).
 * These are encapsulated by the RWInetHost and RWInetPort classes.
 * <LI> the host - network conversion calls (htons() and friends).  These
 * are not needed - the C++ API does the conversions for you automatically.
 * <LI> select().  Implemented using RWSocketEvent class.
 * </UL>
 *
 * In addition to the functions which match the sockets API, some
 * convenience functions are also provided.  These are the
 * functions id(), valid(), socket(), recvAtLeast(), and sendAtLeast().
 *
 * The socket is not shut down by a destructor.
 * It must be explicitly shut down by calling [[close]], [[closesocket]],
 * or [[shutdown]].
 * Use the [[RWPortal]] layer for objects which close the portal using
 * a destructor.
 *
 * The socket portal provided by RWSocket has no state.  All state (whether
 * the socket blocks, etc) is kept in the communications channel. 
 *
 * <H3>Error Handling</H3>
 *
 * One of the main benefits of these calls is that they throw exceptions 
 * when things go wrong.  So you can use a sequence of them inside of a
 * try block instead of having to check the return code of each one.  Or
 * just assume things will go all right and let a higher level part of the 
 * code deal with exceptions.
 */

#include <rw/toolpro/netdefs.h>
#include <rw/toolpro/netbuf.h>
#include <rw/toolpro/sockaddr.h>

#if !defined(RWNET_WINSOCK)
# if defined(RW_NO_FILIO_HEADER)                
#  include <sys/ioctl.h>   /* looking for FIONBIO et al */
#  if defined(RWNET_OS2)
#   include <sys/socket.h>
#  endif
# else
#  include <sys/filio.h>
#  include <sys/sockio.h> /* looking for FIONBIO et al */
# endif
#endif

struct msghdr;

#ifndef RWNET_WINSOCK
  typedef int SOCKET;
#endif
  // SOCKET is the underlying type of the socket reference.  Winsock uses
  // SOCKET automatically, this way unix matches.
  // This is done outside of RWSocket so it can be used elsewhere, for example
  // in the implementation of select.
  // If this ever proves a problem due to namespace pollution we could
  // create an RWSocket::SockFD or something which works everywhere.

class RWNETExport RWSocket {
public:
  RWSocket();
  // The default constructor sets the socket to an invalid state.
  // You must initialize it with a call to [[socket()]],
  // [[connect()]], or [[bind()]] before it can be used.

  RWSocket(const RWSockType& socketType);
  // Creates an unconnected socket of the specified type.  The resulting
  // socket must be bound to an address using bind or connect before
  // it can be used.

  RWSocket(SOCKET existingSocket);
  // Create an RWSocket which encapsulates the C socket.

  RWBoolean operator==(const RWSocket& s) const {return socket()==s.socket();}
  // Returns true if the two sockets refer to the same underlying
  // communications channel

#if !defined(RW_NO_STL) && defined(_AIX)
  RWBoolean operator<(const RWSocket& s) const {
    RWTHROW(RWInternalErr("RWSocket::operator< should not have been used explicitly.")); return 0;}
  // Due to the limitation of the CSet++ 3.1 compiler on AIX (using Standard
  // Library), the function has to be defined instead of declared.
  // Otherwise, a link-time error will be generated, which complains
  // that this operator< symbol is undefined.
  // NOTE: Since we have to define the function under the circumstance, an
  //       exception is thrown. In other words, this function is NOT
  //       supposed to be used explicitly.
#else
  RWBoolean operator<(const RWSocket& s) const;
  // Does nothing.  Declared but not defined.  Will cause
  // linker error if call is attempted.
  // Added to accomidate tools7 with STL for compilers which
  // resolve all template instantiation at compile-time.
#endif

  RWSocket accept(RWSockAddr *addr =0) const;
  // Accept a connection that is waiting at this socket.
  // A queue for incoming connections must first be created using [[listen()]].
  // If [addr] is non-null, 
  // the peer's address is returned in addr.
  // You can also get this information using
  // by calling [[getpeername()]] on the returned [[RWSocket]].
  // <P>
  // If the socket is set to block (the default), then accept() blocks
  // until a connection request arrives.  If it is set to be non-blocking, it
  // returns right away.  If no connections were available, the returned socket
  // is invalid (use .valid() to check) and *addr will be unchanged.

  void bind(const RWSockAddrBase& address);
  // Assign an address to an as-yet unnamed socket.
  // This is used primarily by
  // servers, which need to specify the port on which they wait for
  // connections.
  // If the socket has not yet been initialized via [[socket()]], then
  // [[bind]] initializes it.

  void close();
  void closesocket();
  // Terminate this connection and remove the socket.  The socket is set
  // to an invalid state.  If this is the only file descriptor on the machine
  // using this socket, then the resources used to maintain the socket are 
  // deallocated and any unread data is discarded.
  // <P>close and closesocket are synonyms.  close() keeps unix people happy
  // and Winsock people like closesocket().
  // Peace and love, brother.

  void connect(const RWSockAddrBase& address);
  // Connect to a remote socket.  If this is a stream socket, the initial
  // handshaking with the remote side is done.  If this is a datagram socket
  // this sets up the target for communication, but nothing is sent out over
  // the wire.
  // If the socket has not been initialized with [[socket()]], then [[connect]]
  // first initializes it.

  RWSockAddr getpeername() const;
  // Returns the name of the peer connected to this socket.

  RWSockAddr getsockname() const;
  // Returns the name of this socket.

  RWSockType getsocktype() const;
  // Returns the type of this socket.

  void getsockopt(int level, int option, void *optval, 
	RWSockCallIntType  *optlen) const;
  int  getsockopt(int option) const;
  // Determine a socket option setting.  The second function assumes
  // that you are dealing with the SOL_SOCKET level, and that the option
  // value is an integer.  This is the usual case.

  RWCString id(unsigned level=0) const;
  // Return a string which identifies the local and (if applicable)
  // foreign addresses of this socket.
  // Increasing the level increases the amount of detail in the output.
  // The format depends on the underlying address types.

  void listen(const RWSockAddrBase& addr, int backlog=5);
  void listen(int backlog=5) const;
  // Prepare a socket to accept incoming connections.  The backlog parameter
  // sets the number of incoming connection requests that the protocol software
  // will enqueue while a connection is being processed.
  // The version which takes no address assumes that you first set
  // the address on which to wait using [[bind()]].

  RWNetBuf  recv(int flags=0) const;
  int       recv(char *buf, int len, int flags=0, RWNetBuf::State* s=0) const;
  RWNetBuf  recvfrom(RWSockAddr *addr=0, int flags=0) const;
  int       recvfrom(char *buf, int len, RWSockAddr *addr=0, int flags=0, RWNetBuf::State* state=0) const;
#if !defined(RWNET_NO_MSGHDR)
  int       recvmsg(msghdr *msg, int flags=0,  RWNetBuf::State* s=0) const;
#endif
  // Receive data from the socket.
  // [[recv()]] is used to read data from a connected
  // socket; [[recvfrom()]] and [[recvmsg()]] can be used on any socket.
  // The [[flags]] parameter is formed by ORing one or more of
  // [[MSG_OOB]] (out of band data), or [[MSG_PEEK]] (peek at
  // data on the socket but don't consume it).
  // [[addr]], if it is specified and if the socket is a datagram socket,
  // will be filled in with the address
  // of the originator of the message.
  // The variants using an explicit buffer
  // return the number of bytes actually received.
  // This may be zero in the case of a non-blocking socket with no data
  // waiting, or on end of file.
  // The optional state parameter returns whether end of file was hit.
  // This is the only way to detect end of file when using an explicit buffer.

  RWNetBuf  recvAtLeast(int n) const;
  int       recvAtLeast(char *buf, int len, int n, RWNetBuf::State* s=0) const;
  // This is guaranteed to either receive [[n]] characters or throw
  // an exception.
  // The call is only valid for stream sockets.
  // The implementation loops over [[recv]] until all of the data
  // has been sent.
  // An exception is thrown if one of the calls to [[recv]]
  // returns no data.
  // Calling [[recvAtLeast]] on a non-blocking socket is therefore
  // very likely to cause an exception.

  int send(const RWCString& buf, int flags=0) const;
  int send(const char *buf, int len, int flags=0) const;
  int sendto(const RWCString& buf, const RWSockAddrBase& to, int flags=0) const;
  int sendto(const char *buf, int len, const RWSockAddrBase& to, int flags=0) const;
#if !defined(RWNET_NO_MSGHDR)
  int sendmsg(msghdr *msg, int flags=0) const;
#endif
  // Send data out a socket.
  // [[send()]] is used to send data from a connected
  // socket; [[sendto()]] and [[sendmsg()]] can be used on any socket.
  // The [[to]] parameter is ignored for a connected socket.
  // These return the number of bytes sent.
  // This will be zero if the socket is non-blocking and the internal
  // buffer for the socket is full.

  void sendAtLeast(const RWCString& buf) const;
  int  sendAtLeast(const RWCString& buf, int n) const;
  void sendAtLeast(const char* buf, int len) const;
  int  sendAtLeast(const char* buf, int len, int n) const;
  // This is guaranteed to send at least [[n]] characters 
  // or the entire buffer if no [[n]] is specified.
  // This call is only valid for stream sockets.
  // The implementation loops over [[send]] to send the data.
  // If any of the calls to [[send]] cannot send any data, an
  // exception is thrown.
  // Calling [[sendAtLeast]] on a non-blocking socket will therefore
  // likely cause an exception if [[n]] is greater than the
  // amount of unused space in the 
  // system's buffer for the socket.

  void setsockopt(int level, int option, void *optval, int optlen) const;
  void setsockopt(int option, int optval) const;
  // Set a socket option setting.  The second function assumes
  // that you are dealing with the SOL_SOCKET level, and that the option
  // value is an integer.  This is the usual case.

  void shutdown(int how=2) const;
  void shutdownread()  const {shutdown(0);}
  void shutdownwrite() const {shutdown(1);}
  // Shut down either the reading side (how=0), the writing side (how=1), or
  // both sides (how=2) of a full duplex connection.  The socket resources
  // are not deallocated.  Use close() or closesocket() for that.

  SOCKET socket() const {return socket_;}
  // Return the C API socket descriptor that this [[RWSocket]] is
  // encapsulating.

  void socket(const RWSockType& type);
  // Creates an unconnected socket of the specified type.  The resulting
  // socket must be bound to an address using bind or connect before
  // it can be used.
  // If this [[RWSocket]] was already associated with a socket, that
  // association is lost.

  RWBoolean valid() const;
  // Check if the socket is ready for use.

#ifndef RWNET_DONT_INCLUDE_IOCTL  
  void ioctl(long cmd, void *arg) const;
  void ioctl(long cmd, int arg) const;
  int  ioctl(long cmd) const;
  void ioctlsocket(long cmd, void *arg) const;
  void ioctlsocket(long cmd, int arg) const;
  int  ioctlsocket(long cmd) const;
  // Get or retrieve socket operating parameters.
  // The versions which return an integer are useful with commands
  // that return ints (like FIONREAD or SIOCATMARK).
  // The versions which take an integer are useful with commands that
  // expect an integer argument and return nothing (like FIONBIO).
  // The [[ioctlsocket]] functions are just aliases for [[ioctl]].
  //
  // This function is commonly used to set blocking or non-blocking
  // status on a socket.  To set a socket to non-blocking use
  // [[ioctl(FIONBIO,1)]], to set to blocking use [[ioctl(FIONBIO,0)]].
#endif

protected:
  SOCKET socket_;
  // The socket itself.  No other state is stored in this class.  All state
  // related to the socket is kept (surprise!) in the socket.

  void clearError() const;
  int lastError() const;
  // The first sets the error code to no error, the second
  // returns the last error that happened on this socket.  Using these
  // functions, rather than the underlying OS specific function calls,
  // provides portability.
  
  void raise(const char *funcName) const;
  void raise(const char *funcName, int err) const;
  static void doRaise(int err, const char *funcName);
  void raiseUnlessWouldBlock(const char *funcName) const;
  void raiseUnlessWouldBlock(const char *funcName, int err) const;
  // Throw an exception based on the last error.  We can't call this 
  // function throw, because throw is a keyword.
  // The function raiseUnlessWouldBlock() returns instead of throwing
  // if the error is simply that this is a non-blocking socket which wants
  // to block.
  // <P>
  // All exceptions thrown in the RWSocket implementation are thrown via
  // these calls.  In the end, doRaise() always does the work.  This means
  // error handling policies can be changed by reimplementing doRaise().
  // If it is necessary, a user definable error handler could be dropped in
  // here.
  // Note that we can't just make these functions virtual; this is a concrete
  // class, and virtual functions and concrete classes don't mix because the
  // virtual definitions would get lost when the object was sliced.
};

RWNETExportFunc(RW_SL_IO_STD(ostream)&)
operator<<(RW_SL_IO_STD(ostream)&, const RWSocket& sock);
     
// for convenience, use these macros instead calling ioctl.
// note that the macro expects an RWSocket, not a socket number.
#ifndef RWNET_DONT_INCLUDE_IOCTL  
# ifndef RW_AVOID_PREPROCESSOR_PROBLEMS
#   define SET_BLOCKING(rwsocket)    rwsocket.ioctl(FIONBIO, 0);
#   define SET_NON_BLOCKING(rwsocket) rwsocket.ioctl(FIONBIO, 1);
# endif
inline void rwSetBlocking(RWSocket s)    { s.ioctl(FIONBIO, 0); }
inline void rwSetNonBlocking(RWSocket s) { s.ioctl(FIONBIO, 1); }
#endif /* RWNET_DONT_INCLUDE_IOCTL */

#endif
