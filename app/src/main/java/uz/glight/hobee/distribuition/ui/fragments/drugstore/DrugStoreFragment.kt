package uz.glight.hobee.distribuition.ui.fragments.drugstore

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.glight.hobeedistribuition.utils.Constants
import com.google.android.material.tabs.TabLayoutMediator
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.DrugsViewPagerAdapter
import uz.glight.hobee.distribuition.adapters.TabPagesAdapter
import uz.glight.hobee.distribuition.databinding.FragmentDrugStoreBinding
import uz.glight.hobee.distribuition.ui.fragments.home.ClinicsFragment
import uz.glight.hobee.distribuition.ui.fragments.home.PharmacyFragment

class DrugStoreFragment : Fragment(R.layout.fragment_drug_store) {

    private val viewModel: DrugStoreViewModel by viewModels()
    private var drugStoreBinding: FragmentDrugStoreBinding? = null
lateinit var pagerAdapter:DrugsViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDrugStoreBinding.bind(view)
        drugStoreBinding = binding
        var id=requireParentFragment().arguments?.getInt("drugstore_id")?:1
        pagerAdapter= DrugsViewPagerAdapter(id.toString(),childFragmentManager,lifecycle)
        binding.tab
        binding.pages.adapter = pagerAdapter

        TabLayoutMediator(binding.tab, binding.pages) { tab, position ->
            tab.text = Constants.DRUGSTORE_TABS[position]
            tab.setIcon(Constants.DRUGSTORE_TAB_ICONS[position])
        }.attach()
        viewModel.getWarehouse()
    }

    override fun onDestroyView() {
        drugStoreBinding = null
        super.onDestroyView()
    }


    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.search_menu, menu)
        val search = menu.findItem(R.id.appSearchBar)
        val searchView = search.actionView as SearchView
        searchView.queryHint = "Поиск"
        searchView.setOnCloseListener {
                viewModel.getWarehouse()
            false;
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (drugStoreBinding?.tab?.selectedTabPosition != 0){
                    drugStoreBinding?.pages?.setCurrentItem(0, true)
                }
                if (query != null){
                    if (drugStoreBinding?.tab?.selectedTabPosition == 0){
                        viewModel.getWarehouse(query)
                    }
                } else if (query.isNullOrBlank()){
                    viewModel.getWarehouse()
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