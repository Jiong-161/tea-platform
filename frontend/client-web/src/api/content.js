/**
 * 茶文化内容相关 API 接口
 * 对应后端 content-service（网关路径 /content/**）
 */
import request from '@/utils/request'

/** 获取文章分类列表 */
export function getCategoryList() {
  return request({ url: '/content/category/list', method: 'get' })
}

/** 获取文章分页列表 */
export function getArticleList(params) {
  return request({ url: '/content/article/list', method: 'get', params })
}

/** 获取文章详情 */
export function getArticleDetail(id) {
  return request({ url: `/content/article/detail/${id}`, method: 'get' })
}

/** 发布文章 */
export function publishArticle(data) {
  return request({ url: '/content/article/publish', method: 'post', data })
}