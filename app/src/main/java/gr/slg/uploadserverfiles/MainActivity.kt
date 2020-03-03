package gr.slg.uploadserverfiles

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.loader.content.CursorLoader
import com.google.gson.GsonBuilder
import gr.slg.uploadserverfiles.Retrofit.Api
import gr.slg.uploadserverfiles.Retrofit.MyResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File


class MainActivity : AppCompatActivity() {

    lateinit var btnUpload: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnUpload = findViewById(R.id.btn_upload)


        //checking the permission
        //checking the permission
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(
//                this,
//                Manifest.permission.READ_EXTERNAL_STORAGE
//            )
//            != PackageManager.PERMISSION_GRANTED
//        ) {
//            val intent = Intent(
//                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
//                Uri.parse("package:$packageName")
//            )
//            finish()
//            startActivity(intent)
//            return
//        }

        btnUpload.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var i = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(i, 100)
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            var selectedImage = data.data
            uploadFile(selectedImage)
        }
    }

    private fun uploadFile(fileUri: Uri) {
        //creating a file
        var file = File(getRealPathFromUri(fileUri))

        //creating request body for file
        var requestFile =
            RequestBody.create(MediaType.parse(contentResolver.getType(fileUri)), file)

        var fileSend = MultipartBody.Part.createFormData("file",file.name,requestFile)


        //The gson builder
        val gson = GsonBuilder()
            .setLenient()
            .create()

        //Creating retrofit object
        val retrofit = Retrofit.Builder()
            .baseUrl(Api.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        //creating our Api
        val api = retrofit.create(Api::class.java)

        //creating a call and calling the upload image method
        val call = api.uploadImagePart(fileSend)

        //finally performing call

        call.enqueue(object : retrofit2.Callback<MyResponse> {
            override fun onFailure(call: Call<MyResponse>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<MyResponse>, response: Response<MyResponse>) {
                if (!response.body()!!.error) {
                    Toast.makeText(
                        applicationContext,
                        "File uploaded succesfully",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(applicationContext, "Some error occured", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        })
    }

    private fun getRealPathFromUri(uri: Uri): String? {
        var proj = arrayOf(MediaStore.Images.Media.DATA)
        var loader = CursorLoader(this, uri, proj, null, null, null)
        var cursor = loader.loadInBackground()

        var column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        var result = cursor.getString(column_index)
        cursor.close()
        return result

    }
}
