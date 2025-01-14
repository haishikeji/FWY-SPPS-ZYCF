<template>
  <div class="app-container">
    <div class="block">
      <span class="demonstration"></span>
      <el-select v-model="customerName" placeholder="请选择客户">
        <el-option
          v-for="item in options"
          :key="item.id"
          :label="item.name"
          :value="item.id">
        </el-option>
      </el-select>
      <span class="demonstration"></span>
      <el-date-picker
        v-model="yearTime"
        type="year"
        value-format="yyyy"
        placeholder="选择年">
      </el-date-picker>

      <el-button type="primary" icon="el-icon-search" size="medium" @click="handleQuery">搜索</el-button>
      <el-button icon="el-icon-refresh" size="medium" @click="resetQuery">重置</el-button>
    </div>

    <div id="chartPie" class="chart"></div>
  </div>
  <!--<div id="chartPie" class="chart"></div>-->
</template>
<script>
import { getCustomerDicts, getListOrders, delSort, addSort, updateSort, exportSort } from "@/api/service/statistic";
import echarts from "echarts"
import log from "../../monitor/job/log";
export default {
  data() {
    return {
      selectData:[
      ],
      legendData:[],
      options:[],
      chartPie: null,
      customerName:null,
      yearTime: null
    }
  },
  created() {
    this.getList();
    getCustomerDicts().then(response => {
      this.options = response.rows;
    });
  },
  methods: {
    getList(){
      var data = {"name":this.customerName,"time":this.yearTime};
      getListOrders(data).then(response => {
        this.selectData = response.data;
        if (this.selectData.length>0){
          for (let i=0; i<this.selectData.length;i++) {
            this.selectData[i].data =this.selectData[i].data.split(',');
            this.selectData[i].type='bar';
            this.selectData[i].stack='total';
            this.selectData[i].show=true;
            this.selectData[i].show=true;
            this.selectData[i].emphasis={focus: 'series'};
            this.legendData.push(this.selectData[i]["name"])
          }
        }
        this.drawPieChart();
      });
    },
    //搜索
    handleQuery() {
      var data = {"name":this.customerName,"time":this.yearTime};
      getListOrders(data).then(response => {
        if (this.selectData.length>0){
          for (let i=0; i<this.selectData.length;i++) {
            this.selectData[i].data =this.selectData[i].data.split(',');
            this.selectData[i].type='bar';
            this.selectData[i].stack='total';
            this.selectData[i].show=true;
            this.selectData[i].show=true;
            this.selectData[i].emphasis={focus: 'series'};
            this.legendData.push(this.selectData[i]["name"])
          }
        }else {
          this.selectData=[];
          this.legendData=[];
        }
        alert(JSON.stringify(this.selectData))
        alert(JSON.stringify(this.legendData))
        this.drawPieChart();
      });
    },
    //重置
    resetQuery() {
      this.customerName =null;
      this.yearTime=null;
    },
    drawPieChart() {
      // 基于准备好的dom，初始化echarts实例
      this.chartPie = echarts.init(document.getElementById('chartPie'));
      // 指定图表的配置项和数据
      this.chartPie.setOption({
        tooltip: {
          trigger: 'axis',
          axisPointer: {            // Use axis to trigger tooltip
            type: 'shadow'        // 'shadow' as default; can also be 'line' or 'shadow'
          }
        },
        legend: {
          data:this.legendData
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        yAxis: {
          type: 'value'
        },
        xAxis: {
          type: 'category',
          data: ['一月', '二月', '三月', '四月', '五月', '六月', '七月','八月','九月','十月','十一月','十二月']
        },
        series: this.selectData
      });
    },
  },
 /* mounted() {
    this.$nextTick(() => {
      this.drawPieChart();
    })
  },*/
}
</script>

<style scoped>
  .chart {
    width: 100%;
    height: 800px;
  }
</style>
