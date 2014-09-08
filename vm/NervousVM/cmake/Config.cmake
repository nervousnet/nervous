# detect the OS
# windows based system
if(${CMAKE_SYSTEM_NAME} MATCHES "Windows")
	set(NERVOUSVM_OS_WINDOWS 1)
# linux based system
elseif(${CMAKE_SYSTEM_NAME} MATCHES "Linux")
	set(NERVOUSVM_OS_UNIX 1)
	# android
	if(ANDROID)
		set(NERVOUSVM_OS_ANDROID 1)
	# just linux
	else()
		set(NERVOUSVM_OS_LINUX 1)
	endif()
# freebsd based system
elseif(${CMAKE_SYSTEM_NAME} MATCHES "FreeBSD")
	set(NERVOUSVM_OS_FREEBSD 1)
# apple/darwin based system
elseif(${CMAKE_SYSTEM_NAME} MATCHES "Darwin")
	if(IOS)
		set(NERVOUSVM_OS_IOS 1)
		
		# set target framework and platforms
		set(CMAKE_OSX_SYSROOT "iphoneos")
		set(CMAKE_OSX_ARCHITECTURES "armv6;armv7;i386")
		set(CMAKE_XCODE_EFFECTIVE_PLATFORMS "-iphones;-iphonesimulator")
		
		# help the compiler detection script
		set(CMAKE_COMPILER_IS_GNUCXX 1)
	else()
		set(NERVOUSVM_OS_MACOSX 1)
		EXEC_PROGRAM(/usr/bin/sw_vers ARGS -productVersion OUTPUT_VARIABLE MACOSX_VERSION_RAW)
        STRING(REGEX REPLACE "10\\.([0-9]).*" "\\1" MACOSX_VERSION "${MACOSX_VERSION_RAW}")
        if(${MACOSX_VERSION} LESS 7)
            message(FATAL_ERROR "Unsupported version of OS X: ${MACOSX_VERSION_RAW}")
			return()
		endif()
	endif()
elseif(${CMAKE_SYSTEM_NAME} MATCHES "Android")
	set(NERVOUSVM_OS_ANDROID 1)
else()
	message(FATAL_ERROR "Unsupported OS")
	return()
endif()

# detect the compiler and its version
if(CMAKE_CXX_COMPILER MATCHES ".*clang[+][+]" OR CMAKE_CXX_COMPILER_ID STREQUAL "Clang")
   # CMAKE_CXX_COMPILER_ID is an internal CMake variable subject to change,
   # but there is no other way to detect CLang at the moment
   set(NERVOUSVM_COMPILER_CLANG 1)
   execute_process(COMMAND "${CMAKE_CXX_COMPILER}" "--version" OUTPUT_VARIABLE CLANG_VERSION_OUTPUT)
   string(REGEX REPLACE ".*clang version ([0-9]+\\.[0-9]+).*" "\\1" SFML_CLANG_VERSION "${CLANG_VERSION_OUTPUT}")
elseif(CMAKE_COMPILER_IS_GNUCXX)
    set(NERVOUSVM_COMPILER_GCC 1)
    execute_process(COMMAND "${CMAKE_CXX_COMPILER}" "-dumpversion" OUTPUT_VARIABLE GCC_VERSION_OUTPUT)
    string(REGEX REPLACE "([0-9]+\\.[0-9]+).*" "\\1" SFML_GCC_VERSION "${GCC_VERSION_OUTPUT}")
    execute_process(COMMAND "${CMAKE_CXX_COMPILER}" "--version" OUTPUT_VARIABLE GCC_COMPILER_VERSION)
    string(REGEX MATCHALL ".*(tdm[64]*-[1-9]).*" SFML_COMPILER_GCC_TDM "${GCC_COMPILER_VERSION}")
    execute_process(COMMAND "${CMAKE_CXX_COMPILER}" "-dumpmachine" OUTPUT_VARIABLE GCC_MACHINE)
    string(STRIP "${GCC_MACHINE}" GCC_MACHINE)
    if(${GCC_MACHINE} MATCHES ".*w64.*")
        set(NERVOUSVM_COMPILER_GCC_W64 1)
    endif()
elseif(MSVC)
    set(NERVOUSVM_COMPILER_MSVC 1)
    if(MSVC_VERSION EQUAL 1400)
        set(NERVOUSVM_MSVC_VERSION 8)
    elseif(MSVC_VERSION EQUAL 1500)
        set(NERVOUSVM_MSVC_VERSION 9)
    elseif(MSVC_VERSION EQUAL 1600)
        set(NERVOUSVM_MSVC_VERSION 10)
    elseif(MSVC_VERSION EQUAL 1700)
        set(NERVOUSVM_MSVC_VERSION 11)
    elseif(MSVC_VERSION EQUAL 1800)
        set(NERVOUSVM_MSVC_VERSION 12)
    endif()
else()
    message(FATAL_ERROR "Unsupported compiler")
    return()
endif()

# define the install directory for miscellaneous files
if(NERVOUSVM_OS_WINDOWS OR NERVOUSVM_OS_IOS)
    set(INSTALL_MISC_DIR .)
elseif(NERVOUSVM_OS_LINUX OR NERVOUSVM_OS_FREEBSD OR NERVOUSVM_OS_MACOSX)
    set(INSTALL_MISC_DIR share/NERVOUSVM)
elseif(NERVOUSVM_OS_ANDROID)
    set(INSTALL_MISC_DIR ${ANDROID_NDK}/sources/nervousvm)
endif()
		
