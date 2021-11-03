package uz.glight.hobee.distribuition.ui.fragments.home

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.glight.hobeedistribuition.utils.Constants
import com.google.android.material.tabs.TabLayoutMediator
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.TabPagesAdapter
import uz.glight.hobee.distribuition.databinding.FragmentHomeBinding

class HomeFragment : Fragment(R.layout.fragment_home) {
    private val homeViewModel: HomeViewModel by viewModels()
    private var bindingHome: FragmentHomeBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)
        bindingHome = binding
        val pagesAdapter = TabPagesAdapter(
            childFragmentManager,
            lifecycle,
            arrayOf(PharmacyFragment(), ClinicsFragment())
        )
        binding.pages.adapter = pagesAdapter

        TabLayoutMediator(binding.tab, binding.pages) { tab, position ->
            tab.text = Constants.MAIN_TABS[position]
        }.attach()
        homeViewModel.getPharmacy()
        homeViewModel.getClinic()
    }

    override fun onDestroyView() {
        bindingHome = null
        super.onDestroyView()
    }


    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        val search = menu.findItem(R.id.appSearchBar)
        val searchView = search.actionView as SearchView

        searchView.setOnCloseListener {
            if (bindingHome?.tab?.selectedTabPosition == 0){
                homeViewModel.getPharmacy()
            } else if (bindingHome?.tab?.selectedTabPosition == 1) {
                homeViewModel.getClinic()
            }
            false;
        }
        searchView.queryHint = "Поиск"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotBlank()){
                    if (bindingHome?.tab?.selectedTabPosition == 0){
                        homeViewModel.getPharmacy(query)
                    } else if (bindingHome?.tab?.selectedTabPosition == 1) {
                        homeViewModel.getClinic(query)
                    }
                }
                return false
            }
            override fun onQueryTextChange(newText: String?): Boolean {

                return false
            }
        })
        super.onCreateOptionsMenu(menu, menuInflater)
    }
}