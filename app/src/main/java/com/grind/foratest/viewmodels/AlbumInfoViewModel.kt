package com.grind.foratest.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.grind.foratest.models.Album
import com.grind.foratest.models.AlbumInfo
import com.grind.foratest.models.Info
import com.grind.foratest.models.Song
import com.grind.foratest.repositories.AlbumRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class AlbumInfoViewModel : ViewModel() {

    private val repo: AlbumRepo = AlbumRepo.getInstance()
    private val cd = CompositeDisposable()
    private val sourceData: MutableLiveData<List<Info>> = MutableLiveData()
    val errorData: MutableLiveData<Boolean> = MutableLiveData()
    val data: LiveData<AlbumInfo> = Transformations.switchMap(sourceData) { transformToAlbumInfo(it) }




    fun getAlbumInfo(id: Int) {
        cd.add(repo.getAlbumInfoData(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({sourceData.postValue(it.infoList)}, {errorData.postValue(true)}))
    }

    fun clearResources(){
        cd.clear()
    }

    private fun transformToAlbumInfo(infoList: List<Info>): MutableLiveData<AlbumInfo> {
        var album: Album? = null
        val songs = mutableListOf<Song>()
        infoList.forEach { info ->
            if (info.wrapperType == "collection") {
                album = info.toAlbum()
            } else if (info.wrapperType == "track" && info.kind == "song") {
                Log.e("Track", info.trackName)
                songs.add(info.toSong())
            }
        }

        return MutableLiveData<AlbumInfo>().apply { value = AlbumInfo(album!!, songs) }
    }
}
