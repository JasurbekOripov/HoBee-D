package uz.glight.hobee.distribuition.ui.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.glight.hobeedistribuition.network.model.UserLogin
import com.glight.hobeedistribuition.network.model.UserModel
import com.glight.hobeedistribuition.utils.Constants
import com.glight.hobeedistribuition.utils.ModelPreferencesManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import uz.glight.hobee.distribuition.R
import uz.glight.hobee.distribuition.adapters.TabPagesAdapter
import uz.glight.hobee.distribuition.databinding.FragmentHomeBinding
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import uz.glight.hobee.distribuition.ui.activities.BottomNavigationActivity
import uz.glight.hobee.distribuition.ui.activities.LoginActivity
import uz.glight.hobee.distribuition.utils.NetworkHelper

class HomeFragment : Fragment(R.layout.fragment_home) {
    lateinit var homeViewModel: HomeViewModel
    private var bindingHome: FragmentHomeBinding? = null
    lateinit var networkHelper: NetworkHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentHomeBinding.bind(view)
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]
        bindingHome = binding
        networkHelper = NetworkHelper(requireContext())
        val pagesAdapter = TabPagesAdapter(
            childFragmentManager,
            lifecycle,
            arrayOf(PharmacyFragment(), ClinicsFragment())
        )
        binding.pages.adapter = pagesAdapter

        TabLayoutMediator(binding.tab, binding.pages) { tab, position ->
            tab.text = Constants.MAIN_TABS[position]
        }.attach()
        if (networkHelper.isNetworkConnected()) {
            homeViewModel.getPharmacy("", this)
            homeViewModel.getClinic("", viewLifecycleOwner)
        } else {
            Snackbar.make(view, "No internet connection", Snackbar.LENGTH_SHORT).show()
        }
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
            if (networkHelper.isNetworkConnected()) {
                if (bindingHome?.tab?.selectedTabPosition == 0) {
                    homeViewModel.getPharmacy("", this)
                } else if (bindingHome?.tab?.selectedTabPosition == 1) {
                    homeViewModel.getClinic("", viewLifecycleOwner)
                }
            } else {
                view?.let {
                    Snackbar.make(it, "No internet connection", Snackbar.LENGTH_SHORT).show()
                }
            }

            false;
        }
        searchView.queryHint = "Поиск"
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                lifecycleScope.launch {
                    var res = RemoteRepository.getDiscounts()
                    if (res.code() > 400) {
                        Snackbar.make(
                            view!!,
                            "You access is failed , please re-enter again",
                            Snackbar.LENGTH_LONG
                        ).show()
                        val la = Intent(requireContext(), LoginActivity::class.java)
                        la.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(la)
                        BottomNavigationActivity().finish()
                    }
                }
                if (newText != null && newText != "") {
                    if (networkHelper.isNetworkConnected()) {
                        if (bindingHome?.tab?.selectedTabPosition == 0) {
                            homeViewModel.getPharmacy(newText, viewLifecycleOwner)
                        } else if (bindingHome?.tab?.selectedTabPosition == 1) {
                            homeViewModel.getClinic(newText, viewLifecycleOwner)
                        }
                    } else {
                        view?.let {
                            Snackbar.make(
                                it,
                                "No internet connection",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }

                }else if (newText==""){
                    if (networkHelper.isNetworkConnected()) {
                        if (bindingHome?.tab?.selectedTabPosition == 0) {
                            homeViewModel.getPharmacy(newText, viewLifecycleOwner)
                        } else if (bindingHome?.tab?.selectedTabPosition == 1) {
                            homeViewModel.getClinic(newText, viewLifecycleOwner)
                        }
                    } else {
                        view?.let {
                            Snackbar.make(
                                it,
                                "No internet connection",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                return true
            }
        })
        super.onCreateOptionsMenu(menu, menuInflater)
    }
}