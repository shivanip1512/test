#include "precompiled.h"

#include "checksums.h"

#include <openssl/md5.h>

namespace Cti {

IM_EX_CTIBASE Md5Digest calculateMd5Digest(const std::vector<unsigned char>& data)
{
    Md5Digest result;

    static_assert(result.size() == MD5_DIGEST_LENGTH);

    MD5_CTX context;
    
    MD5_Init(&context);

    MD5_Update(&context, data.data(), data.size());

    MD5_Final(result.data(), &context);

    return result;
}

}