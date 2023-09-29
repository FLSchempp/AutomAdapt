# AutomAdapt
# Configuration implementation
# author: Francisco Luque Schempp
# version: 1.0

import sys
from bs4 import BeautifulSoup
import influxdb_client
from influxdb_client.client.write_api import SYNCHRONOUS

# Auxiliary variables
xmlfilename=''
profile_id=0

# Check if the script is called with the correct parameters
if len(sys.argv) == 3:
    xmlfilename = sys.argv[1]
    profile_id = sys.argv[2]
else:
    print("Error - XML file and profile ID incorrect")

# InfluxDB client configuration
bucket = "schempp"
org = "UMA"
token = "private_token_id"
# Store the URL of your InfluxDB instance
url="http://IP_Address:Port"

# Create a new InfluxDB client instance
client = influxdb_client.InfluxDBClient(
    url=url,
    token=token,
    org=org
)

# Write script
write_api = client.write_api(write_options=SYNCHRONOUS)

# Reading the data inside the xml
# file to a variable under the name
# data
with open(xmlfilename, 'r') as f:
    data = f.read()

# Passing the stored data inside
# the beautifulsoup parser, storing
# the returned object
Bs_data = BeautifulSoup(data, "xml")

# Finding all instances of tag
# `unique`
mon = Bs_data.find_all('managedObject')

# Getting the required RLC AM object
for nokia_object in mon:
    if (nokia_object.attrs['distName'] == 'MRBTS-2/NRBTS-1/NRDRB_RLC_AM-'+profile_id):
        rlc_o = nokia_object

# Looping through the RLC AM object and writing the data to the database
for iterator in rlc_o:
    if (iterator != '\n'):
        childname = iterator.attrs['name']
        childvalue = iterator.contents[0]
        p = influxdb_client.Point("configuration").tag("parameter", childname).field("value", childvalue)
        write_api.write(bucket=bucket, org=org, record=p)

