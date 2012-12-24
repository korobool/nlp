#include <iostream> // ###
#include <algorithm>
#include "traindataparser.h"
#include "wordnet.h"
#include "utf8file.h"

TrainDataParser::TrainDataParser()
	: m_wordNet(std::make_shared<WordNet>())
{
}

bool TrainDataParser::parse(const Utf8File::Data &fileData)
{
	const wchar_t Delimeter = u',';

	int lineNo = 0;
	for (auto line : *fileData) {
		++lineNo;
		if (lineNo % 10000 == 0)
			std::cout << "### Line no: " << lineNo << std::endl; // ###

		if (line.empty())
			continue;

		std::wstring::iterator delimIt = std::find(line.begin(), line.end(), Delimeter);
		if (delimIt == line.end()) {
			std::cerr << "Delimeter not found: line #" << lineNo << std::endl;
			continue;
		}

		std::wstring word(line.begin(), delimIt);
		std::wstring lemma(delimIt + 1, line.end());

		bool badLine = std::find(lemma.begin(), lemma.end(), Delimeter) != lemma.end();
		if (word.empty() || lemma.empty() || badLine) {
			std::cerr << "Too many delimeters: line #" << lineNo << std::endl;
			continue;
		}

		m_wordNet->addString(word, lemma);
	}

	return true;
}

std::shared_ptr<WordNet> TrainDataParser::wordNet()
{
	return m_wordNet;
}

std::string TrainDataParser::errorString() const
{
	return m_errorString;
}
