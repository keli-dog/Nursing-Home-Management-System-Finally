// TODO 初始化Array
export const initArray = (array: any) => {
  return array ? array : [];
};

// TODO 将一个Array的某个字段所有的值全部添加到了一个Array
export const arrayPushAllByFiled = (dataArray: any, pushArray: any, field: any) => {
  // 初始化
  dataArray = initArray(dataArray);
  pushArray = initArray(pushArray);

  // 法一
  // for (const item of dataArray) {
  //   pushArray.push(item[field]);
  // }
  // return pushArray;

  // 法二
  return dataArray.map((item: any) => item[field]);
};

// TODO 将一个Array的数据全部添加到了一个Array
export const arrayPushAllByObj = (dataArray: any, pushArray: any) => {
  // 初始化
  dataArray = initArray(dataArray);
  pushArray = initArray(pushArray);

  // push
  pushArray.push(...dataArray);

  return pushArray;
};

// TODO Array根据字段去重
export const arrayDeduplicationByFiled = (array: any, field: string) => {
  // 初始化
  array = initArray(array);

  // 去重 法一：该方法对编号进行去重返回的是[1,3,5]的编号数组
  // const idArray = [...new Set(array.map((item: any) => item[field]))];
  // 再使用for循环依次取出id对应数据即可

  // 去重 法二
  return array.reduce((newArray: any, thisObj: any) => {
    // 检查当前对象的属性是否已经存在于新数组中
    const exists = newArray.some((item: any) => thisObj[field] === item[field]);
    // 如果属性不存在，则将当前对象添加到新数组中
    if (!exists) {
      newArray.push(thisObj);
    }
    return newArray;
  }, []);
};

// TODO Array根据对象去重(适用于非对象数组)
export const arrayDeduplicationByObj = (array: any) => {
  // 初始化
  array = initArray(array);

  // 去重 法一：该方法对编号进行去重返回的是[1,3,5]的编号数组
  // const idArray = [...new Set(array.map((item: any) => item))];
  // 再使用for循环依次取出id对应数据即可

  // 去重 法二
  return array.reduce((newArray: any, thisObj: any) => {
    // 检查当前对象的属性是否已经存在于新数组中
    const exists = newArray.some((item: any) => thisObj === item);
    // 如果属性不存在，则将当前对象添加到新数组中
    if (!exists) {
      newArray.push(thisObj);
    }
    return newArray;
  }, []);
};
