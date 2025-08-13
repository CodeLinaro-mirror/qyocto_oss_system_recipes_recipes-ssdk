DESCRIPTION = "NSS MACsec Driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

CLEANBROKEN = "1"

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS:prepend := "${THISDIR}/:"

SRC_URI = "file://qca-nss-macsec \
	   file://files/hostapd-macsec.sh \
	   file://files/hostapd-macsec.conf \
	   "

DEPENDS = "virtual/kernel"

S = "${WORKDIR}/qca-nss-macsec"

PR = "r0"

PACKAGES += "kernel-module-qca-nss-macsec"

CFLAGS +="-I${STAGING_INCDIR} -Wno-error=array-bounds"
CFLAGS +="-mfpu=neon -mfloat-abi=hard"
CFLAGS:remove:aarch64 ="-mfpu=neon -mfloat-abi=hard"
CFLAGS += "${@bb.utils.contains('DISTRO_FEATURES','ofw_qca',' -Wno-unused-result ','',d)}"
LDFLAGS +="--sysroot=${STAGING_DIR_TARGET}"

QCA_MACSEC_CONFIG_OPTS+= "TOOL_PATH=${STAGING_BINDIR_TOOLCHAIN} \
                         SYS_PATH=${STAGING_KERNEL_BUILDDIR} \
                         TOOLPREFIX=${TARGET_PREFIX} \
                         KVER=${KERNEL_VERSION} \
                         ARCH=${KARCH} \
                         OS='linux' "

do_compile() {
	${MAKE} -C ${STAGING_KERNEL_BUILDDIR} ${QCA_MACSEC_CONFIG_OPTS} M=${S} modules
	${MAKE} -C ${S} ${QCA_MACSEC_CONFIG_OPTS} -f Makefile.shell
}

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0755 qca-nss-macsec${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${libdir}
	install -m 0755 libfal.so ${D}${libdir}
	install -d ${D}${sbindir}
	install -m 0755 macsec_shell ${D}${sbindir}
	install -d ${D}${includedir}/qca-nss-macsec
	install -m 0644 include/nss_macsec_types.h ${D}${includedir}/qca-nss-macsec
	install -m 0644 include/nss_macsec_secy.h ${D}${includedir}/qca-nss-macsec
	install -m 0644 include/nss_macsec_secy_rx.h ${D}${includedir}/qca-nss-macsec
	install -m 0644 include/nss_macsec_secy_tx.h ${D}${includedir}/qca-nss-macsec
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/files/hostapd-macsec.sh ${D}${bindir}/hostapd-macsec.sh
	install -d ${D}${sysconfdir}
	install -m 0644 ${WORKDIR}/files/hostapd-macsec.conf  -D ${D}${sysconfdir}/data/hostapd-macsec.conf
}

FILES:${PN} = "\
              ${sbindir}/* \
              ${libdir}/* \
              ${includedir}/* \
              ${bindir}/* \
              ${sysconfdir}/* \
              "

FILES_SOLIBSDEV = ""
KERNEL_MODULE_AUTOLOAD += "qca-nss-macsec"
