package com.huddle.handle.client.resources;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.huddle.handle.android.R;

public class IconStore {
    
	Context context;
	public IconStore(Context context) {
		
		this.context = context;
	}
	
    public Drawable getIconFromMimeTypeClass(String mimeTypeClass) {
    	if (mimeTypeClass == null) {
    		return null;
    	}
        if (mimeTypeClass.endsWith("doc")) return context.getResources().getDrawable(R.drawable.doc);
        if (mimeTypeClass.endsWith("xls")) return context.getResources().getDrawable(R.drawable.xls);
        if (mimeTypeClass.endsWith("zip")) return context.getResources().getDrawable(R.drawable.zip);
        if (mimeTypeClass.endsWith("pdf")) return context.getResources().getDrawable(R.drawable.pdf);
        if (mimeTypeClass.endsWith("ppt")) return context.getResources().getDrawable(R.drawable.ppt);
        if (mimeTypeClass.endsWith("ai")) return context.getResources().getDrawable(R.drawable.ai);
        if (mimeTypeClass.endsWith("bmp")) return context.getResources().getDrawable(R.drawable.bmp);
        if (mimeTypeClass.endsWith("img")) return context.getResources().getDrawable(R.drawable.img);
        if (mimeTypeClass.endsWith("gif")) return context.getResources().getDrawable(R.drawable.gif);
        if (mimeTypeClass.endsWith("jpg") || mimeTypeClass.endsWith("jpeg")) return context.getResources().getDrawable(R.drawable.jpg);
        if (mimeTypeClass.endsWith("tiff")) return context.getResources().getDrawable(R.drawable.tiff);
        if (mimeTypeClass.startsWith("image")) return context.getResources().getDrawable(R.drawable.img);
        return context.getResources().getDrawable(R.drawable.file);
    }

    public Drawable getIconFromMimeType(String mimeType) {
    	if (mimeType == null) {
    		return null;
    	}
        if (mimeType.indexOf("word") > -1 || mimeType.indexOf("wordprocessingml") > -1) return context.getResources().getDrawable(R.drawable.doc);
        if (mimeType.indexOf("excel") > -1 || mimeType.indexOf("spreadsheetml") > -1) return context.getResources().getDrawable(R.drawable.xls);
        if (mimeType.indexOf("powerpoint") > -1 || mimeType.indexOf("presentationml") > -1) return context.getResources().getDrawable(R.drawable.ppt); 
        if (mimeType.endsWith("pdf")) return context.getResources().getDrawable(R.drawable.pdf);
        if (mimeType.endsWith("zip")) return context.getResources().getDrawable(R.drawable.zip);
        if (mimeType.endsWith("postscript")) return context.getResources().getDrawable(R.drawable.ai);
        if (mimeType.endsWith("bmp")) return context.getResources().getDrawable(R.drawable.bmp);
        if (mimeType.endsWith("img")) return context.getResources().getDrawable(R.drawable.img);
        if (mimeType.endsWith("gif")) return context.getResources().getDrawable(R.drawable.gif);
        if (mimeType.endsWith("jpg") || mimeType.endsWith("jpeg")) return context.getResources().getDrawable(R.drawable.gif);
        if (mimeType.endsWith("tiff")) return context.getResources().getDrawable(R.drawable.tiff);
        if (mimeType.startsWith("image")) return context.getResources().getDrawable(R.drawable.img);
        return context.getResources().getDrawable(R.drawable.file);
    }
}
