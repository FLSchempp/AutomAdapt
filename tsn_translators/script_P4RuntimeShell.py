import p4runtime_sh.shell as sh


sh.setup(
    device_id=1,
    grpc_addr='localhost:9559',
    election_id=(0, 1), # (high, low)
    config=sh.FwdPipeConfig('/home/morse/P4/translator.proto.txt', '/home/morse/P4/translator.json')
)


te = sh.TableEntry('ingressImpl.mac_da')(action='ingressImpl.set_bd_dmac_intf')
te.match['stdmeta.ingress_port'] = ('1')
#te.match['hdr.ipv4.dstAddr'] =('10.11.32.128')
#te.match['hdr.ipv4.dstAddr'] =('10.45.0.11')
te.action['dmac'] = ('b8:27:eb:13:6f:48')
te.action['intf'] = ('2')
te.priority = 1
te.insert()

#te = sh.TableEntry('ingressImpl.mac_da')(action='ingressImpl.set_bd_dmac_intf')
#te.match['stdmeta.ingress_port'] = ('1')
#te.match['hdr.ipv4.dstAddr'] =('10.11.32.129')
#te.action['dmac'] = ('b8:27:eb:4b:76:bc')
#te.action['intf'] = ('2')
#te.priority = 1
#te.insert()

#te = sh.TableEntry('ingressImpl.mac_da')(action='ingressImpl.set_bd_dmac_intf')
#te.match['stdmeta.ingress_port'] = ('3')
#te.action['dmac'] = ('B0:0C:D1:9D:47:69')
#te.action['intf'] = ('2')
#te.insert()

# te = sh.TableEntry('ingressImpl.mac_da')(action='ingressImpl.set_bd_dmac_intf')
# te.match['stdmeta.ingress_port'] = ('2')
# te.action['dmac'] = ('8e:ed:a0:c0:f8:bb')
# te.action['intf'] = ('1')
# te.priority = 1
# te.insert()


te = sh.TableEntry('ingressImpl.dot1q')(action='ingressImpl.set_dot1q')
#te = sh.TableEntry('ingressImpl.dot1q')(action='ingressImpl.none')
te.match['stdmeta.ingress_port'] = ('1')
te.action['pri'] = ('7')
te.priority = 1
te.insert()

#te = sh.TableEntry('ingressImpl.dot1q')(action='ingressImpl.set_dot1q')
#te.match['stdmeta.ingress_port'] = ('3')
#te.action['pri'] = ('0')
#te.priority = 1
#te.insert()

#te = sh.TableEntry('ingressImpl.dot1q')(action='ingressImpl.rm_dot1q')
# te = sh.TableEntry('ingressImpl.dot1q')(action='ingressImpl.none')
# te.match['stdmeta.ingress_port'] = ('2')
# te.priority = 1
# te.insert()



sh.teardown()
