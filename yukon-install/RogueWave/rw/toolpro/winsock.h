#ifndef __RWNET_WINSOCK_H__
#define __RWNET_WINSOCK_H__
/***************************************************************************
 *
 * winsock.h
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
 * RWWinSocket: A Windows socket
 * 
 * An RWWinSocket is a regular RWSocket that has been enhanced to support
 * the asynchronous Windows winsock calls.
 *
 * The RWWinSockInfo class serves two functions.  It provides information
 * on the specifics of the winsock DLL to which the application is
 * attached.  More importantly, its constructor initializes the winsock
 * DLL for the application and the destructor cleans up.  An RWWinSockInfo
 * must exist while using winsock RWSocket, RWSockAddr, RWWinSock, or any
 * other classes that use the winsock DLL.  There needs to be an RWWinSockInfo
 * object in each application, not just in each DLL.  The easiest way to
 * accomplish this, provided you do not use Net.h++ at static initialization
 * time, is to define an RWWinSockInfo object as the first thing in your
 * WinMain.
 * 
 * The [[RWWinSockInfo]] class also encapsulates the blocking hook mechanism
 * in winsock.  You can set a blocking hook in the constructor - the
 * previous blocking hook will be restored in the destructor.  Note that 
 * the semantics of the blocking hook are different for Windows NT and
 * regular 16 bit Windows.  If you want to write a portable application,
 * use the constructor [[RWWinSockInfo(RWWinSockInfo::win16hook)]].
 * The mechanism of encapsulating blocking hooks with an enum is completely
 * non-extensible, but it is simple, and this is such a limited application
 * that I think it is ok.  Also, you can always use the variant that takes
 * a function pointer.
 */

#include <rw/toolpro/netdefs.h>
#include <rw/cstring.h>
#if defined(RWNET_WINSOCK)
#  if defined(__TURBOC__)
#    if __TURBOC__ >= 0x530
#       include <windows.h>
#    endif
#  endif
#include <winsock.h>
#endif

class RWNETExport RWWinSockInfo {
public:
  enum BlockingHook { win16hook };
#if defined(RWNET_WINSOCK)
  static FARPROC lookUpBlockingHook(BlockingHook);
#endif  
  // Common blocking hooks.  You can install these using the
  // constructor.  If you are in a Windows environment, you can
  // use [[lookUpBlockingHook]] to get the actual function pointer.
  // The advantage of the enum identifiers is that they are portable
  // between unix (where all the blocking hook stuff is no-ops) and
  // Windows.

  RWWinSockInfo();
  // Initialize the underlying winsock DLL.  If the DLL has already been
  // initialized by this application, then the constructor does nothing.
  // An exception of type [[RWNetWinsockInitErr]]
  // is thrown if we can't successfully
  // initialize the winsock DLL.
  // The blocking hook is not changed.

  RWWinSockInfo(BlockingHook);
#if defined(RWNET_WINSOCK)
  RWWinSockInfo(FARPROC blockingHook);
#endif
  // Initialize the winsock DLL and install the blocking hook indicated.
  // The blocking hook will be restored when the [[RWWinSockInfo]] is
  // destroyed.
  // The version taking an instance of the BlockingHook
  // is portable, the other applies only to
  // to the Windows environment.

  ~RWWinSockInfo();
  // Clean up the winsock DLL if this is the outermost [[RWWinSockInfo]]
  // object.  If this is not the outermost [[RWWinSockInfo]] object, then
  // this does nothing.
  // If there is a blocking call in progress, the blocking call is cancelled.
  // If the blocking call was done using an [[RWSocket]], the call will
  // throw [[RWNetWinsockInitErr]].
  // If a new blocking hook was installed at construction time, the new one
  // is restored.

  RWCString description() const;
  // The vendor supplied description of the winsock DLL.  

private:
#if defined(RWNET_WINSOCK)
  WSADATA data_;  // information about this winsock DLL
  FARPROC previousBlockingHook_;  // restore this in dtor
#endif
};

#endif
