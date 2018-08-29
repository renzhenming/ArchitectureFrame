cmake_minimum_required(VERSION 3.4.1)


#引入头文件位置
include_directories(src/main/cpp/include/ffmpeg)
include_directories(src/main/cpp/include/owner)

add_library(
    myffmpeg
    SHARED
    src/main/cpp/ffmpeg_player.c
)

# 编解码(最重要的库)
add_library(
    avcodec
    SHARED
    IMPORTED
)

#指定编码库的位置
set_target_properties(
    avcodec
    PROPERTIES IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavcodec-56.so
)

#设备信息
add_library(
    avdevice
    SHARED
    IMPORTED
)

#指定设备信息的位置
set_target_properties(
    avdevice
    PROPERTIES IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavdevice-56.so
)

#滤镜特效处理库
add_library(
    avfilter
    SHARED
    IMPORTED
)

#指定滤镜库位置
set_target_properties(
    avfilter
    PROPERTIES IMPORTED_LOCATION
    ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavfilter-5.so
)