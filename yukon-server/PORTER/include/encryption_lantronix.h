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
			UDPHEADERSIZE=18//header is 16(IV) + 2(len)
		};

		LantronixEncryptionImpl();

		virtual bool decode(const unsigned char* const cipher, long bufLen, vector<unsigned char>& pText);
		virtual bool encode(const unsigned char* const plainText, long bufLen, vector<unsigned char>& cipher);

		void setKey (string key);
		void setIV (unsigned char iv[]);

	private:

		void generateNewIV(char seed);

		bool _staticIv;
		unsigned char _iv[16];
		unsigned char _key[16];
};


#endif
