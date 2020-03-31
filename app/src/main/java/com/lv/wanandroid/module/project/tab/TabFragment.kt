package com.lv.wanandroid.module.project.tab

import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.lv.wanandroid.R
import com.lv.wanandroid.base.BaseFragmentLazy
import com.lv.wanandroid.module.project.bean.Data
import com.lv.wanandroid.module.project.bean.DataX
import com.lv.wanandroid.module.project.tab.adapter.TabRvAdapter
import com.lv.wanandroid.module.square.tab.adapter.SquTabRvAdapter
import com.lv.wanandroid.module.project.tab.mvp.TabContract
import com.lv.wanandroid.module.project.tab.mvp.TabPresenter
import com.lv.wanandroid.web.AgentWebActivity
import kotlinx.android.synthetic.main.frag_tab.*

//https://www.wanandroid.com/blog/show/2666
class TabFragment :
    BaseFragmentLazy<TabContract.View, TabContract.Presenter>, TabContract.View {

    private var curPage = 0
    private var pageCount = 0
    lateinit var nav: Data
    var mAdapterSqu: TabRvAdapter? = null

    constructor()
    constructor(nav: Data) {
        this.nav = nav
    }

    override fun createPresenter(): TabContract.Presenter {
        return TabPresenter()
    }

    override fun layoutId(): Int {
        return R.layout.frag_tab
    }

    override fun bindView() {
        if (mAdapterSqu == null || mAdapterSqu!!.data.size <= 0) {
            initRv()
            request(0)
        }
    }

    private fun initRv() {
        tab_recycler.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        mAdapterSqu = TabRvAdapter(R.layout.tab_rv_item)
        tab_recycler.adapter = mAdapterSqu
        tab_refresh.setEnableRefresh(false)
        tab_refresh.setOnLoadMoreListener {
            if (++curPage < pageCount) {
                request(curPage)
            } else {
                tab_refresh.finishRefreshWithNoMoreData()
            }
        }
        mAdapterSqu?.setOnItemClickListener { adapter, _, position ->
            val data = adapter.data[position] as DataX
            val intent = Intent(context, AgentWebActivity::class.java)
            intent.putExtra("link", data.link)
            context?.startActivity(intent)
        }
    }

    private fun request(curPage: Int) {
        mPresenter.requestNavData(curPage, nav.id)
    }

    override fun resultNavData(curPage: Int, pageCount: Int, datas: List<DataX>) {
        this.curPage = curPage
        this.pageCount = pageCount
        if (curPage == 0) {
            mAdapterSqu?.setNewData(datas)
        } else {
            mAdapterSqu?.addData(datas)
            tab_refresh.finishLoadMore()
        }
    }

    companion object {
        fun start(nav: Data): TabFragment {
            return TabFragment(nav)
        }
    }
}