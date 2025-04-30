#include "dev_lukebemish_ziggradle_test_TestJNI.h"

JNIEXPORT jobject JNICALL Java_dev_lukebemish_ziggradle_test_TestJNI_test
  (JNIEnv* env, jclass callingCls) {
    jclass cls = (*env)->FindClass(env, "java/lang/Integer");
    jfieldID fid = (*env)->GetStaticFieldID(env, cls, "TYPE", "Ljava/lang/Class;");
    return (*env)->GetStaticObjectField(env, cls, fid);
}
