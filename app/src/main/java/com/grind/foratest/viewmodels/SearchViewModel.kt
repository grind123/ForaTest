package com.grind.foratest.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.grind.foratest.models.Info
import com.grind.foratest.repositories.AlbumRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class SearchViewModel: ViewModel() {

    private val repo: AlbumRepo = AlbumRepo.getInstance()
    private val cd = CompositeDisposable()
    val data: MutableLiveData<List<Info>> = MutableLiveData()
    val errorData: MutableLiveData<Boolean> = MutableLiveData()


    fun getAlbumList(searchSequence: String){
        cd.add(repo.getAlbumsList(searchSequence)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({data.postValue(it.infoList.sortedBy { it.collectionName })}, {errorData.postValue(true)}))
    }

    fun clearResources(){
        cd.clear()
    }

}