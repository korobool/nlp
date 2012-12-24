#ifndef WORDNET_H
#define WORDNET_H

#include <vector>
#include <string>

class WordNode;

class WordNet
{
public:
	WordNet();
	~WordNet();

	void addString(const std::wstring &s, const std::wstring &lemma);

private:
	std::wstring *lemmaIndex(const std::wstring &lemma);

	std::vector<std::wstring*> m_lemmas;
	WordNode *m_root;
};

#endif // WORDNET_H