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
if [ -z "$(ls -A "$ALPINE_DIR" 2>/dev/null)" ]; then
    echo "[*] Extract rootfs..."
    tar -xzf "$FILES_DIR/alpine.tar.gz" -C "$ALPINE_DIR"
fi

# =============================
# 🔥 COPY PROOT
# =============================
if [ ! -f "$BIN_DIR/proot" ]; then
    cp "$FILES_DIR/proot" "$BIN_DIR/proot"
    chmod 755 "$BIN_DIR/proot"
fi

# =============================
# 🔥 COPY LIB
# =============================
for sofile in "$FILES_DIR/"*.so*; do
    dest="$LIB_DIR/$(basename "$sofile")"
    if [ ! -f "$dest" ]; then
        cp "$sofile" "$dest"
        chmod 644 "$dest"
    fi
done

