DESCRIPTION = "SSDK Switch Driver"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://ChangeLog;md5=26274509bf48473d672d111c90379232"

inherit module

FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

SRC_URI = "file://qca-ssdk \
	   "

DEPENDS = "virtual/kernel"

S = "${WORKDIR}/qca-ssdk"

PACKAGES += "kernel-module-ssdk"

EXTRA_OEMAKE += "TOOL_PATH='${STAGING_BINDIR_TOOLCHAIN}' \
		SYS_PATH='${STAGING_KERNEL_BUILDDIR}' \
		TOOLPREFIX='arm-poky-linux-gnueabi-' \
		KVER='${KERNEL_VERSION}' \
		ARCH='arm' -j1 \
		"

do_install() {
	install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
	install -m 0644 build/bin/qca-ssdk${KERNEL_OBJECT_SUFFIX} ${D}${base_libdir}/modules/${KERNEL_VERSION}/kernel/drivers/${PN}
}

KERNEL_MODULE_AUTOLOAD += "qca-ssdk"
