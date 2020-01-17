package apps.mishka.testapp.ui.search

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import apps.mishka.testapp.R
import apps.mishka.testapp.data.model.Album
import apps.mishka.testapp.di.DaggerFragmentComponent
import apps.mishka.testapp.di.FragmentModule
import apps.mishka.testapp.ui.common.CommonDividerItemDecoration
import kotlinx.android.synthetic.main.fragment_search.view.*
import javax.inject.Inject


class SearchFragment : Fragment() {

    @Inject
    lateinit var viewModel: SearchViewModel

    private lateinit var searchResultAlbumAdapter: AlbumAdapter
    private lateinit var searchHistoryAlbumAdapter: AlbumAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFragment()
        initObservers()
        searchResultAlbumAdapter = SearchResultAlbumAdapter(viewModel)
        searchHistoryAlbumAdapter = SearchHistoryAlbumAdapter(viewModel)
    }

    private fun injectFragment() {
        val component = DaggerFragmentComponent.builder()
            .fragmentModule(FragmentModule(this, context ?: return))
            .build()

        component?.inject(this)
    }

    private fun initObservers() {
        viewModel.results.observe(this, Observer { albums ->
            showSearchResults(albums)
        })

        viewModel.navigateToAlbumDetails.observe(this, Observer { event ->
            event?.getContentIfNotHandled()?.let { album ->
                navigateToAlbumDetailsFragment(album)
            }
        })

        viewModel.isSearchFocused.observe(this, Observer { isFocused ->
            if (isFocused) {
                emphasizeSearchEditText()
            } else {
                emphasizeSearchHistory()
            }
        })

        viewModel.searchHistory.observe(this, Observer { albums ->
            searchHistoryAlbumAdapter.setAlbums(albums)
        })

        viewModel.removeFocusFromSearchView.observe(this, Observer { event ->
            event?.getContentIfNotHandled()?.let {
                clearSearchViewFocus()
            }
        })

        viewModel.showSearchResults.observe(this, Observer { event ->
            event?.getContentIfNotHandled()?.let {
                showSearchResultsRecyclerView()
            }
        })

        viewModel.hideSearchResults.observe(this, Observer { event ->
            event?.getContentIfNotHandled()?.let {
                hideSearchResultsRecyclerView()
            }
        })

        viewModel.isLoadingVisible.observe(this, Observer { isVisible ->
            if (isVisible)
                showLoadingView()
            else
                hideLoadingView()
        })

        viewModel.clearSearchQuery.observe(this, Observer { event ->
            event?.getContentIfNotHandled()?.let {
                clearSearchQuery()
            }
        })

        viewModel.isNoHistoryStubVisible.observe(this, Observer { isVisible ->
            if (isVisible)
                showNoSearchHistoryStub()
            else
                hideNoSearchHistoryStub()
        })

        viewModel.showNoInternetConnectionNotification.observe(this, Observer { event ->
            event?.getContentIfNotHandled()?.let {
                showNoInternetConnectionToast()
            }
        })
    }

    //    Show RecyclerView with search results, if list isn't empty
    private fun showSearchResults(albums: List<Album>) {
        if (albums.isNotEmpty())
            showSearchResultsRecyclerView()

        view?.recyclerViewSearchResult?.layoutManager?.scrollToPosition(0)

        searchResultAlbumAdapter.setAlbums(albums)
    }

    private fun navigateToAlbumDetailsFragment(album: Album) {
        val bundle = Bundle().apply { putSerializable(Album.TAG, album) }
        findNavController().navigate(R.id.action_fromSearch_toDetails, bundle)
    }

    //    Visually emphasize SearchEditText via showing shadow
    private fun emphasizeSearchEditText() {
        view?.viewShadow?.fadeIn()
    }

    //    Visually emphasize search history via hiding shadow
    private fun emphasizeSearchHistory() {
        view?.viewShadow?.fadeOut()
        hideSearchResultsRecyclerView()
    }

    private fun clearSearchViewFocus() {
        view?.editTextSearch?.clearFocus()
        view?.viewFocusStub?.requestFocus()
        hideKeyboard()
    }

    private fun showSearchResultsRecyclerView() {
        view?.recyclerViewSearchResult?.visibility = View.VISIBLE
    }

    private fun hideSearchResultsRecyclerView() {
        view?.recyclerViewSearchResult?.visibility = View.GONE
    }

    private fun showLoadingView() {
        view?.loadingView?.fadeIn()
    }

    private fun hideLoadingView() {
        view?.loadingView?.fadeOut()
    }

    private fun clearSearchQuery() {
        this.view?.editTextSearch?.text?.clear()
    }

    private fun showNoSearchHistoryStub() {
        view?.textViewNoHistoryStub?.fadeIn()
    }

    private fun hideNoSearchHistoryStub() {
        view?.textViewNoHistoryStub?.fadeOut()
    }

    private fun showNoInternetConnectionToast() {
        Toast.makeText(context, R.string.no_internet_connection, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private val ANIMATION_DURATION = 400L

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    private fun View.fadeIn() {
        visibility = View.VISIBLE
        animate().alpha(1f).setDuration(ANIMATION_DURATION).start()
    }

    private fun View.fadeOut() {
        animate().alpha(0f).setDuration(ANIMATION_DURATION).start()
        postDelayed({ visibility = View.GONE }, ANIMATION_DURATION)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        view.editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(query: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.onSearchQueryChanged(query.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

        })

        view.editTextSearch.onBackPressedListener = viewModel
        view.editTextSearch.setOnFocusChangeListener { v, hasFocus ->
            viewModel.onSearchViewFocusChanged(hasFocus)
        }
        view.editTextSearch.setOnClickListener {
            viewModel.onSearchViewClick(view.editTextSearch.hasFocus())
        }

        view.viewFocusStub.setOnFocusChangeListener(::onStubFocusObtained)

        view.viewShadow.setOnClickListener {
            viewModel.onShadowClick()
        }

        initSearchResultRecyclerView(view)
        initSearchHistoryRecyclerView(view)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            setElevationOrder(view)
            addSearchViewOutline(view)
        }

        return view
    }

    private fun onStubFocusObtained(view: View, hasFocus: Boolean) {
        if (hasFocus) hideKeyboard()
    }

    private fun initSearchResultRecyclerView(view: View) {
        view.recyclerViewSearchResult.layoutManager = LinearLayoutManager(context)
        view.recyclerViewSearchResult.adapter = searchResultAlbumAdapter
    }

    private fun initSearchHistoryRecyclerView(view: View) {
        val layoutManager = LinearLayoutManager(context)
        view.recyclerViewSearchHistory.layoutManager = layoutManager
        view.recyclerViewSearchHistory.adapter = searchHistoryAlbumAdapter
        view.recyclerViewSearchHistory.addItemDecoration(CommonDividerItemDecoration.getInstance(view.context))
    }

    //    Set proper z-axis order
    private fun setElevationOrder(view: View) {
        view.viewShadow.bringToFront()
        view.editTextSearch.bringToFront()
        view.recyclerViewSearchResult.bringToFront()
        view.loadingView.bringToFront()
    }

    private fun addSearchViewOutline(view: View) {
        view.editTextSearch.background = resources.getDrawable(R.drawable.background_capsule_outlined)
    }

    fun onBackPressed() {
        viewModel.onBackPressed()
    }

    companion object {
        val TAG = SearchFragment::class.java.simpleName
    }
}

