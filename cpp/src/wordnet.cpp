#include "wordnet.h"
#include "wordnode.h"

WordNet::WordNet()
{
	m_root = new WordNode;
}

WordNet::~WordNet()
{
	delete m_root;
}

void WordNet::addString(const std::wstring &s, const std::wstring &lemma)
{
	m_root->addString(s, lemmaIndex(lemma));
}

/* private */

/*!
 *	Returns pointer to found or created lemma.
 */
std::wstring *WordNet::lemmaIndex(const std::wstring &lemma)
{
	// OPTIMIZE: O(n) now, can be improved to O(1) in future
	for (std::wstring *s : m_lemmas) {
		if (*s == lemma)
			return s;
	}

	std::wstring *newLemma = new std::wstring(lemma);
	m_lemmas.push_back(newLemma);
	return newLemma;
}
