AutomAdapt
=======
AutomAdapt is a tool to implement **Zero Touch Configuration of 5G QoS Flows extended for Time-Sensitive Networking**

AutomAdapt Features
---------------

Prerequisites
-----------
PTP synchronization
  - **ptp4l**: PHC synchronization
  - **phc2sys**: System clock synchronization
P4 TSN Translators
  - Chassis configuration
  - P4Runtime script


PTP synchronization
-----------
###### Synchronizing the PHC (https://tsn.readthedocs.io/timesync.html)

The PHC synchronization step is mandatory for all TSN systems. It guarantees the PHC from the NIC is in sync with the GM clock from the gPTP domain. This is achieved by the ptp4l daemon.

To synchronize PHC with the GM clock, run the command below. Make sure to replace eth0 by the interface name corresponding to the TSN-capable NIC in the system.
`sudo ptp4l -2 -i enp11s0 --domainNumber 24 -s -m`

###### System clock synchronization
Synchronize the system clock to a PTP hardware clock (PHC), which itself is synchronized by the ptp4l program
`sudo phc2sys -s enp11s0 -w -n 24 -m`

P4 TSN Translators
-----------

sudo stratum_bmv2 -chassis_config_file=/etc/stratum/chassis_config.pb.txt -bmv2_log_level=off

P4Runtime
-----------
sudo python3.8 ~/P4/Script_P4RuntimeShell/script_P4RuntimeShell.py
