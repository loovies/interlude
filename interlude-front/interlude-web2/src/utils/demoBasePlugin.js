import BasePlugin from 'xgplayer'
import Events from 'xgplayer'

export default class DemoBasePlugin extends BasePlugin {
  /**
   * （必须声明）插件的名称，将作为插件实例的唯一key值
   * 该参数还最为播放器上该插件的配置透传key值，例如：
   * var p = new player({
   *   demoBasePlugin: {
   *     text: '这是插件demoBasePlugin的配置信息'
   *   }
   * })
   * 在插件afterCreate之后可以通过this.config.text获取到改配置参数
   **/
  static get pluginName() {
    return 'demoBasePlugin'
  }
  static get defaultConfig () {
    return {
       text: '这是插件demoBasePlugin的默认Text'
    }
  }
  constructor (args) {
    super(args)
  }
  afterPlayerInit () {
    // TODO 播放器调用start初始化播放源之后的逻辑
  }
  afterCreate () {
    // 在afterCreate中可以加入DOM的事件监听

    this.on(Events.PLAY, () => {
      console.log('播放播放回调')
    })
  }
  destroy () {
    // 播放器销毁的时候一些逻辑
  }
}