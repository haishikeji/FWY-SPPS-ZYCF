<template>
	<view class="page-content">
		<view class="content-layout" :style="{ 'padding-bottom': detail.orderStatus == 1 ? '20rpx' : '120rpx' }">
			<view class="status-layout item-bg">
				<view class="status-text">{{ getStatus() }}</view>
				<view>{{ detail.customerName }}</view>
			</view>
			<view class="item-layout-parent item-bg">
				<view class="tip-layout"><view class="tip-text">配送信息</view></view>
				<view class="item-layout">
					<view class="left-text">收货地址</view>
					<view class="right-text">{{ detail.address }}</view>
				</view>
				<view class="item-layout" v-if="detail.orderStatus != 1">
					<view class="left-text">配送时间</view>
					<view class="right-text">{{ detail.deliveryTime || '' }}</view>
				</view>
				<view class="item-layout" v-if="detail.orderStatus == 1">
					<view class="left-text">送货时间</view>
					<view class="right-text">{{ detail.consignTime || '' }}</view>
				</view>
				<view class="item-layout" v-if="detail.orderStatus == 1">
					<view class="left-text">完成时间</view>
					<view class="right-text">{{ detail.orderTime || '' }}</view>
				</view>
			</view>
			<view class="item-layout-parent item-bg">
				<view class="tip-layout">
					<text class="tip-text">商品信息</text>
					<text class="total-text">共{{ total }}件</text>
				</view>
				<view
					v-if="detail.orderItemList"
					class="common-list-item"
					style="border-bottom: 1rpx solid #efefef;padding: 12rpx 0;"
					v-for="(item, index) in detail.orderItemList"
					:key="index"
				>
					<image v-if="item.image" class="img" :src="imageUrl + item.image"></image>
					<image v-else class="img" src="/static/image/common/icon_default_pic.png"></image>
					<view class="content-layout">
						<text class="name">{{ item.name }}</text>
						<text class="des">{{ item.foodSize }}</text>
						<text class="des">数量{{ item.num }}</text>
					</view>
				</view>
			</view>
			<view class="item-layout-parent item-bg">
				<view class="tip-layout"><view class="tip-text">订单信息</view></view>
				<view class="item-layout">
					<view class="left-text">订单编号</view>
					<view class="right-text">{{ detail.id || '' }}</view>
				</view>
				<view class="item-layout">
					<view class="left-text">下单时间</view>
					<view class="right-text">{{ detail.createTime || '' }}</view>
				</view>
			</view>
		</view>
		<view class="control-layout">
			<input v-model="temperature" v-if="detail.orderStatus == 0 && detail.consignStatus == 0" placeholder="请输入您的体温" type="digit" class="temp-input" />
			<view @click="startDistribution()" v-if="detail.orderStatus == 0 && detail.consignStatus == 0" style="width: 300rpx;" class="control-btn">开始配送</view>
			<view @click="complete()" v-if="detail.orderStatus == 0 && detail.consignStatus == 1" class="control-btn">确认送达</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			interval: undefined,
			imageUrl: process.env.NODE_ENV === 'development' ? this.$config.imageUrl.dev : this.$config.imageUrl.pro,
			id: undefined,
			detail: {},
			temperature: undefined
		};
	},
	computed: {
		total() {
			let total = 0;
			if (this.detail.orderItemList) {
				this.detail.orderItemList.forEach(item => {
					total += item.num;
				});
			}
			return total;
		}
	},
	methods: {
		getStatus() {
			switch (this.detail.orderStatus) {
				case '0':
					return '待收货';
				case '1':
					return '已完成';
				case '2':
					return '已取消';
			}
		},
		uploadLocation() {
			uni.getLocation({
				type: 'gcj02',
				success: res => {
					let param = {
						orderId: this.id,
						latitude: res.latitude,
						longitude: res.longitude
					};
					this.$http.postJson('/upload', param);
				}
			});
		},
		startDistribution() {
			if (!Number(this.temperature)) {
				uni.showToast({
					icon: 'none',
					title: '请输入您的体温'
				});
				return;
			} else if (Number(this.temperature) < 35 || Number(this.temperature) > 37.3) {
				uni.showModal({
					title: '温馨提示',
					content: '您的体温异常，暂时不适合配送哦~',
					showCancel: false
				});
				return;
			}
			this.$debounce.debounce({
				key: 'startDistribution',
				time: 2000,
				success: () => {
					let param = {
						temp: this.temperature
					};
					this.$http.get('/delivery/' + this.id, { params: param }).then(res => {
						if (res.code === 200) {
							this.detail.consignStatus = 1;
							let orderIds = uni.getStorageSync('orderIds') || [];
							orderIds.push(this.id);
							uni.setStorageSync('orderIds', orderIds);
							this.uploadLocation();
							this.interval = setInterval(this.uploadLocation, 15000);
						}
					});
				}
			});
		},
		complete() {
			this.$debounce.debounce({
				key: 'complete',
				time: 2000,
				success: () => {
					this.$http.get('/update_state/' + this.id).then(res => {
						if (res.code === 200) {
							let orderIds = uni.getStorageSync('orderIds');
							let index = orderIds.findIndex(item => item == this.id);
							orderIds.splice(index, 1);
							uni.setStorageSync('orderIds', orderIds);
							uni.showToast({
								icon: 'none',
								title: '订单已完成'
							});
							setTimeout(() => {
								uni.navigateBack();
							}, 1000);
						}
					});
				}
			});
		},
		loadData() {
			this.$http.get('/send/' + this.id).then(res => {
				if (res.code === 200) {
					this.detail = res.data;
				}
			});
		}
	},
	onLoad(data) {
		this.id = data.id;
		this.loadData();
	},
	onUnload() {
		if (this.interval) {
			clearInterval(this.interval);
		}
	}
};
</script>

<style lang="scss" scoped>
@import './order.scss';
/deep/.logo-text {
	display: none;
}
</style>
