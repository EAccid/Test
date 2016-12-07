package com.eaccid.bookreader.file;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.Size;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import com.eaccid.bookreader.activity.pager.PagerActivity;
import com.eaccid.bookreader.R;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import rx.Observable;
import rx.Subscriber;


public class FileToPagesListReader extends ContextWrapper {

    private int pageLinesOnScreen;
    private String filePath;
    private TextPaint paint;
    private DisplayMetrics metrics;
    private ArrayList<String> pagesList = new ArrayList<>();
    int currentChar = 0;
    boolean isLastPageRead = false;

    public FileToPagesListReader(Context context, String filePath) {
        super(context);
        this.filePath = filePath;
        setScreenParameters();

        //TODO save and load current char
//        loadPages();
//        new PagesLoader().execute();
        loadRxPages();
    }

    public ArrayList<String> getPages() {
        return pagesList;
    }

    private void setScreenParameters() {

        float verticalMargin =
                getBaseContext().getResources().getDimension(R.dimen.activity_vertical_margin) * 2;
        PagerActivity pagerActivity = (PagerActivity) getBaseContext();
        LayoutInflater inflater = pagerActivity.getLayoutInflater();

        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.bookpage_item_fragment_0,
                (ViewGroup) pagerActivity.getWindow().getDecorView().findViewById(android.R.id.content), false);
        TextView textOnPage = (TextView) viewGroup.findViewById(R.id.text_on_page);

        paint = textOnPage.getPaint();
        Paint.FontMetrics paintFontMetrics = paint.getFontMetrics();
        float textSizeHeight = Math.abs(paintFontMetrics.top - paintFontMetrics.bottom);

        metrics = new DisplayMetrics();
        pagerActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        pageLinesOnScreen = (int) (Math.floor((metrics.heightPixels - verticalMargin) / textSizeHeight));

    }

    private void loadPages() {

        //TODO remove comment
//        while (!isLastPageRead) {
        int i = 0;
        while (!isLastPageRead && i < 10) {
            pagesList.add(getPage());
            i++;
        }
    }

    private String getPage() {
        //TODO read to slow
        StringBuilder sb = new StringBuilder();
        int lineCountOnScreen = 0;

        while (lineCountOnScreen < pageLinesOnScreen) {
            String lineFromFile = readLineFromFile();
            if (isLastPageRead) break;
            int numChars = 0;

            while ((lineCountOnScreen < pageLinesOnScreen) && (numChars < lineFromFile.length())) {
                numChars = numChars + paint.breakText(lineFromFile.substring(numChars), true, metrics.widthPixels, null);
                lineCountOnScreen++;
            }

            String displayedText = lineFromFile.substring(0, numChars);
            char nextChar = numChars < lineFromFile.length() ? lineFromFile.charAt(numChars) : ' ';
            if (!Character.isWhitespace(nextChar)) {
                displayedText = displayedText.substring(0, displayedText.lastIndexOf(" "));
            }

            currentChar = ++currentChar + displayedText.length();
            sb.append(displayedText.trim());
            if (!displayedText.trim().isEmpty()) {
                sb.append("\n\n");
                lineCountOnScreen++;
            }

        }
        return sb.toString();

    }

    private String readLineFromFile() {
        String line = null;
        BufferedReader bufferedReader = null;
        try {
//            bufferedReader = new BufferedReader(new FileReader(filePath));
            //"Windows-1251"
            //"ISO-8859-1"
            //"UTF-8"
            bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "Windows-1251"));
            bufferedReader.skip(currentChar);
            line = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception ignore) {
                }
            }
        }
        isLastPageRead = line == null;
        return line;
    }


    private class PagesLoader extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... test) {
            try {
                return getPage();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    public void loadRxPages() {

        Observable<String> observable = Observable.create(
                new Observable.OnSubscribe<String>() {
                    @Override
                    public void call(Subscriber<? super String> sub) {

                        String s = "";

                        try {
                            s = new PagesLoader().execute().get();
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }

                        sub.onNext(s);


                        sub.onCompleted();
                    }
                }
        ).limit(1);

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onNext(String s) {
                System.out.println(s);
            }

            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
            }
        };

        observable.subscribe(subscriber);

    }

}