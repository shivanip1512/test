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
 * originally based on software copyright (c) 2001 International
 * Business Machines, Inc., http://www.ibm.com .  For more information
 * on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 */

/*
 * $Id: XMLInteger.hpp,v 1.1 2002/06/05 16:38:58 Yao Exp $
 * $Log: XMLInteger.hpp,v $
 * Revision 1.1  2002/06/05 16:38:58  Yao
 * Added support for WCTP paging terminal, which can send pages to paging company via the internet.
 * A lib directory Xerces has been added to support XML parsing.
 *
 * Revision 1.1.1.1  2002/02/01 22:22:15  peiyongz
 * sane_include
 *
 * Revision 1.1  2001/08/16 21:53:52  peiyongz
 * new class creation
 *
 */

#ifndef XML_INTEGER_HPP
#define XML_INTEGER_HPP

class XMLUTIL_EXPORT XMLInteger
{
public:

	/**
	 * Constructs a newly allocated <code>XMLInteger</code> object 
     *
	 * @param      intVal   the <code>integer</code> 
	 */

	XMLInteger(const int intVal);

	~XMLInteger();

	/**
	 * Returns the built in integer value.
	 */
    int intValue() const;

private:

    // -----------------------------------------------------------------------
    //  Private data members
    //
	//  fData
    //     the value
    //
    // -----------------------------------------------------------------------
    int         fData;

};

inline XMLInteger::XMLInteger(const int intVal)
:fData(intVal)
{
}

inline XMLInteger::~XMLInteger()
{
}

inline int XMLInteger::intValue() const
{
    return fData;
}

#endif
