/**
 * URL 清洗工具 — 解决图片URL中空格编码为 %20 导致 404 的问题
 *
 * 使用场景：
 *   - 数据库存储的URL可能含前导/尾部空格或中间空格
 *   - 浏览器渲染 <img :src="url"> 时，空格被编码为 %20 → 404
 *   - 本工具清洗所有URL，去除多余空格，确保请求路径正确
 */

/**
 * 清洗单个URL字符串
 * @param {string} url - 原始URL（可能含空格）
 * @returns {string} - 清洗后的URL（去首尾空格，中间空格替换为-）
 *
 * 示例:
 *   cleanUrl('http://localhost:9000/bucket/ h004.png ') → 'http://localhost:9000/bucket/h004.png'
 *   cleanUrl(null) → ''
 *   cleanUrl('') → ''
 */
export function cleanUrl(url) {
  if (!url || typeof url !== 'string') return ''
  // 1. 去除首尾空格
  // 2. 将URL路径中的连续空格替换为单个连字符
  // 3. 去除协议/域名中可能的空格
  let cleaned = url.trim()
  // 分离协议+域名部分 和 路径部分，只处理路径中的空格
  const protoIdx = cleaned.indexOf('://')
  if (protoIdx > 0) {
    const pathStart = cleaned.indexOf('/', protoIdx + 3)
    if (pathStart > 0) {
      const prefix = cleaned.substring(0, pathStart)
      let path = cleaned.substring(pathStart)
      // 路径中连续空格 → 单个连字符
      path = path.replace(/\s+/g, '-')
      // 去除连字符和斜杠之间的多余字符
      path = path.replace(/\/-+/g, '/')   // "/-" → "/"
      path = path.replace(/-+\//g, '/')   // "-/" → "/"
      cleaned = prefix + path
    }
  } else {
    // 无协议的相对路径，直接替换空格
    cleaned = cleaned.replace(/\s+/g, '-')
  }
  return cleaned
}

/**
 * 递归清洗对象中所有URL字段（cover、avatar、image、productCover 等）
 * 去除首尾空格和路径中的多余空格，避免渲染时被编码为 %20 导致 404
 *
 * @param {*} obj - 任意类型（对象/数组/原始值）
 * @param {string[]} fields - 要清洗的URL字段名列表
 * @returns {*} - 清洗后的同类型值
 */
export function deepCleanUrls(obj, fields = ['cover', 'avatar', 'image', 'productCover', 'url']) {
  if (!obj || typeof obj !== 'object') return obj

  if (Array.isArray(obj)) {
    return obj.map(item => deepCleanUrls(item, fields))
  }

  const cleaned = { ...obj }
  for (const key of Object.keys(cleaned)) {
    const val = cleaned[key]
    if (typeof val === 'string' && fields.includes(key)) {
      cleaned[key] = cleanUrl(val)
    } else if (val && typeof val === 'object') {
      cleaned[key] = deepCleanUrls(val, fields)
    }
  }
  return cleaned
}