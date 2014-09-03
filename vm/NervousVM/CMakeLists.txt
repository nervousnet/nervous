# cmake version requirement
cmake_minimum_required(VERSION 2.8)

macro(nervousvm_set_option var default type docstring)
	if(NOT DEFINED ${var})
		set(${var} ${default})
	endif()
	set(${var} ${${var}} CACHE ${type} ${docstring} FORCE)
endmacro()

# set a default build type
nervousvm_set_option(CMAKE_BUILD_TYPE Release STRING "Choose the type of build (Debug/Release)")

# project name
project(NervousVM)

# include the configuration file
include(${CMAKE_CURRENT_SOURCE_DIR}/cmake/Config.cmake)

# setup version numbers
set(VERSION_MAJOR 0)
set(VERSION_MINOR 1)
set(VERSION_PATCH 0)

# add the NervousVM header patch
include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include)

# add an option for choosing the build type (shared or static)
if(NOT (NERVOUSVM_OS_IOS OR NERVOUSVM_OS_ANDROID))
	nervousvm_set_option(BUILD_SHARED_LIBS TRUE BOOL "TRUE: build NervousVM shared, FALSE: build NervousVM static")
else()
	# never shared for iOS
	if(NERVOUSVM_OS_IOS)
		set(BUILD_SHARED_LIBS FALSE)
	# always shared for android
	elseif(NERVOUSVM_OS_ANDROID)
		set(BUILD_SHARED_LIBS TRUE)
	endif()
endif()

# add an option for building the API documentation
nervousvm_set_option(NERVOUSVM_BUILD_DOC FALSE BOOL "TRUE: Generate API-doc, FALSE: Don't generate API-doc")

# Mac OS X specific options
if(NERVOUSVM_OS_MACOSX)
    # add an option to build frameworks instead of dylibs (release only)
    nervousvm_set_option(NERVOUSVM_BUILD_FRAMEWORKS FALSE BOOL "TRUE: Build NervousVM as frameworks libraries (release only), FALSE: according to BUILD_SHARED_LIBS")

    # add an option to let the user specify a custom directory for frameworks installation
    nervousvm_set_option(CMAKE_INSTALL_FRAMEWORK_PREFIX "/Library/Frameworks" STRING "Frameworks installation directory")

    # add an option to automatically install Xcode templates
    nervousvm_set_option(NERVOUSVM_INSTALL_XCODE_TEMPLATES FALSE BOOL "TRUE to automatically install the Xcode templates, FALSE to do nothing about it. The templates are compatible with Xcode 4 and 5.")
endif()

# Android options
if(NERVOUSVM_OS_ANDROID)
    # force usage of the STL port
    set(ANDROID_USE_STLPORT TRUE)

    # make sure there's the android library available
    if (${ANDROID_NATIVE_API_LEVEL} LESS 9)
        message(FATAL_ERROR "API level must be equal or greater than 9")
    endif()

    # install everything in $NDK/sources/ because this path is appended by the NDK (convenient)
    set(CMAKE_INSTALL_PREFIX ${ANDROID_NDK}/sources/nervousvm)

    # we install libs in a subdirectory named after the ABI (lib/mips/*.so)
    set(LIB_SUFFIX "/${ANDROID_ABI}")

    # we save the original compilation command line to restore it later in Macro.cmake
    set(CMAKE_CXX_CREATE_SHARED_LIBRARY_WITH_STLPORT ${CMAKE_CXX_CREATE_SHARED_LIBRARY})
    set(CMAKE_CXX_CREATE_SHARED_LIBRARY_WITHOUT_STLPORT "<CMAKE_CXX_COMPILER> <CMAKE_SHARED_LIBRARY_CXX_FLAGS> <LANGUAGE_COMPILE_FLAGS> <LINK_FLAGS> <CMAKE_SHARED_LIBRARY_CREATE_CXX_FLAGS> <SONAME_FLAG><TARGET_SONAME> -o <TARGET> <OBJECTS> <LINK_LIBRARIES>")
endif()

# define NERVOUSVM_STATIC if not shared
if(NOT BUILD_SHARED_LIBS)
	add_definitions(-DNERVOUSVM_STATIC)
endif()

# remove SL security warnings with VC++
if(NERVOUSVM_COMPILER_MSVC)
	add_definitions(-D_CRT_SECURE_NO_DEPRECATE)
endif()

# define an option for choosing between static and dynamic C runtime (on Windows)
if(NERVOUSVM_OS_WINDOWS)
	nervousvm_set_option(NERVOUSVM_USE_STATIC_STD_LIBS FALSE BOOL "TRUE: statically link to the STL, FALSE: Use as DLLs")
	
	# the following combination of flags is not valid
    if (BUILD_SHARED_LIBS AND NERVOUSVM_USE_STATIC_STD_LIBS)
        message(FATAL_ERROR "BUILD_SHARED_LIBS and NERVOUSVM_USE_STATIC_STD_LIBS cannot be used together")
    endif()

    # for VC++, we can apply it globally by modifying the compiler flags
    if(NERVOUSVM_COMPILER_MSVC AND NERVOUSVM_USE_STATIC_STD_LIBS)
        foreach(flag
                CMAKE_CXX_FLAGS CMAKE_CXX_FLAGS_DEBUG CMAKE_CXX_FLAGS_RELEASE
                CMAKE_CXX_FLAGS_MINSIZEREL CMAKE_CXX_FLAGS_RELWITHDEBINFO)
            if(${flag} MATCHES "/MD")
                string(REGEX REPLACE "/MD" "/MT" ${flag} "${${flag}}")
            endif()
        endforeach()
    endif()
