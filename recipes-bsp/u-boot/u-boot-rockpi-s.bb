DEFAULT_PREFERENCE = "1"

DESCRIPTION = "RockPi-S U-Boot"

include u-boot-rockpi.inc

SRC_URI = " \
	git://github.com/radxa/u-boot.git;branch=stable-4.4-rockpis; \
	file://0001-Use-local-command.h-file-instead-of-system-file.patch \
	file://0001-Makefile-disable-gcc9-s-address-of-packed-member-err.patch \
	file://0002-Makefile-add-u-boot-initial-env-target-which-dunfell.patch \
	file://${MACHINE}/boot.cmd \
	file://${MACHINE}/uEnv.txt \
"
SRCREV = "6de46245e680db858d74437dfd655dbf9600e48d"
