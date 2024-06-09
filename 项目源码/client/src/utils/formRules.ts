// 电话规则
import {
  isNotEmail,
  isNotIdNum,
  isNotPhone,
  isNotInteger,
  isNotNumber
} from "@/utils/is";

// 电话规则
export function phoneRule(rule: any, value: any, callback: any, field: string) {
  const trim = value?.trim();
  if (!trim) {
    callback(new Error(field + "电话不能为空"));
  } else if (isNotPhone(trim)) {
    callback(new Error(field + "电话格式有误"));
  } else {
    callback();
  }
}

// 邮箱规则
export function emailRule(rule: any, value: any, callback: any, field: string) {
  const trim = value?.trim();
  if (!trim) {
    callback(new Error(field + "邮箱不能为空"));
  } else if (isNotEmail(trim)) {
    callback(new Error(field + "邮箱格式有误"));
  } else {
    callback();
  }
}

// 身份证号规则
export function idNumRule(rule: any, value: any, callback: any, field: string) {
  const trim = value?.trim();
  if (!trim) {
    callback(new Error(field + "身份证号不能为空"));
  } else if (isNotIdNum(trim)) {
    callback(new Error(field + "身份证号格式有误"));
  } else {
    callback();
  }
}

// 字符串规则
export function stringRule(
  rule: any,
  value: any,
  callback: any,
  field: string | number,
  mixLen: number,
  maxLen: number
) {
  const trim = value?.trim();
  if (!trim) {
    callback(new Error(field + "不能为空"));
  } else if (trim.length < mixLen || trim.length > maxLen) {
    callback(new Error(field + "长度应为" + mixLen + "~" + maxLen));
  } else {
    callback();
  }
}

// 数字（整数/小数）规则
export function numberRule(
  rule: any,
  value: any,
  callback: any,
  field: string,
  mix: number,
  max: number
) {
  if (!value) {
    callback(new Error(field + "不能为空"));
  } else {
    const trim = value.toString()?.trim();
    if (isNotNumber(trim)) {
      callback(new Error(field + "格式有误"));
    } else if (trim < mix || trim > max) {
      callback(new Error(field + "应该在" + mix + "~" + max + "之间"));
    } else {
      callback();
    }
  }
}

// 整数规则
export function integerRule(
  rule: any,
  value: any,
  callback: any,
  field: string,
  mix: number,
  max: number
) {
  if (!value) {
    callback(new Error(field + "不能为空"));
  } else {
    const trim = value.toString()?.trim();
    if (isNotInteger(trim)) {
      callback(new Error(field + "格式有误"));
    } else if (trim < mix || trim > max) {
      callback(new Error(field + "应该在" + mix + "~" + max + "之间"));
    } else {
      callback();
    }
  }
}

// 日期规则
export const disabledNowBeforeDate = (time: Date) => {
  return time.getTime() < Date.now();
};
export const disabledNowAfterDate = (time: Date) => {
  return time.getTime() > Date.now();
};
