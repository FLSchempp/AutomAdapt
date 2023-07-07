#!/bin/bash

# Credentials
ADMIN_CLI=./admin-cli.sh
USERNAME=username
PASSWORD=password
HOST='IP_address'
PORT='port'
FORMAT=human

# Auxiliary variables
currentdate=$(date +'%Y-%m-%d_%H:%M:%S')
name="micro-config"
extension=".xml"
OUTPUT_FILE="$name-$currentdate$extension"
DATA={\"requestId\":1,\"parameters\":{\"name\":\"generateScf\"}}

$ADMIN_CLI --bts-username=$USERNAME --bts-password=$PASSWORD --bts-host=$HOST --bts-port=$PORT --format=$FORMAT --output-file=$OUTPUT_FILE --data=$DATA

python3 xmltodb.py $OUTPUT_FILE $1
