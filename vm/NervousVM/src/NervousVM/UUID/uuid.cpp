/*
 * uuid.cpp
 *
 *  Created on: 02.09.2014
 *      Author: Fabian
 */

extern "C" {
#include <libuuid/uuid.h>
}

uuid_t generateUUID() {
	uuid_t uuid;
	uuid_generate_random(uuid);
	return uuid;
}
