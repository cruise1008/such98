package me.tgmerge.such98.util;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;
import android.util.Pair;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

/**
 * Created by tgmerge on 2/17.
 * Provides methods to render BBCode(to html, on TextView, load images, etc)
 */
public class TextUtil {

    public static final String longTimeString(String datetimeFromApi) {
        return datetimeFromApi.replaceFirst("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})(.\\d+)?", "$1-$2-$3 $4:$5");
    }

    public static final String shortTimeString(String datetimeFromApi) {
        return datetimeFromApi.replaceFirst("(\\d{4})-(\\d{2})-(\\d{2})T(\\d{2}):(\\d{2}):(\\d{2})(.\\d+)?", "$2-$3 $4:$5");
    }

    public static final String abbrTimeString(String datetimeFromApi) {
        Date date = Calendar.getInstance().getTime();
        String thisYear = new SimpleDateFormat("yyyy").format(date);
        String today = new SimpleDateFormat("yyyy-MM-dd").format(date);

        String longString = longTimeString(datetimeFromApi);
        String result = longString.replace(today + " ", "");
        if (result.equals(longString)) {
            result = result.replaceFirst(" \\d{2}:\\d{2}", "");
        }
        result = result.replace(thisYear + "-", "");
        return result;
    }

    public static final void setBBcodeToTextView(TextView textView, Context ctx, String bb) {
        String html = bb2html(bb);
        //Spanned spanned = Html.fromHtml(html, new UILImageGetter(textView, ctx), null);
         //todo  UILgetter not working
        Spanned spanned = Html.fromHtml(html, null, null);
        textView.setText(spanned);
    }

    // - - - private for ubb - - -

    private static final Vector<Pair<String, String>> bbMap = new Vector<>();

    static {
        bbMap.add(new Pair<>("(\r\n|\r|\n|\n\r)", "<br/>"));

        bbMap.add(new Pair<>("\\[b\\]", "<b>"));
        bbMap.add(new Pair<>("\\[/b\\]", "</b>"));

        bbMap.add(new Pair<>("\\[i\\]", "<i>"));
        bbMap.add(new Pair<>("\\[/i\\]", "</i>"));

        bbMap.add(new Pair<>("\\[u\\]", "<u>"));
        bbMap.add(new Pair<>("\\[/u\\]", "</u>"));

        bbMap.add(new Pair<>("\\[h1\\](.+?)\\[/h1\\]", "<h1>$1</h1>"));
        bbMap.add(new Pair<>("\\[h2\\](.+?)\\[/h2\\]", "<h2>$1</h2>"));
        bbMap.add(new Pair<>("\\[h3\\](.+?)\\[/h3\\]", "<h3>$1</h3>"));
        bbMap.add(new Pair<>("\\[h4\\](.+?)\\[/h4\\]", "<h4>$1</h4>"));
        bbMap.add(new Pair<>("\\[h5\\](.+?)\\[/h5\\]", "<h5>$1</h5>"));
        bbMap.add(new Pair<>("\\[h6\\](.+?)\\[/h6\\]", "<h6>$1</h6>"));

        bbMap.add(new Pair<>("\\[quote\\]", "<blockquote>"));
        bbMap.add(new Pair<>("\\[/quote\\]", "</blockquote>"));

        bbMap.add(new Pair<>("\\[quotex\\]", "<blockquote>"));
        bbMap.add(new Pair<>("\\[/quotex\\]", "</blockquote>"));

        bbMap.add(new Pair<>("\\[p\\]", "<p>"));
        bbMap.add(new Pair<>("\\[/p\\]", "</p>"));

        bbMap.add(new Pair<>("\\[right\\]", "<div align='right'>"));
        bbMap.add(new Pair<>("\\[/right\\]", "</div>"));

        bbMap.add(new Pair<>("\\[center\\]", "<div align='center'>"));
        bbMap.add(new Pair<>("\\[/center\\]", "</div>"));

        bbMap.add(new Pair<>("\\[align=(.+?)\\]", "<div align='$1'>"));
        bbMap.add(new Pair<>("\\[/align\\]", "</div>"));

        // disabled: size
        bbMap.add(new Pair<>("\\[size=(.+?)\\]", "<font>"));
        bbMap.add(new Pair<>("\\[/size\\]", "</font>"));

        bbMap.add(new Pair<>("\\[font=(.+?)\\]", "<font face='$1'>"));
        bbMap.add(new Pair<>("\\[/font\\]", "</font>"));

        bbMap.add(new Pair<>("\\[color=(.+?)\\]", "<font color=$1>"));
        bbMap.add(new Pair<>("\\[/color\\]", "</font>"));

        //bbMap.add(new Pair<>("\\[email\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$1</a>"));
        //bbMap.add(new Pair<>("\\[email=(.+?)\\](.+?)\\[/email\\]", "<a href='mailto:$1'>$2</a>"));

        bbMap.add(new Pair<>("\\[url\\]([^\\[]+?)\\[/url\\]", "<a href='such98://url/$1'>$1</a>"));
        bbMap.add(new Pair<>("\\[url=([^\\[]+?)\\]", "<a href='such98://url/$1'>"));

        bbMap.add(new Pair<>("\\[/url\\]", "</a>"));

        //bbMap.add(new Pair<>("\\[img=(.+?),(.+?)\\](.+?)\\[/img\\]", "<img width='$1' height='$2' src='$3' />"));
        //bbMap.add(new Pair<>("\\[upload=?(bmp|png|gif|jpg|jpeg|jpe|tif|tiff)?(,\\d)?\\](.+?)\\[/upload\\]", "<img src='$3' />"));

        // convert img tags to image links before in-app image viewer is ready...
        bbMap.add(new Pair<>("\\[img(=.+?)?\\](.+?)\\[/img\\]", "<a href='such98://img/$2'>[点击查看图片]</a>"));
        bbMap.add(new Pair<>("(?i)\\[upload=?(bmp|png|gif|jpg|jpeg|jpe|tif|tiff)?(,\\d)?\\](.+?)\\[/upload\\]", "<a href='such98://img/$3'>[点击查看图片]</a>"));

        bbMap.add(new Pair<>("\\[em\\d+\\]", "[喵]"));
    }

    private static final String bb2html(String text) {
        String html = text;

        for (Pair<String, String> entry : bbMap) {
            html = html.replaceAll(entry.first, entry.second);
        }

        return html;
    }
}
