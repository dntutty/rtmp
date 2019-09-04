  

cmake_minimum_required(VERSION 3.4.1)

#引入指定目录下的CmakeLists.txt
add_subdirectory(librtmp )
  
#导入头文件的路径
include_directories(librtmp libx264/include)


#add_subdirectory()
#set(CMAKE_CXX_FLAGS "")
#编译静态库时以上两种导入库的方式都可以
#动态库使用第二种
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/libx264/libs/${CMAKE_ANDROID_ARCH_ABI}")


#创建目标库，并设为共享库
add_library(   
        native-lib
        SHARED
        native-lib.cpp)

#链接依赖库到目标库
target_link_libraries(   
        native-lib
        rtmp
        log)