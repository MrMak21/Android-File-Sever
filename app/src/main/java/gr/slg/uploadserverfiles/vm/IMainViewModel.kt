package gr.slg.uploadserverfiles.vm

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File


interface IMainViewModel {



    fun setMainListener(listener: MainListener)
    fun selectFiles()
    fun getSelectedFile(path: Uri)
    fun uploadFile(path: Uri)
}