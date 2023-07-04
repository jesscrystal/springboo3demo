<script setup>
import {reactive, ref} from 'vue'
import {Edit, Lock, Message, User} from "@element-plus/icons-vue";
import router from "@/router";
import {ElMessage} from "element-plus";
import {post} from "@/net";

// do not use same name with ref
const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: '',
})

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if (!/^(?!_)(?!.*?_$)[a-zA-Z0-9_\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('只能包含中文字母和数字以及下划线，下划线不能在首尾'))
  } else callback()
}
const validatePassword = (rule, value, callback)=>{
  if (value === '') {
    callback(new Error('请再次输入密码'))
  }
  else if(value!==form.password){
    callback(new Error('两次输入的密码不一致'))
  }
  else callback()
}

const rules = ({
  username: [
    { validator:validateUsername,trigger: ['blur','change'] },
    { min: 6, max: 12, message: '长度应当在6~12个字符之间', trigger: 'blur' },
  ],
  password: [
    {required:true,message: '请输入密码',trigger:'change'},
    { min: 6, max: 18, message: '长度应当在6~18个字符之间', trigger: 'blur' },
  ],
  password_repeat: [
    {validator:validatePassword,trigger: ['blur','change']},
  ],
  email: [
    {required: true, message: '请输入邮箱地址', trigger: 'blur',},
    {type: 'email', message: '邮箱地址格式无效', trigger: ['blur', 'change']}
  ],
  code: [
      {required: true, message: '验证码不能为空', trigger: 'change'}
  ]
})
const isEmailValid = ref(false)
const coldTime = ref(0)
const formRef = ref()
const onValidate = (prop,isValid)=>{
  if (prop==='email')
    isEmailValid.value=isValid
}

const register = () =>{
  formRef.value.validate((isValid)=>{
    if(isValid){
      post('api/auth/register',{
        username: form.username,
        password: form.password,
        email: form.email,
        code: form.code,
      },(message)=>{
        ElMessage.success(message)
        router.push('/')
      })
    }
    else {
      ElMessage.warning('请将表单填写完整')
    }
  })
}

const validateEmail = () =>{
  coldTime.value = 60
  post('/api/auth/valid-register-email',{
    email: form.email
  },(message)=>{
    ElMessage.success(message)
    setInterval(()=>coldTime.value--,1000)
  },(message)=>{
    ElMessage.warning(message)
    coldTime.value = 0
  })
}

</script>

<template>
  <div style="text-align: center;margin: 0 20px;">
    <div style="margin-top: 100px;">
      <div style="font-size: 25px;font-weight:bold">注册新用户</div>
      <div style="font-size: 14px;color: grey">请填写下列栏目以完成注册</div>
    </div>
    <div style="margin-top: 50px">
      <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
        <el-form-item prop="username">
          <el-input v-model="form.username" :maxlength="12" type="text" placeholder="用户名">
            <template #prefix>
              <el-icon><User/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" :maxlength="18" type="password" placeholder="设置密码">
            <template #prefix>
              <el-icon><Lock/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" type="password" placeholder="请再次输入密码">
            <template #prefix>
              <el-icon><Lock/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" type="email" placeholder="电子邮件地址">
            <template #prefix>
              <el-icon><Message/></el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="10" style="width: 100%">
            <el-col :span="17">
              <el-input v-model="form.code" :maxlength="6" type="text" placeholder="请输入验证码">
                <template #prefix>
                  <el-icon><Edit/></el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="5">
              <el-button type="success" @click="validateEmail" :disabled="!isEmailValid||coldTime > 0">
                {{coldTime > 0 ? '请稍后' + coldTime + '秒':'获取验证码'}}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>

      </el-form>
    </div>

    <div style="margin-top: 80px">
      <el-button style="width: 80%" type="warning" @click="register()">立即注册</el-button>
    </div>
    <div style="margin-top: 20px;">
      <span style="line-height: 15px;font-size: 14px;color: grey">已有账号？</span>
      <el-link type="primary" style="translate: 0 -2px" @click="router.push('/')">返回登陆</el-link>
    </div>

  </div>
</template>

<style scoped>

</style>