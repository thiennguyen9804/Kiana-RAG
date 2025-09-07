#include <jni.h>

namespace {
    float sqL2Distance(const float a[], const float b[], const int size) {
        float sum = 0.0;
        for (int i = 0; i < size; i++) {
            float d = a[i] - b[i];
            sum += d * d;
        }

        return sum;
    }
}

extern "C"
JNIEXPORT jfloat JNICALL
Java_com_example_kianarag_util_DistanceKt_sqL2Distance(JNIEnv *env, jclass clazz, jfloatArray a,
                                                       jfloatArray b, jint size) {
    float* a_buffer = env->GetFloatArrayElements(a, nullptr);
    float* b_buffer = env->GetFloatArrayElements(b, nullptr);
    float result = sqL2Distance(a_buffer, b_buffer, size);
    env->ReleaseFloatArrayElements(a, a_buffer, JNI_ABORT);
    env->ReleaseFloatArrayElements(b, b_buffer, JNI_ABORT);
    return result;
}