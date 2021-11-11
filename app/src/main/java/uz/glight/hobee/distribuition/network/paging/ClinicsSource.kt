package uz.glight.hobee.distribuition.network.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import uz.glight.hobee.distribuition.network.models.ClinicModel
import uz.glight.hobee.distribuition.network.repository.RemoteRepository
import java.lang.Exception

class ClinicsSource( var name: String) : PagingSource<Int, ClinicModel>() {
    override fun getRefreshKey(state: PagingState<Int, ClinicModel>): Int? {
        return state.anchorPosition
    }

    var totalPage = -1
    var pageNumber = -1
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ClinicModel> {

        if(totalPage==-1||totalPage>pageNumber){
            var pageNumber = params.key ?: 1
            var res = RemoteRepository.getClinic(name,pageNumber)
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
                        res.body() as ArrayList<ClinicModel>,
                        pageNumber - 1,
                        pageNumber + 1
                    )
                } else {
                    LoadResult.Page(res.body() as ArrayList<ClinicModel>, null, pageNumber + 1)
                }
            } catch (e: Exception) {
                LoadResult.Page(emptyList(), null, null)
            }
        }
        return LoadResult.Page(emptyList(), null, null)
    }
}