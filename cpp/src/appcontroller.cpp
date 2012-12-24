#include <iostream>
#include "appcontroller.h"
#include "traindataparser.h"

AppController::AppController()
{
}

bool AppController::run()
{
	std::cout << "Reading file..." << std::endl;
	if (!readFile("data/train_data.txt"))
		return false;

	std::cout << "Parsing to WordNet..." << std::endl;
	TrainDataParser *parser = new TrainDataParser;
	// OPTIMIZE: grows in RAM up to 50 MB
	// OPTIMIZE: too long: use valgrind to determine bottleneck in parsing
	if (!parser->parse(m_fileData)) {
		m_errorString = parser->errorString();
		delete parser;
		return false;
	}
	m_wordNet = parser->wordNet();
	delete parser;

	std::cout << "Start doinf main job..." << std::endl;
	// TODO: do something with m_wordNet

	return true;
}

std::string AppController::errorString() const
{
	return m_errorString;
}

/* private */

bool AppController::readFile(const std::string &filePath)
{
	Utf8File *file = new Utf8File;
	
	if (!file->read(filePath)) {
		m_errorString = file->errorString();
		delete file;
		return false;
	}

	m_fileData = file->data();
	delete file;

	return true;
}
