package com.imart.shop.modul;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.engine.cache.MemorySizeCalculator;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.GlideModule;

import java.io.InputStream;

/**
 * A {@link GlideModule} implementation to replace Glide's default
 * {@link java.net.HttpURLConnection} based {@link com.bumptech.glide.load.model.ModelLoader} with a Volley based
 * {@link com.bumptech.glide.load.model.ModelLoader}.
 *
 * <p>
 *     If you're using gradle, you can include this module simply by depending on the aar, the module will be merged
 *     in by manifest merger. For other build systems or for more more information, see
 *     {@link GlideModule}.
 * </p>
 */
public class VolleyGlideModule implements GlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
    	  builder.setDecodeFormat( DecodeFormat.PREFER_RGB_565 );
    	  
        /// memory cache 
        MemorySizeCalculator calculator = new MemorySizeCalculator( context ); 
        int defaultMemoryCacheSize = calculator.getMemoryCacheSize(); 
        int defaultBitmapPoolSize = calculator.getBitmapPoolSize(); 
 
        int customMemoryCacheSize = (int) (1.2 * defaultMemoryCacheSize); 
        int customBitmapPoolSize = (int) (1.2 * defaultBitmapPoolSize); 
 
        builder.setMemoryCache( new LruResourceCache( customMemoryCacheSize ) ); 
        builder.setBitmapPool( new LruBitmapPool( customBitmapPoolSize ) ); 
    }

    @Override
    public void registerComponents(Context context, Glide glide) {
        glide.register(GlideUrl.class, InputStream.class, new VolleyUrlLoader.Factory(context));
    }
}
