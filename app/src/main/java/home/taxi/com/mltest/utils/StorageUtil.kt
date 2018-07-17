package home.taxi.com.mltest.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.graphics.BitmapFactory
import android.content.ContextWrapper





class StorageUtil{

    companion object {

        @SuppressLint("SimpleDateFormat")
        fun getFile(c : Context) : File{
            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
            val file = File(storageDir.absolutePath+imageFileName+timeStamp)
            file.createNewFile()
            return file
        }

        fun convertToBitmap(path : String): Bitmap{
            val options = BitmapFactory.Options()
            options.inPreferredConfig = Bitmap.Config.ARGB_8888
            return BitmapFactory.decodeFile(path, options)
        }

        fun getFile2(c: Context): File? {
            val cw = ContextWrapper(c)
            val directory = cw.getDir("media", Context.MODE_PRIVATE)
            val mediaStorageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CameraDemo")

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    return null
                }
            }

            val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val f =  File(mediaStorageDir.path + File.separator +
                    "IMG_" + timeStamp + ".png")
            f.createNewFile()
            return  f
        }

    }
}