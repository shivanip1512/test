#ifndef __LANTRONICSENCRYPTION__
#define __LANTRONICSENCRYPTION__

#include "EncodingFilter.h"
#include <string>
#include <boost/shared_array.hpp>

using std::string;
using boost::shared_ptr;

class LantronixEncryptionImpl : public EncodingFilter
{
	public:
		enum{
			UPDHEADERSIZE=18//header is 16(IV) + 2(len)
		};

		LantronixEncryptionImpl();

		virtual int decode(const unsigned char* const cipher, long bufLen, unsigned char *& plainText);
		virtual int encode(const unsigned char* const plainText, long bufLen, unsigned char *& cipher);

		void setKey (string key);
		void setIV (unsigned char iv[]);

	private:

		void generateNewIV();

		bool _staticIv;
		unsigned char _iv[16];
		unsigned char _key[16];
};


#endif
