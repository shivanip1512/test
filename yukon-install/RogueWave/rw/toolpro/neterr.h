#ifndef __RWNET_ERR_H__
#define __RWNET_ERR_H__
/***************************************************************************
 *
 * neterr.h
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
 * NetErr: exceptions thrown by Net.h++
 * 
 * Also, Net.h++ portable constants representing the winsock and 
 * BSD socket errors.
 */

#include <rw/toolpro/netdefs.h>
#if defined(RWNET_WINSOCK)
#  if defined(__TURBOC__)
#    if __TURBOC__ >= 0x530
#       include <windows.h>
#    endif
#  endif
#include <winsock.h>
#else
# if defined(RWNET_OS2)
#  include <nerrno.h>
# else
#   include <errno.h>
# endif
#endif
#include <rw/rwerr.h>
#include <rw/cstring.h>

class RWNETExport RWSockType;

/*
 * RWInetServiceNotFound: no internet port with given service name
 *
 * This is an external error because services which may be defined on one
 * machine may not necessarily be defined on another.  The only way to make
 * sure is to use a port number instead of a string.
 */
class RWNETExport RWInetServiceNotFound : public RWExternalErr {
  const RWCString service_;
public:
  RWInetServiceNotFound(const RWCString& service);
  RWCString service() const {return service_;}
};

/*
 * RWInetHostNotFound: no internet port with given service name
 *
 * This is an external error because services which may be defined on one
 * machine may not necessarily be defined on another.  The only way to make
 * sure is to use a port number instead of a string.
 */
class RWNETExport RWInetHostNotFound : public RWExternalErr {
  const RWCString host_;
public:
  RWInetHostNotFound(const RWCString& host);
  RWCString host() const {return host_;}
};

/*
 * RWNetCantCreatePortal: cannot create a communications endpoint
 */
class RWNETExport RWNetCantCreatePortal : public RWExternalErr {
public:
  RWNetCantCreatePortal(const RWCString& err) : RWExternalErr(err) {}
};

/*
 * RWNetNoChannel: there is no underlying communications channel
 *
 * This exception is thrown when you attempt to access communications
 * via a portal with no underlying communications channel present.
 * This happens when you build a Portal with the default constructor and
 * then don't assign anything to it.
 */
class RWNETExport RWNetNoChannel : public RWInternalErr {
public:
  RWNetNoChannel();
};

/*
 * RWNetCant[Send,Recv]: can't send/receive data from channel
 * 
 * No data can be sent/received from the communications channel.  One possiblity
 * is that the channel is non-blocking and you attempted to do a 
 * guaranteedSend()/guaranteedRecv() on a portal.
 */
class RWNETExport RWNetCantRecv : public RWExternalErr {
public:
  RWNetCantRecv();
};

class RWNETExport RWNetCantSend : public RWExternalErr {
public:
  RWNetCantSend();
};

/*
 * RWNetAlreadyRegistered: another address factory already registered
 *
 * You have attempted to register two different address factories with
 * a general address factory for the same family key.
 */
class RWNETExport RWNetAlreadyRegistered : public RWInternalErr {
private:
  RWCString familyAsString_;
  int familyAsInt_;            // negative one if string family used
public:
  RWNetAlreadyRegistered(const RWCString& family);
  RWNetAlreadyRegistered(int family);
};

/*
 * RWNetNoFactoryRegistered: no factory has been registered for this family
 *
 * You are attempting to build a socket address for a family for which
 * no socket address factory has been registered.
 * The derived classes RWNetNoNumberFactoryRegistered and
 * RWNetNoNameFactoryRegistered handle the cases of families identified
 * via number or name, respectively.
 */
class RWNETExport RWNetNoFactoryRegistered : public RWExternalErr {
private:
  RWCString familyAsString_;
  int familyAsInt_;            // negative one if string family used
public:
  RWNetNoFactoryRegistered(const RWCString& msg) : RWExternalErr(msg) {}
};

class RWNETExport RWNetNoNumberFactoryRegistered : public RWNetNoFactoryRegistered {
private:
  int family_;
public:
  RWNetNoNumberFactoryRegistered(int family);
  int family() const {return family_;}
};

class RWNETExport RWNetNoNameFactoryRegistered : public RWNetNoFactoryRegistered {
private:
  RWCString family_;
public:
  RWNetNoNameFactoryRegistered(const RWCString& family);
  RWCString family() const {return family_;}
};

/*
 * RWNetSelectException: select returned an exceptional condition
 *
 * For some reason, select returned a negative one.  Keep track of
 * the actual error number.
 */
class RWNETExport RWNetSelectException : public RWExternalErr {
private:
  int err_;
public:
  RWNetSelectException();
  int error() const {return err_;}
};

