// store/index.js
import { createStore } from 'vuex';

export default createStore({
    state: {
        user: null, // 存储用户信息
        isAuthenticated: false, // 是否已登录
    },
    mutations: {
        SET_USER(state, user) {
            state.user = user;
            state.isAuthenticated = true;
        },
        LOGOUT(state) {
            state.user = null;
            state.isAuthenticated = false;
        },
    },
    actions: {
        login({ commit }, user) {
            commit('SET_USER', user);
        },
        logout({ commit }) {
            commit('LOGOUT');
        },
    },
    getters: {
        user: state => state.user,
        isAuthenticated: state => state.isAuthenticated,
        isAdmin: state => state.user && state.user.role === 2, // 判断是否是管理员
    },
});