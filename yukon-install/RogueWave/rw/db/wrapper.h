/**************************************************************************
 *
 * $Id:
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
 **************************************************************************/


/*****************************************************************
 * RWDBWrapper is a generic memory leak wrapper
 *
 *  Usage:
 *     LeakingResourceType *resource;
 *     RWDBWrapper <LeakingResourceType> resourceWrapper;
 *     resourceWrapper.setResource(resource);
 *       ...
 *     leakingFunctionCall(*resource);
 *       ...
 *     resourceWrapper.unwrap();
 ************************************************************/

template <class T> class RWDBWrapper {
  public:
    T* resource;
    RWBoolean destroy_;
    RWDBWrapper() {destroy_=TRUE;}
    RWDBWrapper(T* setR) {
       destroy_=TRUE;
       resource = setR;
    }
    ~RWDBWrapper() { if (destroy_) delete resource; }
    void setResource(T* set) {resource = set;}
    void unwrap() {destroy_ = FALSE;}
};

/*****************************************************************
 * RWDBArrayWrapper is a generic memory leak wrapper for arrays
 *
 *  Usage:
 *     LeakingResourceType *resource;
 *     RWDBArrayWrapper <LeakingResourceType> resourceWrapper;
 *     resourceWrapper.setResource(resource);
 *       ...
 *     leakingFunctionCall(*resource);
 *       ...
 *     resourceWrapper.unwrap();
 ************************************************************/

template <class T> class RWDBArrayWrapper {
  public:
    T* resource;
    RWBoolean destroy_;
    RWDBArrayWrapper() {destroy_=TRUE;}
    RWDBArrayWrapper(T* setR) {
       destroy_=TRUE;
       resource = setR;
    }
    ~RWDBArrayWrapper() { if (destroy_) delete [] resource; }
    void setResource(T* set) {resource = set;}
    void unwrap() {destroy_ = FALSE;}
};

/*****************************************************************
 *  Since virtually all the potental memory leaks in dbtools are
 *  from RWDBResultImp, this is a wrapper specific to that class.
 *  It should be more effiecent than using the template version.
 *  Usage:
 *     RWDBResultImp *res;
 *     RWDBResultImpWrapper resWrapper(res);
 *       ...
 *     leakingFunctionCall(*res);
 *       ...
 *     resWrapper.unwrap();
 ****************************************************/

#include <rw/db/dbsrc/resulti.h>

class RWDBAccessExport RWDBResultImpWrapper
{
  public:
    RWDBResultImp* resImp_;
    RWBoolean destroy_;

    RWDBResultImpWrapper( RWDBResultImp* resImp ) {
       destroy_ = TRUE;
       resImp_ = resImp;
       }
    ~RWDBResultImpWrapper() { if ( destroy_ )  delete resImp_;  }
    void unwrap( ) {destroy_ = FALSE;}
};


