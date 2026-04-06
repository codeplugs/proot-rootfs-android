package com.rootfs.android;

import android.content.Context;
import java.io.*;

public class ShellRunner {

    public interface Output {
        void log(String line);
    }

    public static void run(String file, Context ctx, Output out) {
        new Thread(() -> {
            try {
                File script = new File(ctx.getFilesDir(), file);

                Process p = new ProcessBuilder(
                        "/system/bin/sh",
                        script.getAbsolutePath()
                ).start();

                read(p.getInputStream(), out);
                read(p.getErrorStream(), out);

                p.waitFor();

            } catch (Exception e) {
                out.log("ERR: " + e.getMessage());
            }
        }).start();
    }

    private static void read(InputStream in, Output out) {
        new Thread(() -> {
            try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = br.readLine()) != null) {
                    out.log(line);
                }
            } catch (Exception ignored) {}
        }).start();
    }
}