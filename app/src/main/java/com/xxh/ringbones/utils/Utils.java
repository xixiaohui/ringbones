package com.xxh.ringbones.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.util.Range;
import android.util.Rational;
import android.util.Size;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Vector;

// package private
public class Utils {


    //https://www.tonesmp3.com/ringtones/bulati-hai-magar-jaane-ka-nahin.mp3
    public static String getFileNameFromUrl(String url) {
//        var filename = url.substring(url.lastIndexOf('/')+1);

        String filename = url.substring(url.lastIndexOf("/") + 1);
        return filename;
    }





}