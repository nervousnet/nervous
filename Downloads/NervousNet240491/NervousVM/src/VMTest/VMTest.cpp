//============================================================================
// Name        : VMTest.cpp
// Author      : 
// Version     :
// Copyright   : 
// Description :
//============================================================================

#ifdef TEST_

#include <iostream>
#include <boost/uuid/uuid.hpp>
#include <boost/uuid/uuid_io.hpp>
#include <NervousVM/UUID/uuid.hpp>
using namespace nervousvm;
using namespace std;

int main() {
	cout << "NervousVM Testing Project" << endl;
	for(int i = 0; i < 100; i++)
	{
		boost::uuids::uuid uuid = generateUUID();
		cout << uuid << endl;
	}
	return 0;
}

#endif
