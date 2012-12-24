#ifndef TRAINDATAPARSER_H
#define TRAINDATAPARSER_H

#include <string>
#include <memory>
#include "utf8file.h"

class WordNet;

class TrainDataParser
{
public:
	TrainDataParser();

	bool parse(const Utf8File::Data &fileData);
	std::string errorString() const;
	std::shared_ptr<WordNet> wordNet();

private:
	std::shared_ptr<WordNet> m_wordNet;
	mutable std::string m_errorString;
};

#endif // TRAINDATAPARSER_H
