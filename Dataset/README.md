AutomAdapt dataset
=======
The AutomAdapt dataset includes data from 4 full months of testing.

## File Naming Convention

In this project, we follow a specific naming convention for the files. The name format for the files is as follows:

- **FileType**\_**TrafficPatternID**\_**MonitoringCycleTime**\_**StartDate**\_**StopDate**.extension 

Where:
- `FileType`: Indicates the type or category of the file (e.g., Automaton, NetworkConfigurations = NC).
- `Traffic pattern ID`: Traffic pattern (TP) ID based on the TSN traffic classes defined in [[1]](#1) (see [traffic pattern table](https://github.com/FLSchempp/AutomAdapt/edit/main/Dataset/README.md#traffic-pattern-id---mapping-table)).
- `MonitoringCycleTime`: Period to calculate the average results of the observed KPIs (e.g., 10s, 60s).
- `StartDate`: Represents the date in YYYY-MM-DDTHH:MM:SSZ on which the execution started.
- `StopDate`: Represents the date in YYYY-MM-DDTHH:MM:SSZ on which the execution was completed.
- `extension`: Refers to the file extension indicating the file format (e.g., .csv).

For example, a file named `Automaton_10s_2023-03-01T00:00:00Z-2023-03-02T00:00:00Z.csv` indicates a learned automaton file with a monitoring cycle time of 10 seconds started on March 01, 2023 00:00:00h and completed on March 02, 2023 00:00:00h. Note that the "ConfigID" can be assigned to specific network parameters using the corresponding network configuration file. In this case, `NetworkConfigurations_10s_2023-03-01T00:00:00Z-2023-03-02T00:00:00Z.csv`

#### Learned automata
  - [March 2023](https://github.com/FLSchempp/AutomAdapt/tree/main/Dataset/March%202023)
  - [April 2023](https://github.com/FLSchempp/AutomAdapt/tree/main/Dataset/April%202023)
  - [May 2023](https://github.com/FLSchempp/AutomAdapt/tree/main/Dataset/May%202023)
  - [June 2023](https://github.com/FLSchempp/AutomAdapt/tree/main/Dataset/June%202023)

#### Network configurations
  - [March 2023](https://github.com/FLSchempp/AutomAdapt/tree/main/Dataset/March%202023)
  - [April 2023](https://github.com/FLSchempp/AutomAdapt/tree/main/Dataset/April%202023)
  - [May 2023](https://github.com/FLSchempp/AutomAdapt/tree/main/Dataset/May%202023)
  - [June 2023](https://github.com/FLSchempp/AutomAdapt/tree/main/Dataset/June%202023)

### Traffic pattern ID - mapping table

| Traffic pattern ID  | 1    | 2     | 3    | 4    | 5    | 6    | 7     | 8      | 9    | 10   | 11    | 12   | 13   | 14   | 15   |
| :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: | :---: |
| **Typical period**  | 2ms  | 10ms  | 10ms | 20ms | 20ms | 10ms | 2ms   | 100ms  | 1s   | 1s   | 200ms | 25ms | 20ms | 50ms | 250ms |
| **Data size (bytes)** | 100  | 500   | 1000  | 100  | 250  | 100  | 500   | 100    | 1000 | 500  | 100   | 50   | 50   | 200   | 1000   |


## References
<a id="1">[1]</a> 
IEC/IEEE 60802. TSN Profile for Industrial Automation, draft v1.4,
published June, 2022. [Online]. Available: http://www.ieee802.org/1/files/private/60802-drafts/d1/60802-d1-4.pdf.
