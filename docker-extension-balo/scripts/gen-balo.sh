#
# Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
#
# WSO2 Inc. licenses this file to you under the Apache License,
# Version 2.0 (the "License"); you may not use this file except
# in compliance with the License.
# You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
#
#!/usr/bin/env bash

DISTRIBUTION_PATH=${1}
DOCKER_BALO_MAVEN_PROJECT_ROOT=${2}

# TEMP
#rm -rf ${DISTRIBUTION_PATH}/*
#cp -r /Users/hemikak/ballerina/dev/ballerina/distribution/zip/jballerina-tools/build/distributions/jballerina-tools-0.992.0-m2-SNAPSHOT/* ${DISTRIBUTION_PATH}

EXECUTABLE="${DISTRIBUTION_PATH}/bin/ballerina"
DOCKER_BALLERINA_PROJECT="${DOCKER_BALO_MAVEN_PROJECT_ROOT}/src/main/ballerina/ballerinax"
BALLERINAX_BIR_CACHE="${DISTRIBUTION_PATH}/bir-cache/ballerinax/"
BALLERINAX_SYSTEM_LIB="${DISTRIBUTION_PATH}/bre/lib/"

mkdir -p ${BALLERINAX_BIR_CACHE}
mkdir -p ${BALLERINAX_SYSTEM_LIB}

rm -rf ${DOCKER_BALLERINA_PROJECT}/target

pushd ${DOCKER_BALLERINA_PROJECT} /dev/null 2>&1
    ${EXECUTABLE} compile --jvmTarget
popd > /dev/null 2>&1

cp -r ${DOCKER_BALLERINA_PROJECT}/target/* ${DOCKER_BALO_MAVEN_PROJECT_ROOT}/target

cp -r ${DOCKER_BALO_MAVEN_PROJECT_ROOT}/target/caches/bir_cache/ballerinax/docker ${BALLERINAX_BIR_CACHE}
cp ${DOCKER_BALO_MAVEN_PROJECT_ROOT}/target/caches/jar_cache/ballerinax/docker/0.0.0/docker.jar ${BALLERINAX_SYSTEM_LIB}

