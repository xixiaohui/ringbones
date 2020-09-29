package com.xxh.ringbones.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileUtils;
import android.os.RemoteException;
import android.provider.MediaStore;
import androidx.annotation.CheckResult;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;

/**
 * Created by Keval on 20-Feb-17.
 * This class contains utility classes for the ringtone.
 *
 * @author {@link 'https://github.com/kevalpatel2106'}
 */
public final class RingtoneUtils {

    private Uri fileUri;
    private int type;

    /**
     * Load the list of all the ringtone registered using {@link RingtoneManager}. It will add title
     * as the key and uri of the sound as value in given {@link LinkedHashMap}.
     *
     * @param context instance of the caller.
     * @return {@link LinkedHashMap} of the title-{@link Uri} pair of all the ringtone.
     */
    @NonNull
    @CheckResult
    public static LinkedHashMap<String, Uri> getRingTone(@NonNull final Context context) {
        return getTone(context, RingtoneManager.TYPE_RINGTONE);
    }

    /**
     * Load the list of all the notification tones registered using {@link RingtoneManager}. It will add title as the key and
     * uri of the sound as value in given {@link LinkedHashMap}.
     *
     * @param context instance of the caller.
     * @return {@link LinkedHashMap} of the title-{@link Uri} pair of all the notification tone.
     */
    @NonNull
    @CheckResult
    public static LinkedHashMap<String, Uri> getNotificationTones(@NonNull final Context context) {
        return getTone(context, RingtoneManager.TYPE_NOTIFICATION);
    }

    /**
     * Load the list of all the alarm tones registered using {@link RingtoneManager}. It will add
     * title as the key and uri of the sound as value in given {@link LinkedHashMap}.
     *
     * @param context instance of the caller.
     * @return {@link LinkedHashMap} of the title-{@link Uri} pair of all the alarm tone.
     */
    @NonNull
    @CheckResult
    public static LinkedHashMap<String, Uri> getAlarmTones(@NonNull final Context context) {
        return getTone(context, RingtoneManager.TYPE_ALARM);
    }

    /**
     * Get the tone from {@link RingtoneManager} for any given type. It will add title as the key and
     * uri of the sound as value in given {@link LinkedHashMap}.
     *
     * @param context instance of the caller
     * @param type    type of the ringtone from
     *                  {RingtonePickerDialog.Builder#TYPE_NOTIFICATION},
     *                {RingtonePickerDialog.Builder#TYPE_RINGTONE} or
     *                {RingtonePickerDialog.Builder#TYPE_ALARM}.
     * @return {@link LinkedHashMap} of the title-{@link Uri} pair of all the ringtone of given type.
     */
    @CheckResult
    @NonNull
    private static LinkedHashMap<String, Uri> getTone(@NonNull final Context context, final int type) {
        final LinkedHashMap<String, Uri> ringToneList = new LinkedHashMap<>();

        final RingtoneManager ringtoneManager = new RingtoneManager(context);
        ringtoneManager.setType(type);

        final Cursor ringsCursor = ringtoneManager.getCursor();
        while (ringsCursor.moveToNext()) {
            ringToneList.put(ringsCursor.getString(RingtoneManager.TITLE_COLUMN_INDEX),
                    Uri.parse(ringsCursor.getString(RingtoneManager.URI_COLUMN_INDEX) + "/"
                            + ringsCursor.getString(RingtoneManager.ID_COLUMN_INDEX)));
        }
        ringsCursor.close();
        return ringToneList;
    }

    /**
     * Get the list of the music (sound) files from the phone storage. It will add title as the key and
     * uri of the sound as value in given {@link LinkedHashMap}.
     *
     * @param context instance of the caller.
     * @return {@link LinkedHashMap} of the title-{@link Uri} pair of all the music tracks.
     * @throws IllegalStateException If storage read permission is not available.
     */
    @NonNull
    @CheckResult
    @SuppressLint("InlinedApi")
    @RequiresPermission(anyOf = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    static LinkedHashMap<String, Uri> getMusic(@NonNull final Context context) {
        final LinkedHashMap<String, Uri> ringToneList = new LinkedHashMap<>();

        //Check for the read permission
        if (!RingtoneUtils.checkForStorageReadPermission(context)) {
            throw new IllegalStateException("Storage permission is not available.");
        }

        //Prepare query
        final Cursor mediaCursor = context.getContentResolver()
                .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media._ID},
                        MediaStore.Audio.Media.IS_MUSIC + "!= 0",
                        null,
                        MediaStore.Audio.Media.TITLE + " ASC");

        if (mediaCursor != null) {
            while (mediaCursor.moveToNext()) {
                ringToneList.put(mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
                        Uri.parse(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI + "/"
                                + mediaCursor.getString(mediaCursor.getColumnIndex(MediaStore.Audio.Media._ID))));
            }
            mediaCursor.close();
        }

