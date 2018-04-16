DESCRIPTION = "SSDK Switch Driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${WORKDIR}/files/copyright;md5=0a674a878fe6f6c9e1261ae3767efebd"

inherit module
inherit systemd

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/:"

SRC_URI = "file://qca-ssdk \
	   file://files \
	   "

DEPENDS = "virtual/kernel"

S = "${WORKDIR}/qca-ssdk"

PACKAGES += "kernel-module-ssdk"

EXTRA_OEMAKE += "TOOL_PATH='${STAGING_BINDIR_TOOLCHAIN}' \
		SYS_PATH='${STAGING_KERNEL_BUILDDIR}' \
		TOOLPREFIX='${TARGET_PREFIX}' \
		KVER='${KERNEL_VERSION}' \
		ARCH='arm' -j1 \
		OS='linux' \
		"

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 build/bin/qca-ssdk${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-ssdk
	install -d ${D}${includedir}/qca-ssdk/api
	install -m 0644 ${S}/include/api/sw_ioctl.h ${D}${includedir}/qca-ssdk/api/sw_ioctl.h
	install -d ${D}${includedir}/qca-ssdk/ref
	install -m 0644 ${S}/include/ref/ref_port_ctrl.h ${D}${includedir}/qca-ssdk/ref/ref_port_ctrl.h
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/files/qca-ssdk ${D}${bindir}/qca-ssdk
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${WORKDIR}/files/qca-ssdk.service ${D}${systemd_unitdir}/system/qca-ssdk.service
}

SYSTEMD_SERVICE_${PN} += "qca-ssdk.service"

FILES_${PN} = " \
	${systemd_unitdir}/system/qca-ssdk.service \
	${bindir}/qca-ssdk \
"

KERNEL_MODULE_AUTOLOAD += "qca-ssdk"
module_autoload_qca-ssdk = "qca-ssdk"
