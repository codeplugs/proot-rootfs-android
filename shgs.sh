#!/system/bin/sh

echo "[*] INIT START"

# =============================
# BASE PATH
# =============================
FILES_DIR="/data/user/0/com.rootfs.android/files"
ALPINE_DIR="$FILES_DIR/alpine"
BIN_DIR="$FILES_DIR/bin"
LIB_DIR="$FILES_DIR/lib"

# fakeroot etc
ETC="$FILES_DIR/etc"
HOME_DIR="$FILES_DIR/home"
TMP="$FILES_DIR/tmp"

mkdir -p "$BIN_DIR" "$LIB_DIR" "$TMP"
mkdir -p "$ETC" "$HOME_DIR"

# =============================
# HOSTNAME
# =============================
echo "android" > "$ETC/hostname"
export HOSTNAME="android"

# =============================
# PASSWD & GROUP (FAKE)
# =============================
cat > "$ETC/passwd" <<EOF
root:x:0:0:root:$HOME_DIR:/system/bin/sh
EOF

cat > "$ETC/group" <<EOF
root:x:0:
EOF

# =============================
# PROFILE (PS1 DISINI 🔥)
# =============================
cat > "$ETC/profile" <<EOF
export PATH=/system/bin:/system/xbin:$BIN_DIR
export HOME=$HOME_DIR
export USER=root
export LOGNAME=root
export HOSTNAME=android
export TERM=xterm-256color

# 🔥 PS1
export PS1='[\u@\h \W]\$ '

cd \$HOME
EOF

# =============================
# ENV EXPORT
# =============================
export HOME="$HOME_DIR"
export USER="root"
export LOGNAME="root"
export PATH="/system/bin:/system/xbin:$BIN_DIR"
export TERM="xterm-256color"
export TMPDIR="$TMP"
export PREFIX="$FILES_DIR"

# 🔥 PS1 juga di global (kalau dipakai manual shell)
export PS1='[\u@\h \W]\$ '

# =============================
# FILE BASIC
# =============================
echo "nameserver 8.8.8.8" > "$ETC/resolv.conf"

# =============================
# LIBRARY PATH
# =============================
export LD_LIBRARY_PATH="$LIB_DIR:/system/lib64:/system/lib"

# =============================
# DEBUG
# =============================
echo "[*] HOSTNAME: $HOSTNAME"
echo "[*] HOME: $HOME"
echo "[*] PWD: $(pwd)"
echo "[*] PATH: $PATH"

# =============================
# TEST (OPSIONAL)
# =============================
if [ -f "$BIN_DIR/busybox" ]; then
    "$BIN_DIR/busybox" echo "[*] BusyBox OK"
else
    echo "[!] busybox tidak ditemukan"
fi

# =============================
# DONE (NO SHELL)
# =============================
echo "[*] INIT DONE"
exit 0