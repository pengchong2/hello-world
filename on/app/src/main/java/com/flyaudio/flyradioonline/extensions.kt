package com.flyaudio.flyradioonline

import android.app.Activity
import android.app.Service
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.TextView
import org.jetbrains.anko.ctx
import org.jetbrains.anko.toast

fun Activity.inflate(layoutId: Int): View = layoutInflater.inflate(layoutId, null)

fun Activity.startActivity(cls: Class<*>) = startActivity(Intent(ctx, cls))
fun Service.startActivity(cls: Class<*>) = startActivity(Intent(ctx, cls))

fun Activity.startActivityForResult(cls: Class<*>, requestCode: Int) = startActivityForResult(Intent(ctx, cls), requestCode)

fun Activity.setResultAndFinish(resultCode: Int) {
    setResult(resultCode)
    finish()
}

fun setOnclickListener(listener: View.OnClickListener, vararg views: View) = views.forEach { it.setOnClickListener(listener) }

fun setViewGone(vararg views: View) = views.forEach { it.visibility = View.GONE }

fun setViewVisible(vararg views: View) = views.forEach { it.visibility = View.VISIBLE }

fun lowIntToByte(i:Int):Byte=(i and 0xFF).toByte()

