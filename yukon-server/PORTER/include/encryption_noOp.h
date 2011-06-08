#ifndef __ENCODINGNOOP__
#define __ENCODINGNOOP__

/*****************************************************************************
*
*    FILE NAME: encoding_noOp.h
*
*    DATE: 10/15/2008
*
*    AUTHOR: Thain Spar
*
*    DESCRIPTION: No op implementation of the EncodingFilter.
*
****************************************************************************
*/
#include "EncodingFilter.h"

class NoOpEncryption : public EncodingFilter
{
	public:

		NoOpEncryption();

		bool encode(const unsigned char* const plainText, long bufLen, std::vector<unsigned char>& cipher);
		bool decode(const unsigned char* const cipher , long bufLen, std::vector<unsigned char>& plainText);
};

#endif
