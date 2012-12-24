cmake_minimum_required(VERSION 2.8)
set(PROJECT lemmatizer)
project(${PROJECT})

include_directories(include)
set(UTF8_LIB_HEADERS
	include/utf8/checked.h
	include/utf8/unchecked.h
	include/utf8/core.h
	include/utf8.h
)
set(HEADERS
	${UTF8_LIB_HEADERS}
	include/appcontroller.h
	include/utf8file.h
	include/wordnet.h
	include/wordnode.h
	include/traindataparser.h)
set(SOURCES
	src/appcontroller.cpp
	src/utf8file.cpp
	src/wordnode.cpp
	src/wordnet.cpp
	src/traindataparser.cpp
	src/main.cpp)

add_definitions(--std=c++0x -Wall)
add_executable(lemmatizer ${SOURCES} ${HEADERS})
install(TARGETS ${PROJECT} DESTINATION bin)
