package com.rootfs.android;

import android.content.Context;
import java.io.*;

public class ShellRunner {

    public interface Output {
        void log(String line);
    }

  
	
	public static void run(String file, Context ctx, Output out, Runnable onDone) {
    new Thread(() -> {
        try {
            Process p = new ProcessBuilder(
                    "/system/bin/sh",
                    new File(ctx.getFilesDir(), file).getAbsolutePath()
            ).start();

            new Thread(() -> read(p.getInputStream(), out)).start();
            new Thread(() -> read(p.getErrorStream(), out)).start();

            p.waitFor();

            if (onDone != null) onDone.run();

        } catch (Exception e) {
            out.log("ERR: " + e);
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