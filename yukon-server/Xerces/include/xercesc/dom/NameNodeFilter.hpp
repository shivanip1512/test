/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2000 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Xerces" and "Apache Software Foundation" must
 *    not be used to endorse or promote products derived from this
 *    software without prior written permission. For written
 *    permission, please contact apache\@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    nor may "Apache" appear in their name, without prior written
 *    permission of the Apache Software Foundation.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation, and was
 * originally based on software copyright (c) 1999, International
 * Business Machines, Inc., http://www.ibm.com .  For more information
 * on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

/*
 * $Log: NameNodeFilter.hpp,v $
 * Revision 1.1  2002/06/05 16:38:32  Yao
 * Added support for WCTP paging terminal, which can send pages to paging company via the internet.
 * A lib directory Xerces has been added to support XML parsing.
 *
 * Revision 1.1.1.1  2002/02/01 22:21:48  peiyongz
 * sane_include
 *
 * Revision 1.4  2000/02/24 20:11:30  abagchi
 * Swat for removing Log from API docs
 *
 * Revision 1.3  2000/02/15 23:17:37  andyh
 * Update Doc++ API comments
 * NameSpace bugfix and update to track W3C
 * Chih Hsiang Chou
 *
 * Revision 1.2  2000/02/06 07:47:33  rahulj
 * Year 2K copyright swat.
 *
 * Revision 1.1.1.1  1999/11/09 01:09:12  twl
 * Initial checkin
 *
 * Revision 1.2  1999/11/08 20:44:28  rahul
 * Swat for adding in Product name and CVS comment log variable.
 *
 */

// DOM_NameNodeFilter.h: interface for the DOM_NameNodeFilter class.
//
//////////////////////////////////////////////////////////////////////

#ifndef DOM_NameNodeFilter_HEADER_GUARD_
#define DOM_NameNodeFilter_HEADER_GUARD_


#include "DOM_NodeFilter.hpp"
#include "NodeFilterImpl.hpp"
#include "DOMString.hpp"
#include "DOM_Node.hpp"


class CDOM_EXPORT NameNodeFilter : public NodeFilterImpl
{
	public:
		NameNodeFilter();
		virtual ~NameNodeFilter();

    // The name to compare with the node name. If null, all node names
    //  are successfully matched.
    void setName(DOMString name);

    // Return the name to compare with node name.
    DOMString getName();

    // If match is true, the node name is accepted when it matches.
    //  If match is false, the node name is accepted when does not match.
    void setMatch(bool match) ;

    // Return match value.
    bool getMatch();

    virtual DOM_NodeFilter::FilterAction acceptNode(DOM_Node n);

	private:
    DOMString fName;
    bool fMatch;

};

#endif
