#ifndef __ENCODINGINTERFACE__
#define __ENCODINGINTERFACE__

class EncodingFilter
{
	public:
		EncodingFilter(){};

		virtual int encode(const unsigned char* const plainText, long bufLen, unsigned char *& cipher)
		{
			cipher = NULL;
			return 0;
		};

		virtual int decode(const unsigned char* const cipher , long bufLen, unsigned char *& plainText)
		{
			plainText = NULL;
			return 0;
		};
};

#endif