endif()

if(NERVOUSVM_OS_MACOSX)
	if(NERVOUSVM_BUILD_FRAMEWORKS)
        if(NOT CMAKE_BUILD_TYPE STREQUAL "Release")
            message(FATAL_ERROR "CMAKE_BUILD_TYPE should be \"Release\" when NERVOUSVM_BUILD_FRAMEWORKS is TRUE")
            return()
        endif()
		
		if(NOT BUILD_SHARED_LIBS)
            message(FATAL_ERROR "BUILD_SHARED_LIBS should be TRUE when NERVOUSVM_BUILD_FRAMEWORKS is TRUE")
            return()
        endif()
    endif()
	
	# configure Xcode templates
    if(CMAKE_OSX_ARCHITECTURES)
        # maybe multiple arches are present in CMAKE_OSX_ARCHITECTURES
        # we simply need to replace ';' by ' ' (space) and store the result in XCODE_TEMPLATES_ARCH
        string(REPLACE ";" " " XCODE_TEMPLATES_ARCH "${CMAKE_OSX_ARCHITECTURES}")
    else()
        # no arch was provided to cmake, so we use the default one
        set(XCODE_TEMPLATES_ARCH "\$(NATIVE_ARCH_ACTUAL)")
    endif()
endif()

if(NERVOUSVM_OS_LINUX OR NERVOUSVM_OS_FREEBSD)
	set(PKGCONFIG_DIR lib${LIB_SUFFIX}/pkgconfig)
	if(NERVOUSVM_OS_FREEBSD)
		set(PKGCONFIG_DIR libdata/pkgconfig)
	endif()
	if(BUILD_SHARED_LIBS)
		nervousvm_set_option(NERVOUSVM_INSTALL_PKGCONFIG_FILES FALSE BOOL "TRUE: automatically install pkg-config files so other projects can find NervousVM")
		if(NERVOUSVM_INSTALL_PKGCONFIG_FILES)
			foreach(nervousvm_module in ITEMS all uuid)
				CONFIGURE_FILE(
					"tools/pkg-config/nervousvm-${nervousvm_module}.pc.in"
					"tools/pkg-config/nervousvm-${nervousvm_module}.pc"
					@ONLY)
				INSTALL(FILES "${CMAKE_CURRENT_BINARY_DIR}/tools/pkg-config/nervousvm-${nervousvm_module}.pc"
					DESTINATION "${CMAKE_INSTALL_PREFIX}/${PKGCONFIG_DIR}")
			endforeach()
		endif()
	endif()
endif()

# enable project folders
set_property(GLOBAL PROPERTY USE_FOLDERS ON)
set_property(GLOBAL PROPERTY PREDEFINED_TARGETS_FOLDER "CMake")

# add the subdirectories
add_subdirectory(src/NervousVM)
if(NERVOUSVM_BUILD_DOC)
	add_subdirectory(doc)
endif()

# setup the install rules
if(NOT NERVOUSVM_BUILD_FRAMEWORKS)
	install(DIRECTORY include
			DESTINATION .
			COMPONENT devel
			FILES_MATCHING PATTERN "*.hpp" PATTERN "*.inl")
else()
	# find only "root" headers
	file(GLOB NERVOUSVM_HEADERS RELATIVE ${PROJECT_SOURCE_DIR} "include/NervousVM/*")
	
	# we need a dummy file in order to compile the framework
    add_custom_command(OUTPUT ${CMAKE_CURRENT_BINARY_DIR}/dummy.cpp
                       COMMAND touch ${CMAKE_CURRENT_BINARY_DIR}/dummy.cpp)

    set(NERVOUSVM_SOURCES ${NERVOUSVM_HEADERS})
    list(APPEND NERVOUSVM_SOURCES ${CMAKE_CURRENT_BINARY_DIR}/dummy.cpp)
			
	# create NervousVM.framework
    add_library(NervousVM ${NERVOUSVM_SOURCES})
	
	# edit target properties
	set_target_properties(NervousVM PROPERTIES
						  BUILD_WITH_INSTALL_RPATH 1
						  INSTALL_NAME_DIR "@executable_path/../Frameworks")
	
	# install rule
	install(TARGETS NervousVM
			FRAMEWORK DESTINATION ${CMAKE_INSTALL_FRAMEWORK_PREFIX}
			COMPONENT devel)
endif()

# install 3rd-party libraries and tools
if(NERVOUSVM_OS_WINDOWS)

    # install future external libraries if necessary, here

elseif(NERVOUSVM_OS_MACOSX)

    # install future external libraries if necessary, here

    # install the Xcode templates if requested, those have to be prepared first, if it's wanted

elseif(NERVOUSVM_OS_IOS)

    # fix CMake install rules broken for iOS (see http://public.kitware.com/Bug/view.php?id=12506)
    if(NERVOUSVM_OS_IOS)
        install(DIRECTORY "${CMAKE_BINARY_DIR}/lib/\$ENV{CONFIGURATION}/" DESTINATION lib${LIB_SUFFIX})
    endif()

    # since the iOS libraries are built as static, we must install the NervousVM dependencies
    # too so that the end user can easily link them to its final application
    # install future external libraries if necessary, here

elseif(NERVOUSVM_OS_ANDROID)

    # install future external libraries if necessary, here

endif()

		
		