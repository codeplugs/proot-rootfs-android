#!/system/bin/sh

# =============================
# 🔥 BASE PATH
# =============================
FILES_DIR="/data/user/0/com.rootfs.android/files"
ALPINE_DIR="$FILES_DIR/alpine"
BIN_DIR="$FILES_DIR/bin"
LIB_DIR="$FILES_DIR/lib"

mkdir -p "$ALPINE_DIR" "$BIN_DIR" "$LIB_DIR"

# =============================
# 🔥 ENV
# =============================

export PATH="/system/bin:/system/xbin:$BIN_DIR:/sbin:/vendor/bin"

export HOME="/root"


export BIN="$BIN_DIR"
export PREFIX="$FILES_DIR"

# TMP
TMPDIR="$FILES_DIR/tmp"
mkdir -p "$TMPDIR"
export TMPDIR="$TMPDIR"

# PROOT TMP
PROOT_TMP_DIR="$TMPDIR/proot"
mkdir -p "$PROOT_TMP_DIR"
export PROOT_TMP_DIR


# =============================
# 🔥 EXTRACT ROOTFS
# =============================
echo "[*] Extract tai"

# =============================
# 🔥 COPY PROOT
# =============================
echo "[*] Extract tai"

# =============================
# 🔥 proot
# =============================

# LINKER
if [ -f "/system/bin/linker64" ]; then
    LINKER="/system/bin/linker64"
else
    LINKER="/system/bin/linker"
fi

export LINKER

# Pastikan rootfs ada
mkdir -p "$ALPINE_DIR"
mkdir -p "$ALPINE_DIR/tmp"
chmod 1777 "$ALPINE_DIR/tmp"

# PROOT ARGS
ARGS="--kill-on-exit"
ARGS="$ARGS -w /root"
ARGS="$ARGS -b /dev"
ARGS="$ARGS -b /proc"
ARGS="$ARGS -b /sys"
ARGS="$ARGS -b /sdcard"
ARGS="$ARGS -b /storage"
ARGS="$ARGS -b /data"
ARGS="$ARGS -b $ALPINE_DIR/tmp:/dev/shm"
ARGS="$ARGS -r $ALPINE_DIR -0 --link2symlink --sysvipc"

echo "[*] Starting Alpine with BusyBox..."

# 🔹 PASTIKAN PATH BUSYBOX BENAR
BUSYBOX="$ALPINE_DIR/bin/busybox"
if [ ! -f "$BUSYBOX" ]; then
    echo "[!] BusyBox not found at $BUSYBOX"
    exit 1
fi

# 🔹 EXEC PROOT + BUSYBOX SH
exec $LINKER "$BIN_DIR/proot" $ARGS "$BUSYBOX" sh

done

