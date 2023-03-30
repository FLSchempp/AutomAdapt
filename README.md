![](https://github.com/FLSchempp/AutomAdapt/blob/main/A%20(1920%20%C3%97%20300%20px).gif)
=======
AutomAdapt is a tool to implement **Zero Touch Configuration of 5G QoS Flows extended for Time-Sensitive Networking**

AutomAdapt Features
---------------

Prerequisites
-----------
#### [PTP synchronization](https://github.com/FLSchempp/AutomAdapt/edit/main/README.md#ptp-synchronization-1)
  - PHC synchronization: [ptp4l](https://manpages.ubuntu.com/manpages/focal/man8/ptp4l.8.html)
  - System clock synchronization: [phc2sys](https://manpages.ubuntu.com/manpages/focal/en/man8/phc2sys.8.html)
#### [P4 TSN Translators](https://github.com/FLSchempp/AutomAdapt/edit/main/README.md#p4-tsn-translators-1)
  - [Stratum](https://github.com/stratum/stratum)
  - [Python3](https://www.python.org/downloads/)
  - [Chassis configuration](tsn_translators/chassis_config.pb.txt)
  - [P4Runtime script](tsn_translators/script_P4RuntimeShell.py)


PTP synchronization
-----------
### Synchronizing the PHC

The PHC synchronization step is mandatory for all TSN systems. It guarantees the PHC from the NIC is in sync with the GM clock from the gPTP domain. This is achieved by the ptp4l daemon.

To synchronize PHC with the GM clock, run the command below. Make sure to replace Interface_name by the interface name corresponding to the TSN-capable NIC in the system:

`$sudo ptp4l -2 -i Interface_name --domainNumber 24 -s -m`

### System clock synchronization
Synchronize the system clock to a PTP hardware clock (PHC), which itself is synchronized by the ptp4l program:

`$sudo phc2sys -s enp11s0 -w -n 24 -m`

For further information, please refer to [TSN Read the Docs](https://tsn.readthedocs.io/timesync.html).

P4 TSN Translators
-----------

`$sudo stratum_bmv2 -chassis_config_file=/etc/stratum/chassis_config.pb.txt -bmv2_log_level=off`

P4Runtime
-----------
`$sudo python3.8 ~/P4/Script_P4RuntimeShell/script_P4RuntimeShell.py`
