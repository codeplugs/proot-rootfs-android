FILES_DIR="/data/user/0/com.rootfs.android/files"
BIN_DIR="$FILES_DIR/bin"


# LINKER FIX
if [ -f "/system/bin/linker64" ]; then
    LINKER="/system/bin/linker64"
else
    LINKER="/system/bin/linker"
fi
export LINKER

ARGS="--kill-on-exit"
ARGS="$ARGS -w /root"

ARGS="$ARGS -b /dev"
ARGS="$ARGS -b /proc"
ARGS="$ARGS -b /sys"
ARGS="$ARGS -b /sdcard"
ARGS="$ARGS -b /storage"
ARGS="$ARGS -b /data"



# SHM FIX
mkdir -p "$ALPINE_DIR/tmp"
chmod 1777 "$ALPINE_DIR/tmp"
ARGS="$ARGS -b $ALPINE_DIR/tmp:/dev/shm"

# ROOTFS
ARGS="$ARGS -r $ALPINE_DIR"
ARGS="$ARGS -0"
ARGS="$ARGS --link2symlink"
ARGS="$ARGS --sysvipc"

echo "[*] Starting Alpine with BusyBox..."

exec $LINKER "$BIN_DIR/proot" $ARGS /bin/sh -l -c "busybox"