#include <core.p4>
#include <v1model.p4>

header ethernet_t {
    bit<48> dstAddr;
    bit<48> srcAddr;
    bit<16> etherType;
}

header dot1q_t {
    bit<3>  pri;
    bit<1>  cfi;
    bit<12> vid;
    bit<16> tpid;
}

header ipv4_t {
    bit<4>  version;
    bit<4>  ihl;
    bit<8>  diffserv;
    bit<16> totalLen;
    bit<16> identification;
    bit<3>  flags;
    bit<13> fragOffset;
    bit<8>  ttl;
    bit<8>  protocol;
    bit<16> hdrChecksum;
    bit<32> srcAddr;
    bit<32> dstAddr;
}

struct fwd_metadata_t {
    bit<32> l2ptr;
    bit<24> out_bd;
}

struct metadata_t {
    fwd_metadata_t  fwd_metadata;
    bit<3>          pri;
    bit<2>          emit_dot1q;             // 0 --> Emit, 1 --> Not Emit, 2 --> None (Default)
}

struct headers_t {
    ethernet_t ethernet;
    dot1q_t    dot1q;
    ipv4_t     ipv4;
}

parser parserImpl(packet_in packet,
                  out headers_t hdr,
                  inout metadata_t meta,
                  inout standard_metadata_t stdmeta)
{
    const bit<16> ETHERTYPE_dot1q = 16w0x8100;

    state start {
        transition parse_ethernet;
    }
    state parse_ethernet {
        packet.extract(hdr.ethernet);
        transition select(hdr.ethernet.etherType){
            ETHERTYPE_dot1q:    parse_dot1q;
            default:            parse_ipv4;
        }
    }

    state parse_dot1q {
        packet.extract(hdr.dot1q);
        transition parse_ipv4;
    }

    state parse_ipv4 {
        packet.extract(hdr.ipv4);
        transition accept;
    }
}

control ingressImpl(inout headers_t hdr,
                    inout metadata_t meta,
                    inout standard_metadata_t stdmeta)
{
    action none() {
       // None 
    }

    action set_bd_dmac_intf(bit<48> dmac, bit<9> intf) {
        hdr.ethernet.srcAddr = hdr.ethernet.dstAddr;
        hdr.ethernet.dstAddr = dmac;

        stdmeta.egress_spec = intf;
    }
    table mac_da {
        key = {
            stdmeta.ingress_port:   exact;
            hdr.ipv4.dstAddr:       ternary;
        }
        actions = {
            set_bd_dmac_intf;
            none;
        }
    }

    action set_dot1q(bit<3> pri) {
        if (!hdr.dot1q.isValid()){
            hdr.ethernet.etherType = 0x8100;
            meta.emit_dot1q = 0;
            meta.pri = pri;
        }
    }

    action rm_dot1q() {
        if (hdr.dot1q.isValid()){
            hdr.ethernet.etherType = 0x800;
            //hdr.dot1q.setInvalid();
            meta.emit_dot1q = 1;
        }
    }
    
    table dot1q {
    	key = {
    	    stdmeta.ingress_port:   exact;
            hdr.ipv4.dstAddr:       ternary;
    	}
    	actions = {
    	    set_dot1q;
            rm_dot1q;
            none;
    	}
    }

    apply {
        meta.emit_dot1q = 2;
        // Routing L2
        mac_da.apply();

        // Put dot1q header
        dot1q.apply();

        if (meta.emit_dot1q == 0){
            hdr.dot1q.setValid();
            hdr.dot1q.pri = meta.pri;
            hdr.dot1q.cfi = 1;
            hdr.dot1q.vid = 0x113;
            hdr.dot1q.tpid = 0x800;
        }else if (meta.emit_dot1q == 1){
            hdr.dot1q.setInvalid();
        }
    }
}

control egressImpl(inout headers_t hdr,
                   inout metadata_t meta,
                   inout standard_metadata_t stdmeta)
{
    apply {

    }
}

control deparserImpl(packet_out packet,
                     in headers_t hdr)
{
    apply {
        packet.emit(hdr.ethernet);
        packet.emit(hdr.dot1q);
        packet.emit(hdr.ipv4);
    }
}

control verifyChecksum(inout headers_t hdr, inout metadata_t meta) {
    apply {
        verify_checksum(hdr.ipv4.isValid() && hdr.ipv4.ihl == 5,
            { hdr.ipv4.version,
                hdr.ipv4.ihl,
                hdr.ipv4.diffserv,
                hdr.ipv4.totalLen,
                hdr.ipv4.identification,
                hdr.ipv4.flags,
                hdr.ipv4.fragOffset,
                hdr.ipv4.ttl,
                hdr.ipv4.protocol,
                hdr.ipv4.srcAddr,
                hdr.ipv4.dstAddr },
            hdr.ipv4.hdrChecksum, HashAlgorithm.csum16);
    }
}

control updateChecksum(inout headers_t hdr, inout metadata_t meta) {
    apply {
        update_checksum(hdr.ipv4.isValid() && hdr.ipv4.ihl == 5,
            { hdr.ipv4.version,
                hdr.ipv4.ihl,
                hdr.ipv4.diffserv,
                hdr.ipv4.totalLen,
                hdr.ipv4.identification,
                hdr.ipv4.flags,
                hdr.ipv4.fragOffset,
                hdr.ipv4.ttl,
                hdr.ipv4.protocol,
                hdr.ipv4.srcAddr,
                hdr.ipv4.dstAddr },
            hdr.ipv4.hdrChecksum, HashAlgorithm.csum16);
    }
}

V1Switch(parserImpl(),
         verifyChecksum(),
         ingressImpl(),
         egressImpl(),
         updateChecksum(),
         deparserImpl()) main;
