<template>
  <div>
    <div style="display: flex;padding: 0.75em;background-color: white">
      <div>
        <label id="el-id-5478-48" for="el-id-5478-64" class="el-form-item__label">楼栋 :</label>
        <el-select v-model="data.buildingId" placeholder="请选择">
          <el-option
            v-for="item in data.buildingList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
            @click="getFloorList"
          />
        </el-select>
      </div>
      <div style="margin: 0 0.75em">
        <label id="el-id-5478-48" for="el-id-5478-64" class="el-form-item__label">楼层 :</label>
        <el-select v-model="data.floorId" placeholder="请选择">
          <el-option
            v-for="item in data.floorList"
            :key="item.id"
            :label="item.name"
            :value="item.id"
            @click="getBedList"
          />
        </el-select>
      </div>
      <div style="display: flex;margin-right: 0.75em">
        <label id="el-id-5478-48" for="el-id-5478-64" class="el-form-item__label">姓名 :</label>
        <el-input v-model="data.elderName" placeholder="请输入"></el-input>
      </div>
      <div style="display: flex">
        <el-button
          class="bg-blue"
          type="primary"
          :icon="Search"
          @click="search"
        >
          搜索
        </el-button>
        <el-button :icon="Delete" @click="reset">重置</el-button>
      </div>
    </div>
    <div style="display: flex;padding: 10px 14px;margin-top: 5px;background-color: white"
         v-show="data.bedList.length > 0">
      <label class="bedFlagLabel">空闲：<p class="bedFlagColor" style="background-color: #67C23A"></p></label>
      <label class="bedFlagLabel" style="margin: 0 30px">预定：<p class="bedFlagColor"
                                                               style="background-color: #E6A23C"></p></label>
      <label class="bedFlagLabel">已入住 / 退住审核：<p class="bedFlagColor" style="background-color: #F56C6C"></p></label>
    </div>
    <el-row>
      <template v-for="(bed,bedIndex) in data.bedList" :key="bedIndex">
        <el-col :span="8">
          <div style="display: flex;align-items: center;padding: 0.75em;margin: 5px 5px;background-color: white">
            <div>
              <img style="width: 120px;height: 192px" :src=getImgUrl(bed.sex) />
            </div>
            <div style="margin-left: 10px;line-height: 30px">
              <div class="bedPanoramaText" v-show="bed.bedFlag !== '空闲'"><b>老人姓名：</b>{{ bed.elderName }}</div>
              <div class="bedPanoramaText" v-show="bed.bedFlag !== '空闲'"><b>老人性别：</b>{{ bed.sex }}</div>
              <div class="bedPanoramaText" v-show="bed.bedFlag !== '空闲'"><b>老人年龄：</b>{{ bed.age }}</div>
              <div class="bedPanoramaText"><b>房间名称：</b>{{ bed.roomName }}</div>
              <div class="bedPanoramaText"><b>床位名称：</b>{{ bed.name }}</div>
              <div :style="{color: getBedFlagColor(bed.bedFlag)}"><b>床位状态：</b>{{ bed.bedFlag }}</div>
            </div>
          </div>
        </el-col>
      </template>
    </el-row>
    <el-empty style="margin-top: 10%" v-show="data.bedList.length < 1" description="暂无数据" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, toRefs, ref } from "vue";
import { Delete, Search } from "@element-plus/icons-vue";
import { listBuilding, listFloorByBuildingId, listRoomByKey } from "@/apis/bedPanorama";

const bedList: any[] = [];

const data = ref({
  buildingId: "",
  floorId: "",
  elderName: "",
  buildingList: [],
  floorList: [],
  roomList: [],
  bedList
}).value;

// 初始化数据
onMounted(() => {
  initData();
});

async function initData() {
  // 楼栋列表
  const buildingList = await listBuilding();
  data.buildingList = buildingList.data;

  // 楼层列表
  await getFloorList();

  // 床位列表
  await getBedList();
}

const getFloorList = async () => {
  data.floorId = "";
  const floorList = await listFloorByBuildingId(data.buildingId);
  data.floorList = floorList.data;
  await getBedList();
};

const getBedList = async () => {
  // 房间列表
  const roomList = await listRoomByKey({
    buildingId: data.buildingId,
    floorId: data.floorId,
    elderName: data.elderName
  });
  // 从房间列表封装床位列表
  data.bedList = [];
  roomList.data.forEach((room: any) => {
    room.bedList.forEach((bed: any) => {
      bed.roomName = room.name;
      data.bedList.push(bed);
    });
  });
};

const getImgUrl = (sex: string) => {
  if (sex === "男") {
    return require("@/assets/imgs/img.png");
  } else if (sex === "女") {
    return require("@/assets/imgs/img_1.png");
  } else {
    return require("@/assets/imgs/img_2.png");
  }
};

const getBedFlagColor = (bedFlag: string) => {
  if (bedFlag === "空闲") {
    return "#67C23A";
  } else if (bedFlag === "预定") {
    return "#E6A23C";
  } else if (bedFlag === "入住" || bedFlag === "退住审核") {
    return "#F56C6C";
  }
};

// 搜索
const search = () => {
  getBedList();
};

// 重置
const reset = () => {
  data.buildingId = "";
  data.floorId = "";
  data.elderName = "";
  initData();
};
</script>

<style lang="scss" scoped>
.bedFlagLabel {
  display: flex;
  align-items: center;
}

.bedFlagColor {
  width: 50px;
  height: 20px;
  margin-left: 5px;
  border-radius: 3px;
}

.bedPanoramaText {
  color: #73767a;
}
</style>
