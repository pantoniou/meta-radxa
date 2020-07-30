require recipes-bsp/u-boot/u-boot.inc

DESCRIPTION = "RockPi U-Boot"

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"
COMPATIBLE_MACHINE = "(rk3036|rk3066|rk3188|rk3288|rk3328|rk3399|rk3308)"

# dunfell _requires_ different S, B
S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

do_compile_prepend () {
	export STAGING_INCDIR=${STAGING_INCDIR_NATIVE};
	export STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE};
}

do_compile_append () {
	# copy to default search path
	if [ ${SPL_BINARY} ]; then
		cp ${B}/spl/${SPL_BINARY} ${B}/
	fi
}

do_deploy_append() {
    cp -a ${B}/tools/mkimage ${DEPLOY_DIR_IMAGE}
}

do_compile_prepend () {
	export STAGING_INCDIR=${STAGING_INCDIR_NATIVE};
	export STAGING_LIBDIR=${STAGING_LIBDIR_NATIVE};
}

do_compile_append () {
	# copy to default search path
	if [ "x${SPL_BINARY}" != "x" ]; then
		cp ${B}/spl/${SPL_BINARY} ${B}/
	fi
	if [ -f "${WORKDIR}/${MACHINE}/boot.cmd" ]; then
		cp ${WORKDIR}/${MACHINE}/boot.cmd ${WORKDIR}/boot.cmd
		${B}/tools/mkimage -C none -A arm -T script -d ${WORKDIR}/boot.cmd ${WORKDIR}/boot.scr
	fi
}

do_deploy_append() {
	cp -a ${B}/tools/mkimage ${DEPLOY_DIR_IMAGE}
	if [ -f "${WORKDIR}/${MACHINE}/uEnv.txt" ]; then
		install -D -m 644 ${WORKDIR}/${MACHINE}/uEnv.txt ${DEPLOYDIR}/
	fi
	if [ -f "${WORKDIR}/boot.scr" ]; then
		install -D -m 644 ${WORKDIR}/boot.scr ${DEPLOYDIR}/
	fi
	if [ -f "${WORKDIR}/boot.cmd" ]; then
		install -D -m 644 ${WORKDIR}/boot.cmd ${DEPLOYDIR}/
	fi
}
