import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import { createRouter, createWebHistory } from 'vue-router'
import Home from './views/Home.vue'
import Chat from './views/Chat.vue'
import DataTable from './views/DataTable.vue'

const app = createApp(App)
app.use(ElementPlus)

const routes = [
  { path: '/', name: 'home', component: Home },
  { path: '/chat', name: 'chat', component: Chat },
  { path: '/table', name: 'table', component: DataTable },
]
const router = createRouter({
  history: createWebHistory(),
  routes,
})

app.use(router)
app.mount('#app')
