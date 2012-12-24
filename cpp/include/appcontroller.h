#ifndef APPCONTROLLER_H
#define APPCONTROLLER_H

#include <string>
#include <memory>
#include "utf8file.h"

class WordNet;

class AppController
{
public:
	AppController();

	bool run();
	std::string errorString() const;

private:
	bool readFile(const std::string &filePath);

	Utf8File::Data m_fileData;
	std::shared_ptr<WordNet> m_wordNet;
	mutable std::string m_errorString;
};

#endif // APPCONTROLLER_H
