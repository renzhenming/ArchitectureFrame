
cmake_minimum_required(VERSION 3.4.1)

#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=gnu++11")
#判断编译器类型,如果是gcc编译器,则在编译选项中加入c++11支持
if(CMAKE_COMPILER_IS_GNUCXX)
set(CMAKE_CXX_FLAGS "-std=c++11 ${CMAKE_CXX_FLAGS}")
message(STATUS "optional:-std=c++11")
endif(CMAKE_COMPILER_IS_GNUCXX)

#####################################共享内容############################################

find_library( log-lib
              log )
find_library( jnigraphics-lib
              jnigraphics )

find_library( android-lib
              android )


include_directories( src/main/cpp/include/ffmpeg )
include_directories( src/main/cpp/include/libyuv )
include_directories( src/main/cpp/head )
include_directories( src/main/cpp/jpeg )
#####################################共享内容############################################

#####################################图片压缩############################################

#图片压缩
add_library( compress_image
             SHARED
             src/main/cpp/compress_image.cpp )


#导入类库,只是作为引用,不编译
add_library( libjpeg
             SHARED
             IMPORTED )
# 指定库的路径
set_target_properties( # Specifies the target library.
                       libjpeg
                       # Specifies the parameter you want to define.
                       PROPERTIES IMPORTED_LOCATION
                       # Provides the path to the library you want to import.
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libjpeg.so )



# 将预构建库与本地库相连
target_link_libraries( compress_image
                       libjpeg
                       ${jnigraphics-lib}
                       ${log-lib} )

#####################################图片压缩############################################

#####################################参数加密############################################

#参数加密
add_library( encrypt
             SHARED
             src/main/cpp/encrypt.cpp src/main/cpp/md5.cpp)

# 将预构建库与本地库相连
target_link_libraries( encrypt
                       ${log-lib} )

#####################################图片压缩############################################

#####################################增量更新############################################

#增量更新
add_library( bspatch
             SHARED
             src/main/cpp/bspatch.c )

#增量更新
add_library( bsdiff
             SHARED
             src/main/cpp/bsdiff.c )

# 将预构建库与本地库相连
target_link_libraries( bspatch
                       ${jnigraphics-lib}
                       ${log-lib} )

#####################################增量更新############################################


#####################################视频ffmpeg############################################

#音视频处理
add_library( myffmpeg
             SHARED
             src/main/cpp/video_player.c
             src/main/cpp/queue.c )




# 编解码(最重要的库)
add_library( avcodec
             SHARED
             IMPORTED )
# 指定库的路径
set_target_properties( # Specifies the target library.
                       avcodec
                       # Specifies the parameter you want to define.
                       PROPERTIES IMPORTED_LOCATION
                       # Provides the path to the library you want to import.
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavcodec-56.so )


#设备信息
add_library( avdevice
             SHARED
             IMPORTED )
# 指定库的路径
set_target_properties(
                       avdevice
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavdevice-56.so )
#滤镜特效处理库
add_library( avfilter
             SHARED
             IMPORTED )
# 指定库的路径
set_target_properties(
                       avfilter
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavfilter-5.so )

#封装格式处理库
add_library( avformat
             SHARED
             IMPORTED )
# 指定库的路径
set_target_properties(
                       avformat
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavformat-56.so)

#工具库(大部分库都需要这个库的支持)
add_library( avutil
             SHARED
             IMPORTED )
# 指定库的路径
set_target_properties(
                       avutil
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libavutil-54.so)
#后期处理
add_library( postproc
             SHARED
             IMPORTED )
# 指定库的路径
set_target_properties(
                       postproc
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libpostproc-53.so)

#音频采样数据格式转换库
add_library( swresample
             SHARED
             IMPORTED )
# 指定库的路径
set_target_properties(
                       swresample
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libswresample-1.so)

#视频像素数据格式转换
add_library( swscale
             SHARED
             IMPORTED )
# 指定库的路径
set_target_properties(
                       swscale
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libswscale-3.so)

#视频像素数据格式转换
add_library( yuv
             SHARED
             IMPORTED )
# 指定库的路径
set_target_properties(
                       yuv
                       PROPERTIES IMPORTED_LOCATION
                       ${CMAKE_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI}/libyuv.so)


# 将预构建库与本地库相连
target_link_libraries(
                       myffmpeg avcodec avdevice avfilter avformat avutil postproc swresample swscale yuv
                       ${android-lib}
                       ${jnigraphics-lib}
                       ${log-lib} )

#####################################视频ffmpeg############################################









