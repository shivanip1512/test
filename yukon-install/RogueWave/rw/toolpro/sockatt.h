#ifndef __RWNET_SOCKET_ATTRIBUTE__
#define __RWNET_SOCKET_ATTRIBUTE__
/***************************************************************************
 *
 * sockatt.h
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
 * RWSocketAttribute:  Socket events and an interface to detect socket events
 *
 * The [[RWSocketAttribute]] class encapsulates conditions that may be
 * true for a socket.
 *
 * The [[RWSocketAttribute]] class is used by the global function [[select]]
 * to do asynchronous I/O.
 *
 * This class could have been implemented using inheritance to represent
 * the various possible types of attributes: a RWSocketReadAtt for a read
 * attribute, for example.  There are two downsides to this approach.  First,
 * it would be impossible to use one object to represent multiple attributes
 * (ok, at least not easily, you could invent composite event objects).
 * More importantly, the chosen design is lightweight and simple, and
 * allows us to pass attributes around by value.  This makes management of
 * event collections much simpler.  The downside to the chosen approach
 * is that it tends to encourage "switch statement" style coding, and
 * clients cannot add new attribute types.
 */

#include <rw/toolpro/socket.h>

#include <rw/tvordvec.h>

class RWNETExport RWSocketAttribute {
public:
  enum Attribute { 
#ifndef RW_AVOID_PREPPOCESSOR_PROBLEMS
    NIL         = 0,  // placebo
    CANREAD     = 1,  // Data is available for reading
    CANWRITE    = 2,  // Data is available for writing
    RWEXCEPTION = 4,  // Deprecated (twice...)
    ISEXCEPTION = 4,  // in case of necessity
    EXCEPTION = 4,    // Expected usage
    ISCONNECTED = 8,  // Connect has completed
#if 0
    ISCLOSED not supported for this release of Net.h++
    ISCLOSED    = 16, // The socket has been closed
#endif
    CANACCEPT   = 32, // A new connection is ready to be accepted
    ANY         = ~0,  // Interested in any Attribute
#endif /* RW_AVOID_PREPPOCESSOR_PROBLEMS */

    sock_attr_nil         = 0,  // placebo
    sock_attr_canread     = 1,  // Data is available for reading
    sock_attr_canwrite    = 2,  // Data is available for writing
    sock_attr_exception   = 4,
    sock_attr_isconnected = 8,  // Connect has completed
#if 0
    sock_attr_isclosed not supported for this release of Net.h++
    sock_atter_isclosed   = 16, // The socket has been closed
#endif
    sock_attr_canaccept   = 32, // A new connection is ready to be accepted
    sock_attr_any         = ~0  // Interested in any Attribute
  };
  // The Attribute type indicates the condition(s) on the socket.
  // Attributes are used both to indicate conditions we are interested in,
  // and to indicate what has happened on the socket.
  // Attributes can be aggregated by oring them together.
  // For example, the attribute [[sock_attr_canread|sock_attr_canwrite]]
  //  on a socket indicates that the socket can either read or write.

  RWSocketAttribute();
  // Construct a null attribute on the undefined socket.

  RWSocketAttribute(const RWSocket& socket, Attribute attribute=NIL);
  // Construct an attribute for a particular socket.

  RWSocket socket() const {return socket_;}
  // The socket with which the attribute is associated.

  Attribute attribute() const {return attribute_;}
  // The attribute with which this is associated.
  // The attribute may be a composite of several attributes ored together.

#if !defined(RW_NO_STL) && defined(_AIX)
  RWBoolean operator<(const RWSocketAttribute& s) const {
    RWTHROW(RWInternalErr("RWSocketAttribute::operator< should not have been used explicitly.")); return 0;}
  // Due to the limitation of the CSet++ 3.1 compiler on AIX (using Standard
  // Library), the function has to be defined instead of declared.
  // Otherwise, a link-time error will be generated, which complains
  // that this operator< symbol is undefined.
  // NOTE: Since we have to define the function under the circumstance, an
  //       exception is thrown. In other words, this function is NOT
  //       supposed to be used explicitly.
#else
  RWBoolean operator<(const RWSocketAttribute& s) const;
  // Does nothing.  Declared but not defined.  Will cause
  // linker error if call is attempted.
  // Added to accomidate tools7 with STL for compilers which
  // resolve all template instantiation at compile-time.
#endif

private:
  RWSocket   socket_;
  Attribute  attribute_;
};

// Wait for a socket attribute in the list to become true
// This may block indefinitely.
RWNETExportFunc(RWTValOrderedVector<RWSocketAttribute>)
rwSocketSelect(const RWTValOrderedVector<RWSocketAttribute>& attributes);

// Wait for a socket attribute in the list to become true
// This may block up to [[timeout]] seconds.
RWNETExportFunc(RWTValOrderedVector<RWSocketAttribute>)
rwSocketSelect(const RWTValOrderedVector<RWSocketAttribute>&, double timeout);

/************* DEPRECATED ***************************************/
/**/ RWNETExportFunc(RWTValOrderedVector<RWSocketAttribute>)
/**/ select(const RWTValOrderedVector<RWSocketAttribute>& attributes);
/**/ RWNETExportFunc(RWTValOrderedVector<RWSocketAttribute>)
/**/ select(const RWTValOrderedVector<RWSocketAttribute>& att, double timeout);
/************* PREFER THE ABOVE-MENTIONED... ********************/

RWNETExportFunc(RWBoolean) operator==(const RWSocketAttribute& x, const RWSocketAttribute& y);// True if [[x]] and [[*this]] refer to the same attribute on the same
// socket.

/*
 * I sure wish I could allow type safe "z = x|y" where x,y,z are 
 * instances of RWSocketAttribute::Attribute.  But the following does
 * not compile (because an overloaded operator must have at least one
 * argument of class type) and I'm not sure how else to do this.
 */
#if 0
RWNETExportFunc(RWSocketAttribute::Attribute)
operator|(RWSocketAttribute::Attribute x, RWSocketAttribute::Attribute y)
{ return (RWSocketAttribute::Attribute)(x|y); }
// Type safe oring of atomic attributes together to aggregate them.
#endif

#endif
