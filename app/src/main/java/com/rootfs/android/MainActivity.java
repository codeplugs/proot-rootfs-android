package com.rootfs.android;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;


import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import java.net.HttpURLConnection;
import javax.net.ssl.HttpsURLConnection;
import java.util.Arrays;
import java.io.FileDescriptor;
import android.os.ParcelFileDescriptor;
import java.io.FileInputStream;
import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    TextView output;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = findViewById(R.id.output);
        Button btn = findViewById(R.id.btnStart);


String nativeLibDir = getApplicationContext().getApplicationInfo().nativeLibraryDir;
        File libFolder = new File(nativeLibDir);

        if (!libFolder.exists() || !libFolder.isDirectory()) {
           
			output.append("Folder nativeLibraryDir tidak ditemukan: " + nativeLibDir + "\n");
            return;
        }

        output.append("Isi folder nativeLibraryDir: " + nativeLibDir+ "\n");
        File[] filess = libFolder.listFiles();
        if (filess != null) {
            for (File f : filess) {
                
				output.append(f.isDirectory() ? "[DIR] " : "[FILE] " + f.getName() + "\n");
            }
        } else {
            
			output.append("Folder kosong atau tidak bisa diakses." + "\n");
        }




        btn.setOnClickListener(v -> start());
		
		    try {
		Process process = Runtime.getRuntime().exec(
                    new String[]{"sh", "-c", "ls -l " + getFilesDir().getAbsolutePath() + "/ 2>&1"}
            );

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                output.append("LS: " + line+ "\n");
            }

            process.waitFor();

        } catch (Exception e) {
            output.append("LS Error: " + e.getMessage()+ "\n");
        }
		
		
		
		
		
		
    }
	
	
	

    private void log(String s) {
        runOnUiThread(() -> output.append(s + "\n"));
    }


// Helper untuk log stdout dan stderr
private void logProcessOutput(Process process) {
    new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log("[OUT] " + line);
            }
        } catch (Exception e) { log("stdout error: " + e); }
    }).start();

    new Thread(() -> {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(process.getErrorStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                log("[ERR] " + line);
            }
        } catch (Exception e) { log("stderr error: " + e); }
    }).start();
}

