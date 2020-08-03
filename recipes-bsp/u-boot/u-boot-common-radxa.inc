HOMEPAGE = "https://github.com/radxa/u-boot"
SECTION = "bootloaders"
DEPENDS += "flex-native bison-native"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"
PE = "1"

SRC_URI = " \
	git://github.com/radxa/u-boot.git;branch=stable-4.4-rockpis; \
	file://0001-Use-local-command.h-file-instead-of-system-file.patch \
	file://0001-Makefile-disable-gcc9-s-address-of-packed-member-err.patch \
	file://0002-Makefile-add-u-boot-initial-env-target-which-dunfell.patch \
	file://${MACHINE}/boot.cmd \
	file://${MACHINE}/uEnv.txt \
"
SRCREV = "6de46245e680db858d74437dfd655dbf9600e48d"

S = "${WORKDIR}/git"
