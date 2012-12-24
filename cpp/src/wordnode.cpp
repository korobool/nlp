#include <iostream>
#include "wordnode.h"

WordNode::WordNode()
{
}

WordNode::~WordNode()
{
	// TODO: remove the whole underlaying structure
}

void WordNode::addString(const std::wstring &s, const std::wstring *lemma)
{
	WordNodeMap::iterator tmpIt;
	WordNode *curNode = this;

	if (!s.length())
		return;

	for (auto c : s) {
		tmpIt = curNode->m_map.find(c);
		if (tmpIt == curNode->m_map.end()) { // node doesn't exist yet
			WordNode *tmpNode = new WordNode;
			curNode->m_map[c] = tmpNode;
			curNode = tmpNode;
		} else { // node already exists
			curNode = tmpIt->second;
		}
	}

	// Since we know that this is lemma (LemmaChar) -- cast it to string
	curNode->m_map[LemmaChar] =
			reinterpret_cast<WordNode*>(const_cast<std::wstring*>(lemma));
}
