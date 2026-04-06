package com.rootfs.android;


import android.content.Context;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SetupEnvironment {

    public interface Callback {
        void log(String msg);
        void done();
        void error(Exception e);
    }

    public static class FileItem {
        String url;
        File file;
        public FileItem(String u, File f) { url = u; file = f; }
    }

    public static void setup(Context ctx, List<FileItem> files, Callback cb) {
        new Thread(() -> {
            try {
                for (FileItem f : files) {
                    if (!f.file.exists()) {
                        cb.log("Downloading " + f.file.getName());
                        download(f.url, f.file, cb);
                    } else {
                        cb.log("Skip " + f.file.getName());
                    }
                    f.file.setExecutable(true);
                }

                

                cb.done();

            } catch (Exception e) {
                cb.error(e);
            }
        }).start();
    }

    private static void download(String urlStr, File out, Callback cb) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.connect();

        if (conn.getResponseCode() != 200) {
            throw new IOException("HTTP error: " + conn.getResponseCode());
        }

        InputStream in = conn.getInputStream();
        FileOutputStream fos = new FileOutputStream(out);

        byte[] buffer = new byte[8192];
        int len;
        long total = 0;

        while ((len = in.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
            total += len;

            // optional log progress
            if (total % (1024 * 1024) < 8192) {
                cb.log("Downloaded " + (total / 1024) + " KB...");
            }
        }

        fos.close();
        in.close();
        conn.disconnect();

        cb.log("Done: " + out.getName());
    }

  
}