import { isArray } from './is'

export function formatValue(callValue: any) {
  // 如果当前值为数组,使用 / 拼接（根据需求自定义）
  if (isArray(callValue)) return callValue.length ? callValue.join(' / ') : '--'
  return callValue ?? '--'
}

export function handleRowAccordingToProp(
  row: { [key: string]: any },
  prop: string
) {
  if (!prop.includes('.')) return row[prop] ?? '--'
  prop.split('.').forEach(item => (row = row[item] ?? '--'))
  return row
}

export function handleProp(prop: string) {
  const propArr = prop.split('.')
  if (propArr.length == 1) return prop
  return propArr[propArr.length - 1]
}

export function filterEnum(
  callValue: any,
  enumData: any[] | undefined,
  fieldNames?: { label: string; value: string },
  type?: 'tag'
): string {
  const value = fieldNames?.value ?? 'value'
  const label = fieldNames?.label ?? 'label'
  let filterData: { [key: string]: any } = {}
  if (Array.isArray(enumData))
    filterData = enumData.find((item: any) => item[value] === callValue)
  if (type == 'tag') return filterData?.tagType ? filterData.tagType : ''
  return filterData ? filterData[label] : '--'
}
