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

EXTRA_CFLAGS = "-I${STAGING_INCDIR} -fno-pic"
EXTRA_CFLAGS_append_aarch64 += " -mcmodel=large -mfix-cortex-a53-843419"
EXTRA_OEMAKE += "TOOL_PATH='${STAGING_BINDIR_TOOLCHAIN}' \
		SYS_PATH='${STAGING_KERNEL_BUILDDIR}' \
		TOOLPREFIX='${TARGET_PREFIX}' \
		KVER='${KERNEL_VERSION}' \
		ARCH='${KARCH}' -j1 \
		OS='linux' \
		EXTRA_CFLAGS='${EXTRA_CFLAGS}' \
		"

EXTRA_OEMAKE_append_ipq807x += "HK_CHIP=enable"
EXTRA_OEMAKE_append_ipq95xx += "CHIP_TYPE=APPE "
EXTRA_OEMAKE_append_ipq95xx += "PTP_FEATURE=enable "

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 build/bin/qca-ssdk${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -d ${D}${includedir}/qca-ssdk
	install -d ${D}${includedir}/qca-ssdk/api
	install -d ${D}${includedir}/qca-ssdk/ref
	install -d ${D}${includedir}/qca-ssdk/fal
	install -d ${D}${includedir}/qca-ssdk/sal
	install -d ${D}${includedir}/qca-ssdk/init
	install -m 0644 ${S}/include/api/sw_ioctl.h ${D}${includedir}/qca-ssdk/api/sw_ioctl.h
	if [ -f ${S}/include/ref/ref_vsi.h ]; then \
	install -m 0644 ${S}/include/ref/ref_vsi.h ${D}${includedir}/qca-ssdk/ref/ref_vsi.h
	fi
	if [ -f ${S}/include/ref/ref_fdb.h ]; then \
	install -m 0644 ${S}/include/ref/ref_fdb.h ${D}${includedir}/qca-ssdk/ref/ref_fdb.h
	fi
	if [ -f ${S}/include/ref/ref_port_ctrl.h ]; then \
	install -m 0644 ${S}/include/ref/ref_port_ctrl.h ${D}${includedir}/qca-ssdk/ref/ref_port_ctrl.h
	fi
	if [ -f ${S}/include/init/ssdk_init.h ]; then \
	install -m 0644 ${S}/include/init/ssdk_init.h ${D}${includedir}/qca-ssdk/init/ssdk_init.h
	fi
	install -m 0644 ${S}/include/fal/* ${D}${includedir}/qca-ssdk/fal
	install -m 0644 ${S}/include/common/*.h ${D}${includedir}/qca-ssdk
	install -m 0644 ${S}/include/sal/os/linux/*.h ${D}${includedir}/qca-ssdk
	install -m 0644 ${S}/include/sal/os/*.h ${D}${includedir}/qca-ssdk
	install -d ${D}${bindir}
	install -m 0755 ${WORKDIR}/files/qca-ssdk ${D}${bindir}/qca-ssdk
	install -d ${D}${systemd_unitdir}/system
	install -m 0644 ${S}/temp/Module.symvers ${D}${includedir}/qca-ssdk/Module.symvers
	install -m 0644 ${WORKDIR}/files/qca-ssdk.service ${D}${systemd_unitdir}/system/qca-ssdk.service
}

SYSTEMD_SERVICE_${PN} += "qca-ssdk.service"

FILES_${PN} = " \
	${systemd_unitdir}/system/qca-ssdk.service \
	${bindir}/qca-ssdk \
"

KERNEL_MODULE_AUTOLOAD += "qca-ssdk"
module_autoload_qca-ssdk = "qca-ssdk"
