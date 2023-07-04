<script setup>
import {reactive, ref} from "vue";
import {Edit, Lock, Message} from "@element-plus/icons-vue";
import router from "@/router";
import {post} from "@/net";
import {ElMessage} from "element-plus";

const validatePassword = (rule, value, callback)=>{
  if (value === '') {
    callback(new Error('请再次输入密码'))
  }
  else if(value!==form.password){
    callback(new Error('两次输入的密码不一致'))
  }
  else callback()
}

const active = ref(0)

const form = reactive({
  email:'',
  code:'',
  password:'',
  password_repeat:'',
})
const rules = {
  email: [
    {required: true, message: '请输入邮箱地址', trigger: 'blur',},
    {type: 'email', message: '邮箱地址格式无效', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '验证码不能为空', trigger: 'change'}
  ],
  password: [
    {required:true,message: '请输入密码',trigger:'change'},
    { min: 6, max: 18, message: '长度应当在6~18个字符之间', trigger: 'blur' },
  ],
  password_repeat: [
    {validator:validatePassword,trigger: ['blur','change']},
  ]
}

const onValidate = (prop,isValid)=>{
  if (prop==='email')
    isEmailValid.value=isValid
}

const isEmailValid = ref(false)
const coldTime = ref(0)
const formRef = ref()

const validateEmail = () =>{
  coldTime.value = 60
  post('/api/auth/valid-reset-email',{
    email: form.email
  },(message)=>{
    ElMessage.success(message)
    setInterval(()=>coldTime.value--,1000)
  },(message)=>{
    ElMessage.warning(message)
    coldTime.value = 0
  })
}
const startReset = () => {
  formRef.value.validate((isValid)=>{
    if(isValid){
      post('/api/auth/start-reset',{
        email: form.email,
        code: form.code
      },()=>{
        active.value++
      })
    }
    else {
      ElMessage.warning('请填写电子邮件和验证码')
    }
  })
}
const doReset = () => {
  formRef.value.validate((isValid)=>{
    if(isValid){
      post('/api/auth/do-reset',{
        password: form.password,
      },(message)=>{
        ElMessage.success(message)
        router.push('/')
      })
    }
    else {
      ElMessage.warning('请填写新的密码')
    }
  })
}
</script>

<template>
  <div>
    <div style="margin:30px 20px">
    <el-steps :active="active" finish-status="success" align-center>
      <el-step title="验证电子邮件" />
      <el-step title="重新设定密码" />
    </el-steps>
    </div>
    <Transition name="el-fade-in-linear">
      <div style="text-align: center;margin: 0 20px;height: 100%" v-if="active === 0">
      <div style="margin-top: 80px;">
        <div style="font-size: 25px;font-weight:bold">重置密码</div>
        <div style="font-size: 14px;color: grey">请输入需要重置密码的电子邮件地址</div>
      </div>
      <div style="margin-top: 50px">
        <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
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

      <div style="margin-top: 70px">
        <el-button @click="startReset()" style="width: 270px;" type="warning" plain>重置密码</el-button>
      </div>

      <el-divider></el-divider>
      <div style="margin-top: 20px;">
        <el-link type="primary" style="translate: 0 -2px" @click="router.push('/')">返回登陆</el-link>
      </div>
    </div>
    </Transition>

    <Transition name="el-fade-in-linear">
      <div style="text-align: center;margin: 0 20px;height: 100%" v-if="active === 1">
        <div style="margin-top: 80px;">
          <div style="font-size: 25px;font-weight:bold">重置密码</div>
          <div style="font-size: 14px;color: grey">请填写您的密码</div>
        </div>
        <div style="margin-top: 50px">
          <el-form :model="form" :rules="rules" @validate="onValidate" ref="formRef">
            <el-form-item prop="password">
              <el-input v-model="form.password" :maxlength="18" type="password" placeholder="设置新的密码">
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
          </el-form>
        </div>
        <div style="margin-top: 70px">
          <el-button @click="doReset()" style="width: 270px;" type="warning" plain>确定</el-button>
        </div>
      </div>
    </Transition>
  </div>


</template>

<style scoped>

</style>