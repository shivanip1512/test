#ifndef _RWIAGENTIMPL_
#define _RWIAGENTIMPL_
/***************************************************************************
 *
 * agenti.h
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

#include <rw/toolpro/inetdefs.h>
#include <rw/cstring.h>
#include <rw/tphdict.h>
#include <rw/thr/barrier.h>
#include <rw/mutex.h>

/*
 * RWIAgentImpl
 *
 * RWIAgentImpl is an implementation class for RWIAgent. It provides
 * lock mechanisms for its interface to have synchronization control
 * in a multi-thread environment. It also supplies some basic methods
 * to record and retrieve properties associated with an agent for a
 * specific protocol.
 *  
 */

class RWINETExport RWIAgentImpl {

  public:

    RWIAgentImpl(void);
    // Default Constructor
    
    virtual ~RWIAgentImpl(void);
    // Destructor
    
    void lock(void);
    // Locks the implementation to prevent
    // access from another thread.
    
    void unlock(void);
    // Unlocks the implementation.
    
    void wait(void);
    // barrier to synchronize threads, guaranties thread
    // acquisition before allowing main thread to continue.
    
    virtual void setProperty(const RWCString& p, const RWCString& v);
    // Sets a named property p to the string v
    
    virtual RWCString getProperty(const RWCString& p);
    // Gets the named property p

    virtual void clearProperties(void);
    // Clears the property hash dictionary
    
  private:
  
    static unsigned hash(const RWCString& str);
    // Hash function for property dictionary
    
    RWTPtrHashDictionary<RWCString, RWCString RWDefHArgs(RWCString)> props_;
    // Property dictionary
    
    RWMutex     apiMutex_;
    // interface mutex
    
    RWBarrier   tSync_;
    // Barrier object used by wait method.
};

#ifdef RW_COMPILE_INSTANTIATE
#include <rw/toolpro/agenti.cc>
#endif

#endif
