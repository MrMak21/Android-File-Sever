package gr.slg.uploadserverfiles

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.loader.content.CursorLoader
import com.google.gson.GsonBuilder
import gr.slg.uploadserverfiles.Retrofit.Api
import gr.slg.uploadserverfiles.Retrofit.MyResponse
import gr.slg.uploadserverfiles.Retrofit.ServerResponse
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

        btnUpload.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                var i = Intent(Intent.ACTION_GET_CONTENT)
                i.setType("*/*")
                startActivityForResult(i, 100)
            }
        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            var selectedFile = data.data
            uploadFile(selectedFile)
        }
    }

    private fun uploadFile(path: Uri) {
        //creating a file
//        var file = File(getRealPathFromURI(path))
        var file = File(path.path)
        var split = arrayOf(file.path.split(":"))
        var filePath = split[0].get(1)
        file = File(filePath)


        //creating request body for file
        var requestFile =
            RequestBody.create(MediaType.parse(contentResolver.getType(path)), file)

        var fileSend = MultipartBody.Part.createFormData("file", file.name, requestFile)


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

        call.enqueue(object : retrofit2.Callback<ServerResponse> {
            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(
                call: Call<ServerResponse>,
                response: Response<ServerResponse>
            ) {
                if (!response.body()!!.fileName.isNullOrEmpty()) {
                    Toast.makeText(applicationContext, "File uploaded succesfully", Toast.LENGTH_SHORT).show()
                    Toast.makeText(applicationContext,response.body()!!.fileName + " " + response.body()!!.fileDownloadUri,Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(applicationContext, "Some error occured", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    fun getRealPathFromURI(contentUri: Uri): String {
        var proj = arrayOf(MediaStore.Audio.Media.DATA)
        var cursor = managedQuery(contentUri, proj, null, null, null);
        var column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst()
        return cursor.getString(column_index)
    }

//    private fun getRealPathFromUri(uri: Uri): String? {
//        var proj = arrayOf(MediaStore.Files.FileColumns.DATA)
////        var loader = CursorLoader(this, uri, proj, null, null, null)
////        var cursor = loader.loadInBackground()
////
////        var column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
////        cursor.moveToFirst()
////        var result = cursor.getString(column_index)
////        cursor.close()
////        return result
//
//        var path: String? = null
//
//        var cursor = contentResolver.query(uri,proj,null,null,null)
//        if (cursor == null) {
//            path = uri.path
//        } else {
//            cursor.moveToFirst()
//            var column_index = cursor.getColumnIndexOrThrow(proj[0])
//            path = cursor.getString(column_index)
//            cursor.close()
//        }
//        if (path == null || path.isEmpty()) {
//            return uri.path
//        } else {
//            return path
//        }
//    }
}