/*
 * RWNetNoWinsock: problem initializing the winsock DLL
 *
 * The winsock DLL couldn't be initialized by the application.  This
 * could be due to the DLL not supporting the correct version of 
 * winsock, for example.
 * This exception is also thrown if there are problems shutting down
 * the winsock DLL after we are finished with it.  This should be a
 * rare condition.
 */
class RWNETExport RWNetWinsockInitErr : public RWExternalErr {
public:
  RWNetWinsockInitErr(int err);
};

/*
 * RWNetNoSuchBlockingHook: specified an invalid blocking hook ID
 *
 * You specified an ID of a Net.h++ blocking hook that does not exist.
 */
class RWNETExport RWNetNoSuchBlockingHook : public RWInternalErr {
public:
  RWNetNoSuchBlockingHook(int ID);
private:
  int id_;
};

/*
 * RWNetInvalidSocket: tried to use an invalid socket
 *
 * This is thrown when you try to use a socket that it is not valid.
 * RWSockets are invalid until they are initialized.
 */
class RWNETExport RWNetInvalidSocket : public RWInternalErr {
public:
  RWNetInvalidSocket();
};

/*
 * RWSockWrongAddressType: tried to convert to wrong address type
 *
 * You attempted to do a narrowing conversion to a class which
 * is not a type of the object.
 */
class RWNETExport RWSockWrongAddressType : public RWInternalErr {
public:
  RWSockWrongAddressType(const RWCString& from, const RWCString& to);
};

/*
 * RWSockErr: error thrown by RWSocket wrapper
 *
 * This exception is thrown by the socket wrapper layer in response to a
 * socket problem.  The error code it carries indicates which error
 * it was.
 */
class RWNETExport RWSockErr : public RWInternalErr {
public:
  RWSockErr(int errorNumber, const RWCString& funcname);
  RWCString where() const {return funcname_;}
  RWCString errDescription() const {return errNumberToString(errno_);}
  int errorNumber()       const {return errno_;}
  static RWCString errNumberToString(int errorNumber);
private:
  RWCString funcname_;
  int       errno_;
};

/*
 * RWSockTypeChange: tried to bind/connect with address of wrong family
 *
 * You tried to change the type of an RWSocket using bind() or connect(),
 * by first setting up the state of a socket (using socket(), probably)
 * and then by calling a function which takes an address.
 */

class RWNETExport RWSockTypeChange : public RWInternalErr {
public:
  RWSockTypeChange(const RWSockType& socktype, const RWSockType& addrtype);
};

/*
 * RWSockBadAddressFormat: tried to input an address I cannot understand
 *
 * The input string cannot be parsed as an address.  This is thrown
 * from one of the constructors which convert a string to an address, or
 * to an address component (like a host name).
 */

class RWNETExport RWSockBadAddressFormat : public RWExternalErr {
public:
  RWSockBadAddressFormat(const RWCString& s);
};

/*
 * Socket error codes.
 *
 * These codes are portable between winsock (including winsock under NT)
 * and unix.  They should really be encapsulated in RWSockErr.
 */

