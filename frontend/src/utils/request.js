import axios from 'axios'

const request = axios.create({
    baseURL: 'http://localhost:8080/api',
    timeout: 10000   // 稍微加长一点，防止网络波动
})

// 响应拦截器（关键优化点）
request.interceptors.response.use(
    response => {
        const data = response.data

        // === 情况1：后端返回的是纯字符串（我们现在的2FA接口就是这种）===
        if (typeof data === 'string') {
            return data   // 直接返回字符串，视为成功
        }

        // === 情况2：后端返回的是标准对象结构（登录注册目前是这种）===
        if (data && typeof data === 'object') {
            if (data.code !== 200) {
                return Promise.reject(new Error(data.message || '请求失败'))
            }
            return data.data || data   // 返回业务数据
        }

        // 兜底：直接返回原始数据
        return data
    },

    error => {
        // 处理网络错误或非 200 响应
        let errorMsg = '网络错误'
        if (error.response) {
            // 优先取后端返回的 message
            errorMsg = error.response.data?.message
                || error.response.data
                || error.response.statusText
                || '请求失败'
        } else if (error.message) {
            errorMsg = error.message
        }
        return Promise.reject(new Error(errorMsg))
    }
)

export default request