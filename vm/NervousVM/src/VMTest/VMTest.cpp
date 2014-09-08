//============================================================================
// Name        : VMTest.cpp
// Author      : 
// Version     :
// Copyright   : 
// Description :
//============================================================================

#ifdef TEST_

#include <iostream>
#include "../NervousVM/UUID/ooid/kashmir/uuid.h"
#include "../NervousVM/UUID/uuid.hpp"
using kashmir::uuid_t;
using nervousvm::generateUUID;
using namespace std;


int main() {
	cout << "NervousVM Testing Project" << endl;
	uuid_t uuid;
	generateUUID(uuid);
	cout << uuid << endl;
	return 0;
}

#endif
