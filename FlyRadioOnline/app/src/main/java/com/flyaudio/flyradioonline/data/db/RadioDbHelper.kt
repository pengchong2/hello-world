package com.flyaudio.flyradioonline.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.flyaudio.flyradioonline.MyApplication
import org.jetbrains.anko.db.*

class RadioDbHelper private constructor(ctx: Context = MyApplication.instance) : ManagedSQLiteOpenHelper(ctx,
        DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        createRadioTable(db, RadioTable.NAME)
        createRadioTable(db, RadioHistoryTable.NAME)
    }

    private fun createRadioTable(db: SQLiteDatabase, tableName: String) {
        db.createTable(tableName, true,
                RadioTable.ID to INTEGER + PRIMARY_KEY,
                RadioTable.DATA_ID to INTEGER,
                RadioTable.RADIO_NAME to TEXT,
                RadioTable.IMG_URL to TEXT,
                RadioTable.RADIO_TYPE to TEXT)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.dropTable(RadioTable.NAME, true)
        db.dropTable(RadioHistoryTable.NAME, true)
        onCreate(db)
    }

    companion object {
        const val DB_NAME = "radio.db"
        const val DB_VERSION = 1
        val instance by lazy { RadioDbHelper() }
    }

}