DESCRIPTION = "Install qca_dsa script"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

SRC_URI = "file://qca_dsa"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${WORKDIR}/qca_dsa ${D}${bindir}
}