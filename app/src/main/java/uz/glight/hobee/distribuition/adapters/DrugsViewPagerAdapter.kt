package uz.glight.hobee.distribuition.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import uz.glight.hobee.distribuition.ui.fragments.drugstore.BasketFragment
import uz.glight.hobee.distribuition.ui.fragments.drugstore.WarehouseFragment
import uz.glight.hobee.distribuition.ui.fragments.home.PharmacyFragment

class DrugsViewPagerAdapter(var id: String, fa: FragmentManager, lifecycle: Lifecycle,) : FragmentStateAdapter(fa,lifecycle) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return if (position == 0) {
            WarehouseFragment.newInstance(id, id)
        } else {
            BasketFragment.newInstance(id, id)
        }
    }
}