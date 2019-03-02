package com.shinetvbox.vod.manager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory;
import com.bumptech.glide.module.AppGlideModule;
import com.shinetvbox.vod.MyApplication;

/**
 * 修改glide缓存位置及大小
 * proguard-rules.pro 中添加配置
 * -keep public class  extends com.bumptech.glide.module.AppGlideModule
 * -keep class com.bumptech.glide.GeneratedAppGlideModuleImpl
 */

@GlideModule
public class GlideCacheManager extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        int diskCacheSizeBytes = 1024 * 1024 * 200; // 200 MB
        builder.setDiskCache( new DiskLruCacheFactory( MyApplication.SHINE_GLIDE_CACHE, diskCacheSizeBytes )
        );
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
    }

    /**
     * 清单解析的开启
     * 这里不开启，避免添加相同的modules两次
     * @return
     */
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }


}
