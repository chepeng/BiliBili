package com.bilibili.lingxiao.home.live.adapter

import android.net.Uri
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bilibili.lingxiao.GlobalProperties
import com.bilibili.lingxiao.R
import com.bilibili.lingxiao.home.live.BannerImageLoader
import com.bilibili.lingxiao.home.live.model.LiveData
import com.bilibili.lingxiao.home.live.model.MultiItemLiveData
import com.camera.lingxiao.common.utills.LogUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.chad.library.adapter.base.util.MultiTypeDelegate
import com.facebook.drawee.view.SimpleDraweeView
import com.youth.banner.Banner
import com.youth.banner.BannerConfig
import com.youth.banner.Transformer
import kotlin.properties.Delegates

class LiveRecyAdapter : BaseQuickAdapter<MultiItemLiveData, BaseViewHolder> {
    var recycledViewPool:RecyclerView.RecycledViewPool by Delegates.notNull()
    constructor(data:MutableList<MultiItemLiveData>, recycledViewPool:RecyclerView.RecycledViewPool) :super(data){
        this.recycledViewPool = recycledViewPool
       /* addItemType(MultiItemLiveData.BANNER,R.layout.layout_banner)
        addItemType(MultiItemLiveData.CATEGORY,R.layout.item_live_category)
        addItemType(MultiItemLiveData.RECOMMEND,R.layout.layout_recommend)
        addItemType(MultiItemLiveData.PARTITION,R.layout.layout_partition)*/

        multiTypeDelegate = object : MultiTypeDelegate<MultiItemLiveData>() {
            override fun getItemType(entity: MultiItemLiveData): Int {
                //根据你的实体类来判断布局类型
                return entity.itemType
            }
        }
        multiTypeDelegate
            //.registerItemType(LiveData.BANNER,R.layout.layout_banner)
            //.registerItemType(LiveData.CATEGORY,R.layout.item_live_category)
            .registerItemType(LiveData.RECOMMEND,R.layout.layout_recommend) //推荐的
            .registerItemType(LiveData.PARTITION,R.layout.layout_partition) //分类的
    }
    override fun convert(helper: BaseViewHolder, item: MultiItemLiveData) {
        when(helper.itemViewType){
            LiveData.BANNER-> {
                //var banner :Banner = helper.getView(R.id.live_banner)
                //initBanner(banner,item.bannerList)
            }
            LiveData.CATEGORY->{
                //initCategory(helper,item.entranceIcons)
                //helper.setText(R.id.item_live_title,item.entranceIconsBean.name)
                //var image : SimpleDraweeView = helper.getView(R.id.item_live_image)
                //image.setImageURI(Uri.parse(item.entranceIconsBean.entrance_icon.src))
            }
            LiveData.RECOMMEND->{
                initRecommend(helper,item.liveList)
                helper.setText(R.id.live_recommend_more,"更多${item.partitionsBean.partition.count}个推荐直播 >")
                helper.addOnClickListener(R.id.live_recommend_more)
            }
            LiveData.PARTITION->{
                var image : SimpleDraweeView = helper.getView(R.id.image_part)
                image.setImageURI(Uri.parse(item.partitionsBean.partition.sub_icon.src + GlobalProperties.IMAGE_RULE_240_150))

                helper.setText(R.id.live_category_name,item.partitionsBean.partition.name)

                initPartition(helper,item.partitionsBean.lives)
                helper.addOnClickListener(R.id.live_look_more)
                helper.setText(R.id.live_look_more,"当前${item.partitionsBean.partition.count}个直播")
                //initPartition(helper,item.partitions)
            }
        }
    }

    private fun initPartition(helper: BaseViewHolder, data: MutableList<LiveData.RecommendDataBean.LivesBean>) {
        var list = data
        if (list.size > 4){
            list = list.subList(0,4)
        }
        var categoryAdapter =
            PartitionVideoAdapter(R.layout.item_live_video, list)
        var manager = GridLayoutManager(mContext,2)
        val recyclerView:RecyclerView = helper.getView(R.id.live_partition_recy)
        recyclerView.adapter = categoryAdapter
        recyclerView.layoutManager = manager
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setRecycledViewPool(recycledViewPool)

        recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    categoryAdapter.isScroller = false
                    categoryAdapter.notifyDataSetChanged()
                }else{
                    categoryAdapter.isScroller = true
                }
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        categoryAdapter.setOnItemClickListener(object :OnItemClickListener{
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                listener?.onPartitionClick(data[position],position)
            }

        })
    }

    private fun initRecommend(helper: BaseViewHolder, lives: MutableList<LiveData.RecommendDataBean.LivesBean>) {
        val recyclerView:RecyclerView = helper.getView(R.id.recycerView)
        recyclerView.setHasFixedSize(true)      //设置固定大小
        recyclerView.setLayoutManager(GridLayoutManager(mContext, 2,GridLayoutManager.VERTICAL,false))
        var liveRecommendAdapter =
            LiveRecommendAdapter(R.layout.item_live_video, lives)
        recyclerView.adapter = liveRecommendAdapter
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.setRecycledViewPool(recycledViewPool)

        liveRecommendAdapter.setOnItemClickListener(object :OnItemClickListener{
            override fun onItemClick(adapter: BaseQuickAdapter<*, *>?, view: View?, position: Int) {
                listener?.onRecommendClick(lives[position],position)
            }
        })
    }
    private  var listener: OnMultiItemClickListener? = null
    fun setMultiItemClickListener(listener: OnMultiItemClickListener){
        this.listener = listener
    }
    interface OnMultiItemClickListener{
        fun onRecommendClick(live: LiveData.RecommendDataBean.LivesBean, position:Int)
        fun onPartitionClick(live: LiveData.RecommendDataBean.LivesBean, position:Int)
    }
}