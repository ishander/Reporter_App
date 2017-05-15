package com.rajasthnapatrika_prod.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.rajasthnapatrika_prod.R;
import com.rajasthnapatrika_prod.activities.AppController_Patrika;

public class Reports_Web_view_Fragment extends Fragment {

    View rootView;
    WebView webview;
    WebSettings web_Settings;
    public Reports_Web_view_Fragment()
    {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.reporter_web_view_fragment, container, false);
        webview = (WebView) rootView.findViewById(R.id.webview);

        Log.e("webview","url  "+AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("webview_url",""));
        webview.loadUrl(AppController_Patrika.getSharedPreferences_FTP_Credentials().getString("webview_url",""));
        return rootView;
    }
}
