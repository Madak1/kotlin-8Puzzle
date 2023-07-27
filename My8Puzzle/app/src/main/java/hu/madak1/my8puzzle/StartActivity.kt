package hu.madak1.my8puzzle

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File

class StartActivity : AppCompatActivity() {

    private lateinit var actImgPath: String

    private val imgLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("EXTRA_BG_PATH", this.actImgPath)
            startActivity(intent)
            this.finish()
        } else Toast.makeText(this, "Failed to take picture", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        val number = MyCardButton(findViewById(R.id.start_number_btn), R.string.btn_text_tv_number)
        val pic = MyCardButton(findViewById(R.id.start_picture_btn), R.string.btn_text_tv_picture)
        val photo = MyCardButton(findViewById(R.id.start_photo_btn), R.string.btn_text_tv_photo)
        val back = MyCardButton(findViewById(R.id.start_back_btn), R.string.btn_text_tv_back)

        number.setClickAction {
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("EXTRA_BG_RES", R.drawable.o_board_v2)
            startActivity(intent)
            this.finish()
        }
        pic.setClickAction {
            val res = when ((0..5).random()) {
                0 -> R.drawable.p1
                1 -> R.drawable.p2
                2 -> R.drawable.p3
                3 -> R.drawable.p4
                4 -> R.drawable.p5
                else -> R.drawable.p6
            }
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("EXTRA_BG_RES", res)
            startActivity(intent)
            this.finish()
        }

        photo.setClickAction {
            if (hasCameraPermission()) this.takePicture()
            else this.requestPermissions()
        }

        back.setClickAction {
            this.finish()
        }
    }

    // - Photo ----------------------------------------------------------------------------------

    private fun takePicture() {
        val imgName = "photo"
        val storageDictionary = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imgFile: File = File.createTempFile(imgName, ".png", storageDictionary) // TODO: jpg
        this.actImgPath = imgFile.absolutePath
        val imgUri = FileProvider
            .getUriForFile(this, "hu.madak1.my8puzzle.fileprovider", imgFile)
        val imgIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        imgIntent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
        this.imgLauncher.launch(imgIntent)
    }

    // - Permissions ----------------------------------------------------------------------------

    private fun hasCameraPermission() =
        ActivityCompat.checkSelfPermission(
            this, android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (!hasCameraPermission())
            permissionsToRequest.add(android.Manifest.permission.CAMERA)
        if (permissionsToRequest.isNotEmpty())
            ActivityCompat.requestPermissions(
                this, permissionsToRequest.toTypedArray(), 111
            )
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 111 && grantResults.isNotEmpty()) {
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("PermissionReq", "${permissions[i]} is granted!")
                } else Log.i("PermissionReq", "${permissions[i]} is denied!")
            }
        }
        if (this.hasCameraPermission()) this.takePicture()
    }
}