package gr.slg.uploadserverfiles.Retrofit

import android.net.Uri

class Repo {

    fun  selectfile(path: Uri?): Uri? {

        if (path!= null) {
            return path
        }
        return null
    }
}