DESCRIPTION = "SSDK Switch Driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${WORKDIR}/copyright;md5=0a674a878fe6f6c9e1261ae3767efebd"


FILESPATH =+ "${TOPDIR}/../opensource/:"
FILESEXTRAPATHS_prepend := "${THISDIR}/files/:"
SRC_URI = "file://copyright \
	   file://qca-ssdk-shell \
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
