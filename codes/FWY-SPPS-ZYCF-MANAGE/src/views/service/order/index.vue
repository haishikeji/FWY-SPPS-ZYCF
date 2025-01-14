<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="客户ID" prop="customerId">
        <el-input
            v-model="queryParams.customerId"
            placeholder="请输入客户ID"
            clearable
            size="small"
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="客户名称" prop="customerName">
        <el-input
            v-model="queryParams.customerName"
            placeholder="请输入客户名称"
            clearable
            size="small"
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="金额合计" prop="totalMoney">
        <el-input
            v-model="queryParams.totalMoney"
            placeholder="请输入金额合计"
            clearable
            size="small"
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="订单状态" prop="orderStatus">
        <el-select v-model="queryParams.orderStatus" placeholder="请选择">
          <el-option
              v-for="dict in orderOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="发货状态" prop="consignStatus">
        <el-select v-model="queryParams.consignStatus" placeholder="请选择发货状态" clearable size="small">
          <el-option
              v-for="dict in consignOptions"
              :key="dict.dictValue"
              :label="dict.dictLabel"
              :value="dict.dictValue"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="配送员" prop="sendId">
        <el-select v-model="queryParams.sendId" placeholder="请选择">
          <el-option
              v-for="dict in sendOptions"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          ></el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="数量合计" prop="totalNum">
        <el-input
            v-model="queryParams.totalNum"
            placeholder="请输入数量合计"
            clearable
            size="small"
            @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="发货时间" prop="consignTime">
        <el-date-picker clearable size="small"
                        v-model="queryParams.consignTime"
                        type="date"
                        value-format="yyyy-MM-dd"
                        placeholder="选择发货时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <!-- <el-col :span="1.5">
         <el-button
           type="primary"
           plain
           icon="el-icon-plus"
           size="mini"
           @click="handleAdd"
           v-hasPermi="['service:order:add']"
         >新增</el-button>
       </el-col>-->
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['service:order:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['service:order:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
            v-hasPermi="['service:order:export']"
        >导出
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            plain
            icon="el-icon-download"
            size="mini"
            :disabled="multiple"
            @click="handlePart"
        >一键分配
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="orderList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="订单号" align="center" prop="id">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="text"
              @click="getOrderItem(scope.row.id)"
          >{{scope.row.id}}
          </el-button>
        </template>
      </el-table-column>
      <!-- <el-table-column label="客户ID" align="center" prop="customerId" />-->
      <el-table-column label="客户名称" align="center" prop="customerName"/>
      <!--  <el-table-column label="金额合计" align="center" prop="totalMoney" />-->
      <el-table-column label="订单状态" align="center" prop="orderStatus" :formatter="formatOrderStatus"/>
      <el-table-column label="发货状态" align="center" prop="consignStatus" :formatter="formatConsignStatus"/>
      <el-table-column label="配送员" align="center" prop="sendId"/>
      <el-table-column label="数量合计" align="center" prop="totalNum"/>
      <el-table-column label="发货时间" align="center" prop="consignTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.consignTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="text"
              icon="el-icon-edit"
              @click="handleUpdate(scope.row)"
              v-hasPermi="['service:order:edit']"
          >修改
          </el-button>
          <!-- <el-button
             size="mini"
             type="text"
             icon="el-icon-delete"
             @click="handleDelete(scope.row)"
             v-hasPermi="['service:order:remove']"
           >删除</el-button>-->
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 选择配送员对话框-->
    <el-dialog :title="title" :visible.sync="openBox" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="配送员" prop="sendId">
          <el-select v-model="form.sendId" placeholder="请选择">
            <el-option
                v-for="dict in sendOptions"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitSend">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!--订单详细对话框-->
    <el-dialog :title="title" :visible.sync="openItem" width="900px" append-to-body>
      <el-table v-loading="loading" :data="itemList">
        <!-- <el-table-column label="订单号" align="center" prop="orderId" />-->
        <el-table-column label="商品ID" align="center" prop="detailId"/>
        <el-table-column label="商品名称" align="center" prop="name"/>
        <el-table-column label="商品规格" align="center" prop="foodSize"/>
        <el-table-column label="数量" align="center" prop="num"/>
        <el-table-column label="一级分类" align="center" prop="categoryId1"/>
        <el-table-column label="二级分类" align="center" prop="categoryId2"/>
        <!-- <el-table-column label="单价" align="center" prop="price" />
         <el-table-column label="总金额" align="center" prop="money" />-->
        <el-table-column label="图片">
          <template slot-scope="scope">
            <el-image style="width: 100px; height: 100px" :src="imageHead+itemList[scope.$index].image"></el-image>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark"/>
      </el-table>
    </el-dialog>


    <!-- 添加或修改订单列对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <!-- <el-form-item label="客户ID" prop="customerId">
           <el-input v-model="form.customerId" placeholder="请输入客户ID" />
         </el-form-item>-->
        <!--  <el-form-item label="客户名称" prop="customerName">
            <el-input v-model="form.customerName" placeholder="请输入客户名称" />
          </el-form-item>-->
        <!--   <el-form-item label="金额合计" prop="totalMoney">
             <el-input v-model="form.totalMoney" placeholder="请输入金额合计" />
           </el-form-item>-->
        <!--  <el-form-item label="订单状态" prop="orderStatus">
            <el-select v-model="form.orderStatus" placeholder="请选择">
              <el-option
                v-for="dict in orderOptions"
                :key="dict.dictValue"
                :label="dict.dictLabel"
                :value="dict.dictValue"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="发货状态">
            <el-select v-model="form.consignStatus" placeholder="请选择">
              <el-option
                v-for="dict in consignOptions"
                :key="dict.dictValue"
                :label="dict.dictLabel"
                :value="dict.dictValue"
              ></el-option>
            </el-select>
          </el-form-item>-->
        <!--  <el-form-item label="配送员" prop="sendId">
            <el-select v-model="form.sendId" placeholder="请选择">
              <el-option
                v-for="dict in sendOptions"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
              ></el-option>
            </el-select>
          </el-form-item>-->
        <!--  <el-form-item label="数量合计" prop="totalNum">
            <el-input v-model="form.totalNum" placeholder="请输入数量合计" />
          </el-form-item>-->
        <el-form-item label="发货时间" prop="consignTime">
          <el-date-picker clearable size="small"
                          v-model="form.consignTime"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="选择发货时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
  import {
    listOrder,
    getOrder,
    delOrder,
    addOrder,
    updateOrder,
    exportOrder,
    getOrderSends,
    getItem,
    updateSends
  } from "@/api/service/order";
  import {EventBus} from "@/utils/event-bus.js";

  export default {
    name: "Order",
    components: {},
    data() {
      return {
        openBox: false,
        sendIds: [],
        imageHead: process.env.VUE_APP_BASE_API,
        //发货状态
        consignOptions: [],
        // 遮罩层
        loading: true,
        // 选中数组
        ids: [],
        sendOptions: [],
        orderOptions: [],
        // 非单个禁用
        single: true,
        // 非多个禁用
        multiple: true,
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        //订单详细数据
        itemList: [],
        // 订单列表格数据
        orderList: [],
        // 弹出层标题
        title: "",
        //是否显示订单详细框
        openItem: false,
        // 是否显示弹出层
        open: false,
        // 查询参数
        queryParams: {
          pageNum: 1,
          pageSize: 10,
          customerId: null,
          customerName: null,
          totalMoney: null,
          orderStatus: null,
          consignStatus: null,
          sendId: null,
          totalNum: null,
          consignTime: null
        },
        // 表单参数
        form: {},
        // 表单校验
        rules: {}
      };
    },
    created() {
      this.getList();
      this.getDicts("t_food_order").then(response => {
        this.orderOptions = response.data;
      });
      this.getDicts("t_consign_status").then(response => {
        this.consignOptions = response.data;
      });
      getOrderSends().then(response => {
        this.sendOptions = response.data;
      });
      EventBus.$on("refreshOrder", () => {
        this.getList()
      });
    },
    beforeDestroy() {
      EventBus.$off('refreshOrder')
    },
    methods: {
      /** 分配提交 */
      submitSend() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.sendId != null) {
              var data = {"sends": this.sendIds, "sendId": this.form.sendId};
              updateSends(data).then(response => {
                this.msgSuccess("一件分配成功");
                this.openBox = false;
                this.getList();
              });
            }
          }
        });
      },
      /** 一件分配 */
      handlePart(row) {
        this.sendIds = row.id || this.ids;
        this.openBox = true;
      },
      getOrderItem(id) {
        this.openItem = true;
        getItem(id).then(response => {
          this.itemList = response.data;
          this.loading = false;
        });
        this.title = "订单详情";
      },
      //订单状态
      formatOrderStatus(row, column, cellValue) {
        if (cellValue === "1") {
          return '已完成';
        } else if (cellValue === "0") {
          return '未完成';
        } else if (cellValue === "2") {
          return '已取消';
        }
      },
      //发货状态
      formatConsignStatus(row, column, cellValue) {
        if (cellValue === "1") {
          return '已发货';
        } else if (cellValue === "0") {
          return '未发货';
        } else if (cellValue === "2") {
          return '已收货';
        }
      },
      /** 查询订单列列表 */
      getList() {
        this.loading = true;
        let param = Object.assign({
          orderByColumn: 'orderStatus,consignStatus,deliveryTime',
          isAsc: 'asc'
        }, this.queryParams)
        listOrder(param).then(response => {
          this.orderList = response.rows;
          this.total = response.total;
          this.loading = false;
        });
      },
      // 取消按钮
      cancel() {
        this.openItem = false;
        this.open = false;
        this.openBox = false;
        this.sendIds = [];
        this.reset();
      },
      // 表单重置
      reset() {
        this.form = {
          id: null,
          customerId: null,
          customerName: null,
          totalMoney: null,
          orderStatus: "0",
          consignStatus: "0",
          createBy: null,
          createTime: null,
          updateBy: null,
          updateTime: null,
          remark: null,
          sendId: null,
          totalNum: null,
          consignTime: null
        };
        this.resetForm("form");
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.resetForm("queryForm");
        this.handleQuery();
      },
      // 多选框选中数据
      handleSelectionChange(selection) {
        this.ids = selection.map(item => item.id)
        this.single = selection.length !== 1
        this.multiple = !selection.length
      },
      /** 新增按钮操作 */
      handleAdd() {
        this.reset();
        this.open = true;
        this.title = "添加订单列";
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.reset();
        const id = row.id || this.ids
        getOrder(id).then(response => {
          this.form = response.data;
          this.open = true;
          this.title = "修改订单列";
        });
      },
      /** 提交按钮 */
      submitForm() {
        this.$refs["form"].validate(valid => {
          if (valid) {
            if (this.form.id != null) {
              updateOrder(this.form).then(response => {
                this.msgSuccess("修改成功");
                this.open = false;
                this.getList();
              });
            } else {
              addOrder(this.form).then(response => {
                this.msgSuccess("新增成功");
                this.open = false;
                this.getList();
              });
            }
          }
        });
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        const ids = row.id || this.ids;
        this.$confirm('是否确认删除订单列编号为"' + ids + '"的数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function () {
          return delOrder(ids);
        }).then(() => {
          this.getList();
          this.msgSuccess("删除成功");
        })
      },
      /** 导出按钮操作 */
      handleExport() {
        const queryParams = this.queryParams;
        this.$confirm('是否确认导出所有订单列数据项?', "警告", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        }).then(function () {
          return exportOrder(queryParams);
        }).then(response => {
          this.download(response.msg);
        })
      }
    }
  };
</script>
