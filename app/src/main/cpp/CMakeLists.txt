cmake_minimum_required(VERSION 3.22.1)
project("sameboy_engine")

add_library(
    sameboy_core
    SHARED
    jni_bridge.cpp
    sameboy/sameboy_core.c
)

find_library(log-lib log)
find_library(android-lib android)

target_link_libraries(
    sameboy_core
    ${log-lib}
    ${android-lib}
)
