/**
 * 展览相关 API 接口
 * 对应后端 exhibition-service（网关路径 /exhibition/**）
 */
import request from '@/utils/request'

/** 获取展览列表 */
export function getExhibitionList() {
  return request({ url: '/exhibition/list', method: 'get' })
}

/** 获取展览详情 */
export function getExhibitionDetail(id) {
  return request({ url: `/exhibition/${id}`, method: 'get' })
}

/** 展览报名 */
export function signupExhibition(data) {
  return request({ url: '/exhibition/signup', method: 'post', data })
}

/** 获取我的展览报名记录 */
export function getMyExhibition() {
  return request({ url: '/exhibition/my', method: 'get' })
}