#if defined(RWNET_WINSOCK)
const int RWNETEINTR                   = WSAEINTR;
const int RWNETEBADF                   = WSAEBADF;
const int RWNETEACCES                  = WSAEACCES;
const int RWNETEFAULT                  = WSAEFAULT;
const int RWNETEINVAL                  = WSAEINVAL;
const int RWNETEMFILE                  = WSAEMFILE;
const int RWNETEWOULDBLOCK             = WSAEWOULDBLOCK;
const int RWNETEINPROGRESS             = WSAEINPROGRESS;
const int RWNETEALREADY                = WSAEALREADY;
const int RWNETENOTSOCK                = WSAENOTSOCK;
const int RWNETEDESTADDRREQ            = WSAEDESTADDRREQ;
const int RWNETEMSGSIZE                = WSAEMSGSIZE;
const int RWNETEPROTOTYPE              = WSAEPROTOTYPE;
const int RWNETENOPROTOOPT             = WSAENOPROTOOPT;
const int RWNETEPROTONOSUPPORT         = WSAEPROTONOSUPPORT;
const int RWNETESOCKTNOSUPPORT         = WSAESOCKTNOSUPPORT;
const int RWNETEOPNOTSUPP              = WSAEOPNOTSUPP;
const int RWNETEPFNOSUPPORT            = WSAEPFNOSUPPORT;
const int RWNETEAFNOSUPPORT            = WSAEAFNOSUPPORT;
const int RWNETEADDRINUSE              = WSAEADDRINUSE;
const int RWNETEADDRNOTAVAIL           = WSAEADDRNOTAVAIL;
const int RWNETENETDOWN                = WSAENETDOWN;
const int RWNETENETUNREACH             = WSAENETUNREACH;
const int RWNETENETRESET               = WSAENETRESET;
const int RWNETECONNABORTED            = WSAECONNABORTED;
const int RWNETECONNRESET              = WSAECONNRESET;
const int RWNETENOBUFS                 = WSAENOBUFS;
const int RWNETEISCONN                 = WSAEISCONN;
const int RWNETENOTCONN                = WSAENOTCONN;
const int RWNETESHUTDOWN               = WSAESHUTDOWN;
const int RWNETETOOMANYREFS            = WSAETOOMANYREFS;
const int RWNETETIMEDOUT               = WSAETIMEDOUT;
const int RWNETECONNREFUSED            = WSAECONNREFUSED;
const int RWNETELOOP                   = WSAELOOP;
const int RWNETENAMETOOLONG            = WSAENAMETOOLONG;
const int RWNETEHOSTDOWN               = WSAEHOSTDOWN;
const int RWNETEHOSTUNREACH            = WSAEHOSTUNREACH;
const int RWNETENOTEMPTY               = WSAENOTEMPTY;
const int RWNETEPROCLIM                = WSAEPROCLIM;
const int RWNETEUSERS                  = WSAEUSERS;
const int RWNETEDQUOT                  = WSAEDQUOT;
const int RWNETESTALE                  = WSAESTALE;
const int RWNETEREMOTE                 = WSAEREMOTE;
const int RWNETEPIPE                   = 0;
const int RWNETSYSNOTREADY             = WSASYSNOTREADY;
const int RWNERVERNOTSUPPORTED         = WSAVERNOTSUPPORTED;
const int RWNETNOTINITIALISED          = WSANOTINITIALISED;
const int RWNETHOST_NOT_FOUND          = WSAHOST_NOT_FOUND;
const int RWNETTRY_AGAIN               = WSATRY_AGAIN;
const int RWNETNO_RECOVERY             = WSANO_RECOVERY;
const int RWNETNO_DATA                 = WSANO_DATA;
const int RWNETNO_ADDRESS              = WSANO_ADDRESS;
#else

//
// OS2 error mapping
//
# if defined(RWNET_OS2)
const int RWNETEINTR                   = SOCEINTR;
const int RWNETEBADF                   = SOCEBADF;
const int RWNETEACCES                  = SOCEACCES;
const int RWNETEFAULT                  = SOCEFAULT;
const int RWNETEINVAL                  = SOCEINVAL;
const int RWNETEMFILE                  = SOCEMFILE;
const int RWNETEWOULDBLOCK             = SOCEWOULDBLOCK;
const int RWNETEINPROGRESS             = SOCEINPROGRESS;
const int RWNETEALREADY                = SOCEALREADY;
const int RWNETENOTSOCK                = SOCENOTSOCK;
const int RWNETEDESTADDRREQ            = SOCEDESTADDRREQ;
const int RWNETEMSGSIZE                = SOCEMSGSIZE;
const int RWNETEPROTOTYPE              = SOCEPROTOTYPE;
const int RWNETENOPROTOOPT             = SOCENOPROTOOPT;
const int RWNETEPROTONOSUPPORT         = SOCEPROTONOSUPPORT;
const int RWNETESOCKTNOSUPPORT         = SOCESOCKTNOSUPPORT;
const int RWNETEOPNOTSUPP              = SOCEOPNOTSUPP;
const int RWNETEPFNOSUPPORT            = SOCEPFNOSUPPORT;
const int RWNETEAFNOSUPPORT            = SOCEAFNOSUPPORT;
const int RWNETEADDRINUSE              = SOCEADDRINUSE;
const int RWNETEADDRNOTAVAIL           = SOCEADDRNOTAVAIL;
const int RWNETENETDOWN                = SOCENETDOWN;
const int RWNETENETUNREACH             = SOCENETUNREACH;
const int RWNETENETRESET               = SOCENETRESET;
const int RWNETECONNABORTED            = SOCECONNABORTED;
const int RWNETECONNRESET              = SOCECONNRESET;
const int RWNETENOBUFS                 = SOCENOBUFS;
const int RWNETEISCONN                 = SOCEISCONN;
const int RWNETENOTCONN                = SOCENOTCONN;
const int RWNETESHUTDOWN               = SOCESHUTDOWN;
const int RWNETETOOMANYREFS            = SOCETOOMANYREFS;
const int RWNETETIMEDOUT               = SOCETIMEDOUT;
const int RWNETECONNREFUSED            = SOCECONNREFUSED;
const int RWNETELOOP                   = SOCELOOP;
const int RWNETENAMETOOLONG            = SOCENAMETOOLONG;
const int RWNETEHOSTDOWN               = SOCEHOSTDOWN;
const int RWNETEHOSTUNREACH            = SOCEHOSTUNREACH;
const int RWNETENOTEMPTY               = SOCENOTEMPTY;
const int RWNETEPIPE                   = SOCEPIPE;
// these aren't defined in the OS2 2.1 dev files
const int RWNETEUSERS                  = 0;
const int RWNETESTALE                  = 0;
const int RWNETEREMOTE                 = 0;
const int RWNETEPROCLIM                = 0;
const int RWNETEDQUOT                  = 0;
const int RWNETSYSNOTREADY             = 0;
const int RWNERVERNOTSUPPORTED         = 0;
const int RWNETNOTINITIALISED          = 0;
const int RWNETHOST_NOT_FOUND          = 0;
const int RWNETTRY_AGAIN               = 0;
const int RWNETNO_RECOVERY             = 0;
const int RWNETNO_DATA                 = 0;
const int RWNETNO_ADDRESS              = 0;

