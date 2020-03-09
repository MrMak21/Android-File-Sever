package gr.slg.uploadserverfiles.vm

import android.app.Application
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.gson.GsonBuilder
import gr.slg.uploadserverfiles.Retrofit.Api
import gr.slg.uploadserverfiles.Retrofit.ServerResponse
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application),IMainViewModel {

    val app = application
    lateinit var listener: MainListener
    var file = MutableLiveData<Uri>()

    var fileName = MutableLiveData<String>()
    var downloadUrl = MutableLiveData<String>()



    override fun setMainListener(listener: MainListener) {
        this.listener = listener
    }

    override fun selectFiles() {
        listener.selectFile()
    }

    override fun getSelectedFile(path: Uri) {
        file.value = path
    }

    override fun uploadFile(path: Uri) {
        var file = File(path.path)
        if (!file.extension.isNullOrEmpty()) {
            var split = arrayOf(file.path.split(":"))
            var filePath = split[0].get(1)
            file = File(filePath)


            //creating request body for file
            var requestFile =
                RequestBody.create(MediaType.parse(app.contentResolver.getType(path)), file)

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
                    Toast.makeText(app.applicationContext, t.message, Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(
                    call: Call<ServerResponse>,
                    response: Response<ServerResponse>
                ) {
                    if (!response.body()!!.fileName.isNullOrEmpty()) {
                        Toast.makeText(
                            app.applicationContext,
                            "File uploaded succesfully",
                            Toast.LENGTH_SHORT
                        ).show()
                        fileName.value = response.body()!!.fileName
                        downloadUrl.value = response.body()!!.fileDownloadUri
                    } else {
                        Toast.makeText(app.applicationContext, "Some error occured", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            })
        } else {
            Toast.makeText(app.applicationContext,"Corrupted file", Toast.LENGTH_SHORT).show()
        }
    }
}