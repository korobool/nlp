#ifndef UTF8FILE_H
#define UTF8FILE_H

#include <fstream>
#include <string>
#include <vector>
#include <memory>

class Utf8File
{
public:
	typedef std::wstring Utf16Line;
	typedef std::vector<Utf16Line> Utf16LineList;
	typedef std::shared_ptr<Utf16LineList> Data; // shared ptr to vector of UTF-16 lines

public:
	Utf8File();

	bool read(const std::string &filePath);
	Data data();
	std::string errorString() const;

private:
	bool readData();
	
	std::ifstream m_file;
	mutable std::string m_errorString;
	Data m_data;
};

#endif // UTF8FILE_H
