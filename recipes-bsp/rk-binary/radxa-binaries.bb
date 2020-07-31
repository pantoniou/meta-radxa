DESCRIPTION = "Rockchip-Radxa binaries generation"

LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${WORKDIR}/LICENSE;md5=3000208d539ec061b899bce1d9ce9404"
PR = "r0"

DEPENDS = "radxa-binary-native radxa-binary-loader"

SRC_URI = "file://LICENSE \
	   "

inherit deploy

DDR_BIN = "radxa-binary/ddr.bin"
LOADER_BIN = "radxa-binary/loader.bin"
MINILOADER_BIN = "radxa-binary/miniloader.bin"
ATF_BIN = "radxa-binary/atf.bin"
BL31_ELF = "radxa-binary/bl31.elf"

IDBLOADER = "idbloader.img"
TRUST_IMG = "trust.img"
UBOOT_IMG = "u-boot.img"

UBOOT_OFFSET_rk3308 ?= "0x600000"
UBOOT_OFFSET_rk3328 ?= "0x200000"
UBOOT_OFFSET_rk3399 ?= "0x200000"

do_deploy () {
	bbnote "radxa-binary-generator: deploy WORKDIR=${WORKDIR} DEPLOY_DIR_IMAGE=${DEPLOY_DIR_IMAGE}"

	bbnote "radxa-binary-generator: generating ${IDBLOADER}"
	${DEPLOY_DIR_IMAGE}/mkimage -n ${SOC_FAMILY} -T rksd -d ${DEPLOY_DIR_IMAGE}/${DDR_BIN} ${DEPLOY_DIR_IMAGE}/${IDBLOADER}
	cat ${DEPLOY_DIR_IMAGE}/${MINILOADER_BIN} >>${DEPLOY_DIR_IMAGE}/${IDBLOADER}

	bbnote "radxa-binary-generator: generating ${TRUST_IMG}"
	cat >${DEPLOY_DIR_IMAGE}/trust.ini <<EOF
[VERSION]
MAJOR=1
MINOR=0
[BL30_OPTION]
SEC=0
[BL31_OPTION]
SEC=1
PATH=${DEPLOY_DIR_IMAGE}/${BL31_ELF}
ADDR=0x10000
[BL32_OPTION]
SEC=0
[BL33_OPTION]
SEC=0
[OUTPUT]
PATH=${DEPLOY_DIR_IMAGE}/${TRUST_IMG}
EOF
	trust_merger --size 1024 1 ${DEPLOY_DIR_IMAGE}/trust.ini

	bbnote "radxa-binary-generator: generating ${UBOOT_IMG}"
	loaderimage --pack --uboot ${DEPLOY_DIR_IMAGE}/u-boot-${MACHINE}.bin ${DEPLOY_DIR_IMAGE}/${UBOOT_IMG} ${UBOOT_OFFSET} --size 1024 1
}

addtask deploy before do_build after do_compile

do_radxa_binary_generator[depends] += " \
	radxa-binary-native:do_populate_sysroot \
	radxa-binary-loader:do_deploy \
	virtual/kernel:do_deploy \
	virtual/bootloader:do_deploy"

do_package[noexec] = "1"
do_packagedata[noexec] = "1"
do_package_write[noexec] = "1"
do_package_write_ipk[noexec] = "1"
do_package_write_rpm[noexec] = "1"
do_package_write_deb[noexec] = "1"
do_package_write_tar[noexec] = "1"
