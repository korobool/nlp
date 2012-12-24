#include <sstream>
#include "utf8file.h"
#include "utf8.h"

/* class Utf8File
 *
 * Read UTF-8 file and provides it data as UTF-16 chars (because of .
 */

Utf8File::Utf8File()
	: m_data(std::make_shared<Utf16LineList>())
{
}

bool Utf8File::read(const std::string &filePath)
{
	m_file.open(filePath);

	if (!m_file.is_open()) {
		m_errorString = "Open file error : probably file doesn't exist";
		return false;
	}

	if (!m_file.good()) {
		m_errorString = "I/O stream error: check if file exists";
		m_file.close();
		return false;
	}

	if (!readData()) {
		m_file.close();
		return false;
	}
	

	m_file.close();
	return true;
}

Utf8File::Data Utf8File::data()
{
	return m_data;
}

std::string Utf8File::errorString() const
{
	return m_errorString;
}

/* private */

/*!
 *	See example at http://utfcpp.sourceforge.net/
 */
bool Utf8File::readData()
{
	std::string line;
	unsigned lineIndex = 1;

	m_data->clear();

	// Read file content
	while (std::getline(m_file, line)) {
		// Check for invalid UTF-8
		std::string::iterator end_it = utf8::find_invalid(line.begin(), line.end());
		if (end_it != line.end()) {
			std::stringstream ss;
			ss << "Invalid UTF-8 encoding detected at line " << lineIndex << std::endl;
			ss << "This part is fine: " << std::string(line.begin(), end_it);
			m_errorString = ss.str();
			return false;
		}

		// Convert it to UTF-16
		Utf16Line utf16line;
		utf8::utf8to16(line.begin(), end_it, std::back_inserter(utf16line));

		// Skip BOM (Byte Order Mark) bytes
		if (lineIndex == 1) {
			bool hasBom = utf8::starts_with_bom(line.begin(), line.end());
			if (hasBom)
				utf16line.erase(0, 1);
		}

		// Append to data
		m_data->push_back(utf16line);

		++lineIndex;
	}

	return true;
}
