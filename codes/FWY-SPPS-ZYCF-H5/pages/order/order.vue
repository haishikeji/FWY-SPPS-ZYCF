<template>
	<view class="page-content">
		<view class="type-layout">
			<view @click="typeClick(index)" v-for="(item, index) in typeList" :key="index" class="type-item">
				<view class="item-content">
					<text :class="{ active: typeIndex == index }">{{ item }}</text>
					<view class="divider" v-if="typeIndex === index"></view>
				</view>
			</view>
		</view>
		<view @click="toDetail(item)" class="list-item" v-for="(item, index) in dataList" :key="index">
			<view class="status-layout">
				<text class="status-text">{{ getStatus(item) }}</text>
				<text class="time-text">{{ getTime(item) }}</text>
				<image src="/static/image/common/icon_arrow_right.png"></image>
			</view>
			<scroll-view :scroll-x="true" class="scroll-layout">
				<view v-if="item.orderItemList" v-for="(food, i) in item.orderItemList" :key="food.id" class="item">
					<image v-if="food.image" :src="imageUrl + food.image"></image>
					<image v-else src="/static/image/common/icon_default_pic.png"></image>
				</view>
			</scroll-view>
			<view class="total-text">共 {{ getTotal(item.orderItemList) }} 件商品</view>
			<view class="control-layout">
				<view v-if="item.orderStatus == 0" @click.stop="cancelOrder(item)" class="control-item cancel">取消订单</view>
				<view v-if="item.orderStatus != 0" @click.stop="deleteOrder(item)" class="control-item">删除订单</view>
				<view @click.stop="buyAgain(item)" class="control-item active">再次购买</view>
			</view>
		</view>
		<empty-layout v-if="loadingType === 'empty'" :option="{ tip: '没有找到订单~', fixed: true, top: '320rpx' }"></empty-layout>
		<uni-load-more v-if="dataList.length > 10" :status="loadingType"></uni-load-more>
	</view>
</template>

<script>
import uniLoadMore from '@/components/uni-load-more/uni-load-more.vue';
import emptyLayout from '@/components/empty-layout/empty-layout.vue';

export default {
	components: {
		uniLoadMore,
		emptyLayout
	},
	data() {
		return {
			imageUrl: process.env.NODE_ENV === 'development' ? this.$config.imageUrl.dev : this.$config.imageUrl.pro,
			typeList: ['全部订单', '已完成', '待收货'],
			typeIndex: 0,
			dataList: [],
			loadingType: 'more',
			pageNum: 1,
			pageSize: 10
		};
	},
	methods: {
		getStatus(item) {
			if (item.orderStatus == 0) {
				return '待收货';
			} else if (item.orderStatus == 1) {
				return '已完成';
			} else if (item.orderStatus == 2) {
				return '已取消';
			}
			return '';
		},
		getTime(item) {
			if (item.orderStatus == 0) {
				return '配送时间 ' + (item.deliveryTime || '');
			} else if (item.orderStatus == 1) {
				return item.orderTime;
			}
			return '';
		},
		getTotal(list) {
			let total = 0;
			list.forEach(item => {
				total += item.num;
			});
			return total;
		},
		typeClick(index) {
			this.typeIndex = index;
			this.pageNum = 1;
			this.loadingType = 'more';
			this.loadDataList();
		},
		loadDataList() {
			let param = {
				pageNum: this.pageNum,
				pageSize: this.pageSize,
				orderByColumn: 'orderStatus,deliveryTime',
				isAsc: 'asc'
			};
			if (this.typeIndex === 1) {
				param.orderState = 1;
			} else if (this.typeIndex === 2) {
				param.orderState = 0;
			}
			this.$http.get('/order/list', { params: param }).then(res => {
				if (res.code === 200) {
					if (this.pageNum == 1) {
						this.dataList = [];
					}
					this.dataList = this.dataList.concat(res.rows);
					if (this.dataList.length === 0) {
						this.loadingType = 'empty';
					} else if (this.dataList.length === Number(res.total)) {
						this.loadingType = 'nomore';
					} else {
						this.loadingType = 'more';
					}
				}
			});
		},
		toDetail(item) {
			uni.navigateTo({
				url: './order-detail?id=' + item.id + '&status=' + item.orderStatus
			});
		},
		cancelOrder(item) {
			uni.showModal({
				title: '提示',
				content: '确定取消订单吗',
				success: res => {
					if (res.confirm) {
						this.$http.get('/order/cancel/' + item.id).then(res => {
							if (res.code === 200) {
								this.loadDataList();
							}
						});
					}
				}
			});
		},
		deleteOrder(item) {
			uni.showModal({
				title: '提示',
				content: '确定删除订单吗',
				success: res => {
					if (res.confirm) {
						this.$http.get('/order/del/' + item.id).then(res => {
							if (res.code === 200) {
								this.loadDataList();
							}
						});
					}
				}
			});
		},
		buyAgain(item) {
			uni.navigateTo({
				url: '/pages/order/order-submit?isFromOrder=1&param=' + JSON.stringify(item.orderItemList)
			});
		}
	},
	onShow() {
		this.loadDataList();
	},
	onReachBottom() {
		if (this.loadingType === 'more') {
			this.pageNum++;
			this.loadingType = 'loading';
			this.loadDataList();
		}
	}
};
</script>

<style lang="scss" scoped>
@import './order.scss';
</style>
