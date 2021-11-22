package uz.glight.hobee.distribuition.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.glight.hobeedistribuition.network.model.DrugModel
import com.google.android.material.snackbar.Snackbar
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import java.lang.Exception

class WareHouseSource(var str: String) : PagingSource<Int, DrugModel>() {
    override fun getRefreshKey(state: PagingState<Int, DrugModel>): Int? {
        return state.anchorPosition
    }

    var totalPage = -1
    var pageNumber = -1
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DrugModel> {
        try {

        if (totalPage == -1 || totalPage > pageNumber) {
            pageNumber = params.key ?: 1
            var res = RemoteRepository.getWarehouse(page = pageNumber, str = str)
            var headers = res.headers()
            if (totalPage == -1) {
                for (i in headers) {
                    if (i.first.contains("X-Pagination-Page-Count")) {
                        totalPage = i.second.toInt() ?: 1
                        break
                    }
                }
            }
            return try {
                if (pageNumber > 1) {
                    LoadResult.Page(
                        res.body() as ArrayList<DrugModel>,
                        pageNumber - 1,
                        pageNumber + 1
                    )
                } else {
                    LoadResult.Page(res.body() as ArrayList<DrugModel>, null, pageNumber + 1)
                }
            } catch (e: Exception) {
                LoadResult.Page(emptyList(), null, null)
            }
        }

        } catch (e: Exception) {
        }
        return LoadResult.Page(emptyList(), null, null)
    }
}