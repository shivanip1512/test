#ifndef __RWWTOKEN_H__
#define __RWWTOKEN_H__

/*
 * RWTokenizer --- converts strings into sequences of tokens
 *
 * $Id$
 *
 * Copyright (c) 1989-1999 Rogue Wave Software, Inc.  All Rights Reserved.
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
 * Commercial Computer Software � Restricted Rights at 48 CFR 52.227-19,
 * as applicable.  Manufacturer is Rogue Wave Software, Inc., 5500
 * Flatiron Parkway, Boulder, Colorado 80301 USA.
 *
 ***************************************************************************
 *
 * $Log$
 * Revision 1.1  2007/02/16 16:54:06  jdayton
 * Adding current RogueWave (yes, DST patched) files so that the build process can continue to move away from requiring mapped network drive locations for its needed files...
 *
 * Revision 7.6  1996/07/18 04:06:00  hart
 *  ObjectStore changes
 *
 * Revision 7.5  1996/05/07 18:17:37  kyle
 * Added export pragma for Mac code fragments
 *
 * Revision 7.4  1996/02/18 01:50:42  griswolf
 * Replace tabs with spaces, per Rogue Wave standard.
 *
 * Revision 7.3  1995/09/05 21:35:20  jims
 * Use new copyright macro
 *
 * Revision 7.2  1995/07/10  18:19:38  griswolf
 * Scopus #1356: Correct handling of embedded nulls.
 *
 * Revision 7.1  1994/10/16  03:13:28  josh
 * Merged 6.1 and 7.0 development trees
 *
 * Revision 6.2  1994/07/12  19:58:19  vriezen
 * Update Copyright notice
 *
 * Revision 6.1  1994/04/15  19:48:55  vriezen
 * Move all files to 6.1
 *
 * Revision 1.2  1993/09/10  02:56:53  keffer
 * Switched RCS idents to avoid spurious diffs
 *
 * Revision 1.1  1993/02/11  02:05:23  myersn
 * Initial revision
 *
 */

#include "rw/wstring.h"

#ifdef RW_PRAGMA_EXPORT
#pragma export on
#endif

class RWExport RWWTokenizer {
  const RWWString*      theString;
  const wchar_t*        place;
public:
  RWWTokenizer(const RWWString& s);     // Construct to lex a string

  // Advance to next token, delimited by s:
  RWWSubString          operator()(const wchar_t* s);
  RWWSubString          operator()(const wchar_t* s, size_t); // nulls ok
  RWWSubString          operator()(); // { return operator()(W" \t\n\0",4);}

  RW_TYPESPEC  /* This macro usually expands to nothing */
};

#ifdef RW_PRAGMA_EXPORT
#pragma export off
#endif

#endif /* __RWWTOKEN_H__ */
