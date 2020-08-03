inherit native deploy

DESCRIPTION = "Rockchip-Radxa binaries generation"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=3000208d539ec061b899bce1d9ce9404"
PR = "r0"

DEPENDS = "radxa-binary-native radxa-binary-loader u-boot-mkimage-radxa-native virtual/bootloader"

SRC_URI = "file://LICENSE \
	   "

inherit deploy

DDR_BIN = "ddr.bin"
LOADER_BIN = "loader.bin"
MINILOADER_BIN = "miniloader.bin"
ATF_BIN = "atf.bin"
BL31_ELF = "bl31.elf"

IDBLOADER = "idbloader.img"
TRUST_IMG = "trust.img"
UBOOT_IMG = "u-boot.img"

FW_DIR_NAME="radxa-binary-loader"

# RKBINARY_DEPLOY_DIR = "${DEPLOY_DIR_IMAGE}/radxa-binary-${SOC_FAMILY}"
RKBINARY_DEPLOY_DIR = "${DEPLOY_DIR_IMAGE}/${FW_DIR_NAME}"

do_compile () {

	bbnote "radxa-binaries: generating ${IDBLOADER}"
	mkimage -n ${SOC_FAMILY} -T rksd -d ${RKBINARY_DEPLOY_DIR}/${DDR_BIN} ${IDBLOADER}
	cat ${RKBINARY_DEPLOY_DIR}/${MINILOADER_BIN} >>${IDBLOADER}

	bbnote "radxa-binaries: generating ${TRUST_IMG}"
	cat >trust.ini <<EOF
[VERSION]
MAJOR=1
MINOR=0
[BL30_OPTION]
SEC=0
[BL31_OPTION]
SEC=1
PATH=${RKBINARY_DEPLOY_DIR}/${BL31_ELF}
ADDR=0x10000
[BL32_OPTION]
SEC=0
[BL33_OPTION]
SEC=0
[OUTPUT]
PATH=${TRUST_IMG}
EOF

	trust_merger --size 1024 1 trust.ini

	# for now only rk3308 has a different offset
	if [ "${SOC_FAMILY}" == "rk3308" ]; then
		UBOOT_OFFSET="0x600000"
	else
		UBOOT_OFFSET="0x200000"
	fi

	bbnote "radxa-binary-generator: generating ${UBOOT_IMG} at offset ${UBOOT_OFFSET}"
	loaderimage --pack --uboot ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.bin ${UBOOT_IMG} ${UBOOT_OFFSET} --size 1024 1
}

do_deploy () {

	install -d ${DEPLOYDIR}/${PN}

	bbnote "radxa-binaries: deploying at ${DEPLOYDIR}/${PN}"

	install -m 0644 ${B}/${IDBLOADER} ${DEPLOYDIR}/${PN}
	install -m 0644 ${B}/${UBOOT_IMG} ${DEPLOYDIR}/${PN}
	install -m 0644 ${B}/${TRUST_IMG} ${DEPLOYDIR}/${PN}

	touch ${DEPLOYDIR}/${PN}/${PN}-${PV}.stamp
}

addtask deploy before do_build after do_compile

do_radxa_binaries[depends] += " \
	radxa-binary-native:do_populate_sysroot \
	radxa-binary-loader:do_deploy \
	virtual/kernel:do_deploy \
	virtual/bootloader:do_deploy"
do_deploy[dirs] += "${DEPLOYDIR}/${PN}"

# COMPATIBLE_MACHINE = "^rock-pi-*(rk3399|rk3328|rk3308)$"

PACKAGE_ARCH = "${MACHINE_ARCH}"

