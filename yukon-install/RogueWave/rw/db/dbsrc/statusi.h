#ifndef __RWDB_STATUSI_H__
#define __RWDB_STATUSI_H__

/**************************************************************************
 *
 * $Id$
 *
 ***************************************************************************
 *
 * Copyright (c) 1994-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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
 **************************************************************************
 *
 * Class RWDBStatusImp encapsulates the status of an object or operation.
 * Most database-specific implementation classes derive from RWDBStatus.
 * RWDBStatus supports error-handling through explicit testing (isValid()),
 * through callbacks(setErrorHandler()), and through exceptions (raise()).
 * See the Users Guide for details.
 *
 **************************************************************************/

#include <rw/db/dbref.h>
#include <rw/db/status.h>


class RWDBExport RWDBStatusImp : public RWDBReference
{
public:
    friend class RWDBExport RWDBStatus;

    RWDBStatusImp(
                  const RWCString& message,
                  const RWCString& vendorMessage1,
                  const RWCString& vendorMessage2,
                  RWDBStatus::ErrorCode  errorCode = RWDBStatus::notInitialized,
                  long             vendorError1 = 0,
                  long             vendorError2 = 0 ,
                  RWBoolean        isTerminal = FALSE );

    virtual ~RWDBStatusImp();

    // Mutator functions (see comment above regarding defaults):
    // NB: setError calls error handler; changeError doesn't. Otherwise
    // they are identical.
    virtual void          setError(RWDBStatus::ErrorCode        code,
                                   RWBoolean        isTerminal,
                                   const RWCString& message,
                                   const RWCString& vendorMessage1,
                                   const RWCString& vendorMessage2,
                                   long             vendorError1 = 0,
                                   long             vendorError2 = 0);

    virtual void          changeError(RWDBStatus::ErrorCode        code,
                                      RWBoolean        isTerminal,
                                      const RWCString& message,
                                      const RWCString& vendorMessage1,
                                      const RWCString& vendorMessage2,
                                      long             vendorError1 = 0,
                                      long             vendorError2 = 0);

    void          raise() const;  // Raises exception if available,
                                  // otherwise aborts
    inline void          setErrorHandler(RWDBStatus::ErrorHandler newHandler)
      { errorHandler_ = newHandler; }

    // Accessor functions:
    inline RWDBStatus::ErrorCode     errorCode() const
      { return errorCode_; }
    inline long          vendorError1() const
      { return vendorError1_; }
    inline long          vendorError2() const
      { return vendorError2_; }
    inline RWBoolean     isValid() const
      { return errorCode_ == RWDBStatus::ok; }
    inline RWBoolean     isTerminal() const
      { return isTerminal_; }
    inline RWCString     message() const
      { return message_; }
    inline RWCString     vendorMessage1() const
      { return vendorMessage1_; }
    inline RWCString     vendorMessage2() const
      { return vendorMessage2_; }
    inline RWDBStatus::ErrorHandler errorHandler() const
      { return errorHandler_; }

    RWDBStatusImp *pattern() const;

protected:

    RWDBStatus::ErrorHandler  errorHandler_;
    RWDBStatus::ErrorCode     errorCode_;         // Generic error code
    RWBoolean                 isTerminal_;        // TRUE if cannot recover
    RWCString                 message_;           // Message string
    RWCString                 vendorMessage1_;    // Vendor-specific message
    RWCString                 vendorMessage2_;    // Vendor-specific message
    long                      vendorError1_;      // Vendor-specific code
    long                      vendorError2_;      // Vendor-specific code

private :
// not implemented
    RWDBStatusImp(const RWDBStatusImp& statusImp);     
    RWDBStatusImp& operator=(const RWDBStatusImp& statusImp); 
};

#endif 
