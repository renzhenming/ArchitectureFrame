#include "com_app_rzm_utils_EncryptUtils.h"
#include "md5.h"
#include <string>

using namespace std;

#define MD5_KEY "renzhenming"

JNIEXPORT jstring JNICALL Java_com_app_rzm_utils_EncryptUtils_encryptToMd5
  (JNIEnv *env, jclass jclazz, jstring jparam){

    const char *param = env->GetStringUTFChars(jparam,NULL);

    //在参数末尾加上MD5_KEY，然后去掉前面四位
    string signature_str(param);
    signature_str.insert(0,MD5_KEY);
    signature_str = signature_str.substr(0,signature_str.length()-2);

    //md5加密
//    MD5_CTX *ctx = new MD5_CTX();
//    MD5Init(ctx);
//    MD5Update(ctx,(unsigned char *)signature_str.c_str(),signature_str.length());
//    unsigned char digest[16];
//    MD5Final(digest,ctx);

    env->ReleaseStringUTFChars(jparam,param);
    return env->NewStringUTF(param);
}