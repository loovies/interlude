import { Plugin } from 'xgplayer'

const { POSITIONS } = Plugin

const axios = import ('axios')

// demoPlugin.js
export default class demoPlugin extends Plugin {
  // 插件的名称，将作为插件实例的唯一key值
  static get pluginName() {
    return 'demoPlugin'
  }

  static get defaultConfig () {
      return {
        // 挂载在controls的右侧，如果不指定则默认挂载在播放器根节点上
        position: POSITIONS.CONTROLS_LEFT
      }
  }

  constructor (args) {
    super(args)
  }

  beforePlayerInit () {
   // TODO 播放器调用start初始化播放源之前的逻辑
  }

  afterPlayerInit () {
    // TODO 播放器调用start初始化播放源之后的逻辑
  }

  afterCreate () {
    /**
     * 自定义插件 弹幕发送模块
     * root.__root__为根节点Vue模板data值
     */
    this.icon = this.find('.danmu-send')
    let input = this.find('.danmu-input')
    this.onIconClick = (e) => {
      console.log('class为danmu-send元素点击回调')
      let danmutext = input.value
      // console.log(this.player.root.__vue__.video_id)
      let video_id = this.player.root.__vue__.video_id
      let tokens3 = this.player.root.__vue__.tokens
      let playertimeinit = this.player.currentTime
    //   console.log(playertimeinit)
      let playertime = playertimeinit * 1000
      if(tokens3 && tokens3 !== 'undefined'){
          axios.post(`弹幕发送存储接口url`,{tokens3,danmutext,video_id,playertime}).then(res=>{
            this.player.danmu.sendComment({
              duration: 15000, //弹幕持续显示时间,毫秒(最低为5000毫秒)
              id: playertime, //弹幕id，需唯一
              start: playertime, //弹幕出现时间，毫秒
              prior: true, //该条弹幕优先显示，默认false
              color: true, //该条弹幕为彩色弹幕，默认false
              txt: danmutext, //弹幕文字内容
              style: {  //弹幕自定义样式
                color: '#ff9500',
                fontSize: '20px',
                border: 'solid 1px #ff9500',
                borderRadius: '50px',
                padding: '5px 11px',
                backgroundColor: 'rgba(255, 255, 255, 0.1)'
              }
            })
            this.danmutext = ''
          }).catch(error=>{
            console.log(error)
          })
        }
    }
    this.onClick = () => {
      console.log('当前插件根节点点击事件')
    }
    // 对当前插件根节点内部类名为.danmu-send的元素绑定click事件
    this.bind('.danmu-send', 'click', this.onIconClick)
    // 对当前插件根节点绑定click事件
    this.bind('click', this.onClick)
    //TODO 插件实例化之后的一些逻辑
    
  }

  destroy () {
    this.unbind('.danmu-send', 'click', this.onIconClick)
    this.unbind('click', this.onClick)
    this.icon = null
    // 播放器销毁的时候一些逻辑
  }

  render () {
    // return `<div class="demo-plugin">这是一个dmeo插件<div class="icon"></div></div>`
    return `<div class="demo-plugin">
            <div class="icon"></div>
            <div class="danmutext">
                <input class="danmu-input" style="width: 320px;
                height: 40px;
                box-sizing: border-box;
                border-radius: 18px;
                background-color:#1d1d1d;
                border: solid rgb(88, 88, 89);
                caret-color: #e7e7e7;
                font-size: 15px;
                color: #fff;
                letter-spacing: 0;
                padding: 3px 60px 3px 75px;
                position: absolute;
                left: 0;" v-model="danmutext" type="text" placeholder="请输入弹幕" style="padding-left:40px" @keyup.enter="danmuSend()">
                <button class="danmu-send" style="position: relative;
                right: 6px;
                width: 60px;
                height: 30px;
                line-height: 30px;
                border-radius: 15px;
                background-color: #01c8d4;
                color: #222;
                font-size: 12px;
                top: 5px;
                /* left: 350px; */
                margin-left: 255px;
                border: none;
                vertical-align: top;
                cursor: pointer;" @click="danmuSend()">发送</button>
            </div>
            </div>`
  }
}