        return ringToneList;
    }

    /**
     * Get the system selected default ringtone.
     *
     * @return Uri of the selected ringtone. You may need android.permission.READ_EXTERNAL_STORAGE
     * permission to read the ringtone selected from the external storage.
     */
    @Nullable
    public static Uri getSystemRingtoneTone() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
    }

    /**
     * Get the system selected default alarm tone.
     *
     * @return Uri of the selected alarm tone. You may need android.permission.READ_EXTERNAL_STORAGE
     * permission to read the ringtone selected from the external storage.
     */
    @Nullable
    public static Uri getSystemAlarmTone() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
    }

    /**
     * Get the system selected default notification tone.
     *
     * @return Uri of the selected notification tone. You may need android.permission.READ_EXTERNAL_STORAGE
     * permission to read the ringtone selected from the external storage.
     */
    @Nullable
    public static Uri getSystemNotificationTone() {
        return RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
    }

    /**
     * Get the title of the ringtone from the uri of ringtone.
     *
     * @param context instance of the caller
     * @param uri     uri of the tone to search
     * @return title of the tone or return null if no tone found.
     */
    @Nullable
    public static String getRingtoneName(@NonNull final Context context,
                                         @NonNull final Uri uri) {
        final Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
        if (ringtone != null) {
            return ringtone.getTitle(context);
        } else {
            Cursor cur = context
                    .getContentResolver()
                    .query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            new String[]{MediaStore.Audio.Media.TITLE},
                            MediaStore.Audio.Media._ID + " =?",
                            new String[]{uri.getLastPathSegment()},
                            null);

            String title = null;
            if (cur != null) {
                title = cur.getString(cur.getColumnIndex(MediaStore.Audio.Media.TITLE));
                cur.close();
            }
            return title;
        }
    }

    /**
     * Check if the {@link Manifest.permission#WRITE_EXTERNAL_STORAGE} permission is granted?
     *
     * @return True if the read permission granted or else false.
     */
    static boolean checkForStorageReadPermission(@NonNull final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }


    public static final String getExternalDirectoryForType(final int type) {
        switch (type) {
            case RingtoneManager.TYPE_RINGTONE:
                return Environment.DIRECTORY_RINGTONES;
            case RingtoneManager.TYPE_NOTIFICATION:
                return Environment.DIRECTORY_NOTIFICATIONS;
            case RingtoneManager.TYPE_ALARM:
                return Environment.DIRECTORY_ALARMS;
            default:
                throw new IllegalArgumentException("Unsupported ringtone type: " + type);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Uri addCustomExternalRingtone(@NonNull final Context mContext, @NonNull final Uri fileUri, final int type)
            throws FileNotFoundException, IllegalArgumentException, IOException {
//        this.fileUri = fileUri;
//        this.type = type;
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            throw new IOException("External storage is not mounted. Unable to install ringtones.");
        }

        // Sanity-check: are we actually being asked to install an audio file?
        final String mimeType = mContext.getContentResolver().getType(fileUri);
        if(mimeType == null ||
                !(mimeType.startsWith("audio/") || mimeType.equals("application/ogg"))) {
            throw new IllegalArgumentException("Ringtone file must have MIME type \"audio/*\"."
                    + " Given file has MIME type \"" + mimeType + "\"");
        }

        // Choose a directory to save the ringtone. Only one type of installation at a time is
        // allowed. Throws IllegalArgumentException if anything else is given.
        final String subdirectory = getExternalDirectoryForType(type);

        // Find a filename. Throws FileNotFoundException if none can be found.
        final File outFile = Utils.getUniqueExternalFile(mContext, subdirectory,
                Utils.buildValidFatFilename(Utils.getFileDisplayNameFromUri(mContext, fileUri)),
                mimeType);

        // Copy contents to external ringtone storage. Throws IOException if the copy fails.
        try (final InputStream input = mContext.getContentResolver().openInputStream(fileUri);
             final OutputStream output = new FileOutputStream(outFile)) {
            FileUtils.copy(input, output);
        }

        // Tell MediaScanner about the new file. Wait for it to assign a {@link Uri}.
        return scanFile(mContext, outFile);
    }

    public static final String SCAN_FILE_CALL = "scan_file";

    public static final String EXTRA_ORIGINATED_FROM_SHELL =
            "android.intent.extra.originated_from_shell";

    public static final String AUTHORITY = "media";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Uri scanFile(Context context, File file) {
        return scan(context, SCAN_FILE_CALL, file, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static Uri scan(Context context, String method, File file,
                            boolean originatedFromShell) {
        final ContentResolver resolver = context.getContentResolver();
        try (ContentProviderClient client = resolver.acquireContentProviderClient(AUTHORITY)) {
            final Bundle in = new Bundle();
            in.putParcelable(Intent.EXTRA_STREAM, Uri.fromFile(file));
            in.putBoolean(EXTRA_ORIGINATED_FROM_SHELL, originatedFromShell);
            final Bundle out = client.call(method, null, in);
            return out.getParcelable(Intent.EXTRA_STREAM);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
