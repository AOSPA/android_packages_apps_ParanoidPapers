#
# Copyright (C) 2012-2020 Paranoid Android
#
# SPDX-License-Identifier: GPL-2.0
#

LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/res

LOCAL_SDK_VERSION := current

LOCAL_PACKAGE_NAME := ParanoidPapers

LOCAL_USE_AAPT2 := true

LOCAL_AAPT_FLAGS := --auto-add-overlay

include $(BUILD_PACKAGE)
