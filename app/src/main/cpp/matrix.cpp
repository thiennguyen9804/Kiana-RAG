#include <jni.h>
#include <CL/cl.h>

namespace {
    void cal(const float a[], const float b[], float c[], int size) {
        for (int i = 0; i < size; i++) {
            c[i] = a[i] + b[i];
        }
    }
}

extern "C"
JNIEXPORT void JNICALL
Java_com_example_kianarag_util_matrix_MatrixAddition_cal(JNIEnv *env, jobject thiz,
                                                         jfloatArray a, jfloatArray b, jfloatArray c, jint size) {
    auto a_buffer= env->GetFloatArrayElements(a, nullptr);
    auto b_buffer = env->GetFloatArrayElements(b, nullptr);
    auto c_buffer = env->GetFloatArrayElements(c, nullptr);

    cal(a_buffer, b_buffer, c_buffer, size);
    env->ReleaseFloatArrayElements(a, a_buffer, JNI_ABORT);
    env->ReleaseFloatArrayElements(b, b_buffer, JNI_ABORT);
    env->ReleaseFloatArrayElements(c, c_buffer, 0);

}