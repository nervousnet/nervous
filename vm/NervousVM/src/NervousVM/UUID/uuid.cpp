#include <boost/uuid/uuid.hpp>
#include <boost/uuid/uuid_generators.hpp>
#include <boost/uuid/uuid_io.hpp>

namespace nervousvm {
boost::uuids::uuid generateUUID() {
	boost::uuids::uuid uuid = boost::uuids::random_generator()();
	return uuid;
}
}
