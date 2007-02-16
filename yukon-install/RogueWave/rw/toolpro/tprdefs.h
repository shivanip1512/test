#ifndef __RW_TPR_DEFS_H__
#define __RW_TPR_DEFS_H__
/***************************************************************************
 *
 * tprdefs.h
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
 * ToolPro Defs: product-wide defines
 * 
 */

/* 
 * Protect users from the cma wrappers hack.
 * If you #include <pthread.h> or <dce/pthread.h>, then system calls
 * such as send, read, write, etc are turned, via preprocessing, into
 * cma_send, cma_read, cma_write, etc. Preprocessors do not respect
 * scoping, so we have to cope. How? By _always_ including the thread
 * header at the very top of our code. That way all the places in our
 * library that might be mangled by the macro will _always_ be
 * mangled. So, if you see "cma_something" in a compiler or linker message, 
 * where you expected plain "something", that is why.
 */

// Is this a posix-like multi-thread build?
#if defined RW_MULTI_THREAD
#  if defined(RW_POSIX_D10_THREADS) || defined(RW_DCE_THREADS)
   /* HP's iostream lib was compiled without cma wrappers */
#    if defined(__hpux) && defined(hpux) && !defined(__CLCC__)
#      include <iostream.h>
#     endif 
#    include <rw/compiler.h>          /* get needed macros */
#    if defined RW_DCE_THREADS
#      if defined(RW_NO_DCE_PTHREAD_H)
#        include <pthread.h>
#      else
#        include <dce/pthread.h>
#      endif
#    else
#      include <pthread.h>
#    endif /* RW_DCE_THREADS */
#  endif /* some thread package */
#endif /* RW_MULTI_THREAD */

#include <rw/defs.h>
#define USE_RWTOOLS 1         // always using tools

/*
 * Check the version of Orbix being used to find out if we need
 * to delete the pointers ourselves, or if Orbix will do it for
 * us. Orbix versions 2.0 and earlier need us to take care of it.
 * In versions 2.1 and 2.2 Orbix takes care of it, and with
 * version 2.3 we need to take care of it once agian.
 */
#if !defined(RW_ORBIX_2_1) && !defined(RW_ORBIX_2_2) && !defined(RW_ORBIX_3_0)
#  define RW_ORBIX_POINTER_DELETE
#endif

#endif /* __RW_TPR_DEFS_H__ */
