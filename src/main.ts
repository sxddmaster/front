import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createRouter, createWebHistory } from 'vue-router'
import Home from './views/Home.vue'
import Chat from './views/Chat.vue'
import DataTable from './views/DataTable.vue'
import CompanyList from './views/CompanyList.vue'
import { createPinia } from 'pinia'

const app = createApp(App)
app.use(ElementPlus)

const routes = [
  { path: '/', name: 'home', component: Home },
  { path: '/myapp/chat', name: 'chat', component: Chat },
  { path: '/myapp/table', name: 'table', component: DataTable },
  { path: '/myapp/company-list', name: 'company-list', component: CompanyList },
]
const router = createRouter({
  history: createWebHistory(),
  routes,
})

const pinia = createPinia()
app.use(pinia)
app.use(router)
app.mount('#app')
