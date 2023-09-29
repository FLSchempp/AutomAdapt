#!/bin/bash

# AutomAdapt
# Configuration implementation
# author: Francisco Luque Schempp
# version: 1.0

# Credentials and connection parameters to the BTS
ADMIN_CLI=./admin-cli.sh
USERNAME=username
PASSWORD=password
HOST='IP_address'
PORT='port'
FORMAT=human

# RLC AM profile
PROFILE=3

# Distinguished name - pico cell
#DISTNAME=MRBTS-4/NRBTS-4/NRDRB_RLC_AM-%PROFILE%
# Distinguished name - micro cell
DISTNAME=MRBTS-2/NRBTS-1/NRDRB_RLC_AM-$PROFILE

# RLC AM parameters (DL and UL)
dlMaxRetxThreshold=$1
ulMaxRetxThreshold=$2
dlPollByte=$3
ulPollByte=$4
dlPollPDU=$5
ulPollPDU=$6
dlTPollRetr=$7
ulTPollRetr=$8
dlTProhib=$9
ulTProhib=$10
dlTReassembly=$11
ulTReassembly=$12

# RLC AM parameters (DL and UL) - BTS objects
dlTPollRetr_Param={\"parameterName\":\"dlTPollRetr\"\,\"operation\":\"update\"\,\"value\":\"$dlTPollRetr\"}
ulTPollRetr_Param={\"parameterName\":\"ulTPollRetr\"\,\"operation\":\"update\"\,\"value\":\"$ulTPollRetr\"}
dlTReassembly_Param={\"parameterName\":\"dlTReassembly\"\,\"operation\":\"update\"\,\"value\":\"$dlTReassembly\"}
ulTReassembly_Param={\"parameterName\":\"ulTReassembly\"\,\"operation\":\"update\"\,\"value\":\"$ulTReassembly\"}
dlTProhib_Param={\"parameterName\":\"dlTProhib\"\,\"operation\":\"update\"\,\"value\":\"$dlTProhib\"}
ulTProhib_Param={\"parameterName\":\"ulTProhib\"\,\"operation\":\"update\"\,\"value\":\"$ulTProhib\"}
dlMaxRetxThreshold_Param={\"parameterName\":\"dlMaxRetxThreshold\"\,\"operation\":\"update\"\,\"value\":\"$dlMaxRetxThreshold\"}
ulMaxRetxThreshold_Param={\"parameterName\":\"ulMaxRetxThreshold\"\,\"operation\":\"update\"\,\"value\":\"$ulMaxRetxThreshold\"}
dlPollPDU_Param={\"parameterName\":\"dlPollPDU\"\,\"operation\":\"update\"\,\"value\":\"$dlPollPDU\"}
ulPollPDU_Param={\"parameterName\":\"ulPollPDU\"\,\"operation\":\"update\"\,\"value\":\"$ulPollPDU\"}
dlPollByte_Param={\"parameterName\":\"dlPollByte\"\,\"operation\":\"update\"\,\"value\":\"$dlPollByte\"}
ulPollByte_Param={\"parameterName\":\"ulPollByte\"\,\"operation\":\"update\"\,\"value\":\"$ulPollByte\"}
dlTReassembly_Param={\"parameterName\":\"dlTReassembly\"\,\"operation\":\"update\"\,\"value\":\"$dlTReassembly\"}
ulTReassembly_Param={\"parameterName\":\"ulTReassembly\"\,\"operation\":\"update\"\,\"value\":\"$ulTReassembly\"}
# RLC AM parameters (DL and UL) - DRB objects
DRB_AM={\"distName\":\"$DISTNAME\"\,\"operation\":\"update\"\,\"parameters\":[$dlTPollRetr_Param\,$ulTPollRetr_Param\,$dlTReassembly_Param\,$ulTReassembly_Param\,$dlTProhib_Param\,$ulTProhib_Param\,$dlMaxRetxThreshold_Param\,$ulMaxRetxThreshold_Param\,$dlPollPDU_Param\,$ulPollPDU_Param\,$dlPollByte_Param\,$ulPollByte_Param]}
# RLC AM parameters (DL and UL) - Data DRB objects
DATA_DRB="{\"requestId\":1,\"parameters\":{\"name\":\"recommission\",\"parameters\":{\"planObjects\":[$DRB_AM],\"shouldBeActivated\":true}}}"

#Update the RLC AM values (profile 3) for DRB1
"$ADMIN_CLI" --bts-username="$USERNAME" --bts-password="$PASSWORD" --bts-host="$HOST" --bts-port="$PORT" --format="$FORMAT" --data="$DATA_DRB"

# Get the configuration from the BTS
./getConfigMicro.sh "$PROFILE"
