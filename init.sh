#!/system/bin/sh

set -e
set -x

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
export PATH="/system/bin:/system/xbin:/sbin:/vendor/bin:$BIN_DIR"

export HOME="/root"
export PREFIX="$FILES_DIR"

# TMP
TMPDIR="$FILES_DIR/tmp"
mkdir -p "$TMPDIR"
export TMPDIR

# PROOT TMP (biar konsisten walau belum dipakai)
PROOT_TMP_DIR="$TMPDIR/proot"
mkdir -p "$PROOT_TMP_DIR"
export PROOT_TMP_DIR

# ⚠️ JANGAN AGRESIF
export LD_LIBRARY_PATH="$LIB_DIR"

# =============================
# 🔥 DEBUG INFO
# =============================
echo "[*] FILES_DIR=$FILES_DIR"
echo "[*] ALPINE_DIR=$ALPINE_DIR"

# =============================
# 🔥 EXTRACT ROOTFS
# =============================
if [ ! -f "$ALPINE_DIR/bin/sh" ]; then
    echo "[*] Extract rootfs..."
    tar -xzf "$FILES_DIR/alpine.tar.gz" -C "$ALPINE_DIR"
else
    echo "[*] Rootfs already exists"
fi

# =============================
# 🔥 COPY PROOT (optional)
# =============================
if [ -f "$FILES_DIR/proot" ] && [ ! -f "$BIN_DIR/proot" ]; then
    echo "[*] Copy proot"
    cp "$FILES_DIR/proot" "$BIN_DIR/proot"
    chmod 755 "$BIN_DIR/proot"
fi

# =============================
# 🔥 COPY LIB (SAFE)
# =============================
for sofile in "$FILES_DIR"/*.so*; do
    [ -e "$sofile" ] || continue
    dest="$LIB_DIR/$(basename "$sofile")"
    if [ ! -f "$dest" ]; then
        echo "[*] Copy lib $(basename "$sofile")"
        cp "$sofile" "$dest"
        chmod 644 "$dest"
    fi
done

# =============================
# 🔥 FINAL CHECK
# =============================
echo "[*] DONE SETUP"

ls -l "$FILES_DIR"
ls -l "$ALPINE_DIR/bin" || true

echo "[*] Script done"
