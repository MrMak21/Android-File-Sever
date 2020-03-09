package gr.slg.uploadserverfiles

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import gr.slg.uploadserverfiles.vm.MainListener
import gr.slg.uploadserverfiles.vm.MainViewModel
import java.io.File


class MainActivity : AppCompatActivity(),MainListener {

    lateinit var vm: MainViewModel

    lateinit var btnUpload: Button
    lateinit var sendFile: Button
    lateinit var fileName: TextView
    lateinit var downloadUrl: TextView
    lateinit var selectedFileName: TextView
    lateinit var selectedFileSize: TextView
    lateinit var selectedImage: ImageView

    lateinit var path: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        vm = ViewModelProvider(this).get(MainViewModel::class.java)
        vm.setMainListener(this)


        btnUpload = findViewById(R.id.btn_upload)
        sendFile = findViewById(R.id.sendFile)
        downloadUrl = findViewById(R.id.downloadUrl)
        fileName = findViewById(R.id.fileName)
        selectedFileName = findViewById(R.id.selectedFileName)
        selectedFileSize = findViewById(R.id.selectedFileSize)
        selectedImage = findViewById(R.id.selectedImage)

        setUpListeners()
        setUpObservers()
    }

    private fun setUpListeners() {

        btnUpload.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                vm.selectFiles()
            }
        })

        sendFile.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                vm.uploadFile(vm.file.value!!)
            }
        })
    }

    private fun setUpObservers() {
        vm.file.observe(this, Observer { it ->
            if (it != null) {
                var file = File(it.path)
                selectedFileName.text = file.name
                selectedFileSize.text = file.length().toString()
                when (file.extension) {
                    "jpeg" -> { selectedImage.setImageResource(R.drawable.ic_img) }
                    "jpg" -> { selectedImage.setImageResource(R.drawable.ic_img) }
                    "png" -> { selectedImage.setImageResource(R.drawable.ic_img) }
                    "json" -> { selectedImage.setImageResource(R.drawable.ic_json) }
                    "pdf" -> { selectedImage.setImageResource(R.drawable.ic_pdf) }
                    else -> { selectedImage.setImageResource(R.drawable.ic_corrupted)
                    }
                }
            }
        })

        vm.fileName.observe(this, Observer {
            if (it != null) {
                fileName.text = it
            }
        })

        vm.downloadUrl.observe(this, Observer {
            if (it != null) {
                downloadUrl.text = it
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

    override fun selectFile() {
        var i = Intent(Intent.ACTION_GET_CONTENT)
        i.setType("*/*")
        startActivityForResult(i, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK && data != null) {
            vm.getSelectedFile(data.data)
        }
    }

}
