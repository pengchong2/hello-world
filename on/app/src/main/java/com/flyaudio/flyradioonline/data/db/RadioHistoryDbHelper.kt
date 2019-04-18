package com.flyaudio.flyradioonline.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.flyaudio.flyradioonline.MyApplication
import org.jetbrains.anko.db.*

class RadioHistoryDbHelper private constructor(ctx: Context = MyApplication.instance) : ManagedSQLiteOpenHelper(ctx,
        DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.createTable(RadioHistoryTable.NAME, true,
                RadioTable.ID to INTEGER + PRIMARY_KEY,
                RadioTable.DATA_ID to INTEGER,
                RadioTable.RADIO_NAME to TEXT,
                RadioTable.IMG_URL to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(RadioTable.NAME, true)
        onCreate(db)
    }

    companion object {
        const val DB_NAME = "radioHistory.db"
        const val DB_VERSION = 1
        val instance by lazy { RadioHistoryDbHelper() }
    }

}