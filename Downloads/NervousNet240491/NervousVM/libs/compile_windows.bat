protoc --version
protoc -I=google/protobuf/ --cpp_out=google/protobuf/ google/protobuf/*.proto
cmd