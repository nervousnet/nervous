LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := nervous
LOCAL_SRC_FILES := \
	/Users/marica/nervous_android/nervous/src/main/jni/Android.mk \

LOCAL_C_INCLUDES += /Users/marica/nervous_android/nervous/src/main/jni
LOCAL_C_INCLUDES += /Users/marica/nervous_android/nervous/src/debug/jni

include $(BUILD_SHARED_LIBRARY)
