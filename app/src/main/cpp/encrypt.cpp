#include "com_app_rzm_utils_EncryptUtils.h"
#include "md5.h"
#include <string>
#include <android/log.h>

using namespace std;

#define LOGI(FORMAT,...) __android_log_print(ANDROID_LOG_INFO,"renzhenming",FORMAT,##__VA_ARGS__);
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR,"renzhenming",FORMAT,##__VA_ARGS__);
#define MD5_KEY "renzhenming"
#define PACKAGE_NAME "com.app.rzm"

//签名校验是否通过，否返回-1
static int signature_verify = -1;

//app包名
static char* PACKAGE_NAME = "com.app.rzm";

//app签名
static char* APP_SIGNATURE = "308201dd30820146020101300d06092a864886f70d010105050030373116301406035504030c0d416e64726f69642044656275673110300e060355040a0c07416e64726f6964310b3009060355040613025553301e170d3137303730343233303635325a170d3437303632373233303635325a30373116301406035504030c0d416e64726f69642044656275673110300e060355040a0c07416e64726f6964310b300906035504061302555330819f300d06092a864886f70d010101050003818d0030818902818100968919d3d2fb955735c0be2e59b068dd5ebf784c9c5c7d1375dcaf392011d3b1ff4629dc85f03ffa6b2051faf65eff9f68cd147d46721a11a386abde4c534b75e7c12a9cf51398d0413843cfd1b89e61d02b7f5ee068be72e74448c980314508fe4078d676efd71e1a3879da9e403f56797988c52a76965e0e929b25d2c616310203010001300d06092a864886f70d0101050500038181006ba292fc04a49c8a004a5d6e72c4af9afb3d4542a1d66d9b10fe2ba2902f7b4b00db61c50ce0d4a0dcfa5dc6c62b8393cd08b86372c8559c62bb7bbbad1fea572a75c8fe00f8bfaab9ee92f0a7363a229b849f9051e9567c1d50d5b4ba59c169afd8ff9eca32f3f2e97e501780a12e28b7113ae991cdc46cea8e60104637feac";
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
JNIEXPORT jstring JNICALL Java_com_app_rzm_utils_EncryptUtils_encrypt
  (JNIEnv *env, jclass jclazz,jobject context,jstring jparam){

    if(signature_verify == -1){
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
JNIEXPORT void JNICALL Java_com_app_rzm_utils_EncryptUtils_checkSignature
  (JNIEnv *env, jclass jclazz, jobject context){
  //1.获取包名  通过Context的getPackageName方法获取

  //获取Context对象的class
  jclass context_class = env->GetObjectClass(context);
  //获取getPackageName的方法id
  jmethodID context_method_id = env->GetMethodID(context_class,"getPackageName","()Ljava/lang/String;")
  //调用getPackageName方法
  jstring package_name = (jstring)env->CallObjectMethod(context,context_method_id);
  //转换为char*
  const char *c_package_name = (char *)env->GetStringUTFChars(package_name,NULL);

  //2.对比包名

  if(strcmp(c_package_name,PACKAGE_NAME)){
      return env->NewStringUTF("package name check err");
  }

  //3.获取签名（通过下边这种方式）
  //PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
  //Signature[] signatures = packageInfo.signatures;
  //LogUtils.d("signature:"+signatures[0].toCharsString());

  //获取Context中的getPackageManager方法id
  jmethodID get_package_manager_method_id = env->GetMethodID(context_class,"getPackageManager","()Landroid/content/pm/PackageManager;");
  //从Context中通过调用getPackageManager获取PackageManager对象
  jobject package_manager = env->CallObjectMethod(context,get_package_manager_method_id);
  //对比签名
//  jclass context_class = env->GetObjectClass(context);
//
//
//  //获取应用包名 通过Context的getPackageName方法获取
//
//  jmethodID getPackageName_method_id = env->GetMethodID(context_class,"getPackageName","()Ljava/lang/String;");
//  jstring package_name = (jstring) env->CallObjectMethod(context, getPackageName_method_id);
//  const char *c_package_name = env->GetStringUTFChars(package_name,NULL);
//  if (strcmp(c_package_name,PACKAGE_NAME) != 0){
//      LOGI("package name not right");
//      return -1;
//  }else{
//      LOGI("package name check right");
//  }
//  //获取PackageManager
//
//
//  //获取方法签名  javap -s -p java.util.Date
//  jmethodID g_method_id = env->GetMethodID(context_class,"getPackageManager","()Landroid/content/pm/PackageManager");
//  jobject p_manager = env->CallObjectMethod(context,g_method_id);
//  return true;
}

