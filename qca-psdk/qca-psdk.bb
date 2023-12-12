DESCRIPTION = "QCA PSDK driver"
LICENSE = "ISC"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/files/common-licenses/${LICENSE};md5=f3b90e78ea0cffb20bf5cca7947a896d"

inherit module

FILESPATH =+ "${TOPDIR}/../opensource/:"

SRC_URI = "file://qca-psdk \
           file://qca-ssdk \
           file://qca-ssdk-shell \
           file://0001-qca-psdk-shell-support-to-build-with-yocto.patch\
          "

DEPENDS = "virtual/kernel"

S = "${WORKDIR}/qca-psdk"

PR = "r0"

INSANE_SKIP:${PN} += "already-stripped"


CFLAGS +="-I${STAGING_INCDIR}"
CFLAGS +="-mfpu=neon -mfloat-abi=hard"
CFLAGS:remove_aarch64 ="-mfpu=neon -mfloat-abi=hard"

LDFLAGS +="--sysroot=${STAGING_DIR_TARGET}"

QCA_PSDK_CONFIG_OPTS+= "TOOL_PATH=${STAGING_BINDIR_TOOLCHAIN} \
                       LINUX_DIR=${STAGING_KERNEL_BUILDDIR} \
                       TOOLPREFIX=${TARGET_PREFIX} \
                       CROSS_COMPILE=${TARGET_PREFIX} \
                       KVER=${KERNEL_VERSION} \
                       OS='linux' \
                       ARCH=${KARCH}"

do_compile() {
    ${MAKE} -C ${S} ${QCA_PSDK_CONFIG_OPTS}
}

do_configure:prepend () {
    cd ${S}/scripts;perl qca-ssdk2psdk.pl
}

do_install() {
    install -d ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra/
    install -m 0755 obj/*.ko ${D}${base_libdir}/modules/${KERNEL_VERSION}/extra/
    install -d ${D}${sbindir}
    install -m 0755 obj/psdk_cli ${D}${sbindir}
}

FILES:${PN} = "${sbindir}/psdk_cli"
