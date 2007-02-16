#ifndef __RWDB_STATUS_H__
#define __RWDB_STATUS_H__

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
 * Class RWDBStatus encapsulates the status of an object or operation.
 * Most database-specific implementation classes derive from RWDBStatus.
 * RWDBStatus supports error-handling through explicit testing (isValid()),
 * through callbacks(setErrorHandler()), and through exceptions (raise()).
 * See the Users Guide for details.
 *
 **************************************************************************/


#include <rw/db/defs.h>
#include <rw/db/tracer.h>
#include <rw/db/future.h>

#include <rw/cstring.h>


class RWDBExport RWDBStatus : public RWDBFuture
{
public:
  typedef void (*ErrorHandler)(const RWDBStatus& status);

  enum ErrorCode {
    ok = 0,              // No error
    noMem,               // Out of Memory
    notInitialized,      // Object never initialized
    typeConversion,      // Type conversion error
    dbNotFound,          // Database not registered
    serverError,         // Error reported by server
    serverMessage,       // Message from server
    vendorLib,           // Error in vendor's library
    notConnected,        // Lost connection
    endOfFetch,          // End of fetch
    invalidUsage,        // invalid usage of object
    columnNotFound,      // Column does not exist
    invalidPosition,     // invalid positioning within object, i.e. bounds error
    notSupported,        // Unsupported feature
    nullReference,       // Null reference parameter
    notFound,            // Database Object not found
    missing,             // Required piece of information is missing
    noMultiReaders,      // This object cannot support multiple readers
    noDeleter,           // This object cannot support deletions
    noInserter,          // This object cannot support insertions
    noUpdater,           // This object cannot support updates
    noReader,            // This object cannot support readers
    noIndex,             // This object cannot support indices
    noDrop,              // This object cannot be dropped
    wrongConnection,     // Incorrect connection was supplied
    noPrivilege,         // This object cannot support privileges
    noCursor,            // This object cannot support cursors
    cantOpen,            // Unable to open
    applicationError,     // For errors produced at the application level
    notReady             // For future use
  };
  /*
  ** Not all compilers support default values for "complicated"
  ** arguments. These overloads mimic default values for CStrings.
  */
  RWDBStatus( ErrorCode errorCode = notInitialized,
              long      vendorError1 = 0,
              long      vendorError2 = 0,
              RWBoolean isTerminal = FALSE );
  RWDBStatus( ErrorHandler errorHandler, 
              ErrorCode errorCode = notInitialized,
              long      vendorError1 = 0,
              long      vendorError2 = 0,
              RWBoolean isTerminal = FALSE );
  RWDBStatus( const RWDBFuture& future, 
              ErrorCode errorCode = notInitialized,
              long      vendorError1 = 0,
              long      vendorError2 = 0,
              RWBoolean isTerminal = FALSE );
  RWDBStatus( const RWCString& message,
              ErrorCode        errorCode = notInitialized,
              long             vendorError1 = 0,
              long             vendorError2 = 0,
              RWBoolean        isTerminal = FALSE );
  RWDBStatus( const RWCString& message,
              const RWCString& vendorMessage1,
              ErrorCode        errorCode = notInitialized,
              long             vendorError1 = 0,
              long             vendorError2 = 0,
              RWBoolean        isTerminal = FALSE );
  RWDBStatus( const RWCString& message,
              const RWCString& vendorMessage1,
              const RWCString& vendorMessage2,
              ErrorCode        errorCode = notInitialized,
              long             vendorError1 = 0,
              long             vendorError2 = 0,
              RWBoolean        isTerminal = FALSE );
  RWDBStatus( RWDBStatusImp *impl );
  RWDBStatus(const RWDBStatus& status);     
  RWDBStatus& operator=(const RWDBStatus& status);
  virtual ~RWDBStatus();

  // Mutator functions (see comment above regarding defaults):
  // NB: setError calls error handler; changeError doesn't. Otherwise
  // they are identical.
  void          setError(ErrorCode code,
                         RWBoolean isTerminal,
                         long      vendorError1 = 0,
                         long      vendorError2 = 0);
  void          setError(ErrorCode code,
                         RWBoolean isTerminal,
                         const     RWCString& message,
                         long      vendorError1 = 0,
                         long      vendorError2 = 0);
  void          setError(ErrorCode        code,
                         RWBoolean        isTerminal,
                         const RWCString& message,
                         const RWCString& vendorMessage1,
                         long             vendorError1 = 0,
                         long             vendorError2 = 0);
  void          setError(ErrorCode        code,
                         RWBoolean        isTerminal,
                         const RWCString& message,
                         const RWCString& vendorMessage1,
                         const RWCString& vendorMessage2,
                         long             vendorError1 = 0,
                         long             vendorError2 = 0);

  void          changeError(ErrorCode code,
                            RWBoolean isTerminal,
                            long      vendorError1 = 0,
                            long      vendorError2 = 0);
  void          changeError(ErrorCode        code,
                            RWBoolean        isTerminal,
                            const RWCString& message,
                            long             vendorError1 = 0,
                            long             vendorError2 = 0);
  void          changeError(ErrorCode        code,
                            RWBoolean        isTerminal,
                            const RWCString& message,
                            const RWCString& vendorMessage1,
                            long             vendorError1 = 0,
                            long             vendorError2 = 0);
  void          changeError(ErrorCode        code,
                            RWBoolean        isTerminal,
                            const RWCString& message,
                            const RWCString& vendorMessage1,
                            const RWCString& vendorMessage2,
                            long             vendorError1 = 0,
                            long             vendorError2 = 0);

  void          setErrorHandler(ErrorHandler newHandler);
  void          raise() const;  // Raises exception if available,
                // otherwise aborts

  // Accessor functions:
  ErrorCode     errorCode() const;
  ErrorHandler  errorHandler() const;
  long          vendorError1() const;
  long          vendorError2() const;
  RWBoolean     isValid() const;
  RWBoolean     isTerminal() const; 
  RWCString     message() const;
  RWCString     vendorMessage1() const;
  RWCString     vendorMessage2() const;

    // This function creates a new RWDBStatus by making a deep copy if the
    // current status is ok, otherwise a shallow copy
  RWDBStatus    pattern() const;

private:
  RWDBStatusImp *impl_;

};

#endif
