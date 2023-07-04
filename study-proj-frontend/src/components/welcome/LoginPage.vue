<script setup>
import {Lock, User} from "@element-plus/icons-vue";
import {reactive} from "vue";
import {ElMessage} from "element-plus";
import {get, post} from "@/net";
import router from "@/router";
import {useStore} from "@/stores";

const store = useStore();

const form=reactive({
  username:'',
  password:'',
  remember:false

})
const login = () => {
  if (!form.username||!form.password){
    ElMessage.warning('请填写用户名和密码！')
  }else{
    post('/api/auth/login',{
      username:form.username,
      password:form.password,
      remember:form.remember
    },(message)=>{
        ElMessage.success(message)
        get('/api/user/me',(message)=>{
          store.auth.user = message
          router.push('/index')
        },()=>{
          store.auth.user = null
        })
    })
  }
}
</script>

<template>
  <div style="text-align: center;margin: 0 20px;">
    <div style="margin-top: 150px;">
      <div style="font-size: 25px;font-weight:bold">登录</div>
      <div style="font-size: 14px;color: grey">请输入账号和密码</div>
    </div>
    <div style="margin-top: 30px;">
      <el-input type="text" v-model="form.username" placeholder="用户名/邮箱">
        <template #prefix>
            <el-icon>
              <User/>
            </el-icon>
        </template>
      </el-input>
      <el-input type="password" v-model="form.password" placeholder="密码" style="margin-top: 10px;">
        <template #prefix>
            <el-icon>
              <Lock/>
            </el-icon>
        </template>
      </el-input>
    </div>
    <div style="margin-top: 5px;display: inline-block">
      <el-row>
        <el-col span="12" style="text-align: left">
          <el-checkbox v-model="form.remember" label="记住我" size="large"/>
        </el-col>
        <el-col span="12" style="text-align: right">
          <el-link @click="router.push('/forget')">忘记密码？</el-link>
        </el-col>
      </el-row>
    </div>

    <div style="margin-top: 40px">
      <el-button @click="login()" style="width: 270px;font-size: 15px;" type="success" plain>登录</el-button>
    </div>
    <el-divider>
      <span style="font-size: 14px;color: grey">没有账号？</span>
    </el-divider>
    <el-button @click="router.push('/register')" style="width: 270px;font-size: 15px;" type="warning" plain>注册</el-button>
  </div>
</template>

<style scoped>

</style>