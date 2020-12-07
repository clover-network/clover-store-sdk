package com.sk.cloversdk;

import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.yanzhenjie.andserver.annotation.Config;
import com.yanzhenjie.andserver.framework.config.Multipart;
import com.yanzhenjie.andserver.framework.config.WebConfig;
import com.yanzhenjie.andserver.framework.website.StorageWebsite;

import java.io.File;

@Config
public class AppConfig implements WebConfig {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onConfig(Context context, Delegate delegate) {
        delegate.addWebsite(new StorageWebsite(
                context.getApplicationContext().getDataDir().getAbsolutePath() +
                        "/" + Constant.OS_CONTENT_DIR));
        delegate.setMultipart(Multipart.newBuilder()
                .allFileMaxSize(1024 * 1024 * 20) // 20M
                .fileMaxSize(1024 * 1024 * 500) // 5M
                .maxInMemorySize(1024 * 1024 * 100) // 1024 * 10 bytes
                .uploadTempDir(new File(context.getCacheDir(), "_server_upload_cache_"))
                .build());
    }
}
