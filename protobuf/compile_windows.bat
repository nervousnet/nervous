protoc --version
protoc -I=../protobuf/ --java_out=../router/router/src/ ../protobuf/sensorupload.proto
cmd