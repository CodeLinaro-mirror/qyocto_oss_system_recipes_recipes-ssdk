DESCRIPTION = "SSDK Switch Driver"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://config;md5=06b5dbe32e9526e1c00f5bda08fa9e00"


FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files/:"
SRC_URI = "file://qca-ssdk-shell \
	   file://0001-qca-ssdk-shell-Add-support-to-build-with-yocto.patch \
	   "

INSANE_SKIP_${PN} += "already-stripped"

S = "${WORKDIR}/qca-ssdk-shell"

do_compile() {
	${MAKE} -C ${S} TOOL_PATH='${STAGING_BINDIR_TOOLCHAIN}' \
		SYS_PATH='${STAGING_KERNEL_BUILDDIR}' \
		TOOLPREFIX='arm-poky-linux-gnueabi-' \
		KVER='${KERNEL_VERSION}' \
		ARCH='arm'
}

do_install() {
	install -d ${D}${bindir}
	install -m 0755 build/bin/ssdk_sh ${D}${bindir}
}
