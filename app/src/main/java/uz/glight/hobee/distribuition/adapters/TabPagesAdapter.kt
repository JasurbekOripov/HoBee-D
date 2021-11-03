package uz.glight.hobee.distribuition.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class TabPagesAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle, private val pages: Array<Fragment>):
    FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment = pages[position]

}