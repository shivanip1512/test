#ifndef IDOM_CDataSection_HEADER_GUARD_
#define IDOM_CDataSection_HEADER_GUARD_


/*
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * originally based on software copyright (c) 2001, International
 * Business Machines, Inc., http://www.ibm.com .  For more information
 * on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

/*
 * $Log: IDOM_CDATASection.hpp,v $
 * Revision 1.1  2002/06/05 16:38:37  Yao
 * Added support for WCTP paging terminal, which can send pages to paging company via the internet.
 * A lib directory Xerces has been added to support XML parsing.
 *
 * Revision 1.1.1.1  2002/02/01 22:21:55  peiyongz
 * sane_include
 *
 * Revision 1.2  2001/05/11 13:25:49  tng
 * Copyright update.
 *
 * Revision 1.1.1.1  2001/04/03 00:14:27  andyh
 * IDOM
 *
 */

#include <xercesc/util/XercesDefs.hpp>
#include "IDOM_Text.hpp"


/**
 * <code>DOM_CDataSection</code> objects refer to the data from an
 * XML CDATA section.  These are used to escape blocks of text containing  characters
 * that would otherwise be regarded as markup.
 *
 * <p>Note that the string data associated with the CDATA section may
 * contain characters that need to be escaped when appearing in an
 * XML document outside of a CDATA section.
 * <p> The <code>IDOM_CDATASection</code> class inherits from the
 * <code>DOM_CharacterData</code> class through the <code>Text</code>
 * interface. Adjacent CDATASection nodes are not merged by use
 * of the Element.normalize() method.
 */
class CDOM_EXPORT IDOM_CDATASection: public IDOM_Text {
protected:
    IDOM_CDATASection() {};
    IDOM_CDATASection(const IDOM_CDATASection &other) {};
    IDOM_CDATASection & operator = (const IDOM_CDATASection &other) {return *this;};


public:

    virtual ~IDOM_CDATASection() {};

};
#endif


