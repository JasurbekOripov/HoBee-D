package uz.glight.hobee.distribuition.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.glight.hobeedistribuition.network.model.OrderModel
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import java.lang.Exception

class ApplicationsSource() : PagingSource<Int, OrderModel>() {
    override fun getRefreshKey(state: PagingState<Int, OrderModel>): Int? {
        return state.anchorPosition
    }

    var totalPage = -1
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, OrderModel> {
        var pageNumber = params.key ?: 1
        if(totalPage==-1||totalPage>=pageNumber){
            var res = RemoteRepository.getApplicationsList(pageNumber)
            var headers = res.headers()
            if (totalPage==-1){
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
                        res.body() as ArrayList<OrderModel>,
                        pageNumber - 1,
                        pageNumber + 1
                    )
                } else {
                    LoadResult.Page(res.body() as ArrayList<OrderModel>, null, pageNumber + 1)
                }
            } catch (e: Exception) {
                LoadResult.Page(emptyList(), null, null)
            }
        }
        return LoadResult.Page(emptyList(), null, pageNumber + 1)
    }
}