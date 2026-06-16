/** 后台 — 文章管理 API */
import request from './request'

/** 文章分页列表 */
export function getArticleList(params) { return request({ url: '/admin/content/article/list', method: 'get', params }) }
/** 新增/编辑文章（id 为空新增，有值编辑） */
export function saveArticle(data, id) { return request({ url: '/admin/content/article/save', method: 'post', data, params: { id } }) }
/** 删除文章 */
export function deleteArticle(id) { return request({ url: `/admin/content/article/${id}`, method: 'delete' }) }
/** 审核文章 */
export function auditArticle(id, status) { return request({ url: `/admin/content/article/audit/${id}`, method: 'put', params: { status } }) }
/** 分类列表 */
export function getCategoryList() { return request({ url: '/admin/content/category/list', method: 'get' }) }