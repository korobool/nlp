#include <cstdlib>
#include <iostream>
#include "appcontroller.h"

int main(int argc, char *argv[])
{
	AppController ac;
	if (!ac.run()) {
		std::cerr << ac.errorString();
		return EXIT_FAILURE;
	}
	return EXIT_SUCCESS;
}
