package com.flyaudio.flyradioonline.data.db

import android.database.Cursor
import com.flyaudio.flyradioonline.data.db.RadioTable.DATA_ID
import com.flyaudio.flyradioonline.data.db.RadioTable.IMG_URL
import com.flyaudio.flyradioonline.data.db.RadioTable.RADIO_NAME
import com.flyaudio.flyradioonline.data.db.RadioTable.RADIO_TYPE
import com.ximalaya.ting.android.opensdk.model.live.radio.Radio
import org.jetbrains.anko.db.*

class RadioDb private constructor(private val radioDbHelper: RadioDbHelper = RadioDbHelper.instance,
                                  private val dataMapper: DbDataMapper = DbDataMapper()) {

    companion object {
        val instance by lazy { RadioDb() }
    }

    fun saveRadio(radioList: List<Radio>, radioType: String) = radioDbHelper.use {
        if (getRadioFromDb(radioType).isNotEmpty()) {
            delete(RadioHistoryTable.NAME, "${RadioTable.RADIO_TYPE} = $radioType")
//            radioList.forEach {
//                update(RadioTable.NAME, DATA_ID to it.dataId,
//                        RADIO_NAME to it.radioName, IMG_URL to it.coverUrlLarge,
//                        RADIO_TYPE to radioType).exec()
//            }
        }
        radioList.forEach {
            insert(RadioTable.NAME, DATA_ID to it.dataId,
                    RADIO_NAME to it.radioName, IMG_URL to it.coverUrlLarge,
                    RADIO_TYPE to radioType)
        }
    }

    fun saveRadioHistory(radio: Radio) = radioDbHelper.use {
        with(radio) {
            if (!checkRadioHistoryIsExistFromDb(radio)) {
                insert(RadioHistoryTable.NAME, DATA_ID to dataId,
                        RADIO_NAME to radioName, IMG_URL to coverUrlLarge)
            }
        }
    }

    fun getRadioFromDb(radioType: String) = radioDbHelper.use {
        parseList(select(RadioTable.NAME).whereSimple("$RADIO_TYPE = ?", radioType))
    }

    private fun checkRadioHistoryIsExistFromDb(radio: Radio) = radioDbHelper.use {
        parseList(select(RadioHistoryTable.NAME).whereSimple("${DATA_ID} = ?", radio.dataId.toString())).isNotEmpty()
    }

    fun getRadioHistoryListFromDb() = radioDbHelper.use {
        select(RadioHistoryTable.NAME).exec {
            val list = mutableListOf<Radio>()
            moveToLast()
            while (!isBeforeFirst) {
                list.add(getParser().parseRow(readColumnsMap(this)))
                moveToPrevious()
            }
            return@exec list
        }
    }

    private fun getParser() = object : MapRowParser<Radio> {
        override fun parseRow(columns: Map<String, Any?>): Radio {
            val radio = Radio()
            radio.radioName = columns[RADIO_NAME].toString()
            radio.dataId = columns[DATA_ID] as Long
            radio.coverUrlLarge = columns[IMG_URL] as String
            return radio
        }
    }

    private fun readColumnsMap(cursor: Cursor): Map<String, Any?> {
        val count = cursor.columnCount
        val map = hashMapOf<String, Any?>()
        for (i in 0..(count - 1)) {
            map.put(cursor.getColumnName(i), cursor.getColumnValue(i))
        }
        return map
    }

    private fun Cursor.getColumnValue(index: Int): Any? {
        if (isNull(index)) return null

        return when (getType(index)) {
            Cursor.FIELD_TYPE_INTEGER -> getLong(index)
            Cursor.FIELD_TYPE_FLOAT -> getDouble(index)
            Cursor.FIELD_TYPE_STRING -> getString(index)
            Cursor.FIELD_TYPE_BLOB -> getBlob(index)
            else -> null
        }
    }

    private fun parseList(selectQueryBuilder: SelectQueryBuilder): List<Radio> {
        return selectQueryBuilder.parseList(getParser())
    }

    fun deleteHistory(radioList: List<Radio>) = radioDbHelper.use {
        radioList.forEach {
            delete(RadioHistoryTable.NAME, "${RadioTable.DATA_ID} = ${it.dataId}")
        }
    }
}