import p4runtime_sh.shell as sh


sh.setup(
    device_id=1,
    grpc_addr='localhost:9559',
    election_id=(0, 1), # (high, low)
    config=sh.FwdPipeConfig('/AutomAdapt/tsn_translators/translator.proto.txt', '/AutomAdapt/tsn_translators/translator.json')
)


te = sh.TableEntry('ingressImpl.mac_da')(action='ingressImpl.set_bd_dmac_intf')
te.match['stdmeta.ingress_port'] = ('1')
te.match['hdr.ipv4.dstAddr'] =('10.45.0.50')
te.action['dmac'] = ('b8:27:eb:13:6f:48')
te.action['intf'] = ('2')
te.priority = 1
te.insert()


te = sh.TableEntry('ingressImpl.dot1q')(action='ingressImpl.set_dot1q')
#te = sh.TableEntry('ingressImpl.dot1q')(action='ingressImpl.none')
te.match['stdmeta.ingress_port'] = ('1')
te.action['pri'] = ('7')
te.priority = 1
te.insert()

sh.teardown()
