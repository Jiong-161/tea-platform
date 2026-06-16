/** 后台 — 展览管理 API */
import request from './request'

/** 展览分页列表 */
export function getExhibitionList(params) { return request({ url: '/admin/exhibition/list', method: 'get', params }) }
/** 新增/编辑展览 */
export function saveExhibition(data, id) { return request({ url: '/admin/exhibition/save', method: 'post', data, params: { id } }) }
/** 删除展览 */
export function deleteExhibition(id) { return request({ url: `/admin/exhibition/${id}`, method: 'delete' }) }
/** 报名记录 */
export function getSignupList(params) { return request({ url: '/admin/exhibition/signup/list', method: 'get', params }) }