#ifndef __RWDB_ENVHANDL_H__
#define __RWDB_ENVHANDL_H__

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
 * RWDBEnvironmentHandle is a base class for database-specific 
 * " environment handles."
 * Each DBtools.h++ access library defines an environment handle for its
 * own database. The functionality provided by the environment handles can be 
 * expected to vary widely among access libraries. Check the access library 
 * specific documentation for the functinality supported by the environment
 * handle. The access library specific environment handle is also expected 
 * provide direct handle to the underlying API to set unsupported 
 * functionalities.  
 *
 **************************************************************************/


#include  <rw/db/defs.h>

class RWDBExport RWDBEnvironmentHandle {

public:
    RWDBEnvironmentHandle           ();
    virtual ~RWDBEnvironmentHandle  ();

        virtual RWBoolean foreignKeysFromView();

    virtual RWBoolean foreignKeysFromView(RWBoolean value);

private:
    RWDBEnvironmentHandle(const RWDBEnvironmentHandle& handle);
    RWDBEnvironmentHandle& operator=(const RWDBEnvironmentHandle& handle);
};

#endif