# else /* UNIX */

const int RWNETEINTR                   = EINTR;
const int RWNETEBADF                   = EBADF;
const int RWNETEACCES                  = EACCES;
const int RWNETEFAULT                  = EFAULT;
const int RWNETEINVAL                  = EINVAL;
const int RWNETEMFILE                  = EMFILE;
const int RWNETEUSERS                  = EUSERS;
const int RWNETESTALE                  = ESTALE;
const int RWNETEREMOTE                 = EREMOTE;
const int RWNETEWOULDBLOCK             = EWOULDBLOCK;
const int RWNETEINPROGRESS             = EINPROGRESS;
const int RWNETEALREADY                = EALREADY;
const int RWNETENOTSOCK                = ENOTSOCK;
const int RWNETEDESTADDRREQ            = EDESTADDRREQ;
const int RWNETEMSGSIZE                = EMSGSIZE;
const int RWNETEPROTOTYPE              = EPROTOTYPE;
const int RWNETENOPROTOOPT             = ENOPROTOOPT;
const int RWNETEPROTONOSUPPORT         = EPROTONOSUPPORT;
const int RWNETESOCKTNOSUPPORT         = ESOCKTNOSUPPORT;
const int RWNETEOPNOTSUPP              = EOPNOTSUPP;
const int RWNETEPFNOSUPPORT            = EPFNOSUPPORT;
const int RWNETEAFNOSUPPORT            = EAFNOSUPPORT;
const int RWNETEADDRINUSE              = EADDRINUSE;
const int RWNETEADDRNOTAVAIL           = EADDRNOTAVAIL;
const int RWNETENETDOWN                = ENETDOWN;
const int RWNETENETUNREACH             = ENETUNREACH;
const int RWNETENETRESET               = ENETRESET;
const int RWNETECONNABORTED            = ECONNABORTED;
const int RWNETECONNRESET              = ECONNRESET;
const int RWNETENOBUFS                 = ENOBUFS;
const int RWNETEISCONN                 = EISCONN;
const int RWNETENOTCONN                = ENOTCONN;
const int RWNETESHUTDOWN               = ESHUTDOWN;
const int RWNETETOOMANYREFS            = ETOOMANYREFS;
const int RWNETETIMEDOUT               = ETIMEDOUT;
const int RWNETECONNREFUSED            = ECONNREFUSED;
const int RWNETELOOP                   = ELOOP;
const int RWNETENAMETOOLONG            = ENAMETOOLONG;
const int RWNETEHOSTDOWN               = EHOSTDOWN;
const int RWNETEHOSTUNREACH            = EHOSTUNREACH;
const int RWNETENOTEMPTY               = ENOTEMPTY;
const int RWNETEPIPE                   = EPIPE;
const int RWNETEPROCLIM                = 0;  // no such code in unix
const int RWNETEDQUOT                  = 0;  // no such code in unix
const int RWNETSYSNOTREADY             = 0;  // no such code in unix;
const int RWNERVERNOTSUPPORTED         = 0;  // no such code in unix;
const int RWNETNOTINITIALISED          = 0;  // no such code in unix;
const int RWNETHOST_NOT_FOUND          = 0;  // no such code in unix;
const int RWNETTRY_AGAIN               = 0;  // no such code in unix;
const int RWNETNO_RECOVERY             = 0;  // no such code in unix;
const int RWNETNO_DATA                 = 0;  // no such code in unix;
const int RWNETNO_ADDRESS              = 0;  // no such code in unix;
#  endif
# endif
#endif /* file wrapper */
