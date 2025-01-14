<template>
	<view class="page-content">
		<scroll-view :scroll-x="true" class="type-layout">
			<view @click="typeClick(index)" v-for="(item, index) in typeList" :key="index" class="item">
				<view class="item-content">
					<image v-if="item.imageIco" :src="imageUrl + item.imageIco"></image>
					<image v-else src="/static/image/common/icon_default_pic.png"></image>
					<text>{{ item.name }}</text>
					<view class="divider" v-if="typeIndex === index"></view>
				</view>
			</view>
		</scroll-view>
		<view class="scroll-panel" id="scroll-panel">
			<view class="list-box">
				<view class="left">
					<scroll-view scroll-y :style="{ height: total > 0 ? scrollHeight - 50 + 'px' : scrollHeight + 'px' }" :scroll-into-view="scrollIntoView" scroll-with-animation>
						<view
							class="item"
							v-for="(item, index) in categoryList"
							:key="index"
							:class="{ active: index == categoryIndex }"
							:id="'id-' + index"
							@click="selectCategory(index)"
						>
							{{ item.foodCategory }}
						</view>
					</scroll-view>
				</view>
				<view class="main">
					<scroll-view scroll-y :style="{ height: total > 0 ? scrollHeight - 50 + 'px' : scrollHeight + 'px' }">
						<view class="common-list-item" v-for="(item, index) in foodList" :key="index" @click="toDetail(item)">
							<image v-if="item.foodPictures" class="img" :src="imageUrl + item.foodPictures"></image>
							<image v-else class="img" src="/static/image/common/icon_default_pic.png"></image>
							<view class="content-layout">
								<text class="name">{{ item.foodName }}</text>
								<text class="des">{{ item.foodRule }}</text>
							</view>
							<view class="add-layout">
								<image v-if="!checkAddedCart(item)" @click.stop="addCart(item, 1)" class="add-icon" src="@/static/image/common/icon_add_cart.png"></image>
								<uni-number-box :max="99" v-if="checkAddedCart(item)" :bindId="item.id + ''" :value="getNumber(item)" @change="onNumberChange"></uni-number-box>
							</view>
						</view>
					</scroll-view>
				</view>
			</view>
		</view>
		<view class="bottom-layout" v-if="total > 0">
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
			scrollHeight: 0,
			imageUrl: process.env.NODE_ENV === 'development' ? this.$config.imageUrl.dev : this.$config.imageUrl.pro,
			typeIndex: 0,
			typeList: [],
			categoryIndex: 0,
			categoryList: [],
			foodList: [],
			addedCartList: []
		};
	},
	computed: {
		scrollIntoView() {
			return `id-${this.categoryIndex > 4 ? this.categoryIndex - 4 : 0}`;
		},
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
		/* 初始化滚动区域 */
		initScrollView() {
			return new Promise((resolve, reject) => {
				let view = uni.createSelectorQuery().select('#scroll-panel');
				view.boundingClientRect(res => {
					this.scrollHeight = res.height - 72;
					this.$nextTick(() => {
						resolve();
					});
				}).exec();
			});
		},
		loadTypeList() {
			this.$http.get('/food/menu-list').then(res => {
				if (res.code === 200) {
					this.typeList = res.data;
					if (this.typeList.length > 0) {
						let categoryId = uni.getStorageSync('categoryId');
						if (categoryId) {
							this.typeIndex = this.typeList.findIndex(item => item.pid == categoryId);
							uni.removeStorageSync('categoryId');
							this.loadCategoryList(categoryId);
						} else {
							this.loadCategoryList(this.typeList[0].pid);
						}
					} else {
						this.categoryList = [];
						this.foodList = [];
					}
				}
			});
		},
		typeClick(index) {
			this.typeIndex = index;
			this.categoryIndex = 0;
			this.loadCategoryList(this.typeList[index].pid);
		},
		loadCategoryList(menuId) {
			this.$http.get('/food/sort-list/' + menuId).then(res => {
				if (res.code === 200) {
					this.categoryList = res.data;
					if (this.categoryList.length > 0) {
						this.loadFoodList(this.categoryList[0].foodPid);
					} else {
						this.foodList = [];
					}
				}
			});
		},
		selectCategory(index) {
			this.categoryIndex = index;
			this.loadFoodList(this.categoryList[index].foodPid);
		},
		loadFoodList(pid) {
			this.$http.get('/food/sort-foods/' + pid).then(res => {
				if (res.code === 200) {
					this.foodList = res.data;
				}
			});
		},
		toDetail(item) {
			uni.navigateTo({
				url: '../food/food-detail?id=' + item.id + '&name=' + item.foodName
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
		}
	},
	onShow() {
		if (uni.getStorageSync('Authorization')) {
			this.loadAddedCartList();
		}
		if (uni.getStorageSync('categoryId') && this.typeList.length > 0) {
			this.loadTypeList();
		}
	},
	onLoad(data) {
		this.showPid = data.pid;
		/* 等待滚动区域初始化完成 */
		setTimeout(() => {
			this.initScrollView().then(() => {
				this.loadTypeList();
			});
		}, 1);
	}
};
</script>

<style lang="scss">
@import './category.scss';
</style>
