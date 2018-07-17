package home.taxi.com.mltest

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.SparseIntArray
import android.view.Surface
import android.widget.TextView
import home.taxi.com.mltest.utils.StorageUtil
import android.os.Build
import android.os.Environment
import android.support.v4.content.FileProvider
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import home.taxi.com.mltest.utils.StorageUtil.Companion.getFile2


class MainActivity : AppCompatActivity() {

    var CAMERA_PERMISSION_RESULT = 2343
    var STORAGE_PERMISSION_RESULT = 2314
    var CAMERA_RESULT_ACTIVITY = 2313
    var isCameraPermisionGranted = false
    var isStoragePermissionGranted = false

    lateinit var fab : FloatingActionButton
    lateinit var text : TextView
    lateinit var photoUri :Uri
    lateinit var orientation : SparseIntArray

    fun Context.toast(message: CharSequence) =
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initArray()
        isCameraPermisionGranted = checkCameraPermission()
        isStoragePermissionGranted = checkStoragePermission()
        fab = findViewById(R.id.fab)
        text = findViewById(R.id.text)
        fab.setOnClickListener{fabClick()}
    }

    fun initArray(){
        orientation = SparseIntArray()
        orientation.append(Surface.ROTATION_0, 90)
        orientation.append(Surface.ROTATION_90, 0)
        orientation.append(Surface.ROTATION_180, 270)
        orientation.append(Surface.ROTATION_270, 180)
    }

    private fun checkCameraPermission():Boolean{
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_RESULT)
            return false
        }
        return true
    }

    private fun checkStoragePermission() : Boolean{
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_PERMISSION_RESULT)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == CAMERA_PERMISSION_RESULT){
            if(!grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isCameraPermisionGranted = true
            }
        }else if (requestCode == STORAGE_PERMISSION_RESULT){
            if(!grantResults.isEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                isStoragePermissionGranted = true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == CAMERA_RESULT_ACTIVITY && resultCode == Activity.RESULT_OK){
            onBitmapReceive()
        }
    }

    private fun fabClick(){
        if (isCameraPermisionGranted && isStoragePermissionGranted){
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if(intent.resolveActivity(packageManager) != null) {
                openCamera()
            }
        }else if(!isStoragePermissionGranted){
            isStoragePermissionGranted = checkStoragePermission()
        }else if(!isCameraPermisionGranted){
            isCameraPermisionGranted = checkCameraPermission()
        }
    }

    fun openCamera(){
        val imageIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        val image = getFile2(this)
        if (image != null) {
            photoUri = FileProvider.getUriForFile(this, "home.taxi.com.mltest.provider", image)
        }

        imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(imageIntent, CAMERA_RESULT_ACTIVITY);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    fun onBitmapReceive(){
        val image = FirebaseVisionImage.fromFilePath(this, photoUri)
        val detector = FirebaseVision.getInstance().visionTextDetector
        val result = detector.detectInImage(image)
                .addOnSuccessListener{value->onSuccess(value)}
                .addOnFailureListener{e->onFail(e)}
    }

    private fun onSuccess(fvt :FirebaseVisionText){
        val line = StringBuilder()
        fvt.blocks.forEach{ block -> line.append(block.text + "\n"); onNextBlock(block)}
        text.text = line.toString()
    }

    fun onNextBlock(block : FirebaseVisionText.Block){
        val a = StringBuilder()
        a.append(block.text)
        val rect = block.boundingBox
        a.append("Lef -> "+ rect!!.left)
        a.append("Right -> "+rect.right)
        a.append("Top -> "+ rect.top)
        a.append("Bottom->"+rect.bottom)
        Log.d("LookBounds", a.toString())
    }

    private fun onFail(e :Exception){
        toast(e.localizedMessage)
    }
}
