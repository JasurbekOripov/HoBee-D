package uz.glight.hobee.distribuition.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import uz.glight.hobee.distribuition.network.models.Item
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import java.lang.Exception

class OrderItemsSource(var id: Int) : PagingSource<Int, Item>() {
    override fun getRefreshKey(state: PagingState<Int, Item>): Int? {
        return state.anchorPosition
    }

    var totalPage = -1
    var pageNumber = -1
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Item> {
        if(totalPage==-1||totalPage>pageNumber){
            pageNumber = params.key ?: 1
            var res = RemoteRepository.getRespons("http://83.69.136.134/v1/api/request-agent-items?request_agent_id=${id}&per-page25&page=${pageNumber}")
        var headers = res.headers()
            if (totalPage==-1){
                for (i in headers) {
                    if (i.first.contains("X-Pagination-Page-Count")) {
                        totalPage = i.second.toInt() ?: 1
                        break
                    }
                }
            }
          return  try {
                if (pageNumber > 1) {
                    LoadResult.Page(
                        res.body() as ArrayList<Item>,
                        pageNumber - 1,
                        pageNumber + 1
                    )
                } else {
                    LoadResult.Page(res.body() as ArrayList<Item>, null, pageNumber + 1)
                }
            } catch (e: Exception) {
                LoadResult.Page(emptyList(), null, null)
            }
        }
        return LoadResult.Page(emptyList(), null, null)
    }
}