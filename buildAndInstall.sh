#!/bin/bash

#
# Builds msvc-easy-ses
#
# Author : Akash Chatterjee
#
# Usage  : sudo ./buildAndInstall.sh <env>
#

export GRADLE_OPTS=${GRADLE_OPTS}" -Dorg.gradle.daemon=false"
./gradlew clean bootJar

USR_GROUP=easy-ses
USR=ubuntu
WORKING_DIR=/opt/easy-ses
ENV=$1

echo "Details of user ${USR}"
id $USR

cd build/libs/ || exit
pattern="email-*.jar"
files=($pattern)
jar_file=${files[0]}
echo "Jar File : ${jar_file}"
cd ../..

echo "Creating service file"
echo "[Unit]
Description=Easy SES Microservice

[Service]
WorkingDirectory=/opt/easy-ses
ExecStart=/usr/bin/java -Dserver.port=8080 -Duser.timezone=UTC -Dspring.profiles.active=${ENV} -Xms500m -Xmx500m -jar ${jar_file}
User=ubuntu
Type=simple
Restart=always
RestartSec=10

[Install]
WantedBy=multi-user.target
" | sudo tee /etc/systemd/system/msvc-easy-ses.service

echo "Contents of service file | START"
cat /etc/systemd/system/msvc-easy-ses.service
echo "Contents of service file | END"

echo "Creating /opt"
sudo mkdir -p /opt

echo "Creating ${WORKING_DIR}"
sudo mkdir -p $WORKING_DIR

echo "Removing old jars"
sudo rm ${WORKING_DIR}/email-*.jar

echo "Copying jar ${jar_file} to ${WORKING_DIR}"
sudo cp "build/libs/${jar_file}" ${WORKING_DIR}/

echo "Changing owner to ${USR} for /opt"
sudo chown -R $USR /opt

sudo systemctl daemon-reload