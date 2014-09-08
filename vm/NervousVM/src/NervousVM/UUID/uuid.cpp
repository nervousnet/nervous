/*
 * uuid.cpp
 *
 *  Created on: 02.09.2014
 *      Author: Fabian Tschopp
 */

#include "ooid/kashmir/uuid.h"
#include "ooid/kashmir/devrand.h"

using kashmir::uuid_t;
using kashmir::system::DevRand;


void generateUUID(uuid_t uuid) {

	DevRand devrandom;
	DevRand& in = devrandom;
	in >> uuid;
}
