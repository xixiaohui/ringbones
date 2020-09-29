package com.xxh.ringbones.utils;

import android.content.Context;
import android.content.ContentResolver;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Vector;

// package private
public class Utils {
    private static final String TAG = "Utils";

    /**
     * Sorts distinct (non-intersecting) range array in ascending order.
     *
     * @throws java.lang.IllegalArgumentException if ranges are not distinct
     */
    public static <T extends Comparable<? super T>> void sortDistinctRanges(Range<T>[] ranges) {
        Arrays.sort(ranges, new Comparator<Range<T>>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public int compare(Range<T> lhs, Range<T> rhs) {
                if (lhs.getUpper().compareTo(rhs.getLower()) < 0) {
                    return -1;
                } else if (lhs.getLower().compareTo(rhs.getUpper()) > 0) {
                    return 1;
                }
                throw new IllegalArgumentException(
                        "sample rate ranges must be distinct (" + lhs + " and " + rhs + ")");
            }
        });
    }

    /**
     * Returns the intersection of two sets of non-intersecting ranges
     *
     * @param one     a sorted set of non-intersecting ranges in ascending order
     * @param another another sorted set of non-intersecting ranges in ascending order
     * @return the intersection of the two sets, sorted in ascending order
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static <T extends Comparable<? super T>>
    Range<T>[] intersectSortedDistinctRanges(Range<T>[] one, Range<T>[] another) {
        int ix = 0;
        Vector<Range<T>> result = new Vector<Range<T>>();
        for (Range<T> range : another) {
            while (ix < one.length &&
                    one[ix].getUpper().compareTo(range.getLower()) < 0) {
                ++ix;
            }
            while (ix < one.length &&
                    one[ix].getUpper().compareTo(range.getUpper()) < 0) {
                result.add(range.intersect(one[ix]));
                ++ix;
            }
            if (ix == one.length) {
                break;
            }
            if (one[ix].getLower().compareTo(range.getUpper()) <= 0) {
                result.add(range.intersect(one[ix]));
            }
        }
        return result.toArray(new Range[result.size()]);
    }

    /**
     * Returns the index of the range that contains a value in a sorted array of distinct ranges.
     *
     * @param ranges a sorted array of non-intersecting ranges in ascending order
     * @param value  the value to search for
     * @return if the value is in one of the ranges, it returns the index of that range.  Otherwise,
     * the return value is {@code (-1-index)} for the {@code index} of the range that is
     * immediately following {@code value}.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static <T extends Comparable<? super T>>
    int binarySearchDistinctRanges(Range<T>[] ranges, T value) {
        return Arrays.binarySearch(ranges, Range.create(value, value),
                new Comparator<Range<T>>() {
                    @Override
                    public int compare(Range<T> lhs, Range<T> rhs) {
                        if (lhs.getUpper().compareTo(rhs.getLower()) < 0) {
                            return -1;
                        } else if (lhs.getLower().compareTo(rhs.getUpper()) > 0) {
                            return 1;
                        }
                        return 0;
                    }
                });
    }

    /**
     * Returns greatest common divisor
     */
    static int gcd(int a, int b) {
        if (a == 0 && b == 0) {
            return 1;
        }
        if (b < 0) {
            b = -b;
        }
        if (a < 0) {
            a = -a;
        }
        while (a != 0) {
            int c = b % a;
            b = a;
            a = c;
        }
        return b;
    }

    /**
     * Returns the equivalent factored range {@code newrange}, where for every
     * {@code e}: {@code newrange.contains(e)} implies that {@code range.contains(e * factor)},
     * and {@code !newrange.contains(e)} implies that {@code !range.contains(e * factor)}.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static Range<Integer> factorRange(Range<Integer> range, int factor) {
        if (factor == 1) {
            return range;
        }
        return Range.create(divUp(range.getLower(), factor), range.getUpper() / factor);
    }

    /**
     * Returns the equivalent factored range {@code newrange}, where for every
     * {@code e}: {@code newrange.contains(e)} implies that {@code range.contains(e * factor)},
     * and {@code !newrange.contains(e)} implies that {@code !range.contains(e * factor)}.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static Range<Long> factorRange(Range<Long> range, long factor) {
        if (factor == 1) {
            return range;
        }
        return Range.create(divUp(range.getLower(), factor), range.getUpper() / factor);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private static Rational scaleRatio(Rational ratio, int num, int den) {
        int common = gcd(num, den);
        num /= common;
        den /= common;
        return new Rational(
                (int) (ratio.getNumerator() * (double) num),     // saturate to int
                (int) (ratio.getDenominator() * (double) den));  // saturate to int
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static Range<Rational> scaleRange(Range<Rational> range, int num, int den) {
        if (num == den) {
            return range;
        }
        return Range.create(
                scaleRatio(range.getLower(), num, den),
                scaleRatio(range.getUpper(), num, den));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static Range<Integer> alignRange(Range<Integer> range, int align) {
        return range.intersect(
                divUp(range.getLower(), align) * align,
                (range.getUpper() / align) * align);
    }

    static int divUp(int num, int den) {
        return (num + den - 1) / den;
    }

    static long divUp(long num, long den) {
        return (num + den - 1) / den;
    }

    /**
     * Returns least common multiple
     */
    private static long lcm(int a, int b) {
        if (a == 0 || b == 0) {
            throw new IllegalArgumentException("lce is not defined for zero arguments");
        }
        return (long) a * b / gcd(a, b);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static Range<Integer> intRangeFor(double v) {
        return Range.create((int) v, (int) Math.ceil(v));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static Range<Long> longRangeFor(double v) {
        return Range.create((long) v, (long) Math.ceil(v));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static Size parseSize(Object o, Size fallback) {
        try {
            return Size.parseSize((String) o);
        } catch (ClassCastException e) {
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
            return fallback;
        }
        Log.w(TAG, "could not parse size '" + o + "'");
        return fallback;
    }

    static int parseIntSafely(Object o, int fallback) {
        if (o == null) {
            return fallback;
        }
        try {
            String s = (String) o;
            return Integer.parseInt(s);
        } catch (ClassCastException e) {
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
            return fallback;
        }
        Log.w(TAG, "could not parse integer '" + o + "'");
        return fallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static Range<Integer> parseIntRange(Object o, Range<Integer> fallback) {
        try {
            String s = (String) o;
            int ix = s.indexOf('-');
            if (ix >= 0) {
                return Range.create(
                        Integer.parseInt(s.substring(0, ix), 10),
                        Integer.parseInt(s.substring(ix + 1), 10));
            }
            int value = Integer.parseInt(s);
            return Range.create(value, value);
        } catch (ClassCastException e) {
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
            return fallback;
        } catch (IllegalArgumentException e) {
        }
        Log.w(TAG, "could not parse integer range '" + o + "'");
        return fallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static Range<Long> parseLongRange(Object o, Range<Long> fallback) {
        try {
            String s = (String) o;
            int ix = s.indexOf('-');
            if (ix >= 0) {
                return Range.create(
                        Long.parseLong(s.substring(0, ix), 10),
                        Long.parseLong(s.substring(ix + 1), 10));
            }
            long value = Long.parseLong(s);
            return Range.create(value, value);
        } catch (ClassCastException e) {
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
            return fallback;
        } catch (IllegalArgumentException e) {
        }
        Log.w(TAG, "could not parse long range '" + o + "'");
        return fallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static Range<Rational> parseRationalRange(Object o, Range<Rational> fallback) {
        try {
            String s = (String) o;
            int ix = s.indexOf('-');
            if (ix >= 0) {
                return Range.create(
                        Rational.parseRational(s.substring(0, ix)),
                        Rational.parseRational(s.substring(ix + 1)));
            }
            Rational value = Rational.parseRational(s);
            return Range.create(value, value);
        } catch (ClassCastException e) {
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
            return fallback;
        } catch (IllegalArgumentException e) {
        }
        Log.w(TAG, "could not parse rational range '" + o + "'");
        return fallback;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static Pair<Size, Size> parseSizeRange(Object o) {
        try {
            String s = (String) o;
            int ix = s.indexOf('-');
            if (ix >= 0) {
                return Pair.create(
                        Size.parseSize(s.substring(0, ix)),
                        Size.parseSize(s.substring(ix + 1)));
            }
            Size value = Size.parseSize(s);
            return Pair.create(value, value);
        } catch (ClassCastException e) {
        } catch (NumberFormatException e) {
        } catch (NullPointerException e) {
            return null;
        } catch (IllegalArgumentException e) {
        }
        Log.w(TAG, "could not parse size range '" + o + "'");
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static void trimFilename(StringBuilder res, int maxBytes) {
        byte[] raw = res.toString().getBytes(StandardCharsets.UTF_8);
        if (raw.length > maxBytes) {
            maxBytes -= 3;
            while (raw.length > maxBytes) {
                res.deleteCharAt(res.length() / 2);
                raw = res.toString().getBytes(StandardCharsets.UTF_8);
            }
            res.insert(res.length() / 2, "...");
        }
    }

    private static boolean isValidFatFilenameChar(char c) {
        if ((0x00 <= c && c <= 0x1f)) {
            return false;
        }
        switch (c) {
            case '"':
            case '*':
            case '/':
            case ':':
            case '<':
            case '>':
            case '?':
            case '\\':
            case '|':
            case 0x7F:
                return false;
            default:
                return true;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String buildValidFatFilename(String name) {
        if (TextUtils.isEmpty(name) || ".".equals(name) || "..".equals(name)) {
            return "(invalid)";
        }
        final StringBuilder res = new StringBuilder(name.length());
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);
            if (isValidFatFilenameChar(c)) {
                res.append(c);
            } else {
                res.append('_');
            }
        }
        // Even though vfat allows 255 UCS-2 chars, we might eventually write to
        // ext4 through a FUSE layer, so use that limit.
        trimFilename(res, 255);
        return res.toString();
    }


    private static File buildFile(File parent, String name, String ext) {
        if (TextUtils.isEmpty(ext)) {
            return new File(parent, name);
        } else {
            return new File(parent, name + "." + ext);
        }
    }

    public static final String MIME_TYPE_DEFAULT = "application/octet-stream";

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String[] splitFileName(String mimeType, String displayName) {
        String name;
        String ext;

        if (DocumentsContract.Document.MIME_TYPE_DIR.equals(mimeType)) {
            name = displayName;
            ext = null;
        } else {
            String mimeTypeFromExt;

            // Extract requested extension from display name
            final int lastDot = displayName.lastIndexOf('.');
            if (lastDot >= 0) {
                name = displayName.substring(0, lastDot);
                ext = displayName.substring(lastDot + 1);
                mimeTypeFromExt = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
                        ext.toLowerCase());
            } else {
                name = displayName;
                ext = null;
                mimeTypeFromExt = null;
            }

            if (mimeTypeFromExt == null) {
                mimeTypeFromExt = MIME_TYPE_DEFAULT;
            }

            final String extFromMimeType;
            if (MIME_TYPE_DEFAULT.equals(mimeType)) {
                extFromMimeType = null;
            } else {
                extFromMimeType = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
            }

            if (Objects.equals(mimeType, mimeTypeFromExt) || Objects.equals(ext, extFromMimeType)) {
                // Extension maps back to requested MIME type; allow it
            } else {
                // No match; insist that create file matches requested MIME
                name = displayName;
                ext = extFromMimeType;
            }
        }

        if (ext == null) {
            ext = "";
        }

        return new String[]{name, ext};
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static File buildUniqueFile(File parent, String mimeType, String displayName)
            throws FileNotFoundException {
        final String[] parts = splitFileName(mimeType, displayName);
        return buildUniqueFileWithExtension(parent, parts[0], parts[1]);
    }

    private static File buildUniqueFileWithExtension(File parent, String name, String ext)
            throws FileNotFoundException {
        File file = buildFile(parent, name, ext);

        // If conflicting file, try adding counter suffix
        int n = 0;
        while (file.exists()) {
            if (n++ >= 32) {
                throw new FileNotFoundException("Failed to create unique file");
            }
            file = buildFile(parent, name + " (" + n + ")", ext);
        }

        return file;
    }

    /**
     * Creates a unique file in the specified external storage with the desired name. If the name is
     * taken, the new file's name will have '(%d)' to avoid overwriting files.
     *
     * @param context      {@link Context} to query the file name from.
     * @param subdirectory One of the directories specified in {@link android.os.Environment}
     * @param fileName     desired name for the file.
     * @param mimeType     MIME type of the file to create.
     * @return the File object in the storage, or null if an error occurs.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static File getUniqueExternalFile(Context context, String subdirectory, String fileName,
                                             String mimeType) {
        File externalStorage = Environment.getExternalStoragePublicDirectory(subdirectory);
        // Make sure the storage subdirectory exists
        externalStorage.mkdirs();

        File outFile = null;
        try {
            // Ensure the file has a unique name, as to not override any existing file
            outFile = buildUniqueFile(externalStorage, mimeType, fileName);
        } catch (FileNotFoundException e) {
            // This might also be reached if the number of repeated files gets too high
            Log.e(TAG, "Unable to get a unique file name: " + e);
            return null;
        }
        return outFile;
    }

    /**
     * Returns a file's display name from its {android.content.ContentResolver.SCHEME_FILE}
     * or {android.content.ContentResolver.SCHEME_CONTENT} Uri. The display name of a file
     * includes its extension.
     *
     * @param context Context trying to resolve the file's display name.
     * @param uri     Uri of the file.
     * @return the file's display name, or the uri's string if something fails or the uri isn't in
     * the schemes specified above.
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static String getFileDisplayNameFromUri(Context context, Uri uri) {
        String scheme = uri.getScheme();

        if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            return uri.getLastPathSegment();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            // We need to query the ContentResolver to get the actual file name as the Uri masks it.
            // This means we want the name used for display purposes only.
            String[] proj = {
                    OpenableColumns.DISPLAY_NAME
            };
            try (Cursor cursor = context.getContentResolver().query(uri, proj, null, null, null)) {
                if (cursor != null && cursor.getCount() != 0) {
                    cursor.moveToFirst();
                    return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }

        // This will only happen if the Uri isn't either SCHEME_CONTENT or SCHEME_FILE, so we assume
        // it already represents the file's name.
        return uri.toString();
    }

    //https://www.tonesmp3.com/ringtones/bulati-hai-magar-jaane-ka-nahin.mp3
    public static String getFileNameFromUrl(String url) {

//        var filename = url.substring(url.lastIndexOf('/')+1);

        String filename = url.substring(url.lastIndexOf("/") + 1);
        return filename;
    }


}