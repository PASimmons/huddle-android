package com.huddle.handle.android;

import com.huddle.handle.android.R;
import android.app.Activity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.widget.ListView;
import android.widget.TextView;

/**
 * This activity allows you to have multiple views (in this case two {@link ListView}s)
 * in one tab activity.  The advantages over separate activities is that you can
 * maintain tab state much easier and you don't have to constantly re-create each tab
 * activity when the tab is selected.
 */
public class AboutActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        
        TextView text = (TextView)findViewById(R.id.about_text);
        Linkify.addLinks(text, Linkify.ALL);
    }
}