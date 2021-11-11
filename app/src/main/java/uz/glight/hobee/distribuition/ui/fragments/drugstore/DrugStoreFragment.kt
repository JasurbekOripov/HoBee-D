package uz.glight.hobee.distribuition.ui.fragments.drugstore

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.glight.hobeedistribuition.utils.Constants
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.DrugsViewPagerAdapter
import uz.glight.hobee.distribuition.databinding.FragmentDrugStoreBinding
import uz.glight.hobee.distribuition.utils.NetworkHelper
import uz.glight.hobee.distribuition.viewmodels.WareHouseViewModel

class DrugStoreFragment : Fragment(R.layout.fragment_drug_store) {

    private lateinit var wareHouseViewModel: WareHouseViewModel
    lateinit var networkHelper: NetworkHelper
    private var drugStoreBinding: FragmentDrugStoreBinding? = null
    lateinit var pagerAdapter: DrugsViewPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentDrugStoreBinding.bind(view)
        wareHouseViewModel = ViewModelProvider(this)[WareHouseViewModel::class.java]
        drugStoreBinding = binding
        networkHelper = NetworkHelper(requireContext())
        var id = requireParentFragment().arguments?.getInt("drugstore_id") ?: 1
        pagerAdapter = DrugsViewPagerAdapter(id.toString(), childFragmentManager, lifecycle)
        binding.tab
        binding.pages.adapter = pagerAdapter
        TabLayoutMediator(binding.tab, binding.pages) { tab, position ->
            tab.text = Constants.DRUGSTORE_TABS[position]
            tab.setIcon(Constants.DRUGSTORE_TAB_ICONS[position])
        }.attach()
        binding.tab.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.text == "Препараты") {
                    setHasOptionsMenu(true)
                } else {
                    setHasOptionsMenu(false)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })

    }

    override fun onDestroyView() {
        drugStoreBinding = null
        super.onDestroyView()
    }



}