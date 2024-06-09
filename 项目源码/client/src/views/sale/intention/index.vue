<template>
  <div class="table-box">
    <ProTable
      ref="proTable"
      title="用户列表"
      :columns="columns"
      :requestApi="getTableList"
      :initParam="initParam"
      :dataCallback="dataCallback"
    >
      <!-- 表格 header 按钮 -->
      <template #tableHeader>
        <el-button type="primary" :icon="CirclePlus" plain>新增</el-button>
        <el-button type="primary" :icon="Upload" plain>设置客户标签</el-button>
        <el-button type="primary" :icon="Download" plain
          >添加回访计划</el-button
        >
      </template>
      <!-- 表格操作 -->
      <template #operation="scope">
        <el-button
          size="small"
          link
          :icon="View"
          @click="checkViewConsult(scope)"
          >查看</el-button
        >
        <el-button
          size="small"
          link
          :icon="EditPen"
          @click="checkeditConsult(scope)"
          >编辑</el-button
        >
        <el-popconfirm
          title="Are you sure to delete this?"
          @confirm="deleteConsult(scope)"
          confirm-button-type="danger"
        >
          <template #reference
            ><el-button size="small" link :icon="Delete"
              >删除</el-button
            ></template
          >
        </el-popconfirm>
      </template>
    </ProTable>

    <counselDialong ref="DialongRef" />
  </div>
</template>

<script setup lang="tsx" name="useProTable">
import { ref, reactive } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useRouter } from 'vue-router'
import { ColumnProps } from '@/components/ProTable/interface'
import ProTable from '@/components/ProTable/index.vue'
import counselDialong from './counselDialog/index.vue'
import {
  CirclePlus,
  Delete,
  EditPen,
  Download,
  Upload,
  View,
  Refresh
} from '@element-plus/icons-vue'
import {
  pageIntentionByKey,
  addIntention,
  editIntention,
  addCommunicationRecord
} from '@/apis/soldManage'
const router = useRouter()

const proTable = ref()

// 如果表格需要初始化请求参数，直接定义传给 ProTable(之后每次请求都会自动带上该参数，此参数更改之后也会一直带上，改变此参数会自动刷新表格数据)

const initParam = reactive({
  type: 1
})

const dataCallback = (data: any) => {
  return {
    list: data.list,
    total: data.total,
    pageNum: data.pageNum,
    pageSize: data.pageSize
  }
}
//删除咨询
const deleteConsult = async (params: any) => {
  const res: any = await addCommunicationRecord(params.row.elderId)
  if (res.code === 200 && res.msg === '操作成功') {
    ElMessage.success('删除成功')
    proTable.value.getTableList()
  } else {
    ElMessage.error('删除失败')
  }
}

//编辑咨询
const checkeditConsult = (params: any) => {
  openDrawer('编辑', params.row)
}
//  点击查看
const checkViewConsult = (params: any) => {
  openDrawer('查看', params.row)
}

// 打开 drawer(新增、查看、编辑)
const DialongRef = ref()
const openDrawer = (title: string, rowData: any = {}) => {
  const params = {
    title,
    rowData: { ...rowData },
    isView: title === '查看',
    api:
      title === '新增' ? addIntention : title === '编辑' ? editIntention : '',
    getTableList: proTable.value.getTableList
  }
  DialongRef.value.acceptParams(params)
}

// 如果你想在请求之前对当前请求参数做一些操作，可以自定义如下函数：params 为当前所有的请求参数（包括分页），最后返回请求列表接口
const getTableList = (params: any) => {
  return pageIntentionByKey(params)
}

// 表格配置项
const columns: ColumnProps<any>[] = [
  { prop: 'rank', label: '序号', width: 55 },
  {
    prop: 'name',
    label: '老人姓名',
    search: { el: 'input' }
  },
  {
    prop: 'phone',
    label: '老人联系电话',
    search: { el: 'input', props: { filterable: true } }
  },
  {
    prop: 'infoSource',
    label: '信息来源',
    search: { el: 'select' }
  },
  { prop: 'sex', label: '性别', search: { el: 'select' } },
  { prop: 'age', label: '年龄' },
  { prop: 'RecommendationChannel', label: '推荐渠道' },
  {
    prop: 'clientTag',
    label: '客户标签',
    search: { el: 'tree-select', props: { filterable: true } },
    fieldNames: { label: 'userLabel', value: 'userStatus' }
  },
  {
    prop: 'BelongSalesStaff ',
    label: '归属销售人员',
    search: { el: 'tree-select', props: { filterable: true } },
    width: 180
  },
  { prop: 'operation', label: '操作', fixed: 'right', width: 200 }
]
</script>
