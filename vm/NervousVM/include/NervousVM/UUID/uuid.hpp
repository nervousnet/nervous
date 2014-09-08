/*
 * uuid.hpp
 *
 *  Created on: 02.09.2014
 *      Author: Fabian
 */

#ifndef UUID_HPP_
#define UUID_HPP_

#include "ooid/kashmir/uuid.h"

using kashmir::uuid_t;

namespace nervousvm
{
	void generateUUID(uuid_t uuid);
}


#endif /* UUID_HPP_ */
