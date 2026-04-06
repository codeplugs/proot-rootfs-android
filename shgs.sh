#!/bin/sh

echo "[*] Initializing Alpine"

# =========================
# 🔥 CONFIG ROOTFS PATH
# =========================
FILES_DIR="/data/user/0/com.rootfs.android/files"
ALPINE_DIR="$FILES_DIR/alpine"

echo "[*] Rootfs path: $ALPINE_DIR"
# TMP
TMPDIR="$FILES_DIR/tmp"
export TMPDIR="$TMPDIR"

# =========================
# 🔥 BASIC SYSTEM CONFIG
# =========================

echo "localhost" > "$ALPINE_DIR/etc/hostname"

cat > "$ALPINE_DIR/etc/hosts" << 'EOF'
127.0.0.1   localhost localhost.localdomain
::1         localhost localhost.localdomain
EOF

cat > "$ALPINE_DIR/etc/resolv.conf" << 'EOF'
nameserver 8.8.8.8
nameserver 1.1.1.1
EOF

cat > "$ALPINE_DIR/etc/apk/repositories" << 'EOF'
https://dl-cdn.alpinelinux.org/alpine/latest-stable/main
https://dl-cdn.alpinelinux.org/alpine/latest-stable/community
EOF

# =========================
# 🔥 REMOVE SETUP-ALPINE
# =========================

rm -f "$ALPINE_DIR/sbin/setup-alpine"
rm -rf "$ALPINE_DIR/etc/alpine-release" 2>/dev/null

# =========================
# 🔥 FIX USER SYSTEM
# =========================

cat > "$ALPINE_DIR/etc/passwd" << 'EOF'
root:x:0:0:root:/root:/bin/sh
EOF

cat > "$ALPINE_DIR/etc/group" << 'EOF'
root:x:0:
EOF

cat > "$ALPINE_DIR/etc/shadow" << 'EOF'
root::0:0:99999:7:::
EOF


# =========================
# 🔥 MOTD & PROFILE
# =========================
mkdir -p "$ALPINE_DIR/etc/profile.d"

cat > "$ALPINE_DIR/etc/profile.d/prompt.sh" << 'EOF'
export PS1="localhost:~# "
EOF

# =========================
# 🔥 INIT FLAG
# =========================

touch "$ALPINE_DIR/root/.initialized"

echo "[*] Alpine rootfs ready"