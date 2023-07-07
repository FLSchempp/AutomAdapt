Evaluation process
-----------
The evaluation process consists of two phases (for further information, please refer to 'AutomAdapt: Zero Touch Configurationof 5G QoS Flows extended forTime-Sensitive Networking')
1. **Pre-learning phase**: initial learning phase where the network configuration is randomly changed to produce an automaton for each traffic pattern. The following files are available:
   - `AutomatonIDX_NetworkConfigurations`: Network configurations applied in the automaton id X (where X indicates the automaton number).
   - `AutomatonIDX_NetworkStates`: Learned automaton
3. **Prediction and reconfiguration phase**: phase in which AutomAdapt is executed using the different automata learned during the initial learning phase. Therefore, predictions and reconfigurations will be performed using the information available in the network states of the corresponding automaton.

#### Learned automata

The generated automata can be dynamically visualized in the following links:
  - [Automaton ID 1](https://public.flourish.studio/visualisation/14284263/)
  - [Automaton ID 2](https://public.flourish.studio/visualisation/14284560/)
  - [Automaton ID 3](https://public.flourish.studio/visualisation/14284598/)
  - [Automaton ID 4](https://public.flourish.studio/visualisation/14284688/)

Each network state is composed of [Configuration ID, qtyID, Transitions, Elapsed time] (see [Network state composition](https://github.com/FLSchempp/AutomAdapt/edit/main/Evaluation/README.md#network-state-composition))

#### Network configurations
Each network configuration is composed of [dlMaxRetxThreshold, dlPollByte, dlPollPDU, dlTPollRetr, dlTProhib, dlTReassembly, ulMaxRetxThreshold, ulPollByte, ulPollPDU, ulTPollRetr, ulTProhib, ulTReassembly, id] (see [[1]](https://www.3gpp.org/ftp/Specs/archive/23_series/23.501/23501-gf0.zip) for further details)

[1] 3GPP. Radio Link Control (RLC) protocol specification, 3rd Generation Partnership Project (3GPP), Technical Specification (TS) 38.322, 03 2019, version 15.5.0.

Network state composition
-----------
  - `Configuration ID`: ID corresponding to the applied network configuration (see network configuration file for more information)
  - `qty ID`: denotes the valid quality function that relates network states and abstract network states
  - `Transitions`: transitions to other states. A transition is composed of [TransitionType_TargetState]. The types of transitions are:
    - `config`: state change due to a network configuration change.
    - `qty`: state change due to a change in the observed KPIs, which causes the qty function to obtain another value.
    - `config_qty`: state change due to both, network configuration and qty
  - `Elapsed time`: time elapsed in that state until it has transitioned to another state (indicated by the Transitions field).
