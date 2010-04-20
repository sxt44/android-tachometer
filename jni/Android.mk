# Copyright (C) 2009 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#
LOCAL_PATH:= /cygdrive/c/workspaces/android/ffttest/jni

include $(CLEAR_VARS)

LOCAL_MODULE    := fft_fix
LOCAL_CFLAGS    := -Werror -DFIXED_POINT=16
LOCAL_SRC_FILES := fix_fft.c kiss_fft.c kiss_fftr.c
LOCAL_LDLIBS    := -llog

include $(BUILD_SHARED_LIBRARY)
