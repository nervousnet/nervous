#!/bin/bash

pid=`ps aux | grep router.jar | awk '{print $2}'`
kill -9 $pid
