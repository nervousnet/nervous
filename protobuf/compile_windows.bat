protoc --version
protoc -I=../protobuf/ --java_out=../router/router/src/ ../protobuf/sensorupload.proto
protoc -I=../protobuf/ --java_out=../javanervousvm/src/ ../protobuf/sensorupload.proto
protoc -I=../protobuf/ --cpp_out=../vm/NervousVM/src/NervousVM/Protobuf/ ../protobuf/sensorupload.proto
cmd