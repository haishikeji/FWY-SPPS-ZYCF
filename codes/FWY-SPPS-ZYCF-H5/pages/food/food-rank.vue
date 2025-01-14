<template>
	<view class="page-content">
		<view class="common-list-item list-item" v-for="(item, index) in dataList" :key="index" @click="toDetail(item)">
			<image v-if="item.foodPictures" class="img" :src="imageUrl + item.foodPictures"></image>
			<image v-else class="img" src="/static/image/common/icon_default_pic.png"></image>
			<view class="content-layout">
				<text class="name">{{ item.foodName }}</text>
				<text class="des">{{ item.foodRule }}</text>
			</view>
			<view class="add-layout">
				<image v-if="!checkAddedCart(item)" @click.stop="addCart(item, 1)" class="add-icon" src="@/static/image/common/icon_add_cart.png"></image>
				<uni-number-box
					style="margin-bottom: 12rpx;"
					:max="99"
					v-if="checkAddedCart(item)"
					:bindId="item.id + ''"
					:value="getNumber(item)"
					@change="onNumberChange"
				></uni-number-box>
			</view>
		</view>
		<view class="bottom-layout" style="bottom: 0;">
			<view class="total-layout">
				<text>合计</text>
				<text class="total">{{ total }}</text>
				<text>件商品</text>
			</view>
			<view @click="toCart()" class="pay-btn">去购物车</view>
			<image class="icon-cart" src="/static/image/cart/icon_cart.png"></image>
		</view>
	</view>
</template>

<script>
import { checkToken } from '@/utils/checkToken.js';

export default {
	data() {
		return {
			imageUrl: process.env.NODE_ENV === 'development' ? this.$config.imageUrl.dev : this.$config.imageUrl.pro,
			dataList: [],
			addedCartList: []
		};
	},
	computed: {
		total() {
			let count = 0;
			this.addedCartList.forEach(item => (count += Number(item.num)));
			return count;
		}
	},
	methods: {
		toCart() {
			uni.switchTab({
				url: '../cart/cart'
			});
		},
		loadDataList() {
			this.$http.get('/food/top-list').then(res => {
				if (res.code === 200) {
					this.dataList = res.data;
				}
			});
		},
		checkAddedCart(item) {
			return this.addedCartList.some(child => child.id == item.id && child.num > 0);
		},
		addCart(food, num) {
			if (!checkToken()) {
				return;
			}
			this.$http.post('/cart/add', { id: food.id, num: num }).then(res => {
				if (res.code === 200) {
					let temp = this.addedCartList.find(item => item.id == food.id);
					if (!temp) {
						this.addedCartList.push({
							id: food.id,
							num: num
						});
					} else {
						if (num == 0) {
							let index = this.addedCartList.findIndex(item => item.id == food.id);
							this.addedCartList.splice(index, 1);
						} else {
							temp.num = num;
						}
					}
				}
			});
		},
		onNumberChange(val) {
			if (!val.number) {
				return;
			}
			this.addCart({ id: val.bindId }, val.number);
		},
		getNumber(item) {
			let target = this.addedCartList.find(child => child.id == item.id);
			if (target) {
				return target.num;
			}
			return 0;
		},
		loadAddedCartList() {
			this.$http.get('/cart/list').then(res => {
				if (res.code === 200) {
					this.addedCartList = [];
					res.data.forEach(item => {
						this.addedCartList.push({ id: item.detailId, num: item.num });
					});
				}
			});
		},
		toDetail(item) {
			uni.navigateTo({
				url: '../food/food-detail?id=' + item.id + '&name=' + item.foodName
			});
		}
	},
	onLoad() {
		if (uni.getStorageSync('Authorization')) {
			this.loadAddedCartList();
		}
		this.loadDataList();
	}
};
</script>

<style lang="scss" scoped>
@import './food-rank.scss';
</style>