/*public void startp(Context context) {
	
	
	
	 new Thread(() -> {
        try {
             String FILES_DIR = context.getFilesDir().getAbsolutePath();
                String ALPINE_DIR = FILES_DIR + "/alpine";
                String BIN_DIR = FILES_DIR + "/bin";
                String LIB_DIR = FILES_DIR + "/lib";

                new File(ALPINE_DIR).mkdirs();
                new File(BIN_DIR).mkdirs();
                new File(LIB_DIR).mkdirs();

                String linker = new File("/system/bin/linker64").exists()
                        ? "/system/bin/linker64"
                        : "/system/bin/linker";

                String prootPath = BIN_DIR + "/proot";

            // -------------------------------
            // STEP 1: Setup base packages
            // -------------------------------
            List<String> setupCmd = new ArrayList<>();
            setupCmd.add(linker);
            setupCmd.add(prootPath);
            setupCmd.add("-r"); setupCmd.add(ALPINE_DIR);
            setupCmd.add("-0");
            setupCmd.add("--link2symlink");
            setupCmd.add("--sysvipc");
            setupCmd.add("-w"); setupCmd.add("/root");
            setupCmd.add("-b"); setupCmd.add("/dev");
            setupCmd.add("-b"); setupCmd.add("/proc");
            setupCmd.add("-b"); setupCmd.add("/sys");

            setupCmd.add("/bin/sh");
            setupCmd.add("-c");
            //setupCmd.add("apk update && apk add alpine-base"); // install base packages
			
            //setupCmd.add("apk update && " + "apk add alpine-base alpine-conf && " + "setup-alpine -f -q");
            
			//setupCmd.add("apk update && apk add alpine-base alpine-conf busybox bash coreutils findutils && printf 'alpine\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n' | setup-alpine -f -q");
			//setupCmd.add("apk update && apk add alpine-base alpine-conf busybox bash coreutils findutils && echo -e 'alpine\\nno\\nnone\\nnone\\nnone\\nnone\\n/var/cache/apk\\n' | setup-alpine -f -q");
           setupCmd.add("apk update && apk add alpine-base alpine-conf busybox bash coreutils findutils && " +
             "printf 'alpine\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n\\n' | " +
             "DEFAULT_DISK=none APKCACHEOPTS='/var/cache/apk' setup-alpine -f -q");

		   log("Starting setup base packages...");
            ProcessBuilder pbSetup = new ProcessBuilder(setupCmd);
           
                Map<String, String> envSetup = pbSetup.environment();
				envSetup.put("PROOT_DEBUG", "1");
                //env.put("PATH", "/system/bin:/system/xbin:" + BIN_DIR + ":/sbin:/vendor/bin");
				//export PATH=/bin:/usr/bin:/sbin:/usr/sbin:$PATH
				// PATH: supaya semua symlink BusyBox bisa ditemukan
                envSetup.put("PATH", "/bin:/usr/bin:/sbin:/usr/sbin:" + BIN_DIR + ":/system/bin:/system/xbin");
                envSetup.put("HOSTNAME", "alpine");
                envSetup.put("HOME", "/root");
                envSetup.put("TERM", "xterm-256color");
                envSetup.put("LANG", "C.UTF-8");
				//envSetup.put("APKCACHEOPTS=","/var/cache/apk");
                String TMPDIR = FILES_DIR + "/tmp";
                //new File(TMPDIR).mkdirs();
                envSetup.put("TMPDIR", TMPDIR);
                String PROOT_TMP = TMPDIR + "/proot";
                new File(PROOT_TMP).mkdirs();
                envSetup.put("PROOT_TMP_DIR", PROOT_TMP);
                envSetup.put("LD_LIBRARY_PATH", LIB_DIR + ":/system/lib64:/system/lib");

                envSetup.put("NATIVE_LIB_DIR", context.getApplicationInfo().nativeLibraryDir);

                String loader = context.getApplicationInfo().nativeLibraryDir + "/libproot-loader.so";
                if (new File(loader).exists()) {
                    envSetup.put("PROOT_LOADER", loader);
                }

            Process setupProcess = pbSetup.start();
            logProcessOutput(setupProcess);
            setupProcess.waitFor();

            log("Base packages setup completed!");

            // -------------------------------
            // STEP 2: Run login shell
            // -------------------------------
            List<String> loginCmd = new ArrayList<>();
            loginCmd.add(linker);
            loginCmd.add(prootPath);
            loginCmd.add("-r"); loginCmd.add(ALPINE_DIR);
            loginCmd.add("-0");
            loginCmd.add("--link2symlink");
            loginCmd.add("--sysvipc");
            loginCmd.add("-w"); loginCmd.add("/root");
            loginCmd.add("-b"); loginCmd.add("/dev");
            loginCmd.add("-b"); loginCmd.add("/proc");
            loginCmd.add("-b"); loginCmd.add("/sys");
            loginCmd.add("-b"); loginCmd.add("/system");
            loginCmd.add("-b"); loginCmd.add("/vendor");
            loginCmd.add("-b"); loginCmd.add("/data");
            loginCmd.add("cat /etc/motd"); // login shell, baca /etc/motd

            log("Starting login shell...");

            ProcessBuilder pbLogin = new ProcessBuilder(loginCmd);
            Map<String, String> envLogin = pbLogin.environment();
            envLogin.putAll(envSetup); // sama env seperti setup
            pbLogin.redirectErrorStream(true);
            Process loginProcess = pbLogin.start();
            logProcessOutput(loginProcess);
            loginProcess.waitFor();

            log("Login shell exited.");

        } catch (Exception e) {
            log("Fatal error: " + e);
        }
    }).start();
	
	
	
     
    }

*/



  public void startp(Context context) {
        new Thread(() -> {
            try {
                String FILES_DIR = context.getFilesDir().getAbsolutePath();
                String ALPINE_DIR = FILES_DIR + "/alpine";
                String BIN_DIR = FILES_DIR + "/bin";
                String LIB_DIR = FILES_DIR + "/lib";

                new File(ALPINE_DIR).mkdirs();
                new File(BIN_DIR).mkdirs();
                new File(LIB_DIR).mkdirs();

                String linker = new File("/system/bin/linker64").exists()
                        ? "/system/bin/linker64"
                        : "/system/bin/linker";

                String prootPath = BIN_DIR + "/proot";

                List<String> cmd = new ArrayList<>();
                cmd.add(linker);
                cmd.add(prootPath);

                // PROOT ARGS
                cmd.add("--kill-on-exit");
                cmd.add("-w"); cmd.add("/root");

                cmd.add("-b"); cmd.add("/dev");
                //cmd.add("-b"); cmd.add("/proc");
                cmd.add("-b"); cmd.add("/sys");
                cmd.add("-b"); cmd.add("/sdcard");
                cmd.add("-b"); cmd.add("/storage");
                cmd.add("-b"); cmd.add("/data");

                //cmd.add("-b"); cmd.add("/proc/self/fd:/dev/fd");
                //cmd.add("-b"); cmd.add("/proc/self/fd/0:/dev/stdin");
                //cmd.add("-b"); cmd.add("/proc/self/fd/1:/dev/stdout");
                //cmd.add("-b"); cmd.add("/proc/self/fd/2:/dev/stderr");

                cmd.add("-b"); cmd.add(ALPINE_DIR + "/tmp:/dev/shm");

                cmd.add("-r"); cmd.add(ALPINE_DIR);
                cmd.add("-0");
                cmd.add("--link2symlink");
                cmd.add("--sysvipc");

                  cmd.add("/bin/sh");
				  cmd.add("-c");
				  // seluruh perintah satu string
                  cmd.add("apk update && apk add alpine-base && apk add expect && exec unbuffer /bin/sh -l");
                  //cmd.add("-l");
				 
				 //cmd.add("/bin/sh");
                 //cmd.add("-c");
                 //cmd.add("target=$(readlink -f /etc/os-release); while IFS= read -r line; do echo \"$line\"; done < \"$target\"");
                
				//cmd.add("-c");
                //cmd.add("echo hello > /data/data/com.rootfs.android/files/tmp/log.txt");

                ProcessBuilder pb = new ProcessBuilder(cmd);

                // ENV
                Map<String, String> env = pb.environment();
				env.put("PROOT_DEBUG", "1");
                //env.put("PATH", "/system/bin:/system/xbin:" + BIN_DIR + ":/sbin:/vendor/bin");
				//export PATH=/bin:/usr/bin:/sbin:/usr/sbin:$PATH
				// PATH: supaya semua symlink BusyBox bisa ditemukan
                env.put("PATH", "/bin:/usr/bin:/sbin:/usr/sbin:" + BIN_DIR + ":/system/bin:/system/xbin");

                env.put("HOME", "/root");
                env.put("TERM", "xterm-256color");
                env.put("LANG", "C.UTF-8");
                String TMPDIR = FILES_DIR + "/tmp";
                //new File(TMPDIR).mkdirs();
                env.put("TMPDIR", TMPDIR);
                String PROOT_TMP = TMPDIR + "/proot";
                new File(PROOT_TMP).mkdirs();
                env.put("PROOT_TMP_DIR", PROOT_TMP);
                env.put("LD_LIBRARY_PATH", LIB_DIR + ":/system/lib64:/system/lib");

                env.put("NATIVE_LIB_DIR", context.getApplicationInfo().nativeLibraryDir);

                String loader = context.getApplicationInfo().nativeLibraryDir + "/libproot-loader.so";
                if (new File(loader).exists()) {
                    env.put("PROOT_LOADER", loader);
                }

                log("Starting proot...");

                Process process = pb.start();

                // =============================
                // 🔥 STDOUT
                // =============================
                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                          log("[OUT] " + line);
                        }
                    } catch (Exception e) {
                        log("stdout error"+ e);
                    }
                }).start();

                // =============================
                // 🔥 STDERR
                // =============================
                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(process.getErrorStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                          log("[ERR] " + line);
                        }
                    } catch (Exception e) {
                        log("stderr error" + e);
                    }
                }).start();

                int exitCode = process.waitFor();
                log("Process exited with code: " + exitCode);

            } catch (Exception e) {
                log("Fatal error"+e);
            }
        }).start();
    }





    private void start() {
        String abi = android.os.Build.SUPPORTED_ABIS[0];

        Map<String, String[]> map = new HashMap<>();
        map.put("arm64-v8a", new String[]{
                "https://raw.githubusercontent.com/Xed-Editor/Karbon-PackagesX/main/aarch64/libtalloc.so.2",
                "https://raw.githubusercontent.com/Xed-Editor/Karbon-PackagesX/main/aarch64/proot",
                "https://dl-cdn.alpinelinux.org/alpine/v3.21/releases/aarch64/alpine-minirootfs-3.21.0-aarch64.tar.gz",
				"https://raw.githubusercontent.com/codeplugs/proot-rootfs-android/refs/heads/main/sho.sh",
				"https://raw.githubusercontent.com/codeplugs/proot-rootfs-android/refs/heads/main/prootsh.sh"
        });

        String[] urls = map.get(abi);
        if (urls == null) {
            log("Unsupported ABI");
            return;
        }

        File base = getFilesDir();

        List<SetupEnvironment.FileItem> list = new ArrayList<>();
        list.add(new SetupEnvironment.FileItem(urls[0], new File(base, "libtalloc.so.2")));
        list.add(new SetupEnvironment.FileItem(urls[1], new File(base, "proot")));
        list.add(new SetupEnvironment.FileItem(urls[2], new File(base, "alpine.tar.gz")));
        list.add(new SetupEnvironment.FileItem(urls[3], new File(base, "init.sh")));
		list.add(new SetupEnvironment.FileItem(urls[3], new File(base, "prootsh.sh")));
        SetupEnvironment.setup(this, list, new SetupEnvironment.Callback() {
            @Override
            public void log(String msg) {
                MainActivity.this.log(msg);
            }

            @Override
            public void done() {
                log("Running script...");
				   ShellRunner.run("init.sh",MainActivity.this, MainActivity.this::log, () -> {
        log("Script done, starting proot...");
        //startp(MainActivity.this);
		
		ShellRunner.run("prootsh.sh",MainActivity.this, MainActivity.this::log, () -> {
        log("Script done, starting proot...");
        //startp(MainActivity.this);
    });
		
		
    });
               
            }

            @Override
            public void error(Exception e) {
                log("ERR: " + e.getMessage());
            }
        });
    }
}

