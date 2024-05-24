![](https://github.com/FLSchempp/AutomAdapt/blob/main/AutomAdapt.gif)

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/2f4106b7e3584bf9bb49a03e7682995d)](https://app.codacy.com/gh/FLSchempp/AutomAdapt/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade)

---------------

AutomAdapt is a tool to implement **Zero Touch Configuration of 5G QoS Flows extended for Time-Sensitive Networking**

https://github.com/FLSchempp/AutomAdapt/assets/79450487/32b7a558-dc8d-47c9-ad0b-8eb0aee1589d

Refer to the research article [AutomAdapt: Zero Touch Configuration of 5G QoS Flows Extended for Time-Sensitive Networking](https://ieeexplore.ieee.org/document/10209171) for further information.

AutomAdapt features
---------------
- [Monitoring](https://github.com/FLSchempp/AutomAdapt/tree/main/Monitoring)
- [Learning](https://github.com/FLSchempp/AutomAdapt/tree/main/Learning)
- [Prediction](https://github.com/FLSchempp/AutomAdapt/tree/main/Prediction)
- [Reconfiguration](https://github.com/FLSchempp/AutomAdapt/tree/main/Reconfiguration)

AutomAdapt dataset
---------------
- [Data set](https://github.com/FLSchempp/AutomAdapt/tree/main/Dataset)
  - Learned automata
  - Network configurations
  - Raw data examples
- [Evaluation](https://github.com/FLSchempp/AutomAdapt/tree/main/Evaluation)
  - Pre-learning phase
  - Prediction and Reconfiguration phase

Prerequisites
-----------
#### [PTP synchronization](https://github.com/FLSchempp/AutomAdapt/edit/main/README.md#ptp-synchronization-1)
  - PHC synchronization: [ptp4l](https://manpages.ubuntu.com/manpages/focal/man8/ptp4l.8.html)
  - System clock synchronization: [phc2sys](https://manpages.ubuntu.com/manpages/focal/en/man8/phc2sys.8.html)
#### [P4 TSN Translators](https://github.com/FLSchempp/AutomAdapt/edit/main/README.md#p4-tsn-translators-1)
  - [Stratum](https://github.com/stratum/stratum)
  - [Python3](https://www.python.org/downloads/)
  - Files
    - [Chassis configuration](tsn_translators/chassis_config.pb.txt)
    - [P4Runtime script](tsn_translators/P4Runtime_DS-TT.py)

PTP synchronization
-----------
### Synchronizing the PHC

The PHC synchronization step is mandatory for all TSN systems. It guarantees the PHC from the NIC is in sync with the GM clock from the gPTP domain. This is achieved by the ptp4l daemon.

To synchronize PHC with the GM clock, run the command below. Make sure to replace Interface_name by the interface name corresponding to the TSN-capable NIC in the system:

`$ sudo ptp4l -2 -i interface_name --domainNumber 24 -s -m`

### System clock synchronization
Synchronize the system clock to a PTP hardware clock (PHC), which itself is synchronized by the ptp4l program:

`$ sudo phc2sys -s interface_name -w -n 24 -m`

For further information, please refer to [TSN Read the Docs](https://tsn.readthedocs.io/timesync.html).

P4 TSN Translators
-----------

`$ sudo stratum_bmv2 -chassis_config_file=~/AutomAdapt/tsn_translators/chassis_config.pb.txt -bmv2_log_level=off`

P4Runtime
-----------
`$ sudo python3 ~/AutomAdapt/tsn_translators/script_P4RuntimeShell.py`
