package com.zmci.safetymonitoringapp.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.widget.Toast
import java.util.*
import com.zmci.safetymonitoringapp.home.detection.model.CameraData
import com.zmci.safetymonitoringapp.home.detection.model.Detection
import kotlin.collections.ArrayList

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // create table sql query
    private val CREATE_CAMERA_TABLE = ("CREATE TABLE " + TABLE_CAMERA + "("
            + COLUMN_CAMERA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CAMERA_NAME + " TEXT,"
            + COLUMN_SERVER_TOPIC + " TEXT,"
            + COLUMN_CLIENT_ID + " TEXT"+ ")")
    private val CREATE_DETECTION_TABLE = ("CREATE TABLE " + TABLE_DETECTION + "("
            + COLUMN_DETECTION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_DETECTION_IMAGE + " TEXT,"
            + COLUMN_DETECTION_CAMERA_NAME + " TEXT,"
            + COLUMN_DETECTION_TIMESTAMP + " TEXT,"
            + COLUMN_DETECTION_VIOLATORS + " TEXT,"
            + COLUMN_TOTAL_VIOLATIONS + " TEXT,"
            + COLUMN_TOTAL_VIOLATORS + " TEXT" + ")")

    // drop table sql query
    private val DROP_CAMERA_TABLE = "DROP TABLE IF EXISTS $TABLE_CAMERA"
    private val DROP_DETECTION_TABLE = "DROP TABLE IF EXISTS $TABLE_DETECTION"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_CAMERA_TABLE)
        db.execSQL(CREATE_DETECTION_TABLE)
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

        //Drop User Table if exist
        db.execSQL(DROP_CAMERA_TABLE)
        db.execSQL(DROP_DETECTION_TABLE)

        // Create tables again
        onCreate(db)

    }

    // Camera database
    fun getAllCamera(context: Context): ArrayList<CameraData> {

        val qry = "SELECT * FROM $TABLE_CAMERA"
        val db = this.readableDatabase
        val cursor = db.rawQuery(qry, null)
        val cameraList = ArrayList<CameraData>()

        if (cursor.count == 0)
            Log.i("Devices", "No Camera Found") else {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val camera = CameraData()
                camera.id = cursor.getInt(cursor.getColumnIndex(COLUMN_CAMERA_ID))
                camera.cameraName = cursor.getString(cursor.getColumnIndex(COLUMN_CAMERA_NAME))
                camera.MQTT_TOPIC = cursor.getString(cursor.getColumnIndex(COLUMN_SERVER_TOPIC))
                camera.MQTT_CLIENT_ID = cursor.getString(cursor.getColumnIndex(COLUMN_CLIENT_ID))
                cameraList.add(camera)
                cursor.moveToNext()
            }
            Log.i("Devices","${cursor.count.toString()} Devices Found")
        }
        cursor.close()
        db.close()
        return cameraList
    }

    /**
     * This method is to create camera record
     *
     * @param camera
     */
    fun addCamera(context: Context, camera: CameraData) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(/* key = */ COLUMN_CAMERA_NAME, /* value = */ camera.cameraName)
        values.put(/* key = */ COLUMN_SERVER_TOPIC, /* value = */ camera.MQTT_TOPIC)
        values.put(/* key = */ COLUMN_CLIENT_ID, /* value = */ camera.MQTT_CLIENT_ID)

        try {
            db.insert(TABLE_CAMERA, null, values)
            Toast.makeText(context, "Device Added", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
        }
        db.close()
    }

    /**
     * This method to update camera record
     *
     * @param camera
     */
    fun updateCamera(id : String, cameraName: String, serverTopic:String) : Boolean {
        val db = this.writableDatabase
        val values = ContentValues()
        var result: Boolean
        values.put(COLUMN_CAMERA_NAME, cameraName)
        values.put(COLUMN_SERVER_TOPIC, serverTopic)
        try {
            db.update(TABLE_CAMERA, values, "$COLUMN_CAMERA_ID = ?", arrayOf(id))
            result = true
        } catch (e : Exception){
            Log.e(ContentValues.TAG, "Error Updating")
            result = false
        }
        return result
    }

    /**
     * This method is to delete camera record
     *
     * @param camera
     */
    fun deleteCamera(cameraID : Int): Boolean {

        val db = this.writableDatabase
        var result = false
        try {
            val cursor = db.delete(TABLE_CAMERA, "$COLUMN_CAMERA_ID = ?",
                arrayOf(cameraID.toString()))
            result = true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error Deleting")
        }
        db.close()
        return result
    }

    // Detection database
    fun getAllDetection(context: Context): ArrayList<Detection> {

        val qry = "SELECT * FROM $TABLE_DETECTION"
        val db = this.readableDatabase
        val cursor = db.rawQuery(qry, null)
        val detectionList = ArrayList<Detection>()

        if (cursor.count == 0)
            Log.i("Records", "No Records Found") else {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val detection = Detection()
                detection.id = cursor.getInt(cursor.getColumnIndex(COLUMN_DETECTION_ID))
                detection.image = cursor.getString(cursor.getColumnIndex(COLUMN_DETECTION_IMAGE))
                detection.cameraName = cursor.getString(cursor.getColumnIndex(COLUMN_DETECTION_CAMERA_NAME))
                detection.timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_DETECTION_TIMESTAMP))
                detection.violators = cursor.getString(cursor.getColumnIndex(COLUMN_DETECTION_VIOLATORS))
                detection.total_violations = cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL_VIOLATIONS))
                detection.total_violators = cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL_VIOLATORS))
                detectionList.add(detection)
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return detectionList
    }

    fun getDetectionOfCamera(context: Context, cameraName: String): ArrayList<Detection> {

        val qry = "SELECT * FROM $TABLE_DETECTION WHERE $COLUMN_DETECTION_CAMERA_NAME = \"$cameraName\""
        val db = this.readableDatabase
        val cursor = db.rawQuery(qry, null)
        val detectionList = ArrayList<Detection>()

        if (cursor.count == 0)
            Toast.makeText(context, "No Records Found", Toast.LENGTH_SHORT).show() else {
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val detection = Detection()
                detection.id = cursor.getInt(cursor.getColumnIndex(COLUMN_DETECTION_ID))
                detection.image = cursor.getString(cursor.getColumnIndex(COLUMN_DETECTION_IMAGE))
                detection.cameraName = cursor.getString(cursor.getColumnIndex(COLUMN_DETECTION_CAMERA_NAME))
                detection.timestamp = cursor.getString(cursor.getColumnIndex(COLUMN_DETECTION_TIMESTAMP))
                detection.violators = cursor.getString(cursor.getColumnIndex(COLUMN_DETECTION_VIOLATORS))
                detection.total_violations = cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL_VIOLATIONS))
                detection.total_violators = cursor.getString(cursor.getColumnIndex(COLUMN_TOTAL_VIOLATORS))
                detectionList.add(detection)
                cursor.moveToNext()
            }
        }
        cursor.close()
        db.close()
        return detectionList
    }


    /**
     * This method is to create camera record
     *
     * @param detection
     */
    fun addDetection(context: Context?, detection: Detection) {
        val db = this.writableDatabase

        val values = ContentValues()
        values.put(/* key = */ COLUMN_DETECTION_IMAGE, /* value = */ detection.image)
        values.put(/* key = */ COLUMN_DETECTION_CAMERA_NAME, /* value = */ detection.cameraName)
        values.put(/* key = */ COLUMN_DETECTION_TIMESTAMP, /* value = */ detection.timestamp)
        values.put(/* key = */ COLUMN_DETECTION_VIOLATORS, /* value = */ detection.violators)
        values.put(/* key = */ COLUMN_TOTAL_VIOLATIONS, /* value = */ detection.total_violations)
        values.put(/* key = */ COLUMN_TOTAL_VIOLATORS, /* value = */ detection.total_violators)

        try {
            db.insert(TABLE_DETECTION, null, values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        db.close()
    }

    /**
     * This method is to delete camera record
     *
     * @param detection
     */
    fun deleteDetection(detectionID : Int): Boolean {

        val db = this.writableDatabase
        var result = false
        try {
            val cursor = db.delete(
                TABLE_DETECTION, "$COLUMN_DETECTION_ID = ?",
                arrayOf(detectionID.toString()))
            result = true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error Deleting")
        }
        db.close()
        return result
    }

    /**
     * This method is to delete ALL camera records
     *
     * @param detection
     */
    fun deleteAllDetection(): Boolean {

        var result = false
        val db = this.writableDatabase
        try {
            db.delete(TABLE_DETECTION, null, null)
            result = true
        } catch (e: Exception) {
            Log.e(ContentValues.TAG, "Error Deleting")
        }
        db.close()
        return result
    }

    companion object {

        // Database Version
        private val DATABASE_VERSION = 2

        // Database Name
        private val DATABASE_NAME = "ZMCI_PPE_DB.db"

        // Camera table name
        private val TABLE_CAMERA = "camera"

        // Camera Table Columns names
        private val COLUMN_CAMERA_ID = "camera_id"
        private val COLUMN_CAMERA_NAME = "camera_name"
        private val COLUMN_SERVER_TOPIC = "server_topic"
        private val COLUMN_CLIENT_ID = "client_id"

        // Detection table name
        private val TABLE_DETECTION = "detection"

        // Detection Table Columns names
        private val COLUMN_DETECTION_ID = "detection_id"
        private val COLUMN_DETECTION_IMAGE = "detection_image"
        private val COLUMN_DETECTION_CAMERA_NAME = "detection_camera_name"
        private val COLUMN_DETECTION_TIMESTAMP = "detection_timestamp"
        private val COLUMN_DETECTION_VIOLATORS = "detection_violators"
        private val COLUMN_TOTAL_VIOLATIONS = "total_violations"
        private val COLUMN_TOTAL_VIOLATORS = "total_violators"
    }
}