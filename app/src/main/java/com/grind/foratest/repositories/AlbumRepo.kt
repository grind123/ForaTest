package com.grind.foratest.repositories


import com.grind.foratest.models.SearchResponse
import com.grind.foratest.retrofit.ITunesApi
import com.grind.foratest.retrofit.MyRetrofit
import io.reactivex.Observable

class AlbumRepo {

    companion object {
        private var instance: AlbumRepo? = null
        fun getInstance(): AlbumRepo {
            if (instance != null) {
                return instance!!
            } else {
                instance = AlbumRepo()
                return instance!!
            }
        }
    }

    private val api = MyRetrofit.getInstance().create(ITunesApi::class.java)

    fun getAlbumInfoData(albumId: Int): Observable<SearchResponse> {
        return api.getAlbumInfo(albumId, "song")
    }

    fun getAlbumsList(searchSequence: String): Observable<SearchResponse> {
        return api.searchAlbum(searchSequence, "album", 200)
    }
}