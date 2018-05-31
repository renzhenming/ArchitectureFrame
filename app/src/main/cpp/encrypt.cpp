#include "com_app_rzm_utils_EncryptUtils.h"
#include "md5.h"
#include <string>
#include <android/log.h>

using namespace std;

#define LOGI(FORMAT,...) __android_log_print(ANDROID_LOG_INFO,"renzhenming",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"renzhenming",FORMAT,##__VA_ARGS__);
#define MD5_KEY "renzhenming"

/**
 * 加密解密的过程：
 * 客户端通过定义的规则将参数加密后，将密文和铭文参数同时传递到服务器
 * 服务器收到参数进行解析，使用同样的加密算法将参数加密，然后对比此次
 * 得到的密文和客户端传递的密文是否相同，如果相同说明数据安全，没有被
 * 篡改，如果不同，则表示数据改变，不再发送数据到客户端
 *
 * 将加密方法打包到so库中的好处就是可以防止对方反编译看到我们的加密条件
 * 如果对方不知道我们是如何加密的，也就可以在一定程度上防止数据泄漏，但是
 * 只是单纯的这样做并不能保证绝对的安全，比如，我不需要知道你是怎么加密的
 * 只需要反编译apk后得到几个信息1.你应用的包名，2.你的so库，3.你的native方法
 * 的完整类名和方法名（native方法不能被混淆，混淆后无法使用，所以可以得到）
 * 只要得到这三个信息，我就可以创建包名相同方法名相同的一个应用，把so放进去
 * 然后就可以绕过密钥检查，轻松的调用你的接口了。
 *
 * 解决这个问题的方法就是在so库中加入签名验证，当调用加密方法对操作参数的时候
 * 验证此时应用签名是否是我们本应用的，如果不是，则表示当前应用是伪应用，直接返回
 * 防止上边那种恶意调用接口情况的出现。对签名做校验，也就是只允许指定的应用可以使用，
 * 类似在微信支付中，也有在官方管理后台申请和配置应用的的签名和包名，否则就禁止使用，
 * 签名和包名必须得要一致。
 *
 * @param env
 * @param jclazz
 * @param jparam
 * @return
 */
JNIEXPORT jstring JNICALL Java_com_app_rzm_utils_EncryptUtils_encryptForAndroid
  (JNIEnv *env, jclass jclazz,jobject context,jstring jparam){

    if(!checkApp(env, context)){
        return env->NewStringUTF("signature check err");
    }

    const char *param = env->GetStringUTFChars(jparam,NULL);

    //在参数末尾加上MD5_KEY，然后去掉前面四位
    string signature_str(param);
    signature_str.insert(0,MD5_KEY);
    signature_str = signature_str.substr(0,signature_str.length()-2);


    //md5加密
    MD5_CTX *ctx = new MD5_CTX();
    MD5Init(ctx);
    MD5Update(ctx,(unsigned char *)signature_str.c_str(),signature_str.length());
    unsigned char digest[16];
    MD5Final(ctx, digest);

    int i = 0;
    char szMd5[32] = {0};
    for(i = 0;i< 16 ; i++){
        LOGI("szMd5[%d]:%s",i,szMd5);
        //最终生成32位，不足前面补一位0
        //x 表示以十六进制形式输出 ,02 表示不足两位，前面补0输出；出过两位，不影响
        sprintf(szMd5,"%s%02x",szMd5,digest[i]);
    }

    env->ReleaseStringUTFChars(jparam,param);

    return env->NewStringUTF(szMd5);
}

jboolean checkApp(JNIEnv *env, jobject context) {
    //获取PackageManager
    jclass context_class = env->GetObjectClass(context);
    return true;
}
