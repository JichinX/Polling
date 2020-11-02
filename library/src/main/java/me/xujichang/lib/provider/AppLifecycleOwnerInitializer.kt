package me.xujichang.lib.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri

/**
 *project：Polling
 *full_path：me.xujichang.lib.provider.ProcessLifecycleOwnerInitializer
 *des:
 *<br>
 *@author xujichang
 *created by 11/2/20 14:19
 */
class AppLifecycleOwnerInitializer : ContentProvider() {
    override fun onCreate(): Boolean {
        context?.let {
            AppLifecycleDispatcher.init(it)
            AppLifecycleOwner.init(it)
        }
        return true
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun getType(uri: Uri): String? = null

    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0
}