#ifndef WORDNODE_H
#define WORDNODE_H

#include <map>
#include <string>

class WordNode
{
public:
	WordNode();
	~WordNode();

	void addString(const std::wstring &s, const std::wstring *lemma);

private:
	typedef std::map<wchar_t, WordNode*> WordNodeMap;
	const wchar_t LemmaChar = u'!';
	
	WordNodeMap m_map;
};

#endif // WORDNODE_H
