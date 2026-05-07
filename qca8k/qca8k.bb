DESCRIPTION = "Install qca_dsa script"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

SRC_URI = "file://qca_dsa"

PACKAGES =+ "qce2k"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/qca_dsa ${D}${bindir}

    # qce2k: install modprobe softdep and modules-load autoload config
    install -d ${D}${sysconfdir}/modprobe.d
    cat <<EOF > ${D}${sysconfdir}/modprobe.d/qce2k.conf
# pcs-qce2204 and qce2204_dsa both depend on qca-nss-dp
# qce2204_dsa additionally depends on pcs-qce2204
softdep pcs-qce2204 pre: qca-nss-dp
softdep qce2204_dsa pre: qca-nss-dp pcs-qce2204
EOF

    install -d ${D}${sysconfdir}/modules-load.d
    cat <<EOF > ${D}${sysconfdir}/modules-load.d/qce2k.conf
pcs-qce2204
qce2204_dsa
EOF
}

FILES:qce2k = "${sysconfdir}/modprobe.d/qce2k.conf \
               ${sysconfdir}/modules-load.d/qce2k.conf"

RDEPENDS:qce2k = "qca-nss-dp"
