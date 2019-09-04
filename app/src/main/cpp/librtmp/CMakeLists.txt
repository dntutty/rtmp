

cmake_minimum_required(VERSION 3.4.1)


file(GLOB rtmp_src *.c)

#纯C项目必须使用CMAKE_C_FLAG 非纯C项目用CMAKE_CXX_FLAGS -定义宏参数
set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -DNO_CRYPTO")

add_library(  
        rtmp
        STATIC
        ${rtmp_src})

