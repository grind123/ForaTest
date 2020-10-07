package com.grind.foratest.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.SurfaceControl
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.*
import com.grind.foratest.models.Info
import com.grind.foratest.R
import com.grind.foratest.adapters.AlbumListAdapter
import com.grind.foratest.utils.ItemOffsetDecoration
import com.grind.foratest.viewmodels.SearchViewModel
import com.jakewharton.rxbinding2.widget.RxTextView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class SearchFragment : Fragment() {
    private val compositeDisposable = CompositeDisposable()

    private lateinit var rv: RecyclerView
    private lateinit var adapter: AlbumListAdapter
    private lateinit var lm: RecyclerView.LayoutManager

    private lateinit var etSearch: EditText
    private lateinit var dropSearch: ImageView
    private lateinit var emptySearch: View

    private lateinit var viewModel: SearchViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inflate = View.inflate(context, R.layout.fragment_album_search, null)
        rv = inflate.findViewById(R.id.rv_album_list)
        etSearch = inflate.findViewById(R.id.et_search)
        dropSearch = inflate.findViewById(R.id.imv_search_drop)
        emptySearch = inflate.findViewById(R.id.ll_search_empty)

        lm = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        if (savedInstanceState != null) {
            lm.onRestoreInstanceState(savedInstanceState.getParcelable("lmState"))
        }
        rv.layoutManager = lm

        viewModel = ViewModelProvider.NewInstanceFactory().create(SearchViewModel::class.java)
        viewModel.data.observe(
            { this.lifecycle },
            { showAlbumList(it) }
        )

        exitTransition = Fade()

        return inflate
    }

    override fun onStart() {
        super.onStart()
        viewModel.getAlbumList("beatles")

        adapter = AlbumListAdapter(object : AlbumListAdapter.AlbumItemClickListener {
            //when some album has been clicked fragment transaction started
            override fun itemClick(albumId: Int, sharedView: View) {
                val bundle = Bundle()
                bundle.putInt("albumId", albumId)
                fragmentManager?.beginTransaction()
                    ?.replace(R.id.main_container,
                        AlbumInfoFragment().apply {
                            arguments = bundle
                            sharedElementEnterTransition = object : TransitionSet(){}.apply {
                                addTransition(ChangeBounds())
                                addTransition(ChangeTransform())
                                addTransition(ChangeImageTransform())
                            }
                            sharedElementReturnTransition = object : TransitionSet(){}.apply {
                                addTransition(ChangeBounds())
                                addTransition(ChangeTransform())
                                addTransition(ChangeImageTransform())
                            }
                            enterTransition = Fade()
                        })
                    ?.addSharedElement(sharedView, ViewCompat.getTransitionName(sharedView)!!)
//                    ?.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    ?.addToBackStack(this.javaClass.simpleName)
                    ?.commit()
            }
        })
        rv.adapter = adapter
        rv.addItemDecoration((ItemOffsetDecoration(context!!, R.dimen.grid_item_margin)))

        dropSearch.setOnClickListener {
            etSearch.setText("")
        }

        //textChangeListener for search
        val searchSubscribe = RxTextView.textChanges(etSearch)
            .subscribeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .filter { it.isNotEmpty() }
            .map { it.toString().replace(' ', '+') }
            .debounce(500, TimeUnit.MILLISECONDS)
            .subscribe({
                viewModel.getAlbumList(it)
            },
                { Log.e(this.javaClass.simpleName, it.message) })
        compositeDisposable.add(searchSubscribe)
    }

    private fun showAlbumList(list: List<Info>) {
        Log.e(this.javaClass.simpleName, "list presented, size ${list.size}")
        if (list.isNotEmpty()) {
            rv.visibility = View.VISIBLE
            emptySearch.visibility = View.GONE
            adapter.setItems(list)
            rv.scrollToPosition(0)
        } else {
            rv.visibility = View.INVISIBLE
            emptySearch.visibility = View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable("lmState", lm.onSaveInstanceState())
    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearResources()
        compositeDisposable.clear()
    }

}