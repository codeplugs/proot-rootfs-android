#!/system/bin/sh

FILES_DIR="/data/user/0/com.rootfs.android/files"
ALPINE_DIR="$FILES_DIR/alpine"
BIN_DIR="$FILES_DIR/bin"

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
exec "$BIN_DIR/proot" $ARGS "$BUSYBOX" sh