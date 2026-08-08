/*
 * DownloadInstallerProvider.java
 * 下载安装器提供者 - 匹配原版 com.clement.livetools.update.DownloadInstallerProvider
 * SOLID: Single Responsibility
 * KISS: 保持简单
 */

package com.clement.livetools.update;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import androidx.annotation.Nullable;

public class DownloadInstallerProvider extends ContentProvider {

    private static final String AUTHORITY = "com.chang.lubojl.fileProvider";
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return "application/vnd.android.package-archive";
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        return 0;
    }
}
