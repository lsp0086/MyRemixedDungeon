@file:JvmName("ExTool")
package com.nyrds.pixeldungeon.kotlin.tools

import com.nyrds.pixeldungeon.ml.R
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.text.TextPaint
import android.text.style.AbsoluteSizeSpan
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import org.json.JSONArray
import org.json.JSONObject

public class CustomAbsoluteSizeSpan(context: Context, sizeSp: Int) : AbsoluteSizeSpan(sizeSp, true) {
    private var context: Context? = null
    private val mSizeSp: Int

    init {
        this.context = context
        this.mSizeSp = sizeSp
    }

    override fun updateMeasureState(p: TextPaint) {

        if (context != null) {
            p.textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                mSizeSp.toFloat(), context!!.resources.displayMetrics
            )
        }
        super.updateMeasureState(p)
    }
    override fun updateDrawState(ds: TextPaint) {
        if (context != null) {
            ds.textSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,
                mSizeSp.toFloat(), context!!.resources.displayMetrics
            )
        }
        super.updateDrawState(ds)
    }
}
fun <T : Comparable<T>> max(a: T, b: T): T {
    return if (a > b) a else b
}

fun <T : Comparable<T>> min(a: T, b: T): T {
    return if (a < b) a else b
}


public class Range{
    private var _start:Int = 0
    val length:Int
        get() {
            return end - start
        }
    var start:Int
        set(value) {
            var final = value
            if (final <= 0){
                final = 0
            }
            if (final >= end && end != 0){
                final = end - 1
            }
            _start = final
        }
        get() {
            return  _start
        }
    private var _end:Int = 0
    var end:Int
        set(value) {
            var final = value
            if (value <= 0){
                final = 0
                start  = 0
            }
            if (final >= end && end != 0){
                final = end - 1
            }
            _end = final
        }
        get() {
            return  _end
        }
}

fun safeJsonObject(res:String? = null):JSONObject{
    if (res != null){
        doTry {
            return JSONObject(res)
        }
    }
    return JSONObject("{}")
}

fun String.rangeOf(str:String?):Range{
    str?.let {
        if (it.isEmpty()){
            return Range()
        }else{
            val range = Range()
            val startIndex = this.indexOf(it)
            if (startIndex != -1) {
                range.start = startIndex
                range.end = startIndex + it.length
                return  range
            }else{
                return Range()
            }
        }

    }?: return  Range()
}
var View.hidden: Boolean
    get() {
        return this.visibility == View.GONE
    }
    set(newValue) {
        if (newValue){
            this.visibility = View.GONE
        }else{
            this.visibility = View.VISIBLE
        }
    }


var nil: Nothing? = null

inline fun JSONArray.forEach(action: (JSONObject) -> Unit) {
    for (i in 0 until this.length()) {
        action(this.safeObject(i))
    }
}
inline  fun doTry(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        e.message?.let { Log.d("Exception  happen   ${e::javaClass}", it) }
    }
}

fun getKTColor(color:Int,alpha:Float):Int{
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    return Color.argb((alpha * 255).toInt(), red, green, blue)
}
fun <T: Any>equal(first:Comparable<T>?,second:Comparable<T>?):Boolean{
    return first == second
}
fun color(r:Int,g:Int,b:Int,alpha: Float):Int{
    return  Color.argb((alpha * 255).toInt(),r,g,b)
}
fun TextView.getTextWidth(activity:Activity?):Int{
    var width = 0
    activity?.let {
        val paint = Paint()
        paint.textSize = this.textSize
        width = paint.measureText((this.text ?: "").toString()).toInt()
    }
    return width
}
val clearColor:Int = color(0,0,0,0f)
fun JSONObject.stringFor(key:String):String{
    if (!this.isNull(key)){
        return  "${this[key]}"
    }
    return  ""
}
fun  JSONObject.safeObject(key:String?):JSONObject{
    if (this.has(key) && key != null){
        if (this.get(key) is  JSONObject){
            return  this.getJSONObject(key)
        }
    }
    return safeJsonObject()
}
fun JSONArray.safeObject(index:Int):JSONObject{
    if (index in 0 until this.length()){
        if (this.get(index) is JSONObject){
            return this.getJSONObject(index)
        }
    }
    return safeJsonObject()
}
val String.extractNumber:String
    get() {
        val regex = Regex("^\\s*([-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?)")
        val matchResult = regex.find(this)
        return matchResult?.value ?: "0"
    }
fun String?.toJsonObject(): JSONObject {
    return safeJsonObject(this)
}
fun ByteArray?.toUtf8Safe():String{
    this?.let {
        return String(it, Charsets.UTF_8)
    }
    return ""
}
fun toInt(obj:Any?):Int{
    var value = 0
    obj?.let {
        value = "$it".extractNumber.toInt()
    }
    return value
}
fun JSONObject.intFor(key:String):Int{
    var value = 0;
    if (!this.isNull(key)){
        value =  this.stringFor(key).extractNumber.toInt()
    }
    return value
}
fun <T> isEmpty(input: T?): Boolean {
    input?.let {
        val isEmptyMethod by lazy {
            it::class.members.find { member ->
                member.name.contains("isEmpty")
            }
        }
        val isCountMode by lazy {
            it::class.members.find { member ->
                member.name.contains("count") || member.name.contains("size") || member.name.contains("length")
            }
        }
        if (isEmptyMethod != null){
            return (isEmptyMethod?.call(input) as? Boolean) != false
        }else if (isCountMode != null){
            val intResult = (isCountMode?.call(input) as? Int)
            return intResult == 0 || intResult == null
        }
    }
    return  true
}

fun <T> isNotEmpty(input: T?): Boolean{
    return !isEmpty(input)
}

fun Number.decimalCount():Int{
    var count = 0
    val split =  "$this".split(".")
    if (split.size == 2){
        count = split[1].length
    }
    return count
}

fun repeatString(str:String?,count:Int):String{
    var result = ""
    str?.let {
        var i = 0
        while (i < count){
            result += it
            i += 1
        }
    }
    return result
}

fun buildString(vararg params: Any?):String{
    var str = ""
    params.forEach {
        str += "$it"
    }
    return  str
}
fun JSONObject.longFor(key:String):Long{
    var value:Long = 0;
    if (!this.isNull(key)){
        value =  this.stringFor(key).extractNumber.toLong()
    }
    return value
}
fun JSONObject.floatFor(key:String):Float{
    var value:Float = 0f;
    if (!this.isNull(key)){
        value =  this.stringFor(key).extractNumber.toFloat()
    }
    return value
}
fun JSONObject.doubleFor(key:String):Double{
    var value:Double = 0.0;
    if (!this.isNull(key)){
        value =  this.stringFor(key).extractNumber.toDouble()
    }
    return value
}

fun JSONObject.boolFor(key:String):Boolean{
    var value = false
    if (!this.isNull(key)){
        if (this.get(key) is String){
            val strValue = this.getString(key)
            value = strValue == "true" || strValue == "1"
        }
        if (this.get(key) is Int){
            value = this.getInt(key) == 1
        }
    }
    return  value
}
fun JSONObject.arrayFor(key:String):JSONArray?{
    var value:JSONArray? = nil
    if (!this.isNull(key)){
        if (this.get(key) is JSONArray){
            value = this.getJSONArray(key)
        }
    }
    return value
}
operator fun JSONArray.plus(other: JSONArray): JSONArray {
    val result = JSONArray()
    for (i in 0 until this.length()) {
        result.put(this.get(i))
    }
    for (i in 0 until other.length()) {
        result.put(other.get(i))
    }
    return result
}
