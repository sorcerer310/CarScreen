# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)
project("carscreen")

#find_package(${CMAKE_SOURCE_DIR})
#set(CMAKE_VERBOSE_MAKEFILE on)
#set(CMAKE_SYSROOT D:/works/AndroidStudioProjects/CarScreen\app\src\main\cpp\include\assimp\)

include_directories(
#        BEFORE SYSTEM
        egl
        include
        include/assimp
        glm
        model
        model3d
        render
        util
)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

file(GLOB src-files
        ${CMAKE_SOURCE_DIR}/*.cpp
        ${CMAKE_SOURCE_DIR}/util/*.cpp
        ${CMAKE_SOURCE_DIR}/render/*.cpp
#        ${CMAKE_SOURCE_DIR}/model3d/*.cpp
#        ${CMAKE_SOURCE_DIR}/model/*.cpp
        ${CMAKE_SOURCE_DIR}/glm/*.cpp
        ${CMAKE_SOURCE_DIR}/egl/*.cpp
        )

add_library( # Sets the name of the library.
        native-render
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        ${src-files}
)



# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
              log-lib

              # Specifies the name of the NDK library that
              # you want CMake to locate.
              log )

set(jnilibs ${CMAKE_SOURCE_DIR}/../jniLibs)

add_library(assimp SHARED IMPORTED)
set_target_properties(assimp PROPERTIES IMPORTED_LOCATION ${jnilibs}/${ANDROID_ABI}/libassimp.so)
#set_target_properties(assimp PROPERTIES IMPORTED_LOCATION ${jnilibs}/libassimp.so)

#add_library(opencv_java3 SHARED IMPORTED)
#set_target_properties(opencv_java3 PROPERTIES IMPORTED_LOCATION ${jnilibs}/libopencv_java3.so)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.
set(third-party-libs
        assimp
#        opencv_java3
)



target_link_libraries( # Specifies the target library.
        native-render
       # Links the target library to the log library
       # included in the NDK.
        ${log-lib}
#        ${third-party-libs}
        EGL
        GLESv3
        android